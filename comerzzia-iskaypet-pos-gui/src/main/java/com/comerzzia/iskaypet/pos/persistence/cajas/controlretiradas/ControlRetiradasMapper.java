package com.comerzzia.iskaypet.pos.persistence.cajas.controlretiradas;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;


/**
 * GAP 73.2 Entradas y salidas (Ampliación)
 */
public interface ControlRetiradasMapper{

	BigDecimal consultarSaldoInicial(@Param(value = "uidActividad") String uidActividad,@Param(value = "uidDiarioCaja")String uidDiarioCaja);
	
	ControlRetiradas consultarTotalesRecuentoEfectivo(@Param(value = "uidActividad") String uidActividad,@Param(value = "uidDiarioCaja")String uidDiarioCaja, @Param(value = "codMedPago")String codMedPago);
	
	
	
}
	
