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

package com.comerzzia.pos.gui.configuracion.tpv;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.TipoDispositivo;
import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionModelo;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoBuilder;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.IDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.escaner.EscanerNoConfig;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.persistence.core.tiendas.cajas.TiendaCajaBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiendas.cajas.TiendaCajaService;
import com.comerzzia.pos.services.core.tiendas.cajas.TiendaCajaServiceException;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

@Component
public class ConfiguracionTPVController extends Controller implements Initializable {
    
    // log
    private static final Logger log = Logger.getLogger(ConfiguracionTPVController.class.getName());
    protected String variableNoUsa;
    
    protected static final String OPCION_CAJON = "CAJON";
    protected static final String OPCION_VISOR = "VISOR";
    protected static final String OPCION_IMPRESORA1 = "IMPRESORA1";
    protected static final String OPCION_IMPRESORA2 = "IMPRESORA2";
    protected static final String OPCION_TARJETA = "TARJETA";
    protected static final String OPCION_GENERAL = "GENERAL";
    protected static final String OPCION_FIDELIZACION = "FODELIZACION";
    protected static final String OPCION_BALANZA = "BALANZA";
    protected static final String OPCION_RECARGA_MOVIL = "RECARGA_MOVIL";
    protected static final String OPCION_ESCANER = "ESCANER";
    
    // Componentes de ventana
    @FXML
    protected ComboBox<String> cbMetConex, cbDesConfig;
    
    @FXML
    protected TextArea taConfig;
    
    @FXML
    protected Label lbDesConfig, lbMetodoConex;
    
    protected AnchorPane panelDetalle, panelDetalleBoton;
    
    @FXML
    protected VBox boxBotones;
    
    @FXML
    protected TextField tfTienda, tfCaja;
    
    @FXML
    protected Button btFidelizacion, btBalanza, btVisor, btGenerales,
            btImpresora, btImpresora2,  btCajon, btRecargaMovil, btAceptar, btCancelar, btEditarConfiguracion;
    
    protected String opcionSeleccionada;
    
    protected HashMap<String,HashMap<String,HashMap<String,ConfiguracionModelo>>> configuracionesDispositivos;
    
    protected ConfiguracionDispositivo configTarjetaProvisional;
    protected ConfiguracionDispositivo configImpresoraProvisional;
    protected ConfiguracionDispositivo configImpresoraProvisional2;
    protected ConfiguracionDispositivo configVisorProvisional;
    protected ConfiguracionDispositivo configFidelizProvisional;
    protected ConfiguracionDispositivo configGeneralProvisional;
    protected ConfiguracionDispositivo configCajonProvisional;
    protected ConfiguracionDispositivo configBalanzaProvisional;
    protected ConfiguracionDispositivo configRecargaMovilProvisional;
    protected ConfiguracionDispositivo configEscanerProvisional;
    
    @Autowired
    private Sesion sesion;
    
    @Autowired
    private TiendaCajaService tiendaCajaService;
    
    protected ChangeListener<String> cbDesConfigListener = new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> paramObservableValue, String oldValue, String newValue) {
			mostrarOpcionesCBoxMetConexiones();
			cbMetConex.getSelectionModel().selectFirst();
		}
	};
    protected ChangeListener<String> cbMetConexListener =new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> paramObservableValue, String oldValue, String newValue) {
			mostrarConfigMetConex();
		}
	};
	
    final IVisor visor = Dispositivos.getInstance().getVisor();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    @Override
    public void initializeForm() {
        visor.limpiar();
    	
        lbMetodoConex.setText(I18N.getTexto("Caja configurada"));
        lbDesConfig.setText(I18N.getTexto("Tienda"));
        variableNoUsa = I18N.getTexto("NO USA");
        
        //Muestra la configuración general por defecto
        tfTienda.setText(sesion.getAplicacion().getTienda().getDesAlmacen());
        tfCaja.setText(sesion.getAplicacion().getCodCaja());
        mostrarComboBox(false);
        taConfig.setVisible(false);
        // Deshabiliramos el foco para que no se quede cogido
        taConfig.setFocusTraversable(false);
        
        opcionSeleccionada = OPCION_GENERAL;
        
        btEditarConfiguracion.setVisible(false);
        
        ocultarBotones();
        
    }
    
    protected void ocultarBotones(){
    	ListIterator<Node> iterator = boxBotones.getChildren().listIterator();
    	while(iterator.hasNext()){
    		Button boton = (Button) iterator.next();
    		if(AppConfig.ocultarBotones.contains(boton.getId())){
    			iterator.remove();
    		}
    	}
    }
    
    @Override
    public void initializeFocus() {
        btGenerales.requestFocus();
    }
    
    @Override
    public void initializeComponents() {
        log.debug("inicializarComponentes() - Inicializando componentes");
        
        cargarDatos();
        
        cbDesConfig.setItems(FXCollections.<String>observableArrayList());
        cbDesConfig.getSelectionModel().selectedItemProperty().addListener(cbDesConfigListener);   
        cbMetConex.getSelectionModel().selectedItemProperty().addListener(cbMetConexListener);
        configBalanzaProvisional = new ConfiguracionDispositivo(TipoDispositivo.BALANZA);
        configCajonProvisional = new ConfiguracionDispositivo(TipoDispositivo.CAJON);
        configFidelizProvisional = new ConfiguracionDispositivo(TipoDispositivo.FIDELIZACION);
        configGeneralProvisional = new ConfiguracionDispositivo();
        configVisorProvisional = new ConfiguracionDispositivo(TipoDispositivo.VISOR);
        configImpresoraProvisional = new ConfiguracionDispositivo(TipoDispositivo.IMPRESORA1);
        configImpresoraProvisional2 = new ConfiguracionDispositivo(TipoDispositivo.IMPRESORA2);
        configTarjetaProvisional = new ConfiguracionDispositivo(TipoDispositivo.TARJETA);
        configRecargaMovilProvisional = new ConfiguracionDispositivo(TipoDispositivo.RECARGA_MOVIL);
        configEscanerProvisional = new ConfiguracionDispositivo(TipoDispositivo.ESCANER);
    }
    
    protected void cargarDatos(){
        
        HashMap<String,HashMap<String,ConfiguracionModelo>> configCajones = Dispositivos.getInstance().getDispositivosDisponibles().getCajones();
        HashMap<String,HashMap<String,ConfiguracionModelo>> configImpresoras = Dispositivos.getInstance().getDispositivosDisponibles().getImpresoras();
        HashMap<String,HashMap<String,ConfiguracionModelo>> configVisores = Dispositivos.getInstance().getDispositivosDisponibles().getVisores();
        HashMap<String,HashMap<String,ConfiguracionModelo>> configTarjetas = Dispositivos.getInstance().getDispositivosDisponibles().getTarjetas();
        HashMap<String,HashMap<String,ConfiguracionModelo>> configFidelizacion = Dispositivos.getInstance().getDispositivosDisponibles().getConfigFidelizacion();
        HashMap<String,HashMap<String,ConfiguracionModelo>> configBalanzas = Dispositivos.getInstance().getDispositivosDisponibles().getBalanzas();
        HashMap<String,HashMap<String,ConfiguracionModelo>> configRecargasMoviles = Dispositivos.getInstance().getDispositivosDisponibles().getConfigRecargaMovil();
        HashMap<String,HashMap<String,ConfiguracionModelo>> configEscaneres = Dispositivos.getInstance().getDispositivosDisponibles().getEscaneres();
        
        configuracionesDispositivos = new HashMap<>();
        configuracionesDispositivos.put(OPCION_CAJON, configCajones);
        configuracionesDispositivos.put(OPCION_IMPRESORA1, configImpresoras);
        configuracionesDispositivos.put(OPCION_IMPRESORA2, configImpresoras);
        configuracionesDispositivos.put(OPCION_VISOR, configVisores);
        configuracionesDispositivos.put(OPCION_TARJETA, configTarjetas);
        configuracionesDispositivos.put(OPCION_BALANZA, configBalanzas);
        configuracionesDispositivos.put(OPCION_FIDELIZACION, configFidelizacion);
        configuracionesDispositivos.put(OPCION_RECARGA_MOVIL, configRecargasMoviles);
        configuracionesDispositivos.put(OPCION_ESCANER, configEscaneres);
        
    }
    
    @FXML
    public void accionBtGenerales() {
        log.debug("accionBtGenerales()");
        
        btEditarConfiguracion.setVisible(false);
        
        lbMetodoConex.setText(I18N.getTexto("Caja configurada"));
        lbDesConfig.setText(I18N.getTexto("Tienda"));
        taConfig.setVisible(false);
        mostrarComboBox(false);
        
        opcionSeleccionada = OPCION_GENERAL;
        tfTienda.setText(sesion.getAplicacion().getTienda().getDesAlmacen());
        tfCaja.setText(sesion.getAplicacion().getCodCaja());
        taConfig.setText("");
    }
    
    @FXML
    public void accionBtImpresora() {
        log.debug("accionBtImpresora()");
        
        btEditarConfiguracion.setVisible(false);
        
        lbMetodoConex.setText(I18N.getTexto("Método de conexión"));
        lbDesConfig.setText(I18N.getTexto("Tipo de impresora"));
        taConfig.setVisible(true);
        mostrarComboBox(true);
        
        opcionSeleccionada = OPCION_IMPRESORA1;
        limpiarComboBox();
        
        ConfiguracionDispositivo impresora;
        
        if(configImpresoraProvisional.getModelo().isEmpty()){
            impresora = Dispositivos.getInstance().getImpresora1().getConfiguracion();
        }
        else{
            impresora = configImpresoraProvisional;
        }
        if(impresora!=null&&!impresora.getModelo().equals(variableNoUsa)){
            establecerConfigDispositivoInicial(impresora);
        }
        else{
            cbDesConfig.getSelectionModel().select(variableNoUsa);
            taConfig.setText("");
        }
        
        cbDesConfig.requestFocus();
    }
    
    
    @FXML
    public void accionBtImpresora2() {
        log.debug("accionBtImpresora2()");
        
        btEditarConfiguracion.setVisible(false);
        
        lbMetodoConex.setText(I18N.getTexto("Método de conexión"));
        lbDesConfig.setText(I18N.getTexto("Tipo de impresora"));
        taConfig.setVisible(true);
        mostrarComboBox(true);
        
        opcionSeleccionada = OPCION_IMPRESORA2;
        limpiarComboBox();
        
        ConfiguracionDispositivo impresora2;
        
        if(configImpresoraProvisional2.getModelo().isEmpty()){
            impresora2 = Dispositivos.getInstance().getImpresora2().getConfiguracion();
        }
        else{
            impresora2 = configImpresoraProvisional2;
        }
        if(impresora2!=null&&!impresora2.getModelo().equals(variableNoUsa)){
            establecerConfigDispositivoInicial(impresora2);
        }
        else{
            cbDesConfig.getSelectionModel().select(variableNoUsa);
            taConfig.setText("");
        }
        
        cbDesConfig.requestFocus();
    }
    
    @FXML
    public void accionBtFidelizacion() {
        log.debug("accionBtFidelizacion()");
        
        btEditarConfiguracion.setVisible(false);
        
        lbMetodoConex.setText(I18N.getTexto("Método de conexión"));
        lbDesConfig.setText(I18N.getTexto("Tipo de tarjeta de fidelización"));
        taConfig.setVisible(true);
        mostrarComboBox(true);
        
        opcionSeleccionada = OPCION_FIDELIZACION;
        limpiarComboBox();
        
        ConfiguracionDispositivo fidelizacion;
        
        if(configFidelizProvisional.getModelo().isEmpty()){
            fidelizacion = Dispositivos.getInstance().getFidelizacion().getConfiguracion();
        }
        else{
            fidelizacion = configFidelizProvisional;
        }
        
        if(fidelizacion!=null&&!fidelizacion.getModelo().equals(variableNoUsa)){
            establecerConfigDispositivoInicial(fidelizacion);
        }
        else{
            cbDesConfig.getSelectionModel().select(variableNoUsa);
            taConfig.setText("");
        }
        
        cbDesConfig.requestFocus();
    }
    
    @FXML
    public void accionBtVisor() {
        log.debug("accionBtVisor()");
        
        btEditarConfiguracion.setVisible(false);
        
        lbMetodoConex.setText(I18N.getTexto("Método de conexión"));
        lbDesConfig.setText(I18N.getTexto("Tipo de visor"));
        taConfig.setVisible(true);
        mostrarComboBox(true);
        
        opcionSeleccionada = OPCION_VISOR;
        limpiarComboBox();
        
        ConfiguracionDispositivo visor;
        
        if(configVisorProvisional.getModelo().isEmpty()){
            visor = Dispositivos.getInstance().getVisor().getConfiguracion();
        }
        else{
            visor = configVisorProvisional;
        }
        
        if(visor!=null&&!visor.getModelo().equals(variableNoUsa)){
            establecerConfigDispositivoInicial(visor);
        }
        else{
            cbDesConfig.getSelectionModel().select(variableNoUsa);
            taConfig.setText("");
        }
        
        cbDesConfig.requestFocus();
    }
        
    @FXML
    public void accionBtCajon() {
        log.debug("accionBtCajon()");
        
        btEditarConfiguracion.setVisible(false);
        
        lbMetodoConex.setText(I18N.getTexto("Método de conexión"));
        lbDesConfig.setText(I18N.getTexto("Tipo de cajón"));
        taConfig.setVisible(true);
        mostrarComboBox(true);
        
        opcionSeleccionada = OPCION_CAJON;
        limpiarComboBox();
        
        ConfiguracionDispositivo cajon;
        
        if(configCajonProvisional.getModelo().isEmpty()){
            cajon = Dispositivos.getInstance().getCajon().getConfiguracion();
        }
        else{
            cajon = configCajonProvisional;
        }
        
        if(cajon!=null&&!cajon.getModelo().equals(variableNoUsa)){
            
            establecerConfigDispositivoInicial(cajon);
        }
        else{
            cbDesConfig.getSelectionModel().select(variableNoUsa);
            taConfig.setText("");
        }
        
        cbDesConfig.requestFocus();
    }
    
    @FXML
    public void accionBtBalanza() {
        log.debug("accionBtBalanza()");
        
        btEditarConfiguracion.setVisible(false);
        
        lbMetodoConex.setText(I18N.getTexto("Método de conexión"));
        lbDesConfig.setText(I18N.getTexto("Tipo de balanza"));
        taConfig.setVisible(true);
        mostrarComboBox(true);
        
        opcionSeleccionada = OPCION_BALANZA;
        limpiarComboBox();
        
        ConfiguracionDispositivo balanza;
        
        if(configBalanzaProvisional.getModelo().isEmpty()){
            balanza = Dispositivos.getInstance().getBalanza().getConfiguracion();
        }
        else{
            balanza = configBalanzaProvisional;
        }
        
        if(balanza!=null&&!balanza.getModelo().equals(variableNoUsa)){
            establecerConfigDispositivoInicial(balanza);
        }
        else{
            cbDesConfig.getSelectionModel().select(variableNoUsa);
            taConfig.setText("");
        }
        
        cbDesConfig.requestFocus();
    }
    
    @FXML
    public void accionBtRecargaMovil() {
        log.debug("accionBtRecargaMovil()");
        
        btEditarConfiguracion.setVisible(false);
        
        lbMetodoConex.setText(I18N.getTexto("Método de conexión"));
        lbDesConfig.setText(I18N.getTexto("Tipo de recarga móvil"));
        taConfig.setVisible(true);
        mostrarComboBox(true);
        
        opcionSeleccionada = OPCION_RECARGA_MOVIL;
        limpiarComboBox();
        
        ConfiguracionDispositivo recargaMovil;
        
        if(configRecargaMovilProvisional.getModelo().isEmpty()){
            recargaMovil = Dispositivos.getInstance().getRecargaMovil().getConfiguracion();
        }
        else{
            recargaMovil = configRecargaMovilProvisional;
        }
        
        if(recargaMovil!=null&&!recargaMovil.getModelo().equals(variableNoUsa)){
            
            establecerConfigDispositivoInicial(recargaMovil);
        }
        else{
            cbDesConfig.getSelectionModel().select(variableNoUsa);
            taConfig.setText("");
        }
        
        cbDesConfig.requestFocus();
    }
    
    @FXML
    public void accionBtEscaner() {
        log.debug("accionBtEscaner()");
        
        btEditarConfiguracion.setVisible(false);
        
        opcionSeleccionada = OPCION_ESCANER;
        lbMetodoConex.setText(I18N.getTexto("Método de conexión"));
        lbDesConfig.setText(I18N.getTexto("Tipo de escáner"));
        taConfig.setVisible(true);
        mostrarComboBox(true);
        
        opcionSeleccionada = OPCION_ESCANER;
        limpiarComboBox();
        
        ConfiguracionDispositivo escaner;
        
        if(configEscanerProvisional.getModelo().isEmpty()){
            if (Dispositivos.getInstance().getEscaner() !=null && !(Dispositivos.getInstance().getEscaner() instanceof EscanerNoConfig)){
                escaner = Dispositivos.getInstance().getEscaner().getConfiguracion();
            }
            else{
                escaner = null;
            }            
        }
        else{
            escaner = configEscanerProvisional;
        }
        if(escaner!=null&&!escaner.getModelo().equals(variableNoUsa)){
            establecerConfigDispositivoInicial(escaner);
        }
        else{
            cbDesConfig.getSelectionModel().select(variableNoUsa);
            taConfig.setText("");
        }
        
        cbDesConfig.requestFocus();
    }
    
    protected void establecerConfigDispositivoInicial(ConfiguracionDispositivo dispositivo){
        HashMap<String, HashMap<String, ConfiguracionModelo>> configuracionDispositivo = configuracionesDispositivos.get(opcionSeleccionada);
        
		LinkedList<String> listaDispositivos = new LinkedList<>(configuracionDispositivo.keySet());
        
        // Fix para hacer que NO USA o la cadena equivalente sea la primera que aparezca
        if (listaDispositivos.removeFirstOccurrence(I18N.getTexto("NO USA"))){
            listaDispositivos.addFirst(I18N.getTexto("NO USA"));
        }
        
        ObservableList<String> listDesConfig = FXCollections.observableList(listaDispositivos);
        cbDesConfig.getSelectionModel().selectedItemProperty().removeListener(cbDesConfigListener);
		cbDesConfig.setItems(listDesConfig);
		
        String modelo = dispositivo.getModelo();
        if(StringUtils.isBlank(modelo)) {
        	modelo = "NO USA";        	
        }
        
		cbDesConfig.getSelectionModel().select(modelo);
		cbDesConfig.getSelectionModel().selectedItemProperty().addListener(cbDesConfigListener);
        HashMap<String, ConfiguracionModelo> configuracion = configuracionDispositivo.get(modelo);
        if(configuracion != null) {
			Set<String> metodosConexion = configuracion.keySet();
			cbMetConex.setItems(FXCollections.observableList(new ArrayList<>(metodosConexion)));
	        cbMetConex.getSelectionModel().select(dispositivo.getNombreConexion());
	        if(dispositivo.getConfiguracionModelo().getConfigConexion()!=null){
	            taConfig.setText(dispositivo.getConfiguracionModelo().getConfigConexion().toString());
	        }
	        //Debemos mostrar las operaciones en caso de tenerlas
	        if(dispositivo.getConfiguracionModelo().getConfigOperaciones()!=null){
	            taConfig.setText(taConfig.getText() +"\n"+ dispositivo.getConfiguracionModelo().getConfigOperaciones().toString());
	        }
        }
    }
    
    /**
     * Limpia las opciones disponibles en los combobox.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void limpiarComboBox(){
        HashMap<String,HashMap<String,ConfiguracionModelo>> listaOpcionesTienda = configuracionesDispositivos.get(opcionSeleccionada);
        cbDesConfig.getSelectionModel().selectedItemProperty().removeListener(cbDesConfigListener);
        if(listaOpcionesTienda!=null){
            LinkedList listaDispositivos = new LinkedList(listaOpcionesTienda.keySet());        
             // Fix para hacer que NO USA o la cadena equivalente sea la primera que aparezca
            if (listaDispositivos.removeFirstOccurrence(I18N.getTexto("NO USA"))){
                listaDispositivos.addFirst(I18N.getTexto("NO USA"));
            }
            cbDesConfig.setItems(FXCollections.observableList(listaDispositivos));
        }
        else{
            cbDesConfig.setItems(FXCollections.<String>observableArrayList());
        }
        cbDesConfig.getSelectionModel().selectedItemProperty().addListener(cbDesConfigListener);
        cbMetConex.setItems(FXCollections.<String>observableArrayList());
    }
    
    @FXML
    public void mostrarOpcionesCBoxMetConexiones(){
        
        if(!opcionSeleccionada.equals(OPCION_GENERAL)){
            HashMap<String,HashMap<String,ConfiguracionModelo>> opcionesBtSeleccion = configuracionesDispositivos.get(opcionSeleccionada);
            
            String opcionSeleccionadaDesConfig = (String)cbDesConfig.getSelectionModel().getSelectedItem();
            
            if(opcionSeleccionadaDesConfig!=null){
                
                HashMap<String,ConfiguracionModelo> metodosConex = opcionesBtSeleccion.get(opcionSeleccionadaDesConfig);
                if(metodosConex!=null){
                    cbMetConex.setItems(FXCollections.observableList(new ArrayList<String>(metodosConex.keySet())));
                }
                else{
                    cbMetConex.setItems(FXCollections.<String>emptyObservableList());
                    establecerConfigProvisional(variableNoUsa, null);
                }
                taConfig.setText("");
            }
        }
    }
    
    @FXML
    public void mostrarConfigMetConex(){
        
        if(!opcionSeleccionada.equals(OPCION_GENERAL)){
            HashMap<String,HashMap<String,ConfiguracionModelo>> opcionesBtSeleccion = configuracionesDispositivos.get(opcionSeleccionada);
            
            String opcionSeleccionadaDesConfig = (String)cbDesConfig.getSelectionModel().getSelectedItem();
            
            if(opcionSeleccionadaDesConfig!=null){
                ConfiguracionModelo metodoConex = null;
                HashMap<String,ConfiguracionModelo> metodosConex = opcionesBtSeleccion.get(opcionSeleccionadaDesConfig);
                if(metodosConex!=null){
                    metodoConex = metodosConex.get((String)cbMetConex.getSelectionModel().getSelectedItem());
                }
                
                if(metodoConex!=null){
                    taConfig.setText(metodoConex.getConfigConexion().toString());            
                    //Debemos mostrar las operaciones en caso de tenerlas
                    if (metodoConex.getConfigOperaciones()!=null){
                        taConfig.setText(taConfig.getText() +"\n"+ metodoConex.getConfigOperaciones().toString());
                    }
                    establecerConfigProvisional(opcionSeleccionadaDesConfig,metodoConex);
                    
                    IDispositivo dispositivo = getDispositivoActual();
                    if(dispositivo != null && dispositivo.isConfigurable()) {
                    	btEditarConfiguracion.setVisible(true);
                    }
                    else {
                    	btEditarConfiguracion.setVisible(false);
                    }
                }
                else{
                    taConfig.setText("");
                }
            }
        }
    }
            
    @FXML
    public void accionCancelar(){
    	configBalanzaProvisional = new ConfiguracionDispositivo(TipoDispositivo.BALANZA);
        configCajonProvisional = new ConfiguracionDispositivo(TipoDispositivo.CAJON);
        configFidelizProvisional = new ConfiguracionDispositivo(TipoDispositivo.FIDELIZACION);
        configGeneralProvisional = new ConfiguracionDispositivo();
        configVisorProvisional = new ConfiguracionDispositivo(TipoDispositivo.VISOR);
        configImpresoraProvisional = new ConfiguracionDispositivo(TipoDispositivo.IMPRESORA1);
        configImpresoraProvisional2 = new ConfiguracionDispositivo(TipoDispositivo.IMPRESORA2);
        configTarjetaProvisional = new ConfiguracionDispositivo(TipoDispositivo.TARJETA);
        configRecargaMovilProvisional = new ConfiguracionDispositivo(TipoDispositivo.RECARGA_MOVIL);
        configEscanerProvisional = new ConfiguracionDispositivo(TipoDispositivo.ESCANER);
        
        getApplication().getMainView().close();
    }
    
    @FXML
    public void accionAceptar(){
        try {
			if(!configTarjetaProvisional.getModelo().equals("")){
			    Dispositivos.getInstance().getTarjeta().setConfiguracion(configTarjetaProvisional);
			}
			if(!configCajonProvisional.getModelo().equals("")){
			    Dispositivos.getInstance().getCajon().setConfiguracion(configCajonProvisional);
			}
			if(!configFidelizProvisional.getModelo().equals("")){
			    Dispositivos.getInstance().getFidelizacion().setConfiguracion(configFidelizProvisional);
			}
			if(!configImpresoraProvisional.getModelo().equals("")){
			    Dispositivos.getInstance().getImpresora1().setConfiguracion(configImpresoraProvisional);
			}
			 if(!configImpresoraProvisional2.getModelo().equals("")){
			    Dispositivos.getInstance().getImpresora2().setConfiguracion(configImpresoraProvisional2);
			}
			if(!configVisorProvisional.getModelo().equals("")){
			    Dispositivos.getInstance().getVisor().setConfiguracion(configVisorProvisional);
			}
			if(!configBalanzaProvisional.getModelo().equals("")){
			    Dispositivos.getInstance().getBalanza().setConfiguracion(configBalanzaProvisional);
			}
			if(!configRecargaMovilProvisional.getModelo().equals("")){
			    Dispositivos.getInstance().getRecargaMovil().setConfiguracion(configRecargaMovilProvisional);
			}
			if(!configEscanerProvisional.getModelo().equals("")){
			    Dispositivos.getInstance().getEscaner().setConfiguracion(configEscanerProvisional);
			}
		
        
	        byte[] confDispositivos = Dispositivos.getInstance().crearConfiguracionDispositivos();
	        
	        if(confDispositivos!=null){
	            TiendaCajaBean confCaja = sesion.getAplicacion().getTiendaCaja();
	            confCaja.setConfiguracion(confDispositivos);
	            try {            
	                tiendaCajaService.grabarConfiguracionDispositivos(confCaja);
	                VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Deberá reiniciar la aplicación para establecer los cambios."), getStage());
	                getApplication().getMainView().close();
	            }
	            catch (TiendaCajaServiceException ex) {
	                VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error guardando información de dispositivos"), getStage());
	            }
	
	            accionBtGenerales();
	            btEditarConfiguracion.setVisible(false);
	        }
        } catch (DispositivoException e) {
			log.error("accionAceptar() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error guardando información de dispositivos"), getStage());
		}
    }
    
    @FXML
    public void accionAceptarEnter(KeyEvent event){
        
        if(event.getCode() == KeyCode.ENTER){
            accionAceptar();
        }
    }
    
    //Metodo para crear la configuración provisional de dispositivos para más adelante, al aceptar,
    //guardar todos los cambios realizados
    protected void establecerConfigProvisional(String modelo, ConfiguracionModelo metodoConex){
        
        switch(opcionSeleccionada){
	        case OPCION_CAJON:
                configCajonProvisional.setModelo(modelo);
                configCajonProvisional.setNombreConexion(metodoConex!=null?metodoConex.getNombreConexion():"");
                configCajonProvisional.setConfiguracionModelo(metodoConex);
                if(Dispositivos.getInstance().getCajon() != null && Dispositivos.getInstance().getCajon().getConfiguracion() != null) {
                	configCajonProvisional.setParametrosConfiguracion(Dispositivos.getInstance().getCajon().getConfiguracion().getParametrosConfiguracion());
                }
                break;
            case OPCION_VISOR:
                configVisorProvisional.setModelo(modelo);
                configVisorProvisional.setNombreConexion(metodoConex!=null?metodoConex.getNombreConexion():"");
                configVisorProvisional.setConfiguracionModelo(metodoConex);
                if(Dispositivos.getInstance().getVisor() != null && Dispositivos.getInstance().getVisor().getConfiguracion() != null) {
                	configVisorProvisional.setParametrosConfiguracion(Dispositivos.getInstance().getVisor().getConfiguracion().getParametrosConfiguracion());
                }
                break;
            case OPCION_IMPRESORA1:
                configImpresoraProvisional.setModelo(modelo);
                configImpresoraProvisional.setNombreConexion(metodoConex!=null?metodoConex.getNombreConexion():"");
                configImpresoraProvisional.setConfiguracionModelo(metodoConex);
                if(Dispositivos.getInstance().getImpresora1() != null && Dispositivos.getInstance().getImpresora1().getConfiguracion() != null) {
                	configImpresoraProvisional.setParametrosConfiguracion(Dispositivos.getInstance().getImpresora1().getConfiguracion().getParametrosConfiguracion());
                }
                break;
            case OPCION_IMPRESORA2:
                configImpresoraProvisional2.setModelo(modelo);
                configImpresoraProvisional2.setNombreConexion(metodoConex!=null?metodoConex.getNombreConexion():"");
                configImpresoraProvisional2.setConfiguracionModelo(metodoConex);
                if(Dispositivos.getInstance().getImpresora2() != null && Dispositivos.getInstance().getImpresora2().getConfiguracion() != null) {
                	configImpresoraProvisional2.setParametrosConfiguracion(Dispositivos.getInstance().getImpresora2().getConfiguracion().getParametrosConfiguracion());
                }
                break;
            case OPCION_TARJETA:
                configTarjetaProvisional.setModelo(modelo);
                configTarjetaProvisional.setNombreConexion(metodoConex!=null?metodoConex.getNombreConexion():"");
                configTarjetaProvisional.setConfiguracionModelo(metodoConex);
                if(Dispositivos.getInstance().getTarjeta() != null && Dispositivos.getInstance().getTarjeta().getConfiguracion() != null) {
                	configTarjetaProvisional.setParametrosConfiguracion(Dispositivos.getInstance().getTarjeta().getConfiguracion().getParametrosConfiguracion());
                }
                break;
            case OPCION_FIDELIZACION:
                configFidelizProvisional.setModelo(modelo);
                configFidelizProvisional.setNombreConexion(metodoConex!=null?metodoConex.getNombreConexion():"");
                configFidelizProvisional.setConfiguracionModelo(metodoConex);
                if(Dispositivos.getInstance().getFidelizacion() != null && Dispositivos.getInstance().getFidelizacion().getConfiguracion() != null) {
                	configFidelizProvisional.setParametrosConfiguracion(Dispositivos.getInstance().getFidelizacion().getConfiguracion().getParametrosConfiguracion());
                }
                break;
            case OPCION_BALANZA:
                configBalanzaProvisional.setModelo(modelo);
                configBalanzaProvisional.setNombreConexion(metodoConex!=null?metodoConex.getNombreConexion():"");
                configBalanzaProvisional.setConfiguracionModelo(metodoConex);
                if(Dispositivos.getInstance().getBalanza() != null && Dispositivos.getInstance().getBalanza().getConfiguracion() != null) {
                	configBalanzaProvisional.setParametrosConfiguracion(Dispositivos.getInstance().getBalanza().getConfiguracion().getParametrosConfiguracion());
                }
                break;
            case OPCION_RECARGA_MOVIL:
                configRecargaMovilProvisional.setModelo(modelo);
                configRecargaMovilProvisional.setNombreConexion(metodoConex!=null?metodoConex.getNombreConexion():"");
                configRecargaMovilProvisional.setConfiguracionModelo(metodoConex);
                if(Dispositivos.getInstance().getRecargaMovil() != null && Dispositivos.getInstance().getRecargaMovil().getConfiguracion() != null) {
                	configRecargaMovilProvisional.setParametrosConfiguracion(Dispositivos.getInstance().getRecargaMovil().getConfiguracion().getParametrosConfiguracion());
                }
                break;
            case OPCION_ESCANER:
            	configEscanerProvisional.setModelo(modelo);
            	configEscanerProvisional.setNombreConexion(metodoConex!=null?metodoConex.getNombreConexion():"");
            	configEscanerProvisional.setConfiguracionModelo(metodoConex);
            	if(Dispositivos.getInstance().getEscaner() != null && Dispositivos.getInstance().getEscaner().getConfiguracion() != null) {
            		configEscanerProvisional.setParametrosConfiguracion(Dispositivos.getInstance().getEscaner().getConfiguracion().getParametrosConfiguracion());
            	}
            	break;
        }
    }
    /**
     * Muestra los combobox de selección de metodo de conexion y tipo de dispositivo.
     * @param mostrarComboBox 
     */
    protected void mostrarComboBox(boolean mostrarComboBox){
        cbDesConfig.setVisible(mostrarComboBox);
        cbMetConex.setVisible(mostrarComboBox);
        tfCaja.setVisible(!mostrarComboBox);
        tfTienda.setVisible(!mostrarComboBox);
    }
    
    public void abrirConfiguracionDispositivo() {
    	IDispositivo dispositivo = getDispositivoActual();
    	if(dispositivo != null) {
    		dispositivo.configurar(getStage());
    	}
    	else {
    		VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido abrir la ventana de configuración"), getStage());
    	}
    }

	protected IDispositivo getDispositivoActual() {
	    ConfiguracionDispositivo configuracionActual = null;
    	switch(opcionSeleccionada){
	        case OPCION_CAJON:
	        	configuracionActual = configCajonProvisional;
                break;
            case OPCION_VISOR:
            	configuracionActual = configVisorProvisional;
                break;
            case OPCION_IMPRESORA1:
            	configuracionActual = configImpresoraProvisional;
                break;
            case OPCION_IMPRESORA2:
            	configuracionActual = configImpresoraProvisional2;
                break;
            case OPCION_TARJETA:
            	configuracionActual = configTarjetaProvisional;
                break;
            case OPCION_FIDELIZACION:
            	configuracionActual = configFidelizProvisional;
                break;
            case OPCION_BALANZA:
            	configuracionActual = configBalanzaProvisional;
                break;
            case OPCION_RECARGA_MOVIL:
            	configuracionActual = configRecargaMovilProvisional;
                break;
            case OPCION_ESCANER:
            	configuracionActual = configEscanerProvisional;
                break;
        }

    	String modelo = (String)cbDesConfig.getSelectionModel().getSelectedItem();
    	String nombreConexion = (String)cbMetConex.getSelectionModel().getSelectedItem();
    	ConfiguracionModelo confModelo = Dispositivos.getInstance().getDispositivosDisponibles().getDispositivos(configuracionActual.getTipoDispositivo()).get(modelo).get(nombreConexion);
		IDispositivo dispositivo = null;
        try {
	        dispositivo = new DispositivoBuilder().create(confModelo);
	        dispositivo.setConfiguracion(configuracionActual);
        }
        catch (Exception e) {
        	log.error("getDispositivoActual() - No se ha podido inicializar el dispositivo: " + e.getMessage());
        }
	    return dispositivo;
    }
}
