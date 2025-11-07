package com.comerzzia.api.loyalty.service.triggers.actions;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerActionExample;
import com.comerzzia.api.loyalty.persistence.triggers.actions.dto.TriggerActionDTO;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface TriggersActionsService {

   TriggerAction insert(IDatosSesion datosSesion, TriggerAction newRecord) throws ApiException;

   TriggerAction selectByPrimaryKey(IDatosSesion datosSesion, String actionUid);

   List<TriggerAction> selectByExample(TriggerActionExample example);

   TriggerActionDTO selectDTOByPrimaryKey(IDatosSesion datosSesion, String actionUid);

   List<TriggerActionDTO> selectDTOByExample(TriggerActionExample example);

   int delete(IDatosSesion datosSesion, String actionUid);

   int deleteByTriggerUid(IDatosSesion datosSesion, String triggerUid);

   int updateByPrimaryKey(IDatosSesion datosSesion, TriggerAction record);

   Object getDataObject(TriggerAction triggerAction);

}