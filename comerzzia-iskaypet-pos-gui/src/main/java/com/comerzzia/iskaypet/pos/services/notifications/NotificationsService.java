package com.comerzzia.iskaypet.pos.services.notifications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.iskaypet.pos.persistence.notifications.NotificationsVersion;
import com.comerzzia.iskaypet.pos.persistence.notifications.NotificationsVersionExample;
import com.comerzzia.iskaypet.pos.persistence.notifications.NotificationsVersionExample.Criteria;
import com.comerzzia.iskaypet.pos.persistence.notifications.NotificationsVersionMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

/**
 * GAP61 - IMPRESIÓN ETIQUETAS LINEAL
 */
@Service
public class NotificationsService{
	
	private static final Logger log = Logger.getLogger(NotificationsService.class);

	@Autowired
	protected Sesion sesion;
	@Autowired
	protected NotificationsVersionMapper notificationsMapper;

	public List<NotificationsVersion> getNotificationsNotView(){
		List<NotificationsVersion> result = new ArrayList<NotificationsVersion>();
		try{
			/*result.addAll(notificationsMapper.selectDistinctMsg(sesion.getAplicacion().getUidActividad()));*/
			//Seleccionamos solo los mensajes que son diferentes en las notificaciones(no por uidNotificacion)
			result.addAll(notificationsMapper.selectMsgDistinct(sesion.getAplicacion().getUidActividad()));
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al consultar las notificaciones de versión : ") + e.getMessage();
			log.error("getNotificationsNotView() - " + msgError, e);
		}
		return result;
	}
	
	public int updateNotificationsView(List<NotificationsVersion> listNotifications){
		int result = 0;
		try{
			for(NotificationsVersion notificationsVersion : listNotifications){
				notificationsVersion.setViewed("S");
				result = notificationsMapper.updateByPrimaryKey(notificationsVersion);
				if(result == 0){
					throw new Exception();
				}
			}
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al marcar las notificaciones de versión como vistas : ") + e.getMessage();
			log.error("updateNotificationsView() - " + msgError, e);
		}
		return result;
	}
/*	public int updateNotificationsViewCheck(NotificationsVersion notificationsVersion){
		int result = 0;
		try{
			NotificationsVersion version = new NotificationsVersion();
			version.setActivityUid(sesion.getAplicacion().getUidActividad());
			version.setMsg(notificationsVersion.getMsg());
			version.setNotificationUid(notificationsVersion.getNotificationUid());	
			version.setViewed("S");
			version.setNotificationDate(new Date());
			result = notificationsMapper.updateByPrimaryKey(version);
				if(result == 0){
					throw new Exception();
				}
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al marcar las notificaciones de versión como vistas : ") + e.getMessage();
			log.error("updateNotificationsView() - " + msgError, e);
		}
		return result;
	}*/
	
	/**
	 * Update de todos los mensajes de mismo nombre a S independiente del UID de la notificacion
	 */
	public int updateNotificationsViewCheckMsgDistinct(NotificationsVersion notificationsVersion){
		int result = 0;
		try{
			NotificationsVersion version = new NotificationsVersion();
			NotificationsVersionExample example = new NotificationsVersionExample();
			Criteria criteria = example.createCriteria();
			criteria.andActivityUidEqualTo(sesion.getAplicacion().getUidActividad());
			criteria.andMsgEqualTo(notificationsVersion.getMsg());
			version.setViewed("S");
			version.setNotificationDate(new Date());
			result = notificationsMapper.updateByExampleSelective(version, example);
				if(result == 0){
					throw new Exception();
				}
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al marcar las notificaciones de versión como vistas : ") + e.getMessage();
			log.error("updateNotificationsViewCheckMsgDistinct() - " + msgError, e);
		}
		return result;
		
	}
	
	public int updateClearNotification() {
		int result = 0;
		try {
			NotificationsVersion notificaciones = new NotificationsVersion();
			notificaciones.setViewed("S");
			notificaciones.setActivityUid(sesion.getAplicacion().getUidActividad());
			NotificationsVersionExample example = new NotificationsVersionExample();
			example.or().andActivityUidEqualTo(sesion.getAplicacion().getUidActividad()).andViewedEqualTo("N");
			notificationsMapper.updateByExampleSelective(notificaciones, example);
		} catch (Exception e) {
			String msgError = I18N.getTexto("Error al marcar las notificaciones de versión como vistas : ")
					+ e.getMessage();
			log.error("updateClearNotification() - " + msgError, e);
		}
		return result;
	}
	
}