package com.comerzzia.iskaypet.pos.services.core.sesion;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.numeros.Numero;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.IskaypetCustomerCouponDTO;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.core.sesion.coupons.types.CouponTypeDTO;
import com.comerzzia.pos.services.cupones.CuponAplicationException;
import com.comerzzia.pos.services.cupones.CuponUseException;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.cupones.generation.GeneratedCouponDto;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.tipos.DatosCuponFuturoDTO;
import com.comerzzia.pos.services.promociones.tipos.PromocionLinea;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Primary
public class IskaypetSesionPromociones extends SesionPromociones {

	@Autowired
	protected Sesion sesion;
	
	public Map<Long, Promocion> getPromocionesActivas() {
		return super.promocionesActivas;
	}

	public Map<Long, PromocionLinea> getPromocionesLineas() {
		return super.promocionesLineas;
	}

	public Map<Long, PromocionLinea> getPromocionesLineasExclusivas() {
		return super.promocionesLineasExclusivas;
	}

	// GAP147 - USO DE CUPONES DE INTEGRACION NAV EN POS
	public Map<String, CouponTypeDTO> getCouponType() {
		return super.couponsType;
	}

	@Override
	public boolean aplicarCupon(CustomerCouponDTO coupon, DocumentoPromocionable ticket) throws CuponUseException, CuponesServiceException, CuponAplicationException {
		if (coupon instanceof IskaypetCustomerCouponDTO && StringUtils.isNotBlank(((IskaypetCustomerCouponDTO) coupon).getCompraMinima())) {
			BigDecimal compraMinima = new BigDecimal(((IskaypetCustomerCouponDTO) coupon).getCompraMinima());
			
			if(BigDecimalUtil.isMenor(ticket.getCabecera().getTotales().getTotalAPagar(),compraMinima)) {
	            log.debug("aplicarCupon() - La promoción a la que hace referencia el cupón no ha podido ser aplicada.");
	            throw new CuponAplicationException();
			}
		}

		return super.aplicarCupon(coupon, ticket);
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void generarCuponesDtoFuturo(ITicket ticket) {
		for(PromocionTicket promocion : (List<PromocionTicket>) ticket.getPromociones()) {
			if(promocion.getTipoDescuento().equals(Promocion.TIPO_DTO_A_FUTURO) && promocion.isCouponFutureDiscount()) {
				DatosCuponFuturoDTO datosCupon = promocion.getPromocion().getDatosCupon();
				GeneratedCouponDto cupon = null;
                try {
	                cupon = cuponesServices.getCuponDtoFuturo(ticket, promocion.getPromocion(), promocion.getImporteTotalAhorro());
                }
                catch (Exception e) {
                	log.error("generarCuponesDtoFuturo() - No se ha podido generar el código de cupón.");
                	break;
                }
				
                if(cupon != null) {
                	String storeLanguageCode = sesion.getAplicacion().getStoreLanguageCode();
					CuponEmitidoTicket cuponEmitido = new CuponEmitidoTicket();
					String title = datosCupon.getTitulo();
			        if(StringUtils.isNotBlank(storeLanguageCode)) {
			        	String titleTranslation = datosCupon.getTitleTranslations().get(storeLanguageCode);
			        	if(StringUtils.isNotBlank(titleTranslation)) {
			        		title = titleTranslation;
			        	}
			        }
			        cuponEmitido.setTituloCupon(title);
				        
			        String description = datosCupon.getDescripcion();
			        if(StringUtils.isNotBlank(storeLanguageCode)) {
			        	String descriptionTranslation = datosCupon.getDescriptionTranslations().get(storeLanguageCode);
			        	if(StringUtils.isNotBlank(descriptionTranslation)) {
			        		description = descriptionTranslation;
			        	}
			        }
					
					if(StringUtils.isNotBlank(description)) {
						description = description.replaceAll("#IMPORTE#", FormatUtil.getInstance().formateaImporte(promocion.getImporteTotalAhorro()));
						cuponEmitido.setDescripcionCupon(description);
					}
					
					cuponEmitido.setCodigoCupon(cupon.getCouponCode());
					cuponEmitido.setIdPromocionOrigen(promocion.getIdPromocion());
					cuponEmitido.setIdPromocionAplicacion(datosCupon.getIdPromocionAplicacion());
					cuponEmitido.setImporteCupon(cupon.getCouponAmount());
					
					
					// ISK-364 - Cupón con fecha diferida
					Boolean generarCupon = true;
					Promocion promocionActivaConExtensiones = getPromocionesActivas().get(promocion.getIdPromocion());
					String waitingDaysMapValue = promocionActivaConExtensiones.getExtension("waitingDays");
					if (StringUtils.isNotBlank(waitingDaysMapValue)) {
						Integer waitingDays = Numero.desformateaInteger(waitingDaysMapValue, 0);
						if (waitingDays > 0) {
							Date waitingDate = DateUtils.addDays(cupon.getStartDate(), waitingDays);
							if (waitingDate.compareTo(cupon.getEndDate()) <= 0) {
								cupon.setStartDate(waitingDate);
							}
							else {
								// Si la fecha de inicio del cupón que se va a generar es posterior a la fecha fin de la
								// promoción a aplicar, no generamos el cupón.
								generarCupon = false;
							}
						}
					}
					cuponEmitido.setFechaInicio(cupon.getStartDate());

					String validityDaysMapValue = promocionActivaConExtensiones.getExtension("validityDays");
					if (StringUtils.isNotBlank(validityDaysMapValue)) {
						Integer validityDays = Numero.desformateaInteger(validityDaysMapValue, 0);
						if (validityDays > 0) {
							Date validityDate = DateUtils.addDays(cupon.getStartDate(), validityDays - 1);
							if (validityDate.compareTo(cupon.getEndDate()) <= 0) {
								cupon.setEndDate(validityDate);
							}
						}
					}
					cuponEmitido.setFechaFin(cupon.getEndDate());
					
					cuponEmitido.setMaximoUsos(datosCupon.getCustomerMaxUses());
					cuponEmitido.setImagenCupon(datosCupon.getUrlImage());
					cuponEmitido.setTipoCupon(datosCupon.getCouponTypeCode());
					
					if(generarCupon) {
						((TicketVentaAbono) ticket).addCuponEmitido(cuponEmitido);
					}
                }
			}
		}

	}

}
