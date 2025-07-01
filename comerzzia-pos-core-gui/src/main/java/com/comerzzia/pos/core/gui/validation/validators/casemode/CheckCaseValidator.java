

package com.comerzzia.pos.core.gui.validation.validators.casemode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.comerzzia.pos.util.i18n.I18N;



public class CheckCaseValidator implements ConstraintValidator<CheckCase, String> {

    private CaseMode caseMode;

    @Override
    public void initialize(CheckCase constraintAnnotation) {
        this.caseMode = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }

        boolean isValid;
        String msg;
        if (caseMode == CaseMode.UPPERCASE) {
            isValid = object.equals(object.toUpperCase());
            msg = I18N.getText("El campo debe estar en mayúsculas");
        } else {
            isValid = object.equals(object.toLowerCase());
            msg = I18N.getText("El campo debe estar en minúsculas");
        }
        // Cambiamos el mensaje por defecto
        if (!isValid && caseMode ==  CaseMode.UPPERCASE ) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
        }

        return isValid;
    }
}
