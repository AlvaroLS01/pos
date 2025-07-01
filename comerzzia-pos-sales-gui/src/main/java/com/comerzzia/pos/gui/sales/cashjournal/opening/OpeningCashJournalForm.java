


package com.comerzzia.pos.gui.sales.cashjournal.opening;


import java.math.BigDecimal;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;
import com.comerzzia.pos.core.gui.validation.validators.date.IsValidDate;
import com.comerzzia.pos.core.gui.validation.validators.number.IsNumeric;
import com.comerzzia.pos.util.format.FormatUtils;

@Component
@Scope("prototype")
public class OpeningCashJournalForm extends ValidationFormGui{   
    //@Past(message = "La fecha de apertura introducida no es válida")
    @NotEmpty(message = "Ha de indicar una fecha de apertura")
    @IsValidDate(isDateTime = false)
    protected String openingDate;
    
    @IsNumeric(isAmount = true)
    @Size(min = 1, max = 10, message = "El saldo ha de tener un valor positivo válido")
    protected String balance;
    
    public OpeningCashJournalForm() {
    }

    public OpeningCashJournalForm(String openingDate, String balance) {
        this.openingDate = openingDate;
        this.balance = balance;
    }

    @Override
    public void clearForm() {
        this.openingDate = "";
        this.balance = "";
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public String getBalance() {
        return balance;
    }

    public BigDecimal getBalanceAsBigDecimal() {
        return FormatUtils.getInstance().parseBigDecimal(balance,2);
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
    
    
    
}
