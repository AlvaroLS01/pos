package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.resumen.ResumenContratoController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.resumen.ResumenContratoView;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.detalles.DetallesTrazabilidadDto;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.util.formatter.IskaypetFormatter;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

@Component
public class ContratoAnimalController extends WindowController implements Initializable, IContenedorBotonera {

	private static final Logger log = Logger.getLogger(ContratoAnimalController.class.getName());

	@FXML
	protected Button btCancelar, btAceptar, btContratos;

	@FXML
	protected TextField tfEspecie, tfRaza, tfNumIden;

	@FXML
	protected TextFieldImporte tfPeso;

	@FXML
	protected ComboBox<String> cbSexo;

	protected IskaypetLineaTicket iskLinea;
	
	public static final String PARAM_CONTRATO = "contrato";
	
	private FidelizacionBean fidelizado;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando ventana...");
	}

	@Override
	public void initializeComponents() {
		log.debug("inicializarComponentes()");
		tfEspecie.setEditable(false);
		tfRaza.setEditable(false);

		// Se cargan las listas de opciones de genero con traduccion
		ObservableList<String> generoOptions = FXCollections.observableArrayList(
				I18N.getTexto("No se puede asegurar"),
				I18N.getTexto("Macho"),
				I18N.getTexto("Hembra"));
		cbSexo.setItems(generoOptions);

		registrarAccionCerrarVentanaEscape();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Inicializando pantalla...");

		iskLinea = (IskaypetLineaTicket) getDatos().get(IskaypetFacturacionArticulosController.LINEA_ENVIADA);
		fidelizado = (FidelizacionBean) getDatos().get(IskaypetFacturacionArticulosController.FIDELIZADO_CONTRATO);
		
		ContratoAnimalDto datos = iskLinea.getContratoAnimal();
		if (datos != null) {
			rellenarFormularioContratoExistente(datos);
		}
		else {
			limpiarFormulario();
			tfEspecie.setText(iskLinea.getArticulo().getDesArticulo());
			tfRaza.setText(iskLinea.getArticulo().getDesArticulo());
		}

		tfPeso.setTextFormatter(IskaypetFormatter.getDoubleFormatNullable(7,3));
		
		//GAP 172 TRAZABILIDAD ANIMALES
		if(iskLinea.getDetallesTrazabilidad()!= null){
			String identificadorCompuesto = "CHIP | ANILLA | CITES";
			//Tenemos que respetar las posiciones, siempre serán las mimas
			for(DetallesTrazabilidadDto detalles :iskLinea.getDetallesTrazabilidad()) {
				String fldName = detalles.getFldName();
				//Sustituimos en el identificador el valor que tenemos en el detalle
				switch (fldName) {
					case "CHIP":
						if(StringUtils.isNotBlank(detalles.getIdentificacionTrazabilidad())){
							identificadorCompuesto = identificadorCompuesto.replace("CHIP", detalles.getIdentificacionTrazabilidad());
						}
						break;
					case "ANILLA":
						if(StringUtils.isNotBlank(detalles.getIdentificacionTrazabilidad())){
							identificadorCompuesto = identificadorCompuesto.replace("ANILLA", detalles.getIdentificacionTrazabilidad());
						}
						break;
					case "CITES":
						if(StringUtils.isNotBlank(detalles.getIdentificacionTrazabilidad())){
							identificadorCompuesto = identificadorCompuesto.replace("CITES", detalles.getIdentificacionTrazabilidad());
						}
						break;
				}
			}
			
			//Comprobamos las sustiticiones, si no han sido reemplazadas le asignamos para el contrato el caracter -
			if(identificadorCompuesto.contains("CHIP")) {
				identificadorCompuesto = identificadorCompuesto.replace("CHIP", "- ");
			}
			if(identificadorCompuesto.contains("ANILLA")) {
				identificadorCompuesto = identificadorCompuesto.replace(" ANILLA ", " - ");
			}
			if(identificadorCompuesto.contains("CITES")) {
				identificadorCompuesto = identificadorCompuesto.replace(" CITES", " -");
			}
			tfNumIden.setText(identificadorCompuesto);
		}
	}

	@Override
	public void initializeFocus() {
		tfEspecie.requestFocus();
	}

	@FXML
	private void mostrarContratos() {
		VentanaDialogoComponent.crearVentanaAviso("Funcionalidad no implementada", getStage());
	}

	@FXML
	private void accionAceptar(){
		log.debug("accionAceptar() - comprobando datos");
		
		ContratoAnimalDto contrato = rellenarContrato();
		
		if(contrato != null) {
			 HashMap<String, Object> datosModal = new HashMap<>();
			 datosModal.put(PARAM_CONTRATO, contrato);
			 datosModal.put(IskaypetFacturacionArticulosController.FIDELIZADO_CONTRATO, fidelizado);
			
			getApplication().getMainView().showModalCentered(ResumenContratoView.class, datosModal, getStage());
			
			Boolean contratoAceptado = (Boolean) datosModal.get(ResumenContratoController.PARAM_CONTRATO_ACEPTADO);
			if(contratoAceptado!=null && contratoAceptado) {
				iskLinea.setContratoAnimal(contrato);
				
				getStage().close();
			}
			
		}
		
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException {
		log.trace("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());

		switch (botonAccionado.getClave()) {

			case "ACCION_ACEPTAR":
				accionAceptar();
				break;
			case "ACCION_CANCELAR":
				accionCancelar();
				break;
			case "ACCION_CONTRATOS":
				mostrarContratos();
				break;
			default:
				break;
		}
	}

	private void rellenarFormularioContratoExistente(ContratoAnimalDto datos) {
		tfEspecie.setText(datos.getEspecie());
		tfRaza.setText(datos.getRaza());
		cbSexo.setValue(datos.getSexo());
		tfPeso.setText(datos.getPeso());

		tfNumIden.setText(datos.getNumIden());
	}

	private void limpiarFormulario() {
		tfEspecie.setText("");
		tfRaza.setText("");
		cbSexo.getSelectionModel().selectFirst();
		tfPeso.setText("");
		tfNumIden.setText("");
	}
	
	/**
	 * @return Contrato relleno, o null si hay algún fallo de validación
	 */
	private ContratoAnimalDto rellenarContrato() {
		//DatosCabeceraContrato contrato = new DatosCabeceraContrato()
		ContratoAnimalDto contrato = new ContratoAnimalDto();

		String especie = tfEspecie.getText();
		if (StringUtils.isBlank(especie)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo especie no puede estar vacio"), getStage());
			return null;
		}else {
			contrato.setEspecie(especie);
		}

		String raza = tfRaza.getText();
		if (StringUtils.isBlank(raza)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo raza no puede estar vacio"), getStage());
			return null;
		}else {
			contrato.setRaza(raza);
		}

		String sexo = cbSexo.getValue();
		if (StringUtils.isBlank(sexo)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo sexo no puede estar vacio"), getStage());
			return null;
		}else {
		contrato.setSexo(sexo);
		}

		String peso = tfPeso.getText();
		if (StringUtils.isNotBlank(peso)) {
			peso = tfPeso.getText().replace(",", ".");
			String[] partes = peso.split("\\.");
			int longitudEntera = partes[0].length();
			int longitudDecimal = partes.length > 1 ? partes[1].length() : 0;

			if (longitudEntera + longitudDecimal > 10 || longitudDecimal > 3) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El peso supera el límite establecido"), getStage());
				return null;
			}
			contrato.setPeso(peso);
		}


		String numIden = tfNumIden.getText();
			if(StringUtils.isBlank(numIden)) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo número identificación no puede estar vacio"), getStage());
				return null;
		}
		else {
			contrato.setNumIden(numIden);
		}
		
		return contrato;
		
	}

}
