package com.comerzzia.pos.core.gui.configuration.paymentsmethod;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.i18n.I18N;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodConfiguration;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodConfigurationPropertyDetail;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodDetail;
import com.comerzzia.omnichannel.facade.model.payments.StorePosPaymentMethods;
import com.comerzzia.omnichannel.facade.service.payments.PaymentsManagerServiceFacade;
import com.comerzzia.omnichannel.facade.service.store.StorePosPaymentMethodServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.services.session.Session;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@Component
@CzzActionScene
public class PaymentsMethodConfigurationController extends ActionSceneController {
	
	private Logger log = Logger.getLogger(PaymentsMethodConfigurationController.class);

	@FXML
	protected AnchorPane panePropertiesContainer;

	@FXML
	protected VBox vbProperties;

	@FXML
	protected ComboBox<PaymentMethodDetail> cbPaymentMethods;
	
	@Autowired
	protected PaymentsManagerServiceFacade paymentsManager;
	
	@Autowired
	protected Session sesion;

	protected PaymentMethodConfiguration selectedConfiguration;
	
	protected StorePosPaymentMethods paymentMethods;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		paymentMethods = sesion.getApplicationSession().getPaymentMethods();
		paymentsManager.loadPaymentMethodManagers(new ArrayList<>(paymentMethods.getPaymentMethodConfigurations().values()));

		List<PaymentMethodDetail> listadoMediosPago = new ArrayList<>(paymentMethods.getPaymentsMethods().values());
				
		Collections.sort(listadoMediosPago, new Comparator<PaymentMethodDetail>(){
		    @Override
		    public int compare(PaymentMethodDetail o1, PaymentMethodDetail o2) {
		        return o1.getPaymentMethodCode().compareToIgnoreCase(o2.getPaymentMethodCode());
		    }
		});
		cbPaymentMethods.setItems(FXCollections.observableArrayList(listadoMediosPago));

		cbPaymentMethods.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PaymentMethodDetail>(){

			@Override
			public void changed(ObservableValue<? extends PaymentMethodDetail> ov, PaymentMethodDetail oldValue, PaymentMethodDetail newValue) {
				paintPaymentMethodProperties(newValue);
			}
		});
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
	}

	@Override
	public void initializeFocus() {
		cbPaymentMethods.requestFocus();
	}

	protected void paintPaymentMethodProperties(PaymentMethodDetail newValue) {
		
		for (PaymentMethodConfiguration paymentMethodConfiguration : paymentMethods.getPaymentMethodConfigurations().values()) {
			if (paymentMethodConfiguration.getPaymentMethodCode().equals(newValue.getPaymentMethodCode())) {
				selectedConfiguration = paymentMethodConfiguration;
				break;
			}
		}

		if (selectedConfiguration != null) {
			vbProperties.getChildren().clear();
			
			double altura = 0.0;
			
			if(!hasConfiguration()) {
				paintWithoutProperties();
			}
			else {
				double alturaPropiedadesPasarela = paintPaymentGatewayProperties();
				double alturaPropiedadesLocales = paintLocalProperties();
				
				altura = alturaPropiedadesPasarela + alturaPropiedadesLocales + 100;
			}
			
			panePropertiesContainer.setMinHeight(altura);
			panePropertiesContainer.setPrefHeight(altura);
			vbProperties.setMinHeight(altura);
			vbProperties.setPrefHeight(altura);
		}
	}

	protected boolean hasConfiguration() {
		return (!selectedConfiguration.getStoreConfigurationProperties().isEmpty() 
				|| !selectedConfiguration.getGatewayConfigurationProperties().isEmpty()
				|| !selectedConfiguration.getFieldConfigurationProperties().isEmpty());
	}

	protected double paintPaymentGatewayProperties() {
		double altura = 0;
		
		if (!selectedConfiguration.getGatewayConfigurationProperties().isEmpty()) {
			String gatewayDescription = selectedConfiguration.getGatewayConfigurationProperties().get(StorePosPaymentMethodServiceFacade.PARAM_DESCRIPCION_GATEWAY);
			
			Label labelTitulo = new Label(I18N.getText("Propiedades de la pasarela: ") + gatewayDescription);
			labelTitulo.getStyleClass().add("label-total");
			labelTitulo.setPrefHeight(50);
			labelTitulo.setPadding(new Insets(0.0, 0.0, 0.0, 10.0));
			labelTitulo.setAlignment(Pos.CENTER);
			vbProperties.getChildren().add(labelTitulo);

			for (String key : selectedConfiguration.getGatewayConfigurationProperties().keySet()) {
				if(key.equals(StorePosPaymentMethodServiceFacade.PARAM_DESCRIPCION_GATEWAY)) {
					continue;
				}
				
				String valor = selectedConfiguration.getGatewayConfigurationProperties().get(key);
				if (valor == null) {
					valor = "";
				}

				HBox hbox = new HBox();
				hbox.setSpacing(10);
				hbox.setPrefHeight(50);
				altura = altura + 50;
				hbox.setAlignment(Pos.CENTER_LEFT);

				String labelString = key.replace("_", " ");
				labelString = WordUtils.capitalize(labelString);
				Label label = new Label(I18N.getText(labelString));
				label.setPrefWidth(200);
				label.setAlignment(Pos.CENTER_RIGHT);
				hbox.getChildren().add(label);

				TextField textField = new TextField(valor);
				textField.setPrefHeight(40);
				textField.setPrefWidth(500);
				textField.setEditable(false);
				hbox.getChildren().add(textField);

				vbProperties.getChildren().add(hbox);
			}
		}
		
		return altura;
	}

	protected double paintLocalProperties() {
		double altura = 0;
		
		if (selectedConfiguration.getFieldConfigurationProperties() != null && !selectedConfiguration.getFieldConfigurationProperties().isEmpty()) {
			altura = paintConfigurationProperties(altura);
		}
		
		return altura;
	}

	protected double paintWithoutProperties() {
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.setPrefHeight(300);
		hbox.setAlignment(Pos.CENTER);

		Label label = new Label(I18N.getText("Este medio de pago no tiene configuración asociada."));
		label.setPrefWidth(1000);
		label.setAlignment(Pos.CENTER);
		hbox.getChildren().add(label);
		
		vbProperties.getChildren().add(hbox);
		
		return 300;
	}

	protected double paintConfigurationProperties(double altura) {
		Label labelTitulo = new Label(I18N.getText("Propiedades de la tienda"));
		labelTitulo.getStyleClass().add("label-total");
		labelTitulo.setPrefHeight(50);
		labelTitulo.setPadding(new Insets(0.0, 0.0, 0.0, 10.0));
		labelTitulo.setAlignment(Pos.CENTER);
		vbProperties.getChildren().add(labelTitulo);

		for (Entry<String, PaymentMethodConfigurationPropertyDetail> configurationPropertyEntry : selectedConfiguration.getFieldConfigurationProperties().entrySet()) {
			String key = configurationPropertyEntry.getKey();
			
			PaymentMethodConfigurationPropertyDetail property = configurationPropertyEntry.getValue();
			String valor = property.getDescription();
			if (valor == null) {
				valor = "";
			}

			HBox hbox = new HBox();
			hbox.setSpacing(10);
			hbox.setPrefHeight(50);
			hbox.setAlignment(Pos.CENTER_LEFT);
			altura = altura + 50;

			Label label = new Label(valor);
			label.setPrefWidth(200);
			label.setAlignment(Pos.CENTER_RIGHT);
			hbox.getChildren().add(label);

			createPropertyField(property, key, hbox);

			vbProperties.getChildren().add(hbox);
		}
		return altura;
	}

	protected void createPropertyField(PaymentMethodConfigurationPropertyDetail configurationProperty, String key, HBox hbox) {
		if(configurationProperty.getType().equals(PaymentMethodConfigurationPropertyDetail.TYPE_TEXT)) {
			TextField textField = new TextField(selectedConfiguration.getStoreConfigurationProperties().get(key));
			textField.setId(key);
			textField.setPrefHeight(40);
			textField.setPrefWidth(400);
			hbox.getChildren().add(textField);
		}
		else if(configurationProperty.getType().equals(PaymentMethodConfigurationPropertyDetail.TYPE_AMOUNT)) {
			NumericTextField textField = new NumericTextField();
			textField.setText(selectedConfiguration.getStoreConfigurationProperties().get(key));
			textField.setId(key);
			textField.setPrefHeight(40);
			textField.setPrefWidth(400);
			hbox.getChildren().add(textField);
		}
		else if(configurationProperty.getType().equals(PaymentMethodConfigurationPropertyDetail.TYPE_DATE)) {
			DatePicker datePicker = new DatePicker();
			DateFormat dateFormat = datePicker.getDateFormat();
			try {
				Date date = dateFormat.parse(selectedConfiguration.getStoreConfigurationProperties().get(key));
				datePicker.setSelectedDate(date);
			}
			catch(ParseException e) {
				log.error("createPropertyField() - Ha habido un error al formatear la fecha solicitad (" + selectedConfiguration.getStoreConfigurationProperties().get(key) + "): " + e.getMessage());
				datePicker.setSelectedDate(new Date());
			}
			datePicker.setId(key);
			datePicker.setPrefHeight(40);
			datePicker.setPrefWidth(400);
			hbox.getChildren().add(datePicker);
		}
	}
	
	public void saveChanges() {
		for(Node node : vbProperties.getChildren()) {
			if(node instanceof HBox) {
				for(Node nodeHbox : ((HBox) node).getChildren()) {
					if(nodeHbox instanceof TextField) {
						String key = nodeHbox.getId();
						String value = ((TextField) nodeHbox).getText();
						
						if(key != null) {
							selectedConfiguration.getStoreConfigurationProperties().put(key, value);
						}
					}
				}
			}
		}
		
		try {
			paymentsManager.savePaymentMethodConfiguration(sesion.getApplicationSession().getPosUid(), new ArrayList<>(paymentMethods.getPaymentMethodConfigurations().values()));
			DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(I18N.getText("Configuración guardada correctamente."));
		}
		catch (Exception e) {
			log.error("saveChanges() - Ha habido un error al guardar la configuración: " + e.getMessage(), e);
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Ha habido un error al guardar la configuración. Contacte con el administrador"), e);
		}
		
	}
	
}
