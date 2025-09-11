package com.comerzzia.iskaypet.pos.gui.ventas.devoluciones.articulos;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.tipodocumento.TipoDocumentoNotFoundException;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaDto;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaTicketView;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.motivos.CargarMotivosController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriasService;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.gui.ventas.devoluciones.articulos.DevolucionArticulosController;
import com.comerzzia.pos.gui.ventas.devoluciones.articulos.LineaProvisionalDevolucion;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;

/**
 * GAPXX - 
 */
@Component
@Primary
public class IskaypetDevolucionArticulosController extends DevolucionArticulosController{

	protected Logger log = Logger.getLogger(getClass());

	public static final String ES_ARTICULO_DEVOLUCION = "ES_ARTICULO_DEVOLUCION";

	protected MotivoAuditoriaDto motivo;

	@Autowired
	private AuditoriasService auditoriasService;

	@Autowired
	private IskaypetFacturacionArticulosController facturacionArticulosController;

	@Override
	protected void actualizarLineasProvisionales(BigDecimal cantidadASumar, LineaTicketAbstract lineaOriginal){
		LineaProvisionalDevolucion lineaProvisional = getLineaProvisional(lineaOriginal.getIdLinea());

		if(lineaProvisional != null){
			lineaProvisional.setCantADevolver(lineaProvisional.getCantADevolver().add(cantidadASumar));
		}
		else{
			lineaProvisional = new LineaProvisionalDevolucion(lineaOriginal);
			lineaProvisional.setIdLinea(lineaOriginal.getIdLinea());
			lineaProvisional.setCantADevolver(cantidadASumar);
			lineasProvisionales.add(lineaProvisional);
		}
		getDatos().put(ES_ARTICULO_DEVOLUCION, true);
	}

	@Override
	protected void asignarNumerosSerie(LineaTicket linea){
		super.asignarNumerosSerie(linea);
		solicitarMotivoDevolucion(linea);
	}

	private void solicitarMotivoDevolucion(LineaTicket linea){
		IskaypetLineaTicket iskLinea = (IskaypetLineaTicket) linea;

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(IskaypetFacturacionArticulosController.TIPO_DOCUMENTO_ENVIADO, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION);
		params.put(IskaypetFacturacionArticulosController.LINEA_ENVIADA, iskLinea);

		if(iskLinea.getMotivoAuditoria() == null){
			getApplication().getMainView().showModalCentered(MotivoAuditoriaTicketView.class, params, getStage());
			motivo = (MotivoAuditoriaDto) params.get(CargarMotivosController.MOTIVO);
			crearAuditoriaDevolucion(iskLinea, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION);
			iskLinea.setMotivoAuditoria(motivo);
		}
	}

	public void crearAuditoriaDevolucion(IskaypetLineaTicket linea, String tipoDoc){
		AuditoriaDto auditoria = facturacionArticulosController.setearDatosAuditoria(null, null, linea);
		auditoria.setCodMotivo(motivo.getCodigo().toString());
		auditoria.setObservaciones(motivo.getDescripcion());
		try {
			auditoriasService.generarAuditoria(auditoria, tipoDoc, null, Boolean.TRUE);
            //GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
			auditoriasService.addAuditoriaLinea(linea, tipoDoc, tipoDoc, auditoria.getCodMotivo());
		} catch (ContadorServiceException e) {
			log.error("crearAuditoriaCancelarVenta() - error al insertar la auditoria" + e.getMessage(), e);
		} catch (EmpresaException e) {
			log.error("crearAuditoriaCancelarVenta() - Empresa no encontrada " + e.getMessage(), e);

		} catch (TipoDocumentoNotFoundException e) {
			log.error("crearAuditoriaCancelarVenta() - Tipo de documento no encontrado " + e.getMessage(), e);

		} catch (DocumentoException e) {
			log.error("crearAuditoriaCambioPrecio() - error al recuperar el tipo de documento " + e.getMessage(), e);
		}
	}
	
}
