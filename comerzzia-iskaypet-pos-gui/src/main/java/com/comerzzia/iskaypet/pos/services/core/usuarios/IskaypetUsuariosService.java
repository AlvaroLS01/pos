package com.comerzzia.iskaypet.pos.services.core.usuarios;

import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.usuarios.UsuariosService;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class IskaypetUsuariosService extends UsuariosService {

    public String getDescripcionUsuario(String nombreUsuario) {
        try {
            UsuarioBean usuario = consultarUsuario(nombreUsuario);
            if (usuario != null && StringUtils.isNotBlank(usuario.getDesusuario())) {
                nombreUsuario = ofuscarUsuario(usuario.getDesusuario());
            }
        } catch (Exception ignore) {

        }

        return nombreUsuario;
    }

    public String ofuscarUsuario(String nombreCompleto) {
        StringBuilder nombreAbreviado = new StringBuilder();
        String[] nombreSplit = nombreCompleto.split(" ");

        nombreAbreviado.append(nombreSplit[0]);

        if (nombreSplit.length > 1) {
            nombreAbreviado.append(" ");
            nombreAbreviado.append(nombreSplit[1].charAt(0));
            nombreAbreviado.append(".");
        }

        return nombreAbreviado.toString().toUpperCase();
    }
}
