
package com.comerzzia.pos.core.gui.login;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
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
public class LoginForm extends ValidationFormGui {
    
    @Size(max = 50, message = "El campo permite un máximo de {max} caracteres")
    @NotBlank (message = "Debe ingresar un nombre de usuario.")
    protected String user;
    
    @Size(max = 250, message = "El campo permite un máximo de {max} caracteres")
    @NotBlank (message = "Debe ingresar un password.")
    protected String password;

    @Override
    public void clearForm() {
        this.user = "";
        this.password = "";
    }
}
