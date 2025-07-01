package com.comerzzia.pos.gui.sales.basket;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.api.loyalty.client.LyCustomersApiClient;
import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.api.loyalty.client.model.LyCustomerLink;
import com.comerzzia.core.facade.model.TaxTreatment;
import com.comerzzia.core.facade.service.country.CountryServiceFacade;
import com.comerzzia.core.facade.service.countryidentificationtype.CountryIdentificationTypeFacade;
import com.comerzzia.core.facade.service.tax.TaxTreatmentServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.core.service.audit.ComerzziaAuditEventBuilder;
import com.comerzzia.omnichannel.facade.model.basketdocument.SaleBasket;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.basket.exception.BasketException;
import com.comerzzia.omnichannel.facade.service.basketdocument.SaleBasketDocumentServiceFacade;
import com.comerzzia.omnichannel.facade.service.sale.customer.CustomerServiceFacade;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.normal.ActionButtonNormalComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.services.balancecard.BalanceCardService;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.basket.retrieval.RetrieveBasketControllerAbstract;
import com.comerzzia.pos.gui.sales.customer.CustomerManagementController;
import com.comerzzia.pos.gui.sales.retail.customer.CustomerDto;
import com.comerzzia.pos.util.base.Status;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class CustomerIdentificationControllerAbstract extends ActionSceneController implements Initializable, ButtonsGroupController {

	@FXML
	protected NumericKeypad numpad;
	@FXML
	protected TextField tfIdentificationCode;
	@FXML
	protected WebView wvMain;
	@FXML
	protected VBox vbBottomButtonsPanel;

	
	@Autowired
	protected Session session;
	
	@Autowired
	protected VariableServiceFacade variablesServices;
	
	@Autowired
	protected CountryIdentificationTypeFacade identTypesService;
	
	@Autowired
	protected CustomerServiceFacade customersService;
	
	@Autowired
	protected CountryServiceFacade countryService;
	
	@Autowired
	protected TaxTreatmentServiceFacade taxTreatmentService;
			
	@Autowired
    protected SaleBasketDocumentServiceFacade saleBasketDocumentService;
	
	@Autowired
	protected BalanceCardService giftCardService;
	
	@Autowired
	protected ModelMapper modelMapper;
	
	@Autowired
	protected LyCustomersApiClient customersApi;
	
	protected List<Customer> result;
	
	protected ButtonsGroupComponent bottomButtonsGroup;
	
	protected boolean offline = false;
	
    final DeviceLineDisplay lineDisplay = Devices.getInstance().getLineDisplay();

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		try {
			tfIdentificationCode.setTextFormatter(new TextFormatter<>((change) -> {
				change.setText(change.getText().toUpperCase());
				return change;
			}));
			loadBottomButtonsPanel();
		}catch(Exception e) {
			throw new InitializeGuiException(e.getMessage(), e, false, false);
		}
	}
		
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		try {
			if (!session.getCashJournalSession().isOpenedCashJournal()) {
				Boolean autoOpeningCashJournal = variablesServices.getVariableAsBoolean(VariableServiceFacade.CAJA_APERTURA_AUTOMATICA, true);
				if(autoOpeningCashJournal){
					DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(I18N.getText("No hay caja abierta. Se abrirá automáticamente."));
					session.getCashJournalSession().openAutomaticCashJournal();
				}else{
					DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No hay caja abierta. Deberá ir a la gestión de caja para abrirla."));
					throw new InitializeGuiException(false);
				}
			}
			
			
			if(sceneData != null && sceneData.get(CustomerManagementController.EDITED_CUSTOMER) != null) {
				Customer customer = (Customer) sceneData.get(CustomerManagementController.EDITED_CUSTOMER);
				sceneData.remove(CustomerManagementController.EDITED_CUSTOMER);
				
				tfIdentificationCode.setText(customer.getVatNumber());
				actionSearch();
			} else {
				clearForm();
			}
		}
		catch(InitializeGuiException e){
			throw e;
		}
		catch (Exception ex) {
			log.error("onSceneOpen() - Se produjo un error en el tratamiento de los tipos de identificacion", ex);
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Error consultando los documentos de identificación de la tienda."));
		}

		if (tfIdentificationCode.getText() != null && !tfIdentificationCode.getText().equalsIgnoreCase("")) {
			actionSearch();
		}
	}

	@Override
	public void initializeFocus() {
		tfIdentificationCode.requestFocus();
	}

	@FXML
	public void actionSearch() {
		log.trace("actionSearch()");
		String vatNumber = tfIdentificationCode.getText().trim();
		//si no hemos indicado el número de documento no hacemos nada
		if(StringUtils.isBlank(vatNumber)){
			return;
		}
		new SearchCustomersTask(vatNumber).start();
		
		
	}

	@FXML
	public void actionSearchKeyboard(KeyEvent event) {
		log.trace("actionSearchKeyboard()");

		if (event.getCode() == KeyCode.ENTER) {
			actionSearch();
		}
	}

	protected class SearchCustomersTask extends BackgroundTask<List<Customer>> {

		protected String ident;
		protected boolean loyalcustomer;

		public SearchCustomersTask(String ident) {
			this.ident = ident;
		}

		@Override
		protected List<Customer> execute() throws Exception {
			auditOperation(new ComerzziaAuditEventBuilder().addOperation("search")
					.addField("identificationCode", ident));
			
			List<Customer> result = null;
			offline = false;
			loyalcustomer = false;
			if(Devices.getInstance().getLoyaltyCard().isLoyaltyCardPrefix(ident)) {
				loyalcustomer = true;
				LyCustomerDetail customer = customersApi.findLyCustomerByCard(ident).getBody();
				LyCustomerLink link = customer.getCustomerLinks().stream().filter(cl -> cl.getClassUid().equals("D_CLIENTES_TBL.CODCLI")).findFirst().orElse(null);
				if(link == null) {
					return null;
				}
				
				Customer customerResult = customersService.findRemoteById(link.getObjectUid());
				if( customerResult == null) {
					return null;
				}
				
				result = Arrays.asList(customerResult);
			}else {
				try {
					result = customersService.findRemoteByIdentificationCode(ident);
				} catch (Exception e) {
					log.warn("SearchCustomersTask() - An error was throw querying remote customer. Trying local query.", e);
				}
				
				if(CollectionUtils.isEmpty(result)) {
					offline = true;
					result = customersService.findByTaxIdentification(null, ident);
				}
			}
			return result;
		}

		@Override
		protected void failed() {
			super.failed();
			log.error(getCMZException().getLocalizedMessage(), getCMZException());
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha ocurrido un error realizando la consulta de clientes."), getCMZException());
			tfIdentificationCode.requestFocus();
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			List<Customer> result = getValue();

			if(result == null || result.isEmpty()) {
				clearForm();
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se ha encontrado ningún cliente con el parámetro introducido."));
				return;
			}
			
			CustomerIdentificationControllerAbstract.this.result = result;
			if(result.size() == 1) {
				sell(0);
				return;
			}
			
			auditOperation(new ComerzziaAuditEventBuilder().addOperation("searchResult")
					.addField("identificationCode", ident)
                    .addField("offline", offline)
                    .addField("loyalcustomer", loyalcustomer));
			
			refreshScreenData();
			tfIdentificationCode.requestFocus();
		}
	}
	
	protected void refreshScreenData() {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("customers", result);
		params.put("offline", offline);
		
		loadWebView(getWebViewPath(), params, wvMain);
	}
	
	protected String getWebViewPath() {
		return "sales/identified/retailidentification";
	}
	
	
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if("EditCustomer".equals(method)) {
			String strId = params.get("index");
			Integer index = Integer.valueOf(strId);
			
			edit(index);
		} 
		else if("SellToCustomer".equals(method)) {
			String strId = params.get("index");
			Integer index = Integer.valueOf(strId);
			
			sell(index);
		} 
		else if("createClient".equals(method)) {
			newCustomer();
		} 
		else if("sellGeneric".equals(method)) {
			genericCustomer();
		}
	}

	public void edit(Integer index) {
		//TODO: MSB
		Customer customer = this.result.get(index);
		if (customer != null) {
			ObservableList<CustomerDto> customerList = FXCollections.observableList(new ArrayList<CustomerDto>());
			customerList.add(new CustomerDto(customer));
			sceneData.clear();
			sceneData.put(CustomerManagementController.INDEX_SELECTED_CUSTOMER, 0);
			sceneData.put(CustomerManagementController.CUSTOMERS_LIST, customerList);
			sceneData.put(CustomerManagementController.EDITION_MODE, true);
			sceneData.put(CustomerManagementController.CUSTOMER_STATUS, Status.MODIFIED);
			openScene(CustomerManagementController.class);
		}
	}

	public void newCustomer() {
		sceneData.clear();
		sceneData.put(CustomerManagementController.EDITION_MODE, true);
		sceneData.put(CustomerManagementController.CUSTOMER_STATUS, Status.NEW);
		openScene(CustomerManagementController.class);
	}

	public void sell(Integer index) {
		Customer customer = this.result.get(index);
		if (customer != null) {
			if (checkClientTaxes(customer)) {
				auditOperation(new ComerzziaAuditEventBuilder().addOperation("sell")
						.addField("customer", customer)
                        .addField("offline", offline));
				sceneData.clear();
				sceneData.put(IdentifiedBasketItemizationControllerAbstract.PARAM_CUSTOMER, customer);
				openSalesScreen();
			}
		}
	}

	protected boolean checkClientTaxes(Customer customer) {
		TaxTreatment taxtTreatment = null;
		TaxTreatment storeTaxTreatment = null;
		try {
			taxtTreatment = taxTreatmentService.findById(customer.getTaxTreatmentId());
			storeTaxTreatment = taxTreatmentService.findById(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getTaxTreatmentId());
		}catch(Exception ignore) {}
		if(taxtTreatment == null || !taxtTreatment.getCountryCode().equals(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode())) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No es posible seleccionar este cliente al tener un tratamiento de impuestos no disponible para el país asociado a la tienda actual."));
			return false;
		}else if(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getTaxTreatmentId().equals(taxtTreatment.getTaxTreatmentId()) ||
				DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("El cliente seleccionado tiene un tratamiento de impuestos {0} diferente al de la tienda: {1}. Confirme si desea continuar.",taxtTreatment.getTaxTreatmentDes(),storeTaxTreatment.getTaxTreatmentDes())) ) {	
			
			return true;
		}
		return false;
	}

	public void genericCustomer() {
		sceneData.clear();
		sceneData.put(IdentifiedBasketItemizationControllerAbstract.PARAM_CUSTOMER, modelMapper.map(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer(), Customer.class));
		openSalesScreen();
	}
	
	protected void clearForm() {
		lineDisplay.writeLineUp(I18N.getText("---NUEVO CLIENTE---"));
		
		tfIdentificationCode.clear();
		this.result = null;
		refreshScreenData();
	}
	
	
	public void retrieveBasket() {
		BasketManager<?, ?> basketManager = getBasketManager();
		
		if (saleBasketDocumentService.count(session.getApplicationSession().getCodAlmacen(), session.getApplicationSession().getTillCode(), basketManager.getDefaultDocumentType().getDocTypeId()) == 0) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No hay tickets aparcados"));
			return;
		}
		
		sceneData.clear();
		sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, basketManager);
		sceneData.put(RetrieveBasketControllerAbstract.PARAM_DOCUMENT_TYPE, basketManager.getDocumentType().getDocTypeId());
		
		openScene(RetrieveBasketControllerAbstract.class, new SceneCallback<SaleBasket>() {
			
			@Override
			public void onSuccess(SaleBasket basket) {					
				try {
					basketManager.loadBasket(basket.getBasketUid(), session.getApplicationSession().getValidCatalog());
				} catch (BasketException e) {
					DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Error recuperando cesta."));
					return;
				}
				sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, basketManager);
				openSalesScreen();
			}
		});
	}
	
	protected abstract BasketManager<?, ?> getBasketManager();
	
	@Override
	public boolean canClose() {
		BasketManager<?, ?> basketManager = getBasketManager();
		try {
			if(saleBasketDocumentService.count(session.getApplicationSession().getCodAlmacen(), session.getApplicationSession().getTillCode(), basketManager.getDefaultDocumentType().getDocTypeId()) > 0) {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Existen tickets aparcados pendientes de confirmar. Antes debería finalizar la operación."));
				return false;
			}
			else {
				return true;
			}
        }
        catch (Exception e) {
        	log.error("canClose() - Ha habido un error al inicializar el ticketManager: " + e.getMessage());
        	DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se ha podido cerrar la pantalla. Contacte con el administrador."));
        	return false;
        }
	}
	
	public void checkUIPermission() {
		super.checkUIPermission();
		bottomButtonsGroup.checkOperationsPermissions();
	}
	
	protected void loadBottomButtonsPanel() throws LoadWindowException {
		try {
			ButtonsGroupModel panelBotoneraBean = getSceneView().loadButtonsGroup("_panel.xml");
			bottomButtonsGroup = new ButtonsGroupComponent(panelBotoneraBean, vbBottomButtonsPanel.getPrefWidth(), vbBottomButtonsPanel.getPrefHeight(), this, ActionButtonNormalComponent.class);
			vbBottomButtonsPanel.getChildren().clear();
			vbBottomButtonsPanel.getChildren().add(bottomButtonsGroup);
		}
		catch (InitializeGuiException e) {
			log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
		}
	}
	
	protected void openSalesScreen() {
		stageController.showActionScene(getAction().getActionId(), getBasketItemizationController(), new SceneCallback<Void>() {
			public void onSuccess(Void callbackData) {
				clearForm();
			};
			@Override
			public void onCancel() {
				clearForm();
			}
		});
	}
	
	protected abstract Class<? extends ActionSceneController> getBasketItemizationController();

}
