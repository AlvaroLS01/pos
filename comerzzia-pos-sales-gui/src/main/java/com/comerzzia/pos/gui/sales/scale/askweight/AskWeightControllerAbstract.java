package com.comerzzia.pos.gui.sales.scale.askweight;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.core.commons.i18n.I18N;
import com.comerzzia.omnichannel.facade.model.basket.BasketCalculateRequest;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.NewBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.basket.BasketManagerBuilder;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.hashcontrolled.HashControlledDevice;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.devices.device.scale.DeviceScale;
import com.comerzzia.pos.core.devices.device.scale.DeviceScaleCallback;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.util.config.AppConfig;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import reactor.util.CollectionUtils;

public abstract class AskWeightControllerAbstract<T extends BasketManager<? extends BasketPromotable<? extends BasketItem>, ? extends BasketItem>> extends SceneController {
	
	private Logger log = Logger.getLogger(AskWeightControllerAbstract.class);
	
	public static final String PARAM_LINE = "WEIGHTED_LINE";
	public static final String PARAM_DETAIL = "ITEM_DETAIL";
	
	@FXML
	protected Button btCancel;
	
	@FXML
	protected WebView webView;
	
	@Autowired
	protected Session session;
	@Autowired
	protected ModelMapper modelMapper;

	protected T basketManager;
	protected DeviceScale scale;
	protected String libraryHash;
	protected DeviceLineDisplay displayDevice;
	
	protected NewBasketItemRequest itemRequest;
	
	protected ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	protected ScheduledFuture<?> scheduler;
	
	protected boolean schedulerCanceled;
	protected boolean blockedClosing;
	
	protected WeakReference<Catalog> catalog;
	
	protected BasketCalculateRequest calculateRequest = new BasketCalculateRequest();
	
	@Override
    public void initialize(URL url, ResourceBundle rb) {
    }

	@Override
	public void initializeComponents() throws InitializeGuiException {
		scale = Devices.getInstance().getScale();
		if(scale instanceof HashControlledDevice) {
			HashControlledDevice scale = (HashControlledDevice) Devices.getInstance().getScale();
			libraryHash = scale.getLibraryHash();
		}
		
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, (Runnable r) -> {
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			return t;
		});
		displayDevice = Devices.getInstance().getLineDisplay();
	}

	@SuppressWarnings("unchecked")
	@Override
    public void onSceneOpen() throws InitializeGuiException {
		itemRequest = (NewBasketItemRequest) sceneData.get(PARAM_DETAIL);
		basketManager = (T)sceneData.get(BasketItemizationControllerAbstract.BASKET_KEY);
		catalog = (WeakReference<Catalog>) sceneData.get(BasketItemizationControllerAbstract.CATALOG_KEY);
		
		Assert.notNull(basketManager, "No BasketManager was found on the scene request.");
		Assert.notNull(catalog, "No Catalog was found on the scene request.");		
		Assert.notNull(itemRequest, "No item request was found on the scene request.");
		Assert.notNull(itemRequest.getItemData(), "No item data was found on the scene request.");
		Assert.isTrue(itemRequest.getItemData().getWeightRequired(), "Item not is weight required");
		
		updateCalculateRequest();
		updateBasketItemRequest(null, null);
		
		BasketItem basketItem = calculateBasketItem();
		
		if(basketItem == null) {
			log.debug("onSceneOpen() - Could not load item data.");
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha ocurrido un error cargando los datos del artículo seleccionado."));
			closeCancel();
		}
		schedulerCanceled = false;
		blockedClosing = false;
		
		updateStatus(DeviceScale.SCAL_STATUS_NOT_READY, basketItem);
		
		btCancel.setDisable(false);
    }
	
	protected void updateCalculateRequest() {
		BasketPromotable<? extends BasketItem> basketTransaction = basketManager.getBasketTransaction();
		
		if (basketTransaction != null) {
			calculateRequest.setLoyaltySettings(basketTransaction.getHeader().getLoyalCustomer());
		}		
	}
		
	@SuppressWarnings("unchecked")
	protected Class<T> getBasketClass() {
		return (Class<T>) getTypeArgumentFromSuperclass(getClass(), BasketManager.class, 0);
	}
	
	@Override
	protected void onSceneShowed() {
		Long weightRequest = AppConfig.getCurrentConfiguration().getWeightRequestMilliseconds();
		if(weightRequest<300) {
			log.warn("Weight request in millisecond should not be less than 300. Forcing it to 300.");
			AppConfig.getCurrentConfiguration().setWeightRequestMilliseconds(300l);
		}
		scheduler = scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> {
			try {
				getWeightRequired();
			} catch (Exception e) {
				scheduler.cancel(true);
				log.error("ScheduledThreadPoolExecutor() - Error al solicitar el peso.", e);
			}
		}, 0, AppConfig.getCurrentConfiguration().getWeightRequestMilliseconds(), TimeUnit.MILLISECONDS);
	}
	
	protected void getWeightRequired() {
		if(schedulerCanceled) {
			scheduler.cancel(true);
			return;
		}
		
		
		scale.weightRequiredMode(calculateBasketItem(), new DeviceScaleCallback() {
			
			@Override
			public void onSuccess(BigDecimal weight, BigDecimal price, Map<String, Object> customData) {
				updateBasketItemRequest(weight, price);
				
				if (itemRequest.getCustomData() != null) {
					itemRequest.getCustomData().putAll(customData);
				} else {
					itemRequest.setCustomData(customData);
				}
				
				BasketItem basketItem = calculateBasketItem();
				
				if(basketItem==null) {
					return;
				}
				
				Platform.runLater(() -> {
					btCancel.setDisable(true);
					updateStatus(DeviceScale.SCAL_STATUS_STABLE_WEIGHT, basketItem);
				});
				
				try {
					Thread.sleep(getSuccessWaitTime());
				} catch (InterruptedException e) {
				}
				
				closeReceived();
			}
			
			@Override
			public void onStatusUpdate(int status, Map<String, Object> statusData) {
				Platform.runLater(() -> {
					if(!schedulerCanceled) {
						updateStatus(status, calculateBasketItem());
					}
				});
			}
			
			@Override
			public void onFailure() {
				schedulerCanceled=true;
				DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha ocurrido un error intentando consultar el peso en la balanza. Por favor, revise el log."));
			}
		});
		
		
	}
	
	protected long getSuccessWaitTime() {
		return scale.getSuccessWaitTime();
	}
		
	protected BasketItem calculateBasketItem() {				
    	calculateRequest.setItems(Arrays.asList(itemRequest));
		
		BasketPromotable<? extends BasketItem> calculateResult = BasketManagerBuilder.basketCalculate(getBasketClass(), 				
				session.getApplicationSession().getStorePosBusinessData(),
				calculateRequest, catalog.get());
				
		if (!CollectionUtils.isEmpty(calculateResult.getLines())) {
			return calculateResult.getLines().get(0);
		}
		
		return null;
	}

	protected void updateBasketItemRequest(BigDecimal weight, BigDecimal price) {
		itemRequest.setWeight(weight!=null?weight:BigDecimal.ONE);
		itemRequest.setUnitMeasureQuantity(itemRequest.getWeight());
		itemRequest.setQuantity(itemRequest.getWeight());
		itemRequest.setDirectPrice(price);
	}
	
	protected void closeReceived() {
		schedulerCanceled = true;
		Platform.runLater(() -> {
			blockedClosing = false;
			closeSuccess(itemRequest);
		});
	}
	
	@Override
	public void onClose() {
		schedulerCanceled = true;
	}
	
	@Override
	public boolean canClose() {
		return !blockedClosing;
	}
	
	protected void updateStatus(int status, BasketItem basketItem) {
		log.debug("Updated scale status - "+status);
		Map<String, Object> params = new HashMap<>();
		String msg = null;
		String template = "sales/scale/scaledata";
		switch (status) {
		case DeviceScale.SCAL_STATUS_STABLE_WEIGHT:
			break;
		case DeviceScale.SCAL_STATUS_TIMEOUT:
			msg = I18N.getText("Tiempo de espera de lectura superado.");
			break;
		case DeviceScale.SCAL_STATUS_NOT_READY:
			msg = I18N.getText("No se puede conectar con la balanza.");
			break;
		case DeviceScale.SCAL_STATUS_WEIGHT_OVERWEIGHT:
			msg = I18N.getText("El artículo sobrepasa el peso máximo de la balanza.");
			break;
		case DeviceScale.SCAL_STATUS_WEIGHT_UNDER_ZERO:
			msg = I18N.getText("Lectura de peso negativo. Ajuste la balanza.");
			break;
		case DeviceScale.SCAL_STATUS_WEIGHT_UNSTABLE:
			msg = I18N.getText("Esperando respuesta de la balanza.");
			break;
		case DeviceScale.SCAL_STATUS_DUPLICATED_WEIGHT:
			msg = I18N.getText("Retire el último artículo pesado de la balanza.");
			break;
		case DeviceScale.SCAL_STATUS_WEIGHT_ZERO:
		default:
			msg = I18N.getText("Posicione el artículo en la balanza.");
			break;
		}
		
		params.put("scaleMsg", msg);
		params.put("basketItem", basketItem);
		params.put("status", status);
		params.put("hashLibrary", libraryHash);
		params.put("producerName", "Comerzzia");
		params.put("typeDesignation", "POS");
		params.put("accuracyClass", scale.getAccuracyClass());
		params.put("certNumber", scale.getCertNumber());
		
		loadWebView(template, params, webView);
		
		displayDevice.weightRequiredMode(status, basketItem);
	}

	@Override
    public void initializeFocus() {
		btCancel.requestFocus();
    }
	
}
