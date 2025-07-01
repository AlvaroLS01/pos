package com.comerzzia.pos.gui.sales.basket;

import java.util.Arrays;
import java.util.Map;

import com.comerzzia.catalog.facade.service.Catalog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.comerzzia.omnichannel.facade.model.basket.BasketCreateRequest;
import com.comerzzia.omnichannel.facade.model.basket.NewBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCustomer;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.pos.core.gui.InitializeGuiException;

import javafx.scene.web.WebView;

public abstract class IdentifiedBasketItemizationControllerAbstract<T extends BasketManager<?, ?>> extends BasketItemizationControllerAbstract<T> {	
	public static final String PARAM_CUSTOMER = "CUSTOMER";

	protected Customer customer;
	
			
	@Override
	public void cancelSale() {
		super.cancelSale();

		customer = null;			
		
		closeCancel();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		super.onSceneOpen();
		
		T basketManager = (T) sceneData.get(BASKET_KEY);
		
		if(basketManager != null) {
			this.basketManager = basketManager;
		}
		
		this.customer = (Customer) sceneData.get(PARAM_CUSTOMER);
		
		Assert.notNull(customer, "Customer not assigned");
	}
	
	@Override
	public void close() {
		try {
			super.close();
		} finally {				
			openActionScene(getAction().getActionId());
		}
	}
	
	@Override
	protected void onPaymentsSuccess() {
		closeSuccess();	
	}
	
		
	
	@Override
	public void loadWebView(String template, Map<String, Object> params, WebView webView) {
		parkedTickets = null; //This screen should not manage parked tickets
		
		if (params != null) {
			params.put("customer", customer);
		}

		super.loadWebView(template, params, webView);
	}
	
	@Override
	protected BasketItem createAndPersistNewBasket(NewBasketItemRequest basketItemRequest) {
		Assert.notNull(basketItemRequest, "basket item request is null");
		Assert.notNull(customer, "customer is null");
		
		BasketCreateRequest createRequest = new BasketCreateRequest();
		
		createRequest.setCustomer(modelMapper.map(customer, BasketCustomer.class));
		
		if (!StringUtils.isBlank(customer.getDefaultDocTypeCode())) {
			createRequest.setDocTypeCode(customer.getDefaultDocTypeCode());
		}
		
		createRequest.setInitialItems(Arrays.asList(basketItemRequest));
		
		basketManager.createBasketTransaction(createRequest, catalog, true);
		
		return basketManager.getBasketTransaction().getLines().get(0);	
	}
	
	@Override
	public boolean parkBasket() {
		boolean result = super.parkBasket();
		
		if (result) closeCancel(); // go to client identification
		
		return result;
	}

	@Override
	protected Catalog getValidCatalog() {
		if (customer == null) return null;

		return getValidCatalogForCustomer(customer);
	}

	@Override
	protected void succededLongOperations() {
		super.succededLongOperations();
		checkCustomerRate(customer.getRateCode());
	}
}
