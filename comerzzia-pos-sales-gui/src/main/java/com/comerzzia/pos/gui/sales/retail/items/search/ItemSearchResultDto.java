package com.comerzzia.pos.gui.sales.retail.items.search;

import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.basket.promotions.BasketLinePromotion;
import com.comerzzia.pos.util.format.FormatUtils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


public class ItemSearchResultDto {
	
    private SimpleStringProperty itemCode;
    private SimpleStringProperty itemDes;
    private SimpleStringProperty combination1Code;
    private SimpleStringProperty combination2Code;
    private SimpleStringProperty pvp;
    private SimpleStringProperty promotionPvp;
    private SimpleStringProperty price;
    private SimpleStringProperty promotionPrice;
    private SimpleObjectProperty<BasketItem> line;
    private SimpleObjectProperty<BasketLinePromotion> promotion;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public ItemSearchResultDto(BasketItem item) {

    	this.itemCode= new SimpleStringProperty(item.getItemCode());
        this.itemDes = new SimpleStringProperty(item.getItemDes());
        this.combination1Code = new SimpleStringProperty(item.getCombination1Code());
        this.combination2Code = new SimpleStringProperty(item.getCombination2Code());
        this.pvp= new SimpleStringProperty(FormatUtils.getInstance().formatAmount(item.getPriceWithTaxesWithoutDiscount()));
        this.promotionPvp= new SimpleStringProperty(FormatUtils.getInstance().formatAmount(item.getPriceWithTaxes()));
        this.price= new SimpleStringProperty(FormatUtils.getInstance().formatAmount(item.getPriceWithoutDiscount()));
        this.promotionPrice= new SimpleStringProperty(FormatUtils.getInstance().formatAmount(item.getPrice()));
        this.line = new SimpleObjectProperty(item);
        
        if (item.getPromotions() != null && item.getPromotions().size() > 0) {
        	this.promotion = new SimpleObjectProperty(item.getPromotions().get(0));
        }
        
    }

    public String getItemCode() {
        return itemCode.getValue();
    }

    public void setItemCode(String itemCode) {
        this.itemCode.setValue(itemCode);
    }

    public SimpleStringProperty getItemCodeProperty() {
        return itemCode;
    }

    public String getDescription() {
        return itemDes.getValue();
    }

    public void setDescription(String description) {
        this.itemDes.setValue(description);
    }

    public SimpleStringProperty getDescriptionProperty() {
        return itemDes;
    }
    
    public String getPvp(){
        return pvp.getValue();
    }
    
    public SimpleStringProperty getPvpProperty(){
        return pvp;
    }
    
    public String getPromotionPvp(){
        return promotionPvp.getValue();
    }
    
    public SimpleStringProperty getPromotionPvpProperty(){
        return promotionPvp;
    }
    
    public String getPrice(){
        return price.getValue();
    }
    
    public SimpleStringProperty getPriceProperty(){
        return price;
    }
    
    public String getPromotionPrice(){
        return promotionPvp.getValue();
    }
    
    public SimpleStringProperty getPromotionPriceProperty(){
        return promotionPrice;
    }

    public SimpleStringProperty getCombination1CodeProperty() {
        return combination1Code;
    }

    public SimpleStringProperty getCombination2CodeProperty() {
        return combination2Code;
    }
    
    public String getCombination1Code() {
        return combination1Code.getValue();
    }

    public String getCombination2Code() {
        return combination2Code.getValue();
    }

	public SimpleObjectProperty<BasketItem> getLine() {
		return line;
	}

	public SimpleObjectProperty<BasketLinePromotion> getPromotion() {
		return promotion;
	}
    
}
