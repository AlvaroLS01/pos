package com.comerzzia.iskaypet.pos.gui.configuracion.mediospago;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.librerias.sipay.client.constants.SipayConstants;
import com.comerzzia.iskaypet.pos.gui.configuracion.mediospago.sipay.BusquedaFicherosSipayController;
import com.comerzzia.iskaypet.pos.gui.configuracion.mediospago.sipay.BusquedaFicherosSipayView;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

@Component
@Primary
public class IskaypetConfiguracionMediosPagoController extends com.comerzzia.pos.gui.configuracion.mediospago.ConfiguracionMediosPagoController {
	
	private Logger log = Logger.getLogger(IskaypetConfiguracionMediosPagoController.class);
	
	@FXML
	protected Button btCargaFicheroTresMil, btTelecargaVerifone;
	
	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	
	@Override
	public void initializeComponents() throws InitializeGuiException {
		super.initializeComponents();
		ocultarBotonesConfSipay();
	}
	
	@FXML
	public void accionCargaFicheroTresMil() {
		log.info("accionCargaFicheroTresMil() - ...");
		abrirPantallaCargaFichero(BusquedaFicherosSipayController.OPERACION_FICHERO_TRESMIL);
	}
	
	@FXML
	public void accionTelecargaVerifone() {
		log.info("accionTelecargaVerifone() - ...");
		abrirPantallaCargaFichero(BusquedaFicherosSipayController.OPERACION_TELECARGA_VERIFONE);
	}
	
	public void abrirPantallaCargaFichero(String parametroOperacion) {
		
		if (cbMediosPago.getSelectionModel().getSelectedItem() != null) {			
			HashMap<String, Object> datos = new HashMap<String, Object>();
			datos.put(BusquedaFicherosSipayController.PARAMETRO_OPERACION, parametroOperacion);
			getApplication().getMainView().showModalCentered(BusquedaFicherosSipayView.class, datos, getStage());
		}
	}
	
	@Override
	protected void pintarPropiedadesMedioPago(MedioPagoBean newValue) {
		super.pintarPropiedadesMedioPago(newValue);
		
		// Sólo mostramos los botones de configuración de Sipay si la clase que controla ese medio de pago es la de tefSipayManager
		boolean mostrarBotonesSipay = false;
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			if (configuration.getPaymentCode().equals(newValue.getCodMedioPago()) && SipayConstants.MANAGER_CONTROL_CLASS.equals(configuration.getControlClass())) {
				mostrarBotonesSipay = true;
			}
		}
		
		if (mostrarBotonesSipay) {
			mostrarBotonesConfSipay();
		} else {
			ocultarBotonesConfSipay();
		}
	}
	
	private void mostrarBotonesConfSipay() {
		btCargaFicheroTresMil.setVisible(true);
		btCargaFicheroTresMil.setDisable(false);
		btTelecargaVerifone.setVisible(true);
		btTelecargaVerifone.setDisable(false);
	}
	
	private void ocultarBotonesConfSipay() {
		btCargaFicheroTresMil.setVisible(false);
		btCargaFicheroTresMil.setDisable(true);
		btTelecargaVerifone.setVisible(false);
		btTelecargaVerifone.setDisable(true);
	}

}
