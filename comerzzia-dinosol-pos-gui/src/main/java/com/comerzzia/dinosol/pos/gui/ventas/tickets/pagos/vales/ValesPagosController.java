package com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.vales;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.persistence.mediospago.ValesManualesComboBean;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class ValesPagosController extends WindowController {

	private static final String CODIGO_VALE_TIENDA = "1XXX";

	protected static final Logger log = Logger.getLogger(ValesPagosController.class);
	
	/* Los que enviamos */
	public static final String PARAMETRO_COD_MEDIOPAGO = "COD_MEDIOPAGO_VALES";
	public static final String PARAMETRO_IMPORTE = "IMPORTE_MEDIOPAGO_VALES";
	public static final String PARAMETRO_CODIGO = "CODIGO_MEDIOPAGO_VALES";
	/* Los que recibimos */
	public static final String PARAMETRO_IMPORTE_PORPAGAR = "IMPORTE_PORPAGAR";
	public static final String PARAMETRO_IMPORTE_SELECCIONADO = "IMPORTE_SELECCIONADO";
	
	public static final String PARAMETRO_TITULO = "Vales de Tienda";
	
	/* Lo usamos para almacenar los medios de pago cargados en el combo */
	private Map<String, String> mediosPago = new HashMap<String, String>();
	private BigDecimal importePorPagar;
	
	@FXML
	protected ComboBox<ValesManualesComboBean> cbMediosPago;
	protected ObservableList<ValesManualesComboBean> mediosPagoCombo;
	@FXML
    private TextFieldImporte tfImporte;    
	@FXML
    private TextFieldImporte tfCodigo;
    @FXML
    private Label lbTitulo;
    @FXML
    private Label lbError;
	
	@Autowired
	private MediosPagosService mediosPagosService;
    
	@Override
    public void initialize(URL url, ResourceBundle rb) {
    }

	@Override
    public void initializeComponents() throws InitializeGuiException {
		 registrarAccionCerrarVentanaEscape();
		 
		 KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		 keyboardDataDto.setPintarPiePantalla(true);
		 tfCodigo.setUserData(keyboardDataDto);
		 tfImporte.setUserData(keyboardDataDto);
		 addSeleccionarTodoEnFoco(tfCodigo);
		 addSeleccionarTodoEnFoco(tfImporte);
    }

	@SuppressWarnings("static-access")
    @Override
    public void initializeForm() throws InitializeGuiException {
		/* Limpiamos todos los datos y añadimos el título de la pantalla */
		cbMediosPago.getItems().clear();
		mediosPagoCombo = FXCollections.observableArrayList(new ArrayList<ValesManualesComboBean>());
		
		tfCodigo.clear();
		lbError.setText("");
		lbTitulo.setText(I18N.getTexto(PARAMETRO_TITULO));
		/* Pasamos el importe que está seleccionado en la pantalla de pagos. */
		tfImporte.setText("");
		importePorPagar = (BigDecimal) getDatos().get(PARAMETRO_IMPORTE_PORPAGAR);
		
		/* Realizamos la carga de los medios de pago que empiezan por 1 */
		for (Map.Entry<String, MedioPagoBean> entry : mediosPagosService.mediosPago.entrySet()) {
			ValesManualesComboBean medioPago = new ValesManualesComboBean();
			medioPago.setMedioPago(entry.getValue());
			String codigo = entry.getValue().getCodMedioPago();
			String nombre = entry.getValue().getDesMedioPago();
			if(codigo.startsWith("1") && !CODIGO_VALE_TIENDA.equals(codigo)){
				log.debug("Medio de pago cargado : " + nombre + " (" + codigo + ")");
				mediosPagoCombo.add(medioPago);
				/* Rellenamos el otro mapa para luego coger el código */
				mediosPago.put(nombre, codigo);
			}
			cbMediosPago.setItems(mediosPagoCombo);
		}
    }

	@Override
    public void initializeFocus() {
		cbMediosPago.requestFocus();
    }

    @FXML
    public void accionAceptarIntro(KeyEvent e){
        if(e.getCode()==KeyCode.ENTER){
            accionAceptar();
        }
    }
    
    @FXML
    public void accionAceptar(){
    	
    	lbError.setText("");
    	
    	if(cbMediosPago.getSelectionModel().getSelectedItem() == null){
    		String mensajeError = "El medio de pago es obligatorio";
    		VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
    		log.error("accionAceptar() - " + mensajeError);
    		return;
    	}
    	
    	String ean13 = tfCodigo.getText();
    	String codigoMedioPago = cbMediosPago.getSelectionModel().getSelectedItem().getMedioPago().getCodMedioPago();
    	
    	BigDecimal importePagado = null;
    	if(tfImporte.getText().contains(",")){
    		importePagado = new BigDecimal(tfImporte.getText().replace(",", "."));
    	}else if(!StringUtils.isBlank(tfImporte.getText()) ){
    		importePagado = new BigDecimal(tfImporte.getText());
    	}
    	
    	if(importePagado == null){
    		String mensajeError = I18N.getTexto("El importe es obligatorio");
    		VentanaDialogoComponent.crearVentanaError(mensajeError, getStage());
    		tfCodigo.requestFocus();
    		tfImporte.requestFocus();
    		log.error("accionAceptar() - " + mensajeError);
    		return;
    	}
    	
    	/* No se puede poner un importe superior al importe por pagar del ticket */
    	if(BigDecimalUtil.isMayor(importePagado, importePorPagar)){
    		String mensajeError = I18N.getTexto("El importe no puede ser superior al total por pagar del ticket");
    		VentanaDialogoComponent.crearVentanaError(mensajeError, getStage());
    		tfCodigo.requestFocus();
    		tfImporte.requestFocus();
    		log.error("accionAceptar() - " + mensajeError);
    		return;
    	}
    	
    	/* Sino se ha producido ningún error enviamos los parámetro y cerramos */
    	if(lbError.getText().isEmpty()){
    		log.debug("accionAceptar() - Acción realizada correctamente, Medio Pago : " + codigoMedioPago);
    		getDatos().put(PARAMETRO_COD_MEDIOPAGO, codigoMedioPago);
    		getDatos().put(PARAMETRO_IMPORTE, importePagado);
    		getDatos().put(PARAMETRO_CODIGO, ean13);
    		getStage().close();
    	}
    	
    }
}
