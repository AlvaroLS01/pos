package com.comerzzia.pos.gui.sales.document;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.facade.model.DocTypeDetail;
import com.comerzzia.core.facade.service.doctype.DocTypeServiceFacade;
import com.comerzzia.omnichannel.facade.model.documents.TicketDocument;
import com.comerzzia.omnichannel.facade.service.documents.TicketDocumentServiceFacade;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScannerDataReaded;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.document.view.DocumentViewerController;
import com.comerzzia.pos.gui.sales.retailrefund.documenttype.DocumentTypeController;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;

@Component
@CzzActionScene
public class DocumentManagementController extends ActionSceneController implements Initializable {

    public static final String PARAM_DOC_TYPE = "DOC_TYPE";
    
    private static final Logger log = Logger.getLogger(DocumentManagementController.class.getName());
    
    @Autowired
    protected Session session;
        
    @FXML
    protected DatePicker tfDate;

    @FXML
    protected TextField tfDocumentNumber, tfTillCode, tfDocTypeCode, tfDocTypeDes, tfLocator;
        
    @FXML
	protected WebView wvPrincipal;

    @FXML
    protected Button btSearchDocType, btSearch;

    @FXML
    protected Label lbError;
    
    protected Map<String, Object> params = new HashMap<String, Object>();

    protected ObservableList<DocumentManagementDto> ticketDocuments;
    protected ButtonsGroupComponent buttonsGroupComponent;

    //Formulario de validación
    protected DocumentManagementForm frDocumentManagement;
    
    protected List<String> validDocTypeCodes;
    protected List<Long> validDocTypeIds;
    
    @Autowired
    protected TicketDocumentServiceFacade ticketDocumentService;
    
    @Autowired
    protected DocTypeServiceFacade documents;
	
    protected final DeviceLineDisplay lineDisplay = Devices.getInstance().getLineDisplay();
    protected ScannerObserver scannerObserver;
    
    public class ScannerObserver implements Observer {

		@Override
		public void update(Observable o, Object arg) {
			if (arg instanceof DeviceScannerDataReaded && tfLocator.isFocused() && !tfLocator.isDisabled()) {
				DeviceScannerDataReaded scanData = (DeviceScannerDataReaded)arg;
				
				
				Platform.runLater(new Runnable(){
					@Override
					public void run() {
						tfLocator.setText(scanData.getScanData());
						actionSearch();
					}
				});		
			}
			
		}
		
	}

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    	tfDocumentNumber.requestFocus();
    	
    	
        //Creamos el validador de los datos introducidos del ticket
        frDocumentManagement = SpringContext.getBean(DocumentManagementForm.class);
        frDocumentManagement.setFormField("tillCode", tfTillCode);
        frDocumentManagement.setFormField("documentNumber", tfDocumentNumber);
        frDocumentManagement.setFormField("date", tfDate);
        frDocumentManagement.setFormField("docTypeCode", tfDocTypeCode);
        
        scannerObserver = new ScannerObserver();
		
		Devices.getInstance().getScanner().addObserver(scannerObserver);
        
    }

    @Override
    public void initializeComponents() {

        try {
            log.debug("initializeComponents() - Inicialización de componentes");

            tfDocTypeCode.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if(oldValue){
                    processDocType();
                }
            }});
            
            loadActionDatePicker();
            
        }catch (Exception ex) {
            log.error("initializeComponents() - Error inicializando pantalla de gestion de tickets");
            DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog("Error cargando pantalla. Para mas información consulte el log.");
        }

    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
    	lineDisplay.clear();
    	
        // Por defecto la caja será la caja configurada
        tfTillCode.setText(session.getApplicationSession().getTillCode());
        lbError.setText("");
        tfDate.setSelectedDate(new Date());
        
        if(sceneData.containsKey(PARAM_DOC_TYPE)){
            
            String docTypeCode = (String)sceneData.get(PARAM_DOC_TYPE);
            
            DocTypeDetail docType = session.getApplicationSession().getDocTypeByDocTypeCode(docTypeCode);
            tfDocTypeCode.setText(docType.getDocTypeCode());
            
            tfDocTypeDes.setText(StringUtils.capitalize(docType.getDocTypeDes()));
        }
        else{
            tfDocTypeCode.setText("");
            tfDocTypeDes.setText("");
        }
        
//      TODO: Esto no deberia comprobarse aqui, sino al lanzar la impresion
    	List<DocTypeDetail> docTypes = session.getApplicationSession().getDocumentsTypes();
    	validDocTypeCodes = new ArrayList<String>();
    	validDocTypeIds = new ArrayList<>();
    	for(DocTypeDetail docType : docTypes){
            if(!docType.getFormatoImpresion().equals(DocTypeServiceFacade.PROPIEDAD_FORMATO_IMPRESION_NO_CONFIGURADO)){
    			validDocTypeCodes.add(docType.getDocTypeCode());
    			validDocTypeIds.add(docType.getDocTypeId());
    		}
    	}

        refreshScreenData();
        loadMainWebView();
    }

    @Override
    public void initializeFocus() {
        tfLocator.requestFocus();
    }


    public void refreshScreenData() {
        ticketDocuments = FXCollections.observableList(new ArrayList<DocumentManagementDto>());
    }

    @FXML
    public void actionSearchEnter(KeyEvent event) {
        log.trace("actionSearchEnter()");
        
        if (event.getCode() == KeyCode.ENTER) {
            actionSearch();
        }
    }
    
    public void actionSearch() {
        log.trace("actionSearch()");
        
//        TODO La lógica de buscar por localizador está rara. Pdte mejor definición
        if(tfLocator.getText().length()>0){
        	log.debug("actionSearch()");
        	
    		try {
    			ticketDocuments.clear();
    			String locator = tfLocator.getText();
    			log.debug("actionSearch() - Realizando búsqueda con localizador = "+locator);
    			
    			
    			
    			List<DocTypeDetail> docTypes = session.getApplicationSession().getDocumentsTypes();

    			for(Long docTypeId : validDocTypeIds) {
    				TicketDocument document = null;
    				
    				try {
    					document = ticketDocumentService.findByLocatorId(docTypeId, locator);
    				}catch (NoSuchElementException e) {}  
    				
    				if(document != null) {
    					DocumentManagementDto documentRow = new DocumentManagementDto(document);
    					ticketDocuments.add(documentRow);
    				}
    							
    			}

    			if(ticketDocuments.isEmpty()){
    				tfLocator.requestFocus();
		    		tfLocator.selectAll();
		    		lbError.setText("No se encontraron resultados.");
    			}
    			else{
                	for (DocumentManagementDto documentRow : ticketDocuments) {
                		for(DocTypeDetail docType : docTypes) {
                			if(documentRow.getDocTypeId().equals(docType.getDocTypeId())) {
                				documentRow.setDocTypeDes(docType.getDocTypeCode() + " · " + StringUtils.capitalize(documentRow.getDocTypeDes()));
                			}
                		}
                	}
    				
    				tfLocator.setText("");	
    			}
    			
	        	loadMainWebView();
    			    			
    		} catch (Exception e) {
    			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Se produjo un error procesando el localizador. ")+ e.getMessage());
    			log.error("Error procesando el localizador ", e);
    			tfLocator.requestFocus();
	    		tfLocator.selectAll();
    		}
    		
        }else{
	        frDocumentManagement.setTillCode(tfTillCode.getText());
	        frDocumentManagement.setDocumentNumber(tfDocumentNumber.getText().equals("") ? null : tfDocumentNumber.getText());
	        frDocumentManagement.setDate(tfDate.getTexto());
	        frDocumentManagement.setDocTypeCode(tfDocTypeCode.getText().equals("") ? null : tfDocTypeCode.getText());
	        
	        tfDocumentNumber.deselect();
	        if(validateFormData()){
	        	ticketDocuments.clear();
	            new BuscarTask().start();
	        }
        }
    }
    
    protected class BuscarTask extends BackgroundTask<List<TicketDocument>>{

        @Override
        protected List<TicketDocument> execute() throws Exception {
        	
        	String docTypeCode = frDocumentManagement.getDocTypeCode();
        	Long docTypeId = null;
        	
        	if(docTypeCode != null){
        		docTypeId = session.getApplicationSession().getDocTypeByDocTypeCode(docTypeCode).getDocTypeId();
        	}
        	
        	return ticketDocumentService.findByFilter(session.getApplicationSession().getCodAlmacen(), frDocumentManagement.getTillCode(), frDocumentManagement.getDocumentNumberAsLong(), frDocumentManagement.getDateAsDate(), docTypeId);
        	       
        }

        @Override
        protected void succeeded() {
            //Ordenamos la lista de tickets obtenida por la fecha de los mismos
            List<TicketDocument> documents = getValue();
            
            List<DocTypeDetail> docTypes = session.getApplicationSession().getDocumentsTypes();
         
            if(documents.isEmpty()){	
            	tfDocumentNumber.requestFocus();
            	tfDocumentNumber.selectAll();
            	lbError.setText("No se encontraron resultados.");
            }
            else{
            	DocumentManagementDto documentRow = null;
            	for (TicketDocument document : documents) {
            		for(DocTypeDetail docType : docTypes) {
            			if(document.getDocTypeId().equals(docType.getDocTypeId())) {
            				documentRow = new DocumentManagementDto(document);
            				documentRow.setDocTypeDes(docType.getDocTypeCode() + " · " + StringUtils.capitalize(docType.getDocTypeDes()));
            				ticketDocuments.add(documentRow);
            			}
            		}
            	}
            }
           
        	loadMainWebView();
        	
            super.succeeded();
        }

        @Override
        protected void failed() {
        	DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(getCMZException().getMessage(), getCMZException());
            tfDocumentNumber.requestFocus();
        	tfDocumentNumber.selectAll();
            super.failed();
        }
    }
    
    /**
     * Valida los valores introducidos para buscar el ticket
     *
     * @return
     */
    protected boolean validateFormData() {

        boolean valid;

        // Limpiamos los errores que pudiese tener el formulario
        frDocumentManagement.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        // Validamos el formulario de login
        Set<ConstraintViolation<DocumentManagementForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frDocumentManagement);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<DocumentManagementForm> next = constraintViolations.iterator().next();
            frDocumentManagement.setErrorStyle(next.getPropertyPath(), true);
            frDocumentManagement.setFocus(next.getPropertyPath());
            lbError.setText(next.getMessage());
            valid = false;
        }
        else {
            valid = true;
        }

        return valid;
    }


    @FXML
    public void actionSearchDocType(){
        
        sceneData = new HashMap<>();
        sceneData.put(DocumentTypeController.PARAM_DOC_POSSIBLE_INPUT, validDocTypeCodes);
        openScene(DocumentTypeController.class, new SceneCallback<DocTypeDetail>() {
			
			@Override
			public void onSuccess(DocTypeDetail o) {
				tfDocTypeDes.setText(StringUtils.capitalize(o.getDocTypeDes()));
				tfDocTypeCode.setText(o.getDocTypeCode());
			}
		});
    }
    
    protected void processDocType(){
        
        String docTypeCode = tfDocTypeCode.getText();
        
        if(!docTypeCode.trim().isEmpty()){
            try {
                DocTypeDetail docType = session.getApplicationSession().getDocTypeByDocTypeCode(docTypeCode);
                tfDocTypeCode.setText(docType.getDocTypeCode());
                tfDocTypeDes.setText(StringUtils.capitalize(docType.getDocTypeDes()));
            }
            catch (NotFoundException ex) {
                log.error("Error obteniendo el tipo de documento", ex);
                DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El tipo de documento indicado no existe en la base de datos."));
                tfDocTypeCode.setText("");
                tfDocTypeDes.setText("");
            }
            catch(NumberFormatException nfe){
                log.error("El id de documento introducido no es válido.", nfe);
                DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El id introducido no es válido."));
                tfDocTypeCode.setText("");
                tfDocTypeDes.setText("");
            }
        }
        else{
            tfDocTypeDes.setText("");
        }
    }
    
    protected void loadActionDatePicker() {
    	tfDate.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent action) {
				actionSearch();
			}
		});
    }
    
	protected void loadMainWebView() {
		Map<String, Object> params = new HashMap<>();
		params.put("documents", ticketDocuments);
		loadWebView("sales/documents/documents", params, wvPrincipal);
	}
    
    @Override
    public void onURLMethodCalled(String method, Map<String, String> params) {
    	if("showTicket".equals(method)) {
    		try {					
				String documentUid = params.get("document_uid");
				showDocument(documentUid);
			}
			catch (Exception e) {
				log.error("addUrlHandlersItemsOperations() - Error:" + e.getMessage(), e);
				DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha habido un error al mostrar la página web"), e);
			}
    	}
    }
    
    protected void showDocument(String documentUid) {
		ticketDocuments.stream().filter(documentRow -> documentRow.getDocumentUid().equals(documentUid)).findFirst()
				.ifPresent(documentRow -> {
					sceneData.put(DocumentViewerController.PARAM_DOCUMENT_UID, documentRow.getDocumentUid());
					sceneData.put(DocumentViewerController.PARAM_DOCUMENT_TYPE_CODE, documentRow.getDocTypeCode());
				});
		openScene(DocumentViewerController.class);
	}

}

