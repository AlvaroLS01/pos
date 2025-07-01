
package com.comerzzia.pos.devices.drivers.javapos.facade.exceptions;

import org.apache.log4j.Logger;

public class JPosErrorException extends Exception{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6822169680536001206L;
	private static Logger log = Logger.getLogger(JPosErrorException.class);
    
    public static void DefinirJPosErrorException (int codError){
        switch(codError){
            case jpos.JposConst.JPOS_E_CLOSED:
                log.error("Código de error: "+codError+". Se ha intentado entrar a un dispositivo JavaPos cerrado.");
                break;
            case jpos.JposConst.JPOS_E_CLAIMED:
                log.error("Código de error: "+codError+". Se ha intentado entrar a un dispositivo JavaPos que ya está intentando ser accedido por otra instancia de DeviceControl.");
                break;
            case jpos.JposConst.JPOS_E_NOTCLAIMED:
                log.error("Código de error: "+codError+". Se tiene que acceder al dispositivo en modo exclusivo.");
                break;
            case jpos.JposConst.JPOS_E_NOSERVICE:
                log.error("Código de error: "+codError+". No se ha podido establecer comunicación con el Servicio.");
                break;
            case jpos.JposConst.JPOS_E_DISABLED:
                log.error("Código de error: "+codError+". No se ha podido realizar la operación mientras el dispositivo está desactivado.");
                break;
            case jpos.JposConst.JPOS_E_ILLEGAL:
                log.error("Código de error: "+codError+". La operación solicitada es ilegal o no está soportada para este dispositivo.");
                break;
            case jpos.JposConst.JPOS_E_NOHARDWARE:
                log.error("Código de error: "+codError+". El dispositivo no está conectado al sistema o está apagado.");
                break;
            case jpos.JposConst.JPOS_E_OFFLINE:
                log.error("Código de error: "+codError+". El dispositivo está offline.");
                break;
            case jpos.JposConst.JPOS_E_NOEXIST:
                log.error("Código de error: "+codError+". El nombre de fichero (u otro valor) no existe.");
                break;
            case jpos.JposConst.JPOS_E_EXISTS:
                log.error("Código de error: "+codError+". El nombre de fichero (u otro valor) ya existe.");
                break;
            case jpos.JposConst.JPOS_E_FAILURE:
                log.error("Código de error: "+codError+". No se ha podido realizar la operación solicitada.");
                break;
            case jpos.JposConst.JPOS_E_TIMEOUT:
                log.error("Código de error: "+codError+". Superado TimeOut del dispositivo.");
                break;
            case jpos.JposConst.JPOS_E_BUSY:
                log.error("Código de error: "+codError+". El estado actual del dispositivo no permite esta operación.");
                break;
            case jpos.JposConst.JPOS_E_EXTENDED:
                log.error("Código de error: "+codError+". Ha ocurrido un error específico del dispositivo, mirar el código de error extendido.");
                break;
            case jpos.JposConst.JPOS_E_DEPRECATED:
                log.error("Código de error: "+codError+". La operación solicitada está deprecada.");
                break;
            default:
                log.error("Código de error: "+codError+". Error inesperado de JavaPos");
                break;
        }
    }
}
