package com.comerzzia.api.loyalty.persistence.accounts.activities;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AccountActivityMapper {
    int countByExample(AccountActivityExample example);

    int deleteByExample(AccountActivityExample example);

    int deleteByPrimaryKey(AccountActivityKey key);

    int insert(AccountActivity record);

    int insertSelective(AccountActivity record);

    List<AccountActivity> selectByExampleWithRowbounds(AccountActivityExample example, RowBounds rowBounds);

    List<AccountActivity> selectByExample(AccountActivityExample example);

    AccountActivity selectByPrimaryKey(AccountActivityKey key);

    int updateByExampleSelective(@Param("record") AccountActivity record, @Param("example") AccountActivityExample example);

    int updateByExample(@Param("record") AccountActivity record, @Param("example") AccountActivityExample example);

    int updateByPrimaryKeySelective(AccountActivity record);

    int updateByPrimaryKey(AccountActivity record);
    
    List<AccountActivity> selectFromViewPaginacion(@Param("example") AccountActivityExample example, @Param("uidActividad") String uidActividad);
    
    Double consultarTotalSalida(@Param("example") AccountActivityExample example);
    
    Double consultarTotalEntrada(@Param("example") AccountActivityExample example);
    
    AccountActivity consultarTransaccion(@Param("example") AccountActivityExample example);
    
    AccountActivity selectByUniqueKey(AccountActivityUK key);
    
}