package com.comerzzia.pampling.pos.gui.ventas.cajas.apuntes;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pampling.pos.devices.impresoras.fiscal.alemania.GermanyFiscalPrinter;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.cajas.apuntes.InsertarApunteController;
import com.comerzzia.pos.util.format.FormatUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

@Component
@Primary
public class PamplingInsertarApunteController extends InsertarApunteController {

	private static final Logger log = Logger.getLogger(PamplingInsertarApunteController.class.getName());

	@FXML
	public void accionAceptar(ActionEvent event) {
		log.debug("accionAceptar()");

		BigDecimal saldo = FormatUtil.getInstance().desformateaBigDecimal(tfImporte.getText());
		
		IPrinter impresora = Dispositivos.getInstance().getImpresora1();
		if (impresora instanceof GermanyFiscalPrinter) {
			try {
				((GermanyFiscalPrinter) impresora).tseApuntes(saldo);
			}
			catch (Exception e) {
				log.error("accionAceptar() - Ha occurido un error al realizar el apunte : " + e.getMessage());
				VentanaDialogoComponent.crearVentanaError("No se ha podido realizar la conexión con el TSE", getStage());
				return;
			}
		}
		super.accionAceptar(event);
	}

}
