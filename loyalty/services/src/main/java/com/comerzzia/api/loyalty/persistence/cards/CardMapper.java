package com.comerzzia.api.loyalty.persistence.cards;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CardMapper {
    int countByExample(CardExample example);

    int deleteByExample(CardExample example);

    int deleteByPrimaryKey(CardKey key);

    int insert(Card record);

    int insertSelective(Card record);

    List<CardDTO> selectByExampleWithRowbounds(CardExample example, RowBounds rowBounds);

    List<Card> selectByExample(CardExample example);
    
    List<CardDTO> selectDTOByExample(CardExample example);

    Card selectByPrimaryKey(CardKey key);
    
    CardDTO selectDTOByPrimaryKey(CardKey key);

    int updateByExampleSelective(@Param("record") Card record, @Param("example") CardExample example);

    int updateByExample(@Param("record") Card record, @Param("example") CardExample example);

    int updateByPrimaryKeySelective(Card record);

    int updateByPrimaryKey(Card record);
    
    Card selectByUniqueKey(CardUK key);
    
    CardDTO selectDTOByUniqueKey(CardUK key);
}