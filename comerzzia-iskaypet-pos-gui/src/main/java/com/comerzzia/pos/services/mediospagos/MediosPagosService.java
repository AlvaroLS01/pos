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

package com.comerzzia.pos.services.mediospagos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.model.mediospago.configuracion.ConfiguracionMedioPagoBean;
import com.comerzzia.core.model.pasarelas.configuraciones.ConfiguracionPasarelaBean;
import com.comerzzia.core.model.pasarelas.tipos.TipoPasarelaBean;
import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.mediospago.configuracion.ConfiguracionMedioPagoService;
import com.comerzzia.core.servicios.pasarelas.configuraciones.ConfiguracionPasarelaService;
import com.comerzzia.core.servicios.pasarelas.tipos.TipoPasarelaService;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoExample;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoKey;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoMapper;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiendas.Tienda;
import com.comerzzia.pos.util.i18n.I18N;

@Service
public class MediosPagosService {

    protected static final Logger log = Logger.getLogger(MediosPagosService.class);
    
    @Autowired
    protected Sesion sesion;
    
    @Autowired
    protected MedioPagoMapper medioPagoMapper;
    
    @Autowired
    private Documentos documentos;
    
    @Autowired
    private TipoPasarelaService tipoPasarelaService;
    
    @Autowired
    private ConfiguracionMedioPagoService confMedioPagoService;
    
    @Autowired
    private ConfiguracionPasarelaService confPasarelaService;
    
    protected static final String COD_MED_PAGO_VALE = "0020";
    
    public static Map<String, MedioPagoBean> mediosPago; // Código - MedioPago
    public static List<MedioPagoBean> mediosPagoTarjetas;
    public static List<MedioPagoBean> mediosPagoContado;
    public static List<MedioPagoBean> mediosPagoVisibleVenta;
    public static MedioPagoBean medioPagoDefecto;
    public static MedioPagoBean medioPagoPromocional;
    public static MedioPagoBean medioPagoEntregaCuenta;
    public static MedioPagoBean medioPagoVale;

    /** Carga en memoria los medios de pago configurados para el sistema.
     * Si alguno de los medios de pago especiales configurados para la tienda
     * no existe, lanzará una excepción.
     * @param uidActividad UidActividad con la que trabaja la aplicación
     * @param tienda Tienda con la que trabaja la aplicación
     * @throws MediosPagoServiceException 
     */
    public void cargarMediosPago(String uidActividad, Tienda tienda) throws MediosPagoServiceException {

        try {
            // inicializamos objetos
            mediosPago = new HashMap<>();
            mediosPagoTarjetas = new ArrayList<>();
            mediosPagoContado = new ArrayList<>();
            mediosPagoVisibleVenta = new ArrayList<>();
            
            // consultamos la base de datos
            MedioPagoExample exampleMedioPago = new MedioPagoExample();
			exampleMedioPago.or().andUidActividadEqualTo(uidActividad).andActivoEqualTo(Boolean.TRUE);
            log.debug("cargarMediosPago() - Cargando medios de pago para el uidActividad " + uidActividad);
            List<MedioPagoBean> listaMediosPagos = medioPagoMapper.selectByExample(exampleMedioPago);
            
            consultarPasarelas(listaMediosPagos);
            
            // construimos mapa y listas
            for (MedioPagoBean medioPago : listaMediosPagos) {
                mediosPago.put(medioPago.getCodMedioPago(), medioPago);
                if(medioPago.getVisibleVenta()){
                	if(medioPago.getManual()){
                		mediosPagoVisibleVenta.add(medioPago);
		                if (medioPago.getTarjetaCredito()){
		                    mediosPagoTarjetas.add(medioPago);
		                }else{
		                    mediosPagoContado.add(medioPago);
		                }
                	}
                }
            }
            
            // tratamos medios de pago especiales
            String codMedioPagoDefecto = tienda.getTiendaBean().getCodMedioPagoPorDefecto();
            String codMedioPagoPromocional = tienda.getTiendaBean().getCodMedioPagoPromocion();
            String codMedioPagoEntregaCuenta = tienda.getTiendaBean().getCodMedioPagoApartado();
            medioPagoDefecto = mediosPago.get(codMedioPagoDefecto);
            if (medioPagoDefecto == null){
                log.error("cargarMediosPago() - No existe medio de pago por defecto");
                throw new MediosPagoServiceException(I18N.getTexto("No existe el medio de pago configurado para la tienda como Por Defecto"));
            }
            medioPagoPromocional = mediosPago.get(codMedioPagoPromocional);
            if (medioPagoPromocional == null){
                log.error("cargarMediosPago() - No existe medio de pago promocional");
                throw new MediosPagoServiceException(I18N.getTexto("No existe el medio de pago configurado para la tienda como Promocional"));
            }
            medioPagoEntregaCuenta = mediosPago.get(codMedioPagoEntregaCuenta);
            if (medioPagoEntregaCuenta == null){
                log.error("cargarMediosPago() - No existe medio de pago de entrega a cuenta");
                throw new MediosPagoServiceException(I18N.getTexto("No existe el medio de pago configurado para la tienda como Entrega a Cuenta"));
            }
            mediosPagoContado.remove(medioPagoDefecto);
            mediosPagoContado.remove(medioPagoEntregaCuenta);
            mediosPagoContado.remove(medioPagoPromocional);
        }
        catch(MediosPagoServiceException e){
            throw e;
        }
        catch (Exception e) {
            String msg = "Se ha producido un error cargando los medios de pagos: " + e.getMessage();
            log.error("cargarMediosPago() - " + msg, e);
            throw new MediosPagoServiceException(e);
        }
    }

	protected void consultarPasarelas(List<MedioPagoBean> listaMediosPagos) throws EmpresaException {
		DatosSesionBean datosSesion = new DatosSesionBean();
		datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
		datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
		
		String codAlmacen = sesion.getAplicacion().getCodAlmacen();
		String uidCaja = sesion.getAplicacion().getUidCaja();
		for (MedioPagoBean medioPago : listaMediosPagos) {
			try {
				consultarPasarela(datosSesion, codAlmacen, uidCaja, medioPago);
			}
			catch (Exception e) {
				log.error("consultarPasarela() - Ha habido un error al consultar la pasarela para los medios de pago: " + e.getMessage(), e);
			}
		}
	}

	protected void consultarPasarela(DatosSesionBean datosSesion, String codAlmacen, String uidCaja, MedioPagoBean medioPago) {
		String codMedioPago = medioPago.getCodMedioPago();
		
		ConfiguracionMedioPagoBean configMedPago = confMedioPagoService.selectByPrimaryKey(codMedioPago, "D_TIENDAS_CAJAS_TBL.UID_CAJA", uidCaja, datosSesion);
		if(configMedPago == null) {
			configMedPago = confMedioPagoService.selectByPrimaryKey(codMedioPago, "D_TIENDAS_TBL.CODALM", codAlmacen, datosSesion);
		}
		if(configMedPago == null) {
			configMedPago = confMedioPagoService.selectByPrimaryKey(codMedioPago, "D_MEDIOS_PAGO_TBL.CODMEDPAG", codMedioPago, datosSesion);
		}
		if (configMedPago != null) {					
			ConfiguracionPasarelaBean configuracionPasarela = confPasarelaService.consultarConfiguracion(configMedPago.getIdConfPasarela(), datosSesion);
			medioPago.setConfigPasarela(configuracionPasarela);
			
			if(configuracionPasarela != null) {
				TipoPasarelaBean tipoPasarela = tipoPasarelaService.consultar(configuracionPasarela.getIdTipoPasarela(), datosSesion);
				medioPago.setTipoPasarela(tipoPasarela);
			}
		}
	}

    /**
     * Devuelve el medio de pago con el código indicado. El medio de pago debe
     * de estar activo. Si el medio de pago no existe, se lanza una excepción.
     *
     * @param codMedioPago :: código del medio de pago que se quiere consultar
     * @return :: MedioPago
     * @throws MediosPagoServiceException
     * @throws MedioPagoNotFoundException :: Lanzada si el medio de pago no
     * existe.
     */
    public MedioPagoBean consultarMedioPago(String codMedioPago) throws MediosPagoServiceException, MedioPagoNotFoundException {
        try {
            String uidActividad = sesion.getAplicacion().getUidActividad();

            MedioPagoKey keyMedioPago = new MedioPagoKey();
            keyMedioPago.setCodMedioPago(codMedioPago);
            keyMedioPago.setUidActividad(uidActividad);
            log.debug("consultarMediosPago() - Realizando consulta de medio de pago");
            MedioPagoBean medioPagoBean = medioPagoMapper.selectByPrimaryKey(keyMedioPago);
            if (medioPagoBean != null && medioPagoBean.getActivo()) {
                return medioPagoBean;
            }
            else {
                throw new MedioPagoNotFoundException();
            }
        }
        catch (Exception e) {
            String msg = "Se ha producido un error consultando los medios de pagos con codigo " + codMedioPago + " : " + e.getMessage();
            log.error("consultarMediosPago() - " + msg);
            throw new MediosPagoServiceException(e);
        }
    }
    
    public MedioPagoBean getMedioPago(String codMedioPago){
        return mediosPago.get(codMedioPago);
    }
    
    public boolean isCodMedioPagoVale(String codMedioPago, Long idTipoDocumento){
    	boolean isCodMedioPagoVale = false;
    	try {
    		if(idTipoDocumento != null) {
		        TipoDocumentoBean documento = documentos.getDocumento(idTipoDocumento);
		        String codFormaPagoVale = documento.getFormaPagoVale();
		        if(codFormaPagoVale.equals(TipoDocumentoBean.PROPIEDAD_FORMA_PAGO_VALE + " NO CONFIGURADO")) {
		        	log.warn("isCodMedioPagoVale() - Medio de pago VALE para este documento no configurado. Se usará 0020 por defecto.");
		        	isCodMedioPagoVale = codMedioPago.equals(COD_MED_PAGO_VALE);
		        }
		        else {
		        	isCodMedioPagoVale = codFormaPagoVale.equals(codMedioPago);
		        }
    		}
    		else {
    			return false;
    		}
        }
        catch (Exception e) {
        	log.error("isCodMedioPagoVale() - Ha habido un error al buscar la propiedad del medio de pago: " + e.getMessage(), e);
        	isCodMedioPagoVale = codMedioPago.equals(COD_MED_PAGO_VALE);
        }
        return isCodMedioPagoVale;
    }
    
    public List<MedioPagoBean> getMediosPagoAutomaticoNoManuales(){
    	List<MedioPagoBean> mediosPagoAuto = new LinkedList<>();
    	for (MedioPagoBean medioPagoBean : mediosPago.values()) {
			if(!medioPagoBean.getManual() || medioPagoBean.getRecuentoAutomaticoCaja()){
				mediosPagoAuto.add(medioPagoBean);
			}
		}
    	return mediosPagoAuto;
    }
    
    public List<String> getCodMediosPagoAutomatico(){
    	List<String> codigos = new ArrayList<String>();
    	for(MedioPagoBean medioPago:mediosPago.values()){
    		if(medioPago.getRecuentoAutomaticoCaja()){
    			codigos.add(medioPago.getCodMedioPago());
    		}
    	}
    	return codigos;
    }
    
}
