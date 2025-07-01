
package com.comerzzia.pos.devices.drivers.escpos.escpos;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EscPosPrinterWritterFile extends EscPosPrinterWritter {
    
    private String m_sFilePrinter;
    private OutputStream m_out;
    
    public EscPosPrinterWritterFile(String sFilePrinter) {
        m_sFilePrinter = sFilePrinter;
        m_out = null;
    }  
    
    protected void internalWrite(byte[] data) {
        try {  
            if (m_out == null) {
                m_out = new FileOutputStream(m_sFilePrinter);  // No poner append = true.
            }
            m_out.write(data);
        } catch (IOException e) {
            System.err.println(e);
        }    
    }
    
    protected void internalFlush() {
        try {  
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
            }
        } catch (IOException e) {
            System.err.println(e);
        }    
    }
    
    protected void internalClose() {
        try {  
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
            }
        } catch (IOException e) {
            System.err.println(e);
        }    
    }    
}
