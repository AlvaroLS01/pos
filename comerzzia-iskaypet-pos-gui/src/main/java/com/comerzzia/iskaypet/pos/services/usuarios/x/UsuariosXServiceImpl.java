package com.comerzzia.iskaypet.pos.services.usuarios.x;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.iskaypet.pos.persistence.usuarios.x.UsuarioX;
import com.comerzzia.iskaypet.pos.persistence.usuarios.x.UsuarioXKey;
import com.comerzzia.iskaypet.pos.persistence.usuarios.x.UsuarioXMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Service
public class UsuariosXServiceImpl {
	
	private static final Logger log = Logger.getLogger(UsuariosXServiceImpl.class);
	
	@Autowired
    protected Sesion sesion;
	@Autowired
	protected UsuarioXMapper usuarioXMapper;
	
	public UsuarioX getUsuarioX(Long idUsuario) throws Exception {
        log.debug("getUsuarioX() - Consultando usuario X con ID: " + idUsuario);

        UsuarioXKey key = new UsuarioXKey();
        key.setUidInstancia(sesion.getAplicacion().getUidInstancia());
        key.setIdUsuario(idUsuario);

        UsuarioX usuarioX = usuarioXMapper.selectByPrimaryKey(key);

        if (usuarioX == null) {
            log.error("getUsuarioX() - No se ha encontrado ningún usuario con ID: " + idUsuario);
        }

        return usuarioX;
    }
	
	public int updateUsuarioX(UsuarioX usuario) throws Exception {
        log.debug("updateUsuarioX() - Actualizando usuario X con ID: " + usuario.getIdUsuario());
        
        return usuarioXMapper.updateByPrimaryKey(usuario);
    }
	
	public int insertUsuarioX(UsuarioX usuario) throws Exception {
        log.debug("insertUsuarioX() - Insertando usuario X con ID: " + usuario.getIdUsuario());
        
        return usuarioXMapper.insert(usuario);
    }

}
