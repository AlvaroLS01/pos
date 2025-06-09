package com.comerzzia.pampling.pos.devices.impresoras.fiscal.italia;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.pampling.pos.services.ticket.cabecera.PamplingCabeceraTicket;
import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.Dispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.dispositivo.fiscal.IFiscalPrinter;
import com.comerzzia.pos.persistence.core.impuestos.porcentajes.PorcentajeImpuestoBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.stage.Stage;
import jpos.FiscalPrinter;
import jpos.FiscalPrinterConst;
import jpos.JposException;
import jpos.util.JposPropertiesConst; 


public class ItaliaFiscalPrinter extends Dispositivo implements IPrinter, IFiscalPrinter {

	public static final String CADENA_QR = "QR";
	
	private Logger log = Logger.getLogger(ItaliaFiscalPrinter.class);
	
	private FiscalPrinter fiscalPrinter;
	
	private List<String> impresionNoFiscal;
	private String lineaImpresionNormal;
	
	private List<String> lineasPie;
	
	private String rtType;

	@Override
	public void conecta() throws DispositivoException {
		try {
			fiscalPrinter = new FiscalPrinter();

			//System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME ,"C:\\Tier1\\CLIENTES\\PAMPLING\\CZZ\\JAVA\\4.8.1\\POS\\comerzzia-pampling-pos\\comerzzia-pampling-pos-resources\\src\\main\\resources\\lib\\ext\\jpos.xml");

			log.debug("conecta() - Fiscal Printer opening");
			fiscalPrinter.open("FiscalPrinter1");
			log.debug("conecta() - Fiscal Printer open OK");
			log.debug("conecta() - Fiscal Printer claiming");
			fiscalPrinter.claim(3000);
			log.debug("conecta() - Fiscal Printer claiming OK");
			log.debug("conecta() - Fiscal Printer enabling");
			fiscalPrinter.setDeviceEnabled(true);
			log.debug("conecta() - Fiscal Printer enabling OK");
			
			if(!fiscalPrinter.getDayOpened()) {
				Map<Long, Map<String, PorcentajeImpuestoBean>> taxes = SpringContext.getBean(SesionImpuestos.class).getPorcentajes();
				loadTaxes(taxes);
				
				setPrinterData();
				
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
				String date = simpleDateFormat.format(new Date());
				fiscalPrinter.setDate(date);
			}

			setFooterLine();
			
			getStatus();
			
			log.debug("conecta() - Reiniciando impresora");
			fiscalPrinter.resetPrinter();
			
			getStatus();
		}
		catch (Throwable e) {
			log.error("conecta() - Ha habido un error al arrancar la impresora: " + e.getMessage(), e);
			throw new DispositivoException("Ha habido un error al arrancar la impresora: " + e.getMessage(), e);
		}
	}

	@Override
	public void desconecta() throws DispositivoException {
		try {
			log.debug("conecta() - Fiscal Printer disabeling");
			fiscalPrinter.setDeviceEnabled(false);
			log.debug("conecta() - 2FP - Fiscal Printer releasing");
			fiscalPrinter.release();
			log.debug("conecta() - Fiscal Printer closing");
			fiscalPrinter.close();
		}
		catch (Exception e) {
			log.error("conecta() - Ha habido un error al cerrar la impresora: " + e.getMessage(), e);
			throw new DispositivoException("Ha habido un error al cerrar la impresora: " + e.getMessage(), e);
		}
	}

	@Override
	public void sendTicket(TicketVentaAbono ticket) throws Exception {
		log.debug("sendTicket()");
		try {
			if (fiscalPrinter.getFiscalReceiptStation() != 1) {
				fiscalPrinter.resetPrinter();
			}

			BigDecimal factorConversionCantidades = new BigDecimal(1000);
			BigDecimal factorConversionImportes = new BigDecimal(10000);

			if (!(ticket.getCabecera() instanceof PamplingCabeceraTicket)) {
				throw new IllegalArgumentException(I18N.getTexto("No se puede proceder a la devolución. Contacte con el administrador."));
			}

			PamplingCabeceraTicket cabecera = (PamplingCabeceraTicket) ticket.getCabecera();

			boolean devolucionCompleta = cabecera.isDevolucionCompleta();
			if (devolucionCompleta) {
				devolucionCompleta = checkDocumentVoidable(cabecera);
			}

			String numReciboFiscalOrigen = cabecera.getNumReciboFiscalOrigen();
			if ("0000".equals(numReciboFiscalOrigen)) {
				numReciboFiscalOrigen = "0001";
			}

			if (devolucionCompleta) {
				log.debug("sendTicket() - Devolución completa");

				int[] icmd = { 0 };
				icmd[0] = 1078;

				StringBuffer sbcmd = new StringBuffer(
				        "0140001REFUND " + cabecera.getzRepNumOrigen() + " " + numReciboFiscalOrigen + " " + cabecera.getFechaReciboFiscalOrigen() + " " + cabecera.getPrinterIdOrigen());

				log.debug("sendTicket() - Request: " + sbcmd);
				fiscalPrinter.directIO(0, icmd, sbcmd);
				log.debug("sendTicket() - Result: " + sbcmd); // if the receipt is voided, printer return "operator +
				                                              // 50" (for ex. 01+50=51)
				if (new Integer(sbcmd.toString()) > 50) {
					return;
				}
			}
			else if (ticket.getCabecera().getDatosDocOrigen() != null) {
				log.debug("sendTicket() - Devolución parcial");

				boolean refundable = checkDocumentRefundable(cabecera);
				if (!refundable) {
					throw new IllegalArgumentException(I18N.getTexto("No se puede proceder a la devolución. Contacte con el administrador."));
				}

				int[] icmd = { 0 };
				icmd[0] = 1078;
				StringBuffer sbcmd = new StringBuffer(
				        "0140001REFUND " + cabecera.getzRepNumOrigen() + " " + numReciboFiscalOrigen + " " + cabecera.getFechaReciboFiscalOrigen() + " " + cabecera.getPrinterIdOrigen());

				log.debug("sendTicket() - Request: " + sbcmd);
				fiscalPrinter.directIO(0, icmd, sbcmd);
				log.debug("sendTicket() - Result: " + sbcmd);
			}

			log.debug("sendTicket() - beginFiscalReceipt");
			fiscalPrinter.beginFiscalReceipt(true);

			for (LineaTicket linea : ticket.getLineas()) {
				String descripcion = linea.getDesArticulo();
				descripcion = StringUtils.substring(descripcion, 0, 38);
				Long precio = Math.abs(linea.getImporteTotalConDto().multiply(factorConversionImportes).longValue());
				Integer cantidad = linea.getCantidad().abs().multiply(factorConversionCantidades).intValue();
				Long precioUnitario = linea.getPrecioTotalConDto().multiply(factorConversionImportes).longValue();
				Integer codImpuesto = new Integer(linea.getArticulo().getCodImpuesto());
				String unidad = "";

				log.debug("sendTicket() - Se va a imprimir la línea: " + descripcion + "(" + precio + " X " + cantidad + ")");

				BigDecimal descuento = linea.getDescuento();
				BigDecimal importeDescuento = linea.getImporteTotalConDto().subtract(linea.getImporteTotalSinDto()).abs();
				Long importeDescuentoFormateado = importeDescuento.multiply(factorConversionImportes).longValue();

				if (fiscalPrinter.getCapOrderAdjustmentFirst() && BigDecimalUtil.isMayorACero(descuento)) {
					log.debug("sendTicket() - RecItemAdjustment: " + FiscalPrinterConst.FPTR_AT_AMOUNT_DISCOUNT + ", SCONTO, " + importeDescuentoFormateado + ", " + codImpuesto);
					fiscalPrinter.printRecItemAdjustment(FiscalPrinterConst.FPTR_AT_AMOUNT_DISCOUNT, "SCONTO", importeDescuentoFormateado, codImpuesto);
				}

				if (ticket.getCabecera().getDatosDocOrigen() == null) {
					log.debug("sendTicket() - RecItem: " + descripcion + ", " + precio + ", " + cantidad + ", " + codImpuesto + ", " + precioUnitario + ", " + unidad);
					fiscalPrinter.printRecItem(descripcion, precio, cantidad, codImpuesto, precioUnitario, unidad);
				}
				else {
					log.debug("sendTicket() - RecRefund: " + descripcion + ", " + precio + ", " + codImpuesto);
					fiscalPrinter.printRecRefund(descripcion, precio, codImpuesto);
				}

				if (fiscalPrinter.getCapOrderAdjustmentFirst() && BigDecimalUtil.isMayorACero(descuento)) {
					log.debug("sendTicket() - RecItemAdjustment: " + FiscalPrinterConst.FPTR_AT_AMOUNT_DISCOUNT + ", SCONTO, " + importeDescuentoFormateado + ", " + codImpuesto);
					fiscalPrinter.printRecItemAdjustment(FiscalPrinterConst.FPTR_AT_AMOUNT_DISCOUNT, "SCONTO", importeDescuentoFormateado, codImpuesto);
				}
			}

			long base = ticket.getTotales().getBase().multiply(factorConversionImportes).longValue();
			log.debug("sendTicket() - RecSubtotal: " + base);
			fiscalPrinter.printRecSubtotal(base);

			long total = Math.abs(ticket.getTotales().getTotal().multiply(factorConversionImportes).longValue());
			
			List<PagoTicket> listaPagosAgrupados = obtenerPagosAgrupados(ticket.getPagos());
			for (PagoTicket pago : listaPagosAgrupados) {
				long importePago = pago.getImporte().abs().multiply(factorConversionImportes).longValue();
				log.debug("sendTicket() - printRecTotal: " + total + ", " + importePago + ", " + pago.getDesMedioPago());
				fiscalPrinter.printRecTotal(total, importePago, pago.getDesMedioPago());
			}

			fiscalPrinter.printRecMessage("Codice del biglietto: " + ticket.getCabecera().getCodTicket());

			printLocator(ticket.getCabecera().getLocalizador());

			for (String line : lineasPie) {
				fiscalPrinter.printRecMessage(line);
			}

			log.debug("sendTicket() - endFiscalReceipt");
			fiscalPrinter.endFiscalReceipt(false);

			readPrinterId(ticket);

			readReceiptNumber(ticket);

			readReceiptDate(ticket);

			readZReportNumber(ticket);
		} 
        catch (JposException e) {
            log.error("sendTicket() - JposException: " + e.getErrorCode() + " - " + e.getMessage(), e);
            if (e.getErrorCode() == 112) {
                Platform.runLater(() -> {
                    Stage mainStage = POSApplication.getInstance().getStage();
                    VentanaDialogoComponent.crearVentanaError("Por favor, revisa si la impresora está desconectada o en un estado incorrecto.", mainStage);
                });
                throw new PrinterDisconnectedException("La impresora está desconectada o en un estado incorrecto.", e);
            } else {
                try {
                    fiscalPrinter.resetPrinter();
                } catch (Throwable ex) {
                    log.error("sendTicket() - Error reiniciando impresora: " + ex.getMessage(), ex);
                }
                throw e;
            }
        } catch (Throwable e) {
            log.error("sendTicket() - Error: " + e.getMessage(), e);
            try {
                fiscalPrinter.resetPrinter();
            } catch (Throwable ex) {
                log.error("sendTicket() - Error reiniciando impresora: " + ex.getMessage(), ex);
            }
            throw e;
        }
    }
    
	private void readZReportNumber(TicketVentaAbono ticket) throws JposException {
		String[] strData = new String[1];
		int[] opt = new int[1];
		fiscalPrinter.getData(FiscalPrinterConst.FPTR_GD_Z_REPORT, opt, strData);
		String zRepNum = strData[0];
		int nextInt = Integer.parseInt(zRepNum) + 1;
		zRepNum = String.format("%04d", nextInt);
		((PamplingCabeceraTicket) ticket.getCabecera()).setzRepNum(zRepNum);		
		log.debug("readZReportNumber() - Z Report: "+ zRepNum);
	}

	private void readReceiptDate(TicketVentaAbono ticket) throws JposException {
		String[] strData = new String[1];
		fiscalPrinter.getDate(strData);
		String strDate = strData[0].substring(0, 8);
		((PamplingCabeceraTicket) ticket.getCabecera()).setFechaReciboFiscal(strDate);
		log.debug("readReceiptDate() - Date: "+ strDate);
	}

	private void readReceiptNumber(TicketVentaAbono ticket) throws JposException {
		String[] strData = new String[1];
		int[] opt = new int[1];
		fiscalPrinter.getData(FiscalPrinterConst.FPTR_GD_FISCAL_REC, opt, strData);
		String recNum = strData[0];
		((PamplingCabeceraTicket) ticket.getCabecera()).setNumReciboFiscal(recNum);
		log.debug("sendTicket() - Rec Num: "+ recNum);
	}

	private void readPrinterId(TicketVentaAbono ticket) throws JposException {
		String[] strData = new String[1];
		int[] opt = new int[1];

		fiscalPrinter.getData(FiscalPrinterConst.FPTR_GD_PRINTER_ID, opt, strData);
		log.debug("readPrinterId() - Returned printerId: " + strData[0]);
		String printerIdModel = strData[0].substring(0, 2);
		String printerIdNumber = strData[0].substring(4, 10);
		String printerIdManufacturer = strData[0].substring(2, 4);
		String printerId = printerIdManufacturer + rtType + printerIdModel + printerIdNumber;
		((PamplingCabeceraTicket) ticket.getCabecera()).setPrinterId(printerId);
		log.debug("readPrinterId() - Printer Id: " + printerId);
	}

	private boolean checkDocumentRefundable(PamplingCabeceraTicket cabecera) throws JposException {
		int[] icmd = {0};
		StringBuffer sbcmd = new StringBuffer("");
		
		icmd[0] = 9205;
		
		sbcmd = new StringBuffer("1" + cabecera.getPrinterIdOrigen() + cabecera.getFechaReciboFiscalOrigen() + cabecera.getNumReciboFiscalOrigen() + cabecera.getzRepNumOrigen());
		
		fiscalPrinter.directIO(0, icmd, sbcmd);
		
		log.debug("checkDocumentRefundable() - DirectIO - icmd[0] = " + icmd[0]);
		log.debug("checkDocumentRefundable() - DirectIO - sbcmd = " + sbcmd);
		
		if (sbcmd.substring(0, 1).equals("0") || sbcmd.substring(0, 1).equals("1")) {
			log.debug("checkDocumentRefundable() - Document refundable");
			return true;
		}
		else {
			log.debug("checkDocumentRefundable() - Document NOT refundable");
			return false;
		}
	}

	private boolean checkDocumentVoidable(PamplingCabeceraTicket cabecera) throws JposException {
		int[] icmd = {0};		
		icmd[0] = 9205;
		
		StringBuffer sbcmd = new StringBuffer("1" + cabecera.getPrinterIdOrigen() + cabecera.getFechaReciboFiscalOrigen() + cabecera.getNumReciboFiscalOrigen() + cabecera.getzRepNumOrigen());

		log.debug("checkDocumentRefundable() - DirectIO - icmd[0] = " + icmd[0]);
		log.debug("checkDocumentRefundable() - DirectIO - sbcmd = " + sbcmd);
		
		fiscalPrinter.directIO(0, icmd, sbcmd);
		
		log.debug("checkDocumentRefundable() - DirectIO - Result = " + sbcmd);
		
		if (sbcmd.substring(0, 1).equals("0") || sbcmd.substring(0, 1).equals("1")) {
			log.debug("checkDocumentRefundable() - Document voidable");
			return true;
		}
		else {
			log.debug("checkDocumentRefundable() - Document NOT voidable");
			return false;
		}
	}

	private boolean loadTaxes(Map<Long, Map<String, PorcentajeImpuestoBean>> taxes) {
		log.debug("loadTaxes() - Loading taxes in fiscal printer");
		
		Long idTratImpuestos = SpringContext.getBean(Sesion.class).getAplicacion().getTienda().getCliente().getIdTratImpuestos();
		
		try {
			if(fiscalPrinter.getCapHasVatTable()) {
				Map<String, PorcentajeImpuestoBean> map = taxes.get(idTratImpuestos);
				for(PorcentajeImpuestoBean taxItem : map.values()) {
					if(BigDecimalUtil.isMayorACero(taxItem.getPorcentaje())) {
						fiscalPrinter.setVatValue(new Integer(taxItem.getCodImpuesto()), FormatUtil.getInstance().formateaNumero(taxItem.getPorcentaje(), 0));
					}
				}
				
				// Mandamos los impuestos a la impresora
				fiscalPrinter.setVatTable();
			}
			
			checkVATTableEntry();

			return true;
		}
		catch (Throwable e) {
			log.error("loadTaxes() - Error: " + e.getMessage(), e);
			return false;
		}
	}
	
	private void checkVATTableEntry() {
		int[] icmd = { 0, 0, 0, 0 };
		StringBuffer sbcmd = new StringBuffer("");

		log.debug("checkVATTableEntry() - Checking VAT table");

		for (int i = 0; i < 11; i++) {
			String VATid = "";
			if (i < 10) {
				VATid = "0" + i;
			}
			else {
				VATid = "" + i;
			}

			try {
				icmd[0] = 4205;
				sbcmd = new StringBuffer(VATid);
				fiscalPrinter.directIO(0, icmd, sbcmd);
				if (sbcmd.length() == 6) {
					log.debug("checkVATTableEntry() - Aliquota IVA Gruppo " + i + " = " + sbcmd.substring(2, 4) + ",00%");
				}
			}
			catch (Throwable e) {
				log.error("checkVATTableEntry() - Error: " + e.getMessage(), e);
			}
		}
	}

	private void setPrinterData() throws JposException {
		setHeaderLines();
	}

	private void setHeaderLines() throws JposException {
		log.debug("setHeaderLines() - Setting header lines in Fiscal Printer");
		
		List<String> headerLines = new ArrayList<String>();
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("fiscalprinter-header.txt");
		Scanner sc = new Scanner(in);
		try {
			while(sc.hasNext()) {
				headerLines.add(sc.nextLine());
			}
		}
		finally {
			sc.close();
		}
		
		log.debug("setHeaderLines() - Header lines: " + headerLines);
		
		for(int i = 0 ; i < headerLines.size() -1 ; i++) {
			String line = headerLines.get(i);
			boolean doubleWidth = line.startsWith("**");
			line = line.replaceAll("\\*\\*", "");
			
			fiscalPrinter.setHeaderLine(i + 1, line, doubleWidth);
		}
	}

	private void setFooterLine() throws JposException {
		log.debug("setFooterLine() - Setting footer line in Fiscal Printer");
		
		lineasPie = new ArrayList<String>();
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("fiscalprinter-footer.txt");
		Scanner sc = new Scanner(in);
		try {
			while(sc.hasNext()) {
				lineasPie.add(sc.nextLine());
			}
		}
		finally {
			sc.close();
		}
		
		log.debug("setFooterLines() - Foote lines: " + lineasPie);
	}

	private void printLocator(String localizador) throws JposException {
		int QRCodeBarCodeCommand = 1075; // Comando nativo

        String[] strObj = new String[1];

        String myOperator = "01";
        String myPosition = "001";                // Allineamento. 000 = sinistra, 001 = centrato e 002 = destra.
        String myDimension = "008";               // Dimensione del QR code. Valori vanno da 001 a 016.
        String myDataType = "0";                   // Tipo di dati. 9 = dati binari. Diverso da 9 = dati alfanumerici.
        String myErrorCorrection = "2";           // Correzione degli errori. 0 = basso (7%), 1 = medio basso (15%), 2 = medio alto (25%) e 3 = alto (30%).
        String myQRCodeType = "92";                // 91 = tipo 1 e 92 = tipo 2. Consigliamo Il tipo 2 in quanto è più compatibile con i smartphone.
        String myBarCode = localizador;
        strObj[0] = myOperator + myPosition + "1" + myDimension + myDataType + myErrorCorrection + "00" + myQRCodeType + myBarCode;

        int[] aib = { 0 };
        aib[0] = QRCodeBarCodeCommand;
        fiscalPrinter.directIO(0, aib, strObj);
    }

	@Override
	public int getEstado() {
		return 0;
	}

	@Override
	public void setEstado(int estado) {
	}

	@Override
	public boolean isConfigurable() {
		return false;
	}

	@Override
	public void configurar(Stage stage) {
	}

	@Override
	public void inicializar() {
	}

	@Override
	public void cerrar() {
	}

	@Override
	public void empezarLinea(int size, int lineCols) {
		lineaImpresionNormal = "";
	}

	@Override
	public void terminarLinea() {
		impresionNoFiscal.add(lineaImpresionNormal);
	}

	@Override
	public void empezarDocumento(Map<String, Object> parametros) {
		impresionNoFiscal = new ArrayList<String>();
	}

	@Override
	public boolean terminarDocumento() {
		if(impresionNoFiscal != null && !impresionNoFiscal.isEmpty()) {
			try {
				fiscalPrinter.beginNonFiscal();
				
				for(String linea : impresionNoFiscal) {

					if (linea.startsWith(CADENA_QR)) {
						printLocator(linea.split(CADENA_QR)[1]);
					} else {
						fiscalPrinter.printNormal(2, linea);
					}
				}
				
				fiscalPrinter.endNonFiscal();
			}
			catch (JposException e) {
				log.error("terminarDocumento() - Ha habido un error al imprimir: " + e.getMessage(), e);
				
				try {
					log.debug("terminarDocumento() - Reiniciando impresora.");
					fiscalPrinter.resetPrinter();
				}
				catch (Throwable ex) {
					log.error("terminarDocumento() - Error al reiniciar la impresora: " + e.getMessage(), e);
				}
			}
		}
		return true;
	}

	@Override
	public void imprimirTexto(String texto, Integer size, String align, Integer style, String fontName, int fontSize) {
		String textoFormateado = texto;
		
		if(size == null) {
			size = texto.length();
		}
		
		try {
			if("center".equals(align)) {
				textoFormateado = StringUtils.center(texto, size);
			}
			else if("right".equals(align)) {
				textoFormateado = StringUtils.leftPad(texto, size);
			}
			else if("left".equals(align)) {
				textoFormateado = StringUtils.rightPad(texto, size);
			}
		}
		catch (Exception e) {
			log.error("imprimirTexto() - Error al imprimir texto: " + e.getMessage(), e);
		}
		
		lineaImpresionNormal = lineaImpresionNormal + textoFormateado;
	}

	@Override
	public void imprimirCodigoBarras(String codigoBarras, String tipo, String alineacion, int tipoAlineacionTexto, int height) {
		lineaImpresionNormal = lineaImpresionNormal + CADENA_QR + codigoBarras;
	}

	@Override
	public void abrirCajon() {
	}

	@Override
	public void cortarPapel() {
	}

	@Override
	public void imprimirLogo() {
	}

	@Override
	public void comandoEntradaPlantilla(String comandoEntradaTexto, int leftMargin) {
	}

	@Override
	public void comandoSalidaPlantilla(String comandoSalidaTexto) {
	}

	@Override
	public void comandoEntradaCodigoBarras(String comandoEntradaCodBar) {
	}

	@Override
	public void comandoSalidaCodBar(String comandoSalidaTexto) {
	}

	@Override
	public void comandoEntradaLinea(String comandoEntradaLinea) {
	}

	@Override
	public void comandoEntradaTexto(String comandoEntradaTexto) {
	}

	@Override
	public void comandoSalidaTexto(String comandoSalidaTexto) {
	}

	@Override
	public void comandoSalidaLinea(String comandoSalidaTexto) {
	}
	
	private void getStatus() throws JposException {
		int[] icmd = { 0 };
		StringBuffer sbcmd = new StringBuffer("");
		String rtMainStatus = "", rtSubStatus = "", sDailyOpen = "", sNoWorkingPeriod = "", fileToSend = "", oldFiletoSend = "", fileRejected = "", expiryDateCD = "", expiryDateCA = "",
		        trainingMode = "";
		boolean dailyOpen = false, noWorkingPeriod = false;

		// Check RT status
		try {
			log.debug("getStatus() - DirectIO (RT status)");
			icmd[0] = 1138;
			sbcmd = new StringBuffer("01");
			fiscalPrinter.directIO(0, icmd, sbcmd);
			log.debug("getStatus() - DirectIO - icmd[0] = " + icmd[0]);
			log.debug("getStatus() - DirectIO - sbcmd = " + sbcmd);
			if ((sbcmd.length()) >= 30) {
				log.debug("getStatus() - Printer is RT model");
				rtType = (sbcmd.toString()).substring(2, 3);
				rtMainStatus = (sbcmd.toString()).substring(3, 5);
				rtSubStatus = (sbcmd.toString()).substring(5, 7);
				sDailyOpen = (sbcmd.toString()).substring(7, 8);
				sNoWorkingPeriod = (sbcmd.toString()).substring(8, 9);
				fileToSend = (sbcmd.toString()).substring(9, 12);
				oldFiletoSend = (sbcmd.toString()).substring(12, 15);
				fileRejected = (sbcmd.toString()).substring(15, 18);
				expiryDateCD = (sbcmd.toString()).substring(21, 27);
				expiryDateCA = (sbcmd.toString()).substring(27, 33);
				trainingMode = (sbcmd.toString()).substring(33, 34);

				if (sDailyOpen.equals("1")) {
					dailyOpen = true;
				}

				if (sNoWorkingPeriod.equals("1")) {
					noWorkingPeriod = true;
				}

				log.debug("getStatus() - RT type : " + rtType);
				log.debug("getStatus() - RT Main status : " + rtMainStatus);
				log.debug("getStatus() - RT Sub status : " + rtSubStatus);
				log.debug("getStatus() - RT Daily open : " + dailyOpen);
				log.debug("getStatus() - RT No Working Period : " + noWorkingPeriod);
				log.debug("getStatus() - RT File to send n. : " + fileToSend);
				log.debug("getStatus() - RT Old File to send n. : " + oldFiletoSend);
				log.debug("getStatus() - RT File rejected n. : " + fileRejected);
				log.debug("getStatus() - RT Expiry Date CD : " + expiryDateCD);
				log.debug("getStatus() - RT Expiry Date CA : " + expiryDateCA);
				log.debug("getStatus() - RT Training mode: " + trainingMode);
			}
			else {
				log.debug("getStatus() - Printer is NOT RT model");
			}
		}
		catch (JposException je) {
			log.error("getStatus() - JposException - errorCode : " + je.getErrorCode() + " - errorCodeExtended : " + je.getErrorCodeExtended());
			throw je;
		}
	}

	@Override
	public void fiscalDailyReport()  {
		log.debug("fiscalDailyReport() - NO IMPLEMENTADO");
	}
	
	public void informeZ() throws JposException {
		log.debug("informeZ() - Se realiza la llamada para el informe Z");
		fiscalPrinter.printZReport();
		log.debug("informeZ() - Informe Z impreso");
	}
	
	@Override
	protected void cargaConfiguracion(ConfiguracionDispositivo config) throws DispositivoException {
	}

	@Override
	public Exception getLastException() {
		return null;
	}

	private List<PagoTicket> obtenerPagosAgrupados(List<PagoTicket> pagos) {
	    return pagos.stream()
	        .collect(Collectors.groupingBy(
	            PagoTicket::getCodMedioPago,
	            Collectors.collectingAndThen(
	                Collectors.toList(),
	                listaPagos -> {
	                    BigDecimal importeTotal = listaPagos.stream()
	                        .map(PagoTicket::getImporte)
	                        .reduce(BigDecimal.ZERO, BigDecimal::add);
	                    PagoTicket pagoAgrupado = new PagoTicket();
	                    pagoAgrupado.setCodMedioPago(listaPagos.get(0).getCodMedioPago());
	                    pagoAgrupado.setDesMedioPago(listaPagos.get(0).getDesMedioPago());
	                    pagoAgrupado.setImporte(importeTotal);
	                    return pagoAgrupado;
	                }
	            )
	        ))
	        .values().stream()
	        .collect(Collectors.toList());
	}

}
