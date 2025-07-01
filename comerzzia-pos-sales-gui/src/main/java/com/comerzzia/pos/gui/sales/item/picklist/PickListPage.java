package com.comerzzia.pos.gui.sales.item.picklist;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PickListPage {
	
	protected Integer maxColumns;
	protected Integer maxRows;
	
	protected List<String> breadcrumbs;
	
	protected List<PickListPageItem> items;
	protected PickListPageItem[][] itemsMatrix;
	
	protected Map<String, Object> customData;

}
