/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */

package com.comerzzia.pos.services.core.sesion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.core.rest.client.usuarios.CambiarClaveUsuarioRequestRest;
import com.comerzzia.api.core.rest.client.usuarios.UsuariosRest;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.response.ResponsePostRest;
import com.comerzzia.pos.persistence.core.acciones.AccionBean;
import com.comerzzia.pos.persistence.core.perfiles.ParametrosBuscarPerfilesBean;
import com.comerzzia.pos.persistence.core.perfiles.PerfilBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.perfiles.PerfilException;
import com.comerzzia.pos.services.core.perfiles.ServicioPerfiles;
import com.comerzzia.pos.services.core.permisos.PermisoException;
import com.comerzzia.pos.services.core.permisos.PermisosEfectivosAccionBean;
import com.comerzzia.pos.services.core.permisos.ServicioPermisos;
import com.comerzzia.pos.services.core.usuarios.UsuarioInvalidLoginException;
import com.comerzzia.pos.services.core.usuarios.UsuariosService;
import com.comerzzia.pos.services.core.usuarios.UsuariosServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;

@Component
public class SesionUsuario {

    protected static final Logger log = Logger.getLogger(SesionUsuario.class.getName());
    protected UsuarioBean usuario;
    protected Map<PermisosCacheKey, PermisosEfectivosAccionBean> permisos; // IdAccion - Permisos
    protected Boolean superAdministrador = false;
    
    @Autowired
    protected Sesion sesion;
    
    @Autowired
    protected VariablesServices variablesServices;
    
    @Autowired
    protected ServicioPerfiles servicioPerfiles;
    @Autowired
    protected ServicioPermisos servicioPermisos;
    @Autowired
    protected UsuariosService usuariosService;

    protected SesionUsuario() {
        permisos = new HashMap<>();
    }

    public void init(String usuario, String password) throws SesionInitException, UsuarioInvalidLoginException {
        try {
            UsuarioBean usuarioBean = usuariosService.login(usuario, password);
            this.usuario = usuarioBean;
            setIsSuperAdministrador();
        }
        catch (UsuarioInvalidLoginException ex) {
            throw ex;
        }
        catch (UsuariosServiceException ex) {
            log.error("init() -  Error iniciando sesión de usuario " + usuario + " - " + ex.getMessageI18N());
            throw new SesionInitException(ex.getMessageI18N(), ex);
        }
        catch (Exception ex) {
            log.error("init() - Error inicializando sesión de usuario: " + usuario + " - " + ex.getMessage(), ex);
            throw new SesionInitException(ex);
        }
    }

    public UsuarioBean getUsuario() {
        return usuario;
    }

    public Map<PermisosCacheKey, PermisosEfectivosAccionBean> getPermisos() {
        return permisos;
    }

    public void clearPermisos() {
        this.permisos.clear();
    }

    public PermisosEfectivosAccionBean getPermisosEfectivos(AccionBean accion) throws PermisoException {
        return getPermisosEfectivos(accion, true);
    }
    
    public PermisosEfectivosAccionBean getPermisosEfectivos(UsuarioBean usuario, AccionBean accion, boolean cache) throws PermisoException {
    	PermisosCacheKey permisosCacheKey = new PermisosCacheKey(accion.getIdAccion(), usuario.getIdUsuario());
    	PermisosEfectivosAccionBean permisosEfectivos = permisos.get(permisosCacheKey);
		if (!cache || permisosEfectivos == null) {
			permisosEfectivos = servicioPermisos.obtenerPermisosEfectivos(accion, usuario.getIdUsuario());
			permisosEfectivos.setSuperAdministrador(isSuperAdministrador(usuario.getIdUsuario()));
			permisos.put(permisosCacheKey, permisosEfectivos);
		}
    	return permisosEfectivos;
    }

    public PermisosEfectivosAccionBean getPermisosEfectivos(AccionBean accion, boolean cache) throws PermisoException {
        return getPermisosEfectivos(getUsuario(), accion, cache);
    }
    
    public boolean cambiarPassword(String newPassword, String oldPassword, String usuario) throws RestException, RestHttpException{
        
        boolean result = false;
        CambiarClaveUsuarioRequestRest request = new CambiarClaveUsuarioRequestRest();
        
        request.setClave(oldPassword);
        request.setClaveNueva(newPassword);
        request.setUsuario(usuario);
        request.setApiKey(variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY));
        request.setUidActividad(sesion.getAplicacion().getUidActividad());
        
        ResponsePostRest response = UsuariosRest.setClaveUsuario(request);
        
        if(response.getNumeroUpdates()>0){
            result = true;
        }
        
        return result;
    }
    
    public Boolean isSuperAdministrador(Long idUsuario) {
    	try {
			boolean isAdmin = false;
			ParametrosBuscarPerfilesBean param = new ParametrosBuscarPerfilesBean();
			param.setTamañoPagina(Integer.MAX_VALUE);
			param.setNumPagina(1);
			param.setIdUsuario(idUsuario);
			@SuppressWarnings("unchecked")
			List<PerfilBean> perfiles = (List<PerfilBean>) servicioPerfiles.consultar(param).getPagina();
			for (PerfilBean perfilBean : perfiles) {
				if (perfilBean.getIdPerfil().equals(0l)) {
					isAdmin = true;
					break;
				}
			}
			return isAdmin;
		} catch (PerfilException e) {
			throw new RuntimeException(e);
		}
    }

	public Boolean isSuperAdministrador() {
		return superAdministrador;
	}

	public void setIsSuperAdministrador() throws PerfilException {
		this.superAdministrador = false;
		ParametrosBuscarPerfilesBean param = new ParametrosBuscarPerfilesBean();
        param.setTamañoPagina(Integer.MAX_VALUE);
        param.setNumPagina(1);
        param.setIdUsuario(usuario.getIdUsuario());
		@SuppressWarnings("unchecked")
		List<PerfilBean> perfiles = (List<PerfilBean>) servicioPerfiles.consultar(param).getPagina();
		for (PerfilBean perfilBean : perfiles) {
			if(perfilBean.getIdPerfil().equals(0l)){
				this.superAdministrador = true;
				break;
			}
		}
	}

	public void clear() {
		usuario = null;
		permisos = new HashMap<>();
    }
	
	static class PermisosCacheKey {
		Long idAccion;
		Long idUsuario;
		public PermisosCacheKey(Long idAccion, Long idUsuario) {
			super();
			this.idAccion = idAccion;
			this.idUsuario = idUsuario;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((idAccion == null) ? 0 : idAccion.hashCode());
			result = prime * result + ((idUsuario == null) ? 0 : idUsuario.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PermisosCacheKey other = (PermisosCacheKey) obj;
			if (idAccion == null) {
				if (other.idAccion != null)
					return false;
			} else if (!idAccion.equals(other.idAccion))
				return false;
			if (idUsuario == null) {
				if (other.idUsuario != null)
					return false;
			} else if (!idUsuario.equals(other.idUsuario))
				return false;
			return true;
		}
	}
	
}
