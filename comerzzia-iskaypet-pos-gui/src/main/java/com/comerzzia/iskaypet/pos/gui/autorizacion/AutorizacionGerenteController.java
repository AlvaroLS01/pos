package com.comerzzia.iskaypet.pos.gui.autorizacion;

import java.net.URL;
import java.util.ResourceBundle;

import com.comerzzia.core.util.criptografia.CriptoException;
import com.comerzzia.core.util.criptografia.CriptoUtil;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.usuarios.UsuarioNotFoundException;
import com.comerzzia.pos.services.core.usuarios.UsuariosService;
import com.comerzzia.pos.services.core.usuarios.UsuariosServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.apuntes.IskaypetInsertarApunteController.AVISO_RETIRADA;

@Component
public class AutorizacionGerenteController extends Controller {

	protected Logger log = Logger.getLogger(getClass());

	public static final String X_POS_CLAVE_AUTORIZACION = "X_POS.CLAVE_AUTORIZACION";
	public static final String ACCION_CANCELAR = "ACCION_CANCELAR";
	public static final String PARAM_SALIDA_CLAVE_CORRECTA = "PARAM_SALIDA_CLAVE_CORRECTA";
	protected static final  String PARAMETRO_REQUIERE_GERENTE = "requiereGerente";
	private boolean requiereGerente = false;
	private Boolean avisoRetirada;

	@Autowired
	private VariablesServices variablesServices;

	@Autowired
	private UsuariosService usuariosService;

	@Autowired
	private Sesion sesion;

	@FXML
    private PasswordField pfNumeroTarjeta;
    @FXML
    private Button btAceptar;
    @FXML
    private Button btCancelar;
    @FXML
    private Label lbTitulo;
    @FXML
    private Label lbError;
	@FXML
	private Label lbNumeroTarjeta;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registraEventoTeclado(new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					chooseAceptar();
				}
				else if (event.getCode().equals(KeyCode.ESCAPE)) {
					accionCancelar();
				}
			}

		}, KeyEvent.KEY_RELEASED);
	}

	@Override
	public void initializeFocus() {
		pfNumeroTarjeta.requestFocus();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		if (getDatos().containsKey(AVISO_RETIRADA)) {
			avisoRetirada = (Boolean) getDatos().get(AVISO_RETIRADA);
			btCancelar.setVisible(avisoRetirada);
		} else{
			btCancelar.setVisible(true);
			avisoRetirada =  null;
		}
		clearData();
	}

	public boolean isRequiereGerente() {
		return requiereGerente;
	}

	public void setRequiereGerente(boolean requiereGerente) {
		this.requiereGerente = requiereGerente;
	}

	private void clearData() {
		lbError.setText("");
		pfNumeroTarjeta.setText("");
		if(getDatos().get(PARAMETRO_REQUIERE_GERENTE) != null && (boolean) getDatos().get(PARAMETRO_REQUIERE_GERENTE)) {
			lbTitulo.setText(I18N.getTexto("Autorización Gerente"));
			lbNumeroTarjeta.setText(I18N.getTexto("Número de tarjeta"));
			setRequiereGerente(true);
		}
		else {
			lbTitulo.setText(I18N.getTexto("Contraseña del colaborador"));
			lbNumeroTarjeta.setText(I18N.getTexto("Contraseña"));
			setRequiereGerente(false);
		}
	}

	@FXML
	void accionAceptar(ActionEvent event) {
		chooseAceptar();
	}

	@FXML
	void accionCancelar(ActionEvent event) {
		accionCancelar();
	}

	private void accionCancelar() {
		getDatos().put(ACCION_CANCELAR, true);
		getStage().close();
	}

	private void chooseAceptar(){
		if(isRequiereGerente()) {
			accionAceptar();
		}
		else {
			try {
				accionAceptarVendedor();
			} catch (CriptoException | UsuariosServiceException | UsuarioNotFoundException e) {
				log.error("accionAceptar() - Error al comprobar la clave de autorización", e);
			}
		}
	}

	private void accionAceptar() {
		log.debug("accionAceptar() - Comprobando que el número de tarjeta sea correcto");

		lbError.setText("");

		String inputPassword = pfNumeroTarjeta.getText().trim();
		String correctPassword = variablesServices.getVariableAsString(X_POS_CLAVE_AUTORIZACION);

		log.debug("accionAceptar() - Clave de autorización: " + correctPassword);
		
		if(StringUtils.isBlank(correctPassword)) {
			lbError.setText(I18N.getTexto("Número de tarjeta de acceso no configurado. Consulte a su administrador."));
			return;
		}

		if (StringUtils.isBlank(inputPassword)) {
			lbError.setText(I18N.getTexto("Debe introducir un número de tarjeta"));
			return;
		}

		if (!inputPassword.equals(correctPassword)) {
			pfNumeroTarjeta.selectAll();
			String mensajeError = I18N.getTexto("Número de tarjeta incorrecto");
			lbError.setText(mensajeError);
			log.debug("accionAceptar() - " + mensajeError);
		}
		else {
			getDatos().put(PARAM_SALIDA_CLAVE_CORRECTA, true);
			getStage().close();
		}
	}

	private void accionAceptarVendedor() throws CriptoException, UsuariosServiceException, UsuarioNotFoundException {
		UsuarioBean usuarioBean = usuariosService.consultarUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
		String inputPassword = pfNumeroTarjeta.getText().trim();
		String hashPassword = CriptoUtil.cifrar("MD5", inputPassword.getBytes());

		log.debug("accionAceptar() - Clave de autorización: " + inputPassword);

		if(StringUtils.isBlank(hashPassword)) {
			lbError.setText(I18N.getTexto("Contraseña no configurada. Consulte a su administrador."));
			return;
		}

		if (StringUtils.isBlank(inputPassword)) {
			lbError.setText(I18N.getTexto("Debe introducir una contraseña"));
			return;
		}

		if (!hashPassword.equals(usuarioBean.getClave())) {
			pfNumeroTarjeta.selectAll();
			String mensajeError = I18N.getTexto("Contraseña incorrecta");
			lbError.setText(mensajeError);
			log.debug("accionAceptarVendedor() - " + mensajeError);
		}
		else {
			getDatos().put(PARAM_SALIDA_CLAVE_CORRECTA, true);
			getStage().close();
		}
	}

}
