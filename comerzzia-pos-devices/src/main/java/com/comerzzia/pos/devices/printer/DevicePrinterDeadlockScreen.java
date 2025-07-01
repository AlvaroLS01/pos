package com.comerzzia.pos.devices.printer;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class DevicePrinterDeadlockScreen extends DevicePrinterScreen {
	
	protected Exception lastException;

	@Override
	public void beginDocument(Map<String, Object> datos) {
		lastException = null;
		super.beginDocument(datos);
	}

	@Override
	public void printText(String texto, Integer size, String align, Integer style, String fontName, int fontSize) {
		if (StringUtils.contains(texto, "SLEEP")) {
            try {
                Thread.sleep(10000l);
				log.debug("FINALIZADO SLEEP");
            } catch (InterruptedException e) {
				lastException = e;
				return;
            }
		}else if (StringUtils.contains(texto, "DEADLOCK")) {
			final Object lock1 = String.class;
			final Object lock2 = Integer.class;

			Thread t = new Thread(() -> {
				synchronized (lock2) {
					try {
						// Espera un poco para asegurar que el hilo principal haya bloqueado lock1
						Thread.sleep(100);
					} catch (InterruptedException e) {
						lastException = e;
						return;
					}

					synchronized (lock1) {
						// Nunca llegará aquí
					}
				}
			});

			synchronized (lock1) {
				t.start();

				try {
					// Aquí provocamos el deadlock: el hilo principal espera lock2
					Thread.sleep(50); // Da tiempo al otro hilo a entrar en lock2
					synchronized (lock2) {
						// Nunca llegará aquí
					}
				} catch (InterruptedException e) {
					lastException = e;
					return;
				}
			}
		}
		super.printText(texto, size, align, style, fontName, fontSize);
	}

	@Override
	public boolean endDocument() {
		if(lastException != null) {
			log.error("Error en el hilo de impresión", lastException);
			return false;
		}

		return super.endDocument();
	}

	@Override
	public boolean reset() {
		try {
			Thread.sleep(3000l);
		} catch (InterruptedException e) {
		}
		return super.reset();
	}

}
