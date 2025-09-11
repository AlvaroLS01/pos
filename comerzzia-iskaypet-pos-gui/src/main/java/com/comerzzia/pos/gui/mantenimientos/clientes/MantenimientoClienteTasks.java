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

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.comerzzia.core.util.base.Estado;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ClienteGui;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.services.clientes.ClienteConstraintViolationException;
import com.comerzzia.pos.services.clientes.ClientesService;
import com.comerzzia.pos.services.clientes.ClientesServiceException;
import com.comerzzia.pos.util.config.SpringContext;

public class MantenimientoClienteTasks {

	static class EliminarClientesTask extends BackgroundTask<Void>{
		private ClienteBean cliente;
		private Stage stage;
		private ObservableList<ClienteGui> list;
		private Callback<Boolean, Void> callback;

		public EliminarClientesTask(ClienteBean cliente, ObservableList<ClienteGui> list, Stage stage, Callback<Boolean, Void> callback) {
			this.cliente = cliente;
			this.list = list;
			this.stage = stage;
			this.callback = callback;
		}
		
		@Override
		protected Void call() throws Exception {
			SpringContext.getBean(ClientesService.class).eliminar(cliente.getCodCliente());
			return null;
		}
		
		@Override
		protected void failed() {
			Throwable e =  getException();
			if(e instanceof ClientesServiceException) {
				VentanaDialogoComponent.crearVentanaError(stage, e);
			}else if(e instanceof ClienteConstraintViolationException) {
				VentanaDialogoComponent.crearVentanaError(e.getMessage(), stage);
			}
			super.failed();
			if(callback != null)
				callback.call(false);
		}
		
		@Override
		protected void succeeded() {
			cliente.setEstadoBean(Estado.BORRADO);
			list.remove(cliente);
			super.succeeded();
			if(callback != null)
				callback.call(true);
		}
		
	}
	
	public static void executeEliminarTask(ClienteBean cliente, ObservableList<ClienteGui> list, Stage stage, Callback<Boolean, Void> callback){
		new EliminarClientesTask(cliente, list, stage, callback).start();
	}
	
}
