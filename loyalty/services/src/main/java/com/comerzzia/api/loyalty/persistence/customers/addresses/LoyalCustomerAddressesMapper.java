package com.comerzzia.api.loyalty.persistence.customers.addresses;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LoyalCustomerAddressesMapper {
    int countByExample(DireccionFidelizadoExample example);

    int deleteByExample(DireccionFidelizadoExample example);

    int deleteByPrimaryKey(DireccionFidelizadoKey key);

    int insert(DireccionFidelizadoBean record);

    int insertSelective(DireccionFidelizadoBean record);

    List<DireccionFidelizadoBean> selectByExampleWithRowbounds(DireccionFidelizadoExample example, RowBounds rowBounds);

    List<DireccionFidelizadoBean> selectByExample(DireccionFidelizadoExample example);

    DireccionFidelizadoBean selectByPrimaryKey(DireccionFidelizadoKey key);

    int updateByExampleSelective(@Param("record") DireccionFidelizadoBean record, @Param("example") DireccionFidelizadoExample example);

    int updateByExample(@Param("record") DireccionFidelizadoBean record, @Param("example") DireccionFidelizadoExample example);

    int updateByPrimaryKeySelective(DireccionFidelizadoBean record);

    int updateByPrimaryKey(DireccionFidelizadoBean record);
    
    int updateDescripcionDireccionFidelizado(@Param("uidInstancia") String uidInstancia, @Param("idFidelizado") Long idFidelizado, @Param("descripcionDireccionOld") String descripcionDireccionOld, @Param("descripcionDireccionNew") String descripcionDireccionNew);
}