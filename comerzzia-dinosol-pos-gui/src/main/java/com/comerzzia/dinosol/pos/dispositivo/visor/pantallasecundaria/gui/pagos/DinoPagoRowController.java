package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.pagos;

import com.comerzzia.pos.core.dispositivos.dispositivo.visor.PagoDocumentoIVisor;
import com.comerzzia.pos.core.gui.componentes.ComponentController;
import com.comerzzia.pos.core.gui.componentes.imagenArticulo.ImagenArticulo;
import com.comerzzia.pos.util.format.FormatUtil;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class DinoPagoRowController extends ComponentController{
	@FXML 
	protected HBox hbox;
	
	@FXML 
	protected ImagenArticulo imageView;
	
	@FXML
	protected Label lbFormaPago;
	
	@FXML
	protected Label lbImportePago;
	
	// lista de pagos
	protected ObservableList<PagoDocumentoIVisor> pagos;
	
	@Override
	public void initializeFocus() {
		
	}

	public void setLineaPago(PagoDocumentoIVisor pagoDocumento) {
		update(pagoDocumento);
	}

	@Override
	public void initializeComponent() {
	}
	
	
	protected void update(PagoDocumentoIVisor pagoDocumento){
        if (pagoDocumento != null) {
		   lbFormaPago.setText(pagoDocumento.getFormaPago());	
		   lbImportePago.setText(FormatUtil.getInstance().formateaImporteMoneda(pagoDocumento.getImporte()));
        } else {
           lbFormaPago.setText("");	
 		   lbImportePago.setText("");
        	
        }
	}



}
