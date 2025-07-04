package com.comerzzia.cardoso.pos.services.ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.services.balanza.TicketBalanzaService;
import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeService;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOCabeceraTicket;
import com.comerzzia.core.servicios.ventas.tickets.TicketException;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.pos.persistence.core.config.configcontadores.ConfigContadorBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.parametros.ConfigContadorParametroBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.config.configContadores.rangos.CounterRangeParamDto;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.cabecera.DatosDocumentoOrigenTicket;

@SuppressWarnings("rawtypes")
@Service
@Primary
public class CardosoTicketsService extends TicketsService{

	private static final Logger log = Logger.getLogger(CardosoTicketsService.class.getName());
	
	private static final Long ID_DOCUMENTO_CIERRE_CAJA= 500L;
		
	/**
	 * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - CAJA ESPECIAL
	 * 
	 * En caso de ser una caja especial, se deberá usar un contador diferente (REP_NUMFAC_ESPECIAL).
	 */
	public static final String VARIABLE_CAJA_ESPECIAL = "TPV.CAJA_ESPECIAL";
	public static final String ID_CONTADOR_ESPECIAL = "REP_NUMFAC_ESPECIAL";
	
	/**TAXFREE**/
	public static final String ESTADO_CREACION = "CREACION";
	
	@Autowired
	protected TaxfreeService taxfreeService;
	
	@Override
	public synchronized void setContadorIdTicket(Ticket ticket) throws TicketsServiceException{
		log.debug("setContadorIdTicket() : GAP - PERSONALIZACIONES V3 - CAJA ESPECIAL");
		
		try{
			log.debug("setContadorIdTicket() - Obteniendo contador para identificador...");
			Map<String, String> parametrosContador = new HashMap<>();
			Map<String, String> condicionesVigencias = new HashMap<>();

			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODEMP, ticket.getEmpresa().getCodEmpresa());
			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODALM, ticket.getTienda().getAlmacenBean().getCodAlmacen());
			/* GAP - PERSONALIZACIONES V3 - SERIE ALBARÁN */
			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODSERIE, ((CARDOSOCabeceraTicket)ticket.getCabecera()).getSerieAlbaran());
			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODCAJA, ticket.getCodCaja());
			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODDOC, ticket.getCabecera().getCodTipoDocumento());
			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_PERIODO, ((new Fecha()).getAño().toString()));

			condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODCAJA, ticket.getCabecera().getCodCaja());
			condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODALM, ticket.getCabecera().getTienda().getCodAlmacen());
			condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODEMP, ticket.getCabecera().getEmpresa().getCodEmpresa());

			TipoDocumentoBean documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(ticket.getCabecera().getCodTipoDocumento());
			ConfigContadorBean confContador = servicioConfigContadores.consultar(documentoActivo.getIdContador());
			
			/* Solo poner este contador especial, en caso de ser "FS" o "FC" y ser país "ES". */
			String variable = null;
			ContadorBean ticketContador = null;
			String codPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
			
			if(codPais.equals("ES")
			        && (ticket.getCabecera().getCodTipoDocumento().equals(Documentos.FACTURA_SIMPLIFICADA) 
			        || ticket.getCabecera().getCodTipoDocumento().equals(Documentos.FACTURA_COMPLETA))){
				variable = variablesServices.getVariableAsString(VARIABLE_CAJA_ESPECIAL);
			}
						
			if(!confContador.isRangosCargados()){
				ConfigContadorRangoExample example = new ConfigContadorRangoExample();
				example.or().andIdContadorEqualTo(confContador.getIdContador());
				example.setOrderByClause(ConfigContadorRangoExample.ORDER_BY_RANGO_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FIN + ", "
				        + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_FIN);
				List<ConfigContadorRangoBean> rangos = servicioConfigContadoresRangos.consultar(example);

				confContador.setRangos(rangos);
				confContador.setRangosCargados(true);
			}
				
			if(variable != null && variable.equals(sesion.getSesionCaja().getCajaAbierta().getCodCaja())){
				ticketContador = servicioContadores.obtenerContador(ID_CONTADOR_ESPECIAL, parametrosContador, ticket.getUidActividad());
			}
			else{
				ticketContador = servicioContadores.consultarContadorActivo(confContador, parametrosContador, condicionesVigencias, ticket.getUidActividad(), true);
			}
				
			if(ticketContador == null || ticketContador.getError() != null){
				throw new ContadorNotFoundException("No se ha encontrado un contador disponible");
			}
			
			ticket.setIdTicket(ticketContador.getValor());
			String codTicket = servicioContadores.obtenerValorTotalConSeparador(ticketContador.getConfigContador().getValorDivisor3Formateado(), ticketContador.getValorFormateado());
			ticket.getCabecera().setSerieTicket(ticketContador.getConfigContador().getValorDivisor3Formateado());
			ticket.getCabecera().setCodTicket(codTicket);
			ticket.getCabecera().setUidTicket(UUID.randomUUID().toString());
			copiaSeguridadTicketService.guardarBackupTicketActivo((TicketVenta) ticket);
			
			/* Rango de contadores de ATCUD */
			CounterRangeParamDto counterRangeParam = new CounterRangeParamDto();
        	counterRangeParam.setCounterId(ticketContador.getIdContador());
        	counterRangeParam.setDivisor1(ticketContador.getDivisor1());
        	counterRangeParam.setDivisor2(ticketContador.getDivisor2());
        	counterRangeParam.setDivisor3(ticketContador.getDivisor3());
        	
        	String rangeId = counterRangeManager.findRangeId(counterRangeParam);
        	
        	if(StringUtils.isNotBlank(rangeId)) {
        		((TicketVentaAbono) ticket).addExtension(EXTENSION_RANGE_ID, rangeId);
        	}
		}
		catch(Exception e){
			String msg = "Se ha producido un error procesando ticket con uid " + ticket.getUidTicket() + " : " + e.getMessage();
			log.error("registrarTicket() - " + msg, e);
			throw new TicketsServiceException(e);
		}
	}
	
	@Override
	public void saveEmptyTicket(ITicket ticketPrincipal, TipoDocumentoBean documentoActivo, TipoDocumentoBean documentoOrigen){
		try{
			boolean generarDosDocumentos = false;
			boolean esDevolucion = !ticketPrincipal.getCabecera().esVenta();

			if(esDevolucion && documentoOrigen != null){
				TipoDocumentoBean documentoAbono = documentos.getDocumentoAbono(documentoActivo.getCodtipodocumento());
				TipoDocumentoBean documentoVenta = documentos.getDocumento(documentoOrigen.getCodtipodocumento());

				if(!documentoAbono.isSignoLibre() || !documentoVenta.isSignoLibre()){
					generarDosDocumentos = true;
				}
			}

			if(!generarDosDocumentos && documentoActivo.isSignoLibre()){
				TicketVentaAbono ticketVacio = generateEmptyTicket(ticketPrincipal, documentoActivo, null);
				registrarTicket(ticketVacio, documentoActivo, false);
			}
			else{
				TipoDocumentoBean documentoAbono = documentos.getDocumentoAbono(documentoActivo.getCodtipodocumento());
				if(esDevolucion){
					documentoAbono = documentoOrigen;
				}

				String signoDoc1 = documentoActivo.getConcepto().getSigno();
				String signoDoc2 = documentoAbono.getConcepto().getSigno();

				Boolean doc1LineasPositivas = null;
				Boolean doc2LineasPositivas = null;
				if(signoDoc1.equals(signoDoc2)){
					if(signoDoc1.equals("+")){
						doc1LineasPositivas = true;
						doc2LineasPositivas = true;
					}
					if(signoDoc1.equals("-")){
						doc1LineasPositivas = false;
						doc2LineasPositivas = false;
					}
				}
				else{
					if(StringUtils.isBlank(signoDoc1)){
						doc2LineasPositivas = signoDoc2.equals("+");
						doc1LineasPositivas = !doc2LineasPositivas;
					}
					else if(StringUtils.isBlank(signoDoc2)){
						doc1LineasPositivas = signoDoc1.equals("+");
						doc2LineasPositivas = !doc1LineasPositivas;
					}
					else{
						doc1LineasPositivas = signoDoc1.equals("+");
						doc2LineasPositivas = signoDoc2.equals("+");
					}
				}

				if(!esDevolucion){
					TicketVentaAbono ticketVenta = generateEmptyTicket(ticketPrincipal, documentoActivo, doc1LineasPositivas, false);
					registrarTicket(ticketVenta, documentoActivo, false);
	
					TicketVentaAbono ticketAbono = generateEmptyTicket(ticketPrincipal, documentoAbono, doc2LineasPositivas, true);
					ticketAbono.getCabecera().setTipoDocumento(documentoAbono.getIdTipoDocumento());
					setContadorIdTicket((Ticket) ticketAbono);
					registrarTicket(ticketAbono, documentoAbono, false);
					ticketPrincipal.getCabecera().setTipoDocumento(documentoActivo.getIdTipoDocumento());
				}
				else {
					TicketVentaAbono ticketAbono = generateEmptyTicket(ticketPrincipal, documentoAbono, doc2LineasPositivas, false);
					ticketAbono.getCabecera().setTipoDocumento(documentoAbono.getIdTipoDocumento());
					ticketAbono.setEsDevolucion(false);
					setContadorIdTicket((Ticket) ticketAbono);
					registrarTicket(ticketAbono, documentoAbono, false);
					ticketPrincipal.getCabecera().setTipoDocumento(documentoActivo.getIdTipoDocumento());
					
					TicketVentaAbono ticketVenta = generateEmptyTicket(ticketAbono, documentoActivo, doc1LineasPositivas, true);
					ticketVenta.getCabecera().setUidTicket(ticketPrincipal.getUidTicket());
					ticketVenta.setIdTicket(ticketPrincipal.getIdTicket());
					ticketVenta.getCabecera().setSerieTicket(ticketPrincipal.getCabecera().getSerieTicket());
					ticketVenta.getCabecera().setCodTicket(ticketPrincipal.getCabecera().getCodTicket());
					ticketVenta.setEsDevolucion(ticketPrincipal.isEsDevolucion());
					registrarTicket(ticketVenta, documentoActivo, false);
				}
			}
		}
		catch(Exception e){
			log.error("saveEmptyTicket() - Ha ocurrido un error al salvar un ticket vacío: " + e.getMessage(), e);
		}
	}
	
	public TicketVentaAbono generateEmptyTicket(ITicket ticketPrincipal, TipoDocumentoBean documentoActivo, Boolean lineasPositivas, Boolean docDevolucion){
		TicketVentaAbono result = super.generateEmptyTicket(ticketPrincipal, documentoActivo, lineasPositivas);
		
		//el documento origen debe estar a null
		result.getCabecera().setDatosDocOrigen(null);
	
		// GAP - PERSONALIZACIONES V3 - SERIE ALBARÁN
		String serieAlbaran = ((CARDOSOCabeceraTicket)ticketPrincipal.getCabecera()).getSerieAlbaran();
		((CARDOSOCabeceraTicket)result.getCabecera()).setSerieAlbaran(serieAlbaran);
		if(docDevolucion){
			DatosDocumentoOrigenTicket datosOrigenDevolucion = new DatosDocumentoOrigenTicket();
			datosOrigenDevolucion.setUidTicket(ticketPrincipal.getUidTicket());
			datosOrigenDevolucion.setNumFactura(ticketPrincipal.getIdTicket());
			datosOrigenDevolucion.setCaja(ticketPrincipal.getCabecera().getCodCaja());
			datosOrigenDevolucion.setTienda(ticketPrincipal.getCabecera().getTienda().getCodAlmacen());
			datosOrigenDevolucion.setSerie(serieAlbaran);
			datosOrigenDevolucion.setCodTicket(ticketPrincipal.getCabecera().getCodTicket());
			datosOrigenDevolucion.setIdTipoDoc(ticketPrincipal.getCabecera().getTipoDocumento());
			datosOrigenDevolucion.setCodTipoDoc(ticketPrincipal.getCabecera().getCodTipoDocumento());
			datosOrigenDevolucion.setDesTipoDoc(ticketPrincipal.getCabecera().getDesTipoDocumento());
			
			result.getCabecera().setDatosDocOrigen(datosOrigenDevolucion);
			result.getCabecera().setUidTicketEnlace(ticketPrincipal.getUidTicket());
		}
		
		return result;
	}
	
	/**
	 * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA
	 * 
	 * Habilitamos la funcionalidad de poder introducir tickets de balanza a través de una venta.
	 * Leemos el ticket de balanza los datos de las lineas y los introducimos en la venta.
	 */
	@Autowired
	private TicketBalanzaService ticketBalanzaService;
	
	@Override
	public synchronized void insertarTicket(SqlSession sqlSession, TicketBean ticket, boolean ticketProcesado) throws TicketsServiceException{
		log.debug("insertarTicket() : GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA");
		
		super.insertarTicket(sqlSession, ticket, ticketProcesado);
		try{
			marcarTicketBalanzaUtilizado(ticket);
		}
		catch(Exception e){
			String msg = "Error al marcar como utilizado el ticket balanza incluido en el ticket " + " : " + e.getMessage();
			log.error("insertarTicket() - " + msg, e);
			throw new TicketsServiceException(e);
		}
		
		/**
		 * ########################################################################################
		 * GAP - TAXFREE - INTEGRACIÓN CON INNOVA TAXFREE
		 * 
		 * Recogemos los datos del ticket para realizar la insercion de los datos de tf tras una venta
		 */
//		try {
//			log.debug("insertarTicket() : GAP - TAXFREE - INTEGRACIÓN CON INNOVA TAXFREE");
//			if(ticket.getIdTipoDocumento() != ID_DOCUMENTO_CIERRE_CAJA) {
//				insercionTaxfreeDB(ticket);				
//			}
//			
//		} catch (XMLDocumentException e) {
//			String msg = "Error al extraer los datos del ticket para su insercion en taxfree" + " : " + e.getMessage();
//			log.error("insertarTicket() - " + msg, e);
//			throw new TicketsServiceException(e);
//		}
		
	}
	
	/**
	 * Además de insertar los artículos en la venta, marca como utilizado ese 
	 * ticket para que no se pueda volver a usar.
	 * @param ticket
	 * @throws XMLDocumentException
	 */
	public void marcarTicketBalanzaUtilizado(TicketBean ticket) throws XMLDocumentException{
		log.debug("marcarTicketBalanzaUtilizado() : GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA");
		
		Long idTipoDocumentoCierre = -1L;
		try{
			idTipoDocumentoCierre = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.CIERRE_CAJA).getIdTipoDocumento();
		}
		catch(DocumentoException e){
			idTipoDocumentoCierre = -1L;
		}
		
		if(!ticket.getIdTipoDocumento().equals(idTipoDocumentoCierre)){
			String LINEAS = "lineas";
			String LINEA = "linea";
			String UIDTICKETBALANZA = "uid_ticket_balanza";

			XMLDocument xmlDocument = new XMLDocument(ticket.getTicket());
			XMLDocumentNode nodeRoot = xmlDocument.getRoot();
			XMLDocumentNode Nodolineas = nodeRoot.getNodo(LINEAS);
			List<XMLDocumentNode> lineas = Nodolineas.getHijos(LINEA);

			for(XMLDocumentNode linea : lineas){
				/* Leemos las lineas y vamos insertando los articulos en la venta */
				if(linea.getNodo(UIDTICKETBALANZA, true) != null){
					String uidTicketBalanza = linea.getNodo(UIDTICKETBALANZA).getValue();

					if(StringUtils.isNotBlank(uidTicketBalanza)){
						ticketBalanzaService.marcarTicketBalanzaUtilizado(uidTicketBalanza, ticket.getUidTicket());
					}
				}
			}
		}
	}
	
	public void insercionTaxfreeDB(TicketBean ticket) throws XMLDocumentException {

		//llamada para la insercion de taxfree en bbdd
		log.debug("insercionTaxfreeDB()- extrayendo valores del xml");
		XMLDocument xmlDocument = new XMLDocument(ticket.getTicket());
		XMLDocumentNode nodeRoot = xmlDocument.getRoot();
		XMLDocumentNode NodoCab = nodeRoot.getNodo("cabecera");
		XMLDocumentNode nodoTf = NodoCab.getNodo("taxfree_barcode", true);
		XMLDocumentNode nodoPasaporte = NodoCab.getNodo("pasaporte", true);
		XMLDocumentNode nodoCodTipoDoc = NodoCab.getNodo("pasaporte", true);

		String barcode = nodoTf != null ? NodoCab.getNodo("taxfree_barcode").getValue() : "";
		String pasaporte = nodoPasaporte != null ? NodoCab.getNodo("pasaporte").getValue() : "";  
		String codTipoDoc = nodoCodTipoDoc!=null ? NodoCab.getNodo("cod_tipo_documento", false).getValue():"";
		
		log.debug("insercionTaxfreeDB()- comprobando el valor del barcode para insercion en bbdd");
		if(StringUtils.isNotBlank(codTipoDoc) && StringUtils.isNotBlank(barcode)) {
			try {
				String codTicket = codTipoDoc + "/"+ ticket.getCodTicket();
				taxfreeService.insertDocument(codTicket, barcode, ESTADO_CREACION, pasaporte, ticket.getCodcaja(), ticket.getUidTicket());
			} catch (Exception e1) {
				log.error("insercionTaxfreeDB() - error al insertar el taxfree en bbdd");
			}
		}
	}
	
	public void inserccionTaxfreeXML(String etiqueta, String codTipoDoc, String codTicketOriginal, String codCaja, String uidTicket) {
		log.debug("insercionTaxfreeXML()");
			try {
				String codTicket = codTipoDoc + "/"+ codTicketOriginal;
				String pasaporte = "";
				taxfreeService.insertDocument(codTicket, etiqueta, ESTADO_CREACION, pasaporte, codCaja, uidTicket);
			}
			catch (Exception e1) {
				log.error("insercionTaxfreeDB() - error al insertar el taxfree en bbdd");
			}
	}
	
	public void modificarTicket(TicketBean ticket) throws TicketException {
		try {
			log.debug("modificarTicket() - Modificando ticket " + ticket.getIdTicket());
			ticketMapper.updateByPrimaryKeyWithBLOBs(ticket);
		}
		catch (Exception e) {
			String msg = "Error modificando ticket: " + e.getMessage();
			log.error("modificarTicket() - " + msg);

			throw new TicketException(msg, e);
		}

	}
}
