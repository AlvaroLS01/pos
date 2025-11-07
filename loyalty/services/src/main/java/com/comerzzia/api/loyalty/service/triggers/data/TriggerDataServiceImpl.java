package com.comerzzia.api.loyalty.service.triggers.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.comerzzia.api.loyalty.persistence.triggers.dto.TriggerData;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Service
public class TriggerDataServiceImpl implements TriggerDataService {
   protected static Logger log = Logger.getLogger(TriggerDataServiceImpl.class);
   
   private static final String SINGLE_QUOTE = "'";
   
   protected static final String TABLA_FIDELIZADOS = "F_FIDELIZADOS_TBL";
   protected static final String TABLA_FIDELIZADOS_COLECTIVOS = "F_FIDELIZADOS_COLECTIVOS_TBL";
   protected static final String TABLA_TARJETAS = "F_TARJETAS_TBL";
   protected static final String TABLA_CLIE_ALBARAN_CAB = "D_CLIE_ALBARANES_CAB_TBL";
   protected static final String TABLA_CLIE_ALBARAN_DET = "D_CLIE_ALBARANES_DET_TBL";
   protected static final String TABLA_ETIQUETAS_ENLACE = "D_ETIQUETAS_ENL_TBL";
   protected static final String TABLA_ARTICULOS = "D_ARTICULOS_TBL";
   protected static final String TABLA_ACTIVIDADES = "D_ACTIVIDADES_TBL";
   
   protected HashMap<String, String> catalogFieldsMap;
   protected HashMap<String, String> customerFieldsMap;
      
   private boolean isQuoted(final String value) {
      return value.startsWith(SINGLE_QUOTE) && value.endsWith(SINGLE_QUOTE);
   }
   
   private String singleQuote(final String value) {
      if ((value != null) && !value.isEmpty()
              && !isQuoted(value)) {
          return SINGLE_QUOTE.concat(value).concat(SINGLE_QUOTE);
      }
      
      return value;
   }
   
   private String bracketString(final String value) {
      String result = "";
            
      if (value != null) {
         result = "(" +  value + ")";   
      }
      
      return result;
   }
   
   protected String operatorString(final String operator) {
      return StringUtils.equals("equal", operator) ? "=" : "<>";
   }
   
   protected String operatorValueString(final String operator, final String value) {
      return operatorString(operator) + " " + singleQuote(value);
   }   
   
   protected void configureFieldMaps() {
      // was configured
      if (catalogFieldsMap != null) return;
      
      // catalog fields map
      catalogFieldsMap = new HashMap<String, String>();
      catalogFieldsMap.put("FAMILIAS", "ART.CODFAM");
      catalogFieldsMap.put("ARTICULOS_NO_GENERICOS", "ART.CODART");      
      catalogFieldsMap.put("ETIQUETAS", "ETI.UID_ETIQUETA");
      catalogFieldsMap.put("CATEGORIZACIONES", "ART.CODCAT");
      catalogFieldsMap.put("PROVEEDORES", "ART.CODPRO");
      catalogFieldsMap.put("SECCION", "ART.CODSECCION");
      catalogFieldsMap.put("MARCA", "ART.CODMARCA");
      
      // customer fields map      
      customerFieldsMap = new HashMap<String, String>();      
      customerFieldsMap.put("ETIQUETAS_FIDELIZADOS", "UID_ETIQUETA");
      customerFieldsMap.put("COLECTIVOS", "COD_COLECTIVO");
   }
   
   // build recursive sql based on json filter tree
   protected String recursiveFilter(JsonNode jsonNode, String filterTree, HashMap<String, String> fieldsMap) {
      String andOr = " " + jsonNode.get("condition").asText() + " ";
      String childConditions = null;
      List<String> filter = new ArrayList<String>();
      
      for (JsonNode node : jsonNode.get("rules")) {
         if (node.get("condition") != null) {
            childConditions = recursiveFilter(node, filterTree, fieldsMap);            
         }
         else {
            JsonNode field = node.get("field");
            if (field == null) {
               continue;
            }
            
            // find mapping
            String fieldName = fieldsMap.get(field.asText());
            if (fieldName == null) {
               log.warn("Catalog filter type not supported: " + field.asText());
            } else {
               filter.add(fieldName + " " + operatorValueString(node.get("operator").asText(), node.get("value").asText()));
            }
         }
      }
      
      if (childConditions != null) {
         filter.add(childConditions);
      }
      
      // build conditions
      String conditions = "";
      
      for (String condition : filter) {
         conditions += conditions.isEmpty() ? condition : andOr + condition; 
      }
      
      // build macro
      filterTree += filterTree.isEmpty() ? bracketString(conditions) : andOr + bracketString(conditions);
      
      return filterTree;
   }
   
   protected String customerRecursiveFilter(JsonNode jsonNode, String filterTree, HashMap<String, String> fieldsMap) {
	      String andOr = " " + jsonNode.get("condition").asText() + " ";
	      String childConditions = null;
	      List<String> filter = new ArrayList<String>();
	      Map<String, List<String>> filterInNotIn = new HashMap<String, List<String>>();
	      
	      for (JsonNode node : jsonNode.get("rules")) {
	         if (node.get("condition") != null) {
	            childConditions = customerRecursiveFilter(node, filterTree, fieldsMap);            
	         }
	         else {
	            JsonNode field = node.get("field");
	            if (field == null) {
	               continue;
	            }
	            
	            // find mapping
	            String fieldName = fieldsMap.get(field.asText());
	            if (fieldName == null) {
	               log.warn("Catalog filter type not supported: " + field.asText());
	            } else {
	            	List<String> filterList = filterInNotIn.get(node.get("operator").asText() + "-" + fieldName);	
	            	
	            	if (filterList == null) {
	            		filterList = new ArrayList<String>();
            		}
	              	filterList.add(singleQuote(node.get("value").asText()));
	            	
	            	filterInNotIn.put(node.get("operator").asText() + "-" + fieldName, filterList);
	            }
	         }
	      }
	      
	      for (Map.Entry<String, List<String>> entry : filterInNotIn.entrySet()) {
	    	  String operator = entry.getKey().split("-")[0];
	    	  String fieldName = entry.getKey().split("-")[1];	    	  
	    	  String subQueryString;
	    	  
	    	  final String fieldFilterString = fieldName 
	    			                           + " IN "  	    	                       
	    			                           + bracketString(StringUtils.join(entry.getValue(), ","));
	    	  
	    	  if ("COD_COLECTIVO".equals(fieldName)) {
	    		  subQueryString = new SQL() {{
		    	        SELECT("ID_FIDELIZADO");
		    	        FROM(TABLA_FIDELIZADOS_COLECTIVOS);
		    	        WHERE("UID_INSTANCIA = #{uidInstancia,jdbcType=VARCHAR} AND " + fieldFilterString);
		    	      }}.toString();
	    	  } else {
	    		  subQueryString = new SQL() {{
		    	        SELECT("ID_OBJETO AS ID_FIDELIZADO");
		    	        FROM(TABLA_ETIQUETAS_ENLACE);
		    	        WHERE("UID_ACTIVIDAD=#{uidActividad,jdbcType=VARCHAR} AND ID_CLASE = 'F_FIDELIZADOS_TBL.ID_FIDELIZADO' AND " 
		    	              + fieldFilterString);
		    	      }}.toString();
	    	  }	   
	    	  
	    	  filter.add("\r\nFID.ID_FIDELIZADO "  
                         + (operator.equals("equal") ? "IN " : "NOT IN ") 
	    			     + bracketString("\r\n" + subQueryString));
	      }
	      
	      if (childConditions != null) {
	         filter.add(childConditions);
	      }
	      
	      // build conditions
	      String conditions = "";
	      
	      for (String condition : filter) {
	         conditions += (conditions.isEmpty() ? condition : andOr + condition) + "\r\n"; 
	      }
	      
	      // build macro
	      filterTree +=  (filterTree.isEmpty() ? bracketString(conditions) : "\r\n" + andOr + bracketString(conditions));
	      
	      // regular expression insert tab character on every line
	      return filterTree.replaceAll("(?m)^", "\t");
	   }
      
   // builds customer subquery filter
   protected String buildCustomerSql(final IDatosSesion datosSesion, final TriggerData triggerData) {
      String filterTree = "";
      if(triggerData.getCustomerFilters() != null && triggerData.getCustomerFilters().length > 0) {
    	  ObjectMapper objectMapper = new ObjectMapper();
          try {
             JsonNode jsonNode = objectMapper.readTree(new String(triggerData.getCustomerFilters()));
             
             filterTree = customerRecursiveFilter(jsonNode, filterTree, customerFieldsMap);
          } catch (IOException e) {
             log.error("Error parsing customer filters " + e.getMessage(), e);
             filterTree = "1 = 0";
          }
      }   
      
      // assign final value
      final String filter = filterTree;
      
      return new SQL() {{
        SELECT_DISTINCT("FID.ID_FIDELIZADO");
        FROM(TABLA_FIDELIZADOS + " FID");
        WHERE("FID.UID_INSTANCIA = #{uidInstancia,jdbcType=VARCHAR}");
        
        if (!filter.isEmpty()) {
           WHERE(filter);
        }
      }}.toString();
   }

   // builds catalog subquery filter
   protected String buildCatalogSql(final IDatosSesion datosSesion, final TriggerData triggerData) {
      String filterTree = "";
            
      ObjectMapper objectMapper = new ObjectMapper();
      try {
         JsonNode jsonNode = objectMapper.readTree(new String(triggerData.getCatalogFilters()));
         
         filterTree = recursiveFilter(jsonNode, filterTree, catalogFieldsMap);
      } catch (IOException e) {
         log.error("Error parsing catalog filters " + e.getMessage(), e);
         filterTree = "1 = 0";
      }      
      
      // assign final value
      final String filter = filterTree;
      
      return new SQL() {{
        SELECT_DISTINCT("CODART");
        FROM(TABLA_ARTICULOS+" ART");
        LEFT_OUTER_JOIN(TABLA_ETIQUETAS_ENLACE+" ETI ON (ETI.ID_CLASE='D_ARTICULOS_TBL.CODART' AND ART.CODART = ETI.ID_OBJETO AND ART.UID_ACTIVIDAD = ETI.UID_ACTIVIDAD)");                
        WHERE("ART.UID_ACTIVIDAD=#{uidActividad,jdbcType=VARCHAR}");
        
        if (!filter.isEmpty()) {
           WHERE(filter);
        }
      }}.toString();
   }   
   
// method to include the sales detail only if the filter has need
   protected boolean isNeedsDetailData(TriggerData triggerData) {
	   return (triggerData.getCatalogFilters() != null && triggerData.getCatalogFilters().length > 0) ||
               (triggerData.getUnitsCount() != null);
   }
   
// method to include the sales data
   protected boolean isNeedsSalesData(TriggerData triggerData) {
	   return triggerData.getStartPurchaseDate() != null ||
				 triggerData.getStartPurchaseDays() != null || 
				 triggerData.getEndPurchaseDate() != null ||
 		         triggerData.getEndPurchaseDays() != null ||
 		         StringUtils.isNotBlank(triggerData.getStoreId()) ||
 		         StringUtils.isNotBlank(triggerData.getSalesChannelId()) ||
 		         triggerData.getPurchaseAmount() != null ||
 		         triggerData.getPurchaseCount() != null ||
 		         isNeedsDetailData(triggerData);
   }

   // query for acumulate results by principal summaries
   protected String buildAcumlateSql(final IDatosSesion datosSesion, final TriggerData triggerData) {      
      return new SQL() {{

      	// primary fields
          SELECT("TARJ.ID_FIDELIZADO");
          SELECT("COUNT(DISTINCT ALB.ID_CLIE_ALBARAN) AS NUMERO_COMPRAS");
          SELECT("SUM(ALB.TOTAL) AS TOTAL");
          
          if (isNeedsDetailData(triggerData)) SELECT("SUM(DET.CANTIDAD) AS UNIDADES");      
          
          FROM(TABLA_CLIE_ALBARAN_CAB+" ALB");
          if (isNeedsDetailData(triggerData)) INNER_JOIN(TABLA_CLIE_ALBARAN_DET+" DET ON (DET.UID_ACTIVIDAD = ALB.UID_ACTIVIDAD AND DET.ID_CLIE_ALBARAN = ALB.ID_CLIE_ALBARAN)");
          INNER_JOIN(TABLA_TARJETAS+" TARJ ON (TARJ.NUMERO_TARJETA = ALB.TARJETA_FIDELIZACION AND TARJ.UID_INSTANCIA = #{uidInstancia,jdbcType=VARCHAR})");
          WHERE("ALB.UID_ACTIVIDAD = #{uidActividad,jdbcType=VARCHAR}");
          
          // store filter
          if (StringUtils.isNotBlank(triggerData.getStoreId())) {
             WHERE("ALB.CODALM = #{storeId,jdbcType=VARCHAR}");
          }
          
          // sales channel filter
          if (StringUtils.isNotBlank(triggerData.getSalesChannelId())) {
             WHERE("ALB.CODALM IN (SELECT CODALM FROM D_TIENDAS_TBL WHERE CODCANAL = #{salesChannelId,jdbcType=VARCHAR})");
          }
          
          // date filters
          if ((triggerData.getStartPurchaseDate() != null && triggerData.getEndPurchaseDate() != null) || (triggerData.getStartPurchaseDays() != null && triggerData.getEndPurchaseDays() != null)) {
             WHERE("ALB.FECHA BETWEEN #{startPurchaseDate,jdbcType=TIMESTAMP} AND #{endPurchaseDate,jdbcType=TIMESTAMP}");
          } else if (triggerData.getStartPurchaseDate() != null || triggerData.getStartPurchaseDays() != null) {
             WHERE("ALB.FECHA >= #{startPurchaseDate,jdbcType=TIMESTAMP}");           
          } else if (triggerData.getEndPurchaseDate() != null) {
             WHERE("ALB.FECHA <= #{endPurchaseDate,jdbcType=TIMESTAMP}");
          }
          
          // customer filters
          if (triggerData.getCustomerFilters() != null && triggerData.getCustomerFilters().length > 0) {
             WHERE("TARJ.ID_FIDELIZADO IN (" + buildCustomerSql(datosSesion, triggerData) + ")");
          }
          
          // catalog filters
          if (triggerData.getCatalogFilters() != null && triggerData.getCatalogFilters().length > 0) {
             WHERE("DET.CODART IN (" + buildCatalogSql(datosSesion, triggerData) + ")");
          }        
          
          GROUP_BY("TARJ.ID_FIDELIZADO");
          
          // Having. Economic filters
          if (triggerData.getPurchaseAmount() != null) {
             HAVING("SUM(ALB.TOTAL) >= #{purchaseAmount,jdbcType=DECIMAL}");
          }
          
          if (triggerData.getUnitsCount() != null) {
             HAVING("SUM(DET.CANTIDAD) >= #{unitsCount,jdbcType=DECIMAL}");
          }
          
          if (triggerData.getPurchaseCount() != null) {
             HAVING("COUNT(DISTINCT ALB.ID_CLIE_ALBARAN) >= #{purchaseCount,jdbcType=DECIMAL}");
          }        
      
          
        }}.toString();
   }
   
   @Override
   public String buildSql(final IDatosSesion datosSesion, final TriggerData triggerData) {
      // initialize the fields maps for customers and catalog
      configureFieldMaps();
      
      return new SQL() {{
        // main query
        SELECT("F.ID_FIDELIZADO, F.NOMBRE, F.APELLIDOS, F.DOMICILIO, F.POBLACION, F.LOCALIDAD, F.PROVINCIA, F.CP, F.CODPAIS, F.DOCUMENTO");
        if(isNeedsSalesData(triggerData)) {
        	FROM("(" + buildAcumlateSql(datosSesion, triggerData) + ") ACU"); 
        }else {
        	FROM("(" + buildCustomerSql(datosSesion, triggerData) + ") ACU"); 
        }
               
        INNER_JOIN(TABLA_FIDELIZADOS+" F ON (F.ID_FIDELIZADO = ACU.ID_FIDELIZADO AND F.UID_INSTANCIA = #{uidInstancia,jdbcType=VARCHAR}) ");
                                                                       
      }}.toString();
    }
   

   @Override
   public <T> T createDataObject(final byte[] triggerData, Class<T> objectType) {
      if (triggerData == null) {
         return null;
      }
      
      ObjectMapper objectMapper = new ObjectMapper();
      // jaxb annotations support
      JaxbAnnotationModule module = new JaxbAnnotationModule();
      objectMapper.registerModule(module);
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      
      try {
         return objectMapper.readValue(new String(triggerData), objectType);
      } catch (JsonParseException | JsonMappingException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public byte[] serializeDataObject(final TriggerData triggerData) {
      ObjectMapper objectMapper = new ObjectMapper();
      // jaxb annotations support
      JaxbAnnotationModule module = new JaxbAnnotationModule();
      objectMapper.registerModule(module);
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      
      try {
         return objectMapper.writeValueAsBytes(triggerData);
      } catch (JsonParseException | JsonMappingException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }      
   }
}
