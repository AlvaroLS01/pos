package com.comerzzia.dinosol.pos.services.ventas.paneles;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ventas.paneles.ArticulosPanelesService;

@Component
@Primary
public class DinoArticulosPanelesService extends ArticulosPanelesService {
	
	private static final String ID_VARIABLE_TIPO_TIENDA = "X_TIPO_TIENDA";
	
	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private Sesion sesion;

	@Override
	protected File getPlantillaExcel() {
		File file = searchFile(sesion.getAplicacion().getCodAlmacen() + ".xls");
		if(file != null) {
			return file;
		}
		else {
			file = searchFile(variablesServices.getVariableAsString(ID_VARIABLE_TIPO_TIENDA) + ".xls");
		}
	    return file;
    }

}
