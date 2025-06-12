package com.comerzzia.pampling.pos.gui.ventas.cajas.cierre;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pampling.pos.devices.impresoras.fiscal.alemania.GermanyFiscalPrinter;
import com.comerzzia.pampling.pos.devices.impresoras.fiscal.italia.ItaliaFiscalPrinter;
import com.comerzzia.pampling.pos.services.fiscal.alemania.GermanyFiscalPrinterService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.exception.ValidationException;
import com.comerzzia.pos.gui.inicio.InicioView;
import com.comerzzia.pos.gui.ventas.cajas.CajasView;
import com.comerzzia.pos.gui.ventas.cajas.cierre.CierreCajaController;
import com.comerzzia.pos.gui.ventas.cajas.cierre.CierreCajaView;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;

import jpos.JposException;
@Component
@Primary
public class PamplingCierreCajaController extends CierreCajaController{
	
	private static final Logger log = Logger.getLogger(PamplingCierreCajaController.class);
	
	@Autowired
	private VariablesServices variablesServices;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void accionCierreCaja() {
		// No necesitamos comprobar que se pulse 2 veces seguidas el botón porque se muestra una ventana de confirmación
		log.debug("accionCierreCaja()");
		try {
			if (getApplication().getMainView().getSubViews().size() > 2) {
				if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Para cerrar la caja se deben cerrar todas las pantallas abiertas. ¿Desea continuar?"), getStage())) {
					return;
				}
				boolean couldClose = getApplication().getMainView().closeAllViewsExcept(InicioView.class, CajasView.class, CierreCajaView.class);
				if (!couldClose) {
					return;
				}
			}

			boolean tieneDescuadres = cajaSesion.tieneDescuadres();
			Integer reintentosMax = variablesServices.getVariableAsInteger(VariablesServices.CAJA_REINTENTOS_CIERRE);
			
			if (tieneDescuadres) {
				if(reintentosCierre == null){
					reintentosCierre = 0;
				}
				Integer reintentosRestantes = reintentosMax - reintentosCierre;
				if (reintentosRestantes > 0) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Caja descuadrada con un importe mayor que el permitido. Revise recuento."), getStage());
					reintentosCierre++;
					return;
				}
				else {
					if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se va a cerrar la caja con descuadres mayores al valor permitido, ¿Desea continuar?"), getStage())) {
						return;
					}
				}
			}
			if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Seguro de realizar el Cierre?"), getStage())) {
				return;
			}

			// Procedemos al cierre de la caja
			// Actualizamos el formulario
			formularioCierreCaja.setFechaCierre(tfFechaCierre.getTexto());
			// validamos el formulario
			accionValidarForm();

			cajaSesion.guardarCierreCaja(formularioCierreCaja.getDateCierre());

			IPrinter printer = Dispositivos.getInstance().getImpresora1();
			if(printer instanceof ItaliaFiscalPrinter) {
				try {
					if (isFiscalPrinter()) {
						log.debug("accionCierreCaja() - Impresión con Impresora Fiscal");
						
						((ItaliaFiscalPrinter)Dispositivos.getInstance().getImpresora1()).informeZ();
					}else {
						imprimirCierre(cajaSesion.getCajaAbierta());
					}
				}
				catch (CajasServiceException ex) {
					log.error("accionCierreCaja() - No se pudo realizar la impresión del cierre de caja. ");
					VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageI18N(), ex);
				} catch (JposException e) {
					String error = "Error mientras se hacia el cierre de caja en la impresora fiscal: ";
					log.error("registrarTicket() - "+error + e.getMessage(), e);
					VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
				}
			}
			
			
			reintentosCierre = 0;
			if (tieneDescuadres) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Caja cerrada con descuadres"), getStage());
			}
			
			if(printer instanceof GermanyFiscalPrinter) {
				if(!cierreImpresorasFiscalTse()) {
					return;
				}
				imprimirCierre(cajaSesion.getCajaAbierta());
			}
			cajaSesion.cerrarCaja();
			getStage().close();

		}
		catch (ValidationException ex) {
			log.debug("accionCierreCaja() - La validación no fué exitosa"); // La validación ya se encarga de mostrar el
			                                                                // error
		}
		catch (CajasServiceException e) {
			log.error("accionCierreCaja() - Error al tratar de realizar cierre de caja: " + e.getCause(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessageI18N(), e);
		}
	}

	private boolean cierreImpresorasFiscalTse() {
		/* Impresora TM-M30 TSE */
		IPrinter impresora = Dispositivos.getInstance().getImpresora1();
		String respuesta = ((GermanyFiscalPrinter) impresora).tseCierreCaja(cajaSesion.getCajaAbierta().getTotalRecuento());
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

		return true;
	}

}
