package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.trazabilidad.seleccion;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.comerzzia.iskaypet.pos.util.formatter.IskaypetFormatter;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.ContratoAnimalDto;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.DetailPets;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.detalles.DetallesTrazabilidadDto;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident.ItemsPetsIdent;
import com.comerzzia.iskaypet.pos.services.ticket.contrato.trazabilidad.TrazabilidadMascotasService;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

/**
 * 	GAP 172 TRAZABILIDAD ANIMALES
 */
@Component
public class AsignarTrazabilidadController extends WindowController implements Initializable {
	
	protected Logger log = Logger.getLogger(getClass());
	
	public static final String CLAVE_TICKET_MANAGER = "ticketManager";
	public static final String CLAVE_SE_HA_CANCELADO_PANTALLA = "seHaCanceladoPantalla";

	public static final String CLAVE_PARAMETRO_LISTA_LINEAS = "lstLineas";
	public static final String CLAVE_PARAMETRO_LINEA_ACTUALIZAR = "lineaTrazabilidad";
	public static final String CLAVE_PARAMETRO_CANCELAR = "cancelar";

	private List<IskaypetLineaTicket> lstLineasTrazabilidad;
	private IskaypetTicketManager ticketManager;
	private boolean esPantallaSeleccionarTraza;
	private boolean esDevolucion;
	private int contador;
	private IskaypetLineaTicket lineaActualizar = null;
	
	public static final String ID_CHIP = "CHIP";
	public static final String ID_ANILLA = "ANILLA";
	public static final String ID_CITES = "CITES";
	public static final String ID_VACIO = "-";
	public static final String ID_SEPARADOR = " | ";

	public static final String MENSAJE_ERROR_MASCOTA_DETALLE = "Error al consultar los detalles para la mascota";
	public static final String MENSAJE_ERROR_NECESITA_CLIENTE = "Debe tener un cliente asociado para poder realizar ventas de mascotas";

	
	@FXML
	protected Label lbCodArt, lbDesArt, lbContador;
	@FXML
	protected ComboBox<ItemsPetsIdent> cbIdentificadores;
	@FXML
	protected TextField tfIdCites, tfIdAnilla, tfIdChip;
	@FXML
	protected HBox hbSeleccionIdentificacion;
	@FXML
	protected HBox hbIntroduccionManual;

	@FXML
	protected Button btIntroduccionManual;
	@FXML
	protected Button btAceptar;
	@FXML
	protected Button btCancelar;

	private List<Integer> idLineasRevisadas;
	
	private List<ItemsPetsIdent> listaIdentificadores;
	
	@Autowired
	private TrazabilidadMascotasService trazabilidadService;
	
	private DetailPets detailPets;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registraEventoTeclado(new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					accionAceptar();
				}
				else if (event.getCode().equals(KeyCode.ESCAPE)) {
					accionCancelar();
				}
			}

		}, KeyEvent.KEY_RELEASED);
		
		// Impedir copiar y pegar
		tfIdCites.focusedProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
		tfIdCites.selectedTextProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
		tfIdAnilla.focusedProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
		tfIdAnilla.selectedTextProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
		tfIdChip.focusedProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
		tfIdChip.selectedTextProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());

		tfIdChip.setTextFormatter(IskaypetFormatter.getIntegerFormatWithLimitNullable(15));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		clearData();
		contador = 1;
		idLineasRevisadas = null;
		lineaActualizar = null;
		listaIdentificadores = new ArrayList<ItemsPetsIdent>();
		lstLineasTrazabilidad = (List<IskaypetLineaTicket>) getDatos().get(CLAVE_PARAMETRO_LISTA_LINEAS);
		ticketManager = (IskaypetTicketManager) getDatos().get(CLAVE_TICKET_MANAGER);

		detailPets = null;
		if(getDatos().containsKey(IskaypetFacturacionArticulosController.PARAM_DETAILS_PETS)) {
			detailPets = (DetailPets) getDatos().get(IskaypetFacturacionArticulosController.PARAM_DETAILS_PETS);
		} else {
			try {
				if (lstLineasTrazabilidad.isEmpty()) {
					throw new InitializeGuiException();
				}

				detailPets = trazabilidadService.consultarTrazabilidad(lstLineasTrazabilidad.get(0));
				if (detailPets == null) {
					throw new InitializeGuiException();
				}
			} catch (Exception e) {
				throw new InitializeGuiException(I18N.getTexto(AsignarTrazabilidadController.MENSAJE_ERROR_MASCOTA_DETALLE), e);
			}
		}
		esDevolucion = ticketManager.isEsDevolucion();
		
		inicializarInsercionTrazas();
		
		if(esDevolucion && listaIdentificadores != null && !listaIdentificadores.isEmpty()) {
			btIntroduccionManual.setDisable(true);
		} else {
			btIntroduccionManual.setDisable(false);
		}

	}
	

	@Override
	public void initializeFocus() {
		cbIdentificadores.requestFocus();
	}
	
	
	@SuppressWarnings("unchecked")
	private void cargarIdentificadores() {
		//Cargamos los identificadores para el articulo, cualquiera de las lineas nos vale, son el mismo
		List<ItemsPetsIdent> lstIdentificadores = trazabilidadService.getIdentificadores(lstLineasTrazabilidad.get(0));
		if(!lstIdentificadores.isEmpty()) {
			listaIdentificadores = lstIdentificadores;
			//Comprobamos las lineas del ticket y validamos que esté usado el identificador para no incluirlo en la lista de selección
			for (IskaypetLineaTicket linea : (List<IskaypetLineaTicket>)ticketManager.getTicket().getLineas()) {
				if(linea.getDetallesTrazabilidad() != null && !linea.getDetallesTrazabilidad().isEmpty()) {
					comprobarIdentificadorEnTicket(linea, linea.getDetallesTrazabilidad(), lstIdentificadores);
				}
			}
		}
	}
	
	private void comprobarIdentificadorEnTicket(IskaypetLineaTicket linea, List<DetallesTrazabilidadDto> lstDetalleTrazabilidad, List<ItemsPetsIdent> lstIdentificadores ) {
		String chip = "", anilla = "", cites = "";
		for(DetallesTrazabilidadDto detalles :lstDetalleTrazabilidad) {
			String fldName = detalles.getFldName();
			switch (fldName) {
				case ID_CHIP:
					if(StringUtils.isNotBlank(detalles.getIdentificacionTrazabilidad())) {
						chip = detalles.getIdentificacionTrazabilidad();
					}
					break;
				case ID_ANILLA:
					if(StringUtils.isNotBlank(detalles.getIdentificacionTrazabilidad())) {
						anilla = detalles.getIdentificacionTrazabilidad();
					}
					break;
				case ID_CITES:
					if(StringUtils.isNotBlank(detalles.getIdentificacionTrazabilidad())) {
						cites = detalles.getIdentificacionTrazabilidad();
					}
					break;
			}
		}
		
		for (int i = 0; i < lstIdentificadores.size(); i++) {
			ItemsPetsIdent itemsPetsIdent = lstIdentificadores.get(i);
			//Lo valores que no existen son nulos, para su comprobación le asignamos ""
			String itemChip = StringUtils.isNotBlank(itemsPetsIdent.getChip()) ? itemsPetsIdent.getChip() : "";
			String itemAnilla = StringUtils.isNotBlank(itemsPetsIdent.getAnilla())? itemsPetsIdent.getAnilla() : "";
			String itemCites = StringUtils.isNotBlank(itemsPetsIdent.getCites())? itemsPetsIdent.getCites() : "";
			if(itemsPetsIdent.getCodart().equals(linea.getCodArticulo()) && itemsPetsIdent.getDesglose1().equals(linea.getDesglose1()) 
					&& itemsPetsIdent.getDesglose2().equals(linea.getDesglose2()) && itemChip.equals(chip) 
					&& itemAnilla.equals(anilla) && itemCites.equals(cites)) {
				listaIdentificadores.remove(itemsPetsIdent);
			}
		} 
	}

	protected void clearData() {
		lbCodArt.setText(null);
		lbDesArt.setText(null);
		cbIdentificadores.setItems(null);
		clearDataIntroduccionManual();
	}
	protected void clearDataIntroduccionManual() {
		tfIdChip.setText(null);
		tfIdAnilla.setText(null);
		tfIdCites.setText(null);
	}
	
	protected void inicializarInsercionTrazas() {
		mostrarSiguienteArticulo();
	}
	
	protected void mostrarSiguienteArticulo()  {
		clearData();
		mostrarPantallaSeleccionarTraza();
		cargarDatos();
	}

	@SuppressWarnings("unchecked")
	protected void cargarDatos()  {
		for (IskaypetLineaTicket linea : lstLineasTrazabilidad) {
			if(idLineasRevisadas == null) {
				idLineasRevisadas = new ArrayList<Integer>();
				idLineasRevisadas.add(linea.getIdLinea());
				lineaActualizar = linea;
				break;
			}else {
				if(!idLineasRevisadas.contains(linea.getIdLinea())) {
					idLineasRevisadas.add(linea.getIdLinea());
					lineaActualizar = linea;
					contador++;
					break;
				}
			}
			
		}
		ArticuloBean articulo = lineaActualizar.getArticulo();
		
		lbCodArt.setText(articulo.getCodArticulo());
		lbDesArt.setText(articulo.getDesArticulo());
		lbContador.setText(contador + " / "+ lstLineasTrazabilidad.size());
		
		//Solo se cargan para las ventas y lo realizamos una si la lista está vacía
		if(!esDevolucion && listaIdentificadores.isEmpty()) {
			cargarIdentificadores();
		}
		
//		//Para la edicion comprobamos que tiene detalles de trazabilidad y si los tienes lo seteamos en el campo
//		if(lineaActualizar.getDetallesTrazabilidad() != null) {
//			for(DetallesTrazabilidadDto detalles :lineaActualizar.getDetallesTrazabilidad()) {
//				if(StringUtils.isNotBlank(detalles.getFldName())) {
//					String fldName = StringUtils.capitalize(detalles.getFldName().toLowerCase());
//					if(tfIdAnilla.getId().contains(fldName)) {
//						tfIdAnilla.setText(detalles.getIdentificacionTrazabilidad());
//					}else if(tfIdChip.getId().contains(fldName)) {
//						tfIdChip.setText(detalles.getIdentificacionTrazabilidad());
//					}else {
//						tfIdCites.setText(detalles.getIdentificacionTrazabilidad());
//					}
//				}
//			}
//		}
		
		if(esDevolucion && !(listaIdentificadores != null && !listaIdentificadores.isEmpty())) {
			
			
				for (IskaypetLineaTicket lineaOrigen : (List<IskaypetLineaTicket>)ticketManager.getTicketOrigen().getLineas()) {
					//Si tengo ya tengo los mismos identificadores de cantidades no devueltas en la lista mandada a devolver me salgo del bucle
					if(lstLineasTrazabilidad.size() == listaIdentificadores.size()) {
						break;
					}
					//Solo usamos los identificadores originales encontrado para el número de lines introducidas para comprobadas
					for (int i = 0; i < lstLineasTrazabilidad.size(); i++) {
						if(BigDecimalUtil.isIgualACero(lineaOrigen.getCantidadDevuelta()) && lstLineasTrazabilidad.get(i).getLineaDocumentoOrigen() == lineaOrigen.getIdLinea()) {
							
							if(lineaOrigen.getDetallesTrazabilidad() != null) {
								ItemsPetsIdent itemsPetsIdent = new ItemsPetsIdent();
								itemsPetsIdent.setCodart(lstLineasTrazabilidad.get(i).getCodArticulo());
								itemsPetsIdent.setDesglose1(lstLineasTrazabilidad.get(i).getDesglose1());
								itemsPetsIdent.setDesglose2(lstLineasTrazabilidad.get(i).getDesglose2());
								for(DetallesTrazabilidadDto detalles :lineaOrigen.getDetallesTrazabilidad()) {
										String fldName = detalles.getFldName();
										switch (fldName) {
											case ID_CHIP:
												if(StringUtils.isNotBlank(detalles.getIdentificacionTrazabilidad())) {
													itemsPetsIdent.setChip(detalles.getIdentificacionTrazabilidad());
												}
												break;
											case ID_ANILLA:
												if(StringUtils.isNotBlank(detalles.getIdentificacionTrazabilidad())) {
													itemsPetsIdent.setAnilla(detalles.getIdentificacionTrazabilidad());
												}
												break;
											case ID_CITES:
												if(StringUtils.isNotBlank(detalles.getIdentificacionTrazabilidad())) {
													itemsPetsIdent.setCites(detalles.getIdentificacionTrazabilidad());
												}
												break;
										}
									}
								listaIdentificadores.add(itemsPetsIdent);
									
								}
						}
				}
			}
		}
		if(!listaIdentificadores.isEmpty()) {
			cbIdentificadores.setItems(FXCollections.observableArrayList(listaIdentificadores));
		}

		if (lineaActualizar.getItemsPetIdent() != null){
			ItemsPetsIdent itemsPetsIdent = lineaActualizar.getItemsPetIdent();
			ObservableList<ItemsPetsIdent> currentItems = cbIdentificadores.getItems();

			if(currentItems == null){
				currentItems = FXCollections.observableArrayList();
			}
			currentItems.add(itemsPetsIdent);
			cbIdentificadores.setItems(currentItems);
			cbIdentificadores.getSelectionModel().select(itemsPetsIdent);
		}
		
		//Ponemos a disabled dependiendo de si tiene marcado los ids a S o N en x_detail_pets_tbl
		if ("S".equals(detailPets.getIdChip())) {
			tfIdChip.setDisable(false);
		}
		else {
			tfIdChip.setDisable(true);
		}
		if ("S".equals(detailPets.getIdAnilla())) {
			tfIdAnilla.setDisable(false);
		}
		else {
			tfIdAnilla.setDisable(true);
		}
		if ("S".equals(detailPets.getIdCites())) {
			tfIdCites.setDisable(false);
		}
		else {
			tfIdCites.setDisable(true);
		}
	}
	
	protected void mostrarPantallaSeleccionarTraza() {
		hbSeleccionIdentificacion.setVisible(true);
		hbSeleccionIdentificacion.setManaged(true);
		hbIntroduccionManual.setVisible(false);
		hbIntroduccionManual.setManaged(false);
		//Botón de introducción manual solo se muestra en ventas y devoluciones sin origen
		btIntroduccionManual.setVisible(true);
		esPantallaSeleccionarTraza = true;
		cbIdentificadores.requestFocus();
		
		this.getStage().setHeight(253.0);
	}
	
	protected void mostrarPantallaInsertarTraza() {
		hbSeleccionIdentificacion.setVisible(false);
		hbSeleccionIdentificacion.setManaged(false);
		hbIntroduccionManual.setVisible(true);
		hbIntroduccionManual.setManaged(true);
		btIntroduccionManual.setVisible(false);	
		esPantallaSeleccionarTraza = false;
		tfIdChip.requestFocus();
		
		this.getStage().setHeight(338.0);
	}
	
	@FXML
    public void accionAceptar() {
        log.debug("accionAceptar()");
        
        if(!validarFormulario()) {
        	return;
        }
        
        if(esPantallaSeleccionarTraza) {
        	ItemsPetsIdent identificador = cbIdentificadores.getSelectionModel().getSelectedItem();
        	//Todos los identificadores tienen el patron CHIP|ANILLA|CITES Si son de introduccion por combo no permitira edicion
    		addDetalleTrazabilidad(ID_CHIP, identificador.getChip(), false);
    		addDetalleTrazabilidad(ID_ANILLA, identificador.getAnilla(), false);
    		addDetalleTrazabilidad(ID_CITES, identificador.getCites(), false);
    		
    		//agregamos a la lista el anterior y seteamos el nuevo
    		if(lineaActualizar.getItemsPetIdent() != null) {
    			listaIdentificadores.add(lineaActualizar.getItemsPetIdent());
    		}
    		lineaActualizar.setItemsPetIdent(cbIdentificadores.getValue());
    		
    		//Lo eliminamos de la lista de identificadores cargadas en el pos
    		listaIdentificadores.remove(identificador);
        }else {
        	//Introducción manual
    		addDetalleTrazabilidad(ID_CHIP, tfIdChip.getText(), true);
    		addDetalleTrazabilidad(ID_ANILLA, tfIdAnilla.getText(), true);
    		addDetalleTrazabilidad(ID_CITES, tfIdCites.getText(), true);

			ItemsPetsIdent itemsPetsIdent = new ItemsPetsIdent();
			if (StringUtils.isNotBlank(tfIdChip.getText())) {
				itemsPetsIdent.setChip(tfIdChip.getText());
			}
			if (StringUtils.isNotBlank(tfIdAnilla.getText())) {
				itemsPetsIdent.setAnilla(tfIdAnilla.getText());
			}
			if (StringUtils.isNotBlank(tfIdCites.getText())) {
				itemsPetsIdent.setCites(tfIdCites.getText());
			}

    		lineaActualizar.setItemsPetIdent(itemsPetsIdent);
        }
        
        if(lineaActualizar.getContratoAnimal() != null && !lineaActualizar.getDetallesTrazabilidad().isEmpty()) {

        	ContratoAnimalDto contrato = lineaActualizar.getContratoAnimal();
        	
        	String identificadorCompuesto = String.join(ID_SEPARADOR, ID_CHIP, ID_ANILLA, ID_CITES);
			//Tenemos que respetar las posiciones, siempre serán las mimas
			for(DetallesTrazabilidadDto detalles : lineaActualizar.getDetallesTrazabilidad()) {
				String fldName = detalles.getFldName();
				//Sustituimos en el identificador el valor que tenemos en el detalle
				String[] identificadores = {ID_CHIP, ID_ANILLA, ID_CITES};
				if (StringUtils.isNotBlank(fldName) && Arrays.stream(identificadores).anyMatch(fldName::equalsIgnoreCase)) {
					if(StringUtils.isNotBlank(detalles.getIdentificacionTrazabilidad())){
						identificadorCompuesto = identificadorCompuesto.replace(fldName, detalles.getIdentificacionTrazabilidad());
					}
				}
			}
			
			//Comprobamos las sustiticiones, si no han sido reemplazadas le asignamos para el contrato el caracter -
			if(identificadorCompuesto.contains(ID_CHIP)) {
				identificadorCompuesto = identificadorCompuesto.replace(ID_CHIP, ID_VACIO);
			}
			if(identificadorCompuesto.contains(ID_ANILLA)) {
				identificadorCompuesto = identificadorCompuesto.replace(ID_ANILLA, ID_VACIO);
			}
			if(identificadorCompuesto.contains(ID_CITES)) {
				identificadorCompuesto = identificadorCompuesto.replace(ID_CITES, ID_VACIO);
			}
			
			contrato.setNumIden(identificadorCompuesto);
        	
        }


        if(contador == lstLineasTrazabilidad.size()) {
			getDatos().put(CLAVE_PARAMETRO_LINEA_ACTUALIZAR, lineaActualizar);
        	getStage().close();
        	return;
        }
        
        if(contador < lstLineasTrazabilidad.size()) {
        	mostrarSiguienteArticulo();
        }
	}

	private void deleteDetalleTrazabilidad(String fldNameEliminar) {
		for (int i = 0; i < lineaActualizar.getDetallesTrazabilidad().size(); i++) {
			if(fldNameEliminar.equals(lineaActualizar.getDetallesTrazabilidad().get(i).getFldName())) {
				lineaActualizar.getDetallesTrazabilidad().remove(i);
				break;
			}
		}
	}

	public void addDetalleTrazabilidad(String fldName, String identificador, boolean esEditable) {
		// Primero eliminamos el intificador
		deleteDetalleTrazabilidad(fldName);
		//Si tiene valor lo add a la linea
		if (StringUtils.isNotBlank(identificador)) {
			DetallesTrazabilidadDto detalle = new DetallesTrazabilidadDto();
			detalle.setFldName(fldName);
			detalle.setIdentificacionTrazabilidad(identificador);
			detalle.setEditable(esEditable);
			lineaActualizar.getDetallesTrazabilidad().add(detalle);
		}
	}


	@FXML
    public void accionCancelar() {
		if(esPantallaSeleccionarTraza) {
			if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("No se ha asignado identificación animal, la pantalla se cerrará, ¿Está seguro?"), getStage())) {
				getDatos().put( CLAVE_PARAMETRO_CANCELAR, Boolean.TRUE);
				super.accionCancelar();
			}
		}
		else {
			boolean cerrar = true;
			
			if(pantallaIntroduccionManualTieneDatos()) {
				cerrar = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Quiere cancelar la introducción manual de identificación animal? "), getStage());
			}
			
			if(cerrar) {
				mostrarPantallaSeleccionarTraza();
			}
		}
	}
	
	@FXML
    public void accionIntroduccionManual() {
		mostrarPantallaInsertarTraza();
		btIntroduccionManual.setVisible(false);
	}
	
	protected boolean validarFormulario() {
		if(esPantallaSeleccionarTraza) {
			if(cbIdentificadores.getSelectionModel().getSelectedItem()==null) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Debe seleccionar un identificador válido"), getStage());
				return false;
			}
		}
		else {
			if(StringUtils.isBlank(tfIdChip.getText()) &&
					StringUtils.isBlank(tfIdCites.getText()) &&
					StringUtils.isBlank(tfIdAnilla.getText()) 
				) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Debe indicar algún identificador válido"), getStage());
				tfIdChip.requestFocus();
				return false;
			}
		}
		
		return true;
	}
	
	private boolean pantallaIntroduccionManualTieneDatos() {
		if(
			StringUtils.isNotEmpty(tfIdChip.getText()) ||
			StringUtils.isNotEmpty(tfIdAnilla.getText()) ||
			StringUtils.isNotEmpty(tfIdCites.getText())
			){
			return true;
		}
		return false;
	}
	
}
