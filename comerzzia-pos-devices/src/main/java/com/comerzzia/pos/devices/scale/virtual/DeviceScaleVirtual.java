package com.comerzzia.pos.devices.scale.virtual;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.scale.DeviceScale;
import com.comerzzia.pos.core.devices.device.scale.DeviceScaleCallback;
import com.comerzzia.pos.util.number.NumberUtils;
import com.comerzzia.pos.util.xml.XMLDocumentNode;

public class DeviceScaleVirtual extends DeviceAbstractImpl implements DeviceScale {
	
	private Logger log = Logger.getLogger(DeviceScaleVirtual.class);
	
	public static final int NUM_DECIMALES = 3;
	public static final String TAG_CT_VIRTUAL = "configuration";
	public static final String TAG_CT_VIRTUAL_FIXEDWEIGHT = "fixedWeight";
	public static final String TAG_CT_VIRTUAL_DELAY = "delay";
	
	protected BigDecimal fixedWeight;
	protected Long delay;
	protected Long successWaitTime;
	
	protected int updateNumber;

	@Override
	public Double getWeight(BigDecimal precio) {
		if(fixedWeight != null) {
			return fixedWeight.doubleValue();
		}
		
		Random random = new Random();
		double peso = 0.1 + 2.9 * random.nextDouble();
		
		double pesoFinal = NumberUtils.round(peso, NUM_DECIMALES);

		return pesoFinal;
	}
	
	protected BigDecimal getBDWeight() {
		return new BigDecimal(getWeight(null).toString());
	}

	@Override
	public boolean showWeightInScreen() {return false;}

	@Override
	public void connect() throws DeviceException {
	}

	@Override
	public void disconnect() throws DeviceException {
	}

	@Override
	protected void loadConfiguration(DeviceConfiguration config) throws DeviceException {
		try {
			XMLDocumentNode nodoConfiguracion = config.getModelConfiguration().getConnectionConfig().getNode(TAG_CT_VIRTUAL);
			XMLDocumentNode pesoFijoNodo = nodoConfiguracion.getNode(TAG_CT_VIRTUAL_FIXEDWEIGHT);
			String pesoFijoNodoValue = pesoFijoNodo.getValue();
			fixedWeight = StringUtils.isNotBlank(pesoFijoNodoValue) && !pesoFijoNodoValue.equals("0.0") ? pesoFijoNodo.getValueAsBigDecimal() : null;
			XMLDocumentNode delayNode = nodoConfiguracion.getNode(TAG_CT_VIRTUAL_DELAY);
			delay = StringUtils.isNotBlank(delayNode.getValue()) ? delayNode.getValueAsLong() : null;
			// Success wait time
			XMLDocumentNode successTag = configuration.getModelConfiguration().getConnectionConfig().getNode(DeviceScale.TAG_SUCCESSWAITTIME, true);
			successWaitTime = successTag!=null&&StringUtils.isNotBlank(successTag.getValue()) ? successTag.getValueAsLong() : DeviceScale.super.getSuccessWaitTime();
		}
		catch (Exception e) {
			log.error("cargaConfiguracion() - Ha habido un error al leer la configuración: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void weightRequiredMode(BasketItem basketItem, DeviceScaleCallback callback) {
		int updateIteration = updateNumber%3;
		addDelay();
		switch(updateIteration) {
		case 0:
			log.debug("weightRequiredMode() - Virtual scale. First iteration.");
			statusUpdateOccurred(DeviceScale.SCAL_STATUS_WEIGHT_ZERO, basketItem, callback);
			break;
		case 1:
			statusUpdateOccurred(DeviceScale.SCAL_STATUS_WEIGHT_UNSTABLE, basketItem, callback);
			break;
		case 2:
		default:
			statusUpdateOccurred(DeviceScale.SCAL_STATUS_STABLE_WEIGHT, basketItem, callback);
			break;
		}
		updateNumber++;
	}
	
	protected void statusUpdateOccurred(int status, BasketItem basketItem, DeviceScaleCallback callback) {
		Map<String, Object> statusData = new HashMap<>();
		callback.onStatusUpdate(status, statusData);
		
		switch (status) {
		case DeviceScale.SCAL_STATUS_STABLE_WEIGHT:
			BigDecimal weight = getBDWeight();
			BigDecimal price = basketItem.getPriceForPromotionApply();
			BigDecimal amount = price.multiply(weight).setScale(2, RoundingMode.HALF_UP);
			statusData.put(PARAM_WEIGHT, weight);
			statusData.put(PARAM_WEIGHT_ID, String.valueOf((new Date()).getTime()));
			statusData.put(PARAM_PRICE, price);
			statusData.put(PARAM_AMOUNT, amount);
			callback.onSuccess(weight,price, statusData);
			break;
		case DeviceScale.SCAL_STATUS_TIMEOUT:
			callback.onFailure();
			break;
		}
	}
	
	protected void addDelay() {
		try {
			if (delay != null && delay > 0) {
				Thread.sleep(delay);
			}
		} catch (InterruptedException ignore) {}
	}
	
	@Override
	public Long getSuccessWaitTime() {
		return successWaitTime;
	}
}
