package com.comerzzia.api.loyalty.tests;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.comerzzia.api.loyalty.persistence.triggers.dto.TriggerData;
import com.comerzzia.api.loyalty.service.triggers.data.TriggerDataServiceImpl;
import com.comerzzia.core.servicios.sesion.BasicSessionBean;

public class TriggerDataTests {
   TriggerDataServiceImpl service;
   
   TriggerData triggerData = new TriggerData();
   BasicSessionBean datosSesion = new BasicSessionBean();
   
   @Before
   public void setUp() throws Exception {
      service = new TriggerDataServiceImpl();
      
      datosSesion.setUidInstancia("THARSIS");
      datosSesion.setUidActividad("NON-FOOD");
   }
   
   private String replaceStrings(String sql) {
      sql = StringUtils.replace(sql, "#{uidActividad,jdbcType=VARCHAR}", "'NON-FOOD'");
      
      return StringUtils.replace(sql, "#{uidInstancia,jdbcType=VARCHAR}", "'THARSIS'");
   }

   //@Test
   public void emptyFilter() {
      System.out.println("-- EMPTY FILTER");
      System.out.println(replaceStrings(service.buildSql(datosSesion, triggerData)));            
      System.out.println("/");
   }

   //@Test
   public void dateFilter() {      
      //triggerData.setStartPurchaseDate(DateUtils.addDays(new Date(), -180));
      triggerData.setEndPurchaseDate(new Date());
      System.out.println("-- DATE FILTER");
      System.out.println(replaceStrings(service.buildSql(datosSesion, triggerData)));            
      System.out.println("/");
   }   
   
   //@Test
   public void catalogFilter() {
      String filtroJson = "{  \"condition\": \"AND\",  \"rules\": [    {      \"id\": \"SECCION\",      \"field\": \"SECCION\",      \"type\": \"ayuda\",      \"input\": \"text\",      \"operator\": \"equal\",      \"value\": \"1010\",      \"des\": \"CICLISMO\"    },    {      \"condition\": \"OR\",      \"rules\": [        {          \"id\": \"FAMILIAS\",          \"field\": \"FAMILIAS\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"205000\",          \"des\": \"BALONES \"        },        {          \"id\": \"ARTICULOS_NO_GENERICOS\",          \"field\": \"ARTICULOS_NO_GENERICOS\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"0000000000201\",          \"des\": \"BACKPACK GREY\"        },        {          \"id\": \"CATEGORIZACIONES\",          \"field\": \"CATEGORIZACIONES\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"1000\",          \"des\": \"CYCLING\"        },        {          \"id\": \"PROVEEDORES\",          \"field\": \"PROVEEDORES\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"40000000017\",          \"des\": \"PAPER SERVICE\"        },        {          \"id\": \"MARCA\",          \"field\": \"MARCA\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"25\",          \"des\": \"BICENTURY\"        },        {          \"id\": \"ETIQUETAS\",          \"field\": \"ETIQUETAS\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"Buen cliente\",          \"des\": \"Buen cliente\"        }      ]    }  ]}";
      
      triggerData.setCatalogFilters(filtroJson.getBytes());

      System.out.println("-- CATALOG FILTER");
      System.out.println(replaceStrings(service.buildSql(datosSesion, triggerData)));            
      System.out.println("/");
   }
   
   @Test
   public void serializeTest() {
      String filtroJson = "{  \"condition\": \"AND\",  \"rules\": [    {      \"id\": \"SECCION\",      \"field\": \"SECCION\",      \"type\": \"ayuda\",      \"input\": \"text\",      \"operator\": \"equal\",      \"value\": \"1010\",      \"des\": \"CICLISMO\"    },    {      \"condition\": \"OR\",      \"rules\": [        {          \"id\": \"FAMILIAS\",          \"field\": \"FAMILIAS\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"205000\",          \"des\": \"BALONES \"        },        {          \"id\": \"ARTICULOS_NO_GENERICOS\",          \"field\": \"ARTICULOS_NO_GENERICOS\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"0000000000201\",          \"des\": \"BACKPACK GREY\"        },        {          \"id\": \"CATEGORIZACIONES\",          \"field\": \"CATEGORIZACIONES\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"1000\",          \"des\": \"CYCLING\"        },        {          \"id\": \"PROVEEDORES\",          \"field\": \"PROVEEDORES\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"40000000017\",          \"des\": \"PAPER SERVICE\"        },        {          \"id\": \"MARCA\",          \"field\": \"MARCA\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"25\",          \"des\": \"BICENTURY\"        },        {          \"id\": \"ETIQUETAS\",          \"field\": \"ETIQUETAS\",          \"type\": \"ayuda\",          \"input\": \"text\",          \"operator\": \"equal\",          \"value\": \"Buen cliente\",          \"des\": \"Buen cliente\"        }      ]    }  ]}";
      
      triggerData.setStoreId("001");
      triggerData.setPurchaseAmount(new BigDecimal(100L));
      triggerData.setCatalogFilters(filtroJson.getBytes());
      
      byte[] data = service.serializeDataObject(triggerData);
      
      triggerData = service.createDataObject(data, TriggerData.class);

      System.out.println("-- SERIALIZE TEST");
      System.out.println(replaceStrings(service.buildSql(datosSesion, triggerData)));            
      System.out.println("/");
   }    
}
