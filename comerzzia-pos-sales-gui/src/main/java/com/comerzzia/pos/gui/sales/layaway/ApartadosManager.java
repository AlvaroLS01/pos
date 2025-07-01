
package com.comerzzia.pos.gui.sales.layaway;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalLine;
import com.comerzzia.omnichannel.facade.model.cashjournal.NewCashJournalLine;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.ApartadosCabeceraBean;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.pagos.ApartadosPagoBean;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethod;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.service.basket.BasketManagerBuilder;
import com.comerzzia.omnichannel.facade.service.basket.exception.BasketException;
import com.comerzzia.omnichannel.facade.service.basket.layaway.LayawayTransactionDTO;
import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.omnichannel.facade.service.cashjournal.CashJournalServiceFacade;
import com.comerzzia.omnichannel.facade.service.deprecated.layawaydocument.LayawayDocumentService;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;

@Component
@Scope("prototype")
public class ApartadosManager {
	
	protected Logger log = Logger.getLogger(getClass());

	protected LayawayTransactionDTO apartadoTicket;
	
	private Customer cliente;

	private int idLinea;
	
	@Autowired
	protected LayawayDocumentService apartadosService;
	
	@Autowired
	protected Session sesion;
	
	@Autowired
	protected CashJournalServiceFacade cashJournalService;
	
	@Autowired
	protected ModelMapper modelMapper;
	
	@Autowired
	protected ComerzziaTenantResolver tenantResolver;
	
	public LayawayTransactionDTO getTicketApartado(){
		return apartadoTicket;
	}
	
	public List<ApartadosCabeceraBean> consultarApartados(Long numApartado, String cliente, boolean verTodo){

		return apartadosService.consultarApartados(cliente, numApartado, verTodo, sesion.getApplicationSession().getConfiguredActivityUid());
	}

	public void cargarApartado(ApartadosCabeceraBean apartado){
		apartadoTicket = SpringContext.getBean(LayawayTransactionDTO.class);
		apartadoTicket.setCabecera(apartado);

		apartadoTicket.setArticulos(apartadosService.consultarArticulosApartados(apartado.getUidApartado()));
		apartadoTicket.setMovimientos(apartadosService.consultarPagos(apartado.getUidApartado()));
		apartadoTicket.calcularTotales();
		idLinea = obtenerUltimoIdLinea(apartadoTicket.getArticulos());
		
		cliente = new Customer();
		cliente.setCustomerCode(apartado.getCodCliente());
		cliente.setPostalCode(apartado.getCodPostal());
		cliente.setCustomerDes(apartado.getDesCliente());
		cliente.setAddress(apartado.getDomicilio());
		cliente.setVatNumber(apartado.getCif());
		cliente.setCity(apartado.getPoblacion());
		cliente.setProvince(apartado.getProvincia());
		cliente.setPhone1(apartado.getTelefono1());
		cliente.setRateCode(apartado.getCodtar());
		cliente.setTaxTreatmentId(apartado.getIdTratImpuestos());
		cliente.setCountryCode(apartado.getCodpais());
		cliente.setEmail(apartado.getEmail());
		cliente.setFax(apartado.getFax());
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
			cabeceraApartado.setCodAlm(sesion.getApplicationSession().getStorePosBusinessData().getStore().getStoreCode());
			cabeceraApartado.setEstadoApartado(ApartadosCabeceraBean.ESTADO_DISPONIBLE);
			cabeceraApartado.setFechaActualizacion(fecha);
			cabeceraApartado.setFechaApartado(fecha);
			cabeceraApartado.setUidActividad(sesion.getApplicationSession().getConfiguredActivityUid());
			cabeceraApartado.setSaldoCliente(BigDecimal.ZERO);
			cabeceraApartado.setUsuario(tenantResolver.getUser().getUserCode());
			cabeceraApartado.setUidApartado(UUID.randomUUID().toString());
			cabeceraApartado.setImporteTotalApartado(apartadoTicket.getTotalPendiente());
			cabeceraApartado.setVersion(1l);

			cabeceraApartado.setIdApartado(apartadosService.obtenerSiguienteIDApartado());
		} catch (Exception e) {
			log.error("Error creando el nuevo apartado", e);
		}
		
		apartadosService.nuevoApartado(cabeceraApartado);
	}
	
	public ApartadosDetalleBean introducirDetalleApartado(BasketItem lineaArticulo){

		ApartadosDetalleBean detalle = new ApartadosDetalleBean();

		detalle.setCantidad(lineaArticulo.getQuantity());
		detalle.setCodart(lineaArticulo.getItemCode());
		detalle.setCodimp(lineaArticulo.getItemData().getTaxTypeCode());
		detalle.setDescuento(lineaArticulo.getDiscount());
		detalle.setDesart(lineaArticulo.getItemDes());
		detalle.setDesglose1(lineaArticulo.getCombination1Code());
		detalle.setDesglose2(lineaArticulo.getCombination2Code());
		detalle.setEstadoLineaApartado((short) 0);
		detalle.setImporteTotal(lineaArticulo.getExtendedPriceWithTaxes());
		detalle.setPrecio(lineaArticulo.getPriceWithTaxes());
		detalle.setLinea(lineaArticulo.getLineId());
		detalle.setUidApartado(apartadoTicket.getCabecera().getUidApartado());
		detalle.setLinea(idLinea);
		detalle.setUidActividad(sesion.getApplicationSession().getConfiguredActivityUid());
		detalle.setFechaApartadoArticulo(new Date());
		detalle.setEstadoLineaApartado(ApartadosCabeceraBean.ESTADO_DISPONIBLE);
		detalle.setPrecioTotalSinDto(lineaArticulo.getPriceWithTaxesWithoutDiscount());
		detalle.setDescuento(lineaArticulo.getDiscount());
		detalle.setImporte(lineaArticulo.getExtendedPriceWithTaxesWithoutDiscount());
		detalle.setPrecioCosto(lineaArticulo.getPriceWithoutDiscount());
		detalle.setArticuloNuevo(true);
		detalle.setUsuario(tenantResolver.getUser().getUserCode());
		
		idLinea++;
		
		apartadoTicket.getArticulos().add(detalle);
						
		return detalle;
	}
	
	public void nuevoTicketApartado(){
		apartadoTicket = SpringContext.getBean(LayawayTransactionDTO.class); 
		apartadoTicket.setArticulos(new ArrayList<ApartadosDetalleBean>());
		apartadoTicket.setMovimientos(new ArrayList<ApartadosPagoBean>());
		apartadoTicket.setCabecera(new ApartadosCabeceraBean());
		apartadoTicket.getCabecera().setSaldoCliente(BigDecimal.ZERO);
		idLinea = 0;
		
		setCliente(modelMapper.map(sesion.getApplicationSession().getStorePosBusinessData().getDefaultCustomer(), Customer.class));
	}
	
	public Customer getCliente(){
		return cliente;
	}
	
	public void setCliente(Customer cliente){
		
		ApartadosCabeceraBean cabecera = apartadoTicket.getCabecera();
		
		cabecera.setCif(cliente.getVatNumber());
		cabecera.setPoblacion(cliente.getCity());
		cabecera.setProvincia(cliente.getProvince());
		cabecera.setTelefono1(cliente.getPhone1());
		cabecera.setCodtar(cliente.getRateCode());
		cabecera.setIdTratImpuestos(cliente.getTaxTreatmentId());
		cabecera.setCodpais(cliente.getCountryCode());
		cabecera.setEmail(cliente.getEmail());
		cabecera.setFax(cliente.getFax());
		cabecera.setDomicilio(cliente.getAddress());
		cabecera.setCodCliente(cliente.getCustomerCode());
		cabecera.setCodPostal(cliente.getPostalCode());
		cabecera.setDesCliente(cliente.getCustomerDes());
		
		this.cliente = cliente;
	}
	
	public String getTarifaDefault(){
		if (StringUtils.isNotBlank(cliente.getRateCode())){
			return cliente.getRateCode();
		}
		if (StringUtils.isNotBlank(sesion.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getRateCode())){
			return sesion.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getRateCode();
		}
		
		return "GENERAL";
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

	public void guardarDatosCliente(Customer cliente){
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
	
	public void grabarMovimientoDevolucion(ApartadosCabeceraBean cabeceraApartado, BigDecimal importe, PaymentMethod medioPago) throws BasketException{
				
		NewCashJournalLine detalleCaja = new NewCashJournalLine();
		
		detalleCaja.setCashJournalDate(new Date());
		detalleCaja.setOutput(importe);
		detalleCaja.setInput(BigDecimal.ZERO);
		detalleCaja.setConcept("Apartado nº " + cabeceraApartado.getIdApartado());
		detalleCaja.setPaymentMethodCode(medioPago.getPaymentMethodCode());
		
		apartadosService.crearMovimientoDevolucion(cabeceraApartado, medioPago, detalleCaja);
		
		
		Map<String,Object> mapaParametros= new HashMap<String,Object>();
		try {
			RetailBasketManager ticketManager = BasketManagerBuilder.build(RetailBasketManager.class, sesion.getApplicationSession().getStorePosBusinessData());
//	        ticketManager.createBasketTransaction();
	        BasketPromotable<?> ticketPrincipal = ticketManager.getBasketTransaction();

			mapaParametros.put("ticket", ticketPrincipal);
        }
        catch (Exception e) {
        	log.error("grabarMovimientoDevolucion() - Ha habido un error al crear el ticket para la impresión: " + e.getMessage());
        }
		
		mapaParametros.put("apartado", cabeceraApartado);
		mapaParametros.put("importe", FormatUtils.getInstance().formatAmount(importe));
		mapaParametros.put("fecha", FormatUtils.getInstance().formatDateTime(detalleCaja.getCashJournalDate()));
		mapaParametros.put("empresa", sesion.getApplicationSession().getCompany());
		mapaParametros.put("cajero", tenantResolver.getUser().getUserDes());
		mapaParametros.put("pago", detalleCaja);
//		try {	
//			ServicioImpresion.imprimir("informe_apartado", mapaParametros);
//		} catch (DeviceException e) {
//			log.error("grabarMovimientoDevolucion() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
//		}	
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
			RetailBasketManager ticketManager = BasketManagerBuilder.build(RetailBasketManager.class, sesion.getApplicationSession().getStorePosBusinessData());
			
			parametros.put("ticket", ticketManager.getBasketTransaction());
        }
        catch (Exception e) {
        	log.error("imprimirApartado() - Ha habido un error al crear el ticket para la impresión: " + e.getMessage());
        }
		
		List<ApartadosDetalleBean> detalles = apartadoTicket.getArticulos();
		parametros.put("articulos", detalles);
		parametros.put("apartado", apartadoTicket.getCabecera());
		parametros.put("fecha", FormatUtils.getInstance().formatDateTime(new Date()));
		parametros.put("empresa", sesion.getApplicationSession().getCompany());
		parametros.put("cajero", tenantResolver.getUser().getUserDes());
		parametros.put("pendiente", apartadoTicket.getTotalPendiente());
		parametros.put("abonado", apartadoTicket.getTotalAbonado());
		parametros.put("saldo", apartadoTicket.getCabecera().getSaldoCliente());
		parametros.put("servido", apartadoTicket.getTotalServido());

		List<CashJournalLine> movimientos = new ArrayList<CashJournalLine>();
		List<ApartadosPagoBean> pagosApartado = apartadoTicket.getMovimientos();			
		CashJournalLine movimientoBean;
		
		for(ApartadosPagoBean pago: pagosApartado){		
			movimientoBean = cashJournalService.findCashJournalLineById(pago.getUidDiarioCaja(), pago.getLinea());
//				movimientoBean.setDesMedioPago(mediosPagosService.findByIdActive(movimientoBean.getCodMedioPago()).getPaymentMethodDes());
			movimientos.add(movimientoBean);
		}

		parametros.put("pagos", movimientos.isEmpty()? null : movimientos);
		
//		ServicioImpresion.imprimir("resumen_apartado", parametros);
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
