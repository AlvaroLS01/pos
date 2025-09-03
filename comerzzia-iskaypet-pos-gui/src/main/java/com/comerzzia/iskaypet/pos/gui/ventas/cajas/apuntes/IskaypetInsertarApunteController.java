package com.comerzzia.iskaypet.pos.gui.ventas.cajas.apuntes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraCajaController;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaDto;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.motivos.CargarMotivosController;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.motivos.CargarMotivosView;
import com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraCajaView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.iskaypet.pos.persistence.movimientos.manualEyS.MovimientoEyS;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriasService;
import com.comerzzia.iskaypet.pos.services.ventas.cajas.apuntes.IskaypetApuntesService;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.cajas.apuntes.InsertarApunteController;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

@Controller
@Primary
public class IskaypetInsertarApunteController extends InsertarApunteController {

	private Logger log = Logger.getLogger(IskaypetInsertarApunteController.class);

	public static final String CONCEPTO_SELECCIONADO = "CONCEPTO_SELECCIONADO";
	public static final String TITULO_CALCULADORA = "TITULO_CALCULADORA";
	public static final String IMPORTE_RETIRAR = "IMPORTE_RETIRAR";
	public static final String AVISO_RETIRADA = "AVISO_RETIRADA";
	private static final String SALDO = "saldo";
	private static final String ENTRADACAJ = "ENTRADACAJ";
	private static final String SALIDACAJ = "SALIDACAJ";

	private String lblTitle;
	private BigDecimal importeRetirar;
	private Boolean avisoRetirada;

	@FXML
	protected Button btBuscarConceptos, btContarSaldo, btCancelar, btAyuda;
	@FXML
	protected TextField tfCodMotivo, tfDesMotivo;
	@FXML
	protected TextArea tfObservaciones;
	@FXML
	protected FlowPane fpPanelDocumento;
	@FXML
	protected VBox vBPanelEyS;

	protected MotivoAuditoriaDto motivoSeleccionado;

	protected CajaConceptoBean concepto;

	@Autowired
	protected AuditoriasService auditoriasService;

	@Autowired
	private Sesion sesion;
	
	protected String tipoAuditoria = null;
	protected boolean esMovimientoEyS=false;
	@Autowired
	protected IskaypetApuntesService iskaypetApuntesService;

	@Override
	public void registrarAccionCerrarVentanaEscape() {
		// No hacer nada
	}

	@Override
	public void initializeForm() {
		super.initializeForm();

		if (getDatos().containsKey(CONCEPTO_SELECCIONADO)) {

			this.concepto = (CajaConceptoBean) datos.get(CONCEPTO_SELECCIONADO);
			tfDescConcepto.setText(concepto.getDesConceptoMovimiento());
			tfCodConcepto.setText(concepto.getCodConceptoMovimiento());
			evalInOutValue(tfImporte.getText());

			btBuscarConceptos.setVisible(false);
			btContarSaldo.setVisible(true);
			tfImporte.setEditable(false);
			tfImporte.setDisable(true);
			tfCodConcepto.setDisable(true);
			tfCodConcepto.setEditable(false);
			tfDescConcepto.setDisable(true);
			tfDescConcepto.setEditable(false);
		}

		if (getDatos().containsKey(TITULO_CALCULADORA)) {
			lblTitle = (String) getDatos().get(TITULO_CALCULADORA);
		}

		if (getDatos().containsKey(IMPORTE_RETIRAR)) {
			importeRetirar = (BigDecimal) getDatos().get(IMPORTE_RETIRAR);
			if (getDatos().containsKey(AVISO_RETIRADA)) {
				avisoRetirada = (Boolean) getDatos().get(AVISO_RETIRADA);
				btCancelar.setVisible(avisoRetirada);
			}
		}
		else {
			btCancelar.setVisible(true);
			importeRetirar = null;
			avisoRetirada = null;
		}
		esMovimientoEyS= concepto.getCodConceptoMovimiento().equals("03") || concepto.getCodConceptoMovimiento().equals("02");
		
		if(esMovimientoEyS) {
			fpPanelDocumento.setVisible(false);
			fpPanelDocumento.setMinHeight(0.0);
			vBPanelEyS.setVisible(true);
			vBPanelEyS.setMinHeight(Control.USE_COMPUTED_SIZE);
			
			tfCodMotivo.setText("");
			tfDesMotivo.setText("");
			tfObservaciones.setText("");
		}else {
			fpPanelDocumento.setVisible(true);
			fpPanelDocumento.setMinHeight(Control.USE_COMPUTED_SIZE);
			vBPanelEyS.setVisible(false);
			vBPanelEyS.setMinHeight(0.0);
		}
	}

	@Override
	public void initializeFocus() {
		super.initializeFocus();
		if (!tfDescConcepto.getText().isEmpty()) {
			tfImporte.requestFocus();
		}
		accionContarImporte();

	}

	@FXML
	public void accionContarImporte() {
		HashMap<String, Object> datos = new HashMap<>();
		if (StringUtils.isNotBlank(lblTitle)) {
			datos.put(TITULO_CALCULADORA, lblTitle);
		}

		if (importeRetirar != null) {
			datos.put(IMPORTE_RETIRAR, importeRetirar);
			if (avisoRetirada != null) {
				datos.put(AVISO_RETIRADA, avisoRetirada);
			}
		}

		getApplication().getMainView().showModalCentered(ContadoraCajaView.class, datos, getStage());
		if ((boolean) datos.getOrDefault(ContadoraCajaController.ACCION_CANCELAR, false)) {
			getStage().close();
		}

		BigDecimal saldo = (BigDecimal) datos.get(SALDO);
		if (saldo != null) {
			tfImporte.setText(FormatUtil.getInstance().formateaImporte(saldo));
		}
	}

	@FXML
	public void accionBuscarMotivos() {
		getDatos().clear();
		
		if (concepto.getCodConceptoMovimiento().equals("03")) {
			tipoAuditoria = AuditoriasService.TIPO_ENTRADA_CAJA;
			
		}
		else if (concepto.getCodConceptoMovimiento().equals("02")) {
			tipoAuditoria = AuditoriasService.TIPO_SALIDA_CAJA;
		}

		getDatos().put(IskaypetFacturacionArticulosController.TIPO_DOCUMENTO_ENVIADO, tipoAuditoria);
		getApplication().getMainView().showModalCentered(CargarMotivosView.class, getDatos(), getStage());
		motivoSeleccionado = (MotivoAuditoriaDto) getDatos().get(CargarMotivosController.MOTIVO);

		if (motivoSeleccionado != null) {
			tfCodMotivo.setText(motivoSeleccionado.getCodigo().toString());
			tfDesMotivo.setText(motivoSeleccionado.getDescripcion());
		}
		lbError.setText("");
	}

	@Override
	public void accionAceptar(ActionEvent event) {

		log.debug("accionAceptar()");

		if ((this.concepto == null) || (!this.concepto.getCodConceptoMovimiento().equals(tfCodConcepto.getText()))) {
			if (!validaConcepto(tfCodConcepto.getText())) {
				return;
			}
		}

		frApunte.setDocumento(tfDocumento.getText());
		frApunte.setImporte(tfImporte.getText());
		frApunte.setConcepto(concepto);
		frApunte.setDescripcion(tfDescConcepto.getText());

		if (!accionValidarFormulario()) {
			log.debug("datos del apunte invalidos");
			return;
		}

		CajaMovimientoBean mov = null;
		// PERSONALIZADOS
		if (importeRetirar != null && StringUtils.isNotBlank(tfImporte.getText())) {
			BigDecimal saldo = FormatUtil.getInstance().desformateaImporte(tfImporte.getText());
			if (!BigDecimalUtil.isIgual(saldo, importeRetirar)) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe a retirar debe ser {0}€, por favor reviselo de nuevo", new Object[] { importeRetirar }), getStage());
				return;
			}
		}
		// GAP 73 ENTRADAS Y SALIDAS DE CAJAS
		if (esMovimientoEyS) {
			if (StringUtils.isBlank(tfCodMotivo.getText())) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar un motivo de la lista."), getStage());
				return;
			}
		}
		try {
			//TODO AQUI SELECCIONAR EL CARGO MENOS EL ABONO
			if(sesion.getAplicacion().getTienda().getTiendaBean().getCodMedioPagoPorDefecto()== "01") {
				
			}
			mov = sesion.getSesionCaja().crearApunteManual(frApunte.getImporteAsBigDecimal(), frApunte.getConcepto().getCodConceptoMovimiento(), frApunte.getDocumento(), tfDescConcepto.getText());
		}
		catch (Exception e) {
			log.error("accionAceptar() - Error inesperado - " + e.getCause(), e);
			VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
		}

		if (mov != null) {
			try {
				imprimirMovimiento(mov);
			}
			catch (Exception e) {
				log.error("accionAceptar() - Error inesperado imprimiendo- " + e.getCause(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}
		}

		// GAP 73 ENTRADAS Y SALIDAS DE CAJAS
		if(esMovimientoEyS && mov !=null) {
			try {
				crearMovimientoManual(mov);
			}
			catch (Exception e) {
				log.error("accionAceptar() - Error al guardar la informacion del movimiento - " + e.getCause(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}
		}
		getStage().close();
	}

	private void crearMovimientoManual(CajaMovimientoBean mov) throws Exception {
		
		log.debug("crearMovimientoManual() - Guardando movimiento entrada/salida manuales...");
		String codMotivo= tfCodMotivo.getText();
		String desMotivo= tfDesMotivo.getText();
		String observaciones= tfObservaciones.getText();
		MovimientoEyS movimiento = new MovimientoEyS();
		movimiento.setLinea(mov.getLinea());
		movimiento.setCodMotivo(Long.parseLong(codMotivo));
		movimiento.setObservaciones(observaciones);
		movimiento.setUidActividad(mov.getUidActividad());
		movimiento.setUidDiarioCaja(mov.getUidDiarioCaja());
		movimiento.setTipoAuditoria(tipoAuditoria);
		movimiento.setDesMotivo(desMotivo);

		iskaypetApuntesService.registrarMovimientoEyS(movimiento);
		String tipoDocumento = mov.getAbono().compareTo(BigDecimal.ZERO) == 0 ? ENTRADACAJ : SALIDACAJ;
		auditoriasService.generarAuditoria(getAuditoria(movimiento, mov), tipoDocumento, null,  Boolean.TRUE);
	}
	
	@Override
	protected boolean evalInOutValue(String inOutValue) {
    	int inOut = -1;
		if(concepto != null && StringUtils.isNotBlank(concepto.getInOut())) {
			if(concepto.getInOut().equals(CajaConceptoBean.MOV_ENTRADA)) {
				inOut = inOut*-1;
				lbTipoImporte.setText(I18N.getTexto("ENTRADA EN CAJA"));
			}else {
				lbTipoImporte.setText(I18N.getTexto("SALIDA DE CAJA"));
			}
		}
		
		if(StringUtils.isNotBlank(inOutValue)) {
			BigDecimal bd = FormatUtil.getInstance().desformateaImporte(inOutValue);
			if(bd != null) {				
				inOut = inOut * bd.signum();
			}
		}
		
    	return true;
    }

	@Override
	public void imprimirMovimiento(CajaMovimientoBean movimiento) throws CajasServiceException {
		try {
			Map<String, Object> contextoTicket = new HashMap();
			contextoTicket.put("movimiento", movimiento);
			contextoTicket.put("caja", this.sesion.getSesionCaja().getCajaAbierta() != null ? this.sesion.getSesionCaja().getCajaAbierta().getCodCaja(): "");
			contextoTicket.put("tienda", this.sesion.getAplicacion().getTienda().getCodAlmacen());
			contextoTicket.put("empleado", this.sesion.getSesionUsuario().getUsuario().getDesusuario());
			ServicioImpresion.imprimir("movimiento_caja", contextoTicket);
		} catch (Exception var3) {
			log.error("imprimirCierre() - Error imprimiendo  cierre de caja. Error inesperado: " + var3.getMessage(), var3);
			throw new CajasServiceException("error.service.cajas.print", var3);
		}
	}

	private AuditoriaDto getAuditoria(MovimientoEyS movimiento, CajaMovimientoBean mov) {
		AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setCodMotivo(movimiento.getCodMotivo().toString());
		auditoria.setObservaciones(movimiento.getObservaciones());
		auditoria.setPrecioInicial(mov.getAbono());
		auditoria.setPrecioFinal(mov.getCargo());
		auditoria.setCodArticulo(mov.getCodConceptoMovimiento());
		auditoria.setDesArt(movimiento.getDesMotivo());
		auditoria.setDesglose1("*");
		auditoria.setDesglose2("*");
		return auditoria;
	}
}
