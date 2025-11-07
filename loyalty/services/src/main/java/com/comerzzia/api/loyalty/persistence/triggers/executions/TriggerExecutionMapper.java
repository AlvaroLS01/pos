package com.comerzzia.api.loyalty.persistence.triggers.executions;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.comerzzia.api.loyalty.persistence.triggers.dto.TriggerData;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataKey;
import com.comerzzia.api.loyalty.persistence.triggers.executions.dto.TriggerExecutionDTO;

public interface TriggerExecutionMapper {
    long countByExample(TriggerExecutionExample example);

    int deleteByExample(TriggerExecutionExample example);

    int deleteByPrimaryKey(TriggerExecutionKey key);

    int insert(TriggerExecution record);

    int insertSelective(TriggerExecution record);

    List<TriggerExecution> selectByExampleWithRowbounds(TriggerExecutionExample example, RowBounds rowBounds);

    List<TriggerExecution> selectByExample(TriggerExecutionExample example);

    TriggerExecution selectByPrimaryKey(TriggerExecutionKey key);

    int updateByExampleSelective(@Param("record") TriggerExecution record, @Param("example") TriggerExecutionExample example);

    int updateByExample(@Param("record") TriggerExecution record, @Param("example") TriggerExecutionExample example);

    int updateByPrimaryKeySelective(TriggerExecution record);

    int updateByPrimaryKey(TriggerExecution record);
    
    List<TriggerExecutionDTO> selectDTOByExample(TriggerExecutionExample example);
    TriggerExecutionDTO selectDTOByPrimaryKey(TriggerExecutionKey key);
    
    List<TriggerExecutionDataKey> executeTriggerQuery(TriggerData triggerData);
}