package com.comerzzia.iskaypet.pos.core.gui.login;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

import com.comerzzia.iskaypet.librerias.sipay.client.constants.SipayConstants;
import com.comerzzia.iskaypet.pos.core.gui.main.cabecera.IskaypetCambioPasswordController;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.FirstLoginException;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.PasswordExpiradoLoginException;
import com.comerzzia.iskaypet.pos.services.core.usuarios.IskaypetUsuariosService;
import com.comerzzia.iskaypet.pos.services.payments.methods.types.sipay.SipayService;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.login.LoginController;
import com.comerzzia.pos.core.gui.main.MainViewController;
import com.comerzzia.pos.core.gui.main.cabecera.cambioPassword.CambioPasswordView;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.usuarios.UsuarioInvalidLoginException;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;


@Primary
@Controller
public class IskaypetLoginController extends LoginController {

	private static final Logger log = Logger.getLogger(IskaypetLoginController.class.getName());

    @Autowired
    private IskaypetUsuariosService usuariosService;
    @Autowired
    private CopiaSeguridadTicketService copiaSeguridadTicketService;
    @Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	@Autowired
	protected SipayService sipayService;

    @Override
    public void initializeForm() {
        super.initializeForm();

        Caja cajaAbierta = sesion.getSesionCaja().getCajaAbierta();

        if(AppConfig.showCashOpeningUser && cajaAbierta != null && StringUtils.isNotBlank(cajaAbierta.getUsuario())) {
            lbLoggedIn.setText(I18N.getTexto("Caja abierta por el usuario {0}", usuariosService.getDescripcionUsuario( cajaAbierta.getUsuario())));
        }
    }

    @Override
    public void actionBtAceptar(ActionEvent event) {
        log.debug("actionBtAceptar()");
        if (accionFrLoginSubmit()){
            try {
            	//Comprobamos que el usuario tiene acceso a la pantalla activa
            	if(!hasPermissionsCurrentScreen(tfUsuario.getText())){
            		VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El usuario no tiene permisos para ejecutar la pantalla activa."), getStage());
            		return;
            	}
            	sesion.initUsuarioSesion(tfUsuario.getText(), tfPassword.getText());
            	iniciarSesion();
            } catch (SesionInitException ex) {
                VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageDefault(), ex);
                accionLimpiarFormulario();
                initializeFocus();
            } catch (UsuarioInvalidLoginException ex) {
                lbError.setText(I18N.getTexto(ex.getMessageI18N()));
                initializeFocus();
                
                // ISK-386-gap-184-renovacion-de-contrasenas
                if (ex.getCause() instanceof PasswordExpiradoLoginException || ex.getCause() instanceof FirstLoginException) {
    				muestraVentanaCambioClave();
    				if (datos.containsKey(IskaypetCambioPasswordController.HA_CAMBIADO_PASSWORD)) {
						iniciarSesion();
    				}
    			}
                // fin ISK-386-gap-184-renovacion-de-contrasenas
            } catch (Exception ex){
                log.debug("actionBtAceptar() - Error no controlado -"+ex.getMessage(),ex);
                VentanaDialogoComponent.crearVentanaError(getStage(), ex);
            }
        }
    }

	private void iniciarSesion() {
		sesion.getSesionUsuario().clearPermisos();
		getApplication().comprobarPermisosUI();

		((MainViewController)getApplication().getMainView().getController()).actualizarUsuario();
		if (isModoSeleccionDeCajero()){
		    getDatos().put(PARAMETRO_SALIDA_CAMBIO_USUARIO, "S");
		    getStage().close();

		    // ISK-379-gap-181-sipay-envio-keep-alive
		    checkSipayKeepAlive();
		} else if(isModoBloqueo()) {
			getStage().close();
		} else {
		    //TODO Esto está incompleto, revisar
//                    if(!Sesion.getInstance().getAplicacion().getConfigTPV().isInicializada()){
//                        VentanaDialogoComponent.crearVentanaAviso(I18nBundle.getInstance().getTextoMensajes(PARAMETRO_USUARIO_SELECCIONADO), getStage());
//                        getApplication().showFullScreenView(ConfiguracionTPVView.class);
//                    }
//                    else{
		        getApplication().showFullScreenView(mainView);
		        copiaSeguridadTicketService.clearBackupReturns();
		        getApplication().activarTimer();
		        actionFilesManager.scheduleCancel();
//                    }
		}
	}

    // ISK-379-gap-181-sipay-envio-keep-alive
    public void checkSipayKeepAlive() {
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			if (SipayConstants.MANAGER_CONTROL_CLASS.equals(configuration.getControlClass())) {
				try {
					if (!sipayService.isKeepingAlive()) {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El pinpad no está disponible, por favor reinicie el pinpad."), getStage());
					}
				} catch (Exception e) {
					log.error("checkSipayKeepAlive() - Error comprobando la conexión del pinpad: " + e.getMessage(), e);
				}
			}
		}
    }
    
    // ISK-386-gap-184-renovacion-de-contrasenas
    public void muestraVentanaCambioClave() {
		log.debug("muestraVentanaCambioClave() - Contraseña expirada, mostrando modal de cambio de contraseña");
		
		HashMap<String, Object> datos = new HashMap<String, Object>();
		datos.put(IskaypetCambioPasswordController.PARAM_PANTALLA_ORIGEN, IskaypetCambioPasswordController.PANTALLA_ORIGEN_LOGIN);
		datos.put(IskaypetCambioPasswordController.USUARIO_CAMBIO_PASSWORD, tfUsuario.getText());
		datos.put(IskaypetCambioPasswordController.USUARIO_PASSWORD_ANTIGUA, tfPassword.getText());
	    btAceptar.requestFocus();
		getApplication().getMainView().showModalCentered(CambioPasswordView.class, datos, this.getStage());
		tfPassword.requestFocus();
		if (datos.containsKey(IskaypetCambioPasswordController.HA_CAMBIADO_PASSWORD)) {
			tfPassword.clear();
			lbError.setText("");
		}
	}

}
