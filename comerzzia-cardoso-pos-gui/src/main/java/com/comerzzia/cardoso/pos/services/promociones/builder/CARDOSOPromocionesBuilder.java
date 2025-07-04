//package com.comerzzia.cardoso.pos.services.promociones.builder;
//
//import java.util.List;
//
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//
//import com.comerzzia.cardoso.pos.services.promociones.tipos.PromocionMonograficaDetalles;
//import com.comerzzia.core.servicios.ContextHolder;
//import com.comerzzia.core.util.xml.XMLDocumentException;
//import com.comerzzia.pos.persistence.promociones.PromocionBean;
//import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleBean;
//import com.comerzzia.pos.persistence.promociones.tipos.PromocionTipoBean;
//import com.comerzzia.pos.services.promociones.Promocion;
//import com.comerzzia.pos.services.promociones.PromocionesBuilder;
//import com.comerzzia.pos.services.promociones.PromocionesBuilderException;
//import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionPrecioDetalles;
//
//@Primary
//@Component
//public class CARDOSOPromocionesBuilder extends PromocionesBuilder{
//
//	//private static final Logger log = Logger.getLogger(CARDOSOPromocionesBuilder.class.getName());
//
//	/**
//	 * ########################################################################################
//	 * GAP - PERSONALIZACIONES V3 - PROMOCIONES MONOGRÁFICAS
//	 */
//	
//	public static final Long ID_TIPO_PROMOCION_MONOGRAFICA = 1002L;
//	
//	public Promocion create(PromocionBean promocionBean) throws PromocionesBuilderException{
//		log.debug("create() - Instanciando promoción: " + promocionBean);
//		try{
//			PromocionTipoBean tipoPromocion = promocionBean.getTipoPromocion();
//			tipoPromocion.parseConfiguracion();
//			String manejador = tipoPromocion.getManejador();
//			if(manejador == null){
//				throw new PromocionesBuilderException("No hay definido \"<Manejador>\" (className) dentro de la configuración del idTipoPromocion " + tipoPromocion.getIdTipoPromocion());
//			}
//			Promocion promocion = (Promocion) ContextHolder.getBean(manejador);
//			
//			if(promocion == null && promocionBean.getIdTipoPromocion().equals(ID_TIPO_PROMOCION_MONOGRAFICA)) {
//    	    	log.debug("create() - Promoción monográfica");
//    	    	promocion = new PromocionMonograficaDetalles(promocionBean);
//                List<PromocionDetalleBean> detalles = promocionesService.consultarDetallesPromocion(promocionBean.getIdPromocion());
//                ((PromocionPrecioDetalles) promocion).setDetalles(detalles);
//    	    }
//			
//			promocion.init(promocionBean);
//			return promocion;
//		}
//		catch(XMLDocumentException | ClassNotFoundException e){
//			throw new PromocionesBuilderException(e);
//		}
//	}
//	
//}
