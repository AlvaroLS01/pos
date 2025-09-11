package com.comerzzia.pos.services.ventas.paneles;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.ArticuloExample;
import com.comerzzia.pos.persistence.articulos.categorizaciones.CategorizacionBean;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.ArticulosServiceException;
import com.comerzzia.pos.services.articulos.categorizaciones.CategorizacionesService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

@Component
public class ArticulosPanelesService {

	private Logger log = Logger.getLogger(ArticulosPanelesService.class);
	
	protected List<ItemPanelDto> articulosVenta;

	@Autowired
	protected CategorizacionesService categorizacionesService;

	@Autowired
	private ArticulosService articulosService;
	
	private static final String DEFINITION_COLUMN = "DEFINICIÓN";
	
	private Boolean visorActive;
	private String defaultBehaviour;

	@Autowired
	private Sesion sesion;

	public List<ItemPanelDto> getArticulosVenta() {
		if (articulosVenta == null) {
			cargarArticulosVenta();
		}

		return articulosVenta;
	}

	public void setArticulosVenta(List<ItemPanelDto> articulosVenta) {
		this.articulosVenta = articulosVenta;
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

	protected void cargarArticulosVenta() {
		try {
			File file = getPlantillaExcel();

			if (file != null) {
				log.info("cargarArticulosVenta() - Cargando paneles de archivo: " + file.getAbsolutePath());

				WorkbookSettings settings = new WorkbookSettings();
				settings.setEncoding("Cp1252");
				settings.setCellValidationDisabled(true);

				Workbook excel = Workbook.getWorkbook(file, settings);

				Sheet[] sheets = excel.getSheets();
				
				setDefaultBehaviour(getDefaultBehaviour(sheets));
				setVisorActive(visorSelected(sheets));

				// Se carga la primera hoja y el resto en recursivo
				articulosVenta = procesarHoja(sheets, sheets[0]);
			}
			else {
				log.debug("cargarArticulosVenta() - No se ha encontrado plantilla excel para los paneles");
				List<CategorizacionBean> categoriasPadre = categorizacionesService.consultarCategorias();
				articulosVenta = new ArrayList<ItemPanelDto>();
				for (CategorizacionBean categoria : categoriasPadre) {
					ItemPanelDto item = convertirCategoriaAItem(categoria);
					articulosVenta.add(item);
				}
			}
		}
		catch (Exception e) {
			log.error("cargarArticulosVenta() - No se ha podido cargar el Excel de los paneles de venta: " + e.getMessage(), e);
		}
	}
	
	private Sheet getDefinitionColumn(Sheet[] sheets) {
		for (int i = 0; i < sheets.length; i++) {
			Sheet selectedSheet = sheets[i];
			if(selectedSheet.getName().equals(DEFINITION_COLUMN)) {
				return selectedSheet;
			}
		}
		return null;
	}

	private String getDefaultBehaviour(Sheet[] sheets) {
		Sheet sheet = getDefinitionColumn(sheets);
		
		if(sheet!=null) {
			String definedBehaviour = sheet.getCell(1,16).getContents().trim();
			
			if(StringUtils.isNotBlank(definedBehaviour) && 
					(definedBehaviour.equals(ItemPanelDto.CONTINUE_BEHAVIOUR) || definedBehaviour.equals(ItemPanelDto.MAIN_BEHAVIOUR) )) {
				return definedBehaviour;
			} else {
				return ItemPanelDto.CLOSE_BEHAVIOUR;
			}
		} else {
			return ItemPanelDto.CLOSE_BEHAVIOUR;
		}
	}
	
	private Boolean visorSelected(Sheet[] sheets) {
		Sheet sheet = getDefinitionColumn(sheets);
		
		if(sheet!=null) {
			String visorSelected = sheet.getCell(1,20).getContents().trim();
			if(StringUtils.isNotEmpty(visorSelected) && visorSelected.equals("S")) {
				return true;
			}
		}
		
		return false;
	}

	protected ItemPanelDto convertirCategoriaAItem(CategorizacionBean categoria) {
		ItemPanelDto item = SpringContext.getBean(ItemPanelDto.class);
		item.setCodigo(categoria.getCodcat());
		item.setDescripción(categoria.getDescat());
		item.setTipo(ItemPanelDto.TIPO_CATEGORIA);
		return item;
	}

	protected File getPlantillaExcel() {
		File file = searchFile(sesion.getAplicacion().getCodAlmacen() + ".xls");
		if (file != null) {
			return file;
		}
		else {
			file = searchFile("manual_selection_panel.xls");
		}
		return file;
	}

	protected File searchFile(String nombreFicheroExcel) {
		File file = null;
		URL url = Thread.currentThread().getContextClassLoader().getResource(nombreFicheroExcel);
		if (url != null) {
			file = new File(url.getPath());
		}
		return file;
	}

	protected List<ItemPanelDto> procesarHoja(Sheet[] sheets, Sheet hoja) {
		log.info("procesarHoja() - Procesando hoja con nombre: " + hoja.getName());

		List<ItemPanelDto> items = new ArrayList<ItemPanelDto>();
		int filas = hoja.getRows();

		Map<Integer, Integer> numColumnas = new HashMap<Integer, Integer>();

		for (int i = 1; i < filas; i++) {
			if (hoja.getCell(0, i).getContents().isEmpty()) {
				continue;
			};

			try {
				ItemPanelDto item = SpringContext.getBean(ItemPanelDto.class);

				Integer fila = new Integer(hoja.getCell(0, i).getContents());
				item.setFila(fila - 1);

				Integer columna = numColumnas.get(fila) == null ? 0 : numColumnas.get(fila) + 1;
				numColumnas.put(fila, columna);
				item.setColumna(columna);

				String tipo = hoja.getCell(1, i).getContents().trim();
				String tipoCodificado = tipo.equals(ItemPanelDto.TIPO_CATEGORIA) || tipo.equals(ItemPanelDto.TIPO_ARTICULO) || tipo.equals(ItemPanelDto.TIPO_HUECO)
				        || tipo.equals(ItemPanelDto.TIPO_FAMILIA) || tipo.equals(ItemPanelDto.TIPO_CATEGORIZACION) || tipo.equals(ItemPanelDto.TIPO_SECCION) 
				        ? tipo : ItemPanelDto.TIPO_ARTICULO;
				item.setTipo(tipoCodificado);

				String codigo = hoja.getCell(2, i).getContents();
				item.setCodigo(codigo.trim());

				String descripcion = hoja.getCell(3, i).getContents();
				if (StringUtils.isNotBlank(descripcion)) {
					item.setDescripción(descripcion.trim());
				}

				String imagen = hoja.getCell(4, i).getContents();
				if (StringUtils.isNotBlank(imagen)) {
					item.setFoto("paneles/" + imagen.trim());
				}
				
				// if empty or incorrect -> default
				String behaviour = hoja.getCell(5, i).getContents();
				
				// ITEM -> behaviour and weighted
				if(ItemPanelDto.TIPO_ARTICULO.equals(tipoCodificado) && StringUtils.isNotBlank(codigo)) {
					
					// close because weight scale
					if(isWeightedItem(codigo)) {
						item.setBehaviour(ItemPanelDto.CLOSE_BEHAVIOUR);
					
					// contain defined correct behaviour
					} else if(StringUtils.isNotBlank(behaviour) && (behaviour.equals(ItemPanelDto.CONTINUE_BEHAVIOUR) 
									|| behaviour.equals(ItemPanelDto.MAIN_BEHAVIOUR) || behaviour.equals(ItemPanelDto.CLOSE_BEHAVIOUR) )) {
						item.setBehaviour(behaviour);
						
					// if not contain correct behaviour -> DEFAULT
					} else {
						item.setBehaviour(defaultBehaviour);
					}
				}

				if (ItemPanelDto.TIPO_CATEGORIA.equals(tipoCodificado) && StringUtils.isNotBlank(codigo)) {
					for (int j = 0; j < sheets.length; j++) {
						String nombreHoja = sheets[j].getName();
						String[] nombreHojaSplit = nombreHoja.split("-");
						
						if (nombreHojaSplit.length > 0 && nombreHojaSplit[0].trim().equals(codigo)) {
							item.setSubItems(procesarHoja(sheets, sheets[j]));
							break;
						}
					}
				}

				if ((ItemPanelDto.TIPO_FAMILIA.equals(tipoCodificado) || ItemPanelDto.TIPO_CATEGORIZACION.equals(tipoCodificado) || ItemPanelDto.TIPO_SECCION.equals(tipoCodificado))
						&& StringUtils.isNotBlank(codigo)) {
					ArticuloExample example = new ArticuloExample();
					
					if (ItemPanelDto.TIPO_FAMILIA.equals(tipoCodificado)){
						List<String> familiesCodes = Arrays.asList(codigo.split(";"));
						for (int j = 0; j < familiesCodes.size(); j++) {
							example.or().andCodFamiliaEqualTo(familiesCodes.get(j).trim()).andActivoEqualTo(Boolean.TRUE);
						}
					} else if (ItemPanelDto.TIPO_SECCION.equals(tipoCodificado)) {
						example.createCriteria().andCodseccionEqualTo(codigo).andActivoEqualTo(Boolean.TRUE);
					} else {
						example.createCriteria().andCodCategorizacionEqualTo(codigo).andActivoEqualTo(Boolean.TRUE);
					}
					
					example.setOrderByClause(ArticuloExample.ORDER_BY_DESART);
					List<ArticuloBean> listaArticulosFamilia = articulosService.consultar(example);
					
					if (listaArticulosFamilia != null && !listaArticulosFamilia.isEmpty()) {
						// Recortamos la lista a 300 registros como maximo
						listaArticulosFamilia = listaArticulosFamilia.subList(0, listaArticulosFamilia.size() > 300 ? 300 : listaArticulosFamilia.size());
						item.setSubItems(transformaArticulosPanel(listaArticulosFamilia, tipoCodificado));
					}
				}
				
				items.add(item);
				
			}
			catch (Exception e) {
				log.error("procesarHoja() - Error cargando hoja " + hoja.getName() + " fila " + i + ":" + e.getMessage());
			}
		}

		return items;
	}
	
	protected Boolean isWeightedItem(String itemCode) {
		try {
			ArticuloBean item = articulosService.consultarArticulo(itemCode);
			if(StringUtils.isNotBlank(item.getBalanzaTipoArticulo()) && item.getBalanzaTipoArticulo().equals("P")) {
				return true;
			}
		} catch (ArticuloNotFoundException e) {
			log.error("isWeightedItem() - No se encuentra el artículo con código " + itemCode);
		} catch (ArticulosServiceException e) {
			log.error("isWeightedItem() - Ha ocurrido un problema con el artículo con código " + itemCode);
		}
		return false;
	}

	public boolean hayPosicionesDefinidas(List<ItemPanelDto> items) {
		for (ItemPanelDto item : items) {
			if (item.getColumna() != null && item.getFila() != null) {
				return true;
			}
		}
		return false;
	}

	protected List<ItemPanelDto> transformaArticulosPanel(List<ArticuloBean> listaArticulos, String tipoCodificado) {
		List<ItemPanelDto> listaPanel = new ArrayList<ItemPanelDto>();
		for (ArticuloBean articulo : listaArticulos) {
			ItemPanelDto itemPanel = new ItemPanelDto();

			itemPanel.setTipo(ItemPanelDto.TIPO_ARTICULO);
			itemPanel.setCodigo(articulo.getCodArticulo());
			itemPanel.setDescripción(articulo.getDesArticulo());
			itemPanel.setBehaviour(this.defaultBehaviour);
			itemPanel.setFoto("paneles/" + articulo.getCodArticulo()+".png");

			listaPanel.add(itemPanel);

		}

		return listaPanel;
	}

}
