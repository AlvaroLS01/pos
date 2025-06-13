package com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRango;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoKey;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoMapper;
import com.comerzzia.core.servicios.config.configContadores.ContadoresConfigException;
import com.comerzzia.core.util.base.Estado;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Component
@Service
public class ServicioConfigContadoresRangos {

	protected static Logger log = Logger.getLogger(ServicioConfigContadoresRangos.class);

	private static ServicioConfigContadoresRangos instance;

	@Autowired
	protected ConfigContadorRangoMapper configContadorRangoMapper;

	public static ServicioConfigContadoresRangos get() {
		if (instance == null) {
			instance = new ServicioConfigContadoresRangos();
		}
		return instance;
	}

	public List<ConfigContadorRango> consultar(String idContador) throws ContadoresConfigException {
		log.debug("consultar() - Consultando rangos del contador " + idContador);
		SqlSession sqlSession = new SqlSession();
		try {

			sqlSession.openSession(SessionFactory.openSession());
			ConfigContadorRangoMapper configContadorRangoMapper = sqlSession.getMapper(ConfigContadorRangoMapper.class);

			ConfigContadorRangoExample configContadorRangoExample = new ConfigContadorRangoExample();
			configContadorRangoExample.or().andIdContadorEqualTo(idContador);

			return configContadorRangoMapper.selectByExample(configContadorRangoExample);
		}
		catch (Exception e) {
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar los rangos de un contador: " + e.getMessage();
			throw new ContadoresConfigException(mensaje, e);
		}
		finally {
			sqlSession.close();
		}
	}

	public List<ConfigContadorRango> consultar(ConfigContadorRangoExample configContadorRangoExample) throws ContadoresConfigException {
		log.debug("consultar() - Consultando rangos ");
		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());
			ConfigContadorRangoMapper configContadorRangoMapper = sqlSession.getMapper(ConfigContadorRangoMapper.class);

			return configContadorRangoMapper.selectByExample(configContadorRangoExample);
		}
		catch (Exception e) {
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar los rangos de un contador: " + e.getMessage();
			throw new ContadoresConfigException(mensaje, e);
		}
		finally {
			sqlSession.close();
		}
	}

	public List<ConfigContadorRango> consultar(String idContador, String codEmp, String codAlm, String codCaj) throws ContadoresConfigException {
		log.debug("consultar() - Consultando rangos del contador " + idContador);
		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());
			ConfigContadorRangoMapper configContadorRangoMapper = sqlSession.getMapper(ConfigContadorRangoMapper.class);
			ConfigContadorRangoExample filtro = new ConfigContadorRangoExample();

			ConfigContadorRangoExample.Criteria cirteria1 = filtro.or();
			ConfigContadorRangoExample.Criteria cirteria2 = filtro.or();
			ConfigContadorRangoExample.Criteria cirteria3 = filtro.or();

			cirteria1.andIdContadorEqualTo(idContador).andCodempEqualTo(codEmp).andCodalmEqualTo(codAlm).andCodcajaEqualTo(codCaj);
			cirteria2.andIdContadorEqualTo(idContador).andCodempEqualTo(codEmp).andCodalmEqualTo(codAlm).andCodcajaIsNull();
			cirteria3.andIdContadorEqualTo(idContador).andCodempEqualTo(codEmp).andCodalmIsNull();

			return configContadorRangoMapper.selectByExample(filtro);
		}
		catch (Exception e) {
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar los rangos de un contador: " + e.getMessage();
			throw new ContadoresConfigException(mensaje, e);
		} finally {
			sqlSession.close();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void salvar(ConfigContadorRango rangoContador) throws ConfigContadoresRangosConstraintViolationException, ConfigContadoresRangosException {
		switch (rangoContador.getEstadoBean()) {
			case Estado.NUEVO:
				crear(rangoContador);
				break;

			case Estado.MODIFICADO:
				modificar(rangoContador);
				break;

			case Estado.BORRADO:
				eliminar(rangoContador);
		}

	}

	protected void eliminar(ConfigContadorRango rangoContador) throws ConfigContadoresRangosConstraintViolationException, ConfigContadoresRangosException {
		log.debug("eliminar() - Eliminando rango " + rangoContador.getIdRango() + " de configuración de contador " + rangoContador.getIdContador());

		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());
			ConfigContadorRangoMapper configContadorRangoMapper = sqlSession.getMapper(ConfigContadorRangoMapper.class);

			configContadorRangoMapper.deleteByPrimaryKey(rangoContador);
		}
		catch (Exception e) {
			String msg = "Error eliminando rango de configuración de contador: " + e.getMessage();
			log.error("eliminar() - " + msg);

			if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
				throw new ConfigContadoresRangosConstraintViolationException("Error eliminando rango de configuración de contador: Se han violado restricciones de integridad entre registros");
			}
			else {
				throw new ConfigContadoresRangosException(msg, e);
			}
		}
		finally {
			sqlSession.close();
		}

	}

	protected void crear(ConfigContadorRango rangoContador) throws ConfigContadoresRangosConstraintViolationException, ConfigContadoresRangosException {
		log.debug("crear() - Creando nuevo rango de configuración de contador");

		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());
			ConfigContadorRangoMapper configContadorRangoMapper = sqlSession.getMapper(ConfigContadorRangoMapper.class);

			configContadorRangoMapper.insert(rangoContador);
		}
		catch (Exception e) {
			String msg = "Error creando nuevo rango de configuración de contador: " + e.getMessage();
			log.error("crear() - " + msg);
			if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
				if (e.getCause().toString().contains("ORA-00001")) {
					throw new ConfigContadoresRangosConstraintViolationException("Error creando rango de configuración de contador: Ya existe un registro con el mismo código en el sistema");
				}
				else {
					throw new ConfigContadoresRangosException(msg, e);
				}
			}
			else {
				throw new ConfigContadoresRangosException(msg, e);
			}
		}
		finally {
			sqlSession.close();
		}

	}

	protected void modificar(ConfigContadorRango rangoContador) throws ConfigContadoresRangosException {
		log.debug("modificar() - Modificando rango " + rangoContador.getIdRango() + " de configuración de contador " + rangoContador.getIdContador());

		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());
			ConfigContadorRangoMapper configContadorRangoMapper = sqlSession.getMapper(ConfigContadorRangoMapper.class);

			configContadorRangoMapper.updateByPrimaryKey(rangoContador);
		}
		catch (Exception e) {
			String msg = "Error modificando rango de configuración de contador: " + e.getMessage();
			log.error("modificar() - " + msg);

			throw new ConfigContadoresRangosException(msg, e);
		}
		finally {
			sqlSession.close();
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void eliminar(String idContador) throws ConfigContadoresRangosConstraintViolationException, ConfigContadoresRangosException {
		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());
			ConfigContadorRangoMapper configContadorRangoMapper = sqlSession.getMapper(ConfigContadorRangoMapper.class);

			log.debug("eliminar() - Eliminando todos los rangos de configuración de contador " + idContador);

			ConfigContadorRangoExample filtro = new ConfigContadorRangoExample();
			filtro.or().andIdContadorEqualTo(idContador);

			configContadorRangoMapper.deleteByExample(filtro);
		}
		catch (Exception e) {
			String msg = "Error eliminando todos los rangos de configuración de contador: " + e.getMessage();
			log.error("eliminar() - " + msg);

			if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
				throw new ConfigContadoresRangosConstraintViolationException("Error eliminando rangos de configuración de contador: Se han violado restricciones de integridad entre registros");
			}
			else {
				throw new ConfigContadoresRangosException(msg, e);
			}
		}
		finally {
			sqlSession.close();
		}

	}

	public ConfigContadorRango consultar(String idContador, String idRangoUltimaPeticion) {
		log.debug("consultar() - Consultando rangos del contador " + idContador);

		SqlSession sqlSession = new SqlSession();
		try {

			sqlSession.openSession(SessionFactory.openSession());
			ConfigContadorRangoMapper configContadorRangoMapper = sqlSession.getMapper(ConfigContadorRangoMapper.class);

			ConfigContadorRangoKey rangoKey = new ConfigContadorRangoKey();
			rangoKey.setIdContador(idContador);
			rangoKey.setIdRango(idRangoUltimaPeticion);

			return configContadorRangoMapper.selectByPrimaryKey(rangoKey);
		}
		finally {
			sqlSession.close();
		}
	}

}
