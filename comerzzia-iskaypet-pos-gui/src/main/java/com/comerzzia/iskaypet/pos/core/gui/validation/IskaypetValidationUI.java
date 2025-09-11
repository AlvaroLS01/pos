package com.comerzzia.iskaypet.pos.core.gui.validation;

import com.comerzzia.pos.util.i18n.I18N;
import org.apache.log4j.Logger;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.text.MessageFormat;
import java.util.Locale;

public class IskaypetValidationUI {

	private static IskaypetValidationUI instancia;
	private ValidatorFactory validatorFactory;
	private Validator validator;
	private static final Logger log = Logger.getLogger(IskaypetValidationUI.class.getName());

	public IskaypetValidationUI() {
		log.debug("Validation() - Creando validator factory y validator");
		this.validatorFactory = ((HibernateValidatorConfiguration) ((HibernateValidatorConfiguration) Validation.byProvider(HibernateValidator.class).configure()).messageInterpolator(
				new IskaypetValidationUI.IskaypetPOSResourceBundleMessageInterpolator())).buildValidatorFactory();
		this.validator = this.validatorFactory.getValidator();
	}

	public static IskaypetValidationUI getInstance() {
		if (instancia == null) {
			instancia = new IskaypetValidationUI();
		}

		return instancia;
	}

	public Validator getValidator() {
		return this.validator;
	}

	static class IskaypetPOSResourceBundleMessageInterpolator extends ResourceBundleMessageInterpolator {

		IskaypetPOSResourceBundleMessageInterpolator() {
		}

		public String interpolate(String message, MessageInterpolator.Context context, Locale locale) {
			return this.interpolate(message, context);
		}

		public String interpolate(String message, MessageInterpolator.Context context) {
			if ("{javax.validation.constraints.Size.message}".equals(message)) {
				return message;
			}
			else {
				Object max = context.getConstraintDescriptor().getAttributes().get("max");
				Object min = context.getConstraintDescriptor().getAttributes().get("min");
				if (max != null & min != null) {
					String texto  = I18N.getTexto(message);
					texto = texto.replace("{max}", "{0}");
					texto = texto.replace("{min}", "{1}");
					return MessageFormat.format(texto, max, min);
				}
				else if (max != null) {
					String texto = I18N.getTexto(message);
					texto = texto.replace("{max}", "{0}");
					return MessageFormat.format(texto, max);
				}
				else if (min != null) {
					String texto = I18N.getTexto(message);
					texto = texto.replace("{min}", "{0}");
					return MessageFormat.format(texto, min);
				}
				else {
					return I18N.getTexto(message);
				}
			}
		}
	}

}
