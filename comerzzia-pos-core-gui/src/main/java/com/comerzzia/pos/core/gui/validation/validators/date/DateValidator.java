

package com.comerzzia.pos.core.gui.validation.validators.date;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.log4j.Logger;

import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

/**
 * Valida que un string sea una fecha. Los booleanos pasada y futura indican si la fehca permite fechas pasadas y/o futuras
 * indicados
 *
 * @ MGRI
 */
public class DateValidator implements ConstraintValidator<IsValidDate, String> {

    // Log
    private static final Logger log = Logger.getLogger(DateValidator.class.getName());
    
    // Decimales indicados en la anotación
    private boolean past;
    private boolean future;
    private boolean isDateTime;

    @Override
    public void initialize(IsValidDate constraintAnnotation) {
        this.past = constraintAnnotation.past();
        this.future = constraintAnnotation.future();
        this.isDateTime = constraintAnnotation.isDateTime();
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }
        boolean isValid = false;
        String msg = I18N.getText("La fecha introducida no es válida");
        Date date = null;
		Date today = null;

        try {

        	if(isDateTime) {
    			today = new Date();
    			date = FormatUtils.getInstance().parseDateTime(object);
        	} 
        	else {
				today = new Date();
				date = FormatUtils.getInstance().parseDate(object);
    		}
        	
        	if(date != null){
	            if (past && future) {
	                // no hacemos nada y es válida
	                isValid = true;
	            } else if (past && !future) {
	                if (date.compareTo(today) <= 0) {
	                    isValid = true;
	                } else {
	                    isValid = false;
	                    msg = I18N.getText("La fecha introducida no puede ser una fecha futura");
	                }
	            } else if (!past && future) {
	                if (date.compareTo(today) >= 0) {
	                    isValid = true;
	                } else {
	                    msg = I18N.getText("La fecha introducida no puede ser una fecha pasada");
	                    isValid = false;
	
	                }
	            }
        	}
        } catch (Exception e) {
            log.debug("isValid() - número no válido : " + object);
            isValid = false;
        }
        // Cambiamos el mensaje por defecto
        if (!isValid && constraintContext.getDefaultConstraintMessageTemplate().equals("{default}")) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
        }

        return isValid;
    }
}
