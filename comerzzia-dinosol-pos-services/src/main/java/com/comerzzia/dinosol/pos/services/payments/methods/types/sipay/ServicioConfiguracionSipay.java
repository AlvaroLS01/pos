package com.comerzzia.dinosol.pos.services.payments.methods.types.sipay;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.model.config.variables.ConfigVariableBean;
import com.comerzzia.core.model.variables.VariableBean;
import com.comerzzia.core.persistencia.config.variables.ConfigVariableMapper;
import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.servicios.variables.VariableConstraintViolationException;
import com.comerzzia.core.servicios.variables.VariableException;
import com.comerzzia.core.servicios.variables.VariableNotFoundException;
import com.comerzzia.core.servicios.variables.VariablesService;
import com.comerzzia.dinosol.librerias.sipay.client.constants.SipayConstants;
import com.comerzzia.dinosol.pos.services.core.variables.DinoVariablesService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServiceException;

@Component
public class ServicioConfiguracionSipay {

	protected static Logger log = Logger.getLogger(ServicioConfiguracionSipay.class);
	public static final String X_POS_SIPAY_MODO_OFFLINE = "X_POS.SIPAY_MODO_OFFLINE";
	@Autowired
	private VariablesService variablesService;
	@Autowired
	private DinoVariablesService variablesPos;
	@Autowired
	private ConfigVariableMapper confiVariableMapper;
	@Autowired
	private Sesion sesion;
	private DatosSesionBean datosSesion;
	@Autowired
	private SipayService sipayService;

	public void validarPassword(String pass) throws Exception {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
			Date fechaActual = new Date();
			String valorFecha = sdf.format(fechaActual);
			BigDecimal resultadoPass = new BigDecimal(valorFecha).add(new BigDecimal(sesion.getAplicacion().getCodAlmacen()));
			if (StringUtils.equals(resultadoPass.toString(), pass)) {
				operativaOffline(true);
			}
			else {
				throw new Exception("Error al validar la contraseña");
			}
		}
		catch (Exception e) {
			log.error("validarPassword() - Error al validar la contraseña");
			throw new Exception("Error al realizar la validación de la contraseña", e);
		}
	}

	private void crearDVariable(DatosSesionBean datosSesion, String valor) throws VariableException, VariableConstraintViolationException {
		VariableBean dVariable = new VariableBean();
		dVariable.setIdVariable(X_POS_SIPAY_MODO_OFFLINE);
		dVariable.setPeso(100L);
		dVariable.setValor(valor);
		variablesService.crear(datosSesion, dVariable);
	}
	public boolean esModoOfflineActivado() {
		return variablesPos.getVariableAsBoolean(ServicioConfiguracionSipay.X_POS_SIPAY_MODO_OFFLINE);
	}

	public DatosSesionBean getDatosSesion() {
		if (datosSesion == null) {
			try {
				datosSesion = new DatosSesionBean();
				datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
				datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
			}
			catch (EmpresaException e) {
				log.error("getDatosSesion() - Error creaando los datos sesión.");
			}
		}
		return datosSesion;
	}

	public void recargarVariableOffline() throws VariablesServiceException {
		variablesPos.recargarVariable(ServicioConfiguracionSipay.X_POS_SIPAY_MODO_OFFLINE);
	}

	public VariableBean comprobarConfigVariable() {
		VariableBean variableModoOffline = null;
		try {
			datosSesion = getDatosSesion();
			variableModoOffline = variablesService.consultar(datosSesion, X_POS_SIPAY_MODO_OFFLINE);
		}
		catch (Exception e) {
			log.error("No se encuentra la definición de la variable y la creamos");
			// Creamos la variable en el config con valor a N en el caso de no existir
			if (confiVariableMapper.selectByPrimaryKey(X_POS_SIPAY_MODO_OFFLINE) == null) {
				ConfigVariableBean variableConfig = new ConfigVariableBean();
				variableConfig.setIdVariable(X_POS_SIPAY_MODO_OFFLINE);
				variableConfig.setValorDefecto("N");
				variableConfig.setEnvioATienda("N");
				variableConfig.setDescripcion("Indica si está activado el modo offline en Sipay");
				confiVariableMapper.insert(variableConfig);
			}
		}
		return variableModoOffline;
	}

	public void comprobarDVariable(VariableBean configVariable) throws VariableException, VariableConstraintViolationException, VariableNotFoundException {
		if (configVariable == null) {
			configVariable = variablesService.consultar(datosSesion, X_POS_SIPAY_MODO_OFFLINE);
		}
		if (variablesService.modificar(datosSesion, configVariable) == 0) {
			crearDVariable(datosSesion, configVariable.getValor());
		}
	}

	public void operativaOffline(boolean cambioUsuario) {
		try {
			VariableBean configVariable = comprobarConfigVariable();
			if (cambioUsuario) {
				String valor = variablesPos.getVariableAsBoolean(X_POS_SIPAY_MODO_OFFLINE) ? "N" : "S";
				configVariable.setValor(valor);
			}
			comprobarDVariable(configVariable);
			recargarVariableOffline();
			cambiarModoOffline();
		}
		catch (Exception e) {
			log.error("operativaOffline() - Ha ocurrido un error cambiado el  estado offline: " + e.getMessage(), e);
		}
	}

	private void cambiarModoOffline() throws Exception {
		if (variablesPos.getVariableAsBoolean(ServicioConfiguracionSipay.X_POS_SIPAY_MODO_OFFLINE)) {
			sipayService.cambiarModoOfflineSipay(SipayConstants.ACTIVATE_OFFLINE_MODE);
			log.info("operativaOffline() - Ha sido activado el modo OFFLINE de SiPay");
		}
		else {
			sipayService.cambiarModoOfflineSipay(SipayConstants.DESACTIVATE_OFFLINE_MODE);
			log.info("operativaOffline() - Ha sido activado el modo ONLINE de SiPay");
		}
	}
}
