package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.rest.client.fidelizados.FidelizadoRequestRest;
import com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.IskaypetFidelizadoRest;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.EditarFidelizadoTask;

import javafx.stage.Stage;

/**
 * IER-383 Alta en TPV CZZ y envío a NAV
 */
@Component
@Scope("prototype")
@Primary
public class IskaypetEditarFidelizadoTask extends EditarFidelizadoTask {

	protected FidelizadoRequestRest fidelizadoRequest;
	
	public IskaypetEditarFidelizadoTask(FidelizadoRequestRest fidelizadoRequest, Callback<FidelizadoBean> callback, Stage stage) {
		super(fidelizadoRequest, callback, stage);
		this.fidelizadoRequest = fidelizadoRequest;
	}

	@Override
	protected FidelizadoBean call() throws Exception {
		
		return IskaypetFidelizadoRest.updateFidelizado(fidelizadoRequest);
	}

}
