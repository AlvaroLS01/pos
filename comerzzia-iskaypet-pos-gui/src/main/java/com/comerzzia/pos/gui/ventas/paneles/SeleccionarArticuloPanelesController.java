package com.comerzzia.pos.gui.ventas.paneles;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.imagen.BotonBotoneraImagenCompletaComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosServiceException;
import com.comerzzia.pos.services.ventas.paneles.ArticulosPanelesService;
import com.comerzzia.pos.services.ventas.paneles.ItemPanelDto;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

@Component
public class SeleccionarArticuloPanelesController extends WindowController implements IContenedorBotonera {

	private Logger log = Logger.getLogger(SeleccionarArticuloPanelesController.class);

	public static final String PARAM_ARTICULOS_INSERTADOS = "Lista_Articulos_Panel";

	public static final String PARAM_TICKET_MANAGER = "SeleccionarArticuloPanelesController.TicketManager";
	
	public static final String PARAM_LINES = "LINES";
	
	public static final String PARAM_BEHAVIOUR = "BEHAVIOUR";
	
	public static final String PARAM_ITEM_DES = "ITEM_DES";
	
	public static final String PARAM_ITEM_PRICE = "ITEM_PRICE";

	@FXML
	protected ScrollPane scrollPane;

	@FXML
	protected AnchorPane panelBotonera;
	
	@FXML
	protected Button btCerrar;
	
	@FXML
	protected Label lbMigasDePan;
	
	protected ObservableList<LineaTicketGui> lines;
	
	@FXML
	protected HBox itemVisor;
	
	@FXML
	protected TableView<LineaTicketGui> tbLines;
	
	@FXML
	protected TableColumn<LineaTicketGui, String> tcItemDes;

	@Autowired
	private ArticulosPanelesService articulosPanelesService;

	protected double anchuraScrollPane;
	
	protected int numFilas;
	protected int numColumnas;
	
	protected List<List<ItemPanelDto>> colaVolverAtras;
	
	protected BotoneraComponent botonera;
	
	protected TicketManager ticketManager;
	
	protected Boolean isVisorActive;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				anchuraScrollPane = scrollPane.getWidth();
			}
		});
	}

	@Override
	public void initializeFocus() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		try {
			ticketManager = (TicketManager) getDatos().get(PARAM_TICKET_MANAGER);
			
			// uncomment when it's neccesary to show previous added items
			// lines = (ObservableList<LineaTicketGui>) getDatos().get(PARAM_LINES);
			if(lines == null) {
				lines = FXCollections.observableList(new ArrayList<LineaTicketGui>());
			}
			
			initializeVisor();
			
			List<ItemPanelDto> articulosVenta = articulosPanelesService.getArticulosVenta();
			
			isVisorActive = articulosPanelesService.isVisorActive();
			if(isVisorActive!=null && !isVisorActive) {
				itemVisor.setDisable(false);
				itemVisor.setManaged(false);
				itemVisor.getChildren().clear();
			}
			
			colaVolverAtras = new ArrayList<List<ItemPanelDto>>();
			colaVolverAtras.add(articulosVenta);
			
			log.debug("initializeForm() - Tamaño de la cola de navegación: " + colaVolverAtras.size());
			
			cargarBotonera(articulosVenta);
			
			setBotonCerrar();
			lbMigasDePan.setText(I18N.getTexto("INICIO"));
		}
		catch (Exception e) {
			log.error("initializeForm() - Ha habido un error al mostrar la botonera: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void initializeVisor() {
		
		tbLines.setPlaceholder(new Label(""));
		tbLines.setItems(lines);

		tcItemDes.setText(I18N.getTexto("ARTÍCULOS"));
		tcItemDes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLines", "tcItemDes", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		
		addLines();
	}
	
	protected void addLines() {
		// ITEM DESCRIPTION
		tcItemDes.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaTicketGui, String> cdf) {
				return cdf.getValue().getDescripcionProperty();
			}
		});
	}

	protected boolean cargarBotonera(List<ItemPanelDto> items) {
		if (items != null && !items.isEmpty()) {
			calcularFilasYColumnas(items);
			
			final List<ConfiguracionBotonBean> botones = new ArrayList<ConfiguracionBotonBean>();
			int contador = 0;
			boolean posicionesDefinidas = articulosPanelesService.hayPosicionesDefinidas(items);

			for(int i = 0 ; i < numFilas ; i++) {
				for(int j = 0 ; j < numColumnas ; j++) {
					ItemPanelDto item = buscarItem(items, i, j, posicionesDefinidas);
					if(item != null) {
						String tipo = item.getTipo();
						
						ConfiguracionBotonBean configuracionBotonBean = null;
						if(tipo.equals(ItemPanelDto.TIPO_HUECO)) {
							configuracionBotonBean = new ConfiguracionBotonBean(null, null, null, null, "HUECO");
						}
						else {
							String metodo = tipo.equals(ItemPanelDto.TIPO_ARTICULO) ? "addArticulo" : "navegarCategoria";
							configuracionBotonBean = new ConfiguracionBotonBean(item.getFoto(), item.getDescripción().toUpperCase(), "", item.getCodigo(), metodo);
							configuracionBotonBean.setParametro(PARAM_BEHAVIOUR, item.getBehaviour());
							configuracionBotonBean.setParametro(PARAM_ITEM_DES, item.getDescripción());
						}
						botones.add(configuracionBotonBean);
						
						if (contador == 300) {
							break;
						}
						contador++;
					}
					else {
						ConfiguracionBotonBean botonHueco = new ConfiguracionBotonBean(null, null, null, null, "HUECO");
						botones.add(botonHueco);
					}
				}
			}

			final SeleccionarArticuloPanelesController controller = this;

			Platform.runLater(new Runnable(){

				@Override
				public void run() {
					try {
						double altura = numFilas * 118.0;
						double addAltura = 5*numFilas;
						if(altura < scrollPane.getHeight()) {
							altura = scrollPane.getHeight() - 20;
						}
						double anchura = getStage().getWidth() - 105;
						if(isVisorActive != null && isVisorActive && itemVisor != null && itemVisor.getWidth() > 0.0) {
							anchura = getStage().getWidth() - itemVisor.getWidth() - 115;
						}
						
						log.debug("cargarBotonera() - La botonera tendrá de alto " + altura + " y de ancho " + anchura);

						if(botonera != null) {
							botonera.destroy();
						}
						botonera = new BotoneraComponent(numFilas, numColumnas, controller, botones, anchura, altura, getTipoBoton());
						panelBotonera.getChildren().clear();
						panelBotonera.getChildren().add(botonera);
						panelBotonera.setPrefHeight(altura + addAltura);
						panelBotonera.setPrefWidth(anchura);
					}
					catch (Exception e) {
						log.error("initializeForm() - Ha habido un error al mostrar la botonera: " + e.getMessage(), e);
					}
				}
			});
			
			return true;
		}
		else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay artículos en esta sección."), getStage());
			return false;
		}
	}

	protected String getTipoBoton() {
        return BotonBotoneraImagenCompletaComponent.class.getName();
    }

	protected ItemPanelDto buscarItem(List<ItemPanelDto> items, int i, int j, boolean posicionesDefinidas) {
		for (ItemPanelDto item : items) {
			Integer filaItem = item.getFila();
			Integer columnaItem = item.getColumna();
			if(filaItem != null && columnaItem != null && filaItem == i && columnaItem == j) {
				return item;
			}
		}
		
		if(!posicionesDefinidas) {
			int index = i * numColumnas + j;
			if(items.size() > index) {
				return items.get(index);
			}
			else {
				return null;
			}
		}
		
	    return null;
    }

	@Override
	public void realizarAccion(BotonBotoneraComponent botonBotoneraComponet) {
		try {
			if (botonBotoneraComponet.getTipo().equals("addArticulo")) {
				addArticulo(botonBotoneraComponet);
			}
			else if (botonBotoneraComponet.getTipo().equals("navegarCategoria")) {
				navegarCategoria(botonBotoneraComponet);
			}
		}
		catch (Exception e) {
			log.error("realizarAccion() - No se ha podido añadir el artículo: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido añadir el artículo. Contacte con el personal de tienda."), getStage());
		}
	}

	protected void navegarCategoria(BotonBotoneraComponent botonBotoneraComponet) {
		ItemPanelDto itemSeleccionado = buscarItemsCategoria(botonBotoneraComponet.getClave());

		if (itemSeleccionado != null) {
			List<ItemPanelDto> subItems = itemSeleccionado.getSubItems();
			boolean cargaHecha = cargarBotonera(subItems);
			
			if(cargaHecha) {
				colaVolverAtras.add(subItems);
				
				log.debug("navegarCategoria() - Tamaño de la cola de navegación: " + colaVolverAtras.size());
				
				String migasDePan = lbMigasDePan.getText();
				migasDePan = migasDePan + " > " + botonBotoneraComponet.getConfiguracionBoton().getTexto();
				lbMigasDePan.setText(migasDePan);
				
				btCerrar.setText(I18N.getTexto("Volver atrás"));
				btCerrar.getStyleClass().removeAll("btCancelar");
				btCerrar.getStyleClass().add("btAtras");
			}
		}
	}

	protected void addArticulo(BotonBotoneraComponent botonBotoneraComponet) throws ArticuloNotFoundException, ArticulosServiceException, InitializeGuiException {
		try {
			log.debug("addArticulo() - Se va a añadir el artículo seleccionado");
			
			String codArt = botonBotoneraComponet.getClave();
			String desArt = (String) botonBotoneraComponet.getParametro(PARAM_ITEM_DES);
			addItem(codArt);
			
			addLine(codArt, desArt);
			
			// reacts like Excel sheet behaviour		
			String behaviour = (String) botonBotoneraComponet.getParametro(PARAM_BEHAVIOUR);
			if(StringUtils.isNotBlank(behaviour) && behaviour.equals(ItemPanelDto.CLOSE_BEHAVIOUR)) {
				colaVolverAtras = null;
				lines = null;
				getStage().close();
			} else if(StringUtils.isNotBlank(behaviour) && behaviour.equals(ItemPanelDto.MAIN_BEHAVIOUR)) {
				showAddedItemInfo(codArt, desArt);
				initializeForm();
			} else {							// CONTINUE BEHAVIOUR
				showAddedItemInfo(codArt, desArt);
			}
		} catch (Exception e) {
			log.error("addArticulo() - Ha ocurrido un error al añadir el artículo: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e);
		}
		
	}
	
	protected void showAddedItemInfo(String codArt, String desArt) {
		// only shows is visor isn't active
		if(!isVisorActive) {
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Se ha añadido el artículo ") + codArt + ": " + desArt, getStage());
		}
	}
	
	protected void addLine(String itemCode, String itemDes) {
		LineaTicketGui line = new LineaTicketGui();
		line.setArticulo(itemCode);
		line.setDescripcion(itemDes);
		lines.add(0,line);
		tbLines.getSelectionModel().select(0);
	}
	
	protected void addItem(String codArt) {
		@SuppressWarnings("unchecked")
		List<String> itemsToAdd = (List<String>) getDatos().get(PARAM_ARTICULOS_INSERTADOS);
		if(itemsToAdd==null) {
			itemsToAdd = new ArrayList<String>();
		}
		itemsToAdd.add(codArt);
		getDatos().put(PARAM_ARTICULOS_INSERTADOS, itemsToAdd);
	}

	protected ItemPanelDto buscarItemsCategoria(String clave) {
		List<ItemPanelDto> items = articulosPanelesService.getArticulosVenta();
		ItemPanelDto item = iterarItem(clave, items);
		return item;
	}

	protected ItemPanelDto iterarItem(String clave, List<ItemPanelDto> items) {
		if (items != null) {
			for (ItemPanelDto item : items) {
				if (item.getCodigo().equals(clave)) {
					return item;
				}
				ItemPanelDto ItemBuscado = iterarItem(clave, item.getSubItemsSinBuscar());
				if (ItemBuscado != null) {
					return ItemBuscado;
				}
			}
		}
		return null;
	}

	public void volverAtras() {
		try {			
			if(colaVolverAtras.size() == 1) {
				lines = null;
				getStage().close();
			}
			else {
				colaVolverAtras.remove(colaVolverAtras.size() - 1);
				List<ItemPanelDto> ultimoNivel = colaVolverAtras.get(colaVolverAtras.size() - 1);
				
				log.debug("navegarCategoria() - Tamaño de la cola de navegación: " + colaVolverAtras.size());
				
				cargarBotonera(ultimoNivel);
				
				String migasDePan = lbMigasDePan.getText();
				String[] trozos =  migasDePan.split(" > ");
				migasDePan = I18N.getTexto("INICIO");
				for(int i = 1 ; i < trozos.length ; i++) {
					if(i != trozos.length -1) {
						migasDePan = migasDePan + " > " + trozos[i];
					}
				}
				lbMigasDePan.setText(migasDePan);
			}
			
			if(colaVolverAtras.size() == 1) {
				setBotonCerrar();
			}
		}
		catch (Exception e) {
			log.error("initializeForm() - Ha habido un error al mostrar la botonera: " + e.getMessage(), e);
		}
}

	protected void setBotonCerrar() {
		btCerrar.setText(I18N.getTexto("Cerrar"));
		btCerrar.getStyleClass().removeAll("btAtras");
		btCerrar.getStyleClass().add("btCancelar");
    }

	protected void calcularFilasYColumnas(List<ItemPanelDto> articulosVenta) {
		if(articulosPanelesService.hayPosicionesDefinidas(articulosVenta)) {
		    numFilas = 1;
			numColumnas = 1;
			for(ItemPanelDto item : articulosVenta) {
				if(item.getFila() != null && item.getFila() + 1 > numFilas) {
					numFilas = item.getFila() + 1;
				}
				if(item.getColumna() != null && item.getColumna() + 1 > numColumnas) {
					numColumnas = item.getColumna() + 1;
				}
			}
		}
		else {
			if(articulosVenta.size()>15) {
				numFilas = articulosVenta.size()/5 + 1;
			} else {
			    numFilas = 3;
				numColumnas = 5;
			}
		}
		
		log.debug("calcularFilasYColumnas() - El panel tendrá " + numFilas + " filas y " + numColumnas + " columnas.");
    }
	
}
