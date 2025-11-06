package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.ventas;

import com.comerzzia.pos.core.dispositivos.dispositivo.visor.LineaDocumentoIVisor;
import com.comerzzia.pos.core.gui.componentes.Component;

public class DinoVentaRow extends Component{
	
	public void setVentaRow(LineaDocumentoIVisor lineaVenta, Boolean usaDescuentoEnLinea) {
		((DinoVentaRowController)getController()).setDescuentoEnLinea(usaDescuentoEnLinea);
		((DinoVentaRowController)getController()).setLineaTicket(lineaVenta);
	}

}
