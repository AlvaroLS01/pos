package com.comerzzia.pos.gui.sales.loyalcustomer.collectives;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.model.Collective;
import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerCollectiveRow;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;

@Component
public class LoyalCustomerCollectivesPanelController extends TabController {
	
	protected static final Logger log = Logger.getLogger(LoyalCustomerCollectivesPanelController.class);
	
	@Autowired
	protected LoyalCustomerController loyalCustomerController;
	
	@FXML
	protected TableView<LoyalCustomerCollectiveRow> tableCollectives;
	
	protected ObservableList<LoyalCustomerCollectiveRow> collectives;
	
	@Getter
	@Setter
	protected LyCustomerDetail lyCustomer;
	
	@FXML
	protected TableColumn<LoyalCustomerCollectiveRow, String> tcCollectiveCode, tcCollectiveDes, tcCollectiveType;

	@SuppressWarnings("unchecked")
	@Override
	public void initializeTab(URL arg0, ResourceBundle arg1) {
		tableCollectives.setPlaceholder(new Text(""));
		
		tcCollectiveCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableCollectives", "tcCollectiveCode", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCollectiveDes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableCollectives", "tcCollectiveDes", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCollectiveType.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableCollectives", "tcCollectiveType", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));

        tcCollectiveCode.setCellValueFactory(new PropertyValueFactory<LoyalCustomerCollectiveRow, String>("collectiveCode"));
        tcCollectiveDes.setCellValueFactory(new PropertyValueFactory<LoyalCustomerCollectiveRow, String>("collectiveDes"));
        tcCollectiveType.setCellValueFactory(new PropertyValueFactory<LoyalCustomerCollectiveRow, String>("collectiveType"));
	}
	
	@Override
	public void selected(){
		LyCustomerDetail newLoyalCustomer = loyalCustomerController.getLyCustomer();
		if(newLoyalCustomer != null && (lyCustomer == null || !lyCustomer.getLyCustomerId().equals(newLoyalCustomer.getLyCustomerId()))){
			lyCustomer = newLoyalCustomer;
			loadCollectives(lyCustomer);
		}
	}
	
	protected void loadCollectives(LyCustomerDetail lyCustomer){
		List<LoyalCustomerCollectiveRow> collectivesRow = new ArrayList<LoyalCustomerCollectiveRow>();
		for(Collective collective : lyCustomer.getCollectives()){
			if(collective.getType() == null || !collective.getType().getPrivated()){
				LoyalCustomerCollectiveRow collectiveRow = new LoyalCustomerCollectiveRow(collective);
				collectivesRow.add(collectiveRow);
			}
		}
		
		collectives = FXCollections.observableArrayList(collectivesRow);
        tableCollectives.setItems(collectives);
	}

}
