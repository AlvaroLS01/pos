package com.comerzzia.iskaypet.pos.gui.contrato.mascota;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.mascotas.DatosCabeceraContrato;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosBean;
import com.comerzzia.iskaypet.pos.services.evicertia.IskaypetEvicertiaService;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.services.ventas.pagos.mascotas.ServicioContratoMascotas;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.application.Platform;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

@Component
public class BarraProgresoController extends WindowController implements Initializable, IContenedorBotonera {

	@Autowired
	protected ServicioContratoMascotas servicioContratoMascota;

	@FXML
	private Label lbTitulo;

	@FXML
	private HBox hbBarraProgreso;

	@FXML
	private ProgressBar pbBarraProgreso;

	@FXML
	protected Button btContinuar;

	@Autowired
	protected ServicioContratoMascotas servicioContratoMascotas;

	
	private List<IskaypetLineaTicket> lineasMascotas;
	private List<DatosCabeceraContrato> contratosFidelizado;

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException {
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		//Antes de inciar esta pantalla de envio, comprobamos si la variable está configurada
		String tituloSinConfiguracion = null;
		String msgConfiguracion = servicioContratoMascota.comprobarConfiguracionEnvio();
		if(StringUtils.isNotBlank(msgConfiguracion)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No está cofigurado el envío de contratos a Evicertia. {0}",msgConfiguracion), getStage());
			tituloSinConfiguracion= "Generando contratos sin envío...";
			lbTitulo.setText(I18N.getTexto(tituloSinConfiguracion));
		}

		lineasMascotas = (List<IskaypetLineaTicket>) getDatos().get(IskaypetEvicertiaService.LINEAS_CON_MASCOTA);
		Map<String, Object> datosAdicionalesFinal = new HashMap<>(getDatos());
		String uidTicket = (String) getDatos().get(IskaypetEvicertiaService.UID_TICKET);
		List<Integer> idsReenviar = (List<Integer>) getDatos().get(IskaypetEvicertiaService.IDS_REENVIAR);
		
		
		int contratosSinEnviar = (idsReenviar==null || idsReenviar.isEmpty())?(int) lineasMascotas.stream()
			    .filter(linea -> !linea.getContratoAnimal().isEnviado())
			    .count() : idsReenviar.size();
		
		if(StringUtils.isBlank(tituloSinConfiguracion)) {
			if (contratosSinEnviar == 0) { //CZZ-490
			    lbTitulo.setText(I18N.getTexto("Todos los contratos se enviaron correctamente a Evicertia"));
			    pbBarraProgreso.progressProperty().unbind();
			    pbBarraProgreso.setProgress(1.0); 
			} else {
			    String key = contratosSinEnviar == 1 
			        ? "Enviando {0} contrato a Evicertia..." 
			        : "Enviando {0} contratos a Evicertia...";
			    lbTitulo.setText(I18N.getTexto(key, contratosSinEnviar));
			}
			
		}
		
		btContinuar.setDisable(true);
		contratosFidelizado = new ArrayList<>();

		runTask(datosAdicionalesFinal, uidTicket, contratosSinEnviar, idsReenviar);
	}

	/**
	 * CZZ-490 - Extraido método de generación de task y ejecución
	 * @param datosAdicionalesFinal
	 * @param uidTicket
	 * @param contratosSinEnviar
	 * @param idsReenviar 
	 */
	private void runTask(Map<String, Object> datosAdicionalesFinal, String uidTicket, int contratosSinEnviar, List<Integer> idsReenviar) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				if(contratosSinEnviar ==0) {
					updateProgress(1, 1);
				}
				for (int i = 0; i < lineasMascotas.size(); i++) {
					try {
						boolean isReenvio = idsReenviar !=null && !idsReenviar.isEmpty();
						procesarLineaContratoMascota(datosAdicionalesFinal, uidTicket, contratosSinEnviar, i, isReenvio);
						 
					} catch (Exception e) {
						// Si hay algun error no controlado permite salir de la pantalla
						btContinuar.setDisable(false);
						btContinuar.requestFocus();
						return null;
					}
					
				}
				if(!lineasMascotas.isEmpty()) {
					Platform.runLater(() -> {
					    if (contratosSinEnviar == 1) {
					        lbTitulo.setText(I18N.getTexto("Enviado 1 contrato a Evicertia."));
					    } else {
					        lbTitulo.setText(I18N.getTexto("Enviados {0} contratos a Evicertia.", contratosSinEnviar));
					    }
					});
					
				}
				btContinuar.setDisable(false);
				btContinuar.requestFocus();
				return null;
			}

			/**
			 * CZZ-490 - Procesa las líneas de contrato de mascotas, generando o recuperando los contratos según su estado de envío, y los añade a la lista de contratos.
			 */
			private void procesarLineaContratoMascota(Map<String, Object> datosAdicionalesFinal, String uidTicket, int contratosSinEnviar, int i, boolean isReenvio) throws InterruptedException {
				DatosCabeceraContrato datosContrato = null;
				boolean contratoEnviado = lineasMascotas.get(i).getContratoAnimal().isEnviado();
				
				if((!isReenvio && !contratoEnviado) || (isReenvio && idsReenviar.contains(lineasMascotas.get(i).getIdLinea()))) {
					datosContrato =  servicioContratoMascotas.generarContrato(lineasMascotas.get(i), datosAdicionalesFinal);
					Thread.sleep(5);
					updateProgress((double) i+1,contratosSinEnviar );
				} else {
					TicketContratosBean contrato = servicioContratoMascotas.getContratosRealizadoByPrimaryKey(uidTicket, lineasMascotas.get(i).getIdLinea());
					datosContrato =  servicioContratoMascotas.convertir(lineasMascotas.get(i), contrato, (String) datosAdicionalesFinal.get(IskaypetEvicertiaService.COD_LENGUAJE));
				}
				contratosFidelizado.add(datosContrato);
			}
			
		};

		pbBarraProgreso.progressProperty().bind(task.progressProperty());

		Thread thread = new Thread(task);
		thread.start();
	}

	@Override
	public void initializeFocus() {
	}

	@FXML
	public void continuar() {
		getDatos().put(IskaypetEvicertiaService.LST_CONTRATOS, contratosFidelizado);
		getStage().close();
	}
	

}