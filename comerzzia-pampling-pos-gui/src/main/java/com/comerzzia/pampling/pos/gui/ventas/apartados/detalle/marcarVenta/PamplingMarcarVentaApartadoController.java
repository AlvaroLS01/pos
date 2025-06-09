package com.comerzzia.pampling.pos.gui.ventas.apartados.detalle.marcarVenta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pampling.pos.devices.impresoras.fiscal.alemania.GermanyFiscalPrinter;
import com.comerzzia.pampling.pos.services.fiscal.alemania.GermanyFiscalPrinterService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.apartados.detalle.marcarVenta.MarcarVentaApartadoController;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Primary
public class PamplingMarcarVentaApartadoController extends MarcarVentaApartadoController{
	
	@Autowired
	protected GermanyFiscalPrinterService germanyFiscalPrinterService;
	
	
	@Override
	public void accionAceptar() {
		if (Dispositivos.getInstance().getImpresora1() instanceof GermanyFiscalPrinter) {
			if (Dispositivos.getInstance().getImpresora1().getConfiguracion().getNombreConexion().equals(GermanyFiscalPrinterService.NOMBRE_CONEXION_TSE)) {
				Boolean correcto = germanyFiscalPrinterService.tseStartTransaction(ticketManager);
				if (!correcto) {
					Boolean resultado = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("No se ha podido realizar la conexión con el TSE, ¿Desea continuar sin TSE?"),
					        getStage());
					if (!resultado) {
						return;
					}
				}
			}
		}
		super.accionAceptar();
	}
}
