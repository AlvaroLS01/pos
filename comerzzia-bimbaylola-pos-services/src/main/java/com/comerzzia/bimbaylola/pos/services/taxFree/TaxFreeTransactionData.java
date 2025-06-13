package com.comerzzia.bimbaylola.pos.services.taxFree;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.CustomerData;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.EncryptionMethodDetail;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.LineItem;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.MerchantData;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.OperatorInfo;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.PaymentMethodDetail;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.TaxFreeVersion;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.Terminal;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.TransactionHeader;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.TransactionTotals;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "taxfree_transaction_data")
@Component
@Primary
@Scope("prototype")
public class TaxFreeTransactionData {

    @XmlElement(name = "version")
	protected TaxFreeVersion version;
    
    @XmlElement(name = "merchant_data")
	protected MerchantData merchant_data;
    
    @XmlElement(name = "terminal")
	protected Terminal terminal;
    
    @XmlElement(name = "operator_info")
	protected OperatorInfo operator_info;
    
    @XmlElement(name = "transaction_header")
	protected TransactionHeader transaction_header;
    
    @XmlElementWrapper(name = "invoice_line_items")
    @XmlElement(name = "line_item")
    protected List<LineItem> invoice_line_items;
    
    @XmlElement(name = "transaction_totals")
	protected TransactionTotals transaction_totals;
    
    @XmlElementWrapper(name = "payment_method_details")
    @XmlElement(name = "payment_method_detail")
    protected List<PaymentMethodDetail> payment_method_details;
    
    @XmlElement(name = "encryption_method_detail")
	protected EncryptionMethodDetail encryption_method_detail;
    
    @XmlElement(name = "customer_data")
	protected CustomerData customer_data;

	public TaxFreeVersion getVersion() {
		return version;
	}

	public void setVersion(TaxFreeVersion version) {
		this.version = version;
	}

	public MerchantData getMerchant_data() {
		return merchant_data;
	}

	public void setMerchant_data(MerchantData merchant_data) {
		this.merchant_data = merchant_data;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	public OperatorInfo getOperator_info() {
		return operator_info;
	}

	public void setOperator_info(OperatorInfo operator_info) {
		this.operator_info = operator_info;
	}

	public TransactionHeader getTransaction_header() {
		return transaction_header;
	}

	public void setTransaction_header(TransactionHeader transaction_header) {
		this.transaction_header = transaction_header;
	}

	public List<LineItem> getInvoice_line_items() {
		return invoice_line_items;
	}

	public void setInvoice_line_items(List<LineItem> invoice_line_items) {
		this.invoice_line_items = invoice_line_items;
	}

	public TransactionTotals getTransaction_totals() {
		return transaction_totals;
	}

	public void setTransaction_totals(TransactionTotals transaction_totals) {
		this.transaction_totals = transaction_totals;
	}

	public List<PaymentMethodDetail> getPayment_method_details() {
		return payment_method_details;
	}

	public void setPayment_method_details(List<PaymentMethodDetail> payment_method_details) {
		this.payment_method_details = payment_method_details;
	}

	public EncryptionMethodDetail getEncryption_method_detail() {
		return encryption_method_detail;
	}

	public void setEncryption_method_detail(EncryptionMethodDetail encryption_method_detail) {
		this.encryption_method_detail = encryption_method_detail;
	}

	public CustomerData getCustomer_data() {
		return customer_data;
	}

	public void setCustomer_data(CustomerData customer_data) {
		this.customer_data = customer_data;
	}

}
