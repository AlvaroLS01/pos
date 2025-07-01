package com.comerzzia.pos.gui.sales.loyalcustomer.latestsales;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.api.loyalty.client.model.LyCustomerPurchaseDetail;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerController;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerSaleRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.QueryLoyalCustomerLatestSalesTask;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

@Component
public class LoyalCustomerLatestSalesPanelController extends TabController implements ButtonsGroupController, Initializable{
	
	protected static final Logger log = Logger.getLogger(LoyalCustomerLatestSalesPanelController.class);
	
	@Autowired
	protected Session session;
	
	@Autowired
    protected VariableServiceFacade variablesService;
	
	@Autowired
	protected LoyalCustomerController loyalCustomerController;
	
	@FXML
	protected TableView<LoyalCustomerSaleRow> tableSales;
	
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, Date> tcSaleDate;
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, String> tcSaleItem;
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, String> tcSaleDescription;
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, String> tcSaleCombination1;
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, String> tcSaleCombination2;
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, BigDecimal> tcSaleQuantity;
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, BigDecimal> tcSaleAmount;
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, String> tcSaleStoreCode;
	
	@Getter
	@Setter
	protected LyCustomerDetail lyCustomer;
	
	protected ObservableList<LoyalCustomerSaleRow> purchases;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initializeTab(URL arg0, ResourceBundle arg1) {
		tableSales.setPlaceholder(new Text(""));
		
		tcSaleDate.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcSaleDate", null, CellFactoryBuilder.CENTER_ALIGN_STYLE));
		tcSaleItem.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcSaleItem", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcSaleDescription.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcSaleDescription", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcSaleCombination1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcSaleCombination1", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcSaleCombination2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcSaleCombination2", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcSaleQuantity.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcSaleQuantity", null, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcSaleAmount.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcSaleAmount", null, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcSaleStoreCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcSaleStoreCode", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));

		tcSaleDate.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, Date>("date"));
		tcSaleItem.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, String>("item"));
		tcSaleDescription.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, String>("description"));
		tcSaleCombination1.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, String>("combination1"));
		tcSaleCombination2.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, String>("combination2"));
		tcSaleQuantity.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, BigDecimal>("quantity"));
		tcSaleAmount.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, BigDecimal>("amount"));
		tcSaleStoreCode.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, String>("storeCode"));
		
		purchases = FXCollections.observableArrayList();
		
		if (session.getApplicationSession().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
			tcSaleCombination1.setText(I18N.getText(variablesService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcSaleCombination1.setVisible(false);
			tcSaleDescription.setPrefWidth(tcSaleDescription.getPrefWidth() + 70.0);
		}
		if (session.getApplicationSession().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
			tcSaleCombination2.setText(I18N.getText(variablesService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcSaleCombination2.setVisible(false);
			tcSaleDescription.setPrefWidth(tcSaleDescription.getPrefWidth() + 70.0);
		}
		
	}
	
	@Override
	public void selected(){
		LyCustomerDetail newLyCustomer = loyalCustomerController.getLyCustomer();
		if(newLyCustomer != null && (lyCustomer == null || !lyCustomer.getLyCustomerId().equals(newLyCustomer.getLyCustomerId()))){
			lyCustomer = newLyCustomer;
			loadPurchases(lyCustomer);
		}
	}
	
	protected void loadPurchases(LyCustomerDetail lyCustomer){
		Date now = new Date();
		Calendar calFrom = Calendar.getInstance();
		calFrom.set(Calendar.YEAR, calFrom.get(Calendar.YEAR)-1);
		Date dateFrom = calFrom.getTime();
		QueryLoyalCustomerLatestSalesTask queryLatestSalesTask = SpringContext.getBean(QueryLoyalCustomerLatestSalesTask.class, 
				lyCustomer.getLyCustomerId(), dateFrom, now, 
					new RestBackgroundTask.FailedCallback<List<LyCustomerPurchaseDetail>>() {
						@Override
						public void succeeded(List<LyCustomerPurchaseDetail> result) {						
							purchases.clear();
							for(LyCustomerPurchaseDetail purchase : result){
								LoyalCustomerSaleRow purchaseRow = new LoyalCustomerSaleRow(purchase);
								purchases.add(purchaseRow);
							}
							tableSales.setItems(purchases);
						}
						@Override
						public void failed(Throwable throwable) {
							loyalCustomerController.closeCancel();
						}
					}, loyalCustomerController.getStage());
		queryLatestSalesTask.start();
	}

	@Override
	public Stage getStage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeAction(ActionButtonComponent pressedButton) {
		log.debug("executeAction() - Realizando la acción : " + pressedButton.getClave() + " de tipo : " + pressedButton.getTipo());
		switch (pressedButton.getClave()) {
			case "ACCION_TABLA_PRIMER_REGISTRO_VENTAS":
				loyalCustomerController.actionTableEventFirst(tableSales);
				break;
			case "ACCION_TABLA_ANTERIOR_REGISTRO_VENTAS":
				loyalCustomerController.actionTableEventPrevious(tableSales);
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO_VENTAS":
				loyalCustomerController.actionTableEventNext(tableSales);
				break;
			case "ACCION_TABLA_ULTIMO_REGISTRO_VENTAS":
				loyalCustomerController.actionTableEventLast(tableSales);
				break;
			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + pressedButton.getClave());
		}
	}

	@Override
	public void checkOperationPermissions(String operation) throws PermissionDeniedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addKeyEventHandler(EventHandler<KeyEvent> keyEventHandler, EventType<KeyEvent> keyEventType) {
		
	}

	@Override
	public void removeKeyEventHandler(EventHandler<KeyEvent> keyEventHandler, EventType<KeyEvent> keyEventType) {
		
	}

	@Override
	public void openActionScene(Long actionId) {
	}

}
