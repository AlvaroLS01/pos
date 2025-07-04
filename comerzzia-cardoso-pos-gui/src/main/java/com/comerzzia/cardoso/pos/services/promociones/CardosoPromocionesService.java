package com.comerzzia.cardoso.pos.services.promociones;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.persistence.promociones.CardosoPromocionesMapper;
import com.comerzzia.cardoso.pos.persistence.promociones.PromocionCandidataBean;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.promociones.PromocionesService;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;

/*
 * FIX - CAR8 - Error en POS a la hora de leer los XML de las promociones.
 */
@Service
@Primary
public class CardosoPromocionesService extends PromocionesService{

	private static Logger log = Logger.getLogger(CardosoPromocionesService.class);
	
	public static Long TIPO_DESCUENTO_AFILIADO = 0L;
    public static Long TIPO_PROMOCION_PRECIO = 1L;
    public static Long TIPO_PROMOCION_DESCUENTO = 3L;
    public static Long TIPO_PROMOCION_NXM = 2L;
    public static Long TIPO_CUPON_DESCUENTO = 4L;
    public static Long TIPO_DESCUENTO_LINEA = 5L;
    public static Long TIPO_GENERACION_CUPONES = 6L;
    public static Long TIPO_DESCUENTO_CABECERA = 7L;
    public static Long TIPO_PUNTOS = 8L;
    public static Long TIPO_DESCUENTO_DETALLES_NxM = 9L;
    
    public static final Long PROMOCION_DTO_EMPLEADO = 1001L;
	public static final Long PROMOCION_MONOGRAFICAS = 1002L;

	@Autowired
	private Sesion sesion;
	
	/**
	 * ########################################################################################
	 * GAP - PROMOCION CANDIDATA
	 * 
	 * Consultamos las promociones candidatas.
	 * Recorremos la lista de promociones aplicables para ver si existe una promoción NxM y otra de descuento.
	 * Si no hay una promoción de NxM y otra de Descuento, se coge la primera
	 */
	
	@Autowired
	private CardosoPromocionesMapper cardosoPromocionesMapper;

	public PromocionCandidataBean[] consultarPromocionAplicableLinea(LineaTicket linea, boolean esFidelizado) throws PromocionesServiceException{
		log.debug("consultarPromocionDescuentoEmpleado() :  GAP - PROMOCION CANDIDATA");
		
		try{
			log.debug("consultarPromocionAplicableLinea() - Consultando promoción aplicable para la línea.");
			PromocionCandidataBean[] result = new PromocionCandidataBean[2];
			PromocionCandidataBean promocionResult = null;
			PromocionCandidataBean promocionDto = null;
			List<PromocionCandidataBean> promocionesAplicables = cardosoPromocionesMapper.selectPromocionAplicable(sesion.getAplicacion().getUidActividad(), linea.getCodArticulo(),
			        linea.getTarifa().getCodTarifa(), esFidelizado ? "S" : "N", new Date());

			if(promocionesAplicables != null && !promocionesAplicables.isEmpty()){
				for(PromocionCandidataBean promo : promocionesAplicables){
					if(promocionResult == null && promo.getIdTipoPromocion().equals(TIPO_PROMOCION_NXM)){
						promocionResult = promo;
					}
					else if(promocionDto == null && promo.getIdTipoPromocion().equals(TIPO_PROMOCION_DESCUENTO)){
						promocionDto = promo;
					}

					if(promocionResult != null && promocionDto != null)
						break;
				}
				if(!(promocionResult != null && promocionDto != null)){
					promocionResult = promocionesAplicables.get(0);
					promocionDto = null;
				}
				result[0] = promocionDto;
				result[1] = promocionResult;
			}
			return result;
		}
		catch(Exception e){
			log.error("consultarPromocionAplicableLinea() - Ha habido un error al consultar la promoción aplicable de la línea: " + e.getMessage(), e);
			throw new PromocionesServiceException(e);
		}
	}
	
	@Override
	public List<PromocionBean> consultarPromocionesActivas() throws PromocionesServiceException {
		return filtraPromocionesPersonalizadas(super.consultarPromocionesActivas());
	}

	/*
	 * FIX - CAR8
	 */
	public List<PromocionBean> filtraPromocionesPersonalizadas(List<PromocionBean> promocionesActivas) {
		for (Iterator<PromocionBean> iterator = promocionesActivas.listIterator(); iterator.hasNext();) {
			PromocionBean promocion = iterator.next();
		    if (promocion.getIdTipoPromocion().equals(PROMOCION_DTO_EMPLEADO) || promocion.getIdTipoPromocion().equals(PROMOCION_MONOGRAFICAS)) {
		    	iterator.remove();
		    	log.debug("filtraPromocionesPersonalizadas() - CAR8 - Promocion[" +  promocion.getIdTipoPromocion() + ":" + promocion.getDescripcion() + "] eliminada de Promociones Activas.");
		    }
		}
		
		return promocionesActivas;
	}
	
}
