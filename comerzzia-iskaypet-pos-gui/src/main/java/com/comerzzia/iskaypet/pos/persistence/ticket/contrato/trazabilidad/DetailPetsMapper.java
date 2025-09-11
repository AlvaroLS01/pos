package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad;

import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.DetailPets;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.DetailPetsExample;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.DetailPetsKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DetailPetsMapper {
    long countByExample(DetailPetsExample example);

    int deleteByExample(DetailPetsExample example);

    int deleteByPrimaryKey(DetailPetsKey key);

    int insert(DetailPets record);

    int insertSelective(DetailPets record);

    List<DetailPets> selectByExample(DetailPetsExample example);

    DetailPets selectByPrimaryKey(DetailPetsKey key);

    int updateByExampleSelective(@Param("record") DetailPets record, @Param("example") DetailPetsExample example);

    int updateByExample(@Param("record") DetailPets record, @Param("example") DetailPetsExample example);

    int updateByPrimaryKeySelective(DetailPets record);

    int updateByPrimaryKey(DetailPets record);
}