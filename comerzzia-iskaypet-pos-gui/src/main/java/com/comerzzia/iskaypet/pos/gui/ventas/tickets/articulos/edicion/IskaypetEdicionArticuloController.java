package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.edicion;

import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.tipodocumento.TipoDocumentoNotFoundException;
import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.iskaypet.pos.gui.autorizacion.AutorizacionGerenteUtils;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaDto;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaTicketController;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaTicketView;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.masivo.MotivosAuditoriaMasivoController;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.motivos.CargarMotivosController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.inyectables.InyectableArticuloManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.inyectables.InyectableController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.inyectables.InyectableView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes.AsignarLoteController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes.AsignarLoteView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes.LoteArticuloManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.trazabilidad.seleccion.AsignarTrazabilidadController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.trazabilidad.seleccion.AsignarTrazabilidadView;
import com.comerzzia.iskaypet.pos.services.articulos.IskaypetArticulosService;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriaLineaTicket;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriasService;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.util.formatter.IskaypetFormatter;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.FormularioEdicionArticuloBean;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.*;

/**
 * GAPXX - BLOQUEO DE LINEAS EN VENTA CON CANTIDAD SUPERIOR A 1 GAP23 : CAMBIO MANUAL DEL PRECIO -
 * TIPIFICACIONES(AUDITORIAS) CON TEXTO DEFINIDO GAP 12 - ISK-8 GESTIÓN DE LOTES GAP97 ISK-238 - APLICACIÓN DE
 * PROMOCIONES EN ARTÍCULOS EDITADOS
 */
@Primary
@Component
public class IskaypetEdicionArticuloController extends EdicionArticuloController {

	private static final Logger log = Logger.getLogger(IskaypetEdicionArticuloController.class.getName());

	public static final String CLAVE_CANTIDAD_SELECCIONADA_TEXTFIELD = "cantidadSeleccionadaTextField";

	@FXML
	protected Button btModificarLote, btModificarIdAnimal, btModificarInyectable;

	@Autowired
	protected LoteArticuloManager loteArticuloManager;

	@Autowired
	private InyectableArticuloManager inyectableArticuloManager;

	protected MotivoAuditoriaDto motivo;

	protected Boolean cancelado;

	@Autowired
	private AuditoriasService auditoriasService;

	// Cifra escrita en el campo de cantidad, antes de que se setee a 1 para la limitación de líneas de Iskaypet
	private BigDecimal cantidadTotalAnadir = null;

	@Autowired
	private IskaypetFacturacionArticulosController iskaypetFacturacionArticulosController;

	@Autowired
	private Sesion sesion;

	private BigDecimal importeTotalConDtoOriginal;
	private BigDecimal precioTotalConDtoOriginal;

	private BigDecimal precioTotalSinDtoOriginal;

	private BigDecimal precioTotalTarifaOrigen;
	private BigDecimal cantidadOriginal;

	//GAP 172 TRAZABILIDAD ANIMALES
	@Autowired
	protected IskaypetArticulosService articulosService;

	protected IskaypetLineaTicket lineaTrazabilidad;

	@Override
	public void initializeForm() {
		super.initializeForm();

		cancelado = false;
		lineaTrazabilidad = null;

		// Ocultamos desplegable de selección de vendedor
		cbVendedor.getParent().setVisible(false);

		precioTotalSinDtoOriginal = lineaOriginal.getPrecioTotalSinDto();
		importeTotalConDtoOriginal = lineaOriginal.getImporteTotalConDto();
		precioTotalConDtoOriginal = FormatUtil.getInstance().desformateaBigDecimal(tfPrecioTotal.getText(), 2);
		precioTotalTarifaOrigen = linea.getPrecioTotalTarifaOrigen();
		cantidadOriginal = linea.getCantidad();

		// Se añaden valores por defecto (cambio entre ventanas, mantiene estado)
		tfCantidad.setEditable(true);
		tfCantidad.setDisable(false);

		double precioOriginal = FormatUtil.getInstance().desformateaBigDecimal(tfPrecio.getText(), 2).doubleValue() ;
		double descuentoOriginal = FormatUtil.getInstance().desformateaBigDecimal(tfDescuento.getText(), 2).doubleValue();

		tfCantidad.setTextFormatter(IskaypetFormatter.getIntegerFormat());
		tfPrecio.setTextFormatter(IskaypetFormatter.getDoubleFormat(4,2, precioOriginal != 0d ? precioOriginal : 0d));
		tfDescuento.setTextFormatter(IskaypetFormatter.getDoubleFormat(3,2, descuentoOriginal != 0d ? descuentoOriginal : 0d));

		if (cantidadOriginal.compareTo(BigDecimal.ZERO) < 0) {
			tfCantidad.setEditable(false);
			tfCantidad.setDisable(true);
		}

		// GAP 12 - ISK-8 GESTIÓN DE LOTES - Solo se habilita visibilidad de botón si es medicamento
		boolean esMedicamento;
		try {
			esMedicamento = loteArticuloManager.esArticuloMedicamento(linea.getArticulo().getCodArticulo(), ticketManager.getTicket().getCabecera().getDatosFidelizado(), true);
			btModificarLote.setVisible(esMedicamento);
			btModificarLote.setManaged(esMedicamento);
		}
		catch (LineaTicketException e) {
			log.error("initializeForm() - Error consultando si artículo es un medicamento [codArt:" + linea.getArticulo().getCodArticulo() + "]. Error: " + e.getMessage());
			accionCancelar();
			return;
		}


		//GAP 172 TRAZABILIDAD ANIMALES
		boolean esMascota = ((IskaypetLineaTicket)linea).isMascota() && ((IskaypetLineaTicket)linea).getDetallesTrazabilidad() != null;

		boolean esInyectable = inyectableArticuloManager.esArticuloInyectable(linea.getArticulo().getCodArticulo());
		btModificarInyectable.setVisible(esInyectable);
		btModificarInyectable.setManaged(esInyectable);


		btModificarIdAnimal.setVisible(esMascota);
		btModificarIdAnimal.setManaged(esMascota);
	}

	@Override
	public void accionCancelar() {
		setDefaultValuesLineaTicket();
		super.accionCancelar();
	}

	@Override
	public void accionAceptar() {
		log.debug("accionAceptar()");
		BigDecimal precio;

		// GAP23 : CAMBIO MANUAL DEL PRECIO - TIPIFICACIONES(AUDITORIAS) CON TEXTO DEFINIDO
		// Se muestra la edición autorizada si hay cambio de precio o descuento manual, o cambio de cantidades negativas
		if (primeraValidacionDatosEdicion() && edicionAutorizada()) {

			// ISK-249 GAP 101 Edición de cantidad en POS
			// Se guarda cantidad introducida y se setea el campo a 1 para los cálculos
			cantidadTotalAnadir = FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText(), 0);
			tfCantidad.setText(String.valueOf(1));

			try {
				precio = FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText(), 0);
				tfCantidad.setText(FormatUtil.getInstance().formateaNumero(precio, 0));
			}
			catch (Exception e) {
			}

			frEdicionArticulo.setCantidad(tfCantidad.getText());

			// GAP97 ISK-238 - Se aplica descuento a precio antes de los cálculos para respetar descuentos de promociones
			tfPrecio.setText(tfPrecioTotal.getText());
			tfDescuento.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ZERO, 2));
			// fin GAP97 ISK-238

			if (!bVentaProfesional) {
				try {
					precio = FormatUtil.getInstance().desformateaBigDecimal(tfPrecio.getText(), 2);
					tfPrecio.setText(FormatUtil.getInstance().formateaImporte(precio));
				}
				catch (Exception e) {
				}

				frEdicionArticulo.setPrecioFinalProfesional("0");
				frEdicionArticulo.setPrecioFinal(tfPrecio.getText());
			}
			else {
				try {
					precio = FormatUtil.getInstance().desformateaBigDecimal(tfPrecio.getText(), 2);
					tfPrecio.setText(FormatUtil.getInstance().formateaNumero(precio, 2));
				}
				catch (Exception e) {
				}

				frEdicionArticulo.setPrecioFinal("0");
				frEdicionArticulo.setPrecioFinalProfesional(tfPrecio.getText());
			}

			frEdicionArticulo.setDescuento(tfDescuento.getText());
			frEdicionArticulo.setVendedor(cbVendedor.getSelectionModel().getSelectedItem());
			frEdicionArticulo.setDesArticulo(tfDescripcion.getText());

			if (validarFormularioEdicionArticulo() && sonNumerosSerieValidos()) {
				BigDecimal nuevaCantidad = frEdicionArticulo.getCantidadAsBD();

				BigDecimal precioSinImpuestos = sesion.getImpuestos().getPrecioSinImpuestos(linea.getCodImpuesto(), linea.getPrecioTotalSinDto(),
				        ticketManager.getTicket().getCabecera().getCliente().getIdTratImpuestos());
				lineaOriginal.setPrecioSinDto(precioSinImpuestos);
				lineaOriginal.setPrecioTotalSinDto(linea.getPrecioTotalSinDto());
				lineaOriginal.setDescuentoManual(linea.getDescuentoManual());
				lineaOriginal.setCantidad(ticketManager.tratarSignoCantidad(nuevaCantidad, linea.getCabecera().getCodTipoDocumento()));
				lineaOriginal.setVendedor(frEdicionArticulo.getVendedor());
				lineaOriginal.setDesArticulo(frEdicionArticulo.getDesArticulo());
				lineaOriginal.setNumerosSerie(linea.getNumerosSerie());
				lineaOriginal.recalcularImporteFinal();
				if (aplicarPromociones) {
					ticketManager.recalcularConPromociones();
				}
				if (lineaOriginal.tieneCambioPrecioManual()) {
					cambioPrecioManual();
				}

				if (lineaOriginal.tieneDescuentoManual()) {
					cambioDescuentoManual();

				}

				// GAP23 : CAMBIO MANUAL DEL PRECIO - TIPIFICACIONES(AUDITORIAS) CON TEXTO DEFINIDO
				// Se genera auditoría si hay cambio de precio o descuento manual
				BigDecimal precioTotalActual = FormatUtil.getInstance().desformateaBigDecimal(tfPrecioTotal.getText(), 2);

				if (!BigDecimalUtil.isIgual(precioTotalConDtoOriginal, precioTotalActual)
				        || (!BigDecimalUtil.isIgual(precioTotalTarifaOrigen, precioTotalActual) && cantidadTotalAnadir.intValue() > 1)) {
					Integer cantidad = cantidadTotalAnadir.intValue();

					if (BigDecimalUtil.isIgual(precioTotalConDtoOriginal, precioTotalActual)
					        && (!BigDecimalUtil.isIgual(precioTotalTarifaOrigen, precioTotalActual) && cantidadTotalAnadir.intValue() > 1)) {
						cantidad--;
					}

					List<AuditoriaLineaTicket> listaAuditoriasLineas = auditoriaEdicionPrecio(precioTotalConDtoOriginal, new BigDecimal(cantidad));
					if (listaAuditoriasLineas == null) {
						setDefaultValuesLineaTicket();
						return;
					}
					else if (!listaAuditoriasLineas.isEmpty()) {
						getDatos().put(IskaypetFacturacionArticulosController.PARAM_LISTA_AUDIT_EDICION_PRECIO, listaAuditoriasLineas);
					}

				}

				// GAP23 : CAMBIO MANUAL DEL PRECIO - TIPIFICACIONES(AUDITORIAS) CON TEXTO DEFINIDO
				// Se genera auditoría de devolución si se cambia la cantidad de positivo a negativo, o de negativo a negativo (porque
				// cambiaría el importe)
				if ((!BigDecimalUtil.isIgual(cantidadOriginal, nuevaCantidad) && cantidadOriginal.intValue() < 0 && nuevaCantidad.intValue() < 0)
				        || (cantidadOriginal.intValue() > 0 && nuevaCantidad.intValue() < 0)) {
					crearAuditoriaCantidadNegativa((IskaypetLineaTicket) lineaOriginal, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION, importeTotalConDtoOriginal);
				}

				// GAP 12 - ISK-8 GESTIÓN DE LOTES
				((IskaypetLineaTicket) lineaOriginal).setLote(((IskaypetLineaTicket) linea).getLote());

				// Se inserta el inyectable
				((IskaypetLineaTicket) lineaOriginal).setInyectable(((IskaypetLineaTicket) linea).getInyectable());

				// ISK-249 GAP 101 Edición de cantidad en POS
				// Se devuelve cantidad seleccionada originalmente a pantalla ventas o devoluciones
				getDatos().put(CLAVE_CANTIDAD_SELECCIONADA_TEXTFIELD, cantidadTotalAnadir);

				// ACTUALIZAR TRAZABILIDAD ANIMALES
				if (lineaTrazabilidad != null) {
					actualizarLineaTrazabilidad(lineaTrazabilidad);
				}

				getStage().close();
			}
		}
	}

	public void setDefaultValuesLineaTicket() {
		IskaypetLineaTicket iskLinea = (IskaypetLineaTicket) ticketManager.getTicket().getLinea(lineaOriginal.getIdLinea());
		BigDecimal precioSinImpuestos = sesion.getImpuestos().getPrecioSinImpuestos(iskLinea.getCodImpuesto(), precioTotalSinDtoOriginal,
		        ticketManager.getTicket().getCabecera().getCliente().getIdTratImpuestos());
		iskLinea.setPrecioSinDto(precioSinImpuestos);
		iskLinea.setPrecioTotalSinDto(precioTotalSinDtoOriginal);
		iskLinea.recalcularImporteFinal();
		if (aplicarPromociones) {
			ticketManager.recalcularConPromociones();
		}
		if (iskLinea.tieneCambioPrecioManual()) {
			cambioPrecioManual();
		}
		if (iskLinea.tieneDescuentoManual()) {
			cambioDescuentoManual();

		}
	}

	/*
	 * #####################################################################################################################
	 * ######
	 */
	/*
	 * #################### GAP23 : CAMBIO MANUAL DEL PRECIO - TIPIFICACIONES(AUDITORIAS) CON TEXTO DEFINIDO
	 * #####################
	 */
	/*
	 * #####################################################################################################################
	 * ######
	 */

	public boolean edicionAutorizada() {
		BigDecimal precioTotalActual = FormatUtil.getInstance().desformateaBigDecimal(tfPrecioTotal.getText(), 2);
		BigDecimal nuevaCantidad = FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText(), 0);
		BigDecimal cantidadTotalAnadir = FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText(), 0);
		try {
			if ((!BigDecimalUtil.isIgual(precioTotalConDtoOriginal, precioTotalActual))
			        || ((!BigDecimalUtil.isIgual(cantidadOriginal, nuevaCantidad) && cantidadOriginal.intValue() < 0 && nuevaCantidad.intValue() < 0)
			                || (cantidadOriginal.intValue() > 0 && nuevaCantidad.intValue() < 0))
			        || (!BigDecimalUtil.isIgual(precioTotalTarifaOrigen, precioTotalActual) && cantidadTotalAnadir.intValue() > 1)) {
				HashMap<String, Object> datos = new HashMap<>();
				datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
				AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
			}
			return true;
		}
		catch (InitializeGuiException e) {
			if (e.isMostrarError()) {
				log.error("edicionAutorizada() - Ha habido un error al autorizar el cambio de precio: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al autorizar el cambio de precio. Contacte con un administrador."), e);
			}
		}
		catch (Exception e) {
			log.error("edicionAutorizada() - Ha habido un error al autorizar el cambio de precio: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al autorizar el cambio de precio. Contacte con un administrador."), e);
		}
		return false;
	}

	/**
	 * Este método se utiliza para generar auditorías de edición de precio en base a la cantidad total y el precio original
	 * proporcionados. Inicialmente, se obtiene la acción seleccionada a través del método getAccionSeleccionada. Si la
	 * acción seleccionada es nula, el método devuelve null. Si la acción seleccionada no es nula, se obtiene la línea de
	 * ticket correspondiente y se verifica la acción seleccionada. Si la acción seleccionada es ACCION_MOTIVOS_TOTAL, se
	 * llama al método accionMotivosTotal. Si la acción seleccionada es ACCION_MOTIVOS_UNIDAD, se llama al método
	 * accionMotivosUnidad. Después de ejecutar la acción correspondiente, se actualiza el uidAuditLineaOriginal si no es
	 * nulo.
	 *
	 * @param precioConDtoLineaOriginal
	 *            El precio original con descuento de la línea de ticket.
	 * @param cantidadTotalAnadir
	 *            La cantidad total a añadir a la línea de ticket.
	 * @return Una lista de objetos AuditoriaLineaTicket que representan las auditorías generadas. Devuelve null si la
	 *         acción seleccionada es nula.
	 */
	private List<AuditoriaLineaTicket> auditoriaEdicionPrecio(BigDecimal precioConDtoLineaOriginal, BigDecimal cantidadTotalAnadir) {
		List<AuditoriaLineaTicket> listaAuditorias = new ArrayList<>();
		String uidAuditLineaOriginal = "";

		String titulo = I18N.getTexto("¿Cómo desea introducir el motivo del cambio de precio a las {0} unidades?", cantidadTotalAnadir);
		String accionSeleccionada = MotivosAuditoriaMasivoController.getAccionSeleccionada(cantidadTotalAnadir, titulo, getApplication(), getStage());
		if (accionSeleccionada == null) {
			return null;
		}

		IskaypetLineaTicket iskLinea = (IskaypetLineaTicket) ticketManager.getTicket().getLinea(lineaOriginal.getIdLinea());
		if (StringUtils.isNotBlank(accionSeleccionada)) {
			if (accionSeleccionada.equals(MotivosAuditoriaMasivoController.ACCION_MOTIVOS_TOTAL)) {
				uidAuditLineaOriginal = accionMotivosTotal(precioConDtoLineaOriginal, cantidadTotalAnadir, iskLinea, listaAuditorias);
			}
			else if (accionSeleccionada.equals(MotivosAuditoriaMasivoController.ACCION_MOTIVOS_UNIDAD)) {
				uidAuditLineaOriginal = accionMotivosUnidad(precioConDtoLineaOriginal, cantidadTotalAnadir, iskLinea, listaAuditorias);
			}
		}

		if (StringUtils.isBlank(uidAuditLineaOriginal)) {
			iskLinea.setAuditorias(null);
			return null;
		}

		updateUidAuditLineaOriginal(iskLinea, uidAuditLineaOriginal);
		return listaAuditorias;
	}

	/**
	 * Este método se utiliza para obtener la acción seleccionada en base a la cantidad total proporcionada. Inicialmente,
	 * la acción seleccionada se establece en ACCION_MOTIVOS_UNIDAD de la clase MotivosAuditoriaMasivoController. Si la
	 * cantidad total es mayor que 1, se crea un HashMap para almacenar los datos y se muestra una vista modal centrada. Si
	 * la operación es cancelada (según el valor de CANCELADO en los datos), el método devuelve null. Si la operación no es
	 * cancelada, se obtiene la acción seleccionada de los datos y se devuelve.
	 *
	 * @param cantidadTotalAnadir
	 *            La cantidad total a añadir. Se utiliza para determinar si se debe mostrar la vista modal.
	 * @return La acción seleccionada. Puede ser ACCION_MOTIVOS_UNIDAD o la acción obtenida de los datos de la vista modal.
	 *         Devuelve null si la operación es cancelada.
	 */

	private String accionMotivosTotal(BigDecimal precioConDtoLineaOriginal, BigDecimal cantidadTotalAnadir, IskaypetLineaTicket iskLinea, List<AuditoriaLineaTicket> listaAuditorias) {
		String uidAuditLineaOriginal = "";
		getMotivoAuditoria(AuditoriasService.TIPO_AUDITORIA_CAMBIOPRECIO, 0, BigDecimal.ONE, Boolean.TRUE);

		if (cancelado) {
			return null;
		}

		AuditoriaDto auditoria = crearAuditoriaCambioPrecio(iskLinea, AuditoriasService.TIPO_AUDITORIA_CAMBIOPRECIO, precioConDtoLineaOriginal, Boolean.FALSE);
		String uidAuditoria = auditoria.getUidAuditoria();
		AuditoriaLineaTicket auditoriaLinea = auditoriasService.addAuditoriaLinea(iskLinea, AuditoriasService.TIPO_AUDITORIA_CAMBIOPRECIO, uidAuditoria, auditoria.getCodMotivo());
		if (auditoriaLinea != null) {
			auditoriasService.guardarAuditoria(auditoria);
			uidAuditLineaOriginal = auditoriaLinea.getUidAuditoria();
			for (int i = 1; i < cantidadTotalAnadir.abs().intValue(); i++) {
				try {
					AuditoriaDto newAuditoria = auditoria.clone();
					newAuditoria.setUidAuditoria(UUID.randomUUID().toString());
					auditoriasService.guardarAuditoria(newAuditoria);
					auditoriaLinea = auditoriasService.addAuditoriaLinea(iskLinea, AuditoriasService.TIPO_AUDITORIA_CAMBIOPRECIO, newAuditoria.getUidAuditoria(), newAuditoria.getCodMotivo());
					listaAuditorias.add(auditoriaLinea);
				}
				catch (Exception e) {
					log.error("auditoriaEdicionPrecio() - Error al crear auditoría de cambio de precio: " + e.getMessage(), e);
				}
			}
		}
		return uidAuditLineaOriginal;
	}

	private String accionMotivosUnidad(BigDecimal precioConDtoLineaOriginal, BigDecimal cantidadTotalAnadir, IskaypetLineaTicket iskLinea, List<AuditoriaLineaTicket> listaAuditorias) {
		String uidAuditLineaOriginal = "";
		for (int i = 0; i < cantidadTotalAnadir.abs().intValue(); i++) {
			log.debug("auditoriaEdicionPrecio() - Creando y guardando auditoria");
			log.info("auditoriaEdicionPrecio() - Creando y guardando auditoria");
			getMotivoAuditoria(AuditoriasService.TIPO_AUDITORIA_CAMBIOPRECIO, i, cantidadTotalAnadir, Boolean.TRUE);
			if (cancelado) {
				return null;
			}

			AuditoriaDto auditoria = crearAuditoriaCambioPrecio(iskLinea, AuditoriasService.TIPO_AUDITORIA_CAMBIOPRECIO, precioConDtoLineaOriginal, Boolean.TRUE);
			String uidAuditoria = auditoria.getUidAuditoria();

			// GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
			AuditoriaLineaTicket auditoriaLinea = auditoriasService.addAuditoriaLinea(iskLinea, AuditoriasService.TIPO_AUDITORIA_CAMBIOPRECIO, uidAuditoria, auditoria.getCodMotivo());

			if (i == 0) {
				uidAuditLineaOriginal = auditoriaLinea.getUidAuditoria();
			}
			// Le decimos que i > 0 porque la auditoría ya está guardada en la linea original, sólo queremos recoger las auditorias
			// de las nuevas líneas
			if (auditoriaLinea != null && i > 0) {
				listaAuditorias.add(auditoriaLinea);
			}
		}
		return uidAuditLineaOriginal;
	}

	private void updateUidAuditLineaOriginal(IskaypetLineaTicket iskLinea, String uidAuditLineaOriginal) {
		for (AuditoriaLineaTicket auditoriasLineaOriginal : iskLinea.getAuditorias()) {
			if (auditoriasLineaOriginal.getTipo().equals(AuditoriasService.TIPO_AUDITORIA_CAMBIOPRECIO)) {
				auditoriasLineaOriginal.setUidAuditoria(uidAuditLineaOriginal);
			}
		}
	}

	public AuditoriaDto crearAuditoriaCambioPrecio(IskaypetLineaTicket linea, String tipoDoc, BigDecimal precioConDtoLineaOriginal, Boolean guardar) {

		AuditoriaDto auditoria = iskaypetFacturacionArticulosController.setearDatosAuditoria(null, precioConDtoLineaOriginal, linea);
		auditoria.setCodMotivo(motivo.getCodigo().toString());
		auditoria.setObservaciones(motivo.getDescripcion());

		try {
			auditoria = auditoriasService.generarAuditoria(auditoria, tipoDoc, null, guardar);

			log.debug("crearAuditoriaCambioPrecio() - Auditoría generada con uid: " + auditoria.getUidAuditoria());
		}
		catch (ContadorServiceException e) {
			log.error("crearAuditoriaCambioPrecio() - error al crear la auditoria de eliminacion de linea " + e.getMessage(), e);
		}
		catch (EmpresaException e) {
			log.error("crearAuditoriaCambioPrecio() - Empresa no encontrada " + e.getMessage(), e);
		}
		catch (TipoDocumentoNotFoundException e) {
			log.error("crearAuditoriaCambioPrecio() - Tipo de documento no encontrado " + e.getMessage(), e);
		}
		catch (DocumentoException e) {
			log.error("crearAuditoriaCambioPrecio() - error al recuperar el tipo de documento " + e.getMessage(), e);
		}
		return auditoria;
	}

	public void crearAuditoriaCantidadNegativa(IskaypetLineaTicket linea, String tipoDoc, BigDecimal importeTotalConDtoOriginal) {

		getMotivoAuditoria(AuditoriasService.TIPO_AUDITORIA_DEVOLUCION);
		AuditoriaDto auditoria = iskaypetFacturacionArticulosController.setearDatosAuditoria(importeTotalConDtoOriginal, null, linea);
		auditoria.setCodMotivo(motivo.getCodigo().toString());
		auditoria.setObservaciones(motivo.getDescripcion());

		try {
			auditoriasService.generarAuditoria(auditoria, tipoDoc, null, Boolean.TRUE);
		}
		catch (ContadorServiceException e) {
			log.error("crearAuditoriaCantidadNegativa() - error al crear la auditoria de eliminacion de linea " + e.getMessage(), e);
		}
		catch (EmpresaException e) {
			log.error("crearAuditoriaCantidadNegativa() - Empresa no encontrada " + e.getMessage(), e);
		}
		catch (TipoDocumentoNotFoundException e) {
			log.error("crearAuditoriaCantidadNegativa() - Tipo de documento no encontrado " + e.getMessage(), e);
		}
		catch (DocumentoException e) {
			log.error("crearAuditoriaCantidadNegativa() - error al recuperar el tipo de documento " + e.getMessage(), e);
		}
	}

	/**
	 * Si se llama a este método se mostrará el contador de auditorías, donde el primer número es el indice+1 y el segundo
	 * el número de artículos nuevos que se van a añadir, es decir, tfCantidad-1
	 */
	public void getMotivoAuditoria(String tipoAuditoria, Integer indice, BigDecimal cantidadTotalAnadir, Boolean permiteCancelar) {
		getDatos().put(IskaypetFacturacionArticulosController.TIPO_DOCUMENTO_ENVIADO, tipoAuditoria);
		getDatos().put(IskaypetFacturacionArticulosController.LINEA_ENVIADA, ticketManager.getTicket().getLinea(lineaOriginal.getIdLinea()));

		if (indice != null && cantidadTotalAnadir != null && cantidadTotalAnadir.intValue() > 1) {
			getDatos().put(MotivoAuditoriaTicketController.CLAVE_NUMERO_TOTAL_ARTICULOS, cantidadTotalAnadir.intValue());
			getDatos().put(MotivoAuditoriaTicketController.CLAVE_NUMERO_INDICE_ARTICULO, indice);
		}

		if (permiteCancelar) {
			getDatos().put(MotivoAuditoriaTicketController.ACTIVAR_CANCELAR, true);
		}

		getApplication().getMainView().showModalCentered(MotivoAuditoriaTicketView.class, getDatos(), getStage());
		motivo = (MotivoAuditoriaDto) getDatos().get(CargarMotivosController.MOTIVO);
		cancelado = (Boolean) getDatos().getOrDefault(MotivoAuditoriaTicketController.CANCELAR, false);
	}

	public void getMotivoAuditoria(String tipoAuditoria) {
		getMotivoAuditoria(tipoAuditoria, null, cantidadTotalAnadir, Boolean.FALSE);
	}

	protected boolean primeraValidacionDatosEdicion(){
		if(tfPrecio.getText().isEmpty()){
			tfPrecio.setText("0,00");
		}
		if(tfCantidad.getText().isEmpty()){
			tfCantidad.setText("1");
		}
		if(tfDescuento.getText().isEmpty()){
			tfDescuento.setText("0,00");
		}

		int cantidad = FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText(), 0).intValueExact();
		double descuento = FormatUtil.getInstance().desformateaBigDecimal(tfDescuento.getText(), 2).doubleValue();

		if(cantidad <= 0 || cantidad > 50){
			lbError.setText(I18N.getTexto(I18N.getTexto("La cantidad debe ser mayor que {0} y menor que {1}", 0, 50)));
			tfCantidad.requestFocus();
			return false;
		}

		if(descuento < 0 || descuento > 100){
			lbError.setText(I18N.getTexto(I18N.getTexto("El descuento debe ser un valor entre {0} y {1}", 0, 100)));
			tfDescuento.requestFocus();
			return false;
		}

		return true;
	}

	@Override
	protected boolean validarFormularioEdicionArticulo() {
		// Limpiamos los errores que pudiese tener el formulario
		frEdicionArticulo.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbError.setText("");

		if (StringUtils.isBlank(frEdicionArticulo.getDesArticulo())) {
			lbError.setText(I18N.getTexto("La descripción no debe estar vacía"));
			return false;
		}

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioEdicionArticuloBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frEdicionArticulo);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioEdicionArticuloBean> next = constraintViolations.iterator().next();
			frEdicionArticulo.setErrorStyle(next.getPropertyPath(), true);
			frEdicionArticulo.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			return false;
		}

		if(cantidadTotalAnadir.intValue() > 50){
			lbError.setText(I18N.getTexto(I18N.getTexto("La cantidad debe ser menor que {0}", 50)));
			tfCantidad.requestFocus();
			return false;
		}

		if (BigDecimalUtil.isMenorACero(frEdicionArticulo.getCantidadAsBD())) {
			// GAP 63 Se comprueba permiso en devoluciones, no se permite cantidad negativa en ventas
			if (ticketManager.getTicketOrigen() == null) {
				lbError.setText(I18N.getTexto("No tiene permisos para realizar una devolución"));
				return false;
			}
		}

		BigDecimal max = new BigDecimal(10000000);
		if (BigDecimalUtil.isMayorOrIgual(frEdicionArticulo.getCantidadAsBD(), max)) {
			lbError.setText(I18N.getTexto(I18N.getTexto("La cantidad debe ser menor que {0}", FormatUtil.getInstance().formateaNumero(max))));
			tfCantidad.requestFocus();
			return false;
		}
		if (BigDecimalUtil.isIgualACero(frEdicionArticulo.getCantidadAsBD())) {
			lbError.setText(I18N.getTexto(I18N.getTexto("La cantidad debe ser distinta de cero.")));
			tfCantidad.requestFocus();
			return false;
		}
		max = new BigDecimal(10000000000L);
		if (BigDecimalUtil.isMayorOrIgual(FormatUtil.getInstance().desformateaImporte(tfPrecio.getText()), max)) {
			lbError.setText(I18N.getTexto(I18N.getTexto("El campo debe ser menor que {0}", FormatUtil.getInstance().formateaNumero(max))));
			tfPrecio.requestFocus();
			return false;
		}
		if (BigDecimalUtil.isMayorOrIgual(FormatUtil.getInstance().desformateaImporte(tfPrecioTotal.getText()), max)) {
			lbError.setText(I18N.getTexto(I18N.getTexto("El campo debe ser menor que {0}", FormatUtil.getInstance().formateaNumero(max))));
			tfPrecioTotal.requestFocus();
			return false;
		}

		max = new BigDecimal(10000000000L);
		if (BigDecimalUtil.isMayorOrIgual(FormatUtil.getInstance().desformateaImporte(tfPrecio.getText()), max)) {
			lbError.setText(I18N.getTexto(I18N.getTexto("El campo debe ser menor que {0}", FormatUtil.getInstance().formateaNumero(max))));
			tfPrecio.requestFocus();
			return false;
		}
		if (BigDecimalUtil.isMayorOrIgual(FormatUtil.getInstance().desformateaImporte(tfPrecioTotal.getText()), max)) {
			lbError.setText(I18N.getTexto(I18N.getTexto("El campo debe ser menor que {0}", FormatUtil.getInstance().formateaNumero(max))));
			tfPrecioTotal.requestFocus();
			return false;
		}

		if (BigDecimalUtil.isMenorACero(frEdicionArticulo.getDescuentoAsBD()) || BigDecimalUtil.isMayor(frEdicionArticulo.getDescuentoAsBD(), new BigDecimal(100))) {
			lbError.setText(I18N.getTexto("El descuento debe ser un valor entre 0 y 100."));
			tfDescuento.requestFocus();
			return false;
		}

		// control de venta a precio cero
		if (BigDecimalUtil.isIgualACero(FormatUtil.getInstance().desformateaBigDecimal(tfPrecio.getText(), 2))) {

			if (permiteVentaPrecioCero) {
				boolean vender = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Desea vender el artículo a precio 0?"), getStage());
				if (!vender) {
					tfPrecio.requestFocus();
					tfPrecio.selectAll();
					return false;
				}
			}
			else {
				lbError.setText(I18N.getTexto("No está permitida la venta a precio 0."));
				if (BigDecimalUtil.isIgual(FormatUtil.getInstance().desformateaBigDecimal(tfDescuento.getText()), new BigDecimal("100"))) {
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							tfDescuento.requestFocus();
							tfDescuento.selectAll();
						}
					});
				}
				else {
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							tfPrecio.requestFocus();
							tfPrecio.selectAll();
						}
					});
				}
				return false;
			}
		}

		return true;
	}

	// GAP 12 - ISK-8 GESTIÓN DE LOTES
	@FXML
	public void modificarLote() {
		HashMap<String, Object> parametrosModificarLote = new HashMap<>();
		parametrosModificarLote.put(AsignarLoteController.CLAVE_PARAMETRO_LISTA_LINEAS, Arrays.asList(linea));
		parametrosModificarLote.put(AsignarLoteController.CLAVE_PARAMETRO_MODIFICAR_LOTE, true);
		parametrosModificarLote.put(AsignarLoteController.CLAVE_TICKET_MANAGER, ticketManager);
		getApplication().getMainView().showModalCentered(AsignarLoteView.class, parametrosModificarLote, this.getStage());
	}

	@FXML
	public void modificarIdAnimal() {
		HashMap<String, Object> parametrosModificarLote = new HashMap<>();
		//Convertimos la linea a una lista por que en la introduccion se barajan multiples articulos y aqui solo uno
		parametrosModificarLote.put(AsignarTrazabilidadController.CLAVE_PARAMETRO_LISTA_LINEAS, Arrays.asList(linea));
		parametrosModificarLote.put(AsignarTrazabilidadController.CLAVE_TICKET_MANAGER, ticketManager);
		getApplication().getMainView().showModalCentered(AsignarTrazabilidadView.class, parametrosModificarLote, this.getStage());

		if (parametrosModificarLote.containsKey(AsignarTrazabilidadController.CLAVE_PARAMETRO_LINEA_ACTUALIZAR)) {
			IskaypetLineaTicket lineaActualizar = (IskaypetLineaTicket) parametrosModificarLote.get(AsignarTrazabilidadController.CLAVE_PARAMETRO_LINEA_ACTUALIZAR);
			if (lineaActualizar != null) {
				lineaTrazabilidad = lineaActualizar;
			}
		}
	}

	private void actualizarLineaTrazabilidad(IskaypetLineaTicket lineaActualizar) {
		if (lineaOriginal instanceof IskaypetLineaTicket) {
			((IskaypetLineaTicket) lineaOriginal).setDetallesTrazabilidad(lineaActualizar.getDetallesTrazabilidad());
			((IskaypetLineaTicket) lineaOriginal).setItemsPetIdent(lineaActualizar.getItemsPetIdent());
			((IskaypetLineaTicket) lineaOriginal).setContratoAnimal(lineaActualizar.getContratoAnimal());
		}
	}


	/**
	 * Método que se encarga de abrir la ventana de modificación de inyectables.
	 */
	@FXML
	public void modificarInyectable() {
		HashMap<String, Object> parametros = new HashMap<>();
		parametros.put(InyectableController.CLAVE_PARAMETRO_LISTA_LINEAS, Arrays.asList(linea));
		parametros.put(InyectableController.CLAVE_PARAMETRO_MODIFICAR_INYECTABLE, true);
		parametros.put(InyectableController.CLAVE_TICKET_MANAGER, ticketManager);
		getApplication().getMainView().showModalCentered(InyectableView.class, parametros, this.getStage());
	}

}
