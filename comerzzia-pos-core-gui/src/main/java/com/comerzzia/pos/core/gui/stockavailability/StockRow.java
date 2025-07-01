package com.comerzzia.pos.core.gui.stockavailability;

import java.math.BigDecimal;

import com.comerzzia.catalog.facade.model.CatalogStoreGeolocated;
import com.comerzzia.catalog.facade.model.CatalogStoreStock;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.number.NumberUtils;

import javafx.beans.property.SimpleStringProperty;

public class StockRow {
	
	protected SimpleStringProperty storeCode, storeDes, province, postalCode, combination1Code, combination2Code, stock, stockA, stockB, stockC, stockD, logisticStock;
	protected CatalogStoreGeolocated store;
	
	public StockRow(CatalogStoreGeolocated store, CatalogStoreStock storeStock){
		this.store = store;
		this.storeCode = new SimpleStringProperty(store.getStoreCode() == null ? "" : store.getStoreCode());
		this.storeDes = new SimpleStringProperty(store.getStoreDes() == null ? "" : store.getStoreDes() + (store.getDistance() == null ? "" : " (" + formatDistance() + ")"));
		this.province = new SimpleStringProperty(store.getProvince() == null ? "" : store.getProvince());
		this.postalCode = new SimpleStringProperty(store.getPostalCode() == null ? "" : store.getPostalCode());

		this.combination1Code = new SimpleStringProperty(storeStock.getCombination1() == null ? "" : storeStock.getCombination1());
		this.combination2Code = new SimpleStringProperty(storeStock.getCombination2() == null ? "" : storeStock.getCombination2());
		BigDecimal stock = new BigDecimal(storeStock.getStock());
		if (storeStock.getStockPendServe() != null) {
			stock = stock.subtract(new BigDecimal(storeStock.getStockPendServe()));
		}
		this.stock = new SimpleStringProperty(FormatUtils.getInstance().formatNumber(stock, 3));
		
		if (storeStock.getStockA() != null) {
			BigDecimal stockA = new BigDecimal(storeStock.getStockA());
			this.stockA = new SimpleStringProperty(FormatUtils.getInstance().formatNumber(stockA, 3));
		}

		if (storeStock.getStockB() != null) {
			BigDecimal stockB = new BigDecimal(storeStock.getStockB());
			this.stockB = new SimpleStringProperty(FormatUtils.getInstance().formatNumber(stockB, 3));
		}

		if (storeStock.getStockC() != null) {
			BigDecimal stockC = new BigDecimal(storeStock.getStockC());
			this.stockC = new SimpleStringProperty(FormatUtils.getInstance().formatNumber(stockC, 3));
		}

		if (storeStock.getStockD() != null) {
			BigDecimal stockD = new BigDecimal(storeStock.getStockD());
			this.stockD = new SimpleStringProperty(FormatUtils.getInstance().formatNumber(stockD, 3));
		}
	}
	
	public SimpleStringProperty getStoreCodeProperty() {
		return storeCode;
	}
	
	public String getStoreCode(){
		return storeCode.get();
	}
	
	public SimpleStringProperty getStoreDesProperty() {
		return storeDes;
	}
	
	public String getStoreDes(){
		return storeDes.get();
	}
	
	public SimpleStringProperty getCombination1CodeProperty() {
		return combination1Code;
	}
	
	public String getCombination1Code(){
		return combination1Code.get();
	}
	
	public SimpleStringProperty getCombination2CodeProperty() {
		return combination2Code;
	}
	
	public String getCombination2Code(){
		return combination2Code.get();
	}
	
	public SimpleStringProperty getProvinceProperty() {
		return province;
	}
	
	public String getProvincia(){
		return province.get();
	}
	
	public SimpleStringProperty getPostalCodeProperty() {
		return postalCode;
	}
	
	public String getPostalCode(){
		return postalCode.get();
	}
	
	public SimpleStringProperty getStockProperty() {
		return stock;
	}
	
	public SimpleStringProperty getStockAProperty() {
		return stockA;
	}
	
	public SimpleStringProperty getStockBProperty() {
		return stockB;
	}
	
	public SimpleStringProperty getStockCProperty() {
		return stockC;
	}
	
	public SimpleStringProperty getStockDProperty() {
		return stockD;
	}
	
	public void setLogisticStock(Double logisticStock) {
		if(logisticStock != null) {
			this.logisticStock = new SimpleStringProperty(FormatUtils.getInstance().formatNumber(new BigDecimal(logisticStock), 3));
		}
	}
	
	public SimpleStringProperty getLogisticStockProperty() {
		return logisticStock;
	}

	public String getStock() {
		return stock.get();
	}
	
	protected String formatDistance(){
		String distancia = "";
		Double dist = store.getDistance().doubleValue();
		if (dist < 1.0) {
			dist = dist * 1000;
			distancia = String.valueOf(dist) + "m";
		}
		else if (dist >= 1.0 && dist < 10.0) {
			distancia = NumberUtils.format(dist, 1) + "km";
		}
		else {
			distancia = NumberUtils.format(dist, 0) + "km";
		}
		return distancia;
	}

	public CatalogStoreGeolocated getStore() {
		return store;
	}

}
