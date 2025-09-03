package com.comerzzia.iskaypet.pos.services.ticket.cupones;

import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement(name = "cupon")
@XmlAccessorType(XmlAccessType.FIELD)
public class IskaypetCuponEmitidoTicket extends CuponEmitidoTicket {

    public IskaypetCuponEmitidoTicket() {
        super();
    }

    private String codCuponOrigen;

    public String getCodCuponOrigen() {
        return codCuponOrigen;
    }

    public void setCodCuponOrigen(String cuponOrigen) {
        this.codCuponOrigen = cuponOrigen;
    }

}
