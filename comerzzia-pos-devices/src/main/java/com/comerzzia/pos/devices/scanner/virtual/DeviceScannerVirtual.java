package com.comerzzia.pos.devices.scanner.virtual;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScannerDataReaded;
import com.comerzzia.pos.core.gui.MainStageManager;
import com.comerzzia.pos.core.gui.components.buttonsgroup.KeyCodeCombinationParser;
import com.comerzzia.pos.devices.scanner.DeviceScannerAbstractImpl;
import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class DeviceScannerVirtual extends DeviceScannerAbstractImpl {
	
	private static final String TAG_SCAN_KEYCODE = "keyCode";
	
	protected EventHandler<KeyEvent> evh = null;
	protected MainStageManager mainStageManager = CoreContextHolder.getInstance("mainStageManager");
	protected KeyCodeCombination combination;
	protected boolean enabled = false;
	
	@Override
	public void enableReader() {
		enabled = true;
	}

	@Override
	public void disableReader() {
		enabled = false;
	}

	@Override
	public void connect() throws DeviceException {
        
        evh = new EventHandler<KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                if (enabled && combination.match(event)) {
                	forceRead();
                }
            }
        };
		
		mainStageManager.getStage().addEventFilter(KeyEvent.KEY_RELEASED, evh);
		enableReader();
	}

	@Override
	public void disconnect() throws DeviceException {
		mainStageManager.getStage().removeEventFilter(KeyEvent.KEY_RELEASED, evh);
	}

	@Override
	protected void loadConfiguration(DeviceConfiguration config) throws DeviceException {
		String nodeStr = null;
		try {
			XMLDocumentNode nodoConfiguracion = config.getModelConfiguration().getConnectionConfig().getNode(TAG_SCAN_KEYCODE, true);
			if(nodoConfiguracion != null) {
				nodeStr = nodoConfiguracion.getValue();
			}
		} catch (XMLDocumentNodeNotFoundException e) {
		}
		
		KeyCodeCombinationParser parser = new KeyCodeCombinationParser();
		combination = parser.parse(nodeStr, new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
	}
	
	/**
	 * Force scanner to update the observers with the content of the clipboard
	 */
	public void forceRead() {
		String data = "no clipboard content";
		try {
			data = (String) Toolkit.getDefaultToolkit()
			        .getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (Exception ignore) {}
		
		DeviceScannerDataReaded scannerDataReaded = new DeviceScannerDataReaded();
		scannerDataReaded.setScanRawData(data.getBytes());
		scannerDataReaded.setScanData(data);
		scannerDataReaded.setScanDataTypeId(501);
		scannerDataReaded.setScanDataType("Other");
		
		notifyScannerDataReaded(scannerDataReaded);
	}

	@Override
	protected void executeCommand(byte[] command) {		
	}
	
}
