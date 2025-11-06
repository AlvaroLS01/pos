package com.comerzzia.dinosol.pos.services.ticket.liquidacion;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.persistence.motivos.MotivosCambioPrecio;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.codbarrasesp.liquidacion.QRLiquidacionDTO;
import com.comerzzia.dinosol.pos.services.codbarrasesp.liquidacion.QRTipoLiquidacion;
import com.comerzzia.dinosol.pos.services.motivos.MotivosCambioPrecioServices;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.dinosol.pos.services.ticket.lineas.DinoLineaTicket;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;

@Component
public class QrLiquidacionService {
	
	private Logger log = Logger.getLogger(QrLiquidacionService.class);
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private MotivosCambioPrecioServices motivosCambioPrecioService;
	
	@SuppressWarnings("rawtypes")
	public void aplicaQRLiquidacion(ITicket ticketPrincipal, LineaTicketAbstract linea, LineaTicket lineaOriginal, QRLiquidacionDTO qrLiquidacionDTO) throws QRLiquidacionException { // DIN-112
		log.debug("aplicaQRLiquidacion() - Aplicando QR LIQUIDACIÓN sobre artículo introducido " + linea.getCodArticulo());
		
		if(qrLiquidacionDTO.getTipoLiquidacion().equals(QRTipoLiquidacion.QR_LIQUIDACION_CAMBIO_PRECIO)){
			aplicaCambioPrecioQRLiquidacion(linea, qrLiquidacionDTO);
		}
		else if(qrLiquidacionDTO.getTipoLiquidacion().equals(QRTipoLiquidacion.QR_LIQUIDACION_DESCUENTO)) {
			aplicaPorcentajeDescuentoQRLiquidacion(linea, qrLiquidacionDTO);
		}
		
		((DinoLineaTicket) linea).setCodMotivo(qrLiquidacionDTO.getTipoLiquidacion().motivo);
		guardaAuditoria(ticketPrincipal, linea, lineaOriginal, qrLiquidacionDTO);
	}

	private void aplicaCambioPrecioQRLiquidacion(LineaTicketAbstract linea, QRLiquidacionDTO qrLiquidacionDTO) { // DIN-112
		log.debug("aplicaCambioPrecioQRLiquidacion() - Aplicando cambio de precio a la línea");

		BigDecimal precioLiquidacion = qrLiquidacionDTO.getPrecioLiquidacion();
		BigDecimal precioLiquidacionSinImpuestos = sesion.getImpuestos().getPrecioSinImpuestos(linea.getCodImpuesto(), precioLiquidacion, linea.getCabecera().getCliente().getIdTratImpuestos());
		linea.setPrecioSinDto(precioLiquidacionSinImpuestos);
		linea.setPrecioTotalSinDto(precioLiquidacionSinImpuestos);
		linea.recalcularImporteFinal();
	}
	
	private void aplicaPorcentajeDescuentoQRLiquidacion(LineaTicketAbstract linea, QRLiquidacionDTO qrLiquidacionDTO)  { // DIN-112
		log.debug("aplicaPorcentajeDescuentoQRLiquidacion - Aplicando porcentaje de descuento usando QR LIQUIDACION sobre el artículo:" + linea.getCodArticulo());

		BigDecimal descuentoLiquidacion = qrLiquidacionDTO.getPrecioLiquidacion();
		linea.setDescuentoManual(descuentoLiquidacion);
		linea.recalcularImporteFinal();
	}
	
	@SuppressWarnings("rawtypes")
	public void guardaAuditoria(ITicket ticketPrincipal, LineaTicketAbstract linea, LineaTicket lineaOriginal, QRLiquidacionDTO qrLiquidacionDTO) throws QRLiquidacionException { // DIN-112
		if (Boolean.TRUE.equals(linea.getGenerico())) {
			log.debug("guardaAuditoria() - No se guardara auditoría por ser el artículo genérico: " + linea.getCodArticulo());
			return;
		}

		try {
			log.info("guardaAuditoria() - El cajero '" + sesion.getSesionUsuario().getUsuario() + "' ha cambiado el precio manual usando QR LIQUIDACION; Precio Original: "
			        + lineaOriginal.getPrecioTotalTarifaOrigen() + ". Precio liquidacion: " + linea.getPrecioTotalConDto());

			log.debug("guardaAuditoria() - Guardando auditoria: ");
			AuditoriaDto auditoria = new AuditoriaDto();
			auditoria.setTipo(QRTipoLiquidacion.QR_LIQUIDACION_DESCUENTO.label);
			auditoria.setUidTicket(ticketPrincipal.getUidTicket());
			auditoria.setCodart(linea.getCodArticulo());
			auditoria.setPrecioAnterior(lineaOriginal.getPrecioTotalSinDto());
			auditoria.setPrecioNuevo(linea.getPrecioTotalConDto());
			auditoria.setCantidadLinea(linea.getCantidad());
			auditoria.setFecha(new Date());
			MotivosCambioPrecio motivo = motivosCambioPrecioService.consultarPorCodigo(QRTipoLiquidacion.QR_LIQUIDACION_DESCUENTO.motivo);
			auditoria.setCodMotivo(motivo.getCodMotivo());
			auditoria.setDesMotivo(motivo.getDesMotivo());

			if (qrLiquidacionDTO != null) {
				auditoria.setCajeroOperacion(qrLiquidacionDTO.getCodOperador());
			}
			
			((DinoTicketVentaAbono) ticketPrincipal).addAuditoriaNoRegistrada(auditoria);
		}
		catch (Exception e) {
			String error = "Error guardando auditoria de cambio de precio por código, tipo: " + QRTipoLiquidacion.QR_LIQUIDACION_DESCUENTO.label;
			log.error("guardaAuditoria() - " + error);
			throw new QRLiquidacionException(error);
		}
	}

}
