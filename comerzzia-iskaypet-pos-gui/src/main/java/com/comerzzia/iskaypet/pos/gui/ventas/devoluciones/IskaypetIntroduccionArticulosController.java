package com.comerzzia.iskaypet.pos.gui.ventas.devoluciones;

import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.tipodocumento.TipoDocumentoNotFoundException;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.iskaypet.pos.gui.autorizacion.AutorizacionGerenteUtils;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaDto;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaTicketController;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaTicketView;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.masivo.MotivosAuditoriaMasivoController;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.motivos.CargarMotivosController;
import com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.detalle.IskaypetDetalleGestionticketsController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.edicion.IskaypetEdicionArticuloController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes.AsignarLoteController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes.AsignarLoteView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes.LoteArticuloManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.previsualizacion.PrevisualizacionTicketController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.previsualizacion.PrevisualizacionTicketView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.trazabilidad.seleccion.AsignarTrazabilidadController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.trazabilidad.seleccion.AsignarTrazabilidadView;
import com.comerzzia.iskaypet.pos.persistence.articulos.lotes.LoteDTO;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.DetailPets;
import com.comerzzia.iskaypet.pos.services.articulos.IskaypetArticulosService;
import com.comerzzia.iskaypet.pos.services.articulos.devoluciones.contrato.ServicioDevolucionAnimales;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriasService;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaDTO;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.IskaypetCabeceraTicket;
import com.comerzzia.iskaypet.pos.services.ticket.contrato.trazabilidad.TrazabilidadMascotasService;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.util.formatter.IskaypetFormatter;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.fidelizacion.ConsultaTarjetaFidelizadoException;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.BusquedaFidelizadoView;
import com.comerzzia.pos.gui.ventas.devoluciones.IntroduccionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FormularioLineaArticuloBean;
import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloView;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.cabecera.DatosDocumentoOrigenTicket;
import com.comerzzia.pos.services.ticket.lineas.ILineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

import static com.comerzzia.iskaypet.pos.devices.fidelizacion.busqueda.IskaypetBusquedaFidelizadoController.*;
import static com.comerzzia.iskaypet.pos.devices.proformas.seleccion.SeleccionProformaController.PARAM_PROFORMA_SELECCIONADA;
import static com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController.*;
import static com.comerzzia.pos.dispositivo.fidelizacion.busqueda.BusquedaFidelizadoController.PARAMETRO_NUM_TARJETA;

@Component
@Primary
@SuppressWarnings({ "rawtypes", "deprecation" })
public class IskaypetIntroduccionArticulosController extends IntroduccionArticulosController {

	protected Logger log = Logger.getLogger(getClass());

	protected MotivoAuditoriaDto motivo;

	// TODO BCR temp mostrar numeración auditorías para líneas múltiples
	// Esto debería integrarse en un manager de auditorías
	private Integer totalArticulosDev = null;
	private Integer indiceArticulosDev = null;

	private ILineaTicket lastLineMemory = null;

	@Autowired
	private AuditoriasService auditoriasService;

	@Autowired
	protected VariablesServices variablesServices;

	@Autowired
	private LoteArticuloManager loteArticuloManager;

	@Autowired
	private Sesion sesion;

	@Autowired
	private IskaypetFacturacionArticulosController facturacionArticulosController;

	@Autowired
	protected ServicioDevolucionAnimales servicioDevolucionAnimales;

	@Autowired
	protected IskaypetArticulosService articulosService;

	//GAP 172 TRAZABILIDAD ANIMALES
	@Autowired
	protected TrazabilidadMascotasService trazabilidadMascotasService;

	@FXML
	protected TableColumn<LineaTicketGui, String> tcLineasLote;

	@FXML
	public Label lbNombreFidelizado;
	@FXML
	public Label lbNombreTarjetaFidelizado;
	@FXML
	public Label lbNumFidelizado;
	@FXML
	public Label lbNumTarjetaFidelizado;
	@FXML
	public Label lbSaldoFidelizado;
	@FXML
	public Label lbSaldoTarjetaFidelizado;


	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);

		// GAP 12 - ISK-8 GESTIÓN DE LOTES
		tcLineasLote.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasLote", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasLote.setCellValueFactory(cdf -> {
			IskaypetLineaTicket linea = (IskaypetLineaTicket) ticketManager.getTicket().getLinea(cdf.getValue().getIdLinea());
			if (linea != null) {
				LoteDTO lote = linea.getLote();
				if (lote != null) {
					return new SimpleStringProperty(lote.getLote());
				}
			}

			return new SimpleStringProperty("");
		});
		// fin GAP 12 - ISK-8

		tcLineasPVP.setCellFactory(column -> new TableCell<LineaTicketGui, BigDecimal>(){

			@Override
			protected void updateItem(BigDecimal item, boolean empty) {
				super.updateItem(item, empty);
				setAlignment(Pos.CENTER_RIGHT);
				if (empty || item == null) {
					setText(null);
					setStyle("");
					getStyleClass().removeAll(Collections.singleton(CLASS_CELL_RENDERER_RESALTED));
				}
				else {
					setText(FormatUtil.getInstance().formateaNumero(item, 2));
					setStyle("-fx-padding: 0px 8px 0px 0px;");
					IskaypetLineaTicket linea = getLineaFromTablRow(ticketManager, getTableRow());
					boolean esDevolucionSinDocOrigen = ((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen() != null;
					if (linea != null && esDevolucionSinDocOrigen) {
						// Si se ha modificaco el precio (descuento manual), se resalta la celda
						if (!BigDecimalUtil.isIgual(linea.getPrecioTotalTarifaOrigen(), linea.getPrecioTotalSinDto())) {
							getStyleClass().add(CLASS_CELL_RENDERER_RESALTED);
						}
						else {
							getStyleClass().removeAll(Collections.singleton(CLASS_CELL_RENDERER_RESALTED));
						}
					}
				}
			}
		});
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();

		boolean esDevolucionSinDocOrigen = ((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen() != null;

		lastLineMemory = null;

		// ISK-182 GAP-63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
		botonera.setAccionDisabled("verDocumentoOrigen", esDevolucionSinDocOrigen);
		// Si es devolución sin doc origen, se permite buscar fidelizado para asociarle la devolución
		botonera.setAccionVisible("fidelizacion", esDevolucionSinDocOrigen);

		try {
			((IskaypetTicketManager)ticketManager).comprobarGeneracionATCUD();
		} catch (Exception e) {
			throw new InitializeGuiException(e);
		}

		tfCantidadIntro.setTextFormatter(IskaypetFormatter.getIntegerFormat());

		// Obtenemos la proforma, si es una devolución de proforma
		HashMap<String, Object> datos = getView().getParentView().getController().getDatos();
		ProformaDTO proforma = (ProformaDTO) datos.getOrDefault(PARAM_PROFORMA_SELECCIONADA, null);
		// Si la proforma no es nula, y no se ha cargado previamente, se carga
		if (proforma != null && ticketManager instanceof IskaypetTicketManager && !((IskaypetTicketManager) ticketManager).esTicketProforma()) {
			log.debug("initializeForm() - Se inicia generacion devolución desde la proforma: " + proforma.getIdProforma());
			((IskaypetTicketManager) ticketManager).generarTicketDesdeProforma(proforma, getStage());
			log.debug("initializeForm() - Ticket generado correctamente");
			refrescarDatosPantalla();
		}
	}

	@Override
	public void refrescarDatosPantalla() {
		log.debug("refrescarDatosPantalla() - Refrescando datos de pantalla...");
		super.refrescarDatosPantalla();

		// Valores por defecto
		lbNombreFidelizado.setVisible(false);
		lbNombreTarjetaFidelizado.setText("");
		lbNombreTarjetaFidelizado.getStyleClass().remove("infoFidelizado");
		lbNumFidelizado.setVisible(false);
		lbNumTarjetaFidelizado.setText("");
		lbSaldoTarjetaFidelizado.setText("");
		lbSaldoFidelizado.setVisible(false);

		FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		if (datosFidelizado != null) {

			String numTarjeta = datosFidelizado.getNumTarjetaFidelizado();
			if (StringUtils.isNotBlank(numTarjeta)) {
				try {
					// Se consulta el fidelizado para mostrar sus datos actualizados
					FidelizacionBean fidelizado = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(this.getStage(), numTarjeta, this.sesion.getAplicacion().getUidActividad());
					if (fidelizado != null) {
						datosFidelizado = fidelizado;
						ticketManager.getTicket().getCabecera().setDatosFidelizado(fidelizado);
					}
				}
				catch (ConsultaTarjetaFidelizadoException e) {
					throw new RuntimeException(e);
				}

				lbNumFidelizado.setVisible(true);
				lbNumTarjetaFidelizado.setText(datosFidelizado.getNumTarjetaFidelizado());
			}

			if (StringUtils.isNotBlank(datosFidelizado.getNombre())) {
				lbNombreFidelizado.setVisible(true);
				lbNombreTarjetaFidelizado.setText(datosFidelizado.getNombre() + " " + datosFidelizado.getApellido());
				lbNombreTarjetaFidelizado.getStyleClass().add("infoFidelizado");
			}

			if (datosFidelizado.getSaldo() != null) {
				lbSaldoFidelizado.setVisible(true);
				lbSaldoTarjetaFidelizado.setText(datosFidelizado.getSaldoTotal().toString());
			}
		}

	}

	public String crearAuditoriaDevolucionAMano(IskaypetLineaTicket linea, String tipoDoc) {
		AuditoriaDto auditoria = facturacionArticulosController.setearDatosAuditoria(null, null, linea);

		auditoria.setCodMotivo(motivo.getCodigo().toString());
		auditoria.setObservaciones(motivo.getDescripcion());
		String uidAuditoria = null;
		try {
			auditoria = auditoriasService.generarAuditoria(auditoria, tipoDoc, null, Boolean.TRUE);
			uidAuditoria = auditoria.getUidAuditoria();
			// GAP 117 RECUPERACIÓN DE CONTRATOS DESDE EL POS
			auditoriasService.addAuditoriaLinea(linea, tipoDoc, uidAuditoria, auditoria.getCodMotivo());
		}
		catch (ContadorServiceException e) {
			log.error("crearAuditoriaDevolucionAMano() - error al crear la auditoria de eliminacion de linea " + e.getMessage(), e);
		}
		catch (EmpresaException e) {
			log.error("crearAuditoriaDevolucionAMano() - Empresa no encontrada " + e.getMessage(), e);

		}
		catch (TipoDocumentoNotFoundException e) {
			log.error("crearAuditoriaDevolucionAMano() - Tipo de documento no encontrado " + e.getMessage(), e);

		}
		catch (DocumentoException e) {
			log.error("crearAuditoriaDevolucionAMano() - error al recuperar el tipo de documento " + e.getMessage(), e);

		}
		return uidAuditoria;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void accionCancelarDevolucion() {

		try {
			super.compruebaPermisos(PERMISO_CANCELAR_VENTA);
			if (confirmaAnularDevolucion()) {

				if (!ticketManager.isTicketVacio()) {
					List<IskaypetLineaTicket> lineas = (List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas();
					for (IskaypetLineaTicket iskLinea : lineas) {
						iskLinea.setMotivoAuditoria(null);
					}
				}

				//personalizado para que en caso de anular una devolución sin lñineas se guarde la copia de seguridad ya que no se hacía al no tener líneas
				if (ticketManager.getTicket().getIdTicket() != null) {
					//Personalizacion, en casos de no haber lineas añadimos la ultima linea eliminada
					if(ticketManager.getTicket().getLineas().isEmpty()) {
						if (lastLineMemory != null) {
							ticketManager.getTicket().getLineas().add(lastLineMemory);
						}
					}

					IskaypetTicketManager iskaypetTicketManager = (IskaypetTicketManager) ticketManager;
					iskaypetTicketManager.salvarTicketVacio();
				}

				ticketManager.finalizarTicket();
				try {
					eliminarEventosTeclado();
					getView().getParentView().loadAndInitialize();
				}
				catch (InitializeGuiException e) {
					VentanaDialogoComponent.crearVentanaError(getStage(), e);
				}
			}
		}
		catch (SinPermisosException e) {
			log.debug("accionCancelarDevolucion() - El usuario no tiene permisos para cancelar la devolución.");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para cancelar la devolución."), getStage());
		}

	}

	@Override
	protected void asignarNumerosSerie(LineaTicket linea) {
		super.asignarNumerosSerie(linea);
		solicitarMotivoDevolucion(linea);
	}

	private void solicitarMotivoDevolucion(LineaTicket linea) {

		// fin GAP 12 - ISK-8 - No se solicita motivo si la línea no se ha añadido al ticket
		if (ticketManager.getTicket().getLinea(linea.getIdLinea()) == null) {
			return;
		}

		IskaypetLineaTicket iskLinea = (IskaypetLineaTicket) linea;

		HashMap<String, Object> params = new HashMap<>();

		params.put(IskaypetFacturacionArticulosController.TIPO_DOCUMENTO_ENVIADO, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION);
		params.put(IskaypetFacturacionArticulosController.LINEA_ENVIADA, iskLinea);

		if (iskLinea.getMotivoAuditoria() == null) {
			getApplication().getMainView().showModalCentered(MotivoAuditoriaTicketView.class, params, getStage());
			motivo = (MotivoAuditoriaDto) params.get(CargarMotivosController.MOTIVO);

			String uidAuditoria = crearAuditoriaDevolucionAMano(iskLinea, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION);
			iskLinea.setMotivoAuditoria(motivo);
			// GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
			auditoriasService.addAuditoriaLinea(iskLinea, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION, uidAuditoria, motivo.getCodigo().toString());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void abrirPagos() {
		log.debug("abrirPagos()");
		log.info("abrirPagos()");

		if (!ticketManager.isTicketVacio()) {
			ITicket ticketOperacion = ticketManager.getTicket();
			IskaypetDetalleGestionticketsController.parsearAdicionales(ticketOperacion);
			IskaypetDetalleGestionticketsController.parsearPromocionesAdicionales(ticketOperacion);

			// GAP 12 - ISK-8 GESTIÓN DE LOTES - Se piden lotes faltantes antes de entrar en pagos
			List<IskaypetLineaTicket> lineasMedicamentosSinLote;
			try {
				lineasMedicamentosSinLote = loteArticuloManager.devuelveLineasSinLote((List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas(),
				        ticketManager.getTicket().getCabecera().getDatosFidelizado(), true);
			}
			catch (Exception e) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error comprobando medicamentos."), getStage());
				return;
			}


			if (!lineasMedicamentosSinLote.isEmpty()) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Hay medicamentos sin lote asignado."), getStage());

				// Agrupamos los medicamentos sin lote por codigo de articulo
				HashMap<String, List<IskaypetLineaTicket>> mapMedicamentosSinLote = new HashMap<>();
				for (IskaypetLineaTicket lineaMedicamentoSinLote: lineasMedicamentosSinLote) {
					mapMedicamentosSinLote
							.computeIfAbsent(lineaMedicamentoSinLote.getCodArticulo(), k -> new ArrayList<>())
							.add(lineaMedicamentoSinLote);
				}

				// Recorremos la inserción de lotes agrupados por codigo de articulo
				for (Map.Entry<String, List<IskaypetLineaTicket>> entry : mapMedicamentosSinLote.entrySet()) {
					getDatos().clear();
					getDatos().put(AsignarLoteController.CLAVE_PARAMETRO_LISTA_LINEAS, lineasMedicamentosSinLote);
					getDatos().put(AsignarLoteController.CLAVE_TICKET_MANAGER, ticketManager);
					getApplication().getMainView().showModalCentered(AsignarLoteView.class, getDatos(), this.getStage());

					Boolean seHaCanceladoSeleccionLotes = (Boolean) getDatos().get(AsignarLoteController.CLAVE_SE_HA_CANCELADO_PANTALLA);
					if (seHaCanceladoSeleccionLotes != null && seHaCanceladoSeleccionLotes) {
						refrescarDatosPantalla();
						return;
					}

					// Después de asignar lotes, refrescamos pantalla
					refrescarDatosPantalla();
				}

			}
			// fin GAP 12 - ISK-8 GESTIÓN DE LOTES
		}

		//GAP 172 TRAZABILIDAD ANIMALES
		if (!ticketManager.isTicketVacio()) {

			List<IskaypetLineaTicket> lineasAnimalSinTrazabilidad = trazabilidadMascotasService.getLineasRequiereTrazabilidadNoAsignadas((List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas());
			if (!lineasAnimalSinTrazabilidad.isEmpty()) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Hay animales sin identificación asignada."), getStage());
				getDatos().put(AsignarTrazabilidadController.CLAVE_PARAMETRO_LISTA_LINEAS, lineasAnimalSinTrazabilidad);
				getDatos().put(AsignarTrazabilidadController.CLAVE_TICKET_MANAGER, ticketManager);
				getApplication().getMainView().showModalCentered(AsignarTrazabilidadView.class, getDatos(), this.getStage());

				if(getDatos().containsKey("cancelar")) {
					return;
				}
			}
		}

		if (compruebaMotivosDevolucionLineas()) {

			if (!previsualizacion()) {
				return;
			}

			quitaDatosDocOrigenEnDevolucionesLibres();
			super.abrirPagos();
		}
		else {
			String texto = I18N.getTexto("No se puede continuar con el proceso de pagos ya que falta seleccionar el motivo a devolver de una o más líneas seleccionadas");
			VentanaDialogoComponent.crearVentanaAviso(texto, getStage());
		}
	}

	private boolean compruebaMotivosDevolucionLineas() {
		log.debug("compruebaMotivosDevolucionLineas() - Comprobando que todas las líneas tienen motivos de devolución");
		// TODO DESCTIVADO HASTA QUE SE ABORDE EL DESARROLLO DE DESGLOSE DE LINEA POR CANTIDAD MAEC
		// if ((ticketManager.getTicket()).getLineas() != null && !(ticketManager.getTicket()).getLineas().isEmpty()) {
		// for (LineaTicketAbstract lineaTicket : (List<LineaTicketAbstract>) (ticketManager.getTicket()).getLineas()) {
		//
		// MotivoAuditoriaDto motivoAuditoria = ((IskaypetLineaTicket) lineaTicket).getMotivoAuditoria();
		//
		// //sacar linea documento origen, si linea origen== null, el articulo es nuevo por tanto no muestra la pantalla de
		// motivo de devolucion
		// Integer posicionLineaOrigen = ((IskaypetLineaTicket) lineaTicket).getLineaDocumentoOrigen();
		//
		// if (lineaTicket instanceof IskaypetLineaTicket && motivoAuditoria == null && posicionLineaOrigen != null) {
		// log.debug("compruebaMotivosDevolucionLineas() - La línea: " + lineaTicket.getIdLinea() + " no tiene motivo de
		// devolución");
		// return false;
		// }
		// }
		// }

		return true;
	}

	/**
	 * Comprueba si se están intentando introducir más cantidad del artículo de la que está disponible para devolver.
	 */
	private boolean esCantidadIntroducidaSuperiorADisponible(String codart, String desglose1, String desglose2, BigDecimal cantidadAIntroducir) {
		// Solo para devoluciones con documento origen (no libres)
		if (ticketManager.getTicket().getCabecera().getDatosDocOrigen() == null) {
			return false;
		}

		BigDecimal cantidadDisponibleDevolver = BigDecimal.ZERO;

		@SuppressWarnings("unchecked")
		List<LineaTicket> lineasOrigen = ticketManager.getTicketOrigen().getLineas();
		for (LineaTicket lineaOrigen : lineasOrigen) {

			// Solo si es el mismo artículo (+ desgloses)
			if (desglose1 == null) {
				desglose1 = "*";
			}
			if (desglose2 == null) {
				desglose2 = "*";
			}
			if (!lineaOrigen.getCodArticulo().equals(codart) || !lineaOrigen.getDesglose1().equals(desglose1) || !lineaOrigen.getDesglose2().equals(desglose2)) {
				continue;
			}

			// Se suma la cantidad disponible para devolución
			// cantidadDisponibleDevolver += cantidad - (cantidadDevuelta + cantidadDevolver)
			cantidadDisponibleDevolver = cantidadDisponibleDevolver.add(lineaOrigen.getCantidad().subtract(lineaOrigen.getCantidadDevuelta().add(lineaOrigen.getCantidadADevolver())));
		}

		return BigDecimalUtil.isMayor(cantidadAIntroducir, cantidadDisponibleDevolver);
	}

	@Override
	protected LineaTicket nuevaLineaArticulo(String codart, String desglose1, String desglose2, BigDecimal cantidad) throws LineaTicketException {
		log.info("nuevaLineaArticulo() - Consultando el artículo: " + codart);
		ArticuloCodBarraBean codigoBarras;
		SqlSession sqlSession = new SqlSession();

		// Se comprueba si el ticket es una proforma, y es una devo con origen y si es así no se permite introducir líneas
		if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
			throw new LineaTicketException(I18N.getTexto("No se puede añadir lineas a la devolución de una proforma"));
		}


		try {
			sqlSession.openSession(SessionFactory.openSession());

			if (desglose1 == null && desglose2 == null) {
				codigoBarras = articulosService.consultarCodigoBarras(sqlSession, codart);
				desglose1 = codigoBarras.getDesglose1();
				desglose2 = codigoBarras.getDesglose2();
				codart = codigoBarras.getCodArticulo();
			}
		}
		catch (Exception e) {
			log.info("nuevaLineaArticulo() - Artículo no encontrado como codigo de barra " + codart);
		}
		finally {
			sqlSession.close();
		}

		// GAP 12 - ISK-8 GESTIÓN DE LOTES
		loteArticuloManager.comprobarPermisoDevolucionMedicamentos(ticketManager, codart, getStage(), ticketManager.getTicket().getCabecera().getDatosFidelizado(), true);

		// ISK-153 INTEGRACION CZZ Y EVICERTIA
		if (servicioDevolucionAnimales.esArticuloMascota(codart)) {

			if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Va a realizar la devolución de una mascota, \n ¿Está seguro?"), getStage())) {
				throw new LineaTicketException(I18N.getTexto("Ha sido cancelada la devolución de la mascota seleccionada"));
			}

		}
		// Se comprueba cantidad. De no hacerlo, añadirá tantas líneas como pueda y dará error, sin mostrar lotes ni auditoría
		if (esCantidadIntroducidaSuperiorADisponible(codart, desglose1, desglose2, cantidad)) {
			throw new LineaTicketException(I18N.getTexto("La cantidad introducida es mayor a la cantidad disponible para devolver"));

		}

		// CZZ-711 - Validar lineas planes de salud
		String msgError = "Debe tener un cliente asociado para poder realizar devoluciones de planes de salud";
		if(ticketManager instanceof IskaypetTicketManager) {
			((IskaypetTicketManager) ticketManager).validarPlanesSalud(codart, msgError);
		}


		// GAPXX - BLOQUEO DE LINEAS EN VENTA CON CANTIDAD SUPERIOR A 1
		// Lógica para insertar en el ticket un número de lineas equivalente a la cantidad introducida.
		List<LineaTicket> listLines = new ArrayList<>();
		String accionSeleccionada = MotivosAuditoriaMasivoController.ACCION_MOTIVOS_UNIDAD;
		if (BigDecimalUtil.isIgual(cantidad, BigDecimal.ONE)) {
			listLines.add(ticketManager.nuevaLineaArticulo(codart, desglose1, desglose2, cantidad, null));
		}
		else {
			int quantity;
			try {
				quantity = cantidad.intValue();
			}
			catch (Exception e) {
				quantity = 1;
			}

			if (quantity > 1) {
				String titulo = I18N.getTexto("¿Cómo desea introducir el motivo de la devolución a las {0} unidades?", quantity);
				accionSeleccionada = MotivosAuditoriaMasivoController.getAccionSeleccionada(BigDecimal.valueOf(quantity), titulo, getApplication(), getStage());
				if (accionSeleccionada == null) {
					throw new LineaTicketException(I18N.getTexto("Ha sido cancelada la introducción de las ") + quantity + I18N.getTexto(" unidades"));
				}

			}

			for (int i = 0; i < quantity; i++) {
				listLines.add(ticketManager.nuevaLineaArticulo(codart, desglose1, desglose2, BigDecimal.ONE, null));
			}
		}

		// Seleccionamos las líneas que hayan sido añadidas al ticket
		// Si no se cumplen las condiciones, #insertarLineaVentaIskaypet() devuelve las líneas igualmente
		List<LineaTicket> lineasDelTicket = loteArticuloManager.devuelveLineasAnadidasATicket(listLines, ticketManager);

		if (!lineasDelTicket.isEmpty()) {

			// ISK-249 GAP 101 Edición de cantidad en POS
			// Se asignan auditorías de devolución para cada línea añadida
			int total = lineasDelTicket.size();
			if (accionSeleccionada.equals(MotivosAuditoriaMasivoController.ACCION_MOTIVOS_TOTAL)) {
				accionAuditoriaTotal(lineasDelTicket);
			}
			else if (accionSeleccionada.equals(MotivosAuditoriaMasivoController.ACCION_MOTIVOS_UNIDAD)) {
				accionAuditoriaUnidad(lineasDelTicket, total);
			}

			// GAP 12 - ISK-8 GESTIÓN DE LOTES
			String codArtLinea = lineasDelTicket.get(0).getArticulo().getCodArticulo();
			boolean esMedicamento = loteArticuloManager.esArticuloMedicamento(codArtLinea, ticketManager.getTicket().getCabecera().getDatosFidelizado(), true);
			if (esMedicamento) {
				getDatos().put(AsignarLoteController.CLAVE_PARAMETRO_LISTA_LINEAS, lineasDelTicket);
				getDatos().put(AsignarLoteController.CLAVE_TICKET_MANAGER, ticketManager);
				getApplication().getMainView().showModalCentered(AsignarLoteView.class, getDatos(), this.getStage());
			}

		}

		//GAP 172 TRAZABILIDAD ANIMALES
		//Validar lineas
		validarTrazabilidadMascotas(listLines);
		
		actualizarContratoLineasOrigen(ticketManager);

		return listLines.get(0);
	}

	@SuppressWarnings("unchecked")
	private void actualizarContratoLineasOrigen(TicketManager ticketManager) {
	    if (ticketManager == null || ticketManager.getTicket() == null || ticketManager.getTicketOrigen() == null) {
	        return;
	    }

	    List<LineaTicket> lineasPrincipal = ticketManager.getTicket().getLineas();
	    List<LineaTicket> lineasOrigen = ticketManager.getTicketOrigen().getLineas();

	    // Indexamos las líneas del ticket origen por su ID para acceso rápido
	    Map<Integer, LineaTicket> mapaLineasOrigen = new HashMap<>();
	    for (LineaTicket lineaOrigen : lineasOrigen) {
	        mapaLineasOrigen.put(lineaOrigen.getIdLinea(), lineaOrigen);
	    }

	    // Recorremos las líneas del ticket principal
	    for (LineaTicket linea : lineasPrincipal) {
	        Integer idLineaOrigen = linea.getLineaDocumentoOrigen();
	        if (idLineaOrigen != null) {
	            LineaTicket lineaOrigen = mapaLineasOrigen.get(idLineaOrigen);
	            if (lineaOrigen != null && ((IskaypetLineaTicket)lineaOrigen).getContratoAnimal() != null) {
	            	((IskaypetLineaTicket)linea).setContratoAnimal(((IskaypetLineaTicket)lineaOrigen).getContratoAnimal());
	            }
	        }
	    }
	}
	public void accionAuditoriaTotal(List<LineaTicket> ticketLines) {
		IskaypetLineaTicket ultimaLinea = (IskaypetLineaTicket) ticketLines.get(ticketLines.size() - 1);
		HashMap<String, Object> params = crearParametros(ultimaLinea, 1, 0, Boolean.TRUE);
		motivo = getMotivo(params);
		if (motivo != null) {
			asignarAuditoriasALineas(ticketLines);
		}
		else {
			eliminarLineasDevolucion(ticketLines);
		}

	}

	private void accionAuditoriaUnidad(List<LineaTicket> lstLineas, int total) {
		for (int i = 0; i < lstLineas.size(); i++) {
			IskaypetLineaTicket lineaSeleccionada = (IskaypetLineaTicket) lstLineas.get(i);
			HashMap<String, Object> params = crearParametros(lineaSeleccionada, total, i, Boolean.TRUE);
			motivo = getMotivo(params);
			if (motivo != null) {
				crearAuditoriaDevolucionAMano(lineaSeleccionada, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION);
				lineaSeleccionada.setMotivoAuditoria(motivo);
			}
			else {
				eliminarLineasDevolucion(lstLineas);
				break;
			}

		}
	}

	private void eliminarLineasDevolucion(List<LineaTicket> lineas) {
		for (LineaTicket linea : lineas) {
			ticketManager.eliminarLineaArticulo(linea.getIdLinea());
		}
	}

	private HashMap<String, Object> crearParametros(IskaypetLineaTicket line, Integer totalArticles, Integer articleIndex, Boolean permiteCancelar) {
		HashMap<String, Object> params = new HashMap<>();
		params.put(IskaypetFacturacionArticulosController.TIPO_DOCUMENTO_ENVIADO, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION);
		params.put(IskaypetFacturacionArticulosController.LINEA_ENVIADA, line);
		params.put(MotivoAuditoriaTicketController.CLAVE_NUMERO_TOTAL_ARTICULOS, totalArticles);
		params.put(MotivoAuditoriaTicketController.CLAVE_NUMERO_INDICE_ARTICULO, articleIndex);
		params.put(MotivoAuditoriaTicketController.ACTIVAR_CANCELAR, permiteCancelar);
		return params;
	}

	private MotivoAuditoriaDto getMotivo(HashMap<String, Object> params) {
		getApplication().getMainView().showModalCentered(MotivoAuditoriaTicketView.class, params, getStage());

		if ((Boolean) params.getOrDefault(MotivoAuditoriaTicketController.CANCELAR, false)) {
			return null;
		}

		return (MotivoAuditoriaDto) params.get(CargarMotivosController.MOTIVO);
	}

	private void asignarAuditoriasALineas(List<LineaTicket> lstLineas) {
		for (LineaTicket linea : lstLineas) {
			if (linea instanceof IskaypetLineaTicket) {
				IskaypetLineaTicket lineaSeleccionada = (IskaypetLineaTicket) linea;
				if (lineaSeleccionada.getMotivoAuditoria() == null) {
					crearAuditoriaDevolucionAMano(lineaSeleccionada, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION);
					lineaSeleccionada.setMotivoAuditoria(motivo);
				}
			}
		}
	}

	// ISK-182 GAP-63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
	@Override
	protected void introducirNuevoArticulo(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad) {
		// No se permite añadir líneas positivas a devolucion, por lo que se informa en vez de dar elección
		VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se permite añadir artículos no presentes en el ticket original"), getStage());
	}

	// //TODO BCR integrar, se dice en GAP de devolución sin origen que no se pueden hacer ventas en devolución
	// @Override
	// protected void introducirNuevoArticulo(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad) {
	// if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se va a introducir un artículo que no estaba en
	// la venta original. ¿Está seguro?"), getStage())) {
	//
	// // GAPXX - BLOQUEO DE LINEAS EN VENTA CON CANTIDAD SUPERIOR A 1
	// // Lógica para insertar en el ticket un número de lineas equivalente a la cantidad introducida.
	// if (BigDecimalUtil.isIgual(cantidad, BigDecimal.ONE)) {
	// introducirLineaPositiva(codArticulo, desglose1, desglose2, cantidad, false);
	// } else {
	// int quantity = 0;
	// try {
	// quantity = cantidad.intValue();
	// } catch (Exception e) {
	// quantity = 1;
	// }
	//
	// for (int i = 0; i < quantity; i++) {
	// introducirLineaPositiva(codArticulo, desglose1, desglose2, BigDecimal.ONE, false);
	// }
	// }
	// }
	// }

	@Override
	// ISK-182 GAP-63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
	protected void introducirArticuloCambio(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad) {
		// No se permite editar líneas en devolución sin doc origen, por lo que se informa en vez de dar elección
		VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Este artículo ya está completamente devuelto en la venta original"), getStage());
	}

	// //TODO BCR integrar, se dice en GAP de devolución sin origen que no se pueden hacer ventas en devolución
	// @Override
	// protected void introducirArticuloCambio(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad)
	// {
	// if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Este artículo ya está completamente devuelto en
	// la venta original. Se introducirá en positivo. ¿Está seguro?"), getStage())) {
	//
	// // GAPXX - BLOQUEO DE LINEAS EN VENTA CON CANTIDAD SUPERIOR A 1
	// // Lógica para insertar en el ticket un número de lineas equivalente a la cantidad introducida.
	// if (BigDecimalUtil.isIgual(cantidad, BigDecimal.ONE)) {
	// introducirLineaPositiva(codArticulo, desglose1, desglose2, cantidad, true);
	// } else {
	// int quantity = 0;
	// try {
	// quantity = cantidad.intValue();
	// } catch (Exception e) {
	// quantity = 1;
	// }
	//
	// for (int i = 0; i < quantity; i++) {
	// introducirLineaPositiva(codArticulo, desglose1, desglose2, BigDecimal.ONE, true);
	// }
	// }
	// }
	// }

	////////////////////////////////////////

	@Override
	protected void accionTablaEditarRegistro() {
		try {
			log.debug("accionTablaEditarRegistro() - Acción ejecutada");
			super.compruebaPermisos(PERMISO_MODIFICAR_LINEA);
			if (tbLineas.getItems() != null && getLineaSeleccionada() != null) {
				// Se comprueba si el ticket es una proforma, si es así no se permite eliminar la línea
				if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()){
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede editar linea de una proforma"), getStage());
					return;
				}

				int linea = getLineaSeleccionada().getIdLinea();
				if (linea > 0) {
					LineaTicket lineaTicket = (LineaTicket) ticketManager.getTicket().getLinea(linea);

					// ISK-182 GAP-63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN. Se permite edición de línea para devoluciones libres
					boolean esDevolucionSinDocOrigen = ((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen() != null;
					boolean esCantidadMayorACero = BigDecimalUtil.isMayorACero(lineaTicket.getCantidad());

					if (esDevolucionSinDocOrigen || esCantidadMayorACero) {
						// Creamos la ventana de edición de artículos

						boolean aplicarPromociones = false;

						HashMap<String, Object> parametrosEdicionArticulo = new HashMap<>();
						parametrosEdicionArticulo.put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, lineaTicket);
						parametrosEdicionArticulo.put(EdicionArticuloController.CLAVE_APLICAR_PROMOCIONES, aplicarPromociones);
						parametrosEdicionArticulo.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
						getApplication().getMainView().showModalCentered(EdicionArticuloView.class, parametrosEdicionArticulo, this.getStage());

						// ISK-249 GAP 101 Edición de cantidad en POS
						// Se insertan las líneas añadidas en cantidad de EdicionArticulo
						BigDecimal cantidadTotal = (BigDecimal) parametrosEdicionArticulo.get(IskaypetEdicionArticuloController.CLAVE_CANTIDAD_SELECCIONADA_TEXTFIELD);

						if (cantidadTotal != null && BigDecimalUtil.isMayor(cantidadTotal, BigDecimal.ONE)) {
							BigDecimal numeroArticulosAnadir = cantidadTotal.subtract(BigDecimal.ONE);
							totalArticulosDev = numeroArticulosAnadir.intValue();
							for (int i = 0; i < numeroArticulosAnadir.intValue(); i++) {
								indiceArticulosDev = i;
								LineaTicket lineaNueva = nuevaLineaArticulo(lineaTicket.getCodArticulo(), lineaTicket.getDesglose1(), lineaTicket.getDesglose2(), BigDecimal.ONE);
								// descuentos
								lineaNueva.setDescuentoManual(lineaTicket.getDescuentoManual());

								// Copia de EdicionArticulos
								BigDecimal precioSinImpuestos = sesion.getImpuestos().getPrecioSinImpuestos(lineaTicket.getCodImpuesto(), lineaTicket.getPrecioTotalSinDto(),
								        ticketManager.getTicket().getCabecera().getCliente().getIdTratImpuestos());
								lineaNueva.setPrecioSinDto(precioSinImpuestos);
								lineaNueva.setPrecioTotalSinDto(lineaTicket.getPrecioTotalSinDto());
								lineaNueva.setDescuentoManual(lineaTicket.getDescuentoManual());
								lineaNueva.setCantidad(ticketManager.tratarSignoCantidad(BigDecimal.ONE, lineaTicket.getCabecera().getCodTipoDocumento()));
								lineaNueva.setVendedor(lineaTicket.getVendedor());
								lineaNueva.setDesArticulo(lineaTicket.getDesArticulo());
								lineaNueva.setNumerosSerie(lineaTicket.getNumerosSerie());
								lineaNueva.recalcularImporteFinal();
								if (aplicarPromociones) {
									ticketManager.recalcularConPromociones();
								}
							}
						}
						totalArticulosDev = null;
						indiceArticulosDev = null;

						if (esDevolucionSinDocOrigen) {
							lineaTicket.setCantidad(lineaTicket.getCantidad());
						}
						else {
							// Funcionamiento estándar
							lineaTicket.setCantidad(lineaTicket.getCantidad().abs());
						}
						lineaTicket.recalcularImporteFinal();
						ticketManager.recalcularConPromociones();

						refrescarDatosPantalla();
					}
					else {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede editar una línea con cantidad negativa."), getStage());
					}
				}
			}
		}
		catch (SinPermisosException ex) {
			log.debug("accionTablaEditarRegistro() - El usuario no tiene permisos para modificar línea");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para modificar una línea."), getStage());
		}
		catch (LineaTicketException ex) {
			log.error("accionTablaEditarRegistro() - Error al añadir línea", ex);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error al añadir línea."), getStage());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
	}

	@Override
	protected void accionTablaEliminarRegistro() {

		if (getLineaSeleccionada() == null) {
			return;
		}

		// Se comprueba si el ticket es una proforma, si es así no se permite eliminar la línea
		if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede eliminar lineas de una proforma"), getStage());
			return;
		}

		// No mostrar con ticket vacio
		getDatos().put(IskaypetFacturacionArticulosController.TIPO_DOCUMENTO_ENVIADO, AuditoriasService.TIPO_AUDITORIA_ANULACION_LINEA);
		getDatos().put(IskaypetFacturacionArticulosController.LINEA_ENVIADA, ticketManager.getTicket().getLinea(getLineaSeleccionada().getIdLinea()));
		// IskaypetLineaTicket iskLinea = (IskaypetLineaTicket)
		// getDatos().get(IskaypetFacturacionArticulosController.LINEA_ENVIADA);

		log.debug("accionTablaEliminarRegistro() - IntroduccionArticulosController ");
		try {
			LineaTicketGui selectedItem = getLineaSeleccionada();
			if (!tbLineas.getItems().isEmpty() && selectedItem != null) {
				super.compruebaPermisos(PERMISO_BORRAR_LINEA);
				boolean confirmar = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar esta línea del ticket?"), getStage());
				if (!confirmar) {
					return;
				}

				if (ticketManager.getTicket().getLineas().size() == 1) {
					lastLineMemory = ((LineaTicket) ticketManager.getTicket().getLineas().get(0)).clone();
				}

				/*
				 * Se comenta funcionalidad auditoria por eliminacion linea ISK-115 //
				 * getApplication().getMainView().showModalCentered(MotivoAuditoriaTicketView.class, getDatos(), getStage()); // motivo
				 * = (MotivoAuditoriaDto) getDatos().get(CargarMotivosController.MOTIVO); // // crearAuditoriaDevolucion(iskLinea,
				 * AuditoriasService.TIPO_AUDITORIA_ANULACION_LINEA);
				 */

				ticketManager.eliminarLineaArticulo(selectedItem.getIdLinea());

				seleccionarSiguienteLinea();
				refrescarDatosPantalla();
			}
		}
		catch (SinPermisosException ex) {
			log.debug("accionTablaEliminarRegistro() - El usuario no tiene permisos para eliminar línea");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para borrar una línea."), getStage());
		}

	}

	private void quitaDatosDocOrigenEnDevolucionesLibres() {
		IskaypetTicketManager iskTicketManager = (IskaypetTicketManager) ticketManager;

		// Al entrar en pagos se quitan datos estándar falsos de doc origen para evitar errores
		if (iskTicketManager.getTicketOrigen() == null || !IskaypetTicketManager.UID_TICKET_FALSO.equalsIgnoreCase(iskTicketManager.getTicketOrigen().getUidTicket())) {
			DatosDocumentoOrigenTicket docOrigen = iskTicketManager.getTicketOrigen().getCabecera().getDatosDocOrigen();
			if (docOrigen != null) {
				docOrigen.setSerie(null);
				docOrigen.setCaja(null);
				docOrigen.setNumFactura(null);
				docOrigen.setIdTipoDoc(null);
				docOrigen.setCodTipoDoc(null);
				docOrigen.setUidTicket(null);
				docOrigen.setCodTicket(null);
			}
		}
	}

	@Override
	protected boolean validarTicket() {
		if (((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen() != null) {
			// ISK-182 GAP-63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
			// Si es devolución sin doc origen se devuelve true
			IskaypetCabeceraTicket cabecera = (IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera();
			cabecera.setDatosOrigenFalsos(((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen());
			return true;
		}
		else {
			return super.validarTicket();
		}
	}

	@Override
	protected void actualizarVisibilidadBtnEditar() {
		botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_EDITAR_REGISTRO", ((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen() == null);
	}

	public void fidelizacion() throws ConsultaTarjetaFidelizadoException {
		this.log.debug("fidelizacion()");

		if(ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede realizar la búsqueda de un cliente en una proforma"), getStage());
			return;
		}

		getDatos().put(PARAMETRO_MOSTRAR_ALTA, false);
		getDatos().put(PARAMETRO_TITULO, I18N.getTexto("Identificación de cliente"));
		getDatos().put(PARAMETRO_BOTON_ACEPTAR, I18N.getTexto("Buscar"));
		getApplication().getMainView().showModalCentered(BusquedaFidelizadoView.class, getDatos(), getStage());
		String numTarjeta = (String) getDatos().get(PARAMETRO_NUM_TARJETA);
		if (StringUtils.isNotBlank(numTarjeta)) {
			FidelizacionBean fidelizado = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(this.getStage(), numTarjeta, this.sesion.getAplicacion().getUidActividad());
			if (fidelizado.isBaja()) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La tarjeta de fidelización {0} no está activa", fidelizado.getNumTarjetaFidelizado()), getStage());
				fidelizado = null;
			}
			ticketManager.getTicket().getCabecera().setDatosFidelizado(fidelizado);
			ticketManager.recalcularConPromociones();
			refrescarDatosPantalla();
		}
	}

	@Override
	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
		List<ConfiguracionBotonBean> listaAcciones = super.cargarAccionesTabla();
		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/previsualizacion.png", null, null, ACCION_PREVISUALIZACION, "REALIZAR_ACCION"));
		return listaAcciones;
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		super.realizarAccion(botonAccionado);
		switch (botonAccionado.getClave()) {
			case ACCION_PREVISUALIZACION:
				abrirPrevisualizacion();
				break;
		}
	}

	protected void abrirPrevisualizacion() {
		previsualizacion();
	}

	protected Boolean previsualizacion() {

		if (ticketManager.isTicketVacio()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede previsualizar un ticket vacío"), getStage());
			return false;
		}

		HashMap<String, Object> datos = new HashMap<>();
		datos.put(PrevisualizacionTicketController.CLAVE_TICKET_MANAGER, ticketManager);
		getApplication().getMainView().showModalCentered(PrevisualizacionTicketView.class, datos, getStage());
		return !(Boolean) datos.getOrDefault(PrevisualizacionTicketController.ACCION_CANCELAR, false);
	}

    @Override
    public void abrirCajon() {
        try {
			HashMap<String, Object> datos = new HashMap<>();
			datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
            AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
            super.abrirCajon();
        } catch (InitializeGuiException initializeGuiException) {
            if (initializeGuiException.isMostrarError()) {
                VentanaDialogoComponent.crearVentanaError(getStage(), initializeGuiException);
            }
        }
    }

	/* ######################################################################################################################### */
	/* ###################### GAP 172 TRAZABILIDAD ANIMALES #################################################################### */
	/* ######################################################################################################################### */

	public void validarTrazabilidadMascotas(List<LineaTicket> listLines) throws LineaTicketException {

		// Comprobamos si la lista de líneas está vacía o si la primera línea no es de tipo IskaypetLineaTicket o no es una mascota
		if (listLines == null || listLines.isEmpty() || !(listLines.get(0) instanceof IskaypetLineaTicket) ||
				!((IskaypetLineaTicket) listLines.get(0)).isMascota()) {
			return;
		}

		// Comprobamos si la mascota requiere identificación y si hay un cliente fidelizado seleccionado
		IskaypetLineaTicket lineaTicket = (IskaypetLineaTicket) listLines.get(0);
		if (trazabilidadMascotasService.requiereClienteIdentificado(lineaTicket) && !((IskaypetTicketManager) ticketManager).isFidelizadoSeleccionado()) {
			trazabilidadMascotasService.eliminarLineasTicket(listLines, ticketManager, AsignarTrazabilidadController.MENSAJE_ERROR_NECESITA_CLIENTE);
		}

		// Comprobamos la configuración de región y la trazabilidad disponible en la tienda
		trazabilidadMascotasService.validarConfiguracion(listLines, ticketManager);

		List<LineaTicket> lineasControlTrazabilidad = trazabilidadMascotasService.consultarLineasRequierenTrazabilidad(listLines);
		DetailPets details = trazabilidadMascotasService.consultarTrazabilidad(lineaTicket);
		if (details == null) {
			throw new LineaTicketException(I18N.getTexto(AsignarTrazabilidadController.MENSAJE_ERROR_MASCOTA_DETALLE));
		}

		if (details != null && ("S".equals(details.getIdChip()) || "S".equals(details.getIdAnilla()) || "S".equals(details.getIdCites()))) {
			if (!lineasControlTrazabilidad.isEmpty()) {
				// Comprobamos que al validar la linea, requiera trazabilidad y abirmos pantalla de requerirla
				if (((IskaypetLineaTicket) lineasControlTrazabilidad.get(0)).isRequiereIdentificacion()) {
					// Si la lista de medicamentos no está vacía, seguimos con el proceso
					HashMap<String, Object> map = new HashMap<>();
					map.put(IskaypetFacturacionArticulosController.PARAM_DETAILS_PETS, details);
					map.put(AsignarTrazabilidadController.CLAVE_PARAMETRO_LISTA_LINEAS, lineasControlTrazabilidad);
					map.put(AsignarTrazabilidadController.CLAVE_TICKET_MANAGER, ticketManager);
					getApplication().getMainView().showModalCentered(AsignarTrazabilidadView.class, map, this.getStage());
				}
			}
		}

	}

	protected boolean accionValidarFormulario() {
		this.frValidacion.clearErrorStyle();
		Set<ConstraintViolation<FormularioLineaArticuloBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(this.frValidacion);
		if (!constraintViolations.isEmpty()) {
			ConstraintViolation<FormularioLineaArticuloBean> next = (ConstraintViolation)constraintViolations.iterator().next();
			this.frValidacion.setErrorStyle(next.getPropertyPath(), true);
			this.frValidacion.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaError(next.getMessage(), this.getScene().getWindow());
			return false;
		} else {
			BigDecimal cantidad = this.frValidacion.getCantidadAsBigDecimal();
			if (cantidad == null) {
				return false;
			} else {
				BigDecimal max = new BigDecimal(50);
				if (com.comerzzia.pos.util.bigdecimal.BigDecimalUtil.isMayor(this.frValidacion.getCantidadAsBigDecimal(), max)) {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La cantidad debe ser menor que {0}", FormatUtil.getInstance().formateaNumero(max)), this.getStage());
					return false;
				} else {
					return true;
				}
			}
		}
	}

}
