package com.comerzzia.api.loyalty.service.coupons;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.BadRequestException;
import com.comerzzia.api.core.service.exception.GeneralErrorKeysConstants;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.loyalty.persistence.coupons.Coupon;
import com.comerzzia.api.loyalty.persistence.coupons.CouponMapper;
import com.comerzzia.api.loyalty.persistence.coupons.CouponUk;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponCustomerUseDTO;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTO;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTOExample;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTOMapper;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponsKpiDTO;
import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkKey;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUse;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUseKey;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponType;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponRedeemData;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponRedeemResult;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponRestriction;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponsFilter;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCouponForCustomers;
import com.comerzzia.api.loyalty.service.coupons.dto.RedeemCoupon;
import com.comerzzia.api.loyalty.service.coupons.dto.RedeemCoupons;
import com.comerzzia.api.loyalty.service.coupons.links.CouponsLinksService;
import com.comerzzia.api.loyalty.service.coupons.uses.CouponsUsesService;
import com.comerzzia.api.loyalty.service.couponsTypes.CouponsTypesService;
import com.comerzzia.core.model.i18n.I18NBean;
import com.comerzzia.core.servicios.contadores.ContadorException;
import com.comerzzia.core.servicios.i18n.I18NService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.base.Estado;

@Service
public class CouponsServiceImpl implements CouponsService {
   @Autowired 
   MessageSourceAccessor messageSourceAccessor; 
   
   @Autowired
   CouponMapper mapper;
   
   @Autowired
   CouponDTOMapper dtoMapper;
   
   @Autowired
   CouponsLinksService couponsLinksService;
   
   @Autowired
   CouponsUsesService couponsUsesService;
   
   @Autowired
   CouponsCounter couponsCounter;
   
   @Autowired
   CouponsTypesService couponsTypesService;
   
   @Autowired
   CouponsCodeGeneratorService couponsCodeGeneratorService;
   
   @Autowired
   I18NService i18nService;
   
   @Override
   public List<CouponDTO> selectByFilter(IDatosSesion datosSesion, CouponsFilter filter) {
      CouponDTOExample example = new CouponDTOExample(datosSesion);
      filter.setLanguageCode(datosSesion.getLocale().getLanguage().toUpperCase());
      example.setFilter(filter);
      
      return dtoMapper.selectByExample(example);   
   }
   
   @Override
   public CouponDTO selectByUk(IDatosSesion datosSesion, String couponCode) {
      CouponUk key = new CouponUk();
      key.setUidActividad(datosSesion.getUidActividad());
      key.setCouponCode(couponCode);
      
      return dtoMapper.selectByUk(key, datosSesion.getLocale().getLanguage().toUpperCase());
   }
   
   @Override
   public CouponCustomerUseDTO selectByUkAndCustomer(IDatosSesion datosSesion, String couponCode, String loyalCustomerId) {
      CouponUk key = new CouponUk();
      key.setUidActividad(datosSesion.getUidActividad());
      key.setCouponCode(couponCode);
      
      return dtoMapper.selectByUkAndCustomer(key, loyalCustomerId, datosSesion.getLocale().getLanguage().toUpperCase());      
   }
   
   @Override
   public List<CouponDTO> selectByCustomer(IDatosSesion datosSesion, CouponsFilter filter) {
      if (filter.getLoyalCustomerId() == null) {
         throw new RuntimeException("Loyal customer id must not be null");   
      }
      filter.setLanguageCode(datosSesion.getLocale().getLanguage().toUpperCase());
      CouponDTOExample example = new CouponDTOExample(datosSesion);
      example.setFilter(filter);
      
      return dtoMapper.selectByCustomer(example);   
   }
   
   @Override
   public void validate(IDatosSesion datosSesion, CouponCustomerUseDTO couponCustomerUseDTO, Date validationDate, String loyalCustomerId) throws ApiException {
      if (couponCustomerUseDTO == null) {
         throw new NotFoundException();
      }

      if (validationDate == null) {
         validationDate = new Date();
      }
      
      // check active
      if (!couponCustomerUseDTO.getActive()) {
    	  throw new BadRequestException(LY_COUPON_NOT_ACTIVE, new String[] {couponCustomerUseDTO.getCouponCode()});
      }
	  
      // vigence validation
      DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, datosSesion.getLocale());
      
      validationDate = DateUtils.truncate(validationDate, Calendar.DAY_OF_MONTH);
      
      if (couponCustomerUseDTO.getStartDate() != null) {
         if (DateUtils.truncate(couponCustomerUseDTO.getStartDate(), Calendar.DAY_OF_MONTH).after(validationDate)) {
            throw new BadRequestException(LY_COUPON_NOT_ACTIVE_UNTIL, new String[]{df.format(couponCustomerUseDTO.getStartDate())});
         }
      }  
      
      if (couponCustomerUseDTO.getEndDate() != null) {
			if (DateUtils.truncate(couponCustomerUseDTO.getEndDate(), Calendar.DAY_OF_MONTH).compareTo(validationDate) <= 0) {
				throw new BadRequestException(LY_COUPON_EXPIRED_AT, new String[] { df.format(couponCustomerUseDTO.getEndDate()) });
			}
      }
            
      // global coupon use control
      if (couponCustomerUseDTO.getUses().getUsed()) {
         throw new BadRequestException(LY_COUPON_USED, new String[]{couponCustomerUseDTO.getCouponCode()});
      }
               
      // customer coupon
      if (!StringUtils.equals("0", couponCustomerUseDTO.getLoyalCustomerId())) {
         // customer use control
         if (couponCustomerUseDTO.getCustomerUses().getUsed()) {
            throw new BadRequestException(LY_COUPON_USED, new String[]{couponCustomerUseDTO.getCouponCode()});
         }
         
         // customer owner control
         if(loyalCustomerId!=null && !couponCustomerUseDTO.getLoyalCustomerId().equals(loyalCustomerId)) {
        	 throw new BadRequestException(LY_COUPON_CUSTOMER_ERROR, new String[]{loyalCustomerId.toString(), couponCustomerUseDTO.getCouponCode()});
         }
      } else {
         // anonymous coupon
         
         // customer use control of anonymous coupon
         if (couponCustomerUseDTO.getCustomerUses().getObjectId() != null) {
            if (couponCustomerUseDTO.getCustomerUses().getUsed()) {
               throw new BadRequestException(LY_COUPON_USED, new String[]{couponCustomerUseDTO.getCouponCode()});
            }            
         }
      }      
   }
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public Coupon insert(IDatosSesion datosSesion, NewCoupon newCoupon) throws ApiException {
      CouponType couponType = couponsTypesService.selectByPrimaryKeyFromCache(datosSesion, newCoupon.getCouponTypeCode());
      
      // default values 
      if (newCoupon.getLoyalCustomerId() == null) {
         newCoupon.setLoyalCustomerId("0");
      }
      
      if (newCoupon.getCustomerMaxUses() == null) {
         newCoupon.setCustomerMaxUses(1L);
      }
      
      if (newCoupon.getPriority() == null) {
         newCoupon.setPriority(0L);
      }
      
      if (newCoupon.getManualSelection() == null) {
         newCoupon.setManualSelection(couponType.getDefManualSelect());
      }
      
      // new coupon record
      Coupon newRecord = new Coupon();
      newRecord.setUidActividad(datosSesion.getUidActividad());
      newRecord.setCouponId(generateCouponId(datosSesion));
      newRecord.setCouponCode(couponsCodeGeneratorService.generateCouponCode(datosSesion, newCoupon));
      newRecord.setCouponName(newCoupon.getCouponName());
      newRecord.setCouponDescription(newCoupon.getCouponDescription());
      newRecord.setCouponTypeCode(newCoupon.getCouponTypeCode());
      newRecord.setStartDate(newCoupon.getStartDate());
      newRecord.setEndDate(newCoupon.getEndDate());
      newRecord.setPromotionId(newCoupon.getPromotionId());
      newRecord.setManualSelection(newCoupon.getManualSelection());
      newRecord.setPriority(newCoupon.getPriority());
      newRecord.setBalance(newCoupon.getBalance());  
      newRecord.setImageUrl(newCoupon.getImageUrl());
      newRecord.setActive(true);
      newRecord.setCreationtDate(new Date());
      
      // links and restrictions/uses lists
      List<CouponUse> uses = new ArrayList<CouponUse>();
      List<CouponLinkKey> links = new ArrayList<CouponLinkKey>();
      
      // register creation entity
      if (newCoupon.getCreatedByClassId() != null && newCoupon.getCreatedByObjectId() != null) {
         links.add(new CouponLinkKey(ID_CLASS_CREATED_CLASS_ID, newCoupon.getCreatedByClassId()));
         links.add(new CouponLinkKey(ID_CLASS_CREATED_OBJECT_ID, newCoupon.getCreatedByObjectId()));
      }
      
      // register restriction for coupon uses
      uses.add(getCouponRestriction(newCoupon, newRecord));
            
      // insert coupon
      try {
         mapper.insert(newRecord);
      } catch (Exception e) {
         if(e.getCause() instanceof SQLIntegrityConstraintViolationException) { 
            throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, GeneralErrorKeysConstants.ERROR_RECORD_DUPLICATE_KEY, e.getMessage());
         }
         else{
            throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, GeneralErrorKeysConstants.ERROR_RECORD_INSERT, e.getMessage());
         }        
      }      
      
      // register link for customer
      links.add((getCustomerLink(newCoupon, newRecord)));      
            
      // register restriction for customer uses if not is anonymous
      if (!StringUtils.equals("0", newCoupon.getLoyalCustomerId())) {
         uses.add(getCustomerRestriction(newCoupon, newRecord));
      }
      
      // get new coupon restrictions
      if (newCoupon.getRestrictions() != null) {
         for (CouponRestriction restriction : newCoupon.getRestrictions()) {
            CouponUse couponUse = new CouponUse();
            couponUse.setClassId(restriction.getClassId());
            couponUse.setObjectId(restriction.getObjectId());
            couponUse.setMaxUses(restriction.getMaxUses());
            uses.add(couponUse);            
         }
      }
            
      // insert origin links
      if (newCoupon.getLinks() != null) {
         links.addAll(newCoupon.getLinks());
      }
      couponsLinksService.insert(datosSesion, newRecord.getCouponId(), links);
      
      // insert uses/restrictions
      couponsUsesService.insert(datosSesion, newRecord.getCouponId(), uses);
      
      //insert translations
      List<I18NBean> translations = new ArrayList<I18NBean>(); 
      for(String languageCode: newCoupon.getCouponNameTranslations().keySet()) {
    	  I18NBean translation = new I18NBean();
    	  translation.setEstadoBean(Estado.NUEVO);
    	  translation.setCodlengua(languageCode);
    	  translation.setIdClase(ID_CLASS_COUPON_NAME);
    	  translation.setIdObjeto(newRecord.getCouponId().toString());
    	  translation.setValor(newCoupon.getCouponNameTranslations().get(languageCode));
    	  translations.add(translation);
      }
      for(String languageCode: newCoupon.getCouponDescriptionTranslations().keySet()) {
    	  I18NBean translation = new I18NBean();
    	  translation.setEstadoBean(Estado.NUEVO);
    	  translation.setCodlengua(languageCode);
    	  translation.setIdClase(ID_CLASS_COUPON_DESCRIPTION);
    	  translation.setIdObjeto(newRecord.getCouponId().toString());
    	  translation.setValor(newCoupon.getCouponDescriptionTranslations().get(languageCode));
    	  translations.add(translation);
      }
      i18nService.insertUpdateDelete(translations, datosSesion);
      
      return newRecord;
   }
   
   protected Long generateCouponId(IDatosSesion datosSesion) {
      try {
         return couponsCounter.getCounterValue(datosSesion);
      } catch (ContadorException e) {
         throw new RuntimeException(e);
      }
   }
   

   
   /*
    *  logic for the coupon restriction record
    */
   protected CouponUse getCouponRestriction(NewCoupon newCoupon, Coupon newRecord) {
      CouponUse couponUse = new CouponUse();
      couponUse.setClassId(ID_CLASS_COUPON_ID);
      couponUse.setObjectId(newRecord.getCouponId().toString());
      if (newCoupon.getCustomerMaxUses() != null) {
         couponUse.setMaxUses(newCoupon.getCustomerMaxUses());
      } else {
         couponUse.setMaxUses(0L);
      }
      couponUse.setUses(0);
      couponUse.setTotalDiscount(BigDecimal.ZERO);
      
      return couponUse;
   }   
   
   /*
    *  logic for the customer restriction record
    */
   protected CouponUse getCustomerRestriction(NewCoupon newCoupon, Coupon newRecord) {
      CouponUse couponUse = new CouponUse();
      
      couponUse.setObjectId(newCoupon.getLoyalCustomerId().toString());
      
      couponUse.setClassId(ID_CLASS_LOYAL_CUSTOMER_ID);
      if (newCoupon.getCustomerMaxUses() != null) {
         couponUse.setMaxUses(newCoupon.getCustomerMaxUses());
      } else {
         couponUse.setMaxUses(0L);
      }
      couponUse.setUses(0);
      couponUse.setTotalDiscount(BigDecimal.ZERO);
      
      return couponUse;
   }     
   
   /*
    *  logic for the customer link record
    */
   protected CouponLinkKey getCustomerLink(NewCoupon newCoupon, Coupon newRecord) {
      CouponLinkKey couponLink= new CouponLinkKey();
      couponLink.setCouponId(newRecord.getCouponId());
      couponLink.setObjectId(newCoupon.getLoyalCustomerId().toString());
      couponLink.setClassId(ID_CLASS_LOYAL_CUSTOMER_ID);
                  
      return couponLink;
   }   
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public List<String> insertMultiple(IDatosSesion datosSesion, List<NewCoupon> newRecords) throws ApiException {
      List<String> couponsCodeList = new ArrayList<String>();
      
      for (NewCoupon newCoupon : newRecords) {
         couponsCodeList.add(insert(datosSesion, newCoupon).getCouponCode());
      }
      
      return couponsCodeList;
   }
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public List<String> insertMultiple(IDatosSesion datosSesion, NewCouponForCustomers newCouponForCustomers) throws ApiException {
      List<String> couponsCodeList = new ArrayList<String>();
      
      for (String customerId : newCouponForCustomers.getLoyalCustomerList()) {
         NewCoupon newCoupon = new NewCoupon();
         
         // copy coupon template properties 
         try {
            BeanUtils.copyProperties(newCoupon, newCouponForCustomers.getCouponTemplate());
         } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
         }
         
         // assign customer
         newCoupon.setLoyalCustomerId(customerId);

         // insert coupon
         couponsCodeList.add(insert(datosSesion, newCoupon).getCouponCode());
      }
      
      return couponsCodeList;
   }
   
   @Override
   @Transactional(rollbackFor=Exception.class)   
   public List<CouponRedeemResult> redeem(IDatosSesion datosSesion, RedeemCoupons redeemCoupons)  {      
      List<CouponRedeemResult> results = new ArrayList<CouponRedeemResult>();
      
      if (redeemCoupons.getDate() == null) {
         redeemCoupons.setDate(new Date());
      }
      
      for (CouponRedeemData couponRedeemData : redeemCoupons.getCoupons()) {
         try {              
            RedeemCoupon redeemCoupon = new RedeemCoupon();
            BeanUtils.copyProperties(redeemCoupons, redeemCoupon);
            redeemCoupon.setCouponRedeemData(couponRedeemData);
            
            redeem(datosSesion, redeemCoupon);
         } catch (ApiException e) {
            CouponRedeemResult couponRedeemResult = new CouponRedeemResult();
            couponRedeemResult.setCouponCode(couponRedeemData.getCouponCode());
            couponRedeemResult.setRedeemError(e.getMessage());
            results.add(couponRedeemResult);            
         } catch(IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
         }
      }
      
      return results;
   }
   
   @Override
   @Transactional(rollbackFor=Exception.class)   
   public void redeem(IDatosSesion datosSesion, RedeemCoupon redeemCoupon) throws ApiException {
      if (redeemCoupon.getDate() == null) {
         redeemCoupon.setDate(new Date());
      }
      
      CouponRedeemData couponRedeemData = redeemCoupon.getCouponRedeemData();
      
      CouponCustomerUseDTO coupon = selectByUkAndCustomer(datosSesion, couponRedeemData.getCouponCode(), redeemCoupon.getLoyalCustomerId());
      
      if (coupon == null) {
         throw new NotFoundException(LY_COUPON_NOT_EXISTS, new String[]{couponRedeemData.getCouponCode()});
      }
      
      // global validate
      validate(datosSesion, coupon, redeemCoupon.getDate(), redeemCoupon.getLoyalCustomerId());
                     
      // balance check
      if (coupon.getBalance() != null && coupon.getBalance().compareTo(BigDecimal.ZERO) > 0) {
         if (coupon.getBalance().compareTo(couponRedeemData.getDiscount()) < 0) {
            throw new BadRequestException(LY_COUPON_BALANCE_DISCOUNT_ERROR, new String[] {couponRedeemData.getCouponCode()});
         }
      }
                     
      // customer coupon
      if (StringUtils.isNotEmpty(coupon.getLoyalCustomerId())) {
         // lock control
         if (coupon.getCustomerUses().getLockByTerminalId() != null) {
            //TODO: Controlar timeout del bloqueo según tiempo pasado
            if (coupon.getCustomerUses().getLockByTerminalId().equals(redeemCoupon.getLockByTerminalId())) {
               throw new BadRequestException(LY_COUPON_LOCKED_BY, new String[] {couponRedeemData.getCouponCode(), coupon.getCustomerUses().getLockByTerminalId()});
            }
         }
         
      } else {
         // anonymous coupon
         
//         if (redeemCoupon.getLoyalCustomerId() != 0L) {
//            // add new customer link to coupon
//            if (coupon.getCustomerUses().getObjectId() == null) {
//               couponsLinksService.insert(datosSesion, coupon.getCouponId(), new CouponLinkKey(ID_CLASS_LOYAL_CUSTOMER_ID, redeemCoupon.getLoyalCustomerId().toString()));
//            }
//         }
      }
      
      // update coupon use
      CouponUse couponUse = new CouponUse();
      if (coupon.getUses().getTotalDiscount() == null) {
         coupon.getUses().setTotalDiscount(BigDecimal.ZERO);
      }
      couponUse.setTotalDiscount(coupon.getUses().getTotalDiscount().add(couponRedeemData.getDiscount()));
      couponUse.setUses(coupon.getUses().getUses()+1);
      if (coupon.getUses().getMaxUses() > 0 && couponUse.getUses() >= coupon.getUses().getMaxUses()) {
         couponUse.setUsed(true);
      }
      if (coupon.getUses().getFirstUse() == null || (coupon.getUses().getFirstUse() != null && coupon.getUses().getFirstUse().after(redeemCoupon.getDate()))) {
         couponUse.setFirstUse(redeemCoupon.getDate());
      }
      if (coupon.getUses().getLastUse() == null || (coupon.getUses().getLastUse() != null && coupon.getUses().getLastUse().before(redeemCoupon.getDate()))) {
         couponUse.setLastUse(redeemCoupon.getDate());
      }

      if (coupon.getUses().getTotalSale() == null) {
         coupon.getUses().setTotalSale(BigDecimal.ZERO);
      }
      couponUse.setTotalSale(coupon.getUses().getTotalSale().add(couponRedeemData.getSaleAmount() == null ? BigDecimal.ZERO : couponRedeemData.getSaleAmount()));
      
      couponUse.setLastTerminalId(redeemCoupon.getLockByTerminalId());
      
      if (couponsUsesService.updateUses(datosSesion, coupon.getUses(), couponUse) == 0) {
         throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, 
                                LY_COUPON_CONCURRENCE_ERROR, 
                                new String[] {couponRedeemData.getCouponCode()});
      }
      
      // update/insert customer use
      CouponUse customerUse = new CouponUse();
      
      customerUse.setLastTerminalId(redeemCoupon.getLockByTerminalId());
               
      // customer use record already exists
      if (coupon.getCustomerUses().getObjectId() != null) {
         if (coupon.getCustomerUses().getTotalDiscount() == null) {
            coupon.getCustomerUses().setTotalDiscount(BigDecimal.ZERO);
         }
         customerUse.setTotalDiscount(coupon.getCustomerUses().getTotalDiscount().add(couponRedeemData.getDiscount()));
         
         if (coupon.getCustomerUses().getTotalSale() == null) {
            coupon.getCustomerUses().setTotalSale(BigDecimal.ZERO);
         }
         customerUse.setTotalSale(coupon.getCustomerUses().getTotalSale().add(couponRedeemData.getSaleAmount()));
         
         customerUse.setUses(coupon.getCustomerUses().getUses()+1);
         if (coupon.getCustomerUses().getMaxUses() != null &&
             coupon.getCustomerUses().getMaxUses() > 0 && 
             customerUse.getUses() >= coupon.getCustomerUses().getMaxUses()) {
            customerUse.setUsed(true);
         }
         if (coupon.getCustomerUses().getFirstUse() == null || (coupon.getCustomerUses().getFirstUse() != null && coupon.getCustomerUses().getFirstUse().after(redeemCoupon.getDate()))) {
            customerUse.setFirstUse(redeemCoupon.getDate());
         }
         if (coupon.getCustomerUses().getLastUse() == null || (coupon.getCustomerUses().getLastUse() != null && coupon.getCustomerUses().getLastUse().before(redeemCoupon.getDate()))) {
            customerUse.setLastUse(redeemCoupon.getDate());
         }                       
         
         if (couponsUsesService.updateUses(datosSesion, coupon.getCustomerUses(), customerUse) == 0) {
            throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE,
                                    LY_COUPON_CONCURRENCE_CUSTOMER_ERROR,
                                    new String[] {couponRedeemData.getCouponCode(), redeemCoupon.getLoyalCustomerId().toString()});
         }   
      } else {
         // new customer use for anonymous coupon
         customerUse.setClassId(ID_CLASS_LOYAL_CUSTOMER_ID);
         customerUse.setObjectId(redeemCoupon.getLoyalCustomerId().toString());
         customerUse.setMaxUses(1L);
         customerUse.setUses(1);
         customerUse.setUsed(true);
         customerUse.setTotalDiscount(couponRedeemData.getDiscount());
         customerUse.setTotalSale(couponRedeemData.getSaleAmount());
         customerUse.setFirstUse(redeemCoupon.getDate());
         customerUse.setLastUse(redeemCoupon.getDate());
         
         couponsUsesService.insert(datosSesion, coupon.getCouponId(), customerUse);
      }
      
      // update/insert other coupon uses accumulative
      
      // by store
      couponsUsesService.addUse(datosSesion,
                                new CouponUseKey(datosSesion.getUidActividad(), ID_CLASS_STORE_ID, redeemCoupon.getStoreId(), coupon.getCouponId()), 
                                couponRedeemData.getDiscount(), 
                                couponRedeemData.getSaleAmount(),
                                redeemCoupon.getDate(), 
                                redeemCoupon.getLockByTerminalId());

      // by sales document id
      couponsUsesService.addUse(datosSesion,
                                new CouponUseKey(datosSesion.getUidActividad(), ID_CLASS_SALES_DOCUMENT_UID, 
                                                 redeemCoupon.getTicketUid(), 
                                                 coupon.getCouponId()), 
                                couponRedeemData.getDiscount(), 
                                couponRedeemData.getSaleAmount(),
                                redeemCoupon.getDate(), 
                                redeemCoupon.getLockByTerminalId());
     
   }
   
   @Override
   public CouponsKpiDTO selectKPIS(IDatosSesion datosSesion, 
                                   String loyalCustomerId,
                                   Date startDate,
                                   Date endDate) {
      return dtoMapper.selectKPIS(datosSesion.getUidActividad(), loyalCustomerId, new Date(), startDate, endDate);
   }

   @Override
   public CouponsKpiDTO selectKPIS(IDatosSesion datosSesion, 
         Date startDate,
         Date endDate) {
      return dtoMapper.selectKPIS(datosSesion.getUidActividad(), null, new Date(), startDate, endDate);
   }   
   
   @Override
   public CouponDTO deactivate(IDatosSesion datosSesion, String couponCode) throws ApiException {
      CouponDTO coupon = selectByUk(datosSesion, couponCode);
      
      if (coupon == null) {
         throw new NotFoundException(LY_COUPON_NOT_EXISTS, new String[]{couponCode});
      }
      
      if (!coupon.getActive()) {
         throw new BadRequestException(LY_COUPON_NOT_ACTIVE, new String[] {couponCode});
      }
      
      if (coupon.getUses().getUses() > 0) {
         throw new BadRequestException(LY_COUPON_IS_USED, new String[] {couponCode});
      }
      
      coupon.setActive(false);
      
      if (mapper.updateActive(coupon) == 0) {
         throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, 
               LY_COUPON_CONCURRENCE_ERROR, 
               new String[] {couponCode});
      }
      
      return coupon;
   }
   
   @Override
   public CouponDTO activate(IDatosSesion datosSesion, String couponCode) throws ApiException {
      CouponDTO coupon = selectByUk(datosSesion, couponCode);
      
      if (coupon == null) {
         throw new NotFoundException(LY_COUPON_NOT_EXISTS, new String[]{couponCode});
      }
      
      if (coupon.getActive()) {
         throw new BadRequestException(LY_COUPON_ACTIVE, new String[] {couponCode});
      }
      
      if (coupon.getUses().getUses() > 0) {
         throw new BadRequestException(LY_COUPON_IS_USED, new String[] {couponCode});
      }
      
      coupon.setActive(true);
      
      if (mapper.updateActive(coupon) == 0) {
         throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, 
               LY_COUPON_CONCURRENCE_ERROR, 
               new String[] {couponCode});
      }
      
      return coupon;
   }
   
   @Override
   public boolean exitsCouponCode(IDatosSesion datosSesion, String couponCode) {
	   CouponUk key = new CouponUk();
	   key.setUidActividad(datosSesion.getUidActividad());
	   key.setCouponCode(couponCode);
	   Coupon coupon = mapper.selectByUk(key);
	   return coupon != null;
   }
}
