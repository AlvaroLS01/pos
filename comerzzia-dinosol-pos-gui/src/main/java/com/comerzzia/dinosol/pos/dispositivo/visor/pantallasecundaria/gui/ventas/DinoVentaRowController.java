package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.ventas;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.ticket.lineas.DinoLineaTicket;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.LineaDocumentoIVisor;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.ventas.VentaRowController;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Primary
@Component
public class DinoVentaRowController extends VentaRowController{
	
	private static final Logger log = Logger.getLogger(DinoVentaRowController.class.getName());
			
	@Override
	protected void update(LineaDocumentoIVisor lineaTicket) {
		hbox.getStyleClass().removeAll("linea-venta-promocion");
		hbox.getStyleClass().removeAll("linea-venta-negativo");
		
		lbDescripcion.setText(lineaTicket.getDescripcion());
		
		String sufijo = "";
				
		if (BigDecimalUtil.isIgual(lineaTicket.getCantidad(), BigDecimal.ONE) || 
			(lineaTicket.getCantidad().stripTrailingZeros().scale() > 0 &&
			 (((DinoLineaTicket)lineaTicket.getLinea()).getCodOperador() == null)
			 )) {
			lbCantidadPrecio.setText("");
		} else {
			if (lineaTicket.getCantidad().stripTrailingZeros().scale() > 0) {
			   sufijo = "/Kg";
			}
			lbCantidadPrecio.setText(FormatUtil.getInstance().formateaNumero(lineaTicket.getCantidad(), 3) + "  x  " + FormatUtil.getInstance().formateaImporteMoneda(lineaTicket.getPrecioSinDescuento()) + sufijo);
		}

		if (usaDescuentoEnLinea) {
			lbDescuento.setVisible(true);
			lbDescuento.setManaged(true);
			if(BigDecimalUtil.isMayorACero(lineaTicket.getDescuento())){
				lbDescuento.setText(I18N.getTexto("Descuento") + ": " + FormatUtil.getInstance().formateaNumero(lineaTicket.getDescuento(), 2) + "%");
				hbox.getStyleClass().add("linea-venta-promocion");
			}else{
				lbDescuento.setText("");
			}		
			lbHueco.setManaged(false);
		} else {
			lbDescuento.setVisible(false);
			lbDescuento.setManaged(false);
			lbHueco.setManaged(true);
		}
		
		if(BigDecimalUtil.isMenorOrIgualACero(lineaTicket.getCantidad())) {
			hbox.getStyleClass().add("linea-venta-negativo");
		}

		lbPrecioTotal.setText(FormatUtil.getInstance().formateaImporteMoneda(lineaTicket.getImporte().setScale(2, RoundingMode.HALF_UP)));

		if (AppConfig.rutaImagenes != null) {
			try {
				imageView.mostrarImagen(lineaTicket.getCodArticulo());
			} catch (Exception e) {
				log.warn("initializeComponent() - Error cargando la imagen del producto:" + e.getMessage());
			}
		} else {
			hbox.getChildren().remove(imageView);	
		}
	
	}	
}
