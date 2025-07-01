package com.comerzzia.pos.gui.sales.specialbarcode;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.model.catalog.ItemSpecialBarcodeConfig;
import com.comerzzia.omnichannel.facade.service.catalog.ItemSpecialBarcodeServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

@Controller
@CzzScene
public class NewSpecialBarcodeController extends SceneController {

	private final Logger log = Logger.getLogger(getClass());

	public static final String PARAM_BARCODE = "BARCODE";

	@FXML
	protected TextField tfDescription, tfPrefix, tfContentPosition, tfContentLength, tfPricePosition, tfPriceWholeNumber, tfPriceFractionPart, tfQuantityPosition, tfQuantityWholeNumber,
	        tfQuantityFractionPart;

	@FXML
	protected CheckBox cbLoyalty;

	@FXML
	protected ComboBox<String> cbCodeType;

	protected ItemSpecialBarcodeConfig specialBarcode;

	protected SpecialBarcodeForm frSpecialBarcode;

	@FXML
	protected Label lbError;

	@Autowired
	protected Session session;

	@Autowired
	protected ItemSpecialBarcodeServiceFacade itemSpecialBarcodeService;

	protected String documentType;
	protected String itemType;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.trace("initialize()- ");

		frSpecialBarcode = SpringContext.getBean(SpecialBarcodeForm.class);

		frSpecialBarcode.setFormField("description", tfDescription);
		frSpecialBarcode.setFormField("prefix", tfPrefix);
		frSpecialBarcode.setFormField("codeType", cbCodeType);
		frSpecialBarcode.setFormField("contentPosition", tfContentPosition);
		frSpecialBarcode.setFormField("contentLength", tfContentLength);
		frSpecialBarcode.setFormField("pricePosition", tfPricePosition);
		frSpecialBarcode.setFormField("priceWholeNumber", tfPriceWholeNumber);
		frSpecialBarcode.setFormField("priceFractionPart", tfPriceFractionPart);
		frSpecialBarcode.setFormField("quantityPosition", tfQuantityPosition);
		frSpecialBarcode.setFormField("quantityWholeNumber", tfQuantityWholeNumber);
		frSpecialBarcode.setFormField("quantityFractionPart", tfQuantityFractionPart);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		log.trace("initializeComponents()- ");
		
		ObservableList<String> codeTypes = FXCollections.observableArrayList();
		documentType = I18N.getText("Ticket");
		codeTypes.add(documentType);
		itemType = I18N.getText("Artículo");
		codeTypes.add(itemType);
		codeTypes.add("");

		cbCodeType.setItems(codeTypes);
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		log.trace("onSceneOpen()- ");

		frSpecialBarcode.clearForm();
		frSpecialBarcode.clearErrorStyle();
		lbError.setText("");

		if (sceneData.containsKey(PARAM_BARCODE)) {
			specialBarcode = (ItemSpecialBarcodeConfig) sceneData.remove(PARAM_BARCODE);
		}
		else {
			specialBarcode = null;
		}

		refreshWindowData();
	}

	@Override
	public void initializeFocus() {
		tfDescription.requestFocus();
	}

	public void refreshWindowData() {
		log.trace("refreshWindowData()- ");

		if (specialBarcode != null) {
			tfDescription.setText(specialBarcode.getDescription());
			cbLoyalty.setSelected(specialBarcode.getLoyalty());
			tfPrefix.setText(specialBarcode.getPrefix());

			String[] itemFields = specialBarcode.getItemCode().split("\\|");
			String[] documentFields = specialBarcode.getDocumentCode().split("\\|");

			if (itemFields.length == 0 || itemFields[0].isEmpty()) {
				tfContentPosition.setText(documentFields[0]);
				tfContentLength.setText(documentFields[1]);
				cbCodeType.getSelectionModel().select(documentType);
			}
			else {
				tfContentPosition.setText(itemFields[0]);
				tfContentLength.setText(itemFields[1]);
				cbCodeType.getSelectionModel().select(itemType);
			}

			String[] priceFields = specialBarcode.getPrice().split("\\|");
			tfPricePosition.setText(priceFields[0]);
			tfPriceWholeNumber.setText(priceFields[1]);
			tfPriceFractionPart.setText(priceFields[2]);

			String[] quantityFields = specialBarcode.getQuantity().split("\\|");
			tfQuantityPosition.setText(quantityFields[0]);
			tfQuantityWholeNumber.setText(quantityFields[1]);
			tfQuantityFractionPart.setText(quantityFields[2]);
		}
		else {
			tfPrefix.setText("");
			tfDescription.setText("");
			tfContentLength.setText("");
			tfQuantityFractionPart.setText("");
			tfPriceFractionPart.setText("");
			tfContentPosition.setText("");
			tfQuantityPosition.setText("");
			tfPricePosition.setText("");
			tfQuantityWholeNumber.setText("");
			tfPriceWholeNumber.setText("");
			cbLoyalty.setSelected(false);
			cbCodeType.getSelectionModel().select("");
		}
	}

	public void actionAccept() {
		log.trace("actionAccept()- ");

		if (validateBarcode()) {

			boolean newBarcode = false;
			try {
				if (specialBarcode == null) {
					specialBarcode = new ItemSpecialBarcodeConfig();
					newBarcode = true;
				}

				specialBarcode.setDescription(tfDescription.getText());
				specialBarcode.setQuantity(FormatUtils.getInstance().completeZerosLeft(tfQuantityPosition.getText(), 2) + "|"
				        + FormatUtils.getInstance().completeZerosLeft(tfQuantityWholeNumber.getText(), 2) + "|" + FormatUtils.getInstance().completeZerosLeft(tfQuantityFractionPart.getText(), 2));
				specialBarcode.setPrice(FormatUtils.getInstance().completeZerosLeft(tfPricePosition.getText(), 2) + "|"
				        + FormatUtils.getInstance().completeZerosLeft(tfPriceWholeNumber.getText(), 2) + "|" + FormatUtils.getInstance().completeZerosLeft(tfPriceFractionPart.getText(), 2));
				String codeType = cbCodeType.getSelectionModel().getSelectedItem();
				if (codeType.equals(itemType)) {
					specialBarcode.setItemCode(FormatUtils.getInstance().completeZerosLeft(tfContentPosition.getText(), 2) + "|"
					        + FormatUtils.getInstance().completeZerosLeft(tfContentLength.getText(), 2));
					specialBarcode.setDocumentCode("|");
				}
				else if (codeType.equals(documentType)) {
					specialBarcode.setDocumentCode(FormatUtils.getInstance().completeZerosLeft(tfContentPosition.getText(), 2) + "|"
					        + FormatUtils.getInstance().completeZerosLeft(tfContentLength.getText(), 2));
					specialBarcode.setItemCode("|");
				}
				specialBarcode.setPrefix(tfPrefix.getText());
				specialBarcode.setLoyalty(cbLoyalty.isSelected() ? true: false);

				if (newBarcode) {					
					itemSpecialBarcodeService.create(specialBarcode);
				}
				else {
					itemSpecialBarcodeService.update(specialBarcode);
				}

				closeSuccess();
			}
			catch (Exception e) {
				log.error("Error confirmando la operación.", e);
				DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Error. No se pudo completar la operación."), e);
			}
		}
	}

	public void actionCancel() {
		log.trace("actionCancel()- ");
		closeCancel();
	}

	protected boolean validateBarcode() {
		log.trace("validateBarcode()- ");

		frSpecialBarcode.clearErrorStyle();

		frSpecialBarcode.setQuantityFractionPart(tfQuantityFractionPart.getText());
		frSpecialBarcode.setPriceFractionPart(tfPriceFractionPart.getText());
		frSpecialBarcode.setDescription(tfDescription.getText());
		frSpecialBarcode.setQuantityWholeNumber(tfQuantityWholeNumber.getText());
		frSpecialBarcode.setQuantityPosition(tfQuantityPosition.getText());
		frSpecialBarcode.setContentPosition(tfContentPosition.getText());
		frSpecialBarcode.setContentLength(tfContentLength.getText());
		frSpecialBarcode.setCodeType(cbCodeType.getSelectionModel().getSelectedItem());
		frSpecialBarcode.setPriceWholeNumber(tfPriceWholeNumber.getText());
		frSpecialBarcode.setPricePosition(tfPricePosition.getText());
		frSpecialBarcode.setPrefix(tfPrefix.getText());

		// Validate form
		Set<ConstraintViolation<SpecialBarcodeForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frSpecialBarcode);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<SpecialBarcodeForm> next = constraintViolations.iterator().next();
			frSpecialBarcode.setErrorStyle(next.getPropertyPath(), true);
			frSpecialBarcode.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			return false;
		}
		else {
			lbError.setText("");
		}
		return true;
	}

}
