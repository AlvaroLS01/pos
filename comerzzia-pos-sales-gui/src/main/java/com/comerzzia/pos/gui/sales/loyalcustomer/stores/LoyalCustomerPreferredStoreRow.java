package com.comerzzia.pos.gui.sales.loyalcustomer.stores;

import com.comerzzia.api.loyalty.client.model.Store;
import com.comerzzia.pos.core.gui.helper.HelperRow;

import javafx.beans.property.SimpleStringProperty;

public class LoyalCustomerPreferredStoreRow extends HelperRow<Store> {

	
	public LoyalCustomerPreferredStoreRow(Store store) {
		object = store;
		helperCode = new SimpleStringProperty(store.getStoreCode());
		helperDesc = new SimpleStringProperty(store.getStoreDes());
	}

	public String getStoreCode() {
		return helperCode.getValue();
	}

	public String getStoreDes() {
		return helperDesc.getValue();
	}
	
}
