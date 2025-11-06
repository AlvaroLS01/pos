package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.pagos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.dispositivos.dispositivo.visor.DocumentoIVisor;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.PagoDocumentoIVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.dispositivo.visor.VisorPantallaSecundaria;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.pagos.PantallaSecundariaPagosController;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

@Primary
@Component
public class DinoPantallaSecundariaPagosController extends PantallaSecundariaPagosController{
	
	@FXML
	public VBox containerPagos;
	
	protected static int MAX = 4;
	
	protected List<DinoPagoRow> pagosRowCache = new ArrayList<>(MAX);
	
	
	@Override
	public void initializeComponents() throws InitializeGuiException {
		setShowKeyboard(false);

		// crear los paneles que representaran los pagos
		for (int i = 0; i < MAX; i++) {
			DinoPagoRow row = new DinoPagoRow();
			row.init(getScene());
			pagosRowCache.add(row);
			containerPagos.getChildren().add(row);
		}
	}
	
	@Override
	public void refrescarDatosPantalla() {
        log.debug("refrescarDatosPantalla() - Refrescando datos de pantalla...");
		
		DocumentoIVisor documento = VisorPantallaSecundaria.getTicket();
		List<PagoDocumentoIVisor> lineaPagos = new ArrayList<PagoDocumentoIVisor>();

		for (PagoDocumentoIVisor pago : documento.getPagos()) {
			lineaPagos.add(pago);
		}
	     Collections.reverse(lineaPagos);
        if(lineaPagos.size() > MAX){
        	lineaPagos = lineaPagos.subList(0, MAX);
        }
		updateRows(lineaPagos);
		lineaPagos = null;
		
		if(documento.isDevolucion()){
			lbTipo.setText(I18N.getTexto("A DEVOLVER"));
		}else{
			lbTipo.setText(I18N.getTexto("TOTAL"));
		}
		
		lbTotalPagado.setText(FormatUtil.getInstance().formateaImporteMoneda(documento.getEntregado()));
		lbTotal.setText(FormatUtil.getInstance().formateaImporteMoneda(documento.getTotalAPagar()));
        lbPendiente.setText(FormatUtil.getInstance().formateaImporteMoneda(documento.getPendiente()));
        lbCambio.setText(FormatUtil.getInstance().formateaImporteMoneda(documento.getCambio()));
        
    
	}
	
	protected void updateRows(List<PagoDocumentoIVisor> pagosOrdenados) {
		for (int i = 0; i < MAX; i++) {
			if (i < pagosOrdenados.size()) {
			   pagosRowCache.get(i).setPagoRow((PagoDocumentoIVisor) pagosOrdenados.get(i));
			} else {
			   pagosRowCache.get(i).setPagoRow(null);
			}
		}
	}
}
		
	
		
		
	
	
	

	


