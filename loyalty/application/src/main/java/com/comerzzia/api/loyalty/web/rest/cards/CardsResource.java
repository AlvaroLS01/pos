package com.comerzzia.api.loyalty.web.rest.cards;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.api.loyalty.persistence.cards.Card;
import com.comerzzia.api.loyalty.persistence.cards.CardDTO;
import com.comerzzia.api.loyalty.persistence.cards.CardExample;
import com.comerzzia.api.loyalty.persistence.cards.CardKey;
import com.comerzzia.api.loyalty.persistence.cards.CardUK;
import com.comerzzia.api.loyalty.service.cards.CardsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/cards/")
@Tag(name = "Cards", description = "Cards services")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
//@Timed
public class CardsResource {

	@Resource(name = "datosSesionRequest")
    ComerzziaDatosSesion datosSesionRequest;
	
	@Autowired
	CardsService service;
	
	@GET
	@Path("/{cardId}")
	@Operation( summary = "Get card from key",
			    description = "Get card from key",
                responses = { @ApiResponse(description = "The card record"), 
		                      @ApiResponse(responseCode = "404", description = "Record not found") })
	public CardDTO getCardById(@PathParam("cardId") 
                                   @Parameter(description = "The card id") 
	                               Long cardId) throws ApiException {
		return service.selectDTOByPrimaryKey(new CardKey(datosSesionRequest.getDatosSesionBean(), cardId));
	}
	
	@GET
	@Path("/cardnumber/{cardNumber}")
	@Operation( summary = "Get card from card number",
			    description = "Get card from card number",
                responses = { @ApiResponse(description = "The card record"), 
		                      @ApiResponse(responseCode = "404", description = "Record not found") })	
	public CardDTO getCardByNumber(@PathParam("cardNumber") 
                                   @Parameter(description = "The card number") 
	                               String cardNumber) throws ApiException {
		CardUK key = new CardUK(datosSesionRequest.getDatosSesionBean(), cardNumber);
		
		return service.selectDTOByUniqueKey(key);
	}	
	
	@GET
	@Operation( summary = "Returns all cards",
			    description = "returns all cards",
                responses = { @ApiResponse(description = "The cards list") })
	//@Timed(extraTags = { "region", "us-east-1", "exception", "none", "method", "GET", "outcome", "none", "status", "ok" })	
	//@Timed( value = "getAllCards", longTask = true) 	
	public List<CardDTO> getAllCards() throws ApiException {
		CardExample example = new CardExample(datosSesionRequest.getDatosSesionBean());
		
		return service.selectDTOByExample(example);
	}
	
	@POST	
	@Operation( summary = "Insert record",
			    responses = { @ApiResponse(description = "Inserted record"), 
			                  @ApiResponse(responseCode = "400", description = "Invalid input data"),
			                  @ApiResponse(responseCode = "409", description = "Invalid input data")})
	public Card newCard(Card record) throws ApiException {
		record.setDatosSesion(datosSesionRequest.getDatosSesionBean());
		service.insert(record);		
		
		return record;
	}
	
	@PUT
	@Operation( summary = "Modify record",
			    responses = { @ApiResponse(description = "Modified record"), 
			                  @ApiResponse(responseCode = "400", description = "Invalid input data"), 
			                  @ApiResponse(responseCode = "404", description = "Record not found"),
			                  @ApiResponse(responseCode = "409", description = "Invalid input data") })
	public Card updateCard(Card record) throws ApiException {
		record.setDatosSesion(datosSesionRequest.getDatosSesionBean());
		
		service.updateByPrimaryKey(record);
				
		return record;
	}
	
	@PUT
	@Path("/{cardId}/deactivate")
	@Operation( summary = "deactivate record",
			    responses = { @ApiResponse(responseCode = "204", description = "Empty response"), 
			                  @ApiResponse(responseCode = "404", description = "Record not found") })
	public Response deactivate(@PathParam("cardId") 
                             @Parameter(description = "The card id") 
                             Long cardId) throws ApiException {
	    CardDTO card = getCardById(cardId);
	    
		service.deactivate(card);		
				
   	    return Response.ok().status(Status.NO_CONTENT).build();
	}

}
