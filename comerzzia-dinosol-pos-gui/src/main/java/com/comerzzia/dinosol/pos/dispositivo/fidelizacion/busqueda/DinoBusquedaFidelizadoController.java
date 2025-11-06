package com.comerzzia.dinosol.pos.dispositivo.fidelizacion.busqueda;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.fxml.FXML;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.model.fidelizacion.fidelizados.FidelizadoBean;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.BusquedaFidelizadoController;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.FormularioBusquedaFidelizadoBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;
import com.comerzzia.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.rest.client.fidelizados.FidelizadosRest;

@Primary
@Component
public class DinoBusquedaFidelizadoController extends BusquedaFidelizadoController{
	

	private static final Logger log = Logger.getLogger(DinoBusquedaFidelizadoController.class);
	
	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	private Sesion sesion;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		frBuscaFidelizado = SpringContext.getBean(FormularioBusquedaFidelizadoBean.class);
		frBuscaFidelizado.setFormField("codTarjRegalo", tfNumero);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		
		tfNumero.setText("");
		String texto = (String) getDatos().get(PARAMETRO_IN_TEXTOCABECERA);
		if (texto == null) {
			texto = I18N.getTexto("Lea o escriba el código de barras de la tarjeta");
		}
		lbTitulo.setText(texto);
	}
	

	@Override
	public void initializeComponents() {

		registrarAccionCerrarVentanaEscape();
		
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfNumero.setUserData(keyboardDataDto);
	}

	
	@FXML
	public void accionAceptar() {
		if (StringUtils.isBlank(tfNumero.getText())) {

			VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(I18N.getTexto("Es necesario indicar el número de tarjeta"), getStage());
		}
		else {
			frBuscaFidelizado.setCodTarjRegalo(tfNumero.getText());
			// Validamos el formulario
			Set<ConstraintViolation<FormularioBusquedaFidelizadoBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBuscaFidelizado);
			if (constraintViolations.size() >= 1) {
				ConstraintViolation<FormularioBusquedaFidelizadoBean> next = constraintViolations.iterator().next();
				frBuscaFidelizado.setErrorStyle(next.getPropertyPath(), true);
				frBuscaFidelizado.setFocus(next.getPropertyPath());
				VentanaDialogoComponent.crearVentanaError(next.getMessage(), this.getStage());
			}
			else {
				ConsultarFidelizadoRequestRest req = new ConsultarFidelizadoRequestRest(variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY), sesion.getAplicacion()
				        .getUidActividad());
				req.setNumeroTarjeta(frBuscaFidelizado.getCodTarjRegalo());

				String tarjeta = "";

				if (frBuscaFidelizado.getCodTarjRegalo() != null && !frBuscaFidelizado.getCodTarjRegalo().isEmpty()) {
					tarjeta = frBuscaFidelizado.getCodTarjRegalo();
					getDatos().put(PARAMETRO_NUM_TARJETA, tarjeta);
					getStage().close();
				}
				else {
					FidelizadoBean fidelizado = null;
					try {
						List<FidelizadoBean> fidelizados = FidelizadosRest.getFidelizadosDatos(req);
						if (fidelizados.size() == 0) {
							VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(I18N.getTexto("No se ha encontrado ningún fidelizado con los datos introducidos"), getStage());
							tarjeta = null;
						}
						else {

							if (fidelizados.size() == 1) {
								fidelizado = fidelizados.get(0);
								getStage().close();
							}

							else if (fidelizados.size() > 1) {

								getDatos().put(PARAMETRO_FIDELIZADOS_SELECCION, fidelizados);
								accionSeleccionarFidelizado();
								fidelizado = (FidelizadoBean) getDatos().get(PARAMETRO_FIDELIZADO_SELECCIONADO);
							}
							if (fidelizado != null) {
								tarjeta = fidelizado.getNumeroTarjeta();
								if (StringUtils.isBlank(tarjeta)) {
									VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(I18N.getTexto("El fidelizado no dispone de una tarjeta"), getStage());
								}
								else if (!StringUtils.isBlank(tarjeta)) {

									getDatos().put(PARAMETRO_NUM_TARJETA, tarjeta);
									getStage().close();
								}
							}

							else {
								if (!StringUtils.isBlank(tarjeta)) {

									getDatos().put(PARAMETRO_NUM_TARJETA, tarjeta);
									getStage().close();
								}
							}
						}

					}
					catch (RestException | RestHttpException e) {
						log.error("accionAceptar() - ha habido un problema con la petición REST: " + e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido establecer la conexión con la central, revise que la conexión es adecuada."), e);
					}
					catch (Exception e) {
						log.error("accionAceptar() - Ha ocurrido un error: " + e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha ocurrido un error durante la petición a la central, consulte con el administrador."), e);
					}

				}

			}
		}
	}


}
