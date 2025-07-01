package com.comerzzia.pos.core.gui.components.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.events.CzzChildComponentClosedEvent;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Use {@link com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder DialogWindowBuilder} instead
 */
@Deprecated
@Component
public class DialogWindowComponent extends Stage {

    private static final Logger log = Logger.getLogger(DialogWindowComponent.class.getName());

    public static int INFO_TYPE = 1;
    public static int WARN_TYPE = 2;
    public static int ERROR_TYPE = 3;
    public static int EXCEPTION_TYPE = 4;
    public static int CONFIRM_TYPE = 5;
    
	/**
     * Crea una ventana de Información sin título
     * @param mensaje
     * @param ventanaPadre 
     */
     public static void openInfoWindow( String mensaje, Window ventanaPadre) {
         log.debug("crearVentanaInfo() ");
         openInfoWindow("", mensaje, ventanaPadre);
     }
     
    /**
     * Crea una ventana de información
     *
     * @param titulo titulo de la ventana
     * @param mensaje mensaje que se mostrará
     * @param ventanaPadre ventana padre
     */
    public static void openInfoWindow(String titulo, String mensaje, Window ventanaPadre) {
        log.debug("crearVentanaInfo() - " + titulo);
        openWindow(titulo, mensaje, INFO_TYPE, false, ventanaPadre, null, false, null, null);
    }

    /**
     * Crea una ventana de error
     * @param ventanaPadre ventana padre
     * @param mensaje mensaje que se mostrará
     */
    public static void openErrorWindow(String mensaje, Window ventanaPadre) {
    	openErrorWindow(ventanaPadre, mensaje, null);
    }

    /**
     * Crea una ventana de error
     * @param ventanaPadre ventana padre
     * @param e Excepción de la que se mostrará su traza
     */
    public static void openErrorWindow(Window ventanaPadre, Throwable e) {
        openErrorWindow(ventanaPadre, I18N.getText("Lo sentimos, ha ocurrido un error."), e);
    }

    /**
     * Crea una ventana de error pasando una excepción
     * @param mensaje
     * @param ventanaPadre
     * @param e 
     */
    public static void openErrorWindow(Window ventanaPadre, String mensaje, Throwable e) {
    	if(AppConfig.getCurrentConfiguration().getDeveloperMode() && (e == null || e.getMessage() == null || e.getMessage().isEmpty() || (e.getCause() != null && e.getCause().getMessage() == null))){
    		log.debug("crearVentanaError() - Modo Desarrollo activo, mostramos la traza del hilo de ejecución para facilitar el debug");
    		Thread.dumpStack();
    	}
        log.debug("crearVentanaError() - Mensaje: " + mensaje);
        if(e != null) {
        	log.error("crearVentanaError() - Excepción: " + e.getMessage());
        }
        openWindow(null, mensaje, EXCEPTION_TYPE, false, ventanaPadre, e, true, null, null);
    }

    /**
     * Crea una ventana de aviso sin título
     * @param mensaje
     * @param ventanaPadre 
     */
    public static void openWarnWindow( String mensaje, Window ventanaPadre) {
        openWarnWindow("", mensaje, ventanaPadre);
    }
    
    /**
     * Crea una ventana de aviso
     *
     * @param titulo titulo de la ventana
     * @param mensaje mensaje que se mostrará
     * @param ventanaPadre ventana padre
     */
    public static void openWarnWindow(String titulo, String mensaje, Window ventanaPadre) {
        log.debug("crearVentanaAviso() - " + mensaje);
        openWindow(titulo, mensaje, WARN_TYPE, false, ventanaPadre, null, true, null, null);
    }
    

    /**
     * Crea una ventana de confirmación sin título
     * @param mensaje
     * @param ventanaPadre
     * @return 
     */
    public static boolean openConfirmWindow(String mensaje, Window ventanaPadre, String textoAceptar, String textoCancelar){
    	 return openConfirmWindow("", mensaje, ventanaPadre, textoAceptar, textoCancelar);      
    }
    
    public static boolean openConfirmWindow(String mensaje, Window ventanaPadre){
   	 return openConfirmWindow("", mensaje, ventanaPadre, null, null);      
   }
    
    public static boolean openConfirmWindow(String titulo, String mensaje, Window ventanaPadre) {
        log.debug("crearVentanaConfirmacion() - " + titulo);
        CzzAlertDialog ventana = openWindow(titulo, mensaje, CONFIRM_TYPE, true, ventanaPadre, null, false,null,null);
       return ventana.isAccepted();
    }
    
    /**
     * Crea una ventana de confirmación
     *
     * @param titulo titulo de la ventana
     * @param mensaje mensaje que se mostrará
     * @param ventanaPadre ventana padre
     * @param textoAceptar handler que lanzaremos si aceptamos la ventan
     * @param textoCancelar handler que lanzaremos si cancelamos la ventana
     */
    public static boolean openConfirmWindow(String titulo, String mensaje, Window ventanaPadre, String textoAceptar, String textoCancelar) {
        log.debug("crearVentanaConfirmacion() - " + titulo);
        CzzAlertDialog ventana = openWindow(titulo, mensaje, CONFIRM_TYPE, true, ventanaPadre, null, false, textoAceptar, textoCancelar);
       return ventana.isAccepted();
    }
    
    public static CzzAlertDialog openWindow(String titulo, String mensaje, int tipo, boolean mostrarCancelar, Window ventanaPadre, Throwable e, boolean beep) {
    	return openWindowInternal(titulo, mensaje, tipo, mostrarCancelar, ventanaPadre, e, beep, null, null, null);
    }
    
    public static CzzAlertDialog openWindow(String titulo, String mensaje, int tipo, boolean mostrarCancelar, Window ventanaPadre, Throwable e, boolean beep, String textoAceptar, String textoCancelar) {
    	return openWindowInternal(titulo, mensaje, tipo, mostrarCancelar, ventanaPadre, e, beep, textoAceptar, textoCancelar, null);
    }
    public static CzzAlertDialog openWindow(String title, String message, int type, boolean showCancel, Window parentStage, Throwable e, boolean beep, String acceptText, String cancelText, String mainMessage) {
    	return openWindowInternal(title, message, type, showCancel, parentStage, e, beep, acceptText, cancelText, mainMessage);
    }
    
    public static boolean openConfirmWindowSingleButton(String mensaje, Window ventanaPadre){
   	 log.debug("crearVentanaConfirmacionUnBoton()" );
   	CzzAlertDialog ventana = openWindow(null, mensaje, ERROR_TYPE, false, ventanaPadre, null, false, null, null);
        return ventana.isAccepted();
    }
    
    protected static CzzAlertDialog openWindowInternal(final String titulo, final String mensaje, final int tipo, final boolean mostrarCancelar, final Window ventanaPadre, final Throwable e, final boolean beep, String textoAceptar, String textoCancelar, String mainMessage) {
    	DialogWindowComponent instance = CoreContextHolder.get().getBean(DialogWindowComponent.class);
    	return instance.openDialog(titulo, mensaje, tipo, mostrarCancelar, ventanaPadre, e, beep, textoAceptar, textoCancelar, mainMessage);
    }
    
	protected CzzAlertDialog openDialog(String titulo, String mensaje, int tipo, boolean mostrarCancelar, Window ventanaPadre, Throwable e, boolean beep, String textoAceptar, String textoCancelar, String mainMessage) {
		AlertType alertType = AlertType.INFORMATION;
		switch (tipo) {
		case 1:
			alertType = AlertType.INFORMATION;
			break;
		case 2:
			alertType = AlertType.WARNING;
			break;
		case 3:
		case 4:
			alertType = AlertType.ERROR;
			break;
		case 5:
			alertType = AlertType.CONFIRMATION;
			break;
		}
		List<ButtonType> buttons = new ArrayList<>();
		if (StringUtils.isNotBlank(textoAceptar)) {
			buttons.add(new ButtonType(textoAceptar, ButtonData.OK_DONE));
		} else {
			buttons.add(ButtonType.OK);
		}
		if (mostrarCancelar && StringUtils.isNotBlank(textoCancelar)) {
			buttons.add(new ButtonType(textoCancelar, ButtonData.CANCEL_CLOSE));
		} else if (mostrarCancelar) {
			buttons.add(ButtonType.CANCEL);
		}
		
		if (mensaje == null) {
			mensaje = I18N.getText("Error inesperado. Para mas información consulte el log.");
		}
		
		return DialogWindowBuilder.getBuilder(ventanaPadre).type(alertType)
				.message(mensaje).mainMessage(mainMessage).title(titulo)
				.buttons(buttons).exception(e)
				.beep(beep).buildAndShow();
	}
    

}
