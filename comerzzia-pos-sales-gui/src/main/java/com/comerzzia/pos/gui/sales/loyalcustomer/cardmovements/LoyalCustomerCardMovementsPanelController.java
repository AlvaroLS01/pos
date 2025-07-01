package com.comerzzia.pos.gui.sales.loyalcustomer.cardmovements;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.model.AccountTransaction;
import com.comerzzia.api.loyalty.client.model.LyCustomerCardDetail;
import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerCardMovementRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerController;
import com.comerzzia.pos.gui.sales.loyalcustomer.QueryCardMovementsTask;
import com.comerzzia.pos.gui.sales.loyalcustomer.QueryLoyalCustomerCardsTask;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

@Component
public class LoyalCustomerCardMovementsPanelController extends TabController implements Initializable, ButtonsGroupController{
	
	protected static final Logger log = Logger.getLogger(LoyalCustomerCardMovementsPanelController.class);
	
	public static final String TABLE_NAME_CARD_TRANSACTIONS = "tableCardTransactions";

	@Autowired
    protected VariableServiceFacade variablesService;
	
	@Autowired
	protected LoyalCustomerController loyalCustomerController;
	
	@FXML
	protected ComboBox<LyCustomerCardDetail> cbCardNumber;
	
	@FXML
	protected TextField tfCardType, tfBalance;
	
	@FXML
	protected TableView<LoyalCustomerCardMovementRow> tableCardTransactions;
	
	@FXML
	protected TableColumn<LoyalCustomerCardMovementRow, String> tcConcept;
	
	@FXML
	protected TableColumn<LoyalCustomerCardMovementRow, String> tcTransactionStatus;
	
	@FXML
	protected TableColumn<LoyalCustomerCardMovementRow, Date> tcDate;
	
	@FXML
	protected TableColumn<LoyalCustomerCardMovementRow, BigDecimal> tcInput, tcOutput;
	
	@FXML
	protected Label lbTotalInput, lbTotalOutput;
	
	@Getter
	@Setter
	protected LyCustomerDetail lyCustomer;
	
	protected ObservableList<LyCustomerCardDetail> lyCustomerCards;
	
	protected ObservableList<LoyalCustomerCardMovementRow> cardTransactions;
	

	@SuppressWarnings("unchecked")
	@Override
	public void initializeTab(URL arg0, ResourceBundle arg1) {
		tableCardTransactions.setPlaceholder(new Text(""));
		
		tcDate.setCellFactory(CellFactoryBuilder.createCellRendererCelda(TABLE_NAME_CARD_TRANSACTIONS, "tcDate", null, CellFactoryBuilder.CENTER_ALIGN_STYLE));
		tcConcept.setCellFactory(CellFactoryBuilder.createCellRendererCelda(TABLE_NAME_CARD_TRANSACTIONS, "tcConcept", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcTransactionStatus.setCellFactory(CellFactoryBuilder.createCellRendererCelda(TABLE_NAME_CARD_TRANSACTIONS, "tcTransactionStatus", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcInput.setCellFactory(CellFactoryBuilder.createCellRendererCelda(TABLE_NAME_CARD_TRANSACTIONS, "tcInput", 2, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcOutput.setCellFactory(CellFactoryBuilder.createCellRendererCelda(TABLE_NAME_CARD_TRANSACTIONS, "tcOutput", 2, CellFactoryBuilder.RIGHT_ALIGN_STYLE));

		tcDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		tcConcept.setCellValueFactory(new PropertyValueFactory<>("concept"));
		tcTransactionStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		tcInput.setCellValueFactory(new PropertyValueFactory<>("input"));
		tcOutput.setCellValueFactory(new PropertyValueFactory<>("output"));

		cbCardNumber.setConverter(new StringConverter<LyCustomerCardDetail>(){

			@Override
			public String toString(LyCustomerCardDetail object) {
				return object != null ? object.getCardNumber() : null;
			}

			@Override
			public LyCustomerCardDetail fromString(String string) {
				return null;
			}

		});
		lyCustomerCards = FXCollections.observableArrayList();
		cbCardNumber.setItems(lyCustomerCards);
		
		cardTransactions = FXCollections.observableArrayList();
		
		tfBalance.setEditable(false);
		tfCardType.setEditable(false);
		
		tfBalance.setAlignment(Pos.CENTER_RIGHT);
		lbTotalInput.setAlignment(Pos.BASELINE_RIGHT);
		lbTotalOutput.setAlignment(Pos.BASELINE_RIGHT);
	}
	
	@Override
	public void selected(){
		LyCustomerDetail newLoyalCustomer = loyalCustomerController.getLyCustomer();
		if(newLoyalCustomer != null && (lyCustomer == null || !lyCustomer.getLyCustomerId().equals(newLoyalCustomer.getLyCustomerId()))){
			lyCustomer = newLoyalCustomer;
			loadCards(lyCustomer);
		}
	}
	
	public void clearForm(){
		tfBalance.clear();
		tfCardType.clear();
	}
	
	protected void loadCards(LyCustomerDetail lyCustomer){
		cardTransactions.clear();
		//TODO Incluir en consulta filtro por linkingAllowed
		QueryLoyalCustomerCardsTask queryCardsTask = SpringContext.getBean(QueryLoyalCustomerCardsTask.class, 
					lyCustomer.getLyCustomerId(), 
					new RestBackgroundTask.FailedCallback<List<LyCustomerCardDetail>>() {
						@Override
						public void succeeded(List<LyCustomerCardDetail> result) {						
							lyCustomerCards.clear();
							result = result.stream().filter(card -> card.getCardType().getLinkingAllowed()).collect(Collectors.toList());
							lyCustomerCards.addAll(result);
							cbCardNumber.setItems(lyCustomerCards);
							
							String lyCustomerCardNumber = loyalCustomerController.getLyCustomerCardNumber();
							for(LyCustomerCardDetail card : cbCardNumber.getItems()){
								if(lyCustomerCardNumber.equals(card.getCardNumber())){
									cbCardNumber.getSelectionModel().select(card);
									break;
								}
							}
						}
						@Override
						public void failed(Throwable throwable) {
							loyalCustomerController.closeCancel();
						}
					}, loyalCustomerController.getStage());
		queryCardsTask.start();
	}
	
	@FXML
	public void loadTransactions(){
		LyCustomerCardDetail selectedCard = cbCardNumber.getValue();
		if(selectedCard != null){
			tfCardType.setText(selectedCard.getCardType().getCardTypeDes());
			
			BigDecimal cardBalance = BigDecimal.ZERO;
			
			if (selectedCard.getAccount() != null) {
				cardBalance = selectedCard.getAccount().getBalance().add(selectedCard.getAccount().getProvisionalBalance());
				
				QueryCardMovementsTask queryTransactionsTask = SpringContext.getBean(QueryCardMovementsTask.class, 
						selectedCard.getAccount().getCardAccountId(), selectedCard.getCardId(), 20, 
						new RestBackgroundTask.FailedCallback<List<AccountTransaction>>() {
							@Override
							public void succeeded(List<AccountTransaction> result) {						
								cardTransactions.clear();
								BigDecimal totalInput = BigDecimal.ZERO;
								BigDecimal totalOutput = BigDecimal.ZERO;
								for(AccountTransaction transaction : result){
									LoyalCustomerCardMovementRow transactionRow = new LoyalCustomerCardMovementRow(transaction);
									if(transactionRow.getInput() != null){
										totalInput = totalInput.add(transactionRow.getInput());
									}
									if(transactionRow.getOutput() != null){
										totalOutput = totalOutput.add(transactionRow.getOutput());
									}
									cardTransactions.add(transactionRow);
								}
								tableCardTransactions.setItems(cardTransactions);
								lbTotalInput.setText(FormatUtils.getInstance().formatNumber(totalInput, 2));
								lbTotalOutput.setText(FormatUtils.getInstance().formatNumber(totalOutput, 2));
							}
							@Override
							public void failed(Throwable throwable) {
								loyalCustomerController.closeCancel();
							}
						}, loyalCustomerController.getStage());
				queryTransactionsTask.start();
			}
			else {
				lbTotalInput.setText(FormatUtils.getInstance().formatNumber(BigDecimal.ZERO, 2));
				lbTotalOutput.setText(FormatUtils.getInstance().formatNumber(BigDecimal.ZERO, 2));
			}
			
			tfBalance.setText(FormatUtils.getInstance().formatNumber(cardBalance, 2));
		}
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
			case "ACCION_TABLA_PRIMER_REGISTRO_MOVIMIENTOS":
				loyalCustomerController.actionTableEventFirst(tableCardTransactions);
				break;
			case "ACCION_TABLA_ANTERIOR_REGISTRO_MOVIMIENTOS":
				loyalCustomerController.actionTableEventPrevious(tableCardTransactions);
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO_MOVIMIENTOS":
				loyalCustomerController.actionTableEventNext(tableCardTransactions);
				break;
			case "ACCION_TABLA_ULTIMO_REGISTRO_MOVIMIENTOS":
				loyalCustomerController.actionTableEventLast(tableCardTransactions);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeKeyEventHandler(EventHandler<KeyEvent> keyEventHandler, EventType<KeyEvent> keyEventType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openActionScene(Long actionId) {
		// TODO Auto-generated method stub
		
	}

}
