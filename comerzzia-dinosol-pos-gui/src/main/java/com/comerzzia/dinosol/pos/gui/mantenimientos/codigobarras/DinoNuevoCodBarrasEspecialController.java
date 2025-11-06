package com.comerzzia.dinosol.pos.gui.mantenimientos.codigobarras;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.mantenimientos.codigoBarras.NuevoCodBarrasEspecialController;
import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Primary
public class DinoNuevoCodBarrasEspecialController extends NuevoCodBarrasEspecialController {

	private Logger log = Logger.getLogger(DinoNuevoCodBarrasEspecialController.class);

	protected String tipoPlu;

	@Override
	public void initializeComponents() throws InitializeGuiException {
		log.trace("initializeComponents()");

		ObservableList<String> tiposCod = FXCollections.observableArrayList();
		tipoTicket = I18N.getTexto("Ticket");
		tiposCod.add(tipoTicket);
		tipoArt = I18N.getTexto("Artículo");
		tiposCod.add(tipoArt);
		tipoPlu = I18N.getTexto("PLU");
		tiposCod.add(tipoPlu);
		tiposCod.add("");

		cbTipoCodigo.setItems(tiposCod);
	}

	@Override
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
				else if (tipoCod.equals(tipoPlu)) {
					codBarras.setCodart(FormatUtil.getInstance().completarCerosIzquierda(tfPosicionContenido.getText(), 2) + "|"
					        + FormatUtil.getInstance().completarCerosIzquierda(tfLongitudContenido.getText(), 2));
					codBarras.setCodticket("  X  ");
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
	
	@Override
	public void refrescarDatosPantalla() {
	    super.refrescarDatosPantalla();
	    
	    if(codBarras != null && codBarras.getCodticket().trim().equals("X")) {
	    	cbTipoCodigo.getSelectionModel().select(tipoPlu);
	    }
	}

}
