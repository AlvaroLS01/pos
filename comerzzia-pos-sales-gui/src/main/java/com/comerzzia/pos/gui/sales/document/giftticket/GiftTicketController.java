package com.comerzzia.pos.gui.sales.document.giftticket;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.documents.PrintDocumentRequest;
import com.comerzzia.omnichannel.facade.model.documents.SaleDocHdr;
import com.comerzzia.omnichannel.facade.model.documents.SaleDocLine;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.devices.printer.PrintService;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

@Controller
@SuppressWarnings({"rawtypes", "unchecked"})
@CzzScene
public class GiftTicketController extends SceneController implements ButtonsGroupController{

	protected static final Logger log = Logger.getLogger(GiftTicketController.class.getName());

	public static final String PARAM_GIFTCARD = "TICKET_REGALO";
	public static final String PARAM_CANCEL = "CANCEL";

	protected SaleDocHdr giftTicket;

	@FXML
	protected TableView tbDocument;
	
	@FXML
	protected TableColumn tcItemCode, tcItemDes, tcQuantity, tcBtSelec, tcCombination1, tcCombination2;
	@FXML
	protected Button btAccept, btCancel;

	protected ObservableList<GiftTicketItemRow> lines;
	
	@Autowired
	protected Session session;

	@Autowired
	protected VariableServiceFacade variablesServices;
	
	@Autowired
	protected PrintService printService;

	protected boolean allSelected;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		tbDocument.setPlaceholder(new Label(""));

		tcItemCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbDocument", "tcItemCode", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcQuantity.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbDocument", "tcQuantity", null,CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcItemDes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbDocument", "tcItemDes", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcCombination1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbDocument", "tcCombination1", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcCombination2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbDocument", "tcCombination2", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
		//tcBtSelec.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbDocument", "tcBtSelec", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));

		tcItemCode.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GiftTicketItemRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<GiftTicketItemRow, String> cdf) {
				return cdf.getValue().getCodArticulo();
			}
		});
		tcItemDes.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GiftTicketItemRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<GiftTicketItemRow, String> cdf) {
				return cdf.getValue().getDesArticulo();
			}
		});
		tcQuantity.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GiftTicketItemRow, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<GiftTicketItemRow, BigDecimal> cdf) {
				return cdf.getValue().getCantidad();
			}
		});
		tcCombination1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GiftTicketItemRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<GiftTicketItemRow, String> cdf) {
				return cdf.getValue().getDesglose1();
			}
		});
		tcCombination2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GiftTicketItemRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<GiftTicketItemRow, String> cdf) {
				return cdf.getValue().getDesglose2();
			}
		});

		tcBtSelec.setCellValueFactory(new PropertyValueFactory<GiftTicketItemRow, Boolean>("lineaSelec"));
		tcBtSelec.setCellFactory(CheckBoxTableCell.forTableColumn(tcBtSelec));
		tcBtSelec.getStyleClass().add(CellFactoryBuilder.CENTER_ALIGN_STYLE);
		tcBtSelec.setEditable(true);
		tbDocument.setEditable(true);		
				
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {

		addEnterTableEvent(tbDocument);
        addNavegationTableEvent(tbDocument);
        
        EventHandler<KeyEvent> evhGeneral = new EventHandler<KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                KeyCodeCombination keyCode = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
                if (keyCode.match(event)) {
                	if (allSelected) {
                		deleteSelectLines();
                	} else {
                		selectLines();
                	}
                	if (tbDocument.isFocused()) {
                		//Si la tabla tiene el foco, se ejecutará tanto este evento como el de accionEventoEnterTabla
                		//por lo que quedaría la linea seleccionada al contrario que todas las demás. Para evitar este efecto, forzamos la ejecución del evento enter
                		actionTableEventEnter(null);
                	}
                }
            }
        };
        if (session.getApplicationSession().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
			tcCombination1.setText(I18N.getText(variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcCombination1.setVisible(false);
		}
		if (session.getApplicationSession().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
			tcCombination2.setText(I18N.getText(variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcCombination2.setVisible(false);
		}
        if (getScene() != null){
        	addKeyEventHandler(evhGeneral, KeyEvent.KEY_RELEASED);
        }
		
	}
	

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		giftTicket = (SaleDocHdr)this.sceneData.get(PARAM_GIFTCARD);
        List<SaleDocLine> giftCardLines = giftTicket.getLines();
        
        lines = FXCollections.observableList(new ArrayList<GiftTicketItemRow>());
        
        tbDocument.setItems(lines);
        
        for(SaleDocLine gifCardLine: giftCardLines){
        	if (!isIntegerValue(gifCardLine.getQuantity())) {
        		lines.add(new GiftTicketItemRow(gifCardLine.getItemDes(), gifCardLine.getQuantity(),
                        gifCardLine.getLineId(), gifCardLine.getItemCode(), gifCardLine.getCombination1Code(), gifCardLine.getCombination2Code()));
        	} else {
	            int quantity = gifCardLine.getQuantity().intValue();
	            
	            for(int i=0;i<quantity;i++){
	                lines.add(new GiftTicketItemRow(gifCardLine.getItemDes(),BigDecimal.ONE,
	                        gifCardLine.getLineId(), gifCardLine.getItemCode(), gifCardLine.getCombination1Code(), gifCardLine.getCombination2Code()));
	            }
        	}
        }
        
        selectLines();
        tbDocument.getSelectionModel().select(0);
        Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initializeFocus();
			}
		});
	}

	protected boolean isIntegerValue(BigDecimal bd) {
		return bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0;
	}
	
	@Override
	public void initializeFocus() {
		btAccept.requestFocus();
	}

	@Override
	public void executeAction(ActionButtonComponent pressedButton) {
		log.trace("realizarAccion() - Realizando la acción : " + pressedButton.getClave() + " de tipo : " + pressedButton.getTipo());
        switch (pressedButton.getClave()) {
            case "ACCION_TABLA_PRIMER_REGISTRO":
                actionTableEventFirst(tbDocument);
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                actionTableEventPrevious(tbDocument);
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                actionTableEventNext(tbDocument);
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                actionTableEventLast(tbDocument);
                break;
            case "ACCION_TABLA_SELECCIONAR_TODO":
                selectLines();
                break;
            case "ACCION_TABLA_BORRAR_SELECCION":
                deleteSelectLines();
                break;
        }
	}
	
	@FXML
    public void actionAccept(){
        
        ArrayList<Integer> selectedLines = new ArrayList<Integer>();
        for(GiftTicketItemRow lineRow: lines){
            if(lineRow.isLineaSelec()){
                SaleDocLine documentLine = giftTicket.getLines().stream()
                		.filter(ln -> 
                		ln.getLineId().equals(lineRow.getIdLinea())
                		).findFirst()
                		.orElse(null);
                if(documentLine!=null) {
                	selectedLines.add(documentLine.getLineId());
                }
            }
        }

		if (!selectedLines.isEmpty()) {
			String giftCardFormat = session.getApplicationSession().getDocTypeByDocTypeCode(giftTicket.getDocTypeCode())
					.getFormatoImpresionTicketRegalo();
			if (giftCardFormat != null) {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("ticket", giftTicket);
				params.put("selectedLines", selectedLines);
				params.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));

				PrintDocumentRequest printRequest = new PrintDocumentRequest();
				printRequest.setCustomParams(params);
				printRequest.setPrintTemplate(giftCardFormat);
				try {
					printService.printDocument(giftTicket.getSalesDocUid(), giftTicket.getDocTypeCode(), printRequest);
				} catch (DeviceException e) {
					DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Lo sentimos, ha ocurrido un error al imprimir."), e);
				}
			} else {
				DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog("No existe un formato de ticket regalo para el tipo de documento del ticket original.");
			}
			closeSuccess();
		} else {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No hay ninguna línea seleccionada."));
		}
    }
	
	/**
     * La acción Enter muestra pantalla de visualización del ticket
     *
     * @param idTabla
     */
    public void actionTableEventEnter(String idTabla) {
        log.debug("accionEventoEnterTabla() - Acción ejecutada");
        GiftTicketItemRow selectedItem = (GiftTicketItemRow) tbDocument.getSelectionModel().getSelectedItem();
        selectedItem.setLineaSelec(!selectedItem.isLineaSelec());
    }
	
	protected void deleteSelectLines(){
        log.debug("deleteSelectLines() - Acción ejecutada");
        
        for(GiftTicketItemRow lineaTicket: lines){
            lineaTicket.setLineaSelec(false);
        }
        allSelected = false;
    }
    
    protected void selectLines(){
        log.debug("selectLines() - Acción ejecutada");
        
        for(GiftTicketItemRow documentLine: lines){
            documentLine.setLineaSelec(true);
        }
        allSelected = true;
    }
	
	protected List<ButtonConfigurationBean> loadTableActions() {
        List<ButtonConfigurationBean> actionsList = new ArrayList<>();
        actionsList.add(new ButtonConfigurationBean("icons/navigate_up2.png", null, KeyCode.HOME.getName(), "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
        actionsList.add(new ButtonConfigurationBean("icons/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
        actionsList.add(new ButtonConfigurationBean("icons/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
        actionsList.add(new ButtonConfigurationBean("icons/navigate_down2.png", null, KeyCode.END.getName(), "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        actionsList.add(new ButtonConfigurationBean("icons/navigate_check.png", null, null, "ACCION_TABLA_SELECCIONAR_TODO", "REALIZAR_ACCION")); 
        actionsList.add(new ButtonConfigurationBean("icons/navigate_cross.png", null, null, "ACCION_TABLA_BORRAR_SELECCION", "REALIZAR_ACCION"));
        
        return actionsList;
    }
}
