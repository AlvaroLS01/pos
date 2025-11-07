package com.comerzzia.api.loyalty.service.triggers.actions.types.coupons;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.Trigger;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataKey;
import com.comerzzia.api.loyalty.service.coupons.CouponsService;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;
import com.comerzzia.api.loyalty.service.triggers.TriggersService;
import com.comerzzia.api.loyalty.service.triggers.actions.types.ActionTypeAbstractService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;


@Component("ActionCouponsService")
@Scope("prototype")
public class ActionCouponsServiceImpl extends ActionTypeAbstractService {
   private final Logger log = LoggerFactory.getLogger(ActionCouponsServiceImpl.class);
   
   protected ActionCouponsData data;
   
   @Autowired
   TriggersService triggerService;
   
   @Autowired
   CouponsService couponsService;
      
   @Override
   public void setTriggerAction(TriggerAction triggerAction) {
      super.setTriggerAction(triggerAction);;
      data = createDataObject(ActionCouponsData.class);
   }   
   
   public Object getDataObject() {
      return data;
   }
      
   @Transactional(rollbackFor = Exception.class)
   public void executeAction(IDatosSesion datosSesion) throws ApiException {
      log.info("Executing action: " + triggerAction.getActionUid() + " Trigger: " + triggerAction.getTriggerUid());
      
      if (data == null) {
         throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, 
               LY_TRIGGER_ACTIION_TYPE_NULL_DATA, 
               new String[] {});         
      }
      
      Trigger trigger = triggerService.selectByPrimaryKey(datosSesion, triggerAction.getTriggerUid());
                  
      // query customers from trigger execution data
      List<TriggerExecutionDataKey> triggerExecutionData = getTriggerExecutionData(datosSesion);
      
      // create coupon for every customer
      for (TriggerExecutionDataKey triggerExecutionDataKey : triggerExecutionData) {
         NewCoupon newCoupon = new NewCoupon();
         newCoupon.setCouponName(data.getCouponName());
         newCoupon.setCouponDescription(data.getCouponDescription());
         newCoupon.setBalance(data.getBalance());
         newCoupon.setManualSelection(data.getManualSelection());
         newCoupon.setCustomerMaxUses(data.getCustomerMaxUses());
         newCoupon.setCreatedByClassId("LY_TRIGGERS_TBL.TRIGGER_UID");
         newCoupon.setCreatedByObjectId(trigger.getTriggerUid());         
         newCoupon.setCouponTypeCode(data.getCouponTypeCode());
         newCoupon.setImageUrl(data.getImageUrl());
         
         // vigence days relative 
         if (data.getVigenceStartDays() != null) {
            newCoupon.setStartDate(DateUtils.addDays(data.getStartDate() == null ? DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH) : data.getStartDate(), data.getVigenceStartDays()));
         } else if (data.getStartDate() != null) {
            newCoupon.setStartDate(data.getStartDate());
         }

         if (data.getVigenceEndDays() != null) {
            newCoupon.setEndDate(DateUtils.addDays(data.getEndDate() == null ? DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH) : data.getEndDate(), data.getVigenceEndDays()));
         } else if (data.getEndDate() != null) {
            newCoupon.setEndDate(data.getEndDate());
         }
                  
         // customer and promotion assign 
         newCoupon.setPromotionId(data.getPromotionId());
         newCoupon.setLoyalCustomerId(triggerExecutionDataKey.getIdFidelizado().toString());
         
         newCoupon.setCouponNameTranslations(data.getCouponNameTranslations());
         newCoupon.setCouponDescriptionTranslations(data.getCouponDescriptionTranslations());
         
         couponsService.insert(datosSesion, newCoupon);   

      }
   }
}
