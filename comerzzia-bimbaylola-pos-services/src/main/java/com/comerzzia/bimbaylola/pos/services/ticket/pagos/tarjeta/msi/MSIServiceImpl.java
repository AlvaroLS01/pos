package com.comerzzia.bimbaylola.pos.services.ticket.pagos.tarjeta.msi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.mediosPago.MediosPagoBIN;
import com.comerzzia.bimbaylola.pos.persistence.mediosPago.MediosPagoBINExample;
import com.comerzzia.bimbaylola.pos.persistence.mediosPago.MediosPagoBINMapper;

import jxl.common.Logger;

@Service
public class MSIServiceImpl implements MSIService {

	@Autowired
	protected MediosPagoBINMapper mapper;
	
	protected static Logger log = Logger.getLogger(MSIServiceImpl.class);

	@Override
	public List<MediosPagoBIN> consultar(String uidActividad, String codMedPag) throws MediosPagoBINNotFoundException, MediosPagoBINException {
		log.debug("consultar() - Consultando fechas de activación para el medio de pago: " + codMedPag);
		List<MediosPagoBIN> lstBin = new ArrayList<MediosPagoBIN>();

		if (StringUtils.isNotBlank(codMedPag)) {
			MediosPagoBINExample example = new MediosPagoBINExample();
			example.or().andUidActividadEqualTo(uidActividad).andCodmedpagEqualTo(codMedPag);

			lstBin = mapper.selectByExample(example);
		}
		
		log.debug("consultar() - Se han encontrado: " + lstBin.size() + " resultado/s");
		return lstBin;
	}

}
