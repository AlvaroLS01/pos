package com.comerzzia.bimbaylola.pos.services.edicom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal.ProcesadorIdFiscalCO;
import com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal.ProcesarDocumentoFiscalException;
import com.comerzzia.bimbaylola.pos.services.core.variables.ByLVariablesServices;
import com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues.Autorizado;
import com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues.Cabcustom;
import com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues.Cabfac;
import com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues.Impfac;
import com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues.Impfaclin;
import com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues.Legalfac;
import com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues.Linfac;
import com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues.Payfac;
import com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues.Reffac;
import com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues.Respfac;
import com.comerzzia.bimbaylola.pos.services.edicom.util.EdicomFormat;
import com.comerzzia.bimbaylola.pos.services.ticket.cabecera.ByLCabeceraTicket;
import com.comerzzia.core.util.fechas.FechaException;
import com.comerzzia.core.util.fechas.Fechas;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.fiscaldata.FiscalData;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
@Scope("prototype")
public class EdicomFile {

	protected static final Logger log = Logger.getLogger(EdicomFile.class);
	
	private static final String CODIGO_PAIS_CO = "CO";
	public static final String CO_DEFAULT_NUMERO_INDENTIFICACION = "222222222222";
	
	protected Autorizado autorizado;
	protected List<Cabcustom> listaCabcustom;
	protected Cabfac cabfac;
	protected Impfac impfac;
	protected List<Impfaclin> listaImpfaclin;
	protected Legalfac legalfac;
	protected List<Linfac> listaLinfac;
	protected Payfac payfac;
	/*
	 * Solo cuando es nota crédito
	 */
	protected Reffac reffac;
	protected List<Respfac> listaRespfac;

	@Autowired
	protected VariablesServices variablesService;
	
	public EdicomFile() {
	}

	protected void inicializaObjetos() {
		autorizado = new Autorizado();
		listaCabcustom = new ArrayList<Cabcustom>();
		cabfac = new Cabfac();
		impfac = new Impfac();
		listaImpfaclin = new ArrayList<Impfaclin>();
		legalfac = new Legalfac();
		listaLinfac = new ArrayList<Linfac>();
		payfac = new Payfac();
		listaRespfac = new ArrayList<Respfac>();
		reffac = new Reffac();
	}

	public File construirArchivoTxt(Ticket ticket) throws Exception {
		log.debug("construirArchivoTxt() - rellena los valores para el envío");
		inicializaObjetos();
		rellenaValores(ticket);
		String registros = toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String nombre = Cabfac.LABEL_CAB + "_" + cabfac.getId() + "_" + sdf.format(new Date());
		File archivo = new File(nombre + ".txt");
		FileWriter escritor = null;
		try {
			escritor = new FileWriter(archivo);
			escritor.write(registros);

			return archivo;
		}
		catch (Exception e) {
			log.error("devuelveArchivoTxtEdicom() - Ha habido un error al construir el fichero de texto", e);
			throw new Exception(e);
		}
		finally {
			if (escritor != null) {
				escritor.close();
			}
		}
	}


	public File devuelveArchivoComprimidoEdicom(Ticket ticket) throws Exception {
		log.debug("devuelveArchivoComprimidoEdicom()");
		ZipOutputStream zos = null;
		FileInputStream fis = null;
		File archivo = null;
		try {
			archivo = construirArchivoTxt(ticket);
			String nombre = archivo.getName();
			if (nombre.contains(".")) {
				nombre = nombre.substring(0, nombre.indexOf("."));
			}
			log.debug("devuelveArchivoComprimidoEdicom() - comprimiendo archivo a: " + nombre);
			File archivoZip = new File(nombre + ".zip");

			FileOutputStream fos = new FileOutputStream(archivoZip);
			zos = new ZipOutputStream(fos);

			byte[] buffer = new byte[1024];
			fis = new FileInputStream(archivo);
			zos.putNextEntry(new ZipEntry(archivo.getName()));

			int longitud;
			while ((longitud = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, longitud);
			}

			return archivoZip;
		}
		catch (Exception e) {
			log.error("devuelveArchivoComprimidoEdicom() - Ha habido un error al construir el fichero comprimido", e);
			throw new ProcesarDocumentoFiscalException(e);
		}
		finally {
			if (zos != null) {
				zos.closeEntry();
			}
			if (fis != null) {
				fis.close();
			}
			if (zos != null) {
				zos.close();
			}
			if (archivo != null) {
				archivo.delete();
			}
		}
	}

	public void rellenaValores(Ticket ticket) {
		log.debug("rellenaValores()");
		rellenaCabfac(ticket);
		rellenaImpfac(ticket);
		rellenaCabcustom(ticket);
		rellenaLegalfac(ticket);
		rellenaInformacionLineas(ticket);
		rellenaPayfac(ticket);
		rellenaReffac(ticket);
		rellenaRespfac(ticket);
	}

	private void rellenaRespfac(Ticket ticket) {
		//boolean esConsumidorFinal = ticket.getCliente().getCif().equals(CO_DEFAULT_NUMERO_INDENTIFICACION);
		Respfac respfacReceptor = new Respfac();
		respfacReceptor.setTipoInterlocutor(Respfac.TIPO_INTERLOCUTOR_RECEPTOR);
		respfacReceptor.setCodigo(Respfac.CODIGO_CONSUMIDOR_FINAL);

		Respfac respfacEmisor = new Respfac();
		respfacEmisor.setTipoInterlocutor(Respfac.TIPO_INTERLOCUTOR_EMISOR);
		respfacEmisor.setCodigo(Respfac.CODIGO_CONSUMIDOR_FINAL);

		log.debug("rellenaRespfac() - Etiqueta RESPFAC para receptor: " + respfacReceptor + " y para emisor: " + respfacEmisor);

		listaRespfac.add(respfacReceptor);
		listaRespfac.add(respfacEmisor);
	}

	private void rellenaReffac(Ticket ticket) {
		log.debug("rellenaReffac() - Rellenando REFFAC");
		if (ticket.isEsDevolucion() && ticket.getCabecera().getDatosDocOrigen() != null) {
			log.debug("rellenaReffac() - Creando etiqueta reffac.");

			if (ticket.getCabecera().getFiscalData().getProperty(ProcesadorIdFiscalCO.ORIGEN_CUFE) != null) {
				reffac.setUUID(ticket.getCabecera().getFiscalData().getProperty(ProcesadorIdFiscalCO.ORIGEN_CUFE).getValue());
			}
			
			if (ticket.getCabecera().getFiscalData().getProperty(ProcesadorIdFiscalCO.ORIGEN_IDENTIFICADOR_FISCAL) != null) {
				reffac.setIdFactura(ticket.getCabecera().getFiscalData().getProperty(ProcesadorIdFiscalCO.ORIGEN_IDENTIFICADOR_FISCAL).getValue());
			}
			
			if (ticket.getCabecera().getDatosDocOrigen() != null) {
				String fechaOrigenFormateada = null;
				try {
					Date fechaOrigen = Fechas.getFecha((ticket.getCabecera().getDatosDocOrigen().getFecha()));
					fechaOrigenFormateada = EdicomFormat.devuelveFechaFormateada(fechaOrigen);
				}
				catch (FechaException ignore) {
				}

				reffac.setFechaFactura(fechaOrigenFormateada);
			}
			
			log.debug("rellenaReffac() - Reffac: " + reffac.toString());
		}
		else {
			reffac = null;
		}
	}

	protected void rellenaPayfac(Ticket ticket) {
		/*
		 * No habría que setear nada, ya tiene valores fijos
		 */
	}

	@SuppressWarnings("unchecked")
	protected void rellenaInformacionLineas(Ticket ticket) {
		List<LineaTicket> lineas = ticket.getLineas();
		// linfac y impfaclin
		for (LineaTicket linea : lineas) {
			// Seteamos información de la linea
			log.debug("rellenaInformacionLineas() - Creando etiquetas linfac y impfaclin de la línea: " + linea.getIdLinea());
			Linfac linfac = new Linfac();
			linfac.setIdlin(linea.getIdLinea().toString());
			linfac.setCantidad(linea.getCantidad());
			linfac.setImporteLinea(linea.getImporteConDto());
			linfac.setDescripcion(linea.getDesArticulo());
			linfac.setIditem(linea.getCodArticulo());
			linfac.setInfoAdicional1(linea.getDesglose1());
			linfac.setInfoAdicional2(linea.getDesglose2());
			linfac.setPrecioUnitario(linea.getPrecioSinDto());
			
			// Seteamos información de los impuestos de las líneas
			Impfaclin impfaclin = new Impfaclin();
			impfaclin.setMontoImpuesto(linea.getImporteTotalConDto().subtract(linea.getImporteConDto()));
			impfaclin.setPorcentajeImpuesto(String.valueOf(((ByLCabeceraTicket)ticket.getCabecera()).getSubtotalesIva().get(0).getPorcentaje().setScale(0)));
			impfaclin.setBaseImponible(linea.getImporteConDto());
			
			log.debug("rellenaInformacionLineas() - Etiquetas linfac: "+ linfac + "y impfaclin: "+ impfaclin + " de la línea: " + linea.getIdLinea());
			
			listaLinfac.add(linfac);
			listaImpfaclin.add(impfaclin);
		}
	}

	protected void rellenaLegalfac(Ticket ticket) {
		log.debug("rellenaLegalfac() - Creando etiqueta legalfac.");

	}

	protected void rellenaCabcustom(Ticket ticket) {
		log.debug("rellenaCabcustom() - Creando etiqueta CABCUSTOM para comerzzia y para ref interna.");
		Cabcustom cabcustomComerzzia = new Cabcustom();
		cabcustomComerzzia.setEtiqueta(Cabcustom.ETIQUETA_SISTEMA_COMERZZIA.toUpperCase());
		cabcustomComerzzia.setDescripcion(Cabcustom.ETIQUETA_SISTEMA_COMERZZIA);
		cabcustomComerzzia.setValor(Cabcustom.ETIQUETA_COMERZZIA);

		Cabcustom cabcustomRefInterna = new Cabcustom();
		cabcustomRefInterna.setEtiqueta(Cabcustom.ETIQUETA_REFERENCIA_INTERNA);
		cabcustomRefInterna.setDescripcion(Cabcustom.ETIQUETA_REFERENCIA_INTERNA);
		cabcustomRefInterna.setValor(ticket.getCabecera().getCodTicket());

		log.debug("rellenaCabcustom() - Etiqueta CABCUSTOM para comerzzia: " + cabcustomComerzzia + " y para ref interna: " + cabcustomRefInterna);
		
		listaCabcustom.add(cabcustomComerzzia);
		listaCabcustom.add(cabcustomRefInterna);

	}

	protected void rellenaImpfac(Ticket ticket) {
		SubtotalIvaTicket subTotal = (SubtotalIvaTicket) ticket.getCabecera().getSubtotalesIva().get(0);
		log.debug("rellenaImpfac() - Creando etiqueta impfac del impuesto: " + subTotal.getCodImpuesto());
		impfac.setMontoImpuesto(subTotal.getImpuestos());
		impfac.setBaseImponible(subTotal.getBase());
		impfac.setPorcentajeImpuesto(subTotal.getPorcentaje().setScale(0).toEngineeringString());
		
		log.debug("rellenaImpfac() - Etiqueta impfac: " + impfac.toString());

	}

	protected void rellenaCabfac(Ticket ticket) {
		log.debug("rellenaCabfac() - Creando etiqueta CABFAC.");
		/*
		 * Ambiente = 1 (producción) ambiente = 2 (Testing)
		 */
		cabfac.setAmbiente(variablesService.getVariableAsString(ByLVariablesServices.CO_EDICOM_AMBIENTE));
		cabfac.setEmisorEmail(variablesService.getVariableAsString(ByLVariablesServices.CO_EDICOM_CORREOELECTRONICO));
		ByLCabeceraTicket cabecera = (ByLCabeceraTicket) ticket.getCabecera();
		if (ticket.isEsDevolucion()) {
			cabfac.setTipodoc(Cabfac.TIPO_DOC_DEVOLUCION);
			cabfac.setTipoFactura(Cabfac.TIPO_FACTURA_DEVOLUCION);
			if (cabecera.getDatosDocOrigen() == null) {
				cabfac.setTipoOperacion(Cabfac.TIPO_OPERA_DEVOLUCION_SIN_REFERENCIA);
			}
			else {
				cabfac.setTipoOperacion(Cabfac.TIPO_OPERA_DEVOLUCION_REFERENCIADA);
			}
		}
		else {
			cabfac.setTipodoc(Cabfac.TIPO_DOC_VENTA);
			cabfac.setTipoFactura(Cabfac.TIPO_FACTURA_VENTA);
			cabfac.setTipoOperacion(Cabfac.TIPO_OPERA_VENTAS);
		}
		FiscalData fiscalData = cabecera.getFiscalData();
		if (fiscalData != null) {
			cabfac.setPrefijoRangoFolios(fiscalData.getProperty(ProcesadorIdFiscalCO.PREF).getValue());

			cabfac.setInicioRangoFolios(fiscalData.getProperty(ProcesadorIdFiscalCO.RANGO_INICIO).getValue());
			cabfac.setFinRangoFolios(fiscalData.getProperty(ProcesadorIdFiscalCO.RANGO_FIN).getValue());

			if (fiscalData.getProperty(ProcesadorIdFiscalCO.CLAVE_TECNICA) != null) {
				cabfac.setClaveTecnica(fiscalData.getProperty(ProcesadorIdFiscalCO.CLAVE_TECNICA).getValue());
			}
			if (fiscalData.getProperty(ProcesadorIdFiscalCO.NUM_RESOLUCION) != null) {
				cabfac.setNumeroResolucion(fiscalData.getProperty(ProcesadorIdFiscalCO.NUM_RESOLUCION).getValue());
			}
			cabfac.setId(fiscalData.getProperty(ProcesadorIdFiscalCO.IDENTIFICADOR_FISCAL_EDICOM).getValue());
			cabfac.setFechaResolucion(null); // no se informa
			
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			String rangoFechaInicio = fiscalData.getProperty(ProcesadorIdFiscalCO.RANGO_FECHA_INICIO).getValue();
			String rangoFechaFin = fiscalData.getProperty(ProcesadorIdFiscalCO.RANGO_FECHA_FIN).getValue();
			try {
				cabfac.setFechaVigenciaInicio(formato.parse(rangoFechaInicio));
				cabfac.setFechaVigenciaFin(formato.parse(rangoFechaFin));
			}
			catch (ParseException ignore) {
				log.error("rellenaCabfac() - Ha ocurrido un error al realizar el parseo de fecha: " + ignore.getMessage(), ignore);
			}
		}

		cabfac.setFechaEmision(ticket.getFecha());
		cabfac.setHoraEmision(ticket.getFecha());

		cabfac.setEmisorId(EdicomFormat.devuelveNITSinDv(cabecera.getEmpresa().getCif()));
		cabfac.setEmisorNombre(cabecera.getEmpresa().getDesEmpresa());
		cabfac.setEmisorDireccion(cabecera.getEmpresa().getDomicilio());
		cabfac.setEmisorCiudad(cabecera.getEmpresa().getPoblacion());
		cabfac.setEmisorRegion(EdicomFormat.devuelveCpDosPrimerosDigitos(cabecera.getEmpresa().getCp()));
		cabfac.setEmisorPais(CODIGO_PAIS_CO);
		cabfac.setEmisorRazonSocial(cabecera.getEmpresa().getDesEmpresa());
		// Cliente identificado o consumidor final
		setClienteEnCabfac((TicketVentaAbono) ticket);

		SubtotalIvaTicket subTotal = (SubtotalIvaTicket) cabecera.getSubtotalesIva().get(0);
		cabfac.setTotalLineas(subTotal.getBase());
		cabfac.setTotalBaseImponible(subTotal.getBase());
		cabfac.setTotalFactura(subTotal.getTotal());
		cabfac.setNumeroLineas(String.valueOf(ticket.getLineas().size()));
		cabfac.setDvEmisor(EdicomFormat.devuelveDvNIT(cabecera.getEmpresa().getCif()));
		cabfac.setTotalBrutoTrib(subTotal.getTotal());
		//Valor 11001
		cabfac.setCodigoMunicipioEmisor(cabecera.getEmpresa().getCp());
		cabfac.setCodigoMunicipioFiscalEmisor(cabecera.getEmpresa().getCp());
		cabfac.setEmisorCidudadFiscal(cabecera.getEmpresa().getPoblacion());
		cabfac.setEmisorRegionFiscal(EdicomFormat.devuelveCpDosPrimerosDigitos(cabecera.getEmpresa().getCp()));
		cabfac.setEmisorDireccionFiscal(cabecera.getEmpresa().getDomicilio());
		cabfac.setEmisorPaisFiscal(CODIGO_PAIS_CO);

	}

	public void setClienteEnCabfac(TicketVentaAbono ticket) {
		log.debug("setClienteEnCabfac()");
		ClienteBean cliente = ticket.getCliente();
		cabfac.setReceptorRazonSocial(cliente.getDesCliente());
		cabfac.setReceptorNombre(cliente.getDesCliente());
		// En devolución no trae el email y no es instancia de ByLFidelizacionBean
		cabfac.setCorreoElectronicoReceptor(cliente.getEmail());
		BigDecimal codigoIdentficacion = null;

		String cif = cliente.getCif().toString();
		if (Autorizado.NIT.equals(cliente.getTipoIdentificacion())) {
			codigoIdentficacion = Autorizado.CODIGO_NIT;
			cabfac.setDvReceptor(EdicomFormat.devuelveDvNIT(cliente.getCif()));
			cif = EdicomFormat.devuelveNITSinDv(cif);
		}
		else if (Autorizado.CEDULA_CIUDADANIA.equals(cliente.getTipoIdentificacion())) {
			codigoIdentficacion = Autorizado.CODIGO_CEDULA_CIUDADANIA;
		}
		else if (Autorizado.CEDULA_EXTRANJERIA.equals(cliente.getTipoIdentificacion())) {
			codigoIdentficacion = Autorizado.CODIGO_CEDULA_EXTRANJERIA;
		}
		else if (Autorizado.PASAPORTE.equals(cliente.getTipoIdentificacion())) {
			codigoIdentficacion = Autorizado.CODIGO_PASAPORTE;
		}
		cabfac.setReceptorTipoIdentificacion(codigoIdentficacion);
		cabfac.setReceptorId(cif);
		//BYL-230 solo si no es devolucion se envía el codPais del cliente.
		if(!ticket.isEsDevolucion()) {
			cabfac.setReceptorPais(cliente.getCodpais());
		}
	}

	@Override
	public String toString() {
		String cadenasRegistros = "";
		cadenasRegistros = EdicomFormat.truncarRegistro(cabfac.toString()) + "\n";
		for (Respfac respfac : listaRespfac) {
			cadenasRegistros += EdicomFormat.truncarRegistro(respfac.toString()) + "\n";
		}
		cadenasRegistros += EdicomFormat.truncarRegistro(impfac.toString()) + "\n";
		cadenasRegistros += EdicomFormat.truncarRegistro(payfac.toString()) + "\n";
		if (reffac != null) {
			cadenasRegistros += EdicomFormat.truncarRegistro(reffac.toString()) + "\n";
		}
		for (Cabcustom cabcustom : listaCabcustom) {
			cadenasRegistros += EdicomFormat.truncarRegistro(cabcustom.toString()) + "\n";
		}
		
		cadenasRegistros += legalfac.toString();
		
		/*
		 * Una cadena por cada línea y sus impuestos
		 */
		for (int i = 0; i < listaLinfac.size(); i++) {
			Linfac linfac = listaLinfac.get(i);
			Impfaclin impfaclin = listaImpfaclin.get(i);
			cadenasRegistros += EdicomFormat.truncarRegistro(linfac.toString()) + "\n";
			cadenasRegistros += EdicomFormat.truncarRegistro(impfaclin.toString()) + "\n";
		}
		
		return cadenasRegistros;
	}
}
