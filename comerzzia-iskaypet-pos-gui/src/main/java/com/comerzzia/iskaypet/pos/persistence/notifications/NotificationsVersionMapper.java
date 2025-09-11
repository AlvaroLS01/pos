package com.comerzzia.iskaypet.pos.persistence.notifications;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface NotificationsVersionMapper {
    long countByExample(NotificationsVersionExample example);

    int deleteByExample(NotificationsVersionExample example);

    int deleteByPrimaryKey(NotificationsVersionKey key);

    int insert(NotificationsVersion record);

    int insertSelective(NotificationsVersion record);

    List<NotificationsVersion> selectByExampleWithRowbounds(NotificationsVersionExample example, RowBounds rowBounds);

    List<NotificationsVersion> selectByExample(NotificationsVersionExample example);

    NotificationsVersion selectByPrimaryKey(NotificationsVersionKey key);

    int updateByExampleSelective(@Param("record") NotificationsVersion record, @Param("example") NotificationsVersionExample example);

    int updateByExample(@Param("record") NotificationsVersion record, @Param("example") NotificationsVersionExample example);

    int updateByPrimaryKeySelective(NotificationsVersion record);

    int updateByPrimaryKey(NotificationsVersion record);
    
    List<NotificationsVersion> selectDistinctMsg(@Param("activityUid") String activityUid);
    
    List<NotificationsVersion> selectMsgDistinct(@Param("activityUid") String activityUid);
    
    
}