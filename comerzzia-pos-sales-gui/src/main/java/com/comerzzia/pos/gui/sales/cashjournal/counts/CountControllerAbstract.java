package com.comerzzia.pos.gui.sales.cashjournal.counts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalCount;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodDetail;
import com.comerzzia.omnichannel.facade.model.payments.StorePosPaymentMethods;
import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.omnichannel.facade.service.store.StorePosPaymentMethodServiceFacade;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.normal.ActionButtonNormalComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.paymentmethod.ActionButtonPaymentMethodComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.components.buttonsgroup.paymentmethod.PaymentMethodButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.CashJournalSession;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.cashjournal.CashJournalAuditEventBuilder;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.config.Variables;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class CountControllerAbstract extends SceneController implements Initializable, ButtonsGroupController {
    protected CashJournalSession cashJournalSession;

    //  - mediopagoSeleccionado
    protected PaymentMethodDetail selectedPaymentMethod;
    
    //  - botonera de medios de pago
    protected ButtonsGroupComponent paymentMethodsButtonGroup;
    //	- botonera de otros medios de pago;
    protected ButtonsGroupComponent paymentMethodsCashButtonGroup;
    //  - botonera de importes
    protected ButtonsGroupComponent amountsButtonGroup;
    //  - botonera de tarjetas
    protected ButtonsGroupComponent cardsButtonGroup;
    //  - botonera de acciones de tabla
    protected ButtonsGroupComponent tableActionsButtonGroup;
    
    // Componentes
    @FXML
    protected VBox vbTopButtonsPanel, vbBotButtonsPanel, vbOtherPayments, vbGeneral, vbOtherButtons;
 
    @FXML
    protected NumericTextField tfQuantity, tfAmount;

    @FXML
    protected NumericKeypad numericKeyboard;

    @FXML
    protected Button btAccept, btCancel;
    
    @FXML
    protected WebView wvDataTable;
    
    @FXML
    protected Label lbTotalCount, lbPaymentMethod;
    
    protected CashCountForm frCashCount;
    
    @Autowired
    protected Session session;
    
    @Autowired
	protected StorePosPaymentMethodServiceFacade storePosPaymentMethodService;
    	
	public RetailBasketManager basketManager;
		
	@Autowired
	protected ComerzziaTenantResolver tenantResolver;
	
	@Autowired
	protected ModelMapper modelMapper;
	
	protected List<CashJournalCountRow> countLines;
	
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Creación e inicialización">
    /**
     * Inicializa el componente tras su creación. No hay acceso al application
     * desde este método.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log.trace("initialize() - Inicializando pantalla...");
        
        countLines = new ArrayList<>();
        
		vbOtherPayments.setVisible(false);
		vbOtherPayments.setManaged(false);
		
        frCashCount = SpringContext.getBean(CashCountForm.class);
        frCashCount.setFormField("amount", tfAmount);
        
    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
        try {
            cashJournalSession = session.getCashJournalSession();
            
            // transform records to row
            countLines = cashJournalSession.findCashJournalCount().stream().map(r -> {
            	CashJournalCountRow row = modelMapper.map(r, CashJournalCountRow.class);
            	row.setPaymentMethod(session.getApplicationSession().getPaymentMethodsMap().get(r.getPaymentMethodCode()));
            	return row;
            }).collect(Collectors.toList());
            
            
            selectedPaymentMethod = session.getApplicationSession().getPaymentMethods().getDefaultPaymentMethod();
            Devices.openCashDrawer();
            screenDataRefresh();
        }
        catch (Exception e) {
            log.error("onSceneOpen() - Error inesperado inicializando formulario. ", e);
            throw new InitializeGuiException(e);
        }
        
    }

    @Override
    public void initializeFocus() {
        tfAmount.requestFocus();
    }

    @Override
    public void initializeComponents() {
    	log.debug("initializeComponents() - Cargando medios de pago");
    	
    	wvDataTable.setFocusTraversable(false);
    	vbTopButtonsPanel.setFocusTraversable(false);
    	selectedPaymentMethod = session.getApplicationSession().getPaymentMethods().getDefaultPaymentMethod();
    	log.debug("initializeComponents() - Inicialización de componentes");
    	try {
    		loadTopButtonsPanel();
    		loadBotButtonsPanel();
    		loadOtherButtonsPanel();
    	} catch (LoadWindowException e) {
    		log.error("Error cargando botoneras");
    	}
    	
    	
    	addSelectedAllFields();
    	
    	addKeyEventHandler(new EventHandler<KeyEvent>(){
    		
    		@Override
    		public void handle(KeyEvent event) {
    			if (event.getCode() == KeyCode.MULTIPLY) {
    				if (tfQuantity.isFocused()) {
    					tfAmount.requestFocus();
    					tfAmount.selectAll();
    				}
    			}
    		}
    	}, KeyEvent.KEY_RELEASED);
    	
    	tfQuantity.focusedProperty().addListener(new ChangeListener<Boolean>(){
    		
    		@Override
    		public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
    			if (oldValue) {
    				tfQuantity.setText(tfQuantity.getText().replace("*", ""));
    				if (tfQuantity.getText().isEmpty()) {
    					tfQuantity.setText(FormatUtils.getInstance().formatNumber(BigDecimal.ONE, 0));
    				}else {
    					BigDecimal number = FormatUtils.getInstance().parseBigDecimal(tfQuantity.getText());
    					tfQuantity.setText(FormatUtils.getInstance().formatNumber(number));
    				}
    			}
    		}
    	});
    }

    protected void loadOtherButtonsPanel() {
		try {
			List<ButtonConfigurationBean> paymentMethodActionsList = new LinkedList<>();
			
			// create buttons for active and non automatic count payments methods
			session.getApplicationSession().getPaymentMethods()
			                               .getCashPaymentsMethods()
			                               .stream()
			                               .filter(paymentMethod -> paymentMethod.getActive() && !paymentMethod.getAutoCashJournalCount())
			                               .forEach(paymentMethod -> {
			                            	   createPaymentMethodButton(paymentMethodActionsList, paymentMethod);
			                               	});
			

			paymentMethodsCashButtonGroup = new ButtonsGroupComponent(9, 1, this, paymentMethodActionsList, null, vbOtherButtons.getPrefHeight(), ActionButtonPaymentMethodComponent.class.getName());
			vbOtherButtons.getChildren().add(paymentMethodsCashButtonGroup);			
		}
		catch (LoadWindowException e) {
			log.error("loadOtherButtonsPanel() - Error: " + e.getMessage(), e);
		}
	}
	
    protected void createPaymentMethodButton(List<ButtonConfigurationBean> paymentMethodActionsList, PaymentMethodDetail paymentMethod) {
		PaymentMethodButtonConfigurationBean cfg = new PaymentMethodButtonConfigurationBean(null, paymentMethod.getPaymentMethodDes(), null, "ACCION_SELECIONAR_MEDIO_PAGO", "", paymentMethod, false);
		paymentMethodActionsList.add(cfg);
	}

	// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Funciones relacionadas con interfaz GUI y manejo de pantalla">
    /**
     * Función que refresca los totales en pantalla
     */
    public void screenDataRefresh() {
    	updateButtonsPaymentMethods();
    	tfAmount.clear();
    	updateTotalCount();
        log.debug("screenDataRefresh() - Cargando la vista web de la pantalla - loadWebViewPrincipal()");
        loadWebViewPrincipal();
    }
    
	protected void updateButtonsPaymentMethods() {
		lbPaymentMethod.setText(selectedPaymentMethod.getPaymentMethodDes());
		ButtonsGroupComponent buttonsPanel = (ButtonsGroupComponent) vbTopButtonsPanel.getChildren().get(0);
		boolean isOther = true;

		for (Button button : buttonsPanel.getButtonsList()) {
			if (button.getUserData() != null && button.getUserData() instanceof ButtonConfigurationBean) {
				try {
					button.getStyleClass().remove("button-selected");

					String paymentCode = ((ButtonConfigurationBean) button.getUserData()).getParam("paymentMethodCode");
					if (paymentCode == null) {
						paymentCode = ((ButtonConfigurationBean) button.getUserData()).getParam("codMedioPago");
					}
					if (paymentCode.equals(selectedPaymentMethod.getPaymentMethodCode())) {
						button.getStyleClass().add("button-selected");
						isOther = false;
					}
				}
				catch (NullPointerException ignore) {
					if (isOther) {
						button.getStyleClass().add("button-selected");
					}
				}
			}
		}
	}

	/**
     * Accion anotar una nueva linea de recuento a la caja
     * 
     */
    public void actionNoteCashJournalCountLine() {
        log.trace("actionNoteCashJournalCountLine() - Anotamos línea de recuento...");
        
        frCashCount.setAmount(tfAmount.getText());

        if(validateCountData()){
        	if (tfQuantity.getText().isEmpty()) {
        		tfQuantity.setText("1");
        	}

	    	BigDecimal amount = FormatUtils.getInstance().parseAmount(tfAmount.getText());
	    	
	    	// invalid number
	    	if (amount == null) return;
	    	
	    	amount = amount.setScale(2, RoundingMode.HALF_UP);
	    	actionNoteCount(amount, tfQuantity.getText());
	    	tfQuantity.setText("1");
        }
    }
    public void actionNoteCount(BigDecimal amount, String quantity) {
        log.debug("actionNoteCount() - Medio de pago: " + selectedPaymentMethod + " // Cantidad: " + quantity + " // Importe: " + amount);
                     
        if (selectedPaymentMethod == null) {
        	DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No hay ninguna forma de pago seleccionada."));
            return;
        }
        newCountLine(selectedPaymentMethod.getPaymentMethodCode(), amount.abs(), Math.abs(Integer.parseInt(quantity)));
        screenDataRefresh();
        tfAmount.requestFocus();

    }

    /**
     * Acción aceptar
     */
    @FXML
    public void actionAccept() {
    	// map to CashJournalCount
    	cashJournalSession.saveCashJournalCount( 
    			  countLines.stream()
    	          .map(r -> modelMapper.map(r, CashJournalCount.class))
    	          .collect(Collectors.toList()));


		closeSuccess();
    }

    /**
     * Acción cancelar
     */
	@FXML
    public void actionCancel() {
        	boolean confirmation = true;

    		if (!CollectionUtils.isEmpty(countLines)) {
    			confirmation = DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Al salir de la pantalla se perderá el recuento en curso."), I18N.getText("Salir de todos modos"), I18N.getText("Continuar"));
    		}
    		
        	auditOperation(new CashJournalAuditEventBuilder(cashJournalSession.getOpenedCashJournal()).addOperation("cashCountCancel"));
    		
    		if (confirmation) {
                closeCancel();
    		}
    }

    /**
     * Acción evento de teclado sobre campo importe
     *
     * @param event
     */
    public void actionTfAmountIntro(KeyEvent event) {
        if (event.getCode() == KeyCode.MULTIPLY) {
        	tfAmount.setText(tfAmount.getText().replace("*", ""));
            tfQuantity.requestFocus();
        }
        else if (event.getCode() == KeyCode.ENTER) {
        	actionNoteCashJournalCountLine();
        }
    }
    
    public void actionTfQuantityIntro(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
           tfAmount.requestFocus();
        }
    }
    
	/**
	 * Preparamos la interfaz para una modificación de la cantidad
	 */
	public void changeQuantity() {
		log.debug("changeQuantity() - preparamos la interfaz para una modificación de la cantidad");
		tfAmount.clear();
	}
    
    /**
     * Oculta el panel numérico
     */
	protected void hideNumericPanel() {
        if (!Variables.MODO_PAD_NUMERICO) {
            log.debug("hideNumericPanel() - PAD Numerico off");
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AccionesMenu">

    /**
     * Método de control de acciones de página de recuento
     *
     * @param pressedButton botón pulsado
     */
    @Override
    public void executeAction(ActionButtonComponent pressedButton) {
        log.debug("executeAction() - Realizando la acción : " + pressedButton.getClave() + " de tipo : " + pressedButton.getTipo());
        switch (pressedButton.getClave()) {
            case "ACCION_SELECIONAR_MEDIO_PAGO":
        	    log.debug("Acción cambiar medio de pago en pantalla");
        	    ActionButtonPaymentMethodComponent button = (ActionButtonPaymentMethodComponent) pressedButton;
        	    selectedPaymentMethod = button.getPaymentMethod();
        	    //lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
        	    if (vbOtherButtons.isVisible())
        	    	switchLateralVBox();
        	    tfAmount.requestFocus();
        	    screenDataRefresh();
    			break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                log.debug("Acción seleccionar último registro de la tabla");
                actionTableEventDelete("");
                break;
            default:
                log.error("No se ha especificado acción en pantalla para la operación :" + pressedButton.getClave());
                
        }
    }


	/**
     * Valida los campos editables
     *
     * @return
     */
	protected boolean validateCountData() {
		log.debug("validarFormularioDatosFiltro()");

		boolean valid;

		Set<ConstraintViolation<CashCountForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frCashCount);
		valid = !(constraintViolations.size() >= 1);
		return valid;
	}
	
	
	/**
	 * Añade a los campos de texto de la pantalla la capacidad de seleccionar todo su texto cuando adquieren el foco
	 */
	protected void addSelectedAllFields() {
		//addSeleccionarTodoEnFoco(tfCantidad);
		addSelectAllOnFocus(tfAmount);
    }

	/**
	 * Método auxuliar para añadir a un campo de texto la capacidad de seleccionar todo su texto cuando adquiere el foco
	 * @param field
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected void addSelectAllOnFocus(final TextField field) {
		field.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
			public void changed(ObservableValue ov, Boolean t, Boolean t1) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (field.isFocused() && !field.getText().isEmpty()) {
							field.selectAll();
						}
					}
				});
			}
		});
    }
	
	
	protected void loadWebViewPrincipal() {
		Map<String, Object> params = new HashMap<String, Object>();
		
		
		params.put("countLines", countLines);
		
		loadWebView(getWebViewPath(), params, wvDataTable);
	}
	
	protected String getWebViewPath() {
		return "count/count";
	}
	
	/*
	 * Sets the top buttons panel to change payment type
	 * */
	protected void loadTopButtonsPanel() throws LoadWindowException {
		try {
			log.debug("loadTopButtonsPanel() - loading top buttons pannel");
			ButtonsGroupModel panelBotoneraBean = getSceneView().loadButtonsGroup("_top_buttons_panel.xml");
			StorePosPaymentMethods pmtMethods = storePosPaymentMethodService.getPaymentMethodsFromCache(
					applicationSession.getStorePosBusinessData().getWarehouse().getWhCode(),
					applicationSession.getTillCode());
			panelBotoneraBean.getButtonsRow().stream().forEach(br -> {
				List<ButtonConfigurationBean> btrowremove = br.getButtonRow().stream().filter(bt -> {
					if (bt.getParams() == null) {
						return false;
					}

					String paymentMethodCode = bt.getParam("paymentMethodCode");
					if (paymentMethodCode == null) {
						paymentMethodCode = bt.getParam("codMedioPago");
					}
					PaymentMethodDetail pmtMethod = pmtMethods.getPaymentsMethods().get(paymentMethodCode);
					return pmtMethod != null && pmtMethod.getAutoCashJournalCount();
				}).collect(Collectors.toList());
				br.getButtonRow().removeAll(btrowremove);
			});

			ButtonsGroupComponent botonera = new ButtonsGroupComponent(panelBotoneraBean, vbTopButtonsPanel.getPrefWidth(), vbTopButtonsPanel.getPrefHeight(), this, ActionButtonNormalComponent.class);
			vbTopButtonsPanel.getChildren().clear();
			vbTopButtonsPanel.getChildren().add(botonera);
		}
		catch (InitializeGuiException e) {
			log.error("loadTopButtonsPanel() - Error al crear botonera: " + e.getMessage(), e);
		}
	}

	protected void loadBotButtonsPanel() throws LoadWindowException {
		try {
			log.debug("loadBotButtonsPanel() - loading bot buttons pannel");
			ButtonsGroupModel panelBotoneraBean = getSceneView().loadButtonsGroup("_bot_buttons_panel.xml");
			ButtonsGroupComponent botonera = new ButtonsGroupComponent(panelBotoneraBean, vbBotButtonsPanel.getPrefWidth(), vbBotButtonsPanel.getPrefHeight(), this, ActionButtonNormalComponent.class);
			vbBotButtonsPanel.getChildren().clear();
			vbBotButtonsPanel.getChildren().add(botonera);
		}
		catch (InitializeGuiException ignore) {}
	}
	
	/*
	 * Gets the Intro key activation to launch newCountLine() function
	 * */
	
	public void actionTfIntroKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			actionNoteCashJournalCountLine();
		}
	}
	
	@Override
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if("deleteItem".equals(method)) {
			log.debug("Entrando en deleteItem handler");
			if(params.containsKey("lineId")) {
				String param = params.get("lineId");
				Integer lineId = Integer.parseInt(param);
				CashJournalCount cajaRecuento = countLines.get(lineId.intValue());
				removeCountLine(cajaRecuento);
				tfAmount.requestFocus();
				screenDataRefresh();
			} else {
				log.debug("addUrlHandlersItemsOperations() - deleteItem: No se ha encontrado la clave propuesta en el mapa de parámetros");
			}
		}
	}
	
	public void selectPaymentMethod(HashMap<String, String> params) {
    	try {
    		if(!params.containsKey("paymentMethodCode") && !params.containsKey("codMedioPago")){
    			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se ha especificado una acción correcta para este botón"));
	        	log.error("No existe el código del medio de pago para este botón.");
	        	return;
    		}
    		String paymentMethodCode = params.get("paymentMethodCode");
			if (paymentMethodCode == null) {
				paymentMethodCode = params.get("codMedioPago");
			}
    		
    		PaymentMethodDetail paymentMethod = session.getApplicationSession().getPaymentMethodsMap().get(paymentMethodCode);
    		
    		if(paymentMethod == null) {
    			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha habido un error al recuperar el medio de pago"));
	            log.error("No se ha encontrado el medio de pago con código: " + paymentMethodCode);
	            return;
    		} 
    		selectedPaymentMethod = paymentMethod;
    		screenDataRefresh();
    		updateButtonsPaymentMethods();
    	}
    	catch (Exception e) {
    		selectDefaultPaymentMethod();
    		throw e;
		}
	}
	
	protected void selectDefaultPaymentMethod() {
		selectedPaymentMethod = session.getApplicationSession().getPaymentMethods().getDefaultPaymentMethod();
		tfAmount.requestFocus();
	}

	@FXML
	public void switchLateralVBox() {

		Boolean visibleVBox = vbOtherPayments.isVisible();

		vbOtherPayments.setVisible(!visibleVBox);
		vbOtherPayments.setManaged(!visibleVBox);
		vbGeneral.setVisible(visibleVBox);
		vbGeneral.setManaged(visibleVBox);
		
		if (vbGeneral.isVisible()) {
			updateButtonsPaymentMethods();
		}
	}
	
	protected void updateTotalCount() {
		BigDecimal total = BigDecimal.ZERO;
		for(CashJournalCount countLine : countLines) {
			total = total.add(countLine.getTotal());
		}
		lbTotalCount.setText(FormatUtils.getInstance().formatAmount(total));
	}
	
	protected void selectBillAmount(HashMap<String, String> params) {	
		tfAmount.setText(params.get("importeEfectivo"));
		tfQuantity.requestFocus();
		
	}
	
	public void removeCountLine(CashJournalCount countLine) {
		countLines.remove(countLine);
	}
	
	public void newCountLine(String code, BigDecimal amount, Integer quantity) {
        log.debug("nuevaLineaRecuento() - Creando nueva línea de recuento...");
        CashJournalCountRow countLine = new CashJournalCountRow();
        countLine.setPaymentMethodCode(code);
        countLine.setPaymentMethod(session.getApplicationSession().getPaymentMethodsMap().get(code));
        countLine.setQuantity(quantity);
        countLine.setCountValue(amount);
        addCountLine(countLine);
    }
	
	public void addCountLine(CashJournalCountRow countLine) {
		CashJournalCountRow existingCountLine = null;
		for (CashJournalCountRow currenCountLine : countLines) {
			if (currenCountLine.getPaymentMethodCode().equals(countLine.getPaymentMethodCode()) && 
				BigDecimalUtil.isEquals(currenCountLine.getCountValue(), countLine.getCountValue()) && 
				currenCountLine.getPaymentMethod().getCashPayment()) {
				existingCountLine = currenCountLine;
			}
		}
		if (existingCountLine != null) {
			existingCountLine.setQuantity(existingCountLine.getQuantity() + countLine.getQuantity());
		}
		else {
			countLines.add(countLine);
		}
	}
	
}
