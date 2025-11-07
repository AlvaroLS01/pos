package com.comerzzia.api.loyalty.persistence.coupons.uses;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CouponUseMapper {
    long countByExample(CouponUseExample example);

    int deleteByExample(CouponUseExample example);

    int deleteByPrimaryKey(CouponUseKey key);

    int insert(CouponUse record);

    int insertSelective(CouponUse record);

    List<CouponUse> selectByExampleWithRowbounds(CouponUseExample example, RowBounds rowBounds);

    List<CouponUse> selectByExample(CouponUseExample example);

    CouponUse selectByPrimaryKey(CouponUseKey key);

    int updateByExampleSelective(@Param("record") CouponUse record, @Param("example") CouponUseExample example);

    int updateByExample(@Param("record") CouponUse record, @Param("example") CouponUseExample example);

    int updateByPrimaryKeySelective(CouponUse record);

    int updateByPrimaryKey(CouponUse record);
    
    int updateUsesSelective(@Param("currentRecord") CouponUse currentRecord, @Param("newRecord") CouponUse newRecord);
    
    int addUse(@Param("key") CouponUseKey key, 
               @Param("discount") BigDecimal discount, 
               @Param("sale") BigDecimal sale,
               @Param("lastUse") Date lastUse, 
               @Param("lastTerminalId") String lastTerminalId); 
}