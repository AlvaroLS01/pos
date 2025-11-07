package com.comerzzia.api.loyalty.service.triggers.actions.types.points;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.accounts.activities.AccountActivity;
import com.comerzzia.api.loyalty.persistence.cards.Card;
import com.comerzzia.api.loyalty.persistence.cards.CardExample;
import com.comerzzia.api.loyalty.persistence.triggers.Trigger;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataKey;
import com.comerzzia.api.loyalty.service.accounts.activities.AccountsActivitiesService;
import com.comerzzia.api.loyalty.service.cards.CardsService;
import com.comerzzia.api.loyalty.service.triggers.TriggersService;
import com.comerzzia.api.loyalty.service.triggers.actions.types.ActionTypeAbstractService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;


@Component("ActionPointsService")
@Scope("prototype")
public class ActionPointsServiceImpl extends ActionTypeAbstractService {
   private final Logger log = LoggerFactory.getLogger(ActionPointsServiceImpl.class);
   
   private final String LOYAL_CUSTOMER_CARD_TYPE = "A";
   
   protected ActionPointsData data;
   
   @Autowired
   TriggersService triggerService;
   
   @Autowired
   AccountsActivitiesService accountActivitiesService;
   
   @Autowired
   private CardsService cardsService;
   
   @Override
   public void setTriggerAction(TriggerAction triggerAction) {
      super.setTriggerAction(triggerAction);;
      data = createDataObject(ActionPointsData.class);
   }  
   
   public Object getDataObject() {
      return data;
   }
      
   @Transactional(rollbackFor = Exception.class)
   public void executeAction(IDatosSesion sessionData) throws ApiException {
	  log.info("Executing action: " + triggerAction.getActionUid() + " Trigger: " + triggerAction.getTriggerUid());
      if (data == null) {
         throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, 
               LY_TRIGGER_ACTIION_TYPE_NULL_DATA, 
               new String[] {});       
      }
      
      Trigger trigger = triggerService.selectByPrimaryKey(sessionData, triggerAction.getTriggerUid());
      
      // query customers from trigger execution data
      List<TriggerExecutionDataKey> triggerExecutionData = getTriggerExecutionData(sessionData);
      
      // create the account's activity for every customer
      for (TriggerExecutionDataKey triggerExecutionDataKey : triggerExecutionData) {
    	  
    	  
    	  CardExample example = new CardExample(sessionData);
    	  example.or().andCodTipoTarjEqualTo(LOYAL_CUSTOMER_CARD_TYPE).andIdFidelizadoEqualTo(triggerExecutionDataKey.getIdFidelizado()).andFechaBajaIsNull();
    	  List<Card> cards = cardsService.selectByExample(example);
    	  if(cards.size()<1) {
    		  //The loyal customer doesn't have any eligible card
    		  continue;
    	  }
    	  Card card = cards.get(0);
    	  
    	  AccountActivity accountActivity = new AccountActivity();
          accountActivity.setIdTarjeta(card.getIdTarjeta());
          accountActivity.setIdCuentaTarjeta(card.getIdCuentaTarjeta());
          accountActivity.setEntrada(data.getPoints().doubleValue());
          accountActivity.setSalida(Double.valueOf(0));
          accountActivity.setFecha(new Date());
          
          accountActivity.setIdUsuario(sessionData.getUserId());
          accountActivity.setEstadoMovimiento(AccountActivity.MOVIMIENTO_DEFINITIVO);
          accountActivity.setConcepto(data.getDescription());
          accountActivity.setDocumento(trigger.getTriggerCode());
    	  accountActivitiesService.insert(accountActivity, sessionData);
      }
   }
}
