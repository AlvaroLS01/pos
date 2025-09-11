/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.pos.services.notificaciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.core.model.notificaciones.Notificacion;
import com.comerzzia.core.model.notificaciones.Notificacion.Tipo;
import com.comerzzia.core.model.notificaciones.NotificacionesContexto;
import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.instoreengine.rest.client.notificaciones.NotificacionesRest;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Component
public class Notificaciones {

	@XmlTransient
	public static final Integer MAX = 99;
	@XmlTransient
	protected static Logger log = Logger.getLogger(Notificaciones.class);
	@XmlTransient
	public static final Object LOCK = new Object();
	
	@XmlTransient
	protected static Notificaciones instance;
	protected List<Notificacion> notification;
	@XmlTransient
	protected List<ChangeListener> listeners;

	protected Notificaciones() {
		notification = new ArrayList<Notificacion>();
		listeners = new ArrayList<>();
	}

	public static Notificaciones get(){
		if(instance == null){
			instance = (Notificaciones)ContextHolder.get().getBean(Notificaciones.class);
			
			try{
				String tempPath = System.getProperty("java.io.tmpdir");
				File file = new File(tempPath + File.separator + "Comerzzia" + File.separator + "notificaciones.xml");
				if(file.exists()){
					List<Notificacion> listParcial = MarshallUtil.leerXMLListaParcial(new FileInputStream(file), Notificacion.class, "notification", MAX);
					instance.notification.addAll(listParcial);
					writeXML();
				}
			}catch(Exception e){
				log.error("get() - Error al leer notificaciones del XML", e);
			}
		}
		
		return instance;
	}
	
	public void init(){
		if(AppConfig.segundosPeticionNotificaciones > 0){
			final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
			scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){
				protected boolean notified = false;
				@Override
				public void run() {
					try {
						NotificacionesContexto notificaciones = NotificacionesRest.getNotificaciones(SpringContext.getBean(Sesion.class).getAplicacion().getUidCaja());
						for (Notificacion notif : notificaciones.getNotificaciones()) {
							addNotification(notif);
						}
						if(notified){
							addNotification(new Notificacion(I18N.getTexto("Se ha restablecido la comunicación de notificaciones"), 
									Tipo.INFO));
						}
						notified = false;
					} catch (RestException | RestHttpException e) {
						log.error("ScheduledThreadPoolExecutor() - Error en la petición para obtener notificaciones.", e);
						if(!notified){
							addNotification(new Notificacion(I18N.getTexto("Ha ocurrido un error al obtener notificaciones. Compruebe la configuración."), 
								Tipo.WARN));
							notified = true;
						}
					} catch(Exception e){
						log.error("ScheduledThreadPoolExecutor() - Error en la petición para obtener notificaciones.", e);
						if(!notified){
							addNotification(new Notificacion(I18N.getTexto("Ha ocurrido un error al obtener notificaciones. Compruebe la configuración."), 
								Tipo.WARN));
							notified = true;
						}
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
			
			writeXML();
		}
	}

	protected static void writeXML(){
		try {
			String tempPath = System.getProperty("java.io.tmpdir");
			String folderPath = tempPath + File.separator + "Comerzzia" + File.separator;
			File folder = new File(folderPath);
			folder.mkdirs();
			File file = new File(folderPath + "notificaciones.xml");
			byte[] xmlBytes = MarshallUtil.crearXML(instance);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(xmlBytes);
			fos.close();
		} catch (Exception e) {
			log.error("get() - Error al escribir notificaciones a XML", e);
		}
	}
	
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
			
			if(write){
				writeXML();
			}
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
			
			writeXML();
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
