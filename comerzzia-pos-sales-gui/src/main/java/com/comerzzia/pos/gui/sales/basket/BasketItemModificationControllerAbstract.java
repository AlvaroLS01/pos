package com.comerzzia.pos.gui.sales.basket;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.comerzzia.catalog.facade.model.CatalogItemDetail;
import com.comerzzia.catalog.facade.model.CatalogItemUnitMeasure;
import com.comerzzia.catalog.facade.model.filter.CatalogItemSearch;
import com.comerzzia.catalog.facade.model.impl.CatalogItemUnitMeasureImpl;
import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.facade.model.User;
import com.comerzzia.core.facade.service.user.UserSearchParams;
import com.comerzzia.core.facade.service.user.UserServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.BasketCalculateRequest;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.NewBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCashier;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItemItemData;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.basket.BasketManagerBuilder;
import com.comerzzia.pos.core.gui.components.alphanumerickeyboard.AlphanumericKeyboard;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.retail.items.RetailBasketItemizationController;
import com.comerzzia.pos.gui.sales.retail.items.edit.RetailBasketItemModificationForm;
import com.comerzzia.pos.gui.sales.retail.items.serialnumbers.RetailSerialNumberController;
import com.comerzzia.pos.gui.sales.serialnumber.SerialNumberControllerAbstract;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class BasketItemModificationControllerAbstract<T extends BasketManager<?, ?>> extends SceneController implements Initializable {    
	//Clave del parametro que recibe la ventana
	public static final String PARAM_TITLE = "Title";
	public static final String PARAM_BASKET_ITEM = "Articulo";
	public static final String PARAM_ITEM_DETAIL = "ItemDetail";

    //Elementos de pantalla
    @FXML
    protected Label windowTitle, lbCombination1, lbCombination2, lbDiscount, lbTotalPrice, lbError, lbSerialNumbers;
    @FXML
    protected HBox hbCombination1, hbCombination2, hbSerialNumbers;
    @FXML
    protected TextField tfCombination1, tfCombination2, tfItem, tfDescription;
    @FXML
    protected NumericTextField tfMeasureQuantity, tfQuantity, tfTotalPrice, tfDiscount, tfAmount, tfPrice;
    @FXML
    protected Button btAccept;
    @FXML
    protected ComboBox<BasketCashier> cbCashier;

    @FXML
    protected ComboBox<CatalogItemUnitMeasure> cbUnitMeasureCodes;
    
    @FXML
    protected Button btSerialNumbers;
	@FXML
	protected HBox hbSerialBundles;
	@FXML
	protected NumericKeypad numpad;
	@FXML
	protected AlphanumericKeyboard keyboard;

	@Autowired
	private VariableServiceFacade variablesServices;
	
	@Autowired
	private UserServiceFacade userService;
	
	@Autowired
	protected ModelMapper modelMapper;
	
	@Autowired
	protected ComerzziaTenantResolver tenantResolver;
	
    @Autowired
    protected Session session;
    
    protected NewBasketItemRequest newItemRequest;    
    protected NewBasketItemRequest originalNewItemRequest;

    // Variables de configuración
    protected boolean combination1Active;
    protected boolean combination2Active;
    protected String combination1;
    protected String combination2;

    protected boolean availablePriceEdition;
    protected boolean availableDiscountEdition;
    protected boolean availablePriceZeroSale;
    
    protected BasketManager<?, ?> basketManager;
    
    protected BasketItemItemData itemData;
	
	protected RetailBasketItemModificationForm frBasketItemModification;

	protected WeakReference<Catalog> catalog;
	
	protected CatalogItemDetail catalogItemDetail;
	
	protected BasketCalculateRequest calculateRequest = new BasketCalculateRequest();
	
	protected FormatUtils formatUtils = FormatUtils.getInstance();

	@SuppressWarnings("unchecked")
	protected Class<T> getBasketClass() {
		return (Class<T>) getTypeArgumentFromSuperclass(getClass(), BasketManager.class, 0);
	}
	
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<User> users = userService.findStoreUsers(session.getApplicationSession().getCodAlmacen(), new UserSearchParams());
        
        List<BasketCashier> cashiers = modelMapper.map(users, new TypeToken<List<BasketCashier>>(){}.getType());        
        
        cbCashier.setItems(FXCollections.observableList(cashiers));
        cbUnitMeasureCodes.setConverter(new StringConverter<CatalogItemUnitMeasure>() {
			
			@Override
			public String toString(CatalogItemUnitMeasure arg0) {
				return arg0.getUnitMeasureDes();
			}
			
			@Override
			public CatalogItemUnitMeasure fromString(String arg0) {
				return null;
			}
		});
        cbUnitMeasureCodes.getSelectionModel().selectedItemProperty().addListener(ls -> {
        	if(cbUnitMeasureCodes.getSelectionModel().getSelectedItem()!=null && !tfMeasureQuantity.getText().isEmpty())
        		recalculate();
        });
    }
    
    @Override
    public void initializeComponents() {
        log.debug("initializeComponents()");
         
        // Inicializamos las variables
        combination1 = variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO);
        combination2 = variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO);
        availablePriceZeroSale = variablesServices.getVariableAsBoolean(VariableServiceFacade.TPV_PERMITIR_VENTA_PRECIO_CERO, true);
        availableDiscountEdition = variablesServices.getVariableAsBoolean(VariableServiceFacade.TICKETS_USA_DESCUENTO_EN_LINEA);
        combination1Active = session.getApplicationSession().isDesglose1Activo();
        combination2Active = session.getApplicationSession().isDesglose2Activo();
        availablePriceEdition = variablesServices.getVariableAsBoolean(VariableServiceFacade.TICKETS_PERMITE_CAMBIO_PRECIO);
        
        frBasketItemModification = SpringContext.getBean(RetailBasketItemModificationForm.class);
        
        frBasketItemModification.setFormField("quantity", tfMeasureQuantity);
        frBasketItemModification.setFormField("finalPrice", tfPrice);
        
        tfMeasureQuantity.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if(oldValue){
                   onQuantityFocusChange(tfMeasureQuantity);
                }
            }
        });

        tfQuantity.focusedProperty().addListener(new ChangeListener<Boolean>() {
        	@Override
        	public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
        		if(oldValue){
        			onQuantityFocusChange(tfQuantity);
        		}
        	}
        });
        
        
        addTfPriceListener();
        
        tfDiscount.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if(oldValue){
                	BigDecimal cantidadNueva = formatUtils.parseBigDecimal(tfDiscount.getText(), 2);
                	tfDiscount.setText(formatUtils.formatNumber(cantidadNueva, 2));
                	recalculate();
                }
            }
        });
        
        tfAmount.setEditable(false);
        addTextLimiter(tfDescription, 60);
                
    }
    
    protected void addTfPriceListener() {
    	tfPrice.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if(oldValue){                  	
                	BigDecimal cantidadNueva = formatUtils.parseBigDecimal(tfPrice.getText(), 2);
					tfPrice.setText(formatUtils.formatAmount(cantidadNueva));

                    recalculate();
                }
            }
        });
	}

	public void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }

	@SuppressWarnings("unchecked")
	@Override
    public void onSceneOpen() {
		String newWindowTitle = (String)sceneData.get(PARAM_TITLE);
		
		if (StringUtils.isBlank(newWindowTitle)) {
			newWindowTitle = I18N.getText("Editar artículo");
		}
		
    	basketManager = (BasketManager<?, ?>) sceneData.get(BasketItemizationControllerAbstract.BASKET_KEY);
    	
    	catalog = (WeakReference<Catalog>)sceneData.get(BasketItemizationControllerAbstract.CATALOG_KEY);

    	// original item request
    	originalNewItemRequest = (NewBasketItemRequest) this.sceneData.get(PARAM_BASKET_ITEM);
    	// clone for edit
    	newItemRequest = modelMapper.map(originalNewItemRequest, NewBasketItemRequest.class);
    	
    	catalogItemDetail = (CatalogItemDetail) this.sceneData.get(PARAM_ITEM_DETAIL);
    	
		Assert.notNull(basketManager, "No BasketManager was found on the scene request.");
		Assert.notNull(catalog, "No Catalog was found on the scene request.");		
		Assert.notNull(originalNewItemRequest, "No basket line was found on the scene request.");

		// for old fxml compatibility
		if (windowTitle != null) {
			windowTitle.setText(newWindowTitle);
		}
		
		itemData = originalNewItemRequest.getItemData();
		
		searchCatalogItemDetail();
		
		updateCalculateRequest();    	    	
        
		// Mostramos u ocultamos el botón de números de serie
        hbSerialBundles.setVisible(originalNewItemRequest.getItemData().getSerialNumbersActive());
        
         // Seteamos los componentes        
        tfPrice.setEditable(availablePriceEdition || itemData.getGenericItem());
        		
        if (tfPrice.isEditable()) {
            frBasketItemModification.setFormField("finalPrice", tfPrice);
        }

        tfTotalPrice.setEditable(false);

        lbDiscount.setVisible(availableDiscountEdition);
        tfDiscount.setVisible(availableDiscountEdition);
        
        if (availableDiscountEdition) {
            frBasketItemModification.setFormField("discount", tfDiscount);
        }

        if (!combination1Active) {
            log.debug("onSceneOpen() - No hay desglose 1");
            hbCombination1.setManaged(false);
            hbCombination1.setVisible(false);
        }
        else {
            lbCombination1.setText(I18N.getText(combination1).toUpperCase());
        }
        
        if (!combination2Active) {
            log.debug("onSceneOpen() - No hay desglose 2");
            hbCombination2.setManaged(false);
            hbCombination2.setVisible(false);
        }
        else {
            lbCombination2.setText(I18N.getText(combination2).toUpperCase());
        }
        if (itemData.getGenericItem()) {
        	tfDescription.setEditable(true);
        	tfDescription.setFocusTraversable(true);
        } else {
        	tfDescription.setEditable(false);
        	tfDescription.setFocusTraversable(false);
        }
        
        cbCashier.getSelectionModel().select(originalNewItemRequest.getCashier());
        hbSerialNumbers.setManaged(originalNewItemRequest.getItemData().getSerialNumbersActive());
        hbSerialNumbers.setVisible(originalNewItemRequest.getItemData().getSerialNumbersActive());
        
        
        List<CatalogItemUnitMeasure> measureUnits = getUnitMeasures();
        cbUnitMeasureCodes.setItems(FXCollections.observableList(measureUnits));
        CatalogItemUnitMeasure selectedMeasureUnit = measureUnits.stream().filter(um -> um.getUnitMeasureCode().equals(originalNewItemRequest.getUnitMeasureCode())).findFirst().orElse(measureUnits.get(0));
        cbUnitMeasureCodes.getSelectionModel().select(selectedMeasureUnit);
        cbUnitMeasureCodes.setDisable(measureUnits.size() == 1);
        tfQuantity.setDisable(StringUtils.isBlank(itemData.getUnitMeasureCodeAlt()));
        
        frBasketItemModification.setFormField("cashier", cbCashier);
        frBasketItemModification.setFormField("itemDescription", tfDescription);
        
        //recalculate();
        
        btSerialNumbers.setDisable(false);
        // Mostramos u ocultamos el botón de números de serie
	    if(itemData.getSerialNumbersActive() && !hbSerialBundles.getChildren().contains(btSerialNumbers)) {
	    		hbSerialBundles.getChildren().add(btSerialNumbers);
	    }
	    else if(!itemData.getSerialNumbersActive() && hbSerialBundles.getChildren().contains(btSerialNumbers)) {
	    	hbSerialBundles.getChildren().remove(btSerialNumbers);
	    }
        refreshScreenData();
    }
	
	protected void updateCalculateRequest() {
		BasketPromotable<? extends BasketItem> basketTransaction = basketManager.getBasketTransaction();
		
		if (basketTransaction != null) {
			calculateRequest.setLoyaltySettings(basketTransaction.getHeader().getLoyalCustomer());
		}		
	}
	
	protected void searchCatalogItemDetail() {
		if (catalogItemDetail != null) return;
		
		CatalogItemSearch catalogItemSearch = modelMapper.map(newItemRequest, CatalogItemSearch.class);
		
		catalogItemDetail = catalog.get().getCatalogItemService().findByFilter(catalogItemSearch);
	}
	
	protected List<CatalogItemUnitMeasure> getUnitMeasures(){
		if(StringUtils.isNotBlank(itemData.getUnitMeasureCodeAlt())) {
			return Arrays.asList(createCatalogItemUnitMeasure(itemData.getUnitMeasureCodeAlt(), itemData.getUnitMeasureCodeAlt(), BigDecimal.ONE));
		}
						
		List<CatalogItemUnitMeasure> result = new ArrayList<>(catalogItemDetail.getUnitsMeasure());
		if(result.stream().noneMatch(mu -> mu.getUnitMeasureCode().equals("UN"))) {
			result.add(createCatalogItemUnitMeasure("UN", I18N.getText("Unidad"), BigDecimal.ONE));
		}
		return result;
	}

	protected CatalogItemUnitMeasure createCatalogItemUnitMeasure(String unitMeasureCode, String unitMeasureDes, BigDecimal conversionFactor) {
		CatalogItemUnitMeasureImpl added = new CatalogItemUnitMeasureImpl();
		added.setConversionFactor(conversionFactor);
		added.setUnitMeasureDes(unitMeasureDes);
		added.setUnitMeasureCode(unitMeasureCode);
		return added;
	}

    @Override
    public void initializeFocus() {
    	frBasketItemModification.clearErrorStyle();
    	lbError.setText("");
    	
    	tfMeasureQuantity.requestFocus();
    }

    @FXML
    public void actionEnterAccept(KeyEvent e){
        log.trace("actionEnterAccept()");

        if (e.getCode() == KeyCode.ENTER) {
        	if(tfMeasureQuantity.isFocused()){
        		if (availablePriceEdition || itemData.getGenericItem()){
        			tfPrice.requestFocus();
        		}
        		else if (availableDiscountEdition) {
                    tfDiscount.requestFocus();
                }
        		else{
        			btAccept.requestFocus();
        		}
        	}
        	else if(tfPrice.isFocused()){
        		if (availableDiscountEdition) {
                    tfDiscount.requestFocus();
                }
        		else{
        			btAccept.requestFocus();
        		}
        	}
        	else if(tfDiscount.isFocused()){
        		btAccept.requestFocus();
        	}
        	else if (tfDescription.isFocused()) {
        		tfMeasureQuantity.requestFocus();
        	}
        	else{
        		actionAccept();
        	}    
        }
    }
    
    protected BigDecimal getCurrentPrice() {
    	if (originalNewItemRequest.getDirectPrice() != null) return originalNewItemRequest.getDirectPrice();
    	return getOriginalPrice();
    }
    
	protected BigDecimal getOriginalPrice() {
		return isPriceWithTaxes() ? originalNewItemRequest.getRateItem().getSalesPriceWithTaxes() : originalNewItemRequest.getRateItem().getSalesPrice();
	}

	@FXML
	public void actionAccept() {
		log.debug("actionAccept()");
		BigDecimal measureQuantity;

		try {
			measureQuantity = formatUtils.parseBigDecimal(tfMeasureQuantity.getText(), 3);
			tfMeasureQuantity.setText(formatUtils.formatNumber(measureQuantity, 3));
		} catch (Exception e) {
			//
		}

		frBasketItemModification.setQuantity(tfMeasureQuantity.getText());

		
		try{
        	BigDecimal price = formatUtils.parseBigDecimal(tfPrice.getText(), 2);
        	tfPrice.setText(formatUtils.formatAmount(price));
    	}
    	catch(Exception e){
    		//
    	}
    	
    	frBasketItemModification.setFinalWholesalePrice("0");
    	frBasketItemModification.setFinalPrice(tfPrice.getText());

		frBasketItemModification.setDiscount(tfDiscount.getText());
		frBasketItemModification.setCashier(cbCashier.getSelectionModel().getSelectedItem());
		frBasketItemModification.setItemDescription(tfDescription.getText());

		if (validateItemModificationForm() && validateSerialNumber()) {
			BigDecimal newQuantity = frBasketItemModification.getQuantityAsBD();
			
			CatalogItemUnitMeasure selectedMeasureUnit = cbUnitMeasureCodes.getSelectionModel().getSelectedItem();
			
			newItemRequest.setQuantity(newQuantity);
			newItemRequest.setUnitMeasureCode(selectedMeasureUnit!=null?selectedMeasureUnit.getUnitMeasureCode():"UN");
			newItemRequest.setUnitMeasureQuantity(BigDecimalUtil.round(newQuantity, 0));
			if(StringUtils.isNotBlank(itemData.getUnitMeasureCodeAlt())) {
				newItemRequest.setQuantity(formatUtils.parseBigDecimal(tfQuantity.getText(),3));
			}else if(selectedMeasureUnit.getConversionFactor()!=null) {
				newItemRequest.setQuantity(BigDecimalUtil.round(newQuantity.multiply(selectedMeasureUnit.getConversionFactor()), 0));
			}
			newItemRequest.setManualDiscount(newItemRequest.getManualDiscount());
			newItemRequest.setCashier(frBasketItemModification.getCashier());
			newItemRequest.setCustomData(originalNewItemRequest.getCustomData());

			if (originalNewItemRequest.getItemData().getGenericItem() || getOriginalPrice().compareTo(frBasketItemModification.getFinalPriceAsBD()) != 0) {				
				newItemRequest.setManualPrice(true);
				newItemRequest.setDirectPrice(frBasketItemModification.getFinalPriceAsBD());
			} else {
				newItemRequest.setManualPrice(false);
				newItemRequest.setDirectPrice(null);
			}
			
			if(!tfDescription.isDisabled() && StringUtils.isNotBlank(tfDescription.getText())){
				newItemRequest.getItemData().setItemDes(tfDescription.getText());
			}

			closeSuccess(newItemRequest);
		}
	}

    
    protected boolean validateSerialNumber() {
    	int newQuantity = formatUtils.parseBigDecimal(tfMeasureQuantity.getText()).intValue();
    	int serialNumbers = newItemRequest.getSerialNumbers()!=null?newItemRequest.getSerialNumbers().size():0;
    			
		if (!newItemRequest.getItemData().getSerialNumbersActive() || (newQuantity == serialNumbers)) {
    		return true;
    	}
    	
		// Comprobamos si se ha modificado la línea una vez insertados números de serie y se ha bajado la cantidad. En ese caso, se borráran 
		// los números de serie registrados y se volverá a mostrar la pantalla de números de serie
		if (DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("La cantidad de la linea no coincide con la cantidad de números de series asignados. Deberá modificarlos."))) {
			assignSerialNumbers();
		}

    	return false;
    }

	/**
     * Valida los datos introducidos
     *
     * @return
     */
    protected boolean validateItemModificationForm() {   	
        // Limpiamos los errores que pudiese tener el formulario
        frBasketItemModification.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");
        
        if(StringUtils.isBlank(frBasketItemModification.getItemDescription())) {
        	 lbError.setText(I18N.getText("La descripción no debe estar vacía"));
             return false;
        }

        // Validamos el formulario de login
        Set<ConstraintViolation<RetailBasketItemModificationForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBasketItemModification);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<RetailBasketItemModificationForm> next = constraintViolations.iterator().next();
            frBasketItemModification.setErrorStyle(next.getPropertyPath(), true);
            frBasketItemModification.setFocus(next.getPropertyPath());
            lbError.setText(next.getMessage());
            return false;
        }

        if (BigDecimalUtil.isLessThanZero(frBasketItemModification.getQuantityAsBD())) {
            try {
                super.checkOperationPermissions(RetailBasketItemizationController.PERMISSION_NEGATE_LINE);
            } catch (PermissionDeniedException e) {
                lbError.setText(I18N.getText("No tiene permisos para realizar una devolución"));
                return false;
            }
        }
        
        BigDecimal max = new BigDecimal(10000000);
		if(BigDecimalUtil.isGreaterOrEquals(frBasketItemModification.getQuantityAsBD(), max)){
			lbError.setText(I18N.getText(I18N.getText("La cantidad debe ser menor que {0}", formatUtils.formatNumber(max))));
        	tfMeasureQuantity.requestFocus();
			return false;
		}
		if(BigDecimalUtil.isEqualsToZero(frBasketItemModification.getQuantityAsBD())){
			lbError.setText(I18N.getText(I18N.getText("La cantidad debe ser distinta de cero.")));
        	tfMeasureQuantity.requestFocus();
			return false;
		}
		max = new BigDecimal(10000000000L);
		if(BigDecimalUtil.isGreaterOrEquals(formatUtils.parseAmount(tfPrice.getText()), max)){
			lbError.setText(I18N.getText(I18N.getText("El campo debe ser menor que {0}", formatUtils.formatNumber(max))));
			tfPrice.requestFocus();
			return false;
		}
		if(BigDecimalUtil.isGreaterOrEquals(formatUtils.parseAmount(tfTotalPrice.getText()), max)){
			lbError.setText(I18N.getText(I18N.getText("El campo debe ser menor que {0}", formatUtils.formatNumber(max))));
        	tfTotalPrice.requestFocus();
			return false;
		}
        
		max = new BigDecimal(10000000000L);
		if(BigDecimalUtil.isGreaterOrEquals(formatUtils.parseAmount(tfPrice.getText()), max)){
			lbError.setText(I18N.getText(I18N.getText("El campo debe ser menor que {0}", formatUtils.formatNumber(max))));
			tfPrice.requestFocus();
			return false;
		}
		if(BigDecimalUtil.isGreaterOrEquals(formatUtils.parseAmount(tfTotalPrice.getText()), max)){
			lbError.setText(I18N.getText(I18N.getText("El campo debe ser menor que {0}", formatUtils.formatNumber(max))));
        	tfTotalPrice.requestFocus();
			return false;
		}
		
        if(BigDecimalUtil.isLessThanZero(frBasketItemModification.getDiscountAsBD()) || BigDecimalUtil.isGreater(frBasketItemModification.getDiscountAsBD(), new BigDecimal(100))){
        	lbError.setText(I18N.getText("El descuento debe ser un valor entre 0 y 100."));
        	tfDiscount.requestFocus();
        	return false;
        }
              
        //control de venta a precio cero
       	if(BigDecimalUtil.isEqualsToZero(formatUtils.parseBigDecimal(tfPrice.getText(),4))){

        	if(availablePriceZeroSale){
        		boolean vender = DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Desea vender el artículo a precio 0?"));
				if(!vender){
					tfPrice.requestFocus();
					tfPrice.selectAll();
					return false;
				}
        	}else{
        		lbError.setText(I18N.getText("No está permitida la venta a precio 0."));
        		if(BigDecimalUtil.isEquals(formatUtils.parseBigDecimal(tfDiscount.getText()), new BigDecimal(100d))){
        			Platform.runLater(new Runnable() {
        				@Override
        				public void run() {
        					tfDiscount.requestFocus();
        					tfDiscount.selectAll();
        				}
        			});
        		}else{
        			Platform.runLater(new Runnable() {
        				@Override
        				public void run() {
        					tfPrice.requestFocus();
        					tfPrice.selectAll();
        				}
        			});
        		}
        		return false;
        	}
        }
        
        return true;
    }


    protected void refreshScreenData() {
        log.debug("accionRefrescaVista()");
                
        tfMeasureQuantity.setText(formatUtils.formatNumber(originalNewItemRequest.getUnitMeasureQuantity(),3));
        tfQuantity.setText(formatUtils.formatNumber(originalNewItemRequest.getQuantity(),3));
        tfCombination1.setText(originalNewItemRequest.getCombination1Code());
        tfCombination2.setText(originalNewItemRequest.getCombination2Code());
        tfDescription.setText(originalNewItemRequest.getItemData().getItemDes());
        tfItem.setText(originalNewItemRequest.getItemCode());
        //tfPrecioTotal.setText(FormatUtil.getInstance().formateaImporte(lineaOriginal.getPrecioTotalTarifaOrigen()));
        if (originalNewItemRequest.getManualDiscount() != null) {
        	tfDiscount.setText(formatUtils.formatNumber(originalNewItemRequest.getManualDiscount(),2));
        } else {
        	tfDiscount.setText("0");
        }
		tfPrice.setText(formatUtils.formatAmount(getCurrentPrice()));
		refreshSerialNumbers();
		recalculate();
	}

	protected void refreshSerialNumbers() {
		if (CollectionUtils.isNotEmpty(newItemRequest.getSerialNumbers())) {
    		lbSerialNumbers.setText(String.join(", ", newItemRequest.getSerialNumbers()));
    	}
    }

	public void assignSerialNumbers(){
		if (itemData.getSerialNumbersActive()) {
			sceneData.put(SerialNumberControllerAbstract.PARAM_LINE_DESCRIPTION, newItemRequest.getItemDes());
			sceneData.put(SerialNumberControllerAbstract.PARAM_LINE_CURRENT_SERIAL_CODES, newItemRequest.getSerialNumbers());
			sceneData.put(SerialNumberControllerAbstract.PARAM_REQUIRED_QUANTITY, newItemRequest.getQuantity().longValue());
			openScene(RetailSerialNumberController.class, new SceneCallback<Set<String>>() {
				@Override
				public void onSuccess(Set<String> callbackData) {
					newItemRequest.setSerialNumbers(new HashSet<>(callbackData));
					refreshSerialNumbers();
					recalculate();
				}
			});
		}
	}
	

	/**
	 * 
	 */
	protected void onQuantityFocusChange(NumericTextField quantity) {
		BigDecimal newQuantity = formatUtils.parseBigDecimal(quantity.getText(),3);
		if(newQuantity != null){
		    if(basketManager.getDocumentType().isNegativeSign()){
		        newQuantity = newQuantity.abs().negate();
		    }
		    else if(basketManager.getDocumentType().isPossitiveSign()){
		        newQuantity = newQuantity.abs();
		    }
		    
		    if(!itemData.getWeightRequired()) {
		    	newQuantity = BigDecimalUtil.round(newQuantity, 0);
		    }
		    
		    quantity.setText(formatUtils.formatNumber(newQuantity, 3));
		    recalculate();

		}
	}
		
    protected void recalculate(){    	
    	CatalogItemUnitMeasure selectedMeasureUnit = cbUnitMeasureCodes.getSelectionModel().getSelectedItem();
    	BigDecimal measureQuantity  = formatUtils.parseBigDecimal(tfMeasureQuantity.getText(),3);
    	BigDecimal totalPrice = formatUtils.parseBigDecimal(tfPrice.getText(),2);
    	BigDecimal discount = BigDecimal.ZERO;
    	BigDecimal quantity = formatUtils.parseBigDecimal(tfQuantity.getText(),3);
    	if(StringUtils.isNotBlank(tfDiscount.getText())){
    		discount = formatUtils.parseBigDecimal(tfDiscount.getText(),2);
    	}
    	
    	
    	newItemRequest.setUnitMeasureCode(selectedMeasureUnit!=null?selectedMeasureUnit.getUnitMeasureCode():"UN");
		newItemRequest.setQuantity(measureQuantity);
		
		if (originalNewItemRequest.getItemData().getGenericItem() || getOriginalPrice().compareTo(totalPrice) != 0) {
			newItemRequest.setManualPrice(true);
			newItemRequest.setDirectPrice(totalPrice);
		} else if(!originalNewItemRequest.getItemData().getGenericItem()) {
			newItemRequest.setManualPrice(false);
			newItemRequest.setDirectPrice(null);
		}
		
		if(discount.compareTo(BigDecimal.ZERO) != 0) {
			newItemRequest.setManualDiscount(discount);
		} else {
			newItemRequest.setManualDiscount(null);
		}
		
		if(itemData.getWeightRequired()) {
			newItemRequest.setWeight(measureQuantity);
		}else {
			newItemRequest.setUnitMeasureQuantity(BigDecimalUtil.round(measureQuantity, 0));
			if(StringUtils.isNotBlank(itemData.getUnitMeasureCodeAlt())) {
				newItemRequest.setQuantity(quantity);
			}else if(measureQuantity != null && selectedMeasureUnit != null && selectedMeasureUnit.getConversionFactor()!=null) {
				newItemRequest.setQuantity(BigDecimalUtil.round(measureQuantity.multiply(selectedMeasureUnit.getConversionFactor()), 0));
			}
		}

		newItemRequest.setPricesWithTaxes(isPriceWithTaxes());

		// calculate new values
		calculateRequest.setItems(Arrays.asList(newItemRequest));
		
    	BasketPromotable<? extends BasketItem> calculateResult = BasketManagerBuilder.basketCalculate(getBasketClass(), 
    			session.getApplicationSession().getStorePosBusinessData(), 
    			calculateRequest, 
    			catalog.get());
    	
		if (CollectionUtils.isEmpty(calculateResult.getLines())) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Se ha producido un error al realizar los calculos. Revise los valores."));
			return;
		}
		
    	BasketItem calculatedItem = calculateResult.getLines().get(0);
    	
    	tfAmount.setText(formatUtils.formatAmount(calculatedItem.getExtendedPriceWithTaxes()));
    	tfTotalPrice.setText(formatUtils.formatAmount(calculatedItem.getPriceWithTaxes()));
    	if(tfQuantity.isDisabled()) {
    		tfQuantity.setText(formatUtils.formatNumber(calculatedItem.getQuantity(), -1));
    	}
    }

	protected abstract boolean isPriceWithTaxes();
}
