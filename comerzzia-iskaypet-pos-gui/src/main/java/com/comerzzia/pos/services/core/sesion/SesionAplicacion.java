/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */


package com.comerzzia.pos.services.core.sesion;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.AccountsApi;
import com.comerzzia.api.loyalty.client.CardsApi;
import com.comerzzia.api.loyalty.client.CouponsApi;
import com.comerzzia.core.servicios.api.ComerzziaApiManager;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.actividades.ActividadBean;
import com.comerzzia.pos.persistence.core.empresas.EmpresaBean;
import com.comerzzia.pos.persistence.core.tiendas.cajas.TiendaCajaBean;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.services.cajas.conceptos.CajaConceptosServiceException;
import com.comerzzia.pos.services.cajas.conceptos.CajaConceptosServices;
import com.comerzzia.pos.services.clientes.ClienteNotFoundException;
import com.comerzzia.pos.services.clientes.ClientesService;
import com.comerzzia.pos.services.clientes.ClientesServiceException;
import com.comerzzia.pos.services.codBarrasEsp.CodBarrasEspecialesException;
import com.comerzzia.pos.services.codBarrasEsp.CodBarrasEspecialesServices;
import com.comerzzia.pos.services.core.actividades.ActividadNotFoundException;
import com.comerzzia.pos.services.core.actividades.ActividadesService;
import com.comerzzia.pos.services.core.actividades.ActividadesServiceException;
import com.comerzzia.pos.services.core.conceptosalmacen.ConceptoServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.DocumentoNotFoundException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.empresas.EmpresaNotFoundException;
import com.comerzzia.pos.services.core.empresas.EmpresasService;
import com.comerzzia.pos.services.core.empresas.EmpresasServiceException;
import com.comerzzia.pos.services.core.menu.MenuService;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.services.core.tiendas.Tienda;
import com.comerzzia.pos.services.core.tiendas.TiendaNotFoundException;
import com.comerzzia.pos.services.core.tiendas.TiendasService;
import com.comerzzia.pos.services.core.tiendas.TiendasServiceException;
import com.comerzzia.pos.services.core.tiendas.cajas.TiendaCajaNotFoundException;
import com.comerzzia.pos.services.core.tiendas.cajas.TiendaCajaService;
import com.comerzzia.pos.services.core.tiendas.cajas.TiendaCajaServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagoServiceException;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.ConfigException;
import com.comerzzia.pos.util.config.TPVConfig;

@Component
public class SesionAplicacion {
    
    protected static final Logger log = Logger.getLogger(SesionAplicacion.class.getName());

    protected String uidInstancia;
    protected String uidActividad;
    protected String uidCaja;
    protected String codCaja;
    protected Tienda tienda;
    protected boolean desglose1Activo;
    protected boolean desglose2Activo;
    protected EmpresaBean empresa;
    protected TiendaCajaBean tiendaCaja;
    
    @Autowired
    protected MediosPagosService mediosPagosService;
    @Autowired
    protected VariablesServices variablesServices;
    @Autowired
    protected ClientesService clientesService;
    @Autowired
    protected CodBarrasEspecialesServices codBarrasEspecialesServices;
    @Autowired
    protected TiendasService tiendasService;
    @Autowired
    protected CajaConceptosServices cajaConceptosServices;
    @Autowired
    protected EmpresasService empresasService;
    @Autowired
    protected ActividadesService actividadesService;
    @Autowired
    protected PaisService paisService;
    
    @Autowired
    protected TPVConfig tpvConfig;
    
    @Autowired
    protected MenuService menuService;
    
    @Autowired
    protected TiendaCajaService tiendaCajaService;
    
    //Documentos  de la aplicación
    @Autowired
    protected Documentos documentos;
    
    @Autowired
    private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
    
    @Autowired
    private ComerzziaApiManager comerzziaApiManager;
    
    protected String storeLanguageCode;
    
    protected SesionAplicacion(){
        //dispositivos = new ConfigDispositivosDisponibles();
        //configTPV = new ConfiguracionTPV();
    }
    
    public void init() throws SesionInitException {
        try{
            // Cargamos fichero pos_config
            log.info("init() - Cargando fichero de configuración de caja...");
            tpvConfig.load();
            uidActividad = tpvConfig.getUidActividad();
            uidCaja = tpvConfig.getUidCaja();
            log.info("init() - UID_ACTIVIDAD: " + uidActividad + " UID_CAJA: " + uidCaja);

            // Consultamos uidActividad
            ActividadBean actividad = actividadesService.consultarActividad(uidActividad);
            uidInstancia = actividad.getUidInstancia();

            // Consultamos caja
            TiendaCajaBean tiendaCaja = tiendaCajaService.consultarTPV(uidActividad, uidCaja);
            tiendaCaja.getUidTpv();
            codCaja = tiendaCaja.getCodcaja();
            this.tiendaCaja = tiendaCaja;

            // Consultamos tienda
            Tienda tienda = tiendasService.consultarTienda(uidActividad, tiendaCaja.getCodAlmacen());
            ClienteBean clienteTienda = clientesService.consultarCliente(uidActividad, tienda.getAlmacenBean().getCodCliente());
            tienda.setCliente(clienteTienda);
            this.tienda = tienda;
            
            // Consultamos empresa
            EmpresaBean empresa = empresasService.consultarEmpresa(uidActividad, tienda.getAlmacenBean().getCodEmpresa());
            this.empresa = empresa;

            // Consultamos los documentos para el país determinado e uid_instancia 
            documentos.inicializar(uidActividad, tienda.getCliente().getCodpais());

            // Cargamos medios de pago
            mediosPagosService.cargarMediosPago(uidActividad, tienda);
            
            paymentsMethodsConfiguration.loadConfiguration();
            
            codBarrasEspecialesServices.cargarCodigosBarrasEspeciales(uidActividad);
            
            // Cargamos conceptos de caja
            cajaConceptosServices.cargarConceptosCaja(uidActividad);

            // Cargamos variables del sistema
            variablesServices.cargarVariables(uidActividad);
            
            // Inicializamos variables de desgloses
            desglose1Activo = true;
            desglose2Activo = true;
            String desglose1 = variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO);
            String desglose2 = variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO);
            if (desglose1 == null || desglose1.isEmpty()){
                desglose1Activo = false;
            }
            if (desglose2 == null || desglose2.isEmpty()){
                desglose2Activo = false;
            }
            
            String restUrl = variablesServices.getVariableAsString(VariablesServices.REST_URL);
            String restUrlOmni = variablesServices.getVariableAsString(VariablesServices.REST_OMNICHANNEL_URL);
            com.comerzzia.api.core.rest.path.CoreWebservicesPath.initPath(restUrl);
            com.comerzzia.api.rest.path.BackofficeWebservicesPath.initPath(restUrl);
            com.comerzzia.instoreengine.rest.path.InStoreEngineWebservicesPath.initPath(AppConfig.iseRestUrl);
            com.comerzzia.api.omnichannel.documentos.webservices.cliente.rest.path.OmnichannelWebservicesPath.initPath(restUrlOmni);
            
            comerzziaApiManager.registerAPI("CouponsApi", CouponsApi.class, "loyalty");
            comerzziaApiManager.registerAPI("CardsApi", CardsApi.class, "loyalty");
            comerzziaApiManager.registerAPI("LoyaltyAccountsApi", AccountsApi.class, "loyalty");
            
        }
        catch (ClienteNotFoundException | 
                ClientesServiceException | 
                TiendaCajaNotFoundException | 
                TiendaCajaServiceException | 
                TiendaNotFoundException | 
                TiendasServiceException | 
                EmpresaNotFoundException | 
                EmpresasServiceException | 
                MediosPagoServiceException | 
                CajaConceptosServiceException | 
                VariablesServiceException |
                ConfigException  |
                ActividadNotFoundException | 
                DocumentoException |
                DocumentoNotFoundException |
                ActividadesServiceException |
                CodBarrasEspecialesException |
                ConceptoServiceException ex) {
            log.error("init() - " + ex.getMessageI18N());
            throw new SesionInitException(ex.getMessageI18N(), ex);
        }
    }
 
    public void actualizarUidPos(String newUidPos) throws TiendaCajaServiceException{
    	tiendaCajaService.actualizarUidPOS(tiendaCaja, newUidPos);
    	tiendaCaja.setUidTpv(newUidPos);
    }
    
    public String getUidActividad() {
        return uidActividad;
    }

    public String getUidCaja() {
        return uidCaja;
    }

    public String getUidInstancia() {
        return uidInstancia;
    }

    public String getCodCaja() {
        return codCaja;
    }

    public Tienda getTienda() {
        return tienda;
    }

    public EmpresaBean getEmpresa() {
        return empresa;
    }

//    public ConfigDispositivosDisponibles getDispositivos() {
//        return dispositivos;
//    }
    
//    public ConfiguracionTPV getConfigTPV() {
//        return configTPV;
//    }
    
    public String getCodAlmacen(){
        if (getTienda() != null){
            return getTienda().getAlmacenBean().getCodAlmacen();
        }
        return null;
    }

    public boolean isDesglose1Activo() {
        return desglose1Activo;
    }

    public boolean isDesglose2Activo() {
        return desglose2Activo;
    }
    
    public boolean isDesglosesActivos() {
        return desglose1Activo || desglose2Activo;
    }
    
    public void setTiendaCaja(TiendaCajaBean tiendaCaja){
        this.tiendaCaja = tiendaCaja;
    }
    
    public TiendaCajaBean getTiendaCaja(){
        return tiendaCaja;
    }
    
    public Documentos getDocumentos() {
        return documentos;
    }

    public void setDocumentos(Documentos documentos) {
        this.documentos = documentos;
    }

	public TPVConfig getTpvConfig() {
		return tpvConfig;
	}
	
	public String getStoreLanguageCode() {
		if(storeLanguageCode == null) {
			storeLanguageCode = getTienda().getCliente().getCodlengua();
	        if(StringUtils.isBlank(storeLanguageCode)) {
	        	String countryCode = getTienda().getCliente().getCodpais();
	        	try {
					PaisBean country = paisService.consultarCodPais(countryCode);
					storeLanguageCode = country.getCodLengua();
					if(StringUtils.isBlank(storeLanguageCode)) {
						storeLanguageCode = AppConfig.idioma;
					}
				} catch (Exception e) {
					 log.error("getStoreLanguageCode() - Error while query store country: " + e.getMessage(), e);
					 storeLanguageCode = AppConfig.idioma;
				}
	        }
		}
        return storeLanguageCode;
	}
    
}