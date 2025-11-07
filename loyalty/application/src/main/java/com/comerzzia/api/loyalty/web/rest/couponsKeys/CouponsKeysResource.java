package com.comerzzia.api.loyalty.web.rest.couponsKeys;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTO;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKey;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKeyExample;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKeyExample.Criteria;
import com.comerzzia.api.loyalty.service.couponsKeys.CouponsKeysService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/couponsKeys")
@Tag(name = "Coupons keys", description = "Coupons issuance keys services")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class CouponsKeysResource {
   @Resource(name = "datosSesionRequest")
   ComerzziaDatosSesion datosSesionRequest;
        
   @Autowired
   CouponsKeysService service;

   @GET
   @Operation( summary = "Get coupons issuance keys",
             description = "Get coupons issuance keys",
                responses = { @ApiResponse(description = "The coupon issuance keys list"), 
                              @ApiResponse(responseCode = "400", description = "Invalid input data") })
   public List<CouponIssuanceKey> getCouponsIssuanceKeys(@BeanParam 
                                           @Parameter(description = "Search params") 
                                           CouponIssuanceKey filter) throws ApiException {                  
      
      CouponIssuanceKeyExample example = new CouponIssuanceKeyExample(datosSesionRequest.getDatosSesionBean());
      
      if (filter != null) {
         Criteria criteria = example.or();
         
         if (filter.getCouponKey() != null) {
            criteria.andCouponKeyLike(filter.getCouponKey());
         }
         
         if (filter.getCouponName() != null) {
            criteria.andCouponNameLike(filter.getCouponName());
         }
         
         if (filter.getCouponTypeCode() != null) {
            criteria.andCouponTypeCodeEqualTo(filter.getCouponTypeCode());
         }
         
         if (filter.getActive() != null) {
            criteria.andActiveEqualTo(filter.getActive());
         }
      }
      
      return service.selectByExample(example);
   }   
   
   @GET
   @Path("/{couponIssuanceKey}")
   @Operation( summary = "Get record from key",
               description = "Get record from key",
               responses = { @ApiResponse(description = "The record data"), 
                             @ApiResponse(responseCode = "404", description = "Record not found") })
   public CouponIssuanceKey getCouponIssuanceKey(@PathParam("couponIssuanceKey") @Parameter(description = "The record id") 
                               String key) throws ApiException {
      CouponIssuanceKey record = service.selectByPrimaryKey(datosSesionRequest.getDatosSesionBean(), key);
      
      if (record == null) {
         throw new NotFoundException();   
      }
      
      return record;
   }
   
   @POST
   @Operation( summary = "Insert record",
               description = "Insert record",
               responses = { @ApiResponse(description = "The record data"), 
                             @ApiResponse(responseCode = "400", description = "Invalid data"), 
                             @ApiResponse(responseCode = "409", description = "Duplicate key error") })
   public CouponIssuanceKey newCouponIssuanceKey(CouponIssuanceKey record) throws ApiException {
      return service.insert(datosSesionRequest.getDatosSesionBean(), record);
   }   
   
   @PUT
   @Operation( summary = "Modify record from key",
               description = "Modify record from key",
               responses = { @ApiResponse(description = "The record data"), 
                             @ApiResponse(responseCode = "400", description = "Invalid data"), 
                             @ApiResponse(responseCode = "409", description = "Duplicate key error") })
   public CouponIssuanceKey updateCouponIssuanceKey(CouponIssuanceKey record) throws ApiException {
      return service.update(datosSesionRequest.getDatosSesionBean(), record);
   }
   
   @DELETE
   @Path("/{couponIssuanceKey}")
   @Operation( summary = "Delete record",
             responses = { @ApiResponse(responseCode = "204", description = "Empty response"), 
                           @ApiResponse(responseCode = "404", description = "Record not found") })
   public Response deactivate(@PathParam("couponIssuanceKey") 
                              @Parameter(description = "Coupon issuance key") 
                              String key) throws ApiException {
      getCouponIssuanceKey(key);
      
      service.delete(datosSesionRequest.getDatosSesionBean(), key);
      
      return Response.ok().status(Status.NO_CONTENT).build();
   }   
   
   @POST
   @Path("/{couponIssuanceKey}/createCoupon")
   @Operation( summary = "Create coupon from key",
             description = "Create coupon from key",
                responses = { @ApiResponse(description = "The coupon"), 
                              @ApiResponse(responseCode = "404", description = "Coupon key not found") })
   public CouponDTO createIssuanceKeyCoupon(@PathParam("couponIssuanceKey") 
                                            @Parameter(description = "Coupon issuance key") 
                                            String key,                  
                                            @QueryParam("customerId") 
                                            @Parameter(description = "The customer id", required=true) 
                                            String loyalCustomerId) throws ApiException {
      getCouponIssuanceKey(key);
      
      return service.createCoupon(datosSesionRequest.getDatosSesionBean(), key, loyalCustomerId);
   }     
}
