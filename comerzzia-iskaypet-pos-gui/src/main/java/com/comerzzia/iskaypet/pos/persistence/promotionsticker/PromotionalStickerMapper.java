package com.comerzzia.iskaypet.pos.persistence.promotionsticker;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 * GAP62 - PEGATINAS PROMOCIONALES
 */
public interface PromotionalStickerMapper{

	long countByExample(PromotionalStickerExample example);

	int deleteByExample(PromotionalStickerExample example);

	int deleteByPrimaryKey(PromotionalStickerKey key);

	int insert(PromotionalSticker record);

	int insertSelective(PromotionalSticker record);

	List<PromotionalSticker> selectByExampleWithRowbounds(PromotionalStickerExample example, RowBounds rowBounds);

	List<PromotionalSticker> selectByExample(PromotionalStickerExample example);

	PromotionalSticker selectByPrimaryKey(PromotionalStickerKey key);

	int updateByExampleSelective(@Param("record") PromotionalSticker record, @Param("example") PromotionalStickerExample example);

	int updateByExample(@Param("record") PromotionalSticker record, @Param("example") PromotionalStickerExample example);

	int updateByPrimaryKeySelective(PromotionalSticker record);

	int updateByPrimaryKey(PromotionalSticker record);
	
}