package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.pagos;

import com.comerzzia.pos.core.dispositivos.dispositivo.visor.PagoDocumentoIVisor;
import com.comerzzia.pos.core.gui.componentes.Component;


public class DinoPagoRow extends Component{
	
	public void setPagoRow(PagoDocumentoIVisor lineaPAgo) {
		((DinoPagoRowController)getController()).setLineaPago(lineaPAgo);
	}

}
