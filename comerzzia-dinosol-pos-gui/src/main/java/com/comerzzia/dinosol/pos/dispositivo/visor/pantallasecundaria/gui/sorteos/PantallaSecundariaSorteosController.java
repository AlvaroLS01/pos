package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.sorteos;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.devices.fidelizacion.DinoFidelizacion;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.dinosol.pos.services.ticket.sorteos.ParticipacionDTO;
import com.comerzzia.dinosol.pos.services.ticket.sorteos.ParticipacionesDTO;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

@Component
public class PantallaSecundariaSorteosController extends Controller {

	@FXML
	private VBox vbIncidenciaTecnica, vbTicketDigital, vbTicketFisicoFidelizado, vbNoFidelizadoPremios, vbNoFidelizadoSinPremios;

	@FXML
	private Label lbIncidenciaTecnica, lbTicketFisicoFidelizado, lbNoFidelizadoPremios, lbNoFidelizadoSinPremios, lbTicketDigital;

	@FXML
	private AnchorPane PantallasVentas;

	private String textoIncidenciaTecnica, textoTicketFisicoFidelizado, textoNoFidelizadoPremios, textoNoFidelizadoSinPremios, textoTicketDigital;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		textoIncidenciaTecnica = lbIncidenciaTecnica.getText();
		textoTicketFisicoFidelizado = lbTicketFisicoFidelizado.getText();
		textoNoFidelizadoPremios = lbNoFidelizadoPremios.getText();
		textoNoFidelizadoSinPremios = lbNoFidelizadoSinPremios.getText();
		textoTicketDigital = lbTicketDigital.getText();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
	}

	@Override
	public void initializeFocus() {
	}

	public void pintarMensaje(DinoTicketVentaAbono ticket) throws Exception {
		ParticipacionesDTO participacionesDTO = ((DinoTicketVentaAbono) ticket).getParticipaciones();

		obtenerDatosSorteoVisor(participacionesDTO);
		if (ticket.hayParticipacionesNoSolicitadas()) {
			mostrarMensajeIncidenciaTecnica(participacionesDTO);
		}
		else {
			if (ticket.getCabecera().getDatosFidelizado() != null) {
				if (ticket.getCabecera().getDatosFidelizado().getAdicionales() != null) {
					Object ticketDigital = ticket.getCabecera().getDatosFidelizado().getAdicionales().get(DinoFidelizacion.FIDELIZADO_TICKET_DIGITAL);
					if (ticketDigital.equals(true) || ticketDigital.equals("true")) {
						mostrarMensajeTicketDigital();
					}
					else {
						mostrarMensajeTicketFisicoFidelizado(participacionesDTO);
					}
				}
				else {
					mostrarMensajeTicketFisicoFidelizado(participacionesDTO);
				}
			}
			else {
				int premios = participacionesDTO.getParticipacionesPremiadas();
				if (premios > 0) {
					mostrarMensajeNoFidelizadoConPremios(participacionesDTO, premios);
				}
				else {
					mostrarMensajeNoFidelizadoSinPremios(participacionesDTO);
				}
			}
		}
	}

	public void obtenerDatosSorteoVisor(ParticipacionesDTO participacion) {
		textoIncidenciaTecnica = participacion.getTextoVisorIncidenciaTecnica().replace("|", "\n");
		textoNoFidelizadoPremios = participacion.getTextoVisorConPremioSinApp().replace("|", "\n");
		textoNoFidelizadoSinPremios = participacion.getTextoVisorSinPremioSinApp().replace("|", "\n");
		textoTicketFisicoFidelizado = participacion.getTextoVisorIdentificadoTicketFisico().replace("|", "\n");
		textoTicketDigital = participacion.getTextoVisorIdentificadoTicketDigital().replace("|", "\n");
	}

	protected void mostrarMensajeNoFidelizadoSinPremios(ParticipacionesDTO participacionesDTO) {
		PantallasVentas.getStyleClass().clear();
		PantallasVentas.getStyleClass().add("fondoSinPremio");

		vbTicketDigital.setVisible(false);
		vbIncidenciaTecnica.setVisible(false);
		vbNoFidelizadoPremios.setVisible(false);
		vbTicketFisicoFidelizado.setVisible(false);

		vbNoFidelizadoSinPremios.setVisible(true);

		String texto = sustituirParametros(participacionesDTO, textoNoFidelizadoSinPremios);
		
		lbNoFidelizadoSinPremios.setText(texto);
	}

	protected void mostrarMensajeNoFidelizadoConPremios(ParticipacionesDTO participacionesDTO, int premios) {
		PantallasVentas.getStyleClass().clear();
		PantallasVentas.getStyleClass().add("fondoConPremio");

		vbTicketDigital.setVisible(false);
		vbIncidenciaTecnica.setVisible(false);
		vbTicketFisicoFidelizado.setVisible(false);
		vbNoFidelizadoSinPremios.setVisible(false);

		vbNoFidelizadoPremios.setVisible(true);

		String texto = sustituirParametros(participacionesDTO, textoNoFidelizadoPremios);
		
		texto = texto.replaceAll("#NPP#", String.valueOf(premios));
		if (premios == 1) {
			texto = texto.replaceAll("premios", "premio");
		}
		
		lbNoFidelizadoPremios.setText(texto);
	}

	protected void mostrarMensajeTicketFisicoFidelizado(ParticipacionesDTO participacionesDTO) {
		PantallasVentas.getStyleClass().clear();
		PantallasVentas.getStyleClass().add("fondoAppTicketFisico");

		vbTicketDigital.setVisible(false);
		vbIncidenciaTecnica.setVisible(false);
		vbNoFidelizadoPremios.setVisible(false);
		vbNoFidelizadoSinPremios.setVisible(false);

		vbTicketFisicoFidelizado.setVisible(true);

		String texto = sustituirParametros(participacionesDTO, textoTicketFisicoFidelizado);
		
		lbTicketFisicoFidelizado.setText(texto);
	}

	protected void mostrarMensajeIncidenciaTecnica(ParticipacionesDTO participacionesDTO) {
		PantallasVentas.getStyleClass().clear();
		PantallasVentas.getStyleClass().add("fondoIncidencia");

		vbTicketDigital.setVisible(false);
		vbTicketFisicoFidelizado.setVisible(false);
		vbNoFidelizadoPremios.setVisible(false);
		vbNoFidelizadoSinPremios.setVisible(false);

		vbIncidenciaTecnica.setVisible(true);

		String texto = sustituirParametros(participacionesDTO, textoIncidenciaTecnica);

		lbIncidenciaTecnica.setText(texto);
	}

	private String sustituirParametros(ParticipacionesDTO participacionesDTO, String textoDefecto) {
		String texto = textoDefecto.replaceAll("#NP#", String.valueOf(participacionesDTO.getParticipacionesObtenidas()));

		List<ParticipacionDTO> participaciones = participacionesDTO.getListaParticipaciones();
		for (ParticipacionDTO participacion : participaciones) {
			String codigoParticipacion = participacion.getCodParticipacion();
			texto = texto.replaceFirst("#CP#", codigoParticipacion);
		}

		if (participacionesDTO.getParticipacionesObtenidas() == 1) {
			texto = texto.replaceAll("veces", "vez");
		}
		return texto;
	}

	protected void mostrarMensajeTicketDigital() {
		PantallasVentas.getStyleClass().clear();
		PantallasVentas.getStyleClass().add("fondoAppTicketDigital");

		vbIncidenciaTecnica.setVisible(false);
		vbTicketFisicoFidelizado.setVisible(false);
		vbNoFidelizadoPremios.setVisible(false);
		vbNoFidelizadoSinPremios.setVisible(false);
		lbTicketDigital.setText(textoTicketDigital);
		vbTicketDigital.setVisible(true);
	}

}
