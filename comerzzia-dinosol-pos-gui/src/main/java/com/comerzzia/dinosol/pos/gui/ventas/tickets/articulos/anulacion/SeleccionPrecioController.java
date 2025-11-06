package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.anulacion;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class SeleccionPrecioController extends WindowController implements Initializable, IContenedorBotonera {

	private Logger log = Logger.getLogger(SeleccionPrecioController.class);

	public static String PARAM_TITULO = "SeleccionPrecioController.Titulo";
	public static String PARAM_CANTIDAD = "SeleccionPrecioController.Cantidad";
	public static String PARAM_PRECIOS = "SeleccionPrecioController.Precios";
	public static String PARAM_PRECIO_SELECCIONADO = "SeleccionPrecioController.PrecioSeleccionado";

	@FXML
	private TableView<BigDecimal> tbPrecios;

	@FXML
	private TableColumn<BigDecimal, String> tcPrecio;

	@FXML
	private VBox vbBotones;
	
	@FXML
	private Label lbTitulo, lbCantidad;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeComponents() throws InitializeGuiException {
		tcPrecio.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPrecios", "tcPrecio", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcPrecio.setCellValueFactory(data -> new SimpleStringProperty(FormatUtil.getInstance().formateaImporte(data.getValue())));

		try {
			List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
			BotoneraComponent botoneraAccionesTabla = new BotoneraComponent(listaAccionesAccionesTabla.size(), 1, this, listaAccionesAccionesTabla, vbBotones.getPrefWidth(),
			        vbBotones.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
			vbBotones.getChildren().add(botoneraAccionesTabla);
		}
		catch (Exception e) {
			log.error("initializeComponents() - Ha habido un error al cargar la botonera de navegación: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();
		
		if(getDatos().containsKey(PARAM_TITULO)) {
			String titulo = (String) getDatos().get(PARAM_TITULO);
			lbTitulo.setText(titulo);
		}
		else {
			lbTitulo.setText(I18N.getTexto("Seleccione el precio al que quiere devolver"));
		}
		
		List<BigDecimal> precios = (List<BigDecimal>) getDatos().get(PARAM_PRECIOS);
		tbPrecios.setItems(FXCollections.observableArrayList(precios));
		tbPrecios.getSelectionModel().selectFirst();
		
		BigDecimal cantidad = (BigDecimal) getDatos().get(PARAM_CANTIDAD);
		if(BigDecimalUtil.isMayor(cantidad, BigDecimal.ONE)) {
			lbCantidad.setVisible(true);
		}
		else {
			lbCantidad.setVisible(false);
		}
	}

	@Override
	public void initializeFocus() {
		tbPrecios.requestFocus();
	}

	public void aceptar() {
		BigDecimal precioSeleccionado = tbPrecios.getSelectionModel().getSelectedItem();
		getDatos().put(PARAM_PRECIO_SELECCIONADO, precioSeleccionado);
		getStage().close();
	}
	
	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION"));
		return listaAcciones;
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException {
		log.trace("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
		switch (botonAccionado.getClave()) {
			case "ACCION_TABLA_PRIMER_REGISTRO":
				accionTablaPrimerRegistro(tbPrecios);
				break;
			case "ACCION_TABLA_ANTERIOR_REGISTRO":
				accionTablaIrAnteriorRegistro(tbPrecios);
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO":
				accionTablaIrSiguienteRegistro(tbPrecios);
				break;
			case "ACCION_TABLA_ULTIMO_REGISTRO":
				accionTablaUltimoRegistro(tbPrecios);
				break;			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
				break;
		}
	}

}
