


package com.comerzzia.pos.core.gui.validation;


import java.text.MessageFormat;
import java.util.Locale;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.Logger;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

import com.comerzzia.pos.util.i18n.I18N;



public class ValidationUI {
    
    private static ValidationUI instance;    
    private ValidatorFactory validatorFactory; //Archivo de recurso de la aplicación
    private Validator validator; //Archivo de recurso de la aplicación
    
     // Logger
    private static final Logger log = Logger.getLogger(ValidationUI.class.getName());
    
     /**
     * Constructor
     */
    public ValidationUI() {
        log.debug("Validation() - Creando validator factory y validator");  
        
        //Creamos el validador con un resourceBundle
		validatorFactory = javax.validation.Validation
				.byProvider(HibernateValidator.class)
				.configure()
				.messageInterpolator(
						new POSResourceBundleMessageInterpolator())
				.buildValidatorFactory();
            
       validator = validatorFactory.getValidator();          
    }
    
    static class POSResourceBundleMessageInterpolator extends ResourceBundleMessageInterpolator {

		@Override
		public String interpolate(String message, Context context, Locale locale) {
			return interpolate(message, context); //No necesitamos locale
		}

		@Override
		public String interpolate(String message, Context context) {
			if ("{javax.validation.constraints.Size.message}".equals(message)) { //Por alguna razón, se llama muchas veces con esta cadena. La ignoro.
				return message;
			}
            Object max = context.getConstraintDescriptor().getAttributes().get("max");
			if (max != null) {
				String texto = I18N.getText(message);
				texto = texto.replace("{max}", "{0}");
				return MessageFormat.format(texto, max);
            } else {
                return I18N.getText(message);
            }
		}
		
    }
    
    /**
     * Devuelve la instancia de I18nBundle
     * @return 
     */
    public static ValidationUI getInstance(){
        if (instance == null){
            instance = new ValidationUI();
        }
        return instance;
    }
    
    /**
     * 
     * @return validator
     */
    public Validator getValidator(){
        return validator;
    }
    
}
