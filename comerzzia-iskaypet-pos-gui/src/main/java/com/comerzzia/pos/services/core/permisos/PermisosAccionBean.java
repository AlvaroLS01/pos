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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.comerzzia.pos.persistence.core.acciones.AccionBean;
import com.comerzzia.pos.persistence.core.acciones.operaciones.OperacionBean;
import com.comerzzia.pos.persistence.core.permisos.PermisoBean;


public class PermisosAccionBean implements Serializable {

    /**
     * serialVersionUID
     */
    protected static final long serialVersionUID = 7215163570075712227L;

    /**
     * Mapa con los permisos efectivos sobre la acción de los usuarios (se usa
     * el idUsuario como clave)
     */
    protected Map<Long, PermisosEfectivosAccionBean> permisosUsuarios;

    /**
     * Mapa con los permisos efectivos sobre la acción de los perfiles (se usa
     * el idPerfil como clave)
     */
    protected Map<Long, PermisosEfectivosAccionBean> permisosPerfiles;

    /**
     * Mapa de operaciones de la acción (se usa idOperacion como clave)
     */
    protected Map<Byte, OperacionBean> operaciones;

    /**
     * Acción
     */
    protected AccionBean accion;

    /**
     * Constructor
     */
    public PermisosAccionBean() {
        this.permisosUsuarios = new HashMap<Long, PermisosEfectivosAccionBean>();
        this.permisosPerfiles = new HashMap<Long, PermisosEfectivosAccionBean>();
        this.operaciones = new HashMap<Byte, OperacionBean>();
    }

    /**
     * Devuelve los permisos efectivos sobre la acción del usuario indicado por
     * su id.
     *
     * @param idUsuario
     * @return PermisosEfectivosAccionBean
     */
    public PermisosEfectivosAccionBean getPermisosUsuario(Long idUsuario) {
        return permisosUsuarios.get(idUsuario);
    }

    /**
     * Devuelve los permisos efectivos sobre la acción del perfil indicado por
     * su id.
     *
     * @param idPerfil
     * @return PermisosEfectivosAccionBean
     */
    public PermisosEfectivosAccionBean getPermisosPerfil(Long idPerfil) {
        return permisosPerfiles.get(idPerfil);
    }

    /**
     * Devuelve el mapa con todos los permisos efectivos sobre la acción de
     * todos los usuarios que tengan algún permiso, con clave en el mapa el id
     * del usuario.
     *
     * @return Map<Long, PermisosEfectivosAccionBean>
     */
    public Map<Long, PermisosEfectivosAccionBean> getPermisosUsuarios() {
        return permisosUsuarios;
    }

    /**
     * Devuelve el mapa con todos los permisos efectivos sobre la acción de
     * todos los perfiles que tengan algún permiso, con clave en el mapa el id
     * del perfil.
     *
     * @return Map<Long, PermisosEfectivosAccionBean>
     */
    public Map<Long, PermisosEfectivosAccionBean> getPermisosPerfiles() {
        return permisosPerfiles;
    }

    /**
     * Devuelve la acción a la que pertenecen los permisos
     *
     * @return AccionBean
     */
    public AccionBean getAccion() {
        return accion;
    }

    /**
     * Establece la acción a la que pertenecen los permisos.
     *
     * @param accion
     */
    public void setAccion(AccionBean accion) {
        this.accion = accion;
    }

    /**
     * Devuelve toda la lista permisos efectivos de la acción, tanto de perfiles
     * como usuarios, por este orden (primero todos los perfiles y después todos
     * los usuarios).
     *
     * @return List<PermisosEfectivosAccionBean>
     */
    public List<PermisosEfectivosAccionBean> getListaPermisosAccion() {
        List<PermisosEfectivosAccionBean> permisosAccion = new ArrayList<PermisosEfectivosAccionBean>(getPermisosPerfiles().values());
        permisosAccion.addAll(getPermisosUsuarios().values());
        return permisosAccion;
    }

    /**
     * Devuelve la lista de operaciones de la acción.
     *
     * @return List<OperacionBean>
	 *
     */
    public List<OperacionBean> getOperaciones() {
        return new ArrayList<OperacionBean>(operaciones.values());
    }

    /**
     * Establece la lista de operaciones de la acción
     *
     * @param operaciones
     */
    public void setOperaciones(List<OperacionBean> operaciones) {
        for (OperacionBean operacion : operaciones) {
            this.operaciones.put(operacion.getIdOperacion(), operacion);
        }
    }

    /**
     * Devuelve la operación de la acción que tiene el identificado indicado.
     *
     * @param idOperacion
     * @return OperacionBean
     */
    public OperacionBean getOperacion(Byte idOperacion) {
        return this.operaciones.get(idOperacion);
    }

    /**
     * Borra todos los permisos registrados para todos los usuarios y perfiles
     * de la acción.
     */
    public void borrarPermisos() {
        Iterator<Long> it = permisosPerfiles.keySet().iterator();
        while (it.hasNext()) {
            PermisosEfectivosAccionBean permisosEfectivos = this.permisosPerfiles.get(it.next());
            permisosEfectivos.borrarPermisos();
        }
        it = permisosUsuarios.keySet().iterator();
        while (it.hasNext()) {
            PermisosEfectivosAccionBean permisosEfectivos = this.permisosUsuarios.get(it.next());
            permisosEfectivos.borrarPermisos();
        }
    }

    /**
     * Añade un nuevo permisos efectivos para el usuario con id y nombre
     * indicado y una lista de permisos vacía.
     *
     * @param idUsuario
     * @param desUsuario
     */
    public void addUsuario(Long idUsuario, String desUsuario) {
        PermisosEfectivosAccionBean permisosUsuario = new PermisosEfectivosAccionBean();
        permisosUsuario.setAccionMenu(getAccion());
        permisosUsuario.setIdUsuario(idUsuario);
        permisosUsuario.setDesUsuario(desUsuario);
        permisosUsuarios.put(idUsuario, permisosUsuario);
    }

    /**
     * Añade un nuevo permisos efectivos para el usuario con id y nombre
     * indicado, y la lista de permisos que se pasa como parámetro.
     *
     * @param idUsuario
     * @param desUsuario
     * @param permisos
     */
    public void addUsuario(Long idUsuario, String desUsuario, List<PermisoBean> permisos) {
        addUsuario(idUsuario, desUsuario);
        getPermisosUsuario(idUsuario).setListaPermisos(permisos);
    }

    /**
     * Añade un nuevo permisos efectivos para el usuario con id y nombre
     * indicado, y la lista de permisos por defecto (permisos heredados para
     * todas las operaciones).
     *
     * @param idUsuario
     * @param desUsuario
     */
    public void addUsuarioDefault(Long idUsuario, String desUsuario) {
        // por defecto tendrá todos los permisos heredados (lista de permisos vacía)
        addUsuario(idUsuario, desUsuario);
    }

    /**
     * Añade un nuevo permisos efectivos para el perfil con id y nombre
     * indicados, y una lista de permisos vacía.
     *
     * @param idPerfil
     * @param desPerfil
     */
    public void addPerfil(Long idPerfil, String desPerfil) {
        PermisosEfectivosAccionBean permisosPerfil = new PermisosEfectivosAccionBean();
        permisosPerfil.setAccionMenu(getAccion());
        permisosPerfil.setIdPerfil(idPerfil);
        permisosPerfil.setDesPerfil(desPerfil);
        permisosPerfiles.put(idPerfil, permisosPerfil);
    }

    /**
     * Añade un nuevo permisos efectivos para el perfil con id y nombre
     * indicados, y la lista de permisos que se pasa como parámetro.
     *
     * @param idPerfil
     * @param desPerfil
     * @param permisos
     */
    public void addPerfil(Long idPerfil, String desPerfil, List<PermisoBean> permisos) {
        addPerfil(idPerfil, desPerfil);
        getPermisosPerfil(idPerfil).setListaPermisos(permisos);
    }

    /**
     * Añade un nuevo permisos efectivos para el perfil con id y nombre
     * indicados, y la lista de permisos por defecto (permisos denegados para
     * todas las operaciones).
     *
     * @param idPerfil
     * @param desPerfil
     * @param permisos
     */
    public void addPerfilDefault(Long idPerfil, String desPerfil) {
        // por defecto tendrá todos los permisos denegados
        List<PermisoBean> permisos = new ArrayList<PermisoBean>(this.operaciones.size());
        for (OperacionBean operacion : getOperaciones()) {
            PermisoBean permiso = new PermisoBean();
            permiso.setIdPerfil(idPerfil);
            permiso.setIdAccion(getAccion().getIdAccion());
            permiso.setOperacion(operacion);
            permiso.setPermisoNoEstablecido();
            permisos.add(permiso);
        }
        addPerfil(idPerfil, desPerfil, permisos);
    }

    /**
     * Elimina los permisos efectivos del usuario indicado sobre la acción.
     *
     * @param idUsuario
     */
    public void borrarUsuario(Long idUsuario) {
        this.permisosUsuarios.remove(idUsuario);
    }

    /**
     * Elimina los permisos efectivos del perfil indicado sobre la acción.
     *
     * @param idPerfil
     */
    public void borrarPerfil(Long idPerfil) {
        this.permisosPerfiles.remove(idPerfil);
    }

    /**
     * Añade un nuevo permiso de usuario para una operación sobre los permisos
     * efectivos del mismo sobre la acción.
     *
     * @param idUsuario
     * @param permiso
     */
    public void addPermisoUsuario(Long idUsuario, PermisoBean permiso) {
        getPermisosUsuario(idUsuario).addPermiso(permiso);
    }

    /**
     * Añade un nuevo permiso de usuario para una operación sobre los permisos
     * efectivos del mismo sobre la acción.
     *
     * @param idUsuario
     * @param idOperacion
     * @param acceso
     */
    public void addPermisoUsuario(Long idUsuario, Byte idOperacion, Byte acceso) {
        PermisoBean permiso = new PermisoBean();
        permiso.setIdAccion(getAccion().getIdAccion());
        permiso.setIdUsuario(idUsuario);
        permiso.setPermiso(acceso);
        permiso.setOperacion(getOperacion(idOperacion));
        addPermisoUsuario(idUsuario, permiso);
    }

    /**
     * Añade un nuevo permiso de perfil para una operación sobre los permisos
     * efectivos del mismo sobre la acción.
     *
     * @param idUsuario
     * @param permiso
     */
    public void addPermisoPerfil(Long idPerfil, PermisoBean permiso) {
        getPermisosPerfil(idPerfil).addPermiso(permiso);
    }

    /**
     * Añade un nuevo permiso de perfil para una operación sobre los permisos
     * efectivos del mismo sobre la acción.
     *
     * @param idPerfil
     * @param idOperacion
     * @param acceso
     */
    public void addPermisoPerfil(Long idPerfil, Byte idOperacion, Byte acceso) {
        PermisoBean permiso = new PermisoBean();
        permiso.setIdAccion(getAccion().getIdAccion());
        permiso.setIdPerfil(idPerfil);
        permiso.setPermiso(acceso);
        permiso.setOperacion(getOperacion(idOperacion));
        addPermisoPerfil(idPerfil, permiso);
    }

    /**
     * Elimina la asignación de permisos de un usuario para la operación
     * indicada.
     *
     * @param idUsuario
     * @param idOperacion
     */
    public void borrarPermisoUsuario(Long idUsuario, Byte idOperacion) {
        String desOperacion = getOperacion(idOperacion).getDesOperacion();
        getPermisosUsuario(idUsuario).getPermisos().remove(desOperacion);
    }

    /**
     * Elimina la asignación de permisos de un perfil para la operación
     * indicada.
     *
     * @param idUsuario
     * @param idOperacion
     */
    public void borrarPermisoPerfil(Long idPerfil, Byte idOperacion) {
        String desOperacion = getOperacion(idOperacion).getDesOperacion();
        getPermisosPerfil(idPerfil).getPermisos().remove(desOperacion);
    }
    
}
