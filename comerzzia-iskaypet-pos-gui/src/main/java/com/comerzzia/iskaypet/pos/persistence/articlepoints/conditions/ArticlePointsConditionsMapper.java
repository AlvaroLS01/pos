package com.comerzzia.iskaypet.pos.persistence.articlepoints.conditions;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 * GAP46 - CANJEO ARTÍCULOS POR PUNTOS
 */
public interface ArticlePointsConditionsMapper{

	long countByExample(ArticlePointsConditionsExample example);

	int deleteByExample(ArticlePointsConditionsExample example);

	int deleteByPrimaryKey(ArticlePointsConditionsKey key);

	int insert(ArticlePointsConditions record);

	int insertSelective(ArticlePointsConditions record);

	List<ArticlePointsConditions> selectByExampleWithRowbounds(ArticlePointsConditionsExample example, RowBounds rowBounds);

	List<ArticlePointsConditions> selectByExample(ArticlePointsConditionsExample example);

	ArticlePointsConditions selectByPrimaryKey(ArticlePointsConditionsKey key);

	int updateByExampleSelective(@Param("record") ArticlePointsConditions record, @Param("example") ArticlePointsConditionsExample example);

	int updateByExample(@Param("record") ArticlePointsConditions record, @Param("example") ArticlePointsConditionsExample example);

	int updateByPrimaryKeySelective(ArticlePointsConditions record);

	int updateByPrimaryKey(ArticlePointsConditions record);
	
}