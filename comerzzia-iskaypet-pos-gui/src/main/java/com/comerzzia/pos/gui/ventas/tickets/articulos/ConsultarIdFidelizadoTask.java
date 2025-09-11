package com.comerzzia.pos.gui.ventas.tickets.articulos;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.stage.Stage;

import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

@Component
@Scope("prototype")
public class ConsultarIdFidelizadoTask extends RestBackgroundTask<Long>{
	
	ConsultarFidelizadoRequestRest fidelizadoRequest;
	
	public ConsultarIdFidelizadoTask(ConsultarFidelizadoRequestRest fidelizadoRequest, Callback<Long> callback, Stage stage) {
		super(callback, stage);
		this.fidelizadoRequest = fidelizadoRequest;
	}

	@Override
	protected Long call() throws Exception {
		return FidelizadosRest.getFidelizado(fidelizadoRequest).getIdFidelizado();
	}

}
