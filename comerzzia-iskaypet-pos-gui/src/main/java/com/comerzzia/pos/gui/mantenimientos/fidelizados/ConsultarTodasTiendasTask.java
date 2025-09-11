package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.core.TiendaBean;
import com.comerzzia.api.rest.client.general.tiendas.ConsultarTiendaRequestRest;
import com.comerzzia.api.rest.client.general.tiendas.TiendasRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class ConsultarTodasTiendasTask extends RestBackgroundTask<List<TiendaBean>>{
	
	ConsultarTiendaRequestRest consultarTiendas;
	
	public ConsultarTodasTiendasTask(ConsultarTiendaRequestRest consultarTiendas, Callback<List<TiendaBean>> callback, Stage stage) {
		super(callback, stage);
		this.consultarTiendas = consultarTiendas;
	}

	@Override
	protected List<TiendaBean> call() throws Exception {
		return TiendasRest.getTodasTiendas(consultarTiendas);
	}

}
