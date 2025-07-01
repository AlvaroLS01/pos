package com.comerzzia.pos.core.gui.validation.validators.number;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

/**
 * Valida que un string sea numérico y que tenga el número de decimales indicados @ MGRI
 */
public class NumericValidator implements ConstraintValidator<IsNumeric, String> {

	// Decimales indicados en la anotación
	private int decimals;
	private boolean isAmount;
	private boolean permitsZero;
	private int scale;

	@Override
	public void initialize(IsNumeric constraintAnnotation) {
		this.decimals = constraintAnnotation.decimals();
		this.isAmount = constraintAnnotation.isAmount();
		this.permitsZero = constraintAnnotation.permitsZero();
		this.scale = constraintAnnotation.precision();
	}

	@Override
	public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
		if (object == null) {
			return true;
		}

		boolean isValid = false;
		String msg = I18N.getText("El campo no es un número válido.");

		BigDecimal bigDecimal = null;
		if(!isAmount) {
			bigDecimal = FormatUtils.getInstance().parseBigDecimal(object);
			if(bigDecimal == null){
				isValid = false;
			}
			else if(!permitsZero && BigDecimalUtil.isEqualsToZero(bigDecimal)){
				isValid = false;
			}
			else if (decimals >= 0 && decimals >= bigDecimal.scale()) { // Comprobación de número de decimales
				isValid = true;
			}
			else {
				isValid = false;
				msg = I18N.getText("El campo permite un maximo de {decimales} decimales.");
			}
		} else {
			try {
				bigDecimal = FormatUtils.getInstance().parseAmount(object);
				if (bigDecimal != null && FormatUtils.getInstance().getFractionDigits() >= bigDecimal.scale()) { // Comprobación de número de decimales
					isValid = true;
				}
				else if(!permitsZero && BigDecimalUtil.isEqualsToZero(bigDecimal)){
					isValid = false;
				}
				else {
					isValid = false;
					msg = I18N.getText("El importe introducido no es válido.");
				}
			} catch (Exception e) {
				isValid = false;
			}
		}
		
		if (bigDecimal != null && scale != 0) {
			if (bigDecimal.toBigInteger().abs().toString().length() > scale) {
				isValid = false;
				msg = I18N.getText("El campo permite un máximo de {0} caracteres", String.valueOf(scale));
			}
		}
		
		
		// Cambiamos el mensaje por defecto
		if (!isValid && constraintContext.getDefaultConstraintMessageTemplate().equals("{default}")) {
			constraintContext.disableDefaultConstraintViolation();
			constraintContext.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
		}

		return isValid;
	}
}
