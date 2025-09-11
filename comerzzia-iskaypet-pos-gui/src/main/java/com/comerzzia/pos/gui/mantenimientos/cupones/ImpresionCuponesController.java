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


package com.comerzzia.pos.gui.mantenimientos.cupones;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.cupones.CuponesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionGeneracionCuponesBean;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class ImpresionCuponesController extends Controller {

	private static Logger log = Logger.getLogger(ImpresionCuponesController.class);
	
    @FXML
    private Button btImprimir;
    
    @FXML
    private Label lbError;
    
    @FXML
    private ComboBox<PromocionGeneracionCuponesBean> cbPromociones;
            
    @FXML
    private TextField tfNumCupones;
    
    @Autowired
    private CuponesServices cuponesService;
    
    @Autowired
    private Sesion sesion;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @Override
    public void initializeComponents() {
    }

	@Override
    public void initializeForm() throws InitializeGuiException {
        try {
            // Actualizamos promociones activas
            sesion.getSesionPromociones().actualizarPromocionesActivas();
            
            List<PromocionGeneracionCuponesBean> promosCupon = new ArrayList<PromocionGeneracionCuponesBean>();
            Map<Long, PromocionGeneracionCuponesBean> promocionesActivasEmisionCuponManual = sesion.getSesionPromociones().getPromocionesActivasEmisionCuponManual();
            for(PromocionGeneracionCuponesBean promocion : promocionesActivasEmisionCuponManual.values()) {
            	if(promocion.isActiva()) {
            		promosCupon.add(promocion);
            	}
            }
            
            ObservableList<PromocionGeneracionCuponesBean> listaPromos = FXCollections.observableList(promosCupon);
			cbPromociones.setItems(listaPromos);
        }
        catch (PromocionesServiceException ex) {
        	VentanaDialogoComponent.crearVentanaError(getStage(), ex);
        	log.error("initializeForm() - Error al consultar promociones: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void initializeFocus() {
        btImprimir.requestFocus();
    }

    @FXML
    public void imprimirCuponesIntro(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            imprimirCupones();
        }
    }
    
    @FXML
    public void imprimirCupones(){
    	try {
            
            PromocionGeneracionCuponesBean promo = (PromocionGeneracionCuponesBean)cbPromociones.getSelectionModel().getSelectedItem();
            Long cantidad= FormatUtil.getInstance().desformateaLong(tfNumCupones.getText());
            if(promo == null){
                //No hay cupones para imprimir
                lbError.setText(I18N.getTexto("Debe escoger una promoción generadora de cupones"));
            }
            else if(cantidad ==null){
                lbError.setText(I18N.getTexto("Debe introducir una cantidad válida"));
            }
            else if(promo.isActiva()){
                lbError.setText("");
                List<CuponEmitidoTicket> cupones = cuponesService.getCuponesImpresionManual(promo.getIdPromocion(), cantidad.intValue());
                if(cupones.isEmpty()){
                    lbError.setText(I18N.getTexto("No existen cupones para impresión manual"));
                }
                else{
                	Map<String,Object> mapaParametrosCupon = new HashMap<String,Object>();
                    for(CuponEmitidoTicket cuponEmitido: cupones){
                    	mapaParametrosCupon.put("cupon", cuponEmitido);                    	
                    	SimpleDateFormat df = new SimpleDateFormat();
                    	mapaParametrosCupon.put("fechaEmision", df.format(new Date()));
                        try {
							ServicioImpresion.imprimir(PagosController.PLANTILLA_CUPON, mapaParametrosCupon);
						} catch (DeviceException e) {
							log.error("imprimirCupones() - Error al imprimir", e);
						}
                    }
                }
            }
            else{
                lbError.setText(I18N.getTexto("La promoción escogida no es válida"));
            }
        }
        catch (CuponesServiceException ex) {
        	log.error("imprimirCupones() - Error al imprimir", ex);
            lbError.setText(I18N.getTexto("Error obteniendo los cupones a imprimir"));
        }
        catch (PromocionesServiceException ex) {
        	log.error("imprimirCupones() - Error al imprimir", ex);
            lbError.setText(I18N.getTexto("Error obteniendo los cupones a imprimir"));
        }
        
    }
    
}
