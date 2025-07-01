
package com.comerzzia.pos.devices.drivers.escpos.escpos;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

// import javax.comm.*; // Java comm library
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier; // RXTX comm library
import gnu.io.SerialPort;

public class EscPosPrinterWritterRXTX extends EscPosPrinterWritter /* implements SerialPortEventListener */ {
    
	private static Logger log = Logger.getLogger(EscPosPrinterWritterRXTX.class);
	
    private CommPortIdentifier m_PortIdPrinter;
    private CommPort m_CommPortPrinter;  
    
    private OutputStream m_out;
    private String puerto;
	private int velocidadImpresion;
	private int bitsTicket;
	private int bitStop;
	private int bitParidad;
    
    /** Creates a new instance of PrinterWritterComm */
    public EscPosPrinterWritterRXTX(String puerto, int velocidadImpresion, int bitsTicket, int bitStop, int bitParidad) {
		this.puerto = puerto;
		this.velocidadImpresion = velocidadImpresion;
		this.bitsTicket = bitsTicket;
		this.bitStop = bitStop;
		this.bitParidad = bitParidad;
        m_out = null;
    }
    
    protected void internalWrite(byte[] data) {
        try {  
            if (m_out == null) {
                m_PortIdPrinter = CommPortIdentifier.getPortIdentifier(puerto); // Tomamos el puerto                   
                m_CommPortPrinter = m_PortIdPrinter.open("PORTID", 2000); // Abrimos el puerto       

                m_out = m_CommPortPrinter.getOutputStream(); // Tomamos el chorro de escritura   

                if (m_PortIdPrinter.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    ((SerialPort)m_CommPortPrinter).setSerialPortParams(velocidadImpresion, bitsTicket, bitStop, bitParidad);
//                    ((SerialPort)m_CommPortPrinter).setSerialPortParams(9200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE); 
//                    ((SerialPort)m_CommPortPrinter).setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);  // this line prevents the printer tmu220 to stop printing after +-18 lines printed
//                } else if (m_PortIdPrinter.getPortType() == CommPortIdentifier.PORT_PARALLEL) {
//                    ((ParallelPort)m_CommPortPrinter).setMode(1);
                }
            }
            m_out.write(data);
        } catch (Exception e) {
        	log.error("internalWrite() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
        }      
    }
    
    protected void internalFlush() {
        try {  
            if (m_out != null) {
                m_out.flush();
            }
        } catch (IOException e) {
        	log.error("internalWrite() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
        }    
    }
    
    protected void internalClose() {
        try {  
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
                m_CommPortPrinter = null;
                m_PortIdPrinter = null;
            }
        } catch (IOException e) {
        	log.error("internalWrite() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
        }    
    }
}
