


package com.comerzzia.pos.gui.sales.retailrefund.originalitems;

import java.math.BigDecimal;

import com.comerzzia.omnichannel.facade.model.documents.RefundedItemLine;
import com.comerzzia.omnichannel.facade.model.documents.SaleDocLine;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ProvisionalRefundItem {
    
	protected int lineId;
    protected SaleDocLine originalLine;
    protected RefundedItemLine refundedItemLine;
    protected BigDecimal originalRefundingQuantity;
    protected BigDecimal refundingQuantity;
    
    public ProvisionalRefundItem(SaleDocLine originalLine, RefundedItemLine refundedItemLine, BigDecimal originalRefundingQuantity){
    	lineId = originalLine.getLineId();
        this.originalLine = originalLine;
        this.refundedItemLine   = refundedItemLine;
        this.originalRefundingQuantity = originalRefundingQuantity;
        refundingQuantity = BigDecimal.ZERO;
    }
    
    
    public BigDecimal getOriginalQuantity() {
    	return refundedItemLine.getOriginalQuantity();
    }
    
    public BigDecimal getRefundSalePrice() {
    	return refundedItemLine.getRefundSalePrice();
    }
    
    public BigDecimal getRefundedQuantity() {
    	return refundedItemLine.getRefundedQuantity();
    }
    
    public BigDecimal getAvailableQuantity() {
    	return getAvailableRefundQuantity().subtract(getRefundingQuantity());
    }
    
    public BigDecimal getAvailableRefundQuantity() {
    	return refundedItemLine.getAvailableRefundQuantity();
    }
}
