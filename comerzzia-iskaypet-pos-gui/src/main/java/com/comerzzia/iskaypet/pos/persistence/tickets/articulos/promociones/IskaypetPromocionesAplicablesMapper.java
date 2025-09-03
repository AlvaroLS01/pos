package com.comerzzia.iskaypet.pos.persistence.tickets.articulos.promociones;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface IskaypetPromocionesAplicablesMapper {
    long countByExample(IskaypetPromocionesAplicablesExample example);

    int deleteByExample(IskaypetPromocionesAplicablesExample example);

    int deleteByPrimaryKey(IskaypetPromocionesAplicablesKey key);

    int insert(IskaypetPromocionesAplicables row);

    int insertSelective(IskaypetPromocionesAplicables row);

    List<IskaypetPromocionesAplicables> selectByExampleWithBLOBsWithRowbounds(IskaypetPromocionesAplicablesExample example, RowBounds rowBounds);

    List<IskaypetPromocionesAplicables> selectByExampleWithBLOBs(IskaypetPromocionesAplicablesExample example);

    List<IskaypetPromocionesAplicables> selectByExampleWithRowbounds(IskaypetPromocionesAplicablesExample example, RowBounds rowBounds);

    List<IskaypetPromocionesAplicables> selectByExample(IskaypetPromocionesAplicablesExample example);

    IskaypetPromocionesAplicables selectByPrimaryKey(IskaypetPromocionesAplicablesKey key);

    int updateByExampleSelective(@Param("row") IskaypetPromocionesAplicables row, @Param("example") IskaypetPromocionesAplicablesExample example);

    int updateByExampleWithBLOBs(@Param("row") IskaypetPromocionesAplicables row, @Param("example") IskaypetPromocionesAplicablesExample example);

    int updateByExample(@Param("row") IskaypetPromocionesAplicables row, @Param("example") IskaypetPromocionesAplicablesExample example);

    int updateByPrimaryKeySelective(IskaypetPromocionesAplicables row);

    int updateByPrimaryKeyWithBLOBs(IskaypetPromocionesAplicables row);

    int updateByPrimaryKey(IskaypetPromocionesAplicables row);
}