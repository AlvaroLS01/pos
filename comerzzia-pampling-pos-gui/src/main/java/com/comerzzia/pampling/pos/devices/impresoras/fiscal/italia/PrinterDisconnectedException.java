package com.comerzzia.pampling.pos.devices.impresoras.fiscal.italia;

import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;

/**
 * Excepción personalizada para indicar que la impresora está desconectada o en un estado incorrecto.
 */
public class PrinterDisconnectedException extends DispositivoException {
    public PrinterDisconnectedException(String message) {
        super(message);
    }

    public PrinterDisconnectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
