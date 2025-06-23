package com.comerzzia.bimbaylola.pos.services.spark130f;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.comerzzia.bimbaylola.pos.persistence.fidelizacion.ByLFidelizacionBean;
import com.comerzzia.bimbaylola.pos.services.spark130f.exception.Spark130FException;
import com.comerzzia.bimbaylola.pos.services.ticket.ByLLineaTicket;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentUtils;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class Spark130FService {

	protected Logger log = Logger.getLogger(getClass());

	public static final int BUFFER_LENGTH = 500000;

	public static Socket socket;
	public static String socketID;
	
	private String ip;
	
	public String conecta() throws Exception  {
		log.debug("conecta() - Se inicia conexion por socket a la ip " + ip + ":8800");
		String socketID = null;
		if (socket == null || socket.isClosed()) {
			socket = new Socket(ip, 8800);
		}

		return socketID;
	}

	public void desconecta() throws IOException {
		log.debug("desconecta() - Desconexión de socket");
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}

	public String lecturaSocket() throws IOException {
		String respuesta = null;
		DataInputStream in;

		try {
			in = new DataInputStream(socket.getInputStream());

			byte[] messageByte = new byte[BUFFER_LENGTH];
			boolean end = false;
			StringBuilder dataString = new StringBuilder(BUFFER_LENGTH);
			int totalBytesRead = 0;
			int totalBytesResponse = 0;
			while (!end) {
				int currentBytesRead = in.read(messageByte);
				if (currentBytesRead != -1) {
					totalBytesRead = currentBytesRead + totalBytesRead;
					if (totalBytesRead <= BUFFER_LENGTH) {
						dataString.append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
					}
					else {
						dataString.append(new String(messageByte, 0, BUFFER_LENGTH - totalBytesRead + currentBytesRead, StandardCharsets.UTF_8));
					}

					if (dataString.length() >= BUFFER_LENGTH || (totalBytesRead >= totalBytesResponse && totalBytesResponse > 0)) {
						end = true;
					}

					if (!dataString.toString().trim().isEmpty()) {
						end = true;
					}
				}
				else {
					end = true;
				}

				respuesta = dataString.toString().replace("&quot;", "\"").trim();
				log.debug("lecturaSocket() - " + respuesta);
			}
		}
		catch (IOException e) {
			log.warn("lecturaSocket() - Error al recuperar información del socket -" + e.getMessage());
			throw new IOException(e.getMessage(), e);
		}

		return respuesta;
	}

	public void enviarPeticion(String peticion) throws IOException {
		log.debug("enviarPeticion() - " + peticion);
		DataOutputStream salida;

		salida = new DataOutputStream(socket.getOutputStream());
		salida.write(peticion.getBytes());
	}
	
	public String realizarLlamada(String comando) throws Spark130FException {
		String respuesta = null;
		if (getIp() == null) {
			String ip = Dispositivos.getInstance().getImpresora1().getConfiguracion().getParametrosConfiguracion().get("IP");
			setIp(ip);
		}

		try {
			conecta();
			enviarPeticion(comando);
			respuesta = lecturaSocket();
			desconecta();
		}
		catch (Exception e) {
			throw new Spark130FException(e.getMessage());
		}
		return respuesta;
	}

	public String getCashier() {
		return "<RParam><SEC>CASHIER</SEC></RParam>";
	}

	/**
	 * Shift Status 0 -> Closed | 1 -> Open | 9 -> Overdue >24h
	 */
	public String shiftStatus() {
		return "<GetShStat Mod='0'/>";
	}
	
	public String closeShift() {
		return "<CloseShift/>";
	}
	
	public String openShift() {
		return "<OpenShift/>";
	}
	
	public String printXReport() {
		return "<GetShStat Mod='1' Print='1'/>";
	}
	
	public String statusLastDocument() {
		return "<GetDocStat/>";
	}
	
	public String fnStatus() {
		return "<GetFNSt/>";
		/*
		 * Posiblemente haya que realizar la siguiente llama manualmente "<GetFNStat Mod='2'/>"
		 */
	}
	
	public String printLastDocument() {
		return "<DublCh/>";
	}
	
	public String cancelLastDocument() {
		return "<CancelFD/>";
	}

	public String addText(String text) {
		return "<AddTXT><Str>" + text + "</Str> </AddTXT>";
	}
	
	public String printDocumentByFD(String fiscalDocument) {
		return "<GetFDocFull FD='" + fiscalDocument + "'/>";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Spark130FOutput procesoVentaDev(ITicket ticket) throws Spark130FException {
		log.debug("procesoVentaDev() - Inicio del proceso de venta ");
		HashMap<String, String> mapaCampos = null;
		String returnCode = null;		
		Spark130FOutput spark130F = null;
		
		try {
			List<String> listaCampos = new ArrayList<String>();
			listaCampos.add(Spark130FConstants.ATT_RC);
			
			/* Abrimos el documento fiscal 
			 * p1054 -> Tipo de transacción (1 - sale | 2 - refund) 
			 * Opcional
			 * p1008 -> email (con @) / telefono (con +) del fidelizado 
			 * p1228 -> documento del fidelizado
			 * p1227 -> nombre completo del fidelizado  */
			
			String tipoTransaccion = ticket.isEsDevolucion() ? "2" : "1";
			String cadenaSCh = "<SCh><p1054>" + tipoTransaccion + "</p1054><p1055>0</p1055>";
			if (ticket.getCabecera().getDatosFidelizado() != null) {
				String nombreCompleto = ((ByLFidelizacionBean) ticket.getCabecera().getDatosFidelizado()).getNombre() + ((ByLFidelizacionBean) ticket.getCabecera().getDatosFidelizado()).getApellido();
				String documento = ((ByLFidelizacionBean) ticket.getCabecera().getDatosFidelizado()).getDocumento();
				String email = ((ByLFidelizacionBean) ticket.getCabecera().getDatosFidelizado()).getEmail();
				String telefono = ((ByLFidelizacionBean) ticket.getCabecera().getDatosFidelizado()).getTelefono();

				cadenaSCh += "<p1227>" + nombreCompleto + "</p1227>";
				cadenaSCh += "<p1008>" + email == null ? telefono : email + "</p1227>";
				cadenaSCh += "<p1228>" + documento + "</p1228>";
			}
			cadenaSCh += "</SCh>";

			mapaCampos = getCamposRespuesta(realizarLlamada(cadenaSCh), listaCampos);
			returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);
			
			if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {
				/* Realizamos el start */
				mapaCampos = getCamposRespuesta(realizarLlamada("<OCh>"), listaCampos);
				returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);

				if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {
					Boolean addItemError = Boolean.FALSE;
					List<LineaTicket> listaLineas = ticket.getLineas();
					for (LineaTicket lineaTicket : listaLineas) {
						
						/*
						 * Primero comprobamos si la linea tiene DataMatrix y en caso afirmativo haremos las llamadas
						 * necesarias
						 */
						if (((ByLLineaTicket) lineaTicket).getTrazabilidad() != null && ((ByLLineaTicket) lineaTicket).getTrazabilidad().getTieneTrazabilidad()) {
							Boolean resultado = checkDataMatrix((ByLLineaTicket) lineaTicket, ((ByLLineaTicket) lineaTicket).getTrazabilidad().getCadenasTrazabilidad().get(0), ticket.isEsDevolucion());
							
							if(!resultado) {
								confirmacionCheckDataMatrix();
							}
							
							envioDataMatrix((ByLLineaTicket) lineaTicket, ticket.isEsDevolucion());
						}

						/*
						 * p1079 -> Price -> Precio unitario x 100 (sin separador) 
						 * p1043 -> Amount -> Precio unitario x cantidad (should not differ by more than 1 kopeck from the product of the price (p1079) by
						 * 		the quantity (p1023)) 
						 * p1199 -> VatRate 
						 * p1023 -> Cantidad 
						 * p1030 -> Descripcion 
						 * p1197 -> Unidad de medida
						 */
						String price = FormatUtil.getInstance().formateaNumeroSinSeparador(lineaTicket.getPrecioTotalConDto());
						String amount = FormatUtil.getInstance().formateaNumeroSinSeparador(lineaTicket.getPrecioTotalConDto().multiply(lineaTicket.getCantidad().setScale(0)).abs());
						String cantidad = FormatUtil.getInstance().formateaNumeroSinSeparador(lineaTicket.getCantidad().setScale(0).abs());
						String cadena = "<AddItem><p1199>1</p1199>";
						cadena += "<p1030>" + lineaTicket.getDesArticulo() + "</p1030>";
						cadena += "<p2108>0</p2108>";
						cadena += "<p1023>" + cantidad + "</p1023>";
						cadena += "<p1079>" + price + "</p1079>";
						cadena += "<p1043>" + amount + "</p1043>";
						cadena += "<p1199>1</p1199>";
						cadena += "</AddItem>";

						mapaCampos = getCamposRespuesta(realizarLlamada(cadena), listaCampos);
						returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);

						if (returnCode == null || !returnCode.equals(Spark130FConstants.NO_ERROR)) {
							addItemError = Boolean.TRUE;
						}
						else {
							if (BigDecimalUtil.isMayorACero(lineaTicket.getDescuento())) {
								String textoDescuento = I18N.getTexto("Descuento del " + lineaTicket.getDescuentoFinalAsString() + "%");
								realizarLlamada(addText(textoDescuento));
							}
						}
					}

					if (!addItemError) {
						/* Total */
						mapaCampos = getCamposRespuesta(realizarLlamada("<Total/>"), listaCampos);
						returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);

						if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {
							/*
							 * Pago p1031 -> importe de cash p1081 -> importe de no-cash
							 */
							List<PagoTicket> listaPagos = ticket.getPagos();
							BigDecimal cashImporte = new BigDecimal(0);
							BigDecimal cardImporte = new BigDecimal(0);

							String cadenaPago = "<ECh>";
							String cadenaCMT = "";
							for (PagoTicket pagoTicket : listaPagos) {
								if (pagoTicket.getMedioPago().getEfectivo()) {
									cashImporte = cashImporte.add(pagoTicket.getImporte());
								}

								if (pagoTicket.getMedioPago().getTarjetaCredito()) {
									cardImporte = cardImporte.add(pagoTicket.getImporte());

									String cadenaCardNo = "";
									String cadenaAuthorization = "";
									String formaPago = null;
									if (pagoTicket.getDatosRespuestaPagoTarjeta() != null) {
										cadenaCardNo = I18N.getTexto(" Card No " + pagoTicket.getDatosRespuestaPagoTarjeta().getPAN());
										// cadenaAuthorization = I18N.getTexto(" Authorization Code:" +
										// StringUtils.replace(pagoTicket.getDatosRespuestaPagoTarjeta().getCodAutorizacion(),
										// " ",""));
										formaPago = pagoTicket.getDatosRespuestaPagoTarjeta().getMarcaTarjeta();
									}
									// String cadenaOwner = "\\x0A" + I18N.getTexto("Owner " +
									// pagoTicket.getDatosRespuestaPagoTarjeta().get);
									cadenaCMT = (formaPago == null ? pagoTicket.getFormaPago() : formaPago) + cadenaCardNo + cadenaAuthorization + I18N.getTexto(" Paid ")
									        + FormatUtil.getInstance().formateaImporte(pagoTicket.getImporte().abs());
									realizarLlamada("<AddTXT FN='000000'><Str>" + cadenaCMT + "</Str></AddTXT>");
								}
							}

							if (!BigDecimalUtil.isIgualACero(cardImporte)) {
								String cardString = FormatUtil.getInstance().formateaNumeroSinSeparador(cardImporte.setScale(2).abs());
								cadenaPago += "<p1081 CMT=''>" + cardString + "</p1081>";
							}
							String cashString = FormatUtil.getInstance().formateaNumeroSinSeparador(cashImporte.setScale(2).abs());
							cadenaPago += "<p1031>" + cashString + "</p1031></ECh>";

							mapaCampos = getCamposRespuesta(realizarLlamada(cadenaPago), listaCampos);
							returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);

							if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {

								/* Añadimos QR con el localizador */
								ticket.getCabecera().setFecha(new Date());
								mapaCampos = getCamposRespuesta(realizarLlamada("<AddBC TYPE='9' ALIGN='1'><VAL>" + ticket.getCabecera().getLocalizador() + "</VAL></AddBC>"), listaCampos);
								returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);

								if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {

									/*
									 * Footer Este comando nos devolverá el número de documento fiscal (FD) así como la
									 * firma del documento (FP) y la fecha
									 */
									mapaCampos = getCamposRespuesta(realizarLlamada("<CCh/>"), listaCampos);
									returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);

									if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {
										/*
										 * Cierre de documento fiscal Este comando nos devolverá el número de documento
										 * fiscal (FD) así como la firma del documento (FP) y la fecha Realiza impresión
										 */
										listaCampos.add(Spark130FConstants.TAG_KASSA);
										listaCampos.add(Spark130FConstants.TAG_SUMCK);
										listaCampos.add(Spark130FConstants.TAG_DATEFD);
										listaCampos.add(Spark130FConstants.TAG_TIMEFD);
										listaCampos.add(Spark130FConstants.ATT_FD);
										listaCampos.add(Spark130FConstants.ATT_FP);
										listaCampos.add(Spark130FConstants.ATT_CHECK);

										mapaCampos = getCamposRespuesta(realizarLlamada("<FCh/>"), listaCampos);
										returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);

										if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {
											spark130F = new Spark130FOutput();
											spark130F.setFd(mapaCampos.get(Spark130FConstants.ATT_FD));
											spark130F.setFp(mapaCampos.get(Spark130FConstants.ATT_FP));
											spark130F.setCheck(mapaCampos.get(Spark130FConstants.ATT_CHECK));
											spark130F.setKassa(mapaCampos.get(Spark130FConstants.TAG_KASSA));
											spark130F.setDate(mapaCampos.get(Spark130FConstants.TAG_DATEFD));
											spark130F.setTime(mapaCampos.get(Spark130FConstants.TAG_TIMEFD));
										}
									}
								}
							}
						}
					}
				}

			}
			
			if(!returnCode.equals(Spark130FConstants.NO_ERROR)) {
				Map<String, String> mapaErrores = Spark130FConstants.setErrors();
				
				String errorDesc = mapaErrores.get(returnCode);
				throw new Spark130FException(errorDesc);
			}
			
			log.debug("procesoVenta() - Fin del proceso de venta ");
		}
		catch (Exception e) {
			try {
				desconecta();
			}
			catch (IOException e1) {
				log.error("procesoVenta() - Ha ocurrido un error al realizar la desconexión con la impresora fiscal", e);
			}
			
			throw new Spark130FException(e.getMessage());
		}

		return spark130F;
	}

	public HashMap<String, String> getCamposRespuesta(String respuesta, List<String> listaCampos) throws Spark130FException {
		Document xml;
		HashMap<String, String> mapaCampos = null;
		try {
			xml = XMLDocumentUtils.createDocumentBuilder().parse(new ByteArrayInputStream(respuesta.getBytes()));

			mapaCampos = new HashMap<String, String>();
			Element root = xml.getDocumentElement();

			for (String campo : listaCampos) {
				String value = null;
				// if (campo.equals(Spark130FConstants.TAG_KASSA) || campo.equals(Spark130FConstants.TAG_SUMCK) ||
				// campo.equals(Spark130FConstants.TAG_DATEFD)
				// || campo.equals(Spark130FConstants.TAG_TIMEFD)) {
				if (campo.equals(Spark130FConstants.NODE_VALUE)) {
					value = root.getChildNodes().item(0).getNodeValue();
				}
				else {
					Element element = XMLDocumentUtils.getElement(root, campo, true);
					if (element != null) {
						value = element.getFirstChild().getNodeValue();
					}
					// }
					else {
						String atributo = XMLDocumentUtils.getAttribute(root, campo, true);
						if (StringUtils.isNotBlank(atributo)) {
							value = atributo;
						}
					}
				}

				if (value != null && !value.isEmpty()) {
					mapaCampos.put(campo, value);
				}
			}
		}
		catch (SAXException | IOException | XMLDocumentException e) {
			throw new Spark130FException(e.getMessage());
		}

		return mapaCampos;
	}
	
	private void envioDataMatrix(ByLLineaTicket linea, boolean isDevolucion) throws Spark130FException {
		HashMap<String, String> mapaCampos = null;
		String returnCode = null;
		List<String> listaCampos = new ArrayList<String>();
		listaCampos.add(Spark130FConstants.ATT_RC);

		/*
		 * <AddItemMarkA> 
			  <p2007> 
			  		<p2000> Data Mark </p2000> 
			  		<p2110> assigned item status </p2110> 
			  </p2007>
		 * </AddItemMarkA>
		 */
		try {
			
			String itemStatus = isDevolucion ? "3" : "1";
			String cadena = "<AddItemMarkA><p2007>";
			cadena += "<p2000>" + linea.getTrazabilidad().getCadenasTrazabilidad().get(0) + "</p2000>"; /* En principio sólo habrá un DataMatrix por item */
			cadena += "<p2110>" + itemStatus + "</p2110>";
			cadena += "</p2007></AddItemMarkA>";

			mapaCampos = getCamposRespuesta(realizarLlamada(cadena), listaCampos);

			returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);
			if (!returnCode.equals(Spark130FConstants.NO_ERROR)) {
				Map<String, String> mapaErrores = Spark130FConstants.setErrors();

				String errorDesc = mapaErrores.get(returnCode);
				throw new Spark130FException(errorDesc);
			}
		}
		catch (Exception e) {
			try {
				desconecta();
			}
			catch (IOException e1) {
				log.error("procesoVenta() - Ha ocurrido un error al realizar la desconexión con la impresora fiscal", e);
			}

			throw new Spark130FException(e.getMessage());
		}
	}
	
//	private void envioDataMatrixProvisional(ByLLineaTicket linea, boolean isDevolucion) throws Spark130FException {
//		HashMap<String, String> mapaCampos = null;
//		String returnCode = null;
//		List<String> listaCampos = new ArrayList<String>();
//		listaCampos.add(Spark130FConstants.ATT_RC);
//		String cadena = "";
//		String itemStatus = isDevolucion ? "3" : "1";
//
//		try {
//			cadena = "<AddItemMark>";
//			cadena += "<p2000>" + linea.getTrazabilidad().getCadenasTrazabilidad().get(0) + "</p2000>";
//			cadena += "<p1023>" + linea.getCantidad().setScale(0).abs() + "</p1023>";
//			cadena += "<p2108>0</p2108><p2003>1</p2003>";
//			cadena += "</AddItemMark>";
//
//			mapaCampos = getCamposRespuesta(realizarLlamada(cadena), listaCampos);
//
//			returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);
//			if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {
//				cadena = "<GetTestReqKM>";
//				cadena += "<p2003>1</p2003>";
//				cadena += "<p1023>" + linea.getCantidad().setScale(0).abs() + "</p1023>";
//				cadena += "</GetTestReqKM>";
//
//				mapaCampos = getCamposRespuesta(realizarLlamada(cadena), listaCampos);
//				returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);
//
//				if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {
//					String nodeValue = mapaCampos.get(Spark130FConstants.NODE_VALUE);
//					linea.getTrazabilidad().setStatus(nodeValue);
//
//					cadena = "<SaveItemMark/>";
//					mapaCampos = getCamposRespuesta(realizarLlamada(cadena), listaCampos);
//					returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);
//
//					if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {
//						cadena = "<AddItemMarkA><p2007>";
//						cadena += "<p2000>" + linea.getTrazabilidad().getCadenasTrazabilidad().get(0) + "</p2000>";
//						cadena += "<p2110>" + itemStatus + "</p2110>";
//						cadena += "</p2007></AddItemMarkA>";
//
//						mapaCampos = getCamposRespuesta(realizarLlamada(cadena), listaCampos);
//
//						returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);
//					}
//				}
//			}
//
//			if (!returnCode.equals(Spark130FConstants.NO_ERROR)) {
//				Map<String, String> mapaErrores = Spark130FConstants.setErrors();
//
//				String errorDesc = mapaErrores.get(returnCode);
//				throw new Spark130FException(errorDesc);
//			}
//		}
//		catch (Exception e) {
//			try {
//				desconecta();
//			}
//			catch (IOException e1) {
//				log.error("procesoVenta() - Ha ocurrido un error al realizar la desconexión con la impresora fiscal", e);
//			}
//
//			throw new Spark130FException(e.getMessage());
//		}
//	}
	
	public Boolean checkDataMatrix(ByLLineaTicket linea, String cadenaTrazabilidad, Boolean isDevolucion)
			throws Spark130FException {
		Boolean resultado = false;
		HashMap<String, String> mapaCampos = null;
		String returnCode = null;
		List<String> listaCampos = new ArrayList<String>();
		listaCampos.add(Spark130FConstants.ATT_RC);
		listaCampos.add("p2005");
		listaCampos.add("p2109");

		try {
			String cadena = "<TestMark>";
			cadena += "<p2000>" + cadenaTrazabilidad + "</p2000>";
			cadena += "<p2003>1</p2003><p1023>1</p1023>";
			cadena += "</TestMark>";

			mapaCampos = getCamposRespuesta(realizarLlamada(cadena), listaCampos);
			returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);

			if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {
				cadena = "<GetTestReqKM>";
				cadena += "<p2003>1</p2003>";
				cadena += "<p2108>0</p2108>";
				cadena += "<p1023>" + linea.getCantidad().setScale(0).abs() + "</p1023>";
				cadena += "</GetTestReqKM>";

				mapaCampos = getCamposRespuesta(realizarLlamada(cadena), listaCampos);
				returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);

				if (returnCode != null && returnCode.equals(Spark130FConstants.NO_ERROR)) {
					confirmacionCheckDataMatrix();

					resultado = true;
				} else if (returnCode != null && returnCode.equals(Spark130FConstants.DM_INCORRECTO)) {

					if (isDevolucion) {
						confirmacionCheckDataMatrix();
					}
					return resultado;
				}
			}

			if (!returnCode.equals(Spark130FConstants.NO_ERROR)) {
				Map<String, String> mapaErrores = Spark130FConstants.setErrors();

				String errorDesc = mapaErrores.get(returnCode);
				throw new Spark130FException(errorDesc);
			}
		} catch (Exception e) {
			try {
				desconecta();
			} catch (IOException e1) {
				log.error("procesoVenta() - Ha ocurrido un error al realizar la desconexión con la impresora fiscal",
						e);
			}

			throw new Spark130FException(e.getMessage());
		}

		return resultado;
	}
	
	public Boolean confirmacionCheckDataMatrix() throws Spark130FException {
		HashMap<String, String> mapaCampos = new HashMap<String, String>();
		List<String> listaCampos = new ArrayList<String>();
		listaCampos.add(Spark130FConstants.ATT_RC);

		String cadena = "<SaveItemMark/>";
		mapaCampos = getCamposRespuesta(realizarLlamada(cadena), listaCampos);
		String returnCode = mapaCampos.get(Spark130FConstants.ATT_RC);

		if (!returnCode.equals(Spark130FConstants.NO_ERROR)) {
			Map<String, String> mapaErrores = Spark130FConstants.setErrors();

			String errorDesc = mapaErrores.get(returnCode);
			throw new Spark130FException(errorDesc);
		}
		
		return true;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String toHex(String arg) {
		StringBuilder stringBuilder = new StringBuilder();

		char[] charArray = arg.toCharArray();
		for (char c : charArray) {
			String charToHex = Integer.toHexString(c);
			stringBuilder.append(charToHex);
		}

		return stringBuilder.toString();
	}

	public String tratamientoHexDataMatrix(String cadenaDataMatrix, List<Integer> listaCaracteresEspeciales) {
		StringBuilder sb = new StringBuilder(cadenaDataMatrix);
		for (Integer charControl : listaCaracteresEspeciales) {
			sb.insert(charControl, " ");
		}
		String dataMatrix = null;
		String[] arrayTexto = sb.toString().split(" ");
		
		String dataMatrix1 = toHex(arrayTexto[0].trim());
		String dataMatrix2 = toHex(arrayTexto[1].trim());
		String dataMatrix3 = toHex(arrayTexto[2].trim());

		dataMatrix = dataMatrix1 + "1d" + dataMatrix2 + "1d" + dataMatrix3;

		return dataMatrix;
	}
}
