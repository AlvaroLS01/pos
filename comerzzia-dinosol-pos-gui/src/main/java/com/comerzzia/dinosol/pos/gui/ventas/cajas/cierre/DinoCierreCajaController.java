package com.comerzzia.dinosol.pos.gui.ventas.cajas.cierre;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.cajas.DinoCajasService;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionAplicacion;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.exception.ValidationException;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.gui.inicio.InicioView;
import com.comerzzia.pos.gui.ventas.cajas.CajasView;
import com.comerzzia.pos.gui.ventas.cajas.cierre.CierreCajaController;
import com.comerzzia.pos.gui.ventas.cajas.cierre.CierreCajaView;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;

@Component
@Primary
public class DinoCierreCajaController extends CierreCajaController {
	
	private Logger log = Logger.getLogger(DinoCierreCajaController.class);
	
	public static final String PERMISO_CIERRE_DESCUADRE = "CERRAR CON DESCUADRE";
	
	@Autowired
	private DinoSesionAplicacion dinoSesionAplicacion;
	
	@Autowired
	private DinoCajasService dinoCajasService;
	
	@Autowired
	private Sesion sesion;
	
	@SuppressWarnings("unchecked")
    @Override
	protected void accionCierreCaja() {
		log.debug("accionCierreCaja()");
		try {
			if (getApplication().getMainView().getSubViews().size() > 2) {
				if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Para cerrar la caja se deben cerrar todas las pantallas abiertas. ¿Desea continuar?"), getStage())) {
					return;
				}
				boolean couldClose = getApplication().getMainView().closeAllViewsExcept(InicioView.class, CajasView.class, CierreCajaView.class);
				if (!couldClose) {
					return;
				}
			}

			boolean tieneDescuadres = cajaSesion.tieneDescuadres();
			
			if (tieneDescuadres) {
				try {
					super.compruebaPermisos(PERMISO_CIERRE_DESCUADRE);
					
					if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se va a cerrar la caja con descuadres mayores al valor permitido, ¿Desea continuar?"), getStage())) {
						if(!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Si cierra la caja, no se podrá modificar posteriormente. ¿Está seguro de que desea hacer el cierre?"), getStage())) {
							return;
						}
					}
					else {
						return;
					}
				}
				catch (SinPermisosException e) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Caja descuadrada con un importe mayor que el permitido. Revise recuento."), getStage());
					return;
				}
			}
			else if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Seguro de realizar el cierre?"), getStage())) {
				return;
			}

			// Procedemos al cierre de la caja
			// Actualizamos el formulario
			formularioCierreCaja.setFechaCierre(tfFechaCierre.getTexto());
			// validamos el formulario
			accionValidarForm();

			transferirCajaAMaster(formularioCierreCaja.getDateCierre());
			
			cajaSesion.guardarCierreCaja(formularioCierreCaja.getDateCierre());

			try {
				imprimirCierre(cajaSesion.getCajaAbierta());
			}
			catch (CajasServiceException ex) {
				log.error("accionCierreCaja() - No se pudo realizar la impresión del cierre de caja. ");
				VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageI18N(), ex);
			}
			cajaSesion.cerrarCaja();
			reintentosCierre = 0;
			if (tieneDescuadres) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Caja cerrada con descuadres"), getStage());
			}
			getStage().close();
		}
		catch (ValidationException ex) {
			log.debug("accionCierreCaja() - La validación no fué exitosa");
		}
		catch (CajasServiceException e) {
			log.error("accionCierreCaja() - Error al tratar de realizar cierre de caja: " + e.getCause(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessageI18N(), e);
		}
	}

	private void transferirCajaAMaster(Date date) {
		if(dinoSesionAplicacion.isCajaMasterActivada()) {
			try {
		        Caja cajaAbierta = sesion.getSesionCaja().getCajaAbierta();
		        cajaAbierta.setFechaCierre(new Date());
		        
				dinoCajasService.transferirCaja(cajaAbierta);
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
	        		mensaje = I18N.getTexto("La caja ya está abierta en otro terminal. Tendrá que cerrar caja en este terminal para poder salir.");
	        		return;
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
