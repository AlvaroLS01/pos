package com.comerzzia.dinosol.pos.services.ticket.promociones;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "promocion")
@Component
@Scope("prototype")
@Primary
public class DinoPromocionTicket extends PromocionTicket {
    
    public DinoPromocionTicket() {
    }
    
    public DinoPromocionTicket(Promocion promocion) {
        idPromocion = promocion.getIdPromocion();
        importeTotalAhorro = BigDecimal.ZERO;
        idTipoPromocion = promocion.getIdTipoPromocion();
        tipoDescuento = promocion.getTipoDto();
        descripcionPromocion = promocion.getDescripcion();
        this.promocion = promocion;
       
        textoPromocion = "";
        puntos = 0;
        exclusiva = promocion.getExclusiva();
    }
	
	public String getDescripcionPromocion() {
		return descripcionPromocion;
	}

}
