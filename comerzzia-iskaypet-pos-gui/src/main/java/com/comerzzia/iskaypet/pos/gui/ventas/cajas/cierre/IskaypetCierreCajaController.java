package com.comerzzia.iskaypet.pos.gui.ventas.cajas.cierre;

import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.devices.proformas.automaticas.ProformasAutomaticasController;
import com.comerzzia.iskaypet.pos.devices.proformas.automaticas.ProformasAutomaticasView;
import com.comerzzia.iskaypet.pos.gui.autorizacion.AutorizacionGerenteUtils;
import com.comerzzia.iskaypet.pos.gui.ventas.cajas.IskaypetCajasController;
import com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraCajaView;
import com.comerzzia.iskaypet.pos.persistence.proformas.ProformaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarProformaBean;
import com.comerzzia.iskaypet.pos.services.cajas.IskaypetCajasService;
import com.comerzzia.iskaypet.pos.services.proformas.ProformaService;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.gui.inicio.InicioView;
import com.comerzzia.pos.gui.ventas.cajas.CajasView;
import com.comerzzia.pos.gui.ventas.cajas.cierre.CierreCajaController;
import com.comerzzia.pos.gui.ventas.cajas.cierre.CierreCajaView;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraCajaController.DOTACION_CAJA;
import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraTipoPagosEnum.EFECTIVO;

/**
 * GAP 79 GESTIÓN DE PROMOCIONES SELECCIÓN MÚLTIPLES
 */
@Primary
@Controller
@SuppressWarnings({ "unchecked", "rawtypes" })
public class IskaypetCierreCajaController extends CierreCajaController {

	private static final Logger log = Logger.getLogger(IskaypetCierreCajaController.class.getName());
	private final String TITULO_CALCULADORA = "TITULO_CALCULADORA";
	private final String TITULO_RECUENTO_CAJA = "RECUENTO CAJA";

	private final String ARQUEO_VISIBLE = "ARQUEO VISIBLE";

	@FXML
	protected Button btAbrirRecuento;

	@Autowired
	protected IskaypetCajasService iskaypetCajasService;

	@Autowired
	private VariablesServices variablesServices;

	@Autowired
	private Sesion sesion;

	@Autowired
	private ProformaService proformaService;

	@Getter
	@Setter
	private boolean cancelado;

	@Override
	public void initializeFocus() {
		super.initializeFocus();
        try {
			setCancelado(false);
			abrirFacturacionProformasAutomaticas();
			if (isCancelado()) {
				log.debug("initializeFocus() - Cancelado por el usuario al abrir el menu de facturación de proformas automáticas");
				accionCancelar();
				return;
			}

			abrirRecuentoConfirmacion(false, Boolean.TRUE);
			if (isCancelado()) {
				log.debug("initializeFocus() - Cancelado por el usuario al abrir el recuento de caja");
				accionCancelar();
			}
        } catch (CajasServiceException e) {
			log.error("abrirRecuentoConfirmacion() -- Error al abrir el recuento de caja.");
            throw new RuntimeException(e);
        } catch (InitializeGuiException e) {
			if (e.isMostrarError()) {
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}
			accionCancelar();
		}
    }

    private void abrirFacturacionProformasAutomaticas() {
        log.debug("abrirFacturacionProformasAutomaticas() - Abriendo facturación de proformas automáticas");
        SqlSession sqlSession = new SqlSession();
        try {
            sqlSession.openSession(SessionFactory.openSession());

            DatosSesionBean datosSesion = new DatosSesionBean();
            datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
            datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());

            ParametroBuscarProformaBean param = new ParametroBuscarProformaBean();
            param.setUidActividad(datosSesion.getUidActividad());
            param.setEstadoActual(ProformaBean.ESTADO_ENVIADA);
            param.setCodalm(sesion.getAplicacion().getCodAlmacen());
            param.setAutomatica(true);

            long proformasEncontradas = proformaService.count(sqlSession, datosSesion, param);

            if (proformasEncontradas > 0) {
                log.debug("abrirFacturacionProformasAutomaticas() - Se han encontrado " + proformasEncontradas + " proformas automáticas.");
                if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se han encontrado {0} proformas automáticas. ¿Desea abrir la pantalla de facturación? \n Nota: Es necesario para continuar con el cierre de caja", proformasEncontradas), getStage())) {
                    setCancelado(true);
					return;
                }

				// Abrimos la pantalla de facturación de proformas automáticas
				getApplication().getMainView().showModalCentered(ProformasAutomaticasView.class, getDatos(), getStage());
				cajaSesion.actualizarDatosCaja();
				refrescarDatosPantalla();
            }

        } catch (Exception e) {
            log.error("abrirFacturacionProformasAutomaticas() - Error al abrir la pantalla de facturación de proformas automáticas: " + e.getMessage(), e);
        }
    }


	@Override
	public void abrirRecuentoCaja() throws CajasServiceException {
		log.debug("abrirRecuentoCaja() - Abrir pantalla de recuento de caja.");
        try {
            abrirRecuentoConfirmacion(true, Boolean.TRUE);
        } catch (InitializeGuiException e) {
			log.error("abrirRecuentoCaja() -- Error al abrir el recuento de caja. Escape contraseña gerente");
        }
    }

	@Override
	public void comprobarPermisosUI() {
		super.comprobarPermisosUI();

		try {
			super.compruebaPermisos(ARQUEO_VISIBLE);
			btAbrirRecuento.setVisible(false);
		}
		catch (SinPermisosException e) {
			btAbrirRecuento.setVisible(true);
		}
	}

	private void abrirRecuentoConfirmacion(Boolean confirmacion, Boolean requiereAbrirCajon) throws CajasServiceException, InitializeGuiException {
		log.debug("abrirRecuentoCaja() - Abrir pantalla de recuento de caja.");
		if (getApplication().getMainView().getSubViews().size() > 2) {

			if (confirmacion) {
				if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Para cerrar la caja se deben cerrar todas las pantallas abiertas. ¿Desea continuar?"), getStage())) {
					setCancelado(true);
					return;
				}
			}
			boolean couldClose = getApplication().getMainView().closeAllViewsExcept(new Class[]{InicioView.class, CajasView.class, CierreCajaView.class});
			if (!couldClose) {
				if (!confirmacion) {
					setCancelado(true);
				}
				return;
			}
		}

		limpiarRecuentos();
		try {
			HashMap<String, Object> datos = new HashMap<>();
			datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, false);
			AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);

			if (requiereAbrirCajon) {
				abrirCajon();
			}
			getDatos().put(TITULO_CALCULADORA, TITULO_RECUENTO_CAJA);
			getApplication().getMainView().showModalCentered(ContadoraCajaView.class, getDatos(), getStage());
			refrescarDatosPantalla();
		} catch (InitializeGuiException initializeGuiException) {
			if (initializeGuiException.isMostrarError()) {
				VentanaDialogoComponent.crearVentanaError(getStage(), initializeGuiException);
			}
			throw initializeGuiException;
		}
	}

	private boolean limpiarRecuentos() throws CajasServiceException {
		if (cajaSesion.existeRecuento(cajaSesion.getCajaAbierta())) {

			if (cajaSesion.getCajaAbierta().getLineasRecuento() != null && !cajaSesion.getCajaAbierta().getLineasRecuento().isEmpty()) {
				cajaSesion.getCajaAbierta().setLineasRecuento(new ArrayList());
			}

			cajaSesion.limpiarRecuentos(cajaSesion.getCajaAbierta());
		}
		return true;
	}

	@Override
	public void imprimirCierre(Caja caja) throws CajasServiceException {
		try {
			iskaypetCajasService.impresionDeCierre(caja);
			mostarInfoEfectivoRetirar(caja);
		}
		catch (Exception e) {
			log.error("impresionDeCierre() - Error imprimiendo  cierre de caja. Error inesperado: " + e.getMessage(), e);
			throw new CajasServiceException("error.service.cajas.print", e);
		}
	}

	public void mostarInfoEfectivoRetirar(Caja caja) {
		BigDecimal saldoEfectivo = caja.getLineasRecuento().stream().filter(el -> el.getCodMedioPago().equals(EFECTIVO.getCodPago()))
				.map(el -> el.getValor().multiply(new BigDecimal(el.getCantidad()))).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal dotacionCaja = this.variablesServices.getVariableAsBigDecimal(DOTACION_CAJA);

		BigDecimal saldoRetirar = saldoEfectivo.subtract(dotacionCaja).setScale(2, BigDecimal.ROUND_HALF_UP);

		if (saldoRetirar.compareTo(BigDecimal.ZERO) > 0) {
			String mensaje = I18N.getTexto("Recuerda sacar de la caja el siguiente efectivo: {0} €",FormatUtil.getInstance().formateaImporte(saldoRetirar));
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensaje), getStage());
		}
	}



}
