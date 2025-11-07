package com.comerzzia.api.loyalty.service.cards;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.BadRequestException;
import com.comerzzia.api.core.service.exception.GeneralErrorKeysConstants;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.loyalty.persistence.accounts.AccountMapper;
import com.comerzzia.api.loyalty.persistence.cards.Card;
import com.comerzzia.api.loyalty.persistence.cards.CardDTO;
import com.comerzzia.api.loyalty.persistence.cards.CardExample;
import com.comerzzia.api.loyalty.persistence.cards.CardKey;
import com.comerzzia.api.loyalty.persistence.cards.CardMapper;
import com.comerzzia.api.loyalty.persistence.cards.CardUK;
import com.comerzzia.api.loyalty.persistence.cardsTypes.CardsTypesMapper;
import com.comerzzia.api.loyalty.service.accounts.AccountsService;
import com.comerzzia.api.loyalty.service.accounts.activities.AccountsActivitiesService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class CardsServiceImpl implements CardsService {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CardMapper mapper;

	@Autowired
	AccountMapper cuentaMapper;

	@Autowired
	AccountsService cuentasService;

	@Autowired
	CardsCounter contadorTarjetas;

	@Autowired	
	AccountsActivitiesService accountsActivitiesService;

	@Autowired
	CardsTypesMapper tipoTarjetaMapper;
	
	@Override
	public List<Card> selectByExample(CardExample example) {
		return mapper.selectByExample(example);
	}
	
	@Override
	public List<CardDTO> selectDTOByExample(CardExample example) {
		return mapper.selectDTOByExample(example);
	}
	
	@Override
	public Card selectByPrimaryKey(CardKey key) throws NotFoundException {
        Card record = mapper.selectByPrimaryKey(key);
		
		if (record == null) {
			throw new NotFoundException(ApiException.STATUS_RESPONSE_ERROR_NOT_FOUND, GeneralErrorKeysConstants.ERROR_RECORD_NOT_FOUND, "");
		}
		return record;
	}
	
	@Override
	public CardDTO selectDTOByPrimaryKey(CardKey key) throws NotFoundException {
        CardDTO record = mapper.selectDTOByPrimaryKey(key);
		
		if (record == null) {
			throw new NotFoundException(ApiException.STATUS_RESPONSE_ERROR_NOT_FOUND, GeneralErrorKeysConstants.ERROR_RECORD_NOT_FOUND, "");
		}
		return record;
	}
	
	@Override
	public Card selectByUniqueKey(CardUK key) throws NotFoundException {
        Card record = mapper.selectByUniqueKey(key);
		
		if (record == null) {
			throw new NotFoundException(ApiException.STATUS_RESPONSE_ERROR_NOT_FOUND, GeneralErrorKeysConstants.ERROR_RECORD_NOT_FOUND, "");
		}
		
		return record;
	}
	
	@Override
	public CardDTO selectDTOByUniqueKey(CardUK key) throws NotFoundException {
        CardDTO record = mapper.selectDTOByUniqueKey(key);
		
		if (record == null) {
			throw new NotFoundException(ApiException.STATUS_RESPONSE_ERROR_NOT_FOUND, GeneralErrorKeysConstants.ERROR_RECORD_NOT_FOUND, "");
		}
		
		return record;
	}	
	
	@Override
	public CardDTO selectByCardNumber(String cardNumber, IDatosSesion datosSesion) throws NotFoundException {
		return selectDTOByUniqueKey(new CardUK(datosSesion, cardNumber)); 
	}	
	
	@Override
	public Card updateByPrimaryKey(Card record) throws ApiException {
		try {
			if (mapper.updateByPrimaryKey(record) == 0) {
				throw new NotFoundException(ApiException.STATUS_RESPONSE_ERROR_NOT_FOUND, GeneralErrorKeysConstants.ERROR_RECORD_NOT_FOUND, "");
			}			
		}
		catch (ApiException e) {
			throw e;
		}
		catch (Exception e) {
			if(e.getCause() instanceof SQLIntegrityConstraintViolationException) { 
				throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, GeneralErrorKeysConstants.ERROR_RECORD_DUPLICATE_KEY, e.getMessage());
			}
			else{
				throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, GeneralErrorKeysConstants.ERROR_RECORD_UPDATE, e.getMessage());
			}			
		}
		
		return record;
	}	
	
	@Override
	@Transactional(rollbackFor = Exception.class)		
	public void insert(Card record) throws ApiException {
		try {
			record.setIdTarjeta(contadorTarjetas.obtenerContador(record.getDatosSesion()));
			mapper.insert(record);		
		}
		catch (Exception e) {
			if(e.getCause() instanceof SQLIntegrityConstraintViolationException) { 
				throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, GeneralErrorKeysConstants.ERROR_RECORD_DUPLICATE_KEY, e.getMessage());
			}
			else{
				throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, GeneralErrorKeysConstants.ERROR_RECORD_INSERT, e.getMessage());
			}			
		}		
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class)	
	public void deactivate(CardKey key) throws ApiException {
		Card record = selectByPrimaryKey(key);
		
		if (record.getFechaBaja() == null) {
			record.setFechaBaja(new Date());
		}
		
		updateByPrimaryKey(record);

		// Si tiene cuenta asignada y la cuenta sólo tiene esta tarjeta asignada,
		// inactivar la cuenta
		if (record.getIdCuentaTarjeta() != null) {
			CardExample example = new CardExample(record.getDatosSesion());
			example.or().andIdCuentaTarjetaEqualTo(record.getIdCuentaTarjeta()).andFechaBajaIsNull();			
					
			if (selectByExample(example).isEmpty()) {
			   try {
				   cuentasService.deactivate(record.getIdCuentaTarjeta(), record.getDatosSesion());
				} catch (NotFoundException e) {
					throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, ACCOUNT_NOT_FOUND, record.getIdCuentaTarjeta().toString());
				}
			}
		}
	}

//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public void linkCards(Long idTarjetaOrigen, Long idTarjetaDestino, IDatosSesion datosSesion) {
//		try {
//			// Consultamos la tarjeta origen
//			CardKey keyTarjOrigen = new CardKey(datosSesion);
//			keyTarjOrigen.setIdTarjeta(idTarjetaOrigen);
//			TarjetaBean tarjetaOrigen = mapper.selectByPrimaryKey(keyTarjOrigen);
//
//			// Consultamos la tarjeta destino
//			CardKey keyTarjDestino = new CardKey(datosSesion);
//			keyTarjDestino.setIdTarjeta(idTarjetaDestino);
//			TarjetaBean tarjetaDestino = mapper.selectByPrimaryKey(keyTarjDestino);
//
//			if (!tarjetaOrigen.getCodTipoTarj().equals(tarjetaDestino.getCodTipoTarj())) {
//				throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, EQUALS_ORIGIN_AND_DESTINATION);
//			}
//
//			// Obtenemos los datos de la cuenta origen y destino
//			CuentaBean cuentaOrigen = cuentaMapper.selectByPrimaryKey(new CuentaKey(datosSesion, tarjetaOrigen.getIdCuentaTarjeta()));
//			CuentaBean cuentaDestino = cuentaMapper.selectByPrimaryKey(new CuentaKey(datosSesion, tarjetaDestino.getIdCuentaTarjeta()));
//
//			if (cuentaDestino != null && cuentaOrigen != null) {
//				if (cuentaOrigen.getIdCuentaTarjeta() == cuentaDestino.getIdCuentaTarjeta()) {
//					throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, ALREADY_LINKED);
//				}
//			}
//			
//			if (cuentaDestino == null) {
//				
//			}
//
//			if (cuentaOrigen == null && cuentaDestino == null) {
//				String msg = "La tarjeta de destino no tiene cuenta asignada";
//				log.info("vincularTarjetas() - " + msg);
//				throw new VincularTarjetaException(msg);
//			}
//			if (cuentaOrigen != null && cuentaDestino == null) {
//				String msg = "La tarjeta de destino no tiene cuenta asignada";
//				log.info("vincularTarjetas() - " + msg);
//				throw new VincularTarjetaException(msg);
//			}
//
//			if (cuentaOrigen == null && cuentaDestino != null) {
//				TarjetaBean tarjeta = new TarjetaBean(datosSesion);
//				tarjeta.setIdTarjeta(tarjetaOrigen.getIdTarjeta());
//				tarjeta.setFechaActivacion(new Date());
//				tarjeta.setIdCuentaTarjeta(cuentaDestino.getIdCuentaTarjeta());
//				mapper.updateByPrimaryKeySelective(tarjeta);
//			}
//
//			if (cuentaOrigen != null && cuentaDestino != null) {
//				Double saldo = Numero.redondea(cuentaOrigen.getSaldo() + cuentaDestino.getSaldo(), 3);
//				cuentaDestino.setSaldo(saldo);
//				cuentaDestino.setFechaActualizacion(new Date());
//				cuentaDestino.setUidInstancia(datosSesion.getUidInstancia());
//				cuentaMapper.updateByPrimaryKey(cuentaDestino);
//
//				// Indicamos que la cuenta destino cambia de estado
//				cuentaOrigen.setEstado(EstadosCuentas.ESTADO_INACTIVA_VINCULACION);
//				cuentaMapper.updateByPrimaryKey(cuentaDestino);
//
//				tarjetaOrigen.setIdCuentaTarjeta(tarjetaDestino.getIdCuentaTarjeta());
//				tarjetaOrigen.setUidInstancia(datosSesion.getUidInstancia());
//				mapper.updateByPrimaryKey(tarjetaOrigen);
//			}
//		} catch (Exception e) {
//			log.debug("vincularTarjetas() - deshaciendo transacción");
//
//			String msg = "Error vinculando tarjetas: " + e.getMessage();
//			log.error("vincularTarjetas() - " + msg);
//
//			throw new TarjetaException(msg, e);
//		}
//	}
//
//	/**
//	 * Realiza una transferencia de la cantidad indicada entre dos tarjetas que
//	 * tengan cuentas asociadas y que además sean distintas.
//	 * 
//	 * @param numeroTarjetaOrigen
//	 * @param numeroTarjetaDestino
//	 * @param cantidad
//	 * @param datosSesion
//	 * @throws TarjetaException
//	 * @throws TarjetaConstraintViolationException
//	 */
//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public void transferBalance(FidelizadoBean fidelizado, String numeroTarjetaOrigen,
//			String numeroTarjetaDestino, Double cantidad, IDatosSesion datosSesion) {
//
//		log.debug("transferirSaldoEntreTarjetas() - Inicio de la transferencia desde la tarjeta número "
//				+ numeroTarjetaOrigen + " a la tarjeta con número " + numeroTarjetaDestino);
//
//		try {
//			log.debug(
//					"transferirSaldoEntreTarjetas() - Comprobando los requisitos necesarios para que se pueda realizar la transferencia");
//
//			// Comprobar que las tarjetas son distintas
//			if (numeroTarjetaOrigen.equalsIgnoreCase(numeroTarjetaDestino)) {
//				throw new TarjetaConstraintViolationException("Las tarjetas origen y destino deben ser distintas");
//			}
//
//			// Obtener las tarjetas por su número						
//			CardDTO tarjetaOrigen = selectByUniqueKey(new CardUK(datosSesion, numeroTarjetaOrigen));
//			
//			if (tarjetaOrigen == null) {
//				throw new TarjetaConstraintViolationException(
//						"La tarjeta origen con número " + numeroTarjetaOrigen + " no existe");
//			}
//			
//			CardDTO tarjetaDestino = selectByUniqueKey(new CardUK(datosSesion, numeroTarjetaDestino));
//			
//			if (tarjetaDestino == null) {
//				throw new TarjetaConstraintViolationException(
//						"La tarjeta destino con número " + numeroTarjetaOrigen + " no existe");
//			}
//
//			// Comprobar que el ordenante de la transferencia es titular de la tarjeta
//			// origen
//			if (tarjetaOrigen.getIdFidelizado() != fidelizado.getIdFidelizado()) {
//				throw new TarjetaConstraintViolationException(
//						"Usted no es el titular de la tarjeta origen con número " + numeroTarjetaOrigen);
//			}
//
//			if (!tarjetaOrigen.getCodTipoTarj().equals(tarjetaDestino.getCodTipoTarj())) {
//				throw new TarjetaConstraintViolationException("Las tarjetas Origen y destino no son del mismo tipo");
//			}
//
//			// Comprobar que ambas tarjetas tienen cuenta asociada y que no comparten la
//			// misma cuenta
//			if (tarjetaOrigen.getIdCuentaTarjeta() == null) {
//				throw new TarjetaConstraintViolationException(
//						"La tarjeta origen con número " + numeroTarjetaOrigen + " aún no ha sido activada");
//			} else if (tarjetaOrigen.getIdCuentaTarjeta().equals(tarjetaDestino.getIdCuentaTarjeta())) {
//				throw new TarjetaConstraintViolationException(
//						"No es posible transferir saldo entre ambas tarjetas porque están vinculadas");
//			}
//
//			// Si la cantidad introducida es mayor que el saldo de la tarjeta origen lanzar
//			// error
//			if (tarjetaOrigen.getCuenta().getSaldo() < cantidad) {
//				throw new TarjetaConstraintViolationException(
//						"La tarjeta origen con número " + numeroTarjetaOrigen + " contiene un saldo insuficiente ("
//								+ tarjetaOrigen.getCuenta().getSaldo() + ") para la cantidad indicada a transferir");
//			}
//
//			// Generar los movimientos para ambas tarjetas
//			MovimientoBean movimientoTarjetaOrigen = new MovimientoBean(datosSesion);
//			movimientoTarjetaOrigen.setIdCuentaTarjeta(tarjetaOrigen.getIdCuentaTarjeta());
//			movimientoTarjetaOrigen.setIdTarjeta(tarjetaOrigen.getIdTarjeta());
//			movimientoTarjetaOrigen.setFecha(new Date());
//			movimientoTarjetaOrigen.setEntrada(0D);
//			movimientoTarjetaOrigen.setSalida(cantidad);
//			movimientoTarjetaOrigen.setConcepto("Transferencia de saldo");
//			accountsActivitiesService.insert(movimientoTarjetaOrigen, datosSesion);
//
//			MovimientoBean movimientoTarjetaDestino = new MovimientoBean(datosSesion);
//			movimientoTarjetaDestino.setIdCuentaTarjeta(tarjetaDestino.getIdCuentaTarjeta());
//			movimientoTarjetaDestino.setIdTarjeta(tarjetaDestino.getIdTarjeta());
//			movimientoTarjetaDestino.setFecha(new Date());
//			movimientoTarjetaDestino.setEntrada(cantidad);
//			movimientoTarjetaDestino.setSalida(0D);
//			movimientoTarjetaDestino.setConcepto("Transferencia de saldo");
//			accountsActivitiesService.insert(movimientoTarjetaDestino, datosSesion);
//
//			log.debug("transferirSaldoEntreTarjetas() - Transferencia realizada");
//		} catch (TarjetaConstraintViolationException e) {
//			log.debug("transferirSaldoEntreTarjetas() - deshaciendo transacción");
//
//			String msg = "Error transfiriendo saldo entre tarjetas: " + e.getMessage();
//			log.error("transferirSaldoEntreTarjetas() - " + msg);
//
//			throw e;
//		} catch (Exception e) {
//			if (e instanceof TarjetaException) {
//				throw  e;				
//			} else {
//				String msg = "Error transfiriendo saldo entre tarjetas: " + e.getMessage();
//				log.error("transferirSaldoEntreTarjetas() - " + msg);
//	
//				throw new TarjetaException(msg, e);
//			}
//		}
//	}	
}
