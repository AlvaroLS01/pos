package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.previsualizacion;

import static com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.detalle.IskaypetDetalleGestionticketsController.generarLineasAgrupadas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.persistence.ticket.lineas.TextoPromocion;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.util.date.DateUtils;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.ventas.devoluciones.articulos.LineaTicketAbonoGui;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * GAP12 - ISK-8 GESTIÓN DE LOTES GAP 12.1 ISK-253
 */
@Component
@SuppressWarnings("unchecked")
public class PrevisualizacionTicketController extends WindowController implements Initializable {

	protected Logger log = Logger.getLogger(getClass());

	public static final String CLAVE_TICKET_MANAGER = "ticketManager";
	public static String ACCION_CANCELAR = "ACCION_CANCELAR";

	protected ObservableList<LineaTicketGui> lineas;

	@FXML
	protected TableView<LineaTicketGui> tbLineas;
	@FXML
	protected TableColumn<LineaTicketGui, String> tcLineasArticulo;
	@FXML
	protected TableColumn<LineaTicketGui, String> tcLineasDescripcion;
	@FXML
	protected TableColumn<LineaTicketGui, BigDecimal> tcLineasCantidad;
	@FXML
	protected TableColumn<LineaTicketGui, BigDecimal> tcLineasPVP;
	@FXML
	protected TableColumn<LineaTicketGui, BigDecimal> tcLineasDto;
	@FXML
	protected TableColumn<LineaTicketGui, BigDecimal> tcLineasImporte;
	@FXML
	protected Label lbTotalDetalle;

	@FXML
	protected Button btAceptar;
	@FXML
	protected Button btCancelar;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tbLineas.setPlaceholder(new Label(""));
		lineas = FXCollections.observableList(new ArrayList<>());
		tcLineasArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasArticulo", null, "leftCol"));
		tcLineasDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasDescripcion", null, "leftCol"));
		tcLineasPVP.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcLineasPVP", "rightCol"));
		tcLineasDto.setCellFactory(CellFactoryBuilder.createCellRendererCeldaPorcentaje("tbLineas", "tcLineasDto", 2, "rightCol"));
		tcLineasImporte.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcLineasImporte", "rightCol"));
		tcLineasCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasCantidad", 3, "rightCol"));
		tbLineas.setItems(lineas);
		tcLineasArticulo.setCellValueFactory(cdf -> cdf.getValue().getArtProperty());
		tcLineasDescripcion.setCellValueFactory(cdf -> cdf.getValue().getDescripcionProperty());
		tcLineasCantidad.setCellValueFactory(cdf -> cdf.getValue().getCantidadProperty());
		tcLineasPVP.setCellValueFactory(cdf -> cdf.getValue().getPvpProperty());
		tcLineasDto.setCellValueFactory(cdf -> cdf.getValue().getDescuentoProperty());
		tcLineasImporte.setCellValueFactory(cdf -> cdf.getValue().getImporteTotalFinalProperty());

		// Color en la parte de promos
		tcLineasDescripcion.setCellFactory(param -> new TableCell<LineaTicketGui, String>(){

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
					return;
				}

				TextFlow textFlow = new TextFlow();
				String[] lines = item.split("\n");
				for (int i = 0; i < lines.length; i++) {
					Text text = new Text(lines[i]);
					Color color = null;
					if (i == 0) {
						color = getTableView().getSelectionModel().isSelected(getIndex()) ? Color.WHITE : Color.BLACK;
					}
					else {
						color = getTableView().getSelectionModel().isSelected(getIndex()) ? Color.YELLOW : Color.RED;
						text.setStyle("-fx-font-weight: bold");
					}
					text.setFill(color);
					textFlow.getChildren().add(text);
					if (i < lines.length - 1) {
						textFlow.getChildren().add(new Text("\n"));
					}
				}
				setPrefHeight(textFlow.prefHeight(-1) + 2);
				setGraphic(textFlow);
			}

		});

		tbLineas.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
			if (newIndex.intValue() >= 0) {
				tcLineasDescripcion.getTableView().refresh();
			}
		});

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {

	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		TicketManager ticketManager = (TicketManager) getDatos().get(CLAVE_TICKET_MANAGER);
		if (ticketManager != null) {
			cargarDatosPantalla(ticketManager);
		}

	}

	@Override
	public void initializeFocus() {

	}

	@FXML
	public void aceptar() {
		getDatos().put(ACCION_CANCELAR, false);
		getStage().close();
	}

	@FXML
	public void cancelar() {
		getDatos().put(ACCION_CANCELAR, true);
		getStage().close();
	}
	
	public void cargarDatosPantalla(TicketManager ticketManager) {
		log.debug("refrescarDatosPantalla() - Refrescando datos de pantalla...");

		lbTotalDetalle.setText(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) + " €");

		List<LineaTicketAbonoGui> lineasAgrupadas = new ArrayList<>();

		List<IskaypetLineaTicket> lstLineaTicket = generarLineasAgrupadas(ticketManager.getTicket().getLineas(), true);

		for (IskaypetLineaTicket lineaTicket : lstLineaTicket) {
			LineaTicketAbonoGui lineaAgrupada = new LineaTicketAbonoGui(lineaTicket);

			if (lineaTicket.getLote() != null) {
				String descripcionActual = lineaAgrupada.getDescripcionProperty().get();
				String lote = I18N.getTexto("Lote: {0}", lineaTicket.getLote().getLote());
				String fechaCaducidadLote = I18N.getTexto("Fecha Caducidad: ") + DateUtils.formatDate(lineaTicket.getLote().getFechaCaducidad(), "dd/MM/yyyy");
				String nuevaDescripcion = String.format("%s \n %s", descripcionActual, String.join(", ", lote, fechaCaducidadLote));
				lineaAgrupada.setDescripcion(nuevaDescripcion);
			}

			if (lineaTicket.getInyectable() != null) {
				String descripcionActual = lineaAgrupada.getDescripcionProperty().get();
				String cantidadSuministrada = I18N.getTexto("Cantidad suministrada: {0}", lineaTicket.getInyectable().getCantidadSuministrada());
				String unidadMedidaSuministrada = I18N.getTexto( lineaTicket.getInyectable().getUnidadMedidaSuministrada());
				String nuevaDescripcion = String.format("%s \n %s", descripcionActual, String.join(" ", cantidadSuministrada, unidadMedidaSuministrada));
				lineaAgrupada.setDescripcion(nuevaDescripcion);
			}

			if (lineaTicket.getArticlePoints() != null && lineaTicket.getArticlePoints().getReedem().equals("1")) {
				String descripcionActual = lineaAgrupada.getDescripcionProperty().get();
				String nuevaDescripcion = String.format("%s \n %s", descripcionActual, I18N.getTexto("** Promo: Articulo a canjear por: {0} puntos.", lineaTicket.getArticlePoints().getPoints()));
				lineaAgrupada.setDescripcion(nuevaDescripcion);
			} else if (lineaTicket.getPegatinaPromocional() != null) {
				String descripcionActual = lineaAgrupada.getDescripcionProperty().get();
				String nuevaDescripcion = String.format("%s \n %s", descripcionActual, I18N.getTexto("** Promo demarca"));
				lineaAgrupada.setDescripcion(nuevaDescripcion);
			} else if (lineaTicket.getPromociones() != null && !lineaTicket.getPromociones().isEmpty()) {
				List<PromocionLineaTicket> lstPromocionesLineaTicket = lineaTicket.getPromociones().stream()
						.filter(el -> BigDecimalUtil.isMayorACero(el.getCantidadPromocionAplicada()) || BigDecimalUtil.isMayorACero(el.getImporteTotalDtoMenosMargen()))
						.collect(Collectors.toList());

				for (PromocionLineaTicket promocion : lstPromocionesLineaTicket) {
					if (promocion != null) {
						String textoPromocion = lineaTicket.getTextosPromociones().stream().filter(el -> el.getIdPromocion().equals(promocion.getIdPromocion())).findFirst()
								.map(TextoPromocion::getTexto).orElse(null);
						if (StringUtils.isNotBlank(textoPromocion)) {
							String descripcionActual = lineaAgrupada.getDescripcionProperty().get();
							String nuevaDescripcion = String.format("%s \n %s", descripcionActual, I18N.getTexto("** Promo: {0}", textoPromocion));
							if (promocion.getAcceso().equalsIgnoreCase("CUPON")) {
								nuevaDescripcion = String.join(". ", nuevaDescripcion, I18N.getTexto("Código: {0}", promocion.getCodAcceso()));
							}
							lineaAgrupada.setDescripcion(nuevaDescripcion);
						}
					}
				}
			}
			lineasAgrupadas.add(lineaAgrupada);
		}

		BigDecimal total = lstLineaTicket.stream().map(IskaypetLineaTicket::getImporteTotalConDto).reduce(BigDecimal.ZERO, BigDecimal::add);

		Collections.sort(lineasAgrupadas, Comparator.comparing(LineaTicketAbonoGui::getArticulo).reversed().thenComparing((l1, l2) -> {
			BigDecimal porcentajeDto1 = l1.getDescuentoProperty().getValue();
			BigDecimal porcentajeDto2 = l2.getDescuentoProperty().getValue();
			return porcentajeDto2.compareTo(porcentajeDto1);
		}));

		// Actualizamos el total del ticket
		lbTotalDetalle.setText(total.setScale(2, RoundingMode.HALF_UP) + " €");

		// Actualizamos la lista de líneas con las líneas agrupadas y no agrupadas
		lineas.clear();
		lineas.addAll(lineasAgrupadas);

		Collections.reverse(lineas);
		tbLineas.getSelectionModel().selectFirst();
		tbLineas.scrollTo(0);
	}

}
