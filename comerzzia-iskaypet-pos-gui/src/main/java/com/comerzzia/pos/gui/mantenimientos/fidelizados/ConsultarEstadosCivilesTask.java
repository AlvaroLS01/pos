package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.EstadoCivilBean;
import com.comerzzia.api.rest.client.estadosciviles.EstadosCivilesRest;
import com.comerzzia.api.rest.client.request.RequestRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class ConsultarEstadosCivilesTask extends RestBackgroundTask<List<EstadoCivilBean>>{

	RequestRest estadoCivilRequest;
	
	public ConsultarEstadosCivilesTask(RequestRest estadoCivilRequest, Callback<List<EstadoCivilBean>> callback, Stage stage) {
		super(callback, stage);
		this.estadoCivilRequest = estadoCivilRequest;
	}

	@Override
	protected List<EstadoCivilBean> call() throws Exception {
		return EstadosCivilesRest.getTodosEstadosCiviles(estadoCivilRequest).getEstadosCiviles();
	}

}
