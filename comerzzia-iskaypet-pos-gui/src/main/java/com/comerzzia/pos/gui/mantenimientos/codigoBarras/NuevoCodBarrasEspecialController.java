/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */
package com.comerzzia.pos.gui.mantenimientos.codigoBarras;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;
import com.comerzzia.pos.services.codBarrasEsp.CodBarrasEspecialesServices;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class NuevoCodBarrasEspecialController extends WindowController {

	private final Logger log = Logger.getLogger(getClass());

	public static final String PARAMETRO_COD_BARRAS = "COD_BARRAS";

	@FXML
	protected TextField tfDescripcion, tfPosicionPrecio, tfPosicionContenido, tfLongitudContenido, tfPEnteraPrecio, tfPDecimalPrecio, tfPosicionCantidad, tfPEnteraCantidad, tfPDecimalCantidad,
	        tfPrefijo;

	@FXML
	protected CheckBox cbFidelizado;

	@FXML
	protected ComboBox<String> cbTipoCodigo;

	protected CodigoBarrasBean codBarras;

	protected FormularioNuevoCodBarrasEspGui frCodBarras;

	@FXML
	protected Label lbError;

	@Autowired
	protected Sesion sesion;

	@Autowired
	protected CodBarrasEspecialesServices codBarrasEspecialesServices;

	protected String tipoTicket;
	protected String tipoArt;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.trace("initialize()- ");

		frCodBarras = SpringContext.getBean(FormularioNuevoCodBarrasEspGui.class);

		frCodBarras.setFormField("descripcion", tfDescripcion);
		frCodBarras.setFormField("posicionPrecio", tfPosicionPrecio);
		frCodBarras.setFormField("posicionContenido", tfPosicionContenido);
		frCodBarras.setFormField("longitudContenido", tfLongitudContenido);
		frCodBarras.setFormField("enterosPrecio", tfPEnteraPrecio);
		frCodBarras.setFormField("decimalesPrecio", tfPDecimalPrecio);
		frCodBarras.setFormField("posicionCantidad", tfPosicionCantidad);
		frCodBarras.setFormField("enterosCantidad", tfPEnteraCantidad);
		frCodBarras.setFormField("decimalesCantidad", tfPDecimalCantidad);
		frCodBarras.setFormField("tipoCodigo", cbTipoCodigo);
		frCodBarras.setFormField("prefijo", tfPrefijo);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		log.trace("initializeComponents()- ");

		ObservableList<String> tiposCod = FXCollections.observableArrayList();
		tipoTicket = I18N.getTexto("Ticket");
		tiposCod.add(tipoTicket);
		tipoArt = I18N.getTexto("Artículo");
		tiposCod.add(tipoArt);
		tiposCod.add("");

		cbTipoCodigo.setItems(tiposCod);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.trace("initializeForm()- ");

		frCodBarras.clearErrorStyle();

		if (getDatos().containsKey(PARAMETRO_COD_BARRAS)) {
			codBarras = (CodigoBarrasBean) getDatos().remove(PARAMETRO_COD_BARRAS);
		}
		else {
			codBarras = null;
		}

		refrescarDatosPantalla();
	}

	@Override
	public void initializeFocus() {

		tfDescripcion.requestFocus();
	}

	public void refrescarDatosPantalla() {
		log.trace("refrescarDatosPantalla()- ");

		if (codBarras != null) {
			tfDescripcion.setText(codBarras.getDescripcion());
			cbFidelizado.setSelected(codBarras.getFidelizacion().equalsIgnoreCase("S"));
			tfPrefijo.setText(codBarras.getPrefijo());

			String[] camposArticulo = codBarras.getCodart().split("\\|");
			String[] camposTicket = codBarras.getCodticket().split("\\|");

			if (camposArticulo.length == 0 || camposArticulo[0].isEmpty()) {
				tfPosicionContenido.setText(camposTicket[0]);
				tfLongitudContenido.setText(camposTicket[1]);
				cbTipoCodigo.getSelectionModel().select(tipoTicket);
			}
			else {
				tfPosicionContenido.setText(camposArticulo[0]);
				tfLongitudContenido.setText(camposArticulo[1]);
				cbTipoCodigo.getSelectionModel().select(tipoArt);
			}

			String[] camposPrecio = codBarras.getPrecio().split("\\|");
			tfPosicionPrecio.setText(camposPrecio[0]);
			tfPEnteraPrecio.setText(camposPrecio[1]);
			tfPDecimalPrecio.setText(camposPrecio[2]);

			String[] camposCantidad = codBarras.getCantidad().split("\\|");
			tfPosicionCantidad.setText(camposCantidad[0]);
			tfPEnteraCantidad.setText(camposCantidad[1]);
			tfPDecimalCantidad.setText(camposCantidad[2]);
		}
		else {
			tfPrefijo.setText("");
			tfDescripcion.setText("");
			tfLongitudContenido.setText("");
			tfPDecimalCantidad.setText("");
			tfPDecimalPrecio.setText("");
			tfPosicionContenido.setText("");
			tfPosicionCantidad.setText("");
			tfPosicionPrecio.setText("");
			tfPEnteraCantidad.setText("");
			tfPEnteraPrecio.setText("");
			cbFidelizado.setSelected(false);
			cbTipoCodigo.getSelectionModel().select("");
		}
	}

	public void accionAceptar() {
		log.trace("aceptar()- ");

		if (validarDatosCodBar()) {

			boolean nuevoCodigo = false;
			try {
				if (codBarras == null) {
					codBarras = new CodigoBarrasBean();
					nuevoCodigo = true;
				}

				codBarras.setDescripcion(tfDescripcion.getText());
				codBarras.setCantidad(FormatUtil.getInstance().completarCerosIzquierda(tfPosicionCantidad.getText(), 2) + "|"
				        + FormatUtil.getInstance().completarCerosIzquierda(tfPEnteraCantidad.getText(), 2) + "|" + FormatUtil.getInstance().completarCerosIzquierda(tfPDecimalCantidad.getText(), 2));
				codBarras.setPrecio(FormatUtil.getInstance().completarCerosIzquierda(tfPosicionPrecio.getText(), 2) + "|"
				        + FormatUtil.getInstance().completarCerosIzquierda(tfPEnteraPrecio.getText(), 2) + "|" + FormatUtil.getInstance().completarCerosIzquierda(tfPDecimalPrecio.getText(), 2));
				String tipoCod = cbTipoCodigo.getSelectionModel().getSelectedItem();
				if (tipoCod.equals(tipoArt)) {
					codBarras.setCodart(FormatUtil.getInstance().completarCerosIzquierda(tfPosicionContenido.getText(), 2) + "|"
					        + FormatUtil.getInstance().completarCerosIzquierda(tfLongitudContenido.getText(), 2));
					codBarras.setCodticket("  |  ");
				}
				else if (tipoCod.equals(tipoTicket)) {
					codBarras.setCodticket(FormatUtil.getInstance().completarCerosIzquierda(tfPosicionContenido.getText(), 2) + "|"
					        + FormatUtil.getInstance().completarCerosIzquierda(tfLongitudContenido.getText(), 2));
					codBarras.setCodart("  |  ");
				}
				codBarras.setPrefijo(tfPrefijo.getText());
				codBarras.setFidelizacion(cbFidelizado.isSelected() ? "S" : "N");
				codBarras.setUidActividad(sesion.getAplicacion().getUidActividad());

				if (nuevoCodigo) {
					codBarras.setOrden(codBarrasEspecialesServices.obtenerSiguienteOrdenDisponible());
					codBarrasEspecialesServices.nuevoCodigo(codBarras);
				}
				else {
					codBarrasEspecialesServices.actualizarCodigo(codBarras);
				}
				codBarrasEspecialesServices.cargarCodigosBarrasEspeciales(sesion.getAplicacion().getUidActividad());

				getStage().close();
			}
			catch (Exception e) {
				log.error("Error confirmando la operación.", e);
				VentanaDialogoComponent.crearVentanaError(this.getStage(), I18N.getTexto("Error. No se pudo completar la operación."), e);
			}
		}
	}

	public void accionCancelar() {
		log.trace("accionCancelar()- ");

		getStage().close();
	}

	protected boolean validarDatosCodBar() {
		log.trace("validarDatosCodBar()- ");

		frCodBarras.clearErrorStyle();

		frCodBarras.setDecimalesCantidad(tfPDecimalCantidad.getText());
		frCodBarras.setDecimalesPrecio(tfPDecimalPrecio.getText());
		frCodBarras.setDescripcion(tfDescripcion.getText());
		frCodBarras.setEnterosCantidad(tfPEnteraCantidad.getText());
		frCodBarras.setPosicionCantidad(tfPosicionCantidad.getText());
		frCodBarras.setPosicionContenido(tfPosicionContenido.getText());
		frCodBarras.setLongitudContenido(tfLongitudContenido.getText());
		frCodBarras.setTipoCodigo(cbTipoCodigo.getSelectionModel().getSelectedItem());
		frCodBarras.setEnterosPrecio(tfPEnteraPrecio.getText());
		frCodBarras.setPosicionPrecio(tfPosicionPrecio.getText());
		frCodBarras.setPrefijo(tfPrefijo.getText());

		// Validamos el formulario
		Set<ConstraintViolation<FormularioNuevoCodBarrasEspGui>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frCodBarras);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioNuevoCodBarrasEspGui> next = constraintViolations.iterator().next();
			frCodBarras.setErrorStyle(next.getPropertyPath(), true);
			frCodBarras.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			return false;
		}
		else {
			lbError.setText("");
		}
		return true;
	}

}
