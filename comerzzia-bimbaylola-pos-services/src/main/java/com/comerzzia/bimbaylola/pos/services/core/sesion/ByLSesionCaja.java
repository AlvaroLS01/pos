package com.comerzzia.bimbaylola.pos.services.core.sesion;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.services.cajas.ByLCajasService;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.SesionCaja;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Primary
public class ByLSesionCaja extends SesionCaja{

	@Autowired
	protected VariablesServices variableService;
	
	@Autowired
    protected ByLCajasService cajasService;
	
	String idVariableGestionCaja = "GESTION.CAJA";
	
	public void setCajaAbierta(Caja caja){
		cajaAbierta = caja;
	}
	
	/**
	 * Se encarga de comprobar el tipo de gestión de caja que tiene la tienda, 
	 * luego comprueba si la caja está abierta o no, en caso de estar abierta 
	 * y el tipo de tienda ser S, mete en sesión la caja.
	 */
	@Override
	public boolean isCajaAbierta(){
		
		Boolean todosMisma = variableService.getVariableAsBoolean(idVariableGestionCaja);
		Caja caja = null;
		
		if(cajaAbierta == null && todosMisma){
			try{
				caja = cajasService.consultarCajaAbierta();
				cajaAbierta = caja;
			}catch(CajasServiceException | CajaEstadoException e){}
		}else if(cajaAbierta != null && todosMisma){
			try{
				caja = cajasService.consultarCajaAbierta();
				if(caja != null){
					Caja ultimaCaja = null;
					try {
						ultimaCaja = cajasService.consultarUltimaCajaCerrada();
					} catch(CajasServiceException | CajaEstadoException ignore){}
					if(ultimaCaja != null && caja.getUidDiarioCaja().equals(ultimaCaja
							.getUidDiarioCaja())){
						cajasService.meterCajaSesion(caja);
					}
				}
			}catch(CajasServiceException | CajaEstadoException e){
				cajasService.quitarCajaSesion();
			}
		}
		
		return cajaAbierta != null;
		
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
				// BYL-295 - No será necesario crear movimiento en las aperturas, solo el documento de apertura (CA)
				cajasService.crearMovimientoApertura(importe, fecha);
				actualizarDatosCaja();
			}
		}
	}
	
}
