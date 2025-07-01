
package com.comerzzia.pos.core.devices.device.linedisplay;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.pos.core.devices.device.Device;

public interface DeviceLineDisplay extends Device {
	
	public static final String CONN_TYPE_SERIAL_PORT = "serialPort";
    public static final String TAG_CT_SERIAL_PORT = "serialPort";
    public static final String TAG_CT_SP_COMPORT = "COMPort";
    public static final String TAG_CT_SP_BAUDS = "bauds";
    public static final String TAG_CT_SP_DATABITS = "dataBits";
    public static final String TAG_CT_SP_STOPBITS = "stopBits";
    public static final String TAG_CT_SP_PARITY = "parity";
    public static final String CONN_TYPE_USB = "USB";
    public static final String TAG_CT_USB = "USB";
    public static final String TAG_CT_USB_VENDORID = "vendorId";
    public static final String TAG_CT_USB_PRODUCTID = "productId";
    public static final String TAG_CT_USB_BUS = "bus";
    public static final String TAG_CT_USB_FILENAME = "filename";
    public static final String TAG_CT_USB_WRITINGZONE = "writingZone";
    public static final String TAG_CT_USB_NUMBER = "number";

	void write(String cad1, String cad2);

	void writeLineUp(String cadena);

	void writeLineDown(String cadena);

	void clear();

	Integer getNumColumns();

	Integer getNumRows();

	void writeRightUp(String cadena);

	void writeRightDown(String cadena);

	void tenderMode(BasketPromotable<?> basket);

	void saleMode(BasketPromotable<?> basket);

	default void weightRequiredMode(int status, BasketItem basketItem) {}

	void standbyMode();
}
