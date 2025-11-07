package com.comerzzia.api.loyalty.persistence.triggers.actions;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.comerzzia.api.loyalty.persistence.triggers.actions.dto.TriggerActionDTO;

public interface TriggerActionMapper {
    long countByExample(TriggerActionExample example);

    int deleteByExample(TriggerActionExample example);

    int deleteByPrimaryKey(TriggerActionKey key);

    int insert(TriggerAction record);

    int insertSelective(TriggerAction record);

    List<TriggerAction> selectByExampleWithBLOBsWithRowbounds(TriggerActionExample example, RowBounds rowBounds);

    List<TriggerAction> selectByExampleWithBLOBs(TriggerActionExample example);

    List<TriggerAction> selectByExampleWithRowbounds(TriggerActionExample example, RowBounds rowBounds);

    List<TriggerAction> selectByExample(TriggerActionExample example);

    TriggerAction selectByPrimaryKey(TriggerActionKey key);

    int updateByExampleSelective(@Param("record") TriggerAction record, @Param("example") TriggerActionExample example);

    int updateByExampleWithBLOBs(@Param("record") TriggerAction record, @Param("example") TriggerActionExample example);

    int updateByExample(@Param("record") TriggerAction record, @Param("example") TriggerActionExample example);

    int updateByPrimaryKeySelective(TriggerAction record);

    int updateByPrimaryKeyWithBLOBs(TriggerAction record);

    int updateByPrimaryKey(TriggerAction record);
    
    List<TriggerActionDTO> selectDTOByExample(TriggerActionExample example);
    TriggerActionDTO selectDTOByPrimaryKey(TriggerActionKey key);

}