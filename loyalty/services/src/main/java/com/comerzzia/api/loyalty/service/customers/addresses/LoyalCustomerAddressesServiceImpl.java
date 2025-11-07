package com.comerzzia.api.loyalty.service.customers.addresses;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.loyalty.persistence.customers.addresses.DireccionFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.addresses.DireccionFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.addresses.DireccionFidelizadoKey;
import com.comerzzia.api.loyalty.persistence.customers.addresses.LoyalCustomerAddressesMapper;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class LoyalCustomerAddressesServiceImpl implements LoyalCustomerAddressesService {
	protected static Logger log = Logger.getLogger(LoyalCustomerAddressesServiceImpl.class);

	@Autowired
	LoyalCustomerAddressesMapper mapper;

	@Override
	public List<DireccionFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		DireccionFidelizadoExample example = new DireccionFidelizadoExample();
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.selectByExample(example);
	}
	
	@Override
	public int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		DireccionFidelizadoExample example = new DireccionFidelizadoExample();
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.deleteByExample(example);
	}	

	@Override
	public List<DireccionFidelizadoBean> selectByExample(DireccionFidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.selectByExample(example);
	}

	@Override
	public DireccionFidelizadoBean selectByPrimaryKey(DireccionFidelizadoKey key, IDatosSesion datosSesion) {
		return mapper.selectByPrimaryKey(key);

	}

	@Override
	public void deleteByPrimaryKey(DireccionFidelizadoKey key, IDatosSesion datosSesion) {
		mapper.deleteByPrimaryKey(key);
	}
	
	@Override
	public int deleteByExample(DireccionFidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.deleteByExample(example);
	}

	@Override
	public void updateByPrimaryKey(DireccionFidelizadoBean direccion, IDatosSesion datosSesion) {
		mapper.updateByPrimaryKey(direccion);
	}

	@Override
	public void insert(DireccionFidelizadoBean direccion, IDatosSesion datosSesion) {
		direccion.setUidInstancia(datosSesion.getUidInstancia());
		mapper.insert(direccion);
	}

	@Override
	public void modificarDescripcionDireccionFidelizado(String direccionOld, DireccionFidelizadoBean direccion,
			IDatosSesion datosSesion) {
		mapper.updateDescripcionDireccionFidelizado(datosSesion.getUidInstancia(),
				direccion.getIdFidelizado(), direccionOld, direccion.getDescripcionDireccion());
	}
}
