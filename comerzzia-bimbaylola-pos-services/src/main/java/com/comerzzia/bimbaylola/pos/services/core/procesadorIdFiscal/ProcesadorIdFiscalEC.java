package com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.ByLConfigContadorBean;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRango;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.bimbaylola.pos.persistence.core.contadores.ByLContadorBean;
import com.comerzzia.bimbaylola.pos.services.core.ByLServicioContadores;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.ByLServicioConfigContadoresImpl;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos.ServicioConfigContadoresRangos;
import com.comerzzia.bimbaylola.pos.services.core.variables.ByLVariablesServices;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings("rawtypes")
@Component
public class ProcesadorIdFiscalEC implements IProcesadorFiscal {

	protected static final Logger log = Logger.getLogger(ProcesadorIdFiscalEC.class);
	
	@Autowired
	protected Documentos documentos;

	@Autowired
	protected VariablesServices variablesService;

	@Autowired
	private ByLServicioContadores byLServicioContadores;
	
	@Override
	public String obtenerIdFiscal(Ticket ticket) throws ProcesadorIdFiscalException {
		log.debug("obtenerIdFiscal() - Obteniendo número de identificación fiscal para Ecuador ...");
		String identificadorFiscal = null;

		Map<String, String> parametrosContador = new HashMap<>();
		Map<String, String> condicionesVigencias = new HashMap<>();

		parametrosContador.put("CODEMP", ticket.getEmpresa().getCodEmpresa());
		parametrosContador.put("CODALM", ticket.getTienda().getAlmacenBean().getCodAlmacen());
		parametrosContador.put("CODSERIE", ticket.getTienda().getAlmacenBean().getCodAlmacen());
		parametrosContador.put("CODCAJA", ticket.getCodCaja());
		parametrosContador.put("CODTIPODOCUMENTO", ticket.getCabecera().getCodTipoDocumento());
		parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

		// Se añaden vigencias para los rangos
		condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODCAJA, ticket.getCabecera().getCodCaja());
		condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODALM, ticket.getCabecera().getTienda().getCodAlmacen());
		condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODEMP, ticket.getCabecera().getEmpresa().getCodEmpresa());

		try {
			TipoDocumentoBean documentoActivo = documentos.getDocumento(ticket.getCabecera().getTipoDocumento());

			// Tratamos los rangos en caso de que los tenga
			ByLConfigContadorBean confContador = (ByLConfigContadorBean) ByLServicioConfigContadoresImpl.get().consultar(ByLVariablesServices.ID_FISCAL_EC + "_" + documentoActivo.getCodtipodocumento());
			if (!confContador.isRangosCargados()) {
				ConfigContadorRangoExample example = new ConfigContadorRangoExample();
				example.or().andIdContadorEqualTo(confContador.getIdContador());
				example.setOrderByClause(ConfigContadorRangoExample.ORDER_BY_RANGO_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FIN + ", "
				        + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_FIN);
				List<ConfigContadorRango> rangos = ServicioConfigContadoresRangos.get().consultar(example);

				if (rangos == null || rangos.isEmpty()) {
					throw new ProcesadorIdFiscalException("No existen rangos para el identificador fiscal.");
				}
				confContador.setRangos(rangos);
				confContador.setRangosCargados(true);
			}

			ByLContadorBean ticketContador;
			ticketContador = byLServicioContadores.consultarContadorActivo(confContador, parametrosContador, condicionesVigencias, ticket.getUidActividad(), true);

			if (ticketContador == null || ticketContador.getError() != null) {
				throw new ContadorNotFoundException(I18N.getTexto("No se ha encontrado contador con rangos disponible"));
			}

			identificadorFiscal = String.valueOf(ticketContador.getValor());
		}
		catch (Exception e1) {
			String msg = "Se ha producido un error procesando el identificador fiscal del ticket con uid " + ticket.getUidTicket() + " : " + e1.getMessage();
			log.error("obtenerIdFiscal() - " + msg, e1);
			throw new ProcesadorIdFiscalException(e1.getMessage());
		}

		return identificadorFiscal;
	}

	@Override
	public byte[] procesarDocumentoFiscal(Ticket ticket) throws Exception {
		return null;
	}
}
