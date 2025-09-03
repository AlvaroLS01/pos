package com.comerzzia.iskaypet.pos.persistence.cajas.cierre;

import java.util.Date;

import org.apache.ibatis.annotations.Param;


public interface ConsultaCierreMapper {

	Date getFechaAperturaHoy (@Param("uidActividad") String uidActividad, @Param("codAlm") String codAlm, @Param("fecha") Date fecha);
}