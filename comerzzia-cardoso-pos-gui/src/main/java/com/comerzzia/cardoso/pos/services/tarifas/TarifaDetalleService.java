package com.comerzzia.cardoso.pos.services.tarifas;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.persistence.tarifas.TarifaDetalleAnexaBean;
import com.comerzzia.cardoso.pos.persistence.tarifas.TarifaDetalleAnexaExample;
import com.comerzzia.cardoso.pos.persistence.tarifas.TarifaDetalleAnexaKey;
import com.comerzzia.cardoso.pos.persistence.tarifas.TarifaDetalleAnexaMapper;
import com.comerzzia.cardoso.pos.services.tarifas.exception.TarifaDetalleConstraintVException;
import com.comerzzia.cardoso.pos.services.tarifas.exception.TarifaDetalleException;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;
import com.comerzzia.pos.services.core.sesion.Sesion;

/**
 * GAP - DESCUENTO TARIFA
 * TABLA : X_TARIFAS_DET_TBL
 * Contiene los métodos de consultar, insertar, modificar y eliminar.
 */
@Service
public class TarifaDetalleService{

	protected static final Logger log = Logger.getLogger(TarifaDetalleService.class);

    public static final String COD_TARIFA_GENERAL = "GENERAL";
	
    @Autowired
    protected Sesion sesion;
	@Autowired
	protected TarifaDetalleAnexaMapper tarifaDetalleMapper;

	public TarifaDetalleAnexaBean getTarifaDetalleAnexa(String uidActividad, String codArt, TarifaDetalleBean tarifa) throws TarifaDetalleException{
		log.debug("getTarifaDetalleAnexa() : GAP - DESCUENTO TARIFA");
		
		if(StringUtils.isBlank(uidActividad) && StringUtils.isBlank(codArt) && tarifa != null){
			return null;
		}

		TarifaDetalleAnexaBean resultado = null;
		try{
			TarifaDetalleAnexaKey key = new TarifaDetalleAnexaKey();
			key.setUidActividad(uidActividad);
			key.setCodart(codArt);
			key.setCodtar(tarifa.getCodTarifa());
			resultado = tarifaDetalleMapper.selectByPrimaryKey(key);
		}
		catch(Exception e){
			String mensajeError = "Error al consultar los datos de tarifa anexa del artículo " + codArt;
			log.error("getTarifaDetalleAnexa() - " + mensajeError + " : " + e.getMessage(), e);
			throw new TarifaDetalleException(mensajeError, e);
		}
		return resultado;
	}

	public List<TarifaDetalleAnexaBean> getTarifasDetallesAnexa(String uidActividad) throws TarifaDetalleException{
		log.debug("getTarifasDetallesAnexa() : GAP - DESCUENTO TARIFA");
		
		if(StringUtils.isBlank(uidActividad)){
			return null;
		}

		List<TarifaDetalleAnexaBean> resultado = null;
		try{
			TarifaDetalleAnexaExample example = new TarifaDetalleAnexaExample();
			example.or().andUidActividadEqualTo(uidActividad);
			resultado = tarifaDetalleMapper.selectByExample(example);
		}
		catch(Exception e){
			String mensajeError = "Error al consultar los datos de tarifa anexa del artículo para el UID_ACTIVIAD " + uidActividad;
			log.error("getTarifasDetallesAnexa() - " + mensajeError + " : " + e.getMessage(), e);
			throw new TarifaDetalleException(mensajeError, e);
		}
		return resultado;
	}

	public int insertTarifaDetalleAnexa(TarifaDetalleAnexaBean tarifaDetalle) throws TarifaDetalleConstraintVException, TarifaDetalleException{
		log.debug("insertTarifaDetalleAnexa() : GAP - DESCUENTO TARIFA");
		
		if(tarifaDetalle == null){
			return 0;
		}
		else{
			if(StringUtils.isBlank(tarifaDetalle.getUidActividad()) && StringUtils.isBlank(tarifaDetalle.getCodart())){
				return 0;
			}
		}

		Integer resultado = 0;
		try{
			resultado = tarifaDetalleMapper.insert(tarifaDetalle);
		}
		catch(Exception e){
			String mensajeError = "";
			if(e instanceof SQLIntegrityConstraintViolationException || e.getCause() instanceof SQLIntegrityConstraintViolationException){
				mensajeError = "No se pudo borrar los datos de tarifa anexa del artículo " + tarifaDetalle.getCodart() + ", porque existen registros asociados.";
				log.error("insertTarifaDetalleAnexa() - " + mensajeError, e);
				throw new TarifaDetalleConstraintVException(mensajeError, e);
			}
			else{
				mensajeError = "Error al insertar los datos de tarifa anexa del artículo " + tarifaDetalle.getCodart();
				log.error("insertTarifaDetalleAnexa() - " + mensajeError + " : " + e.getMessage(), e);
				throw new TarifaDetalleException(mensajeError, e);
			}
		}
		return resultado;
	}

	public int updateTarifaDetalleAnexa(TarifaDetalleAnexaBean tarifaDetalle) throws TarifaDetalleException{
		log.debug("updateTarifaDetalleAnexa() : GAP - DESCUENTO TARIFA");
		
		if(tarifaDetalle == null){
			return 0;
		}
		else{
			if(StringUtils.isBlank(tarifaDetalle.getUidActividad()) && StringUtils.isBlank(tarifaDetalle.getCodart())){
				return 0;
			}
		}

		Integer resultado = 0;
		try{
			resultado = tarifaDetalleMapper.updateByPrimaryKey(tarifaDetalle);
		}
		catch(Exception e){
			String mensajeError = "Error al modificar los datos de tarifa anexa del artículo " + tarifaDetalle.getCodart();
			log.error("updateTarifaDetalleAnexa() - " + mensajeError + " : " + e.getMessage(), e);
			throw new TarifaDetalleException(mensajeError, e);
		}
		return resultado;
	}

	public int deleteTarifaDetalleAnexa(String uidActividad, String codArt) throws TarifaDetalleConstraintVException, TarifaDetalleException{
		log.debug("deleteTarifaDetalleAnexa() : GAP - DESCUENTO TARIFA");
		
		if(StringUtils.isBlank(uidActividad) && StringUtils.isBlank(codArt)){
			return 0;
		}

		Integer resultado = 0;
		try{
			TarifaDetalleAnexaExample example = new TarifaDetalleAnexaExample();
			example.or().andUidActividadEqualTo(uidActividad).andCodartEqualTo(codArt);
			resultado = tarifaDetalleMapper.deleteByExample(example);
		}
		catch(Exception e){
			String mensajeError = "";
			if(e instanceof SQLIntegrityConstraintViolationException || e.getCause() instanceof SQLIntegrityConstraintViolationException){
				mensajeError = "No se ha podido borrar los datos de tarifa anexa del artículo " + codArt + ", porque existen registros asociados.";
				log.error("deleteTarifaDetalleAnexa() - " + mensajeError + " : " + e.getMessage(), e);
				throw new TarifaDetalleConstraintVException(mensajeError, e);
			}
			else{
				mensajeError = "Error al eliminar los datos de tarifa anexa del artículo " + codArt;
				log.error("deleteTarifaDetalleAnexa() - " + mensajeError + " : " + e.getMessage(), e);
				throw new TarifaDetalleException(mensajeError, e);
			}
		}
		return resultado;
	}

}
