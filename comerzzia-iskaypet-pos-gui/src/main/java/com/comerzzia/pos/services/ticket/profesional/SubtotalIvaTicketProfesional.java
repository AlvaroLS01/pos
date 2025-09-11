package com.comerzzia.pos.services.ticket.profesional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@XmlAccessorType(XmlAccessType.NONE)
public class SubtotalIvaTicketProfesional extends SubtotalIvaTicket {
	@Override
	public void addLinea(LineaTicketAbstract linea){     
        base = base.add(linea.getImporteConDto());
    }
	
	@Override
	public void recalcular(){ 
        cuota = BigDecimalUtil.porcentajeR(base, porcentaje.getPorcentaje());
        cuotaRecargo = BigDecimalUtil.porcentajeR(base, porcentaje.getPorcentajeRecargo());
        base = BigDecimalUtil.redondear(base);
        total = base.add(cuota).add(cuotaRecargo);
        impuestos = BigDecimalUtil.redondear(total.subtract(base));
    }
}
