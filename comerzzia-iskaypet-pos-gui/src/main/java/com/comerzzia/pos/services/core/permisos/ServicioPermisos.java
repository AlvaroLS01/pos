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

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.base.Estado;
import com.comerzzia.core.util.base.KeyConstraintViolationException;
import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.acciones.AccionBean;
import com.comerzzia.pos.persistence.core.acciones.operaciones.OperacionesDao;
import com.comerzzia.pos.persistence.core.permisos.PermisoBean;
import com.comerzzia.pos.persistence.core.permisos.PermisosDao;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Service
public class ServicioPermisos {

    protected static Logger log = Logger.getLogger(ServicioPermisos.class);
    
    @Autowired
    protected Sesion sesion;

    /**
     * Obtiene los permisos efectivos que el usuario tiene sobre la acción
     * siendo estos para cada operación definida para la acción el permiso
     * otorgado directamente al usuario y si este no lo tiene, el mayor de los
     * permisos otorgados a sus perfiles
     *
     * @param accion
     * @param idUsuario
     * @return
     * @throws PermisoException
     */
    public PermisosEfectivosAccionBean obtenerPermisosEfectivos(AccionBean accion, Long idUsuario) throws PermisoException {
        SqlSession sqlSession = new SqlSession();
        Connection conn = new Connection();

        PermisosEfectivosAccionBean permisosEfectivos = new PermisosEfectivosAccionBean(accion, idUsuario);

        String uidActividad = sesion.getAplicacion().getUidActividad();
        String uidInstancia = sesion.getAplicacion().getUidInstancia();
        
        try {
            log.debug("obtenerPermisosEfectivos() - Obteniendo los permisos efectivos del usuario [" + idUsuario + "] para la accion [" + accion.getIdAccion() + " - " + accion.getTitulo() + "]");
            sqlSession.openSession(SessionFactory.openSession());
            conn.abrirConexion(sqlSession.getConnection());

            // Obtenemos la lista de permisos heredados de los perfiles del usuario
            List<PermisoBean> permisosPerfiles = PermisosDao.consultarMaxPermisosAccionPerfilesUsuario(conn, accion.getIdAccion(), idUsuario, uidActividad, uidInstancia);

            // Obtenemos la lista de permisos de la acción definidos para el usuario
            List<PermisoBean> permisosUsuario = PermisosDao.consultarPermisosAccionUsuario(conn, accion.getIdAccion(), idUsuario, uidActividad, uidInstancia);

            // Esteblecemos primero la lista de permisos de los perfiles y luego la del usuario
            permisosEfectivos.setListaPermisos(permisosPerfiles);
            permisosEfectivos.setListaPermisos(permisosUsuario);
            return permisosEfectivos;
        }
        catch (SQLException e) {
            log.error("obtenerPermisosEfectivos() - " + e.getMessage());
            String mensaje = "Error al obtener los permisos efectivos de una acción: " + e.getMessage();

            throw new PermisoException(mensaje, e);
        }
        finally {
            conn.cerrarConexion();
        }
    }

    /**
     * Obtiene los permisos para la acción indicada. Se obtendrán todos los
     * permisos tanto de usuarios como perfiles a los que se hayan asignado o
     * denegado.
     *
     * @param accion
     * @return PermisosAccionBean
     * @throws PermisoException
     */
    public PermisosAccionBean obtenerPermisosAccion(AccionBean accion) throws PermisoException {
        SqlSession sqlSession = new SqlSession();
        Connection conn = new Connection();
        try {
            log.debug("obtenerPermisosAccion() - Obteniendo los permisos para la acción: " + accion);
            sqlSession.openSession(SessionFactory.openSession());
            conn.abrirConexion(sqlSession.getConnection());

            String uidActividad = sesion.getAplicacion().getUidActividad();
            
            PermisosAccionBean permisosAccion = new PermisosAccionBean();
            permisosAccion.setAccion(accion);
            accion.setOperaciones(OperacionesDao.consultar(conn, accion.getIdAccion()));
            permisosAccion.setOperaciones(accion.getOperaciones());

            // Obtenemos la lista de permisos de perfiles
            List<PermisoBean> permisos = PermisosDao.consultarPermisosAccion(conn, uidActividad, accion.getIdAccion());

            // construimos permisos de la acción separando perfiles y usuarios, y operaciones de cada uno
            for (PermisoBean permiso : permisos) {
                if (permiso.isPermisoPerfil()) {
                    Long idPerfil = permiso.getIdPerfil();
                    if (permisosAccion.getPermisosPerfil(idPerfil) == null) {
                        permisosAccion.addPerfil(idPerfil, permiso.getDesPerfil());
                    }
                    permisosAccion.addPermisoPerfil(idPerfil, permiso);
                }
                else {
                    Long idUsuario = permiso.getIdUsuario();
                    if (permisosAccion.getPermisosUsuario(idUsuario) == null) {
                        permisosAccion.addUsuario(idUsuario, permiso.getDesUsuario());
                    }
                    permisosAccion.addPermisoUsuario(idUsuario, permiso);
                }
            }
            return permisosAccion;
        }
        catch (SQLException e) {
            log.error("obtenerPermisosAccion() - " + e.getMessage());
            String mensaje = "Error al obtener los permisos de una acción: " + e.getMessage();

            throw new PermisoException(mensaje, e);
        }
        finally {
            conn.cerrarConexion();
        }
    }

    public void salvarPermisosAccion(PermisosAccionBean permisosAccion, List<PermisosEfectivosAccionBean> listaPermisos) throws PermisoConstraintViolationException,
            PermisoException {
        SqlSession sqlSession = new SqlSession();
        Connection conn = new Connection();
        try {
            log.debug("salvarPermisosAccion() - Salvando permisos para la acción: " + permisosAccion.getAccion().getAccion());
            sqlSession.openSession(SessionFactory.openSession());
            conn.abrirConexion(sqlSession.getConnection());
            conn.iniciaTransaccion();

            String uidActividad = sesion.getAplicacion().getUidActividad();
            String uidInstancia = sesion.getAplicacion().getUidInstancia();

            // insertamos todos los permisos nuevos para las operaciones que tengamos permisos de administración              
            for (PermisosEfectivosAccionBean permisosEfectivos : listaPermisos) {
            	List<PermisoBean> permisos = permisosEfectivos.getListaPermisos();
            	for (PermisoBean permiso : permisos) {
            		if (permisosEfectivos.isEstadoBorrado() || permisosEfectivos.getEstadoBean() == Estado.MODIFICADO) {
            			log.debug("salvarPermisosAccion()- Eliminando permiso");                        

            			PermisosDao.delete(conn, permisosAccion.getAccion().getIdAccion(), permiso.getOperacion().getIdOperacion(), permiso.getIdUsuario(), permiso.getIdPerfil(), uidActividad, uidInstancia);
            		}
            		if (permisosEfectivos.isEstadoNuevo() || permisosEfectivos.getEstadoBean() == Estado.MODIFICADO) {
            			log.debug("salvarPermisosAccion()- Guardando permiso");
            			if (!permiso.isNoEstablecido()){ // Solo guardamos los permisos establecidos.
            				PermisosDao.insert(conn, permiso, uidActividad, uidInstancia);
            			}
            		}
            	}
            }

            conn.commit();
            conn.finalizaTransaccion();
        }
        catch (KeyConstraintViolationException e) {
            conn.deshacerTransaccion();
            log.error("salvarPermisosAccion() - No se han podido salvar los permisos: " + e.getMessage());
            throw new PermisoConstraintViolationException();
        }
        catch (SQLException e) {
            conn.deshacerTransaccion();
            log.error("salvarPermisosAccion() - " + e.getMessage());
            throw new PermisoException(e);
        }
        finally {
            conn.cerrarConexion();
        }
    }
}
