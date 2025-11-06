package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.edicion;

import static com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController.PERMISO_DEVOLUCIONES;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.persistence.motivos.MotivosCambioPrecio;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.core.documentos.propiedades.RestriccionesException;
import com.comerzzia.dinosol.pos.services.core.documentos.propiedades.RestriccionesService;
import com.comerzzia.dinosol.pos.services.motivos.MotivosCambioPrecioServices;
import com.comerzzia.dinosol.pos.services.tarjetasregalo.TarjetasRegaloService;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.dinosol.pos.services.ticket.lineas.DinoLineaTicket;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.FormularioEdicionArticuloBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.profesional.LineaTicketProfesional;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

@Component
@Primary
public class DinoEdicionArticuloController extends EdicionArticuloController {

	private static final Logger log = Logger.getLogger(DinoEdicionArticuloController.class.getName());
	private static final String PERMISO_RESTRICCIONES = "V SIN RESTRIC";

	@Autowired
	protected Sesion sesion;

	@Autowired
	private RestriccionesService restriccionesService;	

	@Autowired
	private MotivosCambioPrecioServices motivosServices;
	
	@Autowired
	private TarjetasRegaloService tarjetasRegaloService;

	private BigDecimal precioOriginal;

	@FXML
	ComboBox<MotivosCambioPrecio> cbMotivos;

	protected ObservableList<MotivosCambioPrecio> motivos;

	/**
	 * Comprobamos el permiso para saber si usar las restricciones o no.
	 * 
	 * @return venderSinRestricciones
	 */
	private Boolean comprobarPermisoRestricciones() {
		Boolean venderSinRestricciones = true;
		try {
			compruebaPermisos(PERMISO_RESTRICCIONES);
		}
		catch (SinPermisosException e) {
			log.debug("El usuario no tiene permiso para vender sin restricción.");
			venderSinRestricciones = false;
		}
		return venderSinRestricciones;
	}

	protected boolean validarFormularioEdicionArticulo() {

		// Limpiamos los errores que pudiese tener el formulario
		frEdicionArticulo.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbError.setText("");

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioEdicionArticuloBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frEdicionArticulo);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioEdicionArticuloBean> next = constraintViolations.iterator().next();
			frEdicionArticulo.setErrorStyle(next.getPropertyPath(), true);
			frEdicionArticulo.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			return false;
		}

		if (BigDecimalUtil.isMenorACero(frEdicionArticulo.getCantidadAsBD())) {
			try {
				super.compruebaPermisos(PERMISO_DEVOLUCIONES);
			}
			catch (SinPermisosException e) {
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
		if (BigDecimalUtil.isIgualACero(FormatUtil.getInstance().desformateaBigDecimal(tfPrecio.getText(), 4))) {
			if (linea.getGenerico()) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Precio 0 no permitido"), getStage());
				tfPrecio.requestFocus();
				tfPrecio.selectAll();
				return false;
			}
			else if (permiteVentaPrecioCero) {
				boolean vender = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Desea vender el artículo a precio 0?"), getStage());
				if (!vender) {
					tfPrecio.requestFocus();
					tfPrecio.selectAll();
					return false;
				}
			}
			else {
				lbError.setText(I18N.getTexto("No está permitida la venta a precio 0."));
				if (BigDecimalUtil.isIgual(FormatUtil.getInstance().desformateaBigDecimal(tfDescuento.getText()), new BigDecimal(100d))) {
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

		Boolean venderSinRestricciones = comprobarPermisoRestricciones();
		if (!venderSinRestricciones) {
			/* Cargamos los datos en la linea que enviamos al validador */
			linea.setCantidad(FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText()));
			linea.setPrecioSinDto(FormatUtil.getInstance().desformateaBigDecimal(tfImporte.getText()));
			linea.setDescuentoManual(FormatUtil.getInstance().desformateaBigDecimal(tfDescuento.getText()));

			String mensajeError = null;
			try {
				mensajeError = restriccionesService.validadorLineas(linea);
			}
			catch (RestriccionesException e) {
				log.error("validarFormularioEdicionArticulo() - " + e.getMessage());
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto(e.getMessage()), getStage());
			}

			if (mensajeError == null) {
				return true;
			}
			else {
				log.info("validarFormularioEdicionArticulo() - " + mensajeError);
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(mensajeError), getStage());
				return false;
			}
		}
		else {
			return true;
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	@Override
	public void initializeForm() {
		ticketManager = (TicketManager) getDatos().get(FacturacionArticulosController.TICKET_KEY);
		getDatos().remove(CLAVE_CANCELADO);
		LineaTicket lineaSeleccionada = null;

		habilitarComponentes(false);
		
		// Recuperamos el artículo a editar que nos llega como parámetro
		lineaSeleccionada = (LineaTicket) this.getDatos().get(CLAVE_PARAMETRO_ARTICULO);

		aplicarPromociones = true;
		if (this.getDatos().get(CLAVE_APLICAR_PROMOCIONES) != null) {
			aplicarPromociones = (Boolean) this.getDatos().get(CLAVE_APLICAR_PROMOCIONES);
		}

		bVentaProfesional = lineaSeleccionada instanceof LineaTicketProfesional;

		linea = lineaSeleccionada.clone();

		precioOriginal = linea.getPrecioTotalSinDto();

		setLinea(lineaSeleccionada);

		// Seteamos los componentes
		if (!puedeEditarPrecio && !lineaSeleccionada.getArticulo().getGenerico()) {
			tfPrecio.setEditable(false);
		}
		else {
			tfPrecio.setEditable(true);
			frEdicionArticulo.setFormField("precioTotal", tfPrecio);
		}

		tfPrecioTotal.setEditable(false);
		if (!puedeEditarDescuentos) {
			log.debug("inicializarComponentes() - No se editan descuentos");
			lbDescuento.setVisible(false);
			tfDescuento.setVisible(false);
		}
		else {
			tfDescuento.setEditable(true);
			frEdicionArticulo.setFormField("descuento", tfDescuento);
		}

		if (!desglose1) {
			log.debug("inicializarComponentes() - No hay desglose 1");
			fpLineaDesglose1.setVisible(false);
			fpLineaDesglose2.setVisible(false);
		}
		else {
			lbDesglose1.setText(I18N.getTexto(textoDesglose1));
		}
		if (!desglose2) {
			log.debug("inicializarComponentes() - No hay desglose 2");
			fpLineaDesglose2.setVisible(false);
			fpLineaDesglose2.setVisible(false);
		}
		else {
			lbDesglose2.setText(I18N.getTexto(textoDesglose2));
		}
		if (linea.getGenerico()) {
			tfDescripcion.getStyleClass().remove("solo-lectura");
			tfDescripcion.setEditable(true);
			tfDescripcion.setFocusTraversable(true);
		}
		else {
			tfDescripcion.getStyleClass().add("solo-lectura");
			tfDescripcion.setEditable(false);
			tfDescripcion.setFocusTraversable(false);
		}

		motivos = FXCollections.observableArrayList();

		loadMotivos();

		cbMotivos.setItems(motivos);
		
		try {
			String codMotivoOriginal = ((DinoLineaTicket) lineaOriginal).getCodMotivo();
			if(codMotivoOriginal != null) {
				MotivosCambioPrecio motivo = motivosServices.consultarPorCodigo(codMotivoOriginal);
				cbMotivos.setValue(motivo);
				if ("N".equals(motivo.getVisibleEnEdicion())) {
					habilitarComponentes(true);
				}
				
			}
			else {
				cbMotivos.setValue(null);
			}
		}catch(Exception e) {
			log.error("initializeForm() - Error: se ha producido un error consultando el mótivo original de la línea.",e);
			VentanaDialogoComponent.crearVentanaError("Se ha producido un error consultando el mótivo original de la línea.", getStage());

		}

		frEdicionArticulo.setFormField("desArticulo", tfDescripcion);

		calculaImporte();

		btNumerosSerie.setDisable(false);
		// Mostramos u ocultamos el botón de números de serie
		if (linea.getArticulo().getNumerosSerie() && !panelLotesSerie.getChildren().contains(btNumerosSerie)) {
			panelLotesSerie.getChildren().add(btNumerosSerie);
		}
		else if (!linea.getArticulo().getNumerosSerie() && panelLotesSerie.getChildren().contains(btNumerosSerie)) {
			panelLotesSerie.getChildren().remove(btNumerosSerie);
		}
		
		desactivarEdicionCampoDescripcion();
	}

	@FXML
	public void accionAceptar() {
		log.debug("accionAceptar()");
		BigDecimal precio;

		try {
			precio = FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText(), 3);
			tfCantidad.setText(FormatUtil.getInstance().formateaNumero(precio, 3));
		}
		catch (Exception e) {
		}
		
		frEdicionArticulo.setCantidad(tfCantidad.getText());

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
				precio = FormatUtil.getInstance().desformateaBigDecimal(tfPrecio.getText(), 4);
				tfPrecio.setText(FormatUtil.getInstance().formateaNumero(precio, 4));
			}
			catch (Exception e) {
			}

			frEdicionArticulo.setPrecioFinal("0");
			frEdicionArticulo.setPrecioFinalProfesional(tfPrecio.getText());
		}

		frEdicionArticulo.setDescuento(tfDescuento.getText());
		frEdicionArticulo.setDesArticulo(tfDescripcion.getText());
				
		if (!linea.getPrecioTotalSinDto().equals(lineaOriginal.getPrecioTotalSinDto())) {
			if (tarjetasRegaloService.getTipoTarjeta(linea.getCodArticulo()) == null) {
				if (cbMotivos.getSelectionModel().getSelectedItem() == null) {
					VentanaDialogoComponent.crearVentanaAviso("Seleccione un motivo por el cuál ha modificado el precio.", getStage());
					return;
				}
				else {
					String codMotivo = cbMotivos.getSelectionModel().getSelectedItem().getCodMotivo();
					((DinoLineaTicket) lineaOriginal).setCodMotivo(codMotivo);
				}
			}
			if (validarFormularioEdicionArticulo() && sonNumerosSerieValidos()) {
				BigDecimal nuevaCantidad = frEdicionArticulo.getCantidadAsBD();
				BigDecimal precioSinImpuestos = sesion.getImpuestos().getPrecioSinImpuestos(linea.getCodImpuesto(), linea.getPrecioTotalSinDto(), ticketManager.getTicket().getIdTratImpuestos());
				lineaOriginal.setPrecioSinDto(precioSinImpuestos);
				lineaOriginal.setPrecioTotalSinDto(linea.getPrecioTotalSinDto());
				lineaOriginal.setDescuentoManual(linea.getDescuentoManual());
				lineaOriginal.setCantidad(ticketManager.tratarSignoCantidad(nuevaCantidad, linea.getCabecera().getCodTipoDocumento()));
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
				getStage().close();
			}

		}
		else {
			if (validarFormularioEdicionArticulo() && sonNumerosSerieValidos()) {
				try {
					if (tarjetasRegaloService.getTipoTarjeta(linea.getCodArticulo()) == null) {
						if (cbMotivos.getSelectionModel().getSelectedItem() == null) {
							VentanaDialogoComponent.crearVentanaAviso("Seleccione un motivo por el cuál ha modificado el precio.", getStage());
							return;
						}

						String codMotivo = cbMotivos.getSelectionModel().getSelectedItem().getCodMotivo();
						((DinoLineaTicket) lineaOriginal).setCodMotivo(codMotivo);
					}
				}
				catch (Exception e) {
					log.error("accionAceptar() - Ha ocurrido un error al comprobar la tarjeta regalo");
					VentanaDialogoComponent.crearVentanaError("Ha ocurrido un error al comprobar la tarjeta regalo", getStage());
				}
				
				BigDecimal nuevaCantidad = frEdicionArticulo.getCantidadAsBD();
				BigDecimal precioSinImpuestos = sesion.getImpuestos().getPrecioSinImpuestos(linea.getCodImpuesto(), linea.getPrecioTotalSinDto(), ticketManager.getTicket().getIdTratImpuestos());
				lineaOriginal.setPrecioSinDto(precioSinImpuestos);
				lineaOriginal.setPrecioTotalSinDto(linea.getPrecioTotalSinDto());
				lineaOriginal.setDescuentoManual(linea.getDescuentoManual());
				lineaOriginal.setCantidad(ticketManager.tratarSignoCantidad(nuevaCantidad, linea.getCabecera().getCodTipoDocumento()));
				/* Si la linea tiene un idLineaAsociado distinto de 0, significa que tiene un artículo plástico asociado al que hay que cambiarle la cantidad también */
				Integer idLineaAsociado = ((DinoLineaTicket) lineaOriginal).getIdLineaAsociado();
				if(idLineaAsociado != null && idLineaAsociado != 0) {
					((DinoLineaTicket) ticketManager.getTicket().getLinea(idLineaAsociado)).setCantidad(lineaOriginal.getCantidad());
				}
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
				getStage().close();
			}
		}
	}

	@Override
	public void initializeComponents() {
		super.initializeComponents();

		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setPintarPiePantalla(true);
		tfCantidad.setUserData(keyboardDataDto);
		tfDescuento.setUserData(keyboardDataDto);
		tfImporte.setUserData(keyboardDataDto);
		tfPrecio.setUserData(keyboardDataDto);
		tfPrecioTotal.setUserData(keyboardDataDto);

		addSeleccionarTodoEnFoco(tfCantidad);
		addSeleccionarTodoEnFoco(tfPrecio);
		addSeleccionarTodoEnFoco(tfDescuento);
		addSeleccionarTodoEnFoco(tfPrecioTotal);
		addSeleccionarTodoEnFoco(tfImporte);
	}

	@Override
	public void initializeFocus() {
		super.initializeFocus();
		if (linea.getArticulo().getGenerico() || linea.getGenerico()) {
			tfPrecio.requestFocus();
			tfCantidad.setEditable(false);
			tfCantidad.setFocusTraversable(false);
			tfCantidad.getStyleClass().add("solo-lectura");
			tfDescuento.setEditable(false);
			tfDescuento.setFocusTraversable(false);
			tfDescuento.getStyleClass().add("solo-lectura");
		}
		else {
			tfCantidad.setEditable(true);
			tfCantidad.setFocusTraversable(true);
			tfCantidad.getStyleClass().remove("solo-lectura");
			tfDescuento.setEditable(true);
			tfDescuento.setFocusTraversable(true);
			tfDescuento.getStyleClass().remove("solo-lectura");
		}
	}

	@Override
	protected void cambioDescuentoManual() {
		super.cambioDescuentoManual();

		if(!linea.getGenerico()) {
			AuditoriaDto auditoria = new AuditoriaDto();
			auditoria.setTipo("EDICIÓN PRECIO");
			auditoria.setUidTicket(ticketManager.getTicket().getUidTicket());
			auditoria.setCodart(linea.getCodArticulo());
			auditoria.setPrecioAnterior(precioOriginal);
			auditoria.setPrecioNuevo(lineaOriginal.getImporteTotalConDto().divide(lineaOriginal.getCantidad(), 2, RoundingMode.HALF_UP));
			auditoria.setCantidadLinea(linea.getCantidad());
			auditoria.setFecha(new Date());
			
			MotivosCambioPrecio motivo = cbMotivos.getSelectionModel().getSelectedItem();
			if(motivo != null) {
				auditoria.setCodMotivo(motivo.getCodMotivo());
				auditoria.setDesMotivo(motivo.getDesMotivo());
			}
			
			String usuario = sesion.getSesionUsuario().getUsuario().getUsuario();
			auditoria.setCajeroOperacion(usuario);
			
			((DinoTicketVentaAbono) ticketManager.getTicket()).addAuditoriaNoRegistrada(auditoria);
		}
	}

	@Override
	protected void cambioPrecioManual() {
		super.cambioPrecioManual();

		if(!linea.getGenerico()) {
			AuditoriaDto auditoria = new AuditoriaDto();
			auditoria.setTipo("EDICIÓN PRECIO");
			auditoria.setUidTicket(ticketManager.getTicket().getUidTicket());
			auditoria.setCodart(linea.getCodArticulo());
			auditoria.setPrecioAnterior(precioOriginal);
			auditoria.setPrecioNuevo(lineaOriginal.getPrecioTotalConDto());
			auditoria.setCantidadLinea(linea.getCantidad());
			auditoria.setFecha(new Date());
			
			String usuario = sesion.getSesionUsuario().getUsuario().getUsuario();
			auditoria.setCajeroOperacion(usuario);
			
			MotivosCambioPrecio motivo = cbMotivos.getSelectionModel().getSelectedItem();
			if(motivo != null) {
				auditoria.setCodMotivo(motivo.getCodMotivo());
				auditoria.setDesMotivo(motivo.getDesMotivo());
			}

			((DinoTicketVentaAbono) ticketManager.getTicket()).addAuditoriaNoRegistrada(auditoria);
		}
	}

	protected void loadMotivos() {
		try {
			motivos.clear();
			List<MotivosCambioPrecio> lstMotivos = motivosServices.consultar();
			if (lstMotivos.isEmpty()) {
				// Añadimos elemento vacío
				motivos.add(new MotivosCambioPrecio());
			}
			else {
				List<MotivosCambioPrecio> lstMotivosVisibles = new ArrayList<>();
				for (MotivosCambioPrecio motivo : lstMotivos) {
					if ("S".equals(motivo.getVisibleEnEdicion())) {
						lstMotivosVisibles.add(motivo);
					}
				}
				motivos.addAll(lstMotivosVisibles);
			}
		}
		catch (Exception e) {
			log.error("loadMotivos() - Se produjo un error al cargar los motivos de cambio de precio: " + e.getMessage(), e);
		}
	}

	private void habilitarComponentes(boolean opcion) {
		cbMotivos.setDisable(opcion);
		tfPrecio.setDisable(opcion);
		tfCantidad.setDisable(opcion);
		tfDescuento.setDisable(opcion);
	}

	private void desactivarEdicionCampoDescripcion() {
		tfDescripcion.getStyleClass().add("solo-lectura");
		tfDescripcion.setEditable(false);
		tfDescripcion.setFocusTraversable(false);
	}
}
