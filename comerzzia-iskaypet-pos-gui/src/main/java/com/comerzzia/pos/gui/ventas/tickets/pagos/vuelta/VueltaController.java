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

package com.comerzzia.pos.gui.ventas.tickets.pagos.vuelta;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.medioPago.BotonBotoneraTextoComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.botonera.medioPago.ConfiguracionBotonMedioPagoBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.PaymentsManager;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

@Controller
public class VueltaController extends WindowController implements Initializable, IContenedorBotonera {

    //log
    private static final Logger log = Logger.getLogger(VueltaController.class.getName());

    //Clave del parametro que recibe la ventana
    public static final String CLAVE_PARAMETRO_ENTRADA_VUELTA_CODMEDIOPAGO = "codMedioPagoVuelta";
    public static final String CLAVE_PARAMETRO_ENTRADA_VUELTA_DESMEDIOPAGO = "desMedioPagoVuelta";
    public static final String CLAVE_PARAMETRO_SALIDA_VUELTA = "medioPagoVueltaSeleccionado";
    public static final String CLAVE_OCULTAR_BOTONES = "ocultarBotones";
    public static final String CLAVE_CANCELADO = "cancelado";
    public static final String CLAVE_PAYMENT_MANAGER = "paymentManager";

    // Medio de pago seleccionado en esta ventana (Variable de salida)
    protected String codMedioPagoSeleccionado;

    //Elementos de pantalla
    @FXML
    protected Button btAceptar;

    @FXML
    protected AnchorPane panelMediosPagos;
    
    @FXML
    protected AnchorPane panelBotones;
    
    @FXML
    protected Label lbMedioPago;

    // botonera de medios de pago
    protected BotoneraComponent botoneraMediosPago;
    
    protected PaymentsManager paymentsManager;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initializeComponents() {
        log.debug("inicializarComponentes()");
        
        codMedioPagoSeleccionado = null;
    }
    
    @Override
    public void initializeForm() {
    	this.paymentsManager = (PaymentsManager) getDatos().get(CLAVE_PAYMENT_MANAGER);
    	
    	mostrarMediosPago();
    	
        if (this.getDatos().containsKey(CLAVE_PARAMETRO_ENTRADA_VUELTA_CODMEDIOPAGO)) {
            codMedioPagoSeleccionado = (String) this.getDatos().get(CLAVE_PARAMETRO_ENTRADA_VUELTA_CODMEDIOPAGO);
            String desMedioPagoSeleccionado = (String) this.getDatos().get(CLAVE_PARAMETRO_ENTRADA_VUELTA_DESMEDIOPAGO);
            lbMedioPago.setText(desMedioPagoSeleccionado);
        } else {
	    	codMedioPagoSeleccionado = null;
	    	lbMedioPago.setText("");
        }
    }

	protected void mostrarMediosPago() {
		List<ConfiguracionBotonBean> listaAccionesMP = new LinkedList<ConfiguracionBotonBean>();
        log.debug("mostrarMediosPago() - Creando acciones para botonera de pago contado");
        for (MedioPagoBean pag : MediosPagosService.mediosPagoVisibleVenta) {
        	if(paymentsManager.isExchangePaymentMethodAvailable(pag.getCodMedioPago())) {
	            ConfiguracionBotonMedioPagoBean cfg = new ConfiguracionBotonMedioPagoBean(null, pag.getDesMedioPago(), null, "ACCION_SELECIONAR_MEDIO_PAGO", "", pag);
	            listaAccionesMP.add(cfg);
        	}
        }

        try {
            botoneraMediosPago = new BotoneraComponent(4, 4, this, listaAccionesMP, panelMediosPagos.getPrefWidth() , panelMediosPagos.getPrefHeight(), BotonBotoneraTextoComponent.class.getName());
            botoneraMediosPago.setBotonesFocusables(true);
            panelMediosPagos.getChildren().add(botoneraMediosPago);
        }
        catch (CargarPantallaException ex) {
            log.error("mostrarMediosPago() - Error cargando pantalla de medio de pago de devolución: " + ex.getMessage(), ex);
        }
	}

    @Override
    public void initializeFocus() {
    	btAceptar.requestFocus();
    }

    @FXML
    public void accionAceptar(ActionEvent event) {
        log.debug("accionAceptar()");
        if(codMedioPagoSeleccionado != null) {
	        this.getDatos().put(CLAVE_PARAMETRO_SALIDA_VUELTA, codMedioPagoSeleccionado);
	        getStage().close();
        }
        else {
        	VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar un medio de pago para el cambio."), getStage());
        }
    }

    @FXML
    public void accionCancelar() {
        log.debug("accionCancelar()");
        this.getDatos().remove(CLAVE_PARAMETRO_SALIDA_VUELTA);
        this.getDatos().put(CLAVE_CANCELADO, true);
        getStage().close();
    }

    @Override
    public void realizarAccion(BotonBotoneraComponent botonAccionado) {
        switch (botonAccionado.getClave()) {
            case "ACCION_SELECIONAR_MEDIO_PAGO":
                
                BotonBotoneraTextoComponent boton = (BotonBotoneraTextoComponent) botonAccionado;
                codMedioPagoSeleccionado = boton.getMedioPago().getCodMedioPago();
                lbMedioPago.setText(boton.getMedioPago().getDesMedioPago());
                break;
            default:
                break;
        }
    }
}
