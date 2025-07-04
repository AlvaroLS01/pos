package com.comerzzia.cardoso.pos.services.devoluciones.referenciadas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.request.CancelPaymentSpecificInput;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.request.CancelRequest;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.response.CancelResponse;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.request.RequestDTO;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class CancelDevolucionReferenciadaTask extends RestBackgroundTask<CancelResponse> {

	@Autowired
	private DevolucionesReferenciadasService devolucionesReferenciadasService;
	
	RequestDTO cancelRequestDTO;
	
	public CancelDevolucionReferenciadaTask(RequestDTO cancelRequestDTO, Callback<CancelResponse> callback, Stage stage) {
		super(callback, stage);
		this.cancelRequestDTO = cancelRequestDTO;
	}

	@Override
	protected CancelResponse call() throws Exception {
		CancelRequest cancelRequest = new CancelRequest();
		CancelPaymentSpecificInput cancelPaymentSpecificInput = new CancelPaymentSpecificInput();
		cancelPaymentSpecificInput.setReason("CANCELLED_BY_CARDHOLDER");
		cancelRequest.setCancelPaymentSpecificInput(cancelPaymentSpecificInput);
		return devolucionesReferenciadasService.cancelRefund(cancelRequestDTO, cancelRequest);
	}
}
	

