package com.comerzzia.pos.gui.ventas.profesional.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.identificada.cliente.IdentificacionClienteController;
import com.comerzzia.pos.gui.ventas.identificada.venta.VentaIdentificadaController;
import com.comerzzia.pos.gui.ventas.profesional.venta.VentaProfesionalView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.VentaProfesionalManager;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class IdentificacionClienteProfesionalController extends IdentificacionClienteController {
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;
	
	@Override
	public void initializeComponents() throws InitializeGuiException {
		vProfesional = true;
	    super.initializeComponents(); 
	    lbTitulo.setText(I18N.getTexto("Seleccionar Cliente de Venta Profesional"));
	}
	
	@Override
	protected void abrirPantallaVentas() throws InitializeGuiException {
		getView().changeSubView(VentaProfesionalView.class, datos);
	}
	
	@Override
	protected TicketManager getNuevoTicketManager() {
		return SpringContext.getBean(VentaProfesionalManager.class);
	}
	
}
