package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.busquedas;

import static com.comerzzia.iskaypet.pos.devices.proformas.seleccion.SeleccionProformaController.PARAM_PROFORMA_SELECCIONADA;
import static com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder.ESTILO_ALINEACION_DCHA;
import static com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder.ESTILO_ALINEACION_IZQ;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.LineaResultadoBusqGui;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

@Controller
@Primary
@SuppressWarnings({"unchecked", "rawtypes"})
public class IskaypetBuscarArticulosController extends BuscarArticulosController {

	private static final Logger log = Logger.getLogger(IskaypetBuscarArticulosController.class.getName());

	protected static final String NOMBRE_TABLA = "tbArticulos";

	protected static final String COLUMNA_EAN = "tcEAN";
	protected static final String COLUMNA_PVP = "tcPVP";

	@FXML
	protected TableColumn<IskaypetLineaResultadoBusqGui, String> tcEAN;
	@FXML
	protected TableColumn<IskaypetLineaResultadoBusqGui, BigDecimal> tcPVP;
	@FXML
	protected Button btAceptar;

	@FXML
	protected TextField tfEAN;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);
		tcEAN.setCellFactory(CellFactoryBuilder.createCellRendererCelda(NOMBRE_TABLA, COLUMNA_EAN, null, ESTILO_ALINEACION_IZQ));
		tcPVP.setCellFactory(CellFactoryBuilder.createCellRendererCelda(NOMBRE_TABLA, COLUMNA_PVP, 2, ESTILO_ALINEACION_DCHA));
		tcEAN.setCellValueFactory(cdf -> cdf.getValue().getEan());
		tcPVP.setCellValueFactory(cdf -> cdf.getValue().getPvpProperty());
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();
		this.tfEAN.setText("");

		mostrarBoton(btAceptar);
		boolean esTicketProforma = (boolean) getDatos().getOrDefault(PARAM_PROFORMA_SELECCIONADA, false);
		if (esTicketProforma) {
			// Si se ha pasado una proforma seleccionada, ocultamos el boton de aceptar
			ocultarBoton(btAceptar);
		}
	}

	@Override
	protected void evaluarPromocion(String sCodart, String sDesglose1, String sDesglose2) {

		try {

			limpiarPanelPromociones();

			ticketManager.inicializarTicket();
			TicketVenta ticketAux = (TicketVenta) ticketManager.getTicket();

			// Asignamos el cliente
			ticketAux.setCliente(clienteBusqueda);

			// Insertamos el articulo en el ticket con cantidad 50 para las promociones de NxM
			ticketManager.nuevaLineaArticulo(sCodart, sDesglose1, sDesglose2, new BigDecimal(50), null);
			ticketManager.recalcularConPromociones();
			muestraDatosPromocion(ticketAux, false);

			// Rellenamos los datos de fidelizado para que muestre las promociones de fidelizado
			FidelizacionBean datosFidelizacion = new FidelizacionBean();
			datosFidelizacion.setActiva(true);
			ticketAux.getCabecera().setDatosFidelizado(datosFidelizacion);
			ticketManager.recalcularConPromociones();
			muestraDatosPromocion(ticketAux, true);

			// Añadimos paneles vacíos para que la visualización sea siempre la misma, evitando diferentes interlineados entre los
			// paneles de las promociones
			completarPanel();
		}
		catch (PromocionesServiceException | DocumentoException e) {
			LineaTicketException ex = new LineaTicketException(I18N.getTexto("Error creando el ticket de venta."), e);
			log.error("Error creando ticket", ex);
		}
		catch (LineaTicketException e) {
			LineaTicketException ex = new LineaTicketException(I18N.getTexto("Error insertando línea."), e);
			log.error("Error insertando línea", ex);
		}

	}

	@Override
	protected void muestraDatosPromocion(TicketVenta ticket, boolean mostrandoPromoFidelizado) {
		LineaTicket linea = (LineaTicket) ticket.getLineas().get(0);
		Promocion promocion = null;
		if (linea.getPromociones() != null && !linea.getPromociones().isEmpty()) {
			promocion = ticket.getPromocion(linea.getPromociones().get(0).getIdPromocion()).getPromocion();
		}

		if (promocion != null) {
			String descripcionPromocion = StringUtils.isNotBlank(promocion.getTextoPromocion()) ? promocion.getTextoPromocion() : promocion.getDescripcion();
			String labelTipoPromocion = null;
			if (mostrandoPromoFidelizado && ticket.getCabecera().getDatosFidelizado() != null && promocion.getSoloFidelizacion()) {
				labelTipoPromocion = I18N.getTexto("DATOS PROMOCIÓN:");
			}
			else if (!mostrandoPromoFidelizado && !promocion.getSoloFidelizacion()) {
				labelTipoPromocion = I18N.getTexto("DATOS PROMOCIÓN:");
			}

			if (StringUtils.isNotBlank(labelTipoPromocion)) {
				Label lbTipoPromocion = this.construirLabelTipoPromocion(labelTipoPromocion);
				Label lbDescripcionPromocion = new Label(descripcionPromocion);
				lbDescripcionPromocion.setAlignment(Pos.CENTER_LEFT);
				FlowPane flowPane = this.constuirFlowPanePromocion();
				flowPane.getChildren().add(lbTipoPromocion);
				flowPane.getChildren().add(lbDescripcionPromocion);
				this.panelPromociones.getChildren().add(flowPane);
				FlowPane flowPaneDetalles = this.construirFlowPaneDetallePromocion(promocion, linea);
				if (flowPaneDetalles != null) {
					this.panelPromociones.getChildren().add(flowPaneDetalles);
				}
			}
		}
	}

	@Override
	protected FlowPane construirFlowPaneDetallePromocion(Promocion promocion, LineaTicket linea) {
		String pvpPromocion = this.getPvpPromocion(promocion, linea);
		String dtoPromocion = this.promocionPresentaDescuento(promocion) ? linea.getDescuento().toString() + "%" : null;
		if (!StringUtils.isNotBlank(pvpPromocion) && !StringUtils.isNotBlank(dtoPromocion)) {
			return null;
		}
		else {
			FlowPane flowPaneDetalles = this.constuirFlowPanePromocion();

			if (StringUtils.isNotBlank(dtoPromocion) && !BigDecimalUtil.isIgual(linea.getDescuento(), BigDecimalUtil.CIEN)) {
				Label lbLabelDto = this.construirLabelDetalleDto();
				Label lbDto = this.construirLabelDetalleValorPromocion(dtoPromocion);
				flowPaneDetalles.getChildren().add(lbLabelDto);
				flowPaneDetalles.getChildren().add(lbDto);
				Label lbVacio = this.construirLabelTipoPromocion("");
				flowPaneDetalles.getChildren().add(lbVacio);
			}

			if (StringUtils.isNotBlank(pvpPromocion) && !BigDecimalUtil.isIgualACero(getPvpPromocionAsNumber(promocion, linea))) {
				Label lbLabelPvp = this.constuirLabelDetallePrecio();
				Label lbPvp = this.construirLabelDetalleValorPromocion(pvpPromocion);
				flowPaneDetalles.getChildren().add(lbLabelPvp);
				flowPaneDetalles.getChildren().add(lbPvp);
			}

			if (flowPaneDetalles.getChildren().isEmpty()) {
				return null;
			}

			return flowPaneDetalles;
		}
	}

	@Override
	protected Label construirLabelTipoPromocion(String labelTipoPromocion) {
		Label lbTipoPromocion = new Label(labelTipoPromocion);
		lbTipoPromocion.getStyleClass().add("textoResaltado");
		lbTipoPromocion.setAlignment(Pos.CENTER_LEFT);
		return lbTipoPromocion;
	}

	protected BigDecimal getPvpPromocionAsNumber(Promocion promocion, LineaTicket linea) {
		BigDecimal pvpPromocion = null;
		if (this.promocionPresentaPrecio(promocion) || this.promocionPresentaDescuento(promocion)) {
			pvpPromocion = linea.getPrecioTotalConDto();
		}

		return pvpPromocion;
	}

	@Override
	protected Label constuirLabelDetallePrecio() {
		return this.construirLabelDetallePromocion(I18N.getTexto("PVP PROMOCIÓN") + ": ");
	}

	@Override
	protected Label construirLabelDetallePromocion(String texto) {
		Label label = new Label(texto);
		label.getStyleClass().add("textoResaltado");
		label.setAlignment(Pos.CENTER_LEFT);
		return label;
	}

	@Override
	protected void buscarArticulosSucceeded(List<ArticuloBuscarBean> articulosF) {

		if (articulosF.isEmpty()) {
			this.tbArticulos.setPlaceholder(new Label(I18N.getTexto("No se han encontrado resultados")));
		}

		List<LineaResultadoBusqGui> lineasRes = new ArrayList();
		for (ArticuloBuscarBean articulo : articulosF) {
			lineasRes.add(new IskaypetLineaResultadoBusqGui(articulo));
		}

		this.lineas.addAll(lineasRes);
		this.tbArticulos.getSelectionModel().select(0);
		this.tbArticulos.getFocusModel().focus(0);
	}

	@Override
	protected void lineaSeleccionadaChanged() {
		// Añadimos la customizacion con los nuevos campos a mostrar
		LineaResultadoBusqGui lineaSeleccionada = this.tbArticulos.getSelectionModel().getSelectedItem();
		if (lineaSeleccionada instanceof IskaypetLineaResultadoBusqGui) {
			IskaypetLineaResultadoBusqGui linea = (IskaypetLineaResultadoBusqGui) lineaSeleccionada;
			String ean = "";
			if (linea.getEan() != null) {
				ean = linea.getEan().getValue();
			}
			tfEAN.setText(StringUtils.isNotBlank(ean) ? ean : "");
		}

		super.lineaSeleccionadaChanged();
	}

	@Override
	protected void cargarBotonesStock(LineaResultadoBusqGui lineaSeleccionada) {
		ocultarBoton(btStocksStocks);
		ocultarBoton(btStocksPorModeloStocks);
		ocultarBoton(btStocksPorDesglose1Stocks);
		ocultarBoton(btStocksPorDesglose2Stocks);
		ocultarBoton(btStocksVentas);
		ocultarBoton(btStocksPorModeloVentas);
		ocultarBoton(btStocksPorDesglose1Ventas);
		ocultarBoton(btStocksPorDesglose2Ventas);
	}

	@Override
	public void refrescarDatosPantalla() {
		super.refrescarDatosPantalla();
		tfEAN.setText("");
	}

	@Override
	public void accionBuscar() {
		tfEAN.setText("");
		super.accionBuscar();
	}

	@Override
	public void aceptarArticuloBoton(ActionEvent event) {
		super.aceptarArticuloBoton(event);
	}
}
