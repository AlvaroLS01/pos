package com.comerzzia.pos.gui.sales.basket;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.stream.Collectors;

import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCustomer;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import org.apache.commons.lang.StringUtils;

import com.comerzzia.catalog.facade.model.CatalogItemDetail;
import com.comerzzia.catalog.facade.model.CatalogItemUnitMeasure;
import com.comerzzia.catalog.facade.model.filter.CatalogItemSearch;
import com.comerzzia.core.commons.exception.ApiException;
import com.comerzzia.core.facade.model.DocTypeDetail;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.NewBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketBalanceCard;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItemItemData;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItemOriginDocumentLine;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketRateItem;
import com.comerzzia.omnichannel.facade.model.documents.RefundedItemLine;
import com.comerzzia.omnichannel.facade.model.documents.SaleDocLine;
import com.comerzzia.omnichannel.facade.service.basket.RefundBasketManager;
import com.comerzzia.omnichannel.facade.service.basket.exception.BasketException;
import com.comerzzia.omnichannel.facade.service.basket.exception.BasketLineException;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.DeviceCallback;
import com.comerzzia.pos.core.devices.device.loyaltycard.DeviceLoyaltyCardException;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScannerDataReaded;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.gui.util.modalweb.ModalWebController;
import com.comerzzia.pos.core.services.session.SesionInitException;
import com.comerzzia.pos.gui.sales.balancecard.search.BalanceCardSearchController;
import com.comerzzia.pos.gui.sales.basket.RefundControllerAbstract.RefundSearchData;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class RefundBasketItemizationControllerAbstract<M extends RefundBasketManager<?, ?>> extends BasketItemizationControllerAbstract<M> {
	public static final String PERMISSION_ITEM_CHANGE = "ITEM CHANGE";
	public static final String PERMISSION_NEW_ITEM = "NEW ITEM";
	
	protected DocTypeDetail initialDocumentType;
	protected RefundControllerAbstract.RefundSearchData searchData;
	
	@Override
	protected String getWebViewPath() {
		return "sales/refund/refund";
	}
	
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		log.debug("onSceneOpen() - Inicializando formulario...");
		try {
			tfItemCode.setText("");

			
			if (sceneData.containsKey(RefundControllerAbstract.PARAM_REFUND_SEARCH_DATA)) {
				searchData = (RefundSearchData) sceneData.get(RefundControllerAbstract.PARAM_REFUND_SEARCH_DATA);
				
			}else {
				log.error("onSceneOpen() ----- No refund search data found -----");
				throw new InitializeGuiException();
			}
			
			if (scannerObserver == null) {
				scannerObserver = new ScannerObserver();
			}
			
			Devices.getInstance().getScanner().addObserver(scannerObserver);
			Devices.getInstance().getScanner().enableReader();

		}
		catch (ApiException e) {
			log.error("onSceneOpen() - Error inesperado inicializando formulario. " + e.getMessage(), e);
			throw new InitializeGuiException(I18N.getText("Ha ocurrido un error al conectar con el servidor para consultar la tarjeta regalo"), e);
		}
		catch (InitializeGuiException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("onSceneOpen() - Error inesperado inicializando formulario. " + e.getMessage(), e);
			throw new InitializeGuiException(e);
		}
	}
	
	public class ScannerObserver extends com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract<M>.ScannerObserver{

		@Override
		public void update(Observable o, Object arg) {
			if (!(arg instanceof DeviceScannerDataReaded) || getStage().getScene() != getScene()) return;

			if (!tfItemCode.isFocused() || tfItemCode.isDisabled()) {
				Devices.getInstance().getScanner().beepError();
				return;
			}
			DeviceScannerDataReaded scanData = (DeviceScannerDataReaded)arg;
			
			Platform.runLater(()->{
				frValidation.setCantidad(tfItemQuantity.getText().trim());
				if (validateForm())  {
					BigDecimal quantity = frValidation.getCantidadAsBigDecimal();
					newBasketItemRequest(scanData.getScanData(), quantity, scanData.getScanDataType());
				}	
			});					
		}
		
	}
	
	@Override
    protected void executeLongOperations() throws SesionInitException, InitializeGuiException {
        super.executeLongOperations();
        try {
            boolean found = basketManager.loadRefundOriginalSaleDoc(searchData.getCode(), searchData.getDocType().getDocTypeId());
            if(!found) {
                throw new InitializeGuiException(I18N.getText("Documento ({0}) con código {1} no encontrado.", searchData.getDocType().getDocTypeDes(), searchData.getCode()));
            }
			this.catalog = catalogManager.getCatalogFromKey(basketManager.getBasketTransaction().getCatalogKey());
            
            if(!basketManager.validateFiscalData()) {
                //TODO: Cancel basket?
                throw new InitializeGuiException(I18N.getText("El ticket fue realizando en una tienda con un tratamiento fiscal diferente al de esta tienda. No se puede realizar esta devolución."));
            }
        
            String cardCode = basketManager.getRefundData().getOriginalSaleDoc().getLyCustomerCard();
            if (StringUtils.isNotBlank(cardCode)) {
                findLoyalCustomer(cardCode);
            }
        }
        catch (BasketException e) {
            throw new InitializeGuiException(e);
        }
        catch (DeviceLoyaltyCardException e) {
            log.debug("executeLongOperations() - An exception was thrown ", e);
            DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(e.getMessage());
        }
    }
	
	protected void findLoyalCustomer(String cardCode) throws DeviceLoyaltyCardException {
        Devices.getInstance().getLoyaltyCard().findLoyalCustomerAndCouponsInBackground(cardCode, new DeviceCallback<BasketLoyalCustomer>(){

            @Override
            public void onSuccess(BasketLoyalCustomer tarjeta) {
                if (!tarjeta.getCardActive()) {
                    DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("La tarjeta de fidelización {0} no está activa", tarjeta.getCardNumber()));
                    tarjeta = null;
                }

                basketManager.updateLoyaltySettings(tarjeta);

                refreshScreenData();
            }

            @Override
            public void onFailure(Throwable e) {
                log.debug("findLoyalCustomer() - Could not find loyal customer data.", e);
                DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se pudo cargar la información de fidelizado."));
            }
        });
    }
	
	@Override
	protected void failedLongOperations(Throwable e) {
		if (basketManager != null) basketManager = null;
		super.failedLongOperations(e);
	}
	
	@Override
	protected void succededLongOperations() {		
		if(initialDocumentType == null) {
			initialDocumentType = session.getApplicationSession().getDocTypeByDocTypeId(basketManager.getBasketTransaction().getHeader().getDocTypeId());
			 
		}
		
		super.succededLongOperations();

		BasketCustomer customer = basketManager.getBasketTransaction().getHeader().getCustomer();
		if (customer != null) {
			checkCustomerRate(customer.getRateCode());
		}
	}
	
	@Override
	public void initializeManager() throws SesionInitException  {
		super.initializeManager();
		basketManager.setPersistable(false);
	}
	
	@Override
	public boolean checkWithdrawalWarning() {
		return false;
	}
	
	public void newBasketItemRequest(String code, BigDecimal quantity) {
		newBasketItemRequest(code, quantity, null);
	
	}
	
	@Override
	public void newBasketItemRequest(String code, BigDecimal quantity, String scanCodeType) {
		try {
			if (code.isEmpty()) return;
			
			NewBasketItemRequest itemRequest = new NewBasketItemRequest();
			quantity = quantity.negate();
			itemRequest.setBarcode(code);
			itemRequest.setQuantity(quantity);
			if(StringUtils.isNotBlank(scanCodeType)) {
				itemRequest.setScanned(true);
				itemRequest.setScanCodeType(scanCodeType);
			}
			
			if (itemRequest.getItemData() == null || itemRequest.getRateItem() == null) {
				CatalogItemSearch catalogItemSearch = modelMapper.map(itemRequest, CatalogItemSearch.class);
				
				CatalogItemDetail catalogItem = catalog.getCatalogItemService().findByFilter(catalogItemSearch);
				
				if (catalogItem == null) throw new NoSuchElementException();
				
				itemRequest.setItemCode(catalogItem.getItemCode());
				setRateItemFromCatalogItem(itemRequest, catalogItem);
				itemRequest.setItemData(modelMapper.map(catalogItem, BasketItemItemData.class));
				
				// DUN14 conversion factor
				BigDecimal conversionFactor = itemRequest.getItemData().getBarcodeConversionFactor();
				if (Boolean.TRUE.equals(itemRequest.getItemData().getDun14()) && 
					conversionFactor != null && 
					BigDecimalUtil.isGreaterThanZero(conversionFactor)) {
					
					BigDecimal measureQuantity= itemRequest.getUnitMeasureQuantity();
					
					if(measureQuantity == null) {
						measureQuantity = itemRequest.getQuantity();
					}
					
					// find unit measure
					for (CatalogItemUnitMeasure unitMeasure : catalogItem.getUnitsMeasure()) {
						if (BigDecimalUtil.isEquals(conversionFactor, unitMeasure.getConversionFactor())) {
							itemRequest.setUnitMeasureCode(unitMeasure.getUnitMeasureCode());
							break;
						}						
					}
					
					itemRequest.setUnitMeasureConversionFactor(conversionFactor);
					itemRequest.setQuantity(BigDecimalUtil.round(measureQuantity.multiply(conversionFactor), 3));
					if (itemRequest.getUnitMeasureCode() == null) {
						itemRequest.setUnitMeasureCode("UN");
						itemRequest.setUnitMeasureQuantity(itemRequest.getQuantity());
					} else {						
						itemRequest.setUnitMeasureQuantity(measureQuantity);
					}
				}		
			}
			
			checkItemActive(itemRequest);
			
			setDefaultUnitMeasure(itemRequest);
			
			List<SaleDocLine> originallines = basketManager.getOriginalSaleDocLines(code, catalog);
			if(originallines.isEmpty() && checkNewItemPermissions()) {
				addNewItem(itemRequest);
				return;
			}
			
			List<SaleDocLine> refundingAvailableLines = originallines.stream().filter(ol -> {
				BigDecimal refundingQuantity = basketManager.getBasketRefundedQuantity(ol.getLineId());
				RefundedItemLine refundedItemLine = basketManager.getRefundData().getRefundedItemLine(ol.getLineId());
				return refundedItemLine != null && BigDecimalUtil.isGreaterThanZero(refundedItemLine.getAvailableRefundQuantity().subtract(refundingQuantity));
			}).collect(Collectors.toList());
			if(refundingAvailableLines.isEmpty() && checkItemChangePermissions()) {
				addItemChange(itemRequest);
				return;
			}else if(refundingAvailableLines.isEmpty()){
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El artículo {0} no se encuentra en las líneas del ticket original.", code));
				return;
			}
			
			if(refundingAvailableLines.size()>1) {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El artículo {0} existe en varias líneas del ticket original, deberá indicar la línea de devolución manualmente.", code));
				return;
			}
			
			itemRequest.setOriginDocumentLine(new BasketItemOriginDocumentLine(basketManager.getRefundData().getOriginalSaleDoc().getSalesDocUid(), refundingAvailableLines.get(0).getLineId()));
		
		
			if(itemRequest.getItemData().getWeightRequired()) {
				DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Para devolver un artículo de peso debe devolver la linea completa manualmente."));
				return;
			}
			
			newItemrequest(itemRequest);
		} catch (NoSuchElementException e) {
			log.debug(e.getMessage(), e);
			Devices.getInstance().getScanner().beepError();
			Devices.getInstance().getScanner().disableReader();
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El artículo consultado no se encuentra en el sistema"));
			Devices.getInstance().getScanner().enableReader();
		} catch (BusinessException e) {
			log.error(e.getMessage(), e);
			basketItemRequestFailed(e);
		}
	}
	
	protected BasketItem newItemrequest(NewBasketItemRequest itemRequest) throws BasketLineException {
		checkItemRequiresCombinations(itemRequest);

		BasketItem line = basketManager.createAndInsertBasketItem(itemRequest);

		newItemSuccess(line);
		
		return line;
	}
	
	protected void newItemSuccess(BasketItem line) {
		refreshScreenData();
	}

	protected void addNewItem(NewBasketItemRequest itemRequest) throws BasketLineException {
		if (DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Se va a introducir un artículo que no estaba en la venta original. ¿Está seguro?"))) {
			addPositiveLine(itemRequest);
		}
	}

	protected void addItemChange(NewBasketItemRequest itemRequest) throws BasketLineException {
		if (DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Este artículo ya está completamente devuelto en la venta original. Se introducirá en positivo. ¿Está seguro?"))) {
			addPositiveLine(itemRequest);
		}
	}

	protected void addPositiveLine(NewBasketItemRequest itemRequest) throws BasketLineException {
		if(itemRequest.getItemData().getWeightRequired()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se pueden añadir artículos de peso durante la devolución."));
			return;
		}
		itemRequest.setQuantity(itemRequest.getQuantity().abs());
		itemRequest.setUnitMeasureQuantity(itemRequest.getUnitMeasureQuantity().abs());

		newItemrequest(itemRequest);
	}
	
	@Override
	protected void editItem(Integer lineId) {
		BasketItem basketLine = basketManager.getBasketTransaction().getLine(lineId);
		
		if (basketLine == null) return;
		
		if (basketLine.getQuantity().compareTo(BigDecimal.ZERO)<0) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se puede editar una línea con cantidad negativa."));
			
			return;
		}
		
		super.editItem(lineId);
	}

	protected boolean checkItemChangePermissions() {
		boolean changePermission = false;
		try {
			if(!initialDocumentType.isPossitiveSign()){
				checkOperationPermissions(PERMISSION_ITEM_CHANGE);
				changePermission = true;
			}else {
				log.debug("No es posible añadir una linea de venta a una devolución con el signo forzado a positivo.");
			}
		}
		catch (PermissionDeniedException e) {
		}
		return changePermission;
	}

	protected boolean checkNewItemPermissions() {
		boolean changePermission = false;
		try {
			if(!initialDocumentType.isPossitiveSign()){	
				checkOperationPermissions(PERMISSION_NEW_ITEM);
				changePermission = true;
			}else {
				log.debug("No es posible añadir una linea de venta a una devolución con el signo forzado a positivo.");
			}
		}
		catch (PermissionDeniedException e) {
		}
		return changePermission;
	}

	public void cancelRefund() {

		try {
			super.checkOperationPermissions(PERMISSION_CANCEL_SALE);
			if (confirmCancelRefund()) {
				basketManager.cancelBasket();

				closeCancel();
			}
		}
		catch (PermissionDeniedException e) {
			log.debug("cancelRefund() - El usuario no tiene permisos para cancelar la devolución.");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No tiene permisos para cancelar la devolución."));
		}
	}

	protected boolean confirmCancelRefund() {
		return DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Se anulará la devolución, ¿seguro que desea continuar?"));

	}

    public void refreshScreenData() {
		log.debug("refreshScreenData() - Refrescando datos de pantalla...");
		
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		if (basketManager.isBasketEmpty()) {
			displayDevice.writeLineUp(I18N.getText("---NUEVO CLIENTE---"));
			displayDevice.standbyMode();				
		}
		else {
			if (basketTransaction.getLines().size() > 0) {
				writeDisplayDeviceLine(basketTransaction.getLines().get(basketTransaction.getLines().size() - 1));
			}
			displayDevice.saleMode(basketTransaction);
		}
		
		loadBasketWebView(basketTransaction);
		
		tfItemQuantity.setText(FormatUtils.getInstance().formatNumber(BigDecimal.ONE, 3));
		tfItemCode.setText("");
	}
    
    protected void loadBasketWebView(BasketPromotable<?> basketTransaction) {

		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("cashLimitWarning", cashLimitWarning);
		params.put("cashLimitLock", cashLimitLock);
		params.put("basketsParked", parkedTickets);
		params.put("basket", basketTransaction);
		params.put("combination1Code", combination1Code);
		params.put("combination2Code", combination2Code);
		params.put("defaultServiceType", defaultServiceType);
		params.put("offlineRecovered", !basketManager.getRefundData().getOnlineRecovered());
		
		loadWebView(getWebViewPath(), params, wvPrincipal);
	}

	//Its not possible to use customer coupons on a refund
	protected void useCustomerCoupons() {}
	
	protected void onPaymentsSuccess() {
		closeSuccess();
	}
		
	public void openOriginalTicketDocument() {
		sceneData.put(BASKET_KEY, basketManager);
		sceneData.put(CATALOG_KEY, new WeakReference<>(catalog));
		openScene(getOriginRefundController(), new SceneCallback<Void>() {
			
			@Override
			public void onSuccess(Void callbackData) {
				refreshScreenData();
			}
		});
	}
	
	public abstract Class<? extends SceneController> getOriginRefundController();

	public void checkOriginalgiftcard() {
		log.info("checkOriginalgiftcard()");
		openScene(BalanceCardSearchController.class, new SceneCallback<BasketBalanceCard>() {
			
			@Override
			public void onSuccess(BasketBalanceCard giftcard) {
				Boolean correctOperation = validateGiftcard(giftcard);
				if(!correctOperation) {
					this.onCancel();
				}
				
			}
			
			@Override
			public void onCancel() {
				refreshScreenData();
			}
		});

	}

	protected boolean validateGiftcard(BasketBalanceCard tarjeta) {
		log.info("validateGiftcard()");

		boolean correctOperation = true;
		try {

			// Si encuentra la tarjeta.
			// TODO:AMA, reimplementar
		}
		catch (UnsupportedOperationException ex) {
			log.debug("validateGiftcard() - El dispositivo Giftcard no está configurado o no soporta la operación");
			correctOperation = false;
		}
		catch (Exception ex) {
			log.debug("validateGiftcard() - Error consultando saldo", ex);
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(ex.getMessage());
			correctOperation = false;
		}

		return correctOperation;
	}


	
	@Override
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if("DeleteItemRefund".equals(method)) {
			String paramLineId = params.get("line_id");
			Integer lineId = Integer.valueOf(paramLineId);

			deleteItem(lineId);
		}else if("EditItemRefund".equals(method)) {
			String paramLineId = params.get("line_id");
			Integer lineId = Integer.valueOf(paramLineId);
			
			editItem(lineId);
		}else if("ShowInfoRefund".equals(method)) {
			try {					
				sceneData.put(ModalWebController.PARAM_URL, params.get("url"));
				openScene(ModalWebController.class);
			}
			catch (Exception e) {
				log.error("addUrlHandlersItemsOperations() - Error:" + e.getMessage(), e);
				DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha habido un error al mostrar la página web"), e);
			}
		}
	}
	
	protected void openPaymentsScreen() {
		openScene(getPaymentsController(), new SceneCallback<Void>() {
			
			@Override
			public void onSuccess(Void callbackData) {
				onPaymentsSuccess();
			}
			
			@Override
			public void onCancel() {
				initializeFocus();
				refreshScreenData();
			}

		});
	}
	
	@Override
	protected void checkItemActive(NewBasketItemRequest itemRequest) {
		//On retail refund. The active field is not evaluated
	}

}
