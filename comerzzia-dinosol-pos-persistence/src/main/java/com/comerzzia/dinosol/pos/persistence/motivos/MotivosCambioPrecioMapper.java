package com.comerzzia.dinosol.pos.persistence.motivos;

import com.comerzzia.dinosol.pos.persistence.motivos.MotivosCambioPrecio;
import com.comerzzia.dinosol.pos.persistence.motivos.MotivosCambioPrecioExample;
import com.comerzzia.dinosol.pos.persistence.motivos.MotivosCambioPrecioKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface MotivosCambioPrecioMapper {
    long countByExample(MotivosCambioPrecioExample example);

    int deleteByExample(MotivosCambioPrecioExample example);

    int deleteByPrimaryKey(MotivosCambioPrecioKey key);

    int insert(MotivosCambioPrecio record);

    int insertSelective(MotivosCambioPrecio record);

    List<MotivosCambioPrecio> selectByExampleWithRowbounds(MotivosCambioPrecioExample example, RowBounds rowBounds);

    List<MotivosCambioPrecio> selectByExample(MotivosCambioPrecioExample example);

    MotivosCambioPrecio selectByPrimaryKey(MotivosCambioPrecioKey key);

    int updateByExampleSelective(@Param("record") MotivosCambioPrecio record, @Param("example") MotivosCambioPrecioExample example);

    int updateByExample(@Param("record") MotivosCambioPrecio record, @Param("example") MotivosCambioPrecioExample example);

    int updateByPrimaryKeySelective(MotivosCambioPrecio record);

    int updateByPrimaryKey(MotivosCambioPrecio record);
}