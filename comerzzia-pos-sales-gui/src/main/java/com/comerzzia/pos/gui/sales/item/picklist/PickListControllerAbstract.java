package com.comerzzia.pos.gui.sales.item.picklist;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.services.session.SesionInitException;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class PickListControllerAbstract <T extends BasketManager<?, ?>> extends BasketItemizationControllerAbstract<T> {
	public static final String PARAM_BASKET_MANAGER = "BASKET_MANAGER";
	public static final String PARAM_ACTION = "PARENT_ACTION";
	public static final String PARAM_CATALOG = "CATALOG";
	
	public static final String PARAM_LINES = "LINES";
	
	public static final String PARAM_BEHAVIOUR = "BEHAVIOUR";
	
	public static final String PARAM_ITEM_DES = "ITEM_DES";
	
	public static final String PARAM_ITEM_PRICE = "ITEM_PRICE";
	
	@FXML
	protected Button btHdrBack, btAccept;
	
	@Autowired
	protected ItemPickListService itemPickListService;
	
	@FXML
	protected WebView wvMain; 
	
	protected List<BasketItem> selectedItems;
	
	protected List<ItemPickListDto> currentItems;
	protected List<ItemPickListDto> currentBreadcrumbs;
	protected List<ItemPickListDto> mainPanel;
	protected String lastBehaviour;
	

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		displayDevice = Devices.getInstance().getLineDisplay();
		zeroPriceAllowed = variableService.getVariableAsBoolean(VariableServiceFacade.TPV_PERMITIR_VENTA_PRECIO_CERO, true);
		
	}

	@Override
	public void initializeFocus() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		currentBreadcrumbs = new ArrayList<>();
		selectedItems = new ArrayList<>();
		
		basketManager = (T) sceneData.get(PARAM_BASKET_MANAGER);
		action = (ActionDetail) sceneData.get(PARAM_ACTION);
		catalog = ((WeakReference<Catalog>) sceneData.get(PARAM_CATALOG)).get();
	}
	
	@Override
	public void refreshScreenData() {
		if (!selectedItems.isEmpty()) {
			btAccept.setDisable(false);
			btAccept.setVisible(true);
			btAccept.setManaged(true);
		} else {
			btAccept.setDisable(true);
			btAccept.setVisible(false);
			btAccept.setManaged(false);
		}
		
		if (basketManager.isBasketEmpty()) {
			displayDevice.writeLineUp(I18N.getText("---NUEVO CLIENTE---"));
			displayDevice.standbyMode();				
		}
		else {
			BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
			if (!basketTransaction.getLines().isEmpty()) {
				writeDisplayDeviceLine(basketTransaction.getLines().get(basketTransaction.getLines().size() - 1));
			}
			
			displayDevice.saleMode(basketTransaction);
		}
		
		loadMainWebView();
	}
	
	@Override
	protected void executeLongOperations() throws SesionInitException, InitializeGuiException {
		if (currentItems == null) {
			mainPanel = itemPickListService.getMainPanel();
			currentItems = itemPickListService.getItems();
			
		}
	}
	
	@Override
	protected void succededLongOperations() {
		onMainPanel();
	}
	
	protected void loadMainWebView() {
		Map<String, Object> params = new HashMap<>();
		
		params.put("pickListPage", getPickListPage());
		params.put("isVisorActive", itemPickListService.isVisorActive());
		params.put("selectedItems", selectedItems);
		loadWebView(getMainWebView(), params, wvMain);
	}
	
	protected String getMainWebView() {
		return "sales/picklist/picklist";
	}
	
	@Override
	public void onURLMethodCalled(String method, Map<String, String> params) {
		log.debug("onURLMethodCalled() - Method: " + method + ", Params: " + params);
		if("gotoBehaviour".equals(method)) {
			onBehaviour(params.get("type"), params.get("code"), params.get("behaviour"));
		}else if("gotoBreadcrumb".equals(method)) {
			onBreadcrumb(Integer.valueOf(params.get("index")));
		}else if("goBack".equals(method)) {
			onBreadcrumb(0);
		} else if ("removeSelected".equals(method)) {
			removeSelected(params.get("index"));
		}
	}
	
	protected void onBehaviour(String itemType, String itemCode, String behaviour) {
		if (itemType.equals(ItemPickListDto.ITEM_TYPE)) {
			onBehaviourItemType(itemCode, behaviour);
		} else if(itemType.equals(ItemPickListDto.CATEGORY_TYPE)) {
			onNavigation(itemCode);
        } else if(itemType.equals(ItemPickListDto.SECTION_TYPE)) {
        	onNavigation(itemCode);
        } else if(itemType.equals(ItemPickListDto.CATEGORIZATION_TYPE)) {
        	onNavigation(itemCode);
        }
	}
	
	protected void onBehaviourItemType(String itemCode, String behaviour) {
		lastBehaviour = behaviour;
		newBasketItemRequest(itemCode, BigDecimal.ONE, null);
	}
	
	@Override
	protected void newLineRefreshScreen(BasketItem value) {
		displayDevice.write(value.getItemData().getItemDes(),
				formatUtils.formatNumber(value.getQuantity()) + " X "
						+ formatUtils.formatAmount(value.getPriceWithTaxes()));
		selectedItems.add(value);
		
		if (lastBehaviour.equals(ItemPickListDto.CLOSE_BEHAVIOUR)) {
			closeSuccess(selectedItems);
			return;
		}
		
		if (lastBehaviour.equals(ItemPickListDto.MAIN_BEHAVIOUR)) {
			onMainPanel();
			refreshScreenData();
		} else if (lastBehaviour.equals(ItemPickListDto.CONTINUE_BEHAVIOUR)) {
			refreshScreenData();
		}
	}

	protected void onNavigation(String itemCode) {
		currentItems.stream().filter(item -> itemCode.equals(item.getCode())).findFirst().ifPresent(item -> {
			currentBreadcrumbs.add(item);
			currentItems = item.getSubItems();
			refreshScreenData();
		});
	}
	
	protected void onMainPanel() {
		if(mainPanel != null && !mainPanel.isEmpty()) {
			currentBreadcrumbs = mainPanel;
            currentItems = mainPanel.get(mainPanel.size() - 1).getSubItems();
            refreshScreenData();
		} else {
			onBreadcrumb(0);
		}
		
	}
	
	protected void onBreadcrumb(Integer index) {
		if (index == 0) {
			currentBreadcrumbs = new ArrayList<>();
			currentItems = itemPickListService.getItems();
		} else {
			currentBreadcrumbs = currentBreadcrumbs.subList(0, index);
			currentItems = currentBreadcrumbs.get(index-1).getSubItems();
		}
		refreshScreenData();
	}
	
	protected void removeSelected(String index) {
		if(!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Desea eliminar el artículo seleccionado?"))) return;
		
		BasketItem toRemove = selectedItems.get(Integer.valueOf(index));
		basketManager.deleteBasketItem(toRemove.getLineId());
		selectedItems.remove(toRemove);
		refreshScreenData();
	}
	
	public void comeBack() {
		if(!currentBreadcrumbs.isEmpty()) {
            onBreadcrumb(currentBreadcrumbs.size() - 1);
		}else {
			closeCancel();
		}
	}
	
	public void accept() {
		closeSuccess(selectedItems.stream().map(BasketItem::getItemCode).collect(Collectors.toList()));
	}

	public PickListPage getPickListPage() {
		PickListPage pickListPage = new PickListPage();
		pickListPage.setItems(new ArrayList<>());
		pickListPage.setBreadcrumbs(new ArrayList<>());
		pickListPage.getBreadcrumbs().add("Inicio");
		pickListPage.setMaxColumns(0);
		pickListPage.setMaxRows(0);
		
		currentBreadcrumbs.stream().forEach(breadcrumb -> pickListPage.getBreadcrumbs().add(breadcrumb.getDescription()));
		
		currentItems.stream().forEach(item -> {
			PickListPageItem pickListPageItem = new PickListPageItem(item.getCode(), item.getDescription(),
					item.getType(), item.getPhoto(), item.getBehaviour(), false, item.getRow(), item.getColumn(), null);
			pickListPage.getItems().add(pickListPageItem);
			if (pickListPage.getMaxColumns() == null || pickListPage.getMaxColumns() < pickListPageItem.getColumn()) {
				pickListPage.setMaxColumns(pickListPageItem.getColumn());
			}
			
			if (pickListPage.getMaxRows() == null || pickListPage.getMaxRows() < pickListPageItem.getRow()) {
				pickListPage.setMaxRows(pickListPageItem.getRow());
			}
		});
		
		pickListPage.setMaxColumns(pickListPage.getMaxColumns() + 1);
		pickListPage.setMaxRows(pickListPage.getMaxRows() + 1);
		
		//Seteamos matriz de items
		pickListPage.setItemsMatrix(new PickListPageItem[pickListPage.getMaxRows()][pickListPage.getMaxColumns()]);
		pickListPage.getItems().stream().forEach(item -> pickListPage.getItemsMatrix()[item.getRow()][item.getColumn()] = item);
		
		return pickListPage;
	}
	
	@Override
	public void close() {
		try {
			if (getWebView() != null) getWebView().getEngine().loadContent("");

			this.getStageController().closeCurrentScene();
		} catch (InitializeGuiException e) {
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e);
		} finally {
			sceneData.clear();
		}
	}
	
	@Override
	public boolean canCloseCancel() {
		if(DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Desea cancelar la selección y cerrar la ventana?"))) {
			selectedItems.forEach(
					item -> basketManager.deleteBasketItem(item.getLineId()
							));
			selectedItems.clear();
			
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canClose() {
		return true;
	}
	
	@Override
	public WebView getWebView() {
		return wvMain;
	}

}
