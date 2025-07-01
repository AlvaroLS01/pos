package com.comerzzia.pos.gui.sales.layaway.items.verPagos;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalLine;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.pagos.ApartadosPagoBean;
import com.comerzzia.omnichannel.facade.service.cashjournal.CashJournalServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.layaway.ApartadosManager;
import com.comerzzia.pos.util.format.FormatUtils;

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
import javafx.util.Callback;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
@CzzScene
public class VerPagosApartadoController extends SceneController implements ButtonsGroupController{


	public static final String PARAMETRO_PAGOS = "pagos_apartado";
	public static final String PARAMETRO_APARTADO_MGR = "apartado_mgr";

	@FXML
	protected TableView<VerPagosApartadoGui> tbPagos;


	@FXML
	protected Label lbTitulo;

	@FXML
	protected TableColumn tcFecha, tcImporte, tcMedioPago;

	@FXML
	protected Button btAceptar, btCancelar;

	// botonera de acciones de tabla
	protected ButtonsGroupComponent botoneraAccionesTabla;

	protected Logger log = Logger.getLogger(getClass());

	protected ObservableList<VerPagosApartadoGui> pagos;
	
	protected ApartadosManager apartadosManager;
		
	@Autowired
	protected CashJournalServiceFacade cashJournalService;
	
	@Autowired
	private Session sesion;

	@Autowired
	protected ComerzziaTenantResolver tenantResolver;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		tbPagos.setPlaceholder(new Label(""));

		pagos = FXCollections.observableList(new ArrayList<VerPagosApartadoGui>());

		tcFecha.setCellFactory(CellFactoryBuilder.createCellRendererCeldaFechaHora("tbPagos", "tcCodDoc", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbPagos", "tcDesDoc", CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcMedioPago.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPagos", "tcMedioPago", 2, CellFactoryBuilder.LEFT_ALIGN_STYLE));

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
	public void onSceneOpen() throws InitializeGuiException {

		List<ApartadosPagoBean> pagosApartado = (List<ApartadosPagoBean>)sceneData.get(PARAMETRO_PAGOS);
		apartadosManager = (ApartadosManager)sceneData.get(PARAMETRO_APARTADO_MGR);

		pagos.clear();
		CashJournalLine movimientoBean;
		for(ApartadosPagoBean pago: pagosApartado){		
			movimientoBean = cashJournalService.findCashJournalLineById(pago.getUidDiarioCaja(), pago.getLinea());
//				movimientoBean.setDesMedioPago(mediosPagosService.findByIdActive(movimientoBean.getCodMedioPago()).getPaymentMethodDes());
			pagos.add(new VerPagosApartadoGui(movimientoBean));
		}
		tbPagos.setItems(pagos);
	}

	public void initializeComponents() throws InitializeGuiException {
	}

	public void accionAceptar(){
		log.trace("accionAceptar()");
		
		tbPagos.requestFocus();
		
		Map<String,Object> mapaParametros= new HashMap<String,Object>();
		mapaParametros.put("apartado", apartadosManager.getTicketApartado().getCabecera());
		mapaParametros.put("importe", FormatUtils.getInstance().formatAmount(tbPagos.getSelectionModel().getSelectedItem().getMovimiento().getInput()));
		mapaParametros.put("fecha", FormatUtils.getInstance().formatDateTime(new Date()));
		mapaParametros.put("empresa", sesion.getApplicationSession().getCompany());
		mapaParametros.put("cajero", tenantResolver.getUser().getUserDes());
		mapaParametros.put("pago", tbPagos.getSelectionModel().getSelectedItem().getMovimiento());
		
//		try {
//			ServicioImpresion.imprimir("informe_apartado", mapaParametros);
//		} catch (DeviceException e) {
//			log.error("Error en la reimpresión del pago", e);
//			DialogWindowComponent.openWarnWindow(I18N.getText("Se produjo un error en la impresión."), getStage());
//		}
	}

	public void accionCancelar(){
		log.trace("accionCancelar()");
		closeCancel();
	}

	private List<ButtonConfigurationBean> cargarAccionesTabla() {
		List<ButtonConfigurationBean> listaAcciones = new ArrayList<>();
		listaAcciones.add(new ButtonConfigurationBean("icons/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
		listaAcciones.add(new ButtonConfigurationBean("icons/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
		listaAcciones.add(new ButtonConfigurationBean("icons/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
		listaAcciones.add(new ButtonConfigurationBean("icons/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
		return listaAcciones;
	}
	
	@Override
    public void executeAction(ActionButtonComponent botonAccionado) {
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
