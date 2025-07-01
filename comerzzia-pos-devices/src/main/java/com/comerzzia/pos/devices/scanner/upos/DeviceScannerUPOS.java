package com.comerzzia.pos.devices.scanner.upos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScannerDataReaded;
import com.comerzzia.pos.devices.scanner.DeviceScannerAbstractImpl;

import jpos.JposException;
import jpos.Scanner;
import jpos.ScannerConst;
import jpos.events.DataEvent;
import jpos.events.DataListener;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

public class DeviceScannerUPOS extends DeviceScannerAbstractImpl implements DataListener, ErrorListener, StatusUpdateListener {
	protected String logicalName;

	protected Scanner scanner;

	@Override
	public synchronized void connect() throws DeviceException {
		initialize();
		
		if (!isReady()) {
			throw new DeviceException(getAvailabilityErrorsAsAstring());
		}
	}
	
	@Override
	public boolean isReady() {
		if (!super.isReady()) {
			return false;
		}

		try {
			return scanner.getClaimed();
		} catch (JposException e) {
			log.debug("Error no esperado: " + e.getMessage(), e);
		}
		return false;
	}

	@Override
	public void disconnect() throws DeviceException {
		log.debug("desconecta() - Desconectando escáner.");

		// remove this class as a data event listener.
		scanner.removeDataListener(this);

		// for this example, going to ignore Throwable handling. For actual
		// applications, a similar format of handling each statement found in
		// connectScanner() should be followed.
		try {
			scanner.setDeviceEnabled(false);
			scanner.release();
			scanner.close();
		}
		catch (Throwable e) {
			// ignoring Throwables for this example
			log.error("desconecta() - No se ha podido cerrar el escáner: " + e.getMessage(), e);
		}

		log.debug("desconecta() - Escáner desconectado.");
	}

	@Override
	protected void loadConfiguration(DeviceConfiguration config) throws DeviceException {
		super.loadConfiguration(config);
		
		try {
			logicalName = config.getModelConfiguration().getConnectionName();
			scanner = new Scanner();
		}
		catch (Throwable e) {
			log.error("cargaConfiguracion() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			throw new DeviceException(e);
		}
	}
	
    protected void initialize() {
		log.debug("initialize() - Conectando el escáner: " + logicalName);

		// open the jpos.xml profile for the desired scanner
		try {
			log.debug("initialize() - Operación: open");
			scanner.open(logicalName);
			scanner.claim(1000);
			scanner.setDecodeData(true);
			scanner.setDeviceEnabled(true);
			scanner.setDataEventEnabled(true);
			scanner.addDataListener(this);
		}
		catch (Throwable e) {
			log.error("initialize() - Error al realizar la operación open: " + e.getMessage(), e);
		}
	}

	@Override
	public void dataOccurred(DataEvent event) {
		byte[] scanData = new byte[] {};
		byte[] scanDataLabel = new byte[] {};
		int scanDataType = -1;
		
		try {
			scanData = scanner.getScanData(); // Get raw label data
			scanDataLabel = scanner.getScanDataLabel(); // get decoded label data
			scanDataType = scanner.getScanDataType(); // get label symbology

			// Verify the triggered event was a label read
			if (scanDataLabel.length > 0) {
				DeviceScannerDataReaded scannerDataReaded = new DeviceScannerDataReaded();
				scannerDataReaded.setScanRawData(scanData);
				scannerDataReaded.setScanData(new String(scanDataLabel, StandardCharsets.UTF_8));
				scannerDataReaded.setScanDataTypeId(scanDataType);
				scannerDataReaded.setScanDataType(getBarcodeTypeName(scanDataType));
				
				notifyScannerDataReaded(scannerDataReaded);
			}
		}
		catch (Throwable e) {
			log.error("dataOccurred() - Ha ocurrido un error al leer la información: " + e.getMessage(), e);
		}

		// Data event must be enabled after dataOccurred event
		try {
			scanner.setDataEventEnabled(true);
		} catch (JposException e) {
			log.error("dataOccurred() - Error enabling data events: " + e.getMessage(), e);
		}
	}

	/**
	 * Convenience method that decodes the barcode symbology name from the barcode type.
	 *
	 * @param code
	 *            int indicating the jpos symbology type code
	 * @return String containing barcode symbology name
	 */
	protected String getBarcodeTypeName(int code) {
		String val = "";
		// this is just a quick representation of different symbologies that are
		// contained within ScannerConst. Please refer to ScannerConst for full
		// list.
		switch (code) {
			case ScannerConst.SCAN_SDT_UPCA:
				val = "UPC-A";
				break;
			case ScannerConst.SCAN_SDT_UPCE:
				val = " UPC-E";
				break;
			case ScannerConst.SCAN_SDT_Code39:
				val = "Code 39";
				break;
			case ScannerConst.SCAN_SDT_Code128:
				val = "Code 128";
				break;
			case ScannerConst.SCAN_SDT_EAN8_S:
				val = "EAN-8 with Supplemental";
				break;
			case ScannerConst.SCAN_SDT_EAN13_S:
				val = "EAN-13 with Supplemental";
				break;
			case ScannerConst.SCAN_SDT_EAN128:
				val = "EAN-128";
				break;
			case ScannerConst.SCAN_SDT_QRCODE:
				val = "QR Code";
				break;
			case ScannerConst.SCAN_SDT_OTHER:
				val = "Other";
				break;
			default:
				val = "Unknown";
		}
		return val;
	}

	@Override
    public void statusUpdateOccurred(StatusUpdateEvent e) {
		log.debug("ESTADO: " + e.getSequenceNumber());
    }

	@Override
    public void errorOccurred(ErrorEvent e) {
		log.debug("ERROR: " + e.getSequenceNumber());
    }

	@Override
	public void enableReader() {
		try {
			if (scanner.getClaimed()) {
				log.debug("Enable reader and data event");
				if (!scanner.getDeviceEnabled()) {
					scanner.setDeviceEnabled(true);
				}
				scanner.setDataEventEnabled(true);
			} else {
				log.debug("Device not claimed");
			}
		}
		catch (Throwable e) {
			log.error("Error while enable data event: " + e.getMessage(), e);
		}
		
	}

	@Override
	public void disableReader() {
		try {
			if (scanner.getClaimed()) {
				log.debug("Disable reader and data event");
				scanner.setDeviceEnabled(false);
				scanner.setDataEventEnabled(false);
				beepError();
			} else {
				log.debug("Device not claimed");
			}
		}
		catch (Throwable e) {
			log.error("Error while disable data event: " + e.getMessage(), e);
		}		
	}

	@Override
	protected void executeCommand(byte[] command) {
		try (ByteArrayOutputStream dioResult = new ByteArrayOutputStream()) {
			int[] intArr = new int[50];
			
			scanner.directIO(command[0], intArr, dioResult);
		} catch (JposException | IOException e) {
			log.error("Error en beep: " + e.getMessage(), e);
		}

	}

}
