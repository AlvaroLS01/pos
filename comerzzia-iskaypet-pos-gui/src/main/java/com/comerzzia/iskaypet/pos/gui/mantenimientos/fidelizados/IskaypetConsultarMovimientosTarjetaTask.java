package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados;

import com.comerzzia.api.model.loyalty.MovimientoBean;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.IskaypetFidelizadoRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class IskaypetConsultarMovimientosTarjetaTask extends RestBackgroundTask<List<MovimientoBean>> {

    private final ConsultarFidelizadoRequestRest consultarFidelizado;

    public IskaypetConsultarMovimientosTarjetaTask(ConsultarFidelizadoRequestRest consultarFidelizado, RestBackgroundTask.Callback<List<MovimientoBean>> callback, Stage stage) {
        super(callback, stage);
        this.consultarFidelizado = consultarFidelizado;
    }

    @Override
    protected List<MovimientoBean> call() throws Exception {
        return IskaypetFidelizadoRest.getMovimientosTarjeta(consultarFidelizado);
    }

}
