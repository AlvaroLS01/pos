package com.comerzzia.pos.gui.sales.loyalcustomer.summary;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.model.Collective;
import com.comerzzia.api.loyalty.client.model.LyCustomerCardDetail;
import com.comerzzia.api.loyalty.client.model.LyCustomerContact;
import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.api.loyalty.client.model.LyCustomerFavoriteStore;
import com.comerzzia.api.loyalty.client.model.LyCustomerPurchaseDetail;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerCollectiveRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerController;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerSaleRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.QueryLoyalCustomerLatestSalesTask;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

@Component
public class LoyalCustomerSummaryPanelController extends TabController implements ButtonsGroupController, Initializable{
	
	protected static final Logger log = Logger.getLogger(LoyalCustomerSummaryPanelController.class);
	
	@Autowired
	protected Session session;
	
	@Autowired
    protected VariableServiceFacade variablesService;
	
	@Autowired
	protected LoyalCustomerController loyalCustomerController;
	
	@Getter
	@Setter
	protected LyCustomerDetail lyCustomer;
	
	@FXML
	protected TextField tfCode, tfCardNumber, tfBalance, tfName, tfDocument, tfStoreCode, tfStoreDes, tfEmail, tfMobile, tfDocumentType;
	
	@FXML
	protected DatePicker dpBirthDate;
	
	@FXML
	protected TableView<LoyalCustomerCollectiveRow> tableCollectives;
	
	protected ObservableList<LoyalCustomerCollectiveRow> collectives;
	
	@FXML
	protected TableColumn<LoyalCustomerCollectiveRow, String> tcCollectiveCode, tcCollectiveDes, tcCollectiveType;
	
	@FXML
	protected TableView<LoyalCustomerSaleRow> tableSales;
	
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, String> tcItemSaleSum, tcDescriptionSaleSum, tcCombination1SaleSum, tcCombination2SaleSum, tcStoreCodeSaleSum;
	
	protected ObservableList<LoyalCustomerSaleRow> purchases;
	
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, Date> tcDateSaleSum;
	
	@FXML
	protected TableColumn<LoyalCustomerSaleRow, BigDecimal> tcQuantitySaleSum, tcAmountSaleSum;
	
	protected LyCustomerFavoriteStore favoriteStore;
	
	protected ButtonsGroupComponent buttonsGroupComponent;
	
	@FXML
	protected AnchorPane salesButtonsPanel;
	
	@Override
	public void initializeTab(URL arg0, ResourceBundle arg1) {
		editFields();
		tfBalance.setAlignment(Pos.BASELINE_RIGHT);
	}
	
	@Override
	public void selected(){
		LyCustomerDetail newLyCustomer = loyalCustomerController.getLyCustomer();
		if(newLyCustomer != null && (lyCustomer == null || !lyCustomer.getLyCustomerId().equals(newLyCustomer.getLyCustomerId()))){
			clearForm();
			lyCustomer = newLyCustomer;
			loadGeneralDataSummary(lyCustomer);
			loadCardSummary(lyCustomer);
			loadLastPurchasesSummary(lyCustomer);
		}
	}
		
	public void clearForm(){
		tfCode.clear();
		tfCardNumber.clear();
		tfBalance.clear();
		tfName.clear();
		tfDocument.clear();
		tfStoreCode.clear();
		tfStoreDes.clear();
		tfEmail.clear();
		tfMobile.clear();
		dpBirthDate.clear();
	}
	
	@SuppressWarnings("unchecked")
	protected void editFields(){
		tfCode.setEditable(false);
		tfCardNumber.setEditable(false);
		tfBalance.setEditable(false);
		tfName.setEditable(false);
		tfDocument.setEditable(false);
		tfStoreCode.setEditable(false);
		tfStoreDes.setEditable(false);
		tfEmail.setEditable(false);
		tfMobile.setEditable(false);
		dpBirthDate.setDisable(true);
		tfDocumentType.setEditable(false);
		
		tableCollectives.setPlaceholder(new Text(""));
		
		tcCollectiveCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableCollectives", "tcCollectiveCode", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCollectiveDes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableCollectives", "tcCollectiveDes", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCollectiveType.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableCollectives", "tcCollectiveType", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));

        tcCollectiveCode.setCellValueFactory(new PropertyValueFactory<LoyalCustomerCollectiveRow, String>("collectiveCode"));
        tcCollectiveDes.setCellValueFactory(new PropertyValueFactory<LoyalCustomerCollectiveRow, String>("collectiveDes"));
        tcCollectiveType.setCellValueFactory(new PropertyValueFactory<LoyalCustomerCollectiveRow, String>("collectiveType"));
        
        tableSales.setPlaceholder(new Text(""));

        tcDateSaleSum.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcDateSaleSum", null, CellFactoryBuilder.CENTER_ALIGN_STYLE));
        tcItemSaleSum.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcItemSaleSum", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcDescriptionSaleSum.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcDescriptionSaleSum", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCombination1SaleSum.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcCombination1SaleSum", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCombination2SaleSum.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcCombination2SaleSum", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcQuantitySaleSum.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcQuantitySaleSum", null, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
        tcAmountSaleSum.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcAmountSaleSum", 2, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
        tcStoreCodeSaleSum.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableSales", "tcStoreCodeSaleSum", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));

        tcDateSaleSum.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, Date>("date"));
        tcItemSaleSum.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, String>("item"));
        tcDescriptionSaleSum.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, String>("description"));
        tcCombination1SaleSum.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, String>("combination1"));
        tcCombination2SaleSum.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, String>("combination2"));
        tcQuantitySaleSum.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, BigDecimal>("quantity"));
        tcAmountSaleSum.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, BigDecimal>("amount"));
        tcStoreCodeSaleSum.setCellValueFactory(new PropertyValueFactory<LoyalCustomerSaleRow, String>("storeCode"));
        
        if (session.getApplicationSession().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
        	tcCombination1SaleSum.setText(I18N.getText(variablesService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcCombination1SaleSum.setVisible(false);
			tcDescriptionSaleSum.setPrefWidth(tcDescriptionSaleSum.getPrefWidth() + 54.0);
			
		}
		if (session.getApplicationSession().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
			tcCombination2SaleSum.setText(I18N.getText(variablesService.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcCombination2SaleSum.setVisible(false);
			tcDescriptionSaleSum.setPrefWidth(tcDescriptionSaleSum.getPrefWidth() + 54.0);
		}
	}
	
	protected void loadGeneralDataSummary(LyCustomerDetail lyCustomer){
		log.debug("loadGeneralDataSummary()");
		
		favoriteStore = lyCustomer.getFavoriteStore();

		tfCode.setText(lyCustomer.getLyCustomerCode());
		tfName.setText(lyCustomer.getName() + " " + lyCustomer.getLastName());
		tfDocumentType.setText(lyCustomer.getIdentificationTypeCode());
		dpBirthDate.setValue(lyCustomer.getDateOfBirth());
		
		Optional<LyCustomerContact> email = lyCustomer.getContacts().stream().filter(c -> c.getContactTypeCode().equals("EMAIL")).findFirst();
		Optional<LyCustomerContact> mobile = lyCustomer.getContacts().stream().filter(c -> c.getContactTypeCode().equals("MOVIL")).findFirst();
		if(!isShowSensitiveData()){
			hideSensitiveData(tfDocument, lyCustomer.getVatNumber());
			if(email.isPresent()){
				hideSensitiveData(tfEmail, email.get().getValue());
			}
			if(mobile.isPresent()){
				hideSensitiveData(tfMobile, mobile.get().getValue());
			}
			
		}else{
			tfDocument.setText(lyCustomer.getVatNumber());
			if(email.isPresent()){
				tfEmail.setText(email.get().getValue());
			}
			if(mobile.isPresent()){
				tfMobile.setText(mobile.get().getValue());
			}
		}
		if(favoriteStore != null){
			tfStoreCode.setText(favoriteStore.getStoreCode());
			tfStoreDes.setText(favoriteStore.getStoreDes());
		}
		
		List<LoyalCustomerCollectiveRow> collectivesList = new ArrayList<LoyalCustomerCollectiveRow>();
		for(Collective collective : lyCustomer.getCollectives()){
			if(collective.getType() == null || !collective.getType().getPrivated()){
				LoyalCustomerCollectiveRow collectiveRow = new LoyalCustomerCollectiveRow(collective);
				collectivesList.add(collectiveRow);
			}
		}
		if(isShowCollectives()){			
			collectives = FXCollections.observableArrayList(collectivesList);
		}else{
			collectives = null;
		}
        tableCollectives.setItems(collectives);
	}
	
	protected void loadCardSummary(LyCustomerDetail lyCustomer) {
		List<LyCustomerCardDetail> cards = lyCustomer.getCards();
		Set<Long> accounts = new HashSet<Long>();
		List<LyCustomerCardDetail> loyalCustomerCards = new ArrayList<>();
		BigDecimal accumulatedBalance = BigDecimal.ZERO;
		if (cards != null) {
			for (LyCustomerCardDetail card : cards) {
				if (card.getAccount() != null && !card.getCardType().getPaymentsAllowed()) {
					if (!accounts.contains(card.getAccount().getCardAccountId())) { // Comprobamos que las tarjetas sean de cuentas independientes
						accumulatedBalance = accumulatedBalance.add(card.getAccount().getBalance());
					}
					accounts.add(card.getAccount().getCardAccountId());
				}
				if (card.getCardType().getLinkingAllowed() && !card.getCardType().getPaymentsAllowed() && 
						(card.getDeactivationDate() == null || (card.getDeactivationDate() != null && card.getDeactivationDate().after(new Date())))) {
					loyalCustomerCards.add(card);
				}
			}
			tfBalance.setText(FormatUtils.getInstance().formatNumber(accumulatedBalance, 2));
			if (StringUtils.isNotBlank(loyalCustomerController.getLyCustomerCardNumber())) {
				tfCardNumber.setText(loyalCustomerController.getLyCustomerCardNumber());
			}
			else {
				tfCardNumber.setText("");
			}
		}
	}
	
	protected void loadLastPurchasesSummary(LyCustomerDetail lyCustomer) {
		Date actualDate = new Date();
		Calendar calFrom = Calendar.getInstance();
		calFrom.set(Calendar.YEAR, calFrom.get(Calendar.YEAR)-1);
		Date fromDate = calFrom.getTime();
		QueryLoyalCustomerLatestSalesTask querySalesTask = SpringContext.getBean(QueryLoyalCustomerLatestSalesTask.class, 
				lyCustomer.getLyCustomerId(), fromDate, actualDate,
					new RestBackgroundTask.FailedCallback<List<LyCustomerPurchaseDetail>>() {
						@Override
						public void succeeded(List<LyCustomerPurchaseDetail> result) {
							if(isShowLastPurchases()){
								purchases = FXCollections.observableArrayList();
								for(LyCustomerPurchaseDetail purchaseDetail : result){
									LoyalCustomerSaleRow saleRow = new LoyalCustomerSaleRow(purchaseDetail);
									purchases.add(saleRow);
								}							
							}else{
								purchases = null;
							}
							tableSales.setItems(purchases);
						}
						@Override
						public void failed(Throwable throwable) {
							loyalCustomerController.closeCancel();
						}
					}, loyalCustomerController.getStage());
		querySalesTask.start();
	}
	
	protected void hideSensitiveData(TextField textField, String string) {
		String replace = string.substring(1, string.length()-1);
		String car = replace.replaceAll(".", "*");
		textField.setText(string.replace(replace, car));
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
		
	public boolean isShowSensitiveData() {
		return loyalCustomerController.isShowSensitiveData();
	}
	
	public boolean isShowCollectives(){
		return loyalCustomerController.isShowCollectives();
	}
	
	public boolean isShowLastPurchases(){
		return loyalCustomerController.isShowLastPurchases();
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
