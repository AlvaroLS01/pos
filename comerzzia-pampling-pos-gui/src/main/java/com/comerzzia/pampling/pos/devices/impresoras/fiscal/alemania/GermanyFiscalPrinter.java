package com.comerzzia.pampling.pos.devices.impresoras.fiscal.alemania;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.pampling.pos.gui.configuracion.impresoras.fiscal.alemania.ConfiguracionGermanyFiscalPrinter;
import com.comerzzia.pampling.pos.services.fiscal.alemania.GermanyFiscalPrinterService;
import com.comerzzia.pampling.pos.services.fiscal.alemania.epos.EposOutput;
import com.comerzzia.pampling.pos.services.ticket.cabecera.PamplingCabeceraTicket;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.dispositivo.fiscal.IFiscalPrinter;
import com.comerzzia.pos.dispositivo.impresora.ImpresoraDriver;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.config.SpringContext;

import javafx.stage.Stage;
@SuppressWarnings("unchecked")
public class GermanyFiscalPrinter extends ImpresoraDriver implements IFiscalPrinter, IPrinter{
	
	private static final Logger log = Logger.getLogger(GermanyFiscalPrinter.class);

	@Autowired
	private GermanyFiscalPrinterService germanyFiscalPrinterService;
	
	@Override
	public void conecta() {
		if (Dispositivos.getInstance().getImpresora1() instanceof GermanyFiscalPrinter && Dispositivos.getInstance().getImpresora1().getConfiguracion().getNombreConexion().equals(GermanyFiscalPrinterService.NOMBRE_CONEXION_TSE)) {
				try {
					ConfiguracionDispositivo impresora = Dispositivos.getInstance().getImpresora1().getConfiguracion();
					String ip = impresora.getParametrosConfiguracion().get("IP");
					if (!StringUtils.isBlank(ip)) {
						getEpsonTSEService().procesoInicialTSE(ip);
						compruebaAutoTest();
					}
				}
				catch (Exception e) {
					log.error("conecta() - Ha ocurrido un error al realizar el proceso inicial del TSE", e);
				}
			}
	}


	@Override
	public void configurar(Stage stage) {
		log.debug("configurar() - Abrienda pantalla de configuración de EpsonTM30 ...");
		super.configurar(stage);

		HashMap<String, Object> params = new HashMap<>();
		params.put(GermanyFiscalPrinterService.PARAMETRO_SALIDA_CONFIGURACION, getConfiguracion().getParametrosConfiguracion());

		POSApplication.getInstance().getMainView().showModalCentered(ConfiguracionGermanyFiscalPrinter.class, params, stage);

		/*
		 * Guardamos en sesión los datos de configuración, realmente no están guardado del todo hasta que no se Acepta
		 * los cambios en la pantalla de "Configuración TPV"
		 */
		if (params.containsKey(GermanyFiscalPrinterService.PARAMETRO_SALIDA_CONFIGURACION)) {
			Map<String, String> parametrosConfiguracion = (Map<String, String>) params.get(GermanyFiscalPrinterService.PARAMETRO_SALIDA_CONFIGURACION);
			getConfiguracion().setParametrosConfiguracion(parametrosConfiguracion);
			log.debug("configurar() - Parámetros de configuración de EpsonTM30 nuevos : " + parametrosConfiguracion);
		}
		
	}

	@Override
	public void desconecta() {
		if (Dispositivos.getInstance().getImpresora1() instanceof GermanyFiscalPrinter && Dispositivos.getInstance().getImpresora1().getConfiguracion().getNombreConexion().equals(GermanyFiscalPrinterService.NOMBRE_CONEXION_TSE)) {
				try {
					germanyFiscalPrinterService.desconectaTSE();
				}
				catch (IOException e) {
					log.error("desconecta() - Ha ocurrido un error al realizar el proceso final del TSE", e);
				}
			}
	}

	@Override
	public boolean isConfigurable() {
		return StringUtils.isNotBlank(getConfiguracion().getNombreConexion()) && getConfiguracion().getNombreConexion().equals(GermanyFiscalPrinterService.NOMBRE_CONEXION_TSE);
	}

	@Override
	public void sendTicket(TicketVentaAbono ticket) throws Exception {
		log.debug("sendTicket() - Inicio del proceso de finishTransaction del TSE...");

		try {
			germanyFiscalPrinterService.enviarPeticion(germanyFiscalPrinterService.getGetStorageInfo());
			String respuestaGetStorageInfo = germanyFiscalPrinterService.lecturaSocket();

			List<PagoTicket> pagos = ticket.getPagos();
			BigDecimal pagosEfectivo = null;
			BigDecimal pagosNoEfectivo = null;
			for (PagoTicket pagoTicket : pagos) {
				if (pagoTicket.getCodMedioPago().equals("0000")) {
					pagosEfectivo = new BigDecimal(0);
					pagosEfectivo = pagosEfectivo.add(pagoTicket.getImporte());
				}
				else {
					pagosNoEfectivo = new BigDecimal(0);
					pagosNoEfectivo = pagosNoEfectivo.add(pagoTicket.getImporte());
				}
			}

			EposOutput eposOutput = ((PamplingCabeceraTicket) ticket.getCabecera()).getTse();
			List<String> listaCampos = new ArrayList<String>();

			String peticionFinishTransaction = germanyFiscalPrinterService.finishTransaction(GermanyFiscalPrinterService.PROCESSTYPE_KASSENBELEG_V1, GermanyFiscalPrinterService.TIPO_TRANSACCION_BELEG,
					ticket.getTotales().getTotal(), pagosEfectivo, pagosNoEfectivo, Integer.parseInt(eposOutput.getTransactionNumber()), false);
			germanyFiscalPrinterService.enviarPeticion(peticionFinishTransaction);
			String respuestaFinishTransaction = germanyFiscalPrinterService.lecturaSocket();

			listaCampos = new ArrayList<>();
			listaCampos.add("logTime");
			HashMap<String, String> mapaCampos = germanyFiscalPrinterService.tratamientoRespuesta(respuestaFinishTransaction, listaCampos);
			String logTimeFinish = mapaCampos.get("logTime");
			String result = germanyFiscalPrinterService.tratamientoRespuestaResult(respuestaFinishTransaction);

			if (result.equals(GermanyFiscalPrinterService.EXECUTION_OK)) {
				eposOutput.setLogTimeFinish(logTimeFinish);
				String processData = germanyFiscalPrinterService.generaProcessData(GermanyFiscalPrinterService.PROCESSTYPE_KASSENBELEG_V1, GermanyFiscalPrinterService.TIPO_TRANSACCION_BELEG, ticket.getTotales().getTotal(),
				        pagosEfectivo, pagosNoEfectivo);
				String QR = germanyFiscalPrinterService.generaQR(respuestaGetStorageInfo, GermanyFiscalPrinterService.PROCESSTYPE_KASSENBELEG_V1, processData, eposOutput.getTransactionNumber(),
				        eposOutput.getSignatureCounter(), eposOutput.getLogTimeStart(), logTimeFinish, eposOutput.getSignature());

				log.debug("sendTicket() - QR generado - " + QR);

				eposOutput.setQr(QR);
			}
			else {
				log.error("sendTicket() - Error al realizar el proceso de TSE - " + result);
			}
		}
		catch (Exception e) {
			log.error("sendTicket() - Error al realizar el proceso de TSE - " + e.getMessage());

		}
	}

	public boolean compruebaAutoTest() throws Exception {
		log.debug("compruebaAutoTest() - Inicio de comprobacion del status del TSE");
		if (germanyFiscalPrinterService.socketConectado()) {
			List<String> listaCampos = new ArrayList<>();
			listaCampos.add("timeUntilNextSelfTest");
			germanyFiscalPrinterService.enviarPeticion(germanyFiscalPrinterService.getGetStorageInfo());

			String respuestaGetStorageInfo = germanyFiscalPrinterService.lecturaSocket();
			HashMap<String, String> mapaCampos = germanyFiscalPrinterService.tratamientoRespuestaGetStorageInfo(respuestaGetStorageInfo, listaCampos);
			String timeUntilNextSelfTest = mapaCampos.get("timeUntilNextSelfTest");

			if (StringUtils.isNotBlank(timeUntilNextSelfTest)) {
				log.debug("compruebaAutoTest() - Se ha recuperado la informacion de GetStorageInfo - timeUntilNextSelfTest [" + timeUntilNextSelfTest + "]");
				if (Integer.parseInt(timeUntilNextSelfTest) == 0) {
					germanyFiscalPrinterService.autoTest();
				}
			}
			return true;
		}
		else {
			log.warn("compruebaAutoTest() - Socket no conectado");
			return false;
		}
		
	}

	public String tseAperturaCaja(BigDecimal saldo) {
		String respuesta = null;
		try {
			if (Dispositivos.getInstance().getImpresora1().getConfiguracion().getNombreConexion().equals(GermanyFiscalPrinterService.NOMBRE_CONEXION_TSE)) {
				if (!germanyFiscalPrinterService.socketConectado()) {
					try {
						ConfiguracionDispositivo impresora = Dispositivos.getInstance().getImpresora1().getConfiguracion();
						String ip = impresora.getParametrosConfiguracion().get("IP");
						if (!StringUtils.isBlank(ip)) {
							germanyFiscalPrinterService.procesoInicialTSE(ip);
						}
					}
					catch (Exception e) {
						log.error("accionAceptar() - Ha ocurrido un error al realizar el proceso inicial del TSE", e);
					}
				}

				String peticionStartTransaction = germanyFiscalPrinterService.startTransaction(GermanyFiscalPrinterService.PROCESSTYPE_SONSTINGERVORGANG, GermanyFiscalPrinterService.TIPO_TRANSACCION_TAGESSTART, saldo, null, null, true);
				germanyFiscalPrinterService.enviarPeticion(peticionStartTransaction);
				String respuestaStartTransaction = germanyFiscalPrinterService.lecturaSocket();

				respuesta = germanyFiscalPrinterService.tratamientoRespuestaResult(respuestaStartTransaction);
				if (respuesta.equals(GermanyFiscalPrinterService.EXECUTION_OK)) {
					List<String> listaCampos = new ArrayList<>();
					listaCampos.add("transactionNumber");
					HashMap<String, String> mapaCampos = germanyFiscalPrinterService.tratamientoRespuesta(respuestaStartTransaction, listaCampos);
					String transactionNumber = mapaCampos.get("transactionNumber");

					String peticionFinishTransaction = germanyFiscalPrinterService.finishTransaction(GermanyFiscalPrinterService.PROCESSTYPE_SONSTINGERVORGANG, GermanyFiscalPrinterService.TIPO_TRANSACCION_TAGESSTART, saldo.abs(), null,
					        null, Integer.parseInt(transactionNumber), false);
					germanyFiscalPrinterService.enviarPeticion(peticionFinishTransaction);
					String respuestaFinishTransaction = germanyFiscalPrinterService.lecturaSocket();
					respuesta = germanyFiscalPrinterService.tratamientoRespuestaResult(respuestaFinishTransaction);
				}
			}

		}
		catch (Exception e) {
			log.error("tseAperturaCaja() - Error al realizar el proceso de apertura de caja -" + e.getMessage());
		}

		return respuesta;
	}

	public String tseCierreCaja(BigDecimal saldo) {
		String respuesta = null;
		saldo = saldo.setScale(2, BigDecimal.ROUND_HALF_UP);
		try {
			if (Dispositivos.getInstance().getImpresora1().getConfiguracion().getNombreConexion().equals(GermanyFiscalPrinterService.NOMBRE_CONEXION_TSE)) {
				String peticionStartTransaction = germanyFiscalPrinterService.startTransaction(GermanyFiscalPrinterService.PROCESSTYPE_SONSTINGERVORGANG, GermanyFiscalPrinterService.TIPO_TRANSACCION_TAGESENDE, saldo, null, null, true);
				germanyFiscalPrinterService.enviarPeticion(peticionStartTransaction);
				String respuestaStartTransaction = germanyFiscalPrinterService.lecturaSocket();

				respuesta = germanyFiscalPrinterService.tratamientoRespuestaResult(respuestaStartTransaction);
				if (respuesta.equals(GermanyFiscalPrinterService.EXECUTION_OK)) {
					List<String> listaCampos = new ArrayList<>();
					listaCampos.add("transactionNumber");
					HashMap<String, String> mapaCampos = germanyFiscalPrinterService.tratamientoRespuesta(respuestaStartTransaction, listaCampos);
					String transactionNumber = mapaCampos.get("transactionNumber");

					String peticionFinishTransaction = germanyFiscalPrinterService.finishTransaction(GermanyFiscalPrinterService.PROCESSTYPE_SONSTINGERVORGANG, GermanyFiscalPrinterService.TIPO_TRANSACCION_TAGESENDE, saldo.abs(), null,
					        null, Integer.parseInt(transactionNumber), false);
					germanyFiscalPrinterService.enviarPeticion(peticionFinishTransaction);
					String respuestaFinishTransaction = germanyFiscalPrinterService.lecturaSocket();
					respuesta = germanyFiscalPrinterService.tratamientoRespuestaResult(respuestaFinishTransaction);
				}

				try {
					germanyFiscalPrinterService.desconectaTSE();
				}
				catch (IOException e) {
					log.error("tseCierreCaja() - Error al cerrar el socket del TSE: " + e.getCause(), e);
				}

			}
		}
		catch (Exception e) {
			log.error("tseCierreCaja() - Error al realizar el proceso de cierre de caja -" + e.getMessage());
		}

		return respuesta;
	}

	
	@Override
	public ConfiguracionDispositivo getConfiguracion() {
		return configuracion;
	}

	@Override
	public void setConfiguracion(ConfiguracionDispositivo config) throws DispositivoException {
		this.configuracion = config;
		if (config.getConfiguracionModelo() != null) {
			cargaConfiguracion(config);
		}
	}

	
	public GermanyFiscalPrinterService getEpsonTSEService() {
		if(germanyFiscalPrinterService == null) {
			germanyFiscalPrinterService = SpringContext.getBean(GermanyFiscalPrinterService.class);
		}
		return germanyFiscalPrinterService;
	}

	@Override
	public void fiscalDailyReport() {
		
	}
	
	@Override
	public boolean terminarDocumento() {
		return super.terminarDocumento();
	}


	public void tseApuntes(BigDecimal saldo) throws Exception {
		saldo = saldo.setScale(2, BigDecimal.ROUND_HALF_UP);

		String tipoTransaccion = GermanyFiscalPrinterService.TIPO_TRANSACCION_EINZAHLUNG;
		if (saldo.compareTo(new BigDecimal(0)) < 0) {
			tipoTransaccion = GermanyFiscalPrinterService.TIPO_TRANSACCION_ENTNAHME;
		}

			if (Dispositivos.getInstance().getImpresora1() instanceof GermanyFiscalPrinter) {
				if (Dispositivos.getInstance().getImpresora1().getConfiguracion().getNombreConexion().equals(GermanyFiscalPrinterService.NOMBRE_CONEXION_TSE)) {
					if (!germanyFiscalPrinterService.socketConectado()) {
							ConfiguracionDispositivo impresora = Dispositivos.getInstance().getImpresora1().getConfiguracion();
							String ip = impresora.getParametrosConfiguracion().get("IP");
							if (!StringUtils.isBlank(ip)) {
								germanyFiscalPrinterService.procesoInicialTSE(ip);
							}
					}

					String peticionStartTransaction = germanyFiscalPrinterService.startTransaction(GermanyFiscalPrinterService.PROCESSTYPE_SONSTINGERVORGANG, tipoTransaccion, saldo.abs(), null, null, true);
					germanyFiscalPrinterService.enviarPeticion(peticionStartTransaction);
					String respuestaStartTransaction = germanyFiscalPrinterService.lecturaSocket();

					List<String> listaCampos = new ArrayList<String>();
					listaCampos.add("transactionNumber");
					HashMap<String, String> mapaCampos = germanyFiscalPrinterService.tratamientoRespuesta(respuestaStartTransaction, listaCampos);
					String transactionNumber = mapaCampos.get("transactionNumber");

					String peticionFinishTransaction = germanyFiscalPrinterService.finishTransaction(GermanyFiscalPrinterService.PROCESSTYPE_SONSTINGERVORGANG, tipoTransaccion, saldo.abs(), null, null,
					        Integer.parseInt(transactionNumber), false);
					germanyFiscalPrinterService.enviarPeticion(peticionFinishTransaction);
					germanyFiscalPrinterService.lecturaSocket();

					// String resultFinish = germanyFiscalPrinterService.tratamientoRespuestaResult(respuestaFinishTransaction);
				}
			}
		}

}
