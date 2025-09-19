package com.comerzzia.bimbaylola.pos.services.taxFree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.ByL.backoffice.persistencia.fidelizacion.fidelizados.FidelizadosBean;
import com.comerzzia.ByL.backoffice.rest.client.fidelizados.ByLFidelizadoRequestRest;
import com.comerzzia.ByL.backoffice.rest.client.fidelizados.ByLFidelizadosRest;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.CustomerAddress;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.CustomerData;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.CustomerName;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.ExtraInformation;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.Family;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.LineItem;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.PaymentMethodDetail;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.TaxFreeVersion;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.TransactionHeader;
import com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML.TransactionTotals;
import com.comerzzia.bimbaylola.pos.services.ticket.ByLTicketVentaAbono;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;

@Component
public class TaxFreeLeerXML {

	public static final String TAXFREE_RUTA_CARPETA = "TAXFREE.RUTA_CARPETA";
	public static final String NOMBRE_EJECUTABLE = "Pii.exe";
	public static final String NOMBRE_CARPETA_FICHEROS = "Taxfree Files";
	public static final String NOMBRE_CARPETA_VOUCHER = "Vouchers";

	private static final Logger log = Logger.getLogger(TaxFreeLeerXML.class.getName());

	
	@Autowired
	private static VariablesServices variablesServices;
	@Autowired
	private static Sesion sesion;
	
	public static void leerXML(ByLTicketVentaAbono ticket, String rutaFicheroXML) {
		TaxFreeTransactionData data = generaObjetoTaxFree(ticket);
		creaXMLTaxFree(data, rutaFicheroXML);
	}

	@SuppressWarnings("unchecked")
	private static TaxFreeTransactionData generaObjetoTaxFree(ByLTicketVentaAbono ticket) {

		TaxFreeTransactionData taxFree = new TaxFreeTransactionData();

		TaxFreeVersion version = new TaxFreeVersion();
//		version.setClient_version("2.0.2014.1201");
//		version.setPos_application_version("PII-2.0.2014.1201-STD");
//		version.setXml_version("2.0");

		TransactionHeader transactionHeader = new TransactionHeader();
		transactionHeader.setTransaction_type(ticket.isEsDevolucion() ? 0 : 1);
		transactionHeader.setBarcode_data(ticket.getCabecera().getCodTicket().replace("/", ""));
		transactionHeader.setInvoice_number(ticket.getCabecera().getCodTicket());
		transactionHeader.setNumber_of_items(ticket.getLineas().size());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fecha = sdf.format(ticket.getCabecera().getFecha());

		sdf = new SimpleDateFormat("hh:mm:ss");
		String hora = sdf.format(ticket.getCabecera().getFecha());
		
		transactionHeader.setTransaction_date(fecha);
		transactionHeader.setTransaction_time(hora);

		List<LineaTicket> lineas = ticket.getLineas();

		List<LineItem> listaItems = new ArrayList<LineItem>();
		for (LineaTicket lineaTicket : lineas) {
			LineItem item = new LineItem();

			item.setItem_number(lineaTicket.getIdLinea());
			item.setItem_description(lineaTicket.getDesArticulo());

			List<SubtotalIvaTicket> listaIva = ticket.getCabecera().getSubtotalesIva();
			if (listaIva != null && !listaIva.isEmpty()) {
				for (SubtotalIvaTicket subtotalIvaTicket : listaIva) {
					if (lineaTicket.getCodImpuesto().equals(subtotalIvaTicket.getCodImpuesto())) {
						item.setItem_vat_rate(subtotalIvaTicket.getPorcentaje());
					}
				}
			}

			item.setItem_net_amount(lineaTicket.getImporteConDto());
			item.setItem_gross_amount(lineaTicket.getImporteTotalConDto());
			item.setItem_vat_amount(lineaTicket.getImporteTotalConDto().subtract(lineaTicket.getImporteConDto()));
			item.setIndividual_item_value(lineaTicket.getPrecioTotalConDto());
			item.setItem_quantity(lineaTicket.getCantidad().intValue());

			item.setLine_item_eligible(true);

			ExtraInformation extraInformation = new ExtraInformation();
			extraInformation.setArticle_code(lineaTicket.getCodArticulo());

			// Se corresponde a una categorización de AEAT
			// De momento le pondremos por defecto MOD - Moda y accesorios
			Family family = new Family();
			family.setCode("MOD");
			family.setDescription("Moda y accesorios");

			// Brand brand = new Brand();
			// brand.setCode(code);
			// brand.setDescription(description);

			extraInformation.setFamily(family);
			item.setExtra_information(extraInformation);

			listaItems.add(item);
		}

		TransactionTotals transactionTotals = new TransactionTotals();
		transactionTotals.setTransaction_gross_amount(ticket.getTotales().getTotalAPagar());
		transactionTotals.setTransaction_net_amount(ticket.getTotales().getBase());
		transactionTotals.setTransaction_vat_amount(ticket.getTotales().getImpuestos());

		/*
		 * Metodos de pago CASH - 0 VISA - 1 MASTERCARD - 2 AMEX - 4 DINERS - 5 CREDIT CARD - 9
		 */

		List<PaymentMethodDetail> listPayment = new ArrayList<PaymentMethodDetail>();
		for (PagoTicket pago : ticket.getPagos()) {
			PaymentMethodDetail metodoPago = new PaymentMethodDetail();

			String medioPago = pago.getCodMedioPago();
			int tipoPago;
			switch (medioPago) {
				case "1002":
					tipoPago = 1;
					break;
				case "1003":
					tipoPago = 4;
					break;
				case "1004":
					tipoPago = 5;
					break;
				default:
					tipoPago = 0;
					break;
			}

			metodoPago.setPayment_method(tipoPago);
			metodoPago.setAmount(pago.getImporte());

			listPayment.add(metodoPago);
		}

		CustomerData customerData = new CustomerData();
		if (ticket.getCabecera().getDatosFidelizado() != null) {
			customerData.setCustomer_id(ticket.getCabecera().getDatosFidelizado().getIdFidelizado());

			variablesServices = SpringContext.getBean(VariablesServices.class);
			sesion = SpringContext.getBean(Sesion.class);
			String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
			String uidActividad = sesion.getAplicacion().getUidActividad();

			ByLFidelizadoRequestRest fidelizadoRequestRest = new ByLFidelizadoRequestRest(apiKey, uidActividad);
			fidelizadoRequestRest.setIdFidelizado(ticket.getCabecera().getDatosFidelizado().getIdFidelizado());
			FidelizadosBean fidelizado = new FidelizadosBean();
			try {
				fidelizado = ByLFidelizadosRest.getFidelizadoPorId(fidelizadoRequestRest);
			}
			catch (RestException | RestHttpException e) {
				e.printStackTrace();
			}

			//TODO Revisar el formato del numero de telefono
			if (fidelizado.getTipoContacto("MOVIL") != null) {
				customerData.setMobile_no("+34" + fidelizado.getTipoContacto("MOVIL").getValor());
			}

			if (fidelizado.getTipoContacto("EMAIL") != null) {
				customerData.setEmail(fidelizado.getTipoContacto("EMAIL").getValor());
			}

			CustomerAddress customerAddres = new CustomerAddress();
			customerAddres.setCustomer_address_line_one(fidelizado.getDomicilio());
			customerData.setCustomer_address(customerAddres);
			
			CustomerName customerName = new CustomerName();
			customerName.setCustomer_first_name(ticket.getCabecera().getDatosFidelizado().getNombre());
			customerName.setCustomer_last_name(ticket.getCabecera().getDatosFidelizado().getApellido());

			customerData.setCustomer_name(customerName);
		}

		taxFree.setVersion(version);
		taxFree.setTransaction_header(transactionHeader);
		taxFree.setInvoice_line_items(listaItems);
		taxFree.setTransaction_totals(transactionTotals);
		taxFree.setPayment_method_details(listPayment);
		taxFree.setCustomer_data(customerData);

		return taxFree;

	}

	private static void creaXMLTaxFree(TaxFreeTransactionData data, String rutaFicheroXML) {

		try {
			byte[] xml = MarshallUtil.crearXML(data);

			Path path = Paths.get(rutaFicheroXML);
			Files.write(path, xml);
		}
		catch (MarshallUtilException | IOException e) {
			String msg = "Se ha producido un error al generar el fichero XML del ticket " + data.getTransaction_header().getBarcode_data() + ": " + e.getMessage();
			log.error("creaXMLTaxFree() - " + msg, e);
		}
	}

}