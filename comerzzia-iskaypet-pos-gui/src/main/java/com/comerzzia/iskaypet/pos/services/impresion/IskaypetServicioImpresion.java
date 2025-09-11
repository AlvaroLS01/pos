package com.comerzzia.iskaypet.pos.services.impresion;

import com.comerzzia.iskaypet.pos.devices.impresora.IskaypetImpresoraHTML;
import com.comerzzia.pos.dispositivo.impresora.ImpresoraHTML;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import org.apache.log4j.Logger;

import java.util.Map;

public class IskaypetServicioImpresion extends ServicioImpresion {

    private static final Logger log = Logger.getLogger(IskaypetServicioImpresion.class.getName());

    public static String imprimirPantalla(String plantilla, Map<String, Object> parametros) throws DeviceException {
        log.debug("imprimirPantalla() - Previsualizando por pantalla: " + plantilla);
        IskaypetImpresoraHTML htmlPrinter = IskaypetImpresoraHTML.getInstance();
        htmlPrinter.inicializar();

        htmlPrinter.setPrevisualizacion(true);
        imprimir(htmlPrinter, plantilla, parametros);
        htmlPrinter.setPrevisualizacion(false);

        return htmlPrinter.getPrevisualizacion();
    }


}
