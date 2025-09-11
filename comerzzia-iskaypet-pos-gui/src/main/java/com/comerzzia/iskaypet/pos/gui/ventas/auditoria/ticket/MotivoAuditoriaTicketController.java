package com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.motivos.CargarMotivosController;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.motivos.CargarMotivosView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriasService;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

@Component
public class MotivoAuditoriaTicketController extends WindowController implements Initializable, IContenedorBotonera {

	public static final String CLAVE_NUMERO_TOTAL_ARTICULOS = "numeroTotalArticulos";
	public static final String CLAVE_NUMERO_INDICE_ARTICULO = "indiceArticulo";

	public static final String CANCELAR = "CANCELAR";

	public static final String ACTIVAR_CANCELAR = "ACTIVAR_CANCELAR";

	private static final Logger log = Logger.getLogger(MotivoAuditoriaTicketController.class.getName());

	@FXML
	protected Button btBuscar, btAceptar, btCancelar;

	@FXML
	protected Label lbTitulo, lbProducto, lbMensajeError;

	@FXML
	protected TextField tfCodMotivo, tfDesMotivo;

	@FXML
	protected TextArea tfObservaciones;

	public final static String MOTIVO = "MOTIVO";

	private String codigo;

	private IskaypetLineaTicket iskLinea;

	private MotivoAuditoriaDto motivoSeleccionado;

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException {

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando ventana...");

		tfCodMotivo.setText("");
		tfDesMotivo.setText("");
		tfObservaciones.setText("");

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		log.debug("inicializarComponentes()");

	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Inicializando pantalla...");

		codigo = (String) getDatos().get(IskaypetFacturacionArticulosController.TIPO_DOCUMENTO_ENVIADO);
		iskLinea = (IskaypetLineaTicket) getDatos().get(IskaypetFacturacionArticulosController.LINEA_ENVIADA);

		Integer totalArticulos = (Integer) getDatos().get(CLAVE_NUMERO_TOTAL_ARTICULOS);
		Integer indice = (Integer) getDatos().get(CLAVE_NUMERO_INDICE_ARTICULO);

		lbTitulo.setText(setearTitulo(codigo));

		if (codigo.equals(AuditoriasService.TIPO_AUDITORIA_ANULACION_ORDEN_COMPLETA) || codigo.equals(AuditoriasService.TIPO_AUDITORIA_DESCUENTO_GENERAL)) {
			lbProducto.setText("");
		}
		else {
			lbProducto.setText(iskLinea.getDesArticulo() + getContadorArticulos(indice, totalArticulos));
		}

		tfCodMotivo.setText("");
		tfDesMotivo.setText("");
		tfObservaciones.setText("");
		lbMensajeError.setText("");

		btCancelar.setVisible((Boolean) getDatos().getOrDefault(ACTIVAR_CANCELAR, false));

	}

	// ISK-249 GAP 101 Edición de cantidad en POS
	// Solución temporal hasta reestructuración de auditorías para mostrar índice en auditorías
	// de múltiples unidades de artículo
	private String getContadorArticulos(Integer indice, Integer total) {
		if (indice == null || total == null || total == 1) {
			return "";
		}

		return " (" + (indice + 1) + "/" + total + ")";

	}

	@Override
	public void initializeFocus() {
		tfCodMotivo.requestFocus();

	}

	@FXML
	private void cancelar() {
		log.info("cancelar() - Cerrando ventana...");
		getDatos().put(CANCELAR, Boolean.TRUE);
		getStage().close();

	}

	@FXML
	private void accionAceptar() {
		log.info("accionAceptar() - Comprobando campos...");

		String descripcionCompleta = tfObservaciones.getText();

		// String descripcionCompleta = motivoSeleccionado.getDescripcion() + " : " + tfObservaciones.getText();

		boolean formularioCorrecto = validarFormulario(descripcionCompleta);

		if (formularioCorrecto) {
			MotivoAuditoriaDto mDto = new MotivoAuditoriaDto();
			mDto.setCodigo(motivoSeleccionado.getCodigo());
			mDto.setDescripcion(descripcionCompleta);

			getDatos().put(MOTIVO, mDto);

			getStage().close();

		}
	}

	private boolean validarFormulario(String descripcionCompleta) {
		boolean formularioCorrecto = true;

		if (StringUtils.length(descripcionCompleta) > 100) {
			lbMensajeError.setText("Observación demasiado extensa");
			formularioCorrecto = false;
		}

		if (StringUtils.isBlank(tfCodMotivo.getText()) || StringUtils.isBlank(tfDesMotivo.getText())
		        || (motivoSeleccionado.getPermiteObservaciones().equals("S")) && StringUtils.isBlank(tfObservaciones.getText())) {
			lbMensajeError.setText("Justifique el motivo");
			formularioCorrecto = false;
		}

		return formularioCorrecto;
	}

	@FXML
	private void accionBuscarMotivos() {

		log.info("accionBuscarMotivos() - Buscando motivos para seleccionar...");

		getDatos().put(IskaypetFacturacionArticulosController.TIPO_DOCUMENTO_ENVIADO, codigo);
		getApplication().getMainView().showModalCentered(CargarMotivosView.class, getDatos(), getStage());

		motivoSeleccionado = (MotivoAuditoriaDto) getDatos().get(CargarMotivosController.MOTIVO);

		if (motivoSeleccionado != null) {
			tfCodMotivo.setText(motivoSeleccionado.getCodigo().toString());
			tfDesMotivo.setText(motivoSeleccionado.getDescripcion());
		}
		lbMensajeError.setText("");
	}

	@FXML
	private void accionBuscarTeclado() {

	}

	private String setearTitulo(String codigo) {
		log.info("setearTitulo() - Eligiendo titulo para la ventana...");

		String titulo = "";

		if (AuditoriasService.TIPO_AUDITORIA_ANULACION_LINEA.equals(codigo)) {
			titulo = I18N.getTexto("Anulación línea");
		}
		else if (AuditoriasService.TIPO_AUDITORIA_ANULACION_ORDEN_COMPLETA.equals(codigo)) {
			titulo = I18N.getTexto("Anulación orden completa");
		}
		else if (AuditoriasService.TIPO_AUDITORIA_DEVOLUCION.equals(codigo)) {
			titulo = I18N.getTexto("Devolución");
		}
		else if (AuditoriasService.TIPO_AUDITORIA_CAMBIOPRECIO.equals(codigo)) {
			titulo = I18N.getTexto("Cambio de precio");
		}
		else if (AuditoriasService.TIPO_AUDITORIA_DESCUENTO_GENERAL.equals(codigo)) {
			titulo = I18N.getTexto("Descuento completo a una compra");
		}

		return titulo;
	}

}
