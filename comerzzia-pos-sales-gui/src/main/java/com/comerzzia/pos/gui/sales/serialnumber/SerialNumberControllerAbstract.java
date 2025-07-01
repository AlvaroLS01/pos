package com.comerzzia.pos.gui.sales.serialnumber;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SerialNumberControllerAbstract extends SceneController {

	public static final String PARAM_LINE_DESCRIPTION= "PARAM_LINE_DESCRIPTION";
	public static final String PARAM_REQUIRED_QUANTITY = "PARAM_REQUIRED_QUANTITY";
	public static final String PARAM_LINE_CURRENT_SERIAL_CODES = "PARAM_LINE_CURRENT_SERIAL_CODES";
	public static final String PARAM_LINE_ASSIGNED_SERIAL_CODES = "PARAM_ASSIGNED_SERIAL_CODES";
	
	public static final String CANCEL_CONFIRMATION_LINE_NOT_ADDED = "serial_number.cancel.confirmation.line_not_added";

	@FXML
	protected TextField tfSerialNumber;
	
	@FXML
	protected WebView wvPrincipal;

	@FXML
	protected NumericKeypad tecladoNumerico;

	@FXML
	protected Button btAccept, btCancel;

	protected Long requiredSerialCodes;
	protected String lineDescription;
	protected Set<String> originalSerialNumbers;
	protected Set<String> serialNumbers;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}


	@Override
	public void initializeFocus() {
		tfSerialNumber.requestFocus();
	}
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if("DeleteSerialNumber".equals(method)) {
			String serialnumber = params.get("serialnumber");
			
			removeSerialCode(serialnumber);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		sceneData.remove(PARAM_LINE_ASSIGNED_SERIAL_CODES);
		requiredSerialCodes = (Long) sceneData.get(PARAM_REQUIRED_QUANTITY);
		lineDescription = (String) sceneData.get(PARAM_LINE_DESCRIPTION);
		
		Set<String> paramSerialNumbers = (Set<String>) sceneData.get(PARAM_LINE_CURRENT_SERIAL_CODES);
		if(paramSerialNumbers != null) {
			originalSerialNumbers = new HashSet<>(paramSerialNumbers);
		} else {
			originalSerialNumbers = new HashSet<>();
			
		}
		
		serialNumbers = new HashSet<>(originalSerialNumbers);
		
		
		refreshScreenData();
//		if (serialNumberLine != null) {
			// Redondeamos la cantidad por si tuviese decimales, que no debería
//			cantidadLinea = serialNumberLine.getCantidad().setScale(0, RoundingMode.HALF_UP).abs();
//
//			Set<String> numerosOriginales = serialNumberLine.getNumerosSerie();
//			if (numerosOriginales == null) {
//				lvNumerosSerie.setItems(FXCollections.observableArrayList(new ArrayList<String>()));
//			}
//			else {
//				lvNumerosSerie.setItems(FXCollections.observableArrayList(numerosOriginales));
//			}
//			tfNumeroSerie.clear();
//
//			lbArticulo.setText(serialNumberLine.getDesArticulo());
//			lbCantidad.setText(FormatUtils.getInstance().formatNumber(cantidadLinea));
//			if (serialNumberLine.getNumerosSerie() != null) {
//				lbPendiente.setText(FormatUtils.getInstance().formatNumber(cantidadLinea.subtract(new BigDecimal((serialNumberLine.getNumerosSerie().size())))));
//			}
//			else {
//				lbPendiente.setText(FormatUtils.getInstance().formatNumber(cantidadLinea));
//			}
//
//			if (lvNumerosSerie.getItems().size() > 0) {
//				lvNumerosSerie.getSelectionModel().selectFirst();
//			}
//			
//			if(serialNumberLine.getNumerosSerie() != null && serialNumberLine.getNumerosSerie().size() > 0) {
//				lvNumerosSerie.getItems().addAll(serialNumberLine.getNumerosSerie());
//				lvNumerosSerie.getSelectionModel().selectLast();
//			}
//		else {
//			throw new InitializeGuiException("No se ha encontrado línea para editar");
//		}
		
	}
	protected void refreshScreenData() {
		//TODO: Desactivar cancelar si no corresponde
		loadWebViewPrincipal();
	}
	
	protected void loadWebViewPrincipal() {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("requiredQuantity", requiredSerialCodes);
		params.put("lineDescription", lineDescription);
		params.put("serialNumbers", serialNumbers);
		
		loadWebView(getWebViewPath(), params, wvPrincipal);
	}
	
	protected String getWebViewPath() {
		return "sales/serialnumber/serialnumber";
	}

	public void addNumeroSerie(String numSerie) {
		if(!validateSerialNumber(numSerie)) {
			return;
		}
		
		serialNumbers.add(numSerie);
		tfSerialNumber.clear();
		
		refreshScreenData();
		if(!serialCodesPending()) {
			btAccept.requestFocus();
		}
	}

	/**
	 * @param serialNumber
	 */
	protected boolean validateSerialNumber(String serialNumber) {
		if (StringUtils.isBlank(serialNumber)) {
			if(!serialCodesPending()) {
				btAccept.requestFocus();
			}
			return false;
		}
		
		if (!serialCodesPending()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Imposible insertar el número de serie, ya están todos asignados."));
			btAccept.requestFocus();
			return false;
		}
		
		if(serialNumbers.contains(serialNumber)) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El número de serie debe ser único."));
			tfSerialNumber.clear();
			return false;
		}
		
		return true;
	}

	protected boolean serialCodesPending() {
		return requiredSerialCodes.compareTo(Long.valueOf(serialNumbers.size())) > 0;
	}
// TODO: Solo devoluciones
//	protected boolean numeroSerieValido(String numSerie) {
//		boolean res = true;
//
//		if (numerosSerieDocumentoOrigen != null && numerosSerieDocumentoOrigen.size() > 0) {
//			res = numerosSerieDocumentoOrigen.contains(numSerie);
//		}
//		return res;
//	}
	
	@Override
	public void closeCancel() {
		if (requiredSerialCodes.compareTo(Long.valueOf(originalSerialNumbers.size())) != 0 && originalSerialNumbers.isEmpty()) {
			tfSerialNumber.requestFocus();
			if (DialogWindowComponent.openConfirmWindow(I18N.getText(CANCEL_CONFIRMATION_LINE_NOT_ADDED), getStage())) {
				super.closeCancel();
			}
		}
		else {
			super.closeCancel();
		}
	}
	
	@FXML
	public void actionCancel() {
//		HashMap<String, Object> callbackData = new HashMap<String, Object>();
//		if (quedanNumerosPorAsignar()) {
//			if (!DialogWindowComponent.openConfirmWindow(I18N.getText("Hay números de serie sin asignar, la pantalla se cerrará, ¿Está seguro?"), getStage())) {
//				return;
//			}
//		}
//
//		List<String> numerosSerieOut = new LinkedList<String>();
//		numerosSerieOut.addAll(lvNumerosSerie.getItems());
//		callbackData.put(PARAMETRO_LISTA_NUMEROS_SERIES_ASIGNADOS, numerosSerieOut);
		
//		if(requiredSerialCodes.compareTo(Long.valueOf(originalSerialNumbers.size())) != 0) {
//			tfSerialNumber.requestFocus();
//			DialogWindowComponent.openWarnWindow(I18N.getText("No es posible cancelar la asignación de números de serie."), getStage());
//			return;
//		}
		closeCancel();
	}
	
	@FXML
	public void actionAccept() {
		if (serialCodesPending()) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Quedan números de serie pendientes de asignar. ({0}/{1})", serialNumbers.size(), requiredSerialCodes ));
			tfSerialNumber.requestFocus();
			return;
		}

		if (requiredSerialCodes.compareTo(Long.valueOf(serialNumbers.size())) < 0) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Hay más números de serie asignados que la cantidad de artículos en línea. ({0}/{1})", serialNumbers.size(), requiredSerialCodes));
			return;
		}
		closeSuccess(serialNumbers);
	}

	public void removeAllSerialCodes() {
		if (DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Los números asociados a la línea de venta serán borrados, ¿Está seguro?"))) {
			serialNumbers.clear();
			tfSerialNumber.requestFocus();
			refreshScreenData();
		}
	}

	protected void removeSerialCode(String serialNumber) {
		if(!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("El número de serie indicado será borrado, ¿Está seguro?"))) {
			return;
		}
		serialNumbers.remove(serialNumber);
		tfSerialNumber.requestFocus();
		refreshScreenData();
	}

	@FXML
	public void actionTfSerialNumber(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.MULTIPLY) {
			addNumeroSerie(tfSerialNumber.getText().trim().toUpperCase());
		}
	}

//	TODO: Devolución
//	public void numerosSerieDisponibles() {
//		Integer pendiente= new Integer(lbPendiente.getText());
//		if (pendiente > 0) {
//			List<String> numerosSerieDisponibles = new ArrayList<String>();
//			numerosSerieDisponibles.addAll(numerosSerieDocumentoOrigen);
//			numerosSerieDisponibles.removeAll(lvNumerosSerie.getItems());
//			sceneData.put(SerialNumberOriginalLineController.PARAMETRO_NUMEROS_SERIE_DISPONIBLES, numerosSerieDisponibles);
//			sceneData.put(SerialNumberOriginalLineController.PARAMETRO_ARTICULO, lbArticulo.getText());
//			sceneData.put(SerialNumberOriginalLineController.PARAMETRO_CANTIDAD, lbPendiente.getText());
//			openScene(SerialNumberOriginalLineController.class, new SceneCallback() {
//				@Override
//				public void onSuccess(Map<String, Object> callbackData) {
//					Integer pending = pendiente;
//					@SuppressWarnings("unchecked")
//					List<String> numerosSerieSeleccionados = (List<String>) callbackData.get(SerialNumberOriginalLineController.PARAMETRO_NUMEROS_SERIE_SELECCION);
//					if (numerosSerieSeleccionados != null) {
//						int i = 0;
//						for (; i < pending && i < numerosSerieSeleccionados.size(); i++) {
//							lvNumerosSerie.getItems().add(numerosSerieSeleccionados.get(i));
//						}
//
//						if (numerosSerieSeleccionados.size() > pending) {
//							DialogWindowComponent.openWarnWindow(I18N.getText("No se pueden asignar todos los números de serie seleccionados."), getStage());
//						}
//
//						pending = pending - i;
//						lbPendiente.setText(pending.toString());
//
//						if (pending > 0) {
//							tfNumeroSerie.requestFocus();
//						}
//						else {
//							btCerrar.requestFocus();
//						}
//						lvNumerosSerie.getSelectionModel().selectLast();
//					}
//				}
//			});
//			
//		}
//		else {
//			DialogWindowComponent.openWarnWindow(I18N.getText("No se pueden asignar más números de serie, ya están todos asignados."), getStage());
//			btCerrar.requestFocus();
//		}
//	}
}
