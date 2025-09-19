package com.comerzzia.bimbaylola.pos.services.core.documentos.propiedades;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.pos.persistence.core.documentos.propiedades.PropiedadDocumentoBean;
import com.comerzzia.pos.services.core.documentos.propiedades.PropiedadesDocumentoService;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Service
public class ByLPropiedadesDocumentoService extends PropiedadesDocumentoService {

	protected static final Logger log = Logger.getLogger(ByLPropiedadesDocumentoService.class);

	public static final String TIPO_DOCUMENTO = "TIPO_DOCUMENTO";
	public static final String SIGNO_DOCUMENTO = "SIGNO_DOCUMENTO";
	
	@Autowired
	protected Sesion sesion;

	public String consultarPropiedad(Long idTipoDocumento, String propiedad) {
		List<PropiedadDocumentoBean> propiedadDocumentoList = consultarPropiedadesDocumentos(sesion.getAplicacion().getUidActividad(), propiedad);
		Optional<PropiedadDocumentoBean> propiedadDocumento = propiedadDocumentoList
				.stream()
				.filter(item -> item.getIdTipoDocumento()
				.equals(idTipoDocumento))
				.findFirst();

		return propiedadDocumento.isPresent() ? propiedadDocumento.get().getValor() : null;
	}
}
