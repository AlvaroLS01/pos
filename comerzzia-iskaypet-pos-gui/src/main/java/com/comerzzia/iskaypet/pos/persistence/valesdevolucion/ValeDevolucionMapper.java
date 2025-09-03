package com.comerzzia.iskaypet.pos.persistence.valesdevolucion;

import com.comerzzia.iskaypet.pos.persistence.valesdevolucion.ValeDevolucion;
import com.comerzzia.iskaypet.pos.persistence.valesdevolucion.ValeDevolucionExample;
import com.comerzzia.iskaypet.pos.persistence.valesdevolucion.ValeDevolucionKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface ValeDevolucionMapper {
    long countByExample(ValeDevolucionExample example);

    int deleteByExample(ValeDevolucionExample example);

    int deleteByPrimaryKey(ValeDevolucionKey key);

    int insert(ValeDevolucion record);

    int insertSelective(ValeDevolucion record);

    List<ValeDevolucion> selectByExampleWithRowbounds(ValeDevolucionExample example, RowBounds rowBounds);

    List<ValeDevolucion> selectByExample(ValeDevolucionExample example);

    ValeDevolucion selectByPrimaryKey(ValeDevolucionKey key);

    int updateByExampleSelective(@Param("record") ValeDevolucion record, @Param("example") ValeDevolucionExample example);

    int updateByExample(@Param("record") ValeDevolucion record, @Param("example") ValeDevolucionExample example);

    int updateByPrimaryKeySelective(ValeDevolucion record);

    int updateByPrimaryKey(ValeDevolucion record);
}