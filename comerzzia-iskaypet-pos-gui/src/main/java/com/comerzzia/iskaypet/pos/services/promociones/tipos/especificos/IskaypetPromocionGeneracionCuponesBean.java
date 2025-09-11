package com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.numeros.Numero;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.cupones.generation.GeneratedCouponDto;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionGeneracionCuponesBean;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;

@Component
@Primary
@Scope("prototype")
public class IskaypetPromocionGeneracionCuponesBean extends PromocionGeneracionCuponesBean {

	@Override
    public CuponEmitidoTicket generaCupon(DocumentoPromocionable documento) throws CuponesServiceException {
    	String storeLanguageCode = sesion.getAplicacion().getStoreLanguageCode();
    	GeneratedCouponDto cuponGenerado = generaCodigoCupon(documento);
    	if(cuponGenerado == null) {
    		return null;
    	}
    	CuponEmitidoTicket cupon = new CuponEmitidoTicket();
        
        String title = datosCupon.getTitulo();
        if(StringUtils.isNotBlank(storeLanguageCode)) {
        	String titleTranslation = datosCupon.getTitleTranslations().get(storeLanguageCode);
        	if(StringUtils.isNotBlank(titleTranslation)) {
        		title = titleTranslation;
        	}
        }
        cupon.setTituloCupon(title);
        
        String description = datosCupon.getDescripcion();
        if(StringUtils.isNotBlank(storeLanguageCode)) {
        	String descriptionTranslation = datosCupon.getDescriptionTranslations().get(storeLanguageCode);
        	if(StringUtils.isNotBlank(descriptionTranslation)) {
        		description = descriptionTranslation;
        	}
        }
        
        cupon.setDescripcionCupon(description);
        
        cupon.setIdPromocionOrigen(getIdPromocion());
        cupon.setIdPromocionAplicacion(datosCupon.getIdPromocionAplicacion());
        
        
		cupon.setCodigoCupon(cuponGenerado.getCouponCode());
		cupon.setImporteCupon(cuponGenerado.getCouponAmount());
		
		// ISK-364 - Cupón con fecha diferida
		String waitingDaysMapValue = extensiones.get("waitingDays");
		if (StringUtils.isNotBlank(waitingDaysMapValue)) {
			Integer waitingDays = Numero.desformateaInteger(waitingDaysMapValue, 0);
			if (waitingDays > 0) {
				Date waitingDate = DateUtils.addDays(cuponGenerado.getStartDate(), waitingDays);
				if (waitingDate.compareTo(cuponGenerado.getEndDate()) <= 0) {
					cuponGenerado.setStartDate(waitingDate);
				}
				else {
					// Si la fecha de inicio del cupón que se va a generar es posterior a la fecha fin de la promoción a
					// aplicar, no generamos el cupón.
					return null;
				}
			}
		}
		cupon.setFechaInicio(cuponGenerado.getStartDate());

		String validityDaysMapValue = extensiones.get("validityDays");
		if (StringUtils.isNotBlank(validityDaysMapValue)) {
			Integer validityDays = Numero.desformateaInteger(validityDaysMapValue, 0);
			if (validityDays > 0) {
				Date validityDate = DateUtils.addDays(cuponGenerado.getStartDate(), validityDays - 1);
				if (validityDate.compareTo(cuponGenerado.getEndDate()) <= 0) {
					cuponGenerado.setEndDate(validityDate);
				}
			}
		}
		cupon.setFechaFin(cuponGenerado.getEndDate());
		
		cupon.setMaximoUsos(datosCupon.getCustomerMaxUses());
		cupon.setImagenCupon(datosCupon.getUrlImage());
		cupon.setTipoCupon(datosCupon.getCouponTypeCode());
        return cupon;
    }

}
