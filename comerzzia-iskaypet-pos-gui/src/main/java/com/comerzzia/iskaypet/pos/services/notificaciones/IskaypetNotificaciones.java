package com.comerzzia.iskaypet.pos.services.notificaciones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.core.model.notificaciones.Notificacion;
import com.comerzzia.core.model.notificaciones.Notificacion.Tipo;
import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.iskaypet.pos.persistence.notifications.NotificationsVersion;
import com.comerzzia.iskaypet.pos.services.notifications.NotificationsService;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Component
public class IskaypetNotificaciones {

	@XmlTransient
	public static final Integer MAX = 99;
	@XmlTransient
	protected static Logger log = Logger.getLogger(IskaypetNotificaciones.class);
	@XmlTransient
	public static final Object LOCK = new Object();
	
	@XmlTransient
	protected static IskaypetNotificaciones instance;
	protected List<Notificacion> notification;
	@XmlTransient
	protected List<ChangeListener> listeners;
		
	protected NotificationsService notificationsVersionService = SpringContext.getBean(NotificationsService.class);

	public boolean limpiarPantalla;
	
	public int numNotificaciones;
	
	public boolean inicioApp = true;
	
	private Map<String, NotificationsVersion> mapNotificationIsk = new HashMap<>();
	
	protected IskaypetNotificaciones() {
		notification = new ArrayList<Notificacion>();
		listeners = new ArrayList<>();
	}

	public static IskaypetNotificaciones get(){
		if(instance == null){
			instance = (IskaypetNotificaciones)ContextHolder.get().getBean(IskaypetNotificaciones.class);
			instance.init();
		}
		
		return instance;
	}
	
	public void init(){
		notification.clear();
		if(AppConfig.segundosPeticionNotificaciones > 0){
			final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
			scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){
				@Override
				public void run() {
					try {
						if(!inicioApp) {
							List<NotificationsVersion> response = notificationsVersionService.getNotificationsNotView();
							setLimpiarPantalla(true);
							notification.clear();
							
							mapNotificationIsk = new HashMap<>();
							for(NotificationsVersion notificationsVersion : response){
								Notificacion notificacion = new Notificacion(notificationsVersion.getMsg(), Tipo.INFO, new Date());
								addNotification(notificacion);
								mapNotificationIsk.put(notificacion.getTexto(), notificationsVersion);
							}
							numNotificaciones= notification.size();
						}
						inicioApp = false;
					}
					catch(Exception e){
						log.error("ScheduledThreadPoolExecutor() - Error en la petición para obtener notificaciones.", e);
					}
				}}, 0, AppConfig.segundosPeticionNotificaciones, TimeUnit.SECONDS);
		}
	}

	public List<Notificacion> getNotifications() {
		return Collections.unmodifiableList(notification);
	}

	public void addNotification(Notificacion notif) {
		synchronized(LOCK) {
			notification.add(0, notif);
	
			ChangeEvent evt = new ChangeEvent(notif);
			evt.setAdded();
			for (ChangeListener changeListener : listeners) {
				changeListener.onChange(evt);
			}
	
			//Eliminamos entradas antiguas
			if(notification.size() > MAX){
				List<Notificacion> toRemove = notification.subList(MAX, notification.size());
				for (Notificacion notificacion : toRemove) {
					remove(notificacion, false);
				}
			}
			
		//	writeXML();
		}
	}

//	protected static void writeXML(){
//		try {
//			String tempPath = System.getProperty("java.io.tmpdir");
//			String folderPath = tempPath + File.separator + "Comerzzia" + File.separator;
//			File folder = new File(folderPath);
//			folder.mkdirs();
//			File file = new File(folderPath + "iskaypet_notificaciones.xml");
//			byte[] xmlBytes = MarshallUtil.crearXML(instance);
//			FileOutputStream fos = new FileOutputStream(file,false);
//			fos.write(xmlBytes);
//			fos.close();
//		} catch (Exception e) {
//			log.error("get() - Error al escribir notificaciones a XML", e);
//		}
//	}
	
	public void remove(Notificacion notif) {
		remove(notif, true);
	}
	
	public void remove(Notificacion notif, boolean write) {
		synchronized (LOCK) {
			notification.remove(notif);
			
			ChangeEvent evt = new ChangeEvent(notif);
			evt.setRemoved();
			for (ChangeListener changeListener : listeners) {
				changeListener.onChange(evt);
			}
			
//			if(write){
//				writeXML();
//			}
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
			
			//writeXML();
		}
	}

	public int count() {
	//	return notification.size();
		return numNotificaciones;
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

	public boolean isLimpiarPantalla() {
		return limpiarPantalla;
	}

	public void setLimpiarPantalla(boolean limpiarPantalla) {
		this.limpiarPantalla = limpiarPantalla;
	}

	public int getNumNotificaciones() {
		return numNotificaciones;
	}

	public void setNumNotificaciones(int numNotificaciones) {
		this.numNotificaciones = numNotificaciones;
	}

	public Map<String, NotificationsVersion> getMapNotificationIsk() {
		return mapNotificationIsk;
	}

	public void setMapNotificationIsk(Map<String, NotificationsVersion> mapNotificationIsk) {
		this.mapNotificationIsk = mapNotificationIsk;
	}
	
	
	
	
	
	
	
	

}
