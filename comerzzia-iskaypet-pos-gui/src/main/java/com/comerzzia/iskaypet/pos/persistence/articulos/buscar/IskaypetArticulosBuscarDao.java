package com.comerzzia.iskaypet.pos.persistence.articulos.buscar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.db.PreparedStatement;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosBuscarDao;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosParamBuscar;
import com.comerzzia.pos.services.core.sesion.Sesion;

public class IskaypetArticulosBuscarDao extends ArticulosBuscarDao {

    private static final String D_ARTICULOS_TBL = "D_ARTICULOS_TBL";
    private static final String D_ARTICULOS_CODBAR_TBL = "D_ARTICULOS_CODBAR_TBL";
	private static final String D_TARIFAS_DET_TBL = "D_TARIFAS_DET_TBL";
	private static final String D_TARIFAS_CAB_TBL = "D_TARIFAS_CAB_TBL";
	private static final String D_IMP_PORCENTAJES_TBL = "D_IMP_PORCENTAJES_TBL";

    private static final Logger log = Logger.getLogger(IskaypetArticulosBuscarDao.class);
    
    public static Long consultarGrupoImpuestosActual(Connection conn, ArticulosParamBuscar param) throws SQLException{
    	PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;
        
        sql = "SELECT  ID_GRUPO_IMPUESTOS " +
                "FROM  D_IMP_GRUPOS_TBL " +
                "WHERE " +
                    "UID_ACTIVIDAD = ? " +
                    "AND VIGENCIA_DESDE <= CURDATE()  ORDER BY VIGENCIA_DESDE DESC ";
        
        try {
            pstmt = new PreparedStatement(conn, sql);
            pstmt.setString(1, param.getUidActividad());
            log.debug("buscarArticulos() - " + pstmt);

            rs = pstmt.executeQuery();

            while (rs.next()) {
            	return rs.getLong("ID_GRUPO_IMPUESTOS");
            }
            return null;
        } finally {
            try {
				if(rs != null)
					rs.close();

            } catch (Exception ignore) {
            }
            try {
				if (pstmt != null)
					pstmt.close();
            } catch (Exception ignore) {
            }
        }
        
    }

    public static List<ArticuloBuscarBean> buscarArticulosIskaypet(Connection conn, ArticulosParamBuscar param, Sesion sesion) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;

        List<ArticuloBuscarBean> articulos = new ArrayList<>();

        String whereActivo = param.getActivo() != null ? "AND ACTIVO = ? " : "";

        String codTarTienda = sesion.getAplicacion().getTienda().getCliente().getCodtar();
        String codtarSeleccionado = StringUtils.isNotEmpty(codTarTienda) ? codTarTienda : "GENERAL"; 
        Long idTratImpuesto = sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos();
        
        long grupoImpuestos = consultarGrupoImpuestosActual(conn, param);
        
		sql =
			"SELECT " +
				"art.CODART, " +
				"art.DESART, " +
				"ean.CODIGO_BARRAS as EAN , " +
				"COALESCE( " +
					"IF(cab_clie.PRECIOS_CON_IMPUESTOS = 'S', round(precio_tar_clie.PRECIO_TOTAL / (1+(PORC.PORCENTAJE/100)), 4), precio_tar_clie.PRECIO_VENTA),  " +
					"IF(cab_gral.PRECIOS_CON_IMPUESTOS = 'S', round(precio_tar_gral.PRECIO_TOTAL / (1+(PORC.PORCENTAJE/100)), 4), precio_tar_gral.PRECIO_VENTA) " +
				") AS PRECIO_VENTA, " +
				"COALESCE( " +
					"IF(cab_clie.PRECIOS_CON_IMPUESTOS = 'S', precio_tar_clie.PRECIO_TOTAL, round(precio_tar_clie.PRECIO_VENTA * (1+(PORC.PORCENTAJE/100)), 2)),  " +
					"IF(cab_gral.PRECIOS_CON_IMPUESTOS = 'S', precio_tar_gral.PRECIO_TOTAL, round(precio_tar_gral.PRECIO_VENTA * (1+(PORC.PORCENTAJE/100)), 2)) " +
				") AS PRECIO_TOTAL " +
			"FROM " +
				getNombreElemento(D_ARTICULOS_TBL) + "art " +
			"INNER JOIN " + getNombreElemento(D_IMP_PORCENTAJES_TBL) + "PORC ON ( " +
				"PORC.UID_ACTIVIDAD = '"+param.getUidActividad()+"' " +
				"AND PORC.ID_GRUPO_IMPUESTOS = " + grupoImpuestos + " " +
				"AND PORC.ID_TRAT_IMPUESTOS = " + idTratImpuesto + " " +
				"AND PORC.CODIMP = art.CODIMP " +
			") " +
			"LEFT JOIN " + getNombreElemento(D_ARTICULOS_CODBAR_TBL) + "ean ON ( " +
				"art.UID_ACTIVIDAD = ean.UID_ACTIVIDAD " +
				"AND art.CODART = ean.CODART " +
				"AND ean.PRINCIPAL = 'S' " +
			") " +
			"LEFT JOIN " + getNombreElemento(D_TARIFAS_CAB_TBL) + "cab_clie ON ( " +
				"cab_clie.UID_ACTIVIDAD = '"+param.getUidActividad()+"' " +
				"AND cab_clie.CODTAR = '"+codtarSeleccionado+"' " +
			") " +
			"LEFT JOIN " + getNombreElemento(D_TARIFAS_CAB_TBL) + "cab_gral ON ( " +
				"cab_gral.UID_ACTIVIDAD = '"+param.getUidActividad()+"' " +
				"AND cab_gral.CODTAR = 'GENERAL' " +
			") " +
			"LEFT JOIN ( " +
				"SELECT " +
					"subq_tar_clie.CODART, " +
					"subq_tar_clie.PRECIO_VENTA, " +
					"subq_tar_clie.PRECIO_TOTAL, " +
					"subq_tar_clie.DESGLOSE1, " +
					"subq_tar_clie.DESGLOSE2 " +
				"FROM " + getNombreElemento(D_TARIFAS_DET_TBL) + "subq_tar_clie " +
				"INNER JOIN ( " +
					"SELECT " +
						"subq_tar_clie_unq.UID_ACTIVIDAD, " +
						"subq_tar_clie_unq.CODTAR, " +
						"subq_tar_clie_unq.CODART, " +
						"MAX(subq_tar_clie_unq.FECHA_INICIO) as FECHA_MAX " +
					"FROM " + getNombreElemento(D_TARIFAS_DET_TBL) + "subq_tar_clie_unq " +
					"WHERE " +
						"subq_tar_clie_unq.UID_ACTIVIDAD = '"+param.getUidActividad()+"' " +
						"AND subq_tar_clie_unq.CODTAR = '"+codtarSeleccionado+"' " +
						"AND subq_tar_clie_unq.FECHA_INICIO <= CURDATE() " +
					"GROUP BY " +
						"subq_tar_clie_unq.UID_ACTIVIDAD, " +
						"subq_tar_clie_unq.CODART " +
				") subq_tar_vigencias on ( " +
					"subq_tar_clie.UID_ACTIVIDAD = subq_tar_vigencias.UID_ACTIVIDAD " +
					"AND subq_tar_clie.CODTAR = subq_tar_vigencias.CODTAR " +
					"AND subq_tar_clie.CODART = subq_tar_vigencias.CODART " +
					"AND subq_tar_clie.FECHA_INICIO = subq_tar_vigencias.FECHA_MAX " +
				") " +
				"WHERE " +
					"subq_tar_clie.UID_ACTIVIDAD = '"+param.getUidActividad()+"' " +
					"AND subq_tar_clie.CODTAR = '"+codtarSeleccionado+"' " +
			") precio_tar_clie on ( " +
				"art.CODART = precio_tar_clie.CODART " +
				"AND coalesce(ean.DESGLOSE1,'*') = precio_tar_clie.DESGLOSE1 " +
				"AND coalesce(ean.DESGLOSE2,'*') = precio_tar_clie.DESGLOSE2 " +
			") " +
			"LEFT JOIN ( " +
				"SELECT " +
					"subq_tar_gral.CODART, " +
					"subq_tar_gral.PRECIO_VENTA, " +
					"subq_tar_gral.PRECIO_TOTAL, " +
					"subq_tar_gral.DESGLOSE1, " +
					"subq_tar_gral.DESGLOSE2 " +
				"FROM " + getNombreElemento(D_TARIFAS_DET_TBL) + "subq_tar_gral " +
				"INNER JOIN ( " +
					"SELECT " +
						"subq_tar_gral_unq.UID_ACTIVIDAD, " +
						"subq_tar_gral_unq.CODTAR, " +
						"subq_tar_gral_unq.CODART, " +
						"MAX(subq_tar_gral_unq.FECHA_INICIO) as FECHA_MAX " +
					"FROM " + getNombreElemento(D_TARIFAS_DET_TBL) + "subq_tar_gral_unq " +
					"WHERE " +
						"subq_tar_gral_unq.UID_ACTIVIDAD = '"+param.getUidActividad()+"' " +
						"AND subq_tar_gral_unq.CODTAR = 'GENERAL' " +
						"AND subq_tar_gral_unq.FECHA_INICIO <= CURDATE() " +
					"GROUP BY " +
						"subq_tar_gral_unq.UID_ACTIVIDAD, " +
						"subq_tar_gral_unq.CODART " +
				") subq_tar_vigencias on ( " +
					"subq_tar_gral.UID_ACTIVIDAD = subq_tar_vigencias.UID_ACTIVIDAD " +
					"AND subq_tar_gral.CODTAR = subq_tar_vigencias.CODTAR " +
					"AND subq_tar_gral.CODART = subq_tar_vigencias.CODART " +
					"AND subq_tar_gral.FECHA_INICIO = subq_tar_vigencias.FECHA_MAX " +
				") " +
				"WHERE " +
					"subq_tar_gral.UID_ACTIVIDAD = '"+param.getUidActividad()+"' " +
					"AND subq_tar_gral.CODTAR = 'GENERAL' " +
			") precio_tar_gral on ( " +
				"art.CODART = precio_tar_gral.CODART " +
				"AND coalesce(ean.DESGLOSE1,'*') = precio_tar_gral.DESGLOSE1 " +
				"AND coalesce(ean.DESGLOSE2,'*') = precio_tar_gral.DESGLOSE2 " +
			") " +
			"WHERE " +
				"art.UID_ACTIVIDAD = ? " +
				"AND art.CODART LIKE ? " +
				"AND UPPER (art.DESART) LIKE UPPER(?) " +
				whereActivo +
			"ORDER BY " +
				"CODART ";

        try {
            pstmt = new PreparedStatement(conn, sql);
            pstmt.setString(1, param.getUidActividad());
            pstmt.setString(2, param.getCodigo().trim() + "%");
            pstmt.setString(3, "%" + param.getDescripcion() + "%");
            if (param.getActivo() != null) {
                String activo;
                if (param.getActivo()) {
                    activo = "S";
                } else {
                    activo = "N";
                }
                pstmt.setString(4, activo);
            }
            log.debug("buscarArticulos() - " + pstmt);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                IskaypetArticuloBuscarBean articuloBuscar = new IskaypetArticuloBuscarBean();
                articuloBuscar.setCodArticulo(rs.getString("CODART"));
                articuloBuscar.setDesArticulo(rs.getString("DESART"));
                articuloBuscar.setValorDesglose1("*");
                articuloBuscar.setValorDesglose2("*");

                String codArticulo = rs.getString("CODART");
                articuloBuscar.setCodArticulo(codArticulo != null ? codArticulo : "");

                String ean = rs.getString("EAN");
                articuloBuscar.setEan(ean != null ? ean : "");

                if (StringUtils.isNotBlank(param.getCodTarifa())) {
                    articuloBuscar.setPrecio(rs.getBigDecimal("PRECIO_VENTA"));
                    articuloBuscar.setPrecioTotal(rs.getBigDecimal("PRECIO_TOTAL"));
                }

                articulos.add(articuloBuscar);
            }
            return articulos;
        } finally {
            try {
				if(rs != null)
					rs.close();

            } catch (Exception ignore) {
            }
            try {
				if (pstmt != null)
					pstmt.close();
            } catch (Exception ignore) {
            }
        }
    }

}
