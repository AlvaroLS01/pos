
package com.comerzzia.pos.core.gui.permissions.add;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Scope("prototype")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddPermissionForm extends ValidationFormGui {

	@Size(max = 20, message = "La longitud del identificador debe ser como máximo de {max} caracteres.")
	protected String user;
	
	@Size(max = 45, message = "La longitud del nombre de usuario debe ser como máximo de {max} caracteres.")
	protected String name;

	@Override
	public void clearForm() {
		user = "";
		name = "";
	}

}
