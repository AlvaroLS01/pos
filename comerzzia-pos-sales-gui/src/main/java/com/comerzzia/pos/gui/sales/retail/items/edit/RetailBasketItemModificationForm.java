

package com.comerzzia.pos.gui.sales.retail.items.edit;

import java.math.BigDecimal;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.basket.header.BasketCashier;
import com.comerzzia.pos.core.gui.validation.ValidationFormGui;
import com.comerzzia.pos.core.gui.validation.validators.number.IsNumeric;
import com.comerzzia.pos.util.format.FormatUtils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

@Component
@Scope("prototype")
@SuppressWarnings("rawtypes")
public class RetailBasketItemModificationForm extends ValidationFormGui {

	@Size(min = 1, max = 60, message = "La longitud del campo debe estar entre {min} y {max} caracteres.")  
	protected String itemDescription;
	
    @NotEmpty(message = "La cantidad no puede ser nula")
    @IsNumeric(decimals = 3,message = "El campo cantidad no es un número válido.")
    protected String quantity;
    
    @NotEmpty(message = "El precio no puede ser nulo")
    @IsNumeric(isAmount = true, message = "El campo precio no es un número válido.")
    protected String finalPrice;
    
    @NotEmpty(message = "El precio no puede ser nulo")
	@IsNumeric(decimals = 4, message = "El campo precio no es un número válido.")
	protected String finalWholesalePrice;

    @IsNumeric( decimals = 2, message = "El campo descuento no es un número válido.")
    protected String discount;
    
    protected BasketCashier cashier;
    
  
	// Los campos anteriores no necesarios para validación. En el caso de este formulario necesitamos saber si algunas propiedades han cambiado
    protected SimpleStringProperty unitPriceSP;
    protected SimpleStringProperty discountSP;
    protected SimpleStringProperty finalPriceSP;    
	protected SimpleObjectProperty cashierSP;
    protected SimpleStringProperty itemSP;

    public RetailBasketItemModificationForm() {
        discountSP = new SimpleStringProperty();
        unitPriceSP = new SimpleStringProperty();
        finalPriceSP = new SimpleStringProperty();
        cashierSP = new SimpleObjectProperty();
        itemSP = new SimpleStringProperty();
    }
    
    public String getQuantity() {
        return quantity;
    }

    public void setFinalPriceSP(SimpleStringProperty precioFinalSP) {
        this.finalPriceSP = precioFinalSP;
    }

    public void setQuantity(String cantidad) {
        this.quantity = cantidad;
    }

    public String getDiscount() {
        return discount;
    }
    
    public void setDiscount(String descuento) {
    	this.discount = descuento;
    	discountSP.setValue(descuento);
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String precioFinal) {
        this.finalPrice = precioFinal;
        finalPriceSP.setValue(precioFinal);
    }

    @Override
    public void clearForm() {
        quantity = "";
        discount = "";
        finalPrice = "";
        finalWholesalePrice = "";
    }

    public BigDecimal getFinalPriceAsBD() {
        return FormatUtils.getInstance().parseBigDecimal(finalPrice);
    }
    
    public BigDecimal getQuantityAsBD() {
        return FormatUtils.getInstance().parseBigDecimal(quantity,3);
    }
    
    public BigDecimal getDiscountAsBD() {
        return FormatUtils.getInstance().parseBigDecimal(discount);
    }
    
    // Getters y Setters de propiedades observables para actualizar el formulario
    // Las usaremos desde los setters de los parámetros no observables.
    public SimpleStringProperty getDiscountSP() {
    	return discountSP;
    }
    
    public void setDiscountSP(SimpleStringProperty descuentoSP) {
    	this.discountSP = descuentoSP;
    }
    
    public SimpleStringProperty getFinalPriceSP() {
        return finalPriceSP;
    }
    
    /**
     * @return the vendedor
     */
    public BasketCashier getCashier() {
        return cashier;
    }

    /**
     * @param vendedor the vendedor to set
     */    
	@SuppressWarnings("unchecked")
	public void setCashier(BasketCashier vendedor) {
        this.cashier = vendedor;        
        cashierSP.setValue(vendedor);
    }
    
    public String getItemDescription() {
    	return itemDescription;
    }
    
    public void setItemDescription(String desArticulo) {
        this.itemDescription = desArticulo!=null?desArticulo.trim():desArticulo;
        itemSP.setValue(desArticulo!=null?desArticulo.trim():desArticulo);
    }
    
    public String getFinalWholesalePrice() {
        return finalWholesalePrice;
    }

    public void setFinalWholesalePrice(String precioFinalProfesional) {
        this.finalWholesalePrice = precioFinalProfesional;
        finalPriceSP.setValue(precioFinalProfesional);
    }
}
