package com.comerzzia.pos.services.giftcard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.api.rest.client.fidelizados.ResponseGetFidelizadoRest;
import com.comerzzia.api.rest.client.movimientos.ListaMovimientoRequestRest;
import com.comerzzia.api.rest.client.movimientos.MovimientoRequestRest;
import com.comerzzia.api.rest.client.movimientos.MovimientosRest;
import com.comerzzia.core.servicios.variables.Variables;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfigurationImpl;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.cabecera.TarjetaRegaloTicket;
import com.comerzzia.pos.util.config.SpringContext;

import javafx.stage.Stage;

@Component
public class GiftCardService {

	private Logger log = Logger.getLogger(GiftCardService.class);

	@Autowired
	private Sesion sesion;

	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private PaymentsMethodsConfigurationImpl paymentsMethodsConfigurationImpl;

	@SuppressWarnings("rawtypes")
	public void recharge(Stage stage, String uidTransaccion, TarjetaRegaloTicket giftCard, ITicket ticket) throws RestException, RestHttpException {
		log.debug("recharge() - Recharging gift card with number: " + giftCard.getNumTarjetaRegalo() + " and amount: " + ticket.getTotales().getTotal().abs());

		String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);

		MovimientoRequestRest mov = new MovimientoRequestRest();
		mov.setNumeroTarjeta(giftCard.getNumTarjetaRegalo());
		mov.setUidActividad(sesion.getAplicacion().getUidActividad());

		BigDecimal entrada = ticket.getTotales().getTotal().abs();
		mov.setEntrada(entrada.doubleValue());

		mov.setSalida(0.0);
		mov.setConcepto(ticket.getCabecera().getDesTipoDocumento() + " " + ticket.getCabecera().getCodTicket());
		mov.setUidTransaccion(uidTransaccion);
		mov.setFecha(new Date());
		mov.setDocumento(String.valueOf(ticket.getIdTicket()));
		mov.setApiKey(apiKey);
		mov.setSaldo(giftCard.getSaldo().doubleValue());
		mov.setSaldoProvisional(giftCard.getSaldoProvisional().doubleValue());

		MovimientosRest.crearMovimientoTarjetaRegaloProvisional(mov);
	}

	public GiftCardBean getGiftCard(String giftCardNumber) throws RestException, RestHttpException {
		String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);
		
		ConsultarFidelizadoRequestRest paramConsulta = new ConsultarFidelizadoRequestRest(apiKey, sesion.getAplicacion().getUidActividad());
		GiftCardBean gitfCard = SpringContext.getBean(GiftCardBean.class);
		paramConsulta.setNumeroTarjeta(giftCardNumber);

		ResponseGetFidelizadoRest result = FidelizadosRest.getTarjetaRegalo(paramConsulta);
		gitfCard.setNumTarjetaRegalo(result.getNumeroTarjeta());
		gitfCard.setBaja(result.getBaja().equals("S"));
		gitfCard.setActiva(result.getActiva().equals("S"));
		gitfCard.setSaldoProvisional(BigDecimal.ZERO);
		gitfCard.setSaldo(BigDecimal.valueOf(result.getSaldo()));
		gitfCard.setSaldoProvisional(BigDecimal.valueOf(result.getSaldoProvisional()));
		gitfCard.setCodTipoTarjeta(result.getTipoTarjeta() != null ? result.getTipoTarjeta().getCodtipotarj() : null);

		return gitfCard;
	}

	public boolean isGiftCardItem(String codArticulo) {
		for(PaymentMethodConfiguration paymentMehtodConfiguration : paymentsMethodsConfigurationImpl.getPaymentsMethodsConfiguration()) {
			String itemCodes = paymentMehtodConfiguration.getConfigurationProperty("articulos_gift_card");
			if(StringUtils.isNotBlank(itemCodes)) {
				String[] itemCodesSplit = itemCodes.split(",");
				for(int i = 0 ; i < itemCodesSplit.length ; i++) {
					String itemCodeGiftCard = itemCodesSplit[i];
					itemCodeGiftCard = itemCodeGiftCard.trim();
					if(itemCodeGiftCard.equals(codArticulo)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

}
