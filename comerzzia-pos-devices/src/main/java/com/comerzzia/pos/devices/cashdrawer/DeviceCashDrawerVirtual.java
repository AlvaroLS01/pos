package com.comerzzia.pos.devices.cashdrawer;

import org.apache.log4j.Logger;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.cashdrawer.DeviceCashDrawer;
import com.comerzzia.pos.core.gui.MainStageManager;
import com.comerzzia.pos.core.gui.components.buttonsgroup.KeyCodeCombinationParser;
import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class DeviceCashDrawerVirtual extends DeviceAbstractImpl implements DeviceCashDrawer {
	
	private static final Logger log = Logger.getLogger(DeviceCashDrawerVirtual.class.getName());

	private static final String TAG_CLOSE_KEYCODE = "keyCode";

	protected EventHandler<KeyEvent> evh = null;
	protected MainStageManager mainStageManager = CoreContextHolder.getInstance("mainStageManager");

	protected KeyCodeCombination combination;

	protected boolean opened = false;
	protected boolean enabled = false;

	@Override
	public void open() {
		log.debug("=========== VIRTUAL CASH DRAWER OPENED ===========");
		opened = true;
	}

	public void close() {
		log.debug("=========== VIRTUAL CASH DRAWER CLOSED ===========");
		opened = false;
	}

	@Override
	public boolean isOpened() {
		return opened;
	}

	@Override
	public void connect() throws DeviceException {
		evh = new EventHandler<KeyEvent>() {
			@Override
			public void handle(javafx.scene.input.KeyEvent event) {
				if (enabled && opened && combination.match(event)) {
					close();
				}
			}
		};

		mainStageManager.getStage().addEventFilter(KeyEvent.KEY_RELEASED, evh);
		enabled = true;
	}

	@Override
	public void disconnect() throws DeviceException {
		mainStageManager.getStage().removeEventFilter(KeyEvent.KEY_RELEASED, evh);
		enabled = false;
	}

	@Override
	protected void loadConfiguration(DeviceConfiguration config) throws DeviceException {
		String nodeStr = null;
		try {
			XMLDocumentNode nodoConfiguracion = config.getModelConfiguration().getConnectionConfig()
					.getNode(TAG_CLOSE_KEYCODE, true);
			if (nodoConfiguracion != null) {
				nodeStr = nodoConfiguracion.getValue();
			}
		} catch (XMLDocumentNodeNotFoundException e) {
		}

		KeyCodeCombinationParser parser = new KeyCodeCombinationParser();
		combination = parser.parse(nodeStr,
				new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));

	}
}
