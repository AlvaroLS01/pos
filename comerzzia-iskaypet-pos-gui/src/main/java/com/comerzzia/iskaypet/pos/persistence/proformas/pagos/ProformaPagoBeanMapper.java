package com.comerzzia.iskaypet.pos.persistence.proformas.pagos;

import com.comerzzia.iskaypet.pos.persistence.proformas.pagos.ProformaPagoBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.pagos.ProformaPagoBeanExample;
import com.comerzzia.iskaypet.pos.persistence.proformas.pagos.ProformaPagoBeanKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ProformaPagoBeanMapper {
    long countByExample(ProformaPagoBeanExample example);

    int deleteByExample(ProformaPagoBeanExample example);

    int deleteByPrimaryKey(ProformaPagoBeanKey key);

    int insert(ProformaPagoBean row);

    int insertSelective(ProformaPagoBean row);

    List<ProformaPagoBean> selectByExampleWithRowbounds(ProformaPagoBeanExample example, RowBounds rowBounds);

    List<ProformaPagoBean> selectByExample(ProformaPagoBeanExample example);

    ProformaPagoBean selectByPrimaryKey(ProformaPagoBeanKey key);

    int updateByExampleSelective(@Param("row") ProformaPagoBean row, @Param("example") ProformaPagoBeanExample example);

    int updateByExample(@Param("row") ProformaPagoBean row, @Param("example") ProformaPagoBeanExample example);

    int updateByPrimaryKeySelective(ProformaPagoBean row);

    int updateByPrimaryKey(ProformaPagoBean row);
}