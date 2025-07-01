package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.math.BigDecimal;
import java.util.Date;

import com.comerzzia.api.loyalty.client.model.LyCustomerPurchaseDetail;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class LoyalCustomerSaleRow {
	
	protected SimpleObjectProperty<Date> date;
	protected SimpleStringProperty item;
	protected SimpleStringProperty description;
	protected SimpleStringProperty combination1;
	protected SimpleStringProperty combination2;
	protected SimpleObjectProperty<BigDecimal> quantity;
	protected SimpleObjectProperty<BigDecimal> amount;
	protected SimpleStringProperty storeCode;
	
	public LoyalCustomerSaleRow(LyCustomerPurchaseDetail purchaseDetail){
		this.date = new SimpleObjectProperty<Date>(purchaseDetail.getSalesDocDate());
		this.item = new SimpleStringProperty(purchaseDetail.getItemCode() == null ? "" : purchaseDetail.getItemCode());
		this.description = new SimpleStringProperty(purchaseDetail.getItemDes() == null ? "" : purchaseDetail.getItemDes());
		this.combination1 = new SimpleStringProperty(purchaseDetail.getCombination1Code() == null ? "" : purchaseDetail.getCombination1Code());
		this.combination2 = new SimpleStringProperty(purchaseDetail.getCombination2Code() == null ? "" : purchaseDetail.getCombination2Code());
		this.quantity = new SimpleObjectProperty<BigDecimal>(purchaseDetail.getQuantity());
		this.amount = new SimpleObjectProperty<BigDecimal>(purchaseDetail.getSalesPrice());
		this.storeCode = new SimpleStringProperty(purchaseDetail.getStoreCode() == null ? "" : purchaseDetail.getStoreCode());
	}
	
	public SimpleObjectProperty<Date> dateProperty() {
		return date;
	}
	
	public Date getDate(){
		return date.get();
	}
	
	public SimpleStringProperty itemProperty() {
		return item;
	}
	
	public String getItem(){
		return item.get();
	}
	
	public SimpleStringProperty descriptionProperty() {
		return description;
	}
	
	public String getDescription(){
		return description.get();
	}
	
	public SimpleStringProperty combination1Property() {
		return combination1;
	}
	
	public String getCombination1(){
		return combination1.get();
	}
	
	public SimpleStringProperty combination2Property() {
		return combination2;
	}
	
	public String getCombination2(){
		return combination2.get();
	}
	
	public SimpleObjectProperty<BigDecimal> quantityProperty() {
		return quantity;
	}
	
	public BigDecimal getQuantity(){
		return quantity.get();
	}
	
	public SimpleObjectProperty<BigDecimal> amountProperty() {
		return amount;
	}
	
	public BigDecimal getAmount(){
		return amount.get();
	}
	
	public SimpleStringProperty storeCodeProperty() {
		return storeCode;
	}
	
	public String getStoreCode(){
		return storeCode.get();
	}

}
