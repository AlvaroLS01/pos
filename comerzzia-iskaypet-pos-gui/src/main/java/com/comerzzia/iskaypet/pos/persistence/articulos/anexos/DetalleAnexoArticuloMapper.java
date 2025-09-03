package com.comerzzia.iskaypet.pos.persistence.articulos.anexos;

import com.comerzzia.iskaypet.pos.persistence.articulos.anexos.DetalleAnexoArticulo;
import com.comerzzia.iskaypet.pos.persistence.articulos.anexos.DetalleAnexoArticuloExample;
import com.comerzzia.iskaypet.pos.persistence.articulos.anexos.DetalleAnexoArticuloKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DetalleAnexoArticuloMapper {
    long countByExample(DetalleAnexoArticuloExample example);

    int deleteByExample(DetalleAnexoArticuloExample example);

    int deleteByPrimaryKey(DetalleAnexoArticuloKey key);

    int insert(DetalleAnexoArticulo record);

    int insertSelective(DetalleAnexoArticulo record);

    List<DetalleAnexoArticulo> selectByExampleWithRowbounds(DetalleAnexoArticuloExample example, RowBounds rowBounds);

    List<DetalleAnexoArticulo> selectByExample(DetalleAnexoArticuloExample example);

    DetalleAnexoArticulo selectByPrimaryKey(DetalleAnexoArticuloKey key);

    int updateByExampleSelective(@Param("record") DetalleAnexoArticulo record, @Param("example") DetalleAnexoArticuloExample example);

    int updateByExample(@Param("record") DetalleAnexoArticulo record, @Param("example") DetalleAnexoArticuloExample example);

    int updateByPrimaryKeySelective(DetalleAnexoArticulo record);

    int updateByPrimaryKey(DetalleAnexoArticulo record);
}