package com.comerzzia.pos.util.actionfiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class ActionFilesManager {
	private static final Logger log = Logger.getLogger(ActionFilesManager.class);
	// default actions files
	private String[] actionFiles = { "forcedupdate", "update" };
	private int defaultExitCode = 10;
	private List<ActionFileListener> listeners = new ArrayList<ActionFileListener>();

	Timer timer = null;

	public ActionFilesManager() {
	}

	public ActionFilesManager(ActionFileListener listener) {
		addEventListener(listener);
	}

	public void scheduleCheck(int seconds) {
		scheduleCancel();
		
		log.info(String.format("scheduleCheck() - Querying files actions every %s seconds", seconds));
		
		TimerTask checkActionFilesTask = new TimerTask() {
			public void run() {
				checkActionFiles();			
			}
		};
		
		timer = new Timer("ActionFilesCheck");
		timer.schedule(checkActionFilesTask, seconds * 1000, seconds * 1000);
	}
	
	public void scheduleCancel() {
		if (timer != null) {
			log.debug("scheduleCheck() - Canceling current timer");
			timer.cancel();
			timer = null;
		}		
	}

	public ActionFilesManager(String[] actionFiles) {
		this.actionFiles = actionFiles;
	}

	public void deleteAllPendingActionsFiles() {
		for (String actionFileName : actionFiles) {
			File actionFile = new File(actionFileName);
			if (actionFile.exists()) {
				if (actionFile.delete()) {
					log.trace("deleteAllActionsFiles() - Action file " + actionFileName + " deleted.");
				} else {
					log.trace("deleteAllActionsFiles() - Action file " + actionFileName + " NOT deleted.");
				}
			}
		}
	}

	public List<ActionFileEvent> checkActionFiles() {
		List<ActionFileEvent> events = null;

		for (String actionFileName : actionFiles) {
			File actionFile = new File(actionFileName);
			log.trace("checkActionFiles() - Queryng action file " + actionFile.getAbsolutePath());

			if (actionFile.exists()) {
				log.info(String
						.format("checkActionFiles() - Action file " + actionFile.getAbsolutePath() + " detected"));
				
				if (events == null) {
					events = new ArrayList<ActionFileEvent>();
				}
				
				ActionFileEvent event = new ActionFileEvent();
				event.setEventName(actionFileName);
				event.setExitCode(defaultExitCode);
				events.add(event);
				
				// launch event listeners
				for (ActionFileListener listener : listeners) {
					listener.actionFileEventPerformed(event);

					// the listener has response to the event
					if (event.isActionFilePerformed()) {
						break;
					}
				}
				
				if (listeners.size() > 0 && !event.isActionFilePerformed()) {					
					log.trace("checkActionFiles() - Action " + event.getEventName() + " not managed by any listener");
				}

				break;
			}
		}

		return events;
	}

	public void addEventListener(ActionFileListener listener) {
		listeners.add(listener);
	}

}
