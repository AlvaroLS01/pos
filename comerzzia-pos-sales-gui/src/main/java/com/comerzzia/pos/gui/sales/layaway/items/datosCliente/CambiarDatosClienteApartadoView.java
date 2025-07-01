
package com.comerzzia.pos.gui.sales.layaway.items.datosCliente;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.gui.sales.retail.payments.customerdata.ChangeCustomerDataView;

@Component
public class CambiarDatosClienteApartadoView extends SceneView{

	@Override
	protected String getFXMLName() {
		return getFXMLName(ChangeCustomerDataView.class);
	}
}
