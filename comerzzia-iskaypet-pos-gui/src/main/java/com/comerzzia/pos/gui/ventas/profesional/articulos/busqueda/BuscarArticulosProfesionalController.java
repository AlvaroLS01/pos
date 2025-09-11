package com.comerzzia.pos.gui.ventas.profesional.articulos.busqueda;

import java.math.BigDecimal;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.gui.ventas.tickets.VentaProfesionalManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.LineaResultadoBusqGui;
import com.comerzzia.pos.services.articulos.tarifas.ArticulosTarifaService;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class BuscarArticulosProfesionalController extends BuscarArticulosController {
	
	private Logger log = Logger.getLogger(getClass());
	
	@FXML
	protected TextField tfPrecioIva;
	
	@Autowired
	private ArticulosTarifaService articulosTarifaService;
	
	@Override
	public void initializeForm() throws InitializeGuiException {
	    super.initializeForm();
	    tfPrecioIva.clear();
	    ticketManager = SpringContext.getBean(VentaProfesionalManager.class);
	}
	
	@Override
	public void refrescarDatosPantalla() {
	    super.refrescarDatosPantalla();
	    tfPrecioIva.clear();
	}
	
	@Override
	protected void lineaSeleccionadaChanged() {
		LineaResultadoBusqGui lineaSeleccionada = tbArticulos.getSelectionModel().getSelectedItem();
		limpiarPanelPromociones();
        if (lineaSeleccionada != null) {
            tfDetalleCodArticulo.setText(lineaSeleccionada.getCodArticulo());
            tfDetalleDescripcion.setText(lineaSeleccionada.getDescripcion());
            try {
            	ticketManager.inicializarTicket();
            	ticketManager.getTicket().setCliente(clienteBusqueda);
            	LineaTicket linea = ticketManager.nuevaLineaArticulo(lineaSeleccionada.getCodArticulo(), lineaSeleccionada.getDesglose1(), lineaSeleccionada.getDesglose2(), BigDecimal.ONE, null);
	            tfDetallePrecio.setText(FormatUtil.getInstance().formateaNumero(linea.getPrecioConDto(), 4));
	            tfPrecioIva.setText(FormatUtil.getInstance().formateaImporte(linea.getPrecioTotalConDto()));
            }
            catch (Exception e) {
            	log.error("lineaSeleccionadaChanged() - No se ha podido encontrar la tarifa para este artículo: " + e.getMessage());
                tfDetallePrecio.setText("-");
                tfPrecioIva.setText("-");
            }
            tfDetalleDesglose1.setText(lineaSeleccionada.getDesglose1());
            tfDetalleDesglose2.setText(lineaSeleccionada.getDesglose2());
            
            evaluarPromocion(lineaSeleccionada.getCodArticulo(), lineaSeleccionada.getDesglose1(), lineaSeleccionada.getDesglose2());
        }
	}
	
	@Override
	protected String getPvpPromocion(Promocion promocion, LineaTicket linea) {
		String pvpPromocion = null;
		if (promocionPresentaPrecio(promocion) || promocionPresentaDescuento(promocion)) {
			pvpPromocion = FormatUtil.getInstance().formateaNumero(linea.getPrecioConDto(), 4);
		}
	    return pvpPromocion;
	}
	
	@Override
	protected Label constuirLabelDetallePrecio() {
		return construirLabelDetallePromocion(I18N.getTexto("Precio") + ": ");
	}
	
	@Override
	protected FlowPane construirFlowPaneDetallePromocion(Promocion promocion, LineaTicket linea) {
	    FlowPane flowPane = super.construirFlowPaneDetallePromocion(promocion, linea);
	    
	    if(flowPane != null) {
		    String precioIva = linea.getPrecioTotalConDtoAsString();
			Label lbLabelPvp = construirLabelDetallePromocion(I18N.getTexto("Precio + Iva") + ": ");
			lbLabelPvp.setPrefWidth(80.0);
			Label lbPvp = construirLabelDetalleValorPromocion(precioIva);
			flowPane.getChildren().add(lbLabelPvp);
			flowPane.getChildren().add(lbPvp);
	    }
		
		return flowPane;
	}

}
