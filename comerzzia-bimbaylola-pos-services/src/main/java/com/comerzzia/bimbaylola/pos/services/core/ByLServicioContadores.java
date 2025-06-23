package com.comerzzia.bimbaylola.pos.services.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.ByLConfigContadorBean;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRango;
import com.comerzzia.bimbaylola.pos.persistence.core.contadores.ByLContadorBean;
import com.comerzzia.bimbaylola.pos.persistence.core.contadores.ByLContadorKey;
import com.comerzzia.bimbaylola.pos.persistence.core.contadores.ByLContadorMapper;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.ContadoresConfigInvalidException;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.parametros.ConfigContadoresParametrosInvalidException;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos.ServicioConfigContadoresRangos;
import com.comerzzia.core.util.base.Estado;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.config.configcontadores.ConfigContadorBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.parametros.ConfigContadorParametroBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.mybatis.SpringTransactionSqlSession;
import com.comerzzia.pos.services.core.config.configContadores.parametros.ConfigContadoresParametrosException;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.util.config.SpringContext;

@Component
@Primary
public class ByLServicioContadores extends ServicioContadores {

	public static final String ERROR_RANGOS = "RANGOS";
	public static final String ERROR_FECHAS = "FECHAS";
	public static final String ERROR_CONTADOR_INVALIDO = "CONTADOR_INVALIDO";
	public static final String ERROR_PARAMETRO_INVALIDO = "PARAMETRO_INVALIDO";
	public static final String ERROR_SALTO_NUMERACION = "SALTO NUMERACION";
	public static final String PARAMETRO_RANGO = "ID_RANGO";

	protected static Logger log = Logger.getLogger(ByLServicioContadores.class);

	@Autowired
	protected ByLContadorMapper contadorMapper;

	/**
	 * Obtiene toda la información del contador.
	 * 
	 * @param idContador
	 * @param parametrosContador
	 * @param uidActividad
	 * @return
	 * @throws ContadorNotFoundException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ByLContadorBean obtenerContadorByL(String idContador, Map<String, String> parametrosContador, String uidActividad) throws ContadorServiceException, ContadorNotFoundException {
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);

		ByLContadorBean contador = null;
		ConfigContadorBean configContador = null;
		ByLContadorKey contadorPrimaryKey = new ByLContadorKey();
		contadorPrimaryKey.setUidActividad(uidActividad);
		contadorPrimaryKey.setIdContador(idContador);
		try {
			sqlSession.openSession(SessionFactory.openSession());

			log.debug("obtenerContador() - Obteniendo el valor para el contador " + idContador);

			if (parametrosContador != null) {
				configContador = servicioConfigContadores.getCacheConfigContadores().get(idContador);

				if (configContador == null) {
					// Obtenemos la configuración del contador
					configContador = servicioConfigContadores.consultar(idContador);
				}
				else {
					log.debug("obtenerContador() - La configuración del contador " + idContador + " se ha obtenido desde la caché");
				}

				if (configContador == null) {
					String mensaje = "No se ha encontrado la configuración para el contador " + idContador;
					log.error("obtenerContador() - " + mensaje);
					throw new ContadorNotFoundException(mensaje);
				}

				servicioConfigContadores.getCacheConfigContadores().put(idContador, configContador);

				// Obtenemos el listado de parámetros efectivos del contador. Se entiende por parámetro efectivo aquel
				// que perteneciendo
				// a los parámetros de la configuración del contador, también se encuentra como clave en el Map de
				// parámetros que se pasa.
				Map<String, ConfigContadorParametroBean> mapParametrosEfectivos = obtenerParametrosEfectivos(sqlSession, idContador, parametrosContador);

				// Aplicamos los parámetros efectivos a las máscaras de la configuración
				obtenerValoresParaMascaras(configContador, mapParametrosEfectivos);

				// Comprobamos que todas las máscaras de se han evaluado correctamente, es decir, no queda ningún
				// carácter # en los valores de los divisores
				if (!validarValoresDivisores(configContador)) {
					String mensaje = "No se han establecido todos los parámetros para la configuración del contador " + idContador;
					log.error("obtenerContador() - " + mensaje);
					throw new ContadorServiceException(mensaje);
				}

				// Obtenemos el contador
				contadorPrimaryKey.setDivisor1(configContador.getValorDivisor1());
				contadorPrimaryKey.setDivisor2(configContador.getValorDivisor2());
				contadorPrimaryKey.setDivisor3(configContador.getValorDivisor3());

				configContador.setParametros(new ArrayList(mapParametrosEfectivos.values()));
			}
			contador = contadorMapper.selectByPrimaryKey(contadorPrimaryKey);

			// Si el contador aún no existe, se crea con valor inicial 0.
			if (contador == null) {
				contador = new ByLContadorBean();
				contador.setUidActividad(contadorPrimaryKey.getUidActividad());
				contador.setIdContador(contadorPrimaryKey.getIdContador());
				contador.setDivisor1(contadorPrimaryKey.getDivisor1());
				contador.setDivisor2(contadorPrimaryKey.getDivisor2());
				contador.setDivisor3(contadorPrimaryKey.getDivisor3());
				contador.setValor(Long.valueOf(0));

				contadorMapper.insert(contador);
				sqlSession.commit();
			}
			contador.setConfigContador(configContador);

			Date fechaActual = new Date();
			// Sólo se actualizará el contador si es la primera consulta o si la fecha actual es igual o mayor que la
			// ultima petición
			if (contador.getUltimaPeticion() == null || (fechaActual.equals(contador.getUltimaPeticion()) || fechaActual.after(contador.getUltimaPeticion()))) {
				// Incrementamos en 1 el valor del contador
				contador.setValor(contador.getValor() + 1);
				contador.setUltimaPeticion(new Date());
			}
			else {
				String mensaje = "Se está intentando acceder a un contador cuya fecha de última petición es posterior a la fecha actual";
				log.error("obtenerContador() - " + mensaje);
				throw new ContadorServiceException(mensaje);
			}

			sqlSession.commit();
			return contador;
		}
		catch (Exception e) {
			log.error("obtenerContador() - Error obteniendo contador para actividad " + uidActividad + " y contador " + idContador);
			sqlSession.rollback();
			if (e instanceof ContadorNotFoundException) {
				throw new ContadorNotFoundException(e.getMessage());
			}
			else if (e instanceof ContadorServiceException) {
				throw new ContadorServiceException(e);
			}
			else {
				log.error("obtenerContador() - " + e.getMessage());
				String mensaje = "Error obteniendo el contador " + idContador + ": " + e.getMessage();
				throw new ContadorServiceException(mensaje);
			}
		}
		finally {
			sqlSession.close();
		}
	}

	public void salvarContador(SqlSession sqlSession, ByLContadorBean contador) throws ContadorServiceException, ContadorNotFoundException {

		try {
			log.debug("salvarContador() - Salvando el valor para el contador " + contador.getIdContador());

			Date fechaActual = new Date();
			// Sólo se actualizará el contador si es la primera consulta o si la fecha actual es igual o mayor que la
			// ultima petición
			if (contador.getUltimaPeticion() == null || (fechaActual.equals(contador.getUltimaPeticion()) || fechaActual.after(contador.getUltimaPeticion()))) {
				// Incrementamos en 1 el valor del contador
				contador.setValor(contador.getValor());
				contador.setUltimaPeticion(new Date());
				int resultadoUpdate = contadorMapper.updateByPrimaryKey(contador);
				if(resultadoUpdate == 1) {
					log.debug("salvarContador() - Exito actualizando el valor para el contador " + contador.getIdContador());
				}
				else {
					log.error("salvarContador() - Error actualizando el valor para el contador " + contador.getIdContador());
				}
			}
			else {
				String mensaje = "Se está intentando acceder a un contador cuya fecha de última petición es posterior a la fecha actual";
				log.error("obtenerContador() - " + mensaje);
				throw new ContadorServiceException(mensaje);
			}
		}
		catch (Exception e) {
			log.error("salvarContador() - Error salvando contador para actividad " + contador.getUidActividad() + " y contador " + contador.getIdContador());
			if (e instanceof ContadorNotFoundException) {
				throw new ContadorNotFoundException(e.getMessage());
			}
			else if (e instanceof ContadorServiceException) {
				throw new ContadorServiceException(e);
			}
			else {
				log.error("salvarContador() - " + e.getMessage());
				String mensaje = "Error salvando el contador " + contador.getIdContador() + ": " + e.getMessage();
				throw new ContadorServiceException(mensaje);
			}
		}
	}

	/*
	 * Devuelve el primer contador que puede ser incrementado según los rangos disponibles
	 */
	public ByLContadorBean consultarContadorActivo(ByLConfigContadorBean configContador, Map<String, String> parametrosContador, Map<String, String> condicionesVigencia, String uidActividad,
	        boolean incrementar) throws ConfigContadoresParametrosException, ContadorServiceException {
		
		ByLContadorBean res = new ByLContadorBean();
		ByLContadorBean contador = null;

		try {
			if (configContador.isRangosCargados()) {
				if (!configContador.getRangos().isEmpty()) {
					// Si se encuentra un contador estos errores desaparecerán
					if (configContador.getRangoFechasActivas().isEmpty()) {
						res.setError(ERROR_FECHAS);
					}
					else {
						res.setError(ERROR_RANGOS);
					}

					// Evaluamos todos los rangos para encontrar el que corresponda
					for (ConfigContadorRango rango : configContador.getRangos()) {
						parametrosContador.put(PARAMETRO_RANGO, rango.getIdRango());
						contador = consultarContador(configContador, rango, parametrosContador, uidActividad);
						contador.setConfigContadorRango(rango);

						log.debug("consultarContadorActivo() - Contador encontrado: " + contador.getIdContador() + ". Valor: " + contador.getValor() + ". Rango inicio: " + rango.getRangoInicio()
						        + ". Rango fin: " + rango.getRangoFin() + ". Fecha rango inicio: " + rango.getRangoFechaInicio() + ". Fecha rango fin: " + rango.getRangoFechaFin());

						// Evaluamos si el contador obtenido corresponde al conjunto actual de empresa-almacén-caja
						if (evaluaUsoEnCaja(contador, condicionesVigencia)) {

							// Evaluamos si el contador tiene numeración disponible
							if ((contador.getValor() < rango.getRangoFin() || (incrementar && contador.getValor().equals(rango.getRangoFin())))) {
								log.debug("consultarContadorActivo() - El contador tiene numeración disponible");
								if (contador.disponible()) {
									log.debug("consultarContadorActivo() - Contador disponible.");
									res = contador;

									if (res.getEstadoBean() == Estado.NUEVO) {
										contadorMapper.insert(contador);
									}
									if (incrementar) {
										res = incrementarContador(contador, true);
									}
									break;
								}
								else {
									log.debug("consultarContadorActivo() - Contador no disponible.");
								}
							}
						}
						else {
							log.debug("consultarContadorActivo() - No se ha podido evaluar el uso en caja.");
						}
					}
				}
				else {
					res = consultarContador(configContador, null, parametrosContador, uidActividad);
					if (res.getEstadoBean() == Estado.NUEVO) {
						contadorMapper.insert(res);
					}
					if (incrementar) {
						res = incrementarContador(res, false);
					}
				}

			}
		}
		catch (ContadoresConfigInvalidException e) {
			 res.setError(ERROR_CONTADOR_INVALIDO);
		}
		catch (ConfigContadoresParametrosInvalidException e) {
			res.setError(ERROR_PARAMETRO_INVALIDO);
		}
		// catch(ContadorNumeracionException e){
		// res.setError(ERROR_SALTO_NUMERACION);
		// }
		return res;
	}

	protected boolean evaluaContinuidadContadores(ByLContadorBean mayorConsultado, ByLContadorBean contadorContinuo) {
		return mayorConsultado != null && mayorConsultado.getConfigContadorRango() != null
		        && mayorConsultado.getConfigContadorRango().getRangoInicio().equals(contadorContinuo.getConfigContadorRango().getRangoInicio())
		        && mayorConsultado.getConfigContadorRango().getRangoFin().equals(contadorContinuo.getConfigContadorRango().getRangoFin()) && contadorContinuo.getValor() < mayorConsultado.getValor();
	}

	protected boolean evaluaUsoEnCaja(ByLContadorBean contador, Map<String, String> condicionesVigencia) {
		return contador.getConfigContadorRango().getCodemp().equals(condicionesVigencia.get(ConfigContadorRango.VIGENCIA_CODEMP))
		        && (contador.getConfigContadorRango().getCodalm() == null || contador.getConfigContadorRango().getCodalm().equals(condicionesVigencia.get(ConfigContadorRango.VIGENCIA_CODALM)))
		        && (contador.getConfigContadorRango().getCodcaja() == null || contador.getConfigContadorRango().getCodcaja().equals(condicionesVigencia.get(ConfigContadorRango.VIGENCIA_CODCAJA)));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ByLContadorBean consultarContador(ByLConfigContadorBean configContador, ConfigContadorRango rango, Map<String, String> parametrosContador, String uidActividad)
	        throws ContadorServiceException, ContadoresConfigInvalidException, ConfigContadoresParametrosInvalidException {
		ByLContadorBean contador = null;
		ByLContadorKey contadorPrimaryKey = new ByLContadorKey();
		contadorPrimaryKey.setUidActividad(uidActividad);
		contadorPrimaryKey.setIdContador(configContador.getIdContador());
//		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		
		
		try {
			log.debug("consultarContador() - Obteniendo el valor para el contador " + configContador.getIdContador());
//			sqlSession.openSession(SessionFactory.openSession());
			if (parametrosContador != null) {
				// Obtenemos el listado de parámetros efectivos del contador. Se entiende por parámetro efectivo aquel
				// que perteneciendo
				// a los parámetros de la configuración del contador, también se encuentra como clave en el Map de
				// parámetros que se pasa.
				Map<String, ConfigContadorParametroBean> mapParametrosEfectivos = obtenerParametrosEfectivos(null, configContador.getIdContador(), parametrosContador);

				// Aplicamos los parámetros efectivos a las máscaras de la configuración
				obtenerValoresParaMascaras(configContador, mapParametrosEfectivos);

				// Comprobamos que todas las máscaras de se han evaluado correctamente, es decir, no queda ningún
				// carácter # en los valores de los divisores
				if (!validarValoresDivisores(configContador)) {
					String mensaje = "No se han establecido todos los parámetros para la configuración del contador " + configContador.getIdContador();
					log.error("obtenerContador() - " + mensaje);
					throw new ContadorServiceException(mensaje);
				}

				// Obtenemos el contador
				contadorPrimaryKey.setDivisor1(configContador.getValorDivisor1());
				contadorPrimaryKey.setDivisor2(configContador.getValorDivisor2());
				contadorPrimaryKey.setDivisor3(configContador.getValorDivisor3());

				configContador.setParametros(new ArrayList(mapParametrosEfectivos.values()));
			}
			contador = contadorMapper.selectByPrimaryKey(contadorPrimaryKey);

			if (!configContador.isRangosCargados()) {
				configContador.setRangos(ServicioConfigContadoresRangos.get().consultar(contador.getIdContador()));
				configContador.setRangosCargados(true);
			}

			// Si el contador aún no existe se crea con valor inicial 0 o el mínimo del rango activo
			if (contador == null) {
				contador = new ByLContadorBean();
				contador.setUidActividad(contadorPrimaryKey.getUidActividad());
				contador.setIdContador(contadorPrimaryKey.getIdContador());
				contador.setDivisor1(contadorPrimaryKey.getDivisor1());
				contador.setDivisor2(contadorPrimaryKey.getDivisor2());
				contador.setDivisor3(contadorPrimaryKey.getDivisor3());
				contador.setEstadoBean(Estado.NUEVO);

				if (rango != null) {
					if (contador.getConfigContador() != null && contador.getConfigContador().getLongitudMaxima() != null
					        && contador.getConfigContador().getLongitudMaxima() < String.valueOf(rango.getRangoInicio() - 1).length()) {
						throw new ContadoresConfigInvalidException("El valor del contador supera la longitud máxima.");
					}
					contador.setValor(rango.getRangoInicio() - 1);
				}
				else {
					contador.setValor(Long.valueOf(0));
				}

			}
			contador.setConfigContador(configContador);

			return contador;

		}
		catch (Exception e) {
			String msg = "consultarContador() - Error obteniendo contador para actividad " + uidActividad + " y contador " + configContador.getIdContador() + ". ";

			if (e instanceof ContadorNotFoundException) {
				log.error(msg + "Contador no encontrado. ");
				throw new ContadorNotFoundException(e.getMessage());
			}
			else if (e instanceof ContadoresConfigInvalidException) {
				log.error(msg + "No se ha podido obtener la lista de rangos del contador. ");
				throw new ContadoresConfigInvalidException(e);
			}
			else if (e instanceof ConfigContadoresParametrosInvalidException) {
				log.error(msg + "Configuración de parametros inválida. ");
				throw new ConfigContadoresParametrosInvalidException(e);
			}
			else {
				log.error("consultarContador() - " + e.getMessage());
				String mensaje = "Error obteniendo el contador " + configContador.getIdContador() + ": " + e.getMessage();
				throw new ContadorServiceException(mensaje);
			}
		}
	}

	public ByLContadorBean incrementarContador(ByLContadorBean contador, boolean tieneRangosContador) throws ContadorServiceException, ContadoresConfigInvalidException {
		Date fechaActual = new Date();
		ConfigContadorRango rango = contador.getConfigContadorRango();
		// Si el contador tiene una vigencia activa
		if (rango != null) {
			if (contador.getValor() + 1 > rango.getRangoFin()) {
				String mensaje = "Se está intentando incrementar un contador por encima de su rango numérico máximo";
				log.warn("consultarContador() - " + mensaje);
				return contador;
			}
		}

		// Sólo se actualizará el contador si es la primera consulta o si la fecha actual es igual o mayor que la ultima
		// petición
		if (contador.getUltimaPeticion() == null || (fechaActual.equals(contador.getUltimaPeticion()) || fechaActual.after(contador.getUltimaPeticion()))) {

			if (rango == null || (rango.getRangoFechaInicio().before(new Date()) && rango.getRangoFechaFin().after(new Date()))) {
				// Incrementamos en 1 el valor del contador
				if (contador.getConfigContador() != null && contador.getConfigContador().getLongitudMaxima() != null
				        && contador.getConfigContador().getLongitudMaxima() < String.valueOf(contador.getValor() + 1).length()) {
					throw new ContadoresConfigInvalidException("El valor del contador supera la longitud máxima.");
				}
				contador.setValor(contador.getValor() + 1);
				contador.setUltimaPeticion(new Date());

				if (rango != null) {
					contador.setIdRangoUltimaPeticion(rango.getIdRango());
				}
				
				// Se le quita el update para que sólo se guarde el contador en caso de que el ticket haya finalizado
				// correctamente (Metodo salvarContador)
				if(tieneRangosContador){ // En el caso de que tenga rangos, haremos el update en este momento, ya que tenemos toda la información del rango 
					contadorMapper.updateByPrimaryKey(contador);
				}
			}
		}
		else {
			String mensaje = "Se está intentando acceder a un contador cuya fecha de última petición es posterior a la fecha actual";
			log.error("consultarContador() - " + mensaje);
			throw new ContadorServiceException(mensaje);
		}

		return contador;

	}
	
	@Override
	protected String[] obtenerValorMascara(Map<String, ConfigContadorParametroBean> mapParametrosEfectivos, ConfigContadorBean configContador, String mascara, Matcher matcher) {
		String mascaraFormateada = mascara;
		while(matcher.find()) {
			String param = matcher.group(1);
			if(mapParametrosEfectivos.containsKey(param)) {
				String valorParam = mapParametrosEfectivos.get(param).getValorParametro();
				short longitudMaximaParam = mapParametrosEfectivos.get(param).getLongitudMaxima();
				String direccionCorte = mapParametrosEfectivos.get(param).getDireccionRelleno(); // Cogemos temporalmente la direccion de relleno para la de corte
				valorParam = cortarValorParam(valorParam, longitudMaximaParam, direccionCorte);
				mascara = mascara.replace("#"+param+"#", valorParam);
				
				String valorParamFormateado = configContador.formatearValorMascara(valorParam);
				mascaraFormateada = mascaraFormateada.replace("#"+param+"#", valorParamFormateado);
			}
		}
		
		return new String[]{mascara, mascaraFormateada};
	}

	private String cortarValorParam(String valorParam, short longitudMaximaParam, String direccionCorte) {
		String valorParamCortado = valorParam;
		if (valorParam.length() <= longitudMaximaParam) {
			return valorParamCortado;
		}

		if (direccionCorte == null || direccionCorte.equalsIgnoreCase("D")) { // Cortamos lo de la derecha
			valorParamCortado = valorParam.substring(0, longitudMaximaParam);
		}
		else if (direccionCorte.equalsIgnoreCase("I")) { // Cortamos lo de la izquierda
			valorParamCortado = valorParam.substring(valorParam.length() - longitudMaximaParam);
		}

		return valorParamCortado;
	}
	
	public String obtenerValorTotalConSeparadorMX(String divisor, String valorContadorFormateado, String separador) {
		if ("".equals(separador)) {
			separador = "/";
		}
		return divisor + separador + valorContadorFormateado;
	}
	
}
