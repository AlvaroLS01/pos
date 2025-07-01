


package com.comerzzia.pos.gui.sales.cashjournal.close;

import java.math.BigDecimal;

import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalSummaryByPaymentCode;


public class CloseCashJournalDto {
     protected CashJournalSummaryByPaymentCode cashJournalSummary;

    public CloseCashJournalDto(CashJournalSummaryByPaymentCode cashJournalSummary) {
        this.cashJournalSummary = cashJournalSummary;
    }

    public String getPaymentMethod() {
        return  (cashJournalSummary!=null ? cashJournalSummary.getPaymentMethod().getPaymentMethodDes() : null);
    }

    public BigDecimal getInput() {
        return (cashJournalSummary!=null ? cashJournalSummary.getInput() : null);
    }

    public BigDecimal getOutput() {
        return (cashJournalSummary!=null ? cashJournalSummary.getOutput() : null);
    }

    public BigDecimal getCount() {
        return (cashJournalSummary!=null ? cashJournalSummary.getCount() : null);
    }

    public BigDecimal getTotal() {
        return (cashJournalSummary!=null ? cashJournalSummary.getTotal() : null);
    }

    public BigDecimal getMismatch() {
        return (cashJournalSummary!=null ? cashJournalSummary.getCount().subtract(getTotal()) : null);
    }

     
     
}
