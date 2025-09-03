package com.comerzzia.iskaypet.pos.services.ventas.pagos.mascotas;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.model.clases.parametros.valoresobjetos.ValorParametroObjeto;
import com.comerzzia.core.model.clases.parametros.valoresobjetos.ValorParametroObjetoExample;
import com.comerzzia.core.model.clases.parametros.valoresobjetos.ValorParametroObjetoExample.Criteria;
import com.comerzzia.core.persistencia.clases.parametros.valoresobjetos.ValorParametroObjetoMapper;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.api.evicertia.client.IskaypetEvicertiaRest;
import com.comerzzia.iskaypet.pos.api.evicertia.client.response.EviSignSubmit.EvinSingSubmitDTO;
import com.comerzzia.iskaypet.pos.api.evicertia.client.response.EviSignSubmit.InterestedParty;
import com.comerzzia.iskaypet.pos.api.evicertia.client.response.EviSignSubmit.Options;
import com.comerzzia.iskaypet.pos.api.evicertia.client.response.EviSignSubmit.SigningParty;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.ContratoAnimalDto;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.laws.ContractLaw;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.laws.ContractLawMapper;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.mascotas.DatosCabeceraContrato;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.prefijos.PrefijosPaisesKey;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.prefijos.PrefijosPaisesMapper;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosBean;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosExample;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosKey;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosMapper;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.veterinario.MascotaMailVeterinario;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.veterinario.MascotaMailVeterinarioExample;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.veterinario.MascotaMailVeterinarioMapper;
import com.comerzzia.iskaypet.pos.services.evicertia.IskaypetEvicertiaService;
import com.comerzzia.iskaypet.pos.services.impresion.IskaypetServicioImpresion;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiendas.Tienda;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.servicios.impresion.ImpresionJasper;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

@SuppressWarnings("deprecation")
@Component
public class ServicioContratoMascotas {

	private Logger log = Logger.getLogger(ServicioContratoMascotas.class);

	private static final Long ID_TIPO_DOC_CONTRATO = 700L;

	@Autowired
	private TicketsService ticketsService;

	@Autowired
	private Sesion sesion;

	@Autowired
	private ServicioContadores servicioContadores;

	@Autowired
	protected VariablesServices variablesServices;
	
	protected static final String  CREDENCIALES_EVICERTIA = "X_CONTRATOS_MASCOTA.CREDENCIALES";
	
	protected static final String  PARAMETER_CHIP = "CHIP";
	protected static final String  PARAMETER_ANILLA = "ANILLA";
	protected static final String  PARAMETER_CITES = "CITES";
	protected static final String  CASTELLANO = "ES";

	protected static final String  URL_EVICERTIA = "X_URL_EVICERTIA.CONTRATOS";
	private static final String EVICERTIA_TIMEOUT_MILLIS = "X_EVICERTIA_API_TIMEOUT_MILLIS";
	private static final List<String> INTERESTED_PARTIES_NOTIFICATIONS = Arrays.asList("Sent", "Signed", "Cancelled");

	@Autowired
	protected ContractLawMapper contractLawMapper;
	
	@Autowired
	protected ValorParametroObjetoMapper valorParametroObjetoMapper;
	
	//GAP 117 RECUPERACIÓN DE CONTRATOS DESDE EL POS
	@Autowired
	protected TicketContratosMapper ticketContratosMapper;
	
	//GAP 112 FLUJOS MAIL EVICERTIA
	protected static final String  EMAIL_VETERINARIO = "X_CLINICA_VETERINARIA";
	@Autowired
	protected MascotaMailVeterinarioMapper mailVeterinarioMapper;
	//GAP 125 CONTRATO ANIMAL: PREFIJO TELEFÓNICO
	@Autowired
	protected PrefijosPaisesMapper prefijosPaisesMapper;
	
	public DatosCabeceraContrato generarContrato(IskaypetLineaTicket iskaypetLineaTicket, Map<String, Object> datosAdicionales) {

		log.debug("generarContrato() - Generando contrato con los datos de linea y fidelizado...");

		String uidTicket = (String) datosAdicionales.get(IskaypetEvicertiaService.UID_TICKET);
		String codTicket = (String) datosAdicionales.get(IskaypetEvicertiaService.CODTICKET);
		String codLenguaje  =  StringUtils.isNotBlank((String) datosAdicionales.get(IskaypetEvicertiaService.COD_LENGUAJE)) ? (String) datosAdicionales.get(IskaypetEvicertiaService.COD_LENGUAJE) : CASTELLANO;

		DatosCabeceraContrato datosCabecera = generarDatosContrato(iskaypetLineaTicket);
		datosCabecera.setCodLenguaje(codLenguaje);
		datosCabecera.setIdLinea(iskaypetLineaTicket.getIdLinea());
		datosCabecera.setUidTicket(uidTicket);
		String pdfBase64 = completarContrato(datosCabecera, codLenguaje);

		// Realizamos la llamada a Evicertia
		try {
			EvinSingSubmitDTO datos = new EvinSingSubmitDTO();

			datos.setSubject(codTicket);
			SigningParty firmantes = new SigningParty();
			firmantes.setEmailAddress(datosCabecera.getEmail());
			firmantes.setAddress(datosCabecera.getEmail());
			firmantes.setSigningMethod("MobilePin");
			firmantes.setName(datosCabecera.getNombre());
			//Dependiendo del idioma del fidelizado el código de prefijo seleccionado
			firmantes.setPhoneNumber(datosCabecera.getPrefijo() + datosCabecera.getTlf());
			datos.setDocument(pdfBase64);
			firmantes.setSignOrder(0);
			datos.addSigningParty(firmantes);
			
			//Añadimos el email de la tienda
			String mailTienda = datosCabecera.getCorreoTienda();
			if (StringUtils.isNotBlank(mailTienda)) {
				InterestedParty interestedParty = new InterestedParty();
				interestedParty.setAddress(mailTienda);
				datos.addInterestedParty(interestedParty);
			}
			
			//Comprobamos si necesitamos mandar un mail al veterinario  
			InterestedParty mailVeterinario = getEmailVeterinario(datosCabecera.getCodArt());
			if(mailVeterinario != null) {
				datos.addInterestedParty(mailVeterinario);
			}

			Options options = new Options();
			options.setLanguage(codLenguaje.toLowerCase());
			options.setAffidavitLanguage(codLenguaje.toLowerCase());
			options.setInterestedPartiesNotifications(getInterestedPartiesNotifications());
			datos.setOptions(options);
			
			String timeoutEvicertiaStr = variablesServices.getVariableAsString(EVICERTIA_TIMEOUT_MILLIS);
			String uniqueId = null;
			if(!iskaypetLineaTicket.getContratoAnimal().isEnviado()) {
				uniqueId = IskaypetEvicertiaRest.enviarContrato(datos,consultarCredencialesEvicertia(), consultarUrlEvicertia(), Integer.parseInt(timeoutEvicertiaStr));
			}
			
			

			// Si todo es correcto o no ,generamos igualmente un DTO para el documento con
			// los datos para enviarlo a central

			ContratoMascotaDto contratoMascota = new ContratoMascotaDto();
			contratoMascota.setUniqueId(uniqueId);
			contratoMascota.setContrato(pdfBase64 == null ? "Esto es el contrato en base 64" : pdfBase64);
			contratoMascota.setFechaEnvio(new Date());
			contratoMascota.setUidTicket(uidTicket);
			generarXMLContratoMascota(contratoMascota);
			
			//GAP 117 RECUPERACIÓN DE CONTRATOS DESDE EL POS
			insertContratoRealizado(contratoMascota, iskaypetLineaTicket.getIdLinea());
	
			if (uniqueId == null) {
				log.error("generarContrato() - La petición se ha efectuado pero no se ha iniciado el proceso de firma.");
				datosCabecera.setEnviado(false);
				iskaypetLineaTicket.getContratoAnimal().setEnviado(false);
				
				return datosCabecera;
			}
			
		
			datosCabecera.setEnviado(true);
			
			iskaypetLineaTicket.getContratoAnimal().setEnviado(true);
			
			
			return datosCabecera;

		} catch (Exception e) {
			log.error("generarContrato() - Error de comunicación con Evicertia, no mandamos contratos y deben imprimirse.");
			datosCabecera.setEnviado(false);
			return datosCabecera;
		}
	}

	private List<String> getInterestedPartiesNotifications(){
		return new ArrayList<>(INTERESTED_PARTIES_NOTIFICATIONS);
	}

	//GAP 112 FLUJOS MAIL EVICERTIA
	private InterestedParty getEmailVeterinario(String codArt) {
		InterestedParty envioVeterinario = null;
		String emailVeterinario = variablesServices.getVariableAsString(EMAIL_VETERINARIO);
		
		if(StringUtils.isNotBlank(emailVeterinario)) {
			MascotaMailVeterinarioExample example = new MascotaMailVeterinarioExample();
			example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodartEqualTo(codArt);
			
			List<MascotaMailVeterinario> lstArtVeterinario = mailVeterinarioMapper.selectByExample(example);
			if(!lstArtVeterinario.isEmpty()) {
				envioVeterinario = new InterestedParty();
				envioVeterinario.setAddress(emailVeterinario);
			}
		}
		return envioVeterinario;
	}



	private DatosCabeceraContrato generarDatosContrato(IskaypetLineaTicket iskaypetLineaTicket) {
		
		DatosCabeceraContrato datosCabecera = new DatosCabeceraContrato(iskaypetLineaTicket.getContratoAnimal());
		
		//Seteamos los valores del articulo
		datosCabecera.setCodArt(iskaypetLineaTicket.getCodArticulo());
		datosCabecera.setDesArt(iskaypetLineaTicket.getDesArticulo());
		
		Tienda tienda = sesion.getAplicacion().getTienda();
		datosCabecera.setDatosTienda(tienda.getDesAlmacen() + "," + tienda.getDomicilio() + "," + tienda.getCp() + ","
						+ tienda.getPoblacion());
		
		datosCabecera.setCorreoTienda(getEmailTienda());
		datosCabecera.setPrecio(iskaypetLineaTicket.getPrecioTotalConDto().toString());
		
		return datosCabecera;
	}

	/**
	 * CZZ-490 - Convierte una línea de ticket con datos de contrato animal en un objeto {@link DatosCabeceraContrato}.
	 *
	 * <p>Si el DTO del contrato o el ticket del contrato son nulos, devuelve {@code null}.</p>
	 *
	 * @param linea        Línea del ticket que contiene los datos del contrato animal
	 * @param ticketContrato Ticket con la información de contrato
	 * @param codLenguaje  Código del lenguaje para el contrato
	 * @return Objeto {@link DatosCabeceraContrato} con los datos consolidados o {@code null} si no procede
	 */
	public DatosCabeceraContrato convertir(IskaypetLineaTicket linea, TicketContratosBean ticketContrato, String codLenguaje ) {
		ContratoAnimalDto dto = linea.getContratoAnimal();
		if (dto == null || ticketContrato == null) {
			return null;
		}

		DatosCabeceraContrato contrato = new DatosCabeceraContrato();
		
		// Datos del animal
		contrato.setEspecie(dto.getEspecie());
		contrato.setRaza(dto.getRaza());
		contrato.setSexo(dto.getSexo());
		contrato.setPeso(dto.getPeso());
		contrato.setTipoIden(dto.getTipoIden());
		contrato.setNumIden(dto.getNumIden());
		contrato.setNombre(dto.getNombre());
		
		// Datos del cliente
		contrato.setDireccion(dto.getDireccion());
		contrato.setTlf(dto.getTlf());
		contrato.setEmail(dto.getEmail());
		contrato.setPrefijo(dto.getPrefijo());
		contrato.setCp(dto.getCp());
		contrato.setLocalidad(dto.getLocalidad());
		contrato.setProvincia(dto.getProvincia());
		contrato.setPoblacion(dto.getPoblacion());
		contrato.setEnviado(dto.isEnviado());

		// Datos del artículo
		contrato.setCodArt(linea.getCodArticulo());
		contrato.setDesArt(linea.getDesArticulo());
		contrato.setPrecio(linea.getPrecioTotalConDto().toString());
		
		// Datos de la tienda
		Tienda tienda = sesion.getAplicacion().getTienda();
		contrato.setDatosTienda(tienda.getDesAlmacen() + "," + tienda.getDomicilio() + "," + tienda.getCp() + ","
					+ tienda.getPoblacion());
		contrato.setTienda(tienda);
		contrato.setCorreoTienda(getEmailTienda());
		
		// Otros datos
		contrato.setFechaContrato(ticketContrato.getFecha().toString());
		contrato.setCodLenguaje(codLenguaje);
		contrato.setContratoRecuperad(ticketContrato.getContrato());
		contrato.setIdLinea(linea.getIdLinea());
		contrato.setUidTicket(ticketContrato.getUidTicket());
		
		addLaws(contrato, codLenguaje);
		
		return contrato;
	}
	
	

	private String completarContrato(DatosCabeceraContrato datosCabecera, String idiomaFidelizado) {
		String pdfBase64 = null;
		log.debug("completarContrato() - Completando contrato para envío...");
		try {
			Map<String, Object> mapaParametros = new HashMap<>();
			addLaws(datosCabecera, idiomaFidelizado);
			mapaParametros.put("datosCabecera", datosCabecera);
			
			//Split para mandar por parametros las diferentes IDENTIFICACIONES
			//Si viene con (-) lo mandamos vacío
			String[] identificadores = datosCabecera.getNumIden().split("\\|");
			
			String chip = identificadores[0].trim();
			String anilla = identificadores[1].trim();
			String cites = identificadores[2].trim();
			
			mapaParametros.put(PARAMETER_CHIP, chip);
			mapaParametros.put(PARAMETER_ANILLA, anilla);
			mapaParametros.put(PARAMETER_CITES, cites);
			
			mapaParametros.put("uidActividad", sesion.getAplicacion().getUidActividad());
			mapaParametros.put("lstLaws", datosCabecera.getLstLaws());
			mapaParametros.put("empresa", sesion.getAplicacion().getEmpresa());
			mapaParametros.put("idiomaStore", idiomaFidelizado.trim());

			try {

				JasperPrint jasperPrint = ImpresionJasper.preparaFacturaJasper("facturas/contratos/ContratoMascota", mapaParametros);
				log.debug("completarContrato() - Documento generado.");
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	            @SuppressWarnings("rawtypes")
				JRExporter exporterr = new JRPdfExporter();
	            exporterr.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
	            exporterr.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
	            exporterr.exportReport();
	            log.debug("completarContrato() - Documento generado 1.");
	            byte[] output = outputStream.toByteArray();
	            pdfBase64 = Base64.getEncoder().encodeToString(output);
	            log.debug("completarContrato() - Documento generado 2.");
	           
			} catch (JRException e) {
				log.error("completarContrato() - JRException - " + e.getMessage(), e);
			}
			 log.debug("completarContrato() - Completado contrato para envío");
			
		} catch (Exception e) {
			log.error("completarContrato() - Error al lanzar el contrato para su edición: " + e.getMessage());
		}
		return pdfBase64;

	}

	private void addLaws(DatosCabeceraContrato datosCabecera, String idiomaFidelizado) {
		String uidActividad = sesion.getAplicacion().getUidActividad();
		List<ContractLaw> listaLeyes = Optional.ofNullable(contractLawMapper.selectLaws(uidActividad, idiomaFidelizado))
				.filter(l -> !l.isEmpty())
				.orElseGet(() -> contractLawMapper.selectLaws(uidActividad, CASTELLANO));
		datosCabecera.setLstLaws(listaLeyes);
	}
	
	public void imprimirContratoTienda(DatosCabeceraContrato datosCabecera) {
		try {
			String idiomaTienda = sesion.getAplicacion().getStoreLanguageCode();
			Map<String, Object> mapaParametros = new HashMap<>();
			addLaws(datosCabecera, idiomaTienda);
			mapaParametros.put("datosCabecera", datosCabecera);
			mapaParametros.put("uidActividad", sesion.getAplicacion().getUidActividad());
			mapaParametros.put("lstLaws", datosCabecera.getLstLaws());
			mapaParametros.put("idiomaStore", idiomaTienda.trim());
			mapaParametros.put("empresa", sesion.getAplicacion().getEmpresa());
			IskaypetServicioImpresion.imprimirPantalla("ContratoMascota", mapaParametros);

		} catch (DeviceException e) {
			log.error("completarContrato() - Error al lanzar el contrato para su edición: " + e.getMessage());
		}
	}

	public static String encodePDFToBase64(String filePath) throws IOException {
		File pdfFile = new File(filePath);
		FileInputStream fileInputStream = new FileInputStream(pdfFile);
		byte[] pdfBytes = new byte[(int) pdfFile.length()];
		fileInputStream.read(pdfBytes);
		fileInputStream.close();

		String base64EncodedPDF = Base64.getEncoder().encodeToString(pdfBytes);

		return base64EncodedPDF;
	}

	public void generarXMLContratoMascota(ContratoMascotaDto contratoMascota) {

		TicketBean ticket = new TicketBean();
		SqlSession sqlSession = new SqlSession();
		DatosSesionBean datosSesion = new DatosSesionBean();
		String uidActividad = sesion.getAplicacion().getUidActividad();
		try {
			datosSesion.setUidActividad(uidActividad);

			ticket.setUidActividad(uidActividad);
			ticket.setIdTipoDocumento(ID_TIPO_DOC_CONTRATO);
			String uid = UUID.randomUUID().toString();
			ticket.setUidTicket(uid);
			ticket.setLocatorId(uid);
			String codalm = sesion.getAplicacion().getCodAlmacen();
			ticket.setCodAlmacen(codalm);
			ticket.setFecha(new Date());
			ticket.setCodTicket("*");
			ticket.setFirma("*");
			ticket.setSerieTicket("*");

			String codcaja = sesion.getAplicacion().getCodCaja();
			ticket.setCodcaja(codcaja);

			contratoMascota.setUidActividad(uidActividad);

			ticket.setIdTicket(servicioContadores.obtenerValorContador("ID_TICKET", uidActividad));

			byte[] xml = MarshallUtil.crearXML(contratoMascota);
			log.debug("guardarAuditoria() - XML de auditoría: " + new String(xml));
			ticket.setTicket(xml);

			sqlSession.openSession(SessionFactory.openSession());
			ticketsService.insertarTicket(sqlSession, ticket, false);
			sqlSession.commit();
		} catch (Exception e) {
			log.error("guardarAuditoria() - Ha habido un error al guardar la auditoria: " + e.getMessage(), e);
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}

	}
	
	public String comprobarConfiguracionEnvio() {
		String msg = "";
		if (StringUtils.isBlank(consultarCredencialesEvicertia())) {
			msg= msg+"\n -No están configuradas las credenciales para en el envío de contratos.";
		}
		if (StringUtils.isBlank(consultarUrlEvicertia())) {
			msg= msg+"\n -No está configurada la URL del servicio de Evicertia.";
		}
		return msg;
	}
	
	public String consultarCredencialesEvicertia() {
		String valorCredenciales = variablesServices.getVariableAsString(CREDENCIALES_EVICERTIA);
		return valorCredenciales;
	}
	
	private String consultarUrlEvicertia() {
		String valorUrl = variablesServices.getVariableAsString(URL_EVICERTIA);
		return valorUrl;
	}


	/* ########################################################################################################################### */
	/* ######################GAP 116 ENVIO DE INFORMACION DE NUEVOS CAMPOS DE TIENDA POR CONFIGURACIÓN############################ */
	/* ########################################################################################################################### */
	
	private String getEmailTienda() {
		String idClase = "D_ALMACENES_TBL.CODALM";
		String parametro = "EMAIL_TIENDA";
		String emailTienda = "";
		ValorParametroObjetoExample example = new ValorParametroObjetoExample();
		Criteria criteria = example.createCriteria();
		criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
		criteria.andIdObjetoEqualTo(sesion.getAplicacion().getCodAlmacen());
		criteria.andIdClaseEqualTo(idClase);
		criteria.andParametroEqualTo(parametro);
		
		List<ValorParametroObjeto> lstMailTienda = valorParametroObjetoMapper.selectByExample(example);
		
		if(lstMailTienda!= null && !lstMailTienda.isEmpty()) {
			emailTienda = lstMailTienda.get(0).getValor();
		}
		return emailTienda;
	}
	
	
	/* ########################################################################################################################### */
	/* ##################################GAP 117 RECUPERACIÓN DE CONTRATOS DESDE EL POS########################################### */
	/* ########################################################################################################################### */
	
	public List<TicketContratosBean> getContratosRealizados(@SuppressWarnings("rawtypes") ITicket ticketOperacion) {
		TicketContratosExample example = new TicketContratosExample();
		com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosExample.Criteria criteria = example.createCriteria();
		criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
		criteria.andUidTicketEqualTo(ticketOperacion.getUidTicket());
		List<TicketContratosBean> lstContratos = ticketContratosMapper.selectByExampleWithBLOBs(example);
		
		return lstContratos;
	}
	
	/**
	 * CZZ-490 - Obtiene el contrato realizado por primary key
	 * @param uidTicket
	 * @param idLinea
	 * @return
	 */
	public TicketContratosBean getContratosRealizadoByPrimaryKey(String uidTicket, Integer idLinea) {
		TicketContratosKey key = new TicketContratosKey();
		key.setUidActividad(sesion.getAplicacion().getUidActividad());
		key.setLinea(idLinea);
		key.setUidTicket(uidTicket);
		
		return ticketContratosMapper.selectByPrimaryKey(key);
	}
	
	
	/**
	 * CZZ-490 - Para evitar duplicación de PK antes consulta si existe en base de datos. De momento no se pone el update, porque se ha generado ya el tipodocumento 700.
	 * @param contratoMascota
	 * @param idLinea
	 */
	public void insertContratoRealizado(ContratoMascotaDto contratoMascota, Integer idLinea) {
		TicketContratosKey key = new TicketContratosKey();
		key.setUidTicket(contratoMascota.getUidTicket());
		key.setUidActividad(contratoMascota.getUidActividad());
		key.setLinea(idLinea);
		
		TicketContratosBean contratoInsertado = ticketContratosMapper.selectByPrimaryKey(key);
		if(contratoInsertado == null) {
			TicketContratosBean ticketInsert = new TicketContratosBean();
			ticketInsert.setContrato(contratoMascota.getContrato().getBytes());
			ticketInsert.setFecha(contratoMascota.getFechaEnvio());
			ticketInsert.setUidActividad(contratoMascota.getUidActividad());
			ticketInsert.setUidTicket(contratoMascota.getUidTicket());
			ticketInsert.setLinea(idLinea);
			ticketInsert.setMetodoFirma(IskaypetEvicertiaService.METODO_FIRMA_DIGITAL);
			ticketContratosMapper.insert(ticketInsert);
		}

	}
	
	/**
	 * CZZ-1355 - Actualiza el metodo de firma de esa linea
	 */
	public void actualizarMetodoFirma(String uidActividad, String uidTicket, Integer idLinea, String metodoFirma) {
		TicketContratosKey key = new TicketContratosKey();
		key.setUidTicket(uidTicket);
		key.setUidActividad(uidActividad);
		key.setLinea(idLinea);
		
		TicketContratosBean contrato = ticketContratosMapper.selectByPrimaryKey(key);
		if(contrato != null) {
			contrato.setMetodoFirma(metodoFirma);
			ticketContratosMapper.updateByPrimaryKey(contrato);
		}
	}
	
	
	
	
	
	public void imprimirContrato(DatosCabeceraContrato datosCabecera) throws Exception {

		try {
			String pdfBase64String;
			if (datosCabecera.getContratoRecuperad() != null) {
				pdfBase64String = new String(datosCabecera.getContratoRecuperad());
			} else {
				pdfBase64String = completarContrato(datosCabecera, datosCabecera.getCodLenguaje());
			}

			byte[] decodedBytes = Base64.getDecoder().decode(pdfBase64String);

			// Crear un archivo temporal
			File tempFile = File.createTempFile("temp_pdf_", ".pdf");

			// Guardar el PDF decodificado en el archivo temporal
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				fos.write(decodedBytes);
			}

			// Abrir el PDF con el visor predeterminado del sistema
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(tempFile);
			} else {
				throw  new Exception(I18N.getTexto( "La apertura del visor de PDF no es compatible en este sistema."));
			}
		} catch (IOException e) {
			log.error("imprimirContratoRecuperado() - Error IOException " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("imprimirContratoRecuperado() - Error Exception " + e.getMessage(), e);
			throw e;
		}
	
}
	
	/* ########################################################################################################################### */
	/* ##################################### GAP 125 CONTRATO ANIMAL: PREFIJO TELEFÓNICO ######################################### */
	/* ########################################################################################################################### */
	
	public List<PrefijosPaisesKey> getPrefijosPaises() {
		
		return prefijosPaisesMapper.getPrefijosPaises();
	}
	
	public List<PrefijosPaisesKey> getPrefijoFidelizado(String codPais){
		
		return prefijosPaisesMapper.getPrefijoFidelizado(sesion.getAplicacion().getUidInstancia(), codPais);
		
	}

}
