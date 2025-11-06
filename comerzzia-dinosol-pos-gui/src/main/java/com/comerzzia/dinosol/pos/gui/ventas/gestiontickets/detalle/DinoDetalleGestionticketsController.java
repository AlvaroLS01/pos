package com.comerzzia.dinosol.pos.gui.ventas.gestiontickets.detalle;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasTicketBean;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionticketsController;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

@Component
@Primary
public class DinoDetalleGestionticketsController extends DetalleGestionticketsController {

	private static final Logger log = Logger.getLogger(DinoDetalleGestionticketsController.class);

	private static final String CODIGO_DOC_SAD = "DOCSAD";
	public static final String PERMISO_IMPRIMIR_TICKETS = "IMPRIMIR TICKETS";

	private RutasTicketBean datosTicketSad;
	private TipoDocumentoBean documento;

	@Autowired
	private AuditoriasService auditoriasService;

	@SuppressWarnings("rawtypes")
	public void refrescarDatosPantalla() throws InitializeGuiException {
		log.debug("refrescarDatosPantalla() - Obtenemos el XML del ticket que queremos visualizar");
		comprobarPermisosUI();
		try {
			this.ticket = tickets.get(posicionActual);

			documento = null;
			datosTicketSad = null;
			TicketBean ticketConsultado = null;
			byte[] ticketXML = null;

			ticketConsultado = ticketsService.consultarTicket(ticket.getUidTicket(), sesion.getAplicacion().getUidActividad());
			ticketXML = ticketConsultado.getTicket();

			documento = sesion.getAplicacion().getDocumentos().getDocumento(ticketConsultado.getIdTipoDocumento());
			if (documento.getFormatoImpresion().equals(TipoDocumentoBean.PROPIEDAD_FORMATO_IMPRESION_NO_CONFIGURADO)) {
				if (getStage() != null && getStage().isShowing()) {
					String mensajeInfo = "No es posible visualizar este tipo de documento";
					log.info("refrescarDatosPantalla() - " + mensajeInfo);
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensajeInfo), getStage());
				}
				else {
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							String mensajeInfo = "No es posible visualizar este tipo de documento";
							log.info("refrescarDatosPantalla() - " + mensajeInfo);
							VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensajeInfo), getStage());
						}
					});
				}
				taTicket.setText("");
				return;
			}

			/* Comprobamos si el tipo de documento es de SAD, porque se debe parsear a otro tipo de objeto */
			if (CODIGO_DOC_SAD.equals(documento.getCodtipodocumento())) {

				log.debug("refrescarDatosPantalla() - Iniciando la búsqueda del documento : " + documento.getCodtipodocumento() + " (" + documento.getIdTipoDocumento() + ")");
				datosTicketSad = (RutasTicketBean) MarshallUtil.leerXML(ticketXML, getTicketClasses(documento).toArray(new Class[] {}));
				log.debug("refrescarDatosPantalla() - Finalizada la búsqueda del documento : " + datosTicketSad);

				/* Bloqueamos el botón de Ticket Regalo, porque no tenemos ninguno */
				btnTicketRegalo.setDisable(true);

				try {
					Map<String, Object> mapaParametros = new HashMap<String, Object>();
					mapaParametros.put("datosSAD", datosTicketSad);
					mapaParametros.put("urlQR", variablesService.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));

					/* Hay que obtener el resultado de mostrar en pantalla el ticket y mostrarlo en taTicket */
					String previsualizacion = ServicioImpresion.imprimirPantalla(documento.getFormatoImpresion(), mapaParametros);
					taTicket.setText(previsualizacion);
				}
				catch (Exception e) {
					String mensajeError = "Lo sentimos, ha ocurrido un error al imprimir";
					log.error("refrescarDatosPantalla() - " + mensajeError + " - " + e.getMessage());
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(mensajeError), e);
					throw new InitializeGuiException(false);
				}

				/* En caso de no ser un documento SAD, realizamos las acciones de estándar */
			}
			else {
				log.debug("refrescarDatosPantalla() - Iniciando la búsqueda del documento : " + documento.getIdContador() + " (" + documento.getIdTipoDocumento() + ")");
				ticketOperacion = (TicketVenta) MarshallUtil.leerXML(ticketXML, getTicketClasses(documento).toArray(new Class[] {}));
				log.debug("refrescarDatosPantalla() - Finalizada la búsqueda del documento : " + ticketOperacion);

				if (ticketOperacion != null) {
					ticketOperacion.getCabecera().setDocumento(sesion.getAplicacion().getDocumentos().getDocumento(ticketOperacion.getCabecera().getTipoDocumento()));
					for (LineaTicket linea : ((TicketVentaAbono) ticketOperacion).getLineas()) {
						String codArticulo = linea.getCodArticulo();
						try {
							ArticuloBean articulo = articulosService.consultarArticuloSinEtiqueta(codArticulo);
							linea.setArticulo(articulo);
						}
						catch (Exception e) {
						}

					}
					if (sesion.getAplicacion().getDocumentos().getDocumento(ticketOperacion.getCabecera().getTipoDocumento()).getPermiteTicketRegalo()) {
						btnTicketRegalo.setDisable(false);
					}
					else {
						btnTicketRegalo.setDisable(true);
					}
					try {
						Map<String, Object> mapaParametros = new HashMap<String, Object>();
						mapaParametros.put("ticket", ticketOperacion);
						mapaParametros.put("urlQR", variablesService.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
						/* Hay que obtener el resultado de mostrar en pantalla el ticket y mostrarlo en taTicket */
						String previsualizacion = ServicioImpresion.imprimirPantalla(ticketOperacion.getCabecera().getFormatoImpresion(), mapaParametros);
						taTicket.setText(previsualizacion);
					}
					catch (Exception e) {
						String mensajeError = "Lo sentimos, ha ocurrido un error al imprimir";
						log.error("refrescarDatosPantalla() - " + mensajeError + " - " + e.getMessage());
						VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(mensajeError), e);
						throw new InitializeGuiException(false);
					}
				}
				else {
					String mensajeError = "Error leyendo información de ticket";
					log.error("refrescarDatosPantalla() - " + mensajeError);
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
					throw new InitializeGuiException(false);
				}

			}
		}
		catch (TicketsServiceException ex) {
			String mensajeError = "Error leyendo información de ticket";
			log.error("refrescarDatosPantalla() - " + ex.getMessage());
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(mensajeError), ex);
		}
		catch (DocumentoException e) {
			String mensajeError = "Error recuperando el tipo de documento del ticket";
			log.error("refrescarDatosPantalla() - " + mensajeError + " - " + e.getMessage());
		}
	}

	@FXML
	protected void accionImprimirCopia(ActionEvent event) {
		try {
			log.debug("accionImprimirCopia() - Inicio de la impresión de la copia del documento");
			Map<String, Object> mapaParametros = new HashMap<String, Object>();

			String formatoImpresion = "";
			if (datosTicketSad != null) {
				formatoImpresion = documento.getFormatoImpresion();
				mapaParametros.put("datosSAD", datosTicketSad);
				log.debug("accionImprimirCopia() - Documento para imprimir : " + datosTicketSad);
			}
			else {
				formatoImpresion = ticketOperacion.getCabecera().getFormatoImpresion();
				mapaParametros.put("ticket", ticketOperacion);
				log.debug("accionImprimirCopia() - Documento para imprimir : " + ticketOperacion);
			}

			mapaParametros.put("urlQR", variablesService.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
			mapaParametros.put("esCopia", true);

			ServicioImpresion.imprimir(formatoImpresion, mapaParametros);
			log.debug("accionImprimirCopia() - ");

			AuditoriaDto auditoria = new AuditoriaDto();
			auditoria.setTipo("REIMPRESIÓN TICKET");
			auditoria.setUidTicket(ticketOperacion.getUidTicket());
			auditoriasService.guardarAuditoria(auditoria);
		}
		catch (DeviceException ex) {
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Fallo al imprimir ticket."), getStage());
		}
	}

	@Override
	public void comprobarPermisosUI() {
		Boolean tienePermisoDePantallaAnterior = (Boolean) getDatos().get(PERMISO_IMPRIMIR_TICKETS);
		if(tienePermisoDePantallaAnterior != null) {
			if(tienePermisoDePantallaAnterior) {
				btnImprimirCopia.setDisable(false);
			}
			else {
				btnImprimirCopia.setDisable(true);
			}
		}
		else {		
			try {
				super.compruebaPermisos(PERMISO_IMPRIMIR_TICKETS);
				btnImprimirCopia.setDisable(false);
			}
			catch (SinPermisosException ex) {
				btnImprimirCopia.setDisable(true);
			}
		}
	}

}
