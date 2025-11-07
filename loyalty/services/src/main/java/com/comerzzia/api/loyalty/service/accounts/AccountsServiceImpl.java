package com.comerzzia.api.loyalty.service.accounts;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.BadRequestException;
import com.comerzzia.api.core.service.exception.GeneralErrorKeysConstants;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.loyalty.persistence.accounts.Account;
import com.comerzzia.api.loyalty.persistence.accounts.AccountDTO;
import com.comerzzia.api.loyalty.persistence.accounts.AccountExample;
import com.comerzzia.api.loyalty.persistence.accounts.AccountKey;
import com.comerzzia.api.loyalty.persistence.accounts.AccountMapper;
import com.comerzzia.api.loyalty.persistence.accounts.AccountStates;
import com.comerzzia.api.loyalty.persistence.accounts.activities.AccountActivity;
import com.comerzzia.api.loyalty.persistence.cards.Card;
import com.comerzzia.api.loyalty.persistence.cards.CardKey;
import com.comerzzia.api.loyalty.persistence.coupons.Coupon;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTO;
import com.comerzzia.api.loyalty.service.accounts.activities.AccountsActivitiesService;
import com.comerzzia.api.loyalty.service.accounts.dto.BalanceToCouponRequestDTO;
import com.comerzzia.api.loyalty.service.cards.CardsService;
import com.comerzzia.api.loyalty.service.coupons.CouponsService;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.numeros.Numero;

@Service("CuentasService")
public class AccountsServiceImpl implements AccountsService {	
    protected static Logger log = Logger.getLogger(AccountsServiceImpl.class);
	
   @Autowired
   private MessageSourceAccessor messageSourceAccessor;
    
	@Autowired
	AccountMapper mapper;
	
	@Autowired
	AccountsActivitiesService accountsActivitiesService;
	
	@Autowired
	AccountsCounter contadorCuentas;
	
	@Autowired
	CardsService cardsService;
	
   @Autowired
   CouponsService couponsService;
	
	
	@Override
	public Account selectById(Long idCuenta, IDatosSesion datosSesion) {
		return mapper.selectByPrimaryKey(new AccountKey(datosSesion, idCuenta));
	}
	
	@Override
	public AccountDTO selectDTOById(Long idCuenta, IDatosSesion datosSesion) {
		return mapper.selectDTOByPrimaryKey(new AccountKey(datosSesion, idCuenta));
	}
	
	@Override
	public List<Account> selectByExample(AccountExample example) {
		return mapper.selectByExample(example);
	}
	
	@Override
	public List<AccountDTO> selectDTOByExample(AccountExample example) {
		return mapper.selectDTOByExample(example);
	}
		
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Account insert(IDatosSesion datosSesion, Double saldo, Double saldoProvisional) throws ApiException {
		
		try {
			log.debug("insert() - Creando nueva cuenta");
			
			Account record = new Account(datosSesion);
			record.setIdCuentaTarjeta(contadorCuentas.obtenerContador(datosSesion));
			record.setEstado(AccountStates.ESTADO_ACTIVA);
			record.setSaldo(saldo);
			record.setSaldoProvisional(saldoProvisional);
			record.setFechaActualizacion(new Date());
			
			mapper.insert(record);
			
			return record;
      } catch (Exception e) {
         if(e.getCause() instanceof SQLIntegrityConstraintViolationException) { 
            throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, GeneralErrorKeysConstants.ERROR_RECORD_DUPLICATE_KEY, e.getMessage());
         }
         else{
            throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, GeneralErrorKeysConstants.ERROR_RECORD_INSERT, e.getMessage());
         }        
      }   
	}
	
		
	@Override
	public Account updateByPrimaryKey(Account record, IDatosSesion datosSesion) throws ApiException {
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
	public void updateBalance(Long idCuenta, Account sourceAccount, IDatosSesion datosSesion) throws ApiException {
		Account record = selectById(idCuenta, datosSesion);
		
		if (record == null) {
			throw new NotFoundException(LY_ACCOUNT_NOT_FOUND, new String[] { idCuenta.toString() });
		}
		
		updateBalance(record, sourceAccount, datosSesion);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateBalance(Account record, Account sourceAccount, IDatosSesion datosSesion) throws ApiException {

		log.debug("updateBalance() - Inicio del proceso de actualización del saldo de la cuenta con ID: " + record.getIdCuentaTarjeta());
		
		List<AccountActivity> movimientosNoProcesados = accountsActivitiesService.consultarMovimientosNoProcesados(datosSesion, record.getIdCuentaTarjeta());
		
		Double saldo = record.getSaldo();
		Double saldoProvisional = record.getSaldoProvisional();
		for (AccountActivity movimiento : movimientosNoProcesados) {
			if(movimiento.getEstadoMovimiento() == AccountActivity.MOVIMIENTO_DEFINITIVO.shortValue()){
				saldo = Numero.redondea(saldo + Numero.redondea(movimiento.getEntrada() - movimiento.getSalida(), 3), 3);
			}
			else if(movimiento.getEstadoMovimiento() == AccountActivity.MOVIMIENTO_PROVISIONAL.shortValue()){
				saldoProvisional = Numero.redondea(saldoProvisional + Numero.redondea(movimiento.getEntrada() - movimiento.getSalida(), 3), 3);
			}
			
			movimiento.setFechaProceso(new Date());

			accountsActivitiesService.updateByPrimaryKey(movimiento, datosSesion);
		}
		
		record.setSaldo(saldo);
		record.setSaldoProvisional(saldoProvisional);
		record.setFechaActualizacion(new Date());
		
		// Si viene la cuenta origen, se utiliza updateByExample para evitar la concurrencia y guardar la integridad
		
		if(sourceAccount != null) {
			AccountExample example = new AccountExample(datosSesion);
			example.or().andIdCuentaTarjetaEqualTo(record.getIdCuentaTarjeta()).
						 andSaldoEqualTo(sourceAccount.getSaldo()).
						 andSaldoProvisionalEqualTo(sourceAccount.getSaldoProvisional()).
						 andFechaActualizacionEqualTo(sourceAccount.getFechaActualizacion());
			
			if(mapper.updateByExample(record, example) == 0) {
				throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, AccountsService.LY_ACCOUNT_BALANCE_UPDATED_ANOTHER_TRANSACTION, new String[] {record.getIdCuentaTarjeta().toString()});
			}
		}else {
			updateByPrimaryKey(record, datosSesion);
		}

		log.debug("updateBalance() - Saldo actualizado de la cuenta con ID: " + record.getIdCuentaTarjeta());		
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deactivate(Long idCuenta, IDatosSesion datosSesion) throws ApiException {
		Account record = selectById(idCuenta, datosSesion);
		
		if (record == null) {
		   throw new NotFoundException(LY_ACCOUNT_NOT_FOUND, new String[] { idCuenta.toString() });
		}
		
		record.setEstado(AccountStates.ESTADO_INACTIVA);
		
		updateByPrimaryKey(record, datosSesion);
	}

	@Override
   @Transactional(rollbackFor = Exception.class)   
   public CouponDTO convertBalanceToCoupon(IDatosSesion datosSesion, BalanceToCouponRequestDTO balanceToCouponRequestDTO) throws ApiException {
	   // coupon imput data check
	   final NewCoupon newCoupon = balanceToCouponRequestDTO.getNewCoupon();
	   
	   if (newCoupon.getLoyalCustomerId() == null) {
	      throw new BadRequestException(LY_COUPON_WITHOUT_CLIENT, new String[] {});
	   }
	   	   
	   if (newCoupon.getBalance() == null ||
	       (newCoupon.getBalance() != null && newCoupon.getBalance().compareTo(BigDecimal.ZERO) <= 0)) {
	      throw new BadRequestException(LY_COUPON_BALANCE_ERROR, new String[] {});
	   }
	   
      // check balance
      Account account = selectById(balanceToCouponRequestDTO.getAccountId(), datosSesion);
      
      if (account.getSaldo().compareTo(balanceToCouponRequestDTO.getAmount().doubleValue()) < 0) {
         throw new BadRequestException(LY_ACCOUNT_POINTS_BALANCE_INSUFFICIENT, new String[] {});
      }
      
      // check card
      Card card = cardsService.selectByPrimaryKey(new CardKey(datosSesion, balanceToCouponRequestDTO.getCardId()));
      
      if (card.getIdCuentaTarjeta().compareTo(account.getIdCuentaTarjeta()) != 0) {
         throw new BadRequestException(LY_ACCOUNT_INCORRECT_CARD, new String[] {});
      }
      
      // insert coupon
      Coupon coupon = couponsService.insert(datosSesion, balanceToCouponRequestDTO.getNewCoupon());
      
      // insert balance activity
      AccountActivity accountActivity = new AccountActivity();
      accountActivity.setIdTarjeta(balanceToCouponRequestDTO.getCardId());
      accountActivity.setIdCuentaTarjeta(balanceToCouponRequestDTO.getAccountId());
      accountActivity.setEntrada(Double.valueOf(0));
      accountActivity.setSalida(balanceToCouponRequestDTO.getAmount().doubleValue());
      accountActivity.setFecha(new Date());
      
      accountActivity.setIdUsuario(balanceToCouponRequestDTO.getUserId() == null ? datosSesion.getUserId() : balanceToCouponRequestDTO.getUserId());
      accountActivity.setEstadoMovimiento(AccountActivity.MOVIMIENTO_DEFINITIVO);
      accountActivity.setConcepto(messageSourceAccessor.getMessage(LY_ACCOUNT_POINTS_TO_COUPON_MESSAGE,
                                                                   new String[] {balanceToCouponRequestDTO.getAmount().toString(),
                                                                                 coupon.getBalance().toString(),
                                                                                 coupon.getCouponCode()}));
      accountActivity.setDocumento(coupon.getCouponId().toString());
      
      accountsActivitiesService.insert(accountActivity, account, datosSesion);
     
      return couponsService.selectByUk(datosSesion, coupon.getCouponCode());
   }	
}
