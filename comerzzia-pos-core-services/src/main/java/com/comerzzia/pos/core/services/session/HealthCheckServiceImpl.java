package com.comerzzia.pos.core.services.session;

import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.facade.service.activity.ActivityServiceFacade;
import com.comerzzia.pos.util.i18n.I18N;

import lombok.extern.log4j.Log4j;

@Lazy(false)
@Service
@Log4j
public class HealthCheckServiceImpl extends Observable implements HealthCheckService {
	public static Integer STATUS_INITIALIZING = 0;
	public static Integer STATUS_OK = 1;
	public static Integer STATUS_FAILED = -1;
		
	protected Integer statusId = STATUS_INITIALIZING;
	protected String statusMessage;
	
	@Autowired
	protected ActivityServiceFacade activityService;
	@Autowired
	protected Environment springEnvironment;
	
		
	@Value("${com.comerzzia.healthcheck.seconds:60}")
	protected Integer seconds;
	@Value("${com.comerzzia.healthcheck.svndirectory:}")
	protected String svnDirectory;
	
	protected Timer timer = null;
	
	protected class HealthCheckTimerTask extends TimerTask {		
		public void run() {
			checkStatus();			
		}
	}
	
	
	@PostConstruct
	public void init() {
		log.info(String.format("Initializing health check timer every %s seconds", seconds));
		
		timer = new Timer(getClass().getName(), true);
		timer.schedule(new HealthCheckTimerTask(), seconds * 1000, seconds * 1000);
	}
	
	@Override
	public void close() {
		if (timer != null) {
			log.debug("Closing healtch check timer");
			timer.cancel();
			timer = null;
		}		
	}

	@Override
	public synchronized void checkStatus() {		
		Integer newStatusId = STATUS_INITIALIZING;
		String newStatusMessage = "";
		
		try {
			newStatusMessage = I18N.getText("In progress");
			
            internalCheckStatus();

			newStatusId = STATUS_OK;
			newStatusMessage = "OK";
		} catch(Throwable e) {
			log.error(e.getMessage(), e);
			
			newStatusId = STATUS_FAILED;
			newStatusMessage = e.getLocalizedMessage();
		}
		
		if (newStatusId != statusId || !statusMessage.equals(newStatusMessage)) {
			statusId = newStatusId;
			statusMessage = newStatusMessage;
			
			log.info(String.format("Change status id to value %s with message %s", getStatus(), statusMessage));
			
			setChanged();
			notifyObservers(newStatusId);
		}
	}
	
	protected void internalCheckStatus() {
		if (ApplicationSession.getTpvConfig() == null || ApplicationSession.getTpvConfig().getTpv() == null) {
			throw new RuntimeException(I18N.getText("El archivo de configuración no ha sido cargado"));
		}
		
		if (StringUtils.isBlank(ApplicationSession.getTpvConfig().getTpv().getUidActividad())) {
			throw new RuntimeException(I18N.getText("La actividad no ha sido configurada"));
		}
		
		String activityUid = ApplicationSession.getTpvConfig().getTpv().getUidActividad();

		log.debug("Health check in progress for activity " + activityUid);

		try {
			activityService.findById(activityUid);
		} catch (NotFoundException | NoSuchElementException e) {
			throw new RuntimeException(I18N.getText("La actividad {0} no ha sido encontrada", activityUid));
		}		
	}

	@Override
	public Integer getStatusId() {
		return statusId;
	}
	
	@Override
	public String getStatus() {
		if (statusId == STATUS_INITIALIZING) {
			return I18N.getText("En progreso");
		} else 	if (statusId == STATUS_OK) {
			return I18N.getText("OK");
		} else 	if (statusId == STATUS_FAILED) {
			return I18N.getText("Fallido");
		} else 
		    return I18N.getText("Desconocido");
	}

	@Override
	public String getStatusMessage() {
		return statusMessage;
	}		
	
	@Override
	public synchronized void addObserver(Observer o) {
		super.addObserver(o);
    }

	@Override
    public synchronized void deleteObserver(Observer o) {
		super.deleteObserver(o);
	}


}
