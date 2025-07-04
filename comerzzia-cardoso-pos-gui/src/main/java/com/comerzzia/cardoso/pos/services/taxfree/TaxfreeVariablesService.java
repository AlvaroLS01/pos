package com.comerzzia.cardoso.pos.services.taxfree;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.model.config.variables.ConfigVariableBean;
import com.comerzzia.core.servicios.variables.ServicioVariablesImpl;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;

@Service
public class TaxfreeVariablesService {
	
	private static final Logger log = Logger.getLogger(TaxfreeVariablesService.class.getName());

	public final static String VARIABLE_TAXFREE_URL = "X_POS.TAXFREE_URL";
	public final static String VARIABLE_TAXFREE_USER = "X_POS.TAXFREE_USER";
	public final static String VARIABLE_TAXFREE_PASS = "X_POS.TAXFREE_PASS";
    public static final String CUSTACCOUNT = "X_POS.TAXFREE_CUSTACCOUNT";
    public final static String VARIABLE_TAXFREE_IMPORTE_PT = "X_POS.IMPORTE_TAX_FREE_PT";
    public static final String OPERATION_ID_CREATION = "Creation";
    public static final String OPERATION_ID_GET_DATA = "GetData";
	
	@Autowired
	protected VariablesServices variablesServices;
	
	@Autowired
	protected ServicioVariablesImpl servicioVariablesImpl;
	
	@Autowired
	protected Sesion sesion;
	
	public String getUrlServicio() throws Exception {
		String url = "";
		try{
			url = variablesServices.getVariableAsString(VARIABLE_TAXFREE_URL);
			if (StringUtils.isBlank(url)) {
				throw new Exception();
			}
		}
		catch(Exception e){
			String msgError = "La variable " + VARIABLE_TAXFREE_URL + " no se encontró o está mal definida.";
			log.error("getUrlServicio() - " + msgError + " : " + e.getMessage(), e);
			throw new Exception(msgError + e.getMessage(), e);
		}
		return url;
	}
	
	public String getUsuario() throws Exception{
		String usuario = "";
		try{
			usuario = variablesServices.getVariableAsString(VARIABLE_TAXFREE_USER);
			if (StringUtils.isBlank(usuario)) {
				throw new Exception();
			}
		}
		catch(Exception e){
			String msgError = "La variable " + VARIABLE_TAXFREE_USER + ", no se encontró o está mal definida.";
			log.error("getUsuarioToken() - " + msgError + " : " + e.getMessage(), e);
			throw new Exception(msgError + e.getMessage(), e);
		}
		return usuario;
	}
	
	public String getPassword() throws Exception{
		String password = "";
		try{
			password = variablesServices.getVariableAsString(VARIABLE_TAXFREE_PASS);
			if (StringUtils.isBlank(password)) {
				ConfigVariableBean variable = servicioVariablesImpl.consultarVariableDefinicion(VARIABLE_TAXFREE_PASS);
				password = variable.getValorDefecto();
			}
			if (StringUtils.isBlank(password)) {
				throw new Exception();
			}
		}
		catch(Exception e){
			String msgError = "La variable " + VARIABLE_TAXFREE_PASS + ", no se encontró o está mal definida.";
			log.error("getPasswordToken() - " + msgError + " : " + e.getMessage(), e);
			throw new Exception(msgError + e.getMessage(), e);
		}
		return password;
	}
	
	public String getCustAccount() throws Exception {
		String custAccount = "";
		try{
			custAccount = variablesServices.getVariableAsString(CUSTACCOUNT);
			if (StringUtils.isBlank(custAccount)) {
				throw new Exception();
			}
		}
		catch(Exception e){
			String msgError = "La variable " + CUSTACCOUNT + " no se encontró o está mal definida.";
			log.error("getCustAccount() - " + msgError + " : " + e.getMessage(), e);
			throw new Exception(msgError + e.getMessage(), e);
		}
		return custAccount;
	}
	
	public String getImporte() throws Exception{
		String importe = "";
		try {
			importe = variablesServices.getVariableAsString(VARIABLE_TAXFREE_IMPORTE_PT);
			if(StringUtils.isBlank(importe)) {
				throw new Exception();
			}
		}catch (Exception e) {
			String msgError = "La variable " + VARIABLE_TAXFREE_IMPORTE_PT + " no se encontró o está mal definida.";
			log.error("getImporte() - " + msgError + " : " + e.getMessage(), e);
			throw new Exception(msgError + e.getMessage(), e);
		}
		return importe;
	}
}
