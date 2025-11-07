package com.comerzzia.api.loyalty.persistence.couponsTypes;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CouponTypeMapper {
    long countByExample(CouponTypeExample example);

    int deleteByExample(CouponTypeExample example);

    int deleteByPrimaryKey(CouponTypeKey key);

    int insert(CouponType record);

    int insertSelective(CouponType record);

    List<CouponType> selectByExampleWithRowbounds(CouponTypeExample example, RowBounds rowBounds);

    List<CouponType> selectByExample(CouponTypeExample example);

    CouponType selectByPrimaryKey(CouponTypeKey key);

    int updateByExampleSelective(@Param("record") CouponType record, @Param("example") CouponTypeExample example);

    int updateByExample(@Param("record") CouponType record, @Param("example") CouponTypeExample example);

    int updateByPrimaryKeySelective(CouponType record);

    int updateByPrimaryKey(CouponType record);
}