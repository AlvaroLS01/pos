package com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.rascas;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.rascas.RascasService;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class IntroduccionRascasController extends WindowController {
	
	private Logger log = Logger.getLogger(IntroduccionRascasController.class);
	
	public static String PARAM_TICKET = "IntroduccionRascasController.Ticket";
	public static String PARAM_RASCAS = "IntroduccionRascasController.Rascas";
	public static String PARAM_RASCAS_ENTREGADOS = "IntroduccionRascasController.RascasEntregados";
	public static String PARAM_ULTIMA_LINEA = "IntroduccionRascasController.UltimaLinea";
	public static String PARAM_RASCAS_ENTREGADOS_CAJERO = "IntroduccionRascasController.RascasEntregadosCajero";
	
	@FXML
	private TextField tfTotal, tfRestantes, tfCodigoRasca;
	
	@FXML
	private Label lbCodTicket;
	
	@Autowired
	private RascasService rascasService;
	
	private TicketVentaAbono ticket;
	private int numeroRascasConcedidos;
	private List<String> rascasIntroducidos;
	
	private int rascasEntregados;
	private int ultimaLinea;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setMostrar(false);
		tfCodigoRasca.setUserData(keyboardDataDto);
		
		darFocoSiempreACampoIntroduccion();
	}

	private void darFocoSiempreACampoIntroduccion() {
		tfCodigoRasca.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if(!newPropertyValue) {
					tfCodigoRasca.requestFocus();
				}
			}
		});
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		ticket = (TicketVentaAbono) getDatos().get(PARAM_TICKET);
		numeroRascasConcedidos = (int) getDatos().get(PARAM_RASCAS);
		rascasIntroducidos = new ArrayList<String>();
		
		tfCodigoRasca.clear();
		
		if(getDatos().containsKey(PARAM_ULTIMA_LINEA)) {
			ultimaLinea = (int) getDatos().get(PARAM_ULTIMA_LINEA);
		}
		else {
			ultimaLinea = -1;
		}
		
		if(getDatos().containsKey(PARAM_RASCAS_ENTREGADOS)) {
			rascasEntregados = (int) getDatos().get(PARAM_RASCAS_ENTREGADOS);
		}
		else {
			rascasEntregados = 0;
		}
		
		tfTotal.setText(numeroRascasConcedidos + "");
		tfRestantes.setText(String.valueOf(numeroRascasConcedidos - rascasEntregados));
		
		lbCodTicket.setText(ticket.getCabecera().getCodTicket());
	}

	@Override
	public void initializeFocus() {
		tfCodigoRasca.requestFocus();
	}
	
	public void introducirRasca() {
		try {
			String codigo = tfCodigoRasca.getText();
			
			log.debug("introducirRasca() - Introducido código: " + codigo);
			
			if(StringUtils.isBlank(codigo)) {
				return;
			}
			
			if(!rascasService.isRascaValido(codigo)) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El código introducido no es válido. Contacte con un administrador"), getStage());
				return;
			}
			
			rascasIntroducidos.add(codigo);
			
			int rascasRestantes = numeroRascasConcedidos - rascasEntregados - rascasIntroducidos.size();
			tfRestantes.setText(String.valueOf(rascasRestantes));
			
			if(rascasRestantes == 0) {
				cerrarVentana();
			}
		}
		catch (Exception e) {
			log.error("introducirRasca() - Ha habido un error a leer el código: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error a leer el código: ") + e.getMessage(), e);
		}
		finally {
			tfCodigoRasca.clear();
		}
	}
	
	public void introducirRascaIntro(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			introducirRasca();
        }
	}
	
	@Override
	public void accionCancelar() {
		cerrarVentana();
	}

	protected void cerrarVentana() {
		rascasService.guardarDocumentoEntregaRascas(ticket, rascasIntroducidos, numeroRascasConcedidos, ultimaLinea);
		getDatos().put(PARAM_RASCAS_ENTREGADOS_CAJERO, rascasIntroducidos.size());
		super.accionCancelar();
	}

}
