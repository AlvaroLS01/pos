package com.comerzzia.iskaypet.pos.services.promociones;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.persistence.promociones.PromocionExample;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleBean;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleExample;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
@Service
@Primary
public class IskaypetPromocionesService extends com.comerzzia.pos.services.promociones.PromocionesService {
	
    public String consultarTextoDetallePromocion(Long idPromocion, String codart) throws PromocionesServiceException{
        SqlSession sqlSession = new SqlSession();
        try{
            log.debug("consultarTextoPromocionPorDetalle() - Consultando texto de la promoción " + idPromocion + " por artículo " + codart);
            sqlSession.openSession(SessionFactory.openSession());
            
            String uidActividad = sesion.getAplicacion().getUidActividad();
            PromocionDetalleExample example = new PromocionDetalleExample();
            example.or()
                    .andUidActividadEqualTo(uidActividad)
                    .andIdPromocionEqualTo(idPromocion)
                    .andCodArticuloEqualTo(codart);
            
            List <PromocionDetalleBean> detalles = promocionDetalleMapper.selectByExampleWithBLOBs(example);
            
            if (detalles != null && !detalles.isEmpty() && StringUtils.isNotBlank(detalles.get(0).getTextoPromocion())) {
            	return detalles.get(0).getTextoPromocion();
            } else {
            	return "";
            }

        } catch (Exception e) {
            String msg = "No se ha encontrado ningún detalle de la promoción con id: " + idPromocion + " y artículo " + codart + " - " + e.getMessage();
            log.error("consultarTextoPromocionPorDetalle() - " + msg, e);
            throw new PromocionesServiceException(e);
        } finally {
            sqlSession.close();
        }
    }
    
    public String consultarDescripcionOTextoCabeceraPromocion(Long idPromocion) throws PromocionesServiceException{
        SqlSession sqlSession = new SqlSession();
        try{
            log.debug("consultarDescripcionOTextoCabeceraPromocion() - Consultando texto de la promoción " + idPromocion);
            sqlSession.openSession(SessionFactory.openSession());
            String textoPromocion = "";
            
            String uidActividad = sesion.getAplicacion().getUidActividad();
            PromocionExample example = new PromocionExample();
            example.or()
                    .andUidActividadEqualTo(uidActividad)
                    .andIdPromocionEqualTo(idPromocion);
            
            List<PromocionBean> promociones = promocionMapper.selectByExampleWithBLOBs(example);
            
            if (promociones != null && !promociones.isEmpty()) {
            	if (promociones.get(0).getDatosPromocion() != null) {
            		try {
            			textoPromocion = getTextoPromoFromXML(promociones);
            		} catch (Exception ignore) {}
            	}
            	if (StringUtils.isBlank(textoPromocion)) {
            		textoPromocion = promociones.get(0).getDescripcion();
            	}
            }
            
            return textoPromocion;

        } catch (Exception e) {
            String msg = "No se ha encontrado ninguna promoción con id: " + idPromocion + " - " + e.getMessage();
            log.error("consultarDescripcionOTextoCabeceraPromocion() - " + msg, e);
            throw new PromocionesServiceException(e);
        } finally {
            sqlSession.close();
        }
    }

	public String getTextoPromoFromXML(List<PromocionBean> promociones)
			throws XMLDocumentException {
		XMLDocument xmlPromocion = new XMLDocument(promociones.get(0).getDatosPromocion());
		String storeLanguageCode = sesion.getAplicacion().getStoreLanguageCode();
		
		String textPromo = null;
		String textPromoDefault = null;
		List<XMLDocumentNode> textPromoNodes = xmlPromocion.getNodos("textoPromocion");
		for(XMLDocumentNode textPromoNode : textPromoNodes) {
			String textPromoLanguageCode = textPromoNode.getAtributoValue("lang", true);
			if(StringUtils.isNotBlank(textPromoLanguageCode)){
				if(textPromoLanguageCode.equals(storeLanguageCode)) {
					textPromo = textPromoNode.getValue();
					break;
				}
			}else {
				textPromoDefault = textPromoNode.getValue();
			}
		}

		if(StringUtils.isBlank(textPromo)) {
			textPromo = textPromoDefault;
		}
		
		if (StringUtils.isNotBlank(textPromo)) {
			return textPromo;
		} else {
			return "";			
		}
	}

    public Map<String, String> getExtensionesPromocion(long idPromocion){
        String uidActividad = sesion.getAplicacion().getUidActividad();
        PromocionExample example = new PromocionExample();
        example.or()
                .andUidActividadEqualTo(uidActividad)
                .andIdPromocionEqualTo(idPromocion);

        List<PromocionBean> promociones = promocionMapper.selectByExampleWithBLOBs(example);

        PromocionBean promocion = null;
        if(!promociones.isEmpty()){
            promocion = promociones.get(0);
        }

        Map<String, String> mapaExtensiones = new HashMap<>();

        if (promocion != null && promocion.getDatosPromocion() != null) {
                try {
                    mapaExtensiones = getExtensionesFromXML(promocion);
                } catch (XMLDocumentException e) {
                    log.error("getExtensionesPromocion() - " + e.getMessage());
                }
            }

        return mapaExtensiones;
    }


    public Map<String, String> getExtensionesFromXML(PromocionBean promocion)
            throws XMLDocumentException {
        XMLDocument xmlPromocion = new XMLDocument(promocion.getDatosPromocion());

        Map<String, String> mapaExtensiones = new HashMap<>();
        List<XMLDocumentNode> nodosExtensiones = xmlPromocion.getNodos("extensiones");
        for (XMLDocumentNode nodoExtensiones : nodosExtensiones) {
            List<XMLDocumentNode> nodosExtension = nodoExtensiones.getHijos();
            for (XMLDocumentNode nodoExtension : nodosExtension) {
                String clave = nodoExtension.getNodo("clave").getValue();
                String valor = nodoExtension.getNodo("valor").getValue();

                mapaExtensiones.put(clave, valor);
            }
        }

        return mapaExtensiones;
    }

}
