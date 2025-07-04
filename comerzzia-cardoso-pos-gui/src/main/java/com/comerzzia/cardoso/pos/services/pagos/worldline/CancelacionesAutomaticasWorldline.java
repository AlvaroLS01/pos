package com.comerzzia.cardoso.pos.services.pagos.worldline;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.devices.dispositivo.tarjeta.worldline.TefWorldlineManager;
import com.comerzzia.pos.services.payments.PaymentsManager;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;

@Component
public class CancelacionesAutomaticasWorldline {

	protected static Logger log = Logger.getLogger(CancelacionesAutomaticasWorldline.class);

	@Autowired
	protected PaymentsManager paymentsManager;
	@Autowired
	protected PaymentsMethodsConfiguration paymentsMethodsConfiguration;

	protected TefWorldlineManager worldlineManager;

	public void init() {
		// Se inicia con 10 minutos de delay para dar tiempo al pinpad de hacer su inicializacion al completo
		final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

		paymentsManager.setPaymentsMethodsConfiguration(paymentsMethodsConfiguration);
		worldlineManager = (TefWorldlineManager) paymentsManager.getPaymentsMehtodManagerAvailables().values().stream().filter(manager -> manager instanceof TefWorldlineManager).findFirst().get();
		
		if(worldlineManager == null) {
			log.warn("init() - No se ha encontrado el manager de Worldline. No se puede iniciar el proceso de cancelación automática");
		}

		scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){

			@Override
			public void run() {
				try {
					URL urlCancelaciones = Thread.currentThread().getContextClassLoader().getResource("cancelaciones");
					File cancelacionesFolder = new File(urlCancelaciones.getPath());
					File[] cancelaciones = cancelacionesFolder.listFiles(new FilenameFilter(){

						@Override
						public boolean accept(File dir, String name) {
							if (name.equals("ignore.txt")) {
								return false;
							}
							return true;
						}
					});

					Arrays.sort(cancelaciones, Comparator.comparingLong(File::lastModified));
					for (File cancelacion : cancelaciones) {
						long milisUltimaModificacion = cancelacion.lastModified();
						long milisAhora = new Date().getTime();

						// Archivo con antigüedad mayor a 10 horas
						if ((milisAhora - milisUltimaModificacion) >= 36000000) {
							URL urlErroneos = Thread.currentThread().getContextClassLoader().getResource("erroneos");
							File erroneosFolder = new File(urlErroneos.getPath() + "\\" + cancelacion.getName());
							Files.copy(cancelacion.toPath(), erroneosFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
							log.info("CancelacionesAutomaticasWorldline - run() - Se ha traspasado el archivo " + cancelacion.getName() + " a la carpeta erroneos");
							cancelacion.delete();

						}
						// Archivo con antigüedad mayor a 5 minutos
						else if ((milisAhora - milisUltimaModificacion) >= 300000) {
							String fileName = cancelacion.getName();
							String uidTicket = fileName.substring(0, fileName.indexOf("."));
							worldlineManager.doAutomaticCancel(uidTicket, true);
							cancelacion.delete();
						}

					}
				}
				catch (Exception e) {
					log.warn("CancelacionesAutomaticasWorldline - run() - Ha ocurrido un error al realizar la cancelacion automática de un pago: " + e.getMessage(), e);
				}
			}
		}, 10, 15, TimeUnit.MINUTES);
	}
}
