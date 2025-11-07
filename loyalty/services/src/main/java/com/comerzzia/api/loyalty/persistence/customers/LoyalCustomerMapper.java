package com.comerzzia.api.loyalty.persistence.customers;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LoyalCustomerMapper {
    int countByExample(FidelizadoExample example);

    int deleteByExample(FidelizadoExample example);

    int deleteByPrimaryKey(FidelizadoKey key);

    int insert(FidelizadoBean record);

    int insertSelective(FidelizadoBean record);

    List<FidelizadoBean> selectByExampleWithRowbounds(FidelizadoExample example, RowBounds rowBounds);

    List<FidelizadoBean> selectByExample(FidelizadoExample example);

    FidelizadoBean selectByPrimaryKey(FidelizadoKey key);

    int updateByExampleSelective(@Param("record") FidelizadoBean record, @Param("example") FidelizadoExample example);

    int updateByExample(@Param("record") FidelizadoBean record, @Param("example") FidelizadoExample example);

    int updateByPrimaryKeySelective(FidelizadoBean record);

    int updateByPrimaryKey(FidelizadoBean record);

    FidelizadoBean selectFromViewByPrimaryKey(FidelizadoKey key);

    List<FidelizadoBean> selectFromViewByExampleWithRowbounds(FidelizadoExample example, RowBounds rowBounds);

    List<FidelizadoBean> selectFromViewByExample(FidelizadoExample example);
    
    List<FidelizadoBean> selectFromViewByExampleExportacion(@Param("example") FidelizadoExample example, @Param("paramExportarFidelizados") ParametrosExportarFidelizadosBean paramExportarFidelizados);
    
    List<FidelizadoBean> selectFromViewByExampleTarjeta(FidelizadoExample example);
    
    List<FidelizadoBean> selectFidelizadosFechaCumpleanos(@Param("uidInstancia") String uidInstancia, @Param("mesCumpleDesde") Integer mesCumpleDesde, @Param("diaCumpleDesde") Integer diaCumpleDesde, @Param("mesCumpleHasta") Integer mesCumpleHasta, @Param("diaCumpleHasta") Integer diaCumpleHasta);

    List<FidelizadoBean> selectFidelizadosPorEtiquetas(@Param("uidInstancia") String uidInstancia, @Param("uidActividad") String uidActividad, @Param("etiquetasSeleccionadas") List<String> etiquetasSeleccionadas, @Param("operador") String operador);
    
    Integer getNumeroCuponesEmitidosPorFidelizado(@Param("uidInstancia") String uidInstancia, @Param("uidActividad") String uidActividad, @Param("fechaDesde") Date fechaDesde, @Param("fechaHasta") Date fechaHasta, @Param("idFidelizado") Long idFidelizado);
    
    Integer getNumeroCuponesCanjeadosPorFidelizado(@Param("uidInstancia") String uidInstancia, @Param("uidActividad") String uidActividad, @Param("fechaDesde") Date fechaDesde, @Param("fechaHasta") Date fechaHasta, @Param("idFidelizado") Long idFidelizado);

	List<FidelizadoBean> selectByConsulta(@Param("consultaSQL") String consultaSQL);

}