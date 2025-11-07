package com.comerzzia.api.loyalty.service.cardsTypes;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.loyalty.persistence.cardsTypes.TipoTarjetaBean;
import com.comerzzia.api.loyalty.persistence.cardsTypes.TipoTarjetaExample;
import com.comerzzia.api.loyalty.persistence.cardsTypes.TipoTarjetaKey;
import com.comerzzia.api.loyalty.persistence.cardsTypes.CardsTypesMapper;
import com.comerzzia.api.loyalty.persistence.cardsTypes.activities.TipoActTarjetaBean;
import com.comerzzia.api.loyalty.persistence.cardsTypes.activities.CardsTypesActMapper;
import com.comerzzia.core.servicios.i18n.I18NService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class CardsTypesServiceImpl implements CardsTypesService {
	protected static Logger log = Logger.getLogger(CardsTypesServiceImpl.class);

	
	@Autowired
	CardsTypesMapper mapper;
	
	@Autowired
	CardsTypesActMapper tipoActTarjetaMapper;
	
	@Autowired
	I18NService i18NService;
	
	@Override
	public List<TipoTarjetaBean> selectByExample(TipoTarjetaExample example, IDatosSesion datosSesion) {
		return mapper.selectByExample(example);
	}
	
	@Override
	public List<TipoTarjetaBean> selectAll(IDatosSesion datosSesion) {
		TipoTarjetaExample example = new TipoTarjetaExample();	
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia());
		return selectByExample(example, datosSesion);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insert(TipoTarjetaBean record, IDatosSesion datosSesion) {
		record.setUidInstancia(datosSesion.getUidInstancia());
		
		mapper.insert(record);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateByPrimaryKey(TipoTarjetaBean record, IDatosSesion datosSesion) {
		i18NService.insertUpdateDelete(record, record.getCodtipotarj(), datosSesion);

		TipoActTarjetaBean activityInfo = new TipoActTarjetaBean();
		activityInfo.setUidActividad(datosSesion.getUidActividad());
		activityInfo.setCodtipotarj(record.getCodtipotarj());
				
		return mapper.updateByPrimaryKey(record);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteByPrimaryKey(TipoTarjetaKey key, IDatosSesion datosSesion) {
		i18NService.deleteByObjectId(TipoTarjetaBean.CLASE_TIPOTARJETA.concat(".%"), key.getCodtipotarj(), datosSesion);
		
		TipoActTarjetaBean activityInfo = new TipoActTarjetaBean();
		activityInfo.setUidActividad(datosSesion.getUidActividad());
		activityInfo.setCodtipotarj(key.getCodtipotarj());		
		tipoActTarjetaMapper.deleteByPrimaryKey(activityInfo);
		
		return mapper.deleteByPrimaryKey(key);
	}	
}
