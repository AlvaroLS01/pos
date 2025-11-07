package com.comerzzia.api.loyalty.service.triggers.data;

import com.comerzzia.api.loyalty.persistence.triggers.dto.TriggerData;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface TriggerDataService {

   String buildSql(IDatosSesion datosSesion, TriggerData triggerData);

   <T> T createDataObject(byte[] triggerData, Class<T> objectType);

   byte[] serializeDataObject(TriggerData triggerData);

}