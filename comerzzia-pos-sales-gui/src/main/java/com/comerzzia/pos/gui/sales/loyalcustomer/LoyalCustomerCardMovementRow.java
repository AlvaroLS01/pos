package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.math.BigDecimal;
import java.util.Date;

import com.comerzzia.api.loyalty.client.model.AccountTransaction;
import com.comerzzia.api.loyalty.client.model.AccountTransactionStatus;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class LoyalCustomerCardMovementRow {
	
	private SimpleObjectProperty<Date> date;
	private SimpleStringProperty concept;
	private SimpleObjectProperty<BigDecimal> input, output;
	private SimpleObjectProperty<String> status;
	
	public LoyalCustomerCardMovementRow(AccountTransaction transaction){
		this.date = new SimpleObjectProperty<Date>(transaction.getAccountTransactionDate());
		this.concept = new SimpleStringProperty(transaction.getConcept() == null ? "" : transaction.getConcept());
		this.input = new SimpleObjectProperty<BigDecimal>(BigDecimal.ZERO.equals(transaction.getInput()) ? null : transaction.getInput());
		this.output = new SimpleObjectProperty<BigDecimal>(BigDecimal.ZERO.equals(transaction.getOutput()) ? null : transaction.getOutput());
		
		if (AccountTransactionStatus.DEFINITIVE.getValue().equals(transaction.getMovementStatusId())) {
			this.status = new SimpleObjectProperty<String>(I18N.getText("Definitivo"));
		}
		else if (AccountTransactionStatus.PROVISIONAL.getValue().equals(transaction.getMovementStatusId())) {
			this.status = new SimpleObjectProperty<String>(I18N.getText("Provisional"));
		}
		else if (AccountTransactionStatus.CANCELLED.getValue().equals(transaction.getMovementStatusId())) {
			this.status = new SimpleObjectProperty<String>(I18N.getText("Anulado"));
		}
	}
	
	public SimpleStringProperty conceptProperty() {
        return concept;
    }
    
    public String getConcept(){
    	return concept.get();
    }
    
    public SimpleObjectProperty<Date> dateProperty(){
    	return date;
    }
    
    public Date getDate(){
    	return date.get();
    }
    
    public SimpleObjectProperty<BigDecimal> inputProperty(){
    	return input;
    }
    
    public BigDecimal getInput(){
    	return input.get();
    }
    
    public SimpleObjectProperty<BigDecimal> outputProperty(){
    	return output;
    }
    
    public BigDecimal getOutput(){
    	return output.get();
    }

	public String getStatus() {
		return status.get();
	}
}
