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
package com.comerzzia.pos.gui.ventas.apartados.detalle.verPagos;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.comerzzia.pos.gui.ventas.apartados.ApartadosManager;
import com.comerzzia.pos.persistence.apartados.pagos.ApartadosPagoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class VerPagosApartadoController extends WindowController implements IContenedorBotonera{


	public static final String PARAMETRO_PAGOS = "pagos_apartado";
	public static final String PARAMETRO_APARTADO_MGR = "apartado_mgr";

	@FXML
	protected TableView<VerPagosApartadoGui> tbPagos;

	@FXML
	protected AnchorPane panelBotoneraTabla;

	@FXML
	protected Label lbTitulo;

	@FXML
	protected TableColumn tcFecha, tcImporte, tcMedioPago;

	@FXML
	protected Button btAceptar, btCancelar;

	// botonera de acciones de tabla
	BotoneraComponent botoneraAccionesTabla;

	protected Logger log = Logger.getLogger(getClass());

	ObservableList<VerPagosApartadoGui> pagos;
	
	ApartadosManager apartadosManager;
	
	@Autowired
	private MediosPagosService mediosPagosService;
	@Autowired
	private CajasService cajasService;
	
	@Autowired
	private Sesion sesion;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		tbPagos.setPlaceholder(new Label(""));

		pagos = FXCollections.observableList(new ArrayList<VerPagosApartadoGui>());

		tcFecha.setCellFactory(CellFactoryBuilder.createCellRendererCeldaFechaHora("tbPagos", "tcCodDoc", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbPagos", "tcDesDoc", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcMedioPago.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPagos", "tcMedioPago", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

		tcFecha.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VerPagosApartadoGui, Date>, ObservableValue<Date>>() {
			@Override
			public ObservableValue<Date> call(TableColumn.CellDataFeatures<VerPagosApartadoGui, Date> cdf) {
				return cdf.getValue().getFechaPagoProperty();
			}
		});
		tcImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VerPagosApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<VerPagosApartadoGui, BigDecimal> cdf) {
				return cdf.getValue().getImporteProperty();
			}
		});
		tcMedioPago.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VerPagosApartadoGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<VerPagosApartadoGui, String> cdf) {
				return cdf.getValue().getMedioPagoProperty();
			}
		});
	}

	@Override
	public void initializeForm() throws InitializeGuiException {

		List<ApartadosPagoBean> pagosApartado = (List<ApartadosPagoBean>)getDatos().get(PARAMETRO_PAGOS);
		apartadosManager = (ApartadosManager)getDatos().get(PARAMETRO_APARTADO_MGR);

		pagos.clear();
		try {
			CajaMovimientoBean movimientoBean;
			for(ApartadosPagoBean pago: pagosApartado){		
				movimientoBean = cajasService.consultarMovimientoApartado(pago.getUidDiarioCaja(), pago.getLinea(), sesion.getAplicacion().getUidActividad());
				movimientoBean.setDesMedioPago(mediosPagosService.getMedioPago(movimientoBean.getCodMedioPago()).getDesMedioPago());
				pagos.add(new VerPagosApartadoGui(movimientoBean));
			}
		} catch (CajasServiceException e) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error consultando los pagos del apartado"), this.getStage());
		}
		tbPagos.setItems(pagos);
	}

	public void initializeComponents() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();

		try{
			// Botonera de Tabla
			List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
			botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelBotoneraTabla.getPrefWidth(), panelBotoneraTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
			panelBotoneraTabla.getChildren().add(botoneraAccionesTabla);
		}
		catch (CargarPantallaException ex) {
			VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getApplication().getStage());
		}
	}

	public void accionAceptar(){
		log.trace("accionAceptar()");
		
		tbPagos.requestFocus();
		
		Map<String,Object> mapaParametros= new HashMap<String,Object>();
		mapaParametros.put("apartado", apartadosManager.getTicketApartado().getCabecera());
		mapaParametros.put("importe", FormatUtil.getInstance().formateaImporte(tbPagos.getSelectionModel().getSelectedItem().getMovimiento().getCargo()));
		mapaParametros.put("fecha", FormatUtil.getInstance().formateaFechaHora(new Date()));
		mapaParametros.put("empresa", sesion.getAplicacion().getEmpresa());
		mapaParametros.put("cajero", sesion.getSesionUsuario().getUsuario().getDesusuario());
		mapaParametros.put("pago", tbPagos.getSelectionModel().getSelectedItem().getMovimiento());
		
		try {
			ServicioImpresion.imprimir("informe_apartado", mapaParametros);
		} catch (DeviceException e) {
			log.error("Error en la reimpresión del pago", e);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Se produjo un error en la impresión."), getStage());
		}
	}

	public void accionCancelar(){
		log.trace("accionCancelar()");
		getStage().close();
	}

	private List<ConfiguracionBotonBean> cargarAccionesTabla() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
		return listaAcciones;
	}
	
	@Override
    public void realizarAccion(BotonBotoneraComponent botonAccionado) {
        switch (botonAccionado.getClave()) {
            // BOTONERA TABLA MOVIMIENTOS
            case "ACCION_TABLA_PRIMER_REGISTRO":
                accionTablaPrimerRegistro(tbPagos);
                tbPagos.requestFocus();
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                accionTablaIrAnteriorRegistro(tbPagos);
                tbPagos.requestFocus();
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                accionTablaIrSiguienteRegistro(tbPagos);
                tbPagos.requestFocus();
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                accionTablaUltimoRegistro(tbPagos);
                tbPagos.requestFocus();
                break;
        }
    }
	
    /**
    * @param event 
    */
   public void aceptarDocDobleClick(MouseEvent event) {
        log.debug("aceptarArticuloDobleClick() - Acción aceptar");
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                accionAceptar();
            }
        }
    }

	@Override
	public void initializeFocus() {
		tbPagos.requestFocus();
		tbPagos.getSelectionModel().select(0);
	}
}
