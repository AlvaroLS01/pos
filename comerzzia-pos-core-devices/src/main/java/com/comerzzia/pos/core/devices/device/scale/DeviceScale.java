


package com.comerzzia.pos.core.devices.device.scale;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.pos.core.devices.device.Device;

public interface DeviceScale extends Device {
    public static final int SCAL_STATUS_STABLE_WEIGHT     = 0;
    
    public static final int SCAL_STATUS_TIMEOUT           = -100;
    
    public static final int SCAL_STATUS_WEIGHT_UNSTABLE   = -1;
    public static final int SCAL_STATUS_WEIGHT_ZERO       = -2;
    public static final int SCAL_STATUS_WEIGHT_OVERWEIGHT = -3;
    public static final int SCAL_STATUS_NOT_READY         = -4;
    public static final int SCAL_STATUS_WEIGHT_UNDER_ZERO = -5;
    public static final int SCAL_STATUS_DUPLICATED_WEIGHT = -6;
    
    public static final String CONN_TYPE_SERIAL_PORT = "serialPort";
	public static final String TAG_CT_SERIAL_PORT = "serialPort";
    public static final String TAG_CT_SP_COMPORT = "COMPort";
    public static final String TAG_CT_SP_BAUDS = "bauds";
    public static final String TAG_CT_SP_DATABITS = "dataBits";
    public static final String TAG_CT_SP_STOPBITS = "stopBits";
    public static final String TAG_CT_SP_PARITY = "parity";
    public static final String TAG_CT_SP_REQUESTINTERVAL = "requestInterval";
    public static final String TAG_CT_SP_COMMAND = "command";
    public static final String TAG_CT_SP_RESPONSEFORMAT = "responseFormat";
    public static final String TAG_SHOWWEIGHT = "showWeight";
    public static final String TAG_SUCCESSWAITTIME = "successWaitTime";
    public static final String TAG_PRODUCERNAME = "producerName";
    public static final String TAG_TYPEDESIGNATION = "typeDesignation";
    public static final String TAG_CERTNUMBER = "certNumber";
    
    public static final String PARAM_WEIGHT = "weight";
    public static final String PARAM_PRICE = "price";
    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_WEIGHT_ID = "weight_id";
    public static final String PARAM_HASH = "hash";
    
	public static final String DEFAULT_ACCURACY_CLASS = "";
	    
    /**
     * Devuelve el peso marcado por la balanza
     * @param precio Precio unitario del artículo pesado. Se utiliza en algunas balanzas para mostrar el importe total para el peso marcado.
     * @return
     */
    @Deprecated
    public Double getWeight(BigDecimal price);
    
    @Deprecated
	default public boolean showWeightInScreen() { return false; };
	
	default void weightRequiredMode(BasketItem basketItem, DeviceScaleCallback callback) {
		Double weight = getWeight(basketItem.getRateItem().getSalesPriceWithTaxes());
		if(weight == null) {
			callback.onStatusUpdate(DeviceScale.SCAL_STATUS_NOT_READY, null);
		} else if(BigDecimal.ZERO.compareTo(BigDecimal.valueOf(weight))==0) {
			callback.onStatusUpdate(DeviceScale.SCAL_STATUS_WEIGHT_ZERO, null);
		} else if(BigDecimal.ZERO.compareTo(BigDecimal.valueOf(weight))>0) {
			callback.onStatusUpdate(DeviceScale.SCAL_STATUS_WEIGHT_UNDER_ZERO, null);
		} else {
			BigDecimal price = basketItem.getPriceWithTaxes();
			BigDecimal amount = price.multiply(new BigDecimal(weight)).setScale(2, RoundingMode.HALF_UP);
			Map<String, Object> params = new HashMap<>();
			params.put(PARAM_WEIGHT, BigDecimal.valueOf(weight));
			params.put(PARAM_WEIGHT_ID, String.valueOf((new Date()).getTime()));
			params.put(PARAM_AMOUNT, amount);
			params.put(PARAM_PRICE, price);
			callback.onSuccess(BigDecimal.valueOf(weight), price, params);
		}
	}
	
	default public void displayText(String text) {}
	
	default public void zeroScale() {}
		
	default public Boolean supportsWeightDisplay() { return false; };
	
	default public Boolean supportsTextDisplay() { return false; };
	
	default public Boolean supportsZeroScale() { return false; };		
	
	default public Long getSuccessWaitTime() {return 3000l;}

	default public String getAccuracyClass() {return "";};

	default public String getCertNumber() {return "";};
	
}
