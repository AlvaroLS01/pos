package com.comerzzia.pos.gui.configuracion.mediospago;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

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

import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfigurationImpl;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class ConfiguracionMediosPagoController extends Controller {
	
	private Logger log = Logger.getLogger(ConfiguracionMediosPagoController.class);

	@FXML
	protected AnchorPane panelContenedorPropiedades;

	@FXML
	protected VBox panelPropiedades;

	@FXML
	protected ComboBox<MedioPagoBean> cbMediosPago;

	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;

	protected PaymentMethodConfiguration configuracionSeleccionada;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		Collection<MedioPagoBean> mediosPago = MediosPagosService.mediosPago.values();
		List<MedioPagoBean> listadoMediosPago = new ArrayList<MedioPagoBean>();
		listadoMediosPago.addAll(mediosPago);
		
		Collections.sort(listadoMediosPago, new Comparator<MedioPagoBean>(){
		    @Override
		    public int compare(MedioPagoBean o1, MedioPagoBean o2) {
		        return o1.getCodMedioPago().toString().compareToIgnoreCase(o2.getCodMedioPago().toString());
		    }
		});
		cbMediosPago.setItems(FXCollections.observableArrayList(listadoMediosPago));

		cbMediosPago.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MedioPagoBean>(){

			@Override
			public void changed(ObservableValue<? extends MedioPagoBean> ov, MedioPagoBean oldValue, MedioPagoBean newValue) {
				pintarPropiedadesMedioPago(newValue);
			}
		});
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
	}

	@Override
	public void initializeFocus() {
		cbMediosPago.requestFocus();
	}

	protected void pintarPropiedadesMedioPago(MedioPagoBean newValue) {
		for (PaymentMethodConfiguration paymentMethodConfiguration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			if (paymentMethodConfiguration.getPaymentCode().equals(newValue.getCodMedioPago())) {
				configuracionSeleccionada = paymentMethodConfiguration;
			}
		}

		if (configuracionSeleccionada != null) {
			panelPropiedades.getChildren().clear();
			
			double altura = 0.0;
			
			PaymentMethodManager manager = null;
			try {
				manager = configuracionSeleccionada.getManager();
			}
			catch(Exception e) {
				if(e instanceof NoSuchBeanDefinitionException) {
					log.error("pintarPropiedadesMedioPago() - No existe el manejador del medio de pago.");
				}
				else {
					log.error("pintarPropiedadesMedioPago() - No se ha podido inicializar el controlador del medio de pago: " + e.getMessage(), e);
				}
			}
			
			if(!tieneConfiguracion(manager)) {
				pintarSinPropiedades();
			}
			else {
				double alturaPropiedadesPasarela = pintarPropiedadesPasarela();
				double alturaPropiedadesLocales = pintarPropiedadesLocales(manager);
				
				altura = alturaPropiedadesPasarela + alturaPropiedadesLocales + 100;
			}
			
			panelContenedorPropiedades.setMinHeight(altura);
			panelContenedorPropiedades.setPrefHeight(altura);
			panelPropiedades.setMinHeight(altura);
			panelPropiedades.setPrefHeight(altura);
		}
	}

	protected boolean tieneConfiguracion(PaymentMethodManager manager) {
		return (!configuracionSeleccionada.getConfigurationProperties().isEmpty()) || (manager != null && !manager.getConfigurationProperties().isEmpty());
	}

	protected double pintarPropiedadesPasarela() {
		double altura = 0;
		
		if (!configuracionSeleccionada.getGatewayConfigurationProperties().isEmpty()) {
			String gatewayDescription = configuracionSeleccionada.getGatewayConfigurationProperties().get(PaymentsMethodsConfigurationImpl.PARAM_DESCRIPCION_GATEWAY);
			
			Label labelTitulo = new Label(I18N.getTexto("Propiedades de la pasarela: ") + gatewayDescription);
			labelTitulo.getStyleClass().add("label-total");
			labelTitulo.setPrefHeight(50);
			labelTitulo.setPadding(new Insets(0.0, 0.0, 0.0, 10.0));
			labelTitulo.setAlignment(Pos.CENTER);
			panelPropiedades.getChildren().add(labelTitulo);

			for (String key : configuracionSeleccionada.getGatewayConfigurationProperties().keySet()) {
				if(key.equals(PaymentsMethodsConfigurationImpl.PARAM_DESCRIPCION_GATEWAY)) {
					continue;
				}
				
				String valor = configuracionSeleccionada.getGatewayConfigurationProperty(key);
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
				Label label = new Label(I18N.getTexto(labelString));
				label.setPrefWidth(200);
				label.setAlignment(Pos.CENTER_RIGHT);
				hbox.getChildren().add(label);

				TextField textField = new TextField(valor);
				textField.setPrefHeight(40);
				textField.setPrefWidth(500);
				textField.setEditable(false);
				textField.getStyleClass().add("solo-lectura");
				hbox.getChildren().add(textField);

				panelPropiedades.getChildren().add(hbox);
			}
		}
		
		return altura;
	}

	protected double pintarPropiedadesLocales(PaymentMethodManager manager) {
		double altura = 0;
		
		if (manager != null && !manager.getConfigurationProperties().isEmpty()) {
			altura = pintarPropiedadesConfiguracion(altura, manager);
		}
		
		return altura;
	}

	protected double pintarSinPropiedades() {
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.setPrefHeight(300);
		hbox.setAlignment(Pos.CENTER);

		Label label = new Label(I18N.getTexto("Este medio de pago no tiene configuración asociada."));
		label.setPrefWidth(1000);
		label.setAlignment(Pos.CENTER);
		hbox.getChildren().add(label);
		
		panelPropiedades.getChildren().add(hbox);
		
		return 300;
	}

	protected double pintarPropiedadesConfiguracion(double altura, PaymentMethodManager manager) {
		Label labelTitulo = new Label(I18N.getTexto("Propiedades de la tienda"));
		labelTitulo.getStyleClass().add("label-total");
		labelTitulo.setPrefHeight(50);
		labelTitulo.setPadding(new Insets(0.0, 0.0, 0.0, 10.0));
		labelTitulo.setAlignment(Pos.CENTER);
		panelPropiedades.getChildren().add(labelTitulo);

		for (ConfigurationPropertyDto configurationProperty : manager.getConfigurationProperties()) {
			String key = configurationProperty.getKey();
			
			String valor = configuracionSeleccionada.getStoreConfigurationProperty(key);
			if (valor == null) {
				valor = "";
			}

			HBox hbox = new HBox();
			hbox.setSpacing(10);
			hbox.setPrefHeight(50);
			hbox.setAlignment(Pos.CENTER_LEFT);
			altura = altura + 50;

			Label label = new Label(configurationProperty.getDescription());
			label.setPrefWidth(200);
			label.setAlignment(Pos.CENTER_RIGHT);
			hbox.getChildren().add(label);

			crearCampoPropiedad(configurationProperty, key, valor, hbox);

			panelPropiedades.getChildren().add(hbox);
		}
		return altura;
	}

	protected void crearCampoPropiedad(ConfigurationPropertyDto configurationProperty, String key, String valor, HBox hbox) {
		if(configurationProperty.getType().equals(ConfigurationPropertyDto.TYPE_TEXT)) {
			TextField textField = new TextField(valor);
			textField.setId(key);
			textField.setPrefHeight(40);
			textField.setPrefWidth(400);
			hbox.getChildren().add(textField);
		}
		else if(configurationProperty.getType().equals(ConfigurationPropertyDto.TYPE_AMOUNT)) {
			TextFieldImporte textField = new TextFieldImporte();
			textField.setText(valor);
			textField.setId(key);
			textField.setPrefHeight(40);
			textField.setPrefWidth(400);
			hbox.getChildren().add(textField);
		}
		else if(configurationProperty.getType().equals(ConfigurationPropertyDto.TYPE_DATE)) {
			DatePicker datePicker = new DatePicker();
			DateFormat dateFormat = datePicker.getDateFormat();
			try {
				Date date = dateFormat.parse(valor);
				datePicker.setSelectedDate(date);
			}
			catch(ParseException e) {
				log.error("crearCampoPropiedad() - Ha habido un error al formatear la fecha solicitad (" + valor + "): " + e.getMessage());
				datePicker.setSelectedDate(new Date());
			}
			datePicker.setId(key);
			datePicker.setPrefHeight(40);
			datePicker.setPrefWidth(400);
			hbox.getChildren().add(datePicker);
		}
	}
	
	public void guardarCambios() {
		for(Node node : panelPropiedades.getChildren()) {
			if(node instanceof HBox) {
				for(Node nodeHbox : ((HBox) node).getChildren()) {
					if(nodeHbox instanceof TextField) {
						String key = nodeHbox.getId();
						String value = ((TextField) nodeHbox).getText();
						
						if(key != null) {
							configuracionSeleccionada.putStoreConfigurationProperty(key, value);
						}
					}
				}
			}
		}
		
		try {
			paymentsMethodsConfiguration.saveConfiguration();
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Configuración guardada correctamente."), getStage());
		}
		catch (Exception e) {
			log.error("guardarCambios() - Ha habido un error al guardar la configuración: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al guardar la configuración. Contacte con el administrador."), e);
		}
		
		paymentsMethodsConfiguration.loadConfiguration();
	}

}
