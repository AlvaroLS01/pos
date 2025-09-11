package com.comerzzia.pos.gui.ventas.tickets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.profesional.LineaTicketProfesional;
import com.comerzzia.pos.services.ticket.profesional.TicketVentaProfesional;
import com.comerzzia.pos.util.config.SpringContext;

@Component
@Scope("prototype")
public class VentaProfesionalManager extends TicketManager {

	@Autowired
	private Sesion sesion;

	@Override
	protected LineaTicketAbstract createLinea() {
    	return SpringContext.getBean(LineaTicketProfesional.class);
	}

	public TipoDocumentoBean getNuevoDocumentoActivo() throws DocumentoException {
		return sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_COMPLETA);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class<? extends ITicket> getTicketClass(TipoDocumentoBean tipoDocumento) {
    	String claseDocumento = tipoDocumento.getClaseDocumento();
    	if(claseDocumento != null){
    		try {
				return (Class<? extends ITicket>) Class.forName(claseDocumento);
			} catch (ClassNotFoundException e) {
				log.error(String.format("getTicketClass() - Clase %s no encontrada, devolveremos TicketVentaProfesional", claseDocumento));
			}
    	}
		return TicketVentaProfesional.class;
	}
	
	@Override
	public void borrarDatosFactura() throws DocumentoException {
		log.debug("borrarDatosFactura() - En la venta profesional los datos de factura no se borran.");
	}

}
