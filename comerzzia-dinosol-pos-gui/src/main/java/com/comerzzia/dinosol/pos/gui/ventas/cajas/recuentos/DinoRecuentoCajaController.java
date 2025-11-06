package com.comerzzia.dinosol.pos.gui.ventas.cajas.recuentos;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.tecladonumerico.TecladoNumericoNegativo;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.gui.ventas.cajas.recuentos.RecuentoCajaController;
import com.comerzzia.pos.gui.ventas.cajas.recuentos.RecuentoCajaGui;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;

@Component
@Primary
public class DinoRecuentoCajaController extends RecuentoCajaController{
	
    private static final Logger log = Logger.getLogger(DinoRecuentoCajaController.class.getName());

    @FXML
    protected TecladoNumericoNegativo tecladoNumericoNeg;
    
    @Autowired
    private Sesion sesion;
    
    @Autowired
    private AuditoriasService auditoriasService;
    
    @Override
    public void initializeForm() throws InitializeGuiException {
    	try {
            cajaSesion = sesion.getSesionCaja();
            cajaSesion.actualizarRecuentoCaja();
            refrescarDatosPantalla();
            
            panelMedioPago.focusedProperty().addListener(new ChangeListener<Boolean>() {
    			@Override
    			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
    				if (t.booleanValue() == false && t1.booleanValue() == true){
    					medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
    					lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
    					Platform.runLater(new Runnable() {
    						@Override
    						public void run() {
    							tfImporte.requestFocus();
    						}
    					});  
    				}
    			}
    		});
            
            // Establecemos el medio de pago por defecto
            medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
            lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
        }
        catch (CajasServiceException e) {
            log.error("initializeForm() - Error de caja: "+e.getMessageI18N());
            throw new InitializeGuiException(e.getMessageI18N(), e);
        }
        catch (Exception e) {
            log.error("initializeForm() - Error inesperado inicializando formulario. ", e);
            throw new InitializeGuiException(e);
        }
    	
    	AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setTipo("INICIO DE ARQUEO");
		auditoria.setCajeroOperacion(sesion.getSesionUsuario().getUsuario().getUsuario());
		auditoriasService.guardarAuditoria(auditoria);
    }
    
    @Override
    public void initTecladoNumerico(TecladoNumerico tecladoNumerico) {
    	this.tecladoNumericoNeg.init(getScene());
		setShowKeyboard(false);
    }

    @Override
    public void accionEventoEliminarTabla(String idItem) {
        log.debug("accionBorrarRegistroTabla() - Acción ejecutada");
        if (tbRecuento.getItems() != null && tbRecuento.getItems() != null && tbRecuento.getItems().size() > 0) {
        	if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Confirme operación."), I18N.getTexto("¿Desea eliminar el registro?"), getStage())){
        		int indSiguienteSeleccion = tbRecuento.getSelectionModel().getSelectedIndex();
                RecuentoCajaGui linea = tbRecuento.getSelectionModel().getSelectedItem();
                if (linea == null) {
                    // No hay linea seleccionada. mostrar error mensaje de advertencia de que debe seleccionar una línea
                    return;
                }
                cajaSesion.getCajaAbierta().removeLineaRecuento(linea.getLineaRecuento());
                refrescarDatosPantalla();
                
                tbRecuento.getSelectionModel().selectLast();// .select(0);
		    	int indUltimo = tbRecuento.getSelectionModel().getSelectedIndex();
		    	if( indSiguienteSeleccion > indUltimo)
		    	{
		    		indSiguienteSeleccion = indUltimo;	
		    	}		    
		    	
		    	tbRecuento.requestFocus();
		    	tbRecuento.getSelectionModel().select(indSiguienteSeleccion);
		    	tbRecuento.getFocusModel().focus(indSiguienteSeleccion);
		    	tbRecuento.scrollTo(indSiguienteSeleccion);
		    	tfImporte.requestFocus();
        	}else{
        		log.debug("Se canceló la operación");
        	}
        }
        else {
            VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Borrar pago"), I18N.getTexto("Sin pagos para eliminar. Prueba de un mensaje de información bastante largo."), getStage());
        }
    }
    
    @Override
    public void initializeComponents() {
        super.initializeComponents();
        
        tfCantidad.setEditable(false);
        tfCantidad.setFocusTraversable(false);
        tfCantidad.setDisable(true);
    }
    
	@Override
	public void cambiarCantidad() {
		log.trace("cambiarCantidad() - preparamos la interfaz para una modificación de la cantidad");
		
		tfImporte.setText(tfImporte.getText().replace("*", ""));
		
		BigDecimal cantidad = null;
		try {
			cantidad = FormatUtil.getInstance().desformateaBigDecimal(tfImporte.getText(), 0);
		}
		catch (Exception e) {
			log.trace("cambiarCantidad() - Fallo al desformatear la cantidad: " + e.getMessage(), e);
		}
		
		if(cantidad != null) {
			if (BigDecimalUtil.isIgualACero(FormatUtil.getInstance().desformateaBigDecimal(tfImporte.getText(), 4))) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Cantidad 0 no permitida"), getStage());
				tfImporte.setText("");
			}
			
			tfCantidad.setText(FormatUtil.getInstance().formateaNumero(cantidad, 0));
			
			tfImporte.selectAll();
			tfImporte.setText("");
		}
	}
	
	@Override
	public void accionCancelar() {
		super.accionCancelar();
    	
    	AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setTipo("FIN DE ARQUEO");
		auditoria.setCajeroOperacion(sesion.getSesionUsuario().getUsuario().getUsuario());
		auditoriasService.guardarAuditoria(auditoria);
	}
	
	@Override
	public void aceptar() {
		super.aceptar();
    	
    	AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setTipo("FIN DE ARQUEO");
		auditoria.setCajeroOperacion(sesion.getSesionUsuario().getUsuario().getUsuario());
		auditoriasService.guardarAuditoria(auditoria);
	}

}
