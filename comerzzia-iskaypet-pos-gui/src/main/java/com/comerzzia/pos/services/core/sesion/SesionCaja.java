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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.cajas.acumulados.CajaLineaAcumuladoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoBean;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoExample;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoExample.Criteria;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoMapper;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class SesionCaja {

    protected static final Logger log = Logger.getLogger(SesionCaja.class.getName());
    protected Caja cajaAbierta;
    
    @Autowired
    protected MediosPagosService mediosPagosService;
    @Autowired
    protected CajasService cajasService;
	@Autowired
	private VariablesServices variablesServices;

    protected SesionCaja() {

    }

    public void init() throws SesionInitException {
        try {
            Caja caja = cajasService.consultarCajaAbierta();
            cajaAbierta = caja;
        }
        catch (CajaEstadoException ignore) {
            log.info("init() - No existe caja abierta. ");
        }
        catch (Exception ex) {
            log.error("init() - Error inicializando registro de caja: " + ex.getMessage(), ex);
            throw new SesionInitException(ex);
        }
    }

    public void guardarCierreCaja(Date fechaCierre) throws CajasServiceException {
        log.debug("cerrarCaja() - Cerrando caja ... ");
        this.cajaAbierta.setFechaCierre(fechaCierre);
        cajasService.cerrarCaja(cajaAbierta, fechaCierre);  
    }
    
    public void cerrarCaja(){
        cajaAbierta = null;
    }

    public void abrirCajaAutomatica() throws CajasServiceException, CajaEstadoException {
        try {
            // Antes comprobamos que no hay ya caja abierta
            cajaAbierta = cajasService.consultarCajaAbierta();
            log.debug("abrirCajaAutomatica() - La caja ya se encontraba abierta en BBDD. ");
        }
        catch (CajaEstadoException ignore) {
            log.debug("abrirCajaAutomatica() - Abriendo nueva caja con saldo inicial cero... ");
            cajaAbierta = cajasService.crearCaja(new Date());
        }
    }

    public void abrirCajaManual(Date fecha, BigDecimal importe) throws CajasServiceException, CajaEstadoException {
        try {
            // Antes comprobamos que no hay ya caja abierta
            cajaAbierta = cajasService.consultarCajaAbierta();
            log.debug("abrirCajaManual() - La caja ya se encontraba abierta en BBDD. ");
        }
        catch (CajaEstadoException e) {
            log.debug("abrirCajaManual() - Abriendo nueva caja con parámetros indicados.. ");
            cajaAbierta = cajasService.crearCaja(fecha);
            if (BigDecimalUtil.isMayorACero(importe)) {
                cajasService.crearMovimientoApertura(importe, fecha);
                actualizarDatosCaja();
            }
        }
    }

    public void nuevaLineaRecuento(String codigo, BigDecimal importe, Integer cantidad) {
        log.debug("nuevaLineaRecuento() - Creando nueva línea de recuento...");
        CajaLineaRecuentoBean lineaRecuento = new CajaLineaRecuentoBean();
        lineaRecuento.setCodMedioPago(codigo);
        lineaRecuento.setCantidad(cantidad);
        lineaRecuento.setValor(importe);
        cajaAbierta.addLineaRecuento(lineaRecuento);
    }

    public CajaMovimientoBean crearApunteManual(BigDecimal importe, String codConcepto, String documento, String descConcepto) throws CajasServiceException {
        log.debug("crearApunteManual() - Creando nuevo movimiento de caja manual...");
        CajaMovimientoBean mov = cajasService.crearMovimientoManual(importe, codConcepto, documento, descConcepto);
        actualizarDatosCaja();
        
        return mov;
    }

    public void actualizarDatosCaja() throws CajasServiceException {
        // Si la caja no está abierta, no hay que hacer nada
        if (!isCajaAbierta()) {
            return;
        }
        cajasService.consultarMovimientos(cajaAbierta);
        cajasService.consultarTotales(cajaAbierta);
        cajaAbierta.recalcularTotales();
    }

    public void actualizarRecuentoCaja() throws CajasServiceException {
        // Si la caja no está abierta, no hay que hacer nada
        if (!isCajaAbierta()) {
            return;
        }
        cajasService.consultarRecuento(cajaAbierta);
        cuadrarRecuentosAutomaticos();
    }

    public void salvarRecuento() throws CajasServiceException {
        cajasService.salvarRecuento(cajaAbierta);
    }

    public Caja getCajaAbierta() {
        return cajaAbierta;
    }

    public boolean isCajaAbierta() {
        return cajaAbierta != null;
    }

    public String getUidDiarioCaja() {
        if (getCajaAbierta() == null) {
            return null;
        }
        return getCajaAbierta().getUidDiarioCaja();
    }
    
	public boolean tieneDescuadres() throws CajasServiceException {
		boolean res = false;
		BigDecimal descuadre = BigDecimal.ZERO;
		BigDecimal descuadreMax = variablesServices.getVariableAsBigDecimal(VariablesServices.CAJA_IMPORTE_MAXIMO_DESCUADRE);
		cuadrarRecuentosAutomaticos();

		List<CajaLineaAcumuladoBean> recuentosAcumulados = new ArrayList<>(cajaAbierta.getAcumulados().values());

		for (CajaLineaAcumuladoBean lineaAcumulado : recuentosAcumulados) {
			descuadre = descuadre.add(lineaAcumulado.getDescuadre());
		}

		if (BigDecimalUtil.isMayor(descuadre.abs(), descuadreMax)) {
			res = true;
		}

		return res;
	}
    
    public void cuadrarRecuentosAutomaticos() throws CajasServiceException{
    	 // Si la caja no está abierta, no hay que hacer nada
        if (!isCajaAbierta()) {
            return;
        }
    	List<MedioPagoBean> mediosPagoAutomaticoNoManuales = mediosPagosService.getMediosPagoAutomaticoNoManuales();
    	
    	//Primero los borramos de recuentos, no se pueden añadir manualmente
    	for (ListIterator<CajaLineaRecuentoBean> iterator = cajaAbierta.getLineasRecuento().listIterator(); iterator.hasNext();) {
    		CajaLineaRecuentoBean cajaLineaRecuento = iterator.next();
			for (MedioPagoBean medioPago : mediosPagoAutomaticoNoManuales) {
				if(cajaLineaRecuento.getCodMedioPago().equals(medioPago.getCodMedioPago())){
					iterator.remove();
					break;
				}
			}
		}
    	cajaAbierta.recalcularTotalesRecuento();
    	
    	//Añadimos nuevos recuentos con los descuadres
    	Map<String, CajaLineaAcumuladoBean> acumulados = cajaAbierta.getAcumulados();
    	for (MedioPagoBean medioPago : mediosPagoAutomaticoNoManuales) {
    		CajaLineaAcumuladoBean cajaLineaAcumulado = acumulados.get(medioPago.getCodMedioPago());
    		if(cajaLineaAcumulado != null && !BigDecimalUtil.isIgualACero(cajaLineaAcumulado.getDescuadre())){
    			nuevaLineaRecuento(cajaLineaAcumulado.getMedioPago().getCodMedioPago(), cajaLineaAcumulado.getDescuadre().multiply(new BigDecimal(-1)), 1);
    		}
		}
    	
    	cajasService.salvarRecuento(cajaAbierta);
    	cajaAbierta.recalcularTotalesRecuento();
    }

    public void limpiarRecuentos(Caja caja) throws CajasServiceException {
		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());
			CajaLineaRecuentoExample exampleCajaLineaRecuento = new CajaLineaRecuentoExample();
			CajaLineaRecuentoMapper cajaLineaRecuentoMapper = sqlSession.getMapper(CajaLineaRecuentoMapper.class);

			exampleCajaLineaRecuento.or().andUidDiarioCajaEqualTo(caja.getUidDiarioCaja());
			log.debug("limpiarRecuentos() - Borrando recuento para caja con uid: " + caja.getUidDiarioCaja());
			cajaLineaRecuentoMapper.deleteByExample(exampleCajaLineaRecuento);

			sqlSession.commit();
		}
		catch (Exception e) {
			String msg = "Se ha producido un error borrando los recuentos de caja con UID: " + caja.getUidDiarioCaja() + " : " + e.getMessage();
			log.error("limpiarRecuentos() - " + msg);
			throw new CajasServiceException(I18N.getTexto("Error al borrar los recuentos de cajas en el sistema"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	public boolean existeRecuento(Caja caja) throws CajasServiceException {
		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());
			CajaLineaRecuentoExample exampleCajaLineaRecuento = new CajaLineaRecuentoExample();
			CajaLineaRecuentoMapper cajaLineaRecuentoMapper = sqlSession.getMapper(CajaLineaRecuentoMapper.class);

			List<String> medioPagosAutomaticos = mediosPagosService.getCodMediosPagoAutomatico();
			
			Criteria or = exampleCajaLineaRecuento.or().andUidDiarioCajaEqualTo(caja.getUidDiarioCaja());
			if(medioPagosAutomaticos != null && !medioPagosAutomaticos.isEmpty()) {
				or.andCodMedioPagoNotIn(medioPagosAutomaticos);
			}
			log.debug("existeRecuento() - Consultando recuento para caja con uid: " + caja.getUidDiarioCaja());
			List<CajaLineaRecuentoBean> recuento = cajaLineaRecuentoMapper.selectByExample(exampleCajaLineaRecuento);
			if (recuento == null || recuento.isEmpty()) {
				return false;
			}
			else {
				return true;
			}
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando los recuentos de caja con UID: " + caja.getUidDiarioCaja() + " : " + e.getMessage();
			log.error("existeRecuento() - " + msg);
			throw new CajasServiceException(I18N.getTexto("Error al consultar los recuentos de cajas en el sistema"), e);
		}
		finally {
			sqlSession.close();
		}
	}
    
}
