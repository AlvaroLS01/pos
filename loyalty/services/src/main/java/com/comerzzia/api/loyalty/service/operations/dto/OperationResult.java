package com.comerzzia.api.loyalty.service.operations.dto;

public class OperationResult {
   protected Object operationData;
   protected String error;
   
   public Object getOperationData() {
      return operationData;
   }
   public void setOperationData(Object operationData) {
      this.operationData = operationData;
   }
   public String getError() {
      return error;
   }
   public void setError(String error) {
      this.error = error;
   }

   
}
