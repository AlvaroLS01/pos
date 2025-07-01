package com.comerzzia.pos.util.actionfiles;

import java.io.File;

import org.apache.log4j.Logger;

public class ActionFileEvent {
	private static final Logger log = Logger.getLogger(ActionFileEvent.class);

	protected String eventName;
	protected int exitCode;

	protected boolean actionFilePerformed = false;
   
     public String getEventName() {
	   return eventName;
    }

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public int getExitCode() {
		return exitCode;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	public boolean actionPerformed() {
		File actionFile = new File(eventName);

		int attempts = 1;
		while (actionFile.exists() && !actionFile.delete() && attempts <= 10) {
			log.error("Error deleting action file " + actionFile.getAbsolutePath() + ". Retrying....");
			try {
				attempts++;
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("Action file deleting interrupted");
				break;
			}
		}
		
		actionFilePerformed = true;

		return attempts <= 10;
	}

	public boolean isActionFilePerformed() {
		return actionFilePerformed;
	}
}
