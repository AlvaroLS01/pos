package com.comerzzia.api.loyalty.service.civilStates;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.api.loyalty.persistence.civilStates.EstadoCivilBean;
import com.comerzzia.api.loyalty.persistence.civilStates.EstadoCivilExample;
import com.comerzzia.api.loyalty.persistence.civilStates.EstadoCivilKey;
import com.comerzzia.api.loyalty.persistence.civilStates.EstadoCivilMapper;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public class CivilStatesServiceImpl implements CivilStatesService {	
	protected static Logger log = Logger.getLogger(CivilStatesServiceImpl.class);
	
	@Autowired
	EstadoCivilMapper mapper;
	
	@Override
	public List<EstadoCivilBean> selectByExample(EstadoCivilExample example, IDatosSesion datosSesion) {
		return mapper.selectByExample(example);
	}
	
	@Override
	public List<EstadoCivilBean> selectAll(IDatosSesion datosSesion) {
		EstadoCivilExample example = new EstadoCivilExample();	
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia());
		return selectByExample(example, datosSesion);
	}
	
	@Override
	public void insert(EstadoCivilBean record, IDatosSesion datosSesion) {
		record.setUidInstancia(datosSesion.getUidInstancia());
		mapper.insert(record);
	}
	
	@Override
	public int updateByPrimaryKey(EstadoCivilBean record, IDatosSesion datosSesion) {
		return  mapper.updateByPrimaryKey(record);
	}
	
	@Override
	public int deleteByPrimaryKey(EstadoCivilKey key, IDatosSesion datosSesion) {
		return  mapper.deleteByPrimaryKey(key);
	}

}
