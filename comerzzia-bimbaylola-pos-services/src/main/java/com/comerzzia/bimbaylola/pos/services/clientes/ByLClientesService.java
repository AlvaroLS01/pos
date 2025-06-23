package com.comerzzia.bimbaylola.pos.services.clientes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.clientes.ClienteExample;
import com.comerzzia.pos.persistence.clientes.ClienteExample.Criteria;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.clientes.ClientesService;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentNotFoundException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentServiceException;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Primary
public class ByLClientesService extends ClientesService{

	protected static final Logger log = Logger.getLogger(ByLClientesService.class);
	
	/**
	 * Realiza la comprobación de que el documento esta correctamente
	 * escrito, según el tipo de documento y el país.
	 * @throws ClienteValidatedException 
	 */
 	public Boolean validarDocumento(String claseValidacion, String codigoTipoIdent, 
			String codPaisSelect, String numDocIdent) throws ClienteValidatedException {

		Boolean validador = false;

		try {
			if (claseValidacion != null) {
				IValidadorDocumentoIdentificacion validadorDocumentoIdentificacion = (IValidadorDocumentoIdentificacion) Class
						.forName(claseValidacion).newInstance();

				/* Comprobamos el pais para saber si comprobar el tipo de documento o no. */
				String codPais = StringUtils.isNotEmpty(codPaisSelect) ? codPaisSelect
						: sesion.getAplicacion().getTienda().getCliente().getCodpais();
				/* Según el código de pais, seleccionamos una validación. */
				switch (codPais) {
					/* =========== PANAMA =========== */
				case "PA":
					if(StringUtils.isBlank(numDocIdent)) {
						throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
					}
					if(validadorDocumentoIdentificacion.validarDocumentoIdentificacion(numDocIdent)) {
						validador = true;
					}
					else {
						throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
					}
				/* =========== ESPAÑA =========== */
				case "ES":
					/*
					 * Comprobamos el tipo de documento y en caso de no coincidir, enviamos un aviso
					 * a pantalla.
					 */
					if (comprobarTipoDocumentoES(numDocIdent).equals("")) {
						throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
					} else {
						if (validadorDocumentoIdentificacion.validarDocumentoIdentificacion(numDocIdent)) {
							validador = true;
						} else {
							throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
						}
					}
					break;
				/* =========== CHILE =========== */
				case "CL":
					if (comprobarTipoDocumentoCL(numDocIdent).equals("")) {
						throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
					} else {
						if (validadorDocumentoIdentificacion.validarDocumentoIdentificacion(numDocIdent)) {
							validador = true;
						} else {
							throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
						}
					}
					break;
				/* =========== POLONIA =========== */
				case "PL":
					if (StringUtils.isBlank(numDocIdent)) {
						throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
					} else {
						if (validadorDocumentoIdentificacion.validarDocumentoIdentificacion(numDocIdent)) {
							validador = true;
						} else {
							throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
						}
					}
					break;
				/* =========== ITALIA =========== */
				case "IT":
					/* =========== REINO UNIDO =========== */
				case "GB":
					/* =========== FRANCIA =========== */
				case "FR":
					/* =========== PORTUGAL =========== */
				case "PT":
					if (StringUtils.isBlank(numDocIdent)) {
						throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
					} else {
						if (validadorDocumentoIdentificacion.validarDocumentoIdentificacion(numDocIdent)) {
							validador = true;
						} else {
							throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
						}
					}
					break;
					/* =========== BÉLGICA =========== */
				case "BE":
					/* =========== SINGAPUR =========== */
				case "SG":
					/* =========== MÉXICO =========== */
				case "MX":

					/* =========== COLOMBIA =========== */
				case "CO":
					if (StringUtils.isBlank(numDocIdent)) {
						throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
					} else {
						if (validadorDocumentoIdentificacion.validarDocumentoIdentificacion(numDocIdent)) {
							validador = true;
						} else {
							throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
						}
					}
					break;
					/* =========== ECUADOR =========== */
				case "EC":
					if (StringUtils.isBlank(numDocIdent)) {
						throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
					} else {
						if (validadorDocumentoIdentificacion.validarDocumentoIdentificacion(numDocIdent)) {
							validador = true;
						} else {
							throw new ClienteValidatedException(I18N.getTexto("Por favor, revisa el Tipo Documento seleccionado y si el Documento Fiscal es correcto. Gracias"));
						}
					}
					/* =========== MALASIA =========== */
				case "MY":

//					default:
//
//						if (validadorDocumentoIdentificacion.validarDocumentoIdentificacion(numDocIdent)) {
//							validador = true;
//						}
//						else {
//							throw new ClienteValidatedException(I18N.getTexto("Documento no valido"));
//						}
				

				}
			} else {
				validador = true;
			}
		} catch (Exception e) {
			log.error("Se ha producido un error inesperado: " + e.getMessage(), e);
			throw new ClienteValidatedException(e.getMessage());
		}

		return validador;

	}
	
	/**
	 * Comprueba el tipo documento que hemos seleccionado en la pantalla.
	 * @return numDocIdent : Documento introducido.
	 * @throws TiposIdentNotFoundException
	 * @throws TiposIdentServiceException
	 */
 	public String comprobarTipoDocumentoES(String numDocIdent) 
 			throws TiposIdentNotFoundException, TiposIdentServiceException{
		
		String tipoDocu = "";
		/* Creamos una expresion logica para comprobar que es CIF. */
		Pattern maskCIF = Pattern.compile("^[a-zA-Z].*");
        Matcher matcherCIF = maskCIF.matcher(numDocIdent);
		
    	if(matcherCIF.matches()){
    		tipoDocu = "CIF";
    	}else if(numDocIdent.equals("")){
    		tipoDocu = "";
    	}else{
    		tipoDocu = "DNI";
    	}
    	
		return tipoDocu;
	}
 	
 	public String comprobarTipoDocumentoCL(String numDocIdent) 
 			throws TiposIdentNotFoundException, TiposIdentServiceException{
		
		String tipoDocu = "";
		/* Creamos una expresion logica para comprobar que es RUT/RUN. */
		Pattern maskRUT = Pattern.compile("^[0-9]+-[0-9kK]{1}$");
        Matcher matcherRUT = maskRUT.matcher(numDocIdent);
		
    	if(matcherRUT.matches()){
    		tipoDocu = "RUT";
    	}else if(numDocIdent.equals("")){
    		tipoDocu = "";
    	}else{
    		tipoDocu = "RUN";
    	}
    	
		return tipoDocu;
	}

	public List<ClienteBean> consultarClientesCIF(String cif) {
        log.debug("consultarClientesCIF() - consultando clientes con CIF: " + cif);
        List<ClienteBean> res = new ArrayList<>();
        SqlSession sqlSession = new SqlSession();
        try {
        	if(StringUtils.isNotBlank(cif)) {
	            sqlSession.openSession(SessionFactory.openSession());
	            String uidActividad = sesion.getAplicacion().getUidActividad();
	            ClienteExample example = new ClienteExample();   
	            Criteria or = example.createCriteria();
	            or.andUidActividadEqualTo(uidActividad).andActivoEqualTo(Boolean.TRUE).andCifEqualTo(cif.trim());
	            res = clienteMapper.selectByExample(example);
            }
        }
        catch (Exception e) {
            String msg = "Se ha producido un error consultando clientes:" + e.getMessage();
            log.error("consultarClientesCIF() - " + msg, e);
        } finally {
            sqlSession.close();
        }
        
        return res;   
    }
 	
}
