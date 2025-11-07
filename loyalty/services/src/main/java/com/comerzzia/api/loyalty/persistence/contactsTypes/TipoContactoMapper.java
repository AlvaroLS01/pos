package com.comerzzia.api.loyalty.persistence.contactsTypes;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TipoContactoMapper {
    int countByExample(TipoContactoExample example);

    int deleteByExample(TipoContactoExample example);

    int deleteByPrimaryKey(TipoContactoKey key);

    int insert(TipoContactoBean record);

    int insertSelective(TipoContactoBean record);

    List<TipoContactoBean> selectByExampleWithRowbounds(TipoContactoExample example, RowBounds rowBounds);

    List<TipoContactoBean> selectByExample(TipoContactoExample example);

    TipoContactoBean selectByPrimaryKey(TipoContactoKey key);

    int updateByExampleSelective(@Param("record") TipoContactoBean record, @Param("example") TipoContactoExample example);

    int updateByExample(@Param("record") TipoContactoBean record, @Param("example") TipoContactoExample example);

    int updateByPrimaryKeySelective(TipoContactoBean record);

    int updateByPrimaryKey(TipoContactoBean record);

    TipoContactoBean selectFromViewByPrimaryKey(TipoContactoKey key);

    List<TipoContactoBean> selectFromViewByExampleWithRowbounds(TipoContactoExample example, RowBounds rowBounds);

    List<TipoContactoBean> selectFromViewByExample(TipoContactoExample example);
}