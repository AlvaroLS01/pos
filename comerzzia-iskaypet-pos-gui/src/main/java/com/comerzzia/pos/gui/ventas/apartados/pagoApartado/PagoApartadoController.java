package com.comerzzia.pos.gui.ventas.apartados.pagoApartado;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.validation.ConstraintViolation;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.apartados.detalle.DetalleApartadosController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.pagos.FormularioImportePagosBean;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

/**
 * 
 * @author ABRH
 *
 */
@Component
public class PagoApartadoController extends WindowController{

	public static final String PARAMETRO_IMPORTE_PAGO = "IMPORTE_PAGO";
	
	@FXML
	protected TecladoNumerico tecladoNumerico;
	
	@FXML
	protected TextFieldImporte tfImporte;
	
	protected TicketManager ticketManager;
	
	protected BigDecimal importeMaximoPago;

	protected FormularioImportePagosBean frImportePago;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		initTecladoNumerico(tecladoNumerico);
		frImportePago = SpringContext.getBean(FormularioImportePagosBean.class);
        //Se registra el evento para salir de la pantalla pulsando la tecla escape.
        registrarAccionCerrarVentanaEscape();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		tfImporte.clear();
		importeMaximoPago = (BigDecimal)getDatos().get(DetalleApartadosController.PARAMETRO_IMPORTE_MAXIMO_PAGO);
		tfImporte.setText(FormatUtil.getInstance().formateaImporte(importeMaximoPago));
	}

	@Override
	public void initializeFocus() {
		tfImporte.requestFocus();
	}
	
	public void accionAceptarIntro(KeyEvent e){
		if(e.getCode() == KeyCode.ENTER){
			accionAceptar();
		}
	}

	public void accionAceptar(){
		String importe = tfImporte.getText();
		frImportePago.clearErrorStyle();
		frImportePago.setImporte(tfImporte.getText());

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioImportePagosBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frImportePago);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioImportePagosBean> next = constraintViolations.iterator().next();
			frImportePago.setErrorStyle(next.getPropertyPath(), true);
			frImportePago.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaAviso(next.getMessage(), getStage());
			tfImporte.requestFocus();
		}
		else {
			BigDecimal importePago = FormatUtil.getInstance().desformateaImporte(importe);
	
			if(importePago != null){
				if(BigDecimalUtil.isMayorACero(importePago)) {
					if(importeMaximoPago.compareTo(importePago)>=0){
						getDatos().put(PARAMETRO_IMPORTE_PAGO, importePago);
						getStage().close();
					}
					else{
						initializeFocus();
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe no debe superar el total del apartado."), getStage());
					}
				}
				else {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe debe ser mayor que cero."), getStage());
				}
			}
			else{
				initializeFocus();
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe introducir un número válido."), getStage());
			}
		}
	}

}
