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

package com.comerzzia.pos.gui.ventas.cajas.apuntes;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.cajas.apuntes.tablaApuntes.TablaApuntesController;
import com.comerzzia.pos.gui.ventas.cajas.apuntes.tablaApuntes.TablaApuntesView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.cajas.conceptos.CajaConceptosServiceException;
import com.comerzzia.pos.services.cajas.conceptos.CajaConceptosServices;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionCaja;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Controller
public class InsertarApunteController extends WindowController implements Initializable {

    // <editor-fold desc="Declaración de variables">    
    //log
    private static final Logger log = Logger.getLogger(FacturacionArticulosController.class.getName());
    // Variables del controlador
    private SesionCaja cajaSesion;

    // Componentes del controlador
    @FXML
    protected TextField tfImporte, tfDocumento, tfDescConcepto, tfCodConcepto;
    @FXML
    protected Label lbTipoImporte, lbError;
    
    protected FormularioApunteBean frApunte;
    
    protected CajaConceptoBean concepto;
    
    @Autowired
    private Sesion sesion;
    
    @Autowired
	private CajaConceptosServices cajaConceptosServices;

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Creación e inicialización"> 
    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Inicializamos el formulario de login
        frApunte = SpringContext.getBean(FormularioApunteBean.class);
        // Asignamos un componente a cada elemento del formulario. (Para establecer foco o estilos de error)
        frApunte.setFormField("importe", tfImporte);
        frApunte.setFormField("documento", tfDocumento);
        frApunte.setFormField("concepto", tfCodConcepto);
        frApunte.setFormField("descripcion", tfDescConcepto);            
    }

    @Override
    public void initializeForm() {
    	cajaSesion = sesion.getSesionCaja();
        refrescarDatosPantalla();
        
       // bInicio = true;
    //	lbError.setText("");
     //   tfImporte.requestFocus();
    }

    @Override
    public void initializeFocus() {
    	frApunte.clearErrorStyle();
    	
    	lbError.setText("");
        tfCodConcepto.requestFocus();
        
        tfCodConcepto.requestFocus();
    }

    @Override
    public void initializeComponents() {
        // Aquí van elementos que habría que inicializar tras establecer el application
    	tfImporte.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                evalInOutValue(t1);
            }
        });
    	
    	tfCodConcepto.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
            	if(oldValue){
            		validaConcepto(tfCodConcepto.getText());
            	}
            }
        });
        
        //Se registra el evento de escape para salir de la ventana
        registrarAccionCerrarVentanaEscape();
        
       //registramos el evento al pulsar enter
      		registraEventoTeclado(new EventHandler<KeyEvent>() {			
      			@Override
      			public void handle(KeyEvent arg0) {
      				// TODO Auto-generated method stub
      				if(arg0.getCode().equals(KeyCode.ENTER)){
      					try
      					{
      					   accionAceptar(null);
      					   arg0.consume();
      					}
      					catch(Exception ex)
      					{}		
      				}
      			}
      		}, KeyEvent.KEY_RELEASED);
      		
      	tfDescConcepto.setEditable(false);
    }

    // </editor-fold>
    
    // <editor-fold desc="AccionesMenu">    
    protected boolean validaConcepto(String codigo){
    	frApunte.clearErrorStyle();
    	lbError.setText("");
    	
    	String codConcepto = codigo.trim();
    	
    	if(!codConcepto.isEmpty()){
	    	CajaConceptoBean concepto = null;
	        
	        try {
	        	concepto = cajaConceptosServices.consultarConcepto(codConcepto);
				this.concepto = concepto;
				tfDescConcepto.setText(concepto.getDesConceptoMovimiento());
				evalInOutValue(tfImporte.getText());
	        }
	        catch (CajaConceptosServiceException ex) {
	            log.error("No se encontró el código del concepto de movimiento de caja.");	            
	            lbError.setText(I18N.getTexto("El código de concepto no existe en la base de datos"));        
	            //tfCodConcepto.requestFocus();
	            concepto = null;
	            tfDescConcepto.setText("");
	            return false;
	        }
    	}
    	return true;
    }
    
    
    protected boolean evalInOutValue(String inOutValue){
    	int inOut = -1;
		if(concepto != null && StringUtils.isNotBlank(concepto.getInOut())) {
			if(concepto.getInOut().equals(CajaConceptoBean.MOV_ENTRADA)) {
				inOut = inOut*-1;
			}
		}
		
		if(StringUtils.isNotBlank(inOutValue)) {
			BigDecimal bd = FormatUtil.getInstance().desformateaImporte(inOutValue);
			if(bd != null) {				
				inOut = inOut * bd.signum();
			}
		}
		
		if(inOut<0) {
			lbTipoImporte.setText(I18N.getTexto("SALIDA DE CAJA"));
		}else {
			lbTipoImporte.setText(I18N.getTexto("ENTRADA EN CAJA"));
		}
    	return true;
    }
    
    @FXML
    public void accionAceptar(ActionEvent event) {
        log.debug("accionAceptar()");
        
        if((this.concepto == null) || (!this.concepto.getCodConceptoMovimiento().equals(tfCodConcepto.getText()))){
        	if (!validaConcepto(tfCodConcepto.getText())){
        		return;
        	}
        }
        
        frApunte.setDocumento(tfDocumento.getText());
        frApunte.setImporte(tfImporte.getText());
        frApunte.setConcepto(concepto);
        frApunte.setDescripcion(tfDescConcepto.getText());
        
        if (!accionValidarFormulario()) {
        	log.debug("datos del apunte invalidos");
        	return;
        }
        
        CajaMovimientoBean mov = null;
        
        try {        	            	
            mov = cajaSesion.crearApunteManual(frApunte.getImporteAsBigDecimal(), frApunte.getConcepto().getCodConceptoMovimiento(), frApunte.getDocumento(), tfDescConcepto.getText());
        }
        catch (Exception e) {
            log.error("accionAceptar() - Error inesperado - " + e.getCause(), e);
            VentanaDialogoComponent.crearVentanaError(getStage(), e);
        }
        
                
        if (mov != null) {
	        try {        	            
	            imprimirMovimiento(mov);
	        }
	        catch (Exception e) {
	            log.error("accionAceptar() - Error inesperado imprimiendo- " + e.getCause(), e);
	            VentanaDialogoComponent.crearVentanaError(getStage(), e);	            
	        }
        }

        getStage().close();                        
    }

    @FXML
    public void accionAyudaConcepto(ActionEvent event) {
        log.debug("accionAyudaConcepto()");
    }

    public void refrescarDatosPantalla() {
        tfImporte.setText("");
        tfCodConcepto.setText("");
        tfDescConcepto.setText("");
        tfDocumento.setText("");
        lbTipoImporte.setText("");
        concepto = null;
    }
    
    protected void estableceFoco() {	
		if (tfImporte.getText().trim().equals("")){
			tfImporte.requestFocus();
		}
		else{
			if (tfCodConcepto.getText().trim().equals("")){
				tfCodConcepto.requestFocus();
			}
			else{
				if (tfDescConcepto.getText().trim().equals("")){
					tfDescConcepto.requestFocus();
				}
			}
		}
	}

    protected boolean accionValidarFormulario() {       
        boolean bResultado = true;
		
        //Inicializamos la etiqueta de error
        frApunte.clearErrorStyle();
		lbError.setText("");
		
        // Validamos el formulario 
        Set<ConstraintViolation<FormularioApunteBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frApunte);
        Iterator<ConstraintViolation<FormularioApunteBean>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()){
            ConstraintViolation<FormularioApunteBean> next = iterator.next();
            frApunte.setErrorStyle(next.getPropertyPath(), true);

            lbError.setText(next.getMessage());

            bResultado = false;
        }
        if(!bResultado) {
        	estableceFoco();
        }
        
        return bResultado;
    }

    public void imprimirMovimiento(CajaMovimientoBean movimiento) throws CajasServiceException{
    	try{
    		// String printTicket = VelocityServices.getInstance().getPrintCierreCaja(caja);

    		//Rellenamos los parametros
    		Map<String, Object> contextoTicket = new HashMap<String, Object>();

    		//Introducimos los parámetros que necesita el ticket para imprimir la información del cierre
    		contextoTicket.put("movimiento", movimiento);
    		contextoTicket.put("caja", sesion.getSesionCaja().getCajaAbierta().getCodCaja());
    		contextoTicket.put("tienda", sesion.getAplicacion().getTienda().getCodAlmacen());
    		contextoTicket.put("empleado", sesion.getSesionUsuario().getUsuario().getDesusuario());                

    		// Llamamos al servicio de impresión
    		ServicioImpresion.imprimir(ServicioImpresion.PLANTILLA_MOVIMIENTO_CAJA,contextoTicket);            
    	}
    	catch(Exception e){
            log.error("imprimirCierre() - Error imprimiendo  cierre de caja. Error inesperado: " + e.getMessage(), e);
            throw new CajasServiceException("error.service.cajas.print", e);
        }
    }
    
    public void accionBuscarConceptos(){
    	
    	HashMap<String,Object> datos = new HashMap<String,Object>();
    	getApplication().getMainView().showModalCentered(TablaApuntesView.class, datos, this.getStage());
    	
    	if(datos.containsKey(TablaApuntesController.PARAMETRO_CONCEPTO_SELECCIONADO)){
    		CajaConceptoBean concepto = (CajaConceptoBean) datos.get(TablaApuntesController.PARAMETRO_CONCEPTO_SELECCIONADO);
    		
    		tfDescConcepto.setText(concepto.getDesConceptoMovimiento());
    		tfCodConcepto.setText(concepto.getCodConceptoMovimiento());
    		this.concepto = concepto;
    		evalInOutValue(tfImporte.getText());
    		
    	}
    }
    
    // </editor-fold>
}
