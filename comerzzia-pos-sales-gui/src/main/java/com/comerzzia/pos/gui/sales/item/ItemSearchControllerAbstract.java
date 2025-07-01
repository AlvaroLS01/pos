package com.comerzzia.pos.gui.sales.item;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.catalog.facade.model.CatalogItem;
import com.comerzzia.catalog.facade.model.CatalogItemDetail;
import com.comerzzia.catalog.facade.model.CatalogRateItem;
import com.comerzzia.catalog.facade.model.filter.PaginatedRequest;
import com.comerzzia.catalog.facade.model.filter.PaginatedResponse;
import com.comerzzia.catalog.facade.model.impl.CatalogItemImpl;
import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.catalog.facade.service.CatalogRateServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.BasketCalculateRequest;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.NewBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCustomer;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItemItemData;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketRateItem;
import com.comerzzia.omnichannel.facade.model.basket.promotions.BasketLinePromotion;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.basket.BasketManagerBuilder;
import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScannerDataReaded;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.components.itemimage.ItemImage;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.stockavailability.StockAvailabilityActionController;
import com.comerzzia.pos.core.gui.stockavailability.StockAvailabilityController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.SesionInitException;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.retail.items.search.ItemSearchController;
import com.comerzzia.pos.gui.sales.retail.items.search.ItemSearchForm;
import com.comerzzia.pos.gui.sales.retail.items.search.ItemSearchResultDto;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.Data;


public abstract class ItemSearchControllerAbstract<T extends BasketManager<?, ?>> extends SceneController implements Initializable {
    protected static final Logger log = Logger.getLogger(ItemSearchController.class.getName());
    
    @Autowired
    protected Session session;

    public static final String PARAM_INPUT_CUSTOMER = "PARAM_INPUT_CUSTOMER";
    public static final String PARAM_INPUT_RATE_CODE = "PARAM_INPUT_RATE_CODE";
    public static final String PARAM_INPUT_CATALOG = "CATALOG";
    public static final String PARAM_IS_STOCK = "PARAM_IS_STOCK";
    
    public static final String PARAM_OUTPUT_ITEM_CODE = "output_item_code";
    public static final String PARAM_OUTPUT_COMBINATION_1_CODE = "output_combination_1_code";
    public static final String PARAM_OUTPUT_COMBINATION_2_CODE = "output_combination_2_code";
    public static final String PARAM_OUTPUT_BARCODE = "output_barcode";
    
    public static final String PARAM_MODAL = "modal";

    @FXML
    protected TextField tfItemCode;
    @FXML
    protected TextField tfItemDes;
    @FXML
	protected ItemImage itemImage;
    @FXML
    protected Label lbError, lbCombination1Code, lbCombination2Code;
    
    @FXML
    protected TableView<ItemSearchResultDto> tbItems;
    @FXML
    protected TableColumn<ItemSearchResultDto, String> tcItemCode;
    @FXML
    protected TableColumn<ItemSearchResultDto, String> tcItemDes;
    @FXML
    protected TableColumn<ItemSearchResultDto, String> tcCombination1Code;
    @FXML
    protected TableColumn<ItemSearchResultDto, String> tcCombination2Code;
    @FXML
    protected TableColumn<ItemSearchResultDto, String> tcPrice;
    @FXML
    protected TableColumn<ItemSearchResultDto, String> tcPromotionPrice;
    
    @FXML
    protected TextField tfDetailItemCode, tfDetailItemDes, tfDetailCombination1Code, tfDetailCombination2Code;
    @FXML
    protected VBox promotionPanel;
    
    @FXML
	protected HBox hbStocks;   
    @FXML
    protected Button btStocksStocks;
    @FXML
    protected Button btStocksStocksByModel;
    @FXML
    protected Button btStocksStocksByCombination1Code;
    @FXML
    protected Button btStocksStocksByCombination2Code;
    
	@FXML
	protected HBox hbSales;
    @FXML
    protected Button btStocksSales;
    @FXML
    protected Button btStocksSalesByModel;
    @FXML
    protected Button btStocksSalesByCombination1Code;
    @FXML
    protected Button btStocksSalesByCombination2Code;
    @FXML
    protected Button btCancel;

    protected ObservableList<ItemSearchResultDto> resultRows;

    protected ItemSearchForm frItemSearch;

    protected Runnable searchAction;
    
    protected String searchRateCode;
    
    protected BasketCustomer searchCustomer;
        
    @Autowired
    protected VariableServiceFacade variableService;
    
    @Autowired
    protected ModelMapper modelMapper;
    
	protected WeakReference<Catalog> catalog;
    
    protected T basketManager;

    protected Boolean isStock;
    
    protected Boolean modal;
    
    protected boolean showingLyCustomerPromotion = false;
    
    protected boolean showingParcialData = false;
    
    protected ScannerObserver scannerObserver;
    
    
    public class ScannerObserver implements Observer {

		@Override
		public void update(Observable o, Object arg) {
			if (arg instanceof DeviceScannerDataReaded && !tfItemCode.isDisabled()) {
				DeviceScannerDataReaded scanData = (DeviceScannerDataReaded)arg;
								
				Platform.runLater(new Runnable(){
					@Override
					public void run() {
						tfItemCode.setText(scanData.getScanData());
									
						actionSearch();
					}
				});		
			}
			
		}
		
	}
    
    @Data
    public class ArticulosParamBuscar {
        private String codigo; // puede ser código de artículo o código de barras
        private String descripcion;
        private Boolean activo;
    }

    @SuppressWarnings("unchecked")
	@Override
    public void initialize(URL url, ResourceBundle rb) {		
        log.debug("initialize() - Inicializando ventana...");

        // Message for table without content
        tbItems.setPlaceholder(new Label(""));

        // Listener for selected row in list
        tbItems.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemSearchResultDto>() {
            @Override
            public void changed(ObservableValue<? extends ItemSearchResultDto> observable, ItemSearchResultDto oldValue, ItemSearchResultDto newValue) {
				if (itemImage != null && newValue != null && AppConfig.getCurrentConfiguration().getImagesPath() != null) {
					itemImage.showImage(newValue.getItemCode());
				}
                selectedLineChanged();
            }
        });

        frItemSearch = SpringContext.getBean(ItemSearchForm.class);

        // Assign form fields to the to be validated components
        frItemSearch.setFormField("itemCode", tfItemCode);
        frItemSearch.setFormField("itemDes", tfItemDes);

        // Stylish centered right
        tcItemCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbItems", "tcItemCode", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcItemDes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbItems", "tcItemDes", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCombination1Code.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbItems", "tcCombination1Code", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCombination2Code.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbItems", "tcCombination2Code", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcPrice.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbItems", "tcPrice", null,CellFactoryBuilder.RIGHT_ALIGN_STYLE));
        tcPromotionPrice.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbItems", "tcPromotionPrice", null,CellFactoryBuilder.RIGHT_ALIGN_STYLE));

        // Assign rows to table
        resultRows = FXCollections.observableList(new ArrayList<ItemSearchResultDto>());
        tbItems.setItems(resultRows);

        // Factory for every cell to increase performance
        tcItemCode.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemSearchResultDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ItemSearchResultDto, String> cdf) {
                return cdf.getValue().getItemCodeProperty();
            }
        });
        tcItemDes.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemSearchResultDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ItemSearchResultDto, String> cdf) {
                return cdf.getValue().getDescriptionProperty();
            }
        });
        tcCombination1Code.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemSearchResultDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ItemSearchResultDto, String> cdf) {
                return cdf.getValue().getCombination1CodeProperty();
            }
        });
        tcCombination2Code.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemSearchResultDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ItemSearchResultDto, String> cdf) {
                return cdf.getValue().getCombination2CodeProperty();
            }
        });
        tcPrice.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemSearchResultDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ItemSearchResultDto, String> cdf) {
                return cdf.getValue().getPvpProperty();
            }
        });
        tcPromotionPrice.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemSearchResultDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ItemSearchResultDto, String> cdf) {
                return cdf.getValue().getPromotionPvpProperty();
            }
        });

        log.debug("initialize() - Configuración de la tabla");
        if (session.getApplicationSession().isDesglose1Activo()) { // If combination 1 code is active, set title
            String combination1CodeTitle = I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO));
        	tcCombination1Code.setText(combination1CodeTitle);
            lbCombination1Code.setText(combination1CodeTitle);
            tfDetailCombination1Code.setVisible(true);
        }
        else { // If combination 1 code is not active, compact line
            tcCombination1Code.setVisible(false);
            lbCombination1Code.setText("");
            tfDetailCombination1Code.setVisible(false);
        }
        if (session.getApplicationSession().isDesglose2Activo()) { // If combination 2 code is active, set title
            String combination2CodeTitle = I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO));
        	tcCombination2Code.setText(combination2CodeTitle);
            lbCombination2Code.setText(combination2CodeTitle);
            tfDetailCombination2Code.setVisible(true);
        }
        else { // If combination 2 code is not active, compact line
            tcCombination2Code.setVisible(false);
            lbCombination2Code.setText("");
            tfDetailCombination2Code.setVisible(false);
        }
    
    }

    @Override
    public void initializeComponents() {
        log.debug("initializeComponents()");
        try {
			addEnterTableEvent(tbItems);
			addNavegationTableEvent(tbItems);
		} catch (Exception e) {
			log.error("initializeComponents() - An error was thrown");
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Error cargando pantalla. Para mas información consulte el log."));
		}

    }
    
    @Override
    public void onClose() {
		if (scannerObserver != null) {
			Devices.getInstance().getScanner().deleteObserver(scannerObserver);
		}
		
    	super.onClose();
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Class<? extends T> getBasketClass() {
		return ((Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}
    
	protected void initializeManager() throws SesionInitException {
    	basketManager = BasketManagerBuilder.build(getBasketClass(), session.getApplicationSession().getStorePosBusinessData());
	}
    
	/**
	 * Sets a specific order in the navigation sequence. By default screen navigation is taken
	 */
	protected void initializeFocusControl() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSceneOpen() throws InitializeGuiException {
    	scannerObserver = new ScannerObserver();
		
		Devices.getInstance().getScanner().addObserver(scannerObserver);

		log.debug("onSceneOpen() - Inicializando formulario...");
		
		basketManager = (T)sceneData.get(BasketItemizationControllerAbstract.BASKET_KEY);
		
		this.catalog = (WeakReference<Catalog>) sceneData.get(PARAM_INPUT_CATALOG);
		
		if (basketManager == null) {
			try {
				initializeManager();
			} catch (SesionInitException e) {
				throw new InitializeGuiException(e);
			}
		}
		
		resultRows = FXCollections.observableList(new ArrayList<ItemSearchResultDto>());
		tbItems.setItems(resultRows);

		tfItemCode.setText("");
		tfItemDes.setText("");
		tfDetailItemDes.setText("");
		tfDetailItemCode.setText("");
		tfDetailCombination1Code.setText("");
		tfDetailCombination2Code.setText("");

		clearPromotionPanel();

		searchRateCode = (String) sceneData.get(PARAM_INPUT_RATE_CODE);
		if(searchRateCode == null) {
			searchRateCode = session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getRateCode();
		}
		searchCustomer = (BasketCustomer) sceneData.get(PARAM_INPUT_CUSTOMER);
		if(searchCustomer == null) {
			searchCustomer = modelMapper.map(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer(), BasketCustomer.class);
		}
		isStock = (Boolean) sceneData.get(PARAM_IS_STOCK);
		if(isStock == null) {
			isStock = Boolean.TRUE;
		}

		if (itemImage != null) {
			itemImage.setImage(null);
		}
		if (isStock) {
			hbSales.setVisible(Boolean.FALSE);
			hbSales.setManaged(Boolean.FALSE);
			hbStocks.setVisible(Boolean.TRUE);
			hbStocks.setManaged(Boolean.TRUE);
			if (session.getApplicationSession().isDesglose1Activo()) { // If combination 1 code is active, set title
				btStocksStocksByCombination1Code.setText("Stocks por " + I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO).toLowerCase()));
			}
			if (session.getApplicationSession().isDesglose2Activo()) { // If combination 2 code is active, set title
				btStocksStocksByCombination2Code.setText("Stocks por " + I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO).toLowerCase()));
			}
		}
		else {
			hbSales.setVisible(Boolean.TRUE);
			hbSales.setManaged(Boolean.TRUE);
			hbStocks.setVisible(Boolean.FALSE);
			hbStocks.setManaged(Boolean.FALSE);
			if (session.getApplicationSession().isDesglose1Activo()) { // If combination 1 code is active, set title
				btStocksSalesByCombination1Code.setText("Stocks por " + I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO).toLowerCase()));
			}
			if (session.getApplicationSession().isDesglose2Activo()) { // If combination 2 code is active, set title
				btStocksSalesByCombination2Code.setText("Stocks por " + I18N.getText(variableService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO).toLowerCase()));
			}
		}
		modal = (Boolean) sceneData.get(PARAM_MODAL);
		if(modal == null) {
			modal = Boolean.FALSE;
		}
		if (isStock && modal) {
			showButton(btCancel);
		}
		else {
			hideButton(btCancel);
		}
	}

	protected void clearPromotionPanel() {
		promotionPanel.getChildren().clear();
    }

    @Override
    public void initializeFocus() {
        tfItemCode.requestFocus();
        tfItemCode.selectAll();
    }

	protected void selectedLineChanged() {
		ItemSearchResultDto selectedRow = tbItems.getSelectionModel().getSelectedItem();

		clearPromotionPanel();

		if (selectedRow == null) {
			hideStockButtons();
			return;
		}
		
		tfDetailItemCode.setText(selectedRow.getItemCode());
		tfDetailItemDes.setText(selectedRow.getDescription());
		tfDetailCombination1Code.setText(selectedRow.getCombination1Code());
		tfDetailCombination2Code.setText(selectedRow.getCombination2Code());
		
		showPromotionData(selectedRow);
		
		loadStockButtons(selectedRow);
	}
	
	protected void hideStockButtons() {
		if (isStock) {
			hideButton(btStocksStocks);
			hideButton(btStocksStocksByModel);
			hideButton(btStocksStocksByCombination1Code);
			hideButton(btStocksStocksByCombination2Code);
		}
		else {
			hideButton(btStocksSales);
			hideButton(btStocksSalesByModel);
			hideButton(btStocksSalesByCombination1Code);
			hideButton(btStocksSalesByCombination2Code);		
		}
    }

	protected void loadStockButtons(ItemSearchResultDto selectedRow) {
		if (isStock) {
			showButton(btStocksStocksByModel);

			if (selectedRow.getCombination1Code() != null && !selectedRow.getCombination1Code().isEmpty() && !"*".equals(selectedRow.getCombination1Code()) && selectedRow.getCombination2Code() != null && !selectedRow.getCombination2Code().isEmpty() && !"*".equals(selectedRow.getCombination2Code())) {
				showButton(btStocksStocks);
			}
			else {
				hideButton(btStocksStocks);
			}

			if (selectedRow.getCombination1Code() != null && !selectedRow.getCombination1Code().isEmpty() && !"*".equals(selectedRow.getCombination1Code())) {
				showButton(btStocksStocksByCombination1Code);
			}
			else {
				hideButton(btStocksStocksByCombination1Code);
			}
			if (selectedRow.getCombination2Code() != null && !selectedRow.getCombination2Code().isEmpty() && !"*".equals(selectedRow.getCombination2Code())) {
				showButton(btStocksStocksByCombination2Code);
			}
			else {
				hideButton(btStocksStocksByCombination2Code);
			}
		}
		else {
			showButton(btStocksSalesByModel);

			if (selectedRow.getCombination1Code() != null && !selectedRow.getCombination1Code().isEmpty() && !"*".equals(selectedRow.getCombination1Code()) && selectedRow.getCombination2Code() != null && !selectedRow.getCombination2Code().isEmpty() && !"*".equals(selectedRow.getCombination2Code())) {
				showButton(btStocksSales);
			}
			else {
				hideButton(btStocksSales);
			}
			if (selectedRow.getCombination1Code() != null && !selectedRow.getCombination1Code().isEmpty() && !"*".equals(selectedRow.getCombination1Code())) {
				showButton(btStocksSalesByCombination1Code);
			} 
			else {
				hideButton(btStocksSalesByCombination1Code);
			}
			if (selectedRow.getCombination2Code() != null && !selectedRow.getCombination2Code().isEmpty() && !"*".equals(selectedRow.getCombination2Code())) {
				showButton(btStocksSalesByCombination2Code);
			}
			else {
				hideButton(btStocksSalesByCombination2Code);
			}		
		}
    }

	protected void showButton(Button bt) {
		bt.setAlignment(Pos.CENTER);
		bt.setVisible(Boolean.TRUE);
		bt.setManaged(Boolean.TRUE);
    }
	
	protected void hideButton(Button bt) {
		bt.setVisible(Boolean.FALSE);
		bt.setManaged(Boolean.FALSE);
    }
	
    protected void showPromotionData(ItemSearchResultDto selectedRow) {
    	if (selectedRow.getPromotion() == null) return;
    	
    	BasketLinePromotion basketLinePromotion = selectedRow.getPromotion().getValue();
    	
		String promotionDescription = basketLinePromotion.getBasketPromotion().getPromotionText();
			
		String promotionType = null;
		if(showingLyCustomerPromotion && basketLinePromotion.getBasketPromotion().getPromotionExclusive()) {
			promotionType = I18N.getText("Dato promocion fidelizado:");
		}
		else if (!showingLyCustomerPromotion && !basketLinePromotion.getBasketPromotion().getPromotionExclusive()) {
			promotionType = I18N.getText("Datos promocion NO fidelizado:");
		}
		
		if(StringUtils.isNotBlank(promotionType)) {
			buildPromotion(selectedRow, promotionType, promotionDescription);
		}
	}
    
    protected void buildPromotion(ItemSearchResultDto selectedRow, String promotionType, String promotionDescription) {
		Label promotionTypeLabel = buildPromotionTypeLabel(promotionType);
		Label promotionDescriptionLabel = new Label(promotionDescription);
		
		FlowPane flowPane = buildPromotionFlowPane();
		flowPane.getChildren().add(promotionTypeLabel);
		flowPane.getChildren().add(promotionDescriptionLabel);
		promotionPanel.getChildren().add(flowPane);
    }

	protected void completePane() {
		int currentPanels = promotionPanel.getChildren().size();
		for(int i = 0 ; i < 4 - currentPanels ; i++) {
			promotionPanel.getChildren().add(buildPromotionFlowPane());
		}
    }

	protected String getPromotionPvp(ItemSearchResultDto lineaSeleccionada) {
		String pvpPromocion = null;
		if (promotionShowsPrice(lineaSeleccionada.getPromotion().get()) || promotionShowsDiscount(lineaSeleccionada.getPromotion().get())) {
			pvpPromocion = FormatUtils.getInstance().formatAmount(lineaSeleccionada.getLine().get().getPriceWithTaxes());
		}
	    return pvpPromocion;
    }
	
	protected FlowPane buildPromotionFlowPane() {
	    FlowPane flowPane = new FlowPane();
	    flowPane.setPrefHeight(120.0);
	    flowPane.setHgap(5.0);
	    flowPane.setAlignment(Pos.CENTER);
	    return flowPane;
    }

	protected Label buildPromotionTypeLabel(String promotionType) {
	    Label promotionTypeLabel = new Label(promotionType);
	    promotionTypeLabel.getStyleClass().add("bold");
	    promotionTypeLabel.setPrefWidth(300.0);
	    promotionTypeLabel.setMaxWidth(300.0);
	    promotionTypeLabel.setAlignment(Pos.CENTER_RIGHT);
	    return promotionTypeLabel;
    }

	protected boolean promotionShowsDiscount(BasketLinePromotion promocion) {
//		PromotionAbstract promotionDetail = promocion.get getBasketPromotion().getPromotion();
//		String presentaDescuentoFinalStr = promotionDetail.getPromotionHdr().getPromotionType().getConfigurationMap().get("PresentaDescuentoFinal");
//		
//		return ("S".equals(presentaDescuentoFinalStr) || "true".equals(presentaDescuentoFinalStr));
		return true;
	}
	
	protected boolean promotionShowsPrice(BasketLinePromotion promocion) {
//		PromotionAbstract promotionDetail = promocion.getBasketPromotion().getPromotion();
//		String presentaPrecioFinalStr = promotionDetail.getPromotionHdr().getPromotionType().getConfigurationMap().get("PresentaPrecioFinal");
//		
//		return ("S".equals(presentaPrecioFinalStr) || "true".equals(presentaPrecioFinalStr));		
		return true;
	}
    
    public void refreshScreenData() {
        resultRows.clear();
        tfItemCode.setText("");
        tfItemDes.setText("");
        tfDetailItemCode.setText("");
        tfDetailItemDes.setText("");
        tfDetailCombination1Code.setText("");
        tfDetailCombination2Code.setText("");
    }

    @FXML
    public void actionSearchKeyboard(KeyEvent event) {
        log.trace("actionSearchKeyboard()");

        if (event.getCode() == KeyCode.ENTER && !event.isControlDown()) {
            actionSearch();
        }
        else if(event.getCode() == KeyCode.DOWN && !event.isControlDown() && tbItems.getItems().size() > 0) {
			tbItems.requestFocus();
		}
    }

    @FXML
    public void actionSearch() {
        log.trace("actionSearch()");

        // Clear previous search result
        resultRows.clear();
        
        // Clear item details from possible previous selection
        tfDetailItemCode.setText("");
        tfDetailItemDes.setText("");
        tfDetailCombination1Code.setText("");
        tfDetailCombination2Code.setText("");
        
        frItemSearch.setItemCode(tfItemCode.getText());
        frItemSearch.setItemDes(tfItemDes.getText());
        
        if (validateItemSearchForm()) {
            ArticulosParamBuscar itemSearchParams = new ArticulosParamBuscar();
            itemSearchParams.setCodigo(frItemSearch.getItemCode().toUpperCase());
            itemSearchParams.setDescripcion(frItemSearch.getItemDes());
            itemSearchParams.setActivo(true);
            
            ItemSearchTask itemSearchTask = new ItemSearchTask(itemSearchParams);
            itemSearchTask.start();
        }
        tfItemCode.requestFocus();
        tfItemCode.selectAll();
    }
    
	protected boolean validateItemSearchForm() {
		// Clear possible form errors
		frItemSearch.clearErrorStyle();

		// Clear possible previous error
		lbError.setText("");

		// Validate item search form
		Set<ConstraintViolation<ItemSearchForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frItemSearch);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<ItemSearchForm> next = constraintViolations.iterator().next();
			frItemSearch.setErrorStyle(next.getPropertyPath(), true);
			frItemSearch.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			return false;
		}
		return true;

	}
    
    protected class ItemSearchTask extends BackgroundTask<BasketPromotable<? extends BasketItem>>{
        private final ArticulosParamBuscar itemSearchParams;
                
        public ItemSearchTask(ArticulosParamBuscar itemSearchParams){
            this.itemSearchParams = itemSearchParams;
        }
        
        @Override        
        public BasketPromotable<? extends BasketItem> execute() throws Exception {
        	showingParcialData = false;
        	showingLyCustomerPromotion = false;
        	        	
        	BasketCalculateRequest request = new BasketCalculateRequest();
        	request.setItems(new ArrayList<>());
        	
        	if (session.getApplicationSession().isDesglosesActivos()) {
        		CatalogItemImpl itemParam = getCatalogItemParams();
        		PaginatedResponse<? extends CatalogItem> itemList = getCatalog().getCatalogItemService().findPageDetail(itemParam, new PaginatedRequest(0, 50, "itemCode"));
        		itemList.getContent().forEach(item -> addItem(request, item));
        		
        		showingParcialData = itemList.getTotalPages() > 1;
        	}
        	else {
	        	// Single result
				if (StringUtils.isNotBlank(itemSearchParams.getCodigo())) {
	        		CatalogItemDetail item = getCatalog().getCatalogItemService().findByBarcode(itemSearchParams.getCodigo());
	        		        		
	        		if (item != null) {
	        			NewBasketItemRequest itemRequest = new NewBasketItemRequest();
	        			itemRequest.setItemCode(item.getItemCode());
	        			itemRequest.setCombination1Code(item.getCombination1Code());
	        			itemRequest.setCombination2Code(item.getCombination2Code());
	        			itemRequest.setBarcode(item.getBarcode());
	        			itemRequest.setItemData(modelMapper.map(item, BasketItemItemData.class));
	        			itemRequest.setQuantity(BigDecimal.ONE);
	        			
	        			if (item.getGenericItem()) {
	        				itemRequest.setDirectPrice(BigDecimal.ZERO);
	        			}
	        			if(item.getWeightRequired()) {
	        				itemRequest.setWeight(BigDecimal.ONE);
	        			}
	        			
	        			if (item.getWeightRequired()) {
	        				itemRequest.setWeight(BigDecimal.ONE);
	        			}
	        			        			
	        			if (item.getRateItem() != null) {
	        				itemRequest.setRateItem(modelMapper.map(item.getRateItem(), BasketRateItem.class));        			
	        			
	        				request.getItems().add(itemRequest);
	        			}
	        		}
	        	} 
				else {
	        		// List of results
	        		CatalogItemImpl itemParam = getCatalogItemParams();
					PaginatedResponse<? extends CatalogItem> itemList = getCatalog().getCatalogItemService().findPage(itemParam, new PaginatedRequest(0, 50, "itemCode"));
	        		itemList.getContent().forEach(item -> addItem(request, item));
					
					showingParcialData = itemList.getTotalPages() > 1;
				}
        	}
        	        	
        	if (basketManager.getBasketTransaction() != null) {
        		showingLyCustomerPromotion = basketManager.getBasketTransaction().getHeader().getLoyalCustomer() != null;
        		
        		request.setLoyaltySettings(basketManager.getBasketTransaction().getHeader().getLoyalCustomer());
        	}
        	
        	return BasketManagerBuilder.basketCalculate(RetailBasketManager.class, session.getApplicationSession().getStorePosBusinessData(), request, getCatalog());
        }
        
        protected CatalogItemImpl getCatalogItemParams() {
        	CatalogItemImpl itemParam = new CatalogItemImpl();
			if (StringUtils.isNotBlank(itemSearchParams.getCodigo())) {
				itemParam.setItemCode(itemSearchParams.getCodigo());
			}
			if (StringUtils.isNotBlank(itemSearchParams.getDescripcion())) {
				itemParam.setItemDes(itemSearchParams.getDescripcion());
			}
    		itemParam.setActive(itemSearchParams.getActivo());
    		return itemParam;
        }
        
        protected void addItem(BasketCalculateRequest request, CatalogItem item) {
        	CatalogRateItem rateItem = null;
			if (item instanceof CatalogItemDetail) {
				rateItem = ((CatalogItemDetail) item).getRateItem();
			}
			else {
				try {
					rateItem = getCatalog().getCatalogRateService().getRateDetail(item.getItemCode(), "*", "*", CatalogRateServiceFacade.DEFAULT_UNIT_MEASURE_CODE);
				} catch (NoSuchElementException ignore) {}
			}

			if (rateItem == null) return;

			NewBasketItemRequest itemRequest = new NewBasketItemRequest();
			itemRequest.setItemCode(item.getItemCode());
			itemRequest.setItemData(modelMapper.map(item, BasketItemItemData.class));        			
			itemRequest.setRateItem(modelMapper.map(rateItem, BasketRateItem.class));
			itemRequest.setQuantity(BigDecimal.ONE);
			
			if (item.getGenericItem()) {
				itemRequest.setDirectPrice(BigDecimal.ZERO);
			}
			
			if(item.getWeightRequired()) {
				itemRequest.setWeight(BigDecimal.ONE);
			}

			if (item instanceof CatalogItemDetail) {
				itemRequest.setBarcode(((CatalogItemDetail) item).getBarcode());
			}
			
			request.getItems().add(itemRequest);
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            
            itemSearchSucceeded(getValue());            
        }

        @Override
        protected void failed() {
        	super.failed();
            log.error("actionSearch() - Error consultando artículos", getException());
            DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(getCMZException().getMessage());
            
        }
    }

    protected void itemSearchSucceeded(BasketPromotable<? extends BasketItem> basket) {
	    List<ItemSearchResultDto> resRows = new ArrayList<>();
	                
	    if (basket.getLines().size() == 0) {
	        tbItems.setPlaceholder(new Label(I18N.getText("No se han encontrado resultados")));
	    }
	    
		for (BasketItem basketItem : basket.getLines()) {
	        resRows.add(new ItemSearchResultDto(basketItem));
	    }

	    resultRows.addAll(resRows);
	    
	    tbItems.getSelectionModel().select(0);
	    tbItems.getFocusModel().focus(0);
	    
	    if (showingParcialData) {
	    	DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Se está mostrando un resultado parcial. Debe aplicar una búsqueda más limitada"));
	    }
    }
    
    @Override
	public void actionTableEventEnter(String idTabla) {
    	actionSendItemToRetailBasketItemization();
	}

    public void acceptItemKeyboard(KeyEvent event) {
        log.trace("acceptItemKeyboard(KeyEvent event) - Aceptar");
        if (event.getCode() == KeyCode.ENTER) {
            actionSendItemToRetailBasketItemization();
        }
    }
    
	public void acceptItemDoubleClick(MouseEvent event) {
		log.debug("acceptItemDoubleClick() - Acción aceptar");
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				if (isStock) {
					actionQueryStocks();
				}
				else {
					actionSendItemToRetailBasketItemization();
				}
			}
		}
	}
	
	public void actionBtAccept(ActionEvent event) {
        log.debug("actionBtAccept(ActionEvent event) - Aceptar");
        actionSendItemToRetailBasketItemization();
    }
	
    public void actionBtCancel(){
    	closeCancel();
    }
	
	public void actionQueryStocks() {
		if (isItemSelected("actionQueryStocks")) {
			ItemSearchResultDto lineaSeleccionada = tbItems.getSelectionModel().getSelectedItem();
			if (lineaSeleccionada != null) {
				sceneData.put(StockAvailabilityController.PARAM_ITEM_CODE, lineaSeleccionada.getItemCode());
				sceneData.put(StockAvailabilityController.PARAM_COMBINATION_1_CODE, lineaSeleccionada.getCombination1Code());
				sceneData.put(StockAvailabilityController.PARAM_COMBINATION_2_CODE, lineaSeleccionada.getCombination2Code());
			}
			queryStocks();
		}		
	}
	
	public void actionQueryStocksByModel() {
		if (isItemSelected("actionQueryStocksByModel")) {
			ItemSearchResultDto lineaSeleccionada = tbItems.getSelectionModel().getSelectedItem();
			if (lineaSeleccionada != null) {
				sceneData.put(StockAvailabilityController.PARAM_ITEM_CODE, lineaSeleccionada.getItemCode());
				sceneData.put(StockAvailabilityController.PARAM_COMBINATION_1_CODE, "");
				sceneData.put(StockAvailabilityController.PARAM_COMBINATION_2_CODE, "");
			}
			queryStocks();
		}		
	}

	public void actionQueryStocksByCombination1Code() {
		if (isItemSelected("actionQueryStocksByCombination1Code")) {
			ItemSearchResultDto lineaSeleccionada = tbItems.getSelectionModel().getSelectedItem();
			if (lineaSeleccionada != null) {
				sceneData.put(StockAvailabilityController.PARAM_ITEM_CODE, lineaSeleccionada.getItemCode());
				sceneData.put(StockAvailabilityController.PARAM_COMBINATION_1_CODE, lineaSeleccionada.getCombination1Code());
				sceneData.put(StockAvailabilityController.PARAM_COMBINATION_2_CODE, "");
			}
			queryStocks();
		}		
	}

	public void actionQueryStocksByCombination2Code() {
		if (isItemSelected("actionQueryStocksByCombination2Code")) {
			ItemSearchResultDto lineaSeleccionada = tbItems.getSelectionModel().getSelectedItem();
			if (lineaSeleccionada != null) {
				sceneData.put(StockAvailabilityController.PARAM_ITEM_CODE, lineaSeleccionada.getItemCode());
				sceneData.put(StockAvailabilityController.PARAM_COMBINATION_1_CODE, "");
				sceneData.put(StockAvailabilityController.PARAM_COMBINATION_2_CODE, lineaSeleccionada.getCombination2Code());
			}
			queryStocks();
		}
	}

	protected void queryStocks() {
		openScene(StockAvailabilityActionController.class);
	}

    /**
     * Action for sending item to retail basket itemization
     */
    public void actionSendItemToRetailBasketItemization() {      
        if (isItemSelected("actionSendItemToRetailBasketItemization")) {
        	ItemSearchResultDto selectedItem = tbItems.getSelectionModel().getSelectedItem();
        	
            HashMap<String, Object> callbackData = new HashMap<String, Object>();
            // Set map output parameters
            callbackData.put(ItemSearchController.PARAM_OUTPUT_ITEM_CODE, selectedItem.getItemCode());
            callbackData.put(ItemSearchController.PARAM_OUTPUT_COMBINATION_1_CODE, selectedItem.getCombination1Code());
            callbackData.put(ItemSearchController.PARAM_OUTPUT_COMBINATION_2_CODE, selectedItem.getCombination2Code());
            callbackData.put(ItemSearchController.PARAM_OUTPUT_BARCODE, selectedItem.getLine().get().getBarcode());        

            // Close window
            closeSuccess(callbackData);
        }
    }
    
    protected boolean isItemSelected(String actionName) {
    	boolean itemIsSelected = true;
    	
    	int itemIndex = tbItems.getSelectionModel().getSelectedIndex();
        if (itemIndex < 0) {
            log.debug(actionName + "() - No hay artículo seleccionado");
            DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se ha seleccionado un artículo."));
            itemIsSelected = false;
        }
        
        return itemIsSelected;
    }
    
    protected Catalog getCatalog() {
    	if (catalog == null || catalog.get() == null) {
    		catalog = new WeakReference<>(applicationSession.getValidCatalog());
    	}
    	
    	return catalog.get();
    }
	
}
