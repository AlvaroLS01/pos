package com.comerzzia.api.loyalty.service.customers.access;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.loyalty.persistence.customers.FidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.FidelizadoKey;
import com.comerzzia.api.loyalty.persistence.customers.access.AccesoFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.access.AccesoFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.access.AccesoFidelizadoExample.Criteria;
import com.comerzzia.api.loyalty.persistence.customers.access.LoyalCustomerAccessMapper;
import com.comerzzia.api.loyalty.persistence.customers.contacts.TiposContactoFidelizadoBean;
import com.comerzzia.api.loyalty.service.customers.LoyalCustomersService;
import com.comerzzia.api.loyalty.service.customers.FidelizadoNotFoundException;
import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.core.servicios.notificaciones.NotificacionesFactory;
import com.comerzzia.core.servicios.notificaciones.generica.DestinatarioGenerico;
import com.comerzzia.core.servicios.notificaciones.generica.NotificacionGenerica;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.criptografia.CriptoException;
import com.comerzzia.core.util.criptografia.CriptoUtil;
import com.comerzzia.core.util.db.Database;

@Service
public class LoyalCustomerAccessServiceImpl implements LoyalCustomerAccessService {
	protected static Logger log = Logger.getLogger(LoyalCustomerAccessServiceImpl.class);

	@Autowired
	LoyalCustomerAccessMapper mapper;
	
	@Override
	public List<AccesoFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		AccesoFidelizadoExample example = new AccesoFidelizadoExample();
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.selectByExample(example);
	}
	
	@Override
	public int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		AccesoFidelizadoExample example = new AccesoFidelizadoExample();
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.deleteByExample(example);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insert(AccesoFidelizadoBean accesoFidelizadoBean, IDatosSesion datosSesion) {
		try {
			log.debug("crear() - Creando nuevo Acceso para un Fidelizado");
			accesoFidelizadoBean.setUidInstancia(datosSesion.getUidInstancia());			
			
			// Indicamos que la fecha del alta será la actual
			accesoFidelizadoBean.setFechaAlta(new Date());

			// Si se da de alta inactivo ponemos fecha de baja
			if (!accesoFidelizadoBean.isActivo()) {
				accesoFidelizadoBean.setFechaBaja(new Date());
			}

			// Indicamos el número de intentos fallidos de login
			accesoFidelizadoBean.setNumeroIntentosFallidos((short) 0);			

			mapper.insert(accesoFidelizadoBean);

			sendNotificacionNuevoUsuario(accesoFidelizadoBean, datosSesion, true);
		} catch (Exception e) {
			log.debug("crear() - deshaciendo transacción");

			String msg = "Error creando el acceso para el fidelizado: " + e.getMessage();
			log.error("crear() - " + msg);

			throw new AccesoFidelizadoException(msg, e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateByPrimaryKey(AccesoFidelizadoBean record, IDatosSesion datosSesion) {
		try {
			log.debug("modificar() - Modificando el acceso para un fidelizado");

			mapper.updateByPrimaryKey(record);
			
			sendNotificacionNuevoUsuario(record, datosSesion, false);
		} catch (Exception e) {

			String msg = "Error creando el acceso para el fidelizado: " + e.getMessage();
			log.error("modificar() - " + msg);

			throw new AccesoFidelizadoException(msg, e);
		}
	}
	
	protected String generatePassword(int longitud) {
		String clave = "";
		String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
		for (int i = 0; i < longitud; i++) {
			clave += (caracteres.charAt((int) (Math.random() * caracteres.length())));
		}
		return clave;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertForEmail(Long idFidelizado, String email, IDatosSesion datosSesion) {
		AccesoFidelizadoBean accesoFidelizadoBean = new AccesoFidelizadoBean();
		String clave = generatePassword(6);
		try {
			accesoFidelizadoBean.setClave(CriptoUtil.cifrar(CriptoUtil.ALGORITMO_MD5, clave.getBytes()));
		} catch (CriptoException e) {
			throw new RuntimeException(e);
		}
		accesoFidelizadoBean.setActivo(true);
		accesoFidelizadoBean.setEmail(email);
		accesoFidelizadoBean.setIdFidelizado(idFidelizado);
		accesoFidelizadoBean.setUsuario(email.replace("@", "").replace("_", ""));
		
		insert(accesoFidelizadoBean, datosSesion);
	}

	@Override
	public AccesoFidelizadoBean loginWithEmail(IDatosSesion datosSesion, String email, String clave)
			throws AccesoFidelizadoNotFoundException {
		AccesoFidelizadoBean accesoFidelizado = null;
		try {
			log.debug("consultar() - Consultando datos del acceso del fidelizado: " + email);
			AccesoFidelizadoExample accesoExample = new AccesoFidelizadoExample();
			accesoExample.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andEmailLikeInsensitive(email)
					.andClaveEqualTo(clave);
			List<AccesoFidelizadoBean> accesos = mapper.selectByExample(accesoExample);
			if (!accesos.isEmpty()) {
				accesoFidelizado = accesos.get(0);
			}

			if (accesoFidelizado == null) {
				String msg = "No se ha encontrado el acceso para el fidelizado con usuario: " + email;
				log.info("consultar() - " + msg);

				// Parámetros para actualizar los datos de acceso del fidelizado(incorrecto)
				accesoExample.clear();
				accesoExample.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andEmailLikeInsensitive(email);
				accesos = mapper.selectByExample(accesoExample);

				if (!accesos.isEmpty()) {
					accesoFidelizado = accesos.get(0);
				}

				if (accesoFidelizado != null) {
					accesoFidelizado.setFechaUltimoIntentoFallido(new Date());
					accesoFidelizado
							.setNumeroIntentosFallidos((short) (accesoFidelizado.getNumeroIntentosFallidos() + 1));
					updateByPrimaryKey(accesoFidelizado, datosSesion);

					String msgErrorPassword = "La contraseña para el fidelizado con usuario: " + email
							+ " es incorrecta";
					throw new AccesoFidelizadoException(msgErrorPassword);
				}
				throw new AccesoFidelizadoNotFoundException(msg);
			}

			// Parámetros para actualizar los datos de acceso del fidelizado(correcto)

			accesoFidelizado.setFechaUltimoLogin(new Date());
			accesoFidelizado.setFechaUltimoIntentoFallido(accesoFidelizado.getFechaUltimoIntentoFallido());
			accesoFidelizado.setNumeroIntentosFallidos((short) 0);
			updateByPrimaryKey(accesoFidelizado, datosSesion);

			return accesoFidelizado;
		} catch (Exception e) {
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar el acceso del fidelizado: " + e.getMessage();

			throw new AccesoFidelizadoException(mensaje, e);
		}
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updatePassword(IDatosSesion datosSesion, AccesoFidelizadoBean prevAccesoFidelizado)
			throws AccesoFidelizadoNotFoundException {
		log.debug("updatePassword() - Actualizando la contraseña de acceso del fidelizado: "
				+ prevAccesoFidelizado.getIdFidelizado());
		AccesoFidelizadoExample example = new AccesoFidelizadoExample();
		Criteria criteria = example.or();
		criteria.andUidInstanciaEqualTo(datosSesion.getUidInstancia())
				.andIdFidelizadoEqualTo(prevAccesoFidelizado.getIdFidelizado());

		List<AccesoFidelizadoBean> lstAccesoFidelizado = mapper.selectByExample(example);
		// No se ha encontrado ningun fidelizado con la uidInstancia y el idFidelizado
		// proporcionado
		if (lstAccesoFidelizado.isEmpty()) {
			throw new AccesoFidelizadoNotFoundException();
		}
		// Si la contraseña actual y la proporcionada en la petición coinciden se
		// renueva
		else {
			AccesoFidelizadoBean posAccesoFidelizado = lstAccesoFidelizado.get(0);
			posAccesoFidelizado.setClave(prevAccesoFidelizado.getNuevaClave());
			posAccesoFidelizado.setFechaModificacionClave(new Date());
			criteria.andClaveEqualTo(prevAccesoFidelizado.getClave());
			// Si el resultado del update es igual a cero no se ha modificado al no
			// coincider la contraseña actuales previa del acceso y posterior del acceso.
			if (mapper.updateByExample(posAccesoFidelizado, example) == 0) {
				throw new AccesoFidelizadoRenovacionContraseñaException();
			}
		}
	}


	@Override
	public int deleteByExample(AccesoFidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.deleteByExample(example);
	}

	@Override
	public AccesoFidelizadoBean selectUkByExample(AccesoFidelizadoExample example, IDatosSesion datosSesion)
			throws AccesoFidelizadoNotFoundException {
		AccesoFidelizadoBean accesoFidelizado = null;

		List<AccesoFidelizadoBean> accesos = mapper.selectByExample(example);
		if (!accesos.isEmpty()) {
			accesoFidelizado = accesos.get(0);
		}

		if (accesoFidelizado == null) {
			String msg = "No se ha encontrado el acceso para el fidelizado";
			log.info("consultarPorIdFidelizado() - " + msg);

			throw new AccesoFidelizadoNotFoundException(msg);
		}

		return accesoFidelizado;
	}


	protected void sendNotificacionNuevoUsuario(AccesoFidelizadoBean accesoFidelizado, IDatosSesion datosSesion,  Boolean esAlta) throws FidelizadoNotFoundException {
		log.debug(String.format("sendNotificacionNuevoUsuario() - " + "Enviando notificación"));
		
		LoyalCustomersService customersService = (LoyalCustomersService)ContextHolder.get().getBean(LoyalCustomersService.class);
		
		FidelizadoKey key = new FidelizadoKey();
		key.setUidInstancia(datosSesion.getUidInstancia());
		key.setIdFidelizado(accesoFidelizado.getIdFidelizado());
		
		FidelizadoBean fidelizado = customersService.selectByPrimaryKey(key, datosSesion);

		NotificacionGenerica notificacion = NotificacionesFactory.createNotificacion("NUEVO_USUARIO_FIDELIZADO",
				datosSesion.getCodEmpresa(), null, datosSesion);
		DestinatarioGenerico destinatario = notificacion.createDestinatario();
		destinatario.getObjetos().put("accesoFidelizado", accesoFidelizado);
		destinatario.getObjetos().put("esAlta", esAlta);
		notificacion.addDestinatario(destinatario);

		for (TiposContactoFidelizadoBean contacto : fidelizado.getTiposContacto()) {
			destinatario.addContacto(contacto.getCodTipoCon(), contacto.getValor());
		}

		notificacion.guardar(Database.getSqlSession());
	}
}