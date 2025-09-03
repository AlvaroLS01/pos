package com.comerzzia.iskaypet.pos.gui.ventas.cajas;

import com.comerzzia.iskaypet.pos.gui.autorizacion.AutorizacionGerenteUtils;
import com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraCajaView;
import com.comerzzia.iskaypet.pos.services.cajas.IskaypetCajasService;
import com.comerzzia.iskaypet.pos.services.closingday.ClosingEndDayService;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.view.ModalView;
import com.comerzzia.pos.gui.ventas.cajas.CajasController;
import com.comerzzia.pos.gui.ventas.cajas.apuntes.InsertarApunteView;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.cajas.conceptos.CajaConceptosServices;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.IskaypetCajasConceptosEnum.*;
import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.apuntes.IskaypetInsertarApunteController.AVISO_RETIRADA;
import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.apuntes.IskaypetInsertarApunteController.IMPORTE_RETIRAR;

/**
 * GAP 27 CIERRE DE CAJA DE TODAS LAS TIENDAS ISK-221 GAP 27.2 Ampliación del cierre de fin de día
 */
@Component
@Primary
@SuppressWarnings({ "rawtypes", "unchecked" })
public class IskaypetCajasController extends CajasController {

	private static final Logger log = Logger.getLogger(IskaypetCajasController.class.getName());

	private final String CONCEPTO_SELECCIONADO = "CONCEPTO_SELECCIONADO";
	private final String TITULO_CALCULADORA = "TITULO_CALCULADORA";
	private final String RECUENTO_CAJA = "RECUENTO CAJA";
	private final String APERTURA_CAJA = "APERTURA CAJA";

	private final String CANTIDAD_RETIRADA_EFECTIVO = "X.POS.CANTIDAD_RETIRADA_EFECTIVO";

	@Autowired
	protected ClosingEndDayService closingEndDayService;

	@Autowired
	protected CajaConceptosServices cajaConceptosServices;

	@Autowired
	protected IskaypetCajasService iskaypetCajasService;

	@Autowired
	protected VariablesServices variablesServices;

	@Autowired
	private Sesion sesion;

	@SuppressWarnings("static-access")
	@Override
	public void abrirCaja() {
		log.debug("abrirCaja()");
		try {
			// GAP27.2 - AMPLIACIÓN DEL CIERRE DE FIN DE DÍA
			// Se meten nuevas validaciones para comprobaciones de fin día.
			String msgError = "";
			
			// VALIDACION01 : Existe caja abierta.
			if (cajaSesion.isCajaAbierta()) {
				msgError = I18N.getTexto("Ya existe una caja abierta en el sistema.");
			}
			else {
				// VALIDACION02 : Existe cierre de fin de día para hoy.
				if(!closingEndDayService.comprobarCierreFinDia()){
					msgError = I18N.getTexto(closingEndDayService.ERROR_CIERREZ_GENERADO_HOY);
				}
				else {
					// VALIDACION03 : El último cierre de caja tiene el cierre de fin de día hecho.
					String dateLastConfirmationClosingEndDay = closingEndDayService.lastClosingEndDayConfirmed();
					if(StringUtils.isNotBlank(dateLastConfirmationClosingEndDay)){
						msgError = I18N.getTexto(dateLastConfirmationClosingEndDay);
					}
				}
			}

			if (StringUtils.isNotBlank(msgError)) {
				VentanaDialogoComponent.crearVentanaError(msgError, getStage());
				return;
			}
			else {
				HashMap<String, Object> datos = new HashMap<>();
				datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, false);
				AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
				abrirCajon();
				datos.put(TITULO_CALCULADORA, APERTURA_CAJA);
				getApplication().getMainView().showModalCentered(ContadoraCajaView.class, datos, getStage());
				actualizarEstadoCaja();
				refrescarDatosPantalla();
			}
		} catch (InitializeGuiException initializeGuiException) {
			if (initializeGuiException.isMostrarError()) {
				VentanaDialogoComponent.crearVentanaError(getStage(), initializeGuiException);
			}
		} catch (Exception e) {
			VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
		}
	}

	@Override
	public void abrirCierreCaja() {
		super.abrirCierreCaja();
		refrescarDatosPantalla();
	}

	@Override
	public void actualizarEstadoCaja() {
		super.actualizarEstadoCaja();

		if (!cajaSesion.isCajaAbierta()) {
			lbCaja.setText(I18N.getTexto("Caja CERRADA"));
			botoneraMenu.setAccionDisabled("aperturaCajon", true);
			botoneraMenu.setAccionDisabled("entradaEfectivo", true);
			botoneraMenu.setAccionDisabled("retiradaEfectivo", true);
			botoneraMenu.setAccionDisabled("salidaEfectivo", true);
		}
		else {
			lbCaja.setText(I18N.getTexto("Caja ABIERTA"));
			botoneraMenu.setAccionDisabled("aperturaCajon", false);
			botoneraMenu.setAccionDisabled("entradaEfectivo", false);
			botoneraMenu.setAccionDisabled("retiradaEfectivo", false);
			botoneraMenu.setAccionDisabled("salidaEfectivo", false);
		}

	}

	@Override
	public void abrirRecuentoCaja() throws CajasServiceException {
		log.debug("abrirRecuentoCaja() - Abrir pantalla de recuento de caja.");
		boolean confirmacion = true;
		if (getApplication().getMainView().getSubViews().size() > 1) {
			confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Para realizar el recuento de caja se deben cerrar todas las pantallas abiertas. ¿Desea continuar?"),
					getStage());
		}

		if (confirmacion) {
			if (cajaSesion.existeRecuento(cajaSesion.getCajaAbierta()) && VentanaDialogoComponent.crearVentanaConfirmacion(
					I18N.getTexto("Ya existe un recuento de la caja. ¿Desea borrarlo y empezar de nuevo?"), getStage())) {
				if (cajaSesion.getCajaAbierta().getLineasRecuento() != null && !cajaSesion.getCajaAbierta().getLineasRecuento().isEmpty()) {
					cajaSesion.getCajaAbierta().setLineasRecuento(new ArrayList());
				}

				cajaSesion.limpiarRecuentos(cajaSesion.getCajaAbierta());
			}

			getDatos().put(TITULO_CALCULADORA, RECUENTO_CAJA);
			cerrarFuncionesPendientes(ContadoraCajaView.class, getDatos(), Boolean.TRUE);
		}

		refrescarDatosPantalla();
	}

	private void cerrarFuncionesPendientes(Class<? extends ModalView> viewToShow, HashMap<String, Object> datos, Boolean requiereAbrirCajon) {
		boolean closeAllViewsExcept = getApplication().getMainView().closeAllViewsExcept(new Class[]{getView().getClass()});
		if (closeAllViewsExcept) {
			try {
				datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
				AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
				if (requiereAbrirCajon) {
					abrirCajon();
				}
				getApplication().getMainView().showModalCentered(viewToShow, datos, getStage());
				getView().loadAndInitialize();
			} catch (InitializeGuiException initializeGuiException) {
				if (initializeGuiException.isMostrarError()) {
					VentanaDialogoComponent.crearVentanaError(getStage(), initializeGuiException);
				}
			}
		}
	}

	public void aperturaCajon() throws CajasServiceException {

		try {
			// Se muestra la autorización de gerente
			if (APERTURA_CAJON.getRequiereGerente()) {
				HashMap<String, Object> datos = new HashMap<>();
				datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
				AutorizacionGerenteUtils.muestraPantallaAutorizacion( getApplication().getMainView(), getStage(), datos);
			}
			abrirCajon();

		}
		catch (InitializeGuiException e) {
			if (e.isMostrarError()) {
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}
		}

	}

	public void entradaEfectivo() {
		muestraInsertarApunte(ENTRADA_EFECTIVO, Boolean.TRUE);
	}

	public void retiradaEfectivo() {
		muestraInsertarApunte(RETIRADA_EFECTIVO, Boolean.TRUE);
	}

	public void salidaEfectivo() {
		muestraInsertarApunte(SALIDA_EFECTIVO, Boolean.TRUE);
	}

	private void muestraInsertarApunte(IskaypetCajasConceptosEnum concepto, Boolean requiereAbrirCajon) {

		try {
			HashMap<String, Object> datos = new HashMap<>();
			if (concepto.getRequiereGerente()) {
				if(concepto.equals(RETIRADA_EFECTIVO)) {
					datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, false);
					AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
				}
				else {
					datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
					AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
				}
			}

			if(requiereAbrirCajon) {
				abrirCajon();
			}

			if (comprobarCierreCajaDiarioObligatorio()) {
				CajaConceptoBean conceptoBean = cajaConceptosServices.getConceptoCaja(concepto.getCodConcepto());
				if (conceptoBean != null) {
					if (concepto.equals(RETIRADA_EFECTIVO)) {
						BigDecimal cantidadRetirar = variablesServices.getVariableAsBigDecimal(CANTIDAD_RETIRADA_EFECTIVO).setScale(2, 4);
						if (BigDecimalUtil.isMayor(cantidadRetirar, BigDecimal.ZERO)) {
							getDatos().put(IMPORTE_RETIRAR, cantidadRetirar);
							getDatos().put(AVISO_RETIRADA, true);
						}
					}
					getDatos().put(CONCEPTO_SELECCIONADO, conceptoBean);
					getDatos().put(TITULO_CALCULADORA, concepto.getDesConcepto());
					int registros = movimientos.size();
					getApplication().getMainView().showModalCentered(InsertarApunteView.class, getDatos(), getStage());
					refrescarDatosPantalla();
					if (registros != movimientos.size()) {
						tbMovimientos.requestFocus();
						tbMovimientos.getSelectionModel().selectLast();
						int indSeleccionado = tbMovimientos.getSelectionModel().getSelectedIndex();
						tbMovimientos.getFocusModel().focus(indSeleccionado);
						tbMovimientos.scrollTo(indSeleccionado);
					}
				}
				else {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha ocurrido un error intentando mostrar apunte, concepto no encontrado"), getStage());
				}
			}
			else {
				String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
				String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
				VentanaDialogoComponent.crearVentanaError(
						I18N.getTexto("No se puede insertar un apunte manual. El día de apertura de la caja {0} no coincide con el del sistema {1}",fechaCaja, fechaActual),
						getStage());
			}

		}
		catch (InitializeGuiException e) {
			if (e.isMostrarError()) {
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}
		}

	}

	@Override
	public void imprimirCierre(Caja caja) throws CajasServiceException {
		try {
			iskaypetCajasService.impresionDeCierre(caja);
		}
		catch (Exception e) {
			log.error("impresionDeCierre() - Error imprimiendo  cierre de caja. Error inesperado: " + e.getMessage(), e);
			throw new CajasServiceException("error.service.cajas.print", e);
		}

	}
}
