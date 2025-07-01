package com.comerzzia.pos.gui.sales.basket;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.validation.ConstraintViolation;

import com.comerzzia.catalog.facade.model.CatalogSettings;
import com.comerzzia.catalog.facade.service.CatalogManager;
import com.comerzzia.omnichannel.facade.service.sale.customer.CustomerServiceFacade;
import com.comerzzia.omnichannel.facade.service.store.StorePosServiceFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.comerzzia.api.documents.client.BasketApiClient;
import com.comerzzia.api.documents.client.model.BasketDeleteRequest;
import com.comerzzia.catalog.facade.model.CatalogItemDetail;
import com.comerzzia.catalog.facade.model.CatalogItemUnitMeasure;
import com.comerzzia.catalog.facade.model.filter.CatalogItemSearch;
import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.barcode.decoders.model.BarcodeDecoded;
import com.comerzzia.omnichannel.barcode.decoders.service.BarcodeDecoderManager;
import com.comerzzia.omnichannel.facade.model.PaginatedRequest;
import com.comerzzia.omnichannel.facade.model.basket.BasketCreateRequest;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.NewBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.UpdateBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCashier;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCustomer;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomerCoupon;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketServiceData;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItemItemData;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketRateItem;
import com.comerzzia.omnichannel.facade.model.basket.payments.BasketPayment;
import com.comerzzia.omnichannel.facade.model.basketdocument.SaleBasket;
import com.comerzzia.omnichannel.facade.model.basketdocument.SaleBasketDetail;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.model.store.StorePosServicesData;
import com.comerzzia.omnichannel.facade.model.store.StoreServiceType;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.basket.BasketManagerBuilder;
import com.comerzzia.omnichannel.facade.service.basket.exception.BasketLineException;
import com.comerzzia.omnichannel.facade.service.basket.exception.BasketUpdateException;
import com.comerzzia.omnichannel.facade.service.basket.exception.CouponValidationException;
import com.comerzzia.omnichannel.facade.service.basketdocument.SaleBasketDocumentServiceFacade;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.DeviceCallback;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.devices.device.loyaltycard.DeviceLoyaltyCardException;
import com.comerzzia.pos.core.devices.device.scale.DeviceScale;
import com.comerzzia.pos.core.devices.device.scale.DeviceScaleDummy;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScannerDataReaded;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.actionbutton.normal.ActionButtonNormalComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.htmlparser.LongOperationTask;
import com.comerzzia.pos.core.gui.login.userselection.UserSelectionController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.gui.util.modalweb.ModalWebController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.balancecard.BalanceCardService;
import com.comerzzia.pos.core.services.coupons.CouponsService;
import com.comerzzia.pos.core.services.session.CashJournalSession;
import com.comerzzia.pos.core.services.session.SesionInitException;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.devices.scanner.emulation.DeviceScannerKeyboardEmulation;
import com.comerzzia.pos.gui.sales.balancecard.items.BalanceCardItemizationController;
import com.comerzzia.pos.gui.sales.balancecard.search.BalanceCardSearchController;
import com.comerzzia.pos.gui.sales.basket.retrieval.RetrieveBasketController;
import com.comerzzia.pos.gui.sales.basket.retrieval.RetrieveBasketControllerAbstract;
import com.comerzzia.pos.gui.sales.basket.retrieval.remote.RemoteRetrieveBasketController;
import com.comerzzia.pos.gui.sales.document.DocumentManagementController;
import com.comerzzia.pos.gui.sales.item.picklist.PickListControllerAbstract;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerController;
import com.comerzzia.pos.gui.sales.loyalcustomer.search.LoyalCustomerSearchController;
import com.comerzzia.pos.gui.sales.retail.coupons.CustomerCouponsController;
import com.comerzzia.pos.gui.sales.retail.creditsaleretrieval.CreditSaleRetrievalController;
import com.comerzzia.pos.gui.sales.retail.customer.CustomerSearchController;
import com.comerzzia.pos.gui.sales.retail.items.ItemLineValidationForm;
import com.comerzzia.pos.gui.sales.retail.items.QueryLoyalCustomerByCardNumberTask;
import com.comerzzia.pos.gui.sales.retail.items.search.ItemSearchController;
import com.comerzzia.pos.gui.sales.retail.items.serialnumbers.RetailSerialNumberController;
import com.comerzzia.pos.gui.sales.retail.pointsredemption.PointsRedemptionController;
import com.comerzzia.pos.gui.sales.scale.askweight.AskWeightControllerAbstract;
import com.comerzzia.pos.gui.sales.serialnumber.SerialNumberControllerAbstract;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class BasketItemizationControllerAbstract <T extends BasketManager<?, ?>> extends ActionSceneController implements ButtonsGroupController {
	
	public static final String PERMISSION_CANCEL_SALE = "CANCEL SALE";
	public static final String PERMISSION_NEGATE_LINE = "NEGATE LINE";
	public static final String PERMISSION_GENERIC_ITEMS = "GENERIC ITEMS";
	public static final String PERMISSION_EDIT_LINE = "EDIT LINE";
	
	public static final String PARAM_CANCELED_LINE = "CANCELED_LINE";
	public static final String PARAM_CANCEL_LINE = "CANCEL_LINE";
	
	public static final String BASKET_KEY = "BASKET_KEY";
	public static final String CATALOG_KEY = "CATALOG_KEY";

	public static final String PARAM_CARD_NUMBER = "CARD_NUMBER";
	

	protected T basketManager;
	
	protected Catalog catalog;

	protected DeviceLineDisplay displayDevice;

	@FXML
	protected TextField tfItemCode;
	@FXML
	protected NumericTextField tfItemQuantity;
	@FXML
	protected NumericKeypad numpad;

	@FXML
	protected ImageView imgInfo;
	
	@FXML
	protected Button btPayment;

	@FXML
	protected HBox parentHbox;

	// Barcode validation form
	protected ItemLineValidationForm frValidation, frSearchValidation;

	@Autowired
	protected Session session;
	
	@Autowired
	protected CashJournalSession cashJournalSession;

	@Autowired
	protected CatalogManager catalogManager;
	
	@Autowired
	protected BarcodeDecoderManager barcodeDecoderManager;
	
	@Autowired
	protected VariableServiceFacade variableService;

	@Autowired
	protected StorePosServiceFacade storePosService;

	@Autowired
	protected CustomerServiceFacade customerService;
	
	@Autowired
    protected CouponsService couponsService;
	
	@Autowired
    protected SaleBasketDocumentServiceFacade saleBasketDocumentService;
	
	protected ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

	@Autowired
	protected ComerzziaTenantResolver tenantResolver;
	
	@Autowired
	protected BalanceCardService balanceCardService;
	
	@Autowired
	protected ModelMapper modelMapper;
	
	@Autowired
	protected BasketApiClient basketApi;
		
	@FXML
	protected VBox vbContainerWebView, vbTopButtonsPanel, vbBottomButtonsPanel, vbGeneral, vbOtherButtons, vbOtherButtonsPane;
	
	protected ButtonsGroupComponent topButtonsGroup, bottomButtonsGroup, otherButtonsGroup;
	
	@FXML
	protected WebView wvPrincipal;
	
	protected String combination1Code;
	protected String combination2Code;
	protected boolean zeroPriceAllowed = false;
	
	protected Boolean cashLimitWarning;
	protected Boolean cashLimitLock;
	protected Long parkedTickets;
	
	protected FormatUtils formatUtils = FormatUtils.getInstance();
	
	protected StoreServiceType defaultServiceType;
	
	protected ScannerObserver scannerObserver;
	
	public class ScannerObserver implements Observer {

		@Override
		public void update(Observable o, Object arg) {
			if (!(arg instanceof DeviceScannerDataReaded) || getStage().getScene() != getScene()) return;

			if (!tfItemCode.isFocused() || tfItemCode.isDisabled()) {
				Devices.getInstance().getScanner().beepError();
				return;
			}
			DeviceScannerDataReaded scanData = (DeviceScannerDataReaded)arg;
			
			NewBasketItemRequest itemRequest = new NewBasketItemRequest();
			itemRequest.setBarcode(scanData.getScanData());
			itemRequest.setScanned(true);
			itemRequest.setScanCodeType(scanData.getScanDataType());
			
			Platform.runLater(()->{
				frValidation.setCantidad(tfItemQuantity.getText().trim());
				if (validateForm())  {
					BigDecimal quantity = frValidation.getCantidadAsBigDecimal();
					itemRequest.setQuantity(quantity);
					
					newbasketItemRequest(itemRequest);
				}	
			});					
		}
		
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando pantalla...");
		
		vbOtherButtons.setVisible(false);
		vbOtherButtons.setManaged(false);
	}

	@Override
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if("AddItem".equals(method)) {
			String itemCode = params.get("ItemId");
			String quantity = params.get("Units");
			newBasketItemRequest(itemCode, new BigDecimal(quantity), null);
		} else if("EditItem".equals(method)) {
			String paramLineId = params.get("line_id");
			Integer lineId = Integer.valueOf(paramLineId);
			
			editItem(lineId);
		} else if("DeleteItem".equals(method)) {
			String paramLineId = params.get("line_id");
			Integer lineId = Integer.valueOf(paramLineId);
			
			deleteItem(lineId);
		} else if("CancelItem".equals(method)) {
			String paramLineId = params.get("line_id");
			Integer lineId = Integer.valueOf(paramLineId);
			
			cancelItem(lineId);
		} else if("NegateItem".equals(method)) {
			String paramLineId = params.get("line_id");
			Integer lineId = Integer.valueOf(paramLineId);
			
			negateItem(lineId);
		} else if("DeleteCoupon".equals(method)) {
			String couponCode = params.get("coupon_code");
			
			deleteCandidateCoupon(couponCode);
		} else if("RemoveLoyaltySettings".equals(method)) {
			removeLoyaltySettings();
		} else if("CashWithdraw".equals(method)) {
			Long actionId = Long.valueOf(params.get("action_id"));
			openActionScene(actionId);
		} else if("ShowInfoItem".equals(method)) {
			try {					
				sceneData.put(ModalWebController.PARAM_URL, params.get("url"));
				openScene(ModalWebController.class);
			}
			catch (Exception e) {
				log.error("addUrlHandlersItemsOperations() - Error:" + e.getMessage(), e);
				DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha habido un error al mostrar la página web"), e);
			}
		} else if("AddQuantityItem".equals(method)) {
			String paramLineId = params.get("line_id");
			String paramQuantity = params.get("quantity");
			Integer lineId = Integer.valueOf(paramLineId);
			BigDecimal quantity = new BigDecimal(paramQuantity);
			
			addQuantityItem(lineId, quantity);
		} else if("SalesOperations".equals(method)) {
			String operation = params.get("operation");
			executeOperation(operation, params);
		} else if("AddVoucherPromotionCode".equals(method)) {
			tfItemCode.setText(params.get("VoucherId"));
			tfItemQuantity.setText(formatUtils.formatNumber(BigDecimal.ONE, 3));

			addNewItemCode();
		}
	}
	
	protected void executeOperation(String operation, Map<String, String> params) {
		if(operation.equals("CancelSale")) {
			cancelSale();
		}
		else if(operation.equals("ParkTicket")) {
			parkBasket();
		}
		else if(operation.equals("RetrieveBasket")) {
			retrieveBasket();
		}
		else if(operation.equals("SearchItem")) {
			openSearchItem();
		}
		else if(operation.equals("Customer")) {
			openSearchCustomer();
		}
		else if(operation.equals("LoyalCustomer")) {
			openLoyaltyScene();
		}
		else if(operation.equals("Cashier")) {
			changeCashier();
		}
		else if(operation.equals("ShowCoupons")) {
			useCustomerCoupons();
		}
		else if(operation.equals("ShowLoyaltyInfo")) {
			showLoyaltyInfo();
		}
	}
	
	@Override
	public void initializeComponents() throws InitializeGuiException {
		try {
			log.debug("initializeComponents() - Inicialización de componentes...");
			displayDevice = Devices.getInstance().getLineDisplay();

			loadTopButtonsPanel();
			loadBottomButtonsPanel();
			loadOtherButtonsPanel();

			// Inicializamos los formularios
			frSearchValidation = new ItemLineValidationForm();
			frSearchValidation.setFormField("cantidad", tfItemQuantity);

			frValidation = SpringContext.getBean(ItemLineValidationForm.class);
			frValidation.setFormField("codArticulo", tfItemCode);
			frValidation.setFormField("cantidad", tfItemQuantity);

			//Comprobamos si la tineda tiene desgloses 
			if (session.getApplicationSession().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
				 combination1Code = I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO));
			}
			
			if (session.getApplicationSession().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
				combination2Code = I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO));
			}
			
			zeroPriceAllowed = variableService.getVariableAsBoolean(VariableServiceFacade.TPV_PERMITIR_VENTA_PRECIO_CERO, true);
			
			tfItemQuantity.setText(formatUtils.formatNumber(BigDecimal.ONE, 3));

			tfItemQuantity.focusedProperty().addListener(new ChangeListener<Boolean>(){

				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					if (oldValue) {
						formatQuantity();
					}
				}
			});

			addKeyEventHandler(new EventHandler<KeyEvent>(){

				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.MULTIPLY) {
						if (tfItemQuantity.isFocused()) {
							tfItemCode.requestFocus();
							tfItemCode.selectAll();
						}
						else if(tfItemCode.isFocused() && StringUtils.isNotBlank(tfItemCode.getText())){
							String itemtext = tfItemCode.getText().replaceAll("\\*", "");
							if(formatUtils.parseBigDecimal(itemtext) != null) {
								tfItemQuantity.setText(itemtext);
								formatQuantity();
								tfItemCode.clear();
							}
						}
					}
				}
			}, KeyEvent.KEY_RELEASED);
			
			wvPrincipal.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
					if(newValue) {
						tfItemCode.requestFocus();
					}
				}
			});

			addSelectAllOnFocus();
			
			wvPrincipal.setFocusTraversable(false);
			vbTopButtonsPanel.setFocusTraversable(false);
			vbBottomButtonsPanel.setFocusTraversable(false);
			
			
			
		}
		catch (Exception ex) {
			log.error("initializeComponents() - Error inicializando pantalla de venta de artículos", ex);
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Error cargando pantalla. Para mas información consulte el log."));
		}
	}

	/**
	 * Añade a los campos de texto de la pantalla la capacidad de seleccionar todo su texto cuando adquieren el foco
	 */
	protected void addSelectAllOnFocus() {
		addSelectAllOnFocus(tfItemCode);
		addSelectAllOnFocus(tfItemQuantity);
	}
	
	public void openOnlineCatalog(HashMap<String, String> params) {
		openOnlineCatalog(params.get("url"));
	}
	
	protected void openOnlineCatalog(String url) {
		sceneData.put(ModalWebController.PARAM_URL, url);
		sceneData.put(ModalWebController.PARAM_PARENT_SCENE, this);
		openScene(ModalWebController.class, (NewBasketItemRequest itemRequest) -> newbasketItemRequest(itemRequest));
	}

	/**
	 * Método auxuliar para añadir a un campo de texto la capacidad de seleccionar todo su texto cuando adquiere el foco
	 * 
	 * @param campo
	 */
	@Override
	protected void addSelectAllOnFocus(final TextField campo) {
		campo.focusedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				Platform.runLater(new Runnable(){

					@Override
					public void run() {
						if (campo.isFocused() && !campo.getText().isEmpty()) {
							campo.selectAll();
						}
					}
				});
			}
		});
	}
	
	@Override
	protected void executeLongOperations() throws SesionInitException, InitializeGuiException {
		initializeManager();
		parkedTickets = saleBasketDocumentService.count(session.getApplicationSession().getCodAlmacen(), session.getApplicationSession().getTillCode(), basketManager.getDefaultDocumentType().getDocTypeId());
		
		// Realizamos las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
		canOpen();
	}
	
	@Override
	protected void succededLongOperations() {
		checkWithdrawalWarning();
		if(basketManager.isCancellableByUser() && parkedTickets == 1) {
			retrieveBasket();
			return;
		}
		
		BasketPromotable<?> basketTransaction = this.basketManager.getBasketTransaction();
		
		if (basketTransaction != null && basketTransaction.getTenderMode()) {
			openPayments();
		} else {
			cashLimitWarning = null;
			cashLimitLock = null;
			refreshScreenData();
		}
	}
	
	public void initializeManager() throws SesionInitException {
		if(basketManager==null) {
			basketManager = BasketManagerBuilder.build(getBasketClass(), session.getApplicationSession().getStorePosBusinessData());
		}
		
		this.catalog = getValidCatalog();		
		defaultServiceType = basketManager.getDefaultServiceType();		
	}

		
	@SuppressWarnings("unchecked")
	protected Class<? extends T> getBasketClass() {
		return (Class<? extends T>) getTypeArgumentFromSuperclass(getClass(), BasketManager.class, 0);
	}
	
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		log.debug("onSceneOpen() - Inicializando formulario...");
		try { 
			tfItemCode.clear();
			
			if (scannerObserver == null) {
				scannerObserver = new ScannerObserver();
			}
			
			Devices.getInstance().getScanner().addObserver(scannerObserver);
			Devices.getInstance().getScanner().enableReader();
			
		}
		catch (Exception e) {
			log.error("onSceneOpen() - Error inesperado inicializando formulario. ", e);
			throw new InitializeGuiException(e);
		}
	}
	
	@Override
	public void onSceneShow() {
		super.onSceneShow();
		if(!basketManager.isBasketEmpty()) {
			Devices.getInstance().getLineDisplay().saleMode(basketManager.getBasketTransaction());;
		}
	}

	/**
	 * Realiza las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
	 * 
	 * @throws InitializeGuiException
	 */
	protected void canOpen() throws InitializeGuiException {
		log.debug("canOpen()");
		
		if (!session.getCashJournalSession().isOpenedCashJournal()) {
			Boolean automaticOpening = variableService.getVariableAsBoolean(VariableServiceFacade.CAJA_APERTURA_AUTOMATICA, true);
			if (automaticOpening) {
				//TODO MSB: Add Message to webview
				session.getCashJournalSession().openAutomaticCashJournal();
			}
			else {
				throw new InitializeGuiException(I18N.getText("No hay caja abierta. Deberá ir a la gestión de caja para abrirla."));
			}
		}
		
		if (!session.getCashJournalSession().checkCashJournalClosingMandatory()) {
			String cashJournalDate = formatUtils.formatDate(session.getCashJournalSession().getOpeningDate());
			String actualDate = formatUtils.formatDate(new Date());
			throw new InitializeGuiException(I18N.getText("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", cashJournalDate, actualDate));
		}
				
	}

	@Override
	public void initializeFocus() {
		Devices.getInstance().getScanner().enableReader();
		
		tfItemCode.requestFocus();
	}

	@Override
	public boolean canClose() {
		int numLineas = 0;
		
		if (basketManager.isCancellableByUser()) {
			basketManager.cancelBasket();
		} else {		
			numLineas = basketManager.getBasketTransaction().getLines().size();
		}
				
		if (numLineas > 0) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La venta se ha iniciado. Deberá anular o finalizar la venta."));
			
			return false;
		}
		
		return true;
	}
	
	protected class BackgroundUpdateCatalogTask extends LongOperationTask<Catalog>{

		public BackgroundUpdateCatalogTask(SceneController sceneController) {
			super(sceneController, true);
		}

		@Override
		protected Catalog execute() throws Exception {			
			return getValidCatalog();
		}
		
		@Override
		protected void taskEnd() {			
			finallizedUpdateCatalogTask(getValue());
		}
		
	}
	
	protected Catalog getValidCatalog() {
		Catalog result = applicationSession.getValidCatalog();
		
		// load promotions
		result.getPromotionsSession();
		
		return result;
	}

	protected class BackgroundCreateBasketForCustomerTask extends LongOperationTask<Catalog> {

		protected Customer customer;

		public BackgroundCreateBasketForCustomerTask(SceneController sceneController, Customer customer) {
			super(sceneController, true);
			this.customer = customer;
		}

		@Override
		protected Catalog execute() throws Exception {
			return getValidCatalogForCustomer(customer);
		}

		@Override
		protected void taskEnd() {
			finalizedCreateBasketForCustomerTask(getValue(), customer);
		}

	}

	protected Catalog getValidCatalogForCustomer(Customer customer) {
		CatalogSettings catalogSettings = storePosService.getCatalogSettings(applicationSession.getStorePosBusinessData(), customer);

		Catalog result = catalogManager.getCatalog(catalogSettings);

		result.getPromotionsSession();

		return result;
	}
	
	protected void updateCatalog() {
		new BackgroundUpdateCatalogTask(this).start();
	}

	protected void updateCatalogForCustomer(String customerCode) {
		Customer customer = customerService.findById(customerCode);
		this.catalog = getValidCatalogForCustomer(customer);
	}

	protected void finallizedUpdateCatalogTask(Catalog catalog) {
		this.catalog = catalog;
		
		parkedTickets = null;
		cashLimitWarning = null;
		refreshScreenData();
		initializeFocus();
	}

	protected void finalizedCreateBasketForCustomerTask(Catalog catalog, Customer customer) {
		this.catalog = catalog;

		createAndPersistNewBasket(null, null, null, modelMapper.map(customer, BasketCustomer.class));
		refreshScreenData();
		initializeFocus();
	}
	/**
	 * Acción de introducción de código desde la interfaz
	 *
	 * @param event
	 */
	public void actionTfItemCode(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			String scanTypeCode = null;
			if(!KeyCode.ENTER.toString().equals(event.getText()) && Devices.getInstance().getScanner() instanceof DeviceScannerKeyboardEmulation ) {
				scanTypeCode = DeviceScannerKeyboardEmulation.DEFAULT_SCAN_TYPE_CODE;
			}
			addNewItemCode(scanTypeCode);
		}
	}

	/**
	 * Acción que anula un ticket
	 */
	public void cancelSale() {
		log.debug("cancelSale()");
		
		// check user permissions
		try {
			checkOperationPermissions(PERMISSION_CANCEL_SALE);
		}
		catch (PermissionDeniedException ex) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No tiene permisos para cancelar la venta"));
			return;
		}

		// check if basket has payments
		if (!basketManager.isBasketEmpty()) {
			BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
			
			for(BasketPayment pago : basketTransaction.getPayments().getPaymentsList()) {
				if(pago.getCanBeDeleted()) {
					log.error("cancelSale() - Se ha intentado cancelar una venta con pagos asociados.");
					DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se puede cancelar una venta con pagos asociados."));
					return;
				}
			}
			
			if (!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Está seguro de cancelar la venta?"))) return;
		}
		
		// cancel basket
		try {
			basketManager.cancelBasket();
			
			// Restauramos la cantidad en la pantalla
			tfItemQuantity.setText(formatUtils.formatNumber(BigDecimal.ONE, 3));
			
			updateCatalog();
		}catch(Throwable e) {
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("No se pudo cancelar la cesta"), e);
		}
	}

	/**
	 * Acción de introducción de cantidad desde la interfaz
	 *
	 * @param event
	 */
	public void actionTfItemQuantity(KeyEvent event) {
		log.debug("actionTfCantidadIntro() - acción de introducción de cantidad de artículo");
		if (event.getCode() == KeyCode.ENTER) {
			tfItemCode.requestFocus();
			tfItemCode.selectAll();
		}
	}

	protected void formatQuantity() {
		tfItemQuantity.setText(tfItemQuantity.getText().replace("*", ""));
		if (tfItemQuantity.getText().isEmpty()) {
			tfItemQuantity.setText(formatUtils.formatNumber(BigDecimal.ONE, 3));
		}
		
		frValidation.setCantidad(tfItemQuantity.getText().trim());
		frValidation.setCodArticulo(tfItemCode.getText().trim());
		if (validateForm()) {
			BigDecimal bigDecimal = formatUtils.parseBigDecimal(tfItemQuantity.getText().trim());
			tfItemQuantity.setText(formatUtils.formatNumber(bigDecimal, 3));
		}
		else {
			tfItemQuantity.setText(formatUtils.formatNumber(BigDecimal.ONE, 3));
			tfItemQuantity.selectAll();
			tfItemQuantity.requestFocus();
		}
	}

	/**
	 * @deprecated This method is not longer called by the standard jpos. 
	 */
	@Deprecated
	protected void nuevaCantidad() {
		log.debug("nuevaCantidad() - preparamos la interfaz para un cambio de código tras cambiar una cantidad");
		tfItemQuantity.setText(tfItemQuantity.getText().replace("*", ""));
		if (tfItemQuantity.getText().isEmpty()) {
			tfItemQuantity.setText(formatUtils.formatNumber(BigDecimal.ONE, 3));
		}
	}
	/**
	 * @deprecated This method is not longer called by the standard jpos. 
	 */
	@Deprecated
	public void updateQuantity() {
		log.debug("updateQuantity() - preparamos la interfaz para una modificación de la cantidad");
		tfItemCode.setText(tfItemCode.getText().replace("*", ""));
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				tfItemQuantity.requestFocus();
			}
		});
		tfItemQuantity.selectAll();
	}
	
	public boolean checkWithdrawalWarning() {
		try {
			if(session.getCashJournalSession().validateWarningWithdrawalAmount()){
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Ha superado el límite de efectivo. Deberá realizar una retirada"));
				return true;
			}							
		} catch (Exception e) {
			log.error("checkWithdrawalWarning() - Excepción en cashJournalService : " + e.getCause(), e);
		}
		return false;
	}

	public boolean checkWithdrawalBlock() {
		try {
			if (session.getCashJournalSession().validateBlockWithdrawalAmount()) {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Debe realizar una retirada de efectivo para poder continuar"));
				return true;
			}							
		} catch (Exception e) {
			log.error("checkWithdrawalBlock() - Excepción en cashJournalService : " + e.getCause(), e);
		}
		return false;
	}
	
	public void openLoyaltyScene() {
		sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, basketManager);
		openScene(LoyalCustomerSearchController.class, (String cardNumber)-> searchLoyaltyCard(cardNumber));
	}
	
	protected void searchLoyaltyCard(String cardCode) {
		if (!Devices.getInstance().getLoyaltyCard().isLoyaltyCardPrefix(cardCode)) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La tarjeta de fidelización no es válida."));
			return;
		}
		
		// force basket loyalty card
		BasketLoyalCustomer loyalCustomer = new BasketLoyalCustomer();
		loyalCustomer.setCardNumber(cardCode);
		
		if (basketManager.getBasketTransaction() == null) {
			createAndPersistNewBasket(null, null, loyalCustomer, null);
		} else {
			basketManager.updateLoyaltySettings(loyalCustomer);
		}
		
		refreshScreenData();		

		// launch background task to find customer
		try {
			Devices.getInstance().getLoyaltyCard().findLoyalCustomerAndCouponsInBackground(cardCode, new DeviceCallback<BasketLoyalCustomer>(){

				@Override
				public void onSuccess(BasketLoyalCustomer tarjeta) {					
					if (!tarjeta.getCardActive()) {
						DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("La tarjeta de fidelización {0} no está activa", tarjeta.getCardNumber()));
						tarjeta = null;
					}
					
					basketManager.updateLoyaltySettings(tarjeta);

					refreshScreenData();
				}

				@Override
				public void onFailure(Throwable e) {
					log.debug("searchLoyaltyCard() - Could not find loyal customer data.", e);
					DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se pudo cargar la información de fidelizado."));
				}
			});
		} catch (DeviceLoyaltyCardException e) {
			log.debug("searchLoyaltyCard() - An exception was thrown ", e);
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(e.getMessage());
		}		
	}
	
	
    /**Llamado cuando se inserta un código de barra especial de tipo ticket. El estándar no ejecuta nada pero puede ser sobreescrito
     * @param codBarrasEspecial */
    protected boolean ticketSpecialBarcodeOperation(BarcodeDecoded codBarrasEspecial) {
    	return true;
	}
	
	protected boolean checkSpecialBarcode(NewBasketItemRequest basketItemRequest) {
		BarcodeDecoded specialBarcodeData = null;
		
		try {
			specialBarcodeData = barcodeDecoderManager.isSpecialBarcode(basketItemRequest.getBarcode());
		}catch(Throwable e) {
			log.error("checkSpecialBarcode() - An error was thrown parsing the special barcode.", e);;
		}
		
		if (specialBarcodeData == null) {
			return true;
		}
				
		String itemCode = specialBarcodeData.getFieldValue(BarcodeDecoded.ITEM_CODE);
		String barcode = specialBarcodeData.getFieldValue(BarcodeDecoded.BARCODE);
		String scalePlu = specialBarcodeData.getFieldValue(BarcodeDecoded.SCALE_PLU);

		// no item in barcode. Invoke special barcode operation
		if (itemCode == null && barcode == null && scalePlu == null) {
			return ticketSpecialBarcodeOperation(specialBarcodeData);
		}
		
		// special barcode data to basketItemRequest
		if (itemCode != null) {
			basketItemRequest.setItemCode(itemCode);
						
			String combination1Code = specialBarcodeData.getFieldValue(BarcodeDecoded.ITEM_COMBINATION);
			
			if (combination1Code != null) {			
				basketItemRequest.setCombination1Code(combination1Code);
			}
		}
		
		
		if (barcode != null) {
			basketItemRequest.setBarcode(barcode);
		}
		
		
		if (scalePlu != null) {
			basketItemRequest.setScalePlu(Integer.valueOf(scalePlu));
		}
		
		// continue reading items parameters from barcode
		String unitMeasureQuantity = specialBarcodeData.getFieldValue(BarcodeDecoded.MEASURE_QUANTITY);
		
		if (unitMeasureQuantity != null) {
			basketItemRequest.setUnitMeasureQuantity(new BigDecimal(unitMeasureQuantity));	
		}
						
		String quantity = specialBarcodeData.getFieldValue(BarcodeDecoded.QUANTITY);
		
		if (quantity != null) {
			basketItemRequest.setQuantity(new BigDecimal(quantity));	
		}
		
		String weigth = specialBarcodeData.getFieldValue(BarcodeDecoded.WEIGHT);
		
		if (weigth != null) {
			basketItemRequest.setWeight(new BigDecimal(weigth));
			basketItemRequest.setQuantity(basketItemRequest.getWeight());
		}
				
		String price = specialBarcodeData.getFieldValue(BarcodeDecoded.PRICE);
		
		if (price != null) {
			basketItemRequest.setDirectPrice(new BigDecimal(price));
			basketItemRequest.setManualPrice(true);
		}		
		
		String serialNumber = specialBarcodeData.getFieldValue(BarcodeDecoded.SERIAL_NUMBER);
		
		if (serialNumber != null) {
			if (basketItemRequest.getSerialNumbers() != null) {
				basketItemRequest.getSerialNumbers().add(serialNumber);
			} else {
				basketItemRequest.setSerialNumbers(new HashSet<>(Arrays.asList(serialNumber)));
			}
		}
		
		// add all fields to request custom data
		Map<String, Object> customData = basketItemRequest.getCustomData();
		if (customData == null) {
			customData = new HashMap<>();
			basketItemRequest.setCustomData(customData);
		}
		
		for (Entry<String, String> entry : specialBarcodeData.getValues().entrySet()) {
			if (StringUtils.equalsIgnoreCase(entry.getKey(), BarcodeDecoded.IGNORE)) continue;
			
			customData.put(entry.getKey(), entry.getValue());
		}
		
		
		return true;
	}
	
	protected void validateAndAddCandidateCoupon(String couponCode) throws BasketLineException {
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		
		Long loyalCustomerId = basketTransaction.getHeader().getLoyalCustomer() != null ? basketTransaction.getHeader().getLoyalCustomer().getLyCustomerId() : 0L;
		
		BasketLoyalCustomerCoupon coupon;
		
		try {						
			coupon = couponsService.validateCoupon(couponCode, loyalCustomerId);
		} catch (CouponValidationException ex) {
			log.warn("nuevaLineaArticulo() - Error en la validación del cupón -" + ex.getMessage());
			throw new BasketLineException(I18N.getText("Error en la validación del cupón"), ex);
		}
		
		if (basketManager.isBasketEmpty()) {
			createAndPersistNewBasket(null, coupon, null, null);
		} else {					
			addCandidateCoupon(coupon);
		}
	}
	
	protected void addCandidateCoupon(BasketLoyalCustomerCoupon coupon) throws BasketLineException {
		try {
			basketManager.addCandidateCoupon(coupon);				
		}catch(CouponValidationException e) {
			throw new BasketLineException(e);
		}
	}
	
	
	/**
	 * Calls {@link #addNewItemCode(String)} without scan data.
	 */
	public void addNewItemCode() {
		addNewItemCode(null);
	}
	
	
	/**
	 * Añade un nuevo artículo
	 */
	public void addNewItemCode(String scanCodeType) {
		// Validamos los datos
		log.debug("addNewItemCode() - Creando línea de artículo");

		frValidation.setCantidad(tfItemQuantity.getText().trim());
		frValidation.setCodArticulo(tfItemCode.getText().trim().toUpperCase());
		BigDecimal cantidad = frValidation.getCantidadAsBigDecimal();
		tfItemCode.clear();
		
		// UAT files
		if (manageUATOperation(frValidation.getCodArticulo())) {
			return;
		}

		if (validateForm() && cantidad != null && !BigDecimalUtil.isEqualsToZero(cantidad)) {			
			log.debug("addNewItemCode() - Formulario validado");
			newBasketItemRequest(frValidation.getCodArticulo(), cantidad, scanCodeType);
		}
	}

	
	/**
	 * Método llamado desde la Botonera si hay algún botón de tipo ITEM
	 */
	public void addItem(String item) {
		newBasketItemRequest(item, BigDecimal.ONE, null);
	}
	
	/**
	 * initialize and send NewBasketItemRequest
	 * @param code Item code/barcode
	 * @param quantity Line quantity
	 * @param scanCodeType ScanTypeCode if scanned, otherwise null.
	 */
	public void newBasketItemRequest(String code, BigDecimal quantity, String scanCodeType) {
		NewBasketItemRequest basketItemRequest = new NewBasketItemRequest();
		
		basketItemRequest.setBarcode(code);
		basketItemRequest.setQuantity(quantity);
		
		if(StringUtils.isNotBlank(scanCodeType)) {
			basketItemRequest.setScanned(true);
			basketItemRequest.setScanCodeType(scanCodeType);
		}
		
		newbasketItemRequest(basketItemRequest);
	}
	
	protected boolean manageUATOperation(String code) {
		if (!AppConfig.getCurrentConfiguration().getDeveloperMode() || 
			!StringUtils.startsWith(frValidation.getCodArticulo(), "#")) {
			return false;			
		}
		
		if (StringUtils.startsWith(code, "#!")) {
			if (StringUtils.length(code) == 2 || basketManager.isBasketEmpty()) return false; // no file name or empty basket
			
			// save new UAT file
			String uatName = code.substring(2).toUpperCase();
			
			basketManager.saveToFile(uatName);
			basketManager.cancelBasket();
			updateCatalog();
		} else {
			if (StringUtils.length(code) == 1) return false; // no file name
			
			// load UAT file
			String uatName = code.substring(1).toUpperCase();
			
			try {
				basketManager.loadFromFile(uatName, catalog);
				BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
				if(basketTransaction.getHeader().getLoyalCustomer()!=null) {
					searchLoyaltyCard(basketTransaction.getHeader().getLoyalCustomer().getCardNumber());
				}
			} catch (NotFoundException e) {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Archivo de UAT no encontrado"));
			}
			refreshScreenData();			
		}
		
		return true;
	}	

	/**
	 * Añade un nuevo artículo
	 */
	public void newbasketItemRequest(NewBasketItemRequest basketItemRequest) {	
		if ((StringUtils.isBlank(basketItemRequest.getBarcode()) && StringUtils.isBlank(basketItemRequest.getItemCode())) ||
			 (basketItemRequest.getQuantity() == null || basketItemRequest.getQuantity().compareTo(BigDecimal.ZERO) == 0)) return;
						
		if(checkWithdrawalBlock()) return;
		
		// is coupon direct access code ...
		Long promotionId = catalog.getPromotionsSession().getPromotionIdFromDirectAccessCode(basketItemRequest.getBarcode());
		
		if (promotionId != null) {
			log.debug("newbasketItemRequest() - Is promotion direct access coupon code...");
			BasketLoyalCustomerCoupon coupon = new BasketLoyalCustomerCoupon();
			coupon.setCouponCode(basketItemRequest.getBarcode());
			coupon.setPromotionId(promotionId);
			coupon.setActive(true);
			
			if (basketManager.isBasketEmpty()) {
				createAndPersistNewBasket(null, coupon, null, null);
			} else {			
				try {
					addCandidateCoupon(coupon);
				} catch (BasketLineException e) {
					basketItemRequestFailed(e);
				}
			}
			
			refreshScreenData();
			return;
		}
		
		// is coupon code ...
		if (couponsService.isCouponCode(basketItemRequest.getBarcode())) {					
			log.debug("newbasketItemRequest() - Is coupon...");

			try {
				validateAndAddCandidateCoupon(basketItemRequest.getBarcode());
			} catch (BasketLineException e) {
				basketItemRequestFailed(e);
			}

			refreshScreenData();
			return;
		}
		
		// Si es prefijo de tarjeta fidelizacion, marcamos la venta como fidelizado y llamamos al REST
		if (Devices.getInstance().getLoyaltyCard().isLoyaltyCardPrefix(basketItemRequest.getBarcode())) {
			searchLoyaltyCard(basketItemRequest.getBarcode());
			
			return;
		}
		// update item request if is a special barcode
		if (!checkSpecialBarcode(basketItemRequest)) {
			return;
		}
		
		
		try {
			CatalogItemDetail catalogItem = null;
			
			// en las personalizaciones es posible que ya vengan asignados o manipulados los datos de artículos
			if (basketItemRequest.getItemData() == null || basketItemRequest.getRateItem() == null) {
				CatalogItemSearch catalogItemSearch = modelMapper.map(basketItemRequest, CatalogItemSearch.class);
				
				catalogItem = catalog.getCatalogItemService().findByFilter(catalogItemSearch);
				
				if (catalogItem == null) throw new NoSuchElementException();
							
				basketItemRequest.setItemCode(catalogItem.getItemCode());
				setRateItemFromCatalogItem(basketItemRequest, catalogItem);
				basketItemRequest.setItemData(modelMapper.map(catalogItem, BasketItemItemData.class));
				
				// DUN14 conversion factor
				BigDecimal conversionFactor = basketItemRequest.getItemData().getBarcodeConversionFactor();
				if (Boolean.TRUE.equals(basketItemRequest.getItemData().getDun14()) && 
					conversionFactor != null && 
					BigDecimalUtil.isGreaterThanZero(conversionFactor)) {
					
					BigDecimal quantity = basketItemRequest.getUnitMeasureQuantity();
					
					if(quantity == null) {
						quantity = basketItemRequest.getQuantity();
					}
					
					// find unit measure
					for (CatalogItemUnitMeasure unitMeasure : catalogItem.getUnitsMeasure()) {
						if (BigDecimalUtil.isEquals(conversionFactor, unitMeasure.getConversionFactor())) {
							basketItemRequest.setUnitMeasureCode(unitMeasure.getUnitMeasureCode());
							break;
						}						
					}
					
					basketItemRequest.setUnitMeasureConversionFactor(conversionFactor);
					basketItemRequest.setQuantity(BigDecimalUtil.round(quantity.multiply(conversionFactor), 3));
					if (basketItemRequest.getUnitMeasureCode() == null) {
						basketItemRequest.setUnitMeasureCode("UN");
						basketItemRequest.setUnitMeasureQuantity(basketItemRequest.getQuantity());
					} else {						
						basketItemRequest.setUnitMeasureQuantity(quantity);
					}
				}				
			}			
			
			checkItemActive(basketItemRequest);
			
			setDefaultUnitMeasure(basketItemRequest);
			
			basketItemRequest.setCashier(modelMapper.map(tenantResolver.getUser(), BasketCashier.class));
						
			if(checkBalanceCardItem(basketItemRequest)) {
				createAndInsertBalanceCard(basketItemRequest);
			}else if(basketItemRequest.getItemData().getGenericItem()) {
				basketItemRequest.setDirectPrice(basketItemRequest.getRateItem().getSalesPriceWithTaxes());
				
				createAndInsertGenericItem(basketItemRequest, catalogItem);
			}else if(basketItemRequest.getItemData().getWeightRequired() && (basketItemRequest.getWeight() == null || BigDecimalUtil.isEqualsToZero(basketItemRequest.getWeight()))) {
				createAndInsertWeightedItem(basketItemRequest);
			}else if(basketItemRequest.getItemData().getSerialNumbersActive() != null && basketItemRequest.getItemData().getSerialNumbersActive()) {
				createAndInsertBasketItemWithSerialsNumbers(basketItemRequest);
				
			}else {
				if (askZeroPrice(basketItemRequest)) {
					createAndInsertBasketItem(basketItemRequest);
				}
			}
		}   catch (NoSuchElementException e) {
			log.debug(e.getMessage(), e);
			Devices.getInstance().getScanner().beepError();
			Devices.getInstance().getScanner().disableReader();
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El artículo consultado no se encuentra en el sistema"));
			Devices.getInstance().getScanner().enableReader();
		}	catch (BusinessException e) {
			log.error(e.getMessage(), e);
			basketItemRequestFailed(e);
		}
	}

	protected void setRateItemFromCatalogItem(NewBasketItemRequest basketItemRequest, CatalogItemDetail catalogItemDetail) {
		if (catalogItemDetail.getRateItem() != null) {
			basketItemRequest.setRateItem(modelMapper.map(catalogItemDetail.getRateItem(), BasketRateItem.class));
		}
		else {
			BasketRateItem rateItem = new BasketRateItem();
			rateItem.setProfitFactor(BigDecimal.ZERO);
			rateItem.setUnitCostPrice(BigDecimal.ZERO);
			rateItem.setSalesPrice(BigDecimal.ZERO);
			rateItem.setSalesPriceRef(BigDecimal.ZERO);
			rateItem.setSalesPriceWithTaxes(BigDecimal.ZERO);
			rateItem.setSalesPriceRefWithTaxes(BigDecimal.ZERO);
			basketItemRequest.setRateItem(rateItem);
		}
	}


	/**
	 * @param newBasketItemRequest
	 */
	protected void setDefaultUnitMeasure(NewBasketItemRequest newBasketItemRequest) {		
		if(StringUtils.isNotBlank(newBasketItemRequest.getItemData().getUnitMeasureCodeAlt())) {
			newBasketItemRequest.setUnitMeasureCode(newBasketItemRequest.getItemData().getUnitMeasureCodeAlt());
		}
		
		if(StringUtils.isBlank(newBasketItemRequest.getUnitMeasureCode())) {
			newBasketItemRequest.setUnitMeasureCode("UN");
		}
		
		if(newBasketItemRequest.getUnitMeasureConversionFactor() == null) {
			newBasketItemRequest.setUnitMeasureConversionFactor(BigDecimal.ONE);
		}
		
		if(newBasketItemRequest.getUnitMeasureQuantity() == null) {
			newBasketItemRequest.setUnitMeasureQuantity(newBasketItemRequest.getQuantity());
		}
	}

	protected boolean checkBalanceCardItem(NewBasketItemRequest basketItemRequest) throws BasketLineException {
		boolean isBalanceCard = balanceCardService.isBalanceCardItem(basketItemRequest.getBarcode());
		if(isBalanceCard && !basketManager.isBasketEmpty()) {
			throw new BasketLineException(com.comerzzia.core.commons.i18n.I18N.getText("No se puede realizar la compra del producto indicado en una cesta con otros artículos"));
		}
		return isBalanceCard;
	}

	protected void createAndInsertGenericItem(NewBasketItemRequest basketItemRequest, CatalogItemDetail catalogItemDetail) throws BasketLineException {
		sceneData.put(BasketItemModificationControllerAbstract.PARAM_TITLE, I18N.getText("Nuevo artículo genérico"));
		sceneData.put(BasketItemModificationControllerAbstract.PARAM_BASKET_ITEM, basketItemRequest);
		sceneData.put(BasketItemModificationControllerAbstract.PARAM_ITEM_DETAIL, catalogItemDetail);
		
		openBasketItemModificationScene(new SceneCallback<NewBasketItemRequest>() {
			@Override
			public void onSuccess(NewBasketItemRequest callbackData) {
				try {
					createAndInsertBasketItem(callbackData);
				} catch (BasketLineException e) {
					basketItemRequestFailed(e);
				}
			}
			
			@Override
			public void onCancel() {
				refreshScreenData();
			}
		});
		
	}

	protected void createAndInsertWeightedItem(NewBasketItemRequest basketItemRequest) throws BasketLineException {
		// request weight from scale
		DeviceScale balanza = Devices.getInstance().getScale();
		
		if (balanza instanceof DeviceScaleDummy) {
		   throw new BasketLineException(I18N.getText("Este artículo no puede ser introducido sin ser pesado previamente."));
		}
		
		if(!balanza.isReady()) {
			throw new BasketLineException(I18N.getText("La balanza no está conectada o no está estable."));
		}
		
		openAskWeightScreen(basketItemRequest, new SceneCallback<NewBasketItemRequest>() {
			@Override
			public void onSuccess(NewBasketItemRequest callbackData) {
				try {
					if (askZeroPrice(callbackData)) {
						createAndInsertBasketItem(callbackData);
					}
				} catch (BasketLineException e) {
					basketItemRequestFailed(e);
				}
			}			
			
			@Override
			public void onCancel() {
				refreshScreenData();
			}
		});
		
	}
	
	protected void createAndInsertBasketItem(NewBasketItemRequest basketItemRequest) throws BasketLineException {
		Assert.notNull(basketItemRequest, "basket item request is null");
		Assert.notNull(basketItemRequest.getItemData(), "basket item data is null");
		Assert.notNull(basketItemRequest.getRateItem(), "basket item rate is null");
		
		// validations of request
		checkItemActive(basketItemRequest);
		checkItemRequiresCombinations(basketItemRequest);
		checkZeroPricedBasketItem(basketItemRequest);
		checkGenericBasketItemPermission(basketItemRequest);
		checkItemRequestSerialNumbers(basketItemRequest);
		
		// new basket or add item
		BasketItem basketItem = null;
		
		if (basketManager.getBasketTransaction() == null) {
			basketItem = createAndPersistNewBasket(basketItemRequest);
		} else {
		    basketItem = basketManager.createAndInsertBasketItem(basketItemRequest);
		}

		if (!validateLine(basketItem)) {
			basketManager.deleteBasketItem(basketItem.getLineId());
		}
		
		newLineRefreshScreen(basketItem);
	}
	
	protected BasketItem createAndPersistNewBasket(NewBasketItemRequest basketItemRequest) {
		BasketPromotable<?> result = createAndPersistNewBasket(basketItemRequest, null, null, null);
				
		return result.getLines().get(0);
	}
	
	protected BasketPromotable<?> createAndPersistNewBasket(NewBasketItemRequest basketItemRequest, BasketLoyalCustomerCoupon coupon, BasketLoyalCustomer loyalCustomer, BasketCustomer customer) {
		BasketCreateRequest createRequest = new BasketCreateRequest();
		
		if (basketItemRequest != null) {
			createRequest.setInitialItems(Arrays.asList(basketItemRequest));
		}
		
		if (coupon != null) {
			createRequest.setCandidateCoupons(Arrays.asList(coupon));
		}
		
		if (loyalCustomer != null) {
			createRequest.setLoyalCustomer(loyalCustomer);
		}
		
		if(customer != null) {
			createRequest.setCustomer(customer);
		}
		
		basketManager.createBasketTransaction(createRequest, catalog, true);
		
		BasketPromotable<?> result = basketManager.getBasketTransaction();
		
		log.info("New basket transaction created with uid: " + result.getBasketUid());
		
		return result; 
	}
	
	protected void createAndInsertBasketItemWithSerialsNumbers(NewBasketItemRequest basketItemRequest) throws BasketLineException {
		if (!basketItemRequest.getItemData().getSerialNumbersActive()) return;
//		TODO: Recuperar gestión nº serie
// sceneData.put(SerialNumberControllerAbstract.PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN, new ArrayList<String>());
// sceneData.put(SerialNumberControllerAbstract.PARAMETRO_LINEA_DOCUMENTO_ACTIVO, basketItemRequest);
		sceneData.put(SerialNumberControllerAbstract.PARAM_LINE_DESCRIPTION, basketItemRequest.getItemData().getItemDes());
		sceneData.put(SerialNumberControllerAbstract.PARAM_REQUIRED_QUANTITY, basketItemRequest.getQuantity().abs().longValue());

		if (askZeroPrice(basketItemRequest)) {
			openScene(RetailSerialNumberController.class, new SceneCallback<Set<String>>() {
				@Override
				public void onSuccess(Set<String> callbackData) {
					try {
						basketItemRequest.setSerialNumbers(new LinkedHashSet<>(callbackData));

						createAndInsertBasketItem(basketItemRequest);
					} catch (BasketLineException e) {
						basketItemRequestFailed(e);
					}
				}

				@Override
				public void onCancel() {
					refreshScreenData();
				}
			});
		}
	}
	
	protected void createAndInsertBalanceCard(NewBasketItemRequest basketItemRequest) {
		sceneData.put(BalanceCardItemizationController.PARAM_ITEM_REQUEST, basketItemRequest);
		openActionScene(4025l, BalanceCardItemizationController.class);
	}


	protected void newLineRefreshScreen(BasketItem value) {
		displayDevice.write(value.getItemData().getItemDes(),
				formatUtils.formatNumber(value.getQuantity()) + " X "
						+ formatUtils.formatAmount(value.getPriceWithTaxes()));

		// Restauramos la cantidad en la pantalla
		tfItemQuantity.setText(formatUtils.formatNumber(BigDecimal.ONE, 3));
		
		refreshScreenData();
	}

	protected void basketItemRequestFailed(BusinessException businessException) {
		if (businessException instanceof BasketLineException) {
			log.debug("basketItemRequestFailed: " + businessException);
		} else {
			log.error("basketItemRequestFailed: " + businessException, businessException);
		}		
		Devices.getInstance().getScanner().beepError();
		Devices.getInstance().getScanner().disableReader();
		DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(businessException.getMessage());
		Devices.getInstance().getScanner().enableReader();
	}

	public void refreshScreenData() {
		log.debug("refrescarDatosPantalla() - Refrescando datos de pantalla...");
		
		if (parkedTickets == null) {
			parkedTickets = saleBasketDocumentService.count(applicationSession.getCodAlmacen(), applicationSession.getTillCode(), basketManager.getDefaultDocumentType().getDocTypeId());
		}
		
		if (cashJournalSession.isOpenedCashJournal() && (cashLimitWarning == null || cashLimitLock == null)) {
			cashLimitWarning = cashJournalSession.validateWarningWithdrawalAmount();
			cashLimitLock = cashJournalSession.validateBlockWithdrawalAmount();
		}
			
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();

		if (basketManager.isBasketEmpty()) {
			displayDevice.writeLineUp(I18N.getText("---NUEVO CLIENTE---"));
			displayDevice.standbyMode();				
		}
		else {
			if (basketTransaction.getLines().size() > 0) {
				writeDisplayDeviceLine(basketTransaction.getLines().get(basketTransaction.getLines().size() - 1));
			}
			
			displayDevice.saleMode(basketTransaction);
		}
				
		loadBasketWebView(basketTransaction);
		
		// update payment button status
		btPayment.setDisable(basketManager.isBasketEmpty());
	}
	

	
	public void changeCashier() {
		gotoLogin();
		
		if(basketManager.getBasketTransaction() != null) {
			basketManager.updateCashier(modelMapper.map(tenantResolver.getUser(), BasketCashier.class));
		}

		initializeFocus();
	}

	public void openSearchCustomer() {
		if (!basketManager.isBasketEmpty()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se puede cambiar el cliente de un ticket con líneas."));
			return;
		}
		// Accion ir a la pantalla de consulta de clientes
		openScene(CustomerSearchController.class, new SceneCallback<Customer>() {
			
			@Override
			public void onSuccess(Customer callbackData) {
				updateSaleCustomer(callbackData);

				initializeFocus();
			}
			
			@Override
			public void onCancel() {
				initializeFocus();
			}
		});
	}

	protected void updateSaleCustomer(Customer customer) {
		try {
			checkCustomerRate(customer.getRateCode());

			if (basketManager.isBasketEmpty()) {
				new BackgroundCreateBasketForCustomerTask(this, customer).start();
			} else {
				basketManager.updateCustomer(modelMapper.map(customer, BasketCustomer.class));
			}
		} catch (BasketUpdateException e) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(e.getMessage());
		}
		
		refreshScreenData();
	}

	protected void checkCustomerRate(String rateCode) {
		if (StringUtils.isNotBlank(rateCode)) {
			try {
				catalog.getCatalogRateService().getRate(rateCode);
			}
			catch (NoSuchElementException e) {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El cliente seleccionado dispone de tarifa propia que no existe en la tienda, se aplicarán las tarifas de la tienda."));
			}
		}
	}
	
	protected void editItem(Integer lineId) {
		try {
			checkOperationPermissions(PERMISSION_EDIT_LINE);
		} catch (PermissionDeniedException e) {
			log.debug("editItem() - El usuario no tiene permisos para editar una linea");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No tiene permisos para editar la linea"));
			return;
		}
		
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		BasketItem basketLine = basketTransaction.getLine(lineId);
		
		if (basketLine == null) return;
		
		if (!basketLine.getEditable()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La línea seleccionada no se puede modificar."));
			
			return;
		}
		
		BasketItem item = basketTransaction.getLine(lineId);
		
		// create new item request from line data
		NewBasketItemRequest basketItemRequest = modelMapper.map(item, NewBasketItemRequest.class);
		
		sceneData.put(BasketItemModificationControllerAbstract.PARAM_BASKET_ITEM, basketItemRequest);
		
		openBasketItemModificationScene((NewBasketItemRequest callbackData) -> {
			// convert to update request
			UpdateBasketItemRequest updateRequest = modelMapper.map(callbackData, UpdateBasketItemRequest.class);
			updateRequest.setLineId(item.getLineId());
			updateRequest.setItemDes(callbackData.getItemData().getItemDes());

			basketManager.updateBasketItem(updateRequest);

			auditOperation(new BasketAuditEventBuilder(basketTransaction).addOperation("modifyLine")
					.addField("basketLine", item).addField("updateRequest", updateRequest));

			refreshScreenData();
		});

		
	}

	protected void addQuantityItem(Integer lineId, BigDecimal umAddedQuantity) {
		try {
			checkOperationPermissions(PERMISSION_EDIT_LINE);
		} catch (PermissionDeniedException e) {
			log.debug("addQuantityItem() - El usuario no tiene permisos para editar una linea");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No tiene permisos para editar la linea"));
			return;
		}
		
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		BasketItem basketLine = basketTransaction.getLine(lineId);
		
		if (basketLine == null) return;
		
		if (!basketLine.getEditable()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La línea seleccionada no se puede modificar."));
			return;			
		}
		BigDecimal conversionFactor = basketLine.getUnitMeasureConversionFactor()!=null?basketLine.getUnitMeasureConversionFactor() : BigDecimal.ONE;
		BigDecimal addedQuantity = conversionFactor.multiply(umAddedQuantity);
		
		UpdateBasketItemRequest basketItemRequest = modelMapper.map(basketLine, UpdateBasketItemRequest.class);
		basketItemRequest.setLineId(lineId);
		
		if (conversionFactor.compareTo(BigDecimal.ONE) == 0 || StringUtils.equals(basketLine.getUnitMeasureCode(), "UN")) {
			// sin conversion de unidades
			basketItemRequest.setUnitMeasureQuantity(basketLine.getUnitMeasureQuantity().add(addedQuantity));
		} else {
			// hay una conversion de unidades
			basketItemRequest.setUnitMeasureQuantity(basketLine.getUnitMeasureQuantity().add(umAddedQuantity));
		}		
		
		basketItemRequest.setQuantity(basketItemRequest.getQuantity().add(addedQuantity));
		basketItemRequest.setCustomData(basketLine.getCustomData());
		
		
		basketManager.updateBasketItem(basketItemRequest);
		
		refreshScreenData();
	}
	
	protected void negateItem(Integer lineId) {
		try {
			checkOperationPermissions(PERMISSION_NEGATE_LINE);
		} catch (PermissionDeniedException e) {
			log.debug("accionNegarRegistroTabla() - El usuario no tiene permisos para realizar devolución");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No tiene permisos para introducir un artículo negativo"));
			return;
		}
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		BasketItem basketItem = basketTransaction.getLine(lineId);
		
		if (!basketItem.getEditable()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La línea seleccionada no se puede modificar."));
			return;			
		}
		UpdateBasketItemRequest basketItemRequest = modelMapper.map(basketItem, UpdateBasketItemRequest.class);
		basketItemRequest.setLineId(lineId);
		basketItemRequest.setQuantity(basketItem.getQuantity().negate());
		basketItemRequest.setUnitMeasureQuantity(basketItem.getUnitMeasureQuantity().negate());
		basketItemRequest.setCustomData(basketItem.getCustomData());
				
		basketManager.updateBasketItem(basketItemRequest);
		
		refreshScreenData();
	}
	
	protected void cancelItem(Integer lineId) {
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		BasketItem basketItem = basketTransaction.getLine(lineId);
		
		if(basketItem.getCustomData() != null && (basketItem.getCustomData().containsKey(PARAM_CANCELED_LINE) || basketItem.getCustomData().containsKey(PARAM_CANCEL_LINE))) {
			return;
		}
		
		if (!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Está seguro de querer eliminar esta línea del ticket?"))) {
			return;
		}
		
		try {
			UpdateBasketItemRequest markAsCanceled = modelMapper.map(basketItem, UpdateBasketItemRequest.class);
			Map<String, Object> customData = markAsCanceled.getCustomData();
			if(customData == null) {
				customData = new HashMap<>();
			}
			customData.put(PARAM_CANCELED_LINE, "CANCELED");
			markAsCanceled.setCustomData(customData);
			basketManager.updateBasketItem(markAsCanceled);
			
			NewBasketItemRequest canceledItem = modelMapper.map(basketItem, NewBasketItemRequest.class);

			canceledItem.setQuantity(canceledItem.getQuantity().negate());
			canceledItem.setUnitMeasureQuantity(canceledItem.getUnitMeasureQuantity().negate());
			
			if(basketItem.getItemData().getWeightRequired()) {
				canceledItem.setWeight(canceledItem.getQuantity());
			}
			customData = canceledItem.getCustomData();
			if(customData == null) {
				customData = new HashMap<>();
			}
			customData.put(PARAM_CANCEL_LINE, "CANCEL_LINE");
			canceledItem.setCustomData(customData);
			createAndInsertBasketItem(canceledItem);
			
			refreshScreenData();	
		}	catch (BusinessException e) {
			log.error(e.getMessage(), e);
			basketItemRequestFailed(e);
		}
	}
	
	protected void deleteItem(Integer lineId) {
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		BasketItem basketItem = basketTransaction.getLine(lineId);
		
		if (!basketItem.getEditable()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La línea seleccionada no se puede modificar."));
			return;			
		}
		
		if (!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Está seguro de querer eliminar esta línea del ticket?"))) {
			return;
		}
				
		basketManager.deleteBasketItem(lineId);
		
		refreshScreenData();	
	}
	
	protected void removeLoyaltySettings() {
		basketManager.updateLoyaltySettings(null);
		refreshScreenData();
	}
	protected void deleteCandidateCoupon(String couponCode) {
		if (!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Está seguro de querer eliminar el cupón {0}?", couponCode))) {
			return;
		}
		
		try {
			basketManager.deleteCandidateCoupon(couponCode);
		} catch (Exception e) {
			log.error("deleteCandidateCoupon() - An error was thrown deleting a coupon: ", e);
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha ocurrido un error eliminando el cupón."));
		}

		refreshScreenData();
	}

	protected void openBasketItemModificationScene(SceneCallback<?> childSceneCallback) {
		sceneData.put(BASKET_KEY, basketManager);
		sceneData.put(CATALOG_KEY, new WeakReference<>(catalog));
		
		openScene(getBasketItemModificationController(), childSceneCallback);
	}

	
	public void openSearchItem() {		
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		auditOperation(new BasketAuditEventBuilder(basketTransaction).addOperation("itemsSearch"));
		
		log.debug("abrirBusquedaArticulos()");

		// Validamos que hay introducida una cantidad válida de artículos . Nota : También valida el campo código
		// introducido. Podemos crear otro metodo de validación para que no lo haga
		frSearchValidation.setCantidad(tfItemQuantity.getText());
		
		if (!validateSearchForm()) {
			return; // Si la validación de la cantidad no es satisfactoria, no realizamos la búsqueda
		}

		if (basketTransaction != null) {
			sceneData.put(ItemSearchController.PARAM_INPUT_CUSTOMER, basketTransaction.getHeader().getCustomer());
		} else {
			sceneData.put(ItemSearchController.PARAM_INPUT_CUSTOMER, modelMapper.map(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer(), BasketCustomer.class));
		}
		sceneData.put(ItemSearchController.PARAM_INPUT_CATALOG, new WeakReference<>(catalog));
		sceneData.put(ItemSearchController.PARAM_INPUT_RATE_CODE, session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getRateCode());
		sceneData.put(ItemSearchController.PARAM_MODAL, Boolean.TRUE);
		sceneData.put(UserSelectionController.PARAM_IS_STOCK, Boolean.FALSE);
		
		openSearchItemScene(new SceneCallback<Map<String, Object>>() {			
			@Override
			public void onSuccess(Map<String, Object> callbackData) {
				initializeFocus();

				String codArt = (String) callbackData.get(ItemSearchController.PARAM_OUTPUT_ITEM_CODE);
				String codigoBarras = (String) callbackData.get(ItemSearchController.PARAM_OUTPUT_BARCODE);
				
				if (StringUtils.isBlank(codigoBarras)) {
					codigoBarras = codArt;
				}
				
				auditOperation(new BasketAuditEventBuilder(basketTransaction).addOperation("manualItemFromItemsSearch").addField("barcode", codigoBarras));
				
				newBasketItemRequest(codigoBarras, BigDecimal.ONE, null);
			}

		});
				
	}

	protected void openSearchItemScene(SceneCallback<?> childSceneCallback) {
		openScene(getItemSearchController(), childSceneCallback);
	}

	protected void writeDisplayDeviceLine(BasketItem linea) {
		String desc = linea.getItemData().getItemDes();
		displayDevice.write(desc, formatUtils.formatNumber(linea.getQuantity()) + " X " + formatUtils.formatAmount(linea.getPriceWithTaxes()));
	}

	protected boolean validateSearchForm() {
		// Limpiamos los errores que pudiese tener el formulario
		frSearchValidation.clearErrorStyle();

		// Validamos el formulario de validación de búsqueda
		Set<ConstraintViolation<ItemLineValidationForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frSearchValidation);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<ItemLineValidationForm> next = constraintViolations.iterator().next();
			frSearchValidation.setErrorStyle(next.getPropertyPath(), true);
			frSearchValidation.setFocus(next.getPropertyPath());
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(next.getMessage());
			return false;
		}
		return true;
	}

	protected boolean validateForm() {
		// Limpiamos los errores que pudiese tener el formulario
		frValidation.clearErrorStyle();

		// Validamos el formulario
		Set<ConstraintViolation<ItemLineValidationForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frValidation);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<ItemLineValidationForm> next = constraintViolations.iterator().next();
			frValidation.setErrorStyle(next.getPropertyPath(), true);
			frValidation.setFocus(next.getPropertyPath());
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(next.getMessage());
			return false;
		}

		BigDecimal cantidad = frValidation.getCantidadAsBigDecimal();
		if (cantidad == null) {
			return false;
		}

		BigDecimal max = new BigDecimal(10000000);
		if (BigDecimalUtil.isGreaterOrEquals(frValidation.getCantidadAsBigDecimal(), max)) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("La cantidad debe ser menor que {0}", formatUtils.formatNumber(max)));
			return false;
		}

		return true;
	}

	/**
	 * Basket parking method
	 */
	public boolean parkBasket() {				
		if (basketManager.isBasketEmpty()) { 
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La cesta está vacía."));
			return false;
		}
		
		basketManager.parkBasket();
		
		updateCatalog();
		
		return true;
	}
	
	

	public void retrieveBasket() {
		if (!basketManager.isBasketEmpty()) { 
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La cesta no está vacía."));
			return;
		}
				
		log.trace("retrieveBasket()");
		sceneData.put(RetrieveBasketControllerAbstract.PARAM_DOCUMENT_TYPE, basketManager.getDefaultDocumentType().getDocTypeId());
		if(parkedTickets!=null && parkedTickets == 1l) {
			SaleBasket basket = saleBasketDocumentService.findPage(session.getApplicationSession().getCodAlmacen(), 
					session.getApplicationSession().getTillCode(), basketManager.getDefaultDocumentType().getDocTypeId(), 
					new PaginatedRequest(0, 1)).getContent().get(0);
			loadParkedSaleBasket(basket);
		}else {
			openScene(RetrieveBasketController.class, (SaleBasket callbackData) -> loadParkedSaleBasket(callbackData));
		}

	}
	
	public void retrieveRemoteBasket() {
		if (!basketManager.isBasketEmpty()) { 
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La cesta no está vacía."));
			return;
		}
				
		log.trace("retrieveRemoteBasket()");
		sceneData.put(RetrieveBasketControllerAbstract.PARAM_DOCUMENT_TYPE, basketManager.getDefaultDocumentType().getDocTypeId());
		openScene(RemoteRetrieveBasketController.class, (SaleBasketDetail callbackData) -> loadRemoteParkedSaleBasket(callbackData));
	}

	protected void loadParkedSaleBasket(SaleBasket saleBasket) {
		new LongOperationTask<Void>(this, false) {
			
			@Override
			protected Void execute() throws Exception {
				if (StringUtils.isNotBlank(saleBasket.getCustomerCode())
						&& !StringUtils.equals(applicationSession.getStorePosBusinessData().getDefaultCustomer().getCustomerCode(), saleBasket.getCustomerCode())) {
					updateCatalogForCustomer(saleBasket.getCustomerCode());
				}

				basketManager.loadBasket(saleBasket.getBasketUid(), catalog);
				return null;
			}
			
			@Override
			protected void taskSucceded() {
				onBasketLoaded();
			}

			@Override
			protected void taskFailed() {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Error recuperando cesta."));
				updateCatalog();
			}
		}.start();
	}
	protected void loadRemoteParkedSaleBasket(SaleBasketDetail saleBasket) {
		new LongOperationTask<Void>(this, false) {
			
			@Override
			protected Void execute() throws Exception {
				if (StringUtils.isNotBlank(saleBasket.getCustomerCode())
						&& !StringUtils.equals(applicationSession.getStorePosBusinessData().getDefaultCustomer().getCustomerCode(), saleBasket.getCustomerCode())) {
					updateCatalogForCustomer(saleBasket.getCustomerCode());
				}

				basketManager.loadBasket(saleBasket.getBasketData(), catalog, true);
				return null;
			}
			
			@Override
			protected void taskSucceded() {
				deleteRemoteParkedSaleBasket(saleBasket.getBasketUid());
			}
			
			@Override
			protected void taskFailed() {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Error recuperando cesta."));
				updateCatalog();
			}
		}.start();
	}
	
	protected void deleteRemoteParkedSaleBasket(String basketUid) {
		new LongOperationTask<Void>(this, true) {

			@Override
			protected Void execute() throws Exception {
				basketApi.deleteBasketByUid(basketUid, new BasketDeleteRequest().reason("Remote basket retrieval"));
				return null;
			}

			@Override
			protected void taskSucceded() {
				onBasketLoaded();
			}

			@Override
			protected void taskFailed() {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se ha podido eliminar la cesta remota. Inténtelo de nuevo mas tarde."));
				onBasketLoaded();
			}
		}.start();
	}
	
	public void onBasketLoaded() {
		// get new basket transaction after load
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		
		if(basketTransaction.getHeader().getLoyalCustomer()!=null) {
			searchLoyaltyCard(basketTransaction.getHeader().getLoyalCustomer().getCardNumber());
		}

		BasketCustomer customer = basketTransaction.getHeader().getCustomer();
		if (customer != null) {
			checkCustomerRate(customer.getRateCode());
		}
		
		if (BooleanUtils.isTrue(basketTransaction.getTenderMode())) {
			openPayments();
		} else {
			refreshScreenData();
		}
	}

	public void openPayments() {
		log.trace("openPayments()");
		if (basketManager.isBasketEmpty()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La cesta está vacía."));
			return;
		}
		if (!validateSerialNumbers()) {
			return;
		}
		
		if(checkWithdrawalBlock()) return;
		
		log.debug("openPayments() - El ticket tiene líneas");
		
		Devices.getInstance().getLoyaltyCard().ignoreBackgroundTaskResult();
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		if(CollectionUtils.isNotEmpty(getAvailableCoupons(basketTransaction))) {
			openCustomerCoupons(coupons -> {
				updateCustomerCoupons(basketManager.getBasketTransaction(), coupons);
				openPaymentsScreen();
			});
		} else {
			
			openPaymentsScreen();
		}
	}

	protected void openPaymentsScreen() {
		sceneData.put(BASKET_KEY, basketManager);
		openScene(getPaymentsController(), new SceneCallback<Void>() {
			
			@Override
			public void onSuccess(Void callbackData) {
				onPaymentsSuccess();
			}
			
			@Override
			public void onCancel() {
				initializeFocus();
				refreshScreenData();
			}

		});
	}
	
	protected void openAskWeightScreen(NewBasketItemRequest basketItem, SceneCallback<?> sceneCallback) {
		sceneData.put(BASKET_KEY, basketManager);
		sceneData.put(CATALOG_KEY, new WeakReference<>(catalog));
		sceneData.put(AskWeightControllerAbstract.PARAM_DETAIL, basketItem);
		
		openScene(getAskWeightController(), sceneCallback);
	}
	
	protected abstract Class<? extends SceneController> getAskWeightController();
	protected abstract Class<? extends SceneController> getPaymentsController();
	protected abstract Class<? extends SceneController> getBasketItemModificationController();
	protected abstract Class<? extends SceneController> getPickListController();
	
	protected Class<? extends SceneController> getItemSearchController() {
		return ItemSearchController.class;
	}
	
	protected void onPaymentsSuccess() {
		updateCatalog();	
	}
	
	public boolean validatePointRedemption() {
		log.trace("redeemPoints()");
		if (basketManager.isBasketEmpty()) {
			log.warn("redeemPoints() - Ticket vacio");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La cesta está vacía."));
			return false;
		}
		BasketLoyalCustomer datosFidelizado = basketManager.getBasketTransaction().getHeader().getLoyalCustomer();
		String numTarjetaFidelizado = (datosFidelizado == null) ? null : datosFidelizado.getCardNumber();
		if(numTarjetaFidelizado == null) {
			log.warn("redeemPoints() - Debe insertar tarjeta fidelizado");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Debe insertar tarjeta fidelizado"));
			return false;
		}
		if(datosFidelizado.getCardBalance().compareTo(BigDecimal.ZERO)<=0) {
			log.warn("redeemPoints() - No dispone de puntos");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Fidelizado no dispone de puntos"));
			return false;
		}
		log.trace("redeemPoints() - El ticket tiene líneas y posee tarjeta fidelizado");
		return true;
	}

	protected void redeemPoints() {
		if(!validatePointRedemption()) {
			return;
		}
		openPointsRedemptionScene();

		updateRemoteCoupons();

		refreshScreenData();
	}


	protected void openPointsRedemptionScene() {
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		BasketLoyalCustomer datosFidelizado = basketTransaction.getHeader().getLoyalCustomer();
		
		log.debug("realizaPagoPuntos() - Procede a canjear los puntos");
		BigDecimal puntosPorEuro = BigDecimal.valueOf(Double.valueOf(
				variableService.getVariableAsString(VariableServiceFacade.FIDELIZACION_PUNTOS_FACTOR_CONVERSION)));
		
		BigDecimal saldoFidelizado = datosFidelizado.getCardBalance();
		BigDecimal precioVenta = basketTransaction.getTotals().getTotalWithTaxes();
		BigDecimal dineroDispEnPuntos = saldoFidelizado.multiply(puntosPorEuro).setScale(2, RoundingMode.DOWN);
		
		BigDecimal puntosMaximosCompra;
		if(precioVenta.compareTo(dineroDispEnPuntos)>0) {
			puntosMaximosCompra = saldoFidelizado;
		} else {
			puntosMaximosCompra = precioVenta.divide(puntosPorEuro, 0, RoundingMode.DOWN);
		}
		
		sceneData.put(PointsRedemptionController.AVAILABLES_POINTS, datosFidelizado.getCardBalance());
		sceneData.put(PointsRedemptionController.MAX_POINTS, puntosMaximosCompra);
		sceneData.put(PointsRedemptionController.PARAM_LOYAL_CUSTOMER, datosFidelizado);
		openScene(PointsRedemptionController.class, (Void callback) -> {
			updateRemoteCoupons();

			refreshScreenData();		
		});
	}
	
	public void updateRemoteCoupons() {
		log.debug("updateRemoteCoupons() - Actualizando cupones y fidelizado de manera remota");
		BasketLoyalCustomer fidelizado = basketManager.getBasketTransaction().getHeader().getLoyalCustomer();
		
		// se actualiza el fidelizado con el número de tarjeta actual
		tfItemCode.setText(fidelizado.getCardNumber());
		addNewItemCode();
	}

	public void openDocumentSearch(Map<String, String> parametros) {
		if (parametros.containsKey("idAccion")) {
			String idAccion = parametros.get("idAccion");
			sceneData.put(DocumentManagementController.PARAM_DOC_TYPE, basketManager.getDefaultDocumentType().getDocTypeCode());
			openActionScene(Long.parseLong(idAccion));
		}
		else {
			log.error("No llegó la acción asociada al botón.");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se pudo ejecutar la acción seleccionada."));
		}
	}

	public void openCheckGiftCardBalance() {
		auditOperation(new BasketAuditEventBuilder(basketManager.getBasketTransaction()).addOperation("checkGiftCardBalance"));
		
		log.info("consultarSaldoGiftCard()");
		openScene(BalanceCardSearchController.class);
	}


	public void retrieveCreditSale() {
		HashMap<String, Object> sceneData = new HashMap<>();
		sceneData.put(BASKET_KEY, basketManager);

		openScene(CreditSaleRetrievalController.class, (Void callback) -> refreshScreenData());

	}


	protected boolean validateLine(BasketItem linea) {
		return true;
	}
	
	protected void checkItemActive(NewBasketItemRequest basketItemRequest) throws NewBasketItemNoAllowedException {
		if(BooleanUtils.isTrue(basketItemRequest.getItemData().getActive())) return;
				
		throw new NewBasketItemNoAllowedException(I18N.getText("El artículo consultado está inactivo"), basketItemRequest);
	}

	protected void checkItemRequiresCombinations(NewBasketItemRequest basketItemRequest) throws NewBasketItemNoAllowedException {
		String itemCombination1Code = basketItemRequest.getItemData().getCombination1Code();
		String itemCombination2Code = basketItemRequest.getItemData().getCombination2Code();
		if (BooleanUtils.isTrue(basketItemRequest.getItemData().getCombination1Active() && (StringUtils.isBlank(itemCombination1Code) || itemCombination1Code.equals("*"))) ||
			BooleanUtils.isTrue(basketItemRequest.getItemData().getCombination2Active() && (StringUtils.isBlank(itemCombination2Code) || itemCombination2Code.equals("*")))) {
			throw new NewBasketItemNoAllowedException(I18N.getText("Para este artículo se deben especificar sus desgloses."), basketItemRequest);
		}
	}

	protected void checkZeroPricedBasketItem(NewBasketItemRequest basketItemRequest) throws NewBasketItemNoAllowedException {
		BigDecimal price = basketItemRequest.getDirectPrice() != null ? basketItemRequest.getDirectPrice() : basketItemRequest.getRateItem().getSalesPriceWithTaxes();
				
		if (!BigDecimalUtil.isEqualsToZero(price)) return;
			
		if (!zeroPriceAllowed) {
			throw new NewBasketItemNoAllowedException(getItemWithouRateMessage(basketItemRequest) + I18N.getText("No está permitida la venta a precio 0."), basketItemRequest);
		}
		
	}
	
	protected boolean askZeroPrice(NewBasketItemRequest basketItemRequest) {
		BigDecimal price = basketItemRequest.getDirectPrice() != null ? basketItemRequest.getDirectPrice() : basketItemRequest.getRateItem().getSalesPriceWithTaxes();
		
		if (!BigDecimalUtil.isEqualsToZero(price) || !zeroPriceAllowed) {
			return true;
		}
		
		return DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(getItemWithouRateMessage(basketItemRequest) + I18N.getText("¿Desea vender el artículo a precio 0?"));
	}

	protected String getItemWithouRateMessage(NewBasketItemRequest basketItemRequest) {
		return StringUtils.isBlank(basketItemRequest.getRateItem().getRateCode()) ?
				I18N.getText("El artículo \"{0} - {1}\" no está tarificado.", basketItemRequest.getItemCode(), basketItemRequest.getItemData().getItemDes()) + "\n" : "";
	}
	
	protected void checkGenericBasketItemPermission(NewBasketItemRequest basketItemRequest) throws NewBasketItemNoAllowedException {
		if (!basketItemRequest.getItemData().getGenericItem()) return;
		
		// Si el artículo es genérico y no tiene permiso, no se puede insertar
		try {
			checkOperationPermissions(PERMISSION_GENERIC_ITEMS);
		}
		catch (PermissionDeniedException e) {
			throw new NewBasketItemNoAllowedException(I18N.getText("No tiene permisos para usar articulos genéricos"), basketItemRequest);
		}
	}
	
	protected void checkItemRequestSerialNumbers(NewBasketItemRequest basketItemRequest) throws NewBasketItemNoAllowedException {
		if (Boolean.TRUE.equals(basketManager.getDocumentType().getOrder())) return;
		
		if (!basketItemRequest.getItemData().getSerialNumbersActive()) return;
		
		if (CollectionUtils.isEmpty(basketItemRequest.getSerialNumbers()) ||
			basketItemRequest.getSerialNumbers().size() != basketItemRequest.getQuantity().abs().intValue()) {
			throw new NewBasketItemNoAllowedException(I18N.getText("No se han introducido los números de serie del artículo"), basketItemRequest);
		}
	}	
	
	public static class NewBasketItemNoAllowedException extends BasketLineException {

		private static final long serialVersionUID = 1L;
		protected NewBasketItemRequest linea;

		public NewBasketItemNoAllowedException(NewBasketItemRequest linea) {
			super();
			this.linea = linea;
		}

		public NewBasketItemNoAllowedException(String msg, NewBasketItemRequest linea) {
			super(msg);
			this.linea = linea;
		}

		public NewBasketItemRequest getLinea() {
			return linea;
		}
	}
	

	protected void assignSerialNumber(BasketItem line) {
		if (line != null && line.getItemData().getSerialNumbersActive()) {
			HashMap<String, Object> stageData = new HashMap<>();
			stageData.put(SerialNumberControllerAbstract.PARAM_LINE_DESCRIPTION, line.getItemDes());
			stageData.put(SerialNumberControllerAbstract.PARAM_REQUIRED_QUANTITY, line.getQuantity().abs().longValue());
			openModalScene(RetailSerialNumberController.class, new SceneCallback<Set<String>>() {

				@Override
				public void onSuccess(Set<String> serialnumbers) {
					UpdateBasketItemRequest basketItemRequest = modelMapper.map(line, UpdateBasketItemRequest.class);
					basketItemRequest.setLineId(line.getLineId());
					basketItemRequest.setSerialNumbers(serialnumbers);
					
					basketManager.updateBasketItem(basketItemRequest);
				}
			}, stageData);
		}
	}
	
	/**
	 * Button group method
	 * @param params
	 */
	public void selectService(HashMap<String, String> params) {
		selectService(params.get("serviceTypeCode"));
	}
	
	protected void selectService(String newServiceTypeCode) {
		StorePosServicesData posServiceData = applicationSession.getStorePosBusinessData().getServicesData();
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		
		String currentServiceTypeCode = basketTransaction.getHeader().getServiceData() != null?basketTransaction.getHeader().getServiceData().getServiceTypeCode():null;
		
		if (StringUtils.isBlank(currentServiceTypeCode)) {
			currentServiceTypeCode = defaultServiceType.getServiceTypeCode();
		}
		
		StoreServiceType newServiceType = null;
		
		// volver a asignar el servicio por defecto
		if (StringUtils.equals(currentServiceTypeCode, newServiceTypeCode)) {
			newServiceType = defaultServiceType;
		} else {
			newServiceType = posServiceData.getStoreServicesType().get(newServiceTypeCode);
		}
				
		if(newServiceType == null) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La tienda actual no permite el tipo de servicio seleccionado."));
			return;
		}
		
		BasketServiceData serviceData = new BasketServiceData();
		serviceData.setServiceTypeCode(newServiceType.getServiceTypeCode());
		serviceData.setServiceTypeDes(newServiceType.getServiceTypeDes());
		serviceData.setExtendedData(new HashMap<>(newServiceType.getCustomData()));
		try {
			basketManager.updateServiceData(serviceData);
		} catch (BasketLineException e) {
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("No se ha podido actualizar el servicio seleccionado."), e);
		}
		refreshScreenData();
	}
	

	protected boolean validateSerialNumbers() {
		if (Boolean.TRUE.equals(basketManager.getDocumentType().getOrder())) return false;
		
		long valid = basketManager.getBasketTransaction().getLines().stream().filter(line -> line.getItemData().getSerialNumbersActive() != null && 
				                                                                                line.getItemData().getSerialNumbersActive() &&
				                                                                                (CollectionUtils.isEmpty(line.getSerialNumbers()) ||
				                                                                                 line.getSerialNumbers().size() < line.getQuantity().abs().intValue()))
			    .count();
			    
	    if (valid > 0l) {
	    	DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Quedan números de serie por asignar. Por favor, asígnelos antes de seguir."));
	    }
	    
	    return true;
	}

	public void showLoyaltyInfo() {
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		if (basketTransaction.getHeader().getLoyalCustomer() == null) return;
		
			
		final String cardNumber = basketTransaction.getHeader().getLoyalCustomer().getCardNumber();
		Long lyCustomerId = basketTransaction.getHeader().getLoyalCustomer().getLyCustomerId();
		
		auditOperation(new BasketAuditEventBuilder(basketTransaction).addOperation("viewLoyalCustomer")
				                                       .addField("cardNumber", cardNumber)
				                                       .addField("lyCustomerId", lyCustomerId));
		
		if (lyCustomerId != null) {
			sceneData.put(LoyalCustomerController.PARAM_LY_CUSTOMER_ID, lyCustomerId);
			sceneData.put(LoyalCustomerController.PARAM_MODE, "CONSULTA");
			sceneData.put(BASKET_KEY,basketManager);
			sceneData.put(PARAM_CARD_NUMBER, cardNumber);
			openActionScene(AppConfig.getCurrentConfiguration().getLoyaltyAction());
			return;
		}

		QueryLoyalCustomerByCardNumberTask getLoyalCustomerIdTask = SpringContext.getBean(QueryLoyalCustomerByCardNumberTask.class, cardNumber, new RestBackgroundTask.FailedCallback<Long>(){

			@Override
			public void succeeded(Long result) {
				sceneData.put(LoyalCustomerController.PARAM_LY_CUSTOMER_ID, result);
				sceneData.put(LoyalCustomerController.PARAM_MODE, "CONSULTA");
				sceneData.put(PARAM_CARD_NUMBER, cardNumber);
				openActionScene(AppConfig.getCurrentConfiguration().getLoyaltyAction());
			}

			@Override
			public void failed(Throwable throwable) {
			}
		}, getStage());

		getLoyalCustomerIdTask.start();
	}

    public void openPickList() {
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		auditOperation(new BasketAuditEventBuilder(basketTransaction).addOperation("pickList"));
		
		
		sceneData.put(PickListControllerAbstract.PARAM_BASKET_MANAGER, basketManager);
		sceneData.put(PickListControllerAbstract.PARAM_ACTION, getAction());
		sceneData.put(PickListControllerAbstract.PARAM_CATALOG, new WeakReference<>(catalog));
		openScene(getPickListController(), new SceneCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> itemCodes) {
			
				
				refreshScreenData();
				
			}
		});
	}
	
	protected List<BasketLoyalCustomerCoupon> getAvailableCoupons(BasketPromotable<?> basketTransaction) {
		if (basketManager.getBasketTransaction().getHeader().getLoyalCustomer() == null) return null;
		
		return basketTransaction.getHeader().getLoyalCustomer().getAvailableCoupons();		
	}
	
	protected List<BasketLoyalCustomerCoupon> getActiveCoupons(BasketPromotable<?> basketTransaction) {
		if (basketManager.getBasketTransaction().getHeader().getLoyalCustomer() == null) return null;
		
		return basketTransaction.getHeader().getLoyalCustomer().getActiveCoupons();		
	}
	
	public void openCustomerCoupons(SceneCallback<List<BasketLoyalCustomerCoupon>> callback) {
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		List<BasketLoyalCustomerCoupon> availableCoupons = getAvailableCoupons(basketTransaction);			
		List<BasketLoyalCustomerCoupon> activeCoupons = getActiveCoupons(basketTransaction);
		
		if (CollectionUtils.isEmpty(availableCoupons)) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No hay cupones disponibles."));
			return;
		}
		
		try {
			sceneData.put(CustomerCouponsController.PARAM_CUSTOMER_COUPONS, availableCoupons);			
			sceneData.put(CustomerCouponsController.PARAM_ACTIVE_COUPONS, activeCoupons);
						
			openScene(CustomerCouponsController.class, callback);
			
		}
		catch (Exception e) {
			log.error("seeCustomerCoupons() - ERROR: " + e.getMessage(), e);
			
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha habido un error al mostrar la pantalla de cupones del fidelizado. Por favor, contacte con el administrador."), e);
		}
	}

	protected void useCustomerCoupons() {
		openCustomerCoupons(customerCoupons -> updateCustomerCoupons(basketManager.getBasketTransaction(), customerCoupons));
	}
	
	protected void updateCustomerCoupons(BasketPromotable<?> basketTransaction,
			List<BasketLoyalCustomerCoupon> coupons) {
		BasketLoyalCustomer basketLyCustomer = basketTransaction.getHeader().getLoyalCustomer();
		basketLyCustomer.setActiveCoupons(coupons);
		basketManager.updateLoyaltySettings(basketLyCustomer);
		
		
		basketManager.deleteCandidateCoupons();
		
		if(coupons.isEmpty()) {
			return;
		}
		
		basketManager.addCandidateCoupons(coupons);
		
		int appliedCoupons = basketManager.getBasketTransaction().getCouponsApplied().size();
		String message = "";
		if(appliedCoupons == 0) {
			message = I18N.getText("No se ha podido aplicar ningún cupón.");
		}else if(appliedCoupons == 1) {					
			message = I18N.getText("Se ha podido aplicar un cupón.");
		}
		else if(appliedCoupons > 1){
			message = I18N.getText("Se han podido aplicar {0} cupones.", appliedCoupons);
		}
		
		DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(message);
		if (appliedCoupons > 0) {
			refreshScreenData();
		}
	}
	
	
	protected void loadBasketWebView(BasketPromotable<?> basketTransaction) {

		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("cashLimitWarning", cashLimitWarning);
		params.put("cashLimitLock", cashLimitLock);
		params.put("basketsParked", parkedTickets);
		params.put("basket", basketTransaction);
		params.put("combination1Code", combination1Code);
		params.put("combination2Code", combination2Code);
		params.put("defaultServiceType", defaultServiceType);
		
		loadWebView(getWebViewPath(), params, wvPrincipal);
	}
	
	protected String getWebViewPath() {
		return "sales/basket/basket";
	}

	protected void loadTopButtonsPanel() throws LoadWindowException {
		try {
			ButtonsGroupModel panelBotoneraBean = getSceneView().loadButtonsGroup("_top_buttons_panel.xml");
			topButtonsGroup = new ButtonsGroupComponent(panelBotoneraBean, vbTopButtonsPanel.getPrefWidth(), vbTopButtonsPanel.getPrefHeight(), this, ActionButtonNormalComponent.class);
			vbTopButtonsPanel.getChildren().clear();
			vbTopButtonsPanel.getChildren().add(topButtonsGroup);
		}
		catch (InitializeGuiException e) {
			log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
		}
	}

	protected void loadBottomButtonsPanel() throws LoadWindowException {
		try {
			ButtonsGroupModel panelBotoneraBean = getSceneView().loadButtonsGroup("_bottom_buttons_panel.xml");
			bottomButtonsGroup = new ButtonsGroupComponent(panelBotoneraBean, vbBottomButtonsPanel.getPrefWidth(), vbBottomButtonsPanel.getPrefHeight(), this, ActionButtonNormalComponent.class);
			vbBottomButtonsPanel.getChildren().clear();
			vbBottomButtonsPanel.getChildren().add(bottomButtonsGroup);
		}
		catch (InitializeGuiException e) {
			log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
		}
	}
	
	protected void loadOtherButtonsPanel() throws LoadWindowException {
		try {
			log.debug("loadOtherButtonsPanel() - loading other buttons pannel");
			ButtonsGroupModel panelBotoneraBean = getSceneView().loadButtonsGroup("_other_buttons_panel.xml");
			otherButtonsGroup = new ButtonsGroupComponent(panelBotoneraBean, vbOtherButtonsPane.getPrefWidth(), vbOtherButtonsPane.getPrefHeight(), this, ActionButtonNormalComponent.class);
			vbOtherButtonsPane.getChildren().clear();
			vbOtherButtonsPane.getChildren().add(otherButtonsGroup);
		}
		catch (InitializeGuiException e) {
			log.warn("loadOtherButtonsPanel() - Error al crear botonera: " + e.getMessage());
			log.debug("loadOtherButtonsPanel() - Removing 'Other' button.");
			ButtonsGroupComponent buttonsPanel = (ButtonsGroupComponent) vbBottomButtonsPanel.getChildren().get(0);
			for (Button button : buttonsPanel.getButtonsList()) {
				if (button.getUserData() != null && button.getUserData() instanceof ButtonConfigurationBean) {
					if("switchLateralVBox".equals(((ButtonConfigurationBean) button.getUserData()).getKey())) {
						button.setVisible(false);
						button.setManaged(false);
						break;
					}
				}
			}    	
		}
	}

	@FXML
	public void switchLateralVBox() {

		Boolean visibleVBox = vbOtherButtons.isVisible();

		vbOtherButtons.setVisible(!visibleVBox);
		vbOtherButtons.setManaged(!visibleVBox);
		vbGeneral.setVisible(visibleVBox);
		vbGeneral.setManaged(visibleVBox);
		
	}
	
	@Override
	public void gotoLogin() {
		super.gotoLogin();
		refreshScreenData();
	}
	
	@Deprecated
	public void openTill() {
		openCashDrawer();
	}
	
	public void openCashDrawer() {
		auditOperation(new BasketAuditEventBuilder(basketManager.getBasketTransaction()).addOperation("openCashDrawer"));

		Devices.openCashDrawer();
	}
	
	@Override
	public void checkUIPermission() {
		if(topButtonsGroup != null) {
			topButtonsGroup.checkOperationsPermissions();
		}
		
		if(bottomButtonsGroup != null) {
			bottomButtonsGroup.checkOperationsPermissions();
		}
		
		if(otherButtonsGroup != null) {
			otherButtonsGroup.checkOperationsPermissions();
		}
	}
	
	@Override
	public WebView getWebView() {
		return wvPrincipal;
	}

	@Override
	public void onClose() {
		super.onClose();
		try {
			catalog = null;
			if (scannerObserver != null) {
				Devices.getInstance().getScanner().deleteObserver(scannerObserver);
			}
			
			displayDevice.writeLineUp(I18N.getText("---CAJA CERRADA---"));
			displayDevice.standbyMode();
		} catch (Exception e) {
			log.error("onClose() - Error al cerrar la pantalla de cesta: " + e.getMessage(), e);
		}
	}

}
