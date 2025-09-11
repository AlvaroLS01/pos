/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */

package com.comerzzia.pos.gui.ventas.cajas.apertura;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.cajas.apertura.recuento.RecuentoCajaAperturaView;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionCaja;
import com.comerzzia.pos.util.format.FormatUtil;


@Controller
public class AperturaCajaController extends WindowController implements Initializable {

    private static final Logger log = Logger.getLogger(AperturaCajaController.class.getName());
    private SesionCaja cajaSesion;
    protected AperturaCajaFormularioBean formularioAperturaGui;

    @FXML
    protected TextFieldImporte tfSaldo;
    
    @FXML
    protected DatePicker tfFecha;
    
    @FXML
    protected Button btContarSaldo;
    
    @Autowired
    private Sesion sesion;

    // <editor-fold  desc="Creación e inicialización"> 
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @Override
    public void initializeComponents() {
        registrarAccionCerrarVentanaEscape();
    }

    @Override
    public void initializeForm() throws InitializeGuiException {
        cajaSesion = sesion.getSesionCaja();
        Date d = new Date();
        tfFecha.setSelectedDate(d); //Inicializamos la apertura a fecha de hoy
        tfFecha.setDisable(true);
        tfSaldo.setText("0");
    }

    @Override
    public void initializeFocus() {
    	tfSaldo.requestFocus();
    }

    // </editor-fold>
    // <editor-fold desc="Acciones">  
    @FXML
    protected void accionSeleccionaFecha(ActionEvent event) {
        log.debug("accionSeleccionaFecha()");
    }

    @FXML
    public void accionAceptar() {
        try {
            log.debug("accionAceptar()");
            //Validar formulario                  
            formularioAperturaGui = new AperturaCajaFormularioBean(tfFecha.getTexto(), tfSaldo.getText());
            if (!accionValidarForm()) {
                return;
            }
            
            Date fechaApertura = FormatUtil.getInstance().desformateaFechaHora(tfFecha.getTexto(), true);
            
            Calendar calendarHoy = Calendar.getInstance();
            calendarHoy.set(Calendar.HOUR_OF_DAY, 0);
            calendarHoy.set(Calendar.MINUTE, 0);
            calendarHoy.set(Calendar.SECOND, 0);
            calendarHoy.set(Calendar.MILLISECOND, 0);
            
            //Si la fecha de apertura no es hoy, la ponemos sin hora
            Calendar calendarAperturaSinHora = Calendar.getInstance();
            calendarAperturaSinHora.setTime(fechaApertura);
            calendarAperturaSinHora.set(Calendar.HOUR_OF_DAY, 0);
            calendarAperturaSinHora.set(Calendar.MINUTE, 0);
            calendarAperturaSinHora.set(Calendar.SECOND, 0);
            calendarAperturaSinHora.set(Calendar.MILLISECOND, 0);
            if(!calendarAperturaSinHora.equals(calendarHoy)){
            	fechaApertura = calendarAperturaSinHora.getTime();
            }
            
            cajaSesion.abrirCajaManual(fechaApertura, formularioAperturaGui.getSaldoAsBigDecimal());
            getStage().close();
        }
        catch (CajasServiceException | CajaEstadoException e) {
            log.error("accionAceptar() - Error al tratar de realizar apertura de caja: " + e.getCause(), e);
            VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessageI18N(), e);
        }
        catch (Exception e) {
            log.error("accionAceptar() - Error al tratar de realizar apertura de caja: " + e.getCause(), e);
            VentanaDialogoComponent.crearVentanaError(getStage(),  e);
        }
    }

    /**
     * Validamos el formulario
     *
     * @return
     */
    protected boolean accionValidarForm() {
        // Limpiamos los errores que pudiese tener el formulario
        formularioAperturaGui.clearErrorStyle();

        // Validamos el formulario de login
        Set<ConstraintViolation<AperturaCajaFormularioBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(formularioAperturaGui);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<AperturaCajaFormularioBean> next = constraintViolations.iterator().next();
            formularioAperturaGui.setErrorStyle(next.getPropertyPath(), true);
            formularioAperturaGui.setFocus(next.getPropertyPath());
            // Mostramos el error en una ventana de error
            VentanaDialogoComponent.crearVentanaError(next.getMessage(), getStage());
            return false;
        }
        return true;
    }

    @FXML
    public void accionIntro(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            accionAceptar();
        }
    }
    
    @FXML
    public void accionContarSaldo(){
    	HashMap<String, Object> datos = new HashMap<String, Object>();
    	getApplication().getMainView().showModal(RecuentoCajaAperturaView.class, datos);
    	BigDecimal saldo = (BigDecimal) datos.get("saldo");
    	if(saldo!=null){
    		tfSaldo.setText(FormatUtil.getInstance().formateaImporte(saldo));
    	}
    }

    // </editor-fold>
}
