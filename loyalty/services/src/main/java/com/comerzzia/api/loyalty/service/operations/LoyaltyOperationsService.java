package com.comerzzia.api.loyalty.service.operations;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.service.operations.dto.LoyaltySaleOperation;
import com.comerzzia.api.loyalty.service.operations.dto.LoyaltySaleOperationResult;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface LoyaltyOperationsService {

   LoyaltySaleOperationResult registerLoyaltyOperation(IDatosSesion datosSesion, LoyaltySaleOperation loyaltySaleOperation)
         throws ApiException;

}