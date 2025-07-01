
package com.comerzzia.pos.gui.sales.customer;

import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.service.sale.customer.CustomerServiceFacade;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.gui.sales.retail.customer.CustomerDto;
import com.comerzzia.pos.util.config.SpringContext;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CustomerManagementTasks {

	protected static class DeleteCustomerTask extends BackgroundTask<Void>{
		protected Customer customer;
		protected Stage stage;
		protected ObservableList<CustomerDto> list;
		protected Callback<Boolean, Void> callback;

		public DeleteCustomerTask(Customer customer, ObservableList<CustomerDto> list, Stage stage, Callback<Boolean, Void> callback) {
			this.customer = customer;
			this.list = list;
			this.stage = stage;
			this.callback = callback;
		}
		
		@Override
		protected Void execute() throws Exception {
			SpringContext.getBean(CustomerServiceFacade.class).delete(customer.getCustomerCode());
			return null;
		}
		
		@Override
		protected void failed() {
			Throwable e =  getException();
			DialogWindowBuilder.getBuilder(stage).simpleErrorDialog(e.getMessage());
			super.failed();
			if(callback != null)
				callback.call(false);
		}
		
		@Override
		protected void succeeded() {			
			for (CustomerDto customerRow : list) {
				if (customerRow.getCustomer().getCustomerCode().equals(customer.getCustomerCode())) {
					list.remove(customerRow);
					break;
				}
			}

			super.succeeded();
			if(callback != null)
				callback.call(true);
		}
		
	}
	
	public static void executeDeleteTask(Customer customer, ObservableList<CustomerDto> list, Stage stage, Callback<Boolean, Void> callback){
		new DeleteCustomerTask(customer, list, stage, callback).start();
	}
	
}
