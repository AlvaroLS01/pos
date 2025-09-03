package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.promociones;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.promociones.Promocion;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

@Component
@Primary
public class IskaypetPromocionesAplicablesController extends WindowController implements Initializable, IContenedorBotonera {

	private static final Logger log = Logger.getLogger(IskaypetPromocionesAplicablesController.class.getName());

	public static final String PROMOCION_SELECCIONADA = "PROMOCION_SELECCIONADA";
	
	public static final String NUM_PROMOCIONES_NO_APLICADAS = "NUM_PROMOCIONES_NO_APLICADAS";
	public static final String LISTADO_PROMOCIONES_NO_APLICADAS = "LISTADO_PROMOCIONES_NO_APLICADAS";
	
	public static final String IR_PANTALLA_PAGOS = "IR_PANTALLA_PAGOS";
	public static final String IR_PANTALLA_VENTAS = "IR_PANTALLA_VENTAS";
	
	public static final Long TIPO_PROMOCION_PANTALLA = 3L;
	
	public static final String ACCION_CANCELAR_PROMOS = "ACCION_CANCELAR_PROMOS";
	
	@FXML
	private Label lbTitulo;

	@FXML
	protected Button btAceptar, btCancelar;
	
	@FXML
	private AnchorPane panelBotoneraTabla;
	
	@FXML
	private TableView<IskaypetPromocionesAplicablesGui> tbPromos;

	@FXML
	private TableColumn<IskaypetPromocionesAplicablesGui, String> tcCod, tcDescripcion;

	@FXML 
	private AnchorPane apCargarPromos;
	
	@Autowired
	protected Sesion sesion;

    protected BotoneraComponent botoneraAccionesTabla;
    
	protected ObservableList<IskaypetPromocionesAplicablesGui> promos;

	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		tcCod.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPromos", "tcCod", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcCod.setCellValueFactory(new PropertyValueFactory<IskaypetPromocionesAplicablesGui, String>("idPromocion"));
		tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPromos", "tcDescripcion", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDescripcion.setCellValueFactory(new PropertyValueFactory<IskaypetPromocionesAplicablesGui, String>("desPromocion"));
		
		promos = FXCollections.observableList(new ArrayList<IskaypetPromocionesAplicablesGui>());
		tbPromos.setItems(promos);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		
		log.debug("inicializarComponentes()");
        //Se registra el evento para salir de la pantalla pulsando la tecla escape. 
		registrarAccionCerrarVentanaEscape();
        crearEventoEnterTabla(tbPromos);
        crearEventoNavegacionTabla(tbPromos);

        try {
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = new LinkedList<>();
            
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", ""));
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", ""));
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", ""));
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", ""));

            botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelBotoneraTabla.getPrefWidth(), panelBotoneraTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelBotoneraTabla.getChildren().clear();
            panelBotoneraTabla.getChildren().add(botoneraAccionesTabla);

        }
        catch (CargarPantallaException ex) {
            log.error("inicializarComponentes() - Error inicializando pantalla de gestiond e tickets");
            VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getStage());
        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Inicializando pantalla...");
		
		limpiarDatos();
		
		if(getDatos().containsKey(IskaypetFacturacionArticulosController.PROMO_APLICABLES_CONSULTADAS)){
			cargarDatosTabla((Map<Long, Promocion>)getDatos().get(IskaypetFacturacionArticulosController.PROMO_APLICABLES_CONSULTADAS));
		}
		else{
			String mensajeError = "No se puede iniciar las Promociones Aplicables por falta de Promociones";
			log.error("PromoAplicablesController/initializeForm() " + mensajeError);
			throw new InitializeGuiException(mensajeError);
		}

	}
	
	public void cargarDatosTabla(Map<Long, Promocion> promocionesAplicables){
		log.info("PromoAplicablesController/cargarDatosTabla() - Se van a mostrar " + promocionesAplicables.size() + " Promociones Aplicables...");
		List<IskaypetPromocionesAplicablesGui> listadoPromociones = new ArrayList<IskaypetPromocionesAplicablesGui>();
		
		List<Promocion> mapValues = new ArrayList<Promocion>(promocionesAplicables.values());
		promos.clear();
		for(Promocion promo : mapValues){
			IskaypetPromocionesAplicablesGui promoGui = new IskaypetPromocionesAplicablesGui();
			promoGui.setIdPromocion(new SimpleStringProperty(String.valueOf(promo.getIdPromocion())));
			promoGui.setDesPromocion(new SimpleStringProperty(promo.getDescripcion()));
			promoGui.setIdTipoPromocion(new SimpleStringProperty(String.valueOf(promo.getIdTipoPromocion())));
			listadoPromociones.add(promoGui);
		}
		
		Collections.sort(listadoPromociones, new Comparator<IskaypetPromocionesAplicablesGui>(){
			@Override
			public int compare(IskaypetPromocionesAplicablesGui o1, IskaypetPromocionesAplicablesGui o2){
				return o1.getIdPromocion().compareTo(o2.getIdPromocion());
			}
		});
		
		promos.addAll(listadoPromociones);
	}

	@Override
	public void initializeFocus() {		
		
	}
	
	@FXML
	void accionAceptar(ActionEvent event) {
		log.info("accionAceptar() - Buscando por filtro seleccionado...");
		IskaypetPromocionesAplicablesGui seleccionado = tbPromos.getSelectionModel().getSelectedItem();
		getDatos().put(PROMOCION_SELECCIONADA, seleccionado);
		getStage().close();
	}

	@FXML
	void accionCancelar(ActionEvent event) {
		log.info("accionCancelar() - cancelando busqueda de motivos");
		getDatos().put(ACCION_CANCELAR_PROMOS, true);
		getStage().close();
	}

	@FXML
	void aceptarDocDobleClick(MouseEvent event) {
		log.info("accionAceptarDobleClick() - Buscando por filtro seleccionado...");
		accionAceptar(null);
	}
	@FXML
	public void salirEsc(KeyEvent event) {
		if(event.getCode()==KeyCode.ESCAPE) {
			getStage().close();
		}
	}

	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException {
        switch (botonAccionado.getClave()) {
            // BOTONERA TABLA MOVIMIENTOS
            case "ACCION_TABLA_PRIMER_REGISTRO":
                accionTablaPrimerRegistro();
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                accionTablaAnteriorRegistro();
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                accionTablaSiguienteRegistro();
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                accionTablaUltimoRegistro();
                break;
        }
	}
	
    @FXML
    private void accionTablaPrimerRegistro() {
        log.debug("accionTablaPrimerRegistro() - Acción ejecutada");
        if (tbPromos.getItems() != null && !tbPromos.getItems().isEmpty()) {
        	tbPromos.getSelectionModel().select(0);
        	tbPromos.scrollTo(0);  // Mueve el scroll para que se vea el registro
        }
    }


    @FXML
    private void accionTablaAnteriorRegistro() {
        log.debug("accionTablaAnteriorRegistro() - Acción ejecutada");
        if (tbPromos.getItems() != null && !tbPromos.getItems().isEmpty()) {
            int indice = tbPromos.getSelectionModel().getSelectedIndex();
            if (indice > 0) {
            	tbPromos.getSelectionModel().select(indice - 1);
            	tbPromos.scrollTo(indice - 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    @FXML
    private void accionTablaSiguienteRegistro() {
        log.debug("accionIrSiguienteRegistroTabla() - Acción ejecutada");
        if (tbPromos.getItems() != null && !tbPromos.getItems().isEmpty()) {
            int indice = tbPromos.getSelectionModel().getSelectedIndex();
            if (indice < tbPromos.getItems().size()) {
            	tbPromos.getSelectionModel().select(indice + 1);
            	tbPromos.scrollTo(indice + 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    @FXML
    private void accionTablaUltimoRegistro() {
        log.debug("accionTablaUltimoRegistro() - Acción ejecutada");
        if (tbPromos.getItems() != null && !tbPromos.getItems().isEmpty()) {
        	tbPromos.getSelectionModel().select(tbPromos.getItems().size() - 1);
        	tbPromos.scrollTo(tbPromos.getItems().size() - 1);  // Mueve el scroll para que se vea el registro
        }
    }
    
    public void limpiarDatos(){
		promos.clear();
		tbPromos.setItems(promos);
	}
}
