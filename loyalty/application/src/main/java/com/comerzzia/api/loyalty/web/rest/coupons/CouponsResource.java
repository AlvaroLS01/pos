package com.comerzzia.api.loyalty.web.rest.coupons;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.BadRequestException;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.api.core.service.util.FromDateToDate;
import com.comerzzia.api.loyalty.persistence.coupons.Coupon;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponCustomerUseDTO;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTO;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponsKpiDTO;
import com.comerzzia.api.loyalty.service.coupons.CouponsService;
import com.comerzzia.api.loyalty.service.coupons.CouponsServiceDinosolImpl;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponRedeemData;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponsFilter;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;
import com.comerzzia.api.loyalty.service.coupons.dto.RedeemCoupon;
import com.comerzzia.api.loyalty.service.operations.dto.SaleOperationData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/coupons")
@Tag(name = "Coupons", description = "Coupons services")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class CouponsResource {
   @Resource(name = "datosSesionRequest")
   ComerzziaDatosSesion datosSesionRequest;
  
   @Autowired
   CouponsService service;
   
   @GET
   @Operation( summary = "Get coupons",
             description = "Get coupons",
                responses = { @ApiResponse(description = "The coupon list"), 
                              @ApiResponse(responseCode = "400", description = "Invalid input data") })
   public List<CouponDTO> getCoupons(@BeanParam 
                                     @Parameter(description = "Search params") 
                                     CouponsFilter filter) throws ApiException {                  
      
      return service.selectByFilter(datosSesionRequest.getDatosSesionBean(), filter);
   }
         
   @GET
   @Path("/{couponCode}")
   @Operation( summary = "Get coupon from key",
             description = "Get coupon from key",
                responses = { @ApiResponse(description = "The coupon"), 
                              @ApiResponse(responseCode = "404", description = "Coupon not found") })
   public CouponDTO getCoupon(@PathParam("couponCode") @Parameter(description = "The coupon code") String couponCode) throws ApiException {
      CouponDTO record = service.selectByUk(datosSesionRequest.getDatosSesionBean(), couponCode);
      
      if (record == null) {
         throw new NotFoundException();   
      }
      
      return record;
   }  
   
   @POST
   @Path("/{couponCode}/validate")
   @Operation( summary = "Validate coupon",
             description = "Validate coupon",
                responses = { @ApiResponse(description = "The coupon"), 
                            @ApiResponse(responseCode = "404", description = "Coupon not found") })
   public CouponDTO validateCoupon(@PathParam("couponCode") 
                                   @Parameter(description = "The coupon code") 
                                   String couponCode,                  
                                   @QueryParam("customerId") 
                                   @Parameter(description = "The customer id", required=true) String loyalCustomerId) throws ApiException {
      CouponCustomerUseDTO couponUse = service.selectByUkAndCustomer(datosSesionRequest.getDatosSesionBean(), couponCode, loyalCustomerId);
      
      service.validate(datosSesionRequest.getDatosSesionBean(), couponUse, new Date(), loyalCustomerId);
      
      return couponUse;
   }  
   

//   @POST 
//   @Operation( summary = "Insert coupon",
//             responses = { @ApiResponse(description = "Inserted coupon"), 
//                           @ApiResponse(responseCode = "400", description = "Invalid input data"), 
//                           @ApiResponse(responseCode = "409", description = "Coupon already exists") })
//   public CouponDTO newCoupon(@Valid NewCoupon record) throws ApiException {     
//      Coupon newRecord = service.insert(datosSesionRequest.getDatosSesionBean(), record);    
//      
//      return getCoupon(newRecord.getCouponCode());
//   }
   
   @POST
//   @Path("/newCoupons/")
   @Operation( summary = "Insert coupons",
             responses = { @ApiResponse(description = "List of coupons codes created"), 
                           @ApiResponse(responseCode = "400", description = "Invalid input data"), 
                           @ApiResponse(responseCode = "409", description = "Coupon already exists") })
   public List<String> newCoupons(@Valid List<NewCoupon> records) throws ApiException {     
      return service.insertMultiple(datosSesionRequest.getDatosSesionBean(), records);      
   }    
    
   @GET
   @Path("/bycustomer/{customerId}")
   @Operation( summary = "Get customer coupons",
               description = "Get customer coupons",
               responses = { @ApiResponse(description = "The coupons records"), 
                             @ApiResponse(responseCode = "404", description = "Record not found") })   
   public List<CouponDTO> getCustomerCoupons(@PathParam("customerId") 
                                             @Parameter(description = "The customer id") 
                                             String loyalCustomerId,
                                             @BeanParam
                                             @Parameter(description = "Search params") 
                                             CouponsFilter filter) throws ApiException {
      
      filter.setLoyalCustomerId(loyalCustomerId);
     
      return service.selectByCustomer(datosSesionRequest.getDatosSesionBean(), filter);
   }
   
   @GET
   @Path("/kpis")
   @Operation( summary = "Get coupons KPIs",
             description = "Get coupons KPIs",
                responses = { @ApiResponse(description = "The kpis values"), 
                              @ApiResponse(responseCode = "400", description = "Invalid input data") })
   public CouponsKpiDTO getCouponsKpis(@BeanParam 
                                       @Parameter(description = "Search params") 
                                       FromDateToDate fromDateToDate) throws ApiException {                  
      
      fromDateToDate.validateFormat();
      
      Date startDate = fromDateToDate.getFromDateAsDate(false);
      Date endDate = fromDateToDate.getToDateAsDate(false);      
      
      if (startDate == null) {
         startDate = DateUtils.addMonths(new Date(), -12);
      }
      
      if (endDate == null) {
         endDate = DateUtils.addMonths(new Date(), 12);
      }
      
      return service.selectKPIS(datosSesionRequest.getDatosSesionBean(), startDate, endDate);
   }       
   
   @GET
   @Path("/bycustomer/{customerId}/kpis")
   @Operation( summary = "Get customer coupons KPIs",
             description = "Get customer coupons KPIs",
                responses = { @ApiResponse(description = "The kpis values"), 
                              @ApiResponse(responseCode = "400", description = "Invalid input data") })
   public CouponsKpiDTO getCustomerCouponsKpis(@PathParam("customerId") 
                                       @Parameter(description = "The customer id") 
                                       String loyalCustomerId,
                                       @BeanParam 
                                       @Parameter(description = "Search params") 
                                       FromDateToDate fromDateToDate) throws ApiException {                  
      
      fromDateToDate.validateFormat();
      
      Date startDate = fromDateToDate.getFromDateAsDate(false);
      Date endDate = fromDateToDate.getToDateAsDate(false);      
      
      if (startDate == null) {
         startDate = DateUtils.addMonths(new Date(), -12);
      }
      
      if (endDate == null) {
         endDate = DateUtils.addMonths(new Date(), 12);
      }
      
      return service.selectKPIS(datosSesionRequest.getDatosSesionBean(), loyalCustomerId, startDate, endDate);
   }
      
   @POST 
   @Path("/bycustomer/{customerId}")   
   @Operation( summary = "Insert customer coupon",
             responses = { @ApiResponse(description = "Inserted coupon"), 
                           @ApiResponse(responseCode = "400", description = "Invalid input data"), 
                           @ApiResponse(responseCode = "409", description = "Coupon already exists") })
   public CouponDTO newCustomerCoupon(@PathParam("customerId") 
                                      @Parameter(description = "The customer id") 
                                      String loyalCustomerId,
                                      @Valid NewCoupon record) throws ApiException {
      record.setLoyalCustomerId(loyalCustomerId);
      Coupon newRecord = service.insert(datosSesionRequest.getDatosSesionBean(), record);    
      
      return getCoupon(newRecord.getCouponCode());
   }

   @POST
   @Path("/{couponCode}/redeem")
   @Operation( summary = "Redeem coupon",
             description = "Redeem coupon",
                responses = { @ApiResponse(responseCode = "204", description = "Empty response"),
                              @ApiResponse(responseCode = "404", description = "Coupon not found") })
   public Response redeemCoupon(@PathParam("couponCode") 
                            @Parameter(description = "The coupon code") 
                            String couponCode,                                                    
                            @Valid SaleOperationData redeemData) throws ApiException {
      CouponDTO record = getCoupon(couponCode);
            
      // create redeem coupon request
      RedeemCoupon redeemCoupon = new RedeemCoupon();
      try {
         BeanUtils.copyProperties(redeemCoupon, redeemData);
      } catch (IllegalAccessException | InvocationTargetException e) {
         throw new ApiException(e.getMessage(), e);
      }
      CouponRedeemData couponRedeemData = new CouponRedeemData();
      couponRedeemData.setCouponCode(couponCode);
      couponRedeemData.setDiscount(record.getBalance() == null ? BigDecimal.ZERO : record.getBalance());
      redeemCoupon.setCouponRedeemData(couponRedeemData);
      
      service.redeem(datosSesionRequest.getDatosSesionBean(), redeemCoupon);
      
      return Response.ok().status(Status.NO_CONTENT).build();
   }
   
   @PUT
   @Path("/{couponCode}/deactivate")
   @Operation( summary = "Deactivate coupon",
             description = "Deactivate coupon",
                responses = { @ApiResponse(responseCode = "200", description = "Coupon data"),
                              @ApiResponse(responseCode = "400", description = "Validation error"),
                              @ApiResponse(responseCode = "404", description = "Coupon not found") })
   public CouponDTO deactivateCoupon(@PathParam("couponCode") 
                            @Parameter(description = "The coupon code") 
                            String couponCode) throws ApiException {
      
      return service.deactivate(datosSesionRequest.getDatosSesionBean(), couponCode);
   }
   
   @PUT
   @Path("/{couponCode}/activate")
   @Operation( summary = "Activate coupon",
             description = "Activate coupon",
                responses = { @ApiResponse(responseCode = "200", description = "Coupon data"),
                              @ApiResponse(responseCode = "400", description = "Validation error"),
                              @ApiResponse(responseCode = "404", description = "Coupon not found") })
   public CouponDTO activateCoupon(@PathParam("couponCode") 
                            @Parameter(description = "The coupon code") 
                            String couponCode) throws ApiException {
      
      return service.activate(datosSesionRequest.getDatosSesionBean(), couponCode);
   }    
   

   @PUT
   @Path("/{couponCode}/updateManualSellection")
   @Operation( summary = "Update coupon manual sellection",
             description = "Update coupon manual sellection",
                responses = { @ApiResponse(description = "The coupon"), 
                            @ApiResponse(responseCode = "404", description = "Coupon not found") })
   public CouponDTO updateManualSellection(@PathParam("couponCode") 
                                   @Parameter(description = "The coupon code") 
                                   String couponCode,                  
                                   @QueryParam("manualSellection") 
                                   @Parameter(description = "The new manual sellection value", required=true) 
   								   Boolean manualSellection) throws ApiException {
      CouponDTO coupon = getCoupon(couponCode);
      
      ((CouponsServiceDinosolImpl)service).updateManualSellection(datosSesionRequest.getDatosSesionBean(), coupon, manualSellection);

      coupon.setManualSelection(manualSellection);
      
      return coupon;
   }  
   
   @PUT
   @Path("/{couponCode}/updateLoyalCustomerId")
   @Operation( summary = "Assign anonymous coupon to loyal customer",
             description = "Assign anonymous coupon to loyal customer",
                responses = { @ApiResponse(description = "The coupon"), 
                            @ApiResponse(responseCode = "400", description = "El cupón ya está asignado a un cliente"), 
                            @ApiResponse(responseCode = "404", description = "Coupon not found") })
   public CouponDTO updateLoyalCustomerId(@PathParam("couponCode") 
                                   @Parameter(description = "The coupon code") 
                                   String couponCode,                  
                                   @QueryParam("loyalCustomerId") 
                                   @Parameter(description = "The new loyal customer id", required=true) 
   								   String loyalCustomerId) throws ApiException {
      CouponDTO coupon = getCoupon(couponCode);
      
      if (!StringUtils.equals("0", coupon.getLoyalCustomerId())) {
         throw new BadRequestException("El cupón ya está asignado a un cliente");  
      }
      
      ((CouponsServiceDinosolImpl)service).updateLoyalCustomerId(datosSesionRequest.getDatosSesionBean(), coupon, loyalCustomerId);

      coupon.setLoyalCustomerId(loyalCustomerId);
      
      return coupon;
   } 
}
