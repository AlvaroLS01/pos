package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.lineasorigen;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.comerzzia.cardoso.pos.services.ticket.lineas.CardosoLote;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CardosoLotesLineaOrigenController extends WindowController implements IContenedorBotonera{

	public static final String PARAMETRO_LOTES_DISPONIBLES = "lotesDisponibles";
	public static final String PARAMETRO_LOTE_SELECCION = "loteSeleccion";

	private Logger log = Logger.getLogger(getClass());

	@FXML
	protected TableView<CardosoLotesSerieOrigenGui> tbLotes;
	@FXML
	protected TableColumn tcIdLote;
	@FXML
	protected AnchorPane panelBotonera;
	@FXML
	protected Button btAceptar, btCerrar;

	protected List<CardosoLote> lotes;
	protected ObservableList<CardosoLotesSerieOrigenGui> lineas;

	/* ====================================================================================================================== */
	/* ================================================= IMPLEMENTED METHODS ================================================ */
	/* ====================================================================================================================== */
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1){
		log.debug("initialize() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		tbLotes.setPlaceholder(new Label(""));
		tcIdLote.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLotes", "tcIdLote", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcIdLote.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CardosoLotesSerieOrigenGui, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<CardosoLotesSerieOrigenGui, String> cdf){
				return cdf.getValue().getLote();
			}
		});
		tbLotes.setEditable(true);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException{
		/* =============================== EVENTS =============================== */
		try{	
			registraEventoTeclado(new EventHandler<KeyEvent>(){
				@Override
				public void handle(KeyEvent event){
					if(event.getCode() == KeyCode.ENTER){
						accionAceptar();
					}
				}
			}, KeyEvent.KEY_RELEASED);

			registrarAccionCerrarVentanaEscape();
		}
		catch(Exception e){
			String error = "Error al registrar los eventos de pantalla.";
			log.error("initializeComponents() - " + error + " - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error al registrar los eventos de pantalla."), e);
			throw new InitializeGuiException(error, e);
		}
		/* =============================== BUTTONS =============================== */
		try{
			List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
			listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
			listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));
			
			BotoneraComponent botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAcciones, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getName());
			panelBotonera.getChildren().add(botoneraAccionesTabla);
		}
		catch(Exception e){
			String error = "Error al iniciar los componentes de la botonera de la pantalla.";
			log.error("initializeComponents() - " + error + " - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error al iniciar los componentes de la botonera de la pantalla."), e);
			throw new InitializeGuiException(error, e);
		}
	}

	@Override
	public void initializeFocus(){
		btCerrar.requestFocus();
	}

	@Override
	public void initializeForm() throws InitializeGuiException{
		lotes = (List<CardosoLote>) getDatos().get(PARAMETRO_LOTES_DISPONIBLES);
		lineas = FXCollections.observableList(new ArrayList<CardosoLotesSerieOrigenGui>());
		if(lotes != null){
			for(CardosoLote lote : lotes){
				CardosoLotesSerieOrigenGui gui = new CardosoLotesSerieOrigenGui(lote.getId());
				lineas.add(gui);
			}
		}
		tbLotes.setItems(lineas);
		tbLotes.getSelectionModel().selectFirst();
	}

	/* ================================================================================================================= */
	/* ================================================= BUTTON ACTIONS ================================================ */
	/* ================================================================================================================= */
	
	/**
	 * PANEL : panelBotonera
	 */
	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado){
		switch (botonAccionado.getClave()){
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

	/**
	 * BUTTON : ANTERIOR_REGISTRO
	 */
	public void irAnteriorRegistro(){
		log.trace("irAnteriorRegistro() - Acción ejecutada");
		if(tbLotes.getItems() != null && tbLotes.getItems() != null){
			int indice = tbLotes.getSelectionModel().getSelectedIndex();
			if(indice > 0){
				tbLotes.getSelectionModel().select(indice - 1);
				tbLotes.scrollTo(indice - 1);
			}
		}
	}

	/**
	 * BUTTON : SIGUIENTE_REGISTRO
	 */
	public void irSiguienteRegistro(){
		log.trace("irSiguienteRegistro() - Acción ejecutada");
		if(tbLotes.getItems() != null && tbLotes.getItems() != null){
			int indice = tbLotes.getSelectionModel().getSelectedIndex();
			if(indice < tbLotes.getItems().size()){
				tbLotes.getSelectionModel().select(indice + 1);
				tbLotes.scrollTo(indice + 1);
			}
		}
	}
	
	/**
	 * BUTTON : btAceptar
	 */
	@FXML
	public void accionAceptar(){
		String sLoteSeleccionado = tbLotes.getItems().get(tbLotes.getSelectionModel().getSelectedIndex()).getLote().getValue();
		getDatos().put(PARAMETRO_LOTE_SELECCION, sLoteSeleccionado);
		getStage().close();
	}

	/**
	 * BUTTON : btCerrar
	 */
	@FXML
	public void accionCancelar(){
		getStage().close();
	}

}
