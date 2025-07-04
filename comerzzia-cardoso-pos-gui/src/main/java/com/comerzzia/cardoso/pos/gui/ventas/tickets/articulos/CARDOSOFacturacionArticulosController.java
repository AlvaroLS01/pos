package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.devices.dispositivo.tarjeta.worldline.TefWorldlineManager;
import com.comerzzia.cardoso.pos.gui.promociones.monograficas.PromocionesEspeciales;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.CARDOSOTicketManager;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.autorizaracciones.AutorizarAccionesController;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.autorizaracciones.AutorizarAccionesView;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.consultartotales.ConsultaTotalesController;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.consultartotales.ConsultaTotalesView;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.consultastock.ConsultaStockView;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.CardosoLotesView;
import com.comerzzia.cardoso.pos.persistence.articulos.CardosoArticuloBean;
import com.comerzzia.cardoso.pos.persistence.lotes.anexa.CardosoLoteArticuloBean;
import com.comerzzia.cardoso.pos.services.lotes.CardosoLoteService;
import com.comerzzia.cardoso.pos.services.pagos.worldline.ConsultaTotalesBean;
import com.comerzzia.cardoso.pos.services.pagos.worldline.WorldlineService;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CardosoDatosDevolucionBean;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CardosoLote;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoCallback;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuarioController;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.cajas.CajaRetiradaEfectivoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.payments.PaymentsManager;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.lineas.ILineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
@Primary
public class CARDOSOFacturacionArticulosController extends FacturacionArticulosController {

	private static final Logger log = Logger.getLogger(CARDOSOFacturacionArticulosController.class.getName());

	@Autowired
	private Sesion sesion;
	@Autowired
	private CardosoLoteService loteArticuloService;

	@Autowired
	private PromocionesEspeciales promocionesEspecialesServicio;

	@Autowired
	protected WorldlineService worldlineService;
	@Autowired
	protected CopiaSeguridadTicketService copiaSeguridadTicketService;
	@Autowired
	protected PaymentsManager paymentsManager;
	@Autowired
	protected PaymentsMethodsConfiguration paymentsMethodsConfiguration;

	@Override
	public void initializeManager() throws SesionInitException {
		ticketManager = SpringContext.getBean(CARDOSOTicketManager.class);
		ticketManager.init();
	}

	@Override
	public synchronized LineaTicket insertarLineaVenta(String arg0, String arg1, String arg2, BigDecimal arg3)
	        throws LineaTicketException, PromocionesServiceException, DocumentoException, CajasServiceException, CajaRetiradaEfectivoException {
		CARDOSOLineaTicket linea = (CARDOSOLineaTicket) super.insertarLineaVenta(arg0, arg1, arg2, arg3);

		/*
		 * GAP - PERSONALIZACIONES V3 - DESCUENTO TARIFA Guardamos los datos de precio de la tarifa origen en estos dos
		 * campos para poder recuperarlos luego al aplicar los datos de las promociones
		 */
		log.debug("insertarLineaVenta() : GAP - PERSONALIZACIONES V3 - DESCUENTO TARIFA");
		linea.setBackupPrecioTarifaOrigen(linea.getPrecioTarifaOrigen());
		linea.setBackupPrecioTotalTarifaOrigen(linea.getPrecioTotalTarifaOrigen());

		/* GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS */
		promocionesEspecialesServicio.setPromocionesMonograficasLineaTicket((CARDOSOLineaTicket) linea);

		/* PROMOCIÓN MONOGRÁFICA */
		if (linea.getPromocionMonograficaAplicada() != null) {
			promocionesEspecialesServicio.procesarDescuentosPromocionesMonograficas((CARDOSOTicketManager) ticketManager);
		}

		return linea;
	}

	/**
	 * ######################################################################################## GAP - PERSONALIZACIONES
	 * V3 - CONSULTA DE STOCK Pedimos autorización para poder negar o eliminar una linea y para cancelar la venta.
	 */

	public void abrirConsultaStock() {
		log.debug("abrirConsultaStock() : GAP - PERSONALIZACIONES V3 -  CONSULTA DE STOCK");

		getApplication().getMainView().showModalCentered(ConsultaStockView.class, getDatos(), getStage());
	}

	/**
	 * ######################################################################################## GAP - CAJERO AUXILIAR
	 * Pedimos autorización para poder negar o eliminar una linea y para cancelar la venta.
	 */

	@Override
	protected void accionNegarRegistroTabla() {
		if (((CARDOSOTicketManager) ticketManager).necesitaAutorizacion()) {

			log.debug("accionNegarRegistroTabla() : GAP - CAJERO AUXILIAR");

			LineaTicketGui lineaSeleccionada = getLineaSeleccionada();
			if (lineaSeleccionada != null) {
				int idLinea = lineaSeleccionada.getIdLinea();
				ILineaTicket linea = ticketManager.getTicket().getLinea(idLinea);
				if (linea.getCantidad().compareTo(BigDecimal.ZERO) > 0) {
					getApplication().getMainView().showModalCentered(AutorizarAccionesView.class, getDatos(), getStage());
					if (((String) getDatos().get(AutorizarAccionesController.sDocumento) != null) && ((String) getDatos().get(AutorizarAccionesController.sTienda) != null)) {
						UsuarioBean cajeroAux = new UsuarioBean();
						CardosoDatosDevolucionBean datosDevolucion = new CardosoDatosDevolucionBean();

						cajeroAux.setUsuario((String) getDatos().get(AutorizarAccionesController.sUsuario));
						cajeroAux.setDesusuario((String) getDatos().get(AutorizarAccionesController.sNombre));

						datosDevolucion.setTicketDevolucion((String) getDatos().get(AutorizarAccionesController.sDocumento));
						datosDevolucion.setTiendaDevolucion((String) getDatos().get(AutorizarAccionesController.sTienda));

						((CARDOSOLineaTicket) linea).setCajeroAux(cajeroAux);
						((CARDOSOLineaTicket) linea).setDatosDevolucion(datosDevolucion);
					}
					else {
						return;
					}
				}
				else {
					((CARDOSOLineaTicket) linea).setCajeroAux(null);
					((CARDOSOLineaTicket) linea).setDatosDevolucion(null);
				}
				super.accionNegarRegistroTabla();
			}
		}
		else {
			super.accionNegarRegistroTabla();
		}
	}

	@Override
	protected void accionTablaEliminarRegistro() {
		if (((CARDOSOTicketManager) ticketManager).necesitaAutorizacion()) {

			log.debug("accionTablaEliminarRegistro() : GAP - CAJERO AUXILIAR");

			try {
				if (!tbLineas.getItems().isEmpty() && getLineaSeleccionada() != null) {
					super.compruebaPermisos(PERMISO_BORRAR_LINEA);
					LineaTicketGui selectedItem = getLineaSeleccionada();
					if (selectedItem.isCupon()) {
						ticketManager.recalcularConPromociones();
						refrescarDatosPantalla();
					}
					else {
						int idLinea = getLineaSeleccionada().getIdLinea();
						ILineaTicket linea = ticketManager.getTicket().getLinea(idLinea);
						ILineaTicket lastLineMemory = null;
						if (linea.isEditable()) {
							boolean confirmar = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar esta línea del ticket?"), getStage());
							if (!confirmar) {
								return;
							}
							getDatos().put(AutorizarAccionesController.PANTALLA_ANULACION, true);
							getApplication().getMainView().showModalCentered(AutorizarAccionesView.class, getDatos(), getStage());
							if (getDatos().containsKey(AutorizarAccionesController.sUsuario)) {
								if (ticketManager.getTicket().getLineas().size() == 1) {
									lastLineMemory = ((LineaTicket) ticketManager.getTicket().getLineas().get(0)).clone();
								}
								ticketManager.eliminarLineaArticulo(idLinea);

								int ultimArticulo = ticketManager.getTicket().getLineas().size();
								if (ultimArticulo > 0) {
									LineaTicket ultimaLinea = (LineaTicket) ticketManager.getTicket().getLineas().get(ultimArticulo - 1);
									escribirLineaEnVisor(ultimaLinea);
								}
								else {
									visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
								}

								guardarCopiaSeguridad();
								seleccionarSiguienteLinea();
								refrescarDatosPantalla();

								if (ticketManager.getTicket().getLineas().size() > 0) {
									visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
								}
								else {
									visor.modoEspera();
								}
							}
						}
						else {
							VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea seleccionada no se puede modificar."), this.getStage());
						}
						if (ticketManager.getTicket().getIdTicket() != null && ticketManager.getTicket().getLineas().isEmpty()) {
							if (lastLineMemory != null) {
								ticketManager.getTicket().getLineas().add(lastLineMemory);
							}
							ticketManager.salvarTicketVacio();
							try {
								ticketManager.eliminarTicketCompleto();
							}
							catch (Exception e) {
								log.error("Ha ocurrido un error al eliminar el ticket ", e);
							}
						}
					}
				}
			}
			catch (SinPermisosException ex) {
				log.debug("accionTablaEliminarRegistro() - El usuario no tiene permisos para eliminar línea");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para borrar una línea"), getStage());
			}
		}
		else {
			super.accionTablaEliminarRegistro();
		}
	}

	@Override
	public void cancelarVenta() {
		if (((CARDOSOTicketManager) ticketManager).necesitaAutorizacion()) {

			log.debug("cancelarVenta() : GAP - CAJERO AUXILIAR");

			try {
				boolean confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar todas las líneas del ticket?"), getStage());
				if (!confirmacion) {
					return;
				}
				boolean autorizadaCancelacion = true;
				if (!((TicketVenta) ticketManager.getTicket()).getLineas().isEmpty()) {
					getDatos().put(AutorizarAccionesController.PANTALLA_ANULACION, true);
					getApplication().getMainView().showModalCentered(AutorizarAccionesView.class, getDatos(), getStage());
					autorizadaCancelacion = getDatos().containsKey(AutorizarAccionesController.sUsuario);
				}
				if (autorizadaCancelacion) {
					if (ticketManager.getTicket().getIdTicket() != null) {
						ticketManager.salvarTicketVacio();
					}
					ticketManager.eliminarTicketCompleto();
					tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
					refrescarDatosPantalla();
					initializeFocus();
					tbLineas.getSelectionModel().clearSelection();
					visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
					visor.modoEspera();
				}
			}
			catch (TicketsServiceException | PromocionesServiceException | DocumentoException ex) {
				log.error("accionAnularTicket() - Error inicializando nuevo ticket: " + ex.getMessage(), ex);
				VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageI18N(), ex);
			}
		}
		else {
			super.cancelarVenta();
		}
	}

	@Override
	public void abrirPagos() {
		if (validarTicket()) {
			calcularNumeroArticulosPeligrosos();
			super.abrirPagos();
		}
	}

	/**
	 * ######################################################################################## GAP - PERSONALIZACIONES
	 * V3 - LOTES Personalizamos los métodos necesarios para que al introducir una linea que tenga el dato de lote a
	 * 'S', pidamos la asignación del lote a dicha linea. Excluidas las lineas procedentes de balanza.
	 */

	public Boolean validarTicket() {
		log.debug("validarTicket() : GAP - PERSONALIZACIONES V3 - LOTES");

		boolean valido = true;
		for (Object obj : ticketManager.getTicket().getLineas()) {
			LineaTicket linea = (LineaTicket) obj;
			CardosoArticuloBean articulo = (CardosoArticuloBean) linea.getArticulo();
			if (articulo.getAtributosAdicionalesArticulo() != null && articulo.getAtributosAdicionalesArticulo().getLote()) {
				try {
					/*
					 * GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA No debemos asignar lotes si viene desde
					 * una linea de balanza.
					 */
					if (StringUtils.isBlank(((CARDOSOLineaTicket) linea).getUidTicketBalanza())) {
						if (quedanLotesPorAsignar((CARDOSOLineaTicket) linea)) {
							CardosoLoteArticuloBean loteBD = loteArticuloService.getLoteArticulo(sesion.getAplicacion().getUidActividad(), articulo.getCodArticulo());
							if (loteBD != null && StringUtils.isNotBlank(loteBD.getLote())) {
								CardosoLote lote = new CardosoLote();
								lote.setId(loteBD.getLote());
								lote.setCantidad(linea.getCantidad());
								List<CardosoLote> listaLote = new ArrayList<CardosoLote>();
								listaLote.add(lote);
								((CARDOSOLineaTicket) linea).setLotes(listaLote);
							}
							else {
								VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Quedan lotes por asignar. Asígnelos antes de seguir."), getStage());
								getDatos().put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
								getApplication().getMainView().showModalCentered(CardosoLotesView.class, getDatos(), getStage());
								linea = (LineaTicket) getDatos().get(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO);
								valido = false;
							}
						}
					}
				}
				catch (Exception e) {
					VentanaDialogoComponent.crearVentanaError(getStage(), "Error al validar el ticket. Por favor, contacte con el administrador.", e);
				}
			}
		}
		return valido;
	}

	/**
	 * Realiza la comprobación de que todos los lotes están asignados correctamente.
	 * 
	 * @param linea
	 * @return Boolean
	 */
	public Boolean quedanLotesPorAsignar(CARDOSOLineaTicket linea) {
		log.debug("quedanLotesPorAsignar() : GAP - PERSONALIZACIONES V3 - LOTES");

		BigDecimal cantidadActual = BigDecimal.ZERO;
		if (linea.getLotes() != null && !linea.getLotes().isEmpty()) {
			for (CardosoLote lote : linea.getLotes()) {
				cantidadActual = cantidadActual.add(lote.getCantidad());
			}
		}
		return cantidadActual.doubleValue() < ((LineaTicket) linea).getCantidad().doubleValue();
	}

	/**
	 * ######################################################################################## GAP - PERSONALIZACIONES
	 * V3 - ARTÍCULOS PELIGROSOS Se debe completar un dato en el ticket que es el número de artículos peligrosos.
	 * Etiqueta en xml : peligroso
	 */
	public void calcularNumeroArticulosPeligrosos() {
		log.debug("calcularNumeroArticulosPeligrosos() : GAP - PERSONALIZACIONES V3 - ARTÍCULOS PELIGROSOS");

		Integer nPeligrosos = 0;
		for (LineaTicket linea : (List<CARDOSOLineaTicket>) ticketManager.getTicket().getLineas()) {
			CardosoArticuloBean articulo = (CardosoArticuloBean) linea.getArticulo();
			if (articulo.getAtributosAdicionalesArticulo() != null && articulo.getAtributosAdicionalesArticulo().getPeligroso().equals("S")) {
				nPeligrosos++;
			}
		}
		((CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera()).setNumArticulosPeligrosos(nPeligrosos);
	}

	@Override
	public void abrirBusquedaArticulos() {
		log.debug("abrirBusquedaArticulos() : GAP - PERSONALIZACIONES V3 - LOTES");

		frValidacionBusqueda.setCantidad(tfCantidadIntro.getText());
		if (!accionValidarFormularioBusqueda()) {
			return;
		}

		datos = new HashMap<>();
		getDatos().put(BuscarArticulosController.PARAMETRO_ENTRADA_CLIENTE, ticketManager.getTicket().getCliente());
		getDatos().put(BuscarArticulosController.PARAMETRO_ENTRADA_CODTARIFA, ticketManager.getTarifaDefault());
		getDatos().put(BuscarArticulosController.PARAM_MODAL, Boolean.TRUE);
		getDatos().put(SeleccionUsuarioController.PARAMETRO_ES_STOCK, Boolean.FALSE);
		abrirVentanaBusquedaArticulos();
		initializeFocus();
		if (datos.containsKey(BuscarArticulosController.PARAMETRO_SALIDA_CODART)) {
			String codArt = (String) getDatos().get(BuscarArticulosController.PARAMETRO_SALIDA_CODART);
			String desglose1 = (String) getDatos().get(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE1);
			String desglose2 = (String) getDatos().get(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE2);
			frValidacionBusqueda.setCantidad(tfCantidadIntro.getText());
			try {
				if (accionValidarFormularioBusqueda()) {
					LineaTicket linea = null;
					if (ticketManager.comprobarTarjetaRegalo(codArt)) {
						insertarLineaVenta(codArt, desglose1, desglose2, BigDecimal.ONE);
						recargarGiftcard();
					}
					else {
						linea = insertarLineaVenta(codArt, desglose1, desglose2, frValidacionBusqueda.getCantidadAsBigDecimal());
						comprobarArticuloGenerico(linea);
						if (linea.getGenerico()) {
							HashMap<String, Object> parametrosEdicionArticulo = new HashMap<>();
							parametrosEdicionArticulo.put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
							parametrosEdicionArticulo.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
							abrirVentanaEdicionArticulo(parametrosEdicionArticulo);
							if (parametrosEdicionArticulo.containsKey(EdicionArticuloController.CLAVE_CANCELADO)) {
								throw new LineaInsertadaNoPermitidaException(linea);
							}
						}
						comprobarLineaPrecioCero(linea);
						asignarNumerosSerie(linea);
						ticketManager.recalcularConPromociones();
					}
					tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));

					/* GAP - PERSONALIZACIONES V3 - LOTES */
					if (linea != null) {
						if (((CardosoArticuloBean) linea.getArticulo()).getAtributosAdicionalesArticulo() != null
						        && ((CardosoArticuloBean) linea.getArticulo()).getAtributosAdicionalesArticulo().getLote()) {
							CardosoLoteArticuloBean loteBD;
							try {
								loteBD = loteArticuloService.getLoteArticulo(sesion.getAplicacion().getUidActividad(), linea.getArticulo().getCodArticulo());
								if (loteBD != null && StringUtils.isNotBlank(loteBD.getLote())) {
									CardosoLote lote = new CardosoLote();
									lote.setId(loteBD.getLote());
									lote.setCantidad(linea.getCantidad());
									List<CardosoLote> listaLote = new ArrayList<CardosoLote>();
									listaLote.add(lote);
									((CARDOSOLineaTicket) linea).setLotes(listaLote);
								}
								else {
									getDatos().put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
									getApplication().getMainView().showModalCentered(CardosoLotesView.class, getDatos(), getStage());
									linea = (LineaTicket) getDatos().get(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO);
								}
							}
							catch (Exception e) {
								log.error("insertarLineaVenta() - Error al consultar el lote del artículo: " + e.getMessage());
								getDatos().put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
								getApplication().getMainView().showModalCentered(CardosoLotesView.class, getDatos(), getStage());
								linea = (LineaTicket) getDatos().get(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO);
							}
						}
					}

					refrescarDatosPantalla();
					if (ticketManager.getTicket().getLineas().size() > 0) {
						tbLineas.getSelectionModel().select(0);
						int ultimArticulo = ticketManager.getTicket().getLineas().size();
						LineaTicket lineaNueva = (LineaTicket) ticketManager.getTicket().getLineas().get(ultimArticulo - 1);
						escribirLineaEnVisor(lineaNueva);
						visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
					}
				}
			}
			catch (LineaTicketException ex) {
				log.error("abrirBusquedaArticulos() - ACCION BUSQUEDA ARTICULOS - Error registrando línea de ticket");
				VentanaDialogoComponent.crearVentanaError(ex.getLocalizedMessage(), this.getScene().getWindow());
			}
			catch (TarjetaRegaloException e) {
				log.error(e);
				VentanaDialogoComponent.crearVentanaError(e.getMessageI18N(), getStage());
			}
			catch (LineaInsertadaNoPermitidaException e) {
				ticketManager.getTicket().getLineas().remove(e.getLinea());
				guardarCopiaSeguridad();
				if (e.getMessage() != null) {
					VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
				}
			}
			catch (CajaRetiradaEfectivoException e) {
				VentanaDialogoComponent.crearVentanaError(e.getMessageDefault(), getStage());
			}
			catch (Exception e) {
				log.error("abrirBusquedaArticulos() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido insertar la línea"), e);
			}
		}
	}

	@Override
	public void nuevoCodigoArticulo() {
		log.debug("nuevoCodigoArticulo() : GAP - PERSONALIZACIONES V3 - LOTES");

		if (tbLineas.getItems().size() == 0 && checkBloqueoRetirada()) {
			tfCodigoIntro.clear();
			return;
		}
		if (!tfCodigoIntro.getText().trim().isEmpty()) {
			log.debug("nuevoCodigoArticulo() - Creando línea de artículo");
			frValidacion.setCantidad(tfCantidadIntro.getText().trim());
			frValidacion.setCodArticulo(tfCodigoIntro.getText().trim().toUpperCase());
			BigDecimal cantidad = frValidacion.getCantidadAsBigDecimal();
			tfCodigoIntro.clear();
			if (accionValidarFormulario() && cantidad != null && !BigDecimalUtil.isIgualACero(cantidad)) {
				log.debug("nuevoCodigoArticulo()- Formulario validado");
				if (Dispositivos.getInstance().getFidelizacion().isPrefijoTarjeta(frValidacion.getCodArticulo())) {
					ticketManager.recalcularConPromociones();
					refrescarDatosPantalla();
					Dispositivos.getInstance().getFidelizacion().cargarTarjetaFidelizado(frValidacion.getCodArticulo(), getStage(), new DispositivoCallback<FidelizacionBean>(){

						@Override
						public void onSuccess(FidelizacionBean tarjeta) {
							if (tarjeta.isBaja()) {
								VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La tarjeta de fidelización {0} no está activa", tarjeta.getNumTarjetaFidelizado()), getStage());
								tarjeta = null;
								ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
							}
							else {
								ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
							}
							ticketManager.recalcularConPromociones();
							refrescarDatosPantalla();
						}

						@Override
						public void onFailure(Throwable e) {
							FidelizacionBean tarjeta = null;
							ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
							ticketManager.recalcularConPromociones();
							refrescarDatosPantalla();
						}
					});
					return;
				}

				/* GAP - PERSONALIZACIONES V3 - LOTES */
				CardosoNuevoCodigoArticuloTask taskArticulo = SpringContext.getBean(CardosoNuevoCodigoArticuloTask.class, this, frValidacion.getCodArticulo(), cantidad);
				taskArticulo.start();
			}
		}
	}

	@Component
	@Scope("prototype")
	public class CardosoNuevoCodigoArticuloTask extends BackgroundTask<LineaTicket> {

		protected final String codArticulo;
		protected final BigDecimal cantidad;

		public CardosoNuevoCodigoArticuloTask(String codArticulo, BigDecimal cantidad) {
			super(false);
			this.codArticulo = codArticulo;
			this.cantidad = BigDecimalUtil.redondear(ticketManager.tratarSignoCantidad(cantidad, ticketManager.getTicket().getCabecera().getCodTipoDocumento()), 3);
		}

		@Override
		protected LineaTicket call() throws Exception {
			insertandoLinea = true;
			return nuevoArticuloTaskCall(codArticulo, cantidad);
		}

		@Override
		protected void succeeded() {
			try {
				super.succeeded();
				nuevoArticuloTaskSucceededCardoso(getValue());
			}
			catch (java.lang.Exception e) {
				log.error("succeeded() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}
		}

		@Override
		protected void failed() {
			try {
				nuevoArticuloTaskFailed(getCMZException());
				super.failed();
			}
			catch (java.lang.Exception e) {
				log.error("failed() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}
		}

		@Override
		protected void done() {
			super.done();
			insertandoLinea = false;
		}

	}

	/**
	 * Asignamos los lotes al introducir una linea que el artículo pertenezca a un lote.
	 * 
	 * @param linea
	 */
	public void nuevoArticuloTaskSucceededCardoso(LineaTicket linea) {
		log.debug("nuevoArticuloTaskSucceededCardoso() : GAP - PERSONALIZACIONES V3 - LOTES");

		super.nuevoArticuloTaskSucceeded(linea);

		if (((CardosoArticuloBean) linea.getArticulo()).getAtributosAdicionalesArticulo() != null && ((CardosoArticuloBean) linea.getArticulo()).getAtributosAdicionalesArticulo().getLote()) {
			CardosoLoteArticuloBean loteBD;
			try {
				/*
				 * GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA No debemos asignar lotes si viene desde una
				 * linea de balanza.
				 */
				if (StringUtils.isBlank(((CARDOSOLineaTicket) linea).getUidTicketBalanza())) {
					loteBD = loteArticuloService.getLoteArticulo(sesion.getAplicacion().getUidActividad(), linea.getArticulo().getCodArticulo());
					if (loteBD != null && StringUtils.isNotBlank(loteBD.getLote())) {
						CardosoLote lote = new CardosoLote();
						lote.setId(loteBD.getLote());
						lote.setCantidad(linea.getCantidad());
						List<CardosoLote> listaLote = new ArrayList<CardosoLote>();
						listaLote.add(lote);
						((CARDOSOLineaTicket) linea).setLotes(listaLote);
					}
					else {
						getDatos().put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
						getApplication().getMainView().showModalCentered(CardosoLotesView.class, getDatos(), getStage());
						linea = (LineaTicket) getDatos().get(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO);
					}
				}
			}
			catch (Exception e) {
				log.error("insertarLineaVenta() - Error al consultar el lote del artículo: " + e.getMessage());
				getDatos().put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
				getApplication().getMainView().showModalCentered(CardosoLotesView.class, getDatos(), getStage());
				linea = (LineaTicket) getDatos().get(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO);
			}
		}
	}

	@FXML
	public void consultarTotales() {
		try {
			ConsultaTotalesBean totales = worldlineService.consutarTotales();
			getDatos().put(ConsultaTotalesController.PARAMETRO_ENTRADA, totales);
			getApplication().getMainView().showModalCentered(ConsultaTotalesView.class, getDatos(), getStage());
		}
		catch (Exception e) {
			log.error("consultarTotales() - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(e.getMessage(), this.getStage());
			return;
		}

	}

	@Override
	protected boolean accionValidarFormularioBusqueda() {
		super.accionValidarFormularioBusqueda();

		if (frValidacionBusqueda.getCantidadAsBigDecimal().compareTo(new BigDecimal(0)) != 1) {
			VentanaDialogoComponent.crearVentanaAviso("No se puede añadir artículo con cantidad " + frValidacionBusqueda.getCantidad(), getStage());
			return false;
		}
		return true;
	}

	@Override
	protected void consultarCopiaSeguridad() throws DocumentoException, TicketsServiceException {

		// Comprobamos si existens copias de seguridad de tickets en base de datos para esta pantalla y si es así
		// ofrecemos
		// la posibilidad de recuperarlo

		log.debug("consultarCopiaSeguridad() - Consultando copia de seguridad en inicialización de pantalla...");
		TipoDocumentoBean tipoDocumentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA);
		final TicketAparcadoBean copiaSeguridad = copiaSeguridadTicketService.consultarCopiaSeguridadTicket(tipoDocumentoActivo);

		if (copiaSeguridad != null) {
			log.debug("consultarCopiaSeguridad() - Se ha encontrado una copia de seguridad");
			try {
				log.debug("consultarCopiaSeguridad() - Copia de seguridad: " + new String(copiaSeguridad.getTicket(), "UTF-8"));
			}
			catch (Exception e) {
				log.error("consultarCopiaSeguridad() - Ha ocurrido un error parseando la copia de seguridad: " + e.getMessage(), e);
			}

			TicketVentaAbono ticketRecuperado = (TicketVentaAbono) MarshallUtil.leerXML(copiaSeguridad.getTicket(), ticketManager.getTicketClasses(tipoDocumentoActivo).toArray(new Class[] {}));

			if (ticketRecuperado != null) {
				// CAR-39
				// Comprobamos si hay algun fichero de pago de Worldline en el sistema, para realizar la cancelación
				// automatica de dicho pago de cara a la pasarela
				paymentsManager.setPaymentsMethodsConfiguration(paymentsMethodsConfiguration);
				TefWorldlineManager worldlineManager = (TefWorldlineManager) paymentsManager.getPaymentsMehtodManagerAvailables().values().stream()
				        .filter(manager -> manager instanceof TefWorldlineManager).findFirst().get();

				if (worldlineManager == null) {
					VentanaDialogoComponent.crearVentanaAviso(
					        "No se ha encontrado el manager del medio de pago de Worldline.\nNo se puede enviar una cancelación automática de los posibles pagos con tarjeta", getStage());
				}
				else {
					try {
						worldlineManager.doAutomaticCancel(ticketRecuperado.getCabecera().getUidTicket(), false);
					}
					catch (Exception e) {
						String msg = "Ha ocurrido un error cancelando los pagos realizados con el pinpad";
						log.error(msg + ":" + e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError(msg, getStage());
					}
				}

				// Fin CAR-39
				if (ticketRecuperado.getIdTicket() != null) {
					log.info("consultarCopiaSeguridad() - El id ticket de la copia de seguridad es " + ticketRecuperado.getIdTicket());
					if (ticketRecuperado.getPagos().isEmpty()) {
						VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Existe un ticket sin finalizar. Se tiene que terminar ese ticket antes de poder vender."), getStage());
					}
					else {
						VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Existe un ticket guardado con pagos realizados. Se tiene que terminar ese ticket antes de poder vender."), getStage());
					}
					try {
						ticketManager.recuperarCopiaSeguridadTicket(getStage(), copiaSeguridad);
						log.info("consultarCopiaSeguridad() - Se va a abrir la pantalla de pagos, ya que hay id ticket asignado...");
						abrirPagos();
						return;
					}

					catch (Throwable e) {
						log.error("consultarCopiaSeguridad() - Ha habido un error al recuperar el ticket: " + e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
						return;
					}
				}

				if (!tieneArticuloGiftCard(ticketRecuperado, tipoDocumentoActivo)) {
					log.debug("consultarCopiaSeguridad() - Se va a preguntar al usuario si quiere recuperar la copia de seguridad");
					if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Existe una venta sin finalizar. ¿Desea recuperarla?"), getStage())) {
						log.debug("consultarCopiaSeguridad() - El usuario ha aceptado la recuperación de la copia de seguridad");
						try {
							ticketManager.recuperarCopiaSeguridadTicket(getStage(), copiaSeguridad);
							Platform.runLater(new Runnable(){

								@Override
								public void run() {
									visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
								}
							});
						}
						catch (Throwable e) {
							log.error("consultarCopiaSeguridad() - " + e.getMessage(), e);
							VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
						}
					}
					else {
						log.debug("consultarCopiaSeguridad() - El usuario no ha aceptado la recuperación de la copia de seguridad");
					}
				}
			}
		}
	}
}
