package com.comerzzia.api.loyalty.service.customers;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.cards.Card;
import com.comerzzia.api.loyalty.persistence.cards.CardExample;
import com.comerzzia.api.loyalty.persistence.customers.FidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.FidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.FidelizadoKey;
import com.comerzzia.api.loyalty.persistence.customers.LoyalCustomerMapper;
import com.comerzzia.api.loyalty.service.cards.CardsService;
import com.comerzzia.api.loyalty.service.cardsTypes.CardsTypesService;
import com.comerzzia.api.loyalty.service.customers.access.LoyalCustomerAccessService;
import com.comerzzia.api.loyalty.service.customers.addresses.LoyalCustomerAddressesService;
import com.comerzzia.api.loyalty.service.customers.collectives.LoyalCustomerCollectivesService;
import com.comerzzia.api.loyalty.service.customers.contacts.LoyalCustomerContactsService;
import com.comerzzia.api.loyalty.service.customers.links.LoyalCustomerLinksService;
import com.comerzzia.api.loyalty.service.customers.personsRelations.LoyalCustomerPersonsRelationsService;
import com.comerzzia.api.loyalty.service.customers.wishlists.LoyalCustomerWishListsService;
import com.comerzzia.core.model.etiquetas.categorias.EtiquetaBean;
import com.comerzzia.core.model.etiquetas.enlaces.EtiquetaEnlaceBean;
import com.comerzzia.core.model.etiquetas.enlaces.EtiquetaEnlaceExample;
import com.comerzzia.core.servicios.contadores.ContadorException;
import com.comerzzia.core.servicios.etiquetas.EtiquetasException;
import com.comerzzia.core.servicios.etiquetas.EtiquetasService;
import com.comerzzia.core.servicios.etiquetas.enlaces.EtiquetasEnlacesConstraintViolationException;
import com.comerzzia.core.servicios.etiquetas.enlaces.EtiquetasEnlacesException;
import com.comerzzia.core.servicios.etiquetas.enlaces.EtiquetasEnlacesService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.servicios.variables.Variables;
import com.comerzzia.core.servicios.variables.VariablesService;
import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.mybatis.exception.PersistenceExceptionFactory;

@Service
public class LoyalCustomersServiceImpl implements LoyalCustomersService {
	protected static Logger log = Logger.getLogger(LoyalCustomersServiceImpl.class);
	protected static String ID_CLASE = "F_FIDELIZADOS_TBL.ID_FIDELIZADO";
		
	@Autowired
	LoyalCustomerMapper mapper;
	
	@Autowired
	VariablesService variablesService;
	
	@Autowired
	EtiquetasEnlacesService etiquetasEnlacesService;
	
	@Autowired	
	EtiquetasService etiquetasService;
	
	@Autowired
	CardsService cardsService;
	
	@Autowired
	CardsTypesService cardsTypesService;
	
	@Autowired
	LoyalCustomerAccessService customerAccessService;
	
	@Autowired
	LoyalCustomerAddressesService customerAddressesService;
	
	@Autowired
	LoyalCustomerCollectivesService customerCollectivesService;
	
	@Autowired
	LoyalCustomerContactsService customerContactsService;
	
	@Autowired
	LoyalCustomerLinksService customerLinksService;
	
	@Autowired
	LoyalCustomerPersonsRelationsService customerPersonsRelationsService;	
	
	@Autowired
	LoyalCustomerWishListsService customerWishListsService;
	
	@Autowired
	ContadorFidelizados contadorFidelizados;

	@Override
	public List<FidelizadoBean> selectByExample(FidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.selectByExample(example);
	}
	
	@Override
	public FidelizadoBean selectByPrimaryKey(FidelizadoKey key, IDatosSesion datosSesion) throws FidelizadoNotFoundException {
		FidelizadoBean cliente = mapper.selectFromViewByPrimaryKey(key);

		if (cliente == null) {
			String msg = "No se ha encontrado el fidelizado con identificador: " + key.getIdFidelizado();
			log.info("consultar() - " + msg);

			throw new FidelizadoNotFoundException(msg);
		}

		cliente.setTiposContacto(customerContactsService.selectByCustomer(key.getIdFidelizado(), datosSesion));
		cliente.setPersonasRelacionadas(customerPersonsRelationsService.selectByCustomer(key.getIdFidelizado(), datosSesion));
		cliente.setDirecciones(customerAddressesService.selectByCustomer(key.getIdFidelizado(), datosSesion));
		cliente.setColectivos(customerCollectivesService.selectByCustomer(key.getIdFidelizado(), datosSesion));
		
		try {
			List<EtiquetaBean> etiquetas = etiquetasService.consultarEtiquetasPorClaseYObjeto("F_FIDELIZADOS_TBL.ID_FIDELIZADO", String.valueOf(cliente.getIdFidelizado()), datosSesion);
			cliente.setEtiquetasCategorias(etiquetas);
			cliente.setEtiquetasCategoriasCargados(true);
		}
		catch (EtiquetasException e) {
			log.error("consultar() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}

		return cliente;

	}
	
	public FidelizadoBean selectByUniqueKey(String loyalCustomerCode, IDatosSesion sessionData) throws FidelizadoNotFoundException {
		FidelizadoExample example = new FidelizadoExample();
		example.or().andUidInstanciaEqualTo(sessionData.getUidInstancia()).andCodFidelizadoEqualTo(loyalCustomerCode);
		
		List<FidelizadoBean> fidelizados = selectByExample(example, sessionData);
		
		if(fidelizados.isEmpty()) {
			throw new FidelizadoNotFoundException();
		}
		return fidelizados.get(0);
	}
		
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insert(FidelizadoBean fidelizado, IDatosSesion datosSesion, Connection conn) throws ContadorException {
		fidelizado.setUidInstancia(datosSesion.getUidInstancia());
		
		// Establecer el identificador único
		fidelizado.setIdFidelizado(contadorFidelizados.obtenerContadorId(datosSesion));

		if (fidelizado.getCodFidelizado() == null) {
			fidelizado.setCodFidelizado(fidelizado.getIdFidelizado().toString());
		}

		// Indicamos que la fecha del alta será la actual
		fidelizado.setFechaAlta(new Date());

		// Si se da de alta inactivo ponemos fecha de baja
		if (!fidelizado.isActivo()) {
			fidelizado.setFechaBaja(new Date());
		}

		mapper.insert(fidelizado);
		
		Boolean altaAutoUsuario = variablesService.consultarValorAsBoolean(datosSesion, Variables.FIDELIZACION_ALTA_AUTOMATICA_USUARIO, false);
		
		if (altaAutoUsuario && fidelizado.getTipoContacto("EMAIL") != null) {
			customerAccessService.insertForEmail(fidelizado.getIdFidelizado(), fidelizado.getTipoContacto("EMAIL").getValor(), datosSesion);
		}		
	}
	
	@Override
	public void updateByPrimaryKey(FidelizadoBean fidelizado, IDatosSesion datosSesion)  {
		log.debug("modificar() - Modificando fidelizado con ID " + fidelizado.getIdFidelizado());

		try {
			// Establecer la fecha de modificación
			fidelizado.setFechaModificacion(new Date());

			// Si el cliente está inactivo y antes no tenía fecha de baja se
			// pone la de hoy
			if (!fidelizado.isActivo() && fidelizado.getFechaBaja() == null) {
				fidelizado.setFechaBaja(new Date());
			}
			// Si el cliente está activo y antes estaba de baja se pone la fecha
			// de baja a nula
			else if (fidelizado.isActivo() && fidelizado.getFechaBaja() != null) {
				fidelizado.setFechaBaja(null);
			}

			mapper.updateByPrimaryKey(fidelizado);
		}
		catch (PersistenceException e) {
			log.info("modificar() - No se ha podido modificar el fidelizado: " + e.getMessage());
			if (PersistenceExceptionFactory.getPersistenceExpception(e).isKeyConstraintViolationException()) {
				throw new FidelizadoConstraintViolationException("Ya existe un fidelizado con el mismo identificador");
			}
		}
	}	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteByPrimaryKey(FidelizadoKey key, IDatosSesion datosSesion) throws ApiException {
		try {
			log.debug("eliminar() - Eliminando fidelizado con ID " + key.getIdFidelizado());
						
			// Borramos los colectivos
			customerCollectivesService.deleteByCustomer(key.getIdFidelizado(), datosSesion);

			// Borramos los tipo de contacto
			customerContactsService.deleteByCustomer(key.getIdFidelizado(), datosSesion);

			// Eliminando el acceso del fidelizado
			customerAccessService.deleteByCustomer(key.getIdFidelizado(), datosSesion);
						
			//Eliminando listas de deseo del fidelizado
			customerWishListsService.deleteByCustomer(key.getIdFidelizado(), datosSesion);			

			// Borramos las etiquetas que no esten enlazadas
			EtiquetaEnlaceExample etiquetaEnlaceExample = new EtiquetaEnlaceExample();
			etiquetaEnlaceExample.or().andUidActividadEqualTo(datosSesion.getUidActividad()).andIdClaseEqualTo(ID_CLASE);
			
			for (EtiquetaEnlaceBean etiquetaEnlace : etiquetasEnlacesService.consultar(etiquetaEnlaceExample)) {
				etiquetasEnlacesService.eliminar(etiquetaEnlace, datosSesion);
			}

			// Borramos las personas relacionadas
			customerPersonsRelationsService.deleteByCustomer(key.getIdFidelizado(), datosSesion);

			// Borramos las direcciones del fidelizado
			customerAddressesService.deleteByCustomer(key.getIdFidelizado(), datosSesion);			

			// Dar de baja las tarjetas
			CardExample example = new CardExample(datosSesion);
			example.or().andIdFidelizadoEqualTo(key.getIdFidelizado()).andFechaBajaIsNull();
			for (Card tarjeta : cardsService.selectByExample(example)) {
				cardsService.deactivate(tarjeta);
			}
			
			// Borrar enlaces
			customerLinksService.deleteByCustomer(key.getIdFidelizado(), datosSesion);
			
			mapper.deleteByPrimaryKey(key);
		}
		catch (EtiquetasEnlacesException e) {
		   throw new RuntimeException(e);
		}
		catch (EtiquetasEnlacesConstraintViolationException e) {
			log.info("eliminar() - No se ha podido eliminar el fidelizado: " + e.getMessage());
			
            throw new FidelizadoConstraintViolationException(e.getMessage());
		}
		catch (PersistenceException e) {
			log.info("eliminar() - No se ha podido eliminar el fidelizado: " + e.getMessage());
			
			if (PersistenceExceptionFactory.getPersistenceExpception(e).isForeingKeyConstraintViolationException()) {
				throw new FidelizadoConstraintViolationException(e.getMessage());
			}
		}
	}
}
