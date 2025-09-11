package com.comerzzia.iskaypet.pos.persistence.colectivos;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LocalColectivosMapper {
    int countByExample(LocalColectivosExample example);

    int deleteByExample(LocalColectivosExample example);

    int deleteByPrimaryKey(LocalColectivosKey key);

    int insert(LocalColectivos record);

    int insertSelective(LocalColectivos record);

    List<LocalColectivos> selectByExampleWithRowbounds(LocalColectivosExample example, RowBounds rowBounds);

    List<LocalColectivos> selectByExample(LocalColectivosExample example);

    LocalColectivos selectByPrimaryKey(LocalColectivosKey key);

    int updateByExampleSelective(@Param("record") LocalColectivos record, @Param("example") LocalColectivosExample example);

    int updateByExample(@Param("record") LocalColectivos record, @Param("example") LocalColectivosExample example);

    int updateByPrimaryKeySelective(LocalColectivos record);

    int updateByPrimaryKey(LocalColectivos record);
}