package com.comerzzia.pos.gui.sales.retailrefund.originalitems;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.catalog.facade.model.CatalogItemDetail;
import com.comerzzia.catalog.facade.model.filter.CatalogItemSearch;
import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.InvalidBasketItemsRequest;
import com.comerzzia.omnichannel.facade.model.basket.NewBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCustomer;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItemItemData;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItemOriginDocumentLine;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketRateItem;
import com.comerzzia.omnichannel.facade.model.documents.OriginalDoc;
import com.comerzzia.omnichannel.facade.model.documents.RefundedItemLine;
import com.comerzzia.omnichannel.facade.model.documents.SaleDocLine;
import com.comerzzia.omnichannel.facade.service.basket.RefundBasketManager;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.normal.ActionButtonNormalComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.services.balancecard.BalanceCardService;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

@Controller
@CzzScene
public class OriginRefundDocumentController extends SceneController implements ButtonsGroupController{
    
    protected static final Logger log = Logger.getLogger(OriginRefundDocumentController.class.getName());
    
    // botonera de acciones de tabla
    protected ButtonsGroupComponent botoneraOpcionesSeleccion;
    
    @FXML
    protected WebView wvMain;
    
    @FXML
    protected HBox hbDoc, hbLoyalCustomer, hbCustomer;
    @FXML
    protected Label lbDocNum, lbDocType, lbDocDate, lbDocStore, lbDocCash;
    @FXML
    protected Label lbLoyalCustomer, lbLoyalCard, lbLoyalBalance;
    @FXML
    protected Label lbCustomer, lbCustomerCode, lbCustomerDocType, lbCustomerDocN;
    
    @FXML
    protected VBox vbButtonsPane;
    
    @FXML
    protected Button btAccept, btCancel;
            
    protected RefundBasketManager<?,?> basketManager;
    
    protected WeakReference<Catalog> catalog;
    
    protected boolean doneChanges = false;
    
    protected List<ProvisionalRefundItem> provisionalLines;
    
    @Autowired
    protected VariableServiceFacade variablesServices;
    
    @Autowired
    protected Session session;
    
    @Autowired
    protected BalanceCardService giftCardService;
    
    @Autowired
    protected ModelMapper modelMapper;
    
    protected String combination1, combination2;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initializeComponents() {

        try {
            // Botonera de Tabla
            log.debug("initializeComponents() - Carga de acciones de botonera de tabla de devoluciones");
            
            ButtonsGroupModel panelBotoneraBean = getSceneView().loadButtonsGroup();
            botoneraOpcionesSeleccion = new ButtonsGroupComponent(panelBotoneraBean, vbButtonsPane.getPrefWidth(), vbButtonsPane.getPrefHeight(), this, ActionButtonNormalComponent.class);
            vbButtonsPane.getChildren().add(botoneraOpcionesSeleccion);
            
            wvMain.setFocusTraversable(false);
			vbButtonsPane.setFocusTraversable(false);
			
			//Comprobamos si la tineda tiene desgloses 
			if (session.getApplicationSession().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
				 combination1 = I18N.getText(variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO));
			}
			
			if (session.getApplicationSession().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
				combination2 = I18N.getText(variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO));
			}
        }
        catch (LoadWindowException | InitializeGuiException ex) {
            log.error("initializeComponents() - Error inicializando pantalla de devolución de artículos");
            DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Error cargando pantalla. Para mas información consulte el log."));
        }
        
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onSceneOpen() throws InitializeGuiException {
        Map<String, Object> data = sceneData;
        basketManager = (RefundBasketManager<?,?>) data.get(BasketItemizationControllerAbstract.BASKET_KEY);
		catalog = (WeakReference<Catalog>) data.get(BasketItemizationControllerAbstract.CATALOG_KEY);
		
        hbLoyalCustomer.setVisible(true);
        hbLoyalCustomer.setManaged(true);
		
		provisionalLines = basketManager.getRefundData().getOriginalSaleDoc().getLines().stream()
				.map(ln -> new ProvisionalRefundItem(ln, 
						basketManager.getRefundData().getRefundedItemLine(ln.getLineId()),
						basketManager.getBasketRefundedQuantity(ln.getLineId())
						)
						).
				collect(Collectors.toList());
        
		
		OriginalDoc datosOrigen = basketManager.getRefundData().getOriginalSaleDoc();
		lbDocNum.setText(datosOrigen.getSalesDocCode());
		lbDocType.setText(datosOrigen.getDocTypeCode());
		lbDocDate.setText(FormatUtils.getInstance().formatDate(datosOrigen.getSalesDocDate()));
		lbDocStore.setText(datosOrigen.getStoreCode());
		lbDocCash.setText(datosOrigen.getTillCode());

		BasketLoyalCustomer datosFidelizado = basketManager.getBasketTransaction().getHeader().getLoyalCustomer();
		if (datosFidelizado != null) {
			String lyCustomerText = (datosFidelizado.getName()!=null?datosFidelizado.getName():"") + " " + 
					(datosFidelizado.getLastName()!=null?datosFidelizado.getLastName():"");
			lbLoyalCustomer.setText(lyCustomerText);
			lbLoyalCard.setText(datosFidelizado.getCardNumber());
			lbLoyalBalance.setText(datosFidelizado.getCardBalance()!=null?datosFidelizado.getCardBalance()+"":"");
		}
		else {
			hbLoyalCustomer.setVisible(false);
			hbLoyalCustomer.setManaged(false);
		}

		BasketCustomer datosCliente = basketManager.getBasketTransaction().getHeader().getCustomer();
		lbCustomer.setText(datosCliente.getCustomerDes());
		lbCustomerCode.setText(datosCliente.getCustomerCode());
		lbCustomerDocType.setText(datosCliente.getIdentificationTypeCode());
		lbCustomerDocN.setText(datosCliente.getVatNumber());
		
		
		doneChanges = false;           
		
		refreshScreenData();       
    }
    
    @Override
    public void initializeFocus() {
    }
    
	@FXML
	public void actionCancel() {
		if (!doneChanges || DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Se perderán los cambios realizados, ¿está seguro?"))) {
			closeCancel();
		}
	}
    
    @FXML
    public void actionAccept(){
        //Accion ir a la pantalla de pagos
        log.trace("actionAccept()");
        
        if(doneChanges){
        	boolean confirmation = DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Se procederá a aplicar los cambios en el ticket de devolución, ¿está seguro?"));
            if(confirmation) {
            	invoiceLinesToRefund();
                closeSuccess();
            }
        }
        else{
            closeCancel();
        }
    }
    
    /**
     * Acción seleccionar todo el ticket para devolver
     */
    public void actionTableSelectAll(){
        
		log.debug("accionTablaSeleccionarTodo() - Acción ejecutada");
		for (ProvisionalRefundItem line : provisionalLines) {
			if (BigDecimalUtil.isGreaterThanZero(line.getAvailableQuantity())) {
				BigDecimal cantASumar = line.getAvailableQuantity();
				updateProvisionalLines(cantASumar, line.getLineId());
				doneChanges = true;
			}
		}
		refreshScreenData();
    }

    protected void refreshScreenData(){
        loadWebViewMain();
    }
    
    protected void updateProvisionalLines(BigDecimal amountToAdd, Integer lineId){
        ProvisionalRefundItem provisionalLine = getLineaProvisional(lineId);
        provisionalLine.setRefundingQuantity(provisionalLine.getRefundingQuantity().add(amountToAdd));
    }
    
    protected ProvisionalRefundItem getLineaProvisional(int id){
        ProvisionalRefundItem line = null;
        
        for(ProvisionalRefundItem provLine: provisionalLines){
            if(provLine.getLineId() == id){
                line = provLine;
                break;
            }
        }
        
        return line;
    }
    
    /**
     * Añade las líneas seleccionadas al ticket de devolución.
     */
	protected void invoiceLinesToRefund() {

		List<NewBasketItemRequest> itemRequests = new ArrayList<NewBasketItemRequest>();
		Map<CatalogItemSearch, CatalogItemDetail> cachedCatalog = new HashMap<>();
		for (ProvisionalRefundItem provisionaLine : provisionalLines) {
			SaleDocLine line = provisionaLine.getOriginalLine();
			RefundedItemLine refundedLine = provisionaLine.getRefundedItemLine();
			//Volvemos a poner el devolver original a 0 porque hemos ido actualizando para actualizar la interfaz

			
			try {
				if(BigDecimalUtil.isGreater(provisionaLine.getRefundingQuantity().add(refundedLine.getRefundedQuantity()), refundedLine.getOriginalQuantity())) {
					provisionaLine.setRefundingQuantity(refundedLine.getAvailableRefundQuantity());
				}
				
				if(BigDecimalUtil.isGreaterThanZero(provisionaLine.getRefundingQuantity())) {
                    NewBasketItemRequest itemRequest = new NewBasketItemRequest();
                    itemRequest.setItemCode(line.getItemCode());
                    itemRequest.setCombination1Code(line.getCombination1Code());
                    itemRequest.setCombination2Code(line.getCombination2Code());
                    itemRequest.setQuantity(provisionaLine.getRefundingQuantity().negate());
                    itemRequest.setWeight(itemRequest.getQuantity());
					itemRequest.setOriginDocumentLine(new BasketItemOriginDocumentLine(basketManager.getRefundData().getOriginalSaleDoc().getSalesDocUid(), line.getLineId()));
					itemRequest.setDirectPrice(refundedLine.getRefundSalePrice());
                    itemRequest.setSupportsPromotions(false);
                    if (itemRequest.getItemData() == null || itemRequest.getRateItem() == null) {
        				CatalogItemSearch catalogItemSearch = modelMapper.map(itemRequest, CatalogItemSearch.class);
        				
        				if(!cachedCatalog.containsKey(catalogItemSearch)) {
        					CatalogItemDetail cachedCatalogItem = catalog.get().getCatalogItemService().findByFilter(catalogItemSearch);
        					cachedCatalog.put(catalogItemSearch, cachedCatalogItem);
        				}
        				CatalogItemDetail catalogItem = cachedCatalog.get(catalogItemSearch);
        				
        				if (catalogItem == null) {
        					DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El artículo con código {0} no está disponible en el catálogo de la tienda.", catalogItemSearch.getItemCode()));
        					continue;
        				}

						setRateItemFromCatalogItem(itemRequest, catalogItem);
        				itemRequest.setItemData(modelMapper.map(catalogItem, BasketItemItemData.class));								
        			}
                    
                    itemRequests.add(itemRequest);                    
                    //TODO: MSB: Reimplementar devolucion giftcard
				}
				
			} catch (Exception e) {
				log.error("invoiceLinesToRefund() - Ha habido un error al procesar la línea: " + e.getMessage(), e);
				DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e.getMessage(), e);
			}
		}
		basketManager.deleteBasketItems();
		InvalidBasketItemsRequest invalidLines = basketManager.createAndInsertBasketItems(itemRequests);
		if(!invalidLines.isEmpty()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Ha ocurrido algún error añadiendo las lineas. Por favor, revise las lineas añadidas."));
		}
	}

	protected void setRateItemFromCatalogItem(NewBasketItemRequest basketItemRequest, CatalogItemDetail catalogItemDetail) {
		if (catalogItemDetail.getRateItem() != null) {
			basketItemRequest.setRateItem(modelMapper.map(catalogItemDetail.getRateItem(), BasketRateItem.class));
		}
		else {
			BasketRateItem rateItem = new BasketRateItem();
			rateItem.setProfitFactor(BigDecimal.ZERO);
			rateItem.setUnitCostPrice(BigDecimal.ZERO);
			rateItem.setSalesPrice(BigDecimal.ZERO);
			rateItem.setSalesPriceRef(BigDecimal.ZERO);
			rateItem.setSalesPriceWithTaxes(BigDecimal.ZERO);
			rateItem.setSalesPriceRefWithTaxes(BigDecimal.ZERO);
			basketItemRequest.setRateItem(rateItem);
		}
	}

	protected void assignSerieNumber(BasketItem linea){
//		if (linea.getItemData().getSerialNumbersActive()) { //TODO: MSB reimplementar
//			sceneData.put(NumerosSerieController.PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN, (ticketManager.getTicketOrigen().getBasketLine(linea.getOriginDocumentLine().getLineId())).getSerialNumbers());
//			sceneData.put(NumerosSerieController.PARAMETRO_LINEA_DOCUMENTO_ACTIVO, linea);
//			openScene(NumerosSerieController.class, new SceneCallback<List<String>>() {
//				@Override
//				public void onSuccess(List<String> numerosSerie) {
////					linea.setSerialNumbers(numerosSerie);
//				}
//			});
            
//		}
	}

	protected void loadWebViewMain() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("refunding_lines", provisionalLines);
		params.put("combination1", combination1);
		params.put("combination2", combination2);

		loadWebView("sales/refund/item_refund/item_refund", params, wvMain);
	}
	
	@Override
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if("addOne".equals(method)) {
			String paramLineId = params.get("line_id");
			Integer lineId = Integer.valueOf(paramLineId);

			addOne(lineId);
		}else if("addLine".equals(method)) {
			String paramLineId = params.get("line_id");
			Integer lineId = Integer.valueOf(paramLineId);

			addLine(lineId);
		}
	}

	protected void addOne(Integer lineId) {
		log.debug("addOne() - Acción ejecutada");

		updateProvisionalLines(BigDecimal.ONE, lineId);
		
		doneChanges = true;
		refreshScreenData();
	}

	protected void addLine(Integer lineId) {

		log.debug("addLine() - Acción ejecutada");

		RefundedItemLine selectTicketLine = basketManager.getRefundData().getRefundedItemLine(lineId);
		BigDecimal refundingAmount = getLineaProvisional(lineId).getRefundingQuantity();
		BigDecimal amountToAdd = selectTicketLine.getAvailableRefundQuantity().subtract(refundingAmount);
		updateProvisionalLines(amountToAdd, selectTicketLine.getLineId());

		doneChanges = true;
		refreshScreenData();
	}
	
}