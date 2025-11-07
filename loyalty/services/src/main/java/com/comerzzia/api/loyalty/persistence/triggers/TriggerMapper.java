package com.comerzzia.api.loyalty.persistence.triggers;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.comerzzia.api.loyalty.persistence.triggers.dto.TriggerDTO;

public interface TriggerMapper {
    long countByExample(TriggerExample example);

    int deleteByExample(TriggerExample example);

    int deleteByPrimaryKey(TriggerKey key);

    int insert(Trigger record);

    int insertSelective(Trigger record);

    List<Trigger> selectByExampleWithBLOBsWithRowbounds(TriggerExample example, RowBounds rowBounds);

    List<Trigger> selectByExampleWithBLOBs(TriggerExample example);

    List<Trigger> selectByExampleWithRowbounds(TriggerExample example, RowBounds rowBounds);

    List<Trigger> selectByExample(TriggerExample example);

    Trigger selectByPrimaryKey(TriggerKey key);

    int updateByExampleSelective(@Param("record") Trigger record, @Param("example") TriggerExample example);

    int updateByExampleWithBLOBs(@Param("record") Trigger record, @Param("example") TriggerExample example);

    int updateByExample(@Param("record") Trigger record, @Param("example") TriggerExample example);

    int updateByPrimaryKeySelective(Trigger record);

    int updateByPrimaryKeyWithBLOBs(Trigger record);

    int updateByPrimaryKey(Trigger record);
    
    List<TriggerDTO> selectDTOByExample(TriggerExample example);
    TriggerDTO selectDTOByPrimaryKey(TriggerKey key);
}