
package com.comerzzia.pos.devices.drivers.javapos.facade.jpos;

import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoDeviceException;
import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoLogicalNameException;
import com.comerzzia.pos.devices.drivers.javapos.facade.listener.CashDrawerEventListener;

import jpos.CashDrawer;
import jpos.JposException;

public class CashDrawerFacade extends CommonDeviceCatFacade {
	/**
	 * 
	 */
	private CashDrawerEventListener eventListener;
	
	/**
	 * 
	 */
	public CashDrawerFacade(){
		super();
		this.setDevice(new CashDrawer());
		this.setLogicalName("defaultCashDrawer");
	}
	
	/**
	 * @param logicalName
	 */
	public CashDrawerFacade(String logicalName){
		super();
		this.setDevice(new CashDrawer());
		this.setLogicalName(logicalName);
	}
	
	/**
	 * @param cashDrawer
	 */
	public CashDrawerFacade(CashDrawer cashDrawer){
		super();
		this.setDevice(cashDrawer);
		this.setLogicalName("defaultCashDrawer");
	}
	
	/**
	 * @param cashDrawer
	 * @param logicalName
	 */
	public CashDrawerFacade(CashDrawer cashDrawer, String logicalName){
		super();
		this.setDevice(cashDrawer);
		this.setLogicalName(logicalName);
	}

	/* (non-Javadoc)
	 * @see idinfor.jpos.CommonDeviceCatFacade#prepareDevice()
	 */
	public void prepareDevice() throws JposException, NoLogicalNameException, NoDeviceException{		
		super.prepareDevice();
		getDevice().addStatusUpdateListener(eventListener);
	}
	
	/* (non-Javadoc)
	 * @see idinfor.jpos.CommonDeviceCatFacade#getDevice()
	 */
	@Override
	public CashDrawer getDevice(){
		return (CashDrawer) super.getDevice();
	}
	
	/**
	 * @return
	 */
	public CashDrawerEventListener getEventListener() {
		return eventListener;
	}

	/**
	 * @param eventListener
	 */
	public void setEventListener(CashDrawerEventListener eventListener) {
		this.eventListener = eventListener;
	}
	
	/**
	 * @throws JposException
	 */
	public void openCashDrawer() throws JposException{
		getDevice().openDrawer();
	}
	
	/**
	 * @return
	 * @throws JposException
	 */
	public Boolean getIsCashDrawerOpened() throws JposException{
		return getDevice().getDrawerOpened();
	}
	
	/**
	 * @throws JposException
	 */
	public void waitForClose() throws JposException{
		getDevice().waitForDrawerClose(100,500,100,200);
	}
	
}
