package com.comerzzia.iskaypet.pos.gui.ventas.coupons;

import com.comerzzia.iskaypet.pos.persistence.fidelizacion.IskaypetCustomerCouponDTO;
import com.comerzzia.iskaypet.pos.services.promociones.IskaypetPromocionesService;
import com.comerzzia.iskaypet.pos.util.date.DateUtils;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.imagen.BotonBotoneraImagenCompletaComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.gui.ventas.tickets.articulos.coupons.CustomerCouponsController;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.application.Platform;
import javafx.scene.control.Button;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GAPXX - SOLUCIÓN ERROR PANTALLA CUPONES
 */
@Primary
@Component
@SuppressWarnings("all")
public class IskaypetCustomerCouponsController extends com.comerzzia.pos.gui.ventas.tickets.articulos.coupons.CustomerCouponsController{

	private static final Logger log = Logger.getLogger(IskaypetCustomerCouponsController.class.getName());

    @Autowired
    protected IskaypetPromocionesService iskaypetPromocionesService;

	@Override
	public void initializeForm() throws InitializeGuiException{
		try{
			// PASO01 : Sacamos el listado de cupones recibidos por parámetros, cupones del fidelizado y los activos del mismo.
			List<CustomerCouponDTO> activeCouponsParam = (List<CustomerCouponDTO>) getDatos().get(PARAM_ACTIVE_COUPONS);
			List<IskaypetCustomerCouponDTO> customerCouponsParam = (List<IskaypetCustomerCouponDTO>) getDatos().get(PARAM_CUSTOMER_COUPONS);

			// PASO02 : Limpiamos los listado de pantalla, los que cargan la botonera.
			this.activeCoupons = new HashMap<String, CustomerCouponDTO>();
			this.customerCoupons = new HashMap<String, CustomerCouponDTO>();
			
			// PASO03 : En caso de tener cupones, cargamos los datos en los listados de pantalla que cargan la botonera.
			if(activeCouponsParam != null && !activeCouponsParam.isEmpty()){
				log.debug("initializeForm() - Cargando cupones, CUPONES ACTIVOS encontrados : " + activeCouponsParam.size());
				
				for(CustomerCouponDTO activeCoupon : activeCouponsParam){
					this.activeCoupons.put(activeCoupon.getCouponCode(), activeCoupon);
				}
			}
			if(customerCouponsParam != null && !customerCouponsParam.isEmpty()){
				log.debug("initializeForm() - Cargando cupones, CUPONES DEL FIDELIZADO encontrados : " + customerCouponsParam.size());
				
				for(CustomerCouponDTO customerCoupon : customerCouponsParam){
					this.customerCoupons.put(customerCoupon.getCouponCode(), customerCoupon);
				}
			}
	
			// PASO04 : Parte estándar de carga de botonera.
			loadButtons(customerCouponsParam);
			if(this.activeCoupons.isEmpty()){
				tgSelectAll.setSelected(true);
			}
			refreshToggleButtonText();
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al cargar la pantalla de cupones con los datos recibidos") + " : " + e.getMessage();
			log.error("initializeForm() - " + msgError, e);
			throw new InitializeGuiException(msgError, e);
		}
	}
	
	/**
	 * Método necesario porque en estándar está en private.
	 * @param availableCoupons
	 * @throws CargarPantallaException
	 */
	protected void loadButtons(List<IskaypetCustomerCouponDTO> availableCoupons) throws CargarPantallaException{
		if(availableCoupons != null && !availableCoupons.isEmpty()){
			final List<ConfiguracionBotonBean> buttons = new ArrayList<ConfiguracionBotonBean>();
			numRows = new BigDecimal(availableCoupons.size()).divide(new BigDecimal(numColumns), 0, RoundingMode.UP).intValue();
			availableCoupons.sort(Comparator.comparing(
					CustomerCouponDTO::getEndDate,
					Comparator.nullsLast(Comparator.naturalOrder())
			));

            for(IskaypetCustomerCouponDTO item : availableCoupons){
                String texto = item.getCouponName().toUpperCase();
                if(!contienePrecioEuro(texto)){
                    if(item.getBalance() != null && !item.getBalance().equals(BigDecimal.ZERO)){
                        texto +=  " " + BigDecimalUtil.redondear(item.getBalance()) + "€";
                    } else {
                        texto = getTextoFromExtensiones(item, texto);
                    }
                }
				if (item.getEndDate() != null) {
					texto += " - Val: " + DateUtils.formatDate(item.getEndDate(), "dd/MM/yy");
				}
				ConfiguracionBotonBean configuracionBotonBean = new ConfiguracionBotonBean(item.getImageUrl(), texto, "",
						item.getCouponCode(), "SET_COUPON_STATE");
				buttons.add(configuracionBotonBean);
			}
			if(numRows == 1){
				for(int i = 0; i < numColumns; i++){
					buttons.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
				}
				numRows = 2;
			}
			final CustomerCouponsController controller = this;
			Platform.runLater(new Runnable(){
				@Override
				public void run(){
					try{
						double heigth = numRows * 118.0;
						double extraHeigth = 100;
						if(heigth < scrollPane.getHeight()){
							heigth = scrollPane.getHeight() - 20;
							extraHeigth = 0;
						}
						double anchura = getStage().getWidth() - 80;
						log.trace("loadButtons() - Buttons pane. Height: " + heigth + ". Width: " + anchura);
						if(botoneraAccionesTabla != null){
							botoneraAccionesTabla.destroy();
						}
						botoneraAccionesTabla = new BotoneraComponent(numRows, numColumns, controller, buttons, anchura, heigth, BotonBotoneraImagenCompletaComponent.class.getName());
						for(Button button : (List<Button>) botoneraAccionesTabla.getListaBotones()){
							ConfiguracionBotonBean config = (ConfiguracionBotonBean) button.getUserData();
							if(!activeCoupons.containsKey(config.getClave())){
								button.getStyleClass().add(CLASS_BUTTON_DISABLE);
							}
						}
						buttonPane.getChildren().add(botoneraAccionesTabla);
						buttonPane.setPrefHeight(heigth + extraHeigth);
						buttonPane.setPrefWidth(anchura);
						
						if(activeCoupons != null && customerCoupons != null){
							if(activeCoupons.size() == customerCoupons.size()){
								tgSelectAll.setText(I18N.getTexto("Deseleccionar todo"));
							}
						}
					}
					catch(Exception e){
						log.error("loadButtons() - Error: " + e.getMessage(), e);
					}
				}
			});
		}
		else{
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay cupones disponibles.."), getStage());
		}
	}

    private String getTextoFromExtensiones(IskaypetCustomerCouponDTO item, String texto) {
        Map<String, String> mapaExtensiones = iskaypetPromocionesService.getExtensionesPromocion(item.getIdPromoOrigen());
        if(!mapaExtensiones.isEmpty()) {
            String importe = mapaExtensiones.get("importeDescCupon");
            String tipo = mapaExtensiones.get("tipoDescCupon");
            texto +=  " " + importe + (tipo.equalsIgnoreCase("importe") ? "€" : "%");
        }
        return texto;
    }

    private boolean contienePrecioEuro(String texto) {
        if (texto == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("\\d+(?:[.,]\\d+)?\\s*€$");
        Matcher matcher = pattern.matcher(texto);
        return matcher.find();
    }

    @Override
	public void selectDeselectAll() {
		BotoneraComponent botoneraComponent =  botoneraAccionesTabla;
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
