/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.pos.gui.ventas.apartados;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraBean;
import com.comerzzia.pos.persistence.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.pos.persistence.apartados.pagos.ApartadosPagoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.apartados.ApartadosService;
import com.comerzzia.pos.services.articulos.tarifas.ArticulosTarifaService;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketApartado;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Scope("prototype")
public class ApartadosManager {
	
	protected Logger log = Logger.getLogger(getClass());

	private TicketApartado apartadoTicket;
	
	private ClienteBean cliente;

	private int idLinea;
	
	@Autowired
	private ApartadosService apartadosService;
	@Autowired
	private CajasService cajasService;
	
	@Autowired
	private Sesion sesion;
	@Autowired
	private MediosPagosService mediosPagosService;  
	
	public TicketApartado getTicketApartado(){
		return apartadoTicket;
	}
	
	public List<ApartadosCabeceraBean> consultarApartados(Long numApartado, String cliente, boolean verTodo){

		return apartadosService.consultarApartados(cliente, numApartado, verTodo, sesion.getAplicacion().getUidActividad());
	}

	public void cargarApartado(ApartadosCabeceraBean apartado){
		apartadoTicket = SpringContext.getBean(TicketApartado.class);
		apartadoTicket.setCabecera(apartado);

		apartadoTicket.setArticulos(apartadosService.consultarArticulosApartados(apartado.getUidApartado()));
		apartadoTicket.setMovimientos(apartadosService.consultarPagos(apartado.getUidApartado()));
		apartadoTicket.calcularTotales();
		idLinea = obtenerUltimoIdLinea(apartadoTicket.getArticulos());
		
		cliente = new ClienteBean();
		cliente.setCodCliente(apartado.getCodCliente());
		cliente.setCp(apartado.getCodPostal());
		cliente.setDesCliente(apartado.getDesCliente());
		cliente.setDomicilio(apartado.getDomicilio());
		cliente.setCif(apartado.getCif());
		cliente.setPoblacion(apartado.getPoblacion());
		cliente.setProvincia(apartado.getProvincia());
		cliente.setTelefono1(apartado.getTelefono1());
		cliente.setCodtar(apartado.getCodtar());
		cliente.setIdTratImpuestos(apartado.getIdTratImpuestos());
		cliente.setCodpais(apartado.getCodpais());
		cliente.setEmail(apartado.getEmail());
		cliente.setFax(apartado.getFax());
		cliente.setIdGrupoImpuestos(sesion.getAplicacion().getTienda().getCliente().getIdGrupoImpuestos());
	}
	
	public void actualizarPagos(){
		apartadoTicket.setMovimientos(apartadosService.consultarPagos(apartadoTicket.getCabecera().getUidApartado()));
		apartadoTicket.calcularTotales();
	}
	
	public void nuevoArticuloApartado(ApartadosDetalleBean lineaArticulo){
		apartadosService.nuevoArticuloApartado(lineaArticulo);
	}
	
	public void nuevoApartado(){
		
		ApartadosCabeceraBean cabeceraApartado = apartadoTicket.getCabecera();
		try {
			Date fecha = new Date();
			cabeceraApartado.setCodAlm(sesion.getAplicacion().getTienda().getCodAlmacen());
			cabeceraApartado.setEstadoApartado(ApartadosCabeceraBean.ESTADO_DISPONIBLE);
			cabeceraApartado.setFechaActualizacion(fecha);
			cabeceraApartado.setFechaApartado(fecha);
			cabeceraApartado.setUidActividad(sesion.getAplicacion().getUidActividad());
			cabeceraApartado.setSaldoCliente(BigDecimal.ZERO);
			cabeceraApartado.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
			cabeceraApartado.setUidApartado(UUID.randomUUID().toString());
			cabeceraApartado.setImporteTotalApartado(apartadoTicket.getTotalPendiente());
			cabeceraApartado.setVersion(1l);

			cabeceraApartado.setIdApartado(apartadosService.obtenerSiguienteIDApartado());
		} catch (Exception e) {
			log.error("Error creando el nuevo apartado", e);
		}
		
		apartadosService.nuevoApartado(cabeceraApartado);
	}
	
	public ApartadosDetalleBean introducirDetalleApartado(LineaTicket lineaArticulo){

		ApartadosDetalleBean detalle = new ApartadosDetalleBean();

		detalle.setCantidad(lineaArticulo.getCantidad());
		detalle.setCodart(lineaArticulo.getCodArticulo());
		detalle.setCodimp(lineaArticulo.getCodImpuesto());
		detalle.setDescuento(lineaArticulo.getDescuento());
		detalle.setDesart(lineaArticulo.getDesArticulo());
		detalle.setDesglose1(lineaArticulo.getDesglose1());
		detalle.setDesglose2(lineaArticulo.getDesglose2());
		detalle.setEstadoLineaApartado((short) 0);
		detalle.setImporteTotal(lineaArticulo.getImporteTotalConDto());
		detalle.setPrecio(lineaArticulo.getPrecioTotalConDto());
		detalle.setLinea(lineaArticulo.getIdLinea());
		detalle.setUidApartado(apartadoTicket.getCabecera().getUidApartado());
		detalle.setLinea(idLinea);
		detalle.setUidActividad(sesion.getAplicacion().getUidActividad());
		detalle.setFechaApartadoArticulo(new Date());
		detalle.setEstadoLineaApartado(ApartadosCabeceraBean.ESTADO_DISPONIBLE);
		detalle.setPrecioTotalSinDto(lineaArticulo.getPrecioTotalSinDto());
		detalle.setDescuento(lineaArticulo.getDescuento());
		detalle.setImporte(lineaArticulo.getImporteTotalSinDto());
		detalle.setPrecioCosto(lineaArticulo.getPrecioSinDto());
		detalle.setArticuloNuevo(true);
		detalle.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
		
		idLinea++;
		
		apartadoTicket.getArticulos().add(detalle);
						
		return detalle;
	}
	
	public void nuevoTicketApartado(){
		apartadoTicket = SpringContext.getBean(TicketApartado.class); 
		apartadoTicket.setArticulos(new ArrayList<ApartadosDetalleBean>());
		apartadoTicket.setMovimientos(new ArrayList<ApartadosPagoBean>());
		apartadoTicket.setCabecera(new ApartadosCabeceraBean());
		apartadoTicket.getCabecera().setSaldoCliente(BigDecimal.ZERO);
		idLinea = 0;
		
		setCliente(sesion.getAplicacion().getTienda().getCliente().clone());
	}
	
	public ClienteBean getCliente(){
		return cliente;
	}
	
	public void setCliente(ClienteBean cliente){
		
		ApartadosCabeceraBean cabecera = apartadoTicket.getCabecera();
		
		cabecera.setCif(cliente.getCif());
		cabecera.setPoblacion(cliente.getPoblacion());
		cabecera.setProvincia(cliente.getProvincia());
		cabecera.setTelefono1(cliente.getTelefono1());
		cabecera.setCodtar(cliente.getCodtar());
		cabecera.setIdTratImpuestos(cliente.getIdTratImpuestos());
		cabecera.setCodpais(cliente.getCodpais());
		cabecera.setEmail(cliente.getEmail());
		cabecera.setFax(cliente.getFax());
		cabecera.setDomicilio(cliente.getDomicilio());
		cabecera.setCodCliente(cliente.getCodCliente());
		cabecera.setCodPostal(cliente.getCp());
		cabecera.setDesCliente(cliente.getDesCliente());
		
		this.cliente = cliente;
	}
	
	public String getTarifaDefault(){
		if (cliente.isTarifaAsignada()){
			return cliente.getCodtar();
		}
		if (sesion.getAplicacion().getTienda().getCliente().isTarifaAsignada()){
			return sesion.getAplicacion().getTienda().getCliente().getCodtar();
		}
		return ArticulosTarifaService.COD_TARIFA_GENERAL;
	}

	public void eliminarArticuloApartado(ApartadosDetalleBean articuloBorrado){	
		if(!articuloBorrado.isArticuloNuevo()){
			articuloBorrado.setEstadoLineaApartado(ApartadosCabeceraBean.ESTADO_CANCELADO);
			articuloBorrado.setFechaActualizacion(new Date());
			apartadosService.actualizarDetalleApartado(articuloBorrado);
		}
		else{
			this.apartadoTicket.getArticulos().remove(articuloBorrado);
		}
		apartadoTicket.calcularTotales();
		apartadoTicket.getCabecera().setImporteTotalApartado(apartadoTicket.getImporteTotal());
		actualizarCabecera();
	}

	public void actualizarCabecera(){
		apartadosService.actualizarCabeceraApartado(apartadoTicket.getCabecera());
	}

	public void guardarDatosCliente(ClienteBean cliente){
		setCliente(cliente);
		actualizarCabecera();
	}
	
	private int obtenerUltimoIdLinea(List<ApartadosDetalleBean> articulos){
		
		int ultimaLinea = 0;
		
		for(ApartadosDetalleBean articulo : articulos){
			ultimaLinea = ultimaLinea<=articulo.getLinea()?articulo.getLinea()+1:ultimaLinea;
		}
		
		return ultimaLinea;
	}
	
	public void grabarMovimientoDevolucion(ApartadosCabeceraBean cabeceraApartado, BigDecimal importe, MedioPagoBean medioPago) throws TicketsServiceException{
				
		CajaMovimientoBean detalleCaja = new CajaMovimientoBean();
		
		detalleCaja.setFecha(new Date());
		detalleCaja.setAbono(importe);
		detalleCaja.setCargo(BigDecimal.ZERO);
		detalleCaja.setConcepto("Apartado nº " + cabeceraApartado.getIdApartado());
		detalleCaja.setCodMedioPago(medioPago.getCodMedioPago());
		detalleCaja.setDesMedioPago(medioPago.getDesMedioPago());
		
		apartadosService.crearMovimientoDevolucion(cabeceraApartado, medioPago, detalleCaja);
		
		Long idTipoDocumento = null;
		
		Map<String,Object> mapaParametros= new HashMap<String,Object>();
		try {
			TicketManager ticketManager = SpringContext.getBean(TicketManager.class);
	        ticketManager.nuevoTicket();
			Ticket ticketPrincipal = (Ticket) ticketManager.getTicket();
			((Ticket) ticketPrincipal).getCabecera().setTienda(sesion.getAplicacion().getTienda());
			((Ticket) ticketPrincipal).getCabecera().setEmpresa(sesion.getAplicacion().getEmpresa());
			idTipoDocumento = ticketPrincipal.getCabecera().getTipoDocumento();
			mapaParametros.put("ticket", ticketPrincipal);
        }
        catch (Exception e) {
        	log.error("grabarMovimientoDevolucion() - Ha habido un error al crear el ticket para la impresión: " + e.getMessage());
        }
		
		mapaParametros.put("apartado", cabeceraApartado);
		mapaParametros.put("importe", FormatUtil.getInstance().formateaImporte(importe));
		mapaParametros.put("fecha", FormatUtil.getInstance().formateaFechaHora(detalleCaja.getFecha()));
		mapaParametros.put("empresa", sesion.getAplicacion().getEmpresa());
		mapaParametros.put("cajero", sesion.getSesionUsuario().getUsuario().getDesusuario());
		mapaParametros.put("pago", detalleCaja);
		if(mediosPagosService.isCodMedioPagoVale(medioPago.getCodMedioPago(), idTipoDocumento)){
			mapaParametros.put("importeVale", importe);
		}
		try {
			ServicioImpresion.imprimir("informe_apartado", mapaParametros);
		} catch (DeviceException e) {
			log.error("grabarMovimientoDevolucion() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}	
	}
	
	public void rechazarNuevosArticulos(){
		List<ApartadosDetalleBean> articulosAnteriores = new ArrayList<ApartadosDetalleBean>();
		articulosAnteriores.addAll(apartadoTicket.getArticulos());
		
		for(ApartadosDetalleBean linea: apartadoTicket.getArticulos()){
			if(linea.isArticuloNuevo()){
				articulosAnteriores.remove(linea);
			}
		}
		apartadoTicket.setArticulos(articulosAnteriores);
		apartadoTicket.calcularTotales();
	}
	
	public void actualizarEstadoLineasVendidas(String uidTicket, List<ApartadosDetalleBean> articulos){
		
		for(ApartadosDetalleBean detalleApartado : articulos){
			detalleApartado.setUidTicket(uidTicket);
			detalleApartado.setFechaActualizacion(new Date());
			this.apartadoTicket.getLineaDetalleApartado(detalleApartado.getLinea()).setEstadoLineaApartado(ApartadosCabeceraBean.ESTADO_FINALIZADO);
			detalleApartado.setEstadoLineaApartado(ApartadosCabeceraBean.ESTADO_FINALIZADO);
			apartadosService.actualizarDetalleApartado(detalleApartado);
		}
	}
	
	public void registrarVentaApartado(BigDecimal importeVenta){
		
		BigDecimal saldo = apartadoTicket.getCabecera().getSaldoCliente();		
		apartadoTicket.getCabecera().setSaldoCliente(saldo.subtract(importeVenta));
		
		apartadoTicket.calcularTotales();
		apartadoTicket.getCabecera().setImporteTotalApartado(apartadoTicket.getImporteTotal());
		
		actualizarCabecera();
	}
	
	public void imprimirApartado() throws DeviceException{
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		try {
			TicketManager ticketManager = SpringContext.getBean(TicketManager.class);
	        ticketManager.nuevoTicket();
			Ticket ticketPrincipal = (Ticket) ticketManager.getTicket();
			((Ticket) ticketPrincipal).getCabecera().setTienda(sesion.getAplicacion().getTienda());
			((Ticket) ticketPrincipal).getCabecera().setEmpresa(sesion.getAplicacion().getEmpresa());
			parametros.put("ticket", ticketPrincipal);
        }
        catch (Exception e) {
        	log.error("imprimirApartado() - Ha habido un error al crear el ticket para la impresión: " + e.getMessage());
        }
		
		List<ApartadosDetalleBean> detalles = apartadoTicket.getArticulos();
		parametros.put("articulos", detalles);
		parametros.put("apartado", apartadoTicket.getCabecera());
		parametros.put("fecha", FormatUtil.getInstance().formateaFechaHora(new Date()));
		parametros.put("empresa", sesion.getAplicacion().getEmpresa());
		parametros.put("cajero", sesion.getSesionUsuario().getUsuario().getDesusuario());
		parametros.put("pendiente", apartadoTicket.getTotalPendienteTicket());
		parametros.put("abonado", apartadoTicket.getTotalAbonadoTicket());
		parametros.put("saldo", apartadoTicket.getCabecera().getSaldoClienteTicket());
		parametros.put("servido", apartadoTicket.getServidoTicket());

		List<CajaMovimientoBean> movimientos = new ArrayList<CajaMovimientoBean>();
		try {
			List<ApartadosPagoBean> pagosApartado = apartadoTicket.getMovimientos();			
			CajaMovimientoBean movimientoBean;
			MediosPagosService mediosPagosService = SpringContext.getBean(MediosPagosService.class);
			for(ApartadosPagoBean pago: pagosApartado){		
				movimientoBean = cajasService.consultarMovimientoApartado(pago.getUidDiarioCaja(), pago.getLinea(), sesion.getAplicacion().getUidActividad());
				movimientoBean.setDesMedioPago(mediosPagosService.getMedioPago(movimientoBean.getCodMedioPago()).getDesMedioPago());
				movimientos.add(movimientoBean);
			}
		} catch (CajasServiceException e) {
			e.printStackTrace();
		}

		parametros.put("pagos", movimientos.isEmpty()? null : movimientos);
		
		ServicioImpresion.imprimir("resumen_apartado", parametros);
	}
	
	public boolean cancelarLineas(ApartadosCabeceraBean cabeceraApartado){
		
		boolean hayLineasVendidas = false;
		
		List<ApartadosDetalleBean> articulos = apartadosService.consultarArticulosApartados(cabeceraApartado.getUidApartado());
		
		for(ApartadosDetalleBean detalleApartado : articulos){
			if(detalleApartado.getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_DISPONIBLE){
				detalleApartado.setEstadoLineaApartado(ApartadosCabeceraBean.ESTADO_CANCELADO);
				detalleApartado.setFechaActualizacion(new Date());
				apartadosService.actualizarDetalleApartado(detalleApartado);
			}
			else if(detalleApartado.getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_FINALIZADO){
				hayLineasVendidas = true;
			}
		}
		
		return hayLineasVendidas;
	}
}
