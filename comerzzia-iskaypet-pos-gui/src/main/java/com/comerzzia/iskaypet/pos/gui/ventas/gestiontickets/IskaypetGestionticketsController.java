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
    
package com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.ventas.gestiontickets.GestionTicketGui;
import com.comerzzia.pos.gui.ventas.gestiontickets.GestionticketsController;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * GAP 117 RECUPERACIÓN DE CONTRATOS DESDE EL POS
 *
 */

@Primary
@Controller
public class IskaypetGestionticketsController extends GestionticketsController {

    public static final String PARAMETRO_ENTRADA_TIPO_DOC = "TIPO_DOC";
    
    private static final Logger log = Logger.getLogger(IskaypetGestionticketsController.class.getName());
    
    @Autowired
    private Sesion sesion;
    
	@Autowired
	protected VariablesServices variablesServices;
	public static final String X_POS_CONTRATO_VISIBLE = "X_POS.CONTRATO_VISIBLE"; 
	
    @Autowired
    private TicketsService ticketsService;
    

	@SuppressWarnings("rawtypes")
	@FXML
    protected TableColumn  tcContratos;

    @SuppressWarnings("unchecked")
	@Override
    public void initialize(URL url, ResourceBundle rb) {
    	super.initialize(url, rb);
    	if(checkColumnaContrato()) {
    		tcContratos.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcContratos", null, CellFactoryBuilder.ESTILO_ALINEACION_CEN));
    		tcContratos.setCellValueFactory(new PropertyValueFactory<IskaypetGestionTicketGui, String>("contrato"));
    	}
    }
    
    //Configuramos por actividades y si no están los contratos diponibles no mostrará la columna que lo indica
	public boolean checkColumnaContrato() {
		//Si la variable no existe por defecto se activan
		String valor = variablesServices.getVariableAsString(X_POS_CONTRATO_VISIBLE);
		boolean activo = true;
		if (StringUtils.isNotBlank(valor)) {
			activo = variablesServices.getVariableAsBoolean(X_POS_CONTRATO_VISIBLE);
		}
		return activo;
	}
	
	@Override
	public void accionBuscar() {
        log.trace("accionBuscar()");
        
        tickets.clear();
        tbTickets.setPlaceholder(new Label(""));
        
        if(StringUtils.isNotBlank(tfLocalizador.getText())){
        	log.debug("accionIntroTfLocalizador()");
        	
    		try {
    			String localizador = tfLocalizador.getText().trim();
    			log.debug("accionIntroTfLocalizador() - Realizando búsqueda con localizador = "+localizador);

    			List<TicketBean> ticketsBean = ticketsService.consultarTicketLocalizador(localizador, idTiposDocValidos);
    			Collections.sort(ticketsBean);

    			if(ticketsBean.isEmpty()){
    				tbTickets.setPlaceholder(lbSinResultados);
    				tfLocalizador.requestFocus();
		    		tfLocalizador.selectAll();
    			}
    			else{
    				for (TicketBean ticketBean : ticketsBean) {
    					GestionTicketGui ticketGui = new GestionTicketGui(ticketBean);
    					tickets.add(ticketGui);
    				}
    				tfLocalizador.setText("");	
                	tbTickets.requestFocus();
                	tbTickets.getSelectionModel().select(0);
                	tbTickets.getFocusModel().focus(0);
    			}
    			    			
    		} catch (TicketsServiceException e1) {
    			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pudo obtener el ticket con el localizador introducido."), this.getStage());
    			log.error("Error en la búsqueda del ticket con el localizador.", e1);
    			tfLocalizador.requestFocus();
	    		tfLocalizador.selectAll();
    		}
    		catch (Exception e1) {
    			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Se produjo un error procesando el localizador. " + e1.getMessage()), this.getStage());
    			log.error("Error procesando el localizador ", e1);
    			tfLocalizador.requestFocus();
	    		tfLocalizador.selectAll();
    		}
    		
        }else{
	        frGestionTicket.setCodCaja(tfCaja.getText());
	        frGestionTicket.setCodTicket(tfTicket.getText().equals("") ? null : tfTicket.getText());
	        frGestionTicket.setFecha(tfFecha.getTexto());
	        frGestionTicket.setIdDoc(tfCodDoc.getText().equals("") ? null : tfCodDoc.getText());
	        
	        tfTicket.deselect();
	        if(validarDatosFormulario()){
	            new BuscarTask().start();
	        }
        }
    }
	
	 protected class BuscarTask extends BackgroundTask<List<TicketBean>>{

	        @Override
	        protected List<TicketBean> call() throws Exception {
	        	
	        	String codDoc = frGestionTicket.getIdDoc();
	        	Long idDoc = null;
	        	
	        	if(codDoc != null){
	        		idDoc = sesion.getAplicacion().getDocumentos().getDocumento(codDoc).getIdTipoDocumento();
	        	}
	        	
	        	return ticketsService.consultarTickets(frGestionTicket.getCodCaja(), frGestionTicket.getIdTicketAsLong(), frGestionTicket.getFechaAsDate(), idDoc, idTiposDocValidos);
	        }

	        @Override
	        protected void succeeded() {
	            //Ordenamos la lista de tickets obtenida por la fecha de los mismos
	            List<TicketBean> ticketsBean = getValue();
	            Collections.sort(ticketsBean);
	          
	            if(ticketsBean.isEmpty()){
	            	tbTickets.setPlaceholder(lbSinResultados);	
	            	tfTicket.requestFocus();
	            	tfTicket.selectAll();
	            }
	            else{
	            	for (TicketBean ticketBean : ticketsBean) {
	            		IskaypetGestionTicketGui ticketGui = new IskaypetGestionTicketGui(ticketBean);
	            		tickets.add(ticketGui);
	            	}
	            	//seleccionamos el primer registro del resultado
	            	tbTickets.requestFocus();
	            	tbTickets.getSelectionModel().select(0);
	            	tbTickets.getFocusModel().focus(0);
	            }

	            super.succeeded();
	        }

	        @Override
	        protected void failed() {
	            VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessageI18N(), getCMZException());
	            tfTicket.requestFocus();
	        	tfTicket.selectAll();
	            super.failed();
	        }
	    }
	
	
	
	



    
    
    
    
}
