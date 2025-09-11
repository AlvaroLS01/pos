package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.TarjetaBean;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class ConsultarTarjetasFidelizadoTask extends RestBackgroundTask<List<TarjetaBean>>{
	
	ConsultarFidelizadoRequestRest consultarFidelizado;
	
	public ConsultarTarjetasFidelizadoTask(ConsultarFidelizadoRequestRest consultarFidelizado, Callback<List<TarjetaBean>> callback, Stage stage) {
		super(callback, stage);
		this.consultarFidelizado = consultarFidelizado;
	}

	@Override
	protected List<TarjetaBean> call() throws Exception {
		return FidelizadosRest.getTarjetasFidelizado(consultarFidelizado);
	}

}
