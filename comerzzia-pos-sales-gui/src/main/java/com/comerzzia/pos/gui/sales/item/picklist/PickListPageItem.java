package com.comerzzia.pos.gui.sales.item.picklist;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickListPageItem {
	protected String code;
	protected String description;
	protected String type;
	protected String photo;
	protected String behaviour;
	protected Boolean weightRequired;
	protected Integer row;
	protected Integer column;
	protected Map<String, Object> customData;
}
