package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.rest.client.fidelizados.FidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class CrearFidelizadoTask extends RestBackgroundTask<FidelizadoBean>{

	FidelizadoRequestRest fidelizadoRequest;
	
	public CrearFidelizadoTask(FidelizadoRequestRest fidelizadoRequest, Callback<FidelizadoBean> callback, Stage stage) {
		super(callback, stage);
		this.fidelizadoRequest = fidelizadoRequest;
	}
	
	@Override
	protected FidelizadoBean call() throws Exception {				
		return FidelizadosRest.insertFidelizado(fidelizadoRequest);
	}

}
