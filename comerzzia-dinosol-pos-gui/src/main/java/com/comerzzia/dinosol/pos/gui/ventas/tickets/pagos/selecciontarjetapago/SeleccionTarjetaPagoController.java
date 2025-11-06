package com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.selecciontarjetapago;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.DinoPagosController;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.format.FormatUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

@Component
@SuppressWarnings("unchecked")
public class SeleccionTarjetaPagoController  extends WindowController implements Initializable, IContenedorBotonera{
	
	private static final Logger log = Logger.getLogger(SeleccionTarjetaPagoController.class);
	
	public static final String PARAMETRO_TARJETAS_SELECCIONADA = "TARJETA_SELECCIONADA";

	public static final String PARAMETRO_TARJETAS_CANCELAR = null;

	protected List<PagoTicket> pagos;
	
	@FXML
	protected Label lbTitulo;
	@FXML
	protected Button btAceptar, btCancelar;
	@FXML
	protected TableView<PagoTicket> tvTarjetas;
	protected ObservableList<PagoTicket> listTarjetas;
	@FXML
	protected TableColumn<PagoTicket, String> tcNumeroTarjeta, tcImporte;
	
	@FXML
    protected AnchorPane panelMenuTabla;
	protected BotoneraComponent botoneraAccionesTabla;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tvTarjetas.setPlaceholder(new Label(""));
		tvTarjetas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		listTarjetas = FXCollections.observableList(new ArrayList<PagoTicket>());

		tcNumeroTarjeta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tvTarjetas", "tcNumeroTarjeta", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tvTarjetas", "tcImporte", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		
		tcNumeroTarjeta.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PagoTicket, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<PagoTicket, String> cdf){
				return new SimpleStringProperty(cdf.getValue().getDatosRespuestaPagoTarjeta().getTarjeta());
			}
		});
		tcImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PagoTicket, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<PagoTicket, String> cdf){
				return new SimpleStringProperty((FormatUtil.getInstance().formateaImporte(cdf.getValue().getImporte())));
			}
		});
		tvTarjetas.setItems(listTarjetas);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		// DOBLE CLICK
		tvTarjetas.setOnMouseClicked(mouseEvent -> {
			if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
				if(mouseEvent.getClickCount() == 2){
					accionAceptar();
				}
			}
		});
		
		// EVENTOS TECLADO
		registraEventoTeclado(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent tecla){
				if(tecla.getCode().equals(KeyCode.ESCAPE)){
					accionCancelar();
					tecla.consume();
				}
				else if(tecla.getCode().equals(KeyCode.ENTER)){
					accionAceptar();
					tecla.consume();
				}
			}
		}, KeyEvent.KEY_RELEASED);
		
		// BOTONERA LATERAL
		try{
			List<ConfiguracionBotonBean> listaAccionesTablaVen = new ArrayList<ConfiguracionBotonBean>();
			listaAccionesTablaVen.addAll(BotoneraComponent.cargarAccionesTablaSimple());
	        botoneraAccionesTabla = new BotoneraComponent(8, 1, this, listaAccionesTablaVen, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(),
	        		BotonBotoneraSimpleComponent.class.getName());
	        panelMenuTabla.getChildren().clear();
	        panelMenuTabla.getChildren().add(botoneraAccionesTabla);
		}
		catch(Exception e){
			log.error("inicializarComponentes() - Error inicializando la botonera de navegación de la tabla de selección de tarjetas origen.");
            VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getStage());
		}
		
	}

	@Override
	public void initializeFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeForm() throws InitializeGuiException{
// Sacamos el listado de tarjetas del ticket origen.
		List<PagoTicket> listTarjetasTicketOrigen = (List<PagoTicket>) getDatos().get(DinoPagosController.PARAM_PAGOS);
		listTarjetas.clear();
		if(listTarjetasTicketOrigen != null && !listTarjetasTicketOrigen.isEmpty()){
			listTarjetas.addAll(listTarjetasTicketOrigen);
			
			tvTarjetas.requestFocus();
			tvTarjetas.getSelectionModel().selectFirst();
		}
		else{
			accionCancelar();
		}
		
		getDatos().remove(DinoPagosController.PARAM_PAGOS);
	}
	
	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException{
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
		switch (botonAccionado.getClave()){
			case "ACCION_TABLA_PRIMER_REGISTRO":
				accionTablaPrimerRegistro(tvTarjetas);
				break;
			case "ACCION_TABLA_ANTERIOR_REGISTRO":
				accionTablaIrAnteriorRegistro(tvTarjetas);
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO":
				accionTablaIrSiguienteRegistro(tvTarjetas);
				break;
			case "ACCION_TABLA_ULTIMO_REGISTRO":
				accionTablaUltimoRegistro(tvTarjetas);
				break;
			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
				break;
		}
	}

	public PagoTicket getLineaSeleccionada(){
		return tvTarjetas.getSelectionModel().getSelectedItem();
	}
	
	@FXML
	public void accionAceptar(){
		if(getLineaSeleccionada() != null){
			getDatos().put(PARAMETRO_TARJETAS_SELECCIONADA, getLineaSeleccionada());
			this.getStage().close();
		}
	}
	
	@FXML
	public void accionCancelar(){
		getDatos().put(PARAMETRO_TARJETAS_CANCELAR, true);
		this.getStage().close();
	}
	
}
