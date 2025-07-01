package com.comerzzia.pos.gui.sales.retail.items.serialnumbers.originalline;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.simple.ActionButtonSimpleComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.util.i18n.I18N;

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

@Component
@CzzScene
public class SerialNumberOriginalLineController extends SceneController implements ButtonsGroupController {
	
	public static final String PARAMETRO_NUMEROS_SERIE_DISPONIBLES = "numerosSerieDisponibles";
	public static final String PARAMETRO_ARTICULO = "descripcionArticulo";
	public static final String PARAMETRO_CANTIDAD = "cantidad";
	public static final String PARAMETRO_NUMEROS_SERIE_SELECCION = "numerosSerieSeleccion";
	
	private Logger log = Logger.getLogger(getClass());

	@FXML
	protected Label lbArticulo, lbCantidad;

	@FXML
	protected TableView<SerialNumberOriginalLineRow> tbNumerosSerie;

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
	
	protected ObservableList<SerialNumberOriginalLineRow> lineas;

	@SuppressWarnings("unchecked")
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tbNumerosSerie.setPlaceholder(new Label(""));
		tcNumeroSerie.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbNumerosSerie", "tcNumeroSerie", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcNumeroSerie.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SerialNumberOriginalLineRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<SerialNumberOriginalLineRow, String> cdf) {
				return cdf.getValue().getNumeroSerie();
			}
		});
		tcCheck.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SerialNumberOriginalLineRow, Boolean>, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<SerialNumberOriginalLineRow, Boolean> cdf) {
				return cdf.getValue().getLineaSelec();
			}
		});
		tcCheck.setCellFactory(CheckBoxTableCell.forTableColumn(tcCheck));
		tcCheck.getStyleClass().add(CellFactoryBuilder.CENTER_ALIGN_STYLE);
		tcCheck.setEditable(true);
		tbNumerosSerie.setEditable(true);	
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		try {
			List<ButtonConfigurationBean> listaAcciones = new ArrayList<>();
			listaAcciones.add(new ButtonConfigurationBean("icons/navigate_up.png", null, null, "ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
			listaAcciones.add(new ButtonConfigurationBean("icons/navigate_down.png", null, null, "SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));

			ButtonsGroupComponent botoneraAccionesTabla = new ButtonsGroupComponent(4, 1, this, listaAcciones, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(),
			        ActionButtonSimpleComponent.class.getName());
			panelBotonera.getChildren().add(botoneraAccionesTabla);

		}
		catch (LoadWindowException e) {
			log.error("initializeComponents() - Ha habido un error al cargar la botonera: " + e.getMessage());
		}
	}

	@Override
	public void initializeFocus() {
		btCerrar.requestFocus();
	}

	@SuppressWarnings("unchecked")
    @Override
	public void onSceneOpen() throws InitializeGuiException {
		numerosSerie = (List<String>) sceneData.get(PARAMETRO_NUMEROS_SERIE_DISPONIBLES);
		articulo = (String) sceneData.get(PARAMETRO_ARTICULO);
		cantidad = (String) sceneData.get(PARAMETRO_CANTIDAD);

		lineas = FXCollections.observableList(new ArrayList<SerialNumberOriginalLineRow>());
		for (String numeroSerie : numerosSerie) {
			SerialNumberOriginalLineRow gui = new SerialNumberOriginalLineRow(numeroSerie);
			lineas.add(gui);
		}
		tbNumerosSerie.setItems(lineas);
		tbNumerosSerie.getSelectionModel().selectFirst();
		
		lbArticulo.setText(articulo);
		lbCantidad.setText(cantidad);
	}

	@Override
	public void executeAction(ActionButtonComponent botonAccionado) {
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

		for(SerialNumberOriginalLineRow lineaTicket: lineas) {
			if(lineaTicket.getLineaSelec().getValue()) {
				numerosSerieSeleccionados.add(lineaTicket.getNumeroSerie().getValue());
			}
        }
		
		if(numerosSerieSeleccionados.size() > new Integer(cantidad)) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se pueden asignar más números de serie de la cantidad a devolver."));
			return;
		}
		
		closeSuccess(numerosSerieSeleccionados);
	}

	public void accionCancelar() {
		closeCancel();
	}

}
