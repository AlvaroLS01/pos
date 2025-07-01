package com.comerzzia.pos.gui.sales.loyalcustomer.search;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.LyCustomersApiClient;
import com.comerzzia.api.loyalty.client.model.LyCustomer;
import com.comerzzia.core.commons.exception.ApiException;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerController;
import com.comerzzia.pos.gui.sales.loyalcustomer.search.select.LoyalCustomerSelectController;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.log4j.Log4j;

@Component
@CzzScene
@Log4j
public class LoyalCustomerSearchController extends SceneController {
	public static final String PARAMETRO_IN_HEADER_TEXT = "HEADER_TEXT";
	public static final String PARAMETRO_CARD_TYPE = "CARD_TYPE";
	public static final String PARAMETRO_LOYAL_CUSTOMERS_SELECTION = "LOYAL_CUSTOMERS_SELECTION";
	public static final String PARAMETRO_LOYAL_CUSTOMER_SELECTED = "LOYAL_CUSTOMER_SELECTED";

	@FXML
	protected TextField tfCardNumber, tfName, tfLastName, tfDocument, tfEmail, tfPhone;
	
	@FXML
	protected Button btNewLoyalCustomer;

	protected LoyalCustomerSeachFormValidationBean frSearchLoyalCustomer;
	protected BasketManager<?, ?> basketManager;
	
	@Autowired
	protected LyCustomersApiClient customersApiClient;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		frSearchLoyalCustomer = SpringContext.getBean(LoyalCustomerSeachFormValidationBean.class);
		frSearchLoyalCustomer.setFormField("cardNumber", tfCardNumber);
		frSearchLoyalCustomer.setFormField("name", tfName);
		frSearchLoyalCustomer.setFormField("lastName", tfLastName);
		frSearchLoyalCustomer.setFormField("phone", tfPhone);
		frSearchLoyalCustomer.setFormField("document", tfDocument);
		frSearchLoyalCustomer.setFormField("email", tfEmail);
	}

	@Override
	public void initializeComponents() {

	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		
		validateDeviceConfiguration();
		
		tfCardNumber.setText("");
		tfName.setText("");
		tfLastName.setText("");
		tfEmail.setText("");
		tfPhone.setText("");
		tfDocument.setText("");
		String text = (String) sceneData.get(PARAMETRO_IN_HEADER_TEXT);
		if (text == null) {
			text = I18N.getText("Lea o escriba el código de barras de la tarjeta");
		}

		String cardType = (String) sceneData.get(PARAMETRO_CARD_TYPE);
		if (cardType != null) {
			if ("GIFTCARD".equals(cardType)) {
				btNewLoyalCustomer.setVisible(false);
			}
			else if ("FIDELIZADO".equals(cardType)) {
				btNewLoyalCustomer.setVisible(true);
			}
		}
		basketManager = (BasketManager<?, ?>) sceneData.get(BasketItemizationControllerAbstract.BASKET_KEY);
	}

	protected void validateDeviceConfiguration() throws InitializeGuiException {
		log.debug("validateDeviceConfiguration() - Validating loyal customer device configuration.");
		if(!Devices.getInstance().getLoyaltyCard().isReady()) {
			log.debug("onSceneOpen() - Loyal customer device is not configured.");
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("El dispositivo de fidelización no está configurado."));
			throw new InitializeGuiException(false);
		}
	}

	@Override
	public void initializeFocus() {
		tfCardNumber.requestFocus();
	}

	@FXML
	public void actionAcceptIntro(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			actionAccept();
		}
	}

	@FXML
	public void actionAccept() {
		log.debug("actionAccept() - Selection accepted.");
		if (StringUtils.isBlank(tfCardNumber.getText()) && StringUtils.isBlank(tfLastName.getText()) && StringUtils.isBlank(tfName.getText()) && StringUtils.isBlank(tfPhone.getText())
		        && StringUtils.isBlank(tfEmail.getText()) && StringUtils.isBlank(tfDocument.getText())) {

			DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Es necesario rellenar al menos uno de los campos de búsqueda"));
		}
		else {
			frSearchLoyalCustomer.setCardNumber(tfCardNumber.getText());
			frSearchLoyalCustomer.setLastName(tfLastName.getText());
			frSearchLoyalCustomer.setName(tfName.getText());
			frSearchLoyalCustomer.setPhone(tfPhone.getText());
			frSearchLoyalCustomer.setEmail(tfEmail.getText());
			frSearchLoyalCustomer.setDocument(tfDocument.getText());

			// Validamos el formulario
			Set<ConstraintViolation<LoyalCustomerSeachFormValidationBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frSearchLoyalCustomer);
			if (constraintViolations.size() >= 1) {
				ConstraintViolation<LoyalCustomerSeachFormValidationBean> next = constraintViolations.iterator().next();
				frSearchLoyalCustomer.setErrorStyle(next.getPropertyPath(), true);
				frSearchLoyalCustomer.setFocus(next.getPropertyPath());
				DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(next.getMessage());
			}
			else {
				if (StringUtils.isNotBlank(frSearchLoyalCustomer.getCardNumber())) {
					super.closeSuccess(frSearchLoyalCustomer.getCardNumber());
				}
				else {
					try {
						String name = frSearchLoyalCustomer.getName().isEmpty() ? null : frSearchLoyalCustomer.getName();
						String lastName = frSearchLoyalCustomer.getLastName().isEmpty() ? null : frSearchLoyalCustomer.getLastName();
						String email = frSearchLoyalCustomer.getEmail().isEmpty() ? null : frSearchLoyalCustomer.getEmail();
						String vatNumber = frSearchLoyalCustomer.getDocument().isEmpty() ? null : frSearchLoyalCustomer.getDocument();
						String phone = frSearchLoyalCustomer.getPhone().isEmpty() ? null : frSearchLoyalCustomer.getPhone();
						String cardNumber = frSearchLoyalCustomer.getCardNumber().isEmpty() ? null : frSearchLoyalCustomer.getCardNumber();
						
						List<LyCustomer> customers = customersApiClient.findLyCustomerPage(null, null, name, lastName, email, vatNumber, phone, cardNumber, null, null, null).getBody();
						
						if (customers.size() == 0) {
							DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("No se ha encontrado ningún fidelizado con los datos introducidos"));
						}
						else {
							if (customers.size() == 1) {
								selectCardNumber(customers.get(0));
							}
							else if (customers.size() > 1) {
								sceneData.put(PARAMETRO_LOYAL_CUSTOMERS_SELECTION, customers);
								actionSelectLoyalCustomer(new SceneCallback<LyCustomer>() {
									
									@Override
									public void onSuccess(LyCustomer customer) {
										selectCardNumber(customer);
									}
								});
							}
						}
					}
					catch (ApiException e) {
						log.error("actionAccept() - ha habido un problema con la petición REST: " + e.getMessage(), e);
						DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("No se ha podido establecer la conexión con la central, revise que la conexión es adecuada"),e);
					}
					catch (Exception e) {
						log.error("actionAccept() - Ha ocurrido un error: " + e.getMessage(), e);
						DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha ocurrido un error durante la petición a la central, consulte con el administrador."), e);
					}
				}
			}
		}
	}
	
	protected void selectCardNumber(LyCustomer lyCustomer) {
		String cardNumber = lyCustomer.getCardNumber();
		if (StringUtils.isBlank(cardNumber)) {
			log.debug("selectCardNumber() - No loyalty card available.");
			DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(I18N.getText("El fidelizado no dispone de una tarjeta"));
			
		}
		else {
			log.debug("selectCardNumber() - Selected card number: "+lyCustomer.getCardNumber());
			super.closeSuccess(cardNumber);
		}
	}

	@FXML
	public void actionNewLoyalCustomer() {
		sceneData.put(LoyalCustomerController.PARAM_LY_CUSTOMER_ID, null);
		sceneData.put(LoyalCustomerController.PARAM_MODE, "INSERCION");
		sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, basketManager);
		openActionScene(AppConfig.getCurrentConfiguration().getLoyaltyAction(), LoyalCustomerController.class);
	}

	protected void actionSelectLoyalCustomer(SceneCallback<?> callback) {
		openScene(LoyalCustomerSelectController.class, callback);
	}

}
