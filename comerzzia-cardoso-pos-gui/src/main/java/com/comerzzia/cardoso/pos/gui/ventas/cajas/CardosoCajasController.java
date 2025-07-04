package com.comerzzia.cardoso.pos.gui.ventas.cajas;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.services.cajas.CardosoCajasService;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.cajas.CajasController;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;
import com.ibm.icu.util.Calendar;

@Primary
@Component
public class CardosoCajasController extends CajasController{

	public static final Logger log = Logger.getLogger(CardosoCajasController.class);

	@Autowired
	private VariablesServices variabesService;
	@Autowired
	private CajasService cajasService;

	/**
	 * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - DÍAS ENTRE APERTURA CAJA
	 * 
	 * Se necesita comprobar cuantos días hace que no se abre la última caja.
	 * Comparamos con el valor de la variable "CAJA.DIAS_ENTRE_APERTURA", si la cantidad de días 
	 * desde que se abrió la caja es superior a lo indicado, no dejamos cerrar.
	 */
	
	public static final String VARIABLE_DIAS_ENTRE_APERTURA = "CAJA.DIAS_ENTRE_APERTURA";

	@Override
	public void abrirCaja(){
		log.debug("abrirCaja() : GAP - PERSONALIZACIONES V3 - DÍAS ENTRE APERTURA CAJA");
		
		/* Realizamos las comprobaciones para saber si la caja se puede abrir. */
		Integer diasEntreApertura = variabesService.getVariableAsInteger(VARIABLE_DIAS_ENTRE_APERTURA);
		if(diasEntreApertura == null){
			diasEntreApertura = 0;
		}
		boolean puedeAbrirCaja = diasEntreApertura <= 0 || aperturaCajaPermitido(diasEntreApertura);
		if(puedeAbrirCaja){
			super.abrirCaja();
		}
		else{
			String msgAviso = I18N.getTexto("No se permite abrir la caja porque han pasado más de " + diasEntreApertura 
					+ " día/s desde que se abrió la última. Por favor consulte con el administrador.");
			VentanaDialogoComponent.crearVentanaAviso(msgAviso, getStage());
			return;
		}
	}

	/**
	 * Realiza las comprobaciones para saber si puede abrir la caja.
	 * @param diasEntreApertura
	 * @return Boolean
	 */
	public Boolean aperturaCajaPermitido(Integer diasEntreApertura){
		log.debug("aperturaCajaPermitido() : GAP - PERSONALIZACIONES V3 - DÍAS ENTRE APERTURA CAJA");
		
		Boolean result = true;
		Date hoy = getFechaSinHoras(new Date());
		Caja ultimaCajaAbierta = null;
		Integer diasDiferencia = 0;
		try{
			ultimaCajaAbierta = ((CardosoCajasService) cajasService).consultarUltimaCajaAbierta();
		}
		catch(CajasServiceException | CajaEstadoException e){
			String msgError = I18N.getTexto("No se ha podido obtener la última caja abierta para comprobar su fecha de apertura, "
					+ "por lo que se permitirá la apertura.");
			log.error("aperturaCajaPermitido() - " + msgError, e);
		}
		if(ultimaCajaAbierta != null){
			Date fechaAperturaCaja = getFechaSinHoras(ultimaCajaAbierta.getFechaApertura());
			diasDiferencia = getDiasDiferencia(hoy, fechaAperturaCaja);

		}
		result = diasDiferencia <= diasEntreApertura;
		return result;
	}

	/**
	 * Calcula la diferencia en días entre las dos fechas recibidas.
	 * @param hoy
	 * @param fechaAperturaCaja
	 * @return Integer
	 */
	public Integer getDiasDiferencia(Date hoy, Date fechaAperturaCaja){
		log.debug("getDiasDiferencia() : GAP - PERSONALIZACIONES V3 - DÍAS ENTRE APERTURA CAJA");
		
		long diff = Math.abs(hoy.getTime() - fechaAperturaCaja.getTime());
		return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	/**
	 * Devuelve la fecha sin horas.
	 * @param fecha
	 * @return Date
	 */
	public Date getFechaSinHoras(Date fecha){
		log.debug("getFechaSinHoras() : GAP - PERSONALIZACIONES V3 - DÍAS ENTRE APERTURA CAJA");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
}
