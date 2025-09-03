package com.comerzzia.iskaypet.pos.persistence.articlepoints;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 * GAP46 - CANJEO ARTÍCULOS POR PUNTOS
 */
public interface ArticlePointsBeanMapper{

	long countByExample(ArticlePointsBeanExample example);

	int deleteByExample(ArticlePointsBeanExample example);

	int deleteByPrimaryKey(ArticlePointsBeanKey key);

	int insert(ArticlePointsBean record);

	int insertSelective(ArticlePointsBean record);

	List<ArticlePointsBean> selectByExampleWithRowbounds(ArticlePointsBeanExample example, RowBounds rowBounds);

	List<ArticlePointsBean> selectByExample(ArticlePointsBeanExample example);

	ArticlePointsBean selectByPrimaryKey(ArticlePointsBeanKey key);

	int updateByExampleSelective(@Param("record") ArticlePointsBean record, @Param("example") ArticlePointsBeanExample example);

	int updateByExample(@Param("record") ArticlePointsBean record, @Param("example") ArticlePointsBeanExample example);

	int updateByPrimaryKeySelective(ArticlePointsBean record);

	int updateByPrimaryKey(ArticlePointsBean record);
	
}