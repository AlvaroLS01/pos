package com.comerzzia.pampling.pos.services.ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.pampling.pos.devices.impresoras.fiscal.italia.PrinterDisconnectedException;
import com.comerzzia.pampling.pos.services.fiscal.italia.exception.ItaliaFiscalException;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.dispositivo.fiscal.IFiscalPrinter;
import com.comerzzia.pos.persistence.core.config.configcontadores.ConfigContadorBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.parametros.ConfigContadorParametroBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.config.configContadores.rangos.CounterRangeParamDto;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Service
@Primary

@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
public class PamplingTicketsService extends TicketsService {

	@Override
	@Transactional(rollbackFor = { TicketsServiceException.class })
	public synchronized void registrarTicket(Ticket ticket, TipoDocumentoBean tipoDocumento, boolean procesarTicket) throws TicketsServiceException {
        setContadorIdTicket(ticket);
        
		IPrinter printer = Dispositivos.getInstance().getImpresora1();
		if (printer instanceof IFiscalPrinter && !BigDecimalUtil.isIgualACero(ticket.getTotales().getTotalAPagar())) {
			try {
				((IFiscalPrinter) printer).sendTicket((TicketVentaAbono) ticket);
			}
			catch (PrinterDisconnectedException e) {
                log.error("registrarTicket() - La impresora está desconectada o en un estado incorrecto: " + e.getMessage(), e);
                // Lanzar excepción con mensaje detallado
                throw new TicketsServiceException("Error al salvar el ticket: La impresora está desconectada o en un estado incorrecto. Por favor, verifica la conexión.", e);
            } catch (ItaliaFiscalException e) {
                log.error("registrarTicket() - Error mientras registraba en la impresora fiscal: " + e.getMessage(), e);
                // Lanzar excepción con mensaje detallado
                throw new TicketsServiceException("Error al salvar el ticket: " + e.getMessage(), e);
            } catch (Exception e) {
                log.error("registrarTicket() - Error mientras registraba en la impresora fiscal: " + e.getMessage(), e);
                // Lanzar excepción con mensaje detallado
                throw new TicketsServiceException("Error al salvar el ticket: " + I18N.getTexto("Error mientras se registraba en la impresora fiscal. " + e.getMessage()), e);
            }
        }

        super.registrarTicket(ticket, tipoDocumento, procesarTicket);
    }
    
     public synchronized void setContadorIdTicket(Ticket ticket) throws TicketsServiceException {
        try {
            log.debug("setContadorIdTicket() - Obteniendo contador para identificador...");
            Map<String, String> parametrosContador = new HashMap<>();
            Map<String, String> condicionesVigencias = new HashMap<>();
            
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODEMP, ticket.getEmpresa().getCodEmpresa());
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODALM, ticket.getTienda().getAlmacenBean().getCodAlmacen());
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODSERIE, ticket.getTienda().getAlmacenBean().getCodAlmacen());
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODCAJA, ticket.getCodCaja());
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODDOC, ticket.getCabecera().getCodTipoDocumento());
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_PERIODO, ((new Fecha()).getAño().toString()));
           
            condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODCAJA, ticket.getCabecera().getCodCaja());
            condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODALM, ticket.getCabecera().getTienda().getCodAlmacen());
            condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODEMP, ticket.getCabecera().getEmpresa().getCodEmpresa());
            
            TipoDocumentoBean documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(ticket.getCabecera().getCodTipoDocumento());
            ConfigContadorBean confContador = servicioConfigContadores.consultar(documentoActivo.getIdContador());
            if (!confContador.isRangosCargados()) {
                ConfigContadorRangoExample example = new ConfigContadorRangoExample();
                example.or().andIdContadorEqualTo(confContador.getIdContador());
                example.setOrderByClause(ConfigContadorRangoExample.ORDER_BY_RANGO_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FIN + ", "
                        + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_FIN);
                List<ConfigContadorRangoBean> rangos = servicioConfigContadoresRangos.consultar(example);
                
                confContador.setRangos(rangos);
                confContador.setRangosCargados(true);
            }
            ContadorBean ticketContador = servicioContadores.consultarContadorActivo(confContador, parametrosContador, condicionesVigencias, ticket.getUidActividad(), true);
            if (ticketContador == null || ticketContador.getError() != null) {
                throw new ContadorNotFoundException("No se ha encontrado un contador disponible");
            }
            ticket.setIdTicket(ticketContador.getValor());
            
            String codTicket = servicioContadores.obtenerValorTotalConSeparador(ticketContador.getConfigContador().getValorDivisor3Formateado(), ticketContador.getValorFormateado());
            
            ticket.getCabecera().setSerieTicket(ticketContador.getConfigContador().getValorDivisor3Formateado());
            ticket.getCabecera().setCodTicket(codTicket);
            ticket.getCabecera().setUidTicket(UUID.randomUUID().toString());
            
            copiaSeguridadTicketService.guardarBackupTicketActivo((TicketVenta) ticket);

            CounterRangeParamDto counterRangeParam = new CounterRangeParamDto();
            counterRangeParam.setCounterId(ticketContador.getIdContador());
            counterRangeParam.setDivisor1(ticketContador.getDivisor1());
            counterRangeParam.setDivisor2(ticketContador.getDivisor2());
            counterRangeParam.setDivisor3(ticketContador.getDivisor3());
            
            String rangeId = counterRangeManager.findRangeId(counterRangeParam);
            
            if (StringUtils.isNotBlank(rangeId)) {
                ((TicketVentaAbono) ticket).addExtension(EXTENSION_RANGE_ID, rangeId);
            }
            
        } catch (Exception e) {
            String msg = "Se ha producido un error procesando ticket con uid " + ticket.getUidTicket() + " : " + e.getMessage();
            log.error("setContadorIdTicket() - " + msg, e);
            throw new TicketsServiceException("Error al procesar el contador del ticket: " + e.getMessage(), e);
        }
    }
}

