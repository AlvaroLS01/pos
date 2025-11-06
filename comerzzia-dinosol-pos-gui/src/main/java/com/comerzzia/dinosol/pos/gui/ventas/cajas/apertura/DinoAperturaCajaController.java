package com.comerzzia.dinosol.pos.gui.ventas.cajas.apertura;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.cajas.DinoCajasService;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionAplicacion;
import com.comerzzia.model.ventas.cajas.CajaDTO;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.cajas.apertura.AperturaCajaController;
import com.comerzzia.pos.gui.ventas.cajas.apertura.AperturaCajaFormularioBean;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionCaja;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;

@Component
@Primary
public class DinoAperturaCajaController extends AperturaCajaController {
	
	private Logger log = Logger.getLogger(DinoAperturaCajaController.class);
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private SesionCaja sesionCaja;
	
	@Autowired
	private DinoCajasService dinoCajasService;
	
	@Autowired
	private DinoSesionAplicacion dinoSesionAplicacion;
	
	@Override
	public void initializeComponents() {
	    super.initializeComponents();
	    
	    tfSaldo.setDisable(true);
	    tfSaldo.getStyleClass().add("solo-lectura");
	    
	    btContarSaldo.setDisable(true);
	}
	
	@Override
	public void accionAceptar() {
		try {
            log.debug("accionAceptar()");
            //Validar formulario                  
            formularioAperturaGui = new AperturaCajaFormularioBean(tfFecha.getTexto(), tfSaldo.getText());
            if (!accionValidarForm()) {
                return;
            }
            
            Date fechaApertura = FormatUtil.getInstance().desformateaFechaHora(tfFecha.getTexto(), true);
            
            Calendar calendarHoy = Calendar.getInstance();
            calendarHoy.set(Calendar.HOUR_OF_DAY, 0);
            calendarHoy.set(Calendar.MINUTE, 0);
            calendarHoy.set(Calendar.SECOND, 0);
            calendarHoy.set(Calendar.MILLISECOND, 0);
            
            //Si la fecha de apertura no es hoy, la ponemos sin hora
            Calendar calendarAperturaSinHora = Calendar.getInstance();
            calendarAperturaSinHora.setTime(fechaApertura);
            calendarAperturaSinHora.set(Calendar.HOUR_OF_DAY, 0);
            calendarAperturaSinHora.set(Calendar.MINUTE, 0);
            calendarAperturaSinHora.set(Calendar.SECOND, 0);
            calendarAperturaSinHora.set(Calendar.MILLISECOND, 0);
            if(!calendarAperturaSinHora.equals(calendarHoy)){
            	fechaApertura = calendarAperturaSinHora.getTime();
            }
            
            if(dinoSesionAplicacion.isCajaMasterActivada()) {
	            boolean transferirACajaMaster = true;
	            
	            String cajaEnMaster = existeCajaEnMaster();
	            if(cajaEnMaster != null) {
	            	String mensaje = cajaEnMaster + I18N.getTexto(" Si abre la caja, deberá cerrar la caja en este terminal para poder salir de la aplicación.")
	            			+ System.lineSeparator() + System.lineSeparator() + I18N.getTexto("¿Desea continuar?");
					if(!VentanaDialogoComponent.crearVentanaConfirmacion(mensaje, getStage())) {
						return;
					}
					else {
						transferirACajaMaster = false;
					}
	            }
	            
	            sesionCaja.abrirCajaManual(fechaApertura, formularioAperturaGui.getSaldoAsBigDecimal());
	            
	            if(transferirACajaMaster) {
	            	transferirCajaAMaster();
	            }
            }
            else {
            	sesionCaja.abrirCajaManual(fechaApertura, formularioAperturaGui.getSaldoAsBigDecimal());
            }
            
            getStage().close();
        }
        catch (CajasServiceException | CajaEstadoException e) {
            log.error("accionAceptar() - Error al tratar de realizar apertura de caja: " + e.getCause(), e);
            VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessageI18N(), e);
        }
        catch (Exception e) {
            log.error("accionAceptar() - Error al tratar de realizar apertura de caja: " + e.getCause(), e);
            VentanaDialogoComponent.crearVentanaError(getStage(),  e);
        }
	}

	protected String existeCajaEnMaster() {
	    try {
	        CajaDTO cajaDto = dinoCajasService.pedirCajaEnMaster(sesion.getSesionUsuario().getUsuario());
	        if(cajaDto != null && cajaDto.getCabecera() != null) {
	        	return cajaDto.getCabecera().getCodcaja();
	        }
	        else {
	        	return null;
	        }
        }
        catch (RestException e) {
        	log.error("pedirCajaEnMaster() - Ha habido un error al transferir la caja a la caja máster: " + e.getCause().getMessage(), e);
        	
        	String mensaje = I18N.getTexto("No se ha podido establecer la comunicación con la caja máster. Tendrá que cerrar caja en este terminal para poder salir.");
        	VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(mensaje), e);
        	
        	return null;
        }
        catch (RestHttpException e) {
        	log.error("transferirCajaAMaster() - Ha habido un error al transferir la caja a la caja máster: " + e.getCause().getMessage(), e);
        	
        	return e.getMessage();
        }
    }

	private void transferirCajaAMaster() {
		if(dinoSesionAplicacion.isCajaMasterActivada()) {
			try {
		        dinoCajasService.transferirCaja(sesionCaja.getCajaAbierta());
	        }
	        catch (RestException e) {
	        	log.error("transferirCajaAMaster() - Ha habido un error al transferir la caja a la caja máster: " + e.getCause().getMessage(), e);
	        	
	        	String mensaje = I18N.getTexto("No se ha podido establecer la comunicación con la caja máster. Tendrá que cerrar caja en este terminal para poder salir.");
	        	VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(mensaje), e);
	        }
	        catch (RestHttpException e) {
	        	log.error("transferirCajaAMaster() - Ha habido un error al transferir la caja a la caja máster: " + e.getCause().getMessage(), e);
	        	
	        	String mensaje = I18N.getTexto("No se ha podido abrir la caja en la caja máster, tendrá que cerrar caja en este terminal para poder salir. Contacte con un administrador.");
	        	Short codigoCajaAbierta = 7001;
	        	if(codigoCajaAbierta.equals(e.getCodError())) {
	        		mensaje = e.getMessage();
	        	}
	        	
	        	VentanaDialogoComponent.crearVentanaError(getStage(), mensaje, e);
	        }
			catch (Exception e) {
	        	log.error("transferirCajaAMaster() - Ha habido un error al transferir la caja a la caja máster: " + e.getMessage(), e);
	        	
	        	String mensaje = "Ha habido un error inesperado al transferir la caja a la caja máster. Contacte con un administrador";
	        	VentanaDialogoComponent.crearVentanaError(getStage(), mensaje, e);
			}
		}
    }

}
