package com.comerzzia.pos.gui.sales.retail.creditsaleretrieval;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.facade.model.DocTypeDetail;
import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.retailrefund.documenttype.DocumentTypeController;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Controller
@CzzScene
public class CreditSaleRetrievalController extends SceneController{
		
	private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(getClass());
	
	@Autowired
	protected Session session;
	
	@FXML
	protected TextField tfDocumentCode, tfDocTypeCode, tfDocTypeDes, tfLocator, tfServiceCode, tfOrderCode;
	
	@FXML
	protected Button btAccept, btCancel, btSearchDocType;
	
	@FXML
	protected Label lbError;
	
	protected CreditSaleRetrievalForm formData;
	
	protected RetailBasketManager basketManager;
	
	protected DocTypeDetail sourceDocType;
		
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		formData = SpringContext.getBean(CreditSaleRetrievalForm.class);
		
		formData.setFormField("docType", tfDocTypeCode);
		
		tfDocTypeCode.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if(oldValue){
                    processDocType();
                }
            }
        });
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		basketManager = (RetailBasketManager) sceneData.get(BasketItemizationControllerAbstract.BASKET_KEY);
		
		tfLocator.clear();
		tfServiceCode.clear();
		
		formData.clearErrorStyle();
				
		if(sourceDocType!=null){
			List<DocTypeDetail> salesCreditDocTypes = session.getApplicationSession().getValidOriginDocTypesForType(sourceDocType.getDocTypeCode());
			
			if(salesCreditDocTypes==null || salesCreditDocTypes.isEmpty()){
                log.error("onSceneOpen() - No hay configurado en base de datos un documento de venta de crédito para la aplicación");
				throw new InitializeGuiException(I18N.getText("Error en la búsqueda del documento de venta de cr\u00E9dito asociado a la factura."));
			}
			DocTypeDetail salesCreditDocType = salesCreditDocTypes.get(0);

			if(salesCreditDocType!=null){
				tfDocTypeCode.setText(salesCreditDocType.getDocTypeCode());
				tfDocTypeDes.setText(salesCreditDocType.getDocTypeDes());
			}
		}
	}

	@Override
	public void initializeFocus() {
		tfServiceCode.requestFocus();
	}
	
	public void actionAccept(){
		String locator = tfLocator.getText();
		if (locator != null && !locator.isEmpty()) {
		} else {
			locator = null;
		}
		
		String serviceCode = tfServiceCode.getText();
		
		if (serviceCode != null && serviceCode.isEmpty()) {
			serviceCode = null;
		}
		
		String orderCode = tfOrderCode.getText();
		
		if(orderCode != null && orderCode.isEmpty()){
			orderCode = null;
		}
			
		String docTypeCode = tfDocTypeCode.getText();
		String documentCode = tfDocumentCode.getText();
		
		formData.setDocType(docTypeCode);
		
		if (locator != null || serviceCode != null || orderCode != null) {
			formData.clearErrorStyle();
			lbError.setText("");
			new RetrieveSalesCreditDocumentTask(documentCode, docTypeCode, locator, serviceCode, orderCode).start();
		} else {
			if(validateFormData()){
				new RetrieveSalesCreditDocumentTask(documentCode, docTypeCode, locator, serviceCode, orderCode).start();
			}
		}
	}
	
	@FXML
    public void actionTfCodeIntro(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            btAccept.requestFocus();
        }
    }
	
	public class RetrieveSalesCreditDocumentTask extends RestBackgroundTask<Boolean> {

		protected String documentCode, docTypeCode, locator, serviceCode, orderCode;
		
		public RetrieveSalesCreditDocumentTask(String documentCode, String docTypeCode, String locator, String serviceCode, String orderCode) {
			this.documentCode = documentCode;
			this.docTypeCode = docTypeCode;
			this.locator = locator;
			this.serviceCode = serviceCode;
			this.orderCode = orderCode;
		}

		@Override
		protected Boolean execute() throws Exception {
			
//			TODO
//			Long docTypeId = null;
//			if(StringUtils.isNotBlank(codDoc)) {
//				DocTypeDetail tipoDocumento = sesion.getAplicacion().getDocTypeByDocTypeCode(codDoc);
//				docTypeId = tipoDocumento.getDocTypeId();
//			}
//						
//			DocumentRequest request = new DocumentRequest(docTypeId, codTicket, codPedido, localizador, codServicio);
//			
//			return ticketManager.getSalesCreditDocument(request);
			return true;
		}
	
		@Override
		protected void failed() {
			super.failed();
        	if(!tfServiceCode.getText().isEmpty()){
        		tfServiceCode.requestFocus();
        	}else if(!tfLocator.getText().isEmpty()){
        		tfLocator.requestFocus();
        	}
        	else if(!tfOrderCode.getText().isEmpty()){
        		tfOrderCode.requestFocus();
        	}
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			closeSuccess();
		}
	}
	
	/**
     * Valida los datos introducidos
     *
     * @return
     */
    private boolean validateFormData() {
        boolean valid;

        // Limpiamos los errores que pudiese tener el formulario
        formData.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        // Validamos el formulario de login
        Set<ConstraintViolation<CreditSaleRetrievalForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(formData);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<CreditSaleRetrievalForm> next = constraintViolations.iterator().next();
            formData.setErrorStyle(next.getPropertyPath(), true);
            formData.setFocus(next.getPropertyPath());
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
		sceneData.put(DocumentTypeController.PARAM_DOC_POSSIBLE_INPUT, session.getApplicationSession().getValidOriginDocTypesListForType(basketManager.getDefaultDocumentType().getDocTypeCode()));
		
		openScene(DocumentTypeController.class, new SceneCallback<DocTypeDetail>() {
			
			@Override
			public void onSuccess(DocTypeDetail docType) {
				tfDocTypeCode.setText(docType.getDocTypeCode());
				tfDocTypeDes.setText(docType.getDocTypeDes());
			}
		});
    }

    protected void processDocType(){
    	String docTypeCode = tfDocTypeCode.getText();

    	if(!docTypeCode.trim().isEmpty()){
    		try {
    			DocTypeDetail docType = session.getApplicationSession().getDocTypeByDocTypeCode(docTypeCode);
    			List<String> validOriginDocuments = session.getApplicationSession().getValidOriginDocTypesListForType(docType.getDocTypeCode());
    			
    			if(sourceDocType!=null){
    				if(!validOriginDocuments.contains(docType.getDocTypeCode())){
    					log.warn("Se seleccionó un tipo de documento no válido para la devolución.");
    					DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El documento seleccionado no es válido."));
    					tfDocTypeDes.setText("");
    					tfDocTypeCode.setText("");
    				}
    				else{
    					tfDocTypeCode.setText(docType.getDocTypeCode());
    					tfDocTypeDes.setText(docType.getDocTypeDes());
    				}
    			}
    			else{
    				tfDocTypeCode.setText("");
    				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El documento seleccionado no es válido."));
    			}
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
    		tfDocTypeCode.setText("");
    	}
    }
    
    public void actionTfLocatorIntro(KeyEvent e){
    	if(e.getCode() == KeyCode.ENTER){
    		actionAccept();
    	}
    }

}
