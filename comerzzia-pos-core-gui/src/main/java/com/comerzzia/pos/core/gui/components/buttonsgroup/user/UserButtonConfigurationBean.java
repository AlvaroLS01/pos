
package com.comerzzia.pos.core.gui.components.buttonsgroup.user;

import com.comerzzia.core.facade.model.User;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;

public class UserButtonConfigurationBean extends ButtonConfigurationBean{
    
    public User usuario;
    
    public UserButtonConfigurationBean(String rutaImagen, String texto, String textoAccesoRapido, String nombreAccion, String tipoAccion, User usuario){
        super(rutaImagen, texto, textoAccesoRapido, nombreAccion, tipoAccion);
        
        this.usuario = usuario;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }
    
}
