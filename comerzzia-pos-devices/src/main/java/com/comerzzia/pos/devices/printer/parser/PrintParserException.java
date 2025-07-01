


package com.comerzzia.pos.devices.printer.parser;

import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.pos.util.i18n.I18N;


public class PrintParserException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public PrintParserException() {
    }

    public PrintParserException(String message) {
        super(message);
    }

    public PrintParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrintParserException(Throwable cause) {
        super(cause);
    }

    public PrintParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
    	return I18N.getText("Error parseando documento para enviar instrucciones al dispositivo de impresión configurado");
    }
}
