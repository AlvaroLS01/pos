package com.comerzzia.iskaypet.pos.services.cajas;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.persistence.movimientos.manualEyS.MovimientoEyS;
import com.comerzzia.pos.persistence.cajas.CajaBean;
import com.comerzzia.pos.services.cajas.Caja;

@XmlRootElement(name = "caja")
@XmlAccessorType(XmlAccessType.NONE)
@Primary
@Component
public class IskaypetCaja extends Caja {

	@XmlElementWrapper(name = "movimiento_manual")
	@XmlElement(name ="movimiento")
	protected List<MovimientoEyS> lstMovimientoEyS;

	public IskaypetCaja(CajaBean cajaBean) {
		super(cajaBean);
	}

	public IskaypetCaja() {
		super();
	}

	public List<MovimientoEyS> getLstMovimientoEyS() {
		return lstMovimientoEyS;
	}

	public void setLstMovimientoEyS(List<MovimientoEyS> lstMovimientoEyS) {
		this.lstMovimientoEyS = lstMovimientoEyS;
	}

}
