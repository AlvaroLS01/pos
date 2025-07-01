package com.comerzzia.pos.gui.sales.retail.pointsredemption;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.loyalty.client.AccountsApiClient;
import com.comerzzia.api.loyalty.client.CardsApiClient;
import com.comerzzia.api.loyalty.client.model.BalanceToCoupon;
import com.comerzzia.api.loyalty.client.model.Card;
import com.comerzzia.api.loyalty.client.model.Coupon;
import com.comerzzia.api.loyalty.client.model.NewCoupon;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.number.NumberUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

@Controller
@CzzScene
public class PointsRedemptionController extends SceneController implements Initializable {

	private static final Logger log = Logger.getLogger(PointsRedemptionController.class);

	public static final String PARAM_LOYAL_CUSTOMER = "PARAM_LOYAL_CUSTOMER";
	public static final String POINTS = "POINTS";
	
	public static final String AVAILABLES_POINTS = "AVAILABLES_POINTS";
	public static final String MAX_POINTS = "MAX_POINTS";
	
	@FXML
	protected Label lbPointsToRedeem;
	
	@FXML
	protected Label lbTotalAmount;

	@FXML
	protected NumericTextField tfPointsToRedeem;
	
	@FXML
	protected Label lbTotalAmountNumber;
	
	@FXML
	protected Label lbPointsToRedeemText;
	
	@Autowired
	protected VariableServiceFacade variablesServices;
	
	// variables contabilizar cantidades
	protected BigDecimal maxPoints = BigDecimal.ZERO.setScale(0, RoundingMode.DOWN);
	protected BigDecimal availablePoints = BigDecimal.ZERO.setScale(0, RoundingMode.DOWN);
	protected BigDecimal pointsForEuro = BigDecimal.ONE;
	protected BigDecimal totalPoints = BigDecimal.ZERO;
	
	protected BasketLoyalCustomer basketLyCustomer;
	
	@Autowired
	protected CardsApiClient cardsApiClient;
	
	@Autowired
	protected AccountsApiClient accountsApiClient;
	
	@Autowired
	protected ModelMapper modelMapper;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando ventana...");
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		basketLyCustomer = (BasketLoyalCustomer) sceneData.get(PARAM_LOYAL_CUSTOMER);
		pointsForEuro = BigDecimal.valueOf(Double.valueOf(variablesServices.getVariableAsString(VariableServiceFacade.FIDELIZACION_PUNTOS_FACTOR_CONVERSION)));
		maxPoints = ((BigDecimal) sceneData.get(MAX_POINTS)).divide(BigDecimal.ONE, 0, RoundingMode.DOWN);
		availablePoints = ((BigDecimal) sceneData.get(AVAILABLES_POINTS)).divide(BigDecimal.ONE, 0, RoundingMode.DOWN);
					
		formatForm();
		tfPointsToRedeem.setText(FormatUtils.getInstance().formatNumber(maxPoints,0));
	}

	protected void formatForm() {
		lbPointsToRedeem.setText(I18N.getText("Actualmente dispone de ") 
				+ FormatUtils.getInstance().formatNumber(availablePoints,0)
				+ I18N.getText(" puntos."));
		lbPointsToRedeemText.setText(I18N.getText("Indique cuántos puntos desea canjear por un cupón") + ":");
		lbTotalAmountNumber.setText(I18N.getText("Importe resultante")+":");
		formatPoints(maxPoints);
	}

	@Override
	public void initializeFocus() {
		tfPointsToRedeem.requestFocus();
	}
	
	public void refreshPointsKeyboard(KeyEvent event) {
		log.trace("refreshPointsKeyboard() - Aceptar");
		BigDecimal pointsToRedeem = BigDecimal.ZERO;
		if (!StringUtils.isEmpty(tfPointsToRedeem.getText())) {
			pointsToRedeem = FormatUtils.getInstance().parseBigDecimal(tfPointsToRedeem.getText(),0);
		}
		formatPoints(pointsToRedeem);	
	}
	
	protected void formatPoints(BigDecimal points) {
		log.debug("formatPoints() - Formatea los puntos para que no sobren ninguno con respecto el importe");
		totalPoints = points.multiply(pointsForEuro).setScale(2, RoundingMode.DOWN);
		lbTotalAmountNumber.setText(FormatUtils.getInstance().formatAmount(totalPoints) + FormatUtils.getCurrency().getSymbol());
	}

	public void acceptPointsButton(ActionEvent event) {
		log.debug("acceptPointsButton() - Aceptar");
		actionSendPointsToInvoice();
	}
	
	public void acceptPointsKeyboard(KeyEvent event) {
		log.debug("acceptPointsKeyboard() - Permite actualizar o aceptar en función del evento que se produzca");
		if (event.getCode() == KeyCode.ENTER) {
			log.debug("acceptPointsKeyboard(() - Aceptar");
			actionSendPointsToInvoice();
		} else if(event.getCode() == KeyCode.ESCAPE){
			log.debug("acceptPointsKeyboard(() - Cancelar");
			closeCancel();
		} else {
			// se ejecuta por si se acciona otro botón que no sea ENTER, como por ejemplo SUPR o DEL
			refreshPointsKeyboard(event);
		}
	}
	
	protected void actionSendPointsToInvoice() {
		if(DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Se va a generar un cupón por importe de {0} al fidelizado {1}. Esta operación no se podrá anular. ¿Está seguro de que desea continuar?",
                FormatUtils.getInstance().formatAmount(totalPoints) + FormatUtils.getCurrency().getSymbol(), basketLyCustomer.getName()+" "+basketLyCustomer.getLastName()))){
			BigDecimal points = FormatUtils.getInstance().parseBigDecimal(tfPointsToRedeem.getText(),0);
			if(validateForm(points)) {
				points = adjustPoints(points);
				BigDecimal discount = points.multiply(pointsForEuro).setScale(2, RoundingMode.DOWN);
				addCouponPoints(discount, points);
				closeSuccess();
			}
		}		
	}
	
	protected boolean validateForm(BigDecimal points) {
		boolean res = false;
		if(points.compareTo(BigDecimal.ZERO)<0) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No puede introducir puntos negativos"));
		} else if(points.compareTo(BigDecimal.ZERO)==0) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No puede introducir 0 puntos"));
		} else if(points.compareTo(availablePoints) <= 0) {	
			res = true;
		} else {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El valor del campo debe ser un número positivo entero y menor al total de puntos máximos"));
		}
		return res;
	}

	protected BigDecimal adjustPoints(BigDecimal points) {
		BigDecimal pointsPrice = points.multiply(pointsForEuro).setScale(2, RoundingMode.DOWN);
		return pointsPrice.divide(pointsForEuro, 0, RoundingMode.DOWN);
	}
	
	protected void addCouponPoints(BigDecimal discount, BigDecimal points) {
		try {
			if (discount != null) {
				if (discount.doubleValue() > 0.0) {
					Long lyCustomerId = basketLyCustomer.getLyCustomerId();
					String cardNumber = basketLyCustomer.getCardNumber();

					Date today = new Date(System.currentTimeMillis());
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					String todayStr = sdf.format(today);
					
					String couponType = "DEFAULT";
					String counponTypeVar = variablesServices.getVariableAsString(VariableServiceFacade.FIDELIZACION_PUNTOS_TIPO_CUPON_CANJEO);
					if (StringUtils.isNotBlank(counponTypeVar)) {
						couponType = counponTypeVar;
					}
					else {
						DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El tipo de cupón es nulo"));
						return;
					}

					Card card = cardsApiClient.findCardByNumber(cardNumber).getBody();
					Long cardId = card.getCardId();
					Long accountId = card.getAccount().getCardAccountId();

					String couponTitle = I18N.getText("Cupón {0}", discount.toString()) + FormatUtils.getCurrency().getSymbol();
					String couponDesc = I18N.getText("Cupón {0} por puntos acumulados a {1} en tarjeta fidelizado {2}", discount.toString(), todayStr, cardNumber);

					// La fecha de inicio será el dia de hoy a las 00:00:00
					Date startDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

					String promoId = null;
					String promoIdVar = variablesServices.getVariableAsString(VariableServiceFacade.FIDELIZACION_PUNTOS_PROMOCION_CANJEO);
					if (StringUtils.isNotBlank(promoIdVar)) {
						promoId = promoIdVar;
					}
					else {
						DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("La promoción canjeo puntos en cupón esta vacia o es nula"));
						return;
					}

					NewCoupon newCoupon = new NewCoupon();
					newCoupon.setCouponTypeCode(couponType);
					newCoupon.setCouponName(couponTitle);
					newCoupon.setCouponDescription(couponDesc);
					newCoupon.setBalance(discount);
					newCoupon.setCustomerMaxUses(1L);
					newCoupon.setPromotionId(NumberUtils.unformatLong(promoId, null));
					if (startDate != null) {
						newCoupon.setStartDate(startDate);
					}
					newCoupon.setLoyalCustomerId(lyCustomerId);

					BalanceToCoupon balanceToCoupon = new BalanceToCoupon();
					balanceToCoupon.setAmount(points);
					balanceToCoupon.setCardId(cardId);
					balanceToCoupon.setNewCoupon(newCoupon);
					balanceToCoupon.setUserId(lyCustomerId);

					convertPointsToCoupon(accountId, balanceToCoupon);
				}
			}
		}
		catch (Exception e) {
			log.error("addCouponPoints() - Ha ocurrido un error creando el cupon a partir de puntos: " + e.getMessage(), e);
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha fallado el canjeo de los puntos. Contacte con un administrador."), e);
		}
	}
	
	public void convertPointsToCoupon(Long accountId, BalanceToCoupon balanceToCoupon) {
		try {
			NuevosPuntosToCouponTask newCouponTask = new NuevosPuntosToCouponTask(accountsApiClient, accountId, balanceToCoupon, new RestBackgroundTask.FailedCallback<Coupon>() {
				@Override
				public void succeeded(Coupon result) {
					log.debug("El cupon con código "+result.getCouponCode()+" se ha generado con éxito");
				}

				@Override
				public void failed(Throwable throwable) {
					log.error("failed() - La creación del cupón ha fallado: " + throwable.getMessage(), throwable);
					DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha fallado el canjeo de los puntos. Contacte con un administrador."), throwable);
				}
			},getStage());
			newCouponTask.start();
			
			// espera hasta que se termine la tarea
			newCouponTask.get();
		} catch (Exception e) {
			log.error(I18N.getText("convertPointsToCoupon() - Fallo al convertir puntos a un cupón:\n") + e.getMessage());
		}
	}
	
	public class NuevosPuntosToCouponTask extends RestBackgroundTask<Coupon>{
		
		protected AccountsApiClient apiAccount;
		protected Long accountId;
		protected BalanceToCoupon balanceToCoupon;
		
		public NuevosPuntosToCouponTask(AccountsApiClient apiAccount, Long accountId, BalanceToCoupon balanceToCoupon, FailedCallback<Coupon> callback, Stage stage) {
			super(callback, stage);
			this.apiAccount = apiAccount;
			this.accountId = accountId;
			this.balanceToCoupon = balanceToCoupon;
		}

		@Override
		public Coupon execute() throws Exception {
			return apiAccount.convertBalanceToCoupon(accountId, balanceToCoupon).getBody();
		}
		
	}
}
