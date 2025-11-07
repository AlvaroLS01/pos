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
package com.comerzzia.api.loyalty.persistence.customers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.comerzzia.core.model.empresas.ConfigEmpresaBean;
import com.comerzzia.core.util.base.MantenimientoDao;
import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.db.PreparedStatement;
import com.comerzzia.core.util.fechas.Fechas;
import com.comerzzia.core.util.paginacion.PaginaResultados;

public class FidelizadosDao extends MantenimientoDao{

	private static String TABLA = "F_FIDELIZADOS_TBL";
	private static String VISTA = "F_FIDELIZADOS";
	private static String VISTA_FIDELIZADOS_TARJETA = "F_FIDELIZADOS_TARJETA";
	
	protected static Logger log = Logger.getLogger(FidelizadosDao.class);

	public static PaginaResultados consultar(Connection conn, ConfigEmpresaBean config, ParametrosBuscarFidelizadosBean param) throws SQLException {
		Statement stmt = null;
    	ResultSet rs = null;
    	
    	// Inicializamos la página de resultados
    	List<FidelizadoBean> resultados = new ArrayList<FidelizadoBean>(param.getTamañoPagina());
    	PaginaResultados paginaResultados = new PaginaResultados(param, resultados);
    	
    	// Consultas
    	String sql = obtenerConsulta(config, param);
    	String sqlCount = obtenerConsultaCount(config, param);
        
    	try {
    		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    		
    		// Obtenemos número de resultados
        	log.debug("consultar() - " + sqlCount);
        	rs = stmt.executeQuery(sqlCount);
        	if (rs.next()){
        		paginaResultados.setTotalResultados(rs.getInt(1));
        	}
        	rs.close();
        	
        	// Si tenemos resultados, obtenemos la pagina requerida
            if (paginaResultados.getTotalResultados() > 0) {
            	log.debug("consultar() - " + sql);
                rs = stmt.executeQuery(sql);
                
                int contador = 0;
                if (rs.absolute(paginaResultados.getInicio())) {
                    do {
                		FidelizadoBean fidelizado = new FidelizadoBean();
                		fidelizado.setIdFidelizado(rs.getLong("ID_FIDELIZADO"));
                		fidelizado.setCodFidelizado(rs.getString("CODFIDELIZADO"));
                		fidelizado.setNombre(rs.getString("NOMBRE"));
                		fidelizado.setApellidos(rs.getString("APELLIDOS"));
                		fidelizado.setDomicilio(rs.getString("DOMICILIO"));
                		fidelizado.setPoblacion(rs.getString("POBLACION"));
                		fidelizado.setLocalidad(rs.getString("LOCALIDAD"));
                		fidelizado.setProvincia(rs.getString("PROVINCIA"));
                		fidelizado.setCp(rs.getString("CP"));
                		fidelizado.setCodPais(rs.getString("CODPAIS"));
                		fidelizado.setDesPais(rs.getString("DESPAIS"));
                		fidelizado.setCodTipoIden(rs.getString("CODTIPOIDEN"));
                		fidelizado.setDocumento(rs.getString("DOCUMENTO"));
                		fidelizado.setObservaciones(rs.getString("OBSERVACIONES"));
                		fidelizado.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO"));
                		fidelizado.setSexo(rs.getString("SEXO"));
                		fidelizado.setCodEstCivil(rs.getString("CODESTCIVIL"));
                		fidelizado.setActivo(rs.getString("ACTIVO"));
                		fidelizado.setFechaAlta(rs.getDate("FECHA_ALTA"));
                		fidelizado.setFechaModificacion(rs.getDate("FECHA_MODIFICACION"));
                		fidelizado.setFechaBaja(rs.getDate("FECHA_BAJA"));                		
                		
                		resultados.add(fidelizado);
                        contador++;
                    } while (rs.next() && contador < paginaResultados.getTamañoPagina());                   
                }        		
        	}
            
            return paginaResultados;
    	}
    	finally {
    		try {
    			rs.close();
    		}
    		catch(Exception ignore) {;}
    		try {
    			stmt.close();
    		}
    		catch(Exception ignore) {;}
    	}
	}
	
	
	public static String obtenerConsulta(ConfigEmpresaBean config, ParametrosBuscarFidelizadosBean param) {
		String sql = null;
		String vista = null;
		
		// Establecer la vista que se consultará en función de si se filtra por tarjeta o no
		if (param.getNumeroTarjeta() == null || param.getNumeroTarjeta().isEmpty()) {
			vista = VISTA;
		}
		else {
			vista = VISTA_FIDELIZADOS_TARJETA;
		}
		
		sql = "SELECT ID_FIDELIZADO, " +
					 "CODFIDELIZADO, " +
					 "NOMBRE, " +
					 "APELLIDOS, " +
					 "DOMICILIO, " +				
					 "POBLACION, " +
					 "LOCALIDAD, " +
					 "PROVINCIA, " +
					 "CP, " + 
					 "CODPAIS, " +
					 "DESPAIS, " +
					 "CODTIPOIDEN, " +
					 "DOCUMENTO, " +
					 "OBSERVACIONES, " +
					 "FECHA_NACIMIENTO, " +
					 "SEXO, " +
					 "CODESTCIVIL, " +					 
			         "ACTIVO, " +
			         "FECHA_ALTA, " +
			         "FECHA_MODIFICACION, " +
			         "FECHA_BAJA " +
		       "FROM " + getNombreElemento(vista) + " WHERE UID_INSTANCIA = '"+config.getUidInstancia()+"' ";
		
		// Clausula WHERE
        String where = getClausulaWhere(param);
        if (where.length() > 0) {
            sql += where;
        }
        
        // Clausula ORDER BY
        if (!param.getOrden().isEmpty()) {
        	sql += "ORDER BY " + param.getOrden();
        }
        
		return sql;
	}
	
	
	public static String obtenerConsultaCount(ConfigEmpresaBean config, ParametrosBuscarFidelizadosBean param) {
		String sql = null;
		String vista = null;
		
		// Establecer la vista que se consultará en función de si se filtra por tarjeta o no
		if (param.getNumeroTarjeta() == null || param.getNumeroTarjeta().isEmpty()) {
			vista = VISTA;
		}
		else {
			vista = VISTA_FIDELIZADOS_TARJETA;
		}
		
		sql = "SELECT COUNT(*) " +
		      "FROM " + getNombreElemento(vista) + " WHERE UID_INSTANCIA = '"+config.getUidInstancia()+"' ";
		
		// Clausula WHERE
        String where = getClausulaWhere(param);
        if (where.length() > 0) {
            sql += " " + where;
        }
        
		return sql;
	}
	
	
	protected static String getClausulaWhere(ParametrosBuscarFidelizadosBean param) {
		String where = "";
		
		// Si se filtra por número de tarjeta, no tener en cuenta el resto de parámetros porque el número de tarjeta es único
		if (param.getNumeroTarjeta() != null && !param.getNumeroTarjeta().isEmpty()) {
			// NUMERO_TARJETA
			where = "AND  UPPER(NUMERO_TARJETA) LIKE UPPER('" + param.getNumeroTarjeta() + "') ";
		}
		else {
			// NOMBRE
			if (param.getNombre() != null && !param.getNombre().isEmpty()) {
				where = "AND UPPER(NOMBRE) LIKE UPPER('" + param.getNombre() + "%') ";
			}
			
			// APELLIDOS
			if (param.getApellidos() != null && !param.getApellidos().isEmpty()) {
	
				where += "AND UPPER(APELLIDOS) LIKE UPPER('" + param.getApellidos() + "%') ";
			}
			
			// DOCUMENTO
			if (param.getDocumento() != null && !param.getDocumento().isEmpty()) {
				where += "AND DOCUMENTO  LIKE '" + param.getDocumento() + "%' ";
			}
	
			//ACTIVO
			if (param.getActivo() != null && !param.getActivo().isEmpty()) {

				where += "AND ACTIVO = '" + param.getActivo() + "' ";
			}
		}
		
		return where;
	}
	
	
	public static FidelizadoBean consultar(Connection conn, ConfigEmpresaBean config, Long idCliente) throws SQLException {
		PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	FidelizadoBean fidelizado = null;
    	String sql = null;
    	
    	sql = "SELECT ID_FIDELIZADO, " +
    	 "CODFIDELIZADO, " +
		 "NOMBRE, " +
		 "APELLIDOS, " +
		 "DOMICILIO, " +				
		 "POBLACION, " +
		 "LOCALIDAD, " +
		 "PROVINCIA, " +
		 "CP, " + 
		 "CODPAIS, " +
		 "DESPAIS, " +
		 "CODTIPOIDEN, " +
		 "DOCUMENTO, " +
		 "OBSERVACIONES, " +
		 "FECHA_NACIMIENTO, " +
		 "SEXO, " +
		 "CODESTCIVIL, " +
		 "DESESTCIVIL, " +
        "ACTIVO, " +
        "FECHA_ALTA, " +
        "FECHA_MODIFICACION, " +
        "FECHA_BAJA " +
        "FROM " + getNombreElemento(VISTA) +
       "WHERE UID_INSTANCIA ='"+config.getUidInstancia() + "' AND ID_FIDELIZADO = ? ";
    	try {
    		pstmt = new PreparedStatement(conn, sql);
    		pstmt.setLong(1, idCliente);
    		
        	log.debug("consultar() - " + pstmt);
            rs = pstmt.executeQuery();
        	
        	if (rs.next()){        		
        		fidelizado = new FidelizadoBean();
        		fidelizado.setIdFidelizado(rs.getLong("ID_FIDELIZADO"));
        		fidelizado.setCodFidelizado(rs.getString("CODFIDELIZADO"));
        		fidelizado.setNombre(rs.getString("NOMBRE"));
        		fidelizado.setApellidos(rs.getString("APELLIDOS"));
        		fidelizado.setDomicilio(rs.getString("DOMICILIO"));
        		fidelizado.setPoblacion(rs.getString("POBLACION"));
        		fidelizado.setLocalidad(rs.getString("LOCALIDAD"));
        		fidelizado.setProvincia(rs.getString("PROVINCIA"));
        		fidelizado.setCp(rs.getString("CP"));
        		fidelizado.setCodPais(rs.getString("CODPAIS"));
        		fidelizado.setDesPais(rs.getString("DESPAIS"));
        		fidelizado.setCodTipoIden(rs.getString("CODTIPOIDEN"));
        		fidelizado.setDocumento(rs.getString("DOCUMENTO"));
        		fidelizado.setObservaciones(rs.getString("OBSERVACIONES"));
        		fidelizado.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO"));
        		fidelizado.setSexo(rs.getString("SEXO"));
        		fidelizado.setCodEstCivil(rs.getString("CODESTCIVIL"));
        		fidelizado.setDesEstCivil(rs.getString("DESESTCIVIL"));
        		fidelizado.setActivo(rs.getString("ACTIVO"));
        		fidelizado.setFechaAlta(rs.getDate("FECHA_ALTA"));
        		fidelizado.setFechaModificacion(rs.getDate("FECHA_MODIFICACION"));
        		fidelizado.setFechaBaja(rs.getDate("FECHA_BAJA"));               
        	}
        	
    		return fidelizado;
    	}
    	finally {
    		try{
    			rs.close();
    		}
    		catch(Exception ignore) {;}
    		try{
    			pstmt.close();
    		}
    		catch(Exception ignore) {;}
    	}
	}
	
	/**
	 * Este método obtiene un cliente a partir de su email, que debe de ser único.
	 * 
	 * @param conn
	 * @param config
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public static FidelizadoBean consultar(Connection conn, ConfigEmpresaBean config, String email) throws SQLException {
		/**/
		PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	FidelizadoBean fidelizado = null;
    	String sql = null;
    	
		sql = "SELECT ID_FIDELIZADO " +
    	        "FROM " + getNombreElemento(VISTA) +
		       "WHERE UID_INSTANCIA = ? AND EMAIL = ? ";
    	
    	try {
    		pstmt = new PreparedStatement(conn, sql);
    		pstmt.setString(1, config.getUidInstancia());
    		pstmt.setString(2, email);
    		
        	log.debug("consultar() - " + pstmt);
            rs = pstmt.executeQuery();
        	
        	if (rs.next()) {
        		fidelizado = consultar(conn, config, rs.getLong("ID_FIDELIZADO"));
        	}
        	
    		return fidelizado;
    	}
    	finally {
    		try{
    			rs.close();
    		}
    		catch(Exception ignore) {;}
    		try{
    			pstmt.close();
    		}
    		catch(Exception ignore) {;}
    	}
	}
	
	public static void insert(Connection conn, ConfigEmpresaBean config, FidelizadoBean cliente) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		
		sql = "INSERT INTO " + getNombreElemento(TABLA) + 
				"(" +
				"UID_INSTANCIA, " +
				"ID_FIDELIZADO, " +
				"CODFIDELIZADO, " +
				"NOMBRE, " +
				"APELLIDOS, " +
				"DOMICILIO, " +
				"POBLACION, " +
				"LOCALIDAD, " +
				"PROVINCIA, " +
				"CP, " +
				"CODPAIS, " +		
				"CODTIPOIDEN, " +
				"DOCUMENTO, " + 				
				"OBSERVACIONES, " +
				"FECHA_NACIMIENTO, " +
				"SEXO, " +
				"CODESTCIVIL, " +
				"ACTIVO, " +
				"FECHA_ALTA, " +
				"FECHA_MODIFICACION, " +
				"FECHA_BAJA) " +
		      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setString(1, config.getUidInstancia());
			pstmt.setLong(2, cliente.getIdFidelizado());
			pstmt.setString(3, cliente.getCodFidelizado());
			pstmt.setString(4, cliente.getNombre());
			pstmt.setString(5, cliente.getApellidos());
			pstmt.setString(6, cliente.getDomicilio());
        	pstmt.setString(7, cliente.getPoblacion());
        	pstmt.setString(8, cliente.getLocalidad());
        	pstmt.setString(9, cliente.getProvincia());
			pstmt.setString(10, cliente.getCp());
			pstmt.setString(11, cliente.getCodPais());			
			pstmt.setString(12, cliente.getCodTipoIden());
			pstmt.setString(13, cliente.getDocumento());
        	pstmt.setString(14, cliente.getObservaciones());
        	pstmt.setDate(15,Fechas.toSqlDate(cliente.getFechaNacimiento()));
        	pstmt.setString(16, cliente.getSexo());
        	pstmt.setString(17, cliente.getCodEstCivil());
			pstmt.setString(18, cliente.getActivoString());
			pstmt.setDate(19, Fechas.toSqlDate(cliente.getFechaAlta()));
			pstmt.setDate(20, Fechas.toSqlDate(cliente.getFechaModificacion()));
			pstmt.setDate(21, Fechas.toSqlDate(cliente.getFechaBaja()));
        	
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
    		catch(Exception ignore) {;}
    	}
	}
	
	public static void update(Connection conn, ConfigEmpresaBean config, FidelizadoBean cliente) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		
		sql = "UPDATE " + getNombreElemento(TABLA) +
		         "SET NOMBRE = ?, " +
		         	 "APELLIDOS = ?, " +
		         	 "CODFIDELIZADO = ?, " +
		         	 "DOMICILIO = ?, " +
		         	 "POBLACION = ?, " +
		         	 "LOCALIDAD = ?, " +
		         	 "PROVINCIA = ?," +
		      	     "CP = ?, " +
		      	     "CODPAIS = ?, " +		         	
		      	     "CODTIPOIDEN = ?, " +
		      	     "DOCUMENTO = ?, " +		      	     
		      	     "OBSERVACIONES = ?, " +
		      	     "FECHA_NACIMIENTO = ?, " +
		      	     "SEXO = ?, " +
		      	     "CODESTCIVIL = ?, " +
		      	     "ACTIVO = ?, " +
		      	     "FECHA_MODIFICACION = ?, " +
		      	     "FECHA_BAJA = ? " +		      	     
		       "WHERE UID_INSTANCIA = ? " +
		       		" AND ID_FIDELIZADO = ? ";
		
		try {
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setString(1, cliente.getNombre());
			pstmt.setString(2, cliente.getApellidos());
			pstmt.setString(3, cliente.getCodFidelizado());
			pstmt.setString(4, cliente.getDomicilio());
        	pstmt.setString(5, cliente.getPoblacion());
        	pstmt.setString(6, cliente.getLocalidad());
        	pstmt.setString(7, cliente.getProvincia());
			pstmt.setString(8, cliente.getCp());
			pstmt.setString(9, cliente.getCodPais());			
			pstmt.setString(10, cliente.getCodTipoIden());
			pstmt.setString(11, cliente.getDocumento());     
        	pstmt.setString(12, cliente.getObservaciones());
        	pstmt.setDate(13, Fechas.toSqlDate(cliente.getFechaNacimiento()));
        	pstmt.setString(14, cliente.getSexo());
        	pstmt.setString(15, cliente.getCodEstCivil());
			pstmt.setString(16, cliente.getActivoString());
			pstmt.setTimestamp(17, new Timestamp(cliente.getFechaModificacion().getTime()));
			pstmt.setDate(18, Fechas.toSqlDate(cliente.getFechaBaja()));			
			pstmt.setString(19, config.getUidInstancia());
			pstmt.setLong(20, cliente.getIdFidelizado());
			
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
    		catch(Exception ignore) {;}
    	}
	}
	
	
	public static void delete(Connection conn, ConfigEmpresaBean config, FidelizadoBean cliente) throws SQLException {
		delete(conn, config, cliente.getIdFidelizado());
	}
	
	
	public static void delete(Connection conn, ConfigEmpresaBean config, Long idCliente) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		
		sql = "DELETE " +
				"FROM " + getNombreElemento(TABLA) +
		       "WHERE UID_INSTANCIA = ? AND ID_FIDELIZADO = ?";
		
		try {
			pstmt = new PreparedStatement(conn, sql);
        	pstmt.setString(1, config.getUidInstancia());
        	pstmt.setLong(2, idCliente);
        	
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
    		catch(Exception ignore) {;}
    	}
	}
	

	public static List<FidelizadoBean> consultarFidelizadosPorFiltro(Connection conn, ConfigEmpresaBean config, FidelizadoBean fidelizado) 
			throws SQLException{
		PreparedStatement pstmt = null;
    	ResultSet rs = null;
		List<FidelizadoBean> lstFidelizado = new ArrayList<FidelizadoBean>();
		String sql = obtenerConsultaPorFiltro(config, fidelizado);
		
		try {
    		pstmt = new PreparedStatement(conn, sql);
    		
        	log.debug("consultarFidelizadoPorFiltro() - " + pstmt);
            rs = pstmt.executeQuery();
        	
        	while (rs.next()) {
        		FidelizadoBean fidelizadoBean = new FidelizadoBean();
        		fidelizadoBean.setIdFidelizado(rs.getLong("ID_FIDELIZADO"));
        		fidelizadoBean.setCodFidelizado(rs.getString("CODFIDELIZADO"));
        		fidelizadoBean.setNombre(rs.getString("NOMBRE"));
        		fidelizadoBean.setApellidos(rs.getString("APELLIDOS"));
        		fidelizadoBean.setDomicilio(rs.getString("DOMICILIO"));
        		fidelizadoBean.setPoblacion(rs.getString("POBLACION"));
        		fidelizadoBean.setLocalidad(rs.getString("LOCALIDAD"));
        		fidelizadoBean.setProvincia(rs.getString("PROVINCIA"));
        		fidelizadoBean.setCp(rs.getString("CP"));
        		fidelizadoBean.setCodPais(rs.getString("CODPAIS"));
        		fidelizadoBean.setCodTipoIden(rs.getString("CODTIPOIDEN"));
        		fidelizadoBean.setDocumento(rs.getString("DOCUMENTO"));
        		fidelizadoBean.setObservaciones(rs.getString("OBSERVACIONES"));
        		fidelizadoBean.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO"));
        		fidelizadoBean.setSexo(rs.getString("SEXO"));
        		fidelizadoBean.setCodEstCivil(rs.getString("CODESTCIVIL"));
        		fidelizadoBean.setActivo(rs.getString("ACTIVO"));
        		fidelizadoBean.setFechaAlta(rs.getDate("FECHA_ALTA"));
        		fidelizadoBean.setFechaModificacion(rs.getDate("FECHA_MODIFICACION"));
        		fidelizadoBean.setFechaBaja(rs.getDate("FECHA_BAJA"));                		
        		
        		lstFidelizado.add(fidelizadoBean);
        	}
        	
    		return lstFidelizado;
    	}
    	finally {
    		try{
    			rs.close();
    		}
    		catch(Exception ignore) {;}
    		try{
    			pstmt.close();
    		}
    		catch(Exception ignore) {;}
    	}
	}

	protected static String obtenerConsultaPorFiltro(ConfigEmpresaBean config, FidelizadoBean fidelizado) {
		String sql = "SELECT ID_FIDELIZADO, " +
					 "CODFIDELIZADO, " +
					 "NOMBRE, " +
					 "APELLIDOS, " +
					 "DOMICILIO, " +				
					 "POBLACION, " +
					 "LOCALIDAD, " +
					 "PROVINCIA, " +
					 "CP, " + 
					 "CODPAIS, " +
					 "CODTIPOIDEN, " +
					 "DOCUMENTO, " +
					 "OBSERVACIONES, " +
					 "FECHA_NACIMIENTO, " +
					 "SEXO, " +
					 "CODESTCIVIL, " +
			         "ACTIVO, " +
			         "FECHA_ALTA, " +
			         "FECHA_MODIFICACION, " +
			         "FECHA_BAJA " +
		       "FROM " + getNombreElemento(VISTA_FIDELIZADOS_TARJETA) + " WHERE UID_INSTANCIA = '"+config.getUidInstancia()+"' ";
		
		// Clausula WHERE
        String where = getClausulaWherePorFiltro(fidelizado);
        if (where.length() > 0) {
            sql += where;
        }
		
		return sql;
	}


	protected static String getClausulaWherePorFiltro(FidelizadoBean fidelizado) {
		String where = "";
		
		// NUMERO_TARJETA
		if (fidelizado.getTarjetas() != null && !fidelizado.getTarjetas().isEmpty()) {
			where = "AND NUMERO_TARJETA = '" + fidelizado.getTarjetas().get(0).getNumeroTarjeta() + "' ";
		}
		// ID_FIDELIZADO
		if (fidelizado.getIdFidelizado() != null) {
			where += "AND ID_FIDELIZADO  = '" + fidelizado.getIdFidelizado() + "' ";
		}
		// CODFIDELIZADO
		if (fidelizado.getCodFidelizado() != null) {
			where += "AND CODFIDELIZADO  = '" + fidelizado.getCodFidelizado() + "' ";
		}
		// DOCUMENTO
		if (fidelizado.getDocumento() != null && !fidelizado.getDocumento().isEmpty()) {
				where += "AND DOCUMENTO  = '" + fidelizado.getDocumento() + "' ";
		}
		
		return where;
	}
}
