/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.pos.gui.mantenimientos.codigoBarras;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;
import com.comerzzia.pos.services.codBarrasEsp.CodBarrasEspecialesServices;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class MantenimientoCodigoBarrasController extends Controller implements IContenedorBotonera {

	protected Logger log = Logger.getLogger(getClass());
	
	@FXML
	protected TableView<LineaCodigoBarrasGui> tbCodigos;
	@FXML
	protected TableColumn<LineaCodigoBarrasGui, String> tcDescripcion, tcArticulos, tcPrefijos, tcPrecio, tcCantidad, tcFidelizacion, tcTicket;
	@FXML
	protected AnchorPane panelBotonera;
	
	protected ObservableList<LineaCodigoBarrasGui> codigos;
	
	protected BotoneraComponent botoneraAccionesTabla;
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private CodBarrasEspecialesServices codBarrasEspecialesServices;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.trace("initialize()- ");

		codigos = FXCollections.observableArrayList();
		tbCodigos.setPlaceholder(new Label(""));

		tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcDescripcion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcArticulos.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcArticulos", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcPrefijos.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcPrefijos", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcCantidad", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcPrecio.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcPrecio", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcFidelizacion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcFidelizacion", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcTicket.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcTicket", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

		// Asignamos las lineas a la tabla
		tbCodigos.setItems(codigos);

		// Definimos un factory para cada celda para aumentar el rendimiento
		tcDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaCodigoBarrasGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaCodigoBarrasGui, String> cdf) {
				return cdf.getValue().getDescripcionProperty();
			}
		});
		tcArticulos.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaCodigoBarrasGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaCodigoBarrasGui, String> cdf) {
				return cdf.getValue().getArticuloProperty();
			}
		});
		tcPrefijos.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaCodigoBarrasGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaCodigoBarrasGui, String> cdf) {
				return cdf.getValue().getPrefijoProperty();
			}
		});
		tcCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaCodigoBarrasGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaCodigoBarrasGui, String> cdf) {
				return cdf.getValue().getCantidadProperty();
			}
		});
		tcPrecio.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaCodigoBarrasGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaCodigoBarrasGui, String> cdf) {
				return cdf.getValue().getPrecioProperty();
			}
		});
		tcFidelizacion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaCodigoBarrasGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaCodigoBarrasGui, String> cdf) {
				return cdf.getValue().getFidelizacionProperty();
			}
		});
		tcTicket.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaCodigoBarrasGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaCodigoBarrasGui, String> cdf) {
				return cdf.getValue().getTicketProperty();
			}
		});
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		log.trace("initializeComponents()- ");

		try{          
            log.debug("inicializarComponentes() - Carga de acciones de botonera de tabla de ventas");
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
            botoneraAccionesTabla = new BotoneraComponent(1, 7, this, listaAccionesAccionesTabla, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelBotonera.getChildren().add(botoneraAccionesTabla);
		}
		catch (CargarPantallaException e) {
			log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
		} 			
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.trace("initializeForm()- ");
		
		refrescarPantalla();
	}

	@Override
	public void initializeFocus() {
	}
	
	public void refrescarPantalla(){
		log.trace("refrescarPantalla()- ");
		
		try{
			List<CodigoBarrasBean> codigosBarras = codBarrasEspecialesServices.obtenerCodigosBarras(sesion.getAplicacion().getUidActividad());
			codigos.clear();
			
			for(CodigoBarrasBean codBarra : codigosBarras){
				codigos.add(new LineaCodigoBarrasGui(codBarra));
			}
			tbCodigos.setItems(codigos);
		}catch(Exception e){
			log.error("Error consultando las configuraciones de los códigos de barras", e);
			VentanaDialogoComponent.crearVentanaError(this.getStage(), I18N.getTexto("Se produjo un error consultando las configuraciones de códigos de barra."), e);
		}
	}
	
	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
    	switch (botonAccionado.getClave()) {
    	case "ACCION_TABLA_PRIMER_REGISTRO":
    		accionTablaPrimerRegistro(tbCodigos);
    		break;
    	case "ACCION_TABLA_ANTERIOR_REGISTRO":
    		accionTablaIrAnteriorRegistro(tbCodigos);
    		break;
    	case "ACCION_TABLA_SIGUIENTE_REGISTRO":
    		accionTablaIrSiguienteRegistro(tbCodigos);
    		break;
    	case "ACCION_TABLA_ULTIMO_REGISTRO":
    		accionTablaUltimoRegistro(tbCodigos);
    		break;
    	case "ACCION_TABLA_BORRAR_REGISTRO":
    		eliminarCodigo();
    		break;
    	case "ACCION_TABLA_EDITAR_REGISTRO":
    		editarCodigo();
    		break;
    	case "ACCION_TABLA_NUEVO_REGISTRO":
    		nuevoCodigo();
    		break;
    	default:
    		log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
    		break;
    	}		
	}

	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
		log.trace("cargarAccionesTabla()- ");
		
        List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row_delete.png", null, null, "ACCION_TABLA_BORRAR_REGISTRO", "REALIZAR_ACCION")); //"Delete"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row_edit.png", null, null, "ACCION_TABLA_EDITAR_REGISTRO", "REALIZAR_ACCION"));
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row-plus.png", null, null, "ACCION_TABLA_NUEVO_REGISTRO", "REALIZAR_ACCION"));
        return listaAcciones;
    }
	
	public void editarCodigo(){
		log.trace("editarCodigo()- ");
		
		LineaCodigoBarrasGui lineaSeleccionada = (LineaCodigoBarrasGui)tbCodigos.getSelectionModel().getSelectedItem();

		if(lineaSeleccionada!=null){
			getDatos().put(NuevoCodBarrasEspecialController.PARAMETRO_COD_BARRAS, lineaSeleccionada.getCodBarras());
			getApplication().getMainView().showModalCentered(NuevoCodBarrasEspecialView.class, getDatos(), getStage());
			refrescarPantalla();
		}
	}
	
	public void nuevoCodigo(){
		log.trace("nuevoCodigo()- ");

		getApplication().getMainView().showModalCentered(NuevoCodBarrasEspecialView.class, getDatos(), getStage());
		
		refrescarPantalla();
	}
	
	public void eliminarCodigo(){
		log.trace("eliminarCodigo()- ");

		LineaCodigoBarrasGui lineaSeleccionada = (LineaCodigoBarrasGui)tbCodigos.getSelectionModel().getSelectedItem();

		if(lineaSeleccionada!=null){
			if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se borrará la configuración seleccionada. ¿Está seguro?"), this.getStage())){
				CodigoBarrasBean codBar = lineaSeleccionada.getCodBarras();
				try {
					codBarrasEspecialesServices.eliminarCodigo(codBar);
					codBarrasEspecialesServices.cargarCodigosBarrasEspeciales(sesion.getAplicacion().getUidActividad());
					refrescarPantalla();
				} catch (Exception e) {
					log.error("Error al eliminar el código de barras.", e);
					VentanaDialogoComponent.crearVentanaError(this.getStage(), I18N.getTexto("No se pudo eliminar la configuración seleccionada."), e);
				}
			}
		}
	}
}
