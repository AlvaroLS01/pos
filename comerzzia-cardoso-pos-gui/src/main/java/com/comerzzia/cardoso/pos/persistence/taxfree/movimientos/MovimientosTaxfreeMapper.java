package com.comerzzia.cardoso.pos.persistence.taxfree.movimientos;

import com.comerzzia.cardoso.pos.persistence.taxfree.movimientos.MovimientosTaxfree;
import com.comerzzia.cardoso.pos.persistence.taxfree.movimientos.MovimientosTaxfreeExample;
import com.comerzzia.cardoso.pos.persistence.taxfree.movimientos.MovimientosTaxfreeKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface MovimientosTaxfreeMapper {
    long countByExample(MovimientosTaxfreeExample example);

    int deleteByExample(MovimientosTaxfreeExample example);

    int deleteByPrimaryKey(MovimientosTaxfreeKey key);

    int insert(MovimientosTaxfree row);

    int insertSelective(MovimientosTaxfree row);

    List<MovimientosTaxfree> selectByExampleWithRowbounds(MovimientosTaxfreeExample example, RowBounds rowBounds);

    List<MovimientosTaxfree> selectByExample(MovimientosTaxfreeExample example);

    MovimientosTaxfree selectByPrimaryKey(MovimientosTaxfreeKey key);

    int updateByExampleSelective(@Param("row") MovimientosTaxfree row, @Param("example") MovimientosTaxfreeExample example);

    int updateByExample(@Param("row") MovimientosTaxfree row, @Param("example") MovimientosTaxfreeExample example);

    int updateByPrimaryKeySelective(MovimientosTaxfree row);

    int updateByPrimaryKey(MovimientosTaxfree row);
}