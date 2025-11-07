package com.comerzzia.api.loyalty.service.triggers.actions.types.tags;

import java.util.List;

public class ActionTagsData {
	
   public static final Integer OPERATION_DEASSIGN_TAGS = 0;
   public static final Integer OPERATION_ASSIGN_TAGS = 1;
	
   protected Integer operation;
   protected List<String> tags;
   
   public Integer getOperation() {
      return operation;
   }

   public void setOperation(Integer operation) {
      this.operation = operation;
   }

   public List<String> getTags() {
      return tags;
   }

   public void setTags(List<String> tags) {
      this.tags = tags;
   }
   
   public boolean isOperationDeassignTags() {
	   return OPERATION_DEASSIGN_TAGS.equals(operation);
   }
   
   public boolean isOperationAssignTags() {
	   return OPERATION_ASSIGN_TAGS.equals(operation);
   }
   
}
