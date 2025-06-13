package com.comerzzia.bimbaylola.pos.services.core.documentos;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.services.ticket.cabecera.ByLCabeceraTicket;
import com.comerzzia.bimbaylola.pos.services.ticket.cabecera.PropiedadesDocumento;
import com.comerzzia.pos.persistence.core.documentos.propiedades.PropiedadDocumentoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.conceptosalmacen.ConceptoServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.DocumentoNotFoundException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.documentos.propiedades.PropiedadesDocumentoService;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.cabecera.DatosDocumentoOrigenTicket;
import com.comerzzia.pos.util.config.SpringContext;

@Component
@Primary
public class ByLDocumentos extends Documentos {

	public static final String FACTURA_SIMPLIFICADA_ESTANDAR = "FS";
	public static final String FACTURA_COMPLETA_ESTANDAR = "FT";
	public static final String NOTA_CREDITO_ESTANDAR = "NC";
	public static final String VENTA_CREDITO_ESTANDAR = "VC";
	public static final String APARTADOS_ESTANDAR = "AP";
	public static final String CIERRE_CAJA_ESTANDAR = "CJCIERRE";
	public static final String RESERVAS = "RE";
	public static final String BOLETA = "BO";
	
	// MOVIMIENTOS ERP
	public static final String ANTICIPO_RESERVA_ERP = "AR";
	public static final String APERTURA_CAJA_ERP = "CA";
	public static final String CIERRE_CAJA_ERP = "CC";
	public static final String MOVIMIENTO_CAJA_ERP = "CM";
	public static final String MOVIMIENTO_CAJA_SALIDA_BANCO_ERP = "CB";
	public static final String RECUENTO_CAJA_ERP = "CR";
	
	public static String PROPIEDAD_TIPO_DOCUMENTO_ESTANDAR = "TIPO_DOCUMENTO_ESTANDAR";
	 
	public static final String TIPO_DOCUMENTO = "TIPO_DOCUMENTO";
	public static final String SIGNO_DOCUMENTO = "SIGNO_DOCUMENTO";

	protected Map<String, String> documentosEstandar; // Contiene el código del documento asociado al código de su tipo de documento estandar
 
    public ByLDocumentos() {
    	super();
    	documentosEstandar = new LinkedHashMap<String, String>();
    }
    
	@Override
	public void inicializar(String uidActividad, String codPais) throws DocumentoException, DocumentoNotFoundException, ConceptoServiceException {
	   
	    super.inicializar(uidActividad, codPais);
	    
	    PropiedadesDocumentoService propiedadesDocumentoService = SpringContext.getBean(PropiedadesDocumentoService.class);
	    
	    //Cargamos las relaciones de tipo de documento estandar asociado
        List<PropiedadDocumentoBean> propiedadesDocumentoEstandar = propiedadesDocumentoService.consultarPropiedadesDocumentos(uidActividad, PROPIEDAD_TIPO_DOCUMENTO_ESTANDAR);
        for (PropiedadDocumentoBean propiedadDocumentoEstandar : propiedadesDocumentoEstandar) {
            if (documentos.containsKey(propiedadDocumentoEstandar.getIdTipoDocumento())) {
            	TipoDocumentoBean tipoDocumento = documentos.get(propiedadDocumentoEstandar.getIdTipoDocumento());
            	if(!StringUtils.isEmpty(propiedadDocumentoEstandar.getValor())){
                  documentosEstandar.put(propiedadDocumentoEstandar.getValor(), tipoDocumento.getCodtipodocumento());
            	}
            }
        }
        
        FACTURA_SIMPLIFICADA = documentosEstandar.get(FACTURA_SIMPLIFICADA) != null ? documentosEstandar.get(FACTURA_SIMPLIFICADA) : FACTURA_SIMPLIFICADA;
        FACTURA_COMPLETA = documentosEstandar.get(FACTURA_COMPLETA) != null ? documentosEstandar.get(FACTURA_COMPLETA) : FACTURA_COMPLETA;
        NOTA_CREDITO = documentosEstandar.get(NOTA_CREDITO) != null ? documentosEstandar.get(NOTA_CREDITO) : NOTA_CREDITO;
        VENTA_CREDITO = documentosEstandar.get(VENTA_CREDITO) != null ? documentosEstandar.get(VENTA_CREDITO) : VENTA_CREDITO;
        APARTADOS = documentosEstandar.get(APARTADOS) != null ? documentosEstandar.get(APARTADOS) : APARTADOS;
        CIERRE_CAJA = documentosEstandar.get(CIERRE_CAJA) != null ? documentosEstandar.get(CIERRE_CAJA) : CIERRE_CAJA;
	}
	
	@SuppressWarnings("rawtypes")
	public void setPropiedadesDocumento(Ticket ticket) throws DocumentoException {
		log.debug("setPropiedadesDocumento() - Recogiendo las variables TIPO_DOCUMENTO y SIGNO_DOCUMENTO del ticket " + ticket.getUidTicket());
		
		// ---- DOCUMENTO PRINCIPAL ----
		PropiedadesDocumento propiedadesDocumento = ((ByLCabeceraTicket) ticket.getCabecera()).getPropiedadesDocumento();

		// 1. TIPO_DOCUMENTO
		PropiedadDocumentoBean propiedadTipoDocumento = getDocumento(ticket.getCabecera().getTipoDocumento()).getPropiedades().get(TIPO_DOCUMENTO);
		if (propiedadTipoDocumento != null && StringUtils.isNotBlank(propiedadTipoDocumento.getValor())) {
			propiedadesDocumento.setTipoDocumento(propiedadTipoDocumento.getValor());
		}

		// 2. SIGNO_DOCUMENTO
		PropiedadDocumentoBean propiedadSignoDocumento = getDocumento(ticket.getCabecera().getTipoDocumento()).getPropiedades().get(SIGNO_DOCUMENTO);
		if (propiedadSignoDocumento != null && StringUtils.isNotBlank(propiedadSignoDocumento.getValor())) {
			propiedadesDocumento.setSignoDocumento(propiedadSignoDocumento.getValor());
		}

		// ---- DOCUMENTO ORIGEN ----
		PropiedadesDocumento propiedadesDocumentoOrigen = new PropiedadesDocumento();
		DatosDocumentoOrigenTicket datosDocumentoOrigenTicket = ticket.getCabecera().getDatosDocOrigen();
		if (datosDocumentoOrigenTicket != null && datosDocumentoOrigenTicket.getIdTipoDoc() != null) {
			log.debug("setPropiedadesDocumento() - Recogiendo las variables TIPO_DOCUMENTO y SIGNO_DOCUMENTO del ticket " + datosDocumentoOrigenTicket.getUidTicket());

			// 1. TIPO_DOCUMENTO
			PropiedadDocumentoBean datosDocOrigenpropiedadTipoDocumento = getDocumento(datosDocumentoOrigenTicket.getIdTipoDoc()).getPropiedades().get(TIPO_DOCUMENTO);
			if (datosDocOrigenpropiedadTipoDocumento != null && StringUtils.isNotBlank(datosDocOrigenpropiedadTipoDocumento.getValor())) {
				propiedadesDocumentoOrigen.setTipoDocumento(datosDocOrigenpropiedadTipoDocumento.getValor());
			}

			// 2. SIGNO_DOCUMENTO
			PropiedadDocumentoBean datosDocOrigenpropiedadSignoDocumento = getDocumento(datosDocumentoOrigenTicket.getIdTipoDoc()).getPropiedades().get(SIGNO_DOCUMENTO);
			if (datosDocOrigenpropiedadSignoDocumento != null && StringUtils.isNotBlank(datosDocOrigenpropiedadSignoDocumento.getValor())) {
				propiedadesDocumentoOrigen.setSignoDocumento(datosDocOrigenpropiedadSignoDocumento.getValor());
			}

			((ByLCabeceraTicket) ticket.getCabecera()).setPropiedadesDocumentoOrigen(propiedadesDocumentoOrigen);
		}
	}
	
}
