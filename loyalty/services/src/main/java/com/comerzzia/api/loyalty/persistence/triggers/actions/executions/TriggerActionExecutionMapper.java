package com.comerzzia.api.loyalty.persistence.triggers.actions.executions;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TriggerActionExecutionMapper {
    long countByExample(TriggerActionExecutionExample example);

    int deleteByExample(TriggerActionExecutionExample example);

    int deleteByPrimaryKey(TriggerActionExecutionKey key);

    int insert(TriggerActionExecution record);

    int insertSelective(TriggerActionExecution record);

    List<TriggerActionExecution> selectByExampleWithRowbounds(TriggerActionExecutionExample example, RowBounds rowBounds);

    List<TriggerActionExecution> selectByExample(TriggerActionExecutionExample example);

    TriggerActionExecution selectByPrimaryKey(TriggerActionExecutionKey key);

    int updateByExampleSelective(@Param("record") TriggerActionExecution record, @Param("example") TriggerActionExecutionExample example);

    int updateByExample(@Param("record") TriggerActionExecution record, @Param("example") TriggerActionExecutionExample example);

    int updateByPrimaryKeySelective(TriggerActionExecution record);

    int updateByPrimaryKey(TriggerActionExecution record);
}