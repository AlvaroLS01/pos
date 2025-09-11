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


package com.comerzzia.pos.gui.ventas.tickets.pagos.cambioPagos;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class VentanaCambioController extends WindowController {

    public static final String PARAMETRO_ENTRADA_CAMBIO = "CAMBIO";
    public static final String PARAMETRO_ENTRADA_TOTAL = "TOTAL";
    public static final String PARAMETRO_ENTRADA_ENTREGADO = "PENDIENTE";
    public static final String PARAMETRO_ENTRADA_FORMA_PAGO_CAMBIO = "FORMA_PAGO_CAMBIO";
    public static final String PARAMETRO_CANTIDAD_TOTAL = "CANTIDAD_TOTAL";
    
    @FXML
    protected Label lbTotal, lbCambio, lbEntregado, lbFormaCambio, lbTotalCantidad;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @Override
    public void initializeComponents() {
    }

    @Override
    public void initializeForm() throws InitializeGuiException {
        lbCambio.setText((String)datos.get(PARAMETRO_ENTRADA_CAMBIO));
        lbFormaCambio.setText((String)datos.get(PARAMETRO_ENTRADA_FORMA_PAGO_CAMBIO));
        lbEntregado.setText((String)datos.get(PARAMETRO_ENTRADA_ENTREGADO));
        lbTotal.setText((String)datos.get(PARAMETRO_ENTRADA_TOTAL));
        BigDecimal cantidadArticulos = (BigDecimal)datos.get(PARAMETRO_CANTIDAD_TOTAL);
		lbTotalCantidad.setText(FormatUtil.getInstance().formateaNumero(cantidadArticulos, 0));
    }

    @Override
    public void initializeFocus() {
    }

    
    @Override
    public void accionCancelar() {
    	IVisor visor = Dispositivos.getInstance().getVisor();
    	visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
        this.getStage().close();
    }

}
