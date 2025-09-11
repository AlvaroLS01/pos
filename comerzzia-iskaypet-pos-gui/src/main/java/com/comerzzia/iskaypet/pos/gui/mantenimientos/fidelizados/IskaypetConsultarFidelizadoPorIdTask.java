package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.rest.client.fidelizados.FidelizadoRequestRest;
import com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.IskaypetFidelizadoRest;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ConsultarFidelizadoPorIdTask;

import javafx.stage.Stage;

@Component
@Primary
@Scope("prototype")
public class IskaypetConsultarFidelizadoPorIdTask extends ConsultarFidelizadoPorIdTask {
	
	public IskaypetConsultarFidelizadoPorIdTask(FidelizadoRequestRest fidelizadoRequest,
			Callback<FidelizadoBean> callback, Stage stage) {
		super(fidelizadoRequest, callback, stage);
	}

	@Override
	protected FidelizadoBean call() throws Exception {
		return IskaypetFidelizadoRest.getFidelizadoPorId(fidelizadoRequest);
	}

}

