


package com.comerzzia.pos.gui.sales.retail.items;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;
import com.comerzzia.pos.util.format.FormatUtils;

@Component
@Scope("prototype")
public class ItemLineValidationForm extends ValidationFormGui{
    
    protected String codArticulo;
    
    protected String cantidad;

    public ItemLineValidationForm() {
    	
	}

	public String getCodArticulo() {
        return codArticulo;
    }

    public void setCodArticulo(String codArticulo) {
        this.codArticulo = codArticulo;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getCantidadAsBigDecimal() {
        return FormatUtils.getInstance().parseBigDecimal(cantidad,3);
    }

    @Override
    public void clearForm() {
        codArticulo = "";
        cantidad = "";
    }
    
}
