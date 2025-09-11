package com.comerzzia.pos.gui.ventas.tickets.articulos.coupons;

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

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.imagen.BotonBotoneraImagenCompletaComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

@Component
public class CustomerCouponsController extends WindowController implements IContenedorBotonera {

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
	protected ToggleButton tgSelectAll;

	protected double widthScrollPane;
	protected int numRows;
	protected int numColumns;

	protected Map<String, CustomerCouponDTO> customerCoupons;
	protected Map<String, CustomerCouponDTO> activeCoupons;

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
	public void initializeForm() throws InitializeGuiException {
		try {
			List<CustomerCouponDTO> availableCoupons = (List<CustomerCouponDTO>) getDatos().get(PARAM_CUSTOMER_COUPONS);
			List<CustomerCouponDTO> activeCoupons = (List<CustomerCouponDTO>) getDatos().get(PARAM_ACTIVE_COUPONS);

			buttonPane.getChildren().clear();

			customerCoupons = new HashMap<String, CustomerCouponDTO>();
			for (CustomerCouponDTO coupon : availableCoupons) {
				customerCoupons.put(coupon.getCouponCode(), coupon);
			}

			this.activeCoupons = new HashMap<String, CustomerCouponDTO>();
			for (CustomerCouponDTO coupon : activeCoupons) {
				this.activeCoupons.put(coupon.getCouponCode(), coupon);
			}

			loadButtons(availableCoupons);
			
			if(activeCoupons.isEmpty()) {
				tgSelectAll.setSelected(true);
			}
			refreshToggleButtonText();
		}
		catch (Exception e) {
			log.error("initializeForm() - Error: " + e.getMessage(), e);
			
			throw new InitializeGuiException(I18N.getTexto("Ha habido un error al mostrar la pantalla de cupones del fidelizado. Por favor, contacte con el administrador."), e);
		}
	}

	private void loadButtons(List<CustomerCouponDTO> availableCoupons) throws CargarPantallaException {
		if (availableCoupons != null && !availableCoupons.isEmpty()) {					
			final List<ConfiguracionBotonBean> buttons = new ArrayList<ConfiguracionBotonBean>();

			numRows = new BigDecimal(availableCoupons.size()).divide(new BigDecimal(numColumns), 0, RoundingMode.UP).intValue();	
			
			for(CustomerCouponDTO item : availableCoupons) {
				ConfiguracionBotonBean configuracionBotonBean = new ConfiguracionBotonBean(item.getImageUrl(), item.getCouponName().toUpperCase(), "", item.getCouponCode(), "SET_COUPON_STATE");
				buttons.add(configuracionBotonBean);
			}
			
			if(numRows == 1) {
				for(int i = 0 ; i < numColumns ; i++) {
					buttons.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
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

						BotoneraComponent botoneraAccionesTabla = new BotoneraComponent(numRows, numColumns, controller, buttons, anchura, heigth, BotonBotoneraImagenCompletaComponent.class.getName());
						
						for(Button button : (List<Button>) botoneraAccionesTabla.getListaBotones()) {
							ConfiguracionBotonBean config = (ConfiguracionBotonBean) button.getUserData();
							
							if(!activeCoupons.containsKey(config.getClave())) {
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
		else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay cupones disponibles.."), getStage());
		}
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException {
		String couponCode = ((ConfiguracionBotonBean) botonAccionado.getBtAccion().getUserData()).getClave();
		
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
		Collection<CustomerCouponDTO> values = activeCoupons.values();
		List<CustomerCouponDTO> coupons = new ArrayList<CustomerCouponDTO>();
		coupons.addAll(values);
		
		getDatos().put(PARAM_CUSTOMER_COUPONS, coupons);
		getStage().close();
	}
	
	protected void refreshToggleButtonText() {
		if(tgSelectAll.isSelected()) {
			tgSelectAll.setText(I18N.getTexto("Seleccionar todo"));
		} else {
			tgSelectAll.setText(I18N.getTexto("Deseleccionar todo"));
		}
	}
	
	public void selectDeselectAll() {
		BotoneraComponent botoneraComponent =  (BotoneraComponent) buttonPane.getChildren().get(0);
		for (String couponCode : customerCoupons.keySet()) {
			BotonBotoneraComponent botonAccionado = botoneraComponent.getBotonBotonera(couponCode);
			if(!tgSelectAll.isSelected()) {
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
		refreshToggleButtonText();
		
	}

}
