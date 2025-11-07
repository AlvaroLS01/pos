package com.comerzzia.api.loyalty.persistence.civilStates;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface EstadoCivilMapper {
    int countByExample(EstadoCivilExample example);

    int deleteByExample(EstadoCivilExample example);

    int deleteByPrimaryKey(EstadoCivilKey key);

    int insert(EstadoCivilBean record);

    int insertSelective(EstadoCivilBean record);

    List<EstadoCivilBean> selectByExampleWithRowbounds(EstadoCivilExample example, RowBounds rowBounds);

    List<EstadoCivilBean> selectByExample(EstadoCivilExample example);

    EstadoCivilBean selectByPrimaryKey(EstadoCivilKey key);

    int updateByExampleSelective(@Param("record") EstadoCivilBean record, @Param("example") EstadoCivilExample example);

    int updateByExample(@Param("record") EstadoCivilBean record, @Param("example") EstadoCivilExample example);

    int updateByPrimaryKeySelective(EstadoCivilBean record);

    int updateByPrimaryKey(EstadoCivilBean record);
}