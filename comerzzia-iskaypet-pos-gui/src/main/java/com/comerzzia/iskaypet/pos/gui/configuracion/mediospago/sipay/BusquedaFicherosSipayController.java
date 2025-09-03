package com.comerzzia.iskaypet.pos.gui.configuracion.mediospago.sipay;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.services.payments.methods.types.sipay.SipayService;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

@Component
public class BusquedaFicherosSipayController extends Controller {
	
	private Logger log = Logger.getLogger(BusquedaFicherosSipayController.class);
	
	public static final String PARAMETRO_OPERACION = "PARAMETRO_OPERACION";
	public static final String PARAMETRO_HEADER = "PARAM_HEADER";
	public static final String PARAMETRO_URL = "PARAM_URL";
	public static final String PARAMETRO_TIMEOUT = "PARAM_TIMEOUT";
	public static final String OPERACION_FICHERO_TRESMIL = "OPERACION_FICHERO_TRESMIL";
	public static final String OPERACION_TELECARGA_VERIFONE = "OPERACION_TELECARGA_VERIFONE";

	@FXML
	protected Button btBuscarFichero, btAceptar, btCancelar;
	@FXML
	protected TextField tfRutaFichero;
	@FXML
	protected Label lbTitulo;
	
	@Autowired
	protected SipayService sipayService;
	
	private String operacion;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();
	}

	@Override
	public void initializeFocus() {
		tfRutaFichero.requestFocus();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		lbTitulo.setText("");
		tfRutaFichero.setText("");
		
		operacion = (String) getDatos().get(PARAMETRO_OPERACION);
		if (OPERACION_FICHERO_TRESMIL.equals(operacion)) {
			lbTitulo.setText("Carga de fichero 3000");
		} else if (OPERACION_TELECARGA_VERIFONE.equals(operacion)) {
			lbTitulo.setText("Telecarga Verifone");
		}
	}
	
	 @FXML
	 public void accionAceptarIntro(KeyEvent e){
	    if(e.getCode()==KeyCode.ENTER){
	          accionAceptar();
	    }
	 }
	
	@FXML
	public void accionAceptar() {
			if (StringUtils.isNotBlank(tfRutaFichero.getText())) {
				new CargarFicheroConfiguracion().start();
			} else {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Primero debe seleccionar un fichero"), getStage());
			}
	}
	
	public class CargarFicheroConfiguracion extends BackgroundTask<Void> {
		@Override
		protected Void call() throws Exception {
			if (OPERACION_FICHERO_TRESMIL.equals(operacion)) {
				sipayService.cargarFicheroTresMil(tfRutaFichero.getText());
			} else if (OPERACION_TELECARGA_VERIFONE.equals(operacion)) {
				sipayService.realizarTelecargaVerifone(tfRutaFichero.getText());
			}
			return null;
		}
		
		@Override
		protected void succeeded() {
			super.succeeded();
			VentanaDialogoComponent.crearVentanaInfo("PINPAD RECONFIGURADO CORRECTAMENTE", getStage());
			getStage().close();
		}
		
		@Override
		protected void failed() {
			super.failed();
			Throwable ex = getException();
            //callback.onFailure((Exception) ex);
            VentanaDialogoComponent.crearVentanaError(ex.getMessage(), getStage());
            getStage().close();
		}
	}
	
	@FXML
	public void accionCancelar() {
		getStage().close();
	}
	
	@FXML
	public void accionBuscarFichero() {
		log.info("accionBuscarFichero() - Seleccionando fichero...");
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files (*.*)", "*.*");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = null;
		file = fileChooser.showOpenDialog(getStage());
		if (file != null) {
			byte[] arrayBytes = getFichero(file);
			if (arrayBytes != null) {
				tfRutaFichero.setText(file.getAbsolutePath());
			}
		}
	}
	
	public byte[] getFichero(File file) {
		byte[] arrayBytes = null;
		try {
			arrayBytes = FileUtils.readFileToByteArray(file);
		}
		catch (FileNotFoundException ex) {
			String msgError = "El fichero seleccionado no es válido";
			log.error("getFichero() - " + msgError);
		}
		catch (Exception e) {
			log.error("getFichero() - Error al obtener el fichero seleccionado - " + e.getCause(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e);
		}
		return arrayBytes;
	}
	
	public void registrarAccionCerrarVentanaEscape() {
        registraEventoTeclado(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ESCAPE) {
                    accionCancelar();
                    t.consume();
                }
            }
        }, KeyEvent.KEY_RELEASED);
    }

}
