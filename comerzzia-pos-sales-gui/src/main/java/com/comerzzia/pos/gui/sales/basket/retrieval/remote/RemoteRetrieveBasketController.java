package com.comerzzia.pos.gui.sales.basket.retrieval.remote;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.comerzzia.api.documents.client.BasketApiClient;
import com.comerzzia.api.documents.client.model.BasicSaleBasket;
import com.comerzzia.omnichannel.facade.model.basketdocument.SaleBasket;
import com.comerzzia.omnichannel.facade.model.basketdocument.SaleBasketDetail;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.basket.retrieval.ParkedBasketRow;
import com.comerzzia.pos.gui.sales.basket.retrieval.RetrieveBasketControllerAbstract;

import javafx.collections.FXCollections;

@Component
@CzzScene
public class RemoteRetrieveBasketController extends RetrieveBasketControllerAbstract {
	
	@Autowired
	protected BasketApiClient basketApi;
	@Autowired
	protected ModelMapper modelMapper;
	
	protected void loadParkedBasket() {
		ResponseEntity<List<BasicSaleBasket>> remoteBaskets = basketApi.findBasketPage(0, 100, null, null, session.getApplicationSession().getCodAlmacen(), null, docTypes, null, null, null, null, null, null, null, null, null, null, null, null, "%");
		
		parketBasketsList = FXCollections.observableArrayList();
		
		if (remoteBaskets.hasBody()) {
			remoteBaskets.getBody().stream()
					.map(basket -> new ParkedBasketRow(modelMapper.map(basket, SaleBasket.class)))
					.forEach(parkedBasketRow -> parketBasketsList.add(parkedBasketRow));
		}
	}
	
	@Override
	public void acceptSelectedDocument() {
		ParkedBasketRow selectedLine = (ParkedBasketRow)tbBaskets.getSelectionModel().getSelectedItem();
        
        if (selectedLine == null) return;
        
        ResponseEntity<com.comerzzia.api.documents.client.model.SaleBasket> saleBasket = basketApi.getBasketByUid(selectedLine.getBasket().getBasketUid());
        
        if (!saleBasket.hasBody()) {
        	log.error("Cesta no encontrada: " + selectedLine.getBasket().getBasketUid());
        	return;
        }
        
        closeSuccess(modelMapper.map(saleBasket.getBody(), SaleBasketDetail.class));       
	}

}
