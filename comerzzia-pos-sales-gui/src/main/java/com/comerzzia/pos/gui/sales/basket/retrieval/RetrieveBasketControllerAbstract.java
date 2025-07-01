package com.comerzzia.pos.gui.sales.basket.retrieval;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.omnichannel.facade.model.PaginatedRequest;
import com.comerzzia.omnichannel.facade.model.PaginatedResponse;
import com.comerzzia.omnichannel.facade.model.basketdocument.SaleBasket;
import com.comerzzia.omnichannel.facade.model.basketdocument.SaleBasketFilter;
import com.comerzzia.omnichannel.facade.service.basketdocument.SaleBasketDocumentServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.services.session.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class RetrieveBasketControllerAbstract extends SceneController implements ButtonsGroupController{

    protected Logger log = Logger.getLogger(getClass());
    
    public static final String PARAM_DOCUMENT_TYPE = "DOCUMENT_TYPE";
    public static final String PARAM_RECOVERED_BASKET = "RECOVERED_BASKET";
    
    @FXML
    protected TableView tbBaskets;
    
    @FXML
    protected Button btCancel, btAccept;
    
        
    @Autowired
    protected SaleBasketDocumentServiceFacade basketDocumentService;
    
    @Autowired
    protected Session session;
    
    protected ObservableList<ParkedBasketRow> parketBasketsList;
    
    @FXML
    protected TableColumn<ParkedBasketRow,String> tcDate;
    
    @FXML
    protected TableColumn<ParkedBasketRow,String> tcAmount;
    
    @FXML
    protected TableColumn<ParkedBasketRow,String> tcAlias;
    
    @FXML
    protected TableColumn<ParkedBasketRow,String> tcTill;
    
    @FXML
    protected TableColumn<ParkedBasketRow,String> tcItemsCount;
    
    protected List<Long> docTypes;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcAlias.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbBaskets", "tcAlias", 2,CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcTill.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbBaskets", "tcTill", 2,CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcAmount.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbBaskets", "tcAmount", 2,CellFactoryBuilder.RIGHT_ALIGN_STYLE));
        tcDate.setCellFactory(CellFactoryBuilder.createCellRendererCeldaFechaHora("tbBaskets", "tcDate", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcItemsCount.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbBaskets", "tcItemsCount", null,CellFactoryBuilder.RIGHT_ALIGN_STYLE));
                
        tcAlias.setCellValueFactory(new PropertyValueFactory<>("alias"));
        tcTill.setCellValueFactory(new PropertyValueFactory<>("till"));
        tcDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tcAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tcItemsCount.setCellValueFactory(new PropertyValueFactory<>("itemsCount"));
        
    }

    @Override
    public void initializeComponents() {
        tbBaskets.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY );
    }
    
    @Override
    protected void executeLongOperations() throws Exception {
    	super.executeLongOperations();
    	loadParkedBasket();
    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
        docTypes = Arrays.asList((Long)sceneData.get(PARAM_DOCUMENT_TYPE));
    }
    
    @Override
    protected void succededLongOperations() {
    	super.succededLongOperations();
    	tbBaskets.setItems(parketBasketsList);

        if (tbBaskets.getItems() != null) {
            tbBaskets.getSelectionModel().select(0);
            tbBaskets.scrollTo(0);  // Mueve el scroll para que se vea el registro
        }
    }

	protected void loadParkedBasket() {
		SaleBasketFilter filter = new SaleBasketFilter();
		filter.setStoreCode(session.getApplicationSession().getCodAlmacen());
		filter.setTillCode(session.getApplicationSession().getTillCode());
		filter.setDocTypeIds(docTypes);
		
		PaginatedResponse<SaleBasket> page = basketDocumentService.findPage(filter, new PaginatedRequest(0, 100));
         
        parketBasketsList = FXCollections.observableArrayList();

         if (page.getTotalElements() > 0) {
             for(SaleBasket basket: page.getContent()){
                 parketBasketsList.add(new ParkedBasketRow(basket));
             }
         }
	}
    
    @Override
    public void initializeFocus() {
    	tbBaskets.requestFocus();
    	tbBaskets.getSelectionModel().select(0);
    	tbBaskets.getFocusModel().focus(0);
    }
    
    /**
     * Accion para tratar el doble click en una de las filas de la pantalla
     *
     * @param event
     */
    @FXML
    public void actionTableAcceptDocument(MouseEvent event) {
        log.debug("actionTableAcceptDocument() - Acción aceptar");
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
        	acceptSelectedDocument();
        }
    }
    
    public void acceptSelectedDocument(){
        ParkedBasketRow selectedLine = (ParkedBasketRow)tbBaskets.getSelectionModel().getSelectedItem();
        
        if (selectedLine == null) return;
        
        closeSuccess(selectedLine.getBasket());       
    }
    
    public void acceptLineEnter(KeyEvent event) {
        log.trace("acceptLineEnter(KeyEvent event) - Aceptar");
        if (event.getCode() == KeyCode.ENTER) {
            acceptSelectedDocument();
        }
    }
}
