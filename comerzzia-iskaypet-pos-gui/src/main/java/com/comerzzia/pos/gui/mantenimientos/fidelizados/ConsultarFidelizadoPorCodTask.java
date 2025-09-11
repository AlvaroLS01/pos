package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import javafx.stage.Stage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

@Component
@Scope("prototype")
public class ConsultarFidelizadoPorCodTask extends RestBackgroundTask<Boolean>{

	ConsultarFidelizadoRequestRest consultarFidelizado;
	
	public ConsultarFidelizadoPorCodTask(ConsultarFidelizadoRequestRest consultarFidelizado, Callback<Boolean> callback, Stage stage) {
		super(callback, stage);
		this.consultarFidelizado = consultarFidelizado;
	}

	@Override
	protected Boolean call() throws Exception {
		return FidelizadosRest.getFidelizados(consultarFidelizado).size() != 0;
	}

}
