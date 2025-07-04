package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.formulario.CardosoFormularioLotes;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.lineasorigen.CardosoLotesLineaOrigenController;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.lineasorigen.CardosoLotesLineaOrigenView;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CardosoLote;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
@SuppressWarnings("unchecked")
@Component
public class CardosoLotesController extends WindowController implements IContenedorBotonera{

	private Logger log = Logger.getLogger(getClass());

	public static final String PARAMETRO_LOTES_DOCUMENTO_ORIGEN = "lotes";
	
	@FXML
	private TextField tfLote;
	@FXML
	private TextFieldImporte tfCantidad;
	@FXML
	private Label lbArticulo, lbCantidad, lbError;
	@FXML
	private Button btInsertar, btCerrar, btConsultarDocOrigen;
	
	@FXML
	private TableView<CardosoLote> tbLotes;
	@FXML
	private TableColumn<CardosoLote, String> clLote;
	@FXML
	private TableColumn<CardosoLote, BigDecimal> clCantidad;
	
	@FXML
	private TecladoNumerico tecladoNumerico;
	@FXML
	private AnchorPane panelBotonera;
	
	private CardosoFormularioLotes formulario;
	private LineaTicket linea;
	private List<CardosoLote> listaLotesDocumentoOrigen;
	private boolean bLotesDocumentoOrigen;
	
	/* ====================================================================================================================== */
	/* ================================================= IMPLEMENTED METHODS ================================================ */
	/* ====================================================================================================================== */
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1){
		log.debug("initialize() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		clLote.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "clLote", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		clLote.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CardosoLote, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<CardosoLote, String> param){
				return new SimpleStringProperty(param.getValue().getId());
			}
		});
		clCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "clCantidad", 3, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		clCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CardosoLote, BigDecimal>, ObservableValue<BigDecimal>>(){
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<CardosoLote, BigDecimal> param){
				return new SimpleObjectProperty<BigDecimal>(param.getValue().getCantidad());
			}
		});
		
		formulario = new CardosoFormularioLotes();
		formulario.setFormField("lote", tfLote);
		formulario.setFormField("cantidad", tfCantidad);
	}
	
	@Override
	public void initializeComponents() throws InitializeGuiException{
		/* =============================== EVENTS =============================== */
		try{
			registraEventoTeclado(new EventHandler<KeyEvent>(){
				@Override
				public void handle(KeyEvent event){
					if(event.getCode() == KeyCode.MULTIPLY){
						if(tfCantidad.isFocused()){
							tfLote.requestFocus();
							tfLote.selectAll();
						}
						else{
							tfCantidad.requestFocus();
							tfCantidad.selectAll();
						}
					}
				}
			}, KeyEvent.KEY_RELEASED);
			registrarAccionCerrarVentanaEscape();
		}
		catch(Exception e){
			String error = "Error al registrar los eventos de pantalla.";
			log.error("initializeComponents() - " + error + " - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("" + error), e);
			throw new InitializeGuiException(error, e);
		}
		/* =============================== BUTTONS =============================== */
		try{
			tecladoNumerico.init(getScene());

			List<ConfiguracionBotonBean> listaAcciones = new ArrayList<ConfiguracionBotonBean>();
			listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
			listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));
			listaAcciones.add(new ConfiguracionBotonBean("iconos/cancelar.png", null, null, "BORRAR_REGISTRO", "REALIZAR_ACCION"));
			listaAcciones.add(new ConfiguracionBotonBean("iconos/delete.png", null, null, "BORRAR_TODOS", "REALIZAR_ACCION"));

			BotoneraComponent botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAcciones, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getName());
			panelBotonera.getChildren().add(botoneraAccionesTabla);
		}
		catch(Exception e){
			String error = "Error al iniciar los componentes de la botonera de la pantalla.";
			log.error("initializeComponents() - " + error + " - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("" + error), e);
			throw new InitializeGuiException(error, e);
		}
	}

	@FXML
	public void actionTfCodigoIntro(KeyEvent event){
		if(event.getCode() == KeyCode.ENTER){
			insertarLote();
		}
	}

	@FXML
	public void actionTfCodigoIntroCantidad(KeyEvent event){
		if(event.getCode() == KeyCode.ENTER){
			tfLote.requestFocus();
		}
	}

	@Override
	public void registrarAccionCerrarVentanaEscape(){
		super.registrarAccionCerrarVentanaEscape();
	}
	
	@Override
	public void initializeForm() throws InitializeGuiException{
		linea = (LineaTicket) getDatos().get(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO);
		listaLotesDocumentoOrigen = (List<CardosoLote>) getDatos().get(PARAMETRO_LOTES_DOCUMENTO_ORIGEN);
		log.info("initializeForm() - Se ha encontrado un total de " 
				+ (listaLotesDocumentoOrigen != null && !listaLotesDocumentoOrigen.isEmpty() 
				? listaLotesDocumentoOrigen.size() : "0") + " lotes en el documento origen.");
		
		if(linea != null){
			lbArticulo.setText(linea.getArticulo().getDesArticulo());
			
			List<CardosoLote> lotesOriginales = ((CARDOSOLineaTicket) linea).getLotes();
			if(lotesOriginales == null){
				tbLotes.setItems(FXCollections.observableArrayList(new ArrayList<CardosoLote>()));
			}
			else{
				tbLotes.setItems(FXCollections.observableArrayList(lotesOriginales));
			}
			tbLotes.getSelectionModel().select(0);
			tbLotes.scrollTo(0);			
			
			tfLote.clear();
			if(((CARDOSOLineaTicket) linea).getLotes() != null){
				BigDecimal cantidadTotalActual = BigDecimal.ZERO;
				for(CardosoLote lote : ((CARDOSOLineaTicket) linea).getLotes()){
					cantidadTotalActual = cantidadTotalActual.add(lote.getCantidad());
				}
				tfCantidad.setText(FormatUtil.getInstance().formateaNumero(linea.getCantidad().subtract(cantidadTotalActual), 3));
			}
			else{
				tfCantidad.setText(FormatUtil.getInstance().formateaNumero(linea.getCantidad(), 3));
			}
			lbArticulo.setText(linea.getArticulo().getDesArticulo());
			lbCantidad.setText(FormatUtil.getInstance().formateaNumero(linea.getCantidad(), 3));
			
			bLotesDocumentoOrigen = listaLotesDocumentoOrigen != null && !listaLotesDocumentoOrigen.isEmpty();
			btConsultarDocOrigen.setVisible(bLotesDocumentoOrigen);
			
			
		}
		else{
			throw new InitializeGuiException("No se ha encontrado línea para editar");
		}
	}
	
	@Override
	public void initializeFocus(){
		tfCantidad.requestFocus();
	}	
	
	/* ================================================================================================================= */
	/* ================================================= BUTTON ACTIONS ================================================ */
	/* ================================================================================================================= */
	
	/**
	 * PANEL : panelBotonera
	 */
	@Override
	public void realizarAccion(BotonBotoneraComponent boton) throws CajasServiceException{
		switch(boton.getClave()){
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
	
	/**
	 * BUTTON : ANTERIOR_REGISTRO
	 */
	public void irAnteriorRegistro(){
		log.info("irAnteriorRegistro() - Acción ejecutada");
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
		log.info("irSiguienteRegistro() - Acción ejecutada");
		if(tbLotes.getItems() != null && tbLotes.getItems() != null){
			int indice = tbLotes.getSelectionModel().getSelectedIndex();
			if(indice < tbLotes.getItems().size()){
				tbLotes.getSelectionModel().select(indice + 1);
				tbLotes.scrollTo(indice + 1);
			}
		}
	}
	
	/**
	 * BUTTON : BORRAR_REGISTRO
	 */
	private void borrarRegistro(){
		log.info("borrarRegistro() - Acción ejecutada");
		int index = tbLotes.getSelectionModel().getSelectedIndex();
		if(index >= 0){
			if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("El lote indicado será borrado, ¿Está seguro?"), getStage())){
				tbLotes.getItems().remove(index);
				tbLotes.getSelectionModel().select(tbLotes.getItems().size() - 1);
				BigDecimal cantidadTotal = BigDecimal.ZERO;
				for(CardosoLote loteTabla : tbLotes.getItems()){
					cantidadTotal = cantidadTotal.add(loteTabla.getCantidad());
				}
				tfCantidad.setText(FormatUtil.getInstance().formateaNumero(linea.getCantidad().subtract(cantidadTotal), 3));
				tfCantidad.requestFocus();
				
				tbLotes.getSelectionModel().select(0);
				tbLotes.scrollTo(0);
			}
		}
	}
	
	/**
	 * BUTTON : BORRAR_TODOS
	 */
	private void borrarTodos(){
		log.info("borrarTodos() - Acción ejecutada");
		if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Los lotes a la línea de venta serán borrados, ¿Está seguro?"), getStage())){
			tbLotes.getItems().clear();
			tfCantidad.setText(FormatUtil.getInstance().formateaNumero(linea.getCantidad(), 3));
			tfCantidad.requestFocus();
		}
	}
	
	/**
	 * BUTTON : btConsultarDocOrigen
	 */
	@FXML
	public void getLotesDisponibles(){
		BigDecimal pendiente = linea.getCantidad().subtract(cantidaLotesAsignada());
		if(BigDecimalUtil.isMayorACero(pendiente)){
			List<CardosoLote> lotesDisponibles = new ArrayList<CardosoLote>();
			lotesDisponibles.addAll(((CARDOSOLineaTicket) linea).getLotes() != null ? ((CARDOSOLineaTicket) linea).getLotes() : new ArrayList<CardosoLote>());
			lotesDisponibles.removeAll(tbLotes.getItems());
			getDatos().put(CardosoLotesLineaOrigenController.PARAMETRO_LOTES_DISPONIBLES, listaLotesDocumentoOrigen);
			getApplication().getMainView().showModalCentered(CardosoLotesLineaOrigenView.class, getDatos(), getStage());
			String loteSeleccionado = (String) getDatos().get(CardosoLotesLineaOrigenController.PARAMETRO_LOTE_SELECCION);
			if(loteSeleccionado != null){
				tfLote.setText(loteSeleccionado);
			}
			tfLote.requestFocus();
		}
		else{
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pueden asignar más lotes, ya están todos asignados."), getStage());
			btCerrar.requestFocus();
		}
	}
	
	/**
	 * BUTTON : btInsertar
	 */
	@FXML
	public void insertarLote(){
		if(esFormularioValido()){
			
			BigDecimal cantidad = BigDecimalUtil.redondear(FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText()).abs(), 3);
			tfCantidad.setText(FormatUtil.getInstance().formateaNumero(cantidad, 3));
			BigDecimal cantidadTotal = BigDecimal.ZERO;
			cantidadTotal = cantidaLotesAsignada();
	
			if(quedanLotesPorAsignar(cantidadTotal)){
				if(BigDecimalUtil.isIgualACero(cantidad)){
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La cantidad debe ser distinta de cero."), getStage());
					tfCantidad.requestFocus();
				}
				else{
					if(FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText()).compareTo(linea.getCantidad().subtract(cantidadTotal)) <= 0){
						if(loteValido(tfLote.getText())){
							CardosoLote lote = new CardosoLote();
							lote.setId(tfLote.getText());
							lote.setCantidad(cantidad);
							tbLotes.getItems().add(lote);
							tbLotes.getSelectionModel().select(tbLotes.getItems().size() - 1);
							tfLote.clear();
	
							cantidadTotal = BigDecimal.ZERO;
							for(CardosoLote loteTabla : tbLotes.getItems()){
								cantidadTotal = cantidadTotal.add(loteTabla.getCantidad());
							}
							tfCantidad.setText(FormatUtil.getInstance().formateaNumero(linea.getCantidad().subtract(cantidadTotal), 3));
	
							if(quedanLotesPorAsignar(cantidadTotal)){
								tfCantidad.requestFocus();
							}
							else{
								btCerrar.requestFocus();
							}
						}
						else{
							VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El lote no es válido, introduzca uno de la lista."), getStage());
							tfCantidad.requestFocus();
						}
					}
					else{
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La cantidad indicada es mayor de la disponible."), getStage());
						tfCantidad.requestFocus();
					}
				}
			}
			else{
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Imposible insertar el lote, ya están todos asignados."), getStage());
				btCerrar.requestFocus();
			}
			
		}
	}
	
	/**
	 * BUTTON : btCerrar
	 */
	@FXML
	public void accionCancelar(){
		if(quedanLotesPorAsignar(cantidaLotesAsignada())){
			if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Hay lotes sin asignar, la pantalla se cerrará, ¿Está seguro?"), getStage())){
				List<CardosoLote> lotes = tbLotes.getItems();
				((CARDOSOLineaTicket) linea).setLotes(lotes);
				getDatos().put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
				getStage().close();
			}
		}
		else{
			List<CardosoLote> lotes = tbLotes.getItems();
			((CARDOSOLineaTicket) linea).setLotes(lotes);
			getDatos().put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
			getStage().close();
		}
	}
		
	/* =============================================================================================================== */
	/* ================================================= VALIDATIONS ================================================= */
	/* =============================================================================================================== */
	
	private boolean loteValido(String sLote){
		if(bLotesDocumentoOrigen){
			for(CardosoLote lote : listaLotesDocumentoOrigen){
				if(lote.getId().equals(tfLote.getText())){
					return true;
				}
			}
			return false;
		}
		else{
			return true;
		}
	}
	
	private BigDecimal cantidaLotesAsignada(){
		BigDecimal cantidadActual = BigDecimal.ZERO;
		for(CardosoLote lote : tbLotes.getItems()){
			cantidadActual = cantidadActual.add(lote.getCantidad());
		}
		return cantidadActual;
	}
	
	private boolean quedanLotesPorAsignar(BigDecimal cantidadAsignada){
		return  cantidadAsignada.doubleValue() < linea.getCantidad().doubleValue();
	}
	
	public boolean esFormularioValido(){
		boolean valido;
		formulario.clearErrorStyle();
		formulario.setLote(tfLote.getText());
		formulario.setCantidad(tfCantidad.getText());
		lbError.setText("");

		Set<ConstraintViolation<CardosoFormularioLotes>> constraintViolations = ValidationUI.getInstance().getValidator().validate(formulario);
		if(constraintViolations.size() >= 1){
			ConstraintViolation<CardosoFormularioLotes> next = constraintViolations.iterator().next();
			formulario.setErrorStyle(next.getPropertyPath(), true);
			formulario.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			valido = false;
		}
		else{
			valido = true;
		}
		return valido;
	}
	
}
