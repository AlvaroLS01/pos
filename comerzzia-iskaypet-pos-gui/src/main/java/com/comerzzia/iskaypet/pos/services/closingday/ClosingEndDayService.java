package com.comerzzia.iskaypet.pos.services.closingday;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.iskaypet.pos.persistence.closingday.HeaderClosingEndDay;
import com.comerzzia.iskaypet.pos.persistence.closingday.HeaderClosingEndDayExample;
import com.comerzzia.iskaypet.pos.persistence.closingday.HeaderClosingEndDayExample.Criteria;
import com.comerzzia.iskaypet.pos.persistence.closingday.HeaderClosingEndDayMapper;
import com.comerzzia.iskaypet.pos.services.cajas.IskaypetCajasService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

/**
 * GAP27.2 - AMPLIACIÓN DEL CIERRE DE FIN DE DÍA
 */
@Service
public class ClosingEndDayService{
	
	private static final Logger log = Logger.getLogger(ClosingEndDayService.class);
	
	public static final String ERROR_CIERREZ_GENERADO_HOY = "No es posible realizar la apertura de caja. Existe un cierre Z registrado para hoy.";
	public static final String ERROR_CIERREZ_NO_GENERADO_ANTERIOR = "No es posible realizar la apertura de caja. No se ha realizado el cierre Z correspondiente al día ";

	@Autowired
	protected Sesion sesion;
	@Autowired
	protected HeaderClosingEndDayMapper cabFinMapper;
	@Autowired
	protected IskaypetCajasService cajasService;

	public boolean comprobarCierreFinDia(){
		Date fechaActual = new Date();
		fechaActual = setFechaPrimeraHora(fechaActual);

		// Comprobamos que exista un fin de día que no esté anulado. En ese caso no se podrá abrir la caja
		HeaderClosingEndDayExample example = new HeaderClosingEndDayExample();
		Criteria criteria = example.createCriteria();
		criteria.andActivityUidEqualTo(sesion.getAplicacion().getUidActividad());
		criteria.andEndDayDateEqualTo(fechaActual);
		criteria.andStatusIdNotEqualTo(-1); 
		List<HeaderClosingEndDay> cierresFinDia = cabFinMapper.selectByExample(example);

		return cierresFinDia.isEmpty();
	}

	public Date setFechaPrimeraHora(Date fechaActual){
		Calendar c = Calendar.getInstance();
		c.setTime(fechaActual);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		fechaActual = c.getTime();
		return fechaActual;
	}
	
	public String lastClosingEndDayConfirmed() throws Exception{
		String msgValidationClosingEndDay = "";
		try{
			// Sacamos la fecha del último cierre de caja de los días anteriores al del hoy.
		/*	Caja cashJournal = cajasService.consultarUltimaCajaCerradaIskaypet();
			Date date = cashJournal.getFechaApertura();*/
			Date date = cajasService.consultarFechaUltimaAperturaAnteriorHoy();
			if(date != null){
				date = setFechaPrimeraHora(date);
				
				// Consultamos para la fecha de cierre si tiene un cierre de fin de día confirmado.
				HeaderClosingEndDayExample example = new HeaderClosingEndDayExample();
				Criteria criteria = example.createCriteria();
				criteria.andActivityUidEqualTo(sesion.getAplicacion().getUidActividad());
				criteria.andEndDayDateEqualTo(date);
				criteria.andStatusIdEqualTo(1);
				List<HeaderClosingEndDay> results = cabFinMapper.selectByExample(example);
				
				if(results == null || results.isEmpty()){
					msgValidationClosingEndDay = I18N.getTexto(ERROR_CIERREZ_NO_GENERADO_ANTERIOR) + new SimpleDateFormat("dd/MM/yyyy").format(date);
				}
			}
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al consultar los datos para comprobar si existe un cierre confirmado previo : ") + e.getMessage();
			log.error("lastClosingEndDayConfirmed() - " + msgError, e);
			throw e;
		}
		return msgValidationClosingEndDay;
	}
}
