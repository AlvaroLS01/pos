package com.comerzzia.pos.core.gui.stockavailability;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.catalog.facade.model.CatalogItemStockAvailabilities;
import com.comerzzia.catalog.facade.model.filter.CatalogItemStockAvailabilityFilter;
import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.core.commons.sessions.ComerzziaSession;
import com.comerzzia.core.commons.sessions.ComerzziaThreadSession;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryLoggedStoreStockTask extends RestBackgroundTask<CatalogItemStockAvailabilities>{
	
	@Autowired
	protected Session session;
		
	@Autowired
	protected VariableServiceFacade variableService;
	
	protected String itemCode;
	protected String storeCode;
	protected String combination1;
	protected String combination2;
	protected BigDecimal latitude;
	protected BigDecimal longitude; 
	protected String province;
	protected String storesExpression;
	protected Boolean onlyAvailables;

	public QueryLoggedStoreStockTask(String itemCode, String storeCode, String combination1, String combination2, BigDecimal latitude, BigDecimal longitude, 
			String province, String storesExpression, Boolean onlyAvailables, Callback<CatalogItemStockAvailabilities> callback, Stage stage) {
		super(callback, stage);
		this.itemCode = itemCode;
		this.storeCode = storeCode;
		this.combination1 = combination1;
		this.combination2 = combination2;
		this.latitude = latitude;
		this.longitude = longitude;
		this.province = province;
		this.storesExpression = storesExpression;
		this.onlyAvailables = onlyAvailables;
	}

	@Override
	protected CatalogItemStockAvailabilities execute() throws Exception {
		if (ComerzziaThreadSession.getToken() == null) {
			ComerzziaSession cmzSession = new ComerzziaSession();
			cmzSession.setActivityUid(session.getApplicationSession().getConfiguredActivityUid());
			ComerzziaThreadSession.setComerzziaSession(cmzSession);
    		ComerzziaThreadSession.setApiKey(variableService.getVariableAsString(VariableServiceFacade.WEBSERVICES_APIKEY));
    	}
		Catalog catalog = session.getApplicationSession().getValidCatalog();
		
		CatalogItemStockAvailabilityFilter filter = new CatalogItemStockAvailabilityFilter();
		filter.setOnlyAvailables(onlyAvailables);
		filter.setCombination1(combination1);
		filter.setCombination2(combination2);
		filter.setLatitude(latitude);
		filter.setLongitude(longitude);
		filter.setProvince(province);
		filter.setStoresExpression(storesExpression);
		
		return catalog.getCatalogStockService().findStockAvailabilityAdvanced(itemCode, storeCode, filter);		
	}
	
	@Override
	protected void tratarRestTimeoutException(Throwable e) {		
		DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("La información de stock no está disponible en estos momentos."));
	}

	@Override
	protected void tratarRestConnectException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("La información de stock no está disponible en estos momentos."));
	}
	
	@Override
	protected void tratarNotFoundException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El artículo introducido no existe."));
	}

}
