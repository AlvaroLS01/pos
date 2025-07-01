package com.comerzzia.pos.gui.sales.cashjournal.opening.cashcount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalCount;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodDetail;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.paymentmethod.PaymentMethodButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.cashjournal.counts.CountControllerAbstract;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

@Component
@CzzScene
@Slf4j
public class OpeningCashCountController extends CountControllerAbstract{
	
	@FXML
	protected Label lbTitulo;
	
	@Override
	public void initializeComponents() {
		super.initializeComponents();
		lbTitulo.setText(com.comerzzia.core.commons.i18n.I18N.getText("Apertura de Caja"));
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		try {
			cashJournalSession = session.getCashJournalSession();
			countLines = new ArrayList<>();
			selectedPaymentMethod = session.getApplicationSession().getPaymentMethods().getDefaultPaymentMethod();
			Devices.openCashDrawer();
			screenDataRefresh();
		} catch (Exception e) {
			log.error("onSceneOpen() - Error inesperado inicializando formulario. ", e);
			throw new InitializeGuiException(e);
		}
		
	}
	
	protected String getWebViewPath() {
		return "count/openingcount";
	}
	
	@Override
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if("deleteOpeningCount".equals(method)) {
			log.debug("Entrando en deleteItem handler");
			if(params.containsKey("lineId")) {
				String param = params.get("lineId");
				Integer lineId = Integer.parseInt(param);
				CashJournalCount cajaRecuento = countLines.get(lineId.intValue());
				removeCountLine(cajaRecuento);
				tfAmount.requestFocus();
				screenDataRefresh();
			} else {
				log.debug("addUrlHandlersItemsOperations() - deleteItem: No se ha encontrado la clave propuesta en el mapa de parámetros");
			}
		}
	}
	
	protected void createPaymentMethodButton(List<ButtonConfigurationBean> paymentMethodActionsList, PaymentMethodDetail paymentMethod) {
		if(paymentMethod.getCash() && paymentMethod.getCashPayment() && paymentMethod.getActive()) {
			PaymentMethodButtonConfigurationBean cfg = new PaymentMethodButtonConfigurationBean(null, paymentMethod.getPaymentMethodDes(), null, "ACCION_SELECIONAR_MEDIO_PAGO", "", paymentMethod, false);
			paymentMethodActionsList.add(cfg);
		}
	}

    @FXML
    public void actionAccept() {
    	closeSuccess(countLines);
    }

	@FXML
    public void actionCancel() {
		if(!CollectionUtils.isEmpty(countLines) &&
				!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Al salir de la pantalla se perderá el recuento en curso. ¿Desea continuar?"))) {
			return;
		}
		closeCancel();
    }
	

}
