package com.comerzzia.pos.core.services.notifications;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.usernotifications.client.UserNotificationsApiClient;
import com.comerzzia.api.usernotifications.client.model.UserNotification;
import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.core.commons.exception.ApiException;
import com.comerzzia.core.commons.i18n.I18N;
import com.comerzzia.core.commons.sessions.ComerzziaThreadSession;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.notification.Notification;
import com.comerzzia.omnichannel.facade.model.notification.Notification.NotificationOrigin;
import com.comerzzia.omnichannel.facade.model.notification.Notification.NotificationType;
import com.comerzzia.pos.core.services.session.ApplicationSession;
import com.comerzzia.pos.util.config.AppConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Notifications {
	protected static Logger log = Logger.getLogger(Notifications.class);
	private static final String LOCAL_FILE_NAME = "notifications.json";

	public static final Integer MAX = 99;
	public static final Object LOCK = new Object();
	
	@Autowired
	protected UserNotificationsApiClient userNotificationsApi;
	
	@Autowired
	protected VariableServiceFacade variableService;
	
	@Autowired
	protected ApplicationSession applicationSession;
	
	
	protected String bearer;
	
	protected static Notifications instance;
	protected List<Notification> notification;
	protected List<ChangeListener> listeners;

	protected Notifications() {
		notification = new ArrayList<Notification>();
		listeners = new ArrayList<>();
	}

	public static Notifications get(){
		if(instance == null){
			instance = (Notifications)CoreContextHolder.get().getBean(Notifications.class);
			
			try{
				String tempPath = System.getProperty("java.io.tmpdir");
				File file = new File(tempPath + File.separator + "Comerzzia" + File.separator + LOCAL_FILE_NAME);
				if(file.exists()){
					ObjectMapper mapper = new ObjectMapper();
					List<Notification> listParcial = mapper.readValue(file, new TypeReference<ArrayList<Notification>>(){});
					int from = 0;
					int to = 0;
					
					if (!listParcial.isEmpty()) {
						to = listParcial.size()-1;
					}
					
					if (listParcial.size() > MAX) {
						to = MAX;
					}
					
					instance.notification.addAll(listParcial.subList(from, to));
					writeToLocalFile();
				}
			}catch(Exception e){
				log.error("get() - Error al leer notificaciones de la copia local", e);
			}
		}
		
		return instance;
	}
	
	@PostConstruct
	public void init(){
		instance = this;
		if(AppConfig.getCurrentConfiguration().getNotificationsRequestSeconds() == 0) return;
		
		bearer = variableService.getVariableAsString("WEBSERVICES.APIKEY");
		
		final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
			//Manually setting thread as daemon so its stopped when the application is closed
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        });
		scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){
			protected boolean notified = false;
			
			@PostConstruct
			public void init() {
				ComerzziaThreadSession.setToken(bearer);
				ComerzziaThreadSession.setActivityUid(applicationSession.getConfiguredActivityUid());
			}
			
			@Override
			public void run() {
				try {
					List<UserNotification> notificaciones = userNotificationsApi.findRecipientNotifications("TILL_CODE", applicationSession.getTillCode(), null, null, null).getBody();
					clearRemote();
					if (notificaciones != null) {
						for (UserNotification notif : notificaciones) {
							Notification newNotification = new Notification();
							newNotification.setText(notif.getNotificationMessage());
							newNotification.setDate(notif.getCreationDate());
							newNotification.setType(notif.getNotificationType() == null ? null : NotificationType.valueOf(notif.getNotificationType()));
							newNotification.setOrigin(NotificationOrigin.REMOTE);
							newNotification.setRecipientUid(notif.getRecipientUid());
							
							addNotification(newNotification);
						}
					}
					
					if(notified){
						addNotification(new Notification(I18N.getText("Se ha restablecido la comunicación de notificaciones"), 
								NotificationType.INFO));
					}
					notified = false;
				} catch (ApiException e) {
					log.error("ScheduledThreadPoolExecutor() - Error en la petición para obtener notificaciones: " + e.getMessage());
					if(!notified){
						addNotification(new Notification(I18N.getText("Ha ocurrido un error al obtener notificaciones. Compruebe la configuración."), 
							NotificationType.WARN));
						notified = true;
					}
				} catch(Exception e){
					log.error("ScheduledThreadPoolExecutor() - Error en la petición para obtener notificaciones: " + e.getMessage());
					if(!notified){
						addNotification(new Notification(I18N.getText("Ha ocurrido un error al obtener notificaciones. Compruebe la configuración."), 
							NotificationType.WARN));
						notified = true;
					}
				}
			}}, 0, AppConfig.getCurrentConfiguration().getNotificationsRequestSeconds(), TimeUnit.SECONDS);
	}

	public List<Notification> getNotifications() {
		return Collections.unmodifiableList(notification);
	}
	
	public List<Notification> getLocalNotifications() {
		return Collections.unmodifiableList(notification.stream().filter(nt -> nt.getOrigin().equals(NotificationOrigin.LOCAL)).collect(Collectors.toList()));
	}

	public void addNotification(Notification notif) {
		synchronized(LOCK) {
			notification.add(0, notif);
	
			ChangeEvent evt = new ChangeEvent(notif);
			evt.setAdded();
			for (ChangeListener changeListener : listeners) {
				changeListener.onChange(evt);
			}
	
			//Eliminamos entradas antiguas
			if(notification.size() > MAX){
				List<Notification> toRemove = notification.subList(MAX, notification.size());
				for (Notification notificacion : toRemove) {
					remove(notificacion, false);
				}
			}
			
			writeToLocalFile();
		}
	}

	protected static void writeToLocalFile(){
		try {
			String tempPath = System.getProperty("java.io.tmpdir");
			String folderPath = tempPath + File.separator + "Comerzzia" + File.separator;
			File folder = new File(folderPath);
			folder.mkdirs();
			File file = new File(folderPath + LOCAL_FILE_NAME);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(file, instance.getLocalNotifications());
		} catch (Exception e) {
			log.error("get() - Error al escribir notificaciones a json", e);
		}
	}
	
	public void remove(Notification notif) {
		remove(notif, true);
	}
	
	public void remove(Notification notif, boolean write) {
		synchronized (LOCK) {
			notification.remove(notif);
	
			ChangeEvent evt = new ChangeEvent(notif);
			evt.setRemoved();
			for (ChangeListener changeListener : listeners) {
				changeListener.onChange(evt);
			}
			
			if(notif.getOrigin().equals(NotificationOrigin.REMOTE)) {
				try {
					userNotificationsApi.deleteUserNotification(notif.getRecipientUid());
				} catch (Exception e) {
					log.error("remove() - Error al eliminar notificación remota: " + e.getMessage());
				}
			}
			
			if(write){
				writeToLocalFile();
			}
		}
	}
	
	protected void clearRemote() {
		synchronized (LOCK) {
			List<Notification> remotes = notification.stream().filter(n-> n.getOrigin().equals(NotificationOrigin.REMOTE)).collect(Collectors.toList());
			
			remotes.forEach(n -> {
                if(n.getOrigin().equals(NotificationOrigin.REMOTE)) {
        			ChangeEvent evt = new ChangeEvent(n);
        			evt.setRemoved();
        			for (ChangeListener changeListener : listeners) {
        				changeListener.onChange(evt);
        			}
                }
            });
			
			notification.removeAll(remotes);
		}
	}

	public void clear() {
		synchronized (LOCK) {
			notification.clear();
	
			ChangeEvent evt = new ChangeEvent();
			evt.setCleared();
			for (ChangeListener changeListener : listeners) {
				changeListener.onChange(evt);
			}
			
			try {
				userNotificationsApi.deleteRecipientNotifications("TILL_CODE", applicationSession.getTillCode());
			} catch (Exception e) {
				log.error("remove() - Error al eliminar notificación remota: " + e.getMessage());
			}
			writeToLocalFile();
		}
	}

	public int count() {
		return notification.size();
	}
	
	public void addListener(ChangeListener list) {
		synchronized (LOCK) {
			listeners.add(list);
		}
	}

	public interface ChangeListener {
		public void onChange(ChangeEvent event);
	}

	public class ChangeEvent {
		protected boolean wasAdded, wasRemoved, wasCleared;
		protected Object object;

		public ChangeEvent() {
		}

		public ChangeEvent(Object obj) {
			object = obj;
		}

		public Object getObject() {
			return object;
		}

		public boolean wasAdded() {
			return wasAdded;
		}

		public boolean wasRemoved() {
			return wasRemoved;
		}

		public boolean wasCleared() {
			return wasCleared;
		}

		public void setAdded() {
			wasAdded = true;
		}

		public void setRemoved() {
			wasRemoved = true;
		}

		public void setCleared() {
			wasCleared = true;
		}
	}

}
