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
package com.comerzzia.pos.services.core.perfiles;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.paginacion.PaginaResultados;
import com.comerzzia.pos.persistence.core.perfiles.ParametrosBuscarPerfilesBean;
import com.comerzzia.pos.persistence.core.perfiles.PerfilBean;
import com.comerzzia.pos.persistence.core.perfiles.PerfilesDao;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.sesion.Sesion;


@Service
public class ServicioPerfiles {

	/**
	 * Logger
	 */
	protected static Logger log = Logger.getLogger(ServicioPerfiles.class);
	
	@Autowired
	protected Sesion sesion;

	public PaginaResultados consultar(ParametrosBuscarPerfilesBean param) throws PerfilException {
            SqlSession sqlSession = new SqlSession();
            Connection conn = new Connection();

		try {
			log.debug("consultar() - Consultando perfiles");
                        sqlSession.openSession(SessionFactory.openSession());   
                        String uidInstancia = sesion.getAplicacion().getUidInstancia();
                        String uidActividad = sesion.getAplicacion().getUidActividad();
                        conn.abrirConexion(sqlSession.getConnection());
			return PerfilesDao.consultar(conn, param, uidActividad, uidInstancia);
		}
		catch (SQLException e) {
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar perfiles: " + e.getMessage();

			throw new PerfilException(mensaje, e);
		}
		finally {
			conn.cerrarConexion();
		}
	}

	public List<PerfilBean> consultar() throws PerfilException {
	SqlSession sqlSession = new SqlSession();	
            Connection conn = new Connection();
            
		try {
			log.debug("consultar() - Consultando todos los perfiles");
			sqlSession.openSession(SessionFactory.openSession());    
                        String uidInstancia = sesion.getAplicacion().getUidInstancia();
                        String uidActividad = sesion.getAplicacion().getUidActividad();
                        conn.abrirConexion(sqlSession.getConnection());
			return PerfilesDao.consultar(conn, uidActividad, uidInstancia);
		}
		catch (SQLException e) {
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar todos los perfiles: " + e.getMessage();

			throw new PerfilException(mensaje, e);
		}
		finally {
			conn.cerrarConexion();
		}
	}

	public PerfilBean consultar(Long idPerfil) throws PerfilException, PerfilNotFoundException {
            SqlSession sqlSession = new SqlSession();
            Connection conn = new Connection();

		try {
			log.debug("consultar() - Consultando datos del perfil con identificador: " + idPerfil);
			sqlSession.openSession(SessionFactory.openSession());
                        String uidInstancia = sesion.getAplicacion().getUidInstancia();
                        String uidActividad = sesion.getAplicacion().getUidActividad();
                        conn.abrirConexion(sqlSession.getConnection());
			PerfilBean perfil = PerfilesDao.consultar(conn, idPerfil, uidActividad, uidInstancia);

			if (perfil == null) {
				String msg = "No se ha encontrado el perfil con identificador: " + idPerfil;
				log.info("consultar() - " + msg);
				throw new PerfilNotFoundException(msg);
			}

			return perfil;
		}
		catch (SQLException e) {
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar datos de un perfil: " + e.getMessage();

			throw new PerfilException(mensaje, e);
		}
		finally {
			conn.cerrarConexion();
		}
	}

	
}
