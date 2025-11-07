package com.comerzzia.api.loyalty.persistence.customers.collectives;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LoyalCustomerCollectivesMapper {
    int countByExample(ColectivosFidelizadoExample example);

    int deleteByExample(ColectivosFidelizadoExample example);

    int deleteByPrimaryKey(ColectivosFidelizadoKey key);

    int insert(ColectivosFidelizadoBean record);

    int insertSelective(ColectivosFidelizadoBean record);

    List<ColectivosFidelizadoBean> selectByExampleWithRowbounds(ColectivosFidelizadoExample example, RowBounds rowBounds);

    List<ColectivosFidelizadoBean> selectByExample(ColectivosFidelizadoExample example);

    int updateByExampleSelective(@Param("record") ColectivosFidelizadoBean record, @Param("example") ColectivosFidelizadoExample example);

    int updateByExample(@Param("record") ColectivosFidelizadoBean record, @Param("example") ColectivosFidelizadoExample example);

    ColectivosFidelizadoBean selectByPrimaryKey(ColectivosFidelizadoKey key);
}