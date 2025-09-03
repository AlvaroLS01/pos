package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.resumen;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.comerzzia.iskaypet.pos.devices.IskaypetFidelizacion;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.iskaypet.pos.gui.contrato.prefijos.ayuda.CargarPrefijosView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.ContratoAnimalController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.ContratoAnimalDto;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.prefijos.PrefijosPaisesKey;
import com.comerzzia.iskaypet.pos.services.evicertia.IskaypetEvicertiaService;
import com.comerzzia.iskaypet.pos.services.ventas.pagos.mascotas.ServicioContratoMascotas;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

@Component
public class ResumenContratoController extends WindowController implements Initializable {
	
	private static final Logger log = Logger.getLogger(ResumenContratoController.class.getName());
	
	public static final String PARAM_CONTRATO_ACEPTADO = "contratoAceptado";
	
	
	@FXML
	protected TextField tfEspecie, tfRaza, tfSexo, tfPeso, tfNumIdentificador;

	@FXML
	protected TextField tfFidNombre, tfFidApellidos, tfFidDNI, tfFidDireccion, tfFidCP, tfFidTelefono, tfFidEmail, tfPrefijo,tfCodPais;
	
	@FXML
	protected HBox hbCamposFidelizado;
	
	@FXML
	protected Separator separatorCamposFidelizado;
	
	@FXML
	protected Button btConfirmar, btModificar;
	
	@Autowired
	protected ServicioContratoMascotas servicioContratoMascotas;
	
	protected ContratoAnimalDto contrato;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		log.debug("inicializarComponentes()");
		registrarAccionCerrarVentanaEscape();

		btModificar.setDisable(getDatos().containsKey(IskaypetEvicertiaService.RECUPERA_CONTRATO));
		btModificar.setVisible(!getDatos().containsKey(IskaypetEvicertiaService.RECUPERA_CONTRATO));
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		contrato = (ContratoAnimalDto) getDatos().get(ContratoAnimalController.PARAM_CONTRATO);
		FidelizacionBean fidelizado = (FidelizacionBean) getDatos().get(IskaypetFacturacionArticulosController.FIDELIZADO_CONTRATO);
		
		limpiarCampos();
		
		//Se muestran u ocultan los campos dependiendo de si fidelizado viene con valor
		setVisibleCamposFidelizado(fidelizado!=null);
		
		rellenarFormulario(contrato, fidelizado);
	}
	
	@Override
	public void initializeFocus() {
		btConfirmar.requestFocus();
	}
	
	@FXML
	private void accionAceptar(){
		log.debug("accionAceptar()");
		//El prefijo es obligatorio para poder confirmar el contrato
		if(StringUtils.isBlank(tfPrefijo.getText()) || StringUtils.isBlank(tfCodPais.getText())) {
			String msgAviso = "Para finalizar el contrato debe asignar un prefijo al fidelizado.";
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(msgAviso), getStage());
			return;
		}
		getDatos().put(PARAM_CONTRATO_ACEPTADO, Boolean.TRUE);
		getStage().close();
	}
	
	private void limpiarCampos() {
		tfEspecie.clear();
		tfRaza.clear();
		tfSexo.clear();
		tfPeso.clear();
		tfNumIdentificador.clear();
		tfFidNombre.clear();
		tfFidApellidos.clear();
		tfFidDNI.clear();
		tfFidDireccion.clear();
		tfFidCP.clear();
		tfFidTelefono.clear();
		tfFidEmail.clear();
		tfPrefijo.clear();
		tfCodPais.clear();
	}
	
	private void rellenarFormulario(ContratoAnimalDto contrato, FidelizacionBean fidelizado) {
		tfEspecie.setText(contrato.getEspecie());
		tfRaza.setText(contrato.getRaza());
		tfSexo.setText(contrato.getSexo());
		tfPeso.setText(contrato.getPeso());
		tfNumIdentificador.setText(contrato.getNumIden());
		
		if(fidelizado != null) {
			tfFidNombre.setText(fidelizado.getNombre());
			tfFidApellidos.setText(fidelizado.getApellido());
			tfFidDNI.setText(fidelizado.getDocumento());
			tfFidCP.setText(fidelizado.getCp());
			tfFidDireccion.setText(fidelizado.getDomicilio());
			tfFidTelefono.setText("");
			tfFidEmail.setText("");

			Map<String, Object> adicionales = fidelizado.getAdicionales();
			if(adicionales!=null && !adicionales.isEmpty()) {

				if (adicionales.containsKey(IskaypetFidelizacion.PARAMETRO_EMAIL)) {
					tfFidEmail.setText((String) adicionales.get(IskaypetFidelizacion.PARAMETRO_EMAIL));
				}

				if (adicionales.containsKey(IskaypetFidelizacion.PARAMETRO_MOVIL)) {
					tfFidTelefono.setText((String) adicionales.get(IskaypetFidelizacion.PARAMETRO_MOVIL));
				}

				if (StringUtils.isBlank(tfFidTelefono.getText()) && adicionales.containsKey(IskaypetFidelizacion.PARAMETRO_TELEFONO)) {
					tfFidTelefono.setText((String) adicionales.get(IskaypetFidelizacion.PARAMETRO_TELEFONO));
				}
			}

			addFidelizadoContrato(contrato, fidelizado, tfFidTelefono.getText(), tfFidEmail.getText(), tfFidDireccion.getText());
		}
		
		addPrefijoPaisFidelizado(fidelizado.getCodPais(), contrato);
	}
	



	private void setVisibleCamposFidelizado(boolean visible) {
		hbCamposFidelizado.setVisible(visible);
		hbCamposFidelizado.setManaged(visible);
		separatorCamposFidelizado.setVisible(visible);
		separatorCamposFidelizado.setManaged(visible);
	}

	private void addFidelizadoContrato(ContratoAnimalDto datos, FidelizacionBean fidelizado, String tlf, String email, String direccion) {
		datos.setApellidos(fidelizado.getApellido());
		datos.setNombre(fidelizado.getNombre());
		datos.setCp(fidelizado.getCp());
		datos.setDocumento(fidelizado.getDocumento());
		datos.setDireccion(direccion);
		datos.setEmail(email);
		datos.setTlf(tlf);
		datos.setLocalidad(fidelizado.getLocalidad());
		datos.setPoblacion(fidelizado.getPoblacion());
		datos.setProvincia(fidelizado.getProvincia());
	}
	
	
	/* ########################################################################################################################### */
	/* ##################################### GAP 125 CONTRATO ANIMAL: PREFIJO TELEFÓNICO ######################################### */
	/* ########################################################################################################################### */
	
    @FXML
    void seleccionarPrefijo() {
    	getDatos().clear();
    	getApplication().getMainView().showModalCentered(CargarPrefijosView.class, getDatos(), this.getStage());
    	if(!getDatos().containsKey("cancelar")){
    		PrefijosPaisesKey prefijoSelect =  (PrefijosPaisesKey) getDatos().get("prefijoSelect");
    		tfPrefijo.setText(prefijoSelect.getPrefijo());
			tfCodPais.setText(prefijoSelect.getCodpais());
			contrato.setPrefijo(prefijoSelect.getPrefijo());
    	}
    }
    
	private void addPrefijoPaisFidelizado(String codPais, ContratoAnimalDto contrato) {

		List<PrefijosPaisesKey> prefijoFidelizado = servicioContratoMascotas.getPrefijoFidelizado(codPais);
		
		if(!prefijoFidelizado.isEmpty()) {
			tfPrefijo.setText(prefijoFidelizado.get(0).getPrefijo());
			tfCodPais.setText(prefijoFidelizado.get(0).getCodpais());
			contrato.setPrefijo(prefijoFidelizado.get(0).getPrefijo());
		}
		
	}

}
