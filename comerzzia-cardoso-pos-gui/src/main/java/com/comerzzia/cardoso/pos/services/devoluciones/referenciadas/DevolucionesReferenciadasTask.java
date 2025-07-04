package com.comerzzia.cardoso.pos.services.devoluciones.referenciadas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.ApproveResponse;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.request.RequestDTO;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class DevolucionesReferenciadasTask extends RestBackgroundTask<ApproveResponse> {

	@Autowired
	private DevolucionesReferenciadasService devolucionesReferenciadasService;
	
	RequestDTO refundRequestDTO;

	public DevolucionesReferenciadasTask(RequestDTO refundRequestDTO, Callback<ApproveResponse> callback, Stage stage) {
		super(callback, stage);
		this.refundRequestDTO = refundRequestDTO;
	}

	@Override
	protected ApproveResponse call() throws Exception {
		return devolucionesReferenciadasService.devolucionApproveRefund(refundRequestDTO);
		
	}
}
