package com.comerzzia.iskaypet.pos.services.cupones.generation;

import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.core.sesion.coupons.types.CouponTypeDTO;
import com.comerzzia.pos.services.cupones.generation.CouponsCodeGeneratorService;
import com.comerzzia.pos.services.cupones.generation.GeneratedCouponDto;
import com.comerzzia.pos.util.i18n.I18N;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * IER-176 - NO SE MUESTRA EN LA IMPRESIÓN DE CUPONES CÓDIGO DE BARRAS LARGOS
 */
@Primary
@Component
@SuppressWarnings("unused")
public class IskaypetCouponsCodeGeneratorService extends CouponsCodeGeneratorService{

	private static final Logger log = Logger.getLogger(IskaypetCouponsCodeGeneratorService.class);
	
	public static final int NUM_DIGITOS_PROMOCION = 7;
	public static final int NUM_DIGITOS_PROMOCION_OLD = 8;
	
	public static final int MAX_LENGTH = 2;
	public static final int MAX_LENGTH_RANDOM = 6;
	public static final int MAX_LENGTH_OLD = 5;
	
	@Autowired
	private SesionPromociones sesionPromociones;
	
	/**
	 * Método para generar cupón para fidelizados, 15 dígitos.
	 * @param uidTicket UID del ticket
	 * @param idFidelizado ID del fidelizado
	 * @param couponTypeCode Código del tipo de cupón
	 * @return String Código de cupón
	 * @throws Exception Excepción
	 */
	public String getCouponCode(String uidTicket, Long idFidelizado, String couponTypeCode) throws Exception{
		log.debug("getCouponCode() - Obteniendo código de cupón para el fidelizado '" + idFidelizado + "' con el tipo de cupón '" + couponTypeCode + "'.");
		try{

			CouponTypeDTO couponType = sesionPromociones.getCouponTypeDTO(couponTypeCode);
			String idFidelizadoString = StringUtils.leftPad(idFidelizado.toString(), NUM_DIGITOS_PROMOCION, "0");
			String couponCode = couponType.getPrefix() + idFidelizadoString + uidTicket.substring(uidTicket.length() - 5).toUpperCase();
			
			log.debug("getCouponCode() - Código de cupón generado '" + couponCode + "' para el fidelizado '" + idFidelizado + "'.");
			return couponCode;
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al generar el código de cupón para fidelizados : ") + e.getMessage();
			log.error("getCouponCode() - " + msgError, e);
			throw new Exception(msgError, e);
		}
	}
	
	@Override
	protected String getCouponCodeAleatory(GeneratedCouponDto newCoupon){
		CouponTypeDTO couponType = sesionPromociones.getCouponTypeDTO(newCoupon.getCouponTypeCode());

		String couponBody = getCouponBody(newCoupon);

		String randomUid = java.util.UUID.randomUUID().toString();
		String horaMinSeg = new SimpleDateFormat("HHmmss").format(new Date());
		String couponCode = couponType.getPrefix() + horaMinSeg + randomUid.substring(randomUid.length() - 6).toUpperCase();

		 log.debug("getCouponCodeAleatory() - couponBody=" + couponBody + " | couponCode=" + couponCode);
		
		return couponCode;
	}
	@Override
	protected String getCouponBody(GeneratedCouponDto newCoupon){
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		
		String promotionId = StringUtils.leftPad(newCoupon.getPromotionId().toString(), NUM_DIGITOS_PROMOCION, "0");
		String balance = StringUtils.leftPad(newCoupon.getCouponAmount() == null 
							? "0" 
							: newCoupon.getCouponAmount().multiply(new BigDecimal(100)).abs().toBigInteger().toString(), 5, "0");
		String startDate = (newCoupon.getStartDate() != null) 
							? df.format(newCoupon.getStartDate()) 
							: "000000";
		String endDate = (newCoupon.getEndDate() != null) 
							? df.format(newCoupon.getEndDate()) 
							: "000000";
		
		log.debug("getCouponBody() Se gererará un cupón con el siguienre cuerpo - promoId=" + promotionId + " startDate=" + startDate + " endDate=" + endDate);
		
		/* Por errores de impresión del cupón por culpa del tamaño del código del cupón, se quita la fecha de inicio y fin. */
		return promotionId; //balance + startDate + endDate;
	}
	
}