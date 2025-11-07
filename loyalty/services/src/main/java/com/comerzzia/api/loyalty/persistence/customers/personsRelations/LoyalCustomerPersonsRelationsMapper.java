package com.comerzzia.api.loyalty.persistence.customers.personsRelations;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LoyalCustomerPersonsRelationsMapper {
    int countByExample(PersonaRelacionadaExample example);

    int deleteByExample(PersonaRelacionadaExample example);

    int deleteByPrimaryKey(PersonaRelacionadaKey key);

    int insert(PersonaRelacionadaBean record);

    int insertSelective(PersonaRelacionadaBean record);

    List<PersonaRelacionadaBean> selectByExampleWithRowbounds(PersonaRelacionadaExample example, RowBounds rowBounds);

    List<PersonaRelacionadaBean> selectByExample(PersonaRelacionadaExample example);

    PersonaRelacionadaBean selectByPrimaryKey(PersonaRelacionadaKey key);

    int updateByExampleSelective(@Param("record") PersonaRelacionadaBean record, @Param("example") PersonaRelacionadaExample example);

    int updateByExample(@Param("record") PersonaRelacionadaBean record, @Param("example") PersonaRelacionadaExample example);

    int updateByPrimaryKeySelective(PersonaRelacionadaBean record);

    int updateByPrimaryKey(PersonaRelacionadaBean record);
}