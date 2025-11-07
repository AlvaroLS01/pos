package com.comerzzia.api.loyalty.web.rest.accounts;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.api.loyalty.persistence.accounts.Account;
import com.comerzzia.api.loyalty.persistence.accounts.AccountDTO;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTO;
import com.comerzzia.api.loyalty.service.accounts.AccountsService;
import com.comerzzia.api.loyalty.service.accounts.dto.BalanceToCouponRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/accounts/")
@Tag(name = "Accounts", description = "Accounts services")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class AccountsResource {
	@Resource(name = "datosSesionRequest")
   ComerzziaDatosSesion datosSesionRequest;
	
	@Autowired
	AccountsService service;
	
	@GET
	@Path("/{accountId}")
	@Operation( summary = "Get account from key",
			    description = "Get account from key",
                responses = { @ApiResponse(description = "The account record"), 
		                      @ApiResponse(responseCode = "404", description = "Record not found") })
	public AccountDTO getAccountById(@PathParam("accountId") 
                                   @Parameter(description = "The account id") 
	                               Long accountId) throws ApiException {
	   AccountDTO record = service.selectDTOById(accountId, datosSesionRequest.getDatosSesionBean());
	   
	   if (record == null) {
	      throw new NotFoundException();
	   }
	   
		return service.selectDTOById(accountId, datosSesionRequest.getDatosSesionBean());
	}
	
	@POST	
	@Operation( summary = "Insert record",
			    responses = { @ApiResponse(description = "Inserted record"), 
			                  @ApiResponse(responseCode = "400", description = "Invalid input data"),
			                  @ApiResponse(responseCode = "409", description = "Invalid input data")})
	public Account newAccount(@PathParam("balance") @Parameter(description = "The account balance") Double balance, 
							@PathParam("provisionalBalance") @Parameter(description = "The provisional account balance") Double provisionalBalance) throws ApiException {
		return service.insert(datosSesionRequest.getDatosSesionBean(), balance, provisionalBalance);		
	}
	
	@PUT
	@Operation( summary = "Modify record",
			    responses = { @ApiResponse(description = "Modified record"), 
			                  @ApiResponse(responseCode = "400", description = "Invalid input data"), 
			                  @ApiResponse(responseCode = "404", description = "Record not found"),
			                  @ApiResponse(responseCode = "409", description = "Invalid input data") })
	public Account updateAccount(Account record) throws ApiException {
		return service.updateByPrimaryKey(record, datosSesionRequest.getDatosSesionBean());
	}
	
	@PUT
	@Path("/{accountId}/updateBalance")
	@Operation( summary = "Modify account balance",
			    responses = { @ApiResponse(description = "Modified balance"), 
			                  @ApiResponse(responseCode = "400", description = "Invalid input data"), 
			                  @ApiResponse(responseCode = "404", description = "Record not found"),
			                  @ApiResponse(responseCode = "409", description = "Invalid input data") })
	public void updateBalance(@PathParam("accountId") @Parameter(description = "The account id") Long accountId) throws ApiException {
		service.updateBalance(accountId, null, datosSesionRequest.getDatosSesionBean());
	}
	
	@PUT
	@Path("/{accountId}/deactivate")
	@Operation( summary = "Deactivate account",
			    responses = { @ApiResponse(description = "Account deactivated"), 
			                  @ApiResponse(responseCode = "400", description = "Invalid input data"), 
			                  @ApiResponse(responseCode = "404", description = "Record not found"),
			                  @ApiResponse(responseCode = "409", description = "Invalid input data") })
	public void deactivate(@PathParam("accountId") @Parameter(description = "The account id") Long accountId) throws ApiException {
		service.deactivate(accountId, datosSesionRequest.getDatosSesionBean());
	}

   @POST
   @Path("/{accountId}/convertBalanceToCoupon")
   @Operation( summary = "Converts balance to coupon",
             responses = { @ApiResponse(description = "Coupon created"), 
                           @ApiResponse(responseCode = "400", description = "Invalid input data"), 
                           @ApiResponse(responseCode = "404", description = "Record not found") })
   public CouponDTO convertBalanceToCoupon(@PathParam("accountId") 
                                      @Parameter(description = "The account id") 
                                      Long accountId,
                                      @Valid
                                      @Parameter(description = "Convert balance request data")
                                      BalanceToCouponRequestDTO balanceToCouponRequestDTO) throws ApiException {
      // exists control
      getAccountById(accountId);
      
      // assign account id
      balanceToCouponRequestDTO.setAccountId(accountId);
      
      return service.convertBalanceToCoupon(datosSesionRequest.getDatosSesionBean(), balanceToCouponRequestDTO);
   }
}
