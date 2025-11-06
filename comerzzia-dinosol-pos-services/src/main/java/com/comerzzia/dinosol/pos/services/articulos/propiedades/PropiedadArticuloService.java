package com.comerzzia.dinosol.pos.services.articulos.propiedades;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.dinosol.pos.persistence.articulos.propiedades.PropiedadArticulo;
import com.comerzzia.dinosol.pos.persistence.articulos.propiedades.PropiedadArticuloExample;
import com.comerzzia.dinosol.pos.persistence.articulos.propiedades.PropiedadArticuloMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.sesion.Sesion;

@SuppressWarnings("deprecation")
@Service
public class PropiedadArticuloService {

	protected static final Logger log = Logger.getLogger(PropiedadArticuloService.class);

	private static final String ID_CLASE_D_ARTICULOS_TBL_CODART = "D_ARTICULOS_TBL.CODART";
	private static final String PARAMETRO_ART_CODART_PLASTICO = "ART.CODART_PLASTICO";
	private static final String PARAMETRO_ART_ES_PLASTICO = "ART.ES_PLASTICO";

	@Autowired
	protected PropiedadArticuloMapper propiedadArticuloMapper;
	@Autowired
	protected Sesion sesion;

	/**
	 * Consulta para ver si un artículo tiene la propiedad ART.CODART_PLASTICO
	 * 
	 * @param codArt
	 * @return codArt del artículo de plástico en caso de existir la propiedad, o
	 *         una cadena vacía en caso contrario
	 */
	public String consultarCodartPlastico(String codArt) {
		SqlSession sqlSession = new SqlSession();
		String codArtPlastico = "";
		try {
			log.debug("consultarCodartPlastico() - Comenzando la consulta de la propiedad ART.CODART_PLASTICO para el artículo " + codArt);
			sqlSession.openSession(SessionFactory.openSession());
			PropiedadArticuloExample example = new PropiedadArticuloExample();
			example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
					.andIdClaseEqualTo(ID_CLASE_D_ARTICULOS_TBL_CODART)
					.andIdObjetoEqualTo(codArt)
					.andParametroEqualTo(PARAMETRO_ART_CODART_PLASTICO);
			List<PropiedadArticulo> result = new ArrayList<PropiedadArticulo>();
			result = propiedadArticuloMapper.selectByExample(example);
			if (!result.isEmpty()) {
				codArtPlastico = result.get(0).getValor();
			}
		} catch (Exception e) {
			String msg = "Se ha producido un error consultando la propiedad ART.CODART_PLASTICO en base de datos " + e.getMessage();
			log.error("consultarCodartPlastico() - " + msg, e);
		}
		finally {
			sqlSession.close();
		}
		return codArtPlastico;
	}

	/**
	 * Consulta para ver si un artículo tiene la propiedad ART.ES_PLASTICO con valor S, y por tanto no se puede vender
	 * 
	 * @param codArt
	 * @return true en caso de que la propiedad exista y esté a S, false en caso contrario
	 */
	public boolean consultarEsPlastico(String codArt) {
			SqlSession sqlSession = new SqlSession();
			boolean esPlastico = false;
			try {
				log.debug("consultarEsPlastico() - Comenzando la consulta de la propiedad ART.ES_PLASTICO para el artículo " + codArt);
				sqlSession.openSession(SessionFactory.openSession());
				PropiedadArticuloExample example = new PropiedadArticuloExample();
				example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
						.andIdClaseEqualTo(ID_CLASE_D_ARTICULOS_TBL_CODART)
						.andIdObjetoEqualTo(codArt)
						.andParametroEqualTo(PARAMETRO_ART_ES_PLASTICO);
				List<PropiedadArticulo> result = new ArrayList<PropiedadArticulo>();
				result = propiedadArticuloMapper.selectByExample(example);
				if (!result.isEmpty()) {
					esPlastico = result.get(0).getValor().equals("S") ? true : false;
				}
			} catch (Exception e) {
				String msg = "Se ha producido un error consultando la propiedad ART.ES_PLASTICO en base de datos " + e.getMessage();
				log.error("consultarEsPlastico() - " + msg, e);
			}
			finally {
				sqlSession.close();
			}
			return esPlastico;
		}

}
