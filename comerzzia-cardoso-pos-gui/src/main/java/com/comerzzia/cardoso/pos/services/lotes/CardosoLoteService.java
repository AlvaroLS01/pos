package com.comerzzia.cardoso.pos.services.lotes;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.persistence.lotes.CardosoAtributosAdicionalesArticuloBean;
import com.comerzzia.cardoso.pos.persistence.lotes.CardosoAtributosAdicionalesArticuloExample;
import com.comerzzia.cardoso.pos.persistence.lotes.CardosoAtributosAdicionalesArticuloKey;
import com.comerzzia.cardoso.pos.persistence.lotes.CardosoAtributosAdicionalesArticuloMapper;
import com.comerzzia.cardoso.pos.persistence.lotes.anexa.CardosoLoteArticuloBean;
import com.comerzzia.cardoso.pos.persistence.lotes.anexa.CardosoLoteArticuloExample;
import com.comerzzia.cardoso.pos.persistence.lotes.anexa.CardosoLoteArticuloKey;
import com.comerzzia.cardoso.pos.persistence.lotes.anexa.CardosoLoteArticuloMapper;
import com.comerzzia.cardoso.pos.services.lotes.exception.CardosoLoteConstraintVException;
import com.comerzzia.cardoso.pos.services.lotes.exception.CardosoLoteException;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 * TABLAS : X_LOTE_ARTICULO_TBL, X_ARTICULOS_LOTE_TBL
 * Contiene los métodos de consultar, insertar, modificar y eliminar.
 */
@Service
public class CardosoLoteService{

	protected static final Logger log = Logger.getLogger(CardosoLoteService.class);
	
	@Autowired
    protected CardosoLoteArticuloMapper loteArticuloMapper;
	@Autowired
    protected CardosoAtributosAdicionalesArticuloMapper articuloLoteMapper;
	
	/* ====================================================================================================================== */
	/* ================================================= X_LOTE_ARTICULO_TBL ================================================ */
	/* ====================================================================================================================== */
	
	public CardosoLoteArticuloBean getLoteArticulo(String uidActividad, String codArt) throws CardosoLoteException{
		log.debug("getLoteArticulo() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(StringUtils.isBlank(uidActividad) && StringUtils.isBlank(codArt)){
			return null;
		}
		
		CardosoLoteArticuloBean resultado = null;
		try{
			CardosoLoteArticuloKey key = new CardosoLoteArticuloKey();
			key.setUidActividad(uidActividad);
			key.setCodart(codArt);
			resultado = loteArticuloMapper.selectByPrimaryKey(key);
		}
		catch(Exception e){
			String mensajeError = "Error al consultar el lote del artículo " + codArt;
			log.error("getLoteArticulo() - " + mensajeError + " : " + e.getMessage(), e);
			throw new CardosoLoteException(mensajeError, e);
		}
		return resultado;
	}
	
	public List<CardosoLoteArticuloBean> getLotesArticulos(String uidActividad) throws CardosoLoteException{
		log.debug("getLotesArticulos() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(StringUtils.isBlank(uidActividad)){
			return null;
		}
		
		List<CardosoLoteArticuloBean> resultado = null;
		try{
			CardosoLoteArticuloExample example = new CardosoLoteArticuloExample();
			example.or().andUidActividadEqualTo(uidActividad);
			resultado = loteArticuloMapper.selectByExample(example);
		}
		catch(Exception e){
			String mensajeError = "Error al consultar los lotes de artículos para el UID_ACTIVIAD " + uidActividad;
			log.error("getLotesArticulos() - " + mensajeError + " : " + e.getMessage(), e);
			throw new CardosoLoteException(mensajeError, e);
		}
		return resultado;
	}
	
	public int insertLoteArticulo(CardosoLoteArticuloBean lote) throws CardosoLoteConstraintVException, CardosoLoteException{
		log.debug("insertLoteArticulo() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(lote == null){
			return 0;
		}
		else{
			if(StringUtils.isBlank(lote.getUidActividad()) && StringUtils.isBlank(lote.getCodart())){
				return 0;
			}
		}
		
		Integer resultado = 0;
		try{
			resultado = loteArticuloMapper.insert(lote);
		}
		catch(Exception e){
			String mensajeError = "";
			if(e instanceof SQLIntegrityConstraintViolationException || e.getCause() instanceof SQLIntegrityConstraintViolationException){
				mensajeError = "Ya existe un arqueo de cierre día con el ID_CIERRE indicado : " + e.getMessage();
				log.error("insertLoteArticulo() - " + mensajeError, e);
				throw new CardosoLoteConstraintVException(mensajeError, e);
			}
			else{
				mensajeError = "Error al insertar el lote del artículo " + lote.getCodart();
				log.error("insertLoteArticulo() - " + mensajeError + " : " + e.getMessage(), e);
				throw new CardosoLoteException(mensajeError, e);
			}
		}
		return resultado;
	}
	
	public int updateLoteArticulo(CardosoLoteArticuloBean lote) throws CardosoLoteException{
		log.debug("updateLoteArticulo() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(lote == null){
			return 0;
		}
		else{
			if(StringUtils.isBlank(lote.getUidActividad()) && StringUtils.isBlank(lote.getCodart())){
				return 0;
			}
		}
		
		Integer resultado = 0;
		try{
			resultado = loteArticuloMapper.updateByPrimaryKey(lote);
		}
		catch(Exception e){
			String mensajeError = "Error al modificar el lote del artículo " + lote.getCodart();
			log.error("updateLoteArticulo() - " + mensajeError + " : " + e.getMessage(), e);
			throw new CardosoLoteException(mensajeError, e);
		}
		return resultado;
	}
	
	public int deleteLoteArticulo(String uidActividad, String codArt) throws CardosoLoteConstraintVException, CardosoLoteException{
		log.debug("deleteLoteArticulo() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(StringUtils.isBlank(uidActividad) && StringUtils.isBlank(codArt)){
			return 0;
		}
		
		Integer resultado = 0;
		try{
			CardosoLoteArticuloExample example = new CardosoLoteArticuloExample();
			example.or().andUidActividadEqualTo(uidActividad)
						.andCodartEqualTo(codArt);
			resultado = loteArticuloMapper.deleteByExample(example);
		}
		catch(Exception e){
			String mensajeError = "";
			if(e instanceof SQLIntegrityConstraintViolationException || e.getCause() instanceof SQLIntegrityConstraintViolationException){
				mensajeError = "No se ha podido borrar el lote de artículo" + codArt + ", porque existen registros asociados.";
				log.error("deleteLoteArticulo() - " + mensajeError + " : " + e.getMessage(), e);
				throw new CardosoLoteConstraintVException(mensajeError, e);
			}
			else{
				mensajeError = "Error al eliminar el lote del artículo " + codArt;
				log.error("deleteLoteArticulo() - " + mensajeError + " : " + e.getMessage(), e);
				throw new CardosoLoteException(mensajeError, e);
			}
		}
		return resultado;
	}
	
	/* ====================================================================================================================== */
	/* ================================================= X_ARTICULOS_LOTE_TBL =============================================== */
	/* ====================================================================================================================== */
	
	public CardosoAtributosAdicionalesArticuloBean getAtributosAdicionalesArticulo(String uidActividad, String codArt) throws CardosoLoteException{
		log.debug("CardosoAtributosAdicionalesArticuloBean() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(StringUtils.isBlank(uidActividad) && StringUtils.isBlank(codArt)){
			return null;
		}
		
		CardosoAtributosAdicionalesArticuloBean resultado = null;
		try{
			CardosoAtributosAdicionalesArticuloKey key = new CardosoAtributosAdicionalesArticuloKey();
			key.setUidActividad(uidActividad);
			key.setCodart(codArt);
			resultado = articuloLoteMapper.selectByPrimaryKey(key);
		}
		catch(Exception e){
			String mensajeError = "Error al consultar el lote del artículo " + codArt;
			log.error("getAtributosAdicionalesArticulo() - " + mensajeError + " : " + e.getMessage(), e);
			throw new CardosoLoteException(mensajeError, e);
		}
		return resultado;
	}
	
	public List<CardosoAtributosAdicionalesArticuloBean> getAtributosAdicionalesArticulos(String uidActividad) throws CardosoLoteException{
		log.debug("CardosoAtributosAdicionalesArticuloBean() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(StringUtils.isBlank(uidActividad)){
			return null;
		}
		
		List<CardosoAtributosAdicionalesArticuloBean> resultado = null;
		try{
			CardosoAtributosAdicionalesArticuloExample example = new CardosoAtributosAdicionalesArticuloExample();
			example.or().andUidActividadEqualTo(uidActividad);
			resultado = articuloLoteMapper.selectByExample(example);
		}
		catch(Exception e){
			String mensajeError = "Error al consultar los lotes de artículos para el UID_ACTIVIAD " + uidActividad;
			log.error("getAtributosAdicionalesArticulos() - " + mensajeError + " : " + e.getMessage(), e);
			throw new CardosoLoteException(mensajeError, e);
		}
		return resultado;
	}
	
	public int insertAtributosAdicionalesArticulo(CardosoAtributosAdicionalesArticuloBean lote) throws CardosoLoteConstraintVException, CardosoLoteException{
		log.debug("insertAtributosAdicionalesArticulo() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(lote == null){
			return 0;
		}
		else{
			if(StringUtils.isBlank(lote.getUidActividad()) && StringUtils.isBlank(lote.getCodart())){
				return 0;
			}
		}
		
		Integer resultado = 0;
		try{
			resultado = articuloLoteMapper.insert(lote);
		}
		catch(Exception e){
			String mensajeError = "";
			if(e instanceof SQLIntegrityConstraintViolationException || e.getCause() instanceof SQLIntegrityConstraintViolationException){
				mensajeError = "Ya existe un arqueo de cierre día con el ID_CIERRE indicado : " + e.getMessage();
				log.error("insertAtributosAdicionalesArticulo() - " + mensajeError, e);
				throw new CardosoLoteConstraintVException(mensajeError, e);
			}
			else{
				mensajeError = "Error al insertar el lote del artículo " + lote.getCodart();
				log.error("insertAtributosAdicionalesArticulo() - " + mensajeError + " : " + e.getMessage(), e);
				throw new CardosoLoteException(mensajeError, e);
			}
		}
		return resultado;
	}
	
	public int updateAtributosAdicionalesArticulo(CardosoAtributosAdicionalesArticuloBean lote) throws CardosoLoteException{
		log.debug("updateAtributosAdicionalesArticulo() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(lote == null){
			return 0;
		}
		else{
			if(StringUtils.isBlank(lote.getUidActividad()) && StringUtils.isBlank(lote.getCodart())){
				return 0;
			}
		}
		
		Integer resultado = 0;
		try{
			resultado = articuloLoteMapper.updateByPrimaryKey(lote);
		}
		catch(Exception e){
			String mensajeError = "Error al modificar el lote del artículo " + lote.getCodart();
			log.error("updateAtributosAdicionalesArticulo() - " + mensajeError + " : " + e.getMessage(), e);
			throw new CardosoLoteException(mensajeError, e);
		}
		return resultado;
	}
	
	public int deleteAtributosAdicionalesArticulo(String uidActividad, String codArt) throws CardosoLoteConstraintVException, CardosoLoteException{
		log.debug("deleteAtributosAdicionalesArticulo() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(StringUtils.isBlank(uidActividad) && StringUtils.isBlank(codArt)){
			return 0;
		}
		
		Integer resultado = 0;
		try{
			CardosoAtributosAdicionalesArticuloExample example = new CardosoAtributosAdicionalesArticuloExample();
			example.or().andUidActividadEqualTo(uidActividad)
						.andCodartEqualTo(codArt);
			resultado = articuloLoteMapper.deleteByExample(example);
		}
		catch(Exception e){
			String mensajeError = "";
			if(e instanceof SQLIntegrityConstraintViolationException || e.getCause() instanceof SQLIntegrityConstraintViolationException){
				mensajeError = "No se ha podido borrar el lote de artículo" + codArt + ", porque existen registros asociados.";
				log.error("deleteAtributosAdicionalesArticulo() - " + mensajeError + " : " + e.getMessage(), e);
				throw new CardosoLoteConstraintVException(mensajeError, e);
			}
			else{
				mensajeError = "Error al eliminar el lote del artículo " + codArt;
				log.error("deleteAtributosAdicionalesArticulo() - " + mensajeError + " : " + e.getMessage(), e);
				throw new CardosoLoteException(mensajeError, e);
			}
		}
		return resultado;
	}
	
}
