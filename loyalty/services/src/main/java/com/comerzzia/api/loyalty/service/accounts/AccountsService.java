package com.comerzzia.api.loyalty.service.accounts;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.accounts.Account;
import com.comerzzia.api.loyalty.persistence.accounts.AccountDTO;
import com.comerzzia.api.loyalty.persistence.accounts.AccountExample;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTO;
import com.comerzzia.api.loyalty.service.accounts.dto.BalanceToCouponRequestDTO;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface AccountsService {
   public static final String LY_ACCOUNT_NOT_FOUND = "LY_ACCOUNT_NOT_FOUND";
   public static final String LY_COUPON_WITHOUT_CLIENT = "LY_COUPON_WITHOUT_CLIENT";
   public static final String LY_COUPON_BALANCE_ERROR = "LY_COUPON_BALANCE_ERROR";
   public static final String LY_ACCOUNT_POINTS_BALANCE_INSUFFICIENT = "LY_ACCOUNT_POINTS_BALANCE_INSUFFICIENT";
   public static final String LY_ACCOUNT_INCORRECT_CARD = "LY_ACCOUNT_INCORRECT_CARD";
   public static final String LY_ACCOUNT_POINTS_TO_COUPON_MESSAGE = "LY_ACCOUNT_POINTS_TO_COUPON_MESSAGE";
   public static final String LY_ACCOUNT_BALANCE_UPDATED_ANOTHER_TRANSACTION = "LY_ACCOUNT_BALANCE_UPDATED_ANOTHER_TRANSACTION";

	Account selectById(Long idCuenta, IDatosSesion datosSesion);

	Account insert(IDatosSesion datosSesion, Double saldo, Double saldoProvisional) throws ApiException;

	Account updateByPrimaryKey(Account record, IDatosSesion datosSesion) throws ApiException;	

	void updateBalance(Account record, Account sourceAccount, IDatosSesion datosSesion) throws ApiException;

	void updateBalance(Long idCuenta, Account sourceAccount, IDatosSesion datosSesion) throws ApiException;

	void deactivate(Long idCuenta, IDatosSesion datosSesion) throws ApiException;

	AccountDTO selectDTOById(Long idCuenta, IDatosSesion datosSesion);

	List<Account> selectByExample(AccountExample example);

	List<AccountDTO> selectDTOByExample(AccountExample example);

   CouponDTO convertBalanceToCoupon(IDatosSesion datosSesion, BalanceToCouponRequestDTO balanceToCouponRequestDTO)
         throws ApiException;

}