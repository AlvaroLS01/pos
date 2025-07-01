package com.comerzzia.pos.devices.scanner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScanner;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScannerDataReaded;
import com.comerzzia.pos.core.gui.MainStageManager;
import com.comerzzia.pos.core.gui.controllers.StageController;
import com.comerzzia.pos.core.gui.util.czzrobot.CzzRobot;
import com.comerzzia.pos.core.gui.util.czzrobot.CzzRobotFactory;
import com.comerzzia.pos.devices.drivers.direct.CodesDirect;
import com.comerzzia.pos.util.events.CzzScannedEvent;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;

public abstract class DeviceScannerAbstractImpl extends DeviceAbstractImpl implements DeviceScanner {
	protected static Logger log = Logger.getLogger(DeviceScannerAbstractImpl.class);
	
	protected CodesDirect commandCodes;
	protected MainStageManager manager;
		
	@Override
	protected void loadConfiguration(DeviceConfiguration config) throws DeviceException {
        commandCodes = new CodesDirect();
        commandCodes.configure(getConfiguration().getModelConfiguration().getModelConfiguration());
	}
	
	protected void notifyScannerDataReaded(DeviceScannerDataReaded scannerDataReaded) {
		log.debug("Raw Data: " + scannerDataReaded.getScanRawData() + ", Label Data: " + scannerDataReaded.getScanData() + ", Type: " + scannerDataReaded.getScanDataType());
				
		fireEvent(scannerDataReaded);
		
		if (countObservers() == 0) {
			// send data to keyboard and enable scanner again
			Platform.runLater(() -> sendDataToKeyboard(scannerDataReaded));
		} else {
			setChanged();
			notifyObservers(scannerDataReaded);
		}
	}
	
	protected void sendDataToKeyboard(final DeviceScannerDataReaded scannerDataReaded) {
		StageController focusedController = getManager().getFocusedStageController();
		if (focusedController == null) return;
		
		CzzRobot robot = CzzRobotFactory.createRobot(focusedController.getStage().getScene());

		if (robot != null) {
			for (int i = 0; i < scannerDataReaded.getScanData().length(); i++) {
				String c = Character.toString(scannerDataReaded.getScanData().charAt(i));
				KeyCode keyCode = KeyCode.getKeyCode(c);
				robot.keyType(keyCode == null ? KeyCode.UNDEFINED : keyCode, c);
				try {
					Thread.sleep(2);
				}
				catch (InterruptedException ignore) {
					// ignore errors
				}
			}
			robot.keyType(KeyCode.ENTER, "\n");
		}
	}
	
	@Override
	public void beep() {
		executeCommand(DeviceScanner.COMMAND_BEEP);		 
	}

	@Override
	public void beepError() {
		executeCommand(DeviceScanner.COMMAND_BEEP_ERROR);
	}
	
	protected abstract void executeCommand(byte[] command);
	
    protected void executeCommand(String commandCode) {
    	if (StringUtils.isEmpty(commandCode) || commandCodes == null) return;
    	
    	if (!commandCodes.getCodes().containsKey(commandCode)) return;
    	
    	String command = commandCodes.getCodes().get(commandCode);
    	
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String[] split = command.split(";");
            
            for (String byteStr : split) {
                os.write((byte) Integer.parseInt(byteStr));
            }
            
            executeCommand(os.toByteArray());
        } catch (IOException | NumberFormatException e) {
        	throw new IllegalArgumentException(e.getMessage(), e);
		}
    }
    
    protected MainStageManager getManager() {
    	if (this.manager == null) {
    		this.manager = CoreContextHolder.getInstance("mainStageManager");    		
    	}
    	
    	return this.manager;
    	
    }

	/*
	 * Fire event to initialize inactivity timer
	 */
    protected void fireEvent(DeviceScannerDataReaded scannerDataReaded) {
		Platform.runLater(() -> getManager().getStage().fireEvent(new CzzScannedEvent(CzzScannedEvent.SCANNED_EVENT, scannerDataReaded)));			
	}    
}
