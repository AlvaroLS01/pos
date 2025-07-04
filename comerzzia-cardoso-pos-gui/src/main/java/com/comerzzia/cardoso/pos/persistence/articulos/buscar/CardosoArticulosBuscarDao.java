package com.comerzzia.cardoso.pos.persistence.articulos.buscar;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.comerzzia.core.util.base.MantenimientoDao;
import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.db.PreparedStatement;
import com.comerzzia.core.util.fechas.Fechas;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosParamBuscar;

/**
 * GAP - DESCUENTO TARIFA
 * Incluye métodosde búsqueda de artículos, añadiendo buscar en las tablas de tarifas, para poder sacar los descuentos.
 * Tablas : "X_TARIFAS_DET_TBL", "D_TARIFAS_DET_TBL".
 */
public class CardosoArticulosBuscarDao extends MantenimientoDao{

    private static final String TABLA_ARTICULOS = "D_ARTICULOS_TBL";
    private static final String TABLA_CODBAR = "D_ARTICULOS_CODBAR_TBL";
    private static final String TABLA_TARIFAS = "D_TARIFAS_DET_TBL";
    private static final String TABLA_XTARIFAS = "X_TARIFAS_DET_TBL";

    private static final Logger log = Logger.getLogger(CardosoArticulosBuscarDao.class);

	public static List<ArticuloBuscarBean> buscarArticulos(Connection conn, ArticulosParamBuscar param) throws SQLException{
		log.debug("buscarArticulos() : GAP - DESCUENTO TARIFA");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;

		List<ArticuloBuscarBean> articulos = new ArrayList<ArticuloBuscarBean>();

		sql = "SELECT A.CODART, A.DESART, T.PRECIO_TOTAL, XT.DESCUENTO_TARIFA, (SELECT COUNT(*) "
				+ "FROM D_PROMOCIONES_DET_TBL PRO WHERE PRO.UID_ACTIVIDAD = A.UID_ACTIVIDAD AND PRO.CODART = A.CODART AND ? BETWEEN PRO.FECHA_INICIO AND PRO.FECHA_FIN) AS EN_PROMOCION " 
				+ "FROM " + getNombreElemento(TABLA_ARTICULOS) + " A " 
				+ "LEFT JOIN " + getNombreElemento(TABLA_TARIFAS) + " T " + "ON (A.UID_ACTIVIDAD = T.UID_ACTIVIDAD AND  T.CODTAR = ? AND A.CODART = T.CODART) " 
				+ "LEFT JOIN " + getNombreElemento(TABLA_XTARIFAS) + " XT " + "ON (A.UID_ACTIVIDAD = XT.UID_ACTIVIDAD AND XT.CODTAR = ? AND A.CODART = XT.CODART) " 
				+ "WHERE A.UID_ACTIVIDAD = ? AND UPPER(A.CODART) LIKE UPPER(?) " + "AND UPPER (A.DESART) LIKE UPPER(?) "
				+ "AND T.FECHA_INICIO IN (SELECT MAX(FECHA_INICIO) FROM D_TARIFAS_DET_TBL WHERE UID_ACTIVIDAD = T.UID_ACTIVIDAD AND CODTAR = T.CODTAR AND CODART = T.CODART AND FECHA_INICIO <= CURDATE())"
				+ " ORDER BY CODART";

		try{
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setDate(1, Fechas.toSqlDate(new Date()));
			pstmt.setString(2, param.getCodTarifa());
			pstmt.setString(3, param.getCodTarifa());  
			pstmt.setString(4, param.getUidActividad());
			pstmt.setString(5, param.getCodigo() + "%");
			pstmt.setString(6, "%" + param.getDescripcion() + "%");
			
			log.info("buscarArticulos() - CONSULTA : " + pstmt);

			rs = pstmt.executeQuery();

			while(rs.next()){
				CardosoArticuloBuscarBean articuloBuscar = new CardosoArticuloBuscarBean();
				articuloBuscar.setCodArticulo(rs.getString("CODART"));
				articuloBuscar.setDesArticulo(rs.getString("DESART"));
				articuloBuscar.setValorDesglose1("*");
				articuloBuscar.setValorDesglose2("*");
				articuloBuscar.setPrecio(rs.getBigDecimal("PRECIO_TOTAL"));
				boolean promocionado = false;
				if(rs.getInt("EN_PROMOCION") > 0){
					promocionado = true;
				}
				BigDecimal descuentoTarifa = rs.getBigDecimal("DESCUENTO_TARIFA");
				articuloBuscar.setDescuentoTarifa(descuentoTarifa != null ? descuentoTarifa : BigDecimal.ZERO);
				articuloBuscar.setPromocionado(promocionado);
				articulos.add(articuloBuscar);
			}
			return articulos;
		}
		finally{
			try{
				rs.close();
			}
			catch(Exception ignore){
			}
			try{
				pstmt.close();
			}
			catch(Exception ignore){
			}
		}
	}

	public static List<ArticuloBuscarBean> buscarArticulosDesglose(Connection conn, ArticulosParamBuscar param) throws SQLException{
		log.debug("buscarArticulosDesglose() : GAP - DESCUENTO TARIFA");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;

		List<ArticuloBuscarBean> articulos = new ArrayList<ArticuloBuscarBean>();

		sql = "SELECT A.CODART, A.DESART, T.PRECIO_TOTAL, C.DESGLOSE1, C.DESGLOSE2, "
				+ "(SELECT COUNT(*) FROM D_PROMOCIONES_DET_TBL PRO " + "WHERE PRO.UID_ACTIVIDAD = A.UID_ACTIVIDAD "
		        + "AND PRO.CODART = A.CODART " + "AND ? BETWEEN PRO.FECHA_INICIO AND PRO.FECHA_FIN) AS EN_PROMOCION " 
				+ "FROM " + getNombreElemento(TABLA_ARTICULOS) + " A " 
		        + "LEFT JOIN " + getNombreElemento(TABLA_TARIFAS) + " T ON (A.UID_ACTIVIDAD = T.UID_ACTIVIDAD AND A.CODART = T.CODART AND T.CODTAR = ?) "
		       //+ "LEFT JOIN " + getNombreElemento(TABLA_XTARIFAS) + " XT " + "ON (A.UID_ACTIVIDAD = XT.UID_ACTIVIDAD AND XT.CODTAR = ? AND A.CODART = XT.CODART) " 
				+ "LEFT JOIN " + getNombreElemento(TABLA_CODBAR) + " C ON (A.UID_ACTIVIDAD = C.UID_ACTIVIDAD AND A.CODART = C.CODART) " 
		        + "WHERE A.UID_ACTIVIDAD = ? AND UPPER(A.CODART) LIKE UPPER(?) AND UPPER (A.DESART) LIKE UPPER(?) "
		        + "AND T.FECHA_INICIO IN (SELECT MAX(FECHA_INICIO) FROM D_TARIFAS_DET_TBL WHERE UID_ACTIVIDAD = T.UID_ACTIVIDAD AND CODTAR = T.CODTAR AND CODART = T.CODART AND FECHA_INICIO <= CURDATE())"
				+ " ORDER BY CODART";

		try{
			pstmt = new PreparedStatement(conn, sql);
			pstmt.setDate(1, Fechas.toSqlDate(new Date()));
			pstmt.setString(2, param.getCodTarifa());
			pstmt.setString(3, param.getUidActividad());
			pstmt.setString(4, param.getCodigo() + "%");
			pstmt.setString(5, "%" + param.getDescripcion() + "%");
			
			log.info("buscarArticulosDesglose() - CONSULTA : " + pstmt);

			rs = pstmt.executeQuery();

			while(rs.next()){
				CardosoArticuloBuscarBean articuloBuscar = new CardosoArticuloBuscarBean();
				articuloBuscar.setCodArticulo(rs.getString("CODART"));
				articuloBuscar.setDesArticulo(rs.getString("DESART"));
				articuloBuscar.setValorDesglose1(rs.getString("DESGLOSE1"));
				articuloBuscar.setValorDesglose2(rs.getString("DESGLOSE2"));
				articuloBuscar.setPrecio(rs.getBigDecimal("PRECIO_TOTAL"));
				boolean promocionado = false;
				if(rs.getInt("EN_PROMOCION") > 0){
					promocionado = true;
				}
				articuloBuscar.setPromocionado(promocionado);
				articulos.add(articuloBuscar);
			}
			return articulos;
		}
		finally{
			try{
				rs.close();
			}
			catch(Exception ignore){
			}
			try{
				pstmt.close();
			}
			catch(Exception ignore){
			}
		}
	}
	
}
