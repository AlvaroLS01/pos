package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.comerzzia.api.loyalty.client.CardTypesApiClient;
import com.comerzzia.api.loyalty.client.LyCustomerContactsApiClient;
import com.comerzzia.api.loyalty.client.LyCustomerFavoriteStoresApiClient;
import com.comerzzia.api.loyalty.client.model.CardType;
import com.comerzzia.api.loyalty.client.model.Collective;
import com.comerzzia.api.loyalty.client.model.LyCustomerCardDetail;
import com.comerzzia.api.loyalty.client.model.LyCustomerContact;
import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.api.loyalty.client.model.LyCustomerFavoriteStore;
import com.comerzzia.api.loyalty.client.model.NewLyCustomer;
import com.comerzzia.api.loyalty.client.model.NewLyCustomerContact;
import com.comerzzia.api.loyalty.client.model.NewLyCustomerLink;
import com.comerzzia.api.loyalty.client.model.Store;
import com.comerzzia.api.loyalty.client.model.UpdateLyCustomer;
import com.comerzzia.api.loyalty.client.model.UpdateLyCustomerContact;
import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.facade.service.country.CountryServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.loyaltycard.DeviceLoyaltyCard;
import com.comerzzia.pos.core.devices.device.loyaltycard.DeviceLoyaltyCardException;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.devices.printer.PrintService;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.loyalcustomer.cardmovements.LoyalCustomerCardMovementsPanelController;
import com.comerzzia.pos.gui.sales.loyalcustomer.collectives.LoyalCustomerCollectiveSelectionRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.collectives.LoyalCustomerCollectivesPanelController;
import com.comerzzia.pos.gui.sales.loyalcustomer.generaldata.LoyalCustomerGeneralDataPanelController;
import com.comerzzia.pos.gui.sales.loyalcustomer.latestsales.LoyalCustomerLatestSalesPanelController;
import com.comerzzia.pos.gui.sales.loyalcustomer.remarks.LoyalCustomerRemarksPanelController;
import com.comerzzia.pos.gui.sales.loyalcustomer.stores.LoyalCustomerPreferredStoreRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.summary.LoyalCustomerSummaryPanelController;
import com.comerzzia.pos.gui.sales.loyalcustomer.tags.LoyalCustomerTagsPanelController;
import com.comerzzia.pos.gui.sales.retail.items.RetailBasketItemizationController;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;

@Component
@CzzActionScene
public class LoyalCustomerController extends ActionSceneController implements Initializable {
	
	protected static final Logger log = Logger.getLogger(LoyalCustomerController.class);
	
	public static final String PERMISSION_EXECUTION = "EJECUCION";
	public static final String PERMISSION_EDIT = "EDITAR";
	public static final String PERMISSION_CREATE = "AÑADIR";
	public static final String PERMISSION_COLLECTIVES = "COLECTIVOS";
	public static final String PERMISSION_TRANSACTIONS = "MOVIMIENTOS";
	public static final String PERMISSION_LAST_PURCHASES = "VENTAS";
	public static final String PERMISSION_TAGS= "ETIQUETAS";
	public static final String PERMISSION_SENSITIVE_DATA= "DATOS SENSIBLES";
	public static final String PERMISSION_PRINT= "IMPRIMIR";
	
	public static final String PARAM_LY_CUSTOMER_ID = "LY_CUSTOMER_ID";
	public static final String PARAM_MODE = "MODE";
	
	protected BasketManager<?,?> basketManager;
	
	@Autowired
	protected Session session;
	
	@Autowired
	protected RetailBasketItemizationController basketItemizationController;
	
	@Autowired
    protected VariableServiceFacade variablesService;
	
	@Autowired
	protected CountryServiceFacade countryService;
	
	@Autowired
	protected LyCustomerContactsApiClient custContactsApiClient;
	
	@Autowired
	protected LyCustomerFavoriteStoresApiClient custFavStoresApiClient;
	
	@Autowired
	protected CardTypesApiClient cardTypesApiClient;
	
	@Autowired
	protected ModelMapper modelMapper;
	
	@FXML
	protected LoyalCustomerGeneralDataPanelController loyalCustomerGeneralDataPanelController;
	
	@FXML
	protected LoyalCustomerSummaryPanelController loyalCustomerSummaryPanelController;
	
	@FXML
	protected LoyalCustomerRemarksPanelController loyalCustomerRemarksPanelController;
	
	@FXML
	protected LoyalCustomerCollectivesPanelController loyalCustomerCollectivesPanelController;
	
	@FXML
	protected LoyalCustomerCardMovementsPanelController loyalCustomerCardMovementsPanelController;
	
	@FXML
	protected LoyalCustomerLatestSalesPanelController loyalCustomerLatestSalesPanelController;
	
	@FXML
	protected LoyalCustomerTagsPanelController loyalCustomerTagsPanelController;
	
	@FXML
	protected TabPane tpLoyalCustomerData;
	
	@FXML
	protected AnchorPane loyalCustomerGeneralDataPanel, loyalCustomerSummaryPanel, loyalCustomerRemarksPanel, loyalCustomerTagsPanel;
	
	@FXML
	protected AnchorPane loyalCustomerCollectivesPanel, loyalCustomerCardMovementsPanel, loyalCustomerLatestSalesPanel;
	
	@FXML
	protected Tab tabSummary, tabGeneralData, tabRemarks, tabCollectives, tabCardMovements, tabLatestSales, tabTags;
	
	@FXML
	protected Button btAccept, btCancel, btEdit, btClose, btPrint;
	
	@FXML
	protected Label lbError;
	
	@FXML
	protected Label lblMandatoryFields;
	
	@Autowired
	protected PrintService printService;
	
	protected LoyalCustomerFormValidationBean lyCustomerForm;
	
	@Getter
	protected List<LoyalCustomerPreferredStoreRow> stores = new ArrayList<LoyalCustomerPreferredStoreRow>();
	
	@Getter
	protected List<LoyalCustomerCollectiveSelectionRow> collectives = new ArrayList<LoyalCustomerCollectiveSelectionRow>();
	
	@Getter
	@Setter
	protected String mode;
	
	@Getter
	protected LyCustomerDetail lyCustomer;
	
	@Getter
	protected String lyCustomerCardNumber;
	
	@Getter
	protected boolean showSensitiveData, showCollectives, showLastPurchases, showCardTransactions, showTags, showPrint;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		lyCustomerForm = new LoyalCustomerFormValidationBean();
		lyCustomerForm.setFormField("code", loyalCustomerGeneralDataPanelController.getTfCode());
		lyCustomerForm.setFormField("cardNumber", loyalCustomerGeneralDataPanelController.getTfCardNumber());
		lyCustomerForm.setFormField("name", loyalCustomerGeneralDataPanelController.getTfName());
		lyCustomerForm.setFormField("lastName", loyalCustomerGeneralDataPanelController.getTfLastName());
		lyCustomerForm.setFormField("documentType", loyalCustomerGeneralDataPanelController.getCbDocumentType());
		lyCustomerForm.setFormField("document", loyalCustomerGeneralDataPanelController.getTfDocument());
		lyCustomerForm.setFormField("genre", loyalCustomerGeneralDataPanelController.getCbGenre());
		lyCustomerForm.setFormField("civilStatus", loyalCustomerGeneralDataPanelController.getCbCivilStatus());
		lyCustomerForm.setFormField("birthDate", loyalCustomerGeneralDataPanelController.getDpBirthDate());
		lyCustomerForm.setFormField("email", loyalCustomerGeneralDataPanelController.getTfEmail());
		lyCustomerForm.setFormField("mobile", loyalCustomerGeneralDataPanelController.getTfMobile());
		lyCustomerForm.setFormField("countryCode", loyalCustomerGeneralDataPanelController.getTfCountryCode());
		lyCustomerForm.setFormField("countryDes", loyalCustomerGeneralDataPanelController.getTfCountryDes());
		lyCustomerForm.setFormField("postalCode", loyalCustomerGeneralDataPanelController.getTfPostalCode());
		lyCustomerForm.setFormField("province", loyalCustomerGeneralDataPanelController.getTfProvince());
		lyCustomerForm.setFormField("city", loyalCustomerGeneralDataPanelController.getTfCity());
		lyCustomerForm.setFormField("location", loyalCustomerGeneralDataPanelController.getTfLocation());
		lyCustomerForm.setFormField("address", loyalCustomerGeneralDataPanelController.getTfAddress());
		lyCustomerForm.setFormField("favStoreCode", loyalCustomerGeneralDataPanelController.getTfStoreCode());
		lyCustomerForm.setFormField("favStoreDes", loyalCustomerGeneralDataPanelController.getTfStoreDes());
		lyCustomerForm.setFormField("collectiveCode", loyalCustomerGeneralDataPanelController.getTfCollectiveCode());
		lyCustomerForm.setFormField("collectiveDes", loyalCustomerGeneralDataPanelController.getTfCollectiveDes());
		lyCustomerForm.setFormField("remarks", loyalCustomerRemarksPanelController.getTaRemarks());
		
		lblMandatoryFields.setAlignment(Pos.BASELINE_LEFT);
		lbError.setAlignment(Pos.BASELINE_CENTER);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		initializeTabPaneEvents(tpLoyalCustomerData);
		
		QueryAllStoresTask queryStoresTask = SpringContext.getBean(QueryAllStoresTask.class, 
		        new RestBackgroundTask.FailedCallback<List<Store>>(){
					@Override
			        public void succeeded(List<Store> result) {
						List<LoyalCustomerPreferredStoreRow> preferredStores = new ArrayList<LoyalCustomerPreferredStoreRow>();
				        for (Store store : result) {
							LoyalCustomerPreferredStoreRow storeRow = new LoyalCustomerPreferredStoreRow(store);
							preferredStores.add(storeRow);
						}
						stores = preferredStores;
						
					}
					@Override
					public void failed(Throwable throwable) {
						closeCancel();
					}
				}, getStage());
		queryStoresTask.start();
		
		QueryAllCollectivesTask queryCollectivesTask = SpringContext.getBean(QueryAllCollectivesTask.class, 
				false, 
				new RestBackgroundTask.FailedCallback<List<Collective>>() {
					@Override
					public void succeeded(List<Collective> result) {	
						List<LoyalCustomerCollectiveSelectionRow> newCollectives = new ArrayList<LoyalCustomerCollectiveSelectionRow>();
						for(Collective collective : result){
							LoyalCustomerCollectiveSelectionRow collectiveRow = new LoyalCustomerCollectiveSelectionRow(collective);
							newCollectives.add(collectiveRow);
						}
						collectives = newCollectives;
						
					}
					@Override
					public void failed(Throwable throwable) {
						closeCancel();
					}
				}, getStage());
		queryCollectivesTask.start();
		
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		lyCustomerForm.clearErrorStyle();
		String newMode = (String) sceneData.get(PARAM_MODE);
		basketManager = (BasketManager<?,?>) sceneData.get(BasketItemizationControllerAbstract.BASKET_KEY);
		if("INSERCION".equals(newMode)){
			try {
				super.checkOperationPermissions(PERMISSION_CREATE);
			}
			catch (PermissionDeniedException ex) {
				log.error(ex.getMessage(), ex);
				throw new InitializeGuiException(ex.getMessage());
			}
			resetTabs();
			loyalCustomerGeneralDataPanelController.clearForm();
			loyalCustomerRemarksPanelController.clearForm();
			loyalCustomerSummaryPanelController.clearForm();
			loyalCustomerCardMovementsPanelController.clearForm();
			loyalCustomerTagsPanelController.clearForm();
			setMode("INSERCION");
			lblMandatoryFields.setVisible(true);
			tabSummary.setDisable(true);
			tabCollectives.setDisable(true);
			tabCardMovements.setDisable(true);
			tabLatestSales.setDisable(true);
			tabTags.setDisable(true);
			lyCustomerCardNumber = null;
			tpLoyalCustomerData.getSelectionModel().select(tabGeneralData);
			loyalCustomerGeneralDataPanelController.selected();
			btCancel.setVisible(false);
			btCancel.setManaged(false);
			btClose.setVisible(true);
			btClose.setManaged(true);
			btAccept.setVisible(true);
			btAccept.setManaged(true);
			btEdit.setVisible(false);
			btEdit.setManaged(false);
			btPrint.setVisible(true);  //Permite imprimir formularios vacíos
			btPrint.setManaged(true);
		}

		else if("CONSULTA".equals(newMode)){
			 setMode("CONSULTA");
			 lblMandatoryFields.setVisible(false);
			 tabSummary.setDisable(false);
			 tabCollectives.setDisable(false);
			 tabCardMovements.setDisable(false);
			 tabLatestSales.setDisable(false);
			 tabTags.setDisable(false);
			 btCancel.setVisible(true);			 
		     btClose.setVisible(false);		     
		     btAccept.setVisible(false);		     
		     btEdit.setVisible(true);
		     btPrint.setVisible(true);
		     btAccept.setManaged(false);
		     btClose.setManaged(false);
		     btCancel.setManaged(true);
		     btEdit.setManaged(true);
		     btPrint.setManaged(true);
			 Long lyCustomerId = (Long) sceneData.get(PARAM_LY_CUSTOMER_ID);
			 String cardNumber = (String) sceneData.get(RetailBasketItemizationController.PARAM_CARD_NUMBER);
			 lyCustomerCardNumber = cardNumber;
			 loadLyCustomer(lyCustomerId); 
		 }
	}

	@Override
	public void initializeFocus() {
		// TODO Auto-generated method stub
	}
	
	protected void loadLyCustomer(Long lyCustomerId){
		QueryLoyalCustomerByIdTask queryLoyalCustomerTask = SpringContext.getBean(QueryLoyalCustomerByIdTask.class, 
				lyCustomerId, 
				new RestBackgroundTask.FailedCallback<LyCustomerDetail>() {
					@Override
					public void succeeded(LyCustomerDetail result) {
						lyCustomer = result;
						
						openSummary();
						 
				        btCancel.setVisible(false);
				        btCancel.setManaged(false);
				        btClose.setVisible(true);
				        btClose.setManaged(true);
				        btAccept.setVisible(false);
				        btAccept.setManaged(true);
				        btEdit.setVisible(true);
				        btEdit.setManaged(true);
				        btPrint.setVisible(true);
				        btPrint.setManaged(true);
					}
					@Override
					public void failed(Throwable throwable) {
						closeCancel();
					}
				}, getStage());
		queryLoyalCustomerTask.start();
	}
	
	@FXML
	public void actionPrint(){
		log.debug("actionPrint()");
		List<LyCustomerDetail> loyalCustomers = new ArrayList<>();
		HashMap<String, Object> params = new HashMap<String, Object>();
		String adressCopy = null;
		String documentCopy = null;
		
		loyalCustomers.add(lyCustomer);
		params.put(PrintService.LIST, loyalCustomers);
		if(lyCustomer != null){
			//Se edita temporalmente el domicilio y documento del fidelizado para la impresión por si fuera necesario ocultarlo
			adressCopy = lyCustomer.getAddress();
			documentCopy = lyCustomer.getVatNumber();
			lyCustomer.setAddress(printSensitiveData(lyCustomer.getAddress()));
			lyCustomer.setVatNumber(printSensitiveData(lyCustomer.getVatNumber()));
			
			Optional<LyCustomerContact> phoneContact = lyCustomer.getContacts().stream().filter(c -> c.getContactTypeCode().equals("MOVIL")).findFirst();
			if(phoneContact.isPresent()){
				params.put("MOVIL",printSensitiveData(phoneContact.get().getValue()));
				params.put("MOVIL_NOTIF", phoneContact.get().getGetNotifications() ? I18N.getText("SI") : I18N.getText("NO"));
				params.put("MOVIL_NOTIF_COM", phoneContact.get().getComGetNotifications() ? I18N.getText("SI") : I18N.getText("NO"));
			}else{
				params.put("MOVIL", "");
				params.put("MOVIL_NOTIF", "-");
				params.put("MOVIL_NOTIF_COM", "-");
			}
			
			Optional<LyCustomerContact> emailContact = lyCustomer.getContacts().stream().filter(c -> c.getContactTypeCode().equals("EMAIL")).findFirst();
			if(emailContact.isPresent()){
				params.put("EMAIL", printSensitiveData(emailContact.get().getValue()));
				params.put("EMAIL_NOTIF", emailContact.get().getGetNotifications() ? I18N.getText("SI") : I18N.getText("NO"));
				params.put("EMAIL_NOTIF_COM", emailContact.get().getComGetNotifications() ? I18N.getText("SI") : I18N.getText("NO"));
			}else{
				params.put("EMAIL", "");
				params.put("EMAIL_NOTIF", "-");
				params.put("EMAIL_NOTIF_COM", "-");
			}
			
		}else{ //Para que no aparezca 'null' en 'Permite notificaciones'
			params.put("MOVIL_NOTIF", "");
			params.put("EMAIL_NOTIF", "");
			params.put("MOVIL_NOTIF_COM", "");
			params.put("EMAIL_NOTIF_COM", "");
		}
		params.put("DESEMP", session.getApplicationSession().getCompany().getCompanyDes());
		params.put("DOMICILIO", session.getApplicationSession().getCompany().getAddress());
		params.put("CP", session.getApplicationSession().getCompany().getPostalCode());
		params.put("PROVINCIA", session.getApplicationSession().getCompany().getProvince());
		
		if(session.getApplicationSession().getCompany().getLogo() != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(session.getApplicationSession().getCompany().getLogo());
			params.put("LOGO", bis);
		}
		
		try {
			printService.printJasper("lycustomer/formulariofidelizado", params);
		} catch (DeviceException e) {
			log.error("Ha ocurrido un error al imprimir el informe ", e);
		} finally {
			if(lyCustomer != null) {
				lyCustomer.setAddress(adressCopy);
				lyCustomer.setVatNumber(documentCopy);
			}
		}
	}
	
	protected String printSensitiveData(String value) {
		String res = value;
		if(!showSensitiveData&&!"EDICION".equals(getMode())){
			String replace = res.substring(1, res.length()-1);
			String car = replace.replaceAll(".", "*");
			res = res.replace(replace, car);
		}
		return res;
	}

	@FXML
	public void actionEdit(){
		btAccept.setVisible(true);
		btAccept.setManaged(true);
		btCancel.setVisible(true);
		btCancel.setManaged(true);
		btEdit.setVisible(false);
		btEdit.setManaged(false);
		btClose.setVisible(false);
		btClose.setManaged(false);
		tabSummary.setDisable(true);
		tabCollectives.setDisable(true);
		tabCardMovements.setDisable(true);
		tabLatestSales.setDisable(true);
		tabTags.setDisable(true);
		lblMandatoryFields.setVisible(true);
		setMode("EDICION");
		tpLoyalCustomerData.getSelectionModel().select(tabGeneralData);
		loyalCustomerRemarksPanelController.selected();
		loyalCustomerGeneralDataPanelController.selected();
	}
	
	@FXML
	public void actionAccept(){	
		
		if("INSERCION".equals(getMode())){
			if(validateData()){
				String code = loyalCustomerGeneralDataPanelController.getTfCode().getText();
				if(StringUtils.isNotBlank(code)){
					QueryLoyalCustomerByCodeTask queryLoyalCustomerTask = SpringContext.getBean(QueryLoyalCustomerByCodeTask.class, 
					        code, 
							new RestBackgroundTask.FailedCallback<Boolean>() {
								@Override
								public void succeeded(Boolean result) {
									if(result){
										DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("El código de fidelizado indicado ya existe"));
									}else{
										String cardNumber = loyalCustomerGeneralDataPanelController.getTfCardNumber().getText();
										if(cardNumber != null && !cardNumber.isEmpty()){
											validateCardNumber(cardNumber);
										}else{
											createLyCustomer();
										}
									}
									
								}
								@Override
								public void failed(Throwable throwable) {
									closeCancel();
								}
							}, getStage());
					queryLoyalCustomerTask.start();
				}else{
					String cardNumber = loyalCustomerGeneralDataPanelController.getTfCardNumber().getText();
					if(cardNumber != null && !cardNumber.isEmpty()){
						validateCardNumber(cardNumber);
					}else{
						createLyCustomer();
					}
				}
				
			}
		}else if("EDICION".equals(getMode())){
			if(validateData()){
				updateLyCustomer();
			}
		}
	}
	
	@FXML
	public void cancelAction(){
		btAccept.setVisible(false);
		btAccept.setManaged(false);
		btCancel.setVisible(false);
		btCancel.setManaged(false);
		btEdit.setVisible(true);
		btEdit.setManaged(true);
		btClose.setVisible(true);
		btClose.setManaged(true);
		lblMandatoryFields.setVisible(false);
		setMode("CONSULTA");
		tabSummary.setDisable(false);
		tabCollectives.setDisable(!showCollectives);
		tabCardMovements.setDisable(!showCardTransactions);
		tabLatestSales.setDisable(!showLastPurchases);
		tabTags.setDisable(!showTags);
		loyalCustomerGeneralDataPanelController.setLyCustomer(null);
		loyalCustomerRemarksPanelController.setLyCustomer(null);
		tpLoyalCustomerData.getSelectionModel().select(tabSummary);
		lbError.setText("");
		lyCustomerForm.clearErrorStyle();
	}
	
	@FXML
	public void actionClose(){
		lyCustomer = null;
		closeCancel();
	}
	
	protected LyCustomerDetail getLyCustomerData(String mode){
		LyCustomerDetail lyCustomer = new LyCustomerDetail();

		String lyCustomerCode = !"".equals(loyalCustomerGeneralDataPanelController.getTfCode().getText()) ? loyalCustomerGeneralDataPanelController.getTfCode().getText() : null;
		String name = !"".equals(loyalCustomerGeneralDataPanelController.getTfName().getText()) ? loyalCustomerGeneralDataPanelController.getTfName().getText() : null;
		String lastName = loyalCustomerGeneralDataPanelController.getTfLastName().getText();
		String address = !"".equals(loyalCustomerGeneralDataPanelController.getTfAddress().getText()) ? loyalCustomerGeneralDataPanelController.getTfAddress().getText() : null;
		String city = !"".equals(loyalCustomerGeneralDataPanelController.getTfCity().getText()) ? loyalCustomerGeneralDataPanelController.getTfCity().getText() : null;
		String location = !"".equals(loyalCustomerGeneralDataPanelController.getTfLocation().getText()) ? loyalCustomerGeneralDataPanelController.getTfLocation().getText() : null;
		String province = !"".equals(loyalCustomerGeneralDataPanelController.getTfProvince().getText()) ? loyalCustomerGeneralDataPanelController.getTfProvince().getText() : null;
		String postalCode = !"".equals(loyalCustomerGeneralDataPanelController.getTfPostalCode().getText()) ? loyalCustomerGeneralDataPanelController.getTfPostalCode().getText() : null;
		String countryCode = !"".equals(loyalCustomerGeneralDataPanelController.getTfCountryCode().getText()) ? loyalCustomerGeneralDataPanelController.getTfCountryCode().getText() : null;
		String identificationTypeCode = loyalCustomerGeneralDataPanelController.getCbDocumentType().getValue() != null ? loyalCustomerGeneralDataPanelController.getCbDocumentType().getValue().getIdentTypeCode() : null;
		String vatNumber = !"".equals(loyalCustomerGeneralDataPanelController.getTfDocument().getText()) ? loyalCustomerGeneralDataPanelController.getTfDocument().getText() : null;
		String remarks = !"".equals(loyalCustomerRemarksPanelController.getTaRemarks().getText()) ? loyalCustomerRemarksPanelController.getTaRemarks().getText() : null;
		Date dateOfBirth = loyalCustomerGeneralDataPanelController.getDpBirthDate().getSelectedDate();
		String genderName = loyalCustomerGeneralDataPanelController.getCbGenre().getValue() != null ? loyalCustomerGeneralDataPanelController.getCbGenre().getValue().getCode() : null;
		String maritalStatusCode = loyalCustomerGeneralDataPanelController.getCbCivilStatus().getValue() != null ? loyalCustomerGeneralDataPanelController.getCbCivilStatus().getValue().getCivilStatusCode() : null;
		Boolean paperLess = loyalCustomerGeneralDataPanelController.getChPaperLess().isSelected();
		
		lyCustomer.setLyCustomerCode(lyCustomerCode);
		lyCustomer.setName(name);
		lyCustomer.setLastName(lastName);
		lyCustomer.setAddress(address);
		lyCustomer.setCity(city);
		lyCustomer.setLocation(location);
		lyCustomer.setProvince(province);
		lyCustomer.setPostalCode(postalCode);
		lyCustomer.setCountryCode(countryCode);
		lyCustomer.setIdentificationTypeCode(identificationTypeCode);
		lyCustomer.setVatNumber(vatNumber);
		lyCustomer.setRemarks(remarks);
		lyCustomer.setDateOfBirth(dateOfBirth);
		lyCustomer.setGenderName(genderName);
		lyCustomer.setMaritalStatusCode(maritalStatusCode);
		lyCustomer.setPaperLess(paperLess);
		
		if("INSERCION".equals(mode)){
			lyCustomer.setCollectives(new ArrayList<Collective>());
			String collectiveCode = !"".equals(loyalCustomerGeneralDataPanelController.getTfCollectiveCode().getText()) ? loyalCustomerGeneralDataPanelController.getTfCollectiveCode().getText() : null;
			if(collectiveCode != null){
				Collective collective = new Collective();
				collective.setCollectiveCode(collectiveCode);
					
				lyCustomer.getCollectives().add(collective);
			}
			
			lyCustomer.setContacts(new ArrayList<>());
			String email = !"".equals(loyalCustomerGeneralDataPanelController.getTfEmail().getText()) ? loyalCustomerGeneralDataPanelController.getTfEmail().getText() : null;
			if (email != null) {
				LyCustomerContact customerEmail = new LyCustomerContact();
				customerEmail.setContactTypeCode("EMAIL");
				customerEmail.setValue(email);
				customerEmail.setGetNotifications(loyalCustomerGeneralDataPanelController.getChNotifEmail().isSelected());
				customerEmail.setComGetNotifications(loyalCustomerGeneralDataPanelController.getChNotifComEmail().isSelected());
				
				lyCustomer.getContacts().add(customerEmail);
			}
			
			String phone = !"".equals(loyalCustomerGeneralDataPanelController.getTfMobile().getText()) ? loyalCustomerGeneralDataPanelController.getTfMobile().getText() : null;
			if (phone != null) {
				LyCustomerContact customerPhone = new LyCustomerContact();
				customerPhone.setContactTypeCode("MOVIL");
				customerPhone.setValue(phone);
				customerPhone.setGetNotifications(loyalCustomerGeneralDataPanelController.getChNotifMobile().isSelected());
				customerPhone.setComGetNotifications(loyalCustomerGeneralDataPanelController.getChNotifComMobile().isSelected());
				
				lyCustomer.getContacts().add(customerPhone);
			}
			
			lyCustomer.setCards(new ArrayList<>());
			String cardNumber = !"".equals(loyalCustomerGeneralDataPanelController.getTfCardNumber().getText()) ? loyalCustomerGeneralDataPanelController.getTfCardNumber().getText() : null;
			if(cardNumber != null){
				LyCustomerCardDetail card = new LyCustomerCardDetail();
				card.setCardNumber(cardNumber);
				
				lyCustomer.getCards().add(card);
			}
			
			String favStoreCode = !"".equals(loyalCustomerGeneralDataPanelController.getTfStoreCode().getText()) ? loyalCustomerGeneralDataPanelController.getTfStoreCode().getText() : null;
			if (favStoreCode != null) {
				LyCustomerFavoriteStore customerFavStore = new LyCustomerFavoriteStore();
				customerFavStore.setStoreCode(favStoreCode);
				
				lyCustomer.setFavoriteStore(customerFavStore);
			}
		}
		else if("EDICION".equals(mode)){
			lyCustomer.setLyCustomerId(this.lyCustomer.getLyCustomerId());
		}
		
		return lyCustomer;
	}
	
	public void validateCardNumber(final String cardNumber){
		List<CardType> cardTypes = cardTypesApiClient.findCardTypesList(null, true, null, null, false, null, null).getBody();
		if (CollectionUtils.isEmpty(cardTypes)) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No existen tipos de tarjeta vinculables y que no sean de pago"));
		}
		else {
			List<String> cardTypeCodes = cardTypes.stream().map(CardType::getCardTypeCode).collect(Collectors.toList());
			ValidateCardByNumberTask validateCardTask = SpringContext.getBean(ValidateCardByNumberTask.class, 
					cardNumber, cardTypeCodes,
					new RestBackgroundTask.FailedCallback<Void>() {
						@Override
						public void succeeded(Void result) {
							lyCustomerCardNumber = cardNumber;
							createLyCustomer();
						}
						@Override
						public void failed(Throwable throwable) {
						}
					}, getStage());
			validateCardTask.start();
		}
	}
	
	public void createLyCustomer(){
		LyCustomerDetail customer = getLyCustomerData("INSERCION");
		
		NewLyCustomer newCustomer = modelMapper.map(customer, NewLyCustomer.class);
		newCustomer.setActive(true);
		
		if(customer.getFavoriteStore() != null) {
			NewLyCustomerLink newCustomerLink = new NewLyCustomerLink();
			newCustomerLink.setClassUid("D_TIENDAS_TBL.CODALM");
			newCustomerLink.setObjectUid(customer.getFavoriteStore().getStoreCode());
			newCustomer.setCustomerLink(newCustomerLink);
		}
		
		
		//TODO insertFidelizado.setTipoNotificacion("NUEVO_USUARIO_FIDELIZADO");
		CreateLoyalCustomerTask newCustomerTask = SpringContext.getBean(CreateLoyalCustomerTask.class, 
				newCustomer,
				new RestBackgroundTask.FailedCallback<LyCustomerDetail>() {
					@Override
					public void succeeded(LyCustomerDetail result) {
						lyCustomer = result;
						lyCustomerCardNumber = loyalCustomerGeneralDataPanelController.getTfCardNumber().getText();
						assignLyCustomer();
						setMode("CONSULTA");
						tabSummary.setDisable(false);
						tabCardMovements.setDisable(!showCardTransactions);
						tabCollectives.setDisable(!showCollectives);
						tabLatestSales.setDisable(!showLastPurchases);
						tabTags.setDisable(!showTags);
						
						openSummary();
						
						btAccept.setVisible(false);
						btAccept.setManaged(false);
						btCancel.setVisible(false);
						btCancel.setManaged(false);
						btEdit.setVisible(true);
						btEdit.setManaged(true);
						btPrint.setVisible(true);
						btPrint.setManaged(true);
						btClose.setVisible(true);
						btClose.setManaged(true);
						lblMandatoryFields.setVisible(false);
						DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(I18N.getText("Fidelizado creado correctamente"));
					}
					
					@Override
					public void failed(Throwable throwable) {
						log.error("createLyCustomer() - Ha habido un error al crear el fidelizado: " + throwable.getMessage(), throwable);
						DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha habido un error al crear el fidelizado"));
					}
				}, getStage());
		newCustomerTask.start();
	}
	
	public void updateLyCustomer(){
		updateContacts();
		updateFavoriteStore();
		
		lyCustomer = getLyCustomerData("EDICION");
		
		EditLoyalCustomerTask updateCustomerTask = SpringContext.getBean(EditLoyalCustomerTask.class, 
				lyCustomer.getLyCustomerId(), modelMapper.map(lyCustomer, UpdateLyCustomer.class), 
				new RestBackgroundTask.FailedCallback<LyCustomerDetail>() {
					@Override
					public void succeeded(LyCustomerDetail result) {	
						lyCustomer = result;
						assignLyCustomer();
						setMode("CONSULTA");				
						tabSummary.setDisable(false);
						tabCardMovements.setDisable(!showCardTransactions);
						tabCollectives.setDisable(!showCollectives);
						tabLatestSales.setDisable(!showLastPurchases);
						tabTags.setDisable(!showTags);
						loyalCustomerGeneralDataPanelController.setLyCustomer(null);
						loyalCustomerSummaryPanelController.setLyCustomer(null);
						openSummary();
						btAccept.setVisible(false);
						btAccept.setManaged(false);
						btCancel.setVisible(false);
						btCancel.setManaged(false);
						btEdit.setVisible(true);
						btEdit.setManaged(true);
						btClose.setVisible(true);
						btClose.setManaged(true);
						lblMandatoryFields.setVisible(false);
						DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(I18N.getText("Fidelizado editado correctamente"));
					}
					@Override
					public void failed(Throwable throwable) {
						log.error("updateLyCustomer() - Ha habido un error al editar el fidelizado: " + throwable.getMessage(), throwable);
						DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha habido un error al editar el fidelizado"));
					}
				}, getStage());
		updateCustomerTask.start();
	}
	
	protected void updateContacts() {
		String email = !"".equals(loyalCustomerGeneralDataPanelController.getTfEmail().getText()) ? loyalCustomerGeneralDataPanelController.getTfEmail().getText() : null;
		Boolean getNotifEmail = loyalCustomerGeneralDataPanelController.getChNotifEmail().isSelected();
		Boolean comGetNotifEmail = loyalCustomerGeneralDataPanelController.getChNotifComEmail().isSelected();
		
		updateContact("EMAIL", email, getNotifEmail, comGetNotifEmail);
		
		String phone = !"".equals(loyalCustomerGeneralDataPanelController.getTfMobile().getText()) ? loyalCustomerGeneralDataPanelController.getTfMobile().getText() : null;
		Boolean getNotifPhone = loyalCustomerGeneralDataPanelController.getChNotifMobile().isSelected();
		Boolean comGetNotifPhone = loyalCustomerGeneralDataPanelController.getChNotifComMobile().isSelected();
		
		updateContact("MOVIL", phone, getNotifPhone, comGetNotifPhone);
	}
	
	protected void updateContact(String contactTypeCode, String value, boolean getNotifications, boolean comGetNotifications) {
		Optional<LyCustomerContact> optOldContact = lyCustomer.getContacts().stream().filter(c -> c.getContactTypeCode().equals(contactTypeCode)).findFirst();
		if(optOldContact.isPresent()){
			LyCustomerContact oldContact = optOldContact.get();
			if(value == null){
				custContactsApiClient.deleteLyCustomerContact(lyCustomer.getLyCustomerId(), contactTypeCode);
			}
			else if(!oldContact.getValue().equals(value) || oldContact.getGetNotifications() != getNotifications || oldContact.getComGetNotifications() != comGetNotifications){
				UpdateLyCustomerContact updateContact = new UpdateLyCustomerContact();
				updateContact.setValue(value);
				updateContact.setGetNotifications(getNotifications);
				updateContact.setComGetNotifications(comGetNotifications);
				
				custContactsApiClient.updateLyCustomerContact(lyCustomer.getLyCustomerId(), contactTypeCode, updateContact);
			}
		}
		else{
			if(value != null){
				NewLyCustomerContact newContact = new NewLyCustomerContact();
				newContact.setContactTypeCode(contactTypeCode);
				newContact.setValue(value);
				newContact.setGetNotifications(getNotifications);
				newContact.setComGetNotifications(comGetNotifications);
				
				custContactsApiClient.createLyCustomerContact(lyCustomer.getLyCustomerId(), newContact);
			}
		}
	}
	
	protected void updateFavoriteStore() {
		String favStoreCode = !"".equals(loyalCustomerGeneralDataPanelController.getTfStoreCode().getText()) ? loyalCustomerGeneralDataPanelController.getTfStoreCode().getText() : null;

		if (favStoreCode == null) {
			custFavStoresApiClient.deleteLyCustomerFavoriteStore(lyCustomer.getLyCustomerId());
		}
		else {
			custFavStoresApiClient.updateLyCustomerFavoriteStore(lyCustomer.getLyCustomerId(), favStoreCode);
		}
	}
	
	protected void assignLyCustomer() {
		BasketLoyalCustomer basketLyCustomer = null;
		DeviceLoyaltyCard loyaltyDevice = Devices.getInstance().getLoyaltyCard();
		
		if(StringUtils.isNotBlank(lyCustomerCardNumber)) {
			try {
				basketLyCustomer = loyaltyDevice.findLoyalCustomerAndCoupons(lyCustomerCardNumber);
			}
			catch (DeviceLoyaltyCardException e) {
				log.error("assignLyCustomer() - Ha habido un error al consultar el fidelizado: " + e.getMessage(), e);
				DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha habido un error al consultar el fidelizado"));
			}
			try {
				basketManager.updateLoyaltySettings(basketLyCustomer);
				basketItemizationController.refreshScreenData();
			}catch(Exception e) {
				DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha ocurrido un error al asignar el fidelizado a la venta."));
			}
		}
		else {
			try {
				basketLyCustomer = new BasketLoyalCustomer();
				basketLyCustomer.setLyCustomerId(lyCustomer.getLyCustomerId());
				basketLyCustomer.setName(lyCustomer.getName());
				basketLyCustomer.setLastName(lyCustomer.getLastName());
				basketLyCustomer.setAddress(lyCustomer.getAddress());
				basketLyCustomer.setLocation(lyCustomer.getLocation());
				basketLyCustomer.setCity(lyCustomer.getCity());
				basketLyCustomer.setProvince(lyCustomer.getProvince());
				basketLyCustomer.setPostalCode(lyCustomer.getPostalCode());
				basketLyCustomer.setCountryCode(lyCustomer.getCountryCode());
				basketLyCustomer.setCountryDes(countryService.findById(lyCustomer.getCountryCode()).getCountryDes());
				basketLyCustomer.setCardNumber(getLyCustomerCardNumber());
				basketLyCustomer.setIdentificationTypeCode(lyCustomer.getIdentificationTypeCode());
				basketLyCustomer.setVatNumber(lyCustomer.getVatNumber());
				basketLyCustomer.setPaperLess(lyCustomer.getPaperLess());
				
		        if(lyCustomer.getCollectives() != null) {
		        	basketLyCustomer.setCollectives(lyCustomer.getCollectives().stream().map(Collective::getCollectiveCode).collect(Collectors.toSet()));
		        }
	        
				loyaltyDevice.setLoyalCustomerCoupons(basketLyCustomer);
			}
			catch (Exception e) {
				log.error("assignLyCustomer() - Ha habido un error al consultar el fidelizado: " + e.getMessage(), e);
				DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha habido un error al consultar el fidelizado"));
			}
		}
		
    }
	
	public boolean validateData(){
		boolean valid;

		// Limpiamos los errores que pudiese tener el formulario
		lyCustomerForm.clearErrorStyle();
		
		lyCustomerForm.setLastName(loyalCustomerGeneralDataPanelController.getTfLastName().getText());
		lyCustomerForm.setCollectiveCode(loyalCustomerGeneralDataPanelController.getTfCollectiveCode().getText());
		lyCustomerForm.setCode(loyalCustomerGeneralDataPanelController.getTfCode().getText());
		lyCustomerForm.setPostalCode(loyalCustomerGeneralDataPanelController.getTfPostalCode().getText());
		lyCustomerForm.setDocument(loyalCustomerGeneralDataPanelController.getTfDocument().getText());
		lyCustomerForm.setAddress(loyalCustomerGeneralDataPanelController.getTfAddress().getText());
		lyCustomerForm.setEmail(loyalCustomerGeneralDataPanelController.getTfEmail().getText());
		lyCustomerForm.setFechaNacimiento(loyalCustomerGeneralDataPanelController.getDpBirthDate().getSelectedDate());
		lyCustomerForm.setMobile(loyalCustomerGeneralDataPanelController.getTfMobile().getText());
		lyCustomerForm.setName(loyalCustomerGeneralDataPanelController.getTfName().getText());
		lyCustomerForm.setCardNumber(loyalCustomerGeneralDataPanelController.getTfCardNumber().getText());
		lyCustomerForm.setCity(loyalCustomerGeneralDataPanelController.getTfCity().getText());
		lyCustomerForm.setLocation(loyalCustomerGeneralDataPanelController.getTfLocation().getText());
		lyCustomerForm.setProvince(loyalCustomerGeneralDataPanelController.getTfProvince().getText());
		lyCustomerForm.setRemarks(loyalCustomerRemarksPanelController.getTaRemarks().getText());
		LoyalCustomerCountryIdTypeRow identType = loyalCustomerGeneralDataPanelController.getCbDocumentType().getSelectionModel().getSelectedItem();
		if(identType == null){
			lyCustomerForm.setDocumentType("");
		}
		else{
			lyCustomerForm.setDocumentType(identType.getIdentTypeCode());
		}
		
		LoyalCustomerGenreRow genre = loyalCustomerGeneralDataPanelController.getCbGenre().getSelectionModel().getSelectedItem();
		if(genre == null){
			lyCustomerForm.setGenre("");
		}
		else{
			lyCustomerForm.setGenre(genre.getCode());
		}
		
		LoyalCustomerCivilStatusRow civilStatus = loyalCustomerGeneralDataPanelController.getCbCivilStatus().getSelectionModel().getSelectedItem();
		if(civilStatus == null){
			lyCustomerForm.setCivilStatus("");
		}
		else{
			lyCustomerForm.setCivilStatus(civilStatus.getCivilStatusCode());
		}
		
		try {
            countryService.findById(loyalCustomerGeneralDataPanelController.getTfCountryCode().getText());
        }
        catch (NotFoundException e) {
        	PathImpl path = PathImpl.createPathFromString("countryCode");
        	lyCustomerForm.setFocus(path);
        	lyCustomerForm.setErrorStyle(path, true);
        	return false;
        }
		
		String collectiveCode = loyalCustomerGeneralDataPanelController.getTfCollectiveCode().getText();
		String favStoreCode = loyalCustomerGeneralDataPanelController.getTfStoreCode().getText();
		if(collectiveCode != null && !"".equals(collectiveCode)){
			boolean validCollective = false;
			for(LoyalCustomerCollectiveSelectionRow collective : collectives){
				if(collectiveCode.equals(collective.getCollectiveCode())){
					validCollective = true;
					break;
				}
			}
			if(!validCollective){
				PathImpl path = PathImpl.createPathFromString("collectiveCode");
	        	lyCustomerForm.setFocus(path);
	        	lyCustomerForm.setErrorStyle(path, true);
	        	return false;
			}
		}
		
		if(favStoreCode != null && !"".equals(favStoreCode)){
			boolean validStore = false;
			for(LoyalCustomerPreferredStoreRow store : stores){
				if(favStoreCode.equals(store.getStoreCode())){
					validStore = true;
					break;
				}
			}
			if(!validStore){
				PathImpl path = PathImpl.createPathFromString("favStoreCode");
	        	lyCustomerForm.setFocus(path);
	        	lyCustomerForm.setErrorStyle(path, true);
	        	return false;
			}
		}
		
		//Limpiamos el posible error anterior
		lbError.setText("");
		
		Set<ConstraintViolation<LoyalCustomerFormValidationBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(lyCustomerForm);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<LoyalCustomerFormValidationBean> next = constraintViolations.iterator().next();
			lyCustomerForm.setErrorStyle(next.getPropertyPath(), true);
			lyCustomerForm.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			valid = false;
		}
		else {
			valid = true;
		}

		return valid;
	}

	@Override
	public void onClose(){
		lyCustomer = null;
		resetTabs();
    	super.onClose();
    };
    
    public void openSummary() {
		loyalCustomerSummaryPanelController.selected();
		tpLoyalCustomerData.getSelectionModel().select(tabSummary);
	}
	
	public void resetTabs() {
		loyalCustomerSummaryPanelController.setLyCustomer(null);
		loyalCustomerGeneralDataPanelController.setLyCustomer(null);
		loyalCustomerRemarksPanelController.setLyCustomer(null);
		loyalCustomerCollectivesPanelController.setLyCustomer(null);
		loyalCustomerCardMovementsPanelController.setLyCustomer(null);
		loyalCustomerLatestSalesPanelController.setLyCustomer(null);
		loyalCustomerTagsPanelController.setLyCustomer(null);
	}
	
	@Override
	public void checkUIPermission() {
		super.checkUIPermission();
		try {
			super.checkOperationPermissions(PERMISSION_EDIT);
			btEdit.setDisable(false);
		}
		catch (PermissionDeniedException ex) {
			btEdit.setDisable(true);
		}
		try {
			super.checkOperationPermissions(PERMISSION_COLLECTIVES);
			showCollectives = true;
			if("CONSULTA".equals(getMode())){
				tabCollectives.setDisable(false);
			}
		}
		catch (PermissionDeniedException ex) {
			showCollectives = false;
			tabCollectives.setDisable(true);
			if(tabCollectives.isSelected()){
				openSummary();
			}
		}
		try {
			super.checkOperationPermissions(PERMISSION_LAST_PURCHASES);
			showLastPurchases = true;
			if("CONSULTA".equals(getMode())){
				tabLatestSales.setDisable(false);
			}
		}
		catch (PermissionDeniedException ex) {
			showLastPurchases = false;
			tabLatestSales.setDisable(true);
			if(tabLatestSales.isSelected()){
				openSummary();
			}
		}
		try {
			super.checkOperationPermissions(PERMISSION_TRANSACTIONS);
			showCardTransactions = true;
			if("CONSULTA".equals(getMode())){
				tabCardMovements.setDisable(false);
			}
		}
		catch (PermissionDeniedException ex) {
			showCardTransactions = false;
			tabCardMovements.setDisable(true);
			if(tabCardMovements.isSelected()){
				openSummary();
			}
		}
		try {
			super.checkOperationPermissions(PERMISSION_TAGS);
			showTags = true;
			if("CONSULTA".equals(getMode())){
				tabTags.setDisable(false);
			}
		}
		catch (PermissionDeniedException ex) {
			showTags = false;
			tabTags.setDisable(true);
			if(tabTags.isSelected()){
				openSummary();
			}
		}
		try {
			super.checkOperationPermissions(PERMISSION_PRINT);
			btPrint.setDisable(false);
		}
		catch (PermissionDeniedException ex) {
			btPrint.setDisable(true);
		}
		try {
			super.checkOperationPermissions(PERMISSION_SENSITIVE_DATA);
			showSensitiveData = true;
		}
		catch (PermissionDeniedException ex) {
			showSensitiveData = false;
		}
	}

}
