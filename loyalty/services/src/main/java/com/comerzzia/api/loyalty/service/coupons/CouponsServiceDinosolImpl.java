package com.comerzzia.api.loyalty.service.coupons;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.BadRequestException;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.loyalty.persistence.coupons.Coupon;
import com.comerzzia.api.loyalty.persistence.coupons.CouponExample;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponCustomerUseDTO;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTO;
import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkExample;
import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkKey;
import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkMapper;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUse;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUseExample;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUseMapper;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;
import com.comerzzia.api.loyalty.service.coupons.dto.RedeemCoupon;
import com.comerzzia.core.servicios.eventos.EventosService;
import com.comerzzia.core.servicios.eventos.ServicioEventosImpl;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.servicios.variables.VariableException;
import com.comerzzia.core.servicios.variables.VariableNotFoundException;
import com.comerzzia.core.servicios.variables.VariablesService;
import com.comerzzia.dinosol.librerias.sherpa.client.cupones.ApiClient;
import com.comerzzia.dinosol.librerias.sherpa.client.cupones.CuponesApi;
import com.comerzzia.dinosol.librerias.sherpa.client.cupones.model.PushCuponRequest;
import com.comerzzia.dinosol.librerias.sherpa.client.cupones.model.PushCuponResponse;

import feign.FeignException;

@Primary
@Service
public class CouponsServiceDinosolImpl extends CouponsServiceImpl {
	private static final Logger log = Logger.getLogger(CouponsServiceDinosolImpl.class);

	private ApiClient apiClient;
	private CuponesApi api;

	@Autowired
	CouponLinkMapper couponLinkmapper;

	@Autowired
	CouponUseMapper couponUsemapper;

	@Autowired
	EventosService eventosService;

	@Autowired
	VariablesService variablesService;

	@Override
	public void validate(IDatosSesion datosSesion, CouponCustomerUseDTO couponCustomerUseDTO, Date validationDate,
			String loyalCustomerId) throws ApiException {
		if (couponCustomerUseDTO == null) {
			throw new NotFoundException();
		}

		if (validationDate == null) {
			validationDate = new Date();
		}

		// check active
		if (!couponCustomerUseDTO.getActive()) {
			throw new BadRequestException(LY_COUPON_NOT_ACTIVE, new String[] { couponCustomerUseDTO.getCouponCode() });
		}

		// truncate validation date
		validationDate = DateUtils.truncate(validationDate, Calendar.DAY_OF_MONTH);

		// vigence validation
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, datosSesion.getLocale());

		validationDate = DateUtils.truncate(validationDate, Calendar.DAY_OF_MONTH);

		if (couponCustomerUseDTO.getStartDate() != null) {
			if (DateUtils.truncate(couponCustomerUseDTO.getStartDate(), Calendar.DAY_OF_MONTH).after(validationDate)) {
				throw new BadRequestException(LY_COUPON_NOT_ACTIVE_UNTIL,
						new String[] { df.format(couponCustomerUseDTO.getStartDate()) });
			}
		}

		if (couponCustomerUseDTO.getEndDate() != null) {
			if (DateUtils.truncate(couponCustomerUseDTO.getEndDate(), Calendar.DAY_OF_MONTH).before(validationDate)) {
				throw new BadRequestException(LY_COUPON_EXPIRED_AT,
						new String[] { df.format(couponCustomerUseDTO.getEndDate()) });
			}
		}

		// global coupon use control
		if (couponCustomerUseDTO.getUses().getUsed()) {
			throw new BadRequestException(LY_COUPON_USED, new String[] { couponCustomerUseDTO.getCouponCode() });
		}

		// customer coupon
		if (!StringUtils.equals("0", couponCustomerUseDTO.getLoyalCustomerId())) {
			// customer use control
			if (couponCustomerUseDTO.getCustomerUses().getUsed()) {
				throw new BadRequestException(LY_COUPON_USED, new String[] { couponCustomerUseDTO.getCouponCode() });
			}

			// customer owner control
			// DINOSOL: Quieren que el cupon pueda ser usado por cualquier cliente
//         if(loyalCustomerId!=null && !couponCustomerUseDTO.getLoyalCustomerId().equals(loyalCustomerId)) {
//        	 throw new BadRequestException(LY_COUPON_CUSTOMER_ERROR, new String[]{loyalCustomerId.toString(), couponCustomerUseDTO.getCouponCode()});
//         }
		} else {
			// anonymous coupon

			// customer use control of anonymous coupon
			if (couponCustomerUseDTO.getCustomerUses().getObjectId() != null) {
				if (couponCustomerUseDTO.getCustomerUses().getUsed()) {
					throw new BadRequestException(LY_COUPON_USED,
							new String[] { couponCustomerUseDTO.getCouponCode() });
				}
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Coupon insert(IDatosSesion datosSesion, NewCoupon newCoupon) throws ApiException {
		if (newCoupon.getStartDate() != null) {
			newCoupon.setStartDate(DateUtils.truncate(newCoupon.getStartDate(), Calendar.DAY_OF_MONTH));
		}
		if (newCoupon.getEndDate() != null) {
			newCoupon.setEndDate(DateUtils.truncate(newCoupon.getEndDate(), Calendar.DAY_OF_MONTH));
		}

		Coupon coupon = super.insert(datosSesion, newCoupon);

		sendPushToSherpa(datosSesion, newCoupon, coupon);

		return coupon;
	}

	public void updateManualSellection(IDatosSesion datosSesion, CouponDTO coupon, Boolean manualSellection)
			throws ApiException {
		CouponExample example = new CouponExample();
		example.setUidActividad(datosSesion.getUidActividad());
		example.createCriteria().andUidActividadEqualTo(datosSesion.getUidActividad())
				.andCouponIdEqualTo(coupon.getCouponId());
		Coupon newCoupon = new Coupon();
		newCoupon.setManualSelection(manualSellection);

		if (mapper.updateByExampleSelective(newCoupon, example) == 0) {
			throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, LY_COUPON_CONCURRENCE_ERROR,
					new String[] { coupon.getCouponCode() });
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateLoyalCustomerId(IDatosSesion datosSesion, final CouponDTO coupon, String newLoyalCustomerId)
			throws ApiException {
		CouponLinkExample example = new CouponLinkExample(datosSesion);
		example.createCriteria().andUidActividadEqualTo(datosSesion.getUidActividad())
				.andClassIdEqualTo(ID_CLASS_LOYAL_CUSTOMER_ID).andObjectIdEqualTo(coupon.getLoyalCustomerId())
				.andCouponIdEqualTo(coupon.getCouponId());

		CouponLinkKey record = new CouponLinkKey();
		record.setObjectId(newLoyalCustomerId);

		if (couponLinkmapper.updateByExampleSelective(record, example) == 0) {
			throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, LY_COUPON_CONCURRENCE_ERROR,
					new String[] { coupon.getCouponCode() });
		}

		// actualizar tabla de usos del fidelizado
		CouponUse newCouponUse = new CouponUse();
		newCouponUse.setObjectId(newLoyalCustomerId);

		CouponUseExample couponUseExample = new CouponUseExample();
		couponUseExample.createCriteria().andUidActividadEqualTo(datosSesion.getUidActividad())
				.andClassIdEqualTo(ID_CLASS_LOYAL_CUSTOMER_ID).andObjectIdEqualTo(coupon.getLoyalCustomerId())
				.andCouponIdEqualTo(coupon.getCouponId());

		couponUsemapper.updateByExampleSelective(newCouponUse, couponUseExample);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void redeem(IDatosSesion datosSesion, RedeemCoupon redeemCoupon) throws ApiException {
		CouponDTO originalCoupon = selectByUk(datosSesion, redeemCoupon.getCouponRedeemData().getCouponCode());

		if (originalCoupon == null) {
			throw new NotFoundException(LY_COUPON_NOT_EXISTS,
					new String[] { redeemCoupon.getCouponRedeemData().getCouponCode() });
		}

		String usadoPor = null;

		// si el cupon lo está usando otro fidelizado, asignar al nuevo y dejar rastro
		// del fidelizado original
		if (!StringUtils.isEmpty(originalCoupon.getLoyalCustomerId())
				&& !StringUtils.equals(originalCoupon.getLoyalCustomerId(), "0")
				&& !StringUtils.equals(originalCoupon.getLoyalCustomerId(), redeemCoupon.getLoyalCustomerId())) {
			usadoPor = redeemCoupon.getLoyalCustomerId();

			// cambiar el fidelizado al original del cupon
			redeemCoupon.setLoyalCustomerId(originalCoupon.getLoyalCustomerId());
		}

		super.redeem(datosSesion, redeemCoupon);

		if (usadoPor != null) {
			// dejar rastro del fidelizado que realmente está usando el cupon cuando no
			// coincide con el original
			CouponLinkKey newLink = new CouponLinkKey(ID_CLASS_LOYAL_CUSTOMER_ID + "_USED_BY", usadoPor);
			couponsLinksService.insert(datosSesion, originalCoupon.getCouponId(), newLink);
		}
	}

	protected void sendPushToSherpa(final IDatosSesion datosSesion, final NewCoupon newCouponRequest,
			final Coupon newCoupon) {
		if (StringUtils.isEmpty(newCouponRequest.getLoyalCustomerId())
				|| StringUtils.equals(newCouponRequest.getLoyalCustomerId(), "0")) {
			return;
		}

		if (api == null) {
			apiClient = new ApiClient("sad_jwt_auth");

			try {
				apiClient.setBasePath(variablesService.consultarValor(datosSesion, "X_SHERPA_CUPONES_URL"));
				apiClient.setBearerToken(variablesService.consultarValor(datosSesion, "X_SHERPA_CUPONES_TOKEN"));

				api = apiClient.buildClient(CuponesApi.class);

				log.info("Conexión al API de Sherpa-homai configurada a " + apiClient.getBasePath());
			} catch (VariableException | VariableNotFoundException e) {
				log.error("Error configurando Api de Cupones de Sherpa", e);
			}
		}

		if (api != null) {
			Thread thread = new Thread(new Runnable() {
				private final Logger log = Logger.getLogger(this.getClass());

				@Override
				public void run() {
					PushCuponResponse response = null;

					try {
						log.debug("Enviando notificación a Sherpa para el cupon " + newCoupon.getCouponCode());

						PushCuponRequest request = new PushCuponRequest();
						request.setCouponId(newCoupon.getCouponCode());
						request.setCouponName(newCoupon.getCouponDescription());
						request.setCouponTypeCode("DEFAULT");
						request.setPromotionId(newCoupon.getPromotionId().toString());
						request.setPusheable(true);
						request.setSherpaCode(newCouponRequest.getLoyalCustomerId());

						long inicio = System.currentTimeMillis();

						response = api.pushCupon(request);

						log.debug("Notificación a Sherpa enviada para el cupon " + newCoupon.getCouponCode() + " en "
								+ (System.currentTimeMillis() - inicio) + "ms");
					} catch (FeignException e) {
						log.error(e);
						try {
							eventosService.registrarEvento(datosSesion, datosSesion.getUser(), null, "SHERPA_PUSH",
									"SHERPA", EventosService.SEVERIDAD_ERROR, e.getMessage(),
									newCoupon.getCouponCode());
						} catch (Exception ex) {
							log.error(ex);
						}
					}

					if (response != null && !("ok".equals(response.getCode()))) {
						log.warn("Error enviando notificación a Sherpa para el cupon " + newCoupon.getCouponCode() + ":"
								+ response.getCode() + "/" + response.getMessage());

						try {
							eventosService.registrarEvento(datosSesion, datosSesion.getUser(), null, "SHERPA_PUSH",
									"SHERPA", EventosService.SEVERIDAD_WARNING,
									response.getCode() + "/" + response.getMessage(), newCoupon.getCouponCode());
						} catch (Exception ex) {
							log.error(ex);
						}

					}
				}
			});

			thread.start();
		}

	}
}
