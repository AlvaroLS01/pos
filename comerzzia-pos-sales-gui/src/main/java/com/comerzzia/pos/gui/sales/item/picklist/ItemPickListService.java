package com.comerzzia.pos.gui.sales.item.picklist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.catalog.facade.model.CatalogCategorization;
import com.comerzzia.catalog.facade.model.CatalogItem;
import com.comerzzia.catalog.facade.model.filter.PaginatedRequest;
import com.comerzzia.catalog.facade.model.filter.PaginatedResponse;
import com.comerzzia.catalog.facade.model.impl.CatalogItemImpl;
import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class ItemPickListService {
	protected static final int COLUMN_ITEM_TYPE = 1;
	protected static final int COLUMN_ITEM_CODE = 2;
	protected static final int COLUMN_ITEM_DESCRIPTION = 3;
	protected static final int COLUMN_IMAGE = 4;
	protected static final int COLUMN_BEHAVIOUR = 5;

	protected List<ItemPickListDto> items;

	protected static final String DEFINITION_SHEET = "DEFINICIÓN";
	protected static final String MAIN_PANEL_SEPARATOR = ";";

	protected static final String XLSX_EXTENSION = ".xlsx";
	protected static final String XLS_EXTENSION = ".xls";

	protected Boolean visorActive;
	protected String defaultBehaviour;
	protected String mainPanelName;
	
	@Autowired
	protected Session session;

	public List<ItemPickListDto> getItems() {
		if (items == null) {
			loadItems();
		}

		return items;
	}

	public void setItems(List<ItemPickListDto> items) {
		this.items = items;
	}

	public Boolean isVisorActive() {
		return visorActive;
	}

	public String getDefaultBehaviour() {
		return defaultBehaviour;
	}

	public void setDefaultBehaviour(String defaultBehaviour) {
		this.defaultBehaviour = defaultBehaviour;
	}

	public void setVisorActive(Boolean visorActive) {
		this.visorActive = visorActive;
	}
	
	public void setMainPanelName(String mainPanelName) {
		this.mainPanelName = mainPanelName;
	}
	
	public String getMainPanelName() {
		return mainPanelName;
	}

	protected void loadItems() {
		try {
			Workbook excel = getExcelTemplate();

			if (excel != null) {
				loadFromExcel(excel);
			} else {
				loadFromCatalog();
			}
		} catch (Exception e) {
			log.error("loadItems() - No se ha podido cargar el Excel de los paneles de venta: " + e.getMessage(), e);
		}
	}
	
	protected void loadFromCatalog() {
		log.debug("Loading from catalog");
		Catalog catalog = session.getApplicationSession().getValidCatalog();
		this.defaultBehaviour = ItemPickListDto.CLOSE_BEHAVIOUR;
		
		List<CatalogCategorization> parentCategories = catalog.getCatalogCategorizationService().getParentCategories();
		
		items = new ArrayList<>();
		
		for (CatalogCategorization category : parentCategories) {
			ItemPickListDto item = convertCategoryToItem(category);
			items.add(item);
		}		
	}
	
	protected void loadFromExcel(Workbook excel) {
		log.debug("Loading from excel");
		
		// load definition parameters
		Sheet definitionSheet = excel.getSheet(DEFINITION_SHEET);
		this.defaultBehaviour = getDefaultBehaviourFromSheet(definitionSheet);
		this.visorActive = getVisorSelectedFromSheet(definitionSheet);
		this.mainPanelName = getMainPanelFromSheet(definitionSheet);

		// Se carga la primera hoja y el resto en recursivo
		this.items = processSheet(excel, excel.getSheet(mainPanelName));
	}

	protected String getDefaultBehaviourFromSheet(Sheet definitionSheet) {
		String value = getDefinitionValue(definitionSheet, "COMPORTAMIENTO_PANEL");
				
		if (StringUtils.equalsIgnoreCase(value, ItemPickListDto.CONTINUE_BEHAVIOUR) ||
			StringUtils.equalsIgnoreCase(value, ItemPickListDto.MAIN_BEHAVIOUR)) {
			return value;
		} else {
			return ItemPickListDto.CLOSE_BEHAVIOUR;
		}
	}

	protected Boolean getVisorSelectedFromSheet(Sheet definitionSheet) {		
		String value = getDefinitionValue(definitionSheet, "VISOR_VISIBLE");
		
		return StringUtils.equalsIgnoreCase(value, "S") || StringUtils.equalsIgnoreCase(value, "Y");
	}
	
	protected String getMainPanelFromSheet(Sheet definitionSheet) {
		String value = getDefinitionValue(definitionSheet, "MAIN_PANEL");		
		
		if (StringUtils.isNotBlank(value)) {
			return value;
		}
		
		return "INICIO";
	}

	protected ItemPickListDto convertCategoryToItem(CatalogCategorization category) {
		ItemPickListDto item = SpringContext.getBean(ItemPickListDto.class);
		item.setCode(category.getCategoryCode());
		item.setDescription(category.getCategoryDes());
		item.setType(ItemPickListDto.CATEGORY_TYPE);
		return item;
	}

	protected Workbook getExcelTemplate() throws InvalidFormatException, IOException {
		Workbook workbook = null;

		File file = getExcelTemplate(XLSX_EXTENSION);
		if (file != null) {
			workbook = new XSSFWorkbook(file);
		}
		else {
			file = getExcelTemplate(XLS_EXTENSION);
			if (file != null) {
				workbook = new HSSFWorkbook(new FileInputStream(file));
			}
		}

		if (workbook != null) {
			log.info("loadItems() - Cargando paneles de archivo: " + file.getAbsolutePath());
		}

		return workbook;
	}

	protected List<String> getValidFileNames(String extension) {
		String storeCode = session.getApplicationSession().getCodAlmacen();
		String department = session.getApplicationSession().getStorePosBusinessData().getStorePos().getDepartment();
		String language = AppConfig.getCurrentConfiguration().getLanguage();
		
		List<String> fileNames= new LinkedList<>();
		
		if (StringUtils.isNotBlank(department)) {
			fileNames.add(MessageFormat.format("{0}_{1}_{2}{3}", storeCode, department, language, extension));
        }
		
		fileNames.add(MessageFormat.format("{0}_{1}{2}", storeCode, language, extension));
        fileNames.add(MessageFormat.format("{0}{1}", storeCode, extension));
        
        if (StringUtils.isNotBlank(department)) {
	        fileNames.add(MessageFormat.format("manual_selection_panel_{0}_{1}{2}", department, language, extension));
	        fileNames.add(MessageFormat.format("manual_selection_panel_{0}{1}", department, extension));
        }
        
        fileNames.add(MessageFormat.format("manual_selection_panel_{0}{1}", language, extension));        
        fileNames.add(MessageFormat.format("manual_selection_panel{0}", extension));
        
        return fileNames;
	}
	
	protected File getExcelTemplate(String extension) {
		List<String> fileNames= getValidFileNames(extension);
		
		for (String fileName : fileNames) {
			File file = searchFile(fileName);
			
			if (file != null) {
				return file;
			}
		}

		return null;
	}

	protected File searchFile(String nombreFicheroExcel) {
		File file = null;
		try {
			
			URL url = Thread.currentThread().getContextClassLoader().getResource(nombreFicheroExcel);
			if(url != null) {
				file = new File(url.toURI());
			}
		}
		catch (URISyntaxException e) {
            log.error("searchFile() - Error buscando el fichero excel: " + e.getMessage(), e);
        }
		return file;
	}

	protected List<ItemPickListDto> processSheet(Workbook workbook, Sheet sheet) {
		log.info("processSheet() - Procesando hoja con nombre: " + sheet.getSheetName());

		List<ItemPickListDto> sheetItems = new ArrayList<>();
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();

		Map<Integer, Integer> columnCount = new HashMap<>();

		int i = 0;
		while (rowIterator.hasNext()) {
			i++;
			Row row = rowIterator.next();
			
			if (isEmptyRow(row)) {
				continue;
			}

			try {
				ItemPickListDto item = SpringContext.getBean(ItemPickListDto.class);

				setItemPickListCommonData(row, columnCount, item);

				if (ItemPickListDto.ITEM_TYPE.equals(item.getType()) && StringUtils.isNotBlank(item.getCode())) {
					setItemTypeData(item, getNullSafeStringCellValue(row, 5));
				}

				if (ItemPickListDto.CATEGORY_TYPE.equals(item.getType()) && StringUtils.isNotBlank(item.getCode())) {
					loadCategoryItems(item, workbook);
				}

				if ((ItemPickListDto.FAMILY_TYPE.equals(item.getType()) || ItemPickListDto.CATEGORIZATION_TYPE.equals(item.getType()) || ItemPickListDto.SECTION_TYPE.equals(item.getType()))
				        && StringUtils.isNotBlank(item.getCode())) {
					loadItemsFromCatalog(item);
				}

				sheetItems.add(item);

			} catch (Exception e) {
				log.error("processSheet() - Error cargando hoja " + sheet.getSheetName() + " fila " + i + ":" + e.getMessage());
			}
		}

		return sheetItems;
	}

	protected boolean isEmptyRow(Row row) {
		return row.getCell(0) == null || ((int) row.getCell(0).getNumericCellValue()) == 0;
	}

	protected void setItemPickListCommonData(Row row, Map<Integer, Integer> columnCount, ItemPickListDto item) {
		Integer rowNumber = (int) row.getCell(0).getNumericCellValue();
		item.setRow(rowNumber - 1);

		Integer column = columnCount.get(rowNumber) == null ? 0 : columnCount.get(rowNumber) + 1;
		columnCount.put(rowNumber, column);
		item.setColumn(column);

		String codedType = ItemPickListDto.ITEM_TYPE;
		String type = getNullSafeStringCellValue(row, COLUMN_ITEM_TYPE);
		if (StringUtils.isNotBlank(type) && (type.equals(ItemPickListDto.CATEGORY_TYPE) || type.equals(ItemPickListDto.ITEM_TYPE) || type.equals(ItemPickListDto.GAP_TYPE)
		        || type.equals(ItemPickListDto.FAMILY_TYPE) || type.equals(ItemPickListDto.CATEGORIZATION_TYPE) || type.equals(ItemPickListDto.SECTION_TYPE))) {
			codedType = type;
		}
		item.setType(codedType);

		String code = getNullSafeStringCellValue(row, COLUMN_ITEM_CODE);
		if (StringUtils.isNotBlank(code)) {
			item.setCode(code);
		}

		String description = getNullSafeStringCellValue(row, COLUMN_ITEM_DESCRIPTION);
		if (StringUtils.isNotBlank(description)) {
			item.setDescription(description);
		}

		String image = getNullSafeStringCellValue(row, COLUMN_IMAGE);
		if (StringUtils.isNotBlank(image)) {
			if (!isValidURL(image)) {
				item.setPhoto("paneles/" + image);
			}
			else {
				item.setPhoto(image);
			}
		}
		
		String behaviour = getNullSafeStringCellValue(row, COLUMN_BEHAVIOUR);

		setItemTypeData(item, behaviour);
	}

	protected void setItemTypeData(ItemPickListDto item, String behaviour) {
		if (StringUtils.isNotBlank(behaviour)
		        && (behaviour.equals(ItemPickListDto.CONTINUE_BEHAVIOUR) || behaviour.equals(ItemPickListDto.MAIN_BEHAVIOUR) || behaviour.equals(ItemPickListDto.CLOSE_BEHAVIOUR))) {
			item.setBehaviour(behaviour);
		}
		else {
			item.setBehaviour(defaultBehaviour);
		}
	}

	protected void loadCategoryItems(ItemPickListDto item, Workbook workbook) {
		Iterator<Sheet> sheetIterator = workbook.iterator();
		while (sheetIterator.hasNext()) {
			Sheet nextSheet = sheetIterator.next();
			String sheetName = nextSheet.getSheetName();
			String[] sheetNameSplit = sheetName.split("-");

			if (sheetNameSplit.length > 0 && StringUtils.equalsIgnoreCase(sheetNameSplit[0].trim(), item.getCode())) {
				item.setSubItems(processSheet(workbook, nextSheet));
				break;
			}
		}
	}

	protected void loadItemsFromCatalog(ItemPickListDto item) {
		CatalogItemImpl itemsFilter = new CatalogItemImpl();
		itemsFilter.setActive(true);

		if (ItemPickListDto.FAMILY_TYPE.equals(item.getType())) {
			if (item.getCode().contains(";")) {
				throw new IllegalArgumentException("not supported yet");
			}
			else {
				itemsFilter.setFamilyCode(item.getCode());
			}
		}
		else if (ItemPickListDto.SECTION_TYPE.equals(item.getType())) {
			itemsFilter.setSectionCode(item.getCode());
		}
		else {
			itemsFilter.setCategoryCode(item.getCode());
		}

		Catalog catalog = session.getApplicationSession().getValidCatalog();

		PaginatedResponse<? extends CatalogItem> familyItemsList = catalog.getCatalogItemService().findPage(itemsFilter, new PaginatedRequest(1, 300, "itemDes"));

		if (familyItemsList.getTotalElements() > 0) {
			item.setSubItems(transformItemsPanel(familyItemsList.getContent(), item.getType()));
		}
	}

	protected boolean isValidURL(String url) {
		try {
			URL validUrl = new URL(url);
			return !validUrl.getProtocol().isEmpty();
		} catch (MalformedURLException ignore) {
			// ignore exception
		}
		return false;
	}

	public boolean hasDefinedPositions(List<ItemPickListDto> items) {
		for (ItemPickListDto item : items) {
			if (item.getColumn() != null && item.getRow() != null) {
				return true;
			}
		}
		return false;
	}

	protected List<ItemPickListDto> transformItemsPanel(List<? extends CatalogItem> itemList, String codedType) { //NOSONAR
		List<ItemPickListDto> panelList = new ArrayList<>();
		for (CatalogItem item : itemList) {
			ItemPickListDto itemPanel = new ItemPickListDto();

			itemPanel.setType(ItemPickListDto.ITEM_TYPE);
			itemPanel.setCode(item.getItemCode());
			itemPanel.setDescription(item.getItemDes());
			itemPanel.setBehaviour(this.defaultBehaviour);
			itemPanel.setPhoto("paneles/" + item.getItemCode() + ".png");
			itemPanel.setWeightRequired(item.getWeightRequired());

			panelList.add(itemPanel);
		}

		return panelList;
	}
	
	public List<ItemPickListDto> getMainPanel(){
		return getBreadCrumb(getItems(), getMainPanelBreadcrumbCodes(), new ArrayList<>());
	}
	
	public List<ItemPickListDto> getBreadCrumb(List<ItemPickListDto> subList, List<String> strBreadcrumb, List<ItemPickListDto> breadCrumb) {
		if (strBreadcrumb.isEmpty()) {
			return breadCrumb;
		}
		
		ItemPickListDto item = getItemByCode(strBreadcrumb.get(0), subList);
		
		if (item == null) {
			return breadCrumb;
		}
		
		breadCrumb.add(item);
		if (strBreadcrumb.size() <= 1) {
			return breadCrumb;
		}
		
		return getBreadCrumb(item.getSubItems(), strBreadcrumb.subList(1, strBreadcrumb.size()), breadCrumb);
	}

	protected ItemPickListDto getItemByCode(String code, List<ItemPickListDto> subItems) {
		return subItems.stream().filter(item -> StringUtils.equals(code, item.getCode())).findFirst().orElse(null);
	}
	
	protected List<String> getMainPanelBreadcrumbCodes() {
		return StringUtils.isBlank(mainPanelName) ? new ArrayList<>() : Arrays.asList(mainPanelName);
	}
	
	protected String getNullSafeStringCellValue(Sheet sheet, int rownum, int cellnum) {
		return getNullSafeStringCellValue(sheet.getRow(rownum), cellnum);
	}

	protected String getNullSafeStringCellValue(Row row, int cellnum) {
		String cellValue = null;

		if (row != null) {
			Cell cell = row.getCell(cellnum);
			if (cell != null) {
				if (cell.getCellType().equals(CellType.STRING)) {
					cellValue = cell.getStringCellValue();
				}
				else if (cell.getCellType().equals(CellType.NUMERIC)) {
					cellValue = String.valueOf((int) cell.getNumericCellValue());
				}
			}
		}

		return StringUtils.trim(cellValue);
	}
	
	protected String getDefinitionValue(Sheet sheet, String key) {
		if (sheet == null) return null;
		
		Iterator<Row> rowIterator = sheet.iterator();

		rowIterator.next();
		
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
						
			if (StringUtils.equalsIgnoreCase(getNullSafeStringCellValue(row, 0), key)) {
				return getNullSafeStringCellValue(row, 1);
			}
		}
				
		return null;
	}

}
