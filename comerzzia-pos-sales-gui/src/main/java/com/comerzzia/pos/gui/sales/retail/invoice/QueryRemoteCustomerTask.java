package com.comerzzia.pos.gui.sales.retail.invoice;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.service.sale.customer.CustomerServiceFacade;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryRemoteCustomerTask extends RestBackgroundTask<Customer> {

	@Autowired
	protected CustomerServiceFacade customersService;

	protected String vatNumber;
	protected String identificationTypeCode;
	protected String countryCode;

	public QueryRemoteCustomerTask(String vatNumber, String identificationTypeCode, String countryCode, Callback<Customer> callback, Stage stage) {
		super(callback, stage);
		this.vatNumber = vatNumber;
		this.identificationTypeCode = identificationTypeCode;
		this.countryCode = countryCode;
	}

	@Override
	protected Customer execute() throws Exception {
		return customersService.findRemoteByCountryIdType(vatNumber, identificationTypeCode, countryCode);
	}

	@Override
	protected void handleNotFoundException(Throwable e) {
		String msg;
		if (StringUtils.isNotBlank(identificationTypeCode)) {
			msg = I18N.getText("El cliente con el documento {0}, el tipo de documento {1} y el país {2} no existe en el sistema", vatNumber, identificationTypeCode, countryCode);
		}
		else {
			msg = I18N.getText("El cliente con el documento {0} no existe en el sistema", vatNumber);
		}
		DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(msg);
	}
}
