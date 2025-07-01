package com.comerzzia.pos.gui.sales.basket;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.core.facade.model.DocTypeDetail;
import com.comerzzia.core.facade.service.doctype.DocTypeServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScannerDataReaded;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.ApplicationSession;
import com.comerzzia.pos.core.services.session.CashJournalSession;
import com.comerzzia.pos.gui.sales.retailrefund.RetailRefundController;
import com.comerzzia.pos.gui.sales.retailrefund.RetailRefundBasketItemizationController;
import com.comerzzia.pos.gui.sales.retailrefund.SearchOriginDocumentFormValidationBean;
import com.comerzzia.pos.gui.sales.retailrefund.documenttype.DocumentTypeController;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public abstract class RefundControllerAbstract extends ActionSceneController{
	public static final String PARAM_REFUND_SEARCH_DATA = "PARAM_REFUND_SEARCH_DATA";

	protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RetailRefundController.class.getName());
    
    final DeviceLineDisplay visor = Devices.getInstance().getLineDisplay();
    
    @Autowired
    protected ApplicationSession applicationSession;
    @Autowired
    protected CashJournalSession cashJournalSession;
    
    @Autowired
    protected VariableServiceFacade variablesServices;
    @Autowired
	protected DocTypeServiceFacade docTypeService;
    
    @FXML
    protected TextField tfOperation, tfCodDoc, tfDesDoc;
    
    @FXML
    protected Label lbErrorMessage;
    
    @FXML
    protected Button btAccept, btDoc;
    
    protected SearchOriginDocumentFormValidationBean frConsultTicket;
    
    protected ScannerObserver scannerObserver;
    
    protected List<String> refundDocTypes;
    
    public class ScannerObserver implements Observer {

		@Override
		public void update(Observable o, Object arg) {
			if (arg instanceof DeviceScannerDataReaded && tfOperation.isFocused() && !tfOperation.isDisabled()) {
				DeviceScannerDataReaded scanData = (DeviceScannerDataReaded)arg;
				
				Platform.runLater(new Runnable(){
					@Override
					public void run() {
						tfOperation.setText(scanData.getScanData());
						actionAccept();
					}
				});		
			}
			
		}
		
	}
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        frConsultTicket = SpringContext.getBean(SearchOriginDocumentFormValidationBean.class);

        frConsultTicket.setFormField("codOperacion", tfOperation);
        frConsultTicket.setFormField("tipoDoc", tfCodDoc);
        
        scannerObserver = new ScannerObserver();
		
		Devices.getInstance().getScanner().addObserver(scannerObserver);
    }

    @Override
    public void initializeComponents() {
        
        tfCodDoc.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if(oldValue){
                    procesarTipoDoc();
                }
            }
        });
		addSelectAllOnFocus(tfOperation);
		addSelectAllOnFocus(tfCodDoc);
    }
    
//    protected void initializeManager() {
//    	if(basketManager == null) {
//    		basketManager = BasketManagerBuilder.build(RetailRefundBasketManager.class, applicationSession.getStorePosBusinessData());
//    		basketManager.setPersistable(false);
//    	}
//    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
	    
    	checkScreenOpening();


    	visor.writeLineUp(I18N.getText("--NUEVA DEVOLUCION--"));
    	
        tfOperation.setText("");
        
        btAccept.setDisable(false);

        lbErrorMessage.setText("");
    }

    /**
	 * Realiza las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
	 * @throws InitializeGuiException
	 */
	protected void checkScreenOpening() throws InitializeGuiException {
	    if (!cashJournalSession.isOpenedCashJournal()) {
	    	Boolean automaticOpening = variablesServices.getVariableAsBoolean(VariableServiceFacade.CAJA_APERTURA_AUTOMATICA, true);
	    	if(automaticOpening){
	    		DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(I18N.getText("No hay caja abierta. Se abrirá automáticamente."));
	    		cashJournalSession.openAutomaticCashJournal();
	    	}else{
	    		DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No hay caja abierta. Deberá ir a la gestión de caja para abrirla."));
	    		throw new InitializeGuiException(false);
	    	}
	    }
	    
	    if(!cashJournalSession.checkCashJournalClosingMandatory()){
	    	String fechaCaja = FormatUtils.getInstance().formatDate(cashJournalSession.getOpeningDate());
	    	String fechaActual = FormatUtils.getInstance().formatDate(new Date());
	    	DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual));
	    	throw new InitializeGuiException(false);
	    }
    }
    
    @Override
    public void initializeFocus() {
        tfOperation.requestFocus();
    }

    @FXML
    public void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !btAccept.isDisable()) {
        	actionAccept();
        }
    }
    
    @FXML
    public void actionAccept(){
        lbErrorMessage.setText("");
        if(validateFormSearchDocument()){
            String code = frConsultTicket.getCodOperation();
            String codDoc = frConsultTicket.getCodDoc();
            DocTypeDetail docType = applicationSession.getDocTypeByDocTypeCode(codDoc);
            sceneData.put(PARAM_REFUND_SEARCH_DATA, new RefundSearchData(code, docType));
            openActionScene(getAction().getActionId(), RetailRefundBasketItemizationController.class);
        }
    }
    
    public class RefundSearchData {
    	protected String code;
    	protected DocTypeDetail docType;
    	public RefundSearchData(String code, DocTypeDetail docType) {
    		this.code = code;
    		this.docType = docType;
    	}
    	
    	public String getCode() {
    		return code;
    	}
    	
    	public DocTypeDetail getDocType() {
    		return docType;
    	}
    }
    
//    public class RecoveryRefundTicket extends BackgroundTask<Boolean>{
//    	protected String code;
//    	protected Long typeDocId;
//    	
//		public RecoveryRefundTicket(String codigo,Long idTipoDoc) {
//			this.code = codigo;
//            this.typeDocId = idTipoDoc;
//		}
//
//		@Override
//		protected Boolean execute() throws Exception {
//			return basketManager.loadRefundOriginalSaleDoc(code, typeDocId);
//		}
//
//		@Override
//		protected void failed() {
//			super.failed();
//			log.error("RecoveryRefundTicket() - failed: ", getException());
//			if(getException() instanceof BusinessException){
//				DialogWindowComponent.openErrorWindow(getStage(), getCMZException().getMessage(), getCMZException());
//			}else{
//				DialogWindowComponent.openErrorWindow(getStage(), I18N.getText("Lo sentimos, ha ocurrido un error."), getException());
//			}
//		}
//
//		@Override
//		protected void succeeded() {
//			super.succeeded();
//			boolean res = getValue();
//			recoveryRefundTicketSucceeded(res);
//		}
//    	
//    }
    
//    protected void recoveryRefundTicketSucceeded(boolean encontrado) {
//		if (encontrado) {
//			boolean esMismoTratamientoFiscal = basketManager.validateFiscalData();
//			if(!esMismoTratamientoFiscal) {
//				try {
//					basketManager.cancelBasket();
//				} catch (Exception e) {
//					log.error("recoveryRefundTicketSucceeded() - Ha habido un error al eliminar los tickets: " + e.getMessage(), e);
//				}
//				
//				lbErrorMessage.setText(I18N.getText("El ticket fue realizando en una tienda con un tratamiento fiscal diferente al de esta tienda. No se puede realizar esta devolución.")); 
//				return;
//			}
//			else {
//				sceneData.put(RetailBasketItemizationController.BASKET_KEY, basketManager);
//				boolean recoveredOnline = basketManager.getRefundData().getOnlineRecovered();
//				if(!recoveredOnline) {
//					DialogWindowComponent.openWarnWindow(I18N.getText("No se han podido recuperar las líneas devueltas desde la central. Por favor, compruebe el ticket impreso para ver las líneas ya devueltas."), getStage());
//				}
//				openActionScene(getAction().getActionId(), RetailRefundBasketItemizationController.class);
//			}
//			
//		} else {
//			lbErrorMessage.setText(I18N.getText("No se ha encontrado ningún ticket con esos datos"));
//		}
//    }

	protected boolean validateFormSearchDocument() {
		boolean valid;

		// Limpiamos los errores que pudiese tener el formulario
		frConsultTicket.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbErrorMessage.setText("");

		frConsultTicket.setCodOperation(tfOperation.getText());
		frConsultTicket.setCodDoc(tfCodDoc.getText());

		// Validamos el formulario de login
		Set<ConstraintViolation<SearchOriginDocumentFormValidationBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frConsultTicket);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<SearchOriginDocumentFormValidationBean> next = constraintViolations.iterator().next();
			frConsultTicket.setErrorStyle(next.getPropertyPath(), true);
			frConsultTicket.setFocus(next.getPropertyPath());
			lbErrorMessage.setText(next.getMessage());
			valid = false;
		} else {
			valid = true;
		}

		return valid;
	}
    
    @FXML
    public void actionFindDocumentType(){
    	sceneData = new HashMap<String, Object>();
    	
        sceneData.put(DocumentTypeController.PARAM_DOC_POSSIBLE_INPUT, applicationSession.getValidOriginDocTypesListForType(getDefaultDocumentType().getDocTypeCode()));

        openScene(DocumentTypeController.class, new SceneCallback<DocTypeDetail>() {
			
			@Override
			public void onSuccess(DocTypeDetail o) {
				tfCodDoc.setText(o.getDocTypeCode());
				tfDesDoc.setText(o.getDocTypeDes());
			}
		});
    }
    
    //TODO: Abstract
    protected String getDefaultDocumentNature() {
    	//TODO: From basketmanager?? (BasketManager is not initialized yet)
    	return DocTypeServiceFacade.NATURE_RETAIL_REFUND_DOCUMENT;
	}
    protected DocTypeDetail getDefaultDocumentType() {
    	return docTypeService.getCountryDocTypeForNatureDocumentType(applicationSession.getStorePosBusinessData().getCompany().getCountryCode(), getDefaultDocumentNature());
	}
    
    protected void procesarTipoDoc(){
        String codDoc = tfCodDoc.getText();
        
        if(!codDoc.trim().isEmpty()){
            try {
                DocTypeDetail document = applicationSession.getDocTypeByDocTypeCode(codDoc);
                if(!applicationSession.getDocumentTypeCodes().contains(document.getDocTypeCode())){
                    log.warn("Se seleccionó un tipo de documento no válido para la devolución.");
                    DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("El documento seleccionado no es válido."));
                    tfCodDoc.setText("");
                    tfDesDoc.setText("");
                }
                else{
                    tfCodDoc.setText(document.getDocTypeCode());
                    tfDesDoc.setText(document.getDocTypeDes());
                }
            }
            catch(NumberFormatException nfe){
                log.error("El id de documento introducido no es válido.", nfe);
                DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("El id introducido no es válido."));
                tfCodDoc.setText("");
                tfDesDoc.setText("");
            }
        }
        else{
            tfDesDoc.setText("");
            tfCodDoc.setText("");
        }
    }
    
    @Override
    public boolean canClose() {
		visor.writeLineUp(I18N.getText("---CAJA CERRADA---"));
		visor.standbyMode();
    	return super.canClose();
    }
    
    @SuppressWarnings("rawtypes")
	protected void addSelectAllOnFocus(final TextField field) {
		field.focusedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue ov, Boolean t, Boolean t1) {
				Platform.runLater(new Runnable(){

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
    
    @Override
    protected void executeLongOperations() {
//    	initializeManager();
    	
    	if(refundDocTypes==null) {
    		refundDocTypes = applicationSession.getValidOriginDocTypesListForType(getDefaultDocumentType().getDocTypeCode());
    	}
    }
    
    @Override
    protected void succededLongOperations() {
    	if (refundDocTypes == null || refundDocTypes.isEmpty()) {
    		DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No está configurado el tipo de documento nota de crédito en el entorno."));
			btAccept.setDisable(true);
			return;
		}
        DocTypeDetail refundDocType = applicationSession.getDocTypeByDocTypeCode(refundDocTypes.get(0));
        tfCodDoc.setText(refundDocType.getDocTypeCode());
        tfDesDoc.setText(refundDocType.getDocTypeDes());
    }
}
