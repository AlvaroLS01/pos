package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.laws;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ContractLawMapper {

	List<ContractLaw> selectLaws(@Param("activityId") String activityId, @Param("codLanguaje") String codLanguaje);


}