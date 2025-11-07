package com.comerzzia.api.loyalty.web.rest.configuration;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "variable")
public class VariableDTO {
   protected String variableId;
   protected String value;

   public String getVariableId() {
      return variableId;
   }

   public void setVariableId(String variableId) {
      this.variableId = variableId;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

}
