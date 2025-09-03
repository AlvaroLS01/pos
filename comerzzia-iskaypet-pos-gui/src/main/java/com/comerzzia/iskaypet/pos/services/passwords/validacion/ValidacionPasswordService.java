package com.comerzzia.iskaypet.pos.services.passwords.validacion;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.comerzzia.iskaypet.pos.services.core.sesion.exception.NewPasswordConfirmEmptyException;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.NewPasswordEmptyException;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.NotMatchingException;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.SamePasswordException;

/*
 * ISK-386-gap-184-renovacion-de-contrasenas
 */

@Service
public class ValidacionPasswordService {

	public void validaCambioPassword(String oldPassword, String newPassword, String newPasswordConfirm) throws NewPasswordEmptyException, NewPasswordConfirmEmptyException,
	        NotMatchingException, SamePasswordException {

		validateBlankFields(newPassword, newPasswordConfirm);
		validateOldPassword(oldPassword, newPasswordConfirm);
		validateConfirmation(newPassword, newPasswordConfirm);
	}
	
	public void validateBlankFields(String newPassword, String newPasswordConfirm) throws NewPasswordEmptyException, NewPasswordConfirmEmptyException {
		if (StringUtils.isBlank(newPassword)) {
			throw new NewPasswordEmptyException("newPassword null or empty");
		}
		if (StringUtils.isBlank(newPasswordConfirm)) {
			throw new NewPasswordConfirmEmptyException("newPasswordConfirm null or empty");
		}
	}
	
	public void validateOldPassword(String oldPassword, String newPassword) throws SamePasswordException {
		if (oldPassword.equals(newPassword)) {
			throw new SamePasswordException();
		}
	}
	
	public void validateConfirmation(String newPassword, String newPasswordConfirm) throws NotMatchingException {
		if (!newPassword.equals(newPasswordConfirm)) {
			throw new NotMatchingException();
		}
	}
}
