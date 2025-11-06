package com.comerzzia.dinosol.pos.persistence.articulos.propiedades;

import com.comerzzia.dinosol.pos.persistence.articulos.propiedades.PropiedadArticulo;
import com.comerzzia.dinosol.pos.persistence.articulos.propiedades.PropiedadArticuloExample;
import com.comerzzia.dinosol.pos.persistence.articulos.propiedades.PropiedadArticuloKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface PropiedadArticuloMapper {
    long countByExample(PropiedadArticuloExample example);

    int deleteByExample(PropiedadArticuloExample example);

    int deleteByPrimaryKey(PropiedadArticuloKey key);

    int insert(PropiedadArticulo record);

    int insertSelective(PropiedadArticulo record);

    List<PropiedadArticulo> selectByExampleWithRowbounds(PropiedadArticuloExample example, RowBounds rowBounds);

    List<PropiedadArticulo> selectByExample(PropiedadArticuloExample example);

    PropiedadArticulo selectByPrimaryKey(PropiedadArticuloKey key);

    int updateByExampleSelective(@Param("record") PropiedadArticulo record, @Param("example") PropiedadArticuloExample example);

    int updateByExample(@Param("record") PropiedadArticulo record, @Param("example") PropiedadArticuloExample example);

    int updateByPrimaryKeySelective(PropiedadArticulo record);

    int updateByPrimaryKey(PropiedadArticulo record);
}