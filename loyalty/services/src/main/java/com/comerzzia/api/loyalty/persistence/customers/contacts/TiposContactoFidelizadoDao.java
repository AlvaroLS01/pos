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
package com.comerzzia.api.loyalty.persistence.customers.contacts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.comerzzia.core.model.empresas.ConfigEmpresaBean;
import com.comerzzia.core.util.base.MantenimientoDao;
import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.db.PreparedStatement;

public class TiposContactoFidelizadoDao extends MantenimientoDao {

	protected static Logger log = Logger.getLogger(TiposContactoFidelizadoDao.class);
	public static String TABLA_TIPOS_CONTACTO = "D_TIPOS_CONTACTO_TBL";
	public static String TABLA = "F_FIDELIZADOS_CONTACTO_TBL";
	public static String VISTA = "F_FIDELIZADO_CONTACTO";

	public static List<TiposContactoFidelizadoBean> consultar(Connection conn, ConfigEmpresaBean config, Long idCliente) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;

		List<TiposContactoFidelizadoBean> detalles = null;
		sql = "SELECT CODTIPOCON, DESTIPOCON, PUEDE_RECIBIR_NOTIFICACIONES, ID_FIDELIZADO, VALOR, RECIBE_NOTIFICACIONES, RECIBE_NOTIFICACIONES_COM " + "FROM " + getNombreElemento(VISTA) + " WHERE ID_FIDELIZADO = ? "
		        + "AND UID_INSTANCIA = '" + config.getUidInstancia() + "'";

		try {
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setLong(1, idCliente);

			log.debug("consultar() - " + pstmt);
			rs = pstmt.executeQuery();

			detalles = new ArrayList<TiposContactoFidelizadoBean>();
			while (rs.next()) {
				TiposContactoFidelizadoBean tiposContactos = new TiposContactoFidelizadoBean();
				tiposContactos.setIdFidelizado(rs.getLong("ID_FIDELIZADO"));
				tiposContactos.setCodTipoCon(rs.getString("CODTIPOCON"));
				tiposContactos.setDesTipoCon(rs.getString("DESTIPOCON"));
				tiposContactos.setValor(rs.getString("VALOR"));
				tiposContactos.setRecibeNotificaciones(rs.getString("RECIBE_NOTIFICACIONES"));
				tiposContactos.setRecibeNotificacionesCom(rs.getString("RECIBE_NOTIFICACIONES_COM"));
				tiposContactos.setPuedeRecibirNotificaciones(rs.getString("PUEDE_RECIBIR_NOTIFICACIONES"));
				detalles.add(tiposContactos);
			}

			return detalles;
		}
		catch (SQLException e) {
			throw getDaoException(e);
		}
		finally {
			try {
				rs.close();
			}
			catch (Exception ignore) {
			}

			try {
				pstmt.close();
			}
			catch (Exception ignore) {
			}
		}
	}

	public static TiposContactoFidelizadoBean consultarPorEmail(Connection conn, ConfigEmpresaBean config, String email) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;

		TiposContactoFidelizadoBean tiposContactos = null;
		sql = "SELECT CODTIPOCON, DESTIPOCON, PUEDE_RECIBIR_NOTIFICACIONES, ID_FIDELIZADO, VALOR, RECIBE_NOTIFICACIONES, RECIBE_NOTIFICACIONES_COM " + "FROM " + getNombreElemento(VISTA) + " WHERE CODTIPOCON = 'EMAIL' AND VALOR = ? "
		        + "AND UID_INSTANCIA = '" + config.getUidInstancia() + "'";

		try {
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setString(1, email);

			log.debug("consultar() - " + pstmt);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				tiposContactos = new TiposContactoFidelizadoBean();
				tiposContactos.setIdFidelizado(rs.getLong("ID_FIDELIZADO"));
				tiposContactos.setCodTipoCon(rs.getString("CODTIPOCON"));
				tiposContactos.setDesTipoCon(rs.getString("DESTIPOCON"));
				tiposContactos.setValor(rs.getString("VALOR"));
				tiposContactos.setRecibeNotificaciones(rs.getString("RECIBE_NOTIFICACIONES"));
				tiposContactos.setRecibeNotificacionesCom(rs.getString("RECIBE_NOTIFICACIONES_COM"));
				tiposContactos.setPuedeRecibirNotificaciones(rs.getString("PUEDE_RECIBIR_NOTIFICACIONES"));				
			}

			return tiposContactos;
		}
		catch (SQLException e) {
			throw getDaoException(e);
		}
		finally {
			try {
				rs.close();
			}
			catch (Exception ignore) {
			}

			try {
				pstmt.close();
			}
			catch (Exception ignore) {
			}
		}
	}

	
	public static List<TiposContactoFidelizadoBean> consultarDisponibles(Connection conn, ConfigEmpresaBean config) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;

		List<TiposContactoFidelizadoBean> detalles = null;
		sql = "SELECT CODTIPOCON, DESTIPOCON, PUEDE_RECIBIR_NOTIFICACIONES " + "FROM " + getNombreElemento(TABLA_TIPOS_CONTACTO) + "WHERE UID_INSTANCIA = '" + config.getUidInstancia() + "'";

		try {
			pstmt = new PreparedStatement(conn, sql);
			log.debug("consultar() - " + pstmt);
			rs = pstmt.executeQuery();

			detalles = new ArrayList<TiposContactoFidelizadoBean>();
			while (rs.next()) {
				TiposContactoFidelizadoBean tiposContactos = new TiposContactoFidelizadoBean();
				tiposContactos.setCodTipoCon(rs.getString("CODTIPOCON"));
				tiposContactos.setDesTipoCon(rs.getString("DESTIPOCON"));
				tiposContactos.setPuedeRecibirNotificaciones(rs.getString("PUEDE_RECIBIR_NOTIFICACIONES"));
				detalles.add(tiposContactos);
			}

			return detalles;
		}
		catch (SQLException e) {
			throw getDaoException(e);
		}
		finally {
			try {
				rs.close();
			}
			catch (Exception ignore) {
			}

			try {
				pstmt.close();
			}
			catch (Exception ignore) {
			}
		}
	}

	public static void insert(Connection conn, ConfigEmpresaBean config, TiposContactoFidelizadoBean tipoContacto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;

		sql = "INSERT INTO " + getNombreElemento(TABLA) + "(UID_INSTANCIA, ID_FIDELIZADO, CODTIPOCON, VALOR, RECIBE_NOTIFICACIONES, RECIBE_NOTIFICACIONES_COM) " + "VALUES (?, ?, ?, ?, ?, ?)";

		try {
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setString(1, config.getUidInstancia());
			pstmt.setLong(2, tipoContacto.getIdFidelizado());
			pstmt.setString(3, tipoContacto.getCodTipoCon());
			pstmt.setString(4, tipoContacto.getValor());
			pstmt.setString(5, tipoContacto.getRecibeNotificaciones() ? "S" : "N");
			pstmt.setString(6, tipoContacto.isRecibeNotificacionesCom() ? "S" : "N");

			log.debug("insert() - " + pstmt);

			pstmt.execute();
		}
		catch (SQLException e) {
			throw getDaoException(e);
		}
		finally {
			try {
				pstmt.close();
			}
			catch (Exception ignore) {
				;
			}
		}
	}

	public static void update(Connection conn, ConfigEmpresaBean config, TiposContactoFidelizadoBean tipoContacto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;

		sql = "UPDATE " + getNombreElemento(TABLA) + "SET VALOR = ?, RECIBE_NOTIFICACIONES = ?,  RECIBE_NOTIFICACIONES_COM = ? WHERE UID_INSTANCIA = ? " + "AND ID_FIDELIZADO = ? AND CODTIPOCON = ?";

		try {
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setString(1, tipoContacto.getValor());
			pstmt.setString(2, tipoContacto.getRecibeNotificaciones() ? "S" : "N");
			pstmt.setString(3, tipoContacto.isRecibeNotificacionesCom() ? "S" : "N");
			pstmt.setString(4, config.getUidInstancia());
			pstmt.setLong(5, tipoContacto.getIdFidelizado());
			pstmt.setString(6, tipoContacto.getCodTipoCon());

			log.debug("update() - " + pstmt);
			pstmt.execute();
		}
		catch (SQLException e) {
			throw getDaoException(e);
		}
		finally {
			try {
				pstmt.close();
			}
			catch (Exception ignore) {
				;
			}
		}
	}

	public static void delete(Connection conn, ConfigEmpresaBean config, TiposContactoFidelizadoBean tipoContacto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;

		sql = "DELETE FROM " + getNombreElemento(TABLA) + "WHERE UID_INSTANCIA = ? " + "AND CODTIPOCON = ? AND ID_FIDELIZADO = ? ";

		try {
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setString(1, config.getUidInstancia());
			pstmt.setString(2, tipoContacto.getCodTipoCon());
			pstmt.setLong(3, tipoContacto.getIdFidelizado());
			log.debug("delete() - " + pstmt);

			pstmt.execute();
		}
		catch (SQLException e) {
			throw getDaoException(e);
		}
		finally {
			try {
				pstmt.close();
			}
			catch (Exception ignore) {
				;
			}
		}
	}
	
	public static void delete(Connection conn, ConfigEmpresaBean config, Long idFidelizado) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;

		sql = "DELETE FROM " + getNombreElemento(TABLA) + "WHERE UID_INSTANCIA = ? " + " AND ID_FIDELIZADO = ? ";

		try {
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setString(1, config.getUidInstancia());
			pstmt.setLong(2, idFidelizado);			
			log.debug("delete() - " + pstmt);

			pstmt.execute();
		}
		catch (SQLException e) {
			throw getDaoException(e);
		}
		finally {
			try {
				pstmt.close();
			}
			catch (Exception ignore) {
				;
			}
		}
	}

	public static String consultarEstadoNotificacion(Connection conn,
			ConfigEmpresaBean config, Long idFidelizado, String tipoNotificacion) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		String estadoNotificacionEmail = null;
		
		sql = 	"SELECT RECIBE_NOTIFICACIONES " + 
				"FROM " + getNombreElemento(VISTA) + 
				" WHERE ID_FIDELIZADO = ? " +
		        "AND UID_INSTANCIA = ? "  +
				"AND CODTIPOCON = ?";

		try {
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setLong(1, idFidelizado);
			pstmt.setString(2, config.getUidInstancia());
			pstmt.setString(3, tipoNotificacion);

			log.debug("consultarEstadoNotificacion() - " + pstmt);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				estadoNotificacionEmail = rs.getString("RECIBE_NOTIFICACIONES");
			}

			return estadoNotificacionEmail;
		}
		catch (SQLException e) {
			throw getDaoException(e);
		}
		finally {
			try {
				rs.close();
			}
			catch (Exception ignore) {
			}

			try {
				pstmt.close();
			}
			catch (Exception ignore) {
			}
		}
	}
}
