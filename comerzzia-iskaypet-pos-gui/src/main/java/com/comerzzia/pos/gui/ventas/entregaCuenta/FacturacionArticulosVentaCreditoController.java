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
package com.comerzzia.pos.gui.ventas.entregaCuenta;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.gui.ventas.gestiontickets.GestionticketsController;
import com.comerzzia.pos.gui.ventas.tickets.VentaCreditoManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class FacturacionArticulosVentaCreditoController extends FacturacionArticulosController {
	
	@Autowired
	private Sesion sesion;

	@Override
	public void initializeManager() throws SesionInitException{
    	ticketManager = SpringContext.getBean(VentaCreditoManager.class);
        ticketManager.init();
    }
	
	@Override
    public void initializeForm() throws InitializeGuiException {
		super.initializeForm();
		lbTitulo.setText(I18N.getTexto("Venta Omnicanal"));
    }
	
	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
        List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row_delete.png", null, null, "ACCION_TABLA_BORRAR_REGISTRO", "REALIZAR_ACCION")); //"Delete"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row_edit.png", null, null, "ACCION_TABLA_EDITAR_REGISTRO", "REALIZAR_ACCION"));
        return listaAcciones;
    }
	
	public void abrirGestionTickets(HashMap<String,String> parametros){
        
        if(parametros.containsKey("idAccion")){
            String idAccion = parametros.get("idAccion");
            if(getDatos() == null){
                this.datos = new HashMap<String, Object>();
            }
            getDatos().put(GestionticketsController.PARAMETRO_ENTRADA_TIPO_DOC, Documentos.VENTA_CREDITO);
            POSApplication.getInstance().getMainView().showActionView(Long.parseLong(idAccion), getDatos());
        }
        else{
            log.error("No llegó la acción asociada al botón.");
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pudo ejecutar la acción seleccionada."), this.getStage());
        }
    }
	
    @Override
	public void comprobarPermisosUI() {
		botonera.comprobarPermisosOperaciones();
        try {
        	super.compruebaPermisos(PERMISO_BORRAR_LINEA);
        	botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_BORRAR_REGISTRO", false);
        }
        catch (SinPermisosException ex) {
        	botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_BORRAR_REGISTRO", true);
        }
        try {
        	super.compruebaPermisos(PERMISO_MODIFICAR_LINEA);
        	botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_EDITAR_REGISTRO", false);
        }
        catch (SinPermisosException ex) {
        	botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_EDITAR_REGISTRO", true);
        }
	}
	
}
