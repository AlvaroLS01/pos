
package com.comerzzia.pos.devices.drivers.javapos.facade.exceptions;

public class NoLogicalNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoLogicalNameException(String logicalName){
		super("There is no logicalName: "+logicalName+" in CommonDeviceCatFacade. Use setLogicalName to set the proper logical name specified on jpos.xml for that device");
	}
}
