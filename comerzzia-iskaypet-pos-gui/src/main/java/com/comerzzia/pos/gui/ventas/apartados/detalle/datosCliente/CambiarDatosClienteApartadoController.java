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
package com.comerzzia.pos.gui.ventas.apartados.detalle.datosCliente;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.FXCollections;

import javax.validation.ConstraintViolation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.apartados.ApartadosManager;
import com.comerzzia.pos.gui.ventas.tickets.pagos.datosCliente.CambiarDatosClienteController;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentNotFoundException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentServiceException;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class CambiarDatosClienteApartadoController extends CambiarDatosClienteController{

	public static final String PARAMETRO_APARTADOS_MANAGER = "APARTADO_MANAGER";
	
	@Autowired
	private Sesion sesion;
	
	private ApartadosManager apartadosManager;
	
	@Autowired
	private TiposIdentService tiposIdentService;
	
	FormularioDatosClienteApartadoGui formClienteApartados;
	
	public void initializeComponents() throws InitializeGuiException{
		super.initializeComponents();
		
		formClienteApartados = SpringContext.getBean(FormularioDatosClienteApartadoGui.class);
		
		formClienteApartados.setFormField("desCliente", tfDesCliente);
		formClienteApartados.setFormField("domicilio", tfDomicilio);
		formClienteApartados.setFormField("poblacion", tfPoblacion);
		formClienteApartados.setFormField("provincia", tfProvincia);
		formClienteApartados.setFormField("localidad", tfLocalidad);
		formClienteApartados.setFormField("codigoPostal", tfCP);
		formClienteApartados.setFormField("cif", tfNumDocIdent);
		formClienteApartados.setFormField("telefono", tfTelefono);
		formClienteApartados.setFormField("codPais", tfCodPais);
		
		panelCodCliente.setManaged(true);
		panelCodCliente.setVisible(true);
		tfCodCliente.setEditable(false);
		panelRazonSocial.setManaged(false);
		panelRazonSocial.setVisible(false);
	}
	
	@Override
    public void initializeForm() throws InitializeGuiException {
		
		btBuscar.setVisible(false);
        
        apartadosManager = (ApartadosManager) getDatos().remove(PARAMETRO_APARTADOS_MANAGER);
        tiposIdent = FXCollections.observableArrayList();        
        setTitulo();
        
        //Limpiamos el posible error anterior
        formClienteApartados.clearErrorStyle();
        lbError.setText("");
        
        clienteTicket = apartadosManager.getCliente();
        
        try {
            List<TiposIdentBean> tiposIdentificacion = tiposIdentService.consultarTiposIdent(null, true, 
                    sesion.getAplicacion().getTienda().getCliente().getCodpais());
            
            //Añadimos elemento vacío
            tiposIdent.add(new TiposIdentBean());
                        
            for(TiposIdentBean tipoIdent: tiposIdentificacion){
                tiposIdent.add(tipoIdent);
            }
            cbTipoDocIdent.setItems(tiposIdent);
        }
        catch (TiposIdentNotFoundException ex) {
            log.error("No se encontró ningún tipo de identificación.");
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se encontraron documentos de identificación configurados para la tienda."), this.getStage());
        }
        catch (TiposIdentServiceException ex) {
            log.error("Error consultando los tipos de identificación.",ex);
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error consultando los documentos de identificación de la tienda."), this.getStage());
        }
        catch(Exception ex){
            log.error("Se produjo un error en el tratamiento de los tipos de identificacion",ex);
        }

        String codPais = clienteTicket.getCodpais();
        if(codPais!=null && !codPais.isEmpty()){
        	PaisBean paisCliente = obtenerPais(codPais);
        	if(paisCliente!=null){
        		clienteTicket.setPais(paisCliente.getDesPais());
        	}
        }
        else{
        	tfCodPais.setText(AppConfig.pais);
        	PaisBean pais = obtenerPais(AppConfig.pais);
        	tfDesPais.setText(pais.getDesPais());
        }
        
        refrescarDatosPantalla();
	}    

	public void refrescarDatosPantalla(){

		tfCodCliente.setText(clienteTicket.getCodCliente());
		tfDesCliente.setText(clienteTicket.getDesCliente());
		tfRazonSocial.setText(clienteTicket.getNombreComercial());
		tfProvincia.setText(clienteTicket.getProvincia());
		tfDomicilio.setText(clienteTicket.getDomicilio());
		tfLocalidad.setText(clienteTicket.getLocalidad());
		tfTelefono.setText(clienteTicket.getTelefono1());
		tfNumDocIdent.setText(clienteTicket.getCif());
		tfCodPais.setText(clienteTicket.getCodpais());
		tfDesPais.setText(clienteTicket.getPais());
		tfPoblacion.setText(clienteTicket.getPoblacion());
		tfCP.setText(clienteTicket.getCp());
		TiposIdentBean tipoIdentSeleccionado = null;
		for(TiposIdentBean tipoIdent : tiposIdent) {
			if(tipoIdent != null && tipoIdent.getCodTipoIden() != null && tipoIdent.getCodTipoIden().equals(clienteTicket.getTipoIdentificacion())) {
				tipoIdentSeleccionado = tipoIdent;
			}
		}
		cbTipoDocIdent.getSelectionModel().select(tipoIdentSeleccionado);

		if(clienteTicket.getTipoIdentificacion()!=null && !clienteTicket.getTipoIdentificacion().isEmpty()){
			tipoIdentSeleccionado = null;
    		for(TiposIdentBean tipoIdent : tiposIdent) {
    			if(tipoIdent != null && tipoIdent.getCodTipoIden() != null && tipoIdent.getCodTipoIden().equals(clienteTicket.getTipoIdentificacion())) {
    				tipoIdentSeleccionado = tipoIdent;
    			}
    		}
    		cbTipoDocIdent.getSelectionModel().select(tipoIdentSeleccionado);
		}
	}

	public void accionAceptar(){

		if(validarFormularioDatosFactura()){
			clienteTicket.setCodpais(tfCodPais.getText());
			clienteTicket.setCif(tfNumDocIdent.getText());
			clienteTicket.setCp(tfCP.getText());
			clienteTicket.setDomicilio(tfDomicilio.getText());
			clienteTicket.setPoblacion(tfPoblacion.getText());
			clienteTicket.setLocalidad(tfLocalidad.getText());
			clienteTicket.setTelefono1(tfTelefono.getText());
			clienteTicket.setPais(tfDesPais.getText());
			if(cbTipoDocIdent.getSelectionModel().getSelectedItem() != null){
				clienteTicket.setTipoIdentificacion(cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden());
			}
			clienteTicket.setNombreComercial(tfRazonSocial.getText());
			clienteTicket.setProvincia(tfProvincia.getText());
			clienteTicket.setDesCliente(tfDesCliente.getText());

			apartadosManager.guardarDatosCliente(clienteTicket);

			getStage().close();
		}
	}
	
	private boolean validarFormularioDatosFactura() {
    	log.debug("validarFormularioCambiarDatosClienteApartadoController()");

        boolean valido = true;

        // Limpiamos los errores que pudiese tener el formulario
        formClienteApartados.clearErrorStyle();
        
        formClienteApartados.setDomicilio(tfDomicilio.getText());
        formClienteApartados.setDesCliente(tfDesCliente.getText());
        formClienteApartados.setCodPais(tfCodPais.getText());
        formClienteApartados.setCif(tfNumDocIdent.getText());    
        formClienteApartados.setPoblacion(tfPoblacion.getText());
        formClienteApartados.setProvincia(tfProvincia.getText());
        formClienteApartados.setLocalidad(tfLocalidad.getText());
        formClienteApartados.setCodigoPostal(tfCP.getText());
        formClienteApartados.setTelefono(tfTelefono.getText());
        
        //Limpiamos el posible error anterior
        lbError.setText("");
        
        final Set<ConstraintViolation<FormularioDatosClienteApartadoGui>> constraintViolations = ValidationUI.getInstance().getValidator().validate(formClienteApartados);
        Iterator<ConstraintViolation<FormularioDatosClienteApartadoGui>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()){
            final ConstraintViolation<FormularioDatosClienteApartadoGui> next = iterator.next();
            formClienteApartados.setErrorStyle(next.getPropertyPath(), true);
            
            if(valido){ //Ponemos el foco en el primero que da error
            	lbError.setText(next.getMessage());
            	Platform.runLater(new Runnable() {
    				@Override
    				public void run() {
    					formClienteApartados.setFocus(next.getPropertyPath());
    				}
    			});
            }
            valido = false;
        }
        
        return valido;
    }

}
