package com.comerzzia.pos.core.gui.stockavailability;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.comerzzia.catalog.facade.model.CatalogStoreGeolocated;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.ModalSceneController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

@Component
public class StoreContactController extends ModalSceneController {
	
	public static final String STOCK = "STOCK";
	
	@FXML
	protected Label lbTitle;
	
	@FXML
	protected TextField tfAddress;
	
	@FXML
	protected TextField tfCity;
	
	@FXML
	protected TextField tfProvince;
	
	@FXML
	protected TextField tfPostalCode;
	
	@FXML
	protected TextField tfPhone;
	
	@FXML
	protected TextField tfContactPerson;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {		
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		StockRow stock = (StockRow) getSceneData().get(STOCK);
		CatalogStoreGeolocated store = stock.getStore();
		lbTitle.setText(stock.getStoreCode() + " - " + stock.getStoreDes());
		tfAddress.setText(store.getAddress());
		tfCity.setText(store.getCity());
		tfProvince.setText(store.getProvince() + " (" + store.getCountryCode() + ")");
		tfPostalCode.setText(store.getPostalCode());
		tfPhone.setText(store.getPhone1());
		tfContactPerson.setText(store.getContactPerson());
	}

	@Override
	public void initializeFocus() {		
	}

}
