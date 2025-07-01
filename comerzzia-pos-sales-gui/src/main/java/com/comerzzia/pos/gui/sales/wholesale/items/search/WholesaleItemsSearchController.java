package com.comerzzia.pos.gui.sales.wholesale.items.search;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.service.basket.BasketManagerBuilder;
import com.comerzzia.omnichannel.facade.service.basket.wholesale.WholesaleBasketManager;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.item.ItemSearchControllerAbstract;
import com.comerzzia.pos.gui.sales.retail.items.search.ItemSearchResultDto;
import com.comerzzia.pos.util.format.FormatUtils;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TextField;
import javafx.util.Callback;

@Component
@CzzScene
public class WholesaleItemsSearchController extends ItemSearchControllerAbstract<WholesaleBasketManager> {
	
	@FXML
	protected TextField tfPriceWithTaxes;
	
	@Override
	public void initialize() throws InitializeGuiException {
		super.initialize();
		
		tcPrice.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemSearchResultDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ItemSearchResultDto, String> cdf) {
                return cdf.getValue().getPriceProperty();
            }
        });
        tcPromotionPrice.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemSearchResultDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ItemSearchResultDto, String> cdf) {
                return cdf.getValue().getPromotionPriceProperty();
            }
        });
	}
	
	@Override
	public void onSceneOpen() throws InitializeGuiException {
	    super.onSceneOpen();
	    tfPriceWithTaxes.clear();
	    basketManager = BasketManagerBuilder.build(getBasketClass(), session.getApplicationSession().getStorePosBusinessData());
	}
	
	@Override
	public void refreshScreenData() {
	    super.refreshScreenData();
	    tfPriceWithTaxes.clear();
	}
	
	@Override
	protected void selectedLineChanged() {
		ItemSearchResultDto selectedLine = tbItems.getSelectionModel().getSelectedItem();

		clearPromotionPanel();
		
		if (selectedLine == null) {
			return;
		}
		
        tfDetailItemCode.setText(selectedLine.getItemCode());
        tfDetailItemDes.setText(selectedLine.getDescription());
        tfDetailCombination1Code.setText(selectedLine.getCombination1Code());
        tfDetailCombination2Code.setText(selectedLine.getCombination2Code());
        tfPriceWithTaxes.setText(FormatUtils.getInstance().formatAmount(selectedLine.getLine().get().getPriceWithTaxes()));
        
        showPromotionData(selectedLine);
	}

}
