package com.comerzzia.pos.gui.sales.loyalcustomer.generaldata;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.comerzzia.api.loyalty.client.model.CivilStatus;
import com.comerzzia.api.loyalty.client.model.LyCustomerContact;
import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.facade.model.Country;
import com.comerzzia.core.facade.model.CountryIdType;
import com.comerzzia.core.facade.service.country.CountryServiceFacade;
import com.comerzzia.core.facade.service.countryidentificationtype.CountryIdentificationTypeFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.SceneController.SceneCallback;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.core.services.countryidtypevalidator.CountryDocTypeValidatorBuilder;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.country.CountriesController;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerCivilStatusRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerController;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerCountryIdTypeRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerGenreRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.QueryCivilStatusTask;
import com.comerzzia.pos.gui.sales.loyalcustomer.collectives.LoyalCustomerCollectiveSelectionRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.collectives.LoyalCustomerCollectivesController;
import com.comerzzia.pos.gui.sales.loyalcustomer.stores.LoyalCustomerPreferredStoreController;
import com.comerzzia.pos.gui.sales.loyalcustomer.stores.LoyalCustomerPreferredStoreRow;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

@Component
public class LoyalCustomerGeneralDataPanelController extends TabController{

	protected static final Logger log = Logger.getLogger(LoyalCustomerGeneralDataPanelController.class);
	
	@Autowired
	protected Session session;
	
	@Autowired
    protected VariableServiceFacade variablesService;
	
	@Autowired
	private CountryServiceFacade countryService;
	
	@Autowired
	private CountryIdentificationTypeFacade countryIdentTypeService;
	
	@Autowired
	protected LoyalCustomerController fidelizadoController;
	
	@FXML
	@Getter
	protected TextField tfCode, tfCardNumber, tfName, tfLastName, tfDocument, tfEmail, tfMobile, tfPostalCode;
	
	@FXML
	@Getter
	protected TextField tfProvince, tfCity, tfLocation, tfCountryCode, tfCountryDes, tfAddress, tfStoreCode, tfStoreDes, tfCollectiveCode, tfCollectiveDes;
	
	@FXML
	@Getter
	protected CheckBox chNotifEmail, chNotifMobile, chNotifComEmail, chNotifComMobile, chPaperLess;
	
	@FXML
	protected Button btSearchCountry, btSearchStore, btSearchCollective, btClearStore, btClearCollective, btValidateDocument;
  	
	@FXML
	@Getter
	protected ComboBox<LoyalCustomerCountryIdTypeRow> cbDocumentType;
	
	protected ObservableList<LoyalCustomerCountryIdTypeRow> identificationTypes;
	
	@FXML
	@Getter
	protected ComboBox<LoyalCustomerGenreRow> cbGenre;
	
	@FXML
	@Getter
	protected ComboBox<LoyalCustomerCivilStatusRow> cbCivilStatus;
	
	protected ObservableList<LoyalCustomerCivilStatusRow> civilStatus;
	
	@FXML
	@Getter
	protected DatePicker dpBirthDate, dpCreationDate;
	
	@FXML
	protected Label lbCardNumber, lbCollective, lbCreationDate;
	
	@Getter
	@Setter
	protected LyCustomerDetail lyCustomer;
	
	@Autowired
	protected CountryDocTypeValidatorBuilder documentTypeValidatorBuilder;

	@Override
	public void initializeTab(URL arg0, ResourceBundle arg1) {
		ObservableList<LoyalCustomerGenreRow> genres = FXCollections.observableArrayList();
		LoyalCustomerGenreRow male = new LoyalCustomerGenreRow("H", "HOMBRE");
		LoyalCustomerGenreRow female = new LoyalCustomerGenreRow("M", "MUJER");
		genres.add(female);
		genres.add(male);
		cbGenre.setItems(genres);
		civilStatus = FXCollections.observableArrayList();
		cbCivilStatus.setItems(civilStatus);
		identificationTypes = FXCollections.observableArrayList();
		cbDocumentType.setItems(identificationTypes);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		dpCreationDate.setDateFormat(df);
		
		QueryCivilStatusTask queryCivilStatusTask = SpringContext.getBean(QueryCivilStatusTask.class, 
				new RestBackgroundTask.FailedCallback<List<CivilStatus>>() {
					
					@Override
					public void failed(Throwable throwable) {
						fidelizadoController.closeCancel();
					}
					
					@Override
					public void succeeded(List<CivilStatus> result) {
						List<LoyalCustomerCivilStatusRow> states = new ArrayList<LoyalCustomerCivilStatusRow>();
						for(CivilStatus status : result){
							LoyalCustomerCivilStatusRow statusRow = new LoyalCustomerCivilStatusRow(status);
							states.add(statusRow);
						}
						civilStatus = FXCollections.observableArrayList(states);
						cbCivilStatus.setItems(civilStatus);
						
					}
				}, getScene());
	queryCivilStatusTask.start();
	
	tfCountryCode.focusedProperty().addListener(new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
			tfCountryCode.setText(tfCountryCode.getText().toUpperCase());
		}
	});
	
	tfStoreCode.focusedProperty().addListener(new ChangeListener<Boolean>(){
		@Override
		public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
			tfStoreCode.setText(tfStoreCode.getText().toUpperCase());
			if (!newValue) {
				actionLoadStore();
			}
		}
	 });
	
	tfCollectiveCode.focusedProperty().addListener(new ChangeListener<Boolean>(){
		@Override
		public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
			tfCollectiveCode.setText(tfCollectiveCode.getText().toUpperCase());
			if (!newValue) {
				actionLoadCollective();
			}
		}
	 });
	}
	
	@Override
	public void selected() {
		LyCustomerDetail newLoyalCustomer = fidelizadoController.getLyCustomer();
		if(newLoyalCustomer != null && (lyCustomer == null || !lyCustomer.getLyCustomerId().equals(newLoyalCustomer.getLyCustomerId()))){
			clearForm();
			lyCustomer = newLoyalCustomer;
			loadGeneralData();
		}
		else if (StringUtils.isBlank(tfCountryCode.getText())){
			loadIdentTypes();
		}
		
		if("CONSULTA".equals(fidelizadoController.getMode())){
			editFields(false);
		}else{
			editFields(true);
		}
	}
	
	public void clearForm(){
		tfCode.clear();
		tfCardNumber.clear();
		tfName.clear();
		tfLastName.clear();
		tfDocument.clear();
		tfEmail.clear();
		tfMobile.clear();
		tfPostalCode.clear();
		tfProvince.clear();
		tfCity.clear();
		tfLocation.clear();
		tfCountryCode.clear();
		tfCountryDes.clear();
		tfAddress.clear();
		tfStoreCode.clear();
		tfStoreDes.clear();
		tfCollectiveCode.clear();
		tfCollectiveDes.clear();
		dpBirthDate.clear();
		dpCreationDate.clear();
		cbCivilStatus.setValue(null);
		cbGenre.setValue(null);
		cbDocumentType.setValue(null);
		chNotifEmail.setSelected(false);
		chNotifMobile.setSelected(false);
		chNotifComEmail.setSelected(false);
		chNotifComMobile.setSelected(false);
		chPaperLess.setSelected(false);
	}
	
	protected void loadGeneralData(){
		log.debug("loadGeneralData()");		
		tfCode.setText(lyCustomer.getLyCustomerCode());
		if (!CollectionUtils.isEmpty(lyCustomer.getCards())) {
			tfCardNumber.setText(lyCustomer.getCards().get(0).getCardNumber());
		}
		tfName.setText(lyCustomer.getName());
		tfLastName.setText(lyCustomer.getLastName());
		selectMaritalStatus(lyCustomer.getMaritalStatusCode());
		selectGender(lyCustomer.getGenderName());
		
		dpBirthDate.setValue(lyCustomer.getDateOfBirth());
		dpCreationDate.setValue(lyCustomer.getCreationDate());
		
		Optional<LyCustomerContact> email = lyCustomer.getContacts().stream().filter(c -> c.getContactTypeCode().equals("EMAIL")).findFirst();
		Optional<LyCustomerContact> mobile = lyCustomer.getContacts().stream().filter(c -> c.getContactTypeCode().equals("MOVIL")).findFirst();
		if("CONSULTA".equals(fidelizadoController.getMode()) && !isShowSensitiveData()){
			if(email.isPresent()){
				chNotifEmail.setSelected(email.get().getGetNotifications());
				chNotifComEmail.setSelected(email.get().getComGetNotifications());
				hideSensitiveData(tfEmail, email.get().getValue());
			}
			hideSensitiveData(tfDocument,lyCustomer.getVatNumber());
			hideSensitiveData(tfAddress,lyCustomer.getAddress());
			if(mobile.isPresent()){
				chNotifMobile.setSelected(mobile.get().getGetNotifications());
				chNotifComMobile.setSelected(mobile.get().getComGetNotifications());
				hideSensitiveData(tfMobile, mobile.get().getValue());
			}
		}else{
			tfDocument.setText(lyCustomer.getVatNumber());
			if(email.isPresent()){
				chNotifEmail.setSelected(email.get().getGetNotifications());
				chNotifComEmail.setSelected(email.get().getComGetNotifications());
				tfEmail.setText(email.get().getValue());
			}
			if(mobile.isPresent()){
				chNotifMobile.setSelected(mobile.get().getGetNotifications());
				chNotifComMobile.setSelected(mobile.get().getComGetNotifications());
				tfMobile.setText(mobile.get().getValue());
			}
			tfAddress.setText(lyCustomer.getAddress());
		}
		tfPostalCode.setText(lyCustomer.getPostalCode());
		tfProvince.setText(lyCustomer.getProvince());
		tfCity.setText(lyCustomer.getCity());
		tfLocation.setText(lyCustomer.getLocation());
		
		tfCountryCode.setText(lyCustomer.getCountryCode());
		tfCountryDes.setText(countryService.findById(lyCustomer.getCountryCode()).getCountryDes());
		
		loadIdentTypes();
		selectIdentificationType(lyCustomer.getIdentificationTypeCode());
		
		if(lyCustomer.getFavoriteStore() != null){
			tfStoreCode.setText(lyCustomer.getFavoriteStore().getStoreCode());
			tfStoreDes.setText(lyCustomer.getFavoriteStore().getStoreDes());
		}
		if(lyCustomer.getPaperLess() != null) {
			chPaperLess.setSelected(lyCustomer.getPaperLess());
		}else {
			chPaperLess.setSelected(false);
		}
	}
	
	protected void hideSensitiveData(TextField textField, String string) {
		String replace = string.substring(1, string.length()-1);
		String car = replace.replaceAll(".", "*");
		textField.setText(string.replace(replace, car));
	}
	
	@FXML
	public void actionSearchCountry(){
		fidelizadoController.openScene(CountriesController.class, new SceneCallback<Country>() {
			
			@Override
			public void onSuccess(Country country) {
				tfCountryCode.setText(country.getCountryCode());
				loadIdentTypes();
			}
		});
	}
	
	@FXML
	public void actionSearchCollective(){
		fidelizadoController.getSceneData().put(LoyalCustomerCollectivesController.PARAM_COLLECTIVES, fidelizadoController.getCollectives());
		fidelizadoController.openScene(LoyalCustomerCollectivesController.class, new SceneCallback<LoyalCustomerCollectiveSelectionRow>() {
			
			@Override
			public void onSuccess(LoyalCustomerCollectiveSelectionRow collective) {				
				tfCollectiveDes.setText(collective.getCollectiveDes());
				tfCollectiveCode.setText(collective.getCollectiveCode());
			}
		});
	}
	
	@FXML
	public void actionSearchStore(){
		fidelizadoController.getSceneData().put(LoyalCustomerPreferredStoreController.PARAM_STORES, fidelizadoController.getStores());
		fidelizadoController.openScene(LoyalCustomerPreferredStoreController.class, new SceneCallback<LoyalCustomerPreferredStoreRow>() {
			
			@Override
			public void onSuccess(LoyalCustomerPreferredStoreRow store) {
				tfStoreDes.setText(store.getStoreDes());
				tfStoreCode.setText(store.getStoreCode());
			}
		});
	}
	
	public void editFields(boolean editable){
		tfCardNumber.setEditable(editable);
		tfName.setEditable(editable);
		tfLastName.setEditable(editable);
		tfDocument.setEditable(editable);
		cbCivilStatus.setDisable(!editable);
		cbGenre.setDisable(!editable);
		cbDocumentType.setDisable(!editable);
		dpBirthDate.setDisable(!editable);
		dpCreationDate.setDisable(true);
		chNotifEmail.setDisable(!editable);
		chNotifComEmail.setDisable(!editable);
		tfEmail.setEditable(editable);
		chNotifMobile.setDisable(!editable);
		chNotifComMobile.setDisable(!editable);
		tfMobile.setEditable(editable);
		tfPostalCode.setEditable(editable);
		tfProvince.setEditable(editable);
		tfCity.setEditable(editable);
		tfLocation.setEditable(editable);
		tfCountryCode.setEditable(editable);
		tfCountryDes.setEditable(editable);
		tfAddress.setEditable(editable);
		tfStoreCode.setEditable(editable);
		tfStoreDes.setEditable(editable);
		tfCollectiveCode.setEditable(editable);
		tfCollectiveDes.setEditable(editable);		
		btSearchCollective.setDisable(!editable);
		btSearchCountry.setDisable(!editable);
		btSearchStore.setDisable(!editable);
		btClearCollective.setDisable(!editable);
		btClearStore.setDisable(!editable);
		chPaperLess.setDisable(!editable);
		if("INSERCION".equals(fidelizadoController.getMode())){
			tfCode.setEditable(true);
			tfCardNumber.setVisible(true);
			tfCollectiveCode.setVisible(true);
			tfCollectiveDes.setVisible(true);
			btSearchCollective.setVisible(true);
			btClearCollective.setVisible(true);
			lbCardNumber.setVisible(true);
			lbCollective.setVisible(true);
			lbCreationDate.setVisible(false);
			dpCreationDate.setVisible(false);
		}else{
			tfCode.setEditable(false);
			tfCardNumber.setVisible(false);
			tfCollectiveCode.setVisible(false);
			tfCollectiveDes.setVisible(false);
			btSearchCollective.setVisible(false);
			btClearCollective.setVisible(false);
			lbCardNumber.setVisible(false);
			lbCollective.setVisible(false);
			lbCreationDate.setVisible(true);
			dpCreationDate.setVisible(true);
		}
	}
	
	protected void selectGender(String genderName) {
		List<LoyalCustomerGenreRow> genders = cbGenre.getItems();
		for (LoyalCustomerGenreRow gender : genders) {
			if (gender.getCode().equals(genderName)) {
				cbGenre.getSelectionModel().select(gender);
				break;
			}
		}

	}
	
	protected void selectMaritalStatus(String maritalStatusCode) {
		for (LoyalCustomerCivilStatusRow status : civilStatus) {
			if (status.getCivilStatusCode().equals(maritalStatusCode)) {
				cbCivilStatus.getSelectionModel().select(status);
				break;
			}
		}
	}
	
	protected void selectIdentificationType(String identificationTypeCode) {
		for (LoyalCustomerCountryIdTypeRow identificationType : identificationTypes) {
			if (identificationType.getIdentTypeCode().equals(identificationTypeCode)) {
				cbDocumentType.getSelectionModel().select(identificationType);
				break;
			}
		}
	}
	
	protected List<CountryIdType> loadCountryIdTypes(String countryCode){
		List<CountryIdType> countryIdTypes = new ArrayList<CountryIdType>();
		if(countryCode != null){			
			countryIdTypes = countryIdentTypeService.findByCountryCode(countryCode.toUpperCase(), null);
			
			if (countryIdTypes.isEmpty()) {
				log.error("No se encontró los tipos de documento para el país.");
				DialogWindowBuilder.getBuilder(fidelizadoController.getStage()).simpleWarningDialog(I18N.getText("Error en la búsqueda, tipos de documento no encontrado"));
			} 
		}
		return countryIdTypes;
	}
	
	@FXML
	public void loadIdentTypes(){
		String countryCode = tfCountryCode.getText();
		if (StringUtils.isBlank(countryCode)) {
			countryCode = lyCustomer != null ? lyCustomer.getCountryCode() : session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode();
		}
		
		if(StringUtils.isNotBlank(countryCode)){			
			try {
				Country country = countryService.findById(countryCode);
				tfCountryCode.setText(countryCode);
				tfCountryDes.setText(country.getCountryDes());
				List<CountryIdType> countryIdTypes = loadCountryIdTypes(countryCode);
				List<LoyalCustomerCountryIdTypeRow> identTypesRow = new ArrayList<LoyalCustomerCountryIdTypeRow>();
				for(CountryIdType countryIdType : countryIdTypes){
					LoyalCustomerCountryIdTypeRow identTypeRow = new LoyalCustomerCountryIdTypeRow(countryIdType);
					identTypesRow.add(identTypeRow);
				}
				identificationTypes = FXCollections.observableArrayList(identTypesRow);
				cbDocumentType.setItems(identificationTypes);
			} 
			catch (NotFoundException e) {
				log.error("Error buscando los paises.");
				DialogWindowBuilder.getBuilder(fidelizadoController.getStage()).simpleWarningDialog(I18N.getText("Error en la búsqueda, país no encontrado"));
			}
		}
		
	}
	
	@FXML
	public void actionClearStore(){
		tfStoreCode.clear();
		tfStoreDes.clear();
	}
	
	@FXML
	public void actionClearCollective(){
		tfCollectiveCode.clear();
		tfCollectiveDes.clear();
	}
	
	@FXML
	public void actionLoadStore(){
		String storeCode = tfStoreCode.getText();
		if(StringUtils.isNotBlank(storeCode)){
			boolean valid = false;
			for(LoyalCustomerPreferredStoreRow store : fidelizadoController.getStores()){
				if(store.getStoreCode().equals(storeCode)){
					tfStoreCode.setText(store.getStoreCode());
					tfStoreDes.setText(store.getStoreDes());
					valid = true;
					break;
				}
			}
			if(!valid){
				tfStoreCode.clear();
				tfStoreDes.clear();
			}
		}
	}
	
	@FXML
	public void actionLoadCollective(){
		String collectiveCode = tfCollectiveCode.getText();
		if(StringUtils.isNotBlank(collectiveCode)){
			boolean valid = false;
			for(LoyalCustomerCollectiveSelectionRow collective : fidelizadoController.getCollectives()){
				if(collective.getCollectiveCode().equals(collectiveCode)){
					tfCollectiveCode.setText(collective.getCollectiveCode());
					tfCollectiveDes.setText(collective.getCollectiveDes());
					valid = true;
					break;
				}
			}
			if(!valid){
				tfCollectiveCode.clear();
				tfCollectiveDes.clear();
			}
		}
	}
	
	  
	@FXML
 	public void actionValidateDocument() {
		if (cbDocumentType.getSelectionModel().getSelectedItem() == null) return;
		
		if (documentTypeValidatorBuilder.validate(cbDocumentType.getSelectionModel().getSelectedItem().getCountryIdType(), tfDocument.getText())) {
			DialogWindowBuilder.getBuilder(fidelizadoController.getStage()).simpleInfoDialog(I18N.getText("Documento valido"));
		} else {
			DialogWindowBuilder.getBuilder(fidelizadoController.getStage()).simpleInfoDialog(I18N.getText("Documento no valido"));
		}
 	}
	
	public boolean isShowSensitiveData() {
		return fidelizadoController.isShowSensitiveData();
	}
	
}
