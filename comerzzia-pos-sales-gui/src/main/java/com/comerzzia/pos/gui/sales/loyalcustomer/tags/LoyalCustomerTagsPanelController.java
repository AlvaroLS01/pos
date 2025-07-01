package com.comerzzia.pos.gui.sales.loyalcustomer.tags;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.api.loyalty.client.model.LyCustomerTag;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerController;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;


@Component
public class LoyalCustomerTagsPanelController extends TabController{

	protected static final Logger log = Logger.getLogger(LoyalCustomerTagsPanelController.class);
	
	@Autowired
    protected VariableServiceFacade variablesService;
	
	@Autowired
	protected LoyalCustomerController loyalCustomerController;
	
	@FXML
	@Getter
	protected TextArea taTags;
	
	@Getter
	@Setter
	protected LyCustomerDetail lyCustomer;

	@Override
	public void initializeTab(URL arg0, ResourceBundle arg1) {

	}
	
	@Override
	public void selected(){
		LyCustomerDetail newLyCustomer = loyalCustomerController.getLyCustomer();
		if(newLyCustomer != null && (lyCustomer == null || !lyCustomer.getLyCustomerId().equals(newLyCustomer.getLyCustomerId()))){
			lyCustomer = newLyCustomer;
			List<LyCustomerTag> customerTags = lyCustomer.getTags();			
			Collections.sort(customerTags, new Comparator<LyCustomerTag>(){
				@Override
	            public int compare(LyCustomerTag o1, LyCustomerTag o2) {
	               return o1.getTag().toUpperCase().compareTo(o2.getTag().toUpperCase());
	            }
			});
			
			String auxTag = "";
			Set<String> treeSet = new TreeSet<String>();
			if(!customerTags.isEmpty()){							
				for(LyCustomerTag customerTag : customerTags){
					if(treeSet.add(customerTag.getTag())){						
						auxTag += customerTag.getTag() + ", ";
					}
				}
				String lyCustomerTags = auxTag.substring(0, auxTag.length()-2);
				taTags.setText(lyCustomerTags);
			}
			else{
				taTags.setText("");
			}

		}
	}

	public void clearForm(){
		taTags.clear();
	}

}
