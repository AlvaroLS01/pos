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
package com.comerzzia.pos.gui.mantenimientos.clientes;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.util.base.Estado;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ConsultaClienteController;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class BuscarClienteController extends ConsultaClienteController {

	private static Logger log = Logger.getLogger(BuscarClienteController.class);
	
    @Override
    public void initializeComponents() {
        try {
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = BotoneraComponent.cargarAccionesTablaSimple();
            listaAccionesAccionesTabla.add(0, new ConfiguracionBotonBean("iconos/view.png", null, null, "VER", "REALIZAR_ACCION"));
            listaAccionesAccionesTabla.add(1, new ConfiguracionBotonBean("iconos/add.png", null, null, "AÑADIR", "REALIZAR_ACCION"));
            listaAccionesAccionesTabla.add(2, new ConfiguracionBotonBean("iconos/edit.png", null, null, "EDITAR", "REALIZAR_ACCION"));
            listaAccionesAccionesTabla.add(3, new ConfiguracionBotonBean("iconos/delete.png", null, null, "ELIMINAR", "REALIZAR_ACCION"));

            log.debug("inicializarComponentes() - Configurando botonera");
            botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelMenuTabla.getChildren().add(botoneraAccionesTabla);

            //Se registra el evento para salir de la pantalla pulsando la tecla escape.
            registrarAccionCerrarVentanaEscape();

        }
        catch (CargarPantallaException ex) {
            log.error("inicializarComponentes() - Error creando botonera para la consulta de clientes. error : " + ex.getMessage(), ex);
            VentanaDialogoComponent.crearVentanaError(ex.getMessageI18N(), getStage());
        }
    }
	
	@Override
	public void registrarAccionCerrarVentanaEscape() {
		//No registramos el evento
	}

	@Override
	protected void tratarClienteSeleccionado() {
		try {
			int indice = tbClientes.getSelectionModel().getSelectedIndex();
			HashMap<String, Object> datos = new HashMap<>();
			datos.put(MantenimientoClienteController.INDICE_CLIENTE_SELECCIONADO, indice);
			datos.put(MantenimientoClienteController.LISTA_CLIENTES, clientesBuscados);
			datos.put(MantenimientoClienteController.MODO_EDICION, false);
			datos.put(MantenimientoClienteController.ESTADO_CLIENTE, Estado.SIN_MODIFICAR);
			getView().changeSubView(MantenimientoClienteView.class, datos);
		} catch (InitializeGuiException e) {
			log.error("tratarClienteSeleccionado() - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e);
		}
	}
	
    @Override
    public void realizarAccion(BotonBotoneraComponent botonAccionado) {
    	super.realizarAccion(botonAccionado);
        switch (botonAccionado.getClave()) {

        case "VER":
    		verCliente(false);
        	break;
        case "AÑADIR":
        	crearCliente();
        	break;
        case "EDITAR":
        	verCliente(true);
        	break;
        case "ELIMINAR":
        	eliminarCliente();
        	break;
        }
    }
    
    public void eliminarCliente(){
    	if(tbClientes.getSelectionModel().getSelectedItem()!=null){
    		if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se borrará el cliente seleccionado. ¿Está seguro?"), getStage())){
    			MantenimientoClienteTasks.executeEliminarTask(tbClientes.getSelectionModel().getSelectedItem().getCliente(), clientesBuscados, getStage(), null);
    			accionBuscar();
    		}
    	}
    }
	
	public void verCliente(boolean edicion){
    	try {
			int indice = tbClientes.getSelectionModel().getSelectedIndex();
			if(indice > -1){
				HashMap<String, Object> datos = new HashMap<>();
				datos.put(MantenimientoClienteController.INDICE_CLIENTE_SELECCIONADO, indice);
				datos.put(MantenimientoClienteController.LISTA_CLIENTES, clientesBuscados);
				datos.put(MantenimientoClienteController.MODO_EDICION, edicion);
				if(edicion){
					datos.put(MantenimientoClienteController.ESTADO_CLIENTE, Estado.MODIFICADO);
				}
				else{
					datos.put(MantenimientoClienteController.ESTADO_CLIENTE, Estado.SIN_MODIFICAR);
				}
				getView().changeSubView(MantenimientoClienteView.class, datos);
			}
		} catch (InitializeGuiException e) {
			log.error("verCliente() - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e);
		}
    }
	
	public void crearCliente(){
		try {
			HashMap<String, Object> datos = new HashMap<>();
			datos.put(MantenimientoClienteController.MODO_EDICION, true);
			datos.put(MantenimientoClienteController.ESTADO_CLIENTE, Estado.NUEVO);
			getView().changeSubView(MantenimientoClienteView.class, datos);

		} catch (InitializeGuiException e) {
			log.error("verCliente() - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e);
		}
	}
    
}