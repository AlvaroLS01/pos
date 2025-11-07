package com.comerzzia.api.loyalty.persistence.coupons.links;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CouponLinkMapper {
    long countByExample(CouponLinkExample example);

    int deleteByExample(CouponLinkExample example);

    int deleteByPrimaryKey(CouponLinkKey key);

    int insert(CouponLinkKey record);

    int insertSelective(CouponLinkKey record);

    List<CouponLinkKey> selectByExampleWithRowbounds(CouponLinkExample example, RowBounds rowBounds);

    List<CouponLinkKey> selectByExample(CouponLinkExample example);

    int updateByExampleSelective(@Param("record") CouponLinkKey record, @Param("example") CouponLinkExample example);

    int updateByExample(@Param("record") CouponLinkKey record, @Param("example") CouponLinkExample example);
}