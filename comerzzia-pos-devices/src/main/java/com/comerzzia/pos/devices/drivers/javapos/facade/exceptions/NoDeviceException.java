
package com.comerzzia.pos.devices.drivers.javapos.facade.exceptions;

public class NoDeviceException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoDeviceException(){
		super("There is no device in CommonDeviceCatFacade. Use setDevice to set the proper device");
	}
}
