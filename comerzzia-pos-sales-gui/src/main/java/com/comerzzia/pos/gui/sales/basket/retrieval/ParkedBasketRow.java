package com.comerzzia.pos.gui.sales.basket.retrieval;

import java.util.Date;

import com.comerzzia.omnichannel.facade.model.basketdocument.SaleBasket;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;

import lombok.Data;


@Data
public class ParkedBasketRow {
    private Date date;
    protected String amount; 
    protected String itemsCount; 
    protected String alias;
    protected SaleBasket basket;
    protected String till;
    
    public ParkedBasketRow(SaleBasket basket){
        
        this.amount = FormatUtils.getInstance().formatNumber(basket.getGrandAmount(),2);
        this.alias = basket.getAlias() != null ? basket.getAlias() : "";
        this.date = basket.getLastUpdate();
        this.basket = basket;
        this.till = basket.getTillCode();
        this.itemsCount = basket.getItemsCount() != null ? basket.getItemsCount().toString() : "-";
    }
        
}
