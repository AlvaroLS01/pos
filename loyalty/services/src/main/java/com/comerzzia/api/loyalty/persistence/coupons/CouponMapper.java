package com.comerzzia.api.loyalty.persistence.coupons;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CouponMapper {
    long countByExample(CouponExample example);

    int deleteByExample(CouponExample example);

    int deleteByPrimaryKey(CouponKey key);

    int insert(Coupon record);

    int insertSelective(Coupon record);

    List<Coupon> selectByExampleWithRowbounds(CouponExample example, RowBounds rowBounds);

    List<Coupon> selectByExample(CouponExample example);

    Coupon selectByPrimaryKey(CouponKey key);

    int updateByExampleSelective(@Param("record") Coupon record, @Param("example") CouponExample example);

    int updateByExample(@Param("record") Coupon record, @Param("example") CouponExample example);

    int updateByPrimaryKeySelective(Coupon record);

    int updateByPrimaryKey(Coupon record);
    
    int updateActive(Coupon record);
    
    Coupon selectByUk(CouponUk key);
}