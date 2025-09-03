package com.comerzzia.iskaypet.pos.core.gui.main.pie;

import com.comerzzia.iskaypet.pos.services.core.usuarios.IskaypetUsuariosService;
import com.comerzzia.pos.core.gui.main.pie.PieController;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.usuarios.UsuariosService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

@Controller
@Primary
public class IskaypetPieController extends PieController {

    @Autowired
    protected Sesion sesion;

    @Autowired
    protected IskaypetUsuariosService usuariosService;

    @FXML
    protected Label lbUsuario;

    public void actualizarUsuario() {
        lbUsuario.setText(usuariosService.ofuscarUsuario(sesion.getSesionUsuario().getUsuario().getDesusuario()));
    }



}
