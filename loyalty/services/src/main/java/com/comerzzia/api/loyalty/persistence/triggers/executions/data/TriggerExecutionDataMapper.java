package com.comerzzia.api.loyalty.persistence.triggers.executions.data;

import com.comerzzia.api.loyalty.persistence.triggers.executions.data.dto.TriggerExecutionDataDTO;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TriggerExecutionDataMapper {
    long countByExample(TriggerExecutionDataExample example);

    int deleteByExample(TriggerExecutionDataExample example);

    int deleteByPrimaryKey(TriggerExecutionDataKey key);

    int insert(TriggerExecutionDataKey record);

    int insertSelective(TriggerExecutionDataKey record);

    List<TriggerExecutionDataKey> selectByExampleWithRowbounds(TriggerExecutionDataExample example, RowBounds rowBounds);

    List<TriggerExecutionDataKey> selectByExample(TriggerExecutionDataExample example);

    int updateByExampleSelective(@Param("record") TriggerExecutionDataKey record, @Param("example") TriggerExecutionDataExample example);

    int updateByExample(@Param("record") TriggerExecutionDataKey record, @Param("example") TriggerExecutionDataExample example);
    
    List<TriggerExecutionDataDTO> selectDTOByExample(TriggerExecutionDataExample example);
}