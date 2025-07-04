package com.comerzzia.cardoso.pos.gui.ventas.tickets.pagos;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

import com.comerzzia.cardoso.pos.gui.promociones.monograficas.PromocionesEspeciales;
import com.comerzzia.cardoso.pos.gui.ventas.taxfree.TaxfreeController;
import com.comerzzia.cardoso.pos.gui.ventas.taxfree.TaxfreeView;
import com.comerzzia.cardoso.pos.gui.ventas.taxfree.impresora.SeleccionImpresoraController;
import com.comerzzia.cardoso.pos.gui.ventas.taxfree.impresora.SeleccionImpresoraView;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.CARDOSOTicketManager;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.pagos.rest.empleados.CARDOSOClientRestPromocionEmpleados;
import com.comerzzia.cardoso.pos.persistence.taxfree.barcodecreation.request.JSONOperationData;
import com.comerzzia.cardoso.pos.persistence.taxfree.barcodecreation.request.TaxfreeBarCodeCreationRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.barcodecreation.response.TaxfreeBarCodeCreationResponse;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.request.TaxfreeCountryRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.response.Country;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.response.TaxfreeCountryResponse;
import com.comerzzia.cardoso.pos.services.core.sesion.CardosoSesion;
import com.comerzzia.cardoso.pos.services.rest.PosRestService;
import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeService;
import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeVariablesService;
import com.comerzzia.cardoso.pos.services.taxfree.webservice.TaxfreeWebService;
import com.comerzzia.cardoso.pos.services.ticket.CardosoTicketsService;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales.PromocionEmpleadosCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNodeNotFoundException;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.tickets.pagos.NoCerrarPantallaException;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.persistence.tickets.datosfactura.DatosFactura;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.events.PaymentsSelectEvent;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;

@SuppressWarnings({"static-access", "rawtypes", "unchecked", "unused"})
@Controller
@Primary
public class CARDOSOPagosController extends PagosController{
	
	private static final Logger log = Logger.getLogger(CARDOSOPagosController.class.getName());
	
	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	private PosRestService posRestService;
	
	final IVisor visor = Dispositivos.getInstance().getVisor();
	
	/* GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS */
	@Autowired
	private PromocionesEspeciales promocionesEspecialesServicio;
	@FXML
	private Label lbDescuentosEspeciales;
	
	//TAXFREE
	
	@FXML
	protected Label lbTaxFree;
	
	private Boolean realizarTaxFree;
	private Boolean taxfreeCancelado;
	
	public static final String ESTADO_CREACION = "CREACION";
	
	public static final String PARAM_IMPRESORAS = "impresoras";
	public static final String TAXFREE_IMPRESORA = "X_POS.TAXFREE_IMPRESORA";
	
	
	public static final String PDF_A4 = "PrintType=A4";
	public static final String PDF_80 = "PrintType=80";
	
	@Autowired 
	protected TaxfreeVariablesService taxfreeVariablesService;
	
	@Autowired
	protected TaxfreeService taxfreeService;
	
	@Autowired
	protected CardosoSesion cardosoSesion;
	
	@Autowired
	protected TicketsService ticketsService;
	
	@Autowired
	protected TaxfreeWebService taxfreeWebService;
	
	private boolean impreso;
	private CARDOSOCabeceraTicket cabecera;

	private Boolean solicitaTaxfree;

	protected TaxfreeCountryResponse paisesDisponiblesTaxfree;
		
	@Override
	public void initializeForm() throws InitializeGuiException{
		super.initializeForm();
		
		realizarTaxFree = false;
		lbTaxFree.setVisible(false);
		lbTitulo.setText("Pagos");
		taxfreeCancelado = Boolean.FALSE;
		solicitaTaxfree = false;
		
		panelPagos.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1){
				if(t.booleanValue() == false && t1.booleanValue() == true){
					medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
					lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
					lbSaldo.setText("");
					Platform.runLater(new Runnable(){
						@Override
						public void run(){
							tfImporte.requestFocus();
						}
					});
				}
			}
		});
		
		/* GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS */
		procesarPromocionesEspeciales();
	}
	
	/**
	 * GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS
	 * Realizamos comprobaciones y en caso de tener alguna de las promociones especiales, las aplicamos.
	 * Luego mostramos por pantalla el importe de descuento, no se pueden usar las dos a vez.
	 */
	public void procesarPromocionesEspeciales(){
		log.info("procesarPromocionesEspeciales() - GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS...");
		
		Boolean promocionEspecialUsada = false;
		try{
			/* PROMOCIÓN MONOGRÁFICA */
			if(!promocionEspecialUsada){
				if(((CARDOSOCabeceraTicket)ticketManager.getTicket().getCabecera()).getDatosDescuentoPromocionMonografica() != null){
					promocionEspecialUsada = true;
					
					BigDecimal importeDescuentoEspecial = ((CARDOSOCabeceraTicket)ticketManager.getTicket().getCabecera()).getDatosDescuentoPromocionMonografica().getImporteTotal();
					setDescuentoEspecial(importeDescuentoEspecial);
				}
			}
			
			/* PROMOCIÓN DE EMPLEADOS */
			if(!promocionEspecialUsada){
				promocionesEspecialesServicio.procesarDescuentosPromocionEmpleado((CARDOSOTicketManager) ticketManager);
				if(((CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera()).getDatosDescuentoPromocionEmpleados() != null){
					promocionEspecialUsada = true;
					
					new GetUsosPromocionEmpleado(((CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera()).getDatosDescuentoPromocionEmpleados().getApiKeyPromocionEmpleados(),
							sesion.getAplicacion().getUidActividad(), (CARDOSOTicketManager) ticketManager,
							((CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera()).getDatosDescuentoPromocionEmpleados().getPromocionEmpleado()).start();
				}
			}
		}
		catch(Exception e){
			String msgError = "Error al aplicar promociones especiales : " + e.getMessage();
			log.error("initializeForm() - " + msgError, e);
			VentanaDialogoComponent.crearVentanaError(this.getStage(), msgError, e);
			promocionEspecialUsada = false;
		}
		
		/* En caso de no aplicar ninguna promoción especial, ocultamos Label del descuento especial. */
		if(!promocionEspecialUsada){
			lbDescuentosEspeciales.setDisable(true);
			lbDescuentosEspeciales.setVisible(false);
			
			refrescarDatosPantalla();
		}
	}
	
	public class GetUsosPromocionEmpleado extends BackgroundTask<BigDecimal>{
		
		private String apiKey;
		private String uidActividad;
		private PromocionBean promocion;
		private String numTarjetaFidelizado;
		
		private BigDecimal base;
		private BigDecimal total;
		private BigDecimal totalPagar;
		
		private BigDecimal procentajeDescuento;
		private BigDecimal importeTotalDescuento;
		
		public GetUsosPromocionEmpleado(String apiKey, String uidActividad, CARDOSOTicketManager ticketManager, PromocionBean promocion){
			super();
			this.apiKey = apiKey;
			this.uidActividad = uidActividad;
			this.promocion = promocion;
			this.numTarjetaFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado().getNumTarjetaFidelizado();
		
			this.base = ticketManager.getTicket().getTotales().getBase();
			this.total = ticketManager.getTicket().getTotales().getTotal();
			this.totalPagar = ticketManager.getTicket().getTotales().getTotalAPagar();
			
			this.procentajeDescuento = null;
			this.importeTotalDescuento = null;
		}
		
		@Override
		protected BigDecimal call() throws Exception{
			return CARDOSOClientRestPromocionEmpleados.getAcceso(posRestService.getUrlApiV1(), apiKey, uidActividad,
					numTarjetaFidelizado, promocion.getIdPromocion(), totalPagar);
		}
		
		@Override
		protected void succeeded(){
			super.succeeded();
			
			importeTotalDescuento = getValue();
			log.info("succeeded() -  RESULTADO IMPORTE PROMOCIÓN EMPLEADO : " + importeTotalDescuento);
			
			if(!BigDecimalUtil.isIgualACero(total)){
				/* Si el descuento no supone nada en el total no se aplica, caso de descontar 0.001 por ejemplo,
				 * asi evitamos que nos diga un porcentaje de, por ejemplo el 0.05, que en realidad no afecta al
				 * total de la venta */
				BigDecimal totalMenosDescuento = BigDecimalUtil.redondear(total.subtract(importeTotalDescuento));
				if(total.equals(totalMenosDescuento)){
					importeTotalDescuento = BigDecimal.ZERO;
					procentajeDescuento = BigDecimal.ZERO;
				}
				else{
					double aux = 100 / total.doubleValue();
					procentajeDescuento = BigDecimalUtil.redondear(new BigDecimal(100).subtract(
							((total.subtract(importeTotalDescuento)).multiply(new BigDecimal(aux))).abs()));
				}
			}
			else{
				/* Si la venta es cero no hay descuento */
				importeTotalDescuento = BigDecimal.ZERO;
				procentajeDescuento = BigDecimal.ZERO;
			}

			/* Si tenemos descuento lo aplicamos */
			if(!BigDecimalUtil.isIgualACero(procentajeDescuento)){
				
				PromocionEmpleadosCabeceraTicket datosPromocionEmpleados = ((CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera()).getDatosDescuentoPromocionEmpleados();
				
				datosPromocionEmpleados.setIdPromocion(promocion.getIdPromocion());
				datosPromocionEmpleados.setDescripcion(promocion.getDescripcion());
				datosPromocionEmpleados.setTipoDto(promocion.getTipoDto());
				datosPromocionEmpleados.setTotalVenta(ticketManager.getTicket().getTotales().getTotal());
				
				BigDecimal importeDescuento = BigDecimalUtil.redondear(importeTotalDescuento.divide(
						BigDecimalUtil.redondear(total.divide(base, 2, RoundingMode.HALF_UP)), 2, RoundingMode.HALF_UP));
				datosPromocionEmpleados.setImporteAhorro(importeDescuento);
				datosPromocionEmpleados.setImporteTotalAhorro(BigDecimalUtil.redondear(importeTotalDescuento));
				datosPromocionEmpleados.setDescuento(procentajeDescuento);
				
				((CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera()).setDatosDescuentoPromocionEmpleados(datosPromocionEmpleados);
				ticketManager.getTicket().getCabecera().getTotales().recalcular();
				
				/* Mostramos en pantalla el importe de descuento de la promoción de empleados. 
				 * Si el código pasa por aquí, significa que no se ha aplicado ninguna promoción monográfica, 
				 * con lo cual no pisa nada. */
				//setDescuentoEspecial(importeDescuento);
				setDescuentoEspecial(datosPromocionEmpleados.getImporteTotalAhorro());
			}
			
		}
		
		@Override
		protected void failed(){
			super.failed();
			Throwable e = getException();
			try{
				XMLDocument xml = new XMLDocument(promocion.getDatosPromocion());
				BigDecimal importeMaximo = xml.getNodo("importeMaximo").getValueAsBigDecimal();
				BigDecimal porcentajeDescuento = xml.getNodo("porcentajeDescuento").getValueAsBigDecimal();;

				BigDecimal importeCalculo = total;
				if(total.compareTo(importeMaximo) == 1){
					importeCalculo = importeMaximo;
				}
				importeTotalDescuento = BigDecimalUtil.redondear(importeCalculo.multiply(porcentajeDescuento).divide(new BigDecimal(100)));
			}
			catch(XMLDocumentException e1){
				log.error("aplicarPromocionesEmpleado() - Error al procesar el XML de la promoción de empleado : " + e.getMessage());
			}
		}
	}
	
	public void setDescuentoEspecial(BigDecimal importeDescuentoEspecial){
		log.info("setDescuentoEspecial() - IMPORTE DE DESCUENTO ESPECIAL : " + importeDescuentoEspecial);
		
		lbDescuentosEspeciales.setDisable(false);
		lbDescuentosEspeciales.setVisible(true);
		lbDescuentosEspeciales.setText("DESCUENTOS ESPECIALES  " + FormatUtil.getInstance().formateaImporte(importeDescuentoEspecial));
		
		refrescarDatosPantalla();
	}
	
	@Override
	public void accionCancelar(){
		boolean hayPagos = false;
		for(IPagoTicket pago : (List<IPagoTicket>) ticketManager.getTicket().getPagos()) {
			if(pago.isEliminable()) {
				hayPagos = true;
				break;
			}
		}
		
		
		if(hayPagos){
			log.warn("accionCancelar() - Se ha intentado cancelar un ticket con pagos eliminables.");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Se han efectuados pagos. Debe cancelarlos para volver atrás."), getStage());
		}
		else {
			log.debug("accionCancelar() - Cancelando ticket sin pagos eliminables.");
			try {
				realizarComprobacionesTicketCierrePantalla();
			}
			catch(NoCerrarPantallaException e) {
				return;
			}
			
			/* GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS */
			promocionesEspecialesServicio.limpiarDatosPromocionesEspeciales((CARDOSOTicketManager) ticketManager);
			
			visor.modoVenta(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));
			escribirVisor();
			getDatos().put(ACCION_CANCELAR, Boolean.TRUE);
			getStage().close();
		}
	}
	
	@Override
	public void aceptar(){
		if(!enProceso){
			enProceso = true;
			log.debug("aceptar()");
			try{
				/* Comprobamos que se hayan cubierto los pagos */
				if((((TicketVenta) ticketManager.getTicket()).isPagosCubiertos() && ticketManager.getDocumentoActivo().getRequiereCompletarPagos())
				        || !ticketManager.getDocumentoActivo().getRequiereCompletarPagos()){
					
					/* GAP - PERSONALIZACIONES V3 - VALE PROMOCIONAL */
					calculaValePromocional();
					
					/* GAP - PERSONALIZACIONES V3 - SERIE ALBARÁN */
					((CARDOSOTicketManager)ticketManager).estableceSerieAlbaran();
					
					enProceso = false;
					((CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera()).setIdTaxfree("");
					//TAXFREE
					if(realizarTaxFree != null && realizarTaxFree) {
						solicitarTaxfree();
					}
					super.aceptar();
				}
				else{
					enProceso = false;
				}
			}
			catch(Exception e){
				log.error("aceptar().- Se ha producido un error al aceptar el pago", e);
				enProceso = false;
			}
		}
		else{
			log.warn("aceptar() - Pago en proceso");
		}
	}
	
	/**
	 * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - VALE PROMOCIONAL
	 * 
	 * Implementar la funcionalidad de al realizar una venta, utilizar un vale promocional.
	 * La configuración de este vale se encuentra en el archivo "valesPromocionales.properties".
	 */
	
	public void calculaValePromocional(){
		log.debug("calculaValePromocional() : GAP - PERSONALIZACIONES V3 - VALE PROMOCIONAL");
		
		CARDOSOCabeceraTicket cabecera = (CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera();
		/* Si tenemos datos de vales promocionales y tenemos articulos y el valos de los eruros de compra es mayor a cero */
		if((cabecera.getDatosValesPromocionales() != null) && (!cabecera.getDatosValesPromocionales().getListaArticulos().isEmpty())
		        && (cabecera.getDatosValesPromocionales().getEurosCompra().compareTo(BigDecimal.ZERO) > 0)){
			/* Inicializamos a cero el valor del vale */
			cabecera.getDatosValesPromocionales().setValorVale(BigDecimal.ZERO);
			Fecha fecha = new Fecha(new Date());
			Date fechaActual = (fecha.getFecha(fecha.getString("dd/MM/yyyy"), "dd/MM/yyyy")).getDate();

			/* Si tenemos activa la promocion de vales promocionales */
			if((cabecera.getDatosValesPromocionales().getFechaInicio().before(fechaActual) 
					|| cabecera.getDatosValesPromocionales().getFechaInicio().equals(fechaActual))
			        && (cabecera.getDatosValesPromocionales().getFechaFin().after(fechaActual) 
			        		|| cabecera.getDatosValesPromocionales().getFechaFin().equals(fechaActual))){
				
				cabecera.getDatosValesPromocionales().setValorVale(BigDecimal.ZERO);
				BigDecimal importeCalculoVale = BigDecimal.ZERO;
				BigDecimal totalPagar = ((CARDOSOTicketManager) ticketManager).getTicket().getTotales().getTotalAPagar();
				BigDecimal eurosCompra = cabecera.getDatosValesPromocionales().getEurosCompra();
				
				if((eurosCompra.compareTo(BigDecimal.ZERO) > 0) && (totalPagar.compareTo(BigDecimal.ZERO) > 0) && (totalPagar.compareTo(eurosCompra) >= 0)){
					for(int i = 0; i < ((CARDOSOTicketManager) ticketManager).getTicket().getLineas().size(); i++){
						LineaTicket Linea = (LineaTicket) ((CARDOSOTicketManager) ticketManager).getTicket().getLineas().get(i);
						/* Falta comprobar si el articulo se le puede aplicar el vale */
						for(int j = 0; j < cabecera.getDatosValesPromocionales().getListaArticulos().size(); j++){
							if(cabecera.getDatosValesPromocionales().getListaArticulos().get(j).equals(Linea.getArticulo().getCodArticulo())){
								importeCalculoVale = importeCalculoVale.add(Linea.getImporteTotalConDto().subtract(Linea.getImporteTotalPromocionesMenosIngreso()));
								/* Salimos del for porque ya hemos encontrado el articulo en la lista y contiuamos con el siguiente */
								break;
							}
						}
					}
					if(importeCalculoVale.compareTo(cabecera.getDatosValesPromocionales().getEurosCompra()) > 0){
						importeCalculoVale = importeCalculoVale.divide(cabecera.getDatosValesPromocionales().getEurosCompra(), 2, RoundingMode.HALF_UP);
						importeCalculoVale = importeCalculoVale.multiply(cabecera.getDatosValesPromocionales().getEurosVenta());
						importeCalculoVale = BigDecimalUtil.redondear(importeCalculoVale, 2, BigDecimal.ROUND_HALF_UP);
						cabecera.getDatosValesPromocionales().setValorVale(importeCalculoVale);
					}
				}
			}
		}
	}

	/**
	 * #########################################################################################
	 * GAP - PERSONALIZACIONES V3 - CAJA ESPECIAL
	 * 
	 * En caso de estar la variable completa y ser la caja indicada en esa variable no permitimos usar medio de pago con tarjeta.
	 */
	@Override
	public void anotarPago(BigDecimal importe){
		log.debug("anotarPago() : GAP - PERSONALIZACIONES V3 - CAJA ESPECIAL");
		
		String variable = variablesServices.getVariableAsString(CardosoTicketsService.VARIABLE_CAJA_ESPECIAL);
		if(StringUtils.isNotBlank(variable)){
			if(variable.equals(sesion.getSesionCaja().getCajaAbierta().getCodCaja()) && medioPagoSeleccionado.getTarjetaCredito()){
				String msgError = I18N.getTexto("La caja actual no tiene permitido realizar pagos con el medio de pago seleccionado.");
				log.error("anotarPago() - " + msgError);
				VentanaDialogoComponent.crearVentanaError(msgError, getStage());
				return;
			}
		}
		super.anotarPago(importe);
		
	}
	
	@Override
	protected void selectPayment(PaymentsSelectEvent event){
		super.selectPayment(event);
		establecerControlFocosDefecto();
	}

	////////// TAXFREE //////////
	public void solicitarTaxfree() {
		try {
			log.debug("solicitarTaxfree() - Llamamos a la vetana de Taxfree");
			Boolean taxfreeRealizado = null;
			String url = taxfreeVariablesService.getUrlServicio();

			taxfreeRealizado = generarTaxfree(url, ticketManager.getTicket());
			
			solicitaTaxfree = taxfreeRealizado;
			Exception exceptionImpresion = null;
			log.debug("solicitarTaxfree() - Se realiza taxfree : " + taxfreeRealizado);
			if(taxfreeRealizado) {
				String barcode = (String)getDatos().get(TaxfreeController.BARCODE);
				String pasaporte = (String) getDatos().get("pasaporte");
				log.debug("solicitarTaxfree() - recogemos barcode " + barcode);
				cabecera = (CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera();
				
				log.debug("solicitarTaxfree() - seteamos barcode en el xml");
				cabecera.setIdTaxfree(barcode);
				cabecera.setPasaporte(pasaporte);

				VentanaDialogoComponent.crearVentanaInfo("TAXFREE generado correctamente", getStage());
				getDatos().put("taxfreeCanceladoDiferido", taxfreeCancelado);

			}
		
		} catch (Exception e) {
		log.error("ventanaTaxfree() - " + e.getMessage());
		VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error generando TAXFREE. Si lo desea puede volver a intentarlo"
				+ " desde la pantalla Gestion TAXFREE en el apartado de Ventas"), getStage());
		}
	}

	private Boolean generarTaxfree(String url, ITicket ticket) throws Exception {
		if (getDatos() == null) {
			this.datos = new HashMap<String, Object>();
		}

		log.debug("generarTaxfree() - seteamos en getDatos() los parametros necesarios");
		getDatos().put("url", url);
		getDatos().put("ticket", ticket);
		getDatos().put("esDiferido", Boolean.FALSE);
		getDatos().put("paisesDisponiblesTaxfree", paisesDisponiblesTaxfree);
		getApplication().getMainView().showModalCentered(TaxfreeView.class, getDatos(), getStage());
		
		if (getDatos().containsKey(TaxfreeController.PARAMETRO_TAXFREE_CANCELADO)) {
			taxfreeCancelado = (Boolean)getDatos().get(TaxfreeController.PARAMETRO_TAXFREE_CANCELADO);
			if (taxfreeCancelado) {
				log.debug("generarTaxfree() - Cancelamos Taxfree");
				return Boolean.FALSE;
			} else {
				log.debug("generarTaxfree() - Generamos el taxfree");
				return Boolean.TRUE;
			}
		} else {
			return false;
		}
	}
	
	@FXML
	public void accionComprobarTaxFree() {
		log.info("accionComprobarTaxFree() - Comprobando si la operación es susceptible de TaxFree");
		BackgroundTask<String> task = new BackgroundTask<String>(){

			@Override
			protected String call() throws Exception {
				TaxfreeCountryRequest countryRequest = taxfreeService.crearCountryRequest();
				return taxfreeWebService.llamadaTaxfree(new Gson().toJson(countryRequest));
			}
			
			@Override
			protected void succeeded() {
				super.succeeded();
				Boolean paisOK = null;
				Boolean superaImporteMinimo = null;
				try {
					log.debug("accionComprobarTaxFree() - Comprobando tipo de documento");
					taxfreeService.comprobarTipoDocumento();
				}catch (Exception e){
					String msgError = "No se ha encontrado el tipo de documento para la generación de TAXFREE. Por favor, contacte con el administrador.";
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto(msgError), getStage());
				}
				
				List lineasTicket = ticketManager.getTicket().getLineas();
				BigDecimal importeTotal = ticketManager.getTicket().getTotales().getTotal();
				BigDecimal impuestos = ticketManager.getTicket().getTotales().getImpuestos();
				FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
				DatosFactura datosCliente = ticketManager.getTicket().getCabecera().getCliente().getDatosFactura();
				boolean esVentaAnonima = true;
				if(datosFidelizado != null || datosCliente != null){
					esVentaAnonima = false;
				}
				String codPais = null;
				if(!esVentaAnonima) {
					codPais = datosFidelizado!= null ? datosFidelizado.getCodPais() : null;
					if(StringUtils.isBlank(codPais))
					codPais = datosCliente!= null ? datosCliente.getPais() : null;
				}
				
				log.debug("accionComprobarTaxFree() - Comprobando importe y pais del cliente");
				try {
					taxfreeService.comprobacionesPreviasTicket(lineasTicket, importeTotal, impuestos);
					
				} catch (Exception e) {
					log.error("accionComprobarTaxFree() Task - " + e);
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto(e.getMessage()), getStage());
				}
				try {
					comprobarCantidadYPrecio();
					
					if (ticketManager.getTicket().getLineas().size() == 0) {
						throw new Exception();
					}
					
					try {
						//Añadir distintos paises y moficar la variable del metodo comparaImporteMinimo segun el pais al que pertenezca
						switch (sesion.getAplicacion().getTienda().getCliente().getCodpais()) {
						case "PT":
							superaImporteMinimo = taxfreeService.comparaImporteMinimo(importeTotal);
							break;
						default:
							superaImporteMinimo = true;
							break;
						}		
					} catch (Exception e) {
						log.error("comprobarDisponibilidadTaxFreeTask() - Error obteniendo la variable de importe minimo de la bbdd " + e);
					}
					
					String contenidoRespuesta = getValue();
					
					log.debug("llamadaTaxfree() CardosoPagosController - parseando respuesta a objeto");
					TaxfreeCountryResponse response = null;
					try {
						response = new Gson().fromJson(contenidoRespuesta, TaxfreeCountryResponse.class);
						paisesDisponiblesTaxfree = response;
						
						if(response != null && response.getHasError().equals("false") && !response.getDetails().getCountries().isEmpty()) {
							for (Country countryResponse : response.getDetails().getCountries()) {
								if(countryResponse.getCountryId().equalsIgnoreCase(codPais)) {
									//si la lista tiene el pais del cliente, este vive fuera de la UE por tanto es valido devolviendo true
									paisOK = true;
									break;
								}
							}
							//devuelve false si el pais de residencia esta en la UE (No valido)
							if(paisOK != null && !paisOK) 
							paisOK = false;
						}else {
							//Si no hay respuesta se prohibe el taxfree
							paisOK = false;
						}
						//permitimos el pais ya que es venta anonima
						if(paisOK == null)
							paisOK = true;
						
						Boolean disponibleTaxfree = (superaImporteMinimo && paisOK) ? true : false;
						
						if (disponibleTaxfree) {
							
							if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("TAXFREE disponible, ¿Desea realizar una operación TAXFREE sobre la venta actual?"), getStage())) {
								log.debug("accionComprobarTaxFree() - requisitos ok, confirma taxfree");
								realizarTaxFree = true;
								lbTaxFree.setVisible(true);
								lbTitulo.setText("Pagos - Operación con TAXFREE");
							}
						}else{
							VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No cumple los requisitos para poder generar la solicitud de taxfree."), getStage());
						}
					}
					catch (Exception e){
						VentanaDialogoComponent.crearVentanaAviso("El usuario y la contraseña no son correctos. Pongase en contacto con el administrador.", getStage());
					}
				
				}
				catch (Exception e) {
					
					VentanaDialogoComponent.crearVentanaAviso("El ticket no tiene lineas suceptibles de Taxfree.", getStage());
				}
			}
			
			@Override
			protected void failed() {
				super.failed();
				Throwable e = getException();
				log.error("llamadaCreateTaxfree:failed() - Error recuperando la respuesta del ws: " + e.getMessage(), e);
			}
			
		};
		task.start();
		
	}

	private void comprobarCantidadYPrecio() {
	    log.debug("comprobarCantidadYPrecio() - Se comprobará la cantidad y el precio total, para ver si cumple las condiciones para ser enviadas las líneas.");
	    boolean puedeRealizarTaxfree = true;
	    List<CARDOSOLineaTicket> listaLineasTicket = ticketManager.getTicket().getLineas();
	    Map<String, List<CARDOSOLineaTicket>> mapaAgrupado = new HashMap<>();
	    try {
	        for (CARDOSOLineaTicket linea : listaLineasTicket) {
	            String codArticulo = linea.getCodArticulo();

	            if (mapaAgrupado.containsKey(codArticulo)) {
	                mapaAgrupado.get(codArticulo).add(linea);
	            } else {
	                List<CARDOSOLineaTicket> nuevaLista = new ArrayList<>();
	                nuevaLista.add(linea);
	                mapaAgrupado.put(codArticulo, nuevaLista);
	            }
	        }

	        Iterator<CARDOSOLineaTicket> iter = listaLineasTicket.iterator();
	        while (iter.hasNext()) {
	            CARDOSOLineaTicket linea = iter.next();
	            String codArticulo = linea.getCodArticulo();
	            List<CARDOSOLineaTicket> lineasAgrupadas = mapaAgrupado.get(codArticulo);
	            BigDecimal sumatorioImporte = new BigDecimal(0);

	            for (CARDOSOLineaTicket agrupada : lineasAgrupadas) {
	                sumatorioImporte = sumatorioImporte.add(agrupada.getImporteTotalConDto());
	            }

	            log.debug("IMPORTE " + sumatorioImporte);
	            if (lineasAgrupadas.size() > 1 && sumatorioImporte.compareTo(new BigDecimal(0)) == 0) {
	                puedeRealizarTaxfree = false;
	            }

	            log.debug("El tamaño de la lista de las líneas es:" + listaLineasTicket.size());
	            if (!puedeRealizarTaxfree) {
	            	linea.setBorrada(true);
	                puedeRealizarTaxfree = true;
	            }

	            log.debug("El tamaño de la lista de las líneas es:" + listaLineasTicket.size());
	        }
	    } catch (Exception e) {
	        log.error("Ha ocurrido un error: ", e);
	        e.getMessage();
	    }
	}

	
	private void imprimirPDFTaxfreeTask() throws Exception {
		
		BackgroundTask<String> task = new BackgroundTask<String>(){

			@Override
			protected String call() throws Exception {
				log.debug("getPdfVoucherResponse()");
				TaxfreeBarCodeCreationRequest getPDFRequest = getPdfVoucherRequest();

				log.debug("getPdfVoucherResponse() - llamando al ws de impresion y obteniendo respuesta");
				return taxfreeWebService.llamadaTaxfree(new Gson().toJson(getPDFRequest));
			}
			
			@Override
			protected void succeeded() {
				super.succeeded();
				log.debug("getCountriesTask() taxfreeController - obteniendo respuesta");
				String contenidoRespuesta = getValue();
				
				log.debug("getPdfVoucherResponse() - parseando respuesta");
				TaxfreeBarCodeCreationResponse response = new Gson().fromJson(contenidoRespuesta, TaxfreeBarCodeCreationResponse.class);
				
				if (response.getHasError().equals("false")) {
					log.debug("imprimirPDFTaxfree() - parseando respuesta");
					byte[] decodedPDF = Base64.getDecoder().decode(response.getMessage());
					// PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
					PrintService[] ps = PrintServiceLookup.lookupPrintServices(null, null);

					if (ps.length == 0) {
						throw new IllegalStateException("No Printer found");
					}
					log.debug("Available printers: " + Arrays.asList(ps));

					PrintService printService = null;
					
					log.debug("imprimirPDFTaxfree() - Comprobando variable de impresion en bbdd");
					String taxFreeImpresora = (String) variablesServices.getVariableAsString(TAXFREE_IMPRESORA);

					ConfiguracionDispositivo config = null;
					
					String impresora = "IMPRESORA1";
					
					if (taxFreeImpresora != null) {
						log.debug("imprimirPDFTaxfree() - seteando impresora");
						if (taxFreeImpresora.equals("S")) {
							impresora = "IMPRESORA1";
//							impresora = "Microsoft Print to PDF (vdi)";
						}
						else {
							impresora = "IMPRESORA2";
						}
						for (PrintService impresoraSeleccionada : ps) {
							if(impresoraSeleccionada.getName().equals(impresora)) {
								printService = impresoraSeleccionada;
							}
						}

					}
					else {
						for (PrintService impresoraSeleccionada : ps) {
							if(impresoraSeleccionada.getName().equals(impresora)) {
								printService = impresoraSeleccionada;
							}
						}
					}

					log.debug("imprimirPDFTaxfree() - parseo para el PDF");
					try {
						PDDocument document = PDDocument.load(decodedPDF);

						PrinterJob printJob = PrinterJob.getPrinterJob();
						
						printJob.setPrintService(printService);
						printJob.setPageable(new PDFPageable(document));
						printJob.print();
						document.close();
					} catch (PrinterException | IOException e) {
						log.error("Error a la hora del parseo a PDF " + e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError("Error a la hora del parseo a PDF " + e.getMessage(), getStage());
					}
					impreso = true;
				}
			}
			
			@Override
			protected void failed() {
				super.failed();
				Throwable e = getException();
				log.error("getCountriesTask:failed() - Error recuperando la respuesta del ws: " + e.getMessage(), e);
			}
			
		};
		task.start();

	}
	
	private PrintService seleccionaImpresora(PrintService[] ps) {
		getDatos().put(PARAM_IMPRESORAS, ps);
		getApplication().getMainView().showModalCentered(SeleccionImpresoraView.class, getDatos(), getStage());
		PrintService printService = (PrintService) getDatos().get(SeleccionImpresoraController.PARAM_PRINTSERVICE);

		return printService;
	}
	
	private TaxfreeBarCodeCreationRequest getPdfVoucherRequest() throws Exception {
		log.debug("getPdfVoucherRequest() - creando peticion para webservice de impresion");

		TaxfreeBarCodeCreationRequest pdfRequest = new TaxfreeBarCodeCreationRequest();
		JSONOperationData jsonOpData = new JSONOperationData();

		pdfRequest.setOperationId("GetData");
		jsonOpData.setDataRequest("GetPDFVoucher");
		jsonOpData.setCustAccount(taxfreeVariablesService.getCustAccount());
		jsonOpData.setBarcode((String) getDatos().get(TaxfreeController.BARCODE));
		jsonOpData.setParameters(PDF_80);
		pdfRequest.setJSONOperationData(jsonOpData);

		return pdfRequest;
	}

	private TaxfreeBarCodeCreationResponse getPdfVoucherResponse() throws Exception {
		log.debug("getPdfVoucherResponse()");
		TaxfreeBarCodeCreationRequest getPDFRequest = getPdfVoucherRequest();

		log.debug("getPdfVoucherResponse() - llamando al ws de impresion y obteniendo respuesta");
		String contenidoRespuesta = taxfreeWebService.llamadaTaxfree(new Gson().toJson(getPDFRequest));

		log.debug("getPdfVoucherResponse() - parseando respuesta");
		TaxfreeBarCodeCreationResponse response = new Gson().fromJson(contenidoRespuesta, TaxfreeBarCodeCreationResponse.class);

		return response;
	}

	
	protected void imprimirPDF() {
		log.debug("imprimirPDF() - impresion de ws en PDF");
		if (solicitaTaxfree != null && solicitaTaxfree) {
			try {
				imprimirPDFTaxfreeTask();
			} catch (Exception e) {
				log.error("imprimirPDFTaxfree() - Error imprimiendo en formato pdf " + e);
			}

		}
	}
	@Override
	protected void accionSalvarTicketSucceeded(boolean repiteOperacion) {
		imprimirPDF();
		super.accionSalvarTicketSucceeded(repiteOperacion);
		
	}

//	@Override
//	protected void imprimir() {
//		log.debug("imprimirPDF() - impresion PDF");		
//		imprimirPDF();
//		log.debug("imprimir() - impresion estandar");
//		super.imprimir();
//	}
	
	@Override
	protected void selectDefaultPaymentMethod() {
		super.selectDefaultPaymentMethod();
		// LUST-130797 se queda con el cliente en deshabilitar el foco del tfImporte
		lbTitulo.requestFocus();
	}
	
}
