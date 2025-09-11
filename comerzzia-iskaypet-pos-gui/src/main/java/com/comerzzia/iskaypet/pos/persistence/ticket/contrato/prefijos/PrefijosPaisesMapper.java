package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.prefijos;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface PrefijosPaisesMapper {

    List<PrefijosPaisesKey> getPrefijosPaises();
    List<PrefijosPaisesKey> getPrefijoFidelizado(@Param("uidInstancia") String uidInstancia,@Param("codPais") String codPais);

}