package com.comerzzia.pos.gui.sales.document.view;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.facade.model.DocTypeDetail;
import com.comerzzia.core.facade.service.doctype.DocTypeServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.documents.PrintDocumentRequest;
import com.comerzzia.omnichannel.facade.model.documents.SaleDocHdr;
import com.comerzzia.omnichannel.facade.service.documents.TicketDocumentServiceFacade;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.devices.printer.PrintService;
import com.comerzzia.pos.gui.sales.document.giftticket.GiftTicketController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;

@Controller
@CzzScene
public class DocumentViewerController extends SceneController implements Initializable {

    private static final Logger log = Logger.getLogger(DocumentViewerController.class.getName());

    //Claves para obtener los parámetros que se le pasan al controlador al crear la ventana
    public static final String PARAM_DOCUMENT_UID = "DOCUMENT_UID";
    public static final String PARAM_DOCUMENT_TYPE_CODE = "DOCUMENT_CODE";

    @FXML
    protected WebView taDocument;

    @FXML
    protected Button btPrintCopy, btGiftCard, btBack;

    // Ticket a mostrar en pantalla
    protected String documentUid;
    protected String docTypeCode;


    protected ButtonsGroupComponent buttonsGroupComponent;
    
    @Autowired
    protected VariableServiceFacade variablesService;
        
    @Autowired
    protected PrintService printService;
    
    @Autowired
    protected TicketDocumentServiceFacade ticketDocumentService;
    
    @Autowired
    protected Session session;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initializeComponents() {
        log.debug("initializeComponents() - Inicialización de componentes");
    }

	@Override
    public void onSceneOpen() throws InitializeGuiException {
    	documentUid = null;
    	docTypeCode = null;
        
    	if(this.sceneData.containsKey(PARAM_DOCUMENT_UID) && this.sceneData.containsKey(PARAM_DOCUMENT_TYPE_CODE)) {
        	documentUid = (String) this.sceneData.get(PARAM_DOCUMENT_UID);
        	docTypeCode = (String) this.sceneData.get(PARAM_DOCUMENT_TYPE_CODE);
        } else {
        	throw new InitializeGuiException(I18N.getText("No se ha recibido ningún documento para ser mostrado."), true);
        }
        
        DocTypeDetail docType = session.getApplicationSession() .getDocTypeByDocTypeCode(docTypeCode);
		btGiftCard.setDisable(!StringUtils.equals( docType.getProperties().get(DocTypeServiceFacade.PROPIEDAD_PERMITE_TICKET_REGALO), "S"));
        // Por defecto la caja será la caja configurada        
        refreshScreenData();        
    }

    @Override
    public void initializeFocus() {
        btPrintCopy.requestFocus();
    }

    public void refreshScreenData() throws InitializeGuiException {
        log.debug("refreshScreenData()");

		Map<String, Object> params = new HashMap<>();
		params.put("esGestion", true);
		
		PrintDocumentRequest printRequest = new PrintDocumentRequest();
		printRequest.setCustomParams(params);

		try {
			String preview = printService.printScreen(documentUid, printRequest);
			setTicketText(preview);

		} catch (Exception e) {
			DialogWindowBuilder.getBuilder(getStage())
					.simpleThrowableDialog(I18N.getText("Lo sentimos, ha ocurrido un error al imprimir."), e);
			throw new InitializeGuiException(false);
		}
    }

    @FXML
    protected void actionPrintCopy(ActionEvent event) {

        try {
           // Se reimprime la misma
            Map<String,Object> params= new HashMap<String,Object>();
            params.put("urlQR", variablesService.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
            
            PrintDocumentRequest request = new PrintDocumentRequest();
            request.setCopy(true);
            request.setCustomParams(params);
            printService.printDocument(documentUid,docTypeCode, request);
            
        }
        catch (DeviceException ex) {
        	DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Fallo al imprimir ticket."));
        }
    }

    @FXML
    protected void actionGiftCard(ActionEvent event) {  
    	String giftCardFormat = session.getApplicationSession().getDocTypeByDocTypeCode(docTypeCode).getFormatoImpresionTicketRegalo();
			
		if(giftCardFormat!=null){
			
			SaleDocHdr ticketDocument = ticketDocumentService.findSaleDocById(documentUid);
	        sceneData.put(GiftTicketController.PARAM_GIFTCARD,ticketDocument);
	        openScene(GiftTicketController.class, voidCallback -> closeSuccess());
    	}
		else{
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No existe un formato de ticket regalo para el tipo de documento del ticket original."));
		}
				
    }

    protected void setTicketText(String document) {
    	taDocument.getEngine().loadContent(document);    	
    }

}