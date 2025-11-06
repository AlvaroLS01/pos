package com.comerzzia.dinosol.pos.gui.ventas.promociones.opciones;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.DinoTicketManager;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionPromociones;
import com.comerzzia.dinosol.pos.services.cupones.DinoCuponesService;
import com.comerzzia.dinosol.pos.services.promociones.opciones.OpcionPromocionesDto;
import com.comerzzia.dinosol.pos.services.promociones.opciones.OpcionesPromocionService;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.opciones.AhorroPromoDto;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.opciones.OpcionPromocionesSeleccionadaDto;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@Component
public class SeleccionOpcionesPromocionController extends WindowController {
	
	private Logger log = Logger.getLogger(SeleccionOpcionesPromocionController.class);
	
	public static final String PARAM_CONTINUAR_COMPRA = "SeleccionOpcionesPromocionController.ContinuarCompra";
	
	@Autowired
	private OpcionesPromocionService opcionesPromocionService;
	
	@Autowired
	private DinoSesionPromociones sesionPromociones;
	
	@FXML
	private HBox hbOpciones;
	
	private String uidTicket;
	
	private IVisor visor;

	@Override
	public void initializeComponents() throws InitializeGuiException {
		visor = Dispositivos.getInstance().getVisor();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		try {
			TicketManager ticketManager = (TicketManager) getDatos().get(FacturacionArticulosController.TICKET_KEY);
			uidTicket = ticketManager.getTicket().getUidTicket();
			log.debug("initializeForm() - Abriendo pantalla de selección de promociones para el ticket " + uidTicket);
			
			Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpciones = calcularOpciones(ticketManager);
			
			pintarOpciones(resultadoOpciones, ticketManager);
			
			if(hbOpciones.getChildren().isEmpty()) {
				throw new InitializeGuiException(false);
			}
			
			if(visor instanceof DinoVisorPantallaSecundaria) {
				((DinoVisorPantallaSecundaria) visor).modoOpcionesPromocion(resultadoOpciones);
			}
		}
		catch (InitializeGuiException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("initializeForm() - Ha habido un error que imposibilita mostrar las opciones de promoción: " + e.getMessage(), e);
			throw new InitializeGuiException(I18N.getTexto("No se pueden mostrar las opciones de promoción. Contacte con un administrador."), e);
		}
	}

	private Map<OpcionPromocionesDto, DocumentoPromocionable> calcularOpciones(TicketManager ticketManager)
	        throws MarshallUtilException, TicketsServiceException, PromocionesServiceException, DocumentoException, LineaTicketException {
		TicketAparcadoBean ticketAparcado = crearTicketAparcado(ticketManager);
		Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpciones = new HashMap<OpcionPromocionesDto, DocumentoPromocionable>();
		for(OpcionPromocionesDto opcion : opcionesPromocionService.getOpciones()) {
			DocumentoPromocionable ticket = calcularAplicacionOpcionTicketCopia(ticketAparcado, opcion);
			if(ticket != null) {
				resultadoOpciones.put(opcion, ticket);
			}
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
			
			addBotonElegir(ticketManager, opcion, vbOpcion);
			
			if(BigDecimalUtil.isMayorACero(ahorroTotal)) {
				log.debug("pintarOpciones() - La opción " + opcion.getTitulo() + " se añade como seleccionable. Ahorro: " + ahorroTotal);
				addOpcion(vbOpcion);
			}
			else {
				log.debug("pintarOpciones() - La opción " + opcion.getTitulo() + " no se puede aplicar en este ticket");
			}
		}
	}

	private void addOpcion(VBox vbOpcion) {
		HBox.setHgrow(vbOpcion, Priority.ALWAYS);
		hbOpciones.getChildren().add(vbOpcion);
	}

	private TicketAparcadoBean crearTicketAparcado(TicketManager ticketManager) throws MarshallUtilException {
		log.debug("crearTicketAparcado() - Creando ticket aparcado base para simulaciones.");
		byte[] xmlTicket = MarshallUtil.crearXML(ticketManager.getTicket());
		log.debug("crearTicketAparcado() - Ticket base: " + new String(xmlTicket));
		TicketAparcadoBean ticketAparcado = new TicketAparcadoBean();
		ticketAparcado.setTicket(xmlTicket);
		return ticketAparcado;
	}

	private VBox crearVboxOpcion() {
		VBox vbOpcion = new VBox(10.0);
		vbOpcion.setAlignment(Pos.CENTER);
		return vbOpcion;
	}

	private void addTituloOpcion(OpcionPromocionesDto opcion, VBox vbOpcion) {
		Label labelTitulo = new Label(opcion.getTitulo());
		labelTitulo.getStyleClass().add("lb-destacado");
		labelTitulo.setMinHeight(50.0);
		vbOpcion.getChildren().add(labelTitulo);
	}

	private DocumentoPromocionable calcularAplicacionOpcionTicketCopia(TicketAparcadoBean ticketAparcado, OpcionPromocionesDto opcion)
	        throws TicketsServiceException, PromocionesServiceException, DocumentoException, LineaTicketException {
		log.debug("calcularAplicacionOpcionTicketCopia() - Realizando simulación de aplicación de promociones para opción " + opcion.getTitulo());
		
		DinoTicketManager ticketManagerAux = SpringContext.getBean(DinoTicketManager.class);
		ticketManagerAux.recuperarDatosTicket(ticketAparcado);
		DocumentoPromocionable ticket = (DocumentoPromocionable) ticketManagerAux.getTicket();
		
		sesionPromociones.aplicarOpcionPromociones(opcion, ticket);
		
		log.debug("calcularAplicacionOpcionTicketCopia() - Finalizada simulación de aplicación de promociones para opción " + opcion.getTitulo());
		
		return ticket;
	}

	private BigDecimal addPromocionesOpcion(OpcionPromocionesDto opcion, VBox vbOpcion, DocumentoPromocionable ticket) {
		VBox vbPromociones = new VBox(15.0);
		vbPromociones.setMinHeight(150.0);
		vbPromociones.setAlignment(Pos.TOP_CENTER);
		
		BigDecimal ahorroTotal = BigDecimal.ZERO;
		for(PromocionTicket promocion : ticket.getPromociones()) {
			List<String> promocionesOpcion = opcion.getPromociones();
			String idPromocionSap = promocion.getPromocion().getExtension(DinoCuponesService.ID_PROMOCION_SAP);
			BigDecimal ahorro = promocion.getImporteTotalAhorro();
			
			if(promocionesOpcion.contains(idPromocionSap) && BigDecimalUtil.isMayorACero(ahorro)) {
				log.debug("addPromocionesOpcion() - La promoción " + promocion.getIdPromocion() + " se ha podido aplicar en la simulación con un ahorro de " + ahorro);
				
				String tituloPromocion = promocion.getPromocion().getDescripcion();
				Label labelTituloPromocion = new Label(tituloPromocion);
				labelTituloPromocion.setMinWidth(250.0);
				labelTituloPromocion.setMaxWidth(250.0);
				
				String importeAhorro = promocion.getImporteTotalAhorroAsString() + " €";
				Label labelImporteAhorro = new Label(importeAhorro);
				labelImporteAhorro.setAlignment(Pos.CENTER_RIGHT);
				labelImporteAhorro.setMinWidth(100.0);
				labelImporteAhorro.setMaxWidth(100.0);
				
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
		String importeAhorroTotal = FormatUtil.getInstance().formateaImporte(ahorroTotal) + " €";
		Label labelImporteAhorroTotal = new Label(I18N.getTexto("AHORRO TOTAL: ") + importeAhorroTotal);
		labelImporteAhorroTotal.setAlignment(Pos.CENTER);
		labelImporteAhorroTotal.getStyleClass().add("texto-negrita");
		labelImporteAhorroTotal.setStyle("-fx-border-color: #85b037; -fx-border-width: 3px;");
		labelImporteAhorroTotal.setMinWidth(250.0);
		labelImporteAhorroTotal.setPrefWidth(250.0);
		labelImporteAhorroTotal.setMinHeight(40.0);
		
		HBox hbAhorroTotal = new HBox(labelImporteAhorroTotal);
		hbAhorroTotal.setSpacing(10.0);
		hbAhorroTotal.setAlignment(Pos.CENTER);
		
		vbOpcion.getChildren().add(hbAhorroTotal);
	}

	private void addBotonElegir(TicketManager ticketManager, OpcionPromocionesDto opcion, VBox vbOpcion) {
		Button btElegir = new Button(I18N.getTexto("Elegir ") + opcion.getTitulo());
		btElegir.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				try {
					log.debug("addBotonElegir() - Elegiendo opción " + opcion.getTitulo());
					sesionPromociones.aplicarOpcionPromociones(opcion, (DocumentoPromocionable) ticketManager.getTicket());
					setOpcionSeleccionadaTicket(opcion, (TicketVentaAbono) ticketManager.getTicket());
					getStage().close();
				}
				catch (Exception e) {
					log.error("addBotonElegir() - Error al elegir la opción: " + e.getMessage(), e);
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al seleccionar la opción. Contacte con un responsable."), e);
					getStage().close();
				}
			}
		});
		vbOpcion.getChildren().add(btElegir);
	}

	private void setOpcionSeleccionadaTicket(OpcionPromocionesDto opcion, TicketVentaAbono ticket) {
		OpcionPromocionesSeleccionadaDto opcionSeleccionada = new OpcionPromocionesSeleccionadaDto();
		
		opcionSeleccionada.setTituloOpcion(opcion.getTitulo());
		
		String textoTicket = opcion.getTextoTicket();
		textoTicket = textoTicket.replaceAll("\\{", "");
		textoTicket = textoTicket.replaceAll("\\}", "");

		for(PromocionTicket promocion : ticket.getPromociones()) {
			String idPromocionSap = promocion.getPromocion().getExtension(DinoCuponesService.ID_PROMOCION_SAP);
			if(opcion.getPromociones().contains(idPromocionSap) && !promocion.getIdTipoPromocion().equals(13L)) {
				textoTicket = textoTicket.replaceAll(idPromocionSap, promocion.getImporteTotalAhorroAsString());
				opcionSeleccionada.addAhorroPromocion(promocion.getIdPromocion(), promocion.getImporteTotalAhorro(), promocion.getTipoDescuento());
			}
		}
		
		for(String promocionSap : opcion.getPromociones()) {
			if(textoTicket.contains(promocionSap)) {
				textoTicket = "";
			}
		}
		
		opcionSeleccionada.setTextoTicket(textoTicket);
		
		List<AhorroPromoDto> ahorrosPromociones = opcionSeleccionada.getAhorrosPromociones();
		if(ahorrosPromociones != null && !ahorrosPromociones.isEmpty()) {
			((DinoCabeceraTicket) ticket.getCabecera()).setOpcionPromocionesSeleccionada(opcionSeleccionada);
		}
		else {
			log.warn("setOpcionSeleccionadaTicket() - No se ha aplicado ningún ahorro en la venta.");
		}
	}
	
	public void continuarCompra() {
		log.debug("accionCancelar() - Para el ticket " + uidTicket + " no se ha seleccionado ninguna opción de promoción.");
		getDatos().put(PARAM_CONTINUAR_COMPRA, true);
		super.accionCancelar();
	}

	@Override
	public void initializeFocus() {
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

}
