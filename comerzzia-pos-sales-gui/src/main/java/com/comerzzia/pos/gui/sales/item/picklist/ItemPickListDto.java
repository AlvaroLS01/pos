package com.comerzzia.pos.gui.sales.item.picklist;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.catalog.facade.model.CatalogCategorization;
import com.comerzzia.catalog.facade.model.CatalogItem;
import com.comerzzia.catalog.facade.model.CatalogItemDetail;
import com.comerzzia.catalog.facade.model.filter.PaginatedRequest;
import com.comerzzia.catalog.facade.model.impl.CatalogItemImpl;
import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.pos.core.services.session.ApplicationSession;
import com.comerzzia.pos.util.config.SpringContext;

@Component
@Scope("prototype")
public class ItemPickListDto {
	
	private Logger log = Logger.getLogger(ItemPickListDto.class);

	public static final String ITEM_TYPE = "ARTICULO";

	public static final String CATEGORY_TYPE = "CATEGORIA";

	public static final String GAP_TYPE = "HUECO";

	public static final String FAMILY_TYPE = "FAMILIA";

	public static final String CATEGORIZATION_TYPE = "CATEGORIZACION";
	
	public static final String SECTION_TYPE = "SECCION";
	
	public static final String CONTINUE_BEHAVIOUR = "CONTINUAR";
	
	public static final String MAIN_BEHAVIOUR = "INICIO";
	
	public static final String CLOSE_BEHAVIOUR = "CERRAR";
		
	@Autowired
	protected ApplicationSession applicationSession;

	protected String code;

	protected String description;

	protected String type;

	protected String photo;
	
	protected String behaviour;
	
	protected Boolean weightRequired;

	protected List<ItemPickListDto> subItems;

	protected Integer row;

	protected Integer column;
	
	protected WeakReference<Catalog> catalog;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		if(StringUtils.isBlank(description) && !(type.equals(SECTION_TYPE) || type.equals(FAMILY_TYPE) || type.equals(GAP_TYPE))) {
			log.debug("getDescription() - La descripción del item " + code + " está en blanco. Se intentará buscar en base de datos.");
			description = "";
			try {
				if(type.equals(ITEM_TYPE)) {					
					CatalogItemDetail item = getCatalog().getCatalogItemService().findByBarcode(code);
					
					if(item != null) {
						description = item.getItemDes();
					}
				}
				else {
					CatalogCategorization category = getCatalog().getCatalogCategorizationService().getCategorization(code);
					
					if(category != null) {
						description = category.getCategoryDes();
					}
				}
			}
			catch(Exception e) {
				log.error("getDescription() - No se ha podido cargar la descripción del item con código " + code + ": " + e.getMessage());
			}
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type.equalsIgnoreCase(ItemPickListDto.ITEM_TYPE) || type.equalsIgnoreCase(ItemPickListDto.CATEGORY_TYPE) || type.equalsIgnoreCase(ItemPickListDto.FAMILY_TYPE)
		        || type.equalsIgnoreCase(ItemPickListDto.CATEGORIZATION_TYPE) || type.equalsIgnoreCase(ItemPickListDto.GAP_TYPE) || type.equalsIgnoreCase(ItemPickListDto.SECTION_TYPE)) {
			this.type = type;
		}
		else {
			this.type = ItemPickListDto.ITEM_TYPE;
		}
	}

	public String getPhoto() {
		if (photo == null) {
			if (type.equals(ItemPickListDto.ITEM_TYPE)) {
				return "icons/articulo.png";
			}
			else {
				return "icons/categoria.png";
			}
		}
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public List<ItemPickListDto> getSubItems() {
		if (subItems == null) {
			log.debug("getSubItems() - La lista de subitems del item está vacía. Se va a intentar completar.");
			subItems = new ArrayList<>();

			if (type.equals(ItemPickListDto.CATEGORY_TYPE)) {
				if (StringUtils.isNotBlank(code)) {
					List<CatalogCategorization> childCategorizations = getCatalog().getCatalogCategorizationService().getChildCategories(code);
					
					for (CatalogCategorization childCategorization : childCategorizations) {
						ItemPickListDto subHighlighted = convertCategoryToHighlighted(childCategorization);
						subItems.add(subHighlighted);
					}
				}

				CatalogItemImpl params = new CatalogItemImpl();
				params.setActive(true);
				params.setCategoryCode(code);
				
				List<? extends CatalogItem> categoryItems = getCatalog().getCatalogItemService().findPage(params, new PaginatedRequest(1, 50)).getContent();
				
				for (CatalogItem categoryItem : categoryItems) {
					ItemPickListDto subHighlighted = SpringContext.getBean(ItemPickListDto.class);
					subHighlighted.setCode(categoryItem.getItemCode());
					subHighlighted.setDescription(categoryItem.getItemDes());
					subHighlighted.setType(ItemPickListDto.ITEM_TYPE);
					subHighlighted.setWeightRequired(categoryItem.getWeightRequired());
					
					subItems.add(subHighlighted);
				}
			}
		}
		return subItems;
	}

	protected ItemPickListDto convertCategoryToHighlighted(CatalogCategorization category) {
		ItemPickListDto highlighted = SpringContext.getBean(ItemPickListDto.class);
		highlighted.setCode(category.getCategoryCode());
		highlighted.setDescription(category.getCategoryDes());
		highlighted.setType(ItemPickListDto.CATEGORY_TYPE);
		return highlighted;
	}

	protected Catalog getCatalog() {
		if (catalog == null || catalog.get() == null){
			catalog = new WeakReference<>(applicationSession.getValidCatalog());
		}
		return catalog.get();
	}

	public List<ItemPickListDto> getSubItemsNoSearched() {
		return subItems;
	}

	public void setSubItems(List<ItemPickListDto> subItems) {
		this.subItems = subItems;
	}

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public Integer getColumn() {
		return column;
	}

	public void setColumn(Integer column) {
		this.column = column;
	}

	public String getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}
	
	public Boolean getWeightRequired() {
		return weightRequired == null ? false : weightRequired;
	}

	public void setWeightRequired(Boolean weightRequired) {
		this.weightRequired = weightRequired;
		
		if (weightRequired && StringUtils.isBlank(behaviour)) this.behaviour = ItemPickListDto.CLOSE_BEHAVIOUR;
	}

	public String toString() {
		return code + " -> " + subItems;
	}

}
