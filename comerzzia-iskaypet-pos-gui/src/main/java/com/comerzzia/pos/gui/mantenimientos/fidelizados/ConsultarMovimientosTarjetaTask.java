package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.MovimientoBean;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class ConsultarMovimientosTarjetaTask extends RestBackgroundTask<List<MovimientoBean>>{
	
	ConsultarFidelizadoRequestRest consultarFidelizado;
	
	public ConsultarMovimientosTarjetaTask(ConsultarFidelizadoRequestRest consultarFidelizado, Callback<List<MovimientoBean>> callback, Stage stage) {
		super(callback, stage);
		this.consultarFidelizado = consultarFidelizado;
	}

	@Override
	protected List<MovimientoBean> call() throws Exception {
		return FidelizadosRest.getMovimientosTarjeta(consultarFidelizado);
	}

}
