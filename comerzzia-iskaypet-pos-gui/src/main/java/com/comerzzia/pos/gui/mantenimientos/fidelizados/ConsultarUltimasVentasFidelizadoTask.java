package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.sales.ArticuloAlbaranVentaBean;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class ConsultarUltimasVentasFidelizadoTask extends RestBackgroundTask<List<ArticuloAlbaranVentaBean>>{

	ConsultarFidelizadoRequestRest consultarFidelizado;
	
	public ConsultarUltimasVentasFidelizadoTask(ConsultarFidelizadoRequestRest consultarFidelizado, Callback<List<ArticuloAlbaranVentaBean>> callback, Stage stage) {
		super(callback, stage);
		this.consultarFidelizado = consultarFidelizado;
	}

	@Override
	protected List<ArticuloAlbaranVentaBean> call() throws Exception {
		return FidelizadosRest.getComprasFidelizado(consultarFidelizado).getCompras();
	}

}
