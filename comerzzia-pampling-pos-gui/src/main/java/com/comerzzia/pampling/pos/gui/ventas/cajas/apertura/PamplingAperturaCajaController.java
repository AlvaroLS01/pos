package com.comerzzia.pampling.pos.gui.ventas.cajas.apertura;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pampling.pos.devices.impresoras.fiscal.alemania.GermanyFiscalPrinter;
import com.comerzzia.pampling.pos.services.fiscal.alemania.GermanyFiscalPrinterService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.cajas.apertura.AperturaCajaController;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Primary
public class PamplingAperturaCajaController extends AperturaCajaController {

	private static final Logger log = Logger.getLogger(PamplingAperturaCajaController.class.getName());

	@Override
	public void accionAceptar() {
		super.accionAceptar();
		try {
			// TSE
			String saldo = formularioAperturaGui.getSaldo();
			BigDecimal saldoBigDecimal = FormatUtil.getInstance().desformateaImporte(saldo);
			saldoBigDecimal = saldoBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);

			if (!aperturaImpresorasFiscal(saldoBigDecimal)) {
				return;
			}
		}
		catch (Exception e) {
			log.error("accionAceptar() - Error al tratar de realizar apertura de caja: " + e.getCause(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e);
		}
	}

	private Boolean aperturaImpresorasFiscal(BigDecimal saldoBigDecimal) throws Exception {
		/* Impresora TM-M30 TSE */
		
		 IPrinter impresora = Dispositivos.getInstance().getImpresora1();
			if (impresora instanceof GermanyFiscalPrinter) {
				String respuesta = ((GermanyFiscalPrinter)impresora).tseAperturaCaja(saldoBigDecimal);
				if (respuesta != null && !respuesta.equals(GermanyFiscalPrinterService.EXECUTION_OK)) {
					if (respuesta.equals(GermanyFiscalPrinterService.TSE1_ERROR_WRONG_STATE_NEEDS_SELF_TEST)) {
						VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El estado del TSE es erróneo, por favor, reinicie la Impresora y Comerzzia para poder operar con TSE."), getStage());
						return false;
					}
					else {
						if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Ha ocurrido un error con el TSE y no se ha podido enviar la operación, ¿Desea continuar?."), getStage())) {
							return false;
						}
					}
				}
			}
		return true;
	}
}
