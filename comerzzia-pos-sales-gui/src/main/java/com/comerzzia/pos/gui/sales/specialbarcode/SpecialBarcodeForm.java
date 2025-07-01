
package com.comerzzia.pos.gui.sales.specialbarcode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;
import com.comerzzia.pos.core.gui.validation.validators.number.IsNumeric;

import lombok.Getter;
import lombok.Setter;

@Component
@Scope("prototype")
@Getter
@Setter
public class SpecialBarcodeForm extends ValidationFormGui {

	@NotNull(message = "Debe rellenar el campo descripción.")
	@NotEmpty(message = "Debe rellenar el campo descripción.")
	protected String description;
	
	@NotNull(message = "Debe rellenar el campo prefijo.")
	@NotEmpty(message = "Debe rellenar el campo prefijo.")
	@IsNumeric(message = "El valor introducido debe ser numérico.")
	protected String prefix;
	
	@NotNull(message = "Debe rellenar el campo tipo de código.")
	@NotEmpty(message = "Debe rellenar el campo tipo de código.")
	protected String codeType;
	
	@NotNull(message = "Debe rellenar el campo posición del contenido.")
	@NotEmpty(message = "Debe rellenar el campo posición del contenido.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@IsNumeric(message = "El valor introducido debe ser numérico.")
	protected String contentPosition;
	
	@NotNull(message = "Debe rellenar el campo longitud del contenido.")
	@NotEmpty(message = "Debe rellenar el campo longitud del contenido.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@IsNumeric(message = "El valor introducido debe ser numérico.")
	protected String contentLength;
	
	@NotNull(message = "Debe rellenar el campo posición del precio.")
	@NotEmpty(message = "Debe rellenar el campo posición del precio.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@IsNumeric(message = "El valor introducido debe ser numérico.")
	protected String pricePosition;
	
	@NotNull(message = "Debe rellenar el campo parte entera del precio.")
	@NotEmpty(message = "Debe rellenar el campo parte entera del precio.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@IsNumeric(message = "El valor introducido debe ser numérico.")
	protected String priceWholeNumber;
	
	@NotNull(message = "Debe rellenar el campo parte decimal del precio.")
	@NotEmpty(message = "Debe rellenar el campo parte decimal del precio.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@IsNumeric(message = "El valor introducido debe ser numérico.")
	protected String priceFractionPart;
	
	@NotNull(message = "Debe rellenar el campo posición de la cantidad.")
	@NotEmpty(message = "Debe rellenar el campo posición de la cantidad.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@IsNumeric(message = "El valor introducido debe ser numérico.")
	protected String quantityPosition;
	
	@NotNull(message = "Debe rellenar el campo parte entera de la cantidad.")
	@NotEmpty(message = "Debe rellenar el campo parte entera de la cantidad.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@IsNumeric(message = "El valor introducido debe ser numérico.")
	protected String quantityWholeNumber;
	
	@NotNull(message = "Debe rellenar el campo parte decimal de la cantidad.")
	@NotEmpty(message = "Debe rellenar el campo parte decimal de la cantidad.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@IsNumeric(message = "El valor introducido debe ser numérico.")
	protected String quantityFractionPart;
	
	@Override
	public void clearForm() {
		description = "";
		codeType = "";
		contentLength = "";
		contentPosition = "";
		pricePosition = "";
		priceWholeNumber = "";
		priceFractionPart = "";
		quantityPosition = "";
		quantityWholeNumber = "";
		quantityFractionPart = "";
	}

}
