package com.comerzzia.pos.gui.sales.cashjournal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.facade.model.DocType;
import com.comerzzia.core.facade.service.doctype.DocTypeServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalDocumentIssued;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalHdr;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalLine;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodDetail;
import com.comerzzia.omnichannel.facade.service.cashjournal.CashJournalServiceFacade;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.normal.ActionButtonNormalComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.services.session.ApplicationSession;
import com.comerzzia.pos.core.services.session.CashJournalSession;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.devices.printer.BackgroundPrintTask;
import com.comerzzia.pos.gui.sales.cashjournal.close.CloseCashJournalController;
import com.comerzzia.pos.gui.sales.cashjournal.counts.CashCountController;
import com.comerzzia.pos.gui.sales.cashjournal.lines.AddCashMovementController;
import com.comerzzia.pos.gui.sales.cashjournal.opening.OpeningCashJournalController;
import com.comerzzia.pos.util.date.CzzDate;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import lombok.extern.log4j.Log4j;

@Component
@CzzActionScene
@Log4j
public class CashJournalController extends ActionSceneController implements Initializable, ButtonsGroupController {
	@Autowired
	protected CashJournalSession cashJournalSession;

	// Variables de componente
	// botonera de menú
	protected ButtonsGroupComponent buttonGroupMenu;

	// Componentes de pantalla
	@FXML
	protected TabPane tabPanel;
	
	@FXML
	protected Tab  tabSales, tabMovements;
	
	// Movimientos de la lista de movimientos de Venta
	protected List<CashJournalSalesDto> sales;
	// Movimientos de la lista de movimientos no de ventas
	protected List<CashJournalLine> movements;

	@FXML
	protected WebView wvMovements, wvSales;

	@FXML
	protected Label lbCashierName, lbCashState, lbCashNumber;

	@FXML
	protected HBox hbBottomButtonsPanel;

	@Autowired
	protected VariableServiceFacade variablesService;

	@Autowired
	protected Session session;
	
	@Autowired
	protected ApplicationSession applicationSession;
	
	@Autowired
	protected ComerzziaTenantResolver tenantResolver;
	
	@Autowired
	protected CashJournalServiceFacade cashJournalService;
		
	@Autowired
	protected ModelMapper modelMapper;

	final DeviceLineDisplay lineDisplay = Devices.getInstance().getLineDisplay();

	/**
	 * Initializes the controller class.
	 */

	@Override
	public void initialize(URL url, ResourceBundle rb) {
			log.debug("initialize() - Inicializando pantalla...");
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		try {

			if(sales.isEmpty() || movements.isEmpty()) {
				loadWebView("commons/loadingData", null, getDisplayingWebView());
			}else {
				refreshScreenData();
			}
		}
		catch (Exception e) {
			log.error("onSceneOpen() - Error inesperado inicializando formulario. " + e.getMessage(), e);
			throw new InitializeGuiException(e);
		}

	}
	
	@Override
	protected void executeLongOperations() throws Exception {
		super.executeLongOperations();
		refreshMovementsAndSales();
	}
	
	protected void refreshMovementsAndSales() {
		sales.clear();
		movements.clear();
		if (cashJournalSession.isOpenedCashJournal()) {
			Map<String, PaymentMethodDetail> paymentsMethods = applicationSession.getPaymentMethodsMap();
			List<CashJournalLine> saleLines = cashJournalSession.findSalesLines();
			
			for (CashJournalLine mov : saleLines) {
				CashJournalSalesDto row = modelMapper.map(mov, CashJournalSalesDto.class);
				row.setDocType(applicationSession.getDocTypeByDocTypeId(mov.getDocTypeId()));
				row.setPaymentMethod(paymentsMethods.get(mov.getPaymentMethodCode()));
				
				sales.add(row);
			}
			movements.addAll(cashJournalSession.findManualLines());
		}
	}
	
	protected WebView getDisplayingWebView() {
		Tab tab = tabPanel.getSelectionModel().getSelectedItem();
		if(tab.equals(tabSales)) {
			return wvSales;
		}else {
			return wvMovements;
		}
	}
	
	@Override
	protected void succededLongOperations() {
		super.succededLongOperations();
		refreshScreenData();
	}
	
	@Override
	public void onSceneShow(){
		super.onSceneShow();
		refreshScreenData();
	}

	@Override
	public void initializeFocus() {

	}
	
	protected void initializeTabPane() {
		tabPanel.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab arg2) {
				loadCurrentTab();
			}
		});
	}
	
	protected void loadCurrentTab() {
		Tab tab = tabPanel.getSelectionModel().getSelectedItem();
		
		try {
			if (tab.equals(tabSales)) {
				loadWebViewSales();
			} else if (tab.equals(tabMovements)) {
				loadWebViewMovements();
			}
		} catch (Exception e) {
			log.error("loadCurrentTab() - Error loading webview", e);
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Error cargando la pestaña"), e);
		}
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
			}
		});

	}
	
	public void loadWebViewSales() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("sales", sales);

		loadWebView("cash_management/sales", params, wvSales);
					
		
	}
	public void loadWebViewMovements() {
		Map<String, Object> params = new HashMap<String, Object>();
				
		params.put("moves", movements);
		
		loadWebView("cash_management/movements", params, wvMovements);	
		
	}


	/**
	 * Inicializa componetes personalizados de pantalla: botoneras, etc
	 * 
	 * @throws InitializeGuiException
	 */
	@Override
	public void initializeComponents() throws InitializeGuiException {
		log.debug("initializeComponents() - Inicialización de componentes");
		// Comprobamos que existe el tipo de documento asociado
		try {
			applicationSession.getDocTypeForNatureDocumentType(DocTypeServiceFacade.NATURE_CASH_JOURNAL_CLOSE_DOCUMENT);			
		}
		catch (NotFoundException e) {
			throw new InitializeGuiException(I18N.getText("No está configurado el tipo de documento cierre de caja en el entorno."));
		}

		sales = new ArrayList<>();
		movements = new ArrayList<>();
		
		try {
			log.debug("initializeComponents() - Carga de acciones de botonera inferior");
			loadBottomButtonsPanel();
			initializeTabPane();
		}
		catch (LoadWindowException e) {
			e.printStackTrace();
		}
		wvMovements.setFocusTraversable(false);
		wvSales.setFocusTraversable(false);
		hbBottomButtonsPanel.setFocusTraversable(false);
	}

	
	
	public void changeCashier() {
		gotoLogin();
		loadCashInfo();
	}

	public void insertTransaction() {
		if (checkCashJournalClosingMandatory()) {
			openScene(AddCashMovementController.class, new SceneCallback<CashJournalDocumentIssued<?>>() {
				
				@Override
				public void onSuccess(CashJournalDocumentIssued<?> documentIssued) {
					refreshMovementsAndSales();
					refreshScreenData();
					
					if (documentIssued != null) {
						printDocumentIssued(documentIssued);
					}
				}
			});
			
		}
		else {
			String cashJournalDate = FormatUtils.getInstance().formatDate(cashJournalSession.getOpeningDate());
			String actualDate = FormatUtils.getInstance().formatDate(new Date());
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se puede insertar un apunte manual. El día de apertura de la caja {0} no coincide con el del sistema {1}", cashJournalDate, actualDate));
		}
	}
	
	public boolean checkCashJournalClosingMandatory() {
		Boolean mandatory = variablesService.getVariableAsBoolean(VariableServiceFacade.CAJA_CIERRE_CAJA_DIARIO_OBLIGATORIO, true);
		if (mandatory) {
			CzzDate openingDate = new CzzDate(cashJournalSession.getOpeningDate());
			CzzDate actualDate = new CzzDate(new Date());
			if (!openingDate.equalsDate(actualDate)) {
				return false;
			}
		}
		return true;
	}

	public void cashClose() {
		this.sceneData.put("moves", movements);
		this.sceneData.put("sales", sales);
		
		openScene(CloseCashJournalController.class, new SceneCallback<Map<String, Object>>() {
			
			@Override
			public void onSuccess(Map<String, Object> callbackData) {
				refreshMovementsAndSales();
				refreshScreenData();
				
				CashJournalDocumentIssued<?> documentIssued = (CashJournalDocumentIssued<?>) callbackData.get("document");
								
				new BackgroundPrintTask(getStage(), documentIssued) {
					@Override
					public void printEnd() {
						if ((Boolean)callbackData.get("cashImbalance")) {
							DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog("Caja cerrada con descuadres");
						}												
					}									
				};
				
			}
		});		
	}

	protected void closePendingFunctions(Class<? extends SceneController> sceneToShow) {
		boolean closeAllScenesExcept = stageController.closeAllActionsExcept(this);
		if(!closeAllScenesExcept) {
			return;
		}
		openScene(sceneToShow, new SceneCallback<Map<String, Object>>() {
			
			@Override
			public void onSuccess(Map<String, Object> callbackData) {
				refreshScreenData();
			}
		});
	}


	public void printLastCloseUp() {
		log.debug("printLastCloseUp()");
		try {
			CashJournalHdr lastCashJournal = cashJournalSession.findLastCashJournalClosed();
			
	    	auditOperation(new CashJournalAuditEventBuilder(lastCashJournal).addOperation("printLastCloseUp"));
			
			printCloseUp(lastCashJournal);
		}
		catch (Exception e) {
			log.error("Error recuperando el último cierre", e);
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Error recuperando el último cierre de caja para su impresión."));
		}
	}

	public void cashOpening() {
		log.debug("cashOpening()");
		if (cashJournalSession.isOpenedCashJournal()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ya existe una caja abierta en el sistema."));
			return;
		}
		openScene(OpeningCashJournalController.class, new SceneCallback<CashJournalDocumentIssued<?>>() {
			@Override
			public void onSuccess(CashJournalDocumentIssued<?> documentIssued) {
				refreshMovementsAndSales();
				refreshScreenData();
																
				if(variablesService.getVariableAsBoolean(VariableServiceFacade.TPV_PRINT_CASH_OPENING, false)) {
					printDocumentIssued(documentIssued);
				}
			}
		});
	}

	public void refreshScreenData() {
		// Actualizamos estado de caja en pantalla y botones activos
		updateCashJournalStatus();
	
		// Info cajero
		loadCashInfo();
		
		loadCurrentTab();
	}
	

	protected void loadBottomButtonsPanel() throws LoadWindowException {
		try {
			ButtonsGroupModel panelBotoneraBean = getSceneView().loadButtonsGroup("_panel.xml");
			buttonGroupMenu = new ButtonsGroupComponent(panelBotoneraBean, hbBottomButtonsPanel.getPrefWidth(), hbBottomButtonsPanel.getPrefHeight(), this, ActionButtonNormalComponent.class);
			hbBottomButtonsPanel.getChildren().clear();
			hbBottomButtonsPanel.getChildren().add(buttonGroupMenu);
		}
		catch (InitializeGuiException e) {
			log.error("loadBottomButtonsPanel() - Error al crear botonera: " + e.getMessage(), e);
		}
	}
	
	protected void loadCashInfo() {
		String cashierName = session.getPOSUserSession().getUser().getUserDes();
		lbCashierName.setText(getCashiersNameInitials(cashierName) + " · " + cashierName);
		lbCashNumber.setText(applicationSession.getTillCode());
		if (cashJournalSession.isOpenedCashJournal()) {
			lbCashState.setText(I18N.getText("Abierta"));
		}
		else {
			lbCashState.setText(I18N.getText("Cerrada"));
		}
	}
	
	@Override
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if("PrintMove".equals(method)) {
			log.debug("Entrando en PrintMove handler");
			String paramLineMove = params.get("paymentCode");
			Integer lineInteger = Integer.valueOf(paramLineMove);

			CashJournalLine moveToPrint = null;
			
			for(CashJournalLine move : movements) {
				if (move.getLineId().compareTo(lineInteger) == 0) {
					moveToPrint = move;
					break;
				}						
			}
			
			if (moveToPrint != null) {
			   printCashJournalLine(moveToPrint);
			} else {
				DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("El movimiento seleccionado no se ha encontrado"));
			}
		}
	}
	
	
	public void cashCountOpening() {

		log.debug("cashCountOpening() - Abrir pantalla de recuento de caja.");
		boolean confirmation = true;

		if (getStageController().isAnotherActionOpened()) {
			confirmation = DialogWindowBuilder.getBuilder(getStage())
					.simpleConfirmDialog(I18N.getText("Para realizar el recuento de caja se deben cerrar todas las pantallas abiertas. ¿Desea continuar?"));
		}
		if (confirmation) {
			if (cashJournalSession.hasCashJournalCount()) {
				if (DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Ya existe un recuento de la caja. ¿Desea borrarlo y empezar de nuevo?"))) {
					cashJournalSession.cleanCashJournalCount();
				}
			}
						
			closePendingFunctions(CashCountController.class);
		}

		refreshScreenData();
	}

	public void updateCashJournalStatus() {
		// Comprobamos estado de caja
		if (!cashJournalSession.isOpenedCashJournal()) {
			// Poner etiqueta de estado como Caja CERRADA
			lbCashState.setText(I18N.getText("Cerrada"));
			// Desactivar botones de Cierre, Insertar apunte y Recuento (Activar el resto)
			buttonGroupMenu.setAccionDisabled("cashOpening", false);
			buttonGroupMenu.setAccionDisabled("cashClose", true);
			buttonGroupMenu.setAccionDisabled("insertRecord", true);
			buttonGroupMenu.setAccionDisabled("cashCountOpening", true);
			buttonGroupMenu.setAccionDisabled("insertTransaction", true);
		}
		else {
			// Poner etiqueta de estado como Caja ABIERTA
			lbCashState.setText(I18N.getText("Abierta"));
			// Desactivar botón de Apertura (Activar el resto)
			buttonGroupMenu.setAccionDisabled("cashOpening", true);
			buttonGroupMenu.setAccionDisabled("cashClose", false);
			buttonGroupMenu.setAccionDisabled("insertRecord", false);
			buttonGroupMenu.setAccionDisabled("cashCountOpening", false);
			buttonGroupMenu.setAccionDisabled("insertTransaction", false);

		}
	}
	
	protected void printDocumentIssued(CashJournalDocumentIssued<?> documentIssued) {
		new BackgroundPrintTask(getStage(), documentIssued);
	}


	protected void printCashJournalLine(CashJournalLine cashJournalLine) {
		log.trace("printCashJournalLine()");
		DocType docType = applicationSession.getDocTypeByDocTypeId(cashJournalLine.getDocTypeId());
		
		new BackgroundPrintTask(getStage(), docType.getDocTypeCode(), cashJournalLine.getSalesDocUid());		
	}
	
	
	public void printCloseUp(CashJournalHdr cashJournal) throws DeviceException {
		log.debug("printCloseUp() - CashJournalUid: " + cashJournal.getCashJournalUid());
		
		new BackgroundPrintTask(getStage(), DocTypeServiceFacade.NATURE_CASH_JOURNAL_CLOSE_DOCUMENT, cashJournal.getCashJournalUid());
	}
	
	protected String getCashiersNameInitials(String fullName) {
		String res = "";
		List<String> upperCases = new ArrayList<String>();
		
		for(Character c : fullName.toCharArray()) {
			if(Character.isUpperCase(c)) {
				upperCases.add(c.toString());
			}
		}
		
		for(String s : upperCases) {
			res = res + s;
		}
		
		return res;
	}
	
	public void openCashDrawer() {
		auditOperation(new CashJournalAuditEventBuilder(cashJournalSession.getOpenedCashJournal()).addOperation("openCashDrawer"));
		
		Devices.openCashDrawer();
	}
	
	@Override
	public WebView getWebView() {
		return getDisplayingWebView();
	}
}
