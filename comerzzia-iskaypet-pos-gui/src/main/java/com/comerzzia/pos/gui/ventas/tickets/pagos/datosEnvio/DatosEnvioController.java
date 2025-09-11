/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */
package com.comerzzia.pos.gui.ventas.tickets.pagos.datosEnvio;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.FXCollections;

import javax.validation.ConstraintViolation;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.tickets.pagos.datosCliente.CambiarDatosClienteController;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class DatosEnvioController extends CambiarDatosClienteController {

	@Autowired
	private Sesion sesion;

	protected FormularioDatosEnvio formularioDatosEnvio;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);

		formularioDatosEnvio = SpringContext.getBean(FormularioDatosEnvio.class);
		formularioDatosEnvio.setFormField("nombre", tfRazonSocial);
		formularioDatosEnvio.setFormField("domicilio", tfDomicilio);
		formularioDatosEnvio.setFormField("poblacion", tfPoblacion);
		formularioDatosEnvio.setFormField("provincia", tfProvincia);
		formularioDatosEnvio.setFormField("localidad", tfLocalidad);
		formularioDatosEnvio.setFormField("codigoPostal", tfCP);
		formularioDatosEnvio.setFormField("numDocIdent", tfNumDocIdent); 
		formularioDatosEnvio.setFormField("telefono", tfTelefono);
	}
	
	@Override
	public void initializeForm() throws InitializeGuiException {
	    super.initializeForm();
	    formularioDatosEnvio.clearErrorStyle();
	    lbError.setText("");
//	    isClienteGenerico = clienteTicket.getCodCliente().equals(sesion.getAplicacion().getTienda().getCliente().getCodCliente()) && clienteTicket.getDesCliente().equals(sesion.getAplicacion().getTienda().getCliente().getDesCliente());
	}

	public void refrescarDatosPantalla() {
		log.debug("refrescarDatosPantalla()");
		
		tfCP.setText(clienteTicket.getCp());
		cbTipoDocIdent.getSelectionModel().select(0);
		tfProvincia.setText(clienteTicket.getProvincia());
    	tfCodPais.setText(clienteTicket.getCodpais());
    	codPais = clienteTicket.getCodpais() != null ? clienteTicket.getCodpais().toUpperCase() : "";
    	tfDesPais.setText(clienteTicket.getPais());

		if (!isClienteGenerico) {
			tfRazonSocial.setText(clienteTicket.getDesCliente());//(clienteTicket.getNombreComercial());
			tfProvincia.setText(clienteTicket.getProvincia());
			tfDomicilio.setText(clienteTicket.getDomicilio());
			tfLocalidad.setText(clienteTicket.getLocalidad());
			tfTelefono.setText(clienteTicket.getTelefono1());
			tfNumDocIdent.setText(clienteTicket.getCif());
			tfCodPais.setText(clienteTicket.getCodpais());
			codPais = clienteTicket.getCodpais() != null ? clienteTicket.getCodpais().toUpperCase() : "";
			tfDesPais.setText(clienteTicket.getPais());
			tfPoblacion.setText(clienteTicket.getPoblacion());
			tfCP.setText(clienteTicket.getCp());
			tiposIdent = FXCollections.observableArrayList();
	        loadTiposIdentificacion();
	        cbTipoDocIdent.setItems(tiposIdent);
			TiposIdentBean tipoIdentSeleccionado = null;
			for (TiposIdentBean tipoIdent : tiposIdent) {
				if (tipoIdent != null && tipoIdent.getCodTipoIden() != null && tipoIdent.getCodTipoIden().equals(clienteTicket.getTipoIdentificacion())) {
					tipoIdentSeleccionado = tipoIdent;
				}
			}
			if(tipoIdentSeleccionado != null) {
				cbTipoDocIdent.getSelectionModel().select(tipoIdentSeleccionado);
			}
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
		
		FidelizacionBean fidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		if(fidelizado != null && isClienteGenerico){
			tfRazonSocial.setText(fidelizado.getNombre()+" "+fidelizado.getApellido());
			tfRazonSocial.setEditable(false);
			tfProvincia.setText(fidelizado.getProvincia());
			tfDomicilio.setText(fidelizado.getDomicilio());
			tfLocalidad.setText(fidelizado.getLocalidad());
			tfCodPais.setText(fidelizado.getCodPais());
			codPais = fidelizado.getCodPais() != null ? fidelizado.getCodPais().toUpperCase() : "";
			tfDesPais.setText(fidelizado.getDesPais());
			tfPoblacion.setText(fidelizado.getPoblacion());
			tfCP.setText(fidelizado.getCp());
			if(!clienteTicket.getCodpais().equals(fidelizado.getCodPais())){
				tiposIdent = FXCollections.observableArrayList();
		        loadTiposIdentificacion();
		        cbTipoDocIdent.setItems(tiposIdent);
			}
			TiposIdentBean tipoIdentSeleccionado = null;
			for (TiposIdentBean tipoIdent : tiposIdent) {
				if (tipoIdent != null && tipoIdent.getCodTipoIden() != null && tipoIdent.getCodTipoIden().equals(fidelizado.getCodTipoIden())) {
					tipoIdentSeleccionado = tipoIdent;
				}
			}
			if(tipoIdentSeleccionado != null) {
				cbTipoDocIdent.getSelectionModel().select(tipoIdentSeleccionado);
			}
			if(fidelizado.getCodTipoIden()!=null && !fidelizado.getCodTipoIden().isEmpty()){
				tipoIdentSeleccionado = null;
	    		for(TiposIdentBean tipoIdent : tiposIdent) {
	    			if(tipoIdent != null && tipoIdent.getCodTipoIden() != null && tipoIdent.getCodTipoIden().equals(fidelizado.getCodTipoIden())) {
	    				tipoIdentSeleccionado = tipoIdent;
	    			}
	    		}
	    		cbTipoDocIdent.getSelectionModel().select(tipoIdentSeleccionado);
	        }
			tfNumDocIdent.setText(fidelizado.getDocumento());
		}else{
			tfRazonSocial.setEditable(true);
		}
	}

	protected void getCliente() {
		log.debug("getCliente()");

		clienteTicket = ticketManager.getTicket().getCabecera().getDatosEnvio();
		if (clienteTicket == null) {
			limpiarCampos();

			clienteTicket = new ClienteBean();
			BeanUtils.copyProperties(ticketManager.getTicket().getCabecera().getCliente(), clienteTicket);
		}
	}

	@Override
	public void accionAceptar() {
		log.debug("guardarDatosCliente()");
		if (validarFormulario()) {
			ClienteBean cliente = new ClienteBean();
			cliente.setCodpais(tfCodPais.getText());
			cliente.setCif(tfNumDocIdent.getText());
			cliente.setCp(tfCP.getText());
			cliente.setDomicilio(tfDomicilio.getText());
			cliente.setPoblacion(tfPoblacion.getText());
			cliente.setLocalidad(tfLocalidad.getText());
	        cliente.setTelefono1(tfTelefono.getText());
	        cliente.setPais(tfDesPais.getText());
	        if(cbTipoDocIdent.getSelectionModel().getSelectedItem() != null){
	        	cliente.setTipoIdentificacion(cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden());
	        }
	        cliente.setNombreComercial(tfRazonSocial.getText());
	        cliente.setDesCliente(tfRazonSocial.getText());
	        cliente.setProvincia(tfProvincia.getText());
			ticketManager.getTicket().getCabecera().setDatosEnvio(cliente);
	        
	        getStage().close();
		}
	}

	@Override
	protected boolean validarFormulario() {
		log.debug("validarFormularioDatosFactura()");

		formularioDatosEnvio.setNombre(formulario.trimTextField(tfRazonSocial));
		formularioDatosEnvio.setDomicilio(formulario.trimTextField(tfDomicilio));
		formularioDatosEnvio.setPoblacion(formulario.trimTextField(tfPoblacion));
		formularioDatosEnvio.setProvincia(formulario.trimTextField(tfProvincia));
		formularioDatosEnvio.setLocalidad(formulario.trimTextField(tfLocalidad));
		formularioDatosEnvio.setCodigoPostal(formulario.trimTextField(tfCP));
		formularioDatosEnvio.setTelefono(formulario.trimTextField(tfTelefono));
		formularioDatosEnvio.setnumDocIdent(formulario.trimTextField(tfNumDocIdent)); 

		boolean valido = true;

		// Limpiamos los errores que pudiese tener el formulario
		formularioDatosEnvio.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbError.setText("");
		
		//Limpiamos el posible error anterior       
        final Set<ConstraintViolation<FormularioDatosEnvio>> constraintViolations = ValidationUI.getInstance().getValidator().validate(formularioDatosEnvio);
        Iterator<ConstraintViolation<FormularioDatosEnvio>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()){
            final ConstraintViolation<FormularioDatosEnvio> next = iterator.next();
            formularioDatosEnvio.setErrorStyle(next.getPropertyPath(), true);
            
            if(valido){ //Ponemos el foco en el primero que da error
            	lbError.setText(next.getMessage());
            	Platform.runLater(new Runnable() {
    				@Override
    				public void run() {
    					formularioDatosEnvio.setFocus(next.getPropertyPath());
    				}
    			});
            }
            valido = false;
        }

		return valido;
	}

	protected void setTitulo() {
		log.debug("setTitulo()");
		lbTitulo.setText(I18N.getTexto("Datos del Envío"));
	}
}
