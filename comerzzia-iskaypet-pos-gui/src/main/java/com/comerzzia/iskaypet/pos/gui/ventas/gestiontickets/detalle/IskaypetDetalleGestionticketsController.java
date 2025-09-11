package com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.detalle;

import static com.comerzzia.iskaypet.pos.devices.IskaypetFidelizacion.PARAMETRO_APLICA_CARENCIA;
import static com.comerzzia.iskaypet.pos.devices.IskaypetFidelizacion.PARAMETRO_APLICA_FIDELIZACION;
import static com.comerzzia.iskaypet.pos.devices.IskaypetFidelizacion.PARAMETRO_EMAIL;
import static com.comerzzia.iskaypet.pos.devices.IskaypetFidelizacion.PARAMETRO_SALDO_NO_DISPONIBLE;
import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraTipoPagosEnum.EFECTIVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import com.comerzzia.iskaypet.pos.gui.contrato.mascota.EnvioContratoMascotaGestionDocumentosView;
import com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.IskaypetGestionTicketGui;
import com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.cupones.IskaypetCuponesController;
import com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.cupones.IskaypetCuponesView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.ContratoAnimalDto;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.mascotas.DatosCabeceraContrato;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosBean;
import com.comerzzia.iskaypet.pos.persistence.ticket.lineas.TextoPromocion;
import com.comerzzia.iskaypet.pos.services.evicertia.IskaypetEvicertiaService;
import com.comerzzia.iskaypet.pos.services.impresion.IskaypetServicioImpresion;
import com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos.IskaypetPromocionTextoBean;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.IskaypetCabeceraTicket;
import com.comerzzia.iskaypet.pos.services.ticket.contrato.trazabilidad.TrazabilidadMascotasService;
import com.comerzzia.iskaypet.pos.services.ticket.cupones.IskaypetCuponEmitidoTicket;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.services.ventas.pagos.mascotas.ServicioContratoMascotas;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionticketsController;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.impuestos.porcentajes.PorcentajeImpuestoBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@Primary
@Controller
@SuppressWarnings("rawtypes")
public class IskaypetDetalleGestionticketsController extends DetalleGestionticketsController {

	private static final Logger log = Logger.getLogger(IskaypetDetalleGestionticketsController.class.getName());

	public static final String TICKET = "ticket";
	public static final String PAPERLESS = "paperLess";
	public static final String EMPRESA = "empresa";

	public static final String LINEAS = "lineas";

	public static final String IMPUESTOS = "impuestos";

	public static final String BIG_DECIMAL_100 = "bigDecimalCien";
	public static final String IMPRIMIR_LOGO = "imprimirLogo";
	public static final String PARAMETRO_IMPRIMIR_LOGO = "X_POS.IMPRIMIR_LOGO";

	public static final String APERTURA_CAJON = "aperturaCajon";
	public static final String URL_QR = "urlQR";
	public static final String URL_TEMP_QR = "urlTemporalQR";
	public static final String ES_GESTION = "esGestion";

	public static final String ES_COPIA = "esCopia";

	public static final String TIPO_ACCESO_PROMOCION = "PROMOCION";
	public static final String TIPO_ACCESO_CUPON = "CUPON";

    public static final String QR_TEMPORAL = "QR_TEMPORAL";

	// GAP 117 RECUPERACIÓN DE CONTRATOS DESDE EL POS
	@FXML
	private Button btnImprimirContrato;
	
	@FXML
	private Button btnImprimirCupones;

	@Autowired
	protected ServicioContratoMascotas servicioContratoMascotas;


	@Autowired
	protected VariablesServices variablesServices;

	@Override
	public void initializeComponents() {
		super.initializeComponents();
		// Seteamos la imagen del button contrato, en el caso de que no encuentre la ruta, lo ignoramos y no ponemos imangen
		try {
			Image image = new Image("skins/iskaypet/com/comerzzia/iskaypet/pos/gui/ventas/tickets/articulos/contrato/contract.png");
			ImageView imageView = new ImageView(image);
			btnImprimirContrato.setGraphic(imageView);
		}
		catch (Exception ignore) {
		}
	}

	@Override
	public void refrescarDatosPantalla() throws InitializeGuiException {

		try {
			log.debug("refrescarDatosPantalla()");
			log.debug("Obtenemos el XML del ticket que queremos visualizar");
			ticket = tickets.get(posicionActual);

			Long idTipoDoc = null;
			byte[] ticketXML;
			String ticketXMLStr = (String) getDatos().get(CLAVE_PARAMETRO_TICKET_XML);
			if (StringUtils.isNotBlank(ticketXMLStr)) {
				ticketXML = ticketXMLStr.getBytes();
				if (ticket instanceof IskaypetGestionTicketGui) {
					idTipoDoc = ((IskaypetGestionTicketGui) ticket).getIdTipoDoc();
				}
			}
			else {
				TicketBean ticketConsultado = ticketsService.consultarTicket(ticket.getUidTicket(), sesion.getAplicacion().getUidActividad());
				ticketXML = ticketConsultado.getTicket();
				idTipoDoc = ticketConsultado.getIdTipoDocumento();
			}

			TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(idTipoDoc);
			if (documento.getFormatoImpresion().equals(TipoDocumentoBean.PROPIEDAD_FORMATO_IMPRESION_NO_CONFIGURADO)) {
				if (getStage() != null && getStage().isShowing()) {
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No es posible visualizar este tipo de documento"), getStage());
				}
				else {
					Platform.runLater(
					        () -> VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No es posible visualizar este tipo de documento"), IskaypetDetalleGestionticketsController.this.getStage()));
				}

				setTicketText("<html><body></body></html>");
				return;
			}

			ticketOperacion = (TicketVenta) MarshallUtil.leerXML(ticketXML, getTicketClasses(documento).toArray(new Class[0]));
			if (ticketOperacion == null) {
				log.error("refrescarDatosPantalla()- Error leyendo ticket otriginal");
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error leyendo información de ticket."), getStage());
				throw new InitializeGuiException(false);
			}

			ticketOperacion.getCabecera().setDocumento(sesion.getAplicacion().getDocumentos().getDocumento(ticketOperacion.getCabecera().getTipoDocumento()));
			btnTicketRegalo.setDisable(!sesion.getAplicacion().getDocumentos().getDocumento(ticketOperacion.getCabecera().getTipoDocumento()).getPermiteTicketRegalo());

			try {

				parsearAdicionales(ticketOperacion);
				parsearPromocionesAdicionales(ticketOperacion);

				Map<String, Object> mapaParametros = new HashMap<>();
				parsearLineasCaracteresEspeciales(ticketOperacion.getLineas());
				parsearPromocionesCaracteresEspeciales(ticketOperacion.getPromociones());
				ofuscarNombreCajero(ticketOperacion);
				mapaParametros.put(TICKET, ticketOperacion);
				mapaParametros.put(LINEAS, generarLineasAgrupadas((List<IskaypetLineaTicket>) ticketOperacion.getLineas(), false));
				mapaParametros.put(IMPUESTOS, generarImpuestosAgrupados((List<SubtotalIvaTicket>) ticketOperacion.getCabecera().getSubtotalesIva()));
				mapaParametros.put(BIG_DECIMAL_100, BigDecimalUtil.CIEN);
				mapaParametros.put(APERTURA_CAJON, requiereAbrirCajon(ticketOperacion, Boolean.TRUE));
				mapaParametros.put(URL_QR, variablesService.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
				mapaParametros.put(URL_TEMP_QR, variablesService.getVariableAsString(QR_TEMPORAL));
				mapaParametros.put(ES_GESTION, true);
				mapaParametros.put(IMPRIMIR_LOGO, requierImprimirLogo(variablesServices));
				setTicketText(IskaypetServicioImpresion.imprimirPantalla(ticketOperacion.getCabecera().getFormatoImpresion(), mapaParametros));
			}
			catch (Exception exception) {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), exception);
				throw new InitializeGuiException(false);
			}

			// GAP 117 Recuperación de contratos desde el POS
			// Si no tiene contrato registrado no activamos el botón de reimpimir el contrato
			if (ticket instanceof IskaypetGestionTicketGui) {
				String vlas = ((IskaypetGestionTicketGui) ticket).getContrato();
				btnImprimirContrato.setDisable(StringUtils.isBlank(vlas));
				btnImprimirContrato.setVisible(StringUtils.isNotBlank(vlas));
			}
			
			//CZZ-2293
			if (ticketOperacion instanceof TicketVentaAbono && ((TicketVentaAbono)ticketOperacion).getCuponesEmitidos() != null) {
				TicketVentaAbono venta = (TicketVentaAbono) ticketOperacion;
			    btnImprimirCupones.setDisable(venta.getCuponesEmitidos().isEmpty());
			}
			
		}
		catch (TicketsServiceException ticketsServiceException) {
			log.error("refrescarDatosPantalla() - " + ticketsServiceException.getMessage());
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error leyendo información de ticket"), ticketsServiceException);
		}
		catch (DocumentoException documentoException) {
			log.error("Error recuperando el tipo de documento del ticket.", documentoException);
		}

	}


	public static List<SubtotalIvaTicket> generarImpuestosAgrupados(List<SubtotalIvaTicket> subtotalesIva) {
		HashMap<BigDecimal, SubtotalIvaTicket> mapSubtotales = new HashMap<>();
		for (SubtotalIvaTicket item: subtotalesIva) {
			if (!mapSubtotales.containsKey(item.getPorcentaje())) {
				SubtotalIvaTicket subtotalIvaTicket = new SubtotalIvaTicket();
				PorcentajeImpuestoBean porcentajeBean = new PorcentajeImpuestoBean();
				porcentajeBean.setPorcentaje(item.getPorcentaje());
				porcentajeBean.setPorcentajeRecargo(item.getPorcentajeRecargo());
				porcentajeBean.setCodImpuesto(item.getCodImpuesto());
				subtotalIvaTicket.setPorcentajeImpuestoBean(porcentajeBean);
				subtotalIvaTicket.setBase(item.getBase());
				subtotalIvaTicket.setImpuestos(item.getImpuestos());
				subtotalIvaTicket.setCuota(item.getCuota());
				subtotalIvaTicket.setCuotaRecargo(item.getCuotaRecargo());
				subtotalIvaTicket.setTotal(item.getTotal());
				mapSubtotales.put(item.getPorcentaje(), subtotalIvaTicket);
			} else {
				SubtotalIvaTicket subtotalIvaTicket = mapSubtotales.get(item.getPorcentaje());
				subtotalIvaTicket.setBase(subtotalIvaTicket.getBase().add(item.getBase()));
				subtotalIvaTicket.setImpuestos(subtotalIvaTicket.getImpuestos().add(item.getImpuestos()));
				subtotalIvaTicket.setCuota(subtotalIvaTicket.getCuota().add(item.getCuota()));
				subtotalIvaTicket.setCuotaRecargo(subtotalIvaTicket.getCuotaRecargo().add(item.getCuotaRecargo()));
				subtotalIvaTicket.setTotal(subtotalIvaTicket.getTotal().add(item.getTotal()));
			}
		}

		return new ArrayList<>(mapSubtotales.values());
	}

	public static void parsearPromocionesAdicionales(ITicket ticketOperacion) {
		List<PromocionTicket> lstPromociones = ticketOperacion.getPromociones();
		for (PromocionTicket promocionTicket: lstPromociones) {
            if (promocionTicket.getIdTipoPromocion() == 13L && !CollectionUtils.isEmpty(promocionTicket.getAdicionales())) {
				if (promocionTicket.getAdicionales().containsKey(IskaypetPromocionTextoBean.PARAMETRO_IMPRIMIR_APARTE)) {
					Object valorImprimirAparte = promocionTicket.getAdicionales().get(IskaypetPromocionTextoBean.PARAMETRO_IMPRIMIR_APARTE);
					if (valorImprimirAparte instanceof  String) {
						Boolean imprimirAparte = Boolean.parseBoolean((String) valorImprimirAparte);
						promocionTicket.getAdicionales().put(IskaypetPromocionTextoBean.PARAMETRO_IMPRIMIR_APARTE, imprimirAparte);
					}
				}
            }
		}
	}

	@Override
	protected void accionImprimirCopia(ActionEvent event) {
		try {
			Map<String, Object> mapaParametros = new HashMap();
			mapaParametros.put(TICKET, this.ticketOperacion);
			mapaParametros.put(LINEAS, generarLineasAgrupadas((List<IskaypetLineaTicket>) this.ticketOperacion.getLineas(), false));
			mapaParametros.put(IMPUESTOS, generarImpuestosAgrupados((List<SubtotalIvaTicket>) ticketOperacion.getCabecera().getSubtotalesIva()));
			mapaParametros.put(BIG_DECIMAL_100, BigDecimalUtil.CIEN);
			mapaParametros.put(APERTURA_CAJON, requiereAbrirCajon(ticketOperacion, Boolean.TRUE));
			mapaParametros.put(URL_QR, this.variablesService.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
            mapaParametros.put(URL_TEMP_QR, variablesService.getVariableAsString(QR_TEMPORAL));
			mapaParametros.put(ES_COPIA, true);
            mapaParametros.put(IMPRIMIR_LOGO, requierImprimirLogo(variablesServices));
			ServicioImpresion.imprimir(this.ticketOperacion.getCabecera().getFormatoImpresion(), mapaParametros);
		}
		catch (DeviceException var3) {
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Fallo al imprimir ticket."), this.getStage());
		}

	}

	public static List<IskaypetLineaTicket> generarLineasAgrupadas(List<IskaypetLineaTicket> lineasOriginales, boolean esPrevisualizacion) {
		List<IskaypetLineaTicket> lineas = new ArrayList<>();
		for (IskaypetLineaTicket linea : lineasOriginales) {
			IskaypetLineaTicket lineaAgrupada = lineas.stream().filter(el -> sonIguales(el, linea, esPrevisualizacion)).findFirst().orElse(null);
			if (lineaAgrupada != null) {
				lineaAgrupada.setCantidad(lineaAgrupada.getCantidad().add(linea.getCantidad()));
			}
			else {
				lineaAgrupada = (IskaypetLineaTicket) linea.clone();
				lineas.add(lineaAgrupada);
			}
		}

		for (IskaypetLineaTicket linea : lineas) {
			linea.setImporteTotalConDto(linea.getCantidad().multiply(linea.getImporteTotalConDto()));
		}

		lineas.sort(Comparator.comparing(IskaypetLineaTicket::getCodArticulo).thenComparing((l1, l2) -> {
            BigDecimal porcentajeDto1 = l1.getImporteDescuento();
            BigDecimal porcentajeDto2 = l2.getImporteDescuento();
            return porcentajeDto1.compareTo(porcentajeDto2);
        }));

		return lineas;
	}

	private static boolean sonIguales(IskaypetLineaTicket lineaAgrupada, IskaypetLineaTicket lineaTicket, boolean esPrevisualizacion) {

		// Comprobamos si es el mismo articulo
		if (!lineaAgrupada.getCodArticulo().equals(lineaTicket.getCodArticulo())) {
			return false;
		}

		// Comprobamos si es el mismo precio a pagar
		if (!BigDecimalUtil.isIgual(lineaAgrupada.getImporteTotalConDto().setScale(2, RoundingMode.HALF_UP), lineaTicket.getImporteTotalConDto().setScale(2, RoundingMode.HALF_UP))) {
			log.debug("Se comprueba importe total con dto promocional");
			BigDecimal importeTotalConDtoLinea = calcularImporteConDTO(lineaTicket.getPromociones(),lineaTicket.getImporteTotalSinDto(), TIPO_ACCESO_PROMOCION);
			BigDecimal importeTotalConDtoAgrupada = calcularImporteConDTO(lineaAgrupada.getPromociones(), lineaAgrupada.getImporteTotalSinDto().divide(lineaAgrupada.getCantidad(), RoundingMode.HALF_UP), TIPO_ACCESO_PROMOCION);
			if (!BigDecimalUtil.isIgual(importeTotalConDtoLinea, importeTotalConDtoAgrupada)) {
				log.debug("No se agrupan lineas por importe total con dto promocional es distinto");
				return false;
			}

			if (esPrevisualizacion) {
				log.debug("Se comprueba importe lineas con dto por cupón");
				BigDecimal  importeTotalConDtoLineaCupon = calcularImporteConDTO(lineaTicket.getPromociones(), lineaTicket.getImporteTotalSinDto(), TIPO_ACCESO_CUPON);
				BigDecimal importeTotalConDtoAgrupadaCupon = calcularImporteConDTO(lineaAgrupada.getPromociones(), lineaAgrupada.getImporteTotalSinDto().divide(lineaAgrupada.getCantidad(), RoundingMode.HALF_UP), TIPO_ACCESO_CUPON);
				if (!BigDecimalUtil.isIgual(importeTotalConDtoLineaCupon, importeTotalConDtoAgrupadaCupon)) {
					log.debug("No se agrupan lineas por importe total con dto por cupón es distinto");
					return false;
				}
			}

		}


		VariablesServices variablesServices = SpringContext.getBean(VariablesServices.class);
		TrazabilidadMascotasService mascotasService = SpringContext.getBean(TrazabilidadMascotasService.class);
		// Comprobamos si es mascota (no se agrupan mascotas)
		if (lineaTicket.isMascota(variablesServices, mascotasService)) {
			return false;
		}

		// Comprobamos si es lote (se agrupan lotes por lote y fecha de caducidad)
		if (lineaTicket.getLote() != null && !(lineaAgrupada.getLote().getLote().equals(lineaTicket.getLote().getLote()) && lineaAgrupada.getLote().getFechaCaducidad().equals(lineaTicket.getLote().getFechaCaducidad()))) {
			log.debug("No se agrupan lotes por lote y fecha de caducidad distintas");
			return false;
		}


		// Comprobamos si es inyectable (se agrupan inyectables por cantidad convertida, cantidad suministrada y unidad de medida)
		if (lineaTicket.getInyectable() != null && !(lineaAgrupada.getInyectable().getCantidadConvertida().equals(lineaTicket.getInyectable().getCantidadConvertida())
				&& lineaAgrupada.getInyectable().getCantidadSuministrada().equals(lineaTicket.getInyectable().getCantidadSuministrada())
				&& lineaAgrupada.getInyectable().getUnidadMedidaSuministrada().equals(lineaTicket.getInyectable().getUnidadMedidaSuministrada()))) {
			log.debug("No se agrupan inyectables por cantidad convertida, cantidad suministrada y unidad de medida distintas");
			return false;
		}


		// Comprobamos si es un articulo canjeado por puntos (no se agrupan articulos canjeados por puntos)
		if (lineaTicket.getArticlePoints() != null && lineaTicket.getArticlePoints().getReedem().equals("1")) {
			log.debug("No se agrupan articulos canjeados por puntos");
			return false;
		}

		if (!Objects.equals(lineaAgrupada.getPegatinaPromocional(), lineaTicket.getPegatinaPromocional())) {
			log.debug("No se agrupan articulos con distinta pegatina promocional");
			return false;
		}

		if (lineaAgrupada.getPegatinaPromocional() != null
			&& (!Objects.equals(lineaAgrupada.getPegatinaPromocional().getMotived(), lineaTicket.getPegatinaPromocional().getMotived()) ||
				!BigDecimalUtil.isIgual(lineaAgrupada.getPegatinaPromocional().getDiscount(), lineaTicket.getPegatinaPromocional().getDiscount()))) {
			log.debug("No se agrupan articulos con distinta pegatina promocional");
			return false;
		}

		return true;
	}

	private static BigDecimal calcularImporteConDTO(List<PromocionLineaTicket> lstPromos, BigDecimal importeTotalSinDto, String tipoAcceso) {
		BigDecimal importeTotalConDtoLinea = BigDecimal.ZERO;
		if (!CollectionUtils.isEmpty(lstPromos)) {
			for (PromocionLineaTicket promoLinea : lstPromos) {
				if (tipoAcceso.equalsIgnoreCase(promoLinea.getAcceso()) && promoLinea.getImporteTotalDtoMenosMargen() != null
						&& BigDecimalUtil.isMayorACero(promoLinea.getImporteTotalDtoMenosMargen())) {
					importeTotalConDtoLinea = importeTotalConDtoLinea.add(promoLinea.getImporteTotalDtoMenosMargen());
				}
			}
		}
		return importeTotalSinDto.subtract(importeTotalConDtoLinea);
	}

	public static void parsearLineasCaracteresEspeciales(List<?> lineas) {
		for (Object obj : lineas) {
			if (obj instanceof IskaypetLineaTicket) {
				IskaypetLineaTicket linea = (IskaypetLineaTicket) obj;
				if (StringUtils.isNotBlank(linea.getDesArticulo())) {
					String desArticulo = linea.getDesArticulo();
					desArticulo = desArticulo.replace("<", "&lt;");
					desArticulo = desArticulo.replace(">", "&gt;");
					linea.setDesArticulo(desArticulo);
				}
				parsearTextoLineaPromociones(linea);
			}
		}
	}

	private static void parsearTextoLineaPromociones(IskaypetLineaTicket linea){
		if (linea.getTextoLineaPromociones() != null &&!linea.getTextoLineaPromociones().isEmpty()){
			for (TextoPromocion textoPromocion : linea.getTextoLineaPromociones()) {
				if (StringUtils.isNotBlank(textoPromocion.getTexto())) {
					textoPromocion.setTexto(textoPromocion.getTexto().replace("<", "&lt;").replace(">", "&gt;"));
				} else {
					log.debug("Texto promoción vacío");
				}
			}
		} else {
			log.debug("No se han encontrado textos promociones");
		}
	}

	public static void parsearPromocionesCaracteresEspeciales(List<?> promociones) {
		for (Object obj : promociones) {
			if(obj instanceof  PromocionTicket){
				PromocionTicket promo = (PromocionTicket) obj;
				if(StringUtils.isNotBlank(promo.getTextoPromocion())){
					promo.setTextoPromocion(promo.getTextoPromocion()
							.replace("<", "&lt;").replace(">", "&gt;"));
				} else {
					log.debug("La promoción no tiene texto promoción");
				}
			}
		}

	}


	public static void ofuscarNombreCajero(ITicket ticket) {
		UsuarioBean cajero = ticket.getCabecera().getCajero();
		String nombreCompleto = cajero.getDesusuario().toUpperCase();
		StringBuilder nombreAbreviado = new StringBuilder();
		String[] nombreSplit = nombreCompleto.split(" ");

		nombreAbreviado.append(nombreSplit[0]);

		if (nombreSplit.length > 1) {
			nombreAbreviado.append(" ");
			nombreAbreviado.append(nombreSplit[1].charAt(0));
			nombreAbreviado.append(".");
		}

		cajero.setDesusuario(nombreAbreviado.toString());
		ticket.getCabecera().setCajero(cajero);
	}

	public static boolean requiereAbrirCajon(ITicket ticket, Boolean esCopia) {
		if (esCopia) {
			return false;
		}

		List<IPagoTicket> lstPagos = ((List<IPagoTicket>)ticket.getPagos()).stream()
				.filter(pago -> pago.getCodMedioPago().equals(EFECTIVO.getCodPago()))
				.collect(Collectors.toList());

		if (!lstPagos.isEmpty()) {
			BigDecimal totalEfectivo = lstPagos.stream()
					.map(IPagoTicket::getImporte)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			if (ticket.getCabecera().getDatosDocOrigen() == null && ((IskaypetCabeceraTicket)ticket.getCabecera()).getDatosOrigenFalsos() == null) {
				return BigDecimalUtil.isMayorACero(totalEfectivo); // Si tiene ventas en efectivo (apertura cajon)
			} else {
				return !BigDecimalUtil.isIgualACero(totalEfectivo); // Si tiene devoluciones en efectivo (apertura cajon)
			}
		}

		return false;
	}

	public static Boolean requierImprimirLogo(VariablesServices variablesServices) {
		log.debug("Iniciando requiere imprimir logo");
		Boolean imprimirLogo = variablesServices.getVariableAsBoolean(PARAMETRO_IMPRIMIR_LOGO, Boolean.TRUE);
        log.debug("Finalizando requiere imprimir logo: " + imprimirLogo );
        return imprimirLogo;
	}


	public static void parsearAdicionales(ITicket ticketOperacion) {
		if (ticketOperacion.getCabecera().getDatosFidelizado() != null && !CollectionUtils.isEmpty(ticketOperacion.getCabecera().getDatosFidelizado().getAdicionales())) {

			if (ticketOperacion.getCabecera().getDatosFidelizado().getAdicionales().containsKey(PARAMETRO_SALDO_NO_DISPONIBLE)) {
				Object valorSaldoNoDisponible = ticketOperacion.getCabecera().getDatosFidelizado().getAdicionales().get(PARAMETRO_SALDO_NO_DISPONIBLE);
				if (valorSaldoNoDisponible instanceof String) {
					BigDecimal saldoNoDisponible = new BigDecimal((String) valorSaldoNoDisponible);
					ticketOperacion.getCabecera().getDatosFidelizado().putAdicional(PARAMETRO_SALDO_NO_DISPONIBLE, saldoNoDisponible);
				}
			}

			if (ticketOperacion.getCabecera().getDatosFidelizado().getAdicionales().containsKey(PARAMETRO_APLICA_CARENCIA)) {
				Object valorAplicaCarencia = ticketOperacion.getCabecera().getDatosFidelizado().getAdicionales().get(PARAMETRO_APLICA_CARENCIA);
				if (valorAplicaCarencia instanceof String) {
					Boolean aplicaCarencia = Boolean.parseBoolean((String) valorAplicaCarencia);
					ticketOperacion.getCabecera().getDatosFidelizado().putAdicional(PARAMETRO_APLICA_CARENCIA, aplicaCarencia);
				}
			}

			if (ticketOperacion.getCabecera().getDatosFidelizado().getAdicionales().containsKey(PARAMETRO_APLICA_FIDELIZACION)) {
				Object valorAplicaFidelizacion = ticketOperacion.getCabecera().getDatosFidelizado().getAdicionales().get(PARAMETRO_APLICA_FIDELIZACION);
				if (valorAplicaFidelizacion instanceof String) {
					Boolean aplicaFidelizacion = Boolean.parseBoolean((String) valorAplicaFidelizacion);
					ticketOperacion.getCabecera().getDatosFidelizado().putAdicional(PARAMETRO_APLICA_FIDELIZACION, aplicaFidelizacion);
				}
			}

			if(ticketOperacion.getCabecera().getDatosFidelizado().getAdicionales().containsKey(PARAMETRO_EMAIL)) {
				Object valorEmail = ticketOperacion.getCabecera().getDatosFidelizado().getAdicionales().get(PARAMETRO_EMAIL);
				if (valorEmail instanceof String) {
					String email = (String) valorEmail;
					ticketOperacion.getCabecera().getDatosFidelizado().putAdicional(PARAMETRO_EMAIL, email);
				}
			}

		}
	}

	public List<Class<?>> getTicketClasses(TipoDocumentoBean tipoDocumento) {
		List<Class<?>> classes = new LinkedList<>();
		Class<?> clazz = SpringContext.getBean(getTicketClass(tipoDocumento)).getClass();

		Class<?> superClass = clazz.getSuperclass();
		while (!superClass.equals(Object.class)) {
			classes.add(superClass);
			superClass = superClass.getSuperclass();
		}

		Collections.reverse(classes);

		classes.add(clazz);
		classes.add(SpringContext.getBean(IskaypetCabeceraTicket.class).getClass());
		classes.add(SpringContext.getBean(IskaypetLineaTicket.class).getClass());
		classes.add(SpringContext.getBean(IskaypetCuponEmitidoTicket.class).getClass());

		return classes;
	}

	/*
	 * ############################################################################################################
	 * ################################## GAP 117 Recuperación de contratos desde el POS ##########################
	 * ############################################################################################################
	 */

	@FXML
	void accionImprimirContrato() {

		List<TicketContratosBean> lstContratos = servicioContratoMascotas.getContratosRealizados(ticketOperacion);
		if (lstContratos != null && !lstContratos.isEmpty()) {
			List<DatosCabeceraContrato> lstDatosContrato = new ArrayList<>();
			for (TicketContratosBean ticketContratos : lstContratos) {
				IskaypetLineaTicket linea = (IskaypetLineaTicket) ticketOperacion.getLinea(ticketContratos.getLinea());
				ContratoAnimalDto contrato = linea.getContratoAnimal();

				DatosCabeceraContrato datosCabecera = new DatosCabeceraContrato();
				datosCabecera.setCodArt(linea.getCodArticulo());
				datosCabecera.setDesArt(linea.getDesArticulo());
				datosCabecera.setContratoAnimal(contrato);
				datosCabecera.setIdLinea(linea.getIdLinea());
				datosCabecera.setUidTicket(ticketContratos.getUidTicket());
				datosCabecera.setContratoRecuperad(ticketContratos.getContrato());
				lstDatosContrato.add(datosCabecera);
			}

			HashMap<String, Object> parametros = new HashMap<>();
			parametros.put(IskaypetEvicertiaService.LST_CONTRATOS, lstDatosContrato);
			parametros.put(EMPRESA, sesion.getAplicacion().getEmpresa());
			parametros.put(IskaypetEvicertiaService.RECUPERA_CONTRATO, true);

			getApplication().getMainView().showModalCentered(EnvioContratoMascotaGestionDocumentosView.class, parametros, this.getStage());

		}
	}

	//CZZ-2293
    @FXML
    protected void accionImprimirCupones(ActionEvent event) {
    	if(ticketOperacion instanceof TicketVentaAbono 
    			&& ((TicketVentaAbono)ticketOperacion).getCuponesEmitidos() != null 
    			&& ((TicketVentaAbono)ticketOperacion).getCuponesEmitidos().isEmpty()){
    		return;
    	}
    	
    	log.debug("accionImprimirCupones()");
        HashMap<String,Object> datos = new HashMap<>();
        datos.put(IskaypetCuponesController.PARAMETRO_TICKET_CUPONES,((TicketVentaAbono)ticketOperacion));
        getApplication().getMainView().showModalCentered(IskaypetCuponesView.class, datos, this.getStage());
        HashMap<String,Object> datosSalida = datos;
        if(!datosSalida.containsKey(IskaypetCuponesController.PARAMETRO_SALIDA_CANCELAR)){
            this.getStage().close();
        }  
		
    }


}
