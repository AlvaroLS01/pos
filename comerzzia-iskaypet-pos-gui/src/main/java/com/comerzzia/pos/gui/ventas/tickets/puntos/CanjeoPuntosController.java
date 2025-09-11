package com.comerzzia.pos.gui.ventas.tickets.puntos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.loyalty.client.AccountsApi;
import com.comerzzia.api.loyalty.client.CardsApi;
import com.comerzzia.api.loyalty.client.model.BalanceToCouponRequestDTO;
import com.comerzzia.api.loyalty.client.model.CardDTO;
import com.comerzzia.api.loyalty.client.model.CouponDTO;
import com.comerzzia.api.loyalty.client.model.NewCoupon;
import com.comerzzia.core.model.variables.VariableBean;
import com.comerzzia.core.servicios.api.ComerzziaApiManager;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.servicios.variables.VariableNotFoundException;
import com.comerzzia.core.servicios.variables.Variables;
import com.comerzzia.core.servicios.variables.VariablesService;
import com.comerzzia.core.util.numeros.Numero;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

@Controller
public class CanjeoPuntosController extends WindowController implements Initializable {

	private static final Logger log = Logger.getLogger(CanjeoPuntosController.class);

	@FXML
	protected Label lbTextoPuntosAGastar;
	
	@FXML
	protected Label lbImporteResultante;

	@FXML
	protected TextFieldImporte tfPuntosAGastar;
	
	@FXML
	protected Label lbPrecioTraducido;
	
	@FXML
	protected Label lbPuntosACambiar;
	
	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private VariablesService variablesService;
	
	public static final String VARIABLE_FIDELIZADO = "PUNTOS_FIDELIZADO";
	public static final String RESULTADOS_PUNTOS = "RESULTADOS_PUNTOS";
	
	public static final String PUNTOS_DISPONIBLES = "PUNTOS_DISPONIBLES";
	public static final String PUNTOS_MAXIMOS = "PUNTOS_MAXIMOS";
	
	// variables contabilizar cantidades
	protected BigDecimal puntosMaximos = BigDecimal.ZERO.setScale(0, RoundingMode.DOWN);
	protected BigDecimal puntosDisponibles = BigDecimal.ZERO.setScale(0, RoundingMode.DOWN);
	protected BigDecimal puntosPorEuro = BigDecimal.ONE;
	protected BigDecimal precioAcanjear = BigDecimal.ZERO;
	
	@Autowired
	private Sesion sesion;
	
	protected FidelizacionBean fidelizado;
	
	@Autowired
	protected ComerzziaApiManager apiManager;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando ventana...");
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
        registrarAccionCerrarVentanaEscape();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		fidelizado = (FidelizacionBean) getDatos().get(VARIABLE_FIDELIZADO);
		puntosPorEuro = BigDecimal.valueOf(Double.valueOf(variablesServices.getVariableAsString(VariablesServices.FIDELIZACION_PUNTOS_FACTOR_CONVERSION)));
		puntosMaximos = ((BigDecimal) getDatos().get(PUNTOS_MAXIMOS)).divide(BigDecimal.ONE, 0, RoundingMode.DOWN);
		puntosDisponibles = ((BigDecimal) getDatos().get(PUNTOS_DISPONIBLES)).divide(BigDecimal.ONE, 0, RoundingMode.DOWN);
					
		formateaVentanaPagoPuntos();
		tfPuntosAGastar.setText(FormatUtil.getInstance().formateaNumero(puntosMaximos,0));
		getStage().close();
	}

	protected void formateaVentanaPagoPuntos() {
		lbTextoPuntosAGastar.setText(I18N.getTexto("Actualmente dispone de ") 
				+ FormatUtil.getInstance().formateaNumero(puntosDisponibles,0)
				+ I18N.getTexto(" puntos."));
		lbPuntosACambiar.setText(I18N.getTexto("Indique cuántos puntos desea canjear por un cupón") + ":");
		lbPrecioTraducido.setText(I18N.getTexto("Importe resultante")+":");
		formatearPrecioPuntos(puntosMaximos);
	}

	@Override
	public void initializeFocus() {
		tfPuntosAGastar.requestFocus();
	}
	
	public void actualizarPuntosTeclado(KeyEvent event) {
		log.trace("actualizarPuntosTeclado() - Aceptar");
		BigDecimal puntosAgastar = FormatUtil.getInstance().desformateaBigDecimal(tfPuntosAGastar.getText(),0);
		formatearPrecioPuntos(puntosAgastar);	
	}
	
	protected void formatearPrecioPuntos(BigDecimal puntosAgastar) {
		log.debug("formatearPrecioPuntos() - Formatea los puntos para que no sobren ninguno con respecto el importe");
		precioAcanjear = puntosAgastar.multiply(puntosPorEuro).setScale(2, RoundingMode.DOWN);
		lbPrecioTraducido.setText(FormatUtil.getInstance().formateaImporte(precioAcanjear) + FormatUtil.getCurrency().getSymbol());
	}

	public void aceptarPuntosBoton(ActionEvent event) {
		log.debug("aceptarArticuloBoton() - Aceptar");
		accionEnviarPuntosFacturacion();
	}
	
	public void aceptarPuntosTeclado(KeyEvent event) {
		log.debug("aceptarPuntosTeclado() - Permite actualizar o aceptar en función del evento que se produzca");
		if (event.getCode() == KeyCode.ENTER) {
			log.debug("aceptarPuntosTeclado(() - Aceptar");
			accionEnviarPuntosFacturacion();
		} else if(event.getCode() == KeyCode.ESCAPE){
			log.debug("aceptarPuntosTeclado(() - Cancelar");
			accionCancelar();
		} else {
			// se ejecuta por si se acciona otro botón que no sea ENTER, como por ejemplo SUPR o DEL
			actualizarPuntosTeclado(event);
		}
	}
	
	protected void accionEnviarPuntosFacturacion() {
		if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se va a generar un cupón por importe de {0} al fidelizado {1}. "+
				"Esta operación no se podrá anular. ¿Está seguro de que desea continuar?",
				FormatUtil.getInstance().formateaImporte(precioAcanjear) + FormatUtil.getCurrency().getSymbol(), fidelizado.getNombre()+" "+fidelizado.getApellido()), getStage())){
			BigDecimal puntosAGastar = FormatUtil.getInstance().desformateaBigDecimal(tfPuntosAGastar.getText(),0);
			boolean validacionOk = validarFormulario(puntosAGastar);
			if(validacionOk) {
				puntosAGastar = aprovecharPuntos(puntosAGastar);
				BigDecimal descuento = puntosAGastar.multiply(puntosPorEuro).setScale(2, RoundingMode.DOWN);
				addCuponPuntos(descuento, puntosAGastar);
				getStage().close();
			}
		}		
	}
	
	protected boolean validarFormulario(BigDecimal puntosAGastar) {
		boolean res = false;
		if(puntosAGastar.compareTo(BigDecimal.ZERO)<0) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No puede introducir puntos negativos"), this.getStage());
		} else if(puntosAGastar.compareTo(BigDecimal.ZERO)==0) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No puede introducir 0 puntos"), this.getStage());
		} else if(puntosAGastar.compareTo(puntosDisponibles) <= 0) {	
			res = true;
		} else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El valor del campo debe ser un número positivo entero y menor al total de puntos máximos"), this.getStage());
		}
		return res;
	}

	protected BigDecimal aprovecharPuntos(BigDecimal puntos) {
		BigDecimal precioPuntos = puntos.multiply(puntosPorEuro).setScale(2, RoundingMode.DOWN);
		return precioPuntos.divide(puntosPorEuro, 0, RoundingMode.DOWN);
	}
	
	protected void addCuponPuntos(BigDecimal descuento, BigDecimal puntos) { 
		try {
			if(descuento!=null) {
				if(descuento.doubleValue()>0.0) {
					
					DatosSesionBean datosSesion = new DatosSesionBean();
					datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
					datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
					datosSesion.setLocale(new Locale(AppConfig.idioma, AppConfig.pais));
					
					Long fidelizadoId = fidelizado.getIdFidelizado();
					String cardNumber = fidelizado.getNumTarjetaFidelizado();
					
					Date today = new Date(System.currentTimeMillis());
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					String todayStr = sdf.format(today);
					String couponType = "DEFAULT";
					
					CardsApi cardsApi = apiManager.getClient(datosSesion, "CardsApi");
					CardDTO card = cardsApi.getCardByNumber(cardNumber);
					Long cardId = card.getIdTarjeta();
					Long accountId = card.getCuenta().getIdCuentaTarjeta();
					
					String couponTitle = I18N.getTexto("Cupón {0}", descuento.toString()) + FormatUtil.getCurrency().getSymbol();
					String couponDesc = I18N.getTexto("Cupón {0} por puntos acumulados a {1} en tarjeta fidelizado {2}", descuento.toString(),  todayStr, cardNumber);
					
					// Fecha algo anterior (fijamos 10 min) para evitar problemas de tiempo de activación
					LocalDateTime fechaAnterior = LocalDateTime.now().minusMinutes(10L);
					Date startDate = Date.from(fechaAnterior.atZone(ZoneId.systemDefault()).toInstant());
					
					String idPromo = null;
					try {						
						VariableBean idPromoVar = variablesService.consultar(datosSesion, Variables.FIDELIZACION_PUNTOS_PROMOCION_CANJEO);
						idPromo = idPromoVar.getValor();
					}catch (VariableNotFoundException e) {
						log.error(I18N.getTexto("No se ha encontrado id de promocion en las variables:\n") + e.getMessage());
					}
					
					NewCoupon newCoupon = new NewCoupon();
					newCoupon.setCouponTypeCode(couponType);
					newCoupon.setCouponName(couponTitle);
					newCoupon.setCouponDescription(couponDesc);
					newCoupon.setBalance(descuento);
					newCoupon.setCustomerMaxUses(1L);
					newCoupon.setPromotionId(Numero.desformateaLong(idPromo, null));
					if(startDate != null){				
						newCoupon.setStartDate(startDate);
					}
					newCoupon.setLoyalCustomerId(fidelizadoId);
					
					BalanceToCouponRequestDTO dto = new BalanceToCouponRequestDTO();
					dto.setAmount(puntos);
					dto.setCardId(cardId);
					dto.setNewCoupon(newCoupon);
					dto.setUserId(fidelizadoId);
					
					convertPointsToCoupon(datosSesion,puntos,accountId,dto);
				}
			}
		} catch (Exception e) {
			log.error("addCuponPuntos() - Ha ocurrido un error creando el cupon a partir de puntos: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha fallado el canjeo de los puntos. Contacte con un administrador."), e);
		}
	}
	
	
	public void convertPointsToCoupon(DatosSesionBean datosSesion, BigDecimal puntos, Long accountId, BalanceToCouponRequestDTO dto) {
		try {
			AccountsApi apiAccount = apiManager.getClient(datosSesion, "LoyaltyAccountsApi");
			NuevosPuntosToCouponTask nuevoCuponTask = new NuevosPuntosToCouponTask(apiAccount,accountId, dto, new RestBackgroundTask.FailedCallback<CouponDTO>() {
				@Override
				public void succeeded(CouponDTO result) {
					log.debug("El cupon con código "+result.getCouponCode()+" se ha generado con éxito");
				}

				@Override
				public void failed(Throwable throwable) {
					log.error("failed() - La creación del cupón ha fallado: " + throwable.getMessage(), throwable);
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha fallado el canjeo de los puntos. Contacte con un administrador."), throwable);
				}
			},getStage());
			nuevoCuponTask.start();
			
			// espera hasta que se termine la tarea
			nuevoCuponTask.get();
		} catch (Exception e) {
			log.error(I18N.getTexto("convertPointsToCoupon() - Fallo al convertir puntos a un cupón:\n") + e.getMessage());
		}
	}
	
	public class NuevosPuntosToCouponTask extends RestBackgroundTask<CouponDTO>{
		
		protected AccountsApi apiAccount;
		protected BalanceToCouponRequestDTO dto = new BalanceToCouponRequestDTO();
		protected Long accountId;
		
		public NuevosPuntosToCouponTask(AccountsApi apiAccount, Long accountId, BalanceToCouponRequestDTO dto, FailedCallback<CouponDTO> callback, Stage stage) {
			super(callback, stage);
			this.apiAccount = apiAccount;
			this.accountId = accountId;
			this.dto = dto;
		}

		@Override
		public CouponDTO call() throws Exception {		
			return apiAccount.convertBalanceToCoupon(accountId, dto);
		}
		
	}
}
