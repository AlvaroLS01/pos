package com.comerzzia.api.loyalty.persistence.couponsKeys;

import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKey;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKeyExample;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKeyKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CouponIssuanceKeyMapper {
    long countByExample(CouponIssuanceKeyExample example);

    int deleteByExample(CouponIssuanceKeyExample example);

    int deleteByPrimaryKey(CouponIssuanceKeyKey key);

    int insert(CouponIssuanceKey record);

    int insertSelective(CouponIssuanceKey record);

    List<CouponIssuanceKey> selectByExampleWithRowbounds(CouponIssuanceKeyExample example, RowBounds rowBounds);

    List<CouponIssuanceKey> selectByExample(CouponIssuanceKeyExample example);

    CouponIssuanceKey selectByPrimaryKey(CouponIssuanceKeyKey key);

    int updateByExampleSelective(@Param("record") CouponIssuanceKey record, @Param("example") CouponIssuanceKeyExample example);

    int updateByExample(@Param("record") CouponIssuanceKey record, @Param("example") CouponIssuanceKeyExample example);

    int updateByPrimaryKeySelective(CouponIssuanceKey record);

    int updateByPrimaryKey(CouponIssuanceKey record);
}