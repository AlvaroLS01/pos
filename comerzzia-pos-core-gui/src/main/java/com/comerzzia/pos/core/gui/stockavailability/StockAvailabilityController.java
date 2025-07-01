package com.comerzzia.pos.core.gui.stockavailability;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.catalog.facade.model.CatalogItemCombinations;
import com.comerzzia.catalog.facade.model.CatalogItemDetail;
import com.comerzzia.catalog.facade.model.CatalogItemStockAvailabilities;
import com.comerzzia.catalog.facade.model.CatalogItemStockAvailabilityDetail;
import com.comerzzia.catalog.facade.model.CatalogItemStockAvailabilityDetailGeolocated;
import com.comerzzia.catalog.facade.model.CatalogStoreGeolocated;
import com.comerzzia.catalog.facade.model.CatalogStoreStock;
import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.core.commons.sessions.ComerzziaSession;
import com.comerzzia.core.commons.sessions.ComerzziaThreadSession;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.store.Store;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

@Component
@CzzActionScene
public class StockAvailabilityController extends ActionSceneController implements Initializable {
	
	protected Logger log = Logger.getLogger(getClass());
	
	public static final String PARAM_ITEM_CODE = "itemCode";
	public static final String PARAM_COMBINATION_1_CODE = "combination1Code";
	public static final String PARAM_COMBINATION_2_CODE = "combination2Code";
	public static final String PARAM_MODAL = "modal";
	
	@Autowired
	protected Session session;
	
	@Autowired
	protected VariableServiceFacade variableService;
	
	protected Catalog catalog;
			
	@Autowired
	protected ModelMapper modelMapper;
	
    @FXML
    protected Button btCancel;
    
    @FXML
    protected Button btSearch;
    
    @FXML
    protected TextField tfEnterCode;
    @FXML
    protected TextField tfItemDes;
    
    @FXML
    protected TextField tfStoresExpression;
    
    @FXML
    protected CheckBox cbOnlyAvailables;

    @FXML
    protected Label lbCombination1Code;
    @FXML
    protected Label lbCombination2Code;
    
    @FXML
    protected ImageView itemImage;
    
    @FXML
    protected TableView<StockRow> tbLoggedStoreStock;
    
	@FXML
	protected TableColumn<StockRow, String> tcCombination1Code;
	@FXML
	protected TableColumn<StockRow, String> tcCombination2Code;
	@FXML
	protected TableColumn<StockRow, String> tcStock;
	@FXML
	protected TableColumn<StockRow, String> tcStockA;
	@FXML
	protected TableColumn<StockRow, String> tcStockB;
	@FXML
	protected TableColumn<StockRow, String> tcStockC;
	@FXML
	protected TableColumn<StockRow, String> tcStockD;
	@FXML
	protected TableColumn<StockRow, String> tcLogisticStock;
	
	@FXML
    protected TableView<StockRow> tbOthersStock;
    
	@FXML
	protected TableColumn<StockRow, String> tcOthersCode;
	@FXML
	protected TableColumn<StockRow, String> tcOthersDes;
	@FXML
	protected TableColumn<StockRow, String> tcOthersProvince;
	@FXML
	protected TableColumn<StockRow, String> tcOthersCombination1Code;
	@FXML
	protected TableColumn<StockRow, String> tcOthersCombination2Code;
	@FXML
	protected TableColumn<StockRow, String> tcOthersStock;
	@FXML
	protected TableColumn<StockRow, Button> tcOthersContact;
	
	protected ObservableList<StockRow> loggedStoreStock;
	
	protected ObservableList<StockRow> othersStock;
	
	@FXML
	protected ComboBox<String> cbCombination1Code;
	
	@FXML
	protected ComboBox<String> cbCombination2Code;
	
	@FXML
	protected HBox hbButtons, modalTitle;
	
	protected Boolean modal;
	
	protected CatalogItemCombinations itemCombinations;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando pantalla...");

		// Message for table without content
		tbLoggedStoreStock.setPlaceholder(new Label(I18N.getText("No hay stock en la tienda")));

		loggedStoreStock = FXCollections.observableList(new ArrayList<StockRow>());
		
		tcCombination1Code.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockTiendaLogada", "tcDesglose1", null, CellFactoryBuilder.CENTER_ALIGN_STYLE));
		tcCombination2Code.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockTiendaLogada", "tcDesglose2", null, CellFactoryBuilder.CENTER_ALIGN_STYLE));
		tcStock.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockTiendaLogada", "tcStock", null, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcStockA.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockTiendaLogada", "tcStockA", null, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcStockB.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockTiendaLogada", "tcStockB", null, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcStockC.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockTiendaLogada", "tcStockC", null, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcStockD.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockTiendaLogada", "tcStockD", null, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcLogisticStock.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockTiendaLogada", "tcStockLogistico", null, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		
		tcCombination1Code.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getCombination1CodeProperty();
			}
		});
		tcCombination2Code.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getCombination2CodeProperty();
			}
		});
		tcStock.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getStockProperty();
			}
		});
		tcStockA.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getStockAProperty();
			}
		});
		tcStockB.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getStockBProperty();
			}
		});
		tcStockC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getStockCProperty();
			}
		});
		tcStockD.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getStockDProperty();
			}
		});
		tcLogisticStock.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getLogisticStockProperty();
			}
		});

		// Message for table without content
		tbOthersStock.setPlaceholder(new Label(I18N.getText("No hay stock en el resto de tiendas")));

		othersStock = FXCollections.observableList(new ArrayList<StockRow>());

		tcOthersCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockResto", "tcCodigoResto", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcOthersDes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockResto", "tcDescripcionResto", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcOthersProvince.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockResto", "tcProvinciaResto", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcOthersCombination1Code.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockResto", "tcDesglose1Resto", null, CellFactoryBuilder.CENTER_ALIGN_STYLE));
		tcOthersCombination2Code.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockResto", "tcDesglose2Resto", null, CellFactoryBuilder.CENTER_ALIGN_STYLE));
		tcOthersStock.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbStockResto", "tcStockResto", null, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcOthersContact.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, Button>, ObservableValue<Button>>(){

			@Override
			public ObservableValue<Button> call(CellDataFeatures<StockRow, Button> p) {
				StockRow stockRow = p.getValue();

				final Button contactBtn = new Button();

				contactBtn.setPrefSize(90, 30);
				contactBtn.setMaxSize(90, 30);
				contactBtn.setMinSize(90, 30);
				contactBtn.getStyleClass().add("btn-helper");

				contactBtn.setUserData(stockRow);
				contactBtn.setAlignment(Pos.BOTTOM_CENTER);

				contactBtn.setOnAction(new EventHandler<ActionEvent>(){

					@Override
					public void handle(ActionEvent evt) {
						StockRow stockRow = (StockRow) ((Node) evt.getSource()).getUserData();
						CatalogStoreGeolocated store = stockRow.getStore();
						if (store != null) {
							HashMap<String, Object> stageData = new HashMap<String, Object>();
							stageData.put(StoreContactController.STOCK, stockRow);
							openModalScene(StoreContactController.class, null, stageData);
						}
					}
				});

				return new ObservableValueBase<Button>(){

					@Override
					public Button getValue() {
						return contactBtn;
					}
				};
			}
		});

		// Factory for every cell to increase performance
		tcOthersCode.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getStoreCodeProperty();
			}
		});
		tcOthersDes.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getStoreDesProperty();
			}
		});
		tcOthersProvince.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getProvinceProperty();
			}
		});
		tcOthersCombination1Code.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getCombination1CodeProperty();
			}
		});
		tcOthersCombination2Code.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getCombination2CodeProperty();
			}
		});
		tcOthersStock.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockRow, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<StockRow, String> cdf) {
				return cdf.getValue().getStockProperty();
			}
		});
	}
	
    @Override
    public void initializeComponents() {  
    	try {
	    	log.debug("inicializarComponentes() - Configuración de la tabla");
			if (session.getApplicationSession().isDesglose1Activo()) { // If combination 1 code is active, set title
				tcCombination1Code.setText(I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO)));
			}
			else { // If combination 1 code is not active, compact line
				tcCombination1Code.setVisible(false);
			}
			if (session.getApplicationSession().isDesglose2Activo()) { // If combination 2 code is active, set title
				tcCombination2Code.setText(I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO)));
			}
			else { // If combination 2 code is not active, compact line
				tcCombination2Code.setVisible(false);
			}			
			if (session.getApplicationSession().isDesglose1Activo()) { // If combination 1 code is active, set title
				tcOthersCombination1Code.setText(I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO)));
			}
			else { // If combination 1 code is not active, compact line
				tcOthersCombination1Code.setVisible(false);
			}
			if (session.getApplicationSession().isDesglose2Activo()) { // If combination 2 code is active, set title
				tcOthersCombination2Code.setText(I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO)));
			}
			else { // If combination 2 code is not active, compact line
				tcOthersCombination2Code.setVisible(false);
			}
			if (session.getApplicationSession().isDesglose1Activo()) { // If combination 1 code is active, set title
				lbCombination1Code.setText(I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO)));
			} else {
				lbCombination1Code.setVisible(false);
				cbCombination1Code.setVisible(false);
			}
			if (session.getApplicationSession().isDesglose2Activo()) { // If combination 2 code is active, set title
				lbCombination2Code.setText(I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO)));
			} else {
				lbCombination2Code.setVisible(false);
				cbCombination2Code.setVisible(false);
			}
			
			String titleStockA = variableService.getVariableAsString(VariableServiceFacade.STOCK_TITULO_A);
			if(StringUtils.isNotBlank(titleStockA)) {
				tcStockA.setText(I18N.getText(variableService.getVariableAsString(VariableServiceFacade.STOCK_TITULO_A)));
			}
			else {
				tcStockA.setVisible(false);
			}
			
			String titleStockB = variableService.getVariableAsString(VariableServiceFacade.STOCK_TITULO_B);
			if(StringUtils.isNotBlank(titleStockB)) {
				tcStockB.setText(I18N.getText(variableService.getVariableAsString(VariableServiceFacade.STOCK_TITULO_B)));
			}
			else {
				tcStockB.setVisible(false);
			}
			
			String titleStockC = variableService.getVariableAsString(VariableServiceFacade.STOCK_TITULO_C);
			if(StringUtils.isNotBlank(titleStockC)) {
				tcStockC.setText(I18N.getText(variableService.getVariableAsString(VariableServiceFacade.STOCK_TITULO_C)));
			}
			else {
				tcStockC.setVisible(false);
			}
			
			String titleStockD = variableService.getVariableAsString(VariableServiceFacade.STOCK_TITULO_D);
			if(StringUtils.isNotBlank(titleStockD)) {
				tcStockD.setText(I18N.getText(variableService.getVariableAsString(VariableServiceFacade.STOCK_TITULO_D)));
			}
			else {
				tcStockD.setVisible(false);
			}
			
			tbLoggedStoreStock.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			tbOthersStock.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			tbLoggedStoreStock.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			
			cbCombination1Code.getItems().clear();
			cbCombination2Code.getItems().clear();
			
			tfEnterCode.textProperty().addListener(new ChangeListener<String>() {
				@Override
	            public void changed(ObservableValue<? extends String> paramObservableValue, String oldValue, String newValue) {
					showItemDescription(newValue);
					fillCombinationCodes(newValue, null, null);
	            }
			});
			
			tfStoresExpression.textProperty().addListener(new ChangeListener<String>() {
				@Override
	            public void changed(ObservableValue<? extends String> paramObservableValue, String oldValue, String newValue) {
					if(StringUtils.isNotBlank(newValue)) {
						tfStoresExpression.setText(newValue.toUpperCase());
					}
	            }
			});
			
			cbCombination1Code.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
				@Override
	            public void changed(ObservableValue<? extends String> paramObservableValue, String oldValue, String newValue) {
					fillLinkedCombinationCodes(newValue, null);
	            }
			});
			
		} catch (Exception e) {
			log.error("inicializarComponentes() - Error inicializando pantalla de stock");
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Error cargando pantalla. Para mas información consulte el log."));
		}
		
    }

    @Override
    public void onSceneOpen() {
    	
    	if (ComerzziaThreadSession.getToken() == null) {
			ComerzziaSession cmzSession = new ComerzziaSession();
			cmzSession.setActivityUid(session.getApplicationSession().getConfiguredActivityUid());
			ComerzziaThreadSession.setComerzziaSession(cmzSession);
    		ComerzziaThreadSession.setApiKey(variableService.getVariableAsString(VariableServiceFacade.WEBSERVICES_APIKEY));
    	}

    	catalog = applicationSession.getValidCatalog();
    	
    	cbOnlyAvailables.setSelected(true);
    	tfStoresExpression.clear();
    	
		List<StockRow> loggedStoreStockList = new ArrayList<StockRow>();
		loggedStoreStock = FXCollections.observableArrayList(loggedStoreStockList);
	    tbLoggedStoreStock.setItems(loggedStoreStock);
	    
	    List<StockRow> othersStoresStockList = new ArrayList<StockRow>();
		othersStock = FXCollections.observableArrayList(othersStoresStockList);
	    tbOthersStock.setItems(othersStock);
		
	    String selectedItemCode = (String) sceneData.get(PARAM_ITEM_CODE);
		String selectedCombination1Code = (String) sceneData.get(PARAM_COMBINATION_1_CODE);
		String selectedCombination2Code = (String) sceneData.get(PARAM_COMBINATION_2_CODE);
	    if(selectedItemCode != null){
	    	tfEnterCode.setText(selectedItemCode);
	    	showItemDescription(selectedItemCode);
	    	fillCombinationCodes(selectedItemCode, selectedCombination1Code, selectedCombination2Code);
	    	btSearch.fire();
	    }else{
	    	tfEnterCode.setText("");
	    	tfItemDes.setText("");
	    	itemImage.setImage(null);
	    	cbCombination1Code.getItems().clear();
	    	cbCombination2Code.getItems().clear();
	    }
    }

	protected void showItemDescription(String itemCode) {
		tfItemDes.setText("");
		
		if (StringUtils.isNotBlank(itemCode)) {
			try {
				CatalogItemDetail item = catalog.getCatalogItemService().findByBarcode(itemCode);
		        if (item != null) {
		        	tfItemDes.setText(item.getItemDes());
		        }
		    }
		    catch (Exception e) {
		    	if(AppConfig.getCurrentConfiguration().getDeveloperMode()) {
		    		log.error("focusScene() - Ha habido un error al consultar el artículo: " + e.getMessage(), e);
		    	}
		    }
		}
    }

    protected void fillCombinationCodes(String itemCode, String combination1CodeSelected, String combination2CodeSelected) {
    	cbCombination1Code.getItems().clear();
    	cbCombination1Code.getItems().add("");
		cbCombination1Code.getSelectionModel().selectLast();
		itemCombinations = null;
    	
    	if (StringUtils.isNotBlank(itemCode) && StringUtils.isNotBlank(tfItemDes.getText())) {
    		try {
    			itemCombinations = catalog.getCatalogItemService().findCombinations(itemCode);

				if (itemCombinations != null && itemCombinations.getCombination1Codes() != null) {
					for(String combination1Code : itemCombinations.getCombination1Codes().keySet()) {
            			cbCombination1Code.getItems().add(combination1Code);
            			if(combination1Code.equals(combination1CodeSelected)) {
            				cbCombination1Code.getSelectionModel().selectLast();
            			}
            		}
					
					fillLinkedCombinationCodes(combination1CodeSelected, combination2CodeSelected);
				}
        	}
		    catch (Exception e) {
		    	log.error("fillCombinationCodes() - Ha habido un error al rellenar los combos de desgloses: " + e.getMessage(), e);
		    	DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha habido un error al cargar los desgloses del artículo. Se cargará el desglose seleccionado anteriormente."), e);
		    }
    	}
    	else {
    		cbCombination2Code.getItems().clear();
	    	cbCombination2Code.getItems().add("");
			cbCombination2Code.getSelectionModel().selectLast();
    	}
    }
    
    protected void fillLinkedCombinationCodes(String combination1CodeSelected, String combination2CodeSelected) {
    	cbCombination2Code.getItems().clear();
    	cbCombination2Code.getItems().add("");
		cbCombination2Code.getSelectionModel().selectLast();
		
		if (StringUtils.isBlank(combination1CodeSelected)) {
			if (itemCombinations != null && itemCombinations.getCombination2Codes() != null) {
				for(String combination2Code : itemCombinations.getCombination2Codes().keySet()) {
					cbCombination2Code.getItems().add(combination2Code);
	    			if(combination2Code.equals(combination2CodeSelected)) {
	    				cbCombination2Code.getSelectionModel().selectLast();
	    			}
        		}
			}
		}
		else if (itemCombinations != null && itemCombinations.getCombination1Codes().containsKey(combination1CodeSelected)) {
    		for(String combination2Code : itemCombinations.getCombination1Codes().get(combination1CodeSelected)) {
    			cbCombination2Code.getItems().add(combination2Code);
    			if(combination2Code.equals(combination2CodeSelected)) {
    				cbCombination2Code.getSelectionModel().selectLast();
    			}
    		}
    	}
    }

	@Override
    public void initializeFocus() {
    	tfEnterCode.requestFocus();
    }
    
    public void actionBtCancel(){
    	closeCancel();
    }
    
    public void actionTfEnterCode(KeyEvent event){
    	if (event.getCode() == KeyCode.ENTER) {
			searchAvailability();
		}
    }
    
    public void actionTfEnterCombination1Code(KeyEvent event){
    	if (event.getCode() == KeyCode.ENTER) {
			searchAvailability();
		}
    }
    
    public void actionTfEnterCombination2Code(KeyEvent event){
    	if (event.getCode() == KeyCode.ENTER) {
			searchAvailability();
		}
    }
    
    public void actionTfStoresExpression(KeyEvent event){
    	if (event.getCode() == KeyCode.ENTER) {
			searchAvailability();
		}
    }
    
    public void actionBtSearch(){
    	searchAvailability();
    }
    
	protected void searchAvailability() {
		String itemCode = tfEnterCode.getText();		
 		String combination1Code = cbCombination1Code.getSelectionModel().getSelectedItem();
		String combination2Code = cbCombination2Code.getSelectionModel().getSelectedItem();
				
		try {
			CatalogItemDetail item = catalog.getCatalogItemService().findByCombination(itemCode, combination1Code, combination2Code);
			if(item != null) {
				itemCode = item.getItemCode();
			}
		}
		catch(Exception e) {
			if(AppConfig.getCurrentConfiguration().getDeveloperMode()) {
				log.error("searchAvailability() - Ha habido un error al buscar el código de barras:" + e.getMessage(), e);
			}
		}

		if (itemCode != null && !itemCode.isEmpty()) {
			Store store = session.getApplicationSession().getStorePosBusinessData().getStore();
			BigDecimal latitude = store.getLatitude() != null ? store.getLatitude() : null;
			BigDecimal longitude = store.getLongitude() != null ? store.getLongitude() : null;

			QueryLoggedStoreStockTask consultarStockTask = SpringContext.getBean(QueryLoggedStoreStockTask.class, itemCode, store.getStoreCode(), combination1Code, combination2Code,
					latitude, longitude, null, tfStoresExpression.getText(), cbOnlyAvailables.isSelected(), new RestBackgroundTask.FailedCallback<CatalogItemStockAvailabilities>(){

				@Override
				public void succeeded(CatalogItemStockAvailabilities result) {
					searchAvailabilitySucceeded(result);
				}

				@Override
				public void failed(Throwable throwable) {
					clearForm();
					tfEnterCode.requestFocus();
				}

			}, getStage());

			consultarStockTask.start();
		}
		else {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Debe introducir el código de artículo o código de barras"));
			tfEnterCode.requestFocus();
		}
	}
    
    protected void searchAvailabilitySucceeded(CatalogItemStockAvailabilities itemAvailabilities){
    	try {
	    	List<StockRow> stockLoggedStore = new ArrayList<StockRow>();
	    	itemImage.setImage(null);

// TODO: Carga de la foto de artículo seleccionado	    	
//    		byte[] foto = null;
//	    		if(!itemAvailabilities.getLstDisponibilidadTienda().isEmpty()){
//	    			DisponibilidadArticuloBean disponibilidadArticulo = itemAvailabilities.getLstDisponibilidadTienda().get(0);
//	    			if(disponibilidadArticulo != null){
//	    				if(disponibilidadArticulo.getStock().getFoto() != null){
//	    					foto = disponibilidadArticulo.getStock().getFoto();
//	    				}
//	    			}
//	    			
//	    		}
//	    		if(!itemAvailabilities.getLstDisponibilidadResto().isEmpty()){
//	    			DisponibilidadArticuloBean disponibilidadArticulo = itemAvailabilities.getLstDisponibilidadResto().get(0);
//	    			if(disponibilidadArticulo != null){
//	    				if(foto != null && disponibilidadArticulo.getStock().getFoto() != null){
//	    					foto = disponibilidadArticulo.getStock().getFoto();
//	    				}
//	    			}
//	    			
//	    		}
//    		if(foto != null){
//    			ByteArrayInputStream bis = new ByteArrayInputStream(foto);
//        		Image image = new Image(bis, 90.0, 90.0, true, true);
//        		imgArticulo.setImage(image);
//    		}else{
//    			imgArticulo.setImage(null);
//    		}
    		
	    	if (itemAvailabilities != null) {
	    		CatalogItemStockAvailabilityDetail storeAvailability = itemAvailabilities.getItemAvailabilityStore();
	    		if (storeAvailability != null) {
	    			CatalogStoreGeolocated loggedStore = modelMapper.map(storeAvailability.getStore(), CatalogStoreGeolocated.class);
	    			for(CatalogStoreStock storeStock : storeAvailability.getStoreStocks()){
	        			stockLoggedStore.add(new StockRow(loggedStore, storeStock));
	        		}
	    		}
	    		loggedStoreStock = FXCollections.observableArrayList(stockLoggedStore);
	            tbLoggedStoreStock.setItems(loggedStoreStock);
	    		
	    		CatalogItemStockAvailabilityDetail logisticAvailability = itemAvailabilities.getItemAvailabilityLogistic();
	    		if (logisticAvailability != null) {
	    			linkLogisticStoreStocks(logisticAvailability.getStoreStocks());
	    		}
	            
	            List<StockRow> stockOtherStores = new ArrayList<StockRow>();
	    		for(CatalogItemStockAvailabilityDetailGeolocated itemAvailability : itemAvailabilities.getItemAvailabilityOther()){
	    			CatalogStoreGeolocated otherStore = itemAvailability.getStore();
	    			for (CatalogStoreStock storeStock : itemAvailability.getStoreStocks()) {
	    				stockOtherStores.add(new StockRow(otherStore, storeStock));
	    			}
	    		}
	    		this.othersStock = FXCollections.observableArrayList(stockOtherStores);
	            tbOthersStock.setItems(this.othersStock);
	    	}
    		
    	}
    	catch(Exception e) {
    		log.error("searchAvailabilitySucceeded() - Ha habido un error al pintar en la pantalla: " + e.getMessage(), e);
    	}
    }
    
    protected void linkLogisticStoreStocks(List<CatalogStoreStock> logisticStoreStocks) {
    	for(CatalogStoreStock storeStock : logisticStoreStocks) {
    		for(StockRow stockTienda : loggedStoreStock) {
				if (stockTienda.getCombination1Code().equals(storeStock.getCombination1()) && stockTienda.getCombination2Code().equals(storeStock.getCombination2())) {
					stockTienda.setLogisticStock(storeStock.getStock());
				}
    		}
    	}
    }

	protected void clearForm(){
    	tfItemDes.setText("");
    	itemImage.setImage(null);
    	ObservableList<StockRow> loggedStoreEmptyList = FXCollections.observableArrayList(new ArrayList<StockRow>());
    	tbLoggedStoreStock.setItems(loggedStoreEmptyList);
    	ObservableList<StockRow> otherStoresEmptyList = FXCollections.observableArrayList(new ArrayList<StockRow>());
    	tbOthersStock.setItems(otherStoresEmptyList);
    }
	
	public void actionStoresExpressionInfo(){
		openModalScene(StoresExpressionInfoController.class);
	}

	@Override
	public Class<? extends SceneView> getSceneViewClass() {
		return StockAvailabilityModalView.class;
	}
	
	@Override
	public void onClose() {
		if (ComerzziaThreadSession.getApiKey() != null) {
			ComerzziaThreadSession.setComerzziaSession(null);
			ComerzziaThreadSession.setApiKey(null);
		} 
		
		catalog = null;
		super.onClose();
	}

}
