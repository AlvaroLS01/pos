package com.comerzzia.dinosol.pos.devices.recarga;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.dinosol.pos.devices.recarga.dto.DatosConexionServicioRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion.DatosPeticionCancelacionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion.DatosRespuestaCancelacionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.recarga.DatosPeticionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.recarga.DatosRespuestaRecargaDto;
import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.Dispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.recargamovil.IRecargaMovil;

public abstract class DispositivoRecargaAbstract extends Dispositivo implements IRecargaMovil {

	private Logger log = Logger.getLogger(DispositivoRecargaAbstract.class);
	
	private static final Integer NUM_REINTENTOS_DEFAULT = 12;
	private static final Integer TIEMPO_PERIODO_REINTENTOS_DEFAULT = 10000;

	protected DatosConexionServicioRecargaDto datosConexion;
	
	private Integer numIntentosAnulacionAutomatica;
	private Integer tiempoPeriodoAnulacionAutomatica;

	@Override
	protected void cargaConfiguracion(ConfiguracionDispositivo configuracion) throws DispositivoException {
		try {
			datosConexion = new DatosConexionServicioRecargaDto();
			
			XMLDocumentNode configNode = configuracion.getConfiguracionModelo().getConfigConexion();
		    					    
			numIntentosAnulacionAutomatica = configNode.getNodo("num_reintentos_cancelacion").getValueAsInteger();
			if (numIntentosAnulacionAutomatica == null) {
				numIntentosAnulacionAutomatica = NUM_REINTENTOS_DEFAULT;
			}
			
			tiempoPeriodoAnulacionAutomatica = configNode.getNodo("tiempo_periodo_reintento_cancelacion").getValueAsInteger();
			if (tiempoPeriodoAnulacionAutomatica == null) {
				tiempoPeriodoAnulacionAutomatica = TIEMPO_PERIODO_REINTENTOS_DEFAULT;
			}
		}
		catch (Exception ex) {
			log.error("cargaConfiguracion() - Error recuperando la información de configuración de recargas móviles:" + ex.getMessage(), ex);
		}
	}

	@Override
	public void conecta() throws DispositivoException {
	}

	@Override
	public void desconecta() throws DispositivoException {
	}

	public boolean isConfigurado() {
		return datosConexion.isConfigurado();
	}

	public abstract DatosRespuestaRecargaDto recargar(DatosPeticionRecargaDto datosPeticionRecargaDto) throws DispositivoException;

	public abstract DatosRespuestaCancelacionRecargaDto cancelarRecarga(DatosPeticionCancelacionRecargaDto datosPeticionCancelacionRecarga) throws DispositivoException;

	public void cancelarRecargaAutomatica(DatosPeticionCancelacionRecargaDto datosPeticionCancelacionRecarga) {
		final int[] counter = { 1 };
		final Timer timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				if (counter[0] <= numIntentosAnulacionAutomatica) {
					try {
						log.debug("accionRecargar() - Intento " + counter[0] + " de " + numIntentosAnulacionAutomatica + " de cancelación de la recarga.");
						counter[0]++;
						DatosRespuestaCancelacionRecargaDto responseCancelacion = cancelarRecarga(datosPeticionCancelacionRecarga);
						if (responseCancelacion.isRespuestaOk()) {
							log.debug("accionRecargar() - La cancelación de la recarga se ha realizado de forma exitosa.");
							timer.cancel();
							timer.purge();
						}
					}
					catch (Exception e) {
						log.error("accionRecargar() - Se ha producido un error al realizar la cancelación, reintentandolo en " + tiempoPeriodoAnulacionAutomatica / 1000 + " segundos: "
						        + e.getMessage(), e);
					}
				}
				else {
					timer.cancel();
					timer.purge();
				}
			}
		}, 0, tiempoPeriodoAnulacionAutomatica);
	}

}
