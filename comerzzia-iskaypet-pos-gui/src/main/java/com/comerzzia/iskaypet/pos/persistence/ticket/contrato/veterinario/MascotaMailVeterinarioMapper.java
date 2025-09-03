package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.veterinario;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface MascotaMailVeterinarioMapper {
    long countByExample(MascotaMailVeterinarioExample example);

    int deleteByExample(MascotaMailVeterinarioExample example);

    int deleteByPrimaryKey(MascotaMailVeterinarioKey key);

    int insert(MascotaMailVeterinario record);

    int insertSelective(MascotaMailVeterinario record);

    List<MascotaMailVeterinario> selectByExample(MascotaMailVeterinarioExample example);

    MascotaMailVeterinario selectByPrimaryKey(MascotaMailVeterinarioKey key);

    int updateByExampleSelective(@Param("record") MascotaMailVeterinario record, @Param("example") MascotaMailVeterinarioExample example);

    int updateByExample(@Param("record") MascotaMailVeterinario record, @Param("example") MascotaMailVeterinarioExample example);

    int updateByPrimaryKeySelective(MascotaMailVeterinario record);

    int updateByPrimaryKey(MascotaMailVeterinario record);
}