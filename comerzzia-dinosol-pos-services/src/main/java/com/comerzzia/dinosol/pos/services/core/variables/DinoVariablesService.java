package com.comerzzia.dinosol.pos.services.core.variables;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.core.variables.VariableBean;
import com.comerzzia.pos.persistence.core.variables.VariableExample;
import com.comerzzia.pos.persistence.core.variables.config.ConfigVariableBean;
import com.comerzzia.pos.persistence.core.variables.config.ConfigVariableExample;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;

@Primary
@Component
public class DinoVariablesService extends VariablesServices {

	@Autowired
	private Sesion sesion;

	public void recargarVariable(String idVariable) throws VariablesServiceException {
		try {
			log.debug("recargarVariable() - Recargando valor para variable: " + idVariable);

			ConfigVariableExample exampleConfigVariable = new ConfigVariableExample();
			exampleConfigVariable.or().andIdVariableEqualTo(idVariable).andIdVariableEqualTo(idVariable);
			
			VariableExample exampleVariable = new VariableExample();
			exampleVariable.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andIdVariableEqualTo(idVariable);
			
			ConfigVariableBean configVariablesBean = configVariableMapper.selectByExample(exampleConfigVariable).get(0);

			List<VariableBean> lstVariablesBean = variableMapper.selectByExample(exampleVariable);

			if (configVariablesBean != null) {
				variables.put(configVariablesBean.getIdVariable(), configVariablesBean.getValorDefecto());
				if (!lstVariablesBean.isEmpty() ) {
					VariableBean variablesBean = lstVariablesBean.get(0);
					if (variablesBean.getValor() != null && !variablesBean.getValor().isEmpty()) {
						variables.put(variablesBean.getIdVariable(), variablesBean.getValor());
					}
					else if (variablesBean.getValorDefecto() != null) {
						variables.put(variablesBean.getIdVariable(), variablesBean.getValorDefecto());
					}
					else {
						variables.put(variablesBean.getIdVariable(), "");
					}
				}
			}
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando variables en el sistema para uidActividad " + sesion.getAplicacion().getUidActividad() + ": " + e.getMessage();
			log.error("recargarVariable() - " + msg);
			throw new VariablesServiceException(e);
		}
	}
}
