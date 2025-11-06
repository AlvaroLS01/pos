package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.busquedas;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.DinoTicketManager;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionPromociones;
import com.comerzzia.dinosol.pos.services.cupones.DinoCuponesService;
import com.comerzzia.dinosol.pos.services.promociones.opciones.OpcionPromocionesDto;
import com.comerzzia.dinosol.pos.services.promociones.opciones.OpcionesPromocionService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.TicketVentaDocumentoVisorConverter;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.LineaResultadoBusqGui;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

@Controller
@Primary
public class DinoBuscarArticulosController extends BuscarArticulosController {
	
	private Logger log = Logger.getLogger(DinoBuscarArticulosController.class);
	
	public static final String PARAM_TICKET_MANAGER_CURSO = "DinoBuscarArticulosController.TicketManagerEnCurso";
	
	@Autowired
	private OpcionesPromocionService opcionesPromocionService;
	
	@Autowired
	private DinoSesionPromociones sesionPromociones;
	
	@Autowired
	protected TicketVentaDocumentoVisorConverter visorConverter;
	
	@Autowired
	private AuditoriasService auditoriasService;
	
	@FXML
	private Label lbPrecioLineal;
	
	@FXML
	private HBox hbOpciones;
	
	private boolean interceptar;
	private boolean decimalKey;
	
	private IVisor visor;
	
	private TicketManager ticketManagerEnCurso;
	
	private int numBusquedas;
	
	private Clip clipError;
	
	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();
		
		ticketManagerEnCurso = (TicketManager) getDatos().get(PARAM_TICKET_MANAGER_CURSO);
		
		desactivarControlSeguridad();
	}

	@Override
	public void initializeFocus() {
		tfCodigoArt.requestFocus();
	}

	@Override
	public void accionBuscar() {
		guardarAuditoria();
		
		if(numBusquedas > 0) {
			reproducirSonidoError();
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No es posible realizar la búsqueda de más de un artículo sin volver a la pantalla de ventas."), getStage());
			return;
		}
		else {
			activarControlSeguridad();
		}
		
		lbPrecioLineal.setText("");
		
		super.accionBuscar();

		if(StringUtils.isNotBlank(tfCodigoArt.getText()) && StringUtils.isBlank(tfDescripcion.getText())) {
			tfCodigoArt.selectAll();
			tfCodigoArt.requestFocus();
		}
		else {
			tbArticulos.requestFocus();
		}
	}

	private void guardarAuditoria() {
		AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setTipo("BÚSQUEDA ARTÍCULO");
		auditoria.setCodart(StringUtils.abbreviate(tfCodigoArt.getText(), 20));
		auditoriasService.guardarAuditoria(auditoria);
	}

	@Override
	public void initializeComponents() {
		super.initializeComponents();
		
		addSeleccionarTodoEnFoco(tfCodigoArt);
		addSeleccionarTodoEnFoco(tfDescripcion);
		
		tfCodigoArt.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event) {
				interceptar = false;
				decimalKey = false;
				// "." de teclado numérico lo cambiamos por el decimal separator
				if (event.getCode().equals(KeyCode.DECIMAL)) {
					decimalKey = true;
					return;
				}
				// Interceptamos los siguientes:
				if (event.getCode().isLetterKey() || event.getCode().isWhitespaceKey() || event.getText().equalsIgnoreCase("ñ")) {
					interceptar = true;
				}
			}
		});

		tfCodigoArt.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event) {
				if (((TextField) event.getSource()).isEditable()) {
					if (interceptar || !StringUtils.isNumeric(event.getCharacter())) {
						event.consume();
						interceptar = false;
					}
					else if (decimalKey) {
						event.consume();
						((TextField) event.getSource()).appendText(String.valueOf(FormatUtil.getInstance().getDecimalSeparator()));
						decimalKey = false;
					}
				}
			}
		});
		
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		tfCodigoArt.setUserData(keyboardDataDto);
		
		visor = Dispositivos.getInstance().getVisor();
	}
	
	@Override
	protected void cargarBotonesStock(LineaResultadoBusqGui lineaSeleccionada) {
		ocultarBotonesStock();
	}
	
	@Override
	protected void buscarArticulosSucceeded(List<ArticuloBuscarBean> articulosF) {
		List<LineaResultadoBusqGui> lineasRes = new ArrayList<>();
        
	    if (articulosF.isEmpty()) {
	        tbArticulos.setPlaceholder(new Label(I18N.getTexto("No se han encontrado resultados")));
	    }
	    for(ArticuloBuscarBean articulo: articulosF){
	        lineasRes.add(new DinoLineaResultadoBusqGui(articulo));
	    }

	    lineas.addAll(lineasRes);
	    
	    tbArticulos.getSelectionModel().select(0);
	    tbArticulos.getFocusModel().focus(0);
	    
		numBusquedas++;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	    super.initialize(url, rb);
	    
	    TableColumn<LineaResultadoBusqGui, String> tcEanPrincipal = new TableColumn<LineaResultadoBusqGui, String>();
	    tcEanPrincipal.setText(I18N.getTexto("EAN"));
	    double anchoColumna = 150.0;
		tcEanPrincipal.setPrefWidth(anchoColumna);
	    tcEanPrincipal.setMinWidth(anchoColumna);
	    tcEanPrincipal.setMaxWidth(anchoColumna);
	    tcEanPrincipal.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaResultadoBusqGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<LineaResultadoBusqGui, String> cdf) {
                return ((DinoLineaResultadoBusqGui) cdf.getValue()).getEanPrincipal();
            }
        });
	    tbArticulos.getColumns().remove(4);
	    tbArticulos.getColumns().add(tcEanPrincipal);
	}
	
	@Override
	protected void lineaSeleccionadaChanged() {
	    super.lineaSeleccionadaChanged();
	    
	    if(tbArticulos.getItems().isEmpty()) {
	    	tfDetallePrecio.setText("");
	    	lbPrecioLineal.setText("");
	    }
	    else {
		    LineaTicket linea = (LineaTicket) ticketManager.getTicket().getLineas().get(0);
		    linea.setCantidad(BigDecimal.ONE);
		    ticketManager.recalcularConPromociones();
		    tfDetallePrecio.setText(FormatUtil.getInstance().formateaImporte(linea.getPrecioTotalConDto()));
		    lbPrecioLineal.setText(FormatUtil.getInstance().formateaImporte(linea.getPrecioTotalSinDto()));
		    
		    mostrarOpcionesPromocion();
	    } 
	}

	private void mostrarOpcionesPromocion() {
		try {
		    Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpciones = calcularOpciones(ticketManager);
			pintarOpciones(resultadoOpciones, ticketManager);
			mostrarOpcionesEnVisor(resultadoOpciones);
		}
		catch (Exception e) {
			log.error("mostrarOpcionesPromocion() - Ha habido un error al pintar las opciones de promoción: " + e.getMessage(), e);
		}
	}

	private void mostrarOpcionesEnVisor(Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpciones) {
		if(visor instanceof DinoVisorPantallaSecundaria && !hbOpciones.getChildren().isEmpty()) {
			((DinoVisorPantallaSecundaria) visor).modoOpcionesPromocion(resultadoOpciones);
		}
		else {
			if(ticketManagerEnCurso.getTicket() == null || ticketManagerEnCurso.getTicket().getLineas() == null ||  ticketManagerEnCurso.getTicket().getLineas().isEmpty()) {
				visor.modoEspera();
			}
			else {
				((DinoVisorPantallaSecundaria)visor).modoVenta(visorConverter.convert((TicketVentaAbono) ticketManagerEnCurso.getTicket()));
			}
		}
	}
	
	private Map<OpcionPromocionesDto, DocumentoPromocionable> calcularOpciones(TicketManager ticketManager)
	        throws MarshallUtilException, TicketsServiceException, PromocionesServiceException, DocumentoException, LineaTicketException {
		TicketAparcadoBean ticketAparcado = crearTicketAparcado(ticketManager);
		Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpciones = new HashMap<OpcionPromocionesDto, DocumentoPromocionable>();
		for(OpcionPromocionesDto opcion : opcionesPromocionService.getOpciones()) {
			DocumentoPromocionable ticket = calcularAplicacionOpcionTicketCopia(ticketAparcado, opcion);
			resultadoOpciones.put(opcion, ticket);
		}
		return resultadoOpciones;
	}

	private void pintarOpciones(Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpciones, TicketManager ticketManager) {
		hbOpciones.getChildren().clear();
		
		for(OpcionPromocionesDto opcion : resultadoOpciones.keySet()) {
			DocumentoPromocionable ticket = resultadoOpciones.get(opcion);
			
			VBox vbOpcion = crearVboxOpcion();
			
			addTituloOpcion(opcion, vbOpcion);
			
			BigDecimal ahorroTotal = addPromocionesOpcion(opcion, vbOpcion, ticket);
			
			addAhorroTotalLabel(vbOpcion, ahorroTotal);
			
			if(BigDecimalUtil.isMayorACero(ahorroTotal)) {
				addOpcion(vbOpcion);
			}
		}
		
		hbOpciones.setPadding(new Insets(0.0, 200.0, 0.0, 0.0));
	}

	private void addOpcion(VBox vbOpcion) {
		HBox.setHgrow(vbOpcion, Priority.ALWAYS);
		hbOpciones.getChildren().add(vbOpcion);
	}

	private TicketAparcadoBean crearTicketAparcado(TicketManager ticketManager) throws MarshallUtilException {
		byte[] xmlTicket = MarshallUtil.crearXML(ticketManager.getTicket());
		TicketAparcadoBean ticketAparcado = new TicketAparcadoBean();
		ticketAparcado.setTicket(xmlTicket);
		return ticketAparcado;
	}

	private VBox crearVboxOpcion() {
		VBox vbOpcion = new VBox(10.0);
		vbOpcion.setAlignment(Pos.TOP_LEFT);
		return vbOpcion;
	}

	private void addTituloOpcion(OpcionPromocionesDto opcion, VBox vbOpcion) {
		Label labelTitulo = new Label(opcion.getTitulo());
		labelTitulo.getStyleClass().add("texto-negrita");
		vbOpcion.getChildren().add(labelTitulo);
	}

	private DocumentoPromocionable calcularAplicacionOpcionTicketCopia(TicketAparcadoBean ticketAparcado, OpcionPromocionesDto opcion)
	        throws TicketsServiceException, PromocionesServiceException, DocumentoException, LineaTicketException {
		DinoTicketManager ticketManagerAux = SpringContext.getBean(DinoTicketManager.class);
		ticketManagerAux.recuperarDatosTicket(ticketAparcado);
		
		if(ticketManagerEnCurso.getTicket().getCabecera().getDatosFidelizado() != null) {
			FidelizacionBean datosFidelizado = new FidelizacionBean();
			BeanUtils.copyProperties(ticketManagerEnCurso.getTicket().getCabecera().getDatosFidelizado(), datosFidelizado);
			ticketManagerAux.getTicket().getCabecera().setDatosFidelizado(datosFidelizado);
		}
		
		DocumentoPromocionable ticket = (DocumentoPromocionable) ticketManagerAux.getTicket();
		
		sesionPromociones.aplicarOpcionPromociones(opcion, ticket);
		return ticket;
	}

	private BigDecimal addPromocionesOpcion(OpcionPromocionesDto opcion, VBox vbOpcion, DocumentoPromocionable ticket) {
		VBox vbPromociones = new VBox(15.0);
		
		BigDecimal ahorroTotal = BigDecimal.ZERO;
		for(PromocionTicket promocion : ticket.getPromociones()) {
			List<String> promocionesOpcion = opcion.getPromociones();
			String idPromocionSap = promocion.getPromocion().getExtension(DinoCuponesService.ID_PROMOCION_SAP);
			BigDecimal ahorro = promocion.getImporteTotalAhorro();
			
			if(promocionesOpcion.contains(idPromocionSap) && BigDecimalUtil.isMayorACero(ahorro)) {
				String tituloPromocion = promocion.getPromocion().getDescripcion();
				Label labelTituloPromocion = new Label(tituloPromocion);
				labelTituloPromocion.setMinWidth(250.0);
				labelTituloPromocion.setMaxWidth(250.0);
				
				String importeAhorro = promocion.getImporteTotalAhorroAsString() + " €";
				Label labelImporteAhorro = new Label(importeAhorro);
				labelImporteAhorro.setAlignment(Pos.CENTER_RIGHT);
				labelImporteAhorro.setMinWidth(50.0);
				labelImporteAhorro.setMaxWidth(50.0);
				
				HBox hbPromocion = new HBox(labelTituloPromocion, labelImporteAhorro);
				hbPromocion.setSpacing(10.0);
				
				vbPromociones.getChildren().add(hbPromocion);
				
				ahorroTotal = ahorroTotal.add(ahorro);					
			}			
		}
		
		vbOpcion.getChildren().add(vbPromociones);
		return ahorroTotal;
	}

	private void addAhorroTotalLabel(VBox vbOpcion, BigDecimal ahorroTotal) {
		Label lbTitulo = (Label) vbOpcion.getChildren().get(0);
		
		String importeAhorroTotal = FormatUtil.getInstance().formateaImporte(ahorroTotal) + " €";
		
		lbTitulo.setText(lbTitulo.getText() + I18N.getTexto(". AHORRO: ") + importeAhorroTotal);
	}
	
	private void reproducirSonidoError() {
		try {
			if (clipError == null) {
				ClassPathResource resource = new ClassPathResource("error.wav");
			
				clipError = AudioSystem.getClip();
				clipError.open(AudioSystem.getAudioInputStream(resource.getFile()));					
			}
			clipError.setFramePosition(0);
			clipError.start();				
		}
		catch (Exception e) {
			log.error("reproducirSonidoError() - Ha habido un problema al reproducir el WAV: " + e.getMessage(), e);
		}
	}

	private void activarControlSeguridad() {
		tfCodigoArt.setEditable(false);
		tfDescripcion.setEditable(false);
		tfCodigoArt.getStyleClass().add("solo-lectura");
		tfDescripcion.getStyleClass().add("solo-lectura");
	}

	private void desactivarControlSeguridad() {
		numBusquedas = 0;
		tfCodigoArt.setEditable(true);
		tfDescripcion.setEditable(true);
		tfCodigoArt.getStyleClass().remove("solo-lectura");
		tfDescripcion.getStyleClass().remove("solo-lectura");
	}
	
	@Override
	public void accionCancelar() {
		if(numBusquedas > 0) {
			reproducirSonidoError();
		}
		super.accionCancelar();
	}	
}
