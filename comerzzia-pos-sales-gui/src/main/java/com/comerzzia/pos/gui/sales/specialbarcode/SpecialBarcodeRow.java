package com.comerzzia.pos.gui.sales.specialbarcode;

import com.comerzzia.omnichannel.facade.model.catalog.ItemSpecialBarcodeConfig;

import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

public class SpecialBarcodeRow {

	@Getter
	protected ItemSpecialBarcodeConfig barcode;
	protected SimpleStringProperty description, prefix, itemCode, documentCode, price, quantity, loyalty;

	public SpecialBarcodeRow(ItemSpecialBarcodeConfig barcode) {
		this.barcode = barcode;
		this.description = new SimpleStringProperty(barcode.getDescription());
		this.prefix = new SimpleStringProperty(barcode.getPrefix());
		this.itemCode = new SimpleStringProperty(barcode.getItemCode());
		this.documentCode = new SimpleStringProperty(barcode.getDocumentCode());
		this.price = new SimpleStringProperty(barcode.getPrice());
		this.quantity = new SimpleStringProperty(barcode.getQuantity());
		this.loyalty = new SimpleStringProperty(barcode.getLoyalty() ? "S" : "N");
	}
	
	public SimpleStringProperty getItemCodeProperty() {
		return itemCode;
	}

	public void setItemCodeProperty(SimpleStringProperty itemCode) {
		this.itemCode = itemCode;
	}

	public SimpleStringProperty getDescriptionProperty() {
		return description;
	}

	public void setDescriptionProperty(SimpleStringProperty description) {
		this.description = description;
	}

	public SimpleStringProperty getLoyaltyProperty() {
		return loyalty;
	}

	public void setLoyaltyProperty(SimpleStringProperty loyalty) {
		this.loyalty = loyalty;
	}

	public SimpleStringProperty getPrefixProperty() {
		return prefix;
	}

	public void setPrefixProperty(SimpleStringProperty prefix) {
		this.prefix = prefix;
	}

	public SimpleStringProperty getQuantityProperty() {
		return quantity;
	}

	public void setQuantityProperty(SimpleStringProperty quantity) {
		this.quantity = quantity;
	}

	public SimpleStringProperty getPriceProperty() {
		return price;
	}

	public void setPriceProperty(SimpleStringProperty price) {
		this.price = price;
	}

	public SimpleStringProperty getDocumentCodeProperty() {
		return documentCode;
	}

	public void setDocumentCodeProperty(SimpleStringProperty documentCode) {
		this.documentCode = documentCode;
	}
	
}
