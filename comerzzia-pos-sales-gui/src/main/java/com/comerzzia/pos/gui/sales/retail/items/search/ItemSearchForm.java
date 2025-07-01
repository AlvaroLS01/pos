

package com.comerzzia.pos.gui.sales.retail.items.search;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
@Scope("prototype")
@NoArgsConstructor
public class ItemSearchForm extends ValidationFormGui {

    @Size(max=20, message = "La longitud del código de artículo debe ser como máximo de {max} caracteres,")
    @Getter
    protected String itemCode;

    @Size(max=100, message = "La longitud de la descripción del artículo debe ser como máximo de {max} caracteres,")
    @Getter
    protected String itemDes;

    public ItemSearchForm(String itemCode, String itemDes) {
    	setItemCode(itemCode);
    	setItemDes(itemDes);
    }

    public void setItemCode(String itemCode) {
		this.itemCode = itemCode != null ? itemCode.trim() : itemCode;
    }

    public void setItemDes(String itemDes) {
		this.itemDes = itemDes != null ? itemDes.trim() : itemDes;
    }

    @Override
    public void clearForm() {
		itemCode = "";
		itemDes = "";
    }

}
