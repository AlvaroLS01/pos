package com.comerzzia.iskaypet.pos.persistence.fidelizacion;

import org.springframework.context.annotation.Primary;

import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;

@Primary
public class IskaypetCustomerCouponDTO extends CustomerCouponDTO {

	protected String couponCodeNavision;
	protected String compraMinima;
    protected long idPromoOrigen;

	public IskaypetCustomerCouponDTO(String couponCode, boolean validationRequired) {
		super(couponCode, validationRequired);
	}

	public String getCouponCodeNavision() {
		return couponCodeNavision;
	}

	public void setCouponCodeNavision(String couponCodeNavision) {
		this.couponCodeNavision = couponCodeNavision;
	}

	public String getCompraMinima() {
		return compraMinima;
	}

	public void setCompraMinima(String compraMinima) {
		this.compraMinima = compraMinima;
	}

    public long getIdPromoOrigen() {
        return idPromoOrigen;
    }

    public void setIdPromoOrigen(long idPromoOrigen) {
        this.idPromoOrigen = idPromoOrigen;
    }
}
