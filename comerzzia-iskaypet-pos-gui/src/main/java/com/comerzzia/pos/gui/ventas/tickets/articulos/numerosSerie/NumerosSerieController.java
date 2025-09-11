package com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.botonera.KeyCodeCombinationParser;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.lineaOrigen.NumerosSerieLineaOrigenController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.lineaOrigen.NumerosSerieLineaOrigenView;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class NumerosSerieController extends WindowController implements IContenedorBotonera {

	private Logger log = Logger.getLogger(getClass());

	public static final String PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN = "numerosSerie";
	public static final String PARAMETRO_LINEA_DOCUMENTO_ACTIVO = "linea";
	public static final String PARAMETRO_LISTA_NUMEROS_SERIES_ASIGNADOS = "listaNumerosSerie";

	@FXML
	protected Label lbArticulo, lbCantidad, lbPendiente;

	@FXML
	protected TextField tfNumeroSerie;

	@FXML
	protected ListView<String> lvNumerosSerie;

	@FXML
	protected TecladoNumerico tecladoNumerico;

	@FXML
	protected AnchorPane panelBotonera;

	@FXML
	protected Button btCerrar, btConsultarDocOrigen;

	protected LineaNumerosSerie linea;

	protected BigDecimal cantidadLinea;

	protected List<String> numerosSerieDocumentoOrigen;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		try {
			initTecladoNumerico(tecladoNumerico);

			List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
			listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
			listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));
			listaAcciones.add(new ConfiguracionBotonBean("iconos/cancelar.png", null, null, "BORRAR_REGISTRO", "REALIZAR_ACCION"));
			listaAcciones.add(new ConfiguracionBotonBean("iconos/delete.png", null, null, "BORRAR_TODOS", "REALIZAR_ACCION"));

			BotoneraComponent botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAcciones, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getName());
			panelBotonera.getChildren().add(botoneraAccionesTabla);

			registrarEventosTeclado();
			registrarAccionCerrarVentanaEscape();
		}
		catch (Exception e) {
			log.error("initialize() - Ha habido un error al inicializar la pantalla: " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(getStage(), "Ha habido un error al inicializar la pantalla", e);
		}
	}

	protected void registrarEventosTeclado() {
		log.trace("registrarEventosTeclado()");

        KeyCodeCombinationParser parser = new KeyCodeCombinationParser();
        final KeyCodeCombination keyCodeEliminarGeneral = parser.parse(AppConfig.keyCodesInfo.getKeyCodeTablaEliminar(), new KeyCodeCombination(KeyCode.DELETE, KeyCombination.CONTROL_DOWN));
        
        EventHandler<KeyEvent> evhGeneral = new EventHandler<KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                if (keyCodeEliminarGeneral.match(event)) {
                	borrarRegistro();
                }
            }
        };

        registraEventoTeclado(evhGeneral, KeyEvent.KEY_RELEASED);
    }

	@Override
	public void initializeFocus() {
		tfNumeroSerie.requestFocus();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		linea = convertLineaNumerosSerie(getDatos().get(PARAMETRO_LINEA_DOCUMENTO_ACTIVO));

		numerosSerieDocumentoOrigen = null;
		numerosSerieDocumentoOrigen = (List<String>) getDatos().get(PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN);

		btConsultarDocOrigen.setVisible(numerosSerieDocumentoOrigen != null && numerosSerieDocumentoOrigen.size() > 0);

		if (linea != null) {
			// Redondeamos la cantidad por si tuviese decimales, que no debería
			cantidadLinea = linea.getCantidad().setScale(0, RoundingMode.HALF_UP).abs();

			List<String> numerosOriginales = linea.getNumerosSerie();
			if (numerosOriginales == null) {
				lvNumerosSerie.setItems(FXCollections.observableArrayList(new ArrayList<String>()));
			}
			else {
				lvNumerosSerie.setItems(FXCollections.observableArrayList(numerosOriginales));
			}
			tfNumeroSerie.clear();

			lbArticulo.setText(linea.getDesArticulo());
			lbCantidad.setText(FormatUtil.getInstance().formateaNumero(cantidadLinea));
			if (linea.getNumerosSerie() != null) {
				lbPendiente.setText(FormatUtil.getInstance().formateaNumero(cantidadLinea.subtract(new BigDecimal((linea.getNumerosSerie().size())))));
			}
			else {
				lbPendiente.setText(FormatUtil.getInstance().formateaNumero(cantidadLinea));
			}

			if (lvNumerosSerie.getItems().size() > 0) {
				lvNumerosSerie.getSelectionModel().selectFirst();
			}
			
			if(numerosSerieDocumentoOrigen != null && numerosSerieDocumentoOrigen.size() > 0) {
				registrarEventoTecladoDocOrigen();
			}
		}
		else {
			throw new InitializeGuiException("No se ha encontrado línea para editar");
		}
	}

	protected LineaNumerosSerie convertLineaNumerosSerie(final Object o) {
		if (o instanceof LineaTicket) {
			return new LineaNumerosSerie() {
				@Override
				public BigDecimal getCantidad() {
					return ((LineaTicket) o).getCantidad();
				}

				@Override
				public String getDesArticulo() {
					return ((LineaTicket) o).getDesArticulo();
				}

				@Override
				public List<String> getNumerosSerie() {
					return ((LineaTicket) o).getNumerosSerie();
				}
			};
		} else {
			return (LineaNumerosSerie) o;
		}
	}

	protected void registrarEventoTecladoDocOrigen() {
		KeyCodeCombinationParser parser = new KeyCodeCombinationParser();
        final KeyCodeCombination keyCodeEliminarGeneral = parser.parse(AppConfig.keyCodesInfo.getKeyCodeTablaEliminar(), new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));
        
        EventHandler<KeyEvent> evhGeneral = new EventHandler<KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                if (keyCodeEliminarGeneral.match(event)) {
                	numerosSerieDisponibles();
                }
            }
        };

        registraEventoTeclado(evhGeneral, KeyEvent.KEY_RELEASED);
    }

	public void addNumeroSerie() {
		String numSerie = tfNumeroSerie.getText().trim().toUpperCase();
		if (StringUtils.isNotBlank(numSerie)) {
			if (!hayRepetidos(numSerie)) {
				if (quedanNumerosPorAsignar()) {
					if (numeroSerieValido(numSerie)) {
						lvNumerosSerie.getItems().add(numSerie);
						lvNumerosSerie.getSelectionModel().selectLast();
						tfNumeroSerie.clear();

						lbPendiente.setText(FormatUtil.getInstance().formateaNumero(cantidadLinea.subtract(new BigDecimal(lvNumerosSerie.getItems().size()))));

						if (!quedanNumerosPorAsignar()) {
							btCerrar.requestFocus();
						}
						else {
							tfNumeroSerie.requestFocus();
						}
					}
					else {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El número de serie no es válido, introduzca uno de la lista."), getStage());
						tfNumeroSerie.clear();
					}
				}
				else {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Imposible insertar el número de serie, ya están todos asignados."), getStage());
					btCerrar.requestFocus();
				}
			}
			else {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El número de serie debe ser único."), getStage());
				tfNumeroSerie.clear();
			}
		}
		else {
			if (quedanNumerosPorAsignar()) {
				tfNumeroSerie.requestFocus();
			}
			else {
				btCerrar.requestFocus();
			}
		}
	}

	protected boolean quedanNumerosPorAsignar() {
		return lvNumerosSerie.getItems().size() < cantidadLinea.intValue();
	}

	protected boolean numeroSerieValido(String numSerie) {
		boolean res = true;

		if (numerosSerieDocumentoOrigen != null && numerosSerieDocumentoOrigen.size() > 0) {
			res = numerosSerieDocumentoOrigen.contains(numSerie);
		}
		return res;
	}

	protected boolean hayRepetidos(String numSerie) {
		boolean res = false;
		for (String num : lvNumerosSerie.getItems()) {
			if (num.equals(numSerie)) {
				res = true;
				break;
			}
		}
		return res;
	}

	@Override
	public void accionCancelar() {
		if (quedanNumerosPorAsignar()) {
			if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Hay números de serie sin asignar, la pantalla se cerrará, ¿Está seguro?"), getStage())) {
				return;
			}
		}

		List<String> numerosSerieOut = new LinkedList<String>();
		numerosSerieOut.addAll(lvNumerosSerie.getItems());
		getDatos().put(PARAMETRO_LISTA_NUMEROS_SERIES_ASIGNADOS, numerosSerieOut);
		getStage().close();
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
			case "BORRAR_REGISTRO":
				borrarRegistro();
				break;
			case "BORRAR_TODOS":
				borrarTodos();
				break;
			default:
				break;
		}
	}

	protected void borrarTodos() {
		if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Los números asociados a la línea de venta serán borrados, ¿Está seguro?"), getStage())) {
			lvNumerosSerie.getItems().clear();
			lbPendiente.setText(FormatUtil.getInstance().formateaNumero(cantidadLinea.subtract(new BigDecimal(lvNumerosSerie.getItems().size()))));
			tfNumeroSerie.requestFocus();
		}
	}

	protected void borrarRegistro() {
		if (lvNumerosSerie.getSelectionModel().getSelectedItem() != null
		        && VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("El número de serie indicado será borrado, ¿Está seguro?"), getStage())) {
			int index = lvNumerosSerie.getSelectionModel().getSelectedIndex();
			lvNumerosSerie.getItems().remove(index);
			
			if(index > lvNumerosSerie.getItems().size()-1) {
				index = lvNumerosSerie.getItems().size()-1;
			}
			lvNumerosSerie.getSelectionModel().select(index);
			lbPendiente.setText(FormatUtil.getInstance().formateaNumero(cantidadLinea.subtract(new BigDecimal(lvNumerosSerie.getItems().size()))));
		}
	}

	public void irAnteriorRegistro() {
		log.trace("accionIrAnteriorRegistroTabla() - Acción ejecutada");
		if (lvNumerosSerie.getItems() != null && lvNumerosSerie.getItems() != null) {
			int indice = lvNumerosSerie.getSelectionModel().getSelectedIndex();
			if (indice > 0) {
				lvNumerosSerie.getSelectionModel().select(indice - 1);
				lvNumerosSerie.scrollTo(indice - 1);
			}
		}
	}

	public void irSiguienteRegistro() {
		log.trace("accionIrSiguienteRegistroTabla() - Acción ejecutada");
		if (lvNumerosSerie.getItems() != null && lvNumerosSerie.getItems() != null) {
			int indice = lvNumerosSerie.getSelectionModel().getSelectedIndex();
			if (indice < lvNumerosSerie.getItems().size()) {
				lvNumerosSerie.getSelectionModel().select(indice + 1);
				lvNumerosSerie.scrollTo(indice + 1);
			}
		}
	}

	@FXML
	public void actionTfCodigoIntro(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.MULTIPLY) {
			addNumeroSerie();
		}
	}

	public void numerosSerieDisponibles() {
		Integer pendiente = new Integer(lbPendiente.getText());
		if (pendiente > 0) {
			List<String> numerosSerieDisponibles = new ArrayList<String>();
			numerosSerieDisponibles.addAll(numerosSerieDocumentoOrigen);
			numerosSerieDisponibles.removeAll(lvNumerosSerie.getItems());
			getDatos().put(NumerosSerieLineaOrigenController.PARAMETRO_NUMEROS_SERIE_DISPONIBLES, numerosSerieDisponibles);
			getDatos().put(NumerosSerieLineaOrigenController.PARAMETRO_ARTICULO, lbArticulo.getText());
			getDatos().put(NumerosSerieLineaOrigenController.PARAMETRO_CANTIDAD, lbPendiente.getText());
			getApplication().getMainView().showModalCentered(NumerosSerieLineaOrigenView.class, getDatos(), getStage());
			@SuppressWarnings("unchecked")
			List<String> numerosSerieSeleccionados = (List<String>) getDatos().get(NumerosSerieLineaOrigenController.PARAMETRO_NUMEROS_SERIE_SELECCION);
			if (numerosSerieSeleccionados != null) {
				int i = 0;
				for (; i < pendiente && i < numerosSerieSeleccionados.size(); i++) {
					lvNumerosSerie.getItems().add(numerosSerieSeleccionados.get(i));
				}

				if (numerosSerieSeleccionados.size() > pendiente) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pueden asignar todos los números de serie seleccionados."), getStage());
				}

				pendiente = pendiente - i;
				lbPendiente.setText(pendiente.toString());

				if (pendiente > 0) {
					tfNumeroSerie.requestFocus();
				}
				else {
					btCerrar.requestFocus();
				}
				lvNumerosSerie.getSelectionModel().selectLast();
			}
		}
		else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pueden asignar más números de serie, ya están todos asignados."), getStage());
			btCerrar.requestFocus();
		}
	}

}
