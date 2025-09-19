package com.comerzzia.bimbaylola.pos.services.ticket.pagos.tarjeta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.util.format.FormatUtil;

public class ByLDatosRespuestaPagoTarjeta extends DatosRespuestaPagoTarjeta{

	protected String technologyType;
	protected String expDate;
	protected String documentKey;
	protected String documentDescription;
	protected String sessionKey;
	protected String sessionId;
	protected String referenceNumnber;
	protected String chargeType;
	protected String processorKey;
	protected String proccesorDescription;
	protected String bankKey;
	protected String cardHolderName;
	protected String dccConversionRate;
	protected String dccAmount;
	protected String dccCardHolderCurrencyAlfa;
	protected String dccCardHolderCurrencySymbol;
	protected String dccDecimalPlaces;

	protected String tipoTarjeta;

	public ByLDatosRespuestaPagoTarjeta(DatosPeticionPagoTarjeta peticion){
		super(peticion);
	}

	public String getTechnologyType(){
		return technologyType;
	}

	public void setTechnologyType(String technologyType){
		this.technologyType = technologyType;
	}

	public String getExpDate(){
		return expDate;
	}

	public void setExpDate(String expDate){
		this.expDate = expDate;
	}

	public String getDocumentKey(){
		return documentKey;
	}

	public void setDocumentKey(String documentKey){
		this.documentKey = documentKey;
	}

	public String getDocumentDescription(){
		return documentDescription;
	}

	public void setDocumentDescription(String documentDescription){
		this.documentDescription = documentDescription;
	}

	public String getSessionKey(){
		return sessionKey;
	}

	public void setSessionKey(String sessionKey){
		this.sessionKey = sessionKey;
	}

	public String getSessionId(){
		return sessionId;
	}

	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}

	public String getReferenceNumnber(){
		return referenceNumnber;
	}

	public void setReferenceNumnber(String referenceNumnber){
		this.referenceNumnber = referenceNumnber;
	}

	public String getChargeType(){
		return chargeType;
	}

	public void setChargeType(String chargeType){
		this.chargeType = chargeType;
	}

	public String getProcessorKey(){
		return processorKey;
	}

	public void setProcessorKey(String processorKey){
		this.processorKey = processorKey;
	}

	public String getProccesorDescription(){
		return proccesorDescription;
	}

	public void setProccesorDescription(String proccesorDescription){
		this.proccesorDescription = proccesorDescription;
	}

	public String getBankKey(){
		return bankKey;
	}

	public void setBankKey(String bankKey){
		this.bankKey = bankKey;
	}

	public String getCardHolderName(){
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName){
		this.cardHolderName = cardHolderName;
	}

	public String getDccConversionRate(){
		return dccConversionRate;
	}

	public void setDccConversionRate(String dccConversionRate){
		this.dccConversionRate = dccConversionRate;
	}

	public String getDccAmount(){
		return dccAmount;
	}

	public void setDccAmount(String dccAmount){
		this.dccAmount = dccAmount;
	}

	public String getDccCardHolderCurrencyAlfa(){
		return dccCardHolderCurrencyAlfa;
	}

	public void setDccCardHolderCurrencyAlfa(String dccCardHolderCurrencyAlfa){
		this.dccCardHolderCurrencyAlfa = dccCardHolderCurrencyAlfa;
	}

	public String getDccCardHolderCurrencySymbol(){
		return dccCardHolderCurrencySymbol;
	}

	public void setDccCardHolderCurrencySymbol(String dccCardHolderCurrencySymbol){
		this.dccCardHolderCurrencySymbol = dccCardHolderCurrencySymbol;
	}

	public String getDccDecimalPlaces(){
		return dccDecimalPlaces;
	}

	public void setDccDecimalPlaces(String dccDecimalPlaces){
		this.dccDecimalPlaces = dccDecimalPlaces;
	}

	public String getTipoTarjeta(){
		return tipoTarjeta;
	}

	public void setTipoTarjeta(String tipoTarjeta){
		this.tipoTarjeta = tipoTarjeta;
	}
	
	public String getFechaTransaccionAsLocale() throws ParseException {
		Date fecha = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(getFechaTransaccion());
		if (fecha == null) {
			fecha = new Date();
		}
		String fechaTransaccion = FormatUtil.getInstance().formateaFechaCorta(fecha);
		String horaTransaccion = FormatUtil.getInstance().formateaHora(fecha);
		return fechaTransaccion + " " + horaTransaccion;
	}
	
}
