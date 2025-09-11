package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.stage.Stage;

import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.tarjetas.ResponseGetTarjetaregaloRest;
import com.comerzzia.api.rest.client.fidelizados.tarjetas.TarjetasRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

@Component
@Scope("prototype")
public class ValidarNumeroTarjetaTask extends RestBackgroundTask<ResponseGetTarjetaregaloRest>{
	
	ConsultarFidelizadoRequestRest consultarFidelizadoRequest;

	public ValidarNumeroTarjetaTask(ConsultarFidelizadoRequestRest consultarFidelizadoRequest, Callback<ResponseGetTarjetaregaloRest> callback, Stage stage) {
		super(callback, stage);
		this.consultarFidelizadoRequest = consultarFidelizadoRequest;
	}

	@Override
	protected ResponseGetTarjetaregaloRest call() throws Exception {
		return TarjetasRest.validarTarjetaFidelizado(consultarFidelizadoRequest);
	}

}
