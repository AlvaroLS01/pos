
package com.comerzzia.pos.core.services.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.exception.BadRequestException;
import com.comerzzia.core.commons.i18n.I18N;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalCount;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalDocumentIssued;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalHdr;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalLine;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalSummaryByPaymentCode;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalTotals;
import com.comerzzia.omnichannel.facade.model.cashjournal.NewCashJournalManualMove;
import com.comerzzia.omnichannel.facade.model.documents.PrintDocumentRequest;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodDetail;
import com.comerzzia.omnichannel.facade.model.payments.StorePosPaymentMethods;
import com.comerzzia.omnichannel.facade.service.cashjournal.CashJournalServiceFacade;
import com.comerzzia.omnichannel.facade.service.store.StorePosPaymentMethodServiceFacade;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

import javafx.beans.property.SimpleObjectProperty;

@Component
public class CashJournalSession extends Observable{
    protected static final Logger log = Logger.getLogger(CashJournalSession.class.getName());
    
    public static final String OPENING_CONCEPT_CODE = "00"; //CODIGO_CONCEPTO_APERTURA
        
    @Autowired
    protected StorePosPaymentMethodServiceFacade storePaymentsMethodService;
    
    @Autowired
    protected VariableServiceFacade variableService;
    
    @Autowired
    protected CashJournalServiceFacade cashJournalService;
        
    @Autowired
    protected ApplicationSession applicationSession;
        
    protected SimpleObjectProperty<CashJournalHdr> openedCashJournal = new SimpleObjectProperty<>();
    	
    protected CashJournalSession() {
    	openedCashJournal.addListener(changed -> {
        	setChanged();
        	notifyObservers(this);
        });
    }

    public void init() throws SesionInitException {
    	
    	try {
    		openedCashJournal.set(cashJournalService.findOpenedCashJournal(applicationSession.getStorePosBusinessData().getStorePos().getStoreCode(), 
        			applicationSession.getStorePosBusinessData().getStorePos().getTillCode()));
        	if (getOpenedCashJournal() == null) {
        		log.info("init() - No existe caja abierta. ");
        	}
        	
        }
        catch (Exception ex) {
            log.error("init() - Error inicializando registro de caja: " + ex.getMessage(), ex);
            throw new SesionInitException(ex);
        }
    }

    public CashJournalDocumentIssued<?> closeCashJournal(Date fechaCierre, Map<String, Object> extensions, PrintDocumentRequest printSettings) {
        log.debug("guardarCierreCaja() - Cerrando caja ... ");
        getOpenedCashJournal().setClosingDate(fechaCierre);
        
        return cashJournalService.closeCashJournal(getOpenedCashJournal().getCashJournalUid(), fechaCierre, extensions, printSettings);     
    }
    
    public void resetCashJournal(){
        openedCashJournal.set(null);
    }

    public void openAutomaticCashJournal() {
    	openManualCashJournal(new Date(), BigDecimal.ZERO);        
    }

    public void openManualCashJournal(Date fecha, BigDecimal importe) {
    	ArrayList<CashJournalCount> countLines = new ArrayList<>();
    	CashJournalCount directAmount = new CashJournalCount();
    	directAmount.setLineId(1);
    	directAmount.setPaymentMethodCode(applicationSession.getStorePosBusinessData().getDefaultPymtMethod().getPaymentMethodCode());
    	directAmount.setQuantity(1);
    	directAmount.setCountValue(importe);
		countLines.add(directAmount);
		
		openManualCashJournal(fecha, countLines, null, null);        
    }
    
    public CashJournalHdr getOpenedCashJournal() {
        return openedCashJournal.get();
    }
    
    public SimpleObjectProperty<CashJournalHdr> openedCashJournalProperty(){
    	return openedCashJournal;
    }

    public boolean isOpenedCashJournal() {
        return getOpenedCashJournal() != null;
    }

    public String getCashJournalUid() {
    	if (isOpenedCashJournal()) return getOpenedCashJournal().getCashJournalUid();
    	else return null;
    }
    
    public boolean hasMismatch() {
		BigDecimal descuadreMax = variableService.getVariableAsBigDecimal(VariableServiceFacade.CAJA_IMPORTE_MAXIMO_DESCUADRE);
		
		CashJournalTotals totals = virtualCashJournalCount();
					
		List<CashJournalSummaryByPaymentCode> summaryList = new ArrayList<>(totals.getSummaryByPaymentCode().values());
		
		BigDecimal descuadre = summaryList.stream()
				                          .map(summary -> summary.getBalance())
				                          .reduce(BigDecimal.ZERO, BigDecimal::add);
		
		return (BigDecimalUtil.isGreater(descuadre.abs(), descuadreMax));
	}
    
    public void saveCashJournalCount(List<CashJournalCount> countLines) {	
    	cashJournalService.updateCashJournalCount(getOpenedCashJournal().getCashJournalUid(), countLines);
    }
    
	
    public CashJournalTotals virtualCashJournalCount() {
		// Si la caja no está abierta, no hay que hacer nada
		if (!isOpenedCashJournal()) {
			return null;
		}
		
		StorePosPaymentMethods paymentsMethods = storePaymentsMethodService.getPaymentMethodsFromCache(getOpenedCashJournal().getStoreCode(), getOpenedCashJournal().getTillCode());
		
		Set<String> automaticCountPaymentMethods = paymentsMethods.getPaymentsMethods().values()
		        .stream()
		        .filter(paymentMethod -> !paymentMethod.getManual() || paymentMethod.getAutoCashJournalCount())
		        .map(PaymentMethodDetail::getPaymentMethodCode)
		        .collect(Collectors.toSet());

		CashJournalTotals totals = cashJournalService.findTotalsById(getOpenedCashJournal().getCashJournalUid());

		for (CashJournalSummaryByPaymentCode paymentMethodTotal : totals.getSummaryByPaymentCode().values()) {
			if (automaticCountPaymentMethods.contains(paymentMethodTotal.getPaymentMethod().getPaymentMethodCode())) {
				paymentMethodTotal.setCount(paymentMethodTotal.getInput().subtract(paymentMethodTotal.getOutput()));	
			}			
		}
		
		return totals;
	}
    
    public void cleanCashJournalCount() {
		log.debug("limpiarRecuentos() - Borrando recuento para caja con uid: " + getOpenedCashJournal().getCashJournalUid());
		cashJournalService.deleteCashJournalCountByCashJournalUid(getOpenedCashJournal().getCashJournalUid());
	}

    public boolean hasCashJournalCount() {
					
		List<CashJournalCount> countLines = cashJournalService.findCashJournalCountByCashJournalUid(getOpenedCashJournal().getCashJournalUid());
		
		return !countLines.isEmpty();
	}
    
    public List<CashJournalCount> findCashJournalCount(){
    	return cashJournalService.findCashJournalCountByCashJournalUid(getOpenedCashJournal().getCashJournalUid());
    }

    public boolean checkCashJournalClosingMandatory() {
    	Boolean obligatorio = variableService.getVariableAsBoolean(VariableServiceFacade.CAJA_CIERRE_CAJA_DIARIO_OBLIGATORIO, true);
    	
		return !obligatorio || DateUtils.truncatedCompareTo(getOpeningDate(), new Date(), Calendar.DAY_OF_MONTH) == 0;
	}
    
    public boolean validateBlockWithdrawalAmount() {
    	return isOpenedCashJournal() && cashJournalService.validateBlockWithdrawalAmount(getOpenedCashJournal().getCashJournalUid());
    }
    
    public boolean validateWarningWithdrawalAmount() {
    	return isOpenedCashJournal() && cashJournalService.validateWarningWithdrawalAmount(getOpenedCashJournal().getCashJournalUid());
    }
    
    public BigDecimal getCashJournalAccumulatedCashAmount() {
    	return isOpenedCashJournal() ? cashJournalService.getCashJournalAccumulatedCashAmount(getOpenedCashJournal().getCashJournalUid()) : BigDecimal.ZERO;
    }
    
    public Date getOpeningDate() {
		return getOpenedCashJournal().getOpeningDate();
	}

	public Date getClosingDate() {
		return getOpenedCashJournal().getClosingDate();
	}

	public String getTillCode() {
		return getOpenedCashJournal().getTillCode();
	}

	public String getUserCode() {
		return getOpenedCashJournal().getOpenUserCode();
	}

	public Date getAccountingDate() {
		return getOpenedCashJournal().getAccountingDate();
	}
	
	public String getStoreCode() {
		return getOpenedCashJournal().getStoreCode();
	}
		
	public List<CashJournalLine> findSalesLines(){
		return cashJournalService.findSaleLines(getOpenedCashJournal().getCashJournalUid());
	}
	
	public List<CashJournalLine> findManualLines(){
		return cashJournalService.findManualLines(getOpenedCashJournal().getCashJournalUid());
	}
	
	public CashJournalHdr findLastCashJournalClosed() {
		return cashJournalService.findLastCashJournalClosed(applicationSession.getStorePosBusinessData().getStorePos().getStoreCode(), applicationSession.getStorePosBusinessData().getStorePos().getTillCode());
	}
	
	public CashJournalLine findOpeningLine() {
		return findManualLines().stream().filter(ml -> ml.getMovConceptCode().equals(OPENING_CONCEPT_CODE)).findFirst().orElse(null);
	}
		
    public CashJournalDocumentIssued<?> openManualCashJournal(Date fecha, List<CashJournalCount> count, Map<String, Object> extensions, PrintDocumentRequest printSettings) {    	
    	CashJournalHdr cashJournal = cashJournalService.findOpenedCashJournal(applicationSession.getStorePosBusinessData().getStorePos().getStoreCode(),
    			applicationSession.getStorePosBusinessData().getStorePos().getTillCode());
    	
    	if(cashJournal != null) {
    		throw new BadRequestException(I18N.getText("La caja ya está abierta")); 
		}
    	
		log.debug("abrirCajaManual() - Abriendo nueva caja con parámetros indicados.. ");
		CashJournalDocumentIssued<?> documentIssued = cashJournalService.createCashJournal(applicationSession.getStorePosBusinessData().getStorePos().getStoreCode(),
				applicationSession.getStorePosBusinessData().getStorePos().getTillCode(), fecha, count, extensions, printSettings);
        
		
		openedCashJournal.set(cashJournalService.findOpenedCashJournal(applicationSession.getStorePosBusinessData().getStorePos().getStoreCode(),
    			applicationSession.getStorePosBusinessData().getStorePos().getTillCode()));
		
		return documentIssued;
    }

    public CashJournalDocumentIssued<?> createManualMovement(NewCashJournalManualMove cashJournalMove, PrintDocumentRequest printSettings) {
        log.debug("crearApunteManual() - Creando nuevo movimiento de caja manual...");
		return cashJournalService.createManualMovement(getOpenedCashJournal().getCashJournalUid(), cashJournalMove, printSettings);
    }
	
}
