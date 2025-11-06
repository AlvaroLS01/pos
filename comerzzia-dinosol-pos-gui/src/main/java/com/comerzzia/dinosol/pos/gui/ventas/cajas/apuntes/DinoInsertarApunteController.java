package com.comerzzia.dinosol.pos.gui.ventas.cajas.apuntes;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.cajas.apuntes.FormularioApunteBean;
import com.comerzzia.pos.gui.ventas.cajas.apuntes.InsertarApunteController;

@Component
@Primary
public class DinoInsertarApunteController extends InsertarApunteController {

	@Override
	public void initializeComponents() {
		super.initializeComponents();

		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setPintarPiePantalla(true);
		tfCodConcepto.setUserData(keyboardDataDto);
		tfDocumento.setUserData(keyboardDataDto);
		
		KeyboardDataDto keyboardImporteDataDto = new KeyboardDataDto();
		keyboardImporteDataDto.setPintarPiePantalla(true);
		keyboardImporteDataDto.setPintarSignoNegativo(true);
		tfImporte.setUserData(keyboardImporteDataDto);
		
		addSeleccionarTodoEnFoco(tfImporte);
		addSeleccionarTodoEnFoco(tfCodConcepto);
		addSeleccionarTodoEnFoco(tfDocumento);

		tfDescConcepto.setFocusTraversable(false);
		tfDocumento.setFocusTraversable(true);
	}

	@Override
	protected boolean accionValidarFormulario() {
		boolean bResultado = true;

		// Inicializamos la etiqueta de error
		frApunte.clearErrorStyle();
		lbError.setText("");

		// Validamos el formulario
		Set<ConstraintViolation<FormularioApunteBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frApunte);
		Iterator<ConstraintViolation<FormularioApunteBean>> iterator = constraintViolations.iterator();
		while (iterator.hasNext()) {
			ConstraintViolation<FormularioApunteBean> next = iterator.next();
			frApunte.setErrorStyle(next.getPropertyPath(), true);

			lbError.setText(next.getMessage());

			bResultado = false;
		}

		if (!bResultado) {
			estableceFoco();
		}

		return bResultado;
	}

}
