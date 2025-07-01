package com.comerzzia.pos.core.services.session;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.audit4j.core.IAuditManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.catalog.facade.service.CatalogManager;
import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.commons.sessions.ComerzziaSession;
import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.facade.model.Activity;
import com.comerzzia.core.facade.model.Company;
import com.comerzzia.core.facade.model.DocTypeDetail;
import com.comerzzia.core.facade.service.activity.ActivityServiceFacade;
import com.comerzzia.core.facade.service.doctype.DocTypeServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.core.service.audit.ComerzziaAuditEventBuilder;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodDetail;
import com.comerzzia.omnichannel.facade.model.payments.StorePosPaymentMethods;
import com.comerzzia.omnichannel.facade.model.store.StorePosBusinessData;
import com.comerzzia.omnichannel.facade.service.store.StorePosPaymentMethodServiceFacade;
import com.comerzzia.omnichannel.facade.service.store.StorePosServiceFacade;
import com.comerzzia.pos.core.services.config.Environment;
import com.comerzzia.pos.core.services.config.EnvironmentSelector;
import com.comerzzia.pos.util.config.TPVConfig;

import javafx.beans.property.SimpleObjectProperty;

@Component
public class ApplicationSession extends Observable{    
    protected static final Logger log = Logger.getLogger(ApplicationSession.class.getName());
        
    protected String configuredInstanceUid;
    protected String configuredActivityUid;
    protected String posUid;
    protected String tillCode;
    protected boolean combination1CodeActive;
    protected boolean combination2CodeActive;
    
    protected SimpleObjectProperty<Locale> currentLocale = new SimpleObjectProperty<>(Locale.getDefault());
    
    protected Catalog currentCatalog;
        
    @Autowired
    protected StorePosPaymentMethodServiceFacade storePosPaymentMethodService;
        
    @Autowired
    protected VariableServiceFacade variablesServices;

    @Autowired
    protected ActivityServiceFacade activityService;
    
    @Autowired
    protected StorePosServiceFacade storePosService;
    
    @Autowired
    protected DocTypeServiceFacade docTypeService;
        
    @Autowired
    protected ComerzziaTenantResolver tenantResolver;
    
	@Autowired(required = false)
	protected IAuditManager auditManager;
	
	@Autowired
	protected CatalogManager catalogManager;
    
    protected String storeLanguageCode;
    
    protected StorePosBusinessData storePosBusinessData;
        
    protected ApplicationSession(){
    }
    
    public void init() throws SesionInitException {
        try{
            // Cargamos fichero pos_config
            log.info("init() - Cargando fichero de configuración de caja...");
            
            configuredActivityUid = getTpvConfig().getTpv().getUidActividad();
            posUid = getTpvConfig().getTpv().getUidCaja();
            
            log.info("init() - UID_ACTIVIDAD: " + configuredActivityUid + " UID_CAJA: " + posUid);

            // Consultamos uidActividad
            Activity actividad = activityService.findById(configuredActivityUid);
            configuredInstanceUid = actividad.getInstanceUid();
            
            ComerzziaSession tenantSession = new ComerzziaSession();
            tenantSession.setActivityUid(actividad.getActivityUid());
            tenantSession.setInstanceUid(configuredInstanceUid);
			tenantResolver.forceCurrentTenantSession(tenantSession);

            // Consultamos caja
            storePosBusinessData = storePosService.getStorePosBusinessData(posUid);
            
            storePosBusinessData.setTrainingMode(EnvironmentSelector.isTrainingEnvironment());
            
            tillCode = storePosBusinessData.getStorePos().getTillCode();
            
            storeLanguageCode = storePosBusinessData.getDefaultCustomer().getLanguageCode();
                       
            // Consultamos los documentos para el país determinado e uid_instancia 
            docTypeService.loadCountryCache(storePosBusinessData.getDefaultCustomer().getCountryCode());

            // Cargamos medios de pago
            storePosPaymentMethodService.preloadPaymentsMethodsCache(storePosBusinessData.getStore().getStoreCode(), tillCode);
                                    
            // Inicializamos variables de desgloses
            combination1CodeActive = true;
            combination2CodeActive = true;
            
            notifyObservers();
            
            String desglose1 = variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO);
            String desglose2 = variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO);
            if (desglose1 == null || desglose1.isEmpty()){
                combination1CodeActive = false;
            }
            if (desglose2 == null || desglose2.isEmpty()){
                combination2CodeActive = false;
            }
            
        }
        catch (NotFoundException ex) {
            log.error("init() - " + ex.getMessage());
            throw new SesionInitException(ex.getMessage(), ex);
        }
    }
 
    public void updatePosValidationUid(String newPosValidationUid) {
    	storePosService.updatePosValidationUid(storePosBusinessData.getStorePos().getPosUid(), newPosValidationUid);
    	
    	storePosBusinessData.getStorePos().setPosValidationUid(newPosValidationUid);
    }
    
    public String getConfiguredActivityUid() {
        return configuredActivityUid;
    }

    public String getPosUid() {
        return posUid;
    }

    public String getConfiguredInstanceUid() {
        return configuredInstanceUid;
    }

    public String getTillCode() {
        return tillCode;
    }
    
    public String getCodAlmacen(){
        if (storePosBusinessData != null && storePosBusinessData.getStore() != null){
            return storePosBusinessData.getStore().getStoreCode();
        }
        return null;
    }

    public boolean isDesglose1Activo() {
        return combination1CodeActive;
    }

    public boolean isDesglose2Activo() {
        return combination2CodeActive;
    }
    
    public boolean isDesglosesActivos() {
        return combination1CodeActive || combination2CodeActive;
    }        
	
	public String getStoreLanguageCode() {		
        return storeLanguageCode;
	}
	
	public Company getCompany() {
		return storePosBusinessData.getCompany();
	}
	
	public StorePosBusinessData getStorePosBusinessData() {
		return storePosBusinessData;
	}
	
		    
    public DocTypeDetail getDocTypeByDocTypeCode(String docTypeCode) throws NotFoundException {
        return docTypeService.getCountryDocTypeByDocTypeCode(getStorePosBusinessData().getDefaultCustomer().getCountryCode(), docTypeCode);
    }
    
    public DocTypeDetail getDocTypeByDocTypeId(Long docTypeId) throws NotFoundException {
        return docTypeService.getCountryDocTypeByDocTypeId(getStorePosBusinessData().getDefaultCustomer().getCountryCode(), docTypeId);
    }
    
    public DocTypeDetail getDocTypeForNatureDocumentType(String nature) throws NotFoundException {
    	return docTypeService.getCountryDocTypeForNatureDocumentType(getStorePosBusinessData().getDefaultCustomer().getCountryCode(), nature);
	}
    
    public List<DocTypeDetail> getDocumentsTypes() {
        return docTypeService.findAllByCountryCode(getStorePosBusinessData().getDefaultCustomer().getCountryCode());
    }
    
    public List<DocTypeDetail> getRefundDocumentsTypes() {
        return docTypeService.getCountryRefundDocumentsTypes(getStorePosBusinessData().getDefaultCustomer().getCountryCode());
    }
    
    public List<String> getDocumentTypeCodes() {
        return docTypeService.getCountryDocumentTypeCodes(getStorePosBusinessData().getDefaultCustomer().getCountryCode());
    }
    
    public List<String> getRefundDocumentTypeCodes() {
        return docTypeService.getCountryRefundDocumentTypeCodes(getStorePosBusinessData().getDefaultCustomer().getCountryCode());
    }
    
    public List<String> getValidOriginDocTypesListForType(String docTypeCode) {
    	return docTypeService.getCountryValidOriginDocTypesListForType(getStorePosBusinessData().getDefaultCustomer().getCountryCode(), docTypeCode);
    }
    
    public List<DocTypeDetail> getValidOriginDocTypesForType(String docTypeCode) {
    	return docTypeService.getCountryValidOriginDocTypesForType(getStorePosBusinessData().getDefaultCustomer().getCountryCode(), docTypeCode);
    }
    
    public void updatedEnvironment(Environment oldEnvironment, Environment newEnvironment) {
    	if(!oldEnvironment.equals(newEnvironment)) {
    		setChanged();
    	}
    }
        
    public static TPVConfig getTpvConfig() {
    	return EnvironmentSelector.getCurrentEnvironment().getConfig();
    }
    
	public void auditOperation(ComerzziaAuditEventBuilder event) {
		if (auditManager == null) return;
				
		auditManager.audit(event.build());
	}
	
	public Catalog getValidCatalog() {
		if (currentCatalog != null && currentCatalog.validate()) return currentCatalog;
		
		currentCatalog = catalogManager.getCatalog(storePosService.getCatalogSettings(storePosBusinessData, null));
		
		return currentCatalog;
	}
			
	public Map<String, PaymentMethodDetail> getPaymentMethodsMap() {
	   return getPaymentMethods().getPaymentsMethods();
	}
	
	public StorePosPaymentMethods getPaymentMethods() {
	   return storePosPaymentMethodService.getPaymentMethodsFromCache(getCodAlmacen(), getTillCode());	
	}
	
	public void setLocale(Locale locale) {
		Locale.setDefault(locale);
		currentLocale.set(locale);
	}
	
	public Locale getCurrentLocale() {
		return currentLocale.get();
	}
	
	public SimpleObjectProperty<Locale> currentLocaleProperty() {
		return currentLocale;
	}

    
}