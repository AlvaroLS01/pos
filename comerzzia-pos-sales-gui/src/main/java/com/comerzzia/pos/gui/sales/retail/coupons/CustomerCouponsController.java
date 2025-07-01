package com.comerzzia.pos.gui.sales.retail.coupons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomerCoupon;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.image.ActionButtonImageComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

@Component
@CzzScene
public class CustomerCouponsController extends SceneController implements ButtonsGroupController {

	private Logger log = Logger.getLogger(CustomerCouponsController.class);

	public static final String PARAM_CUSTOMER_COUPONS = "CustomerCouponsController.CustomerCoupons";
	public static final String PARAM_ACTIVE_COUPONS = "CustomerCouponsController.ActiveCoupons";

	public static final String CLASS_BUTTON_DISABLE = "composite-button-disable";

	@FXML
	protected ScrollPane scrollPane;

	@FXML
	protected AnchorPane buttonPane;

	@FXML
	protected Button btAccept;
	
	@FXML
	protected Button btSelectAll;

	protected boolean anySelected;

	protected double widthScrollPane;
	protected int numRows;
	protected int numColumns;

	protected Map<String, BasketLoyalCustomerCoupon> customerCoupons;
	protected Map<String, BasketLoyalCustomerCoupon> activeCoupons;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		numColumns = 3;
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				widthScrollPane = scrollPane.getWidth();
			}
		});
		
		buttonPane.getChildren().clear();
	}

	@Override
	public void initializeFocus() {
		btAccept.requestFocus();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		try {
			buttonPane.getChildren().clear();
			customerCoupons = new HashMap<>();
			this.activeCoupons = new HashMap<>();
			
			List<BasketLoyalCustomerCoupon> availableCoupons = (List<BasketLoyalCustomerCoupon>) sceneData.get(PARAM_CUSTOMER_COUPONS);
			List<BasketLoyalCustomerCoupon> activeCoupons = (List<BasketLoyalCustomerCoupon>) sceneData.get(PARAM_ACTIVE_COUPONS);

			if (availableCoupons != null) {
				for (BasketLoyalCustomerCoupon coupon : availableCoupons) {
					customerCoupons.put(coupon.getCouponCode(), coupon);
				}
			}

			if (activeCoupons != null) {
				for (BasketLoyalCustomerCoupon coupon : activeCoupons) {
					this.activeCoupons.put(coupon.getCouponCode(), coupon);
				}
			}

			loadButtons(availableCoupons);
			
			anySelected = this.activeCoupons.isEmpty();

			refreshToggleButtonText();
		}
		catch (Exception e) {
			log.error("onSceneOpen() - Error: " + e.getMessage(), e);
			
			throw new InitializeGuiException(I18N.getText("Ha habido un error al mostrar la pantalla de cupones del fidelizado. Por favor, contacte con el administrador."), e);
		}
	}

	private void loadButtons(List<BasketLoyalCustomerCoupon> availableCoupons) throws LoadWindowException {
		if (availableCoupons == null || availableCoupons.isEmpty()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No hay cupones disponibles."));
			return;
		}
		
		final List<ButtonConfigurationBean> buttons = new ArrayList<ButtonConfigurationBean>();

		numRows = new BigDecimal(availableCoupons.size()).divide(new BigDecimal(numColumns), 0, RoundingMode.UP).intValue();	
		
		for(BasketLoyalCustomerCoupon item : availableCoupons) {
			ButtonConfigurationBean configuracionBotonBean = new ButtonConfigurationBean(item.getImageUrl(), item.getCouponName().toUpperCase(), "", item.getCouponCode(), "SET_COUPON_STATE");
			buttons.add(configuracionBotonBean);
		}
		
		if(numRows == 1) {
			for(int i = 0 ; i < numColumns ; i++) {
				buttons.add(new ButtonConfigurationBean(null, null, null, null, "HUECO"));
			}
			numRows = 2;
		}

		final CustomerCouponsController controller = this;

		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				try {
					double heigth = numRows  * 118.0;
					double extraHeigth = 100;
					if(heigth < scrollPane.getHeight()) {
						heigth = scrollPane.getHeight() - 20;
						extraHeigth = 0;
					}
					double anchura = getStage().getWidth() - 80;
					
					log.trace("loadButtons() - Buttons pane. Height: " + heigth + ". Width: " + anchura);

					ButtonsGroupComponent botoneraAccionesTabla = new ButtonsGroupComponent(numRows, numColumns, controller, buttons, anchura, heigth, ActionButtonImageComponent.class.getName());
					
					for(Button button : (List<Button>) botoneraAccionesTabla.getButtonsList()) {
						ButtonConfigurationBean config = (ButtonConfigurationBean) button.getUserData();
						
						if(!activeCoupons.containsKey(config.getKey())) {
							button.getStyleClass().add(CLASS_BUTTON_DISABLE);
						}
					}
					
					buttonPane.getChildren().add(botoneraAccionesTabla);
					buttonPane.setPrefHeight(heigth + extraHeigth);
					buttonPane.setPrefWidth(anchura);
				}
				catch (Exception e) {
					log.error("loadButtons() - Error: " + e.getMessage(), e);
				}
			}
		});
	}

	@Override
	public void executeAction(ActionButtonComponent botonAccionado) {
		String couponCode = ((ButtonConfigurationBean) botonAccionado.getBtAccion().getUserData()).getKey();
		
		if (botonAccionado.getBtAccion().getStyleClass().contains(CLASS_BUTTON_DISABLE)) {
			botonAccionado.getBtAccion().getStyleClass().removeAll(CLASS_BUTTON_DISABLE);
			activeCoupons.put(couponCode, customerCoupons.get(couponCode));
		}
		else {
			botonAccionado.getBtAccion().getStyleClass().add(CLASS_BUTTON_DISABLE);
			activeCoupons.remove(couponCode);
		}
	}

	public void accept() {
		Collection<BasketLoyalCustomerCoupon> values = activeCoupons.values();
		List<BasketLoyalCustomerCoupon> coupons = new ArrayList<>();
		coupons.addAll(values);
		
		closeSuccess(coupons);
	}
	
	protected void refreshToggleButtonText() {
		if(anySelected) {
			btSelectAll.setText(I18N.getText("Seleccionar todo"));
		} else {
			btSelectAll.setText(I18N.getText("Deseleccionar todo"));
		}
	}
	
	public void selectDeselectAll() {
		ButtonsGroupComponent botoneraComponent =  (ButtonsGroupComponent) buttonPane.getChildren().get(0);
		for (String couponCode : customerCoupons.keySet()) {
			ActionButtonComponent botonAccionado = botoneraComponent.getButtonGroupButtonByKey(couponCode);
			if(anySelected) {
				log.debug("selectDeselectAll() - Se están seleccionando todos los cupones");
				if(botonAccionado.getBtAccion().getStyleClass().contains(CLASS_BUTTON_DISABLE)) {
					botonAccionado.getBtAccion().getStyleClass().removeAll(CLASS_BUTTON_DISABLE);
					activeCoupons.put(couponCode, customerCoupons.get(couponCode));
				}
			} else {
				log.debug("selectDeselectAll() - Se están deseleccionando todos los cupones");
				if(!botonAccionado.getBtAccion().getStyleClass().contains(CLASS_BUTTON_DISABLE)) {
					botonAccionado.getBtAccion().getStyleClass().add(CLASS_BUTTON_DISABLE);
					activeCoupons.remove(couponCode);
				}
			}
		}
		anySelected = !anySelected;
		refreshToggleButtonText();
		
	}

}
