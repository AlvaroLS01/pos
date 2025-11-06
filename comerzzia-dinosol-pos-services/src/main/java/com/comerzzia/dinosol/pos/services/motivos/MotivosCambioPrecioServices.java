package com.comerzzia.dinosol.pos.services.motivos;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.persistence.motivos.MotivosCambioPrecio;
import com.comerzzia.dinosol.pos.persistence.motivos.MotivosCambioPrecioExample;
import com.comerzzia.dinosol.pos.persistence.motivos.MotivosCambioPrecioExample.Criteria;
import com.comerzzia.dinosol.pos.persistence.motivos.MotivosCambioPrecioMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Component
public class MotivosCambioPrecioServices {

	private Logger log = Logger.getLogger(MotivosCambioPrecioServices.class);

	@Autowired
	private MotivosCambioPrecioMapper motivosMapper;

	@Autowired
	private Sesion sesion;

	public List<MotivosCambioPrecio> consultar() throws Exception {
		log.debug("consultar() - Consultando motivos de cambio de precio");
		List<MotivosCambioPrecio> motivos = new ArrayList<MotivosCambioPrecio>();

		try {

			MotivosCambioPrecioExample motivosExample = new MotivosCambioPrecioExample();

			Criteria crit = motivosExample.createCriteria();

			crit.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());

			motivos = motivosMapper.selectByExample(motivosExample);
			
			if (motivos == null || motivos.isEmpty()) {
				throw new NotFoundException("No se ha encontrado ningún motivo");
			}
			
			return motivos;
		}
		catch (Exception e) {
			log.error("consultar() - Se ha producido un error consultando los motivos de cambio de precio: " + e.getMessage(), e);
			throw e;
		}
	}
	
	public MotivosCambioPrecio consultarPorCodigo(String codMotivo) throws Exception {
		log.debug("consultar() - Consultando Motivo: " + codMotivo);
		
		MotivosCambioPrecio motivo = null;
		
		try {
			MotivosCambioPrecioExample motivosExample = new MotivosCambioPrecioExample();

			Criteria crit = motivosExample.createCriteria();

			crit.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());

			if (codMotivo != null) {
				crit.andCodMotivoEqualTo(codMotivo);
			}

			List<MotivosCambioPrecio> motivos = motivosMapper.selectByExample(motivosExample);
			if (motivos == null || motivos.isEmpty()) {
				throw new NotFoundException("No se ha encontrado ningún motivo");
			}
			motivo = motivos.get(0);
		}
		catch (NotFoundException e) {
			log.debug("consultarPorCodigo() - No se ha encontrado ningún motivo", e);
			throw e;
		}
		catch (Exception e) {
			log.error("consultarPorCodigo() - Se ha producido un error consultando motivos", e);
			throw e;
		}
		return motivo;
	}

}
