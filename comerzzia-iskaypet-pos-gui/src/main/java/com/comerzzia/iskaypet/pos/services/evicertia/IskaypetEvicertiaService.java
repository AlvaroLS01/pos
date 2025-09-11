package com.comerzzia.iskaypet.pos.services.evicertia;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.iskaypet.pos.gui.contrato.mascota.BarraProgresoView;
import com.comerzzia.iskaypet.pos.gui.contrato.mascota.EnvioContratoMascotaView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.email.EmailConstants;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.mascotas.DatosCabeceraContrato;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosBean;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketVentaAbono;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.services.ventas.pagos.mascotas.ServicioContratoMascotas;
import com.comerzzia.pos.core.gui.main.MainView;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.ibm.icu.text.SimpleDateFormat;

import javafx.stage.Stage;

@Service
public class IskaypetEvicertiaService {
	
	
	protected Logger log = Logger.getLogger(getClass());
	public static final String COD_LENGUAJE = "codLenguaje";
	public static final String UID_TICKET = "uidTicket";
	public static final String CODTICKET = "codTicket";
	public static final String LST_CONTRATOS = "lstContratos";
	public static final String RECUPERA_CONTRATO = "recuperaContrato";
	public static final String LINEAS_CON_MASCOTA = "lineasConMascota";
	
	public static final String OPERACION = "idOperacion";
	public static final String IDS_REENVIAR = "IDS_REENVIAR";
	public static final String OPERACION_REENVIAR = "OPERACION_REENVIAR_EVICERTIA";
	public static final String OPERACION_SALIR = "OPERACION_SALIR";
	public static final String OPERACION_APARCAR = "OPERACION_APARCAR";

	
	public static final String METODO_FIRMA_DIGITAL = "DIGITAL";
	public static final String METODO_FIRMA_MANUAL = "MANUAL";
	public static final String ASUNTO_CORREO = "subject";
	
	
	
	@Autowired
	protected ServicioContratoMascotas servicioContratoMascotas;
	
	
	
	public void openEvicertiaView(boolean ventanaProgreso, TicketVentaAbono ticket, Stage stage, HashMap<String, Object> datos, MainView mainView) {
		// GAP 09 VENTA DE ANIMALES CON CONTRATO
		List<IskaypetLineaTicket> lineasMascotas = new ArrayList<>();
		for (Object lineaTicket : ticket.getLineas()) {
			IskaypetLineaTicket linea = (IskaypetLineaTicket) lineaTicket;
			if (linea.isMascota() && linea.isRequiereContrato() ) {
				lineasMascotas.add(linea);
			}
		}

		if (!lineasMascotas.isEmpty()) {
			// ISK -153 INTEGRACION CZZ Y EVICERTIA
			String accionEmailSeleccionada = null;
			try {
				// Salvamos la opcion de envio email antes de borrar en las siguientes pantallas
				if (datos.get(EmailConstants.ACCION_SELECCIONADA) != null) {
					accionEmailSeleccionada = (String) datos.get(EmailConstants.ACCION_SELECCIONADA);
				}
				String codLenguaje = (String) ticket.getCabecera().getDatosFidelizado().getAdicionales().get(COD_LENGUAJE);
				String operacion = (String) datos.get(OPERACION);
				datos.put(UID_TICKET, ticket.getUidTicket());
				datos.put(LINEAS_CON_MASCOTA, lineasMascotas);
				
				//Antes el asunto era codTicket ahora el asunto del correo se tiene que generar
				
				datos.put(COD_LENGUAJE, codLenguaje);

				if(ventanaProgreso) {
					// En esta pantalla estamos llamando al servicio de evicertia y ejecutandolo en
					// segundo plano
					log.debug("aceptar() - Iniciamos la llamada...");
					mainView.showModalCentered(BarraProgresoView.class, datos, stage);
				}else {
					datos.put(LST_CONTRATOS, recuperarContratosTicket(ticket,codLenguaje));
				}
				datos.get(LST_CONTRATOS);
				datos.put(OPERACION, operacion);
				
				log.debug("aceptar() - Fin de la llamada");
				
				// Le pasamos la lista de contratos para mostar el resulado de las llamadas
				mainView.showModalCentered(EnvioContratoMascotaView.class, datos, stage);

				// Recuperamos la opcion de envío email para usarla
				if (accionEmailSeleccionada != null) {
					datos.put(EmailConstants.ACCION_SELECCIONADA, accionEmailSeleccionada);
				}
			}catch (Exception e) {
				log.error("aceptar() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}
		}

	}

	private List<DatosCabeceraContrato> recuperarContratosTicket(TicketVentaAbono ticket, String codLenguaje) {
		List<DatosCabeceraContrato> contratos = new ArrayList<>();

		String uidTicket = ticket.getUidTicket();

		for (LineaTicket lineaBase : ticket.getLineas()) {
			if (lineaBase instanceof IskaypetLineaTicket) {
				IskaypetLineaTicket linea = (IskaypetLineaTicket) lineaBase;
				
				if(linea.getContratoAnimal()!=null) {
					TicketContratosBean contrato = servicioContratoMascotas.getContratosRealizadoByPrimaryKey(uidTicket,linea.getIdLinea());
	
					DatosCabeceraContrato datosContrato = servicioContratoMascotas.convertir(linea, contrato, codLenguaje);
	
					contratos.add(datosContrato);
				}
			}
		}

		return contratos;
	}
	
    public boolean tieneContratoAnimalLineas(IskaypetTicketVentaAbono ticketRecuperado) {
        return ticketRecuperado.getLineas() != null &&
               ticketRecuperado.getLineas().stream()
                   .filter(linea -> linea instanceof IskaypetLineaTicket)
                   .map(linea -> (IskaypetLineaTicket) linea)
                   .anyMatch(linea -> linea.getContratoAnimal() != null);
    }

}
