package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.ColectivoBean;
import com.comerzzia.api.rest.client.colectivos.ColectivosRequestRest;
import com.comerzzia.api.rest.client.colectivos.ColectivosRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class ConsultarTodosColectivosTask extends RestBackgroundTask<List<ColectivoBean>>{

	ColectivosRequestRest consultarColectivos;
	
	public ConsultarTodosColectivosTask(ColectivosRequestRest consultarColectivos, Callback<List<ColectivoBean>> callback, Stage stage) {
		super(callback, stage);
		this.consultarColectivos = consultarColectivos;
	}

	@Override
	protected List<ColectivoBean> call() throws Exception {
		return ColectivosRest.getTodosColectivos(consultarColectivos).getColectivos();
	}
	
}
