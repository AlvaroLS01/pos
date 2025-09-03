package com.comerzzia.iskaypet.pos.persistence.proformas;

import com.comerzzia.iskaypet.pos.persistence.proformas.ProformaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.ProformaBeanExample;
import com.comerzzia.iskaypet.pos.persistence.proformas.ProformaBeanKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ProformaBeanMapper {
    long countByExample(ProformaBeanExample example);

    int deleteByExample(ProformaBeanExample example);

    int deleteByPrimaryKey(ProformaBeanKey key);

    int insert(ProformaBean row);

    int insertSelective(ProformaBean row);

    List<ProformaBean> selectByExampleWithRowbounds(ProformaBeanExample example, RowBounds rowBounds);

    List<ProformaBean> selectByExample(ProformaBeanExample example);

    ProformaBean selectByPrimaryKey(ProformaBeanKey key);

    int updateByExampleSelective(@Param("row") ProformaBean row, @Param("example") ProformaBeanExample example);

    int updateByExample(@Param("row") ProformaBean row, @Param("example") ProformaBeanExample example);

    int updateByPrimaryKeySelective(ProformaBean row);

    int updateByPrimaryKey(ProformaBean row);
}