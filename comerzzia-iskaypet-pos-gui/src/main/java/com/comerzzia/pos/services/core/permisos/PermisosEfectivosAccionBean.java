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

package com.comerzzia.pos.services.core.permisos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comerzzia.core.util.base.MantenimientoBean;
import com.comerzzia.pos.persistence.core.acciones.AccionBean;
import com.comerzzia.pos.persistence.core.permisos.PermisoBean;


public class PermisosEfectivosAccionBean extends MantenimientoBean implements Serializable {

    /**
     * serialVersionUID
     */
    protected static final long serialVersionUID = 7215163570075712227L;

    protected AccionBean accionMenu;
    protected Long idUsuario;
    protected Long idPerfil;
    protected String desUsuario;
    protected String desPerfil;
    protected Map<String, PermisoBean> permisos;
    protected Map<String, PermisoBean> permisosEjecucion;

	protected boolean esSuperAdministrador = false;

    /**
     * Constructor
     */
    public PermisosEfectivosAccionBean() {
        permisos = new HashMap<String, PermisoBean>();
        permisosEjecucion = new HashMap<String, PermisoBean>();
    }

    /**
     * Constructor
     *
     * @param idAccion
     * @param idUsuario
     */
    public PermisosEfectivosAccionBean(AccionBean accionMenu, Long idUsuario) {
        this.accionMenu = accionMenu;
        this.idUsuario = idUsuario;
        permisos = new HashMap<String, PermisoBean>();
        permisosEjecucion = new HashMap<String, PermisoBean>();
    }

    /**
     * @return the idUsuario
     */
    public Long getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
        if (!idUsuario.equals(-1L)) {
            this.idPerfil = -1L;
        }
    }

    public Long getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(Long idPerfil) {
        this.idPerfil = idPerfil;
        if (!idPerfil.equals(-1L)) {
            this.idUsuario = -1L;
        }
    }

    public String getDesUsuario() {
        return desUsuario;
    }

    public void setDesUsuario(String desUsuario) {
        this.desUsuario = desUsuario;
    }

    public String getDesPerfil() {
        return desPerfil;
    }

    public void setDesPerfil(String desPerfil) {
        this.desPerfil = desPerfil;
    }

    /**
     * @return the accionMenu
     */
    public AccionBean getAccionMenu() {
        return accionMenu;
    }

    /**
     * @param accionMenu the accionMenu to set
     */
    public void setAccionMenu(AccionBean accionMenu) {
        this.accionMenu = accionMenu;
    }

    /**
     * Establece la lista de permisos disponibles para la acción
     *
     * @param permisos
     */
    public void setListaPermisos(List<PermisoBean> permisos) {
        for (PermisoBean permiso : permisos) {
            this.permisos.put(permiso.getOperacion().getDesOperacion(), permiso);
            this.permisosEjecucion.put(permiso.getOperacion().getEjecucion(), permiso);
        }
    }

    /**
     * Añade permiso disponible para la acción
     *
     * @param permiso
     */
    public void addPermiso(PermisoBean permiso) {
        this.permisos.put(permiso.getOperacion().getDesOperacion(), permiso);
        this.permisosEjecucion.put(permiso.getOperacion().getEjecucion(), permiso);
    }

    /**
     * Indica si se tiene permiso para ejecutar la operación indicada
     *
     * @param operacion
     * @return true si se tiene permiso para ejecutar la operación, false en
     * caso contrario
     */
    public boolean isPuede(String operacion) {
    	if(esSuperAdministrador){
			return true;
		}
    	
        // Obtenemos los permisos para la operacion
        PermisoBean permiso = permisos.get(operacion);
        if (permiso == null) {
            return false;
        }

        return (permiso.isConcedido() || permiso.isAdministrar());
    }
    
    public boolean isPuedeEjecucion(String ejecucion) {
    	if(esSuperAdministrador){
			return true;
		}
    	
        // Obtenemos los permisos para la operacion
        PermisoBean permiso = permisosEjecucion.get(ejecucion);
        if (permiso == null) {
            return false;
        }

        return (permiso.isConcedido() || permiso.isAdministrar());
    }

    /**
     * Indica si se tiene permiso para administrar la operación indicada
     *
     * @param operacion
     * @return true si se tiene permiso para administrar la operación, false en
     * caso contrario
     */
    public boolean isPuedeAdministrar(String operacion) {
    	if(esSuperAdministrador){
			return true;
		}
    	
        // Obtenemos los permisos para la operacion
        PermisoBean permiso = permisos.get(operacion);
        if (permiso == null) {
            return false;
        }

        return (permiso.isAdministrar());
    }

    /**
     * Indica si se tiene permiso de EJECUTAR sobre la acción
     *
     * @return true si se tiene permiso, false en caso contrario
     */
    public boolean isPuedeEjecutar() {
        return isPuedeEjecucion("EJECUCION");
    }

    /**
     * Indica si se tiene permiso de AÑADIR sobre la acción
     *
     * @return true si se tiene permiso, false en caso contrario
     */
    public boolean isPuedeAñadir() {
        return isPuedeEjecucion("AÑADIR");
    }

    /**
     * Indica si se tiene permiso de EDITAR sobre la acción
     *
     * @return true si se tiene permiso, false en caso contrario
     */
    public boolean isPuedeEditar() {
        return isPuedeEjecucion("EDITAR");
    }

    /**
     * Indica si se tiene permiso de ELIMINAR sobre la acción
     *
     * @return true si se tiene permiso, false en caso contrario
     */
    public boolean isPuedeEliminar() {
        return isPuedeEjecucion("ELIMINAR");
    }

    /**
     * Indica si se tiene permiso de ADMINISTRAR sobre la acción
     *
     * @return true si se tiene permiso, false en caso contrario
     */
    public boolean isPuedeAdministrar() {
    	if(esSuperAdministrador){
			return true;
		}
    	
		// Comprobamos si se tiene permiso de administración
        // en alguna de las operaciones
        for (PermisoBean permiso : permisos.values()) {
            if (permiso.isAdministrar()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Devuelve un mapa con todos los permisos del usuario o perfil sobre la
     * acción, utlizando como clave el nombre de la operación.
     *
     * @return Map<String, PermisoBean>
     */
    public Map<String, PermisoBean> getPermisos() {
        return permisos;
    }
    
    public Map<String, PermisoBean> getPermisosEjecucion() {
        return permisosEjecucion;
    }

    /**
     * Devuelve la lista de permisos del usuario o perfil sobre la acción.
     *
     * @return List<PermisoBean>
     */
    public List<PermisoBean> getListaPermisos() {
        return new ArrayList<PermisoBean>(getPermisos().values());
    }

    /**
     * Indica si los permisos efectivos pertenecen a un usuario.
     *
     * @return tru o false en si los permisos pertenecen a un usuario
     */
    public boolean isPermisoUsuario() {
        return (getIdUsuario() != null && !this.getIdUsuario().equals(-1L));
    }

    /**
     * Indica si los permisos efectivos pertenecen a un perfil.
     *
     * @return tru o false en si los permisos pertenecen a un perfil
     */
    public boolean isPermisoPerfil() {
        return (getIdPerfil() != null && !this.getIdPerfil().equals(-1L));
    }

    /**
     * Elimina toda la asignación de permisos para todas las operaciones de la
     * acción.
     */
    public void borrarPermisos() {
        this.permisos.clear();
    }

    public void setSuperAdministrador(boolean esSuperAdministrador) {
	    this.esSuperAdministrador = esSuperAdministrador;
    }

	@Override
	protected void initNuevoBean() {
	}
    
}
