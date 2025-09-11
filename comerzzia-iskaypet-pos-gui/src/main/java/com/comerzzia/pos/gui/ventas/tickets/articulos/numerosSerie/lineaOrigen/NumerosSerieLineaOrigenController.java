package com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.lineaOrigen;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class NumerosSerieLineaOrigenController extends WindowController implements IContenedorBotonera {
	
	public static final String PARAMETRO_NUMEROS_SERIE_DISPONIBLES = "numerosSerieDisponibles";
	public static final String PARAMETRO_ARTICULO = "descripcionArticulo";
	public static final String PARAMETRO_CANTIDAD = "cantidad";
	public static final String PARAMETRO_NUMEROS_SERIE_SELECCION = "numerosSerieSeleccion";
	
	private Logger log = Logger.getLogger(getClass());

	@FXML
	protected Label lbArticulo, lbCantidad;

	@FXML
	protected TableView<NumerosSerieOrigenGui> tbNumerosSerie;

	@SuppressWarnings("rawtypes")
	@FXML
	protected TableColumn tcNumeroSerie, tcCheck;

	@FXML
	protected AnchorPane panelBotonera;

	@FXML
	protected Button btCerrar;
	
	protected List<String> numerosSerie;
	protected String articulo;
	protected String cantidad;
	
	protected ObservableList<NumerosSerieOrigenGui> lineas;

	@SuppressWarnings("unchecked")
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tbNumerosSerie.setPlaceholder(new Label(""));
		tcNumeroSerie.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbNumerosSerie", "tcNumeroSerie", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcNumeroSerie.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<NumerosSerieOrigenGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<NumerosSerieOrigenGui, String> cdf) {
				return cdf.getValue().getNumeroSerie();
			}
		});
		tcCheck.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<NumerosSerieOrigenGui, Boolean>, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<NumerosSerieOrigenGui, Boolean> cdf) {
				return cdf.getValue().getLineaSelec();
			}
		});
		tcCheck.setCellFactory(CheckBoxTableCell.forTableColumn(tcCheck));
		tcCheck.getStyleClass().add(CellFactoryBuilder.ESTILO_ALINEACION_CEN);
		tcCheck.setEditable(true);
		tbNumerosSerie.setEditable(true);	
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		try {
			List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
			listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
			listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));

			BotoneraComponent botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAcciones, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getName());
			panelBotonera.getChildren().add(botoneraAccionesTabla);

			registrarAccionCerrarVentanaEscape();
		}
		catch (CargarPantallaException e) {
			log.error("initializeComponents() - Ha habido un error al cargar la botonera: " + e.getMessage());
		}
	}

	@Override
	public void initializeFocus() {
		btCerrar.requestFocus();
	}

	@SuppressWarnings("unchecked")
    @Override
	public void initializeForm() throws InitializeGuiException {
		numerosSerie = (List<String>) getDatos().get(PARAMETRO_NUMEROS_SERIE_DISPONIBLES);
		articulo = (String) getDatos().get(PARAMETRO_ARTICULO);
		cantidad = (String) getDatos().get(PARAMETRO_CANTIDAD);

		lineas = FXCollections.observableList(new ArrayList<NumerosSerieOrigenGui>());
		for (String numeroSerie : numerosSerie) {
			NumerosSerieOrigenGui gui = new NumerosSerieOrigenGui(numeroSerie);
			lineas.add(gui);
		}
		tbNumerosSerie.setItems(lineas);
		tbNumerosSerie.getSelectionModel().selectFirst();
		
		lbArticulo.setText(articulo);
		lbCantidad.setText(cantidad);
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		switch (botonAccionado.getClave()) {
			case "ANTERIOR_REGISTRO":
				irAnteriorRegistro();
				break;
			case "SIGUIENTE_REGISTRO":
				irSiguienteRegistro();
				break;
			default:
				break;
		}
	}

	public void irAnteriorRegistro() {
		log.trace("accionIrAnteriorRegistroTabla() - Acción ejecutada");
		if (tbNumerosSerie.getItems() != null && tbNumerosSerie.getItems() != null) {
			int indice = tbNumerosSerie.getSelectionModel().getSelectedIndex();
			if (indice > 0) {
				tbNumerosSerie.getSelectionModel().select(indice - 1);
				tbNumerosSerie.scrollTo(indice - 1);
			}
		}
	}

	public void irSiguienteRegistro() {
		log.trace("accionIrSiguienteRegistroTabla() - Acción ejecutada");
		if (tbNumerosSerie.getItems() != null && tbNumerosSerie.getItems() != null) {
			int indice = tbNumerosSerie.getSelectionModel().getSelectedIndex();
			if (indice < tbNumerosSerie.getItems().size()) {
				tbNumerosSerie.getSelectionModel().select(indice + 1);
				tbNumerosSerie.scrollTo(indice + 1);
			}
		}
	}

	public void accionAceptar() {
		List<String> numerosSerieSeleccionados = new ArrayList<String>();
		for(NumerosSerieOrigenGui lineaTicket: lineas) {
			if(lineaTicket.getLineaSelec().getValue()) {
				numerosSerieSeleccionados.add(lineaTicket.getNumeroSerie().getValue());
			}
        }
		
		if(numerosSerieSeleccionados.size() > new Integer(cantidad)) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se pueden asignar más números de serie de la cantidad a devolver."), getStage());
			return;
		}
		
		getDatos().put(PARAMETRO_NUMEROS_SERIE_SELECCION, numerosSerieSeleccionados);
		getStage().close();
	}

	public void accionCancelar() {
		getStage().close();
	}

}
