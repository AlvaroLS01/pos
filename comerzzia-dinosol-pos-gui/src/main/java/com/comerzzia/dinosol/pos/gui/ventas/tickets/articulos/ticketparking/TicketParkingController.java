package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.ticketparking;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.parking.TicketParkingService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.dispositivo.impresora.parser.PrintParserException;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class TicketParkingController extends WindowController {

	public static String PARAM_MINUTOS_PARKING = "TicketParkingController.MinutosParking";
	public static String PARAM_ARTICULO_PARKING = "TicketParkingController.ArticuloParking";
	public static String PARAM_CODIGO_SALIDA = "TicketParkingController.CodigoSalidaParking";
	public static String PARAM_HORA_SALIDA = "TicketParkingController.HoraSalidaParking";
	public static String PARAM_ELIMINAR_PARKING = "TicketParkingController.EliminarParking";
	public static String PARAM_GENERAR_QR = "TicketParkingController.GenerarQR";

	public static final String ID_VARIABLE_MINUTOS_CORTESIA = "X_PARKING_MINUTOS_CORTESIA";
	public static final String ID_VARIABLE_FORMATO_CODIGO = "X_PARKING_FORMATO_CODIGO";
	public static final String ID_VARIABLE_CODART_PARKING = "X_PARKING_CODART";
	public static final String ID_VARIABLE_CODART_EXTRAVIO = "X_PARKING_CODART_EXTRAVIO";
	public static final String ID_VARIABLE_PREFIJO = "X_PARKING_PREFIJO";
	public static final String ID_VARIABLE_TERMINAL = "X_PARKING_TERMINAL";
	
	private Logger log = Logger.getLogger(getClass());
	
	@FXML
	private TextField tfCodigoParking;
	
	@FXML
	private CheckBox cbSalidaGratuita;
	
	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private TicketParkingService ticketParkingService;
	
	@Autowired
	private AuditoriasService auditoriasService;
	
	private String codartParking, codartExtravio;
	
	@SuppressWarnings("rawtypes")
    private ITicket ticket;
	
	@Override
    public void initialize(URL url, ResourceBundle rb) {
    }

	@Override
    public void initializeComponents() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();
		
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfCodigoParking.setUserData(keyboardDataDto);
		
		addSeleccionarTodoEnFoco(tfCodigoParking);
    }

	@Override
    public void initializeFocus() {
		tfCodigoParking.requestFocus();
    }

	@SuppressWarnings("rawtypes")
    @Override
    public void initializeForm() throws InitializeGuiException {
		tfCodigoParking.clear();

		codartParking = variablesServices.getVariableAsString(ID_VARIABLE_CODART_PARKING);
		if(StringUtils.isBlank(codartParking)) {
			throw new InitializeGuiException(I18N.getTexto("No está configurado correctamente el artículo del parking. Contacte con el administrador."));
		}
		
		codartExtravio = variablesServices.getVariableAsString(ID_VARIABLE_CODART_EXTRAVIO);
		if(StringUtils.isBlank(codartExtravio)) {
			throw new InitializeGuiException(I18N.getTexto("No está configurado correctamente el artículo de extravío del ticket del parking. Contacte con el administrador."));
		}
		
		ticket = (ITicket) getDatos().get(FacturacionArticulosController.TICKET_KEY);
		
		cbSalidaGratuita.selectedProperty().setValue(false);
		
		try {
			super.compruebaPermisos("PARKING GRATIS");
			cbSalidaGratuita.setDisable(false);
		}
		catch (SinPermisosException e) {
			cbSalidaGratuita.setDisable(true);
		}
    }
	
	public void aceptarIntro(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			aceptar();
		}
	}
	
	public void aceptar() {
		String codigoParking = tfCodigoParking.getText();
		String formatoParking = variablesServices.getVariableAsString(ID_VARIABLE_FORMATO_CODIGO);
		
		if("03".equals(formatoParking) && !cbSalidaGratuita.isSelected()) {
			if(StringUtils.isBlank(codigoParking)) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe leer el código de barras del parking."), getStage());
				return;
			}
			getDatos().put(PARAM_GENERAR_QR, true);
			getStage().close();
			return;
		}
		
		boolean articuloYaIntroducido = existeArticuloParkingEnTicket();
		if(articuloYaIntroducido) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Ya se ha introducido un artículo de parking en el ticket."), getStage());
			return;
		}
		
		if(cbSalidaGratuita.isSelected()) {
			generarTicketSalidaGratuita(); 
		}
		else if(StringUtils.isNotBlank(codigoParking)) {
			Date horaEntrada;
			
			// En modo desarrollo, si poner *<minutos> te hace la simulación de la hora de entrada sin tener que calcular un código de barras
			if (AppConfig.modoDesarrollo && "*".equals(StringUtils.left(codigoParking, 1))) {
				horaEntrada = DateUtils.addMinutes(new Date(), Integer.valueOf(codigoParking.substring(1)) * -1);
			} else {
 			   horaEntrada = leerCodigoBarras(codigoParking);
			}
			
			if(horaEntrada != null) {
				try {
				    Date horaSalida = new Date();
				    long milisegundosDiferencia = horaSalida.getTime() - horaEntrada.getTime();
				    Integer minutosDiferencia = Math.round(milisegundosDiferencia / 1000 / 60);
				    if(minutosDiferencia < 0) {
				    	VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La fecha del código de barras introducido no es válida ya que es un fecha futura."), getStage());
				    	
				    	tfCodigoParking.requestFocus();
				    	
				    	return;
				    }
				    
					String[] resultado = generarCodigoBarrasSalida(horaEntrada);
					
	                String codigo = resultado[0];
	                
					getDatos().put(PARAM_CODIGO_SALIDA, codigo);
	                getDatos().put(PARAM_HORA_SALIDA, resultado[1]);
	                getDatos().put(PARAM_ARTICULO_PARKING, codartParking);
				    getDatos().put(PARAM_MINUTOS_PARKING, minutosDiferencia);
	                
	                getStage().close();
                }
                catch (Exception e) {
                	log.error("aceptar() - Ha habido un error al generar el código de barras de salida: " + e.getMessage(), e);
        	    	VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido generar correctamente el código de barras. Contacte con el administrador"), e);
                }
			}
		}
		else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe leer el código de barras del parking."), getStage());
		}
	}
	
	@SuppressWarnings("unchecked")
    private boolean existeArticuloParkingEnTicket() {
		if(ticket == null) {
			return false;
		}
		
		for(LineaTicket linea : (List<LineaTicket>) ticket.getLineas()) {
			String codArticulo = linea.getCodArticulo();
			if(codArticulo.equals(codartParking) || codArticulo.equals(codartExtravio)) {
				return true;
			}
		}
		
	    return false;
    }

	private void generarTicketSalidaGratuita() {
		try {
			String formatoParking = variablesServices.getVariableAsString(ID_VARIABLE_FORMATO_CODIGO);
			
			String codigoSalida = null;
			String horaSalida = null;
			
			Date date = new Date();
			if("03".equals(formatoParking)) {
				int random = new Random().nextInt(99999);
				String marcaTiempo = 9 + StringUtils.leftPad(new Integer(random).toString(), 5, "0");
				codigoSalida = ticketParkingService.generarCodigoQrDescuento(marcaTiempo);
			}
			else {
		    	String[] resultado = generarCodigoBarrasSalida(date);
				
		        codigoSalida = resultado[0];
	            horaSalida = resultado[1];
			}
            
            imprimirTicketSalidaGratuita(codigoSalida, horaSalida);
		}
		catch(Exception e) {
			log.error("introducirArticuloExtravio() - Ha habido un error al generar el código de barras del parking: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido procesar correctamente el código de barras. Contacte con el administrador"), e);
		}
    }

	private void imprimirTicketSalidaGratuita(String codigoSalida, String horaSalida) {
		try {
			String formatoImpresion = "parking_gratuito";
	
			Map<String, Object> mapaParametros = new HashMap<String, Object>();
			mapaParametros.put("codigoSalida", codigoSalida);
			mapaParametros.put("horaSalida", horaSalida);
			if(StringUtils.isBlank(horaSalida)) {
				mapaParametros.put("formatoQr", true);				
			}
			mapaParametros.put("empresa", sesion.getAplicacion().getEmpresa());
			mapaParametros.put("tienda", sesion.getAplicacion().getTienda());
	
			ServicioImpresion.imprimir(formatoImpresion, mapaParametros);
			
			guardarAuditoria();
	        
	        getStage().close();
		}
		catch(Exception e) {
			log.error("imprimirTicketSalidaGratuita() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			
			String error = e.getMessage();
			if(e instanceof DeviceException) {
				if(e.getCause() instanceof PrintParserException) {
					error = e.getCause().getCause().getMessage();
				}
				else {
					error = e.getCause().getMessage();
				}
			}
			
			String mensajeError = I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir.") 
					+ System.lineSeparator() + System.lineSeparator() 
					+ error
					+ System.lineSeparator()
					+ I18N.getTexto("¿Desea reintentar la impresión?");
			
			VentanaDialogoComponent ventana = VentanaDialogoComponent.crearVentana(null, mensajeError, VentanaDialogoComponent.TIPO_ERROR, true, getStage(), e, true);
			if(ventana.isPulsadoAceptar()) {
				new BackgroundTask<Void>(){
					@Override
                    protected Void call() throws Exception {
						Dispositivos.getInstance().getImpresora1().reset();
	                    return null;
                    }
					
					@Override
					protected void succeeded() {
					    super.succeeded();
					    
					    imprimirTicketSalidaGratuita(codigoSalida, horaSalida);
					}
				}.start();
			}
			else {
		        getStage().close();
			}
		}
    }

	private void guardarAuditoria() {
        AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setTipo("SALIDA GRATUITA PARKING");
		auditoriasService.guardarAuditoria(auditoria);
	}

	public void introducirArticuloExtravio() {
		try {
			String formatoParking = variablesServices.getVariableAsString(ID_VARIABLE_FORMATO_CODIGO);
			
			if("03".equals(formatoParking)) {
				getDatos().put(PARAM_GENERAR_QR, true);
			}
			else {
				boolean articuloYaIntroducido = existeArticuloParkingEnTicket();
				if(articuloYaIntroducido) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Ya se ha introducido un artículo de parking en el ticket."), getStage());
					return;
				}
		    	
		    	String[] resultado = generarCodigoBarrasSalida(new Date());
				
		        getDatos().put(PARAM_CODIGO_SALIDA, resultado[0]);
	            getDatos().put(PARAM_HORA_SALIDA, resultado[1]);
		        getDatos().put(PARAM_ARTICULO_PARKING, codartExtravio);
			    getDatos().put(PARAM_MINUTOS_PARKING, 1);		        
			}
			getStage().close();
		}
		catch(Exception e) {
			log.error("introducirArticuloExtravio() - Ha habido un error al generar el código de barras del parking: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido procesar correctamente el código de barras. Contacte con el administrador"), e);
		}
	}

	protected String[] generarCodigoBarrasSalida(Date horaEntrada) throws CheckDigitException {
    	String terminal = variablesServices.getVariableAsString(ID_VARIABLE_TERMINAL);
		String prefijo = variablesServices.getVariableAsString(ID_VARIABLE_PREFIJO);
		String formatoParking = variablesServices.getVariableAsString(ID_VARIABLE_FORMATO_CODIGO);
		
		String[] resultado = new String[2];
		resultado[0] = "";
		resultado[1] = "";
		
		if("01".equals(formatoParking)) {
		    Date horaSalida = new Date();
		    
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(horaSalida);
		    
		    Integer minutosCortesia = variablesServices.getVariableAsInteger(ID_VARIABLE_MINUTOS_CORTESIA);
		    Integer minutos = new Integer(calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + minutosCortesia * 60);
		    
		    SimpleDateFormat dateFormat = new SimpleDateFormat("D");
		    String dia = StringUtils.leftPad(dateFormat.format(horaSalida), 3, '0');
		    
		    String codigoBarrasSalida = prefijo + terminal + dia + minutos;
		    codigoBarrasSalida = codigoBarrasSalida + new EAN13CheckDigit().calculate(codigoBarrasSalida);
		    
		    Integer horaDia = new Integer(minutos / 3600); 
	        Integer minuto = new Integer((minutos - (horaDia * 3600)) / 60);
	        String horaSalidaStr = StringUtils.leftPad(horaDia.toString(), 2, '0') + ":" + StringUtils.leftPad(minuto.toString(), 2, '0');
	        
	        resultado[0] = codigoBarrasSalida;
	        resultado[1] = horaSalidaStr;
		}
		
	    return resultado;
    }

	protected Date leerCodigoBarras(String codigoParking) {
	    Date horaEntrada = null;
	    
	    try {	    	
	    	String fecha = StringUtils.substring(codigoParking, 4, 7);
	    	
	        String hora = StringUtils.substring(codigoParking, 7, 12);
	        Integer horaDia = new Integer(new Integer(hora) / 3600); 
	        Integer minuto = new Integer((new Integer(hora) - (horaDia * 3600)) / 60);
	        
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.DAY_OF_YEAR, new Integer(fecha));
	        calendar.set(Calendar.HOUR_OF_DAY, horaDia);
	        calendar.set(Calendar.MINUTE, minuto);
	        
	        horaEntrada = calendar.getTime();
	    }
	    catch (Exception e) {
	    	log.error("leerCodigoBarras() - Ha habido un error al parsear el código de barras: " + e.getMessage(), e);
	    	VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido procesar correctamente el código de barras. Contacte con el administrador"), e);
	    }
	    return horaEntrada;
    }

}
