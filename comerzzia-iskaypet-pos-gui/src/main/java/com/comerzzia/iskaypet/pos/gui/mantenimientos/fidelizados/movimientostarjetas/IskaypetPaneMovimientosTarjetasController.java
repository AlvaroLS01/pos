package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.movimientostarjetas;

import com.comerzzia.api.model.loyalty.MovimientoBean;
import com.comerzzia.api.model.loyalty.TarjetaBean;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.IskaypetConsultarMovimientosTarjetaTask;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.MovimientoGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.movimientostarjetas.PaneMovimientosTarjetasController;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Primary
@Component
public class IskaypetPaneMovimientosTarjetasController extends PaneMovimientosTarjetasController {

    @Autowired
    private Sesion sesion;

    @Override
    public void cargarMovimientos() {
        String apiKey = variablesService.getVariableAsString("WEBSERVICES.APIKEY");
        String uidActividad = sesion.getAplicacion().getUidActividad();
        TarjetaBean tarjetaSeleccionada = cbNumeroTarjeta.getValue();
        if (tarjetaSeleccionada != null) {
            tfTipoTarjeta.setText(tarjetaSeleccionada.getDesTipoTarj());
            tfSaldo.setText(FormatUtil.getInstance().formateaNumero(
                    BigDecimal.valueOf(tarjetaSeleccionada.getSaldo() + tarjetaSeleccionada.getSaldoProvisional()), 2));

            ConsultarFidelizadoRequestRest consultaMovimientos = new ConsultarFidelizadoRequestRest(apiKey, uidActividad);
            consultaMovimientos.setIdFidelizado(String.valueOf(fidelizado.getIdFidelizado()));
            consultaMovimientos.setIdTarjeta(String.valueOf(tarjetaSeleccionada.getIdTarjeta()));
            consultaMovimientos.setUltimosMovimientos(20);

            IskaypetConsultarMovimientosTarjetaTask consultarMovimientosTask = SpringContext.getBean(
                    IskaypetConsultarMovimientosTarjetaTask.class, consultaMovimientos,
                    new RestBackgroundTask.FailedCallback<List<MovimientoBean>>() {
                public void succeeded(List<MovimientoBean> result) {
                    movimientosTarjeta.clear();
                    BigDecimal totalEntrada = BigDecimal.ZERO;
                    BigDecimal totalSalida = BigDecimal.ZERO;

                    for (MovimientoBean mov : result) {
                        MovimientoGui movGui = new MovimientoGui(mov);
                        if (movGui.getEntrada() != null) {
                            totalEntrada = totalEntrada.add(movGui.getEntrada());
                        }
                        if (movGui.getSalida() != null) {
                            totalSalida = totalSalida.add(movGui.getSalida());
                        }
                        movimientosTarjeta.add(movGui);
                    }

                    tableMovimientos.setItems(movimientosTarjeta);
                    lbTotalEntrada.setText(FormatUtil.getInstance().formateaNumero(totalEntrada, 2));
                    lbTotalSalida.setText(FormatUtil.getInstance().formateaNumero(totalSalida, 2));
                }

                public void failed(Throwable throwable) {
                    getTabParentController().getApplication().getMainView().close();
                }
            }, getTabParentController().getStage());
            consultarMovimientosTask.start();
        }
    }
}
