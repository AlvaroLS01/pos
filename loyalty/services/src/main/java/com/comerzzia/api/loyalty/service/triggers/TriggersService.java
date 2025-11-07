package com.comerzzia.api.loyalty.service.triggers;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.Trigger;
import com.comerzzia.api.loyalty.persistence.triggers.TriggerExample;
import com.comerzzia.api.loyalty.persistence.triggers.dto.TriggerDTO;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface TriggersService {

   Trigger insert(IDatosSesion datosSesion, Trigger newTrigger) throws ApiException;

   Trigger selectByPrimaryKey(IDatosSesion datosSesion, String triggerUid);

   List<Trigger> selectByExample(TriggerExample example);

   TriggerDTO selectDTOByPrimaryKey(IDatosSesion datosSesion, String triggerUid);

   List<TriggerDTO> selectDTOByExample(TriggerExample example);

   int delete(IDatosSesion datosSesion, String triggerUid);

   int updateByPrimaryKey(IDatosSesion datosSesion, Trigger record);

}