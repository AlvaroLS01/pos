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
package com.comerzzia.pos.gui.ventas.apartados.detalle.marcarVenta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.ventas.apartados.ApartadosManager;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager.SalvarTicketCallback;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.NumerosSerieController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.NumerosSerieView;
import com.comerzzia.pos.gui.ventas.tickets.factura.FacturaView;
import com.comerzzia.pos.persistence.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class MarcarVentaApartadoController extends WindowController implements IContenedorBotonera{
	
	protected final Logger log = Logger.getLogger(getClass());
	
	public static final String PARAMETRO_ARTICULOS = "articulos_venta";
	
	public static final String PARAMETRO_APARTADO_MGR = "apartado_manager";
	
	protected BotoneraComponent botoneraAccionesTabla;
	
	@FXML
	protected AnchorPane panelBotonera;
	@FXML
	protected TableView<LineaVentaApartadoGui> tbArticulos;
	@FXML
	protected TableColumn tcArticulo, tcDescripcion, tcDesglose1, tcDesglose2, tcPrecio, tcDescuento, tcCantidad, tcImporte, tcBtSelec;
	
	protected ObservableList<LineaVentaApartadoGui> articulos;
	
	protected List<ApartadosDetalleBean> articulosApartado;
	
	protected ApartadosManager apartadosManager;
	
	protected TicketManager ticketManager;
	
	@Autowired
	protected VariablesServices variablesServices;
	
	@Autowired
	protected ArticulosService articulosService;
	
	@Autowired
	protected TicketsService ticketsService;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		articulos = FXCollections.observableArrayList();
		
		tbArticulos.setPlaceholder(new Label(""));
		
		tbArticulos.setItems(articulos);
		
		tcArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcArticulo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcDescripcion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDesglose1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcDesglose1", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDesglose2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcDesglose2", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcPrecio.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcPrecio", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcImporte", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcDescuento.setCellFactory(CellFactoryBuilder.createCellRendererCeldaPorcentaje("tbArticulos", "tcDescuento", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcCantidad", 3, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcBtSelec.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcBtSelec", null, CellFactoryBuilder.ESTILO_ALINEACION_CEN));

		Boolean usaDescuentoEnLinea = variablesServices.getVariableAsBoolean(VariablesServices.TICKETS_USA_DESCUENTO_EN_LINEA);
		if(!usaDescuentoEnLinea){
			tcDescuento.setVisible(false);
		}

		// Asignamos las lineas a la tabla
		tbArticulos.setItems(articulos);

		// Definimos un factory para cada celda para aumentar el rendimiento
		tcArticulo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaVentaApartadoGui, String> cdf) {
				return cdf.getValue().getCodArtProperty();
			}
		});
		tcDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaVentaApartadoGui, String> cdf) {
				return cdf.getValue().getDesArtProperty();
			}
		});
		tcCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaVentaApartadoGui, BigDecimal> cdf) {
				return cdf.getValue().getCantidadProperty();
			}
		});
		tcDesglose1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaVentaApartadoGui, String> cdf) {
				return cdf.getValue().getDesglose1Property();
			}
		});
		tcDesglose2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaVentaApartadoGui, String> cdf) {
				return cdf.getValue().getDesglose2Property();
			}
		});
		tcPrecio.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaVentaApartadoGui, BigDecimal> cdf) {
				return cdf.getValue().getPrecioProperty();
			}
		});
		tcImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaVentaApartadoGui, BigDecimal> cdf) {
				return cdf.getValue().getImporteProperty();
			}
		});
		tcDescuento.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaVentaApartadoGui, BigDecimal> cdf) {
				return cdf.getValue().getDtoProperty();
			}
		});
		tcBtSelec.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaVentaApartadoGui, String> cdf) {
				if(cdf.getValue().isLineaSelec()){
					return new SimpleStringProperty("X");
				}
				else{
					return new SimpleStringProperty("");
				}
			}
		});
		
		tbArticulos.setRowFactory(new Callback<TableView<LineaVentaApartadoGui>, TableRow<LineaVentaApartadoGui>>() {

			@Override
			public TableRow<LineaVentaApartadoGui> call(TableView<LineaVentaApartadoGui> p) {
				final TableRow<LineaVentaApartadoGui> row = new TableRow<LineaVentaApartadoGui>() {
					@Override
					protected void updateItem(LineaVentaApartadoGui linea, boolean empty){
						super.updateItem(linea, empty);
						if (linea!=null){
							if(linea.isLineaSelec()) {
								if (!getStyleClass().contains("cell-renderer-cupon")) {
									getStyleClass().add("cell-renderer-cupon");
								}
							}
							else {
        						getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
        					}
							
        				} 
        				else {
        					getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
        					
        				}
					}
				};
				return row;
			}
		});
		
		tbArticulos.setEditable(true);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();
		
		ticketManager = SpringContext.getBean(TicketManager.class);
		
		apartadosManager = (ApartadosManager)getDatos().remove(PARAMETRO_APARTADO_MGR);

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
		
		articulosApartado = (List<ApartadosDetalleBean>)getDatos().remove(PARAMETRO_ARTICULOS);
		refrescarPantalla();
	}

	@Override
	public void initializeFocus() {
		tbArticulos.requestFocus();
		tbArticulos.getSelectionModel().select(0);
	}
	
	public void refrescarPantalla(){
		
		int rowSelected = tbArticulos.getSelectionModel().getSelectedIndex();
		articulos.clear();
		
		for(ApartadosDetalleBean articulo : articulosApartado){
			articulos.add(new LineaVentaApartadoGui(articulo));
		}
		tbArticulos.getSelectionModel().select(rowSelected);
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
    	switch (botonAccionado.getClave()) {
    	case "ACCION_TABLA_PRIMER_REGISTRO":
    		accionTablaPrimerRegistro(tbArticulos);
    		tbArticulos.requestFocus();
    		break;
    	case "ACCION_TABLA_ANTERIOR_REGISTRO":
    		accionTablaIrAnteriorRegistro(tbArticulos);
    		tbArticulos.requestFocus();
    		break;
    	case "ACCION_TABLA_SIGUIENTE_REGISTRO":
    		accionTablaIrSiguienteRegistro(tbArticulos);
    		tbArticulos.requestFocus();
    		break;
    	case "ACCION_TABLA_ULTIMO_REGISTRO":
    		accionTablaUltimoRegistro(tbArticulos);
    		tbArticulos.requestFocus();
    		break;
    	case "ACCION_TABLA_SELECCIONAR_TODOS":
    		seleccionarTodos(true);
    		break;
    	case "ACCION_TABLA_DESELECCIONAR_TODOS":
    		seleccionarTodos(false);
    		break;
    	case "ACCION_TABLA_SELECCIONAR_MARCADO":
    		seleccionarUno();
    		break;
    	default:
    		log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
    		break;
    	}
	}
	
	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
        List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row-plus.png", null, null, "ACCION_TABLA_SELECCIONAR_MARCADO", "REALIZAR_ACCION")); //"Delete"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row-all.png", null, null, "ACCION_TABLA_SELECCIONAR_TODOS", "REALIZAR_ACCION")); //"Delete"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row-del-sel.png", null, null, "ACCION_TABLA_DESELECCIONAR_TODOS", "REALIZAR_ACCION"));
        return listaAcciones;
    }
	
	public void seleccionarUno(){
		log.trace("seleccionarUno()");
		
		LineaVentaApartadoGui linea = tbArticulos.getSelectionModel().getSelectedItem();
		
		for(ApartadosDetalleBean lineaArticulo : articulosApartado){
			if(lineaArticulo.getLinea() == linea.getArticulo().getLinea()){
				if(lineaArticulo.isLineaSeleccionadaVenta()){
					lineaArticulo.setLineaSeleccionadaVenta(false);
				}
				else{
					lineaArticulo.setLineaSeleccionadaVenta(true);
				}
			}
		}
		
		refrescarPantalla();
	}

	public void seleccionarTodos(boolean seleccionar){
		log.trace("seleccionarTodos()");
		
		for(ApartadosDetalleBean lineaArticulo : articulosApartado){
			lineaArticulo.setLineaSeleccionadaVenta(seleccionar);
		}
		refrescarPantalla();
	}
	
	public void accionAceptar(){
		log.trace("accionAceptar()");

		List<ApartadosDetalleBean> articulosVenta = new ArrayList<ApartadosDetalleBean>();
		BigDecimal importeVenta = BigDecimal.ZERO;

		for(ApartadosDetalleBean linea : articulosApartado){
			if(linea.isLineaSeleccionadaVenta()){
				
				try {
					ArticuloBean articulo = articulosService.consultarArticulo(linea.getCodart());
					if (articulo.getNumerosSerie()) {
						ticketManager.nuevoTicket();
						ticketManager.getTicket().setCliente(apartadosManager.getCliente());
						
						LineaTicket lineaTicket = ticketManager.nuevaLineaArticulo(linea.getCodart(), linea.getDesglose1(), linea.getDesglose2(), linea.getCantidad(), null);
						asignarNumerosSerie(lineaTicket, linea);
						
						if(lineaTicket.getNumerosSerie() == null || (lineaTicket.getNumerosSerie() != null && lineaTicket.getNumerosSerie().isEmpty()) || (lineaTicket.getNumerosSerie() != null && lineaTicket.getNumerosSerie().size() < lineaTicket.getCantidad().setScale(0, RoundingMode.HALF_UP).abs().intValue())) {
							VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe indicar los números de serie para continuar."), getStage());
							return;
						}
					}
				}
				catch(Exception e) {
					log.error("accionAceptar() - No se han podido procesar los números de serie: " + e.getMessage(), e);
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al procesar los números de serie"), e);
				}
				
				articulosVenta.add(linea);
				importeVenta = importeVenta.add(linea.getImporteTotal());
			}
		}
		
		
		if(articulosVenta.isEmpty()){
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar al menos un artículo para la venta."), getStage());
		}
		else{
			if(importeVenta.compareTo(apartadosManager.getTicketApartado().getCabecera().getSaldoCliente())>0){
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El saldo disponible del cliente es inferior al total de la venta."), getStage());
			}
			else{
					try {
						boolean errorVigencia = false;
						ticketManager.generarVentaDeApartados(articulosVenta, apartadosManager.getCliente(), apartadosManager.getTicketApartado().getCabecera());
						if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Desea asignar datos de facturación?"), getStage())){
							if(!ticketManager.comprobarConfigContador(Documentos.FACTURA_COMPLETA)){
								errorVigencia = true;
							}else{
								getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
								getApplication().getMainView().showModalCentered(FacturaView.class, getDatos(), getStage());								
							}
						}else{
							errorVigencia = !ticketManager.comprobarConfigContador(ticketManager.getDocumentoActivo().getCodtipodocumento());
						}
						if(errorVigencia){
							ticketManager.crearVentanaErrorContador(getStage());
						}else{
							apartadosManager.actualizarEstadoLineasVendidas(ticketManager.getTicket().getCabecera().getUidTicket(), articulosVenta);
							apartadosManager.registrarVentaApartado(importeVenta);						
							ticketsService.setContadorIdTicket((Ticket) ticketManager.getTicket());
							ticketManager.salvarTicket(getStage(), new SalvarTicketCallback() {
								
								@Override
								public void onSucceeded() {
									Map<String,Object> mapaParametros= new HashMap<String,Object>();
									mapaParametros.put("ticket",ticketManager.getTicket());
									mapaParametros.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
									mapaParametros.put("apartado", apartadosManager.getTicketApartado().getCabecera().getIdApartado().toString());
									
									try{
										ServicioImpresion.imprimir(ticketManager.getTicket().getCabecera().getFormatoImpresion(), mapaParametros);
									}catch (Exception e) {
										VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), e);
									}
									ticketManager.finalizarTicket();
									getStage().close();
								}
								
								@Override
								public void onFailure(Exception e) {
									log.error("onFailure() - Error al salvar el ticket", e);
									VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error al salvar el ticket."), e);
									getStage().close();
								}
							});
						}
					} catch (LineaTicketException e) {
						log.error(e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
					} catch (TicketsServiceException e) {
						log.error(e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
					}
			}
		}
	}

	public void accionCancelar(){
		log.trace("accionCancelar()");
		
		seleccionarTodos(false);
		getStage().close();
	}
	
	public void aceptarArticuloDobleClick(MouseEvent event) {
		log.debug("aceptarArticuloDobleClick() - Acción aceptar");
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				seleccionarUno();
			}
		}
	}
	
	protected boolean quedanNumerosSeriePorAsignar(List<ApartadosDetalleBean> articulosVenta, Map<ApartadosDetalleBean, List<String>> numerosSerie) {
		boolean res = false;
		for(ApartadosDetalleBean detalle : articulosVenta) {
			List<String> numerosSerieDetalle = numerosSerie.get(detalle);
			res = numerosSerieDetalle != null && numerosSerieDetalle.size() < detalle.getCantidad().intValue();
			if(res) {
				break;
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	protected void asignarNumerosSerie(LineaTicket linea, ApartadosDetalleBean detalleApartado){
		getDatos().put(NumerosSerieController.PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN, new ArrayList<String>());
		getDatos().put(NumerosSerieController.PARAMETRO_LINEA_DOCUMENTO_ACTIVO, linea);
		getApplication().getMainView().showModalCentered(NumerosSerieView.class, getDatos(), getStage());
	    List<String> numerosSerie = (List<String>) getDatos().get(NumerosSerieController.PARAMETRO_LISTA_NUMEROS_SERIES_ASIGNADOS);
	    detalleApartado.setNumerosSerie(numerosSerie);
	}
}
