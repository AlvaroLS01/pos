package com.comerzzia.pos.gui.sales.basket;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.core.facade.model.DocTypeDetail;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.conversion.BasketDocumentIssued;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.omnichannel.facade.model.basket.payments.BasketPayment;
import com.comerzzia.omnichannel.facade.model.basket.payments.BasketPaymentMethod;
import com.comerzzia.omnichannel.facade.model.basket.payments.BasketPaymentRequest;
import com.comerzzia.omnichannel.facade.model.basket.payments.BasketRefundRequest;
import com.comerzzia.omnichannel.facade.model.basket.payments.BasketTenderRequest;
import com.comerzzia.omnichannel.facade.model.documents.PrintDocumentRequest;
import com.comerzzia.omnichannel.facade.model.documentvalidation.SaleDocumentDataValidationResult;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodDetail;
import com.comerzzia.omnichannel.facade.model.payments.tendermode.TenderException;
import com.comerzzia.omnichannel.facade.model.payments.tendermode.TenderProgress;
import com.comerzzia.omnichannel.facade.model.payments.tendermode.TenderRequest;
import com.comerzzia.omnichannel.facade.model.payments.tendermode.TenderResponse;
import com.comerzzia.omnichannel.facade.model.store.StorePosServicesData;
import com.comerzzia.omnichannel.facade.model.store.StoreServiceType;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.basket.BasketPaymentsManager;
import com.comerzzia.omnichannel.facade.service.payments.PaymentMethodManager;
import com.comerzzia.omnichannel.model.documents.DocumentIssued;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.paymentmethod.ActionButtonPaymentMethodComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupPanelRowBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.paymentmethod.PaymentMethodButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.dialogs.CzzAlertDialog;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.devices.printer.BackgroundPrintTask;
import com.comerzzia.pos.devices.printer.PrintService;
import com.comerzzia.pos.gui.sales.document.view.DocumentViewerController;
import com.comerzzia.pos.gui.sales.retail.invoice.InvoiceController;
import com.comerzzia.pos.gui.sales.retail.payments.changedue.ChangeDueScreenController;
import com.comerzzia.pos.gui.sales.retail.payments.changepaymentmethod.ChangeController;
import com.comerzzia.pos.gui.sales.retail.payments.customerdata.ChangeCustomerDataController;
import com.comerzzia.pos.gui.sales.retail.payments.shippingdata.ShippingDataController;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class PaymentsControllerAbstract<T extends BasketManager<?, ?>> extends SceneController implements Initializable, ButtonsGroupController {
	public static final String ACCION_CANCELAR = "ACCION_CANCELAR";
	public static final String IMP_MAX_SUPERADO = "IMPORTE_MAXIMO_SUPERADO";
	public static final String PLANTILLA_VALE = "vale";
	public static final String PLANTILLA_CUPON = "cupon_promocion";
	public static final String TIPO_DOC_INICIAL = "TIPO_DOC_INICIAL";
	public static final String ACTION_SELECT_PAYMENT_METHOD = "ACTION_SELECT_PAYMENT_METHOD";

	public static final String BTN_SELECTED_CLASS = "button-selected";

	public static final String PAYMENT_GUI_BASKET_MANAGER = "BASKET_MANAGER";
	public static final String PAYMENT_GUI_BASKET_TENDER_REQUEST = "BASKET_TENDER_REQUEST";

	protected T basketManager;

	@Autowired
	protected Session session;
		
	final protected DeviceLineDisplay displayDevice = Devices.getInstance().getLineDisplay();

	protected PaymentMethodDetail paymentMethodSelected;

	@FXML
	protected NumericTextField tfBalance;
	
	@FXML
	protected Button btAccept, btCancel, btServiceData, btFiscalValidation;
	
	@FXML
	protected Label lbDocumentType, lbCustomer, lbSaleType, lbPaymentMethod;
	
	@FXML
	protected WebView wvPrincipal;
	
	@FXML
	protected VBox vbGeneral, vbOtherPaymentMethods, vbPaymentsMethods, vbOtherMethodsButtons;
	
	@FXML
	protected HBox parentHbox, hbSaleType;	
	
	@FXML
	protected NumericKeypad numpad;	
		
	@Autowired
	protected VariableServiceFacade variablesServices;
			
	@Autowired
	protected PrintService printService;
	
	@Autowired
	protected ModelMapper modelMapper;
	
	protected SaleDocumentDataValidationResult saleDocumentDataValidation;
	
	protected BasketPaymentsManager<T> basketPaymentsManager;
	
	protected ButtonsGroupComponent principalPaymentMethodsButtonGroup, otherPaymentMethodsButtonGroup;
	protected ButtonsGroupModel principalPaymentMethodsButtonsConfiguration;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando pantalla...");
		
		vbOtherPaymentMethods.setVisible(false);
		vbOtherPaymentMethods.setManaged(false);		
	}

	@Override
	public void initializeComponents() {
		log.debug("inicializarComponentes()");
		log.debug("inicializarComponentes() - Cargando medios de pago");

		addSelectAllOnFocus();
		
		wvPrincipal.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if(newValue) {
					tfBalance.requestFocus();
				}
			}
		});

	}
	
	protected List<ButtonConfigurationBean> buttonsModelRowToPaymentsButtonsModel(ButtonsGroupPanelRowBean row) {
		List<ButtonConfigurationBean> finalButtonsList = new ArrayList<>();
				
		// replace payment method button configuration for specialized PaymentMethodButtonConfigurationBean
		for (ButtonConfigurationBean buttonConfiguration : row.getButtonRow())  {						
			if (buttonConfiguration.getParams() != null) {
				String paymentCode = buttonConfiguration.getParams().get("paymentMethodCode");
				
				if (paymentCode == null) paymentCode = buttonConfiguration.getParams().get("codMedioPago"); // old version compatibility				
				
				if (StringUtils.isNotBlank(paymentCode)) {
					PaymentMethodDetail paymetMethodDetail = basketPaymentsManager.getPaymentMethod(paymentCode);
					
					if (paymetMethodDetail == null || !paymetMethodDetail.getActive()) {
						log.warn("Payment method button configured with incorrect payment method code: " + paymentCode);
						continue;						
					}
					
					// new payment method configuration 
					buttonConfiguration = new PaymentMethodButtonConfigurationBean(buttonConfiguration.getImagePath(), 
							                                                       buttonConfiguration.getText(), 
							                                                       buttonConfiguration.getShortcut(), 
							                                                       ACTION_SELECT_PAYMENT_METHOD, 
							                                                       "", 
							                                                       paymetMethodDetail,
							                                                       StringUtils.equalsIgnoreCase(buttonConfiguration.getParams().get("directBalanceDuePayment"), "true") );											
				}
			}
			
			finalButtonsList.add(buttonConfiguration);						
		}
		
		return finalButtonsList;
	}

	protected void loadButtonsGroupPaymentsMethods() {
		// buttons initialized
		if (principalPaymentMethodsButtonGroup != null) return;
		
		Set<String> paymentsMethodsConfigured = new HashSet<>();
		otherPaymentMethodsButtonGroup = null;
		
		try {
			principalPaymentMethodsButtonsConfiguration = getSceneView().loadButtonsGroup();
			
			
			if (!principalPaymentMethodsButtonsConfiguration.getButtonsRow().isEmpty()) {
				// first row buttons configuration
				List<ButtonConfigurationBean> principalPaymentsButtons = buttonsModelRowToPaymentsButtonsModel(principalPaymentMethodsButtonsConfiguration.getButtonsRow().get(0));
				
				paymentsMethodsConfigured = principalPaymentsButtons.stream()
					                         .filter(PaymentMethodButtonConfigurationBean.class::isInstance)
					                         .map(b -> ((PaymentMethodButtonConfigurationBean)b).getPaymentMethod().getPaymentMethodCode())
					                         .collect(Collectors.toSet());

				// add button group to ui
				principalPaymentMethodsButtonGroup = new ButtonsGroupComponent(1, principalPaymentsButtons.size(), this, principalPaymentsButtons, vbPaymentsMethods.getPrefWidth(), vbPaymentsMethods.getPrefHeight(), ActionButtonPaymentMethodComponent.class.getName());
				vbPaymentsMethods.getChildren().clear();
				vbPaymentsMethods.getChildren().add(principalPaymentMethodsButtonGroup);
				
				// optional second row for other payments method
				if (principalPaymentMethodsButtonsConfiguration.getButtonsRow().size() > 1) {
					// second row buttons configuration
					List<ButtonConfigurationBean> otherPaymentMethodsButtons = buttonsModelRowToPaymentsButtonsModel(principalPaymentMethodsButtonsConfiguration.getButtonsRow().get(1));
					
					otherPaymentMethodsButtonGroup = new ButtonsGroupComponent(9, 1, this, otherPaymentMethodsButtons, null, vbOtherMethodsButtons.getPrefHeight(), ActionButtonPaymentMethodComponent.class.getName());
					
					// add component to vBox
					vbOtherMethodsButtons.getChildren().clear();
					vbOtherMethodsButtons.getChildren().add(otherPaymentMethodsButtonGroup);
				}
			}
			
		} catch (InitializeGuiException ex) {
			log.info("inicializarComponentes() - The payment methods button panel has not been loaded.: " + ex.getMessage());
		} catch (Exception e) {
			log.error("loadButtonsPanelPaymentsMethods() - Error: " + e.getMessage(), e);
		}
		
		// others payment methods load from code
		if (otherPaymentMethodsButtonGroup == null) {
		   loadOtherMethodsButtons(paymentsMethodsConfigured);
		}		
	}
	
	protected void loadOtherMethodsButtons(Set<String> paymentsMethodsConfigured) {
		try {
			List<PaymentMethodDetail> paymentMethods = basketPaymentsManager.getPaymentsMethods().getVisibleInSalesPaymentsMethods();
								
			List<ButtonConfigurationBean> otherPaymentMethodsConfig = new LinkedList<>();
			
			// create model configuration
			for (PaymentMethodDetail paymentMethod : paymentMethods) {
				if (paymentsMethodsConfigured.contains(paymentMethod.getPaymentMethodCode()) || !paymentMethod.getActive()) continue;
				
				otherPaymentMethodsConfig.add(new PaymentMethodButtonConfigurationBean(null, I18N.getText(paymentMethod.getPaymentMethodDes()), null, ACTION_SELECT_PAYMENT_METHOD, "", paymentMethod, false));
			}

			// create button group
			otherPaymentMethodsButtonGroup = new ButtonsGroupComponent(9, 1, this, otherPaymentMethodsConfig, null, vbOtherMethodsButtons.getPrefHeight(), ActionButtonPaymentMethodComponent.class.getName());
			
			// add component to vBox
			vbOtherMethodsButtons.getChildren().clear();
			vbOtherMethodsButtons.getChildren().add(otherPaymentMethodsButtonGroup);
		}
		catch (Exception e) {
			log.error("loadOtherMethodsButtons() - Error: " + e.getMessage(), e);
		}
	}
	
	protected void updateSelectedPaymentMethodButton() {	
		lbPaymentMethod.setText(paymentMethodSelected.getPaymentMethodDes());
		
		// List of all paymentMethod buttons
		ArrayList<Button> allButtons = new ArrayList<>(principalPaymentMethodsButtonGroup.getButtonsList());
		
		if (otherPaymentMethodsButtonGroup != null) allButtons.addAll(otherPaymentMethodsButtonGroup.getButtonsList());
		
		Set<String> availablePaymentMethods = basketPaymentsManager.getAvailablePaymentMethods();

		for (Button button : allButtons) {
			button.getStyleClass().remove(BTN_SELECTED_CLASS);

			if (!(button.getUserData() instanceof PaymentMethodButtonConfigurationBean)) {
				continue;
			}
			
			String paymentCode = null;
			
			PaymentMethodButtonConfigurationBean configuration = ((PaymentMethodButtonConfigurationBean) button.getUserData());

			if (configuration.getPaymentMethod() != null) {
				paymentCode = configuration.getPaymentMethod().getPaymentMethodCode();
				
				button.setDisable(!availablePaymentMethods.contains(paymentCode));
			}
				
			if (StringUtils.equals(paymentCode, paymentMethodSelected.getPaymentMethodCode())) {
				button.getStyleClass().add(BTN_SELECTED_CLASS);

				if (!principalPaymentMethodsButtonGroup.getButtonsList().contains(button)) {
					Optional<Button> othersButton = principalPaymentMethodsButtonGroup.getButtonsList().stream()
					        .filter(btn -> !(btn.getUserData() instanceof PaymentMethodButtonConfigurationBean)).findFirst();
					if (othersButton.isPresent()) {
						othersButton.get().getStyleClass().add(BTN_SELECTED_CLASS);
					}
				}
			}
		}
	}
	
	protected void selectDefaultPaymentMethod() {
		requestFocusTfBalance(basketManager.getBasketTransaction());

		selectPaymentMethod(basketPaymentsManager.getDefaultPaymentMethod());
	}
	
	protected void processPaymentError(Throwable exception) {
		log.error("PaymentErrorEvent :: Ha habido un error al registrar el pago: " + exception.getMessage(), exception);
		
		if (exception instanceof TenderException) {		
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(exception.getMessage(),exception);
		}
		else {
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Error inesperado al registrar el pago"), exception);
		}
		
		requestFocusTfBalance();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		if(!session.getCashJournalSession().checkCashJournalClosingMandatory()){
			String fechaCaja = FormatUtils.getInstance().formatDate(session.getCashJournalSession().getOpeningDate());
			String fechaActual = FormatUtils.getInstance().formatDate(new Date());
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual));
			throw new InitializeGuiException(false);
		}
		
		basketManager = (T) sceneData.get(BasketItemizationControllerAbstract.BASKET_KEY);
		
		basketPaymentsManager = (BasketPaymentsManager<T>) basketManager.getBasketPaymentsManager();
		
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		
		if(basketTransaction != null && basketTransaction.getPayments() != null && !basketTransaction.getPayments().isEmpty()) {
			log.debug("onSceneOpen() - Inicializando la pantalla de pagos con estos pagos rescatados:");
			for(BasketPayment payment : basketTransaction.getPayments().getPaymentsList()) {
				log.debug("onSceneOpen() - " + payment);
			}
		}

		// payment methods buttons
		loadButtonsGroupPaymentsMethods();

		// select default payment method
		selectDefaultPaymentMethod();		

	}

	@Override
	public void initializeFocus() {
		// if scene is reopen after change due scene
		if (basketManager.getBasketTransaction() == null) return;
		
		requestFocusTfBalance();
	}

	/**
	 * Función que refresca los totales en pantalla
	 */
	public void refreshScreenData() {
		log.debug("refreshScreenData()");
		
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
				
		requestFocusTfBalance(basketTransaction);

		updateButtonsStatus(basketTransaction);
		
		updateDocumentTypeLabel(basketTransaction);
		
		updateCustomerLabel(basketTransaction);
		
		writeDisplayDevice(basketTransaction);
		
		validateFiscalData();
		
		validateServiceData(basketTransaction);
		
		loadWebViewPrincipal(basketTransaction);
	}
	
	protected void loadWebViewPrincipal(BasketPromotable<?> basketTransaction) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("basketTransaction", basketTransaction);
		
		loadWebView(getWebViewPath(), params, wvPrincipal);
	}
		
	protected String getWebViewPath() {
		return "sales/payments/payments";
	}

	protected void updateCustomerLabel(BasketPromotable<?> basketTransaction) {
		String customerDescription = basketTransaction.getHeader().getCustomer().getCustomerDes();
		
		if(basketTransaction.getHeader().getInvoiceData() != null) {
			customerDescription = basketTransaction.getHeader().getInvoiceData().getCustomerDes();
		}
		
		lbCustomer.setText(customerDescription);
	}

	protected void updateDocumentTypeLabel(BasketPromotable<?> basketTransaction) {
		Long documentTypeId = basketTransaction.getHeader().getDocTypeId();
		DocTypeDetail documentType = session.getApplicationSession().getDocTypeByDocTypeId(documentTypeId);

		lbDocumentType.setText(documentType.getDocTypeDes());
	}
	
	protected void updateButtonsStatus(BasketPromotable<?> basketTransaction) {
		log.debug("updateButtonsStatus() - Activando y desactivando botones de aceptar y cancelar.");
		
		BigDecimal pendiente = basketTransaction.getTotals().getBalanceDue();
		
		btAccept.setDisable(BigDecimalUtil.isGreaterThanZero(pendiente.abs()) || (saleDocumentDataValidation != null && !saleDocumentDataValidation.isValid()));
		
		btCancel.setDisable(basketTransaction.getTenderMode());
	}

	
	
	/**
	 * Accion anotarPagos. Crea linea de pago con el importe indicado y el
	 * medio de pago seleccionado en pantalla
	 *
	 * @param importe importe a anotar
	 */
	public void addPayment(BigDecimal importe) {
		if (paymentMethodSelected == null) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No hay ninguna forma de pago seleccionada."));
			return;
		}

		log.debug("addPayment() - payment method code:" + paymentMethodSelected + " // Amount: " + importe);
		
		if(importe == null) {
			requestFocusTfBalance();
			return;
		}
		
		if(importe.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
						
		paymentRequest(importe);
	}

	protected void endTenderRequest() {
		endTenderRequest(true);
	}
	
	protected void endTenderRequest(boolean succeded) {
		tfBalance.requestFocus();

		if(succeded) {
			selectDefaultPaymentMethod();
		}
		
		refreshScreenData();
	}
	
	protected void writeDisplayDevice(BasketPromotable<?> basketTransaction) {
		displayDevice.tenderMode(basketTransaction);
		
		String cad1 = I18N.getText("TOTAL:     ");
		if (BigDecimalUtil.isLessThanZero(basketTransaction.getTotals().getTotalWithTaxes())) {
			//Si el total es negativo, quitamos un espacio a la cadena anterior
			cad1 = cad1.substring(0, cad1.length()-1);
		}
		displayDevice.write(cad1 + FormatUtils.getInstance().formatNumber(basketTransaction.getTotals().getTotalWithTaxes()),
					   I18N.getText("PENDIENTE: ") + FormatUtils.getInstance().formatNumber(basketTransaction.getTotals().getBalanceDue()));
	}

    protected void paymentRequest(BigDecimal amount) {
    	BasketTenderRequest basketTenderRequest;
    	
		if(!isRefund()){
			basketTenderRequest = new BasketPaymentRequest();
		}
		else {
			basketTenderRequest = new BasketRefundRequest();
		}
		
		basketTenderRequest.setPaymentMethodId(paymentMethodSelected.getPaymentMethodId());
		basketTenderRequest.setAmount(amount);
		basketTenderRequest.setData(new HashMap<>());
		basketTenderRequest.getData().put(TenderRequest.TENDER_DATA_USER_CODE, session.getPOSUserSession().getUser().getUserCode());

		// payment pre-validations
		if (!validatePayment(basketTenderRequest)) {
			return;
		}
		
		// create and execute tender request
		if (basketTenderRequest instanceof BasketPaymentRequest){
			payRequest((BasketPaymentRequest) basketTenderRequest);
		}
		else {
			refundRequest((BasketRefundRequest) basketTenderRequest);
		}
		
	}

	protected boolean isRefund() {
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		return basketTransaction.getRefund() || basketTransaction.getTotals().getTotalWithTaxes().compareTo(BigDecimal.ZERO)<0;
	}

	protected void saveDocumentSucceeded(BasketPromotable<?> basketTransactionCopy, BasketDocumentIssued<?> documentIssued) {
		new BackgroundPrintTask(getStage(), documentIssued) {
			@Override
			protected DialogWindowBuilder errorDialogBuilder(Stage stage, String message, boolean canRetry) {
				return super.errorDialogBuilder(stage, message, canRetry)
						.addButton(new ButtonType(I18N.getText("Ver documento"), ButtonData.NEXT_FORWARD));
			}
			@Override
			protected void handleErrorDialog(Stage stage, PrintDocumentRequest printRequest,
					DocumentIssued<?> documentIssued, CzzAlertDialog window, boolean canRetry) {
				if (window.getResult() != null && window.getResult().getButtonData().equals(ButtonData.NEXT_FORWARD)) {
					sceneData.put(DocumentViewerController.PARAM_DOCUMENT_UID, documentIssued.getDocumentUid());
					openScene(DocumentViewerController.class,new SceneCallback<Void>() {

						@Override
						public void onSuccess(Void callbackData) {
							printEnd();
						}
						
						@Override
						public void onCancel() {
							printEnd();
						}
						
						@Override
						public void onFailure() {
							printEnd();
						}
					});
				} else {
					super.handleErrorDialog(stage, printRequest, documentIssued, window, canRetry);
				}
			}
			
			@Override
			public void printEnd() {
				//Mostramos la ventana con la información de importe pagado, cambio...
				if(!BigDecimalUtil.isEqualsToZero(basketTransactionCopy.getTotals().getChangeDue().getAmount())){
					openChangeDueScene(basketTransactionCopy, voidCallback -> endBasketTransaction());
				}else {
					endBasketTransaction();
				}				
			}
		};
    }
	
	
	protected void saveDocumentFailed(Exception e) {
		log.error("acceptPayments() - onFailure - An error was thrown saving the ticket: ",e );
		if(e instanceof BusinessException) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(e.getLocalizedMessage());
		}else {
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e);
		}
		btAccept.setDisable(false);
	}
	
	protected void endBasketTransaction() {
		resetSaleDocumentDataValidation();
		closeSuccess();
	}		
	
	
	/**
	 * Acción realizada al pulsar el botón de facturación
	 */
	public void actionInvoice() {
		log.debug("accionFactura()");
		try {
			sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, basketManager);
			openScene(InvoiceController.class, new SceneCallback<Void>() {
				@Override
				public void onSuccess(Void callbackData) {
					refreshScreenData();
				}
			});
		}
		catch (Exception e) {
			log.error("accionFactura() - Excepción no controlada : " + e.getCause(), e);
		}
		
	}
	
	/**
	 * Acción evento de teclado sobre campo importe
	 *
	 * @param event
	 */
	public void actionTfBalance(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			log.debug("actionTfBalance(() - ENTER key in balance field");
			BigDecimal importe = FormatUtils.getInstance().parseAmount(tfBalance.getText());
			addPayment(importe);
		}
	}


	public void actionChangePaymentMethod(){
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		
		sceneData.put(ChangeController.PARAM_INPUT_PAYMENT_METHOD_CODE_CHANGE, basketTransaction.getTotals().getChangeDue().getChangePaymentMethod().getPaymentMethodCode());
		sceneData.put(ChangeController.PARAM_INPUT_PAYMENT_METHOD_DES_CHANGE, basketTransaction.getTotals().getChangeDue().getChangePaymentMethod().getPaymentMethodDes());

		openScene(ChangeController.class, new SceneCallback<String>() {
			
			@Override
			public void onSuccess(String codMedioPagoSeleccionado) {				
				PaymentMethodDetail medioPago = basketPaymentsManager.getPaymentMethod(codMedioPagoSeleccionado);
				basketManager.updateChangeDuePaymentMethod(modelMapper.map(medioPago, BasketPaymentMethod.class));
				
			}
		});

	}

	
	
	/**
	 * Método de control de acciones de página de pagos
	 *
	 * @param actionButton botón pulsado
	 */
	@Override
	public void executeAction(ActionButtonComponent actionButton) {
		log.debug("executeAction() - Executing action : " + actionButton.getClave() + " of type : " + actionButton.getTipo());
		
		switch (actionButton.getClave()) {
		case ACTION_SELECT_PAYMENT_METHOD:
			ActionButtonPaymentMethodComponent paymentButton = (ActionButtonPaymentMethodComponent) actionButton;
			
			if (paymentButton.getPaymentMethod() != null) {
				if (vbOtherPaymentMethods.isVisible()) {
					switchLateralVBox();				
				}
				
				selectPaymentMethod(paymentButton.getPaymentMethod());
				
				if (paymentButton.getDirectBalanceDuePayment()) {
					addPayment(basketManager.getBasketTransaction().getTotals().getBalanceDue());
				}
				
				tfBalance.requestFocus();
			} else {
				switchLateralVBox();
			}
			break;
		default:
			log.error("Action not implemented :" + actionButton.getClave());
			requestFocusTfBalance();
			break;
		}

	}

	/**
	 * Es llamado desde BotoneraComponent si hay botones de tipo PAGO
	 * */
	public void addDefaultPayment(BigDecimal amount) {
		log.debug("addDefaultPayment() - " + amount.toPlainString());
		paymentMethodSelected = basketPaymentsManager.getDefaultPaymentMethod();
		addPayment(amount);
	}

	/**
	 * Se hace focus al text field del importe con el importe pendiente por pagar cargado.
	 */
	protected void requestFocusTfBalance() {
		requestFocusTfBalance(basketManager.getBasketTransaction());
	}
	
	protected void requestFocusTfBalance(BasketPromotable<?> basketTransaction){
		tfBalance.setText(FormatUtils.getInstance().formatAmount(basketTransaction.getTotals().getBalanceDue()));
		tfBalance.requestFocus();
		tfBalance.selectAll();
	}

	protected void openChangeDueScene(BasketPromotable<?> basketTransaction, SceneCallback<?> childSceneCallback){
		BigDecimal cambio = basketTransaction.getTotals().getChangeDue().getAmount();
		String cambioFormateado = FormatUtils.getInstance().formatAmount(cambio);

		sceneData.put(ChangeDueScreenController.PARAM_BASKET_TRANSACTION, basketTransaction);
		
		displayDevice.write(I18N.getText("CAMBIO"), cambioFormateado);

		openScene(ChangeDueScreenController.class, childSceneCallback);
	}

	public void actionChangeCustomerData() {
		sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, basketManager);
		openScene(ChangeCustomerDataController.class);
	}
	
	public void actionOpenShippingData() {
		sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, basketManager);
		openScene(ShippingDataController.class, new SceneCallback<Void>() {
			@Override
			public void onSuccess(Void callbackData) {
				refreshScreenData();
			}
		});
	}
	
    public void selectPaymentMethod(HashMap<String, String> params) {
		String paymentMethodCode = params.get("paymentMethodCode");
		
		if (paymentMethodCode == null) paymentMethodCode = params.get("codMedioPago");
		
		
		if(paymentMethodCode == null){
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se ha especificado una acción correcta para este botón"));
        	log.error("No existe el código del medio de pago para este botón.");
        	return;
		}
		
		PaymentMethodDetail paymentMethod = basketPaymentsManager.getPaymentMethod(paymentMethodCode);
		
		if(paymentMethod == null) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha habido un error al recuperar el medio de pago"));
			log.error("No se ha encontrado el medio de pago con código: " + paymentMethodCode);
			return;
		}

		selectPaymentMethod(paymentMethod);
		
		String value = params.get("value");
		
		if (value == null) value = params.get("valor");
		
		if (value != null){
			try {
				BigDecimal balance = new BigDecimal(value);
				addPayment(balance);
			}
			catch(Exception e) {
				log.error("El valor configurado no se puede formatear: " + value, e);
			}
		}
	}
    
    public void selectPaymentMethod(PaymentMethodDetail paymentMethod) {
		paymentMethodSelected = paymentMethod;
		
		updateSelectedPaymentMethodButton();
	}

	/**
	 * Añade a los campos de texto de la pantalla la capacidad de seleccionar todo su texto cuando adquieren el foco
	 */
	protected void addSelectAllOnFocus() {
		addSelectAllOnFocus(tfBalance);
    }

	/**
	 * Acción aceptar
	 */
	@FXML
	public void actionAccept() {
		if (basketManager.getBasketTransaction().getTotals().getBalanceDue().compareTo(BigDecimal.ZERO) != 0 && basketManager.getDocumentType().getRequiereCompletarPagos()) {
			log.debug("aceptar() - Pagos no cubiertos");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Los pagos han de cubrir el importe a pagar."));
			return;
			
		}

		if(!session.getCashJournalSession().checkCashJournalClosingMandatory()){
			String fechaCaja = FormatUtils.getInstance().formatDate(session.getCashJournalSession().getOpeningDate());
			String fechaActual = FormatUtils.getInstance().formatDate(new Date());
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual));
			return;
		}		
		
		acceptPayments();
	}
	
	protected void acceptPayments() {
		// evitar dobles pulsaciones
		btAccept.setDisable(true);
				
		log.debug("acceptPayments() - Pagos cubiertos");
		
		// assign cash journal data
		basketManager.updateCashJournalData(session.getCashJournalSession().getCashJournalUid(), session.getCashJournalSession().getAccountingDate());
		
		// update document print settings
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		BasketLoyalCustomer datosFidelizado = basketTransaction.getHeader().getLoyalCustomer();
						
		PrintDocumentRequest printSettings = printService.getDefaultPrintSettings(basketTransaction.getHeader().getDocTypeCode());
		printSettings.getCustomParams().put("paperLess", datosFidelizado != null && datosFidelizado.getPaperLess() != null && datosFidelizado.getPaperLess());
		
		basketManager.updatePrintSettings(printSettings);		

		// save document
		new SaveDocumentBackgroundTask(basketManager, new SaveDocumentCallback() {			
			@Override
			public void onSucceeded(BasketPromotable<?> basketTransactionCopy, BasketDocumentIssued<?> documentIssued) {
				saveDocumentSucceeded(basketTransactionCopy, documentIssued);				
			}
			
			@Override
			public void onFailure(Exception e) {
				saveDocumentFailed(e);
			}			
		}).start();
	}
	
    protected synchronized void payRequest(BasketPaymentRequest basketPayRequest) {
    	    	
    	try {
			PaymentMethodManager manager = basketPaymentsManager.getPaymentMethodManager(basketPayRequest.getPaymentMethodId());
			log.debug("payRequest() - Using payment method manager: " + manager.getClass());
    		
    		String guiView = manager.getPaymentGuiView();
    		
    		if(StringUtils.isNotBlank(guiView)) {
    			log.debug("payRequest() - Payment gui view found: "+guiView);
				openPaymentGuiView(basketPayRequest, guiView + "Controller", new SceneCallback<BasketPaymentRequest>(){
    				
    				@Override
					public void onSuccess(BasketPaymentRequest basketPayRequest) {
    					log.debug("payRequest() - onSuccess() - Success at the gui view.");
						try {
							TenderRequest request = basketPaymentsManager.createPayRequest(basketPayRequest);
							executePayRequest(request);
						}
						catch (TenderException e) {
							processPaymentError(e);
						}
    				}

    				@Override
    				public void onCancel() {
    					log.debug("payRequest() - onCancel(): Cancelling the pending tender request.");
    					endTenderRequest(false);
    				}
    				
    				@Override
    				public void onFailure() {
    					log.debug("payRequest() - onFailure(): The gui view could not be loaded. Cancelling the pending tender request.");
    					endTenderRequest(false);
    				}
    			});
    		} else {
    			log.debug("payRequest() - No aditional data required in pay request");
				TenderRequest request = basketPaymentsManager.createPayRequest(basketPayRequest);
    			executePayRequest(request);
    		}
    	}catch (Exception e) {
    		log.error("payRequest() - An exception was throw: ",e);
    		DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha ocurrido un error intentando realizar el pago de la venta."));
		}
    	
	}

	protected void openPaymentGuiView(BasketTenderRequest basketTenderRequest, String guiView, SceneCallback<?> callback) throws TenderException {
		try {
			SceneController sceneController = CoreContextHolder.getInstance(guiView);

			sceneData.put(PAYMENT_GUI_BASKET_MANAGER, basketManager);
			sceneData.put(PAYMENT_GUI_BASKET_TENDER_REQUEST, basketTenderRequest);
			openScene(sceneController.getClass(), callback);
		}catch(Exception e) {
			throw new TenderException(null, "Payment gui view "+guiView+" could not be found.", e);
		}
		
	}

	protected void executePayRequest(TenderRequest request) {
		try {
			log.debug("executePayRequest() - Executing pay request in background task.");
			new BackgroundTask<TenderResponse>(true) {
				@Override
				protected TenderResponse execute() throws Exception {
					request.addObserver(this);
					return basketPaymentsManager.executePayRequest(request);				
				}
	
				@Override
				protected void succeeded() {
					super.succeeded();
	
					endTenderRequest();
					
				}
	
				@Override
				protected void failed() {
					super.failed();
	
					processPaymentError(getException());
					endTenderRequest(false);
				}
				
				@Override
			    public void update(Observable o, Object arg) {
					if (arg instanceof TenderProgress) {
						updateProgressMessage(((TenderProgress)arg).getProgressMessage());
					}
	   
			    }
			}.start();
		} catch (Exception e) {
			log.error("payRequest() - An error was thrown: ", e);
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha ocurrido error intentando realizar la petición de pago."), e);
		}
	}

	protected void refundRequest(BasketRefundRequest basketRefund) {
		
		try {
			PaymentMethodManager manager = basketManager.getBasketPaymentsManager().getPaymentMethodManager(basketRefund.getPaymentMethodId());
			log.debug("refundRequest() - Using payment method manager: " + manager.getClass());

			String guiView = manager.getPaymentGuiView();			
			
			if (StringUtils.isNotBlank(guiView)) {
				log.debug("refundRequest() - Payment gui view found: " + guiView);

				openPaymentGuiView(basketRefund, guiView + "Controller", new SceneCallback<BasketRefundRequest>(){
    				@Override
					public void onSuccess(BasketRefundRequest basketRefund) {
    					log.debug("refundRequest() - onSuccess() - Success at the gui view.");
						try {
							TenderRequest request = basketPaymentsManager.createRefundRequest(basketRefund);
							executeRefundRequest(request);
						}
						catch (TenderException e) {
							processPaymentError(e);
						}
    				}
    				@Override
    				public void onCancel() {
    					log.debug("refundRequest() - onCancel(): Cancelling the pending tender request.");
    					endTenderRequest(false);
    				}
    				
					@Override
    				public void onFailure() {
    					log.debug("refundRequest() - onFailure(): The gui view could not be loaded. Cancelling the pending tender request.");
    					endTenderRequest(false);
    				}

    			});
			} else {
				log.debug("refundRequest() - No aditional data required in pay request");
				TenderRequest request = basketPaymentsManager.createRefundRequest(basketRefund);
				executeRefundRequest(request);
			}
		} catch (Exception e) {
			log.error("refundRequest() - An exception was throw: ", e);
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Se ha producido un error intentando realizar el pago."), e);
		}
	}

	protected void executeRefundRequest(TenderRequest request) {
		new BackgroundTask<TenderResponse>(true) {
			@Override
			protected TenderResponse execute() throws Exception {
				request.addObserver(this);
				return basketPaymentsManager.executeRefundRequest(request);
			}

			@Override
			protected void succeeded() {
				super.succeeded();

				endTenderRequest();
			}

			@Override
			protected void failed() {
				super.failed();

				processPaymentError(getException());
				endTenderRequest(false);
			}

			@Override
			public void update(Observable o, Object arg) {
				if (arg instanceof TenderProgress) {
					updateProgressMessage(((TenderProgress) arg).getProgressMessage());
				}

			}
		}.start();
	}

	protected synchronized void deletePayment(Integer paymentId) {
		// validate paymentId
		if (!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Desea eliminar el pago?"))) {
			return;
		}
				
		log.debug("deletePayment() - User request for cancel payment id: " + paymentId);
						
		new BackgroundTask<TenderResponse>(true) {
			@Override
			protected TenderResponse execute() throws Exception {
				TenderResponse response;
				
				if(!isRefund()){
					TenderRequest request = basketPaymentsManager.createCancelPayRequest(paymentId, null);
					request.addObserver(this);
					response = basketPaymentsManager.executeCancelPayRequest(request);	
				}
				else {
					TenderRequest request = basketPaymentsManager.createCancelRefundRequest(paymentId, null);
					request.addObserver(this);
					response = basketPaymentsManager.executeCancelRefundRequest(request);	
				}
				
				return response;				
			}

			@Override
			protected void succeeded() {
				super.succeeded();

				endTenderRequest();
			}

			@Override
			protected void failed() {
				super.failed();

				processPaymentError(getException());
			}
			
			@Override
		    public void update(Observable o, Object arg) {
				if (arg instanceof TenderProgress) {
					updateProgressMessage(((TenderProgress)arg).getProgressMessage());
				}
   
		    }
		}.start();
	}

    
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if("DeletePayment".equals(method)) {
			String paramLineId = params.get("line_id");
			Integer lineId = Integer.valueOf(paramLineId);

			deletePayment(lineId);
		}
	}

	@FXML
	public void switchLateralVBox() {

		Boolean visibleVBox = vbOtherPaymentMethods.isVisible();

		vbOtherPaymentMethods.setVisible(!visibleVBox);
		vbOtherPaymentMethods.setManaged(!visibleVBox);
		vbGeneral.setVisible(visibleVBox);
		vbGeneral.setManaged(visibleVBox);
	}
	
	@FXML
	public void showFiscalValidations() {
		String message = "";
		if(saleDocumentDataValidation == null || (saleDocumentDataValidation.isValid() && !saleDocumentDataValidation.isConfirmRequired())) {
			message = I18N.getText("El documento es fiscalmente válido.");
			DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(message);
		}else {
			message = I18N.getText("El documento no es fiscalmente válido.") + System.lineSeparator() + System.lineSeparator();
			boolean hasErrors = false;
			if(!saleDocumentDataValidation.isValid()) {
				hasErrors = true;
				message += I18N.getText("El documento tiene los siguientes errores: ") + System.lineSeparator() + saleDocumentDataValidation.getErrorValidationMessages();
			}
			if(saleDocumentDataValidation.isConfirmRequired()) {
				if(hasErrors) {
					message += System.lineSeparator();
				}				
				message += I18N.getText("El documento tiene las siguientes advertencias: ")  +System.lineSeparator() + saleDocumentDataValidation.getWarnValidationMessages();				
			}
			
			if(hasErrors) {
				DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(message);
			}else {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(message);
			}
			
		}
		
	}
	
	protected void validateServiceData(BasketPromotable<?> basketTransaction) {
		StorePosServicesData posServiceData = session.getApplicationSession().getStorePosBusinessData().getServicesData();
		String basketServiceTypeCode = basketTransaction.getHeader().getServiceData() != null ? basketTransaction.getHeader().getServiceData().getServiceTypeCode() : null;
		
		StoreServiceType basketServiceType = null;
		
		if (StringUtils.isBlank(basketServiceTypeCode)) {
			basketServiceType = basketManager.getDefaultServiceType();
		} else {
			basketServiceType = posServiceData.getStoreServicesType().get(basketServiceTypeCode);			
		}

		btServiceData.setDisable(basketServiceType == null || BooleanUtils.isNotTrue(basketServiceType.getExternalLogistic()));
		if (basketServiceType != null) {
			lbSaleType.setText(basketServiceType.getServiceTypeDes());
		}
	}
	protected void validateFiscalData() {
		saleDocumentDataValidation = basketManager.validateBasket();
		btFiscalValidation.getStyleClass().clear();
		if(saleDocumentDataValidation.isValid() && !saleDocumentDataValidation.isConfirmRequired()) {
			btFiscalValidation.getStyleClass().add("btn-notification");
			btFiscalValidation.getStyleClass().add("info");
		}else if(saleDocumentDataValidation.isValid() && saleDocumentDataValidation.isConfirmRequired()) {
			btFiscalValidation.getStyleClass().add("btn-notification");
			btFiscalValidation.getStyleClass().add("warn");
		}else if(!saleDocumentDataValidation.isValid()){
			btFiscalValidation.getStyleClass().add("btn-notification");
			btFiscalValidation.getStyleClass().add("error");
			btAccept.setDisable(true);
		}
	}
	
	protected void resetSaleDocumentDataValidation() {
		saleDocumentDataValidation = null;
	}
	
	protected boolean validatePayment(BasketTenderRequest basketTenderRequest) {
		SaleDocumentDataValidationResult paymentValidationResult = basketPaymentsManager.validateRequest(basketTenderRequest);
		
		boolean hasErrors = false;
		String paymentValidationMessage = "";
		
		if(!paymentValidationResult.isValid()) {
			hasErrors = true;
			paymentValidationMessage = I18N.getText("No se puede añadir el pago.")+System.lineSeparator()+System.lineSeparator();
			paymentValidationMessage += paymentValidationResult.getErrorValidationMessages();
		}
		if(paymentValidationResult.isConfirmRequired()) {
			if(hasErrors) {
				paymentValidationMessage += System.lineSeparator();
				paymentValidationMessage += I18N.getText("Además, existen las siguientes advertencias.")+System.lineSeparator()+System.lineSeparator();
				paymentValidationMessage += paymentValidationResult.getWarnValidationMessages();
				DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(paymentValidationMessage);
				return false;
			}else {
				paymentValidationMessage = I18N.getText("El pago puede añadirse, pero existen las siguientes advertencias. ¿Está seguro de que quiere continuar?")+System.lineSeparator()+System.lineSeparator();
				paymentValidationMessage += paymentValidationResult.getWarnValidationMessages();
				if(!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(paymentValidationMessage)) {
					return false;
				}
			}			
		}
		
		if(hasErrors) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(paymentValidationMessage);
			return false;
		}
		
		return true;
	}
	
	/**
	 * @deprecated Use {@link #closeCancel()} instead
	 */
	@Deprecated
	@FXML
	public void actionCancel() {
		closeCancel();
	}
	
	@Override
	public boolean canCloseCancel() {
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		boolean tenderMode = basketTransaction.getTenderMode();
		
		if(tenderMode) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se puede abandonar la pantalla mientras la cesta se encuentra en modo pagos."));
			return false;
		}
		
		return true;
	}
	

	
	@Override
	public WebView getWebView() {
		return wvPrincipal;
	}
	
	@Override
	protected void succededLongOperations() {
		refreshScreenData();
	}
}
