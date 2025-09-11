package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.core.TiendaBean;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class ConsultarTiendaFavoritaFidelizadoTask extends RestBackgroundTask<TiendaBean>{
	
	ConsultarFidelizadoRequestRest consultarFidelizadoRequest;

	public ConsultarTiendaFavoritaFidelizadoTask(ConsultarFidelizadoRequestRest consultarFidelizadoRequest, Callback<TiendaBean> callback, Stage stage) {
		super(callback, stage);
		this.consultarFidelizadoRequest = consultarFidelizadoRequest;
	}

	@Override
	protected TiendaBean call() throws Exception {
		return FidelizadosRest.getMiTiendaFavorita(consultarFidelizadoRequest);
	}

}
