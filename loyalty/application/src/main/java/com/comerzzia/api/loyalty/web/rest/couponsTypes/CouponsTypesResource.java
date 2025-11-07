package com.comerzzia.api.loyalty.web.rest.couponsTypes;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponType;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponTypeExample;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponTypeExample.Criteria;
import com.comerzzia.api.loyalty.service.couponsTypes.CouponsTypesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/couponsTypes")
@Tag(name = "Coupons", description = "Coupons services")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class CouponsTypesResource {
   @Resource(name = "datosSesionRequest")
   ComerzziaDatosSesion datosSesionRequest;
        
   @Autowired
   CouponsTypesService service;

   @GET
   @Operation( summary = "Get coupons types",
             description = "Get coupons types",
                responses = { @ApiResponse(description = "The coupon types list"), 
                              @ApiResponse(responseCode = "400", description = "Invalid input data") })
   public List<CouponType> getCouponsTypes(@BeanParam 
                                           @Parameter(description = "Search params") 
                                           CouponType filter) throws ApiException {                  
      
      CouponTypeExample example = new CouponTypeExample(datosSesionRequest.getDatosSesionBean());
      
      if (filter != null) {
         Criteria criteria = example.or();
         
         if (filter.getCouponTypeName() != null) {
            criteria.andCouponTypeNameLike(filter.getCouponTypeName());
         }
         
         if (filter.getGenerationMode() != null) {
            criteria.andGenerationModeEqualTo(filter.getGenerationMode());
         }
         
         if (filter.getPrefix() != null) {
            criteria.andPrefixLike(filter.getPrefix() + "%");
         }
      }
      
      return service.selectByExample(example);
   }   
   
   @GET
   @Path("/{couponTypeCode}")
   @Operation( summary = "Get record from key",
               description = "Get record from key",
               responses = { @ApiResponse(description = "The record data"), 
                             @ApiResponse(responseCode = "404", description = "Record not found") })
   public CouponType getCouponType(@PathParam("couponTypeCode") @Parameter(description = "The record uid") 
                               String id) throws ApiException {
      CouponType record = service.selectByPrimaryKey(datosSesionRequest.getDatosSesionBean(), id);
      
      if (record == null) {
         throw new NotFoundException();   
      }
      
      return record;
   }
}
