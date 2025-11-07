package com.comerzzia.api.loyalty.service.accounts.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.accounts.Account;
import com.comerzzia.api.loyalty.persistence.accounts.activities.AccountActivity;
import com.comerzzia.api.loyalty.persistence.accounts.activities.AccountActivityExample;
import com.comerzzia.api.loyalty.persistence.accounts.activities.AccountActivityExample.Criteria;
import com.comerzzia.api.loyalty.persistence.accounts.activities.AccountActivityMapper;
import com.comerzzia.api.loyalty.persistence.accounts.activities.ParametrosBuscarMovimientosBean;
import com.comerzzia.api.loyalty.persistence.cards.Card;
import com.comerzzia.api.loyalty.persistence.cards.CardDTO;
import com.comerzzia.api.loyalty.persistence.cards.CardKey;
import com.comerzzia.api.loyalty.persistence.cards.CardMapper;
import com.comerzzia.api.loyalty.service.accounts.AccountsService;
import com.comerzzia.api.loyalty.service.cards.CardsService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.servicios.ventas.tickets.TicketException;
import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.db.Database;
import com.comerzzia.core.util.mybatis.exception.PersistenceExceptionFactory;
import com.comerzzia.core.util.numeros.Numero;

@Service
public class AccountsActivitiesServiceImpl implements AccountsActivitiesService {	
    private static Logger log = Logger.getLogger(AccountsActivitiesServiceImpl.class);
		
	@Autowired
	ContadorMovimientos contadorMovimientos;
	
	@Autowired
	AccountsService accountsService;
	
	@Autowired
	CardsService cardsService;
	
	@Autowired
	AccountActivityMapper mapper;
	
	@Autowired
	CardMapper tarjetaMapper;
			
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#consultar(com.comerzzia.core.servicios.sesion.DatosSesionBean, org.apache.ibatis.session.SqlSession, com.comerzzia.api.loyalty.persistence.accounts.activities.ParametrosBuscarMovimientosBean)
	 */
	@Override
	public List<AccountActivity> consultar(IDatosSesion datosSesion, ParametrosBuscarMovimientosBean param){
		log.debug("consultar() - Consultando movimientos de la tarjeta");
		AccountActivityExample example = new AccountActivityExample(datosSesion);
		Criteria criteria = example.or();
		
		// ESTADO_MOVIMIENTO
		if(param.getEstado() != null){
			criteria.andMovimientoEstadoMovimientoEqualTo(param.getEstado());
		}
		// ID_TARJETA
		if (param.getIdTarjeta() != null ) {
			criteria.andMovimientoIdTarjetaEqualTo(param.getIdTarjeta());
		}
		// ID_CUENTA_TARJETA
		if (param.getIdCuenta() != null ) {
			criteria.andMovimientoIdCuentaTarjetaEqualTo(param.getIdCuenta());
		}
		// MOVIMIENTOS NO PROCESADOS
		if (param.isMovimientosNoProcesados()) {
			criteria.andFechaProcesoMovimientoIsNull();
		}
		// FECHA_DESDE
		if (param.getFechaDesde() != null) {
			criteria.andMovimientoFechaGreaterThanOrEqualTo(param.getFechaDesde());
		}
		// FECHA_HASTA
		//si tenemos fecha hasta la preparamos para que sea hasta las 23:59:59 del dia seleccionado
		if (param.getFechaHasta() != null) {
			Date d = param.getFechaHasta();
			
			Calendar fechaUsuario = Calendar.getInstance();
    		fechaUsuario.setTime(d);
    		fechaUsuario.set(Calendar.HOUR_OF_DAY, 23);
    		fechaUsuario.set(Calendar.MINUTE, 59);
    		fechaUsuario.set(Calendar.SECOND, 59);
    		
			criteria.andMovimientoFechaLessThanOrEqualTo(param.getFechaHasta());
		}
		//SALIDA
		if(param.isSoloSalida()){
			criteria.andSalidaNotEqualTo(0.0);
		}
		
		//ENTRADA
		if(param.isSoloEntrada()){
			criteria.andEntradaNotEqualTo(0.0);
		}
		
		example.setOrderByClause("MOVIMIENTO.FECHA DESC");
		
		return mapper.selectFromViewPaginacion(example, datosSesion.getUidActividad());
	}
	

	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#consultarTotalSalida(com.comerzzia.core.servicios.sesion.DatosSesionBean, com.comerzzia.api.loyalty.persistence.accounts.activities.ParametrosBuscarMovimientosBean)
	 */
	@Override
	public Double consultarTotalSalida(IDatosSesion datosSesion, ParametrosBuscarMovimientosBean param) {		
		AccountActivityExample example = new AccountActivityExample(datosSesion);
		Criteria criteria = example.or();
		
		// ESTADO_MOVIMIENTO
		if(param.getEstado() != null){
			criteria.andEstadoMovimientoEqualTo(param.getEstado());
		}
		// ID_TARJETA
		if (param.getIdTarjeta() != null ) {
			criteria.andIdTarjetaEqualTo(param.getIdTarjeta());
		}
		// ID_CUENTA_TARJETA
		if (param.getIdCuenta() != null ) {
			criteria.andIdCuentaTarjetaEqualTo(param.getIdCuenta());
		}
		// MOVIMIENTOS NO PROCESADOS
		if (param.isMovimientosNoProcesados()) {
			criteria.andFechaProcesoIsNull();
		}
		// FECHA_DESDE
		if (param.getFechaDesde() != null) {
			criteria.andFechaGreaterThanOrEqualTo(param.getFechaDesde());
		}
		// FECHA_HASTA
		//si tenemos fecha hasta la preparamos para que sea hasta las 23:59:59 del dia seleccionado
		if (param.getFechaHasta() != null) {
			Date d = param.getFechaHasta();
			
			Calendar fechaUsuario = Calendar.getInstance();
    		fechaUsuario.setTime(d);
    		fechaUsuario.set(Calendar.HOUR_OF_DAY, 23);
    		fechaUsuario.set(Calendar.MINUTE, 59);
    		fechaUsuario.set(Calendar.SECOND, 59);
    		
			criteria.andFechaLessThanOrEqualTo(param.getFechaHasta());
		}
		//SOLO POSITIVOS
		if(param.isSoloMovPositivos()){
			criteria.andSalidaGreaterThanOrEqualTo(0.0);
		}
		
		return mapper.consultarTotalSalida(example);
	
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#consultarTotalEntrada(com.comerzzia.core.servicios.sesion.DatosSesionBean, com.comerzzia.api.loyalty.persistence.accounts.activities.ParametrosBuscarMovimientosBean)
	 */
	@Override
	public Double consultarTotalEntrada(IDatosSesion datosSesion, ParametrosBuscarMovimientosBean param) {
		AccountActivityExample example = new AccountActivityExample(datosSesion);
		Criteria criteria = example.or();
		
		// ESTADO_MOVIMIENTO
		if(param.getEstado() != null){
			criteria.andEstadoMovimientoEqualTo(param.getEstado());
		}
		// ID_TARJETA
		if (param.getIdTarjeta() != null ) {
			criteria.andIdTarjetaEqualTo(param.getIdTarjeta());
		}
		// ID_CUENTA_TARJETA
		if (param.getIdCuenta() != null ) {
			criteria.andIdCuentaTarjetaEqualTo(param.getIdCuenta());
		}
		// MOVIMIENTOS NO PROCESADOS
		if (param.isMovimientosNoProcesados()) {
			criteria.andFechaProcesoIsNull();
		}
		// FECHA_DESDE
		if (param.getFechaDesde() != null) {
			criteria.andFechaGreaterThanOrEqualTo(param.getFechaDesde());
		}
		// FECHA_HASTA
		//si tenemos fecha hasta la preparamos para que sea hasta las 23:59:59 del dia seleccionado
		if (param.getFechaHasta() != null) {
			Date d = param.getFechaHasta();
			
			Calendar fechaUsuario = Calendar.getInstance();
    		fechaUsuario.setTime(d);
    		fechaUsuario.set(Calendar.HOUR_OF_DAY, 23);
    		fechaUsuario.set(Calendar.MINUTE, 59);
    		fechaUsuario.set(Calendar.SECOND, 59);
    		
			criteria.andFechaLessThanOrEqualTo(param.getFechaHasta());
		}
		
		return mapper.consultarTotalEntrada(example);
	}
		
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#consultarMovimientosNoProcesados(com.comerzzia.core.servicios.sesion.DatosSesionBean, org.apache.ibatis.session.SqlSession, java.lang.Long)
	 */
	@Override
	public List<AccountActivity> consultarMovimientosNoProcesados(IDatosSesion datosSesion, Long idCuenta) {
		log.debug("consultar() - Consultando movimientos no procesados de la cuenta con identificador: " + idCuenta);
		
		ParametrosBuscarMovimientosBean param = new ParametrosBuscarMovimientosBean();
		param.setIdCuenta(idCuenta);
		param.setMovimientosNoProcesados(true);
		return consultar(datosSesion, param);
	}
	
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#crear(org.apache.ibatis.session.SqlSession, com.comerzzia.api.loyalty.persistence.accounts.activities.MovimientoBean, com.comerzzia.core.servicios.sesion.DatosSesionBean)
	 */
	@Override
	public void insert(AccountActivity activity, IDatosSesion datosSesion) {
		insert(activity, null, datosSesion);
	}
	
	@Override
	public void insert(AccountActivity activity, Account sourceAccount, IDatosSesion datosSesion) {

		try {
			log.debug("insert() - Creando nuevo movimiento para la tarjeta con id " + activity.getIdTarjeta());
			
			//obtenemos la tarjeta para comprobar que esta activa, en caso de estar dada de baja no registramos ningún movimiento.
			CardKey tarjetaKey = new CardKey(datosSesion);
			tarjetaKey.setIdTarjeta(activity.getIdTarjeta());
			Card tarjeta = tarjetaMapper.selectByPrimaryKey(tarjetaKey);
			
			if (!tarjeta.isActivo()){
				throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_BAD_REQUEST, "La tarjeta se encuentra dada de baja.");
			}
			
			// Se comprueba si la tarjeta aún no está activada; no tiene fecha de activación
			if(tarjeta.getFechaActivacion() == null) {
				tarjeta.setFechaActivacion(new Date());
				
			}
			tarjeta.setFechaUltimoUso(new Date());
			tarjetaMapper.updateByPrimaryKey(tarjeta);
			
			// Obtener el ID del movimiento
			Long idMovimiento = contadorMovimientos.obtenerContador(datosSesion);
			activity.setIdCuentaMovimiento(idMovimiento);
			
			
			// Si el movimiento no tiene el ID de la cuenta de la tarjeta, crear la cuenta
			if(activity.getIdCuentaTarjeta() == null){
				Account cuenta = accountsService.insert(datosSesion, 0.0, 0.0);
				
				// Asignar la cuenta creada al movimiento
				activity.setIdCuentaTarjeta(cuenta.getIdCuentaTarjeta());
				
				tarjeta.setFechaActivacion(new Date());
				tarjeta.setIdCuentaTarjeta(cuenta.getIdCuentaTarjeta());
				tarjeta.setFechaUltimoUso(new Date());
				tarjetaMapper.updateByPrimaryKey(tarjeta);
			}
			
			// control del uid de transaccion
			if (activity.getUidTransaccion() == null) {
			   if (activity.getEstadoMovimiento() == AccountActivity.MOVIMIENTO_DEFINITIVO) {
				   activity.setUidTransaccion(UUID.randomUUID().toString());
			   } else if (activity.getEstadoMovimiento() == AccountActivity.MOVIMIENTO_PROVISIONAL) {
			      throw new MovimientoException("Un movimiento de tipo provisional debe tener asignado el uid de transacción");			      
			   }
			}
			
			// Insertar el movimiento
			activity.setUidInstancia(datosSesion.getUidInstancia());
			mapper.insert(activity);
			
						
			// Actualizar el saldo provisional de la cuenta
			accountsService.updateBalance(activity.getIdCuentaTarjeta(), sourceAccount, datosSesion); 			
		}
		
		catch (PersistenceException e) {
			String msg = "Error creando movimiento: " + e.getMessage();
			log.error(msg, e);
			if(PersistenceExceptionFactory.getPersistenceExpception(e).isKeyConstraintViolationException()) {
				throw new MovimientoConstraintViolationException(msg, e);
			}else{
				throw new MovimientoException(msg, e);
			}
		}
		catch(Exception e){
			String msg = "Error creando movimiento: " + e.getMessage();
			log.error(msg, e);
			throw new MovimientoException(msg, e);
		}
	}
		
	@Override
	public void updateByPrimaryKey(AccountActivity movimiento, IDatosSesion datosSesion) {
		mapper.updateByPrimaryKey(movimiento);
	}
	
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#modificarMovimiento(org.apache.ibatis.session.SqlSession, com.comerzzia.api.loyalty.persistence.accounts.activities.MovimientoBean, com.comerzzia.core.servicios.sesion.DatosSesionBean, java.lang.Integer)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void modificarMovimiento(AccountActivity movimiento, IDatosSesion datosSesion, Integer nuevoEstado) throws ApiException  {
		AccountActivityExample example = new AccountActivityExample(datosSesion);
		Criteria criteria = example.or();
		criteria.andIdCuentaMovimientoEqualTo(movimiento.getIdCuentaMovimiento())
				.andEstadoMovimientoEqualTo(movimiento.getEstadoMovimiento());
		
		movimiento.setEstadoMovimiento(nuevoEstado);
		
		//modificamos el estado del movimiento
		mapper.updateByExample(movimiento, example);
		
		//también hay que modificar el saldo de la cuenta ya que a este metodo se llega porque se ha pasado un movimiento de provisional a definitivo.
		//restamos en la cantidad provisional y sumamos en el saldo.
		Account cuenta = accountsService.selectById(movimiento.getIdCuentaTarjeta(), datosSesion);
		
		cuenta.setSaldo(Numero.redondea(cuenta.getSaldo() + Numero.redondea(movimiento.getEntrada() - movimiento.getSalida(), 3), 3));
		cuenta.setSaldoProvisional(Numero.redondea(cuenta.getSaldoProvisional() - Numero.redondea(movimiento.getEntrada() - movimiento.getSalida(), 3), 3));
		cuenta.setFechaActualizacion(new Date());
		
		accountsService.updateByPrimaryKey(cuenta,  datosSesion);
	}

	
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#consultar(com.comerzzia.core.servicios.sesion.DatosSesionBean, java.lang.String)
	 */
	@Override
	public AccountActivity consultar(IDatosSesion datosSesion, String uidTransaccion) {		
		log.debug("consultar() - Consultando movimiento con identificador de transacción: " + uidTransaccion);
		
		AccountActivityExample example = new AccountActivityExample(datosSesion);
		Criteria criteria = example.or();
		criteria.andUidTransaccionEqualTo(uidTransaccion);
		
		return mapper.consultarTransaccion(example);
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#consultarAnulacion(com.comerzzia.core.servicios.sesion.DatosSesionBean, java.lang.String, java.lang.String)
	 */
	@Override
	public AccountActivity consultarAnulacion(IDatosSesion datosSesion, String uidTransaccionBuscado, String cadenaAnulacion) {
		log.debug("consultar() - Consultando movimiento con identificador de transacción: " + uidTransaccionBuscado);
		AccountActivityExample example = new AccountActivityExample(datosSesion);
		Criteria criteria = example.or();
		criteria.andDocumentoEqualTo(uidTransaccionBuscado)
				.andConceptoEqualTo(cadenaAnulacion);
				
		return mapper.consultarTransaccion(example);
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#anularMovimientoProvisional(com.comerzzia.api.loyalty.persistence.accounts.activities.MovimientoBean, com.comerzzia.core.servicios.sesion.DatosSesionBean, com.comerzzia.core.util.db.Connection, com.comerzzia.core.util.mybatis.session.SqlSession)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void anularMovimientoProvisional(AccountActivity movimiento, IDatosSesion datosSesion) throws ApiException {
	    AccountActivityExample example = new AccountActivityExample(datosSesion);
	    Criteria criteria = example.or();
	    criteria.andIdCuentaMovimientoEqualTo(movimiento.getIdCuentaMovimiento())
	    		.andEstadoMovimientoEqualTo(movimiento.getEstadoMovimiento());
	    
	    movimiento.setEstadoMovimiento(AccountActivity.MOVIMIENTO_ANULADO);
	    
	    //modificamos el estado del movimiento
	    mapper.updateByExample(movimiento, example);
	    
	    //también hay que modificar el saldo de la cuenta ya que a este metodo se llega porque se ha pasado un movimiento de provisional a definitivo.
	    //restamos en la cantidad provisional y sumamos en el saldo.
	    Account cuenta = accountsService.selectById(movimiento.getIdCuentaTarjeta(), datosSesion);
	    
	    cuenta.setSaldoProvisional(Numero.redondea(cuenta.getSaldoProvisional() - Numero.redondea(movimiento.getEntrada() - movimiento.getSalida(), 3), 3));
	    cuenta.setFechaActualizacion(new Date());
	    
	    accountsService.updateByPrimaryKey(cuenta, datosSesion);
    }
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#modificarMovimientos(org.apache.ibatis.session.SqlSession, java.lang.String, java.lang.String, com.comerzzia.core.servicios.sesion.DatosSesionBean)
	 */
	@Override
	public void modificarMovimientos(String numeroTarjetaFidelizacion, String uidTransaccionTarjetaRegalo, IDatosSesion datosSesion) throws TicketException, ApiException {
		
		if ( numeroTarjetaFidelizacion != null && !numeroTarjetaFidelizacion.isEmpty()){
			// Hacemos definitivo el movimiento.
			String uidTransaccion = uidTransaccionTarjetaRegalo;
			if (uidTransaccion == null){
				throw new TicketException ("Error al hacer definitivo movimiento de tarjeta regalo. No se ha indicado uid de tranascción");
			}
			
			// Consultamos la tarjeta para comprobar que existe, esta activa y 
			CardDTO tarj = cardsService.selectByCardNumber(numeroTarjetaFidelizacion, datosSesion);
			
			if (!tarj.getTipoTarjeta().getPermitePago()){
				log.error("La tarjeta con número "+numeroTarjetaFidelizacion+ " no es de tipo regalo");
				throw new TicketException ("La tarjeta cuyo movimiento se intentan hacer definitivo es de tipo regalo");
			}				
			
			// Consultamos el movimiento para comprobar que existe (Para movimientos de carga de tarjeta regalo)
			AccountActivityExample movimientoExample = new AccountActivityExample(datosSesion);
			Criteria criteria = movimientoExample.or();
			criteria.andUidTransaccionEqualTo(uidTransaccion);
			
			AccountActivity mov = mapper.consultarTransaccion(movimientoExample);
			
			if (mov.getEstadoMovimiento() != AccountActivity.MOVIMIENTO_PROVISIONAL.shortValue()){
				log.error("El movimiento con uid de transacción "+uidTransaccion+ " ya es un movimiento definitivo");
				throw new TicketException ("El movimiento que se intenta procesar ya es un movimiento definitivo");
			}
			// hacemos el movimiento definitivo
			modificarMovimiento(mov, datosSesion, AccountActivity.MOVIMIENTO_DEFINITIVO);				
		}
		
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#anularMovimientosProvisionalesTicket(com.comerzzia.core.util.db.Connection, com.comerzzia.core.servicios.sesion.DatosSesionBean, java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void anularMovimientosProvisionalesTicket(Connection conn, IDatosSesion datosSesion, String uidTicket) throws ApiException {
		
		log.debug("anularMovimientosTicket() - Anulando movimientos que estén en estado provisional en el ticket " + uidTicket);
		
		AccountActivityExample example = new AccountActivityExample(datosSesion);
		example.or().andConceptoEqualTo("Ticket").andDocumentoEqualTo(uidTicket).andEstadoMovimientoEqualTo(AccountActivity.MOVIMIENTO_PROVISIONAL);
		List<AccountActivity> movimientosProvisionales = mapper.selectByExample(example);
		
		for(AccountActivity movimiento : movimientosProvisionales) {
			Long idCuentaTarjeta = movimiento.getIdCuentaTarjeta();
			
			//cambiamos el estado del movimiento a anulado
			movimiento.setEstadoMovimiento(AccountActivity.MOVIMIENTO_ANULADO);
			mapper.updateByPrimaryKey(movimiento);
			
			//actualizamos el saldo de la cuenta
			Account cuenta = accountsService.selectById(idCuentaTarjeta, datosSesion);
			cuenta.setSaldoProvisional(Numero.redondea(cuenta.getSaldoProvisional() - Numero.redondea(movimiento.getEntrada() - movimiento.getSalida(), 2), 2));
			cuenta.setFechaActualizacion(new Date());
			
			accountsService.updateByPrimaryKey(cuenta, datosSesion);
		}
    }

	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.accounts.activities.MovimientosCuentaService#crearMovimientos(java.util.List, com.comerzzia.core.servicios.sesion.DatosSesionBean)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void crearMovimientos(List<AccountActivity> movimientos, IDatosSesion datosSesion) {
		for(AccountActivity movimiento : movimientos) {
			log.debug("crearMovimientos() - Creando nuevo movimiento para la tarjeta con id " + movimiento.getIdTarjeta());
			insert(movimiento, datosSesion);
		}
    }

}