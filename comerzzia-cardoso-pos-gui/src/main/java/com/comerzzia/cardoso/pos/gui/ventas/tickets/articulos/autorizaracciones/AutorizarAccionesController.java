package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.autorizaracciones;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.autorizaracciones.formulario.FormularioAutorizaDevolucionBean;
import com.comerzzia.pos.core.gui.InitializeGuiException;
// import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.usuarios.UsuarioInvalidLoginException;
import com.comerzzia.pos.services.core.usuarios.UsuariosService;
import com.comerzzia.pos.services.core.usuarios.UsuariosServiceException;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * GAP - CAJERO AUXILIAR
 * Se realizarán validaciones para las siguientes acciones : 
 * 1.- VENTAS :
 * 	- Negar una linea.
 *  - Eliminar una linea.
 *  - Cancelar una venta.
 * 2.- DEVOLUCIONES : 
 *  - Recuperar un ticket para su devolución.
 *  - Eliminar una linea.
 *  - Cancelar una devolución.
 */
@Component
public class AutorizarAccionesController extends WindowController implements Initializable{

	private static final Logger log = Logger.getLogger(AutorizarAccionesController.class.getName());

	public static final String PANTALLA_ANULACION = "PANTALLA_ANULACION";
	public static final String sUsuario = "usuario", sNombre = "nombre", sDocumento = "documento", sTienda = "tienda";

	@FXML
	protected TextField tfUsuario, tfDocumento, tfTienda;
	@FXML
	protected PasswordField tfPass;
	@FXML
	protected Label lbError, lbTitulo, lbMensaje, lbTituloDatosDevolucion, lbDocumento, lbTienda;

	@Autowired
	private Sesion sesion;
	@Autowired
	private UsuariosService usuariosService;

	FormularioAutorizaDevolucionBean frAutorizaDevolucion;
	private boolean esAnulacion;

	@Override
	public void initialize(URL url, ResourceBundle rb){
		log.debug("initialize() : GAP - CAJERO AUXILIAR");
		
		frAutorizaDevolucion = SpringContext.getBean(FormularioAutorizaDevolucionBean.class);
		frAutorizaDevolucion.setFormField("usuario", tfUsuario);
		frAutorizaDevolucion.setFormField("pass", tfPass);
		frAutorizaDevolucion.setFormField("documento", tfDocumento);
		frAutorizaDevolucion.setFormField("tienda", tfTienda);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException{
		registrarAccionCerrarVentanaEscape();
		registraEventoTeclado(new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent arg0){
				if(arg0.getCode().equals(KeyCode.ENTER)){
					try{
						accionAceptar(null);
					}
					catch(Exception ex){
					}
				}
			}
		}, KeyEvent.KEY_RELEASED);
	}

	@Override
	public void initializeForm() throws InitializeGuiException{
		/* Inicializamos los componentes de pantalla */
		tfUsuario.setText("");
		tfPass.setText("");
		if(getDatos().get(PANTALLA_ANULACION) != null){
			esAnulacion = true;
			lbTitulo.setText(I18N.getTexto("Autorizar anulación"));
			lbMensaje.setText(I18N.getTexto("Indique el usuario que autoriza la anulación :"));
			lbTituloDatosDevolucion.setVisible(false);
			lbDocumento.setVisible(false);
			tfDocumento.setVisible(false);
			lbTienda.setVisible(false);
			tfTienda.setVisible(false);
		}
		else{
			esAnulacion = false;
			lbTitulo.setText(I18N.getTexto("Autorizar devolución"));
			lbMensaje.setText(I18N.getTexto("Indique el usuario que autoriza la devolución :"));
			lbTituloDatosDevolucion.setVisible(true);
			lbDocumento.setVisible(true);
			tfDocumento.setVisible(true);
			lbTienda.setVisible(true);
			tfTienda.setVisible(true);
			tfDocumento.setText("");
			tfTienda.setText("");
		}
		/* Limpiamos el error */
		frAutorizaDevolucion.clearErrorStyle();
		lbError.setText("");
		/* Establecemos los parametros a vacío */
		getDatos().put(sUsuario, "");
		getDatos().put(sNombre, "");
		getDatos().put(sDocumento, "");
		getDatos().put(sTienda, "");
	}

	@Override
	public void initializeFocus(){
		tfUsuario.requestFocus();
	}

	@FXML
	public void accionAceptar(ActionEvent event){
		String sDesUsuario = "";
		frAutorizaDevolucion.setUsuario(tfUsuario.getText());
		frAutorizaDevolucion.setPass(tfPass.getText());
		frAutorizaDevolucion.setDocumento(tfDocumento.getText());
		frAutorizaDevolucion.setTienda(tfTienda.getText());
		try{
			if(accionValidarFormulario()){
				try{
					UsuarioBean usuarioBean = usuariosService.login(tfUsuario.getText(), tfPass.getText());
					sDesUsuario = usuarioBean.getDesusuario();
				}
				catch(UsuarioInvalidLoginException ex){
					throw ex;
				}
				catch(UsuariosServiceException ex){
					log.error("init() -  Error iniciando sesión de usuario : " + tfUsuario.getText() + " - " + ex.getMessageI18N());
					throw new SesionInitException(ex.getMessageI18N(), ex);
				}
				catch(Exception ex){
					log.error("init() - Error inicializando sesión de usuario : " + tfUsuario.getText() + " - " + ex.getMessage(), ex);
					throw new SesionInitException(ex);
				}
				if(sesion.getSesionUsuario().getUsuario().getUsuario().toUpperCase().equals(tfUsuario.getText().toUpperCase())){
					tfUsuario.setText("");
					tfPass.setText("");
					String textoError = null;
					if(esAnulacion){
						textoError = I18N.getTexto("No tiene permisos para realizar anulaciones");
					}
					else{
						textoError = I18N.getTexto("No tiene permisos para realizar devoluciones");
					}
					VentanaDialogoComponent.crearVentanaError(textoError, getStage());
					tfUsuario.requestFocus();
					return;
				}
			}
			else{
				log.debug("Datos autorización devolución incompletos");
				return;
			}
			/* Si todo es correcto realizamos las validaciones necesarias */
			getDatos().put(sUsuario, tfUsuario.getText());
			getDatos().put(sNombre, sDesUsuario);
			getDatos().put(sDocumento, tfDocumento.getText());
			getDatos().put(sTienda, tfTienda.getText());
			getStage().close();
		}
		catch(Exception ex){
			log.error("actionBtAceptar() - Error no controlado - " + ex.getMessage(), ex);
			lbError.setText(I18N.getTexto("Usuario y/o contraseña no válidos"));
			tfUsuario.setText("");
			tfPass.setText("");
			tfUsuario.requestFocus();
		}
	}

	public boolean accionValidarFormulario(){
		boolean bResultado = true;
		/* Inicializamos la etiqueta de error */
		frAutorizaDevolucion.clearErrorStyle();
		lbError.setText("");

		/* Validamos el formulario */
		Set<ConstraintViolation<FormularioAutorizaDevolucionBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frAutorizaDevolucion);
		Iterator<ConstraintViolation<FormularioAutorizaDevolucionBean>> iterator = constraintViolations.iterator();
		while(iterator.hasNext()){
			ConstraintViolation<FormularioAutorizaDevolucionBean> next = iterator.next();
			/* Si estamos en el proceso de anulación no comprobamos los campos documento y tienda */
			if((esAnulacion && (!next.getPropertyPath().toString().equals("documento") 
					&& !next.getPropertyPath().toString().equals("tienda"))) || !esAnulacion){
				frAutorizaDevolucion.setErrorStyle(next.getPropertyPath(), true);
				lbError.setText(next.getMessage());
				bResultado = false;
			}
		}
		if(!bResultado){
			estableceFoco();
		}
		return bResultado;
	}

	public void estableceFoco(){
		if(tfUsuario.getText().trim().equals("")){
			tfUsuario.requestFocus();
		}
		else{
			if(tfPass.getText().trim().equals("")){
				tfPass.requestFocus();
			}
			else{
				if(tfDocumento.getText().trim().equals("")){
					tfDocumento.requestFocus();
				}
				else{
					if(tfTienda.getText().trim().equals("")){
						tfTienda.requestFocus();
					}
				}
			}
		}
	}

}
