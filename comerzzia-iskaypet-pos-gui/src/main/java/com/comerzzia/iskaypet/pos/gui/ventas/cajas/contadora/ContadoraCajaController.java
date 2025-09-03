package com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora;

import com.comerzzia.iskaypet.pos.services.closingday.ClosingEndDayService;
import com.comerzzia.iskaypet.pos.util.formatter.IskaypetFormatter;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.cajas.acumulados.CajaLineaAcumuladoBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionCaja;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;
import java.util.List;

import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.apuntes.IskaypetInsertarApunteController.AVISO_RETIRADA;
import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.apuntes.IskaypetInsertarApunteController.IMPORTE_RETIRAR;
import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraTipoPagosEnum.*;

@Component
public class ContadoraCajaController extends WindowController implements Initializable {

	private static final Logger log = Logger.getLogger(ContadoraCajaController.class.getName());
	private static final Integer CANTIDAD_UNO = 1;
	private static final Integer CANTIDAD_ZERO = 0;
	private static String SALDO = "saldo";
	private final String TITULO_CALCULADORA = "TITULO_CALCULADORA";
	private final String RECUENTO_CAJA = "RECUENTO CAJA";
	private final String APERTURA_CAJA = "APERTURA CAJA";
	public static final  String ACCION_CANCELAR = "ACCION_CANCELAR";

	public static final String DOTACION_CAJA = "X_POS.DOTACION_CAJA";

	protected SesionCaja cajaSesion;
	protected boolean esRecuentoCaja;
	protected boolean esAperturaCaja;

	protected BigDecimal importeRetirar;

	@Autowired
	private Sesion sesion;

	@Autowired
	protected MediosPagosService mediosPagosService;

	@Autowired
	protected VariablesServices variablesServices;
	
	@Autowired
	protected ClosingEndDayService closingEndDayService;

	protected Robot robot;

	@FXML
	private TextFieldImporte tfQuinientosEuros;
	@FXML
	private TextFieldImporte tfDoscientosEuros;
	@FXML
	private TextFieldImporte tfCienEuros;
	@FXML
	private TextFieldImporte tfCincuentaEuros;
	@FXML
	private TextFieldImporte tfVeinteEuros;
	@FXML
	private TextFieldImporte tfDiezEuros;
	@FXML
	private TextFieldImporte tfCincoEuros;
	@FXML
	private TextFieldImporte tfDosEuros;
	@FXML
	private TextFieldImporte tfUnEuro;
	@FXML
	private TextFieldImporte tfCincuentaCents;
	@FXML
	private TextFieldImporte tfVeinteCents;
	@FXML
	private TextFieldImporte tfDiezCents;
	@FXML
	private TextFieldImporte tfCincoCents;
	@FXML
	private TextFieldImporte tfDosCents;
	@FXML
	private TextFieldImporte tfUnCent;
	@FXML
	private Label lbTitle;
	@FXML
	private Label lbTienda;
	@FXML
	private Label lbCaja;
	@FXML
	private Label lbFecha;
	@FXML
	private Label lbVendedor;
	@FXML
	private Label lbTarjeta;
	@FXML
	private TextFieldImporte tfTarjeta;
	@FXML
	private Label lbTotalTarjetaManual;
	@FXML
	private TextFieldImporte tfTotalTarjetaManual;
	@FXML
	private Label lbVales;
	@FXML
	private TextFieldImporte tfVales;
	@FXML
	private TextFieldImporte tfPagoRemoto;
	@FXML
	private Label lbTotal;
	@FXML
	private TextFieldImporte tfTotal;
	@FXML
	protected TecladoNumerico tecladoNumerico;
	@FXML
	protected HBox hbTarjetaManual;
	@FXML
	protected HBox hbTarjeta;
	@FXML
	protected HBox hbVales;
	@FXML
	protected HBox hbPagoRemoto;
	@FXML
	protected HBox hbTransferencia;
	@FXML
	protected HBox hbFinanciacion;
	@FXML
	protected HBox hbGlovo;
	@FXML
	protected HBox hbUber;
	@FXML
	protected HBox hbFrakmenta;
	@FXML
	protected HBox hbPaygold;
	@FXML
	protected HBox hbGlobalPayments;
	@FXML
	protected HBox hBoxEfectivo;
	@FXML
	protected HBox hbTotalEfectivo;
	@FXML
	protected HBox hbTotal;
	@FXML
	protected Label lbPagoRemoto;
	@FXML
	protected Label lbTransferencia;
	@FXML
	protected TextFieldImporte tfTransferencia;
	@FXML
	protected Label lbFinanciacion;
	@FXML
	protected TextFieldImporte tfFinanciacion;
	@FXML
	protected Label lbGlovo;
	@FXML
	protected TextFieldImporte tfGlovo;
	@FXML
	protected Label lbUber;
	@FXML
	protected TextFieldImporte tfUber;
	@FXML
	protected Label lbFrakmenta;
	@FXML
	protected TextFieldImporte tfFrakmenta;
	@FXML
	protected Label lbPaygold;
	@FXML
	protected TextFieldImporte tfPaygold;
	@FXML
	protected Label lbGlobalPayments;
	@FXML
	protected TextFieldImporte tfGlobalPayments;

	@FXML
	protected Label lbTotalEfectivo;
	@FXML
	protected TextFieldImporte tfTotalEfectivo;

	@FXML
	protected Button btCancelar;

	private HashMap<BigDecimal, TextFieldImporte> listadoImportes;
	private boolean isInitTecladoNumerico;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		crearRobot();

		// Cargar cantidad billetes
		listadoImportes = new HashMap<>();
		listadoImportes.put(new BigDecimal(500).setScale(2, BigDecimal.ROUND_HALF_UP), tfQuinientosEuros);
		listadoImportes.put(new BigDecimal(200).setScale(2, BigDecimal.ROUND_HALF_UP), tfDoscientosEuros);
		listadoImportes.put(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_UP), tfCienEuros);
		listadoImportes.put(new BigDecimal(50).setScale(2, BigDecimal.ROUND_HALF_UP), tfCincuentaEuros);
		listadoImportes.put(new BigDecimal(20).setScale(2, BigDecimal.ROUND_HALF_UP), tfVeinteEuros);
		listadoImportes.put(new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_UP), tfDiezEuros);
		listadoImportes.put(new BigDecimal(5).setScale(2, BigDecimal.ROUND_HALF_UP), tfCincoEuros);

		// Cargar cantidad de monedas
		listadoImportes.put(new BigDecimal(2).setScale(2, BigDecimal.ROUND_HALF_UP), tfDosEuros);
		listadoImportes.put(new BigDecimal(1).setScale(2, BigDecimal.ROUND_HALF_UP), tfUnEuro);
		listadoImportes.put(new BigDecimal(0.50).setScale(2, BigDecimal.ROUND_HALF_UP), tfCincuentaCents);
		listadoImportes.put(new BigDecimal(0.20).setScale(2, BigDecimal.ROUND_HALF_UP), tfVeinteCents);
		listadoImportes.put(new BigDecimal(0.10).setScale(2, BigDecimal.ROUND_HALF_UP), tfDiezCents);
		listadoImportes.put(new BigDecimal(0.05).setScale(2, BigDecimal.ROUND_HALF_UP), tfCincoCents);
		listadoImportes.put(new BigDecimal(0.02).setScale(2, BigDecimal.ROUND_HALF_UP), tfDosCents);
		listadoImportes.put(new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_UP), tfUnCent);

		// Una vez cargados la lista de importes se añade el formato de los campos
		for (TextFieldImporte textField : listadoImportes.values()) {
			textField.setTextFormatter(IskaypetFormatter.getIntegerFormat(CANTIDAD_ZERO));
		}

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		initTecladoNumerico(tecladoNumerico);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {

		try {

			if (getDatos().containsKey(TITULO_CALCULADORA)) {
				esRecuentoCaja = getDatos().get(TITULO_CALCULADORA).equals(RECUENTO_CAJA);
				esAperturaCaja = getDatos().get(TITULO_CALCULADORA).equals(APERTURA_CAJA);
			} else {
				esRecuentoCaja = false;
				esAperturaCaja = false;
			}

			// inicializar sesion caja
			if (esAperturaCaja || esRecuentoCaja) {
				cajaSesion = sesion.getSesionCaja();
			}

			listadoImportes.forEach((clave, valor) -> valor.setTextFormatter(IskaypetFormatter.getIntegerFormatWithLimitNullable(999)));

			if (esRecuentoCaja) {
				cajaSesion.actualizarRecuentoCaja();
				hbTarjeta.setVisible(true);
				hbTarjetaManual.setVisible(true);
				hbVales.setVisible(true);
				hbPagoRemoto.setVisible(true);
				hbTransferencia.setVisible(true);
				hbFinanciacion.setVisible(true);
				hbGlovo.setVisible(true);
				hbUber.setVisible(true);
				hbFrakmenta.setVisible(true);
				hbPaygold.setVisible(true);
				hbGlobalPayments.setVisible(true);

				tfTotalEfectivo.setText(TOTAL_EFECTIVO.getDefaultValue());
				tfTotal.setText(TOTAL.getDefaultValue());

				// Se carga valores por defecto tarjeta (valor manual)
				tfTotalTarjetaManual.setText(TARJETA_GPRS.getDefaultValue());

				// Se cargan medios automaticos de recuento
				setPaymentDetails(TARJETA, lbTarjeta, tfTarjeta, hbTarjeta);
				setPaymentDetails(VALE_DEVOLUCION, lbVales, tfVales, hbVales);
				setPaymentDetails(PAGO_REMOTO, lbPagoRemoto, tfPagoRemoto, hbPagoRemoto);
				setPaymentDetails(TRANSFERENCIA, lbTransferencia, tfTransferencia, hbTransferencia);
				setPaymentDetails(FINANCIACION, lbFinanciacion, tfFinanciacion, hbFinanciacion);
				setPaymentDetails(GLOVO, lbGlovo, tfGlovo, hbGlovo);
				setPaymentDetails(UBER, lbUber, tfUber, hbUber);
				setPaymentDetails(FRAKMENTA, lbFrakmenta, tfFrakmenta, hbFrakmenta);
				setPaymentDetails(PAYGOLD, lbPaygold, tfPaygold, hbPaygold);
				setPaymentDetails(GLOBAL_PAYMENTS, lbGlobalPayments, tfGlobalPayments, hbGlobalPayments);
			} else {
				hbTarjetaManual.setVisible(false);
				removeElement(hbTotalEfectivo);
				removeElement(hbTarjeta);
				removeElement(hbVales);
				removeElement(hbPagoRemoto);
				removeElement(hbTransferencia);
				removeElement(hbFinanciacion);
				removeElement(hbGlovo);
				removeElement(hbUber);
				removeElement(hbFrakmenta);
				removeElement(hbPaygold);
				removeElement(hbGlobalPayments);
				hBoxEfectivo.setPrefHeight(500);
			}

			if (getDatos().containsKey(IMPORTE_RETIRAR)) {
				importeRetirar = (BigDecimal) getDatos().get(IMPORTE_RETIRAR);
				hbTarjetaManual.setVisible(true);
				lbTotalTarjetaManual.setText(I18N.getTexto("Importe a retirar"));
				tfTotalTarjetaManual.setText(FormatUtil.getInstance().formateaImporte(importeRetirar));
			}
			else {
				importeRetirar = null;
			}

			if (getDatos().containsKey(TITULO_CALCULADORA)) {
				lbTitle.setText(I18N.getTexto((String) getDatos().get(TITULO_CALCULADORA)));
			}
			else {
				lbTitle.setText(I18N.getTexto("Calculadora"));
			}

			lbTienda.setText(sesion.getAplicacion().getTienda().getCliente().getDesCliente());
			lbCaja.setText(sesion.getAplicacion().getCodCaja());
			lbFecha.setText(sesion.getSesionCaja().isCajaAbierta() ?
					FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura())
					: FormatUtil.getInstance().formateaFecha(new Date()));
			lbVendedor.setText(sesion.getSesionUsuario().getUsuario().getDesusuario());

			lbTotalEfectivo.setText(I18N.getTexto(TOTAL_EFECTIVO.getText()));
			tfTotalEfectivo.setEditable(false);
			tfTotalEfectivo.setDisable(true);

			lbTotal.setText(I18N.getTexto(TOTAL.getText()));
			tfTotal.setEditable(false);
			tfTotal.setDisable(true);

			// Se actualiza el total
			actualizarTotalEfectivo();
			actualizarTotal();

			// Visbilidad del botón cancelar
			comprobarVisualizacionBotonCancelar(getDatos());
		}
		catch (CajasServiceException cajasServiceException) {
			log.error("initializeForm() - Error de caja: " + cajasServiceException.getMessageI18N());
			throw new InitializeGuiException(cajasServiceException.getMessageI18N(), cajasServiceException);
		}
		catch (Exception e) {
			log.error("initializeForm() - Error inesperado inicializando formulario. ", e);
			throw new InitializeGuiException(e);
		}

	}


	private void comprobarVisualizacionBotonCancelar(HashMap<String, Object> datos) {
		// Visbilidad del botón cancelar
		if (esRecuentoCaja) {
			btCancelar.setVisible(false);
		} else if (datos.containsKey(IMPORTE_RETIRAR)) {
			if (datos.containsKey(AVISO_RETIRADA)) {
				btCancelar.setVisible((Boolean) getDatos().get(AVISO_RETIRADA));
			}
		} else {
			btCancelar.setVisible(true);
		}
	}

	private void setPaymentDetails(ContadoraTipoPagosEnum paymentType, Label label, TextFieldImporte textField, Node parent) {
		log.debug("setPaymentDetails() - paymentType: " + paymentType);
		try {

			label.setText(I18N.getTexto(paymentType.getText()));
			String paymentCode = paymentType.getCodPago();
			MedioPagoBean pagoBean = mediosPagosService.consultarMedioPago(paymentCode);

			// Paso 1: Comprobar si el medio de pago existe y está activo, en caso contrario se elimina
			if (pagoBean == null || !pagoBean.getActivo()) {
				log.warn("setPaymentDetails() - El medio de pago " + paymentCode + " no existe o no está activo");
				removeElement(parent);
				return;
			}

			// Paso 2: Comprobar si el medio de pago tiene recuento automático
			BigDecimal total = BigDecimal.ZERO;
			boolean editable = true;
			boolean disable = false;
			if (pagoBean.getRecuentoAutomaticoCaja()) {
				log.debug("setPaymentDetails() - El medio de pago " + paymentCode + " tiene recuento automático");
				// Paso 2.1: Comprobar si el medio de pago tiene recuentos en la caja abierta
				if (this.cajaSesion.getCajaAbierta().getAcumulados().containsKey(paymentCode)) {
					CajaLineaAcumuladoBean cajaLineaAcumuladoBean = this.cajaSesion.getCajaAbierta().getAcumulados().get(paymentCode);
					total = cajaLineaAcumuladoBean.getTotal();
				}

				// Paso 2.2: Si el recuento automatico total es cero se elimina el elemento, para no mostrarlo y se finaliza
				if (BigDecimalUtil.isIgualACero(total)) {
					log.warn("setPaymentDetails() - El recuento automático del medio de pago " + paymentCode + " es cero");
					removeElement(parent);
					return;
				}

				// Paso 2.3: Como es un medio de pago con recuento automático se deshabilita y no se puede editar
				editable = false;
				disable = true;

			}

			// Paso 3: Se muestra el total y se establece si es editable, si es recuento manual o automático
			setPaymentDetailsInfo(textField, total, editable, disable);
			log.debug("setPaymentDetails() - Total: " + total + ", editable: " + editable + ", disable: " + disable);

		} catch (Exception e) {
			removeElement(parent);
		}

	}

	private void setPaymentDetailsInfo(TextFieldImporte textField, BigDecimal total, Boolean editable, Boolean disable) {
		textField.setText(total.setScale(2, RoundingMode.HALF_UP).toString());
		textField.setEditable(editable);
		textField.setDisable(disable);
	}

	private void removeElement(Node node) {
		Parent parent = node.getParent();
		if (parent != null) {
			if (parent instanceof Pane) {
				((Pane) parent).getChildren().remove(node);
			}
			else if (parent instanceof Group) {
				((Group) parent).getChildren().remove(node);
			}
		}
	}

	@Override
	public void initializeFocus() {
		tfQuinientosEuros.requestFocus();
		tfQuinientosEuros.selectAll();
	}

	@SuppressWarnings("static-access")
	public void accionAceptar() {
		// Se comprueba si ya se ha generado el cierre de fin de día
		if (!closingEndDayService.comprobarCierreFinDia()) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(closingEndDayService.ERROR_CIERREZ_GENERADO_HOY), getStage());
			getStage().close();
			return;
		}
		
		try {

			BigDecimal saldo = calcularTotal();
			if (importeRetirar != null) {
				if (!BigDecimalUtil.isIgual(saldo, importeRetirar)) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe a retirar debe ser {0}€, por favor reviselo de nuevo", new Object[] { importeRetirar }), getStage());
					return;
				}
			}

			if (esAperturaCaja) {

				if (saldo.compareTo(this.variablesServices.getVariableAsBigDecimal(DOTACION_CAJA)) != 0) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe de la dotación para esta caja no es correcto, por favor reviselo de nuevo"), getStage());
					return;
				}
				cajaSesion.abrirCajaManual(new Date(), saldo);

			} else if (esRecuentoCaja) {

				// Se añade el recuento de efectivo
				addEfectivoPaymentToCajaSesion();

				// Se añaden los medios de pago con recuento manual
				addPaymentToCajaSesion(TARJETA_GPRS, tfTotalTarjetaManual);
                addPaymentToCajaSesion(FRAKMENTA, tfFrakmenta);

				cajaSesion.salvarRecuento();
				cajaSesion.actualizarRecuentoCaja();
			}

			getDatos().put(SALDO, saldo);
			getStage().close();
		}
		catch (CajasServiceException | CajaEstadoException e) {
			VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessageI18N(), e);
		}
	}

	private void addEfectivoPaymentToCajaSesion() {
		listadoImportes.forEach((key, cantidadField) -> {
			Integer cantidad = StringUtils.isNotBlank(cantidadField.getText()) ? Integer.parseInt(cantidadField.getText()) : 0;
			cajaSesion.nuevaLineaRecuento(EFECTIVO.getCodPago(), key, cantidad);
		});
	}

	private void addPaymentToCajaSesion(ContadoraTipoPagosEnum paymentType, TextFieldImporte textField) {
		// En caso de no ser un valor vacio no se añade
		if (textField == null || StringUtils.isBlank(textField.getText())) {
			return;
		}

		BigDecimal importe = new BigDecimal(textField.getText().replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_UP);
		if (importe.compareTo(BigDecimal.ZERO) != 0) {
			cajaSesion.nuevaLineaRecuento(paymentType.getCodPago(), importe, CANTIDAD_UNO);
		}
	}

	private BigDecimal calcularEfectivo() {

		List<BigDecimal> importes = new ArrayList<>();
		listadoImportes.forEach((key, cantidadField) -> {
			Integer cantidad = StringUtils.isNotBlank(cantidadField.getText()) ? Integer.parseInt(cantidadField.getText()) : 0;
			importes.add(key.multiply(new BigDecimal(cantidad).setScale(2, BigDecimal.ROUND_HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_UP));
		});

		BigDecimal saldo = importes.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		return saldo;
	}

	private BigDecimal calcularTotal() {

		// Se hacen calculos
		BigDecimal saldo = calcularEfectivo();

		if (esRecuentoCaja && cajaSesion.isCajaAbierta()) {

			// Sumar cada medio de pago
			if (tfTotalTarjetaManual != null && StringUtils.isNotBlank(tfTotalTarjetaManual.getText())) {
				saldo = saldo.add(new BigDecimal(tfTotalTarjetaManual.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

			if (tfTarjeta != null && StringUtils.isNotBlank(tfTarjeta.getText())) {
				saldo = saldo.add(new BigDecimal(tfTarjeta.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

			if (tfVales != null && StringUtils.isNotBlank(tfVales.getText())) {
				saldo = saldo.add(new BigDecimal(tfVales.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

			if (tfPagoRemoto != null && StringUtils.isNotBlank(tfPagoRemoto.getText())) {
				saldo = saldo.add(new BigDecimal(tfPagoRemoto.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

			if (tfTransferencia != null && StringUtils.isNotBlank(tfTransferencia.getText())) {
				saldo = saldo.add(new BigDecimal(tfTransferencia.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

			if (tfFinanciacion != null && StringUtils.isNotBlank(tfFinanciacion.getText())) {
				saldo = saldo.add(new BigDecimal(tfFinanciacion.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

			if(tfGlovo != null && StringUtils.isNotBlank(tfGlovo.getText())) {
				saldo = saldo.add(new BigDecimal(tfGlovo.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

			if(tfUber != null && StringUtils.isNotBlank(tfUber.getText())) {
				saldo = saldo.add(new BigDecimal(tfUber.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

			if(tfFrakmenta != null && StringUtils.isNotBlank(tfFrakmenta.getText())) {
				saldo = saldo.add(new BigDecimal(tfFrakmenta.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

			if(tfPaygold != null && StringUtils.isNotBlank(tfPaygold.getText())) {
				saldo = saldo.add(new BigDecimal(tfPaygold.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

			if(tfGlobalPayments != null && StringUtils.isNotBlank(tfGlobalPayments.getText())) {
				saldo = saldo.add(new BigDecimal(tfGlobalPayments.getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP));
			}

		}
		return saldo;
	}

	public void accionCancelar() {
		try {
			if (esRecuentoCaja) {
				cajaSesion.actualizarRecuentoCaja();
			}

			datos.put(ACCION_CANCELAR, true);

			getStage().close();
		}
		catch (CajasServiceException cajasServiceException) {
			VentanaDialogoComponent.crearVentanaError(getStage(), cajasServiceException.getMessageI18N(), cajasServiceException);
		}

	}

	public void crearRobot() {
		try {
			if (robot == null) {
				robot = new Robot();
			}
		}
		catch (AWTException e) {
			throw new RuntimeException(e);
		}
	}

	public void actionIfPressOK(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			if (robot != null) {
				robot.keyPress(java.awt.event.KeyEvent.VK_TAB);
				robot.keyRelease(java.awt.event.KeyEvent.VK_TAB);
			}
		}
		else if (event.getTarget().equals(tfQuinientosEuros) && !isInitTecladoNumerico) {
			tfQuinientosEuros.requestFocus();
			tfQuinientosEuros.selectAll();
			isInitTecladoNumerico = true;
		}
		actualizarTotalEfectivo();
		actualizarTotal();
	}

	private void actualizarTotalEfectivo() {
		BigDecimal decimal = calcularEfectivo();
		tfTotalEfectivo.setText(decimal.toString());
	}

	private void actualizarTotal() {
		BigDecimal decimal = calcularTotal();
		tfTotal.setText(decimal.toString());
	}

}
