package com.comerzzia.pos.gui.sales.digital;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.core.commons.i18n.I18N;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.NewBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.UpdateBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.payments.BasketPayment;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.basket.BasketManagerBuilder;
import com.comerzzia.omnichannel.facade.service.basket.exception.BasketLineException;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.services.session.SesionInitException;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.basket.PaymentsControllerAbstract;
import com.comerzzia.pos.gui.sales.retail.payments.RetailPaymentsController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DigitalItemizationControllerAbstract <T extends BasketManager<?, ?>> extends ActionSceneController {
	
	public static final String PARAM_ITEM_REQUEST = "ITEM_REQUEST";
	
    @Autowired
	protected Session session;
    
    @Autowired
    protected ModelMapper modelMapper;
    
    public T basketManager;
	
    
    protected void executeLongOperations() throws SesionInitException, InitializeGuiException {
		initializeManager();
		basketManager.createBasketTransaction(session.getApplicationSession().getValidCatalog());
	}

	public void initializeManager() throws SesionInitException{
		if(basketManager==null) {
			basketManager = BasketManagerBuilder.build(getBasketClass(), session.getApplicationSession().getStorePosBusinessData());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected Class<? extends T> getBasketClass() {
		return (Class<? extends T>) getTypeArgumentFromSuperclass(getClass(), BasketManager.class, 0);
	}
	
	public void actionAccept() {
		try {
			if(basketManager.isBasketEmpty()) {
				createAndInsertDigitalItem(createNewBasketItemRequest());
			}else {
				updateDigitalItem(createUpdateBasketItemRequest());
			}
			openPayments();
		} catch (BasketLineException e) {
			log.debug("actionAccept() - An error was thrown. ", e);
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha ocurrido un error añadiendo el artículo a la cesta."));
		}
		
	}
	
	public void createAndInsertDigitalItem(NewBasketItemRequest itemRequest) throws BasketLineException{
		basketManager.createAndInsertBasketItem(itemRequest);
	}
	
	public void updateDigitalItem(UpdateBasketItemRequest updateRequest) {
		if(updateRequest!=null) {
			basketManager.updateBasketItem(updateRequest);
		}
	}
	
	public void openPayments() {
		if (basketManager.isBasketEmpty()) {
			log.warn("abrirPagos() - Ticket vacio");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El ticket no contiene líneas de artículo."));
			return;
		}
		Devices.getInstance().getLoyaltyCard().ignoreBackgroundTaskResult();
		sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, basketManager);
		openPaymentsScreen();
	}
	
	protected void openPaymentsScreen() {
		log.debug("openPaymentsScreen()");
		openScene(getPaymentControllerClass(), new SceneCallback<Void>() {
			
			@Override
			public void onSuccess(Void callbackData) {
				basketManager.createBasketTransaction(session.getApplicationSession().getValidCatalog());
				closeSuccess();
			}
			
			@Override
			public void onCancel() {
				initializeFocus();
			}

		});
	}
	
	protected Class<? extends PaymentsControllerAbstract<?>> getPaymentControllerClass(){
		return RetailPaymentsController.class;
	}
	
	public boolean validatePayments() {
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		if(basketTransaction != null) {
			for(BasketPayment pago : basketTransaction.getPayments().getPaymentsList()) {
				if(pago.getCanBeDeleted()) {
					log.error("canClose() - Se ha intentado cancelar una venta para la que existen pagos eliminables asociados.");
					DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se puede cancelar una venta con pagos asociados."));
					return false;
				}
			}
		}
		return true;
	}
	@Override
	public boolean canClose() {
		return validatePayments();
	}
	
	public void actionClose() {
		log.debug("actionClose()");
		if(!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Está seguro de querer cancelar la venta en curso?")) ) {
			return;
		}
		
		if(!validatePayments()) {
			return;
		}
		
		try {
			basketManager.cancelBasket();
			closeCancel();
		}catch(Throwable e) {
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("No se pudo cancelar la cesta"),e);
		}
	}

	/**
	 * Create and returns the UpdateBasketItemRequest or null if the item should not be updated.
	 * @return 
	 */
	public abstract UpdateBasketItemRequest createUpdateBasketItemRequest();
	
	/**
	 * Create and return the NewBasketItemRequest. This can be the original object received by the class or a new modified copy.
	 * @return
	 */
	public abstract NewBasketItemRequest createNewBasketItemRequest();
	
	
}
