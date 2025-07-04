package com.comerzzia.cardoso.pos.gui.promociones.monograficas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.gui.promociones.monograficas.bean.PromocionMonograficaCabeceraTicket;
import com.comerzzia.cardoso.pos.gui.promociones.monograficas.bean.PromocionMonograficaLineaTicket;
import com.comerzzia.cardoso.pos.gui.promociones.monograficas.exception.PromocionMonograficaException;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.CARDOSOTicketManager;
import com.comerzzia.cardoso.pos.services.rest.PosRestService;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales.PromocionEmpleadosCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.persistence.promociones.PromocionExample;
import com.comerzzia.pos.persistence.promociones.PromocionMapper;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.promociones.PromocionesService;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.ibm.icu.util.Calendar;

/**
 * GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS
 * 
 * Las dos promociones no pueden convivir en el mismo ticket.
 * 
 * PROMOCIONES MONOGRÁFICAS : 
 * Promoción personalizada para el cliente, con esta promoción se busca poder aplicar un descuento adicional para algunos artículos.
 * 1.- Cargamos los datos de la promoción al insertar la linea del ticket.
 * 2.- Al pasar a la pantalla de pagos, aplicamos los descuentos de dichas promociones en caso de tener, y guardamos un resumen
 * de los descuentos aplicados en el ticket.
 * 
 * PROMOCIONES DE EMPLEADOS : 
 * Promoción personalizada para el cliente, con esta promoción se busca poder aplicar un descuento adicional para los empleados.
 * 1.- Cargamos los datos de la promoción al pasar a la pantalla de pagos.
 * 2.- Justo después de cargar los datos iniciales de la promoción de empleados (esto incluye pasar algunas validaciones), aplicamos
 * directamente los descuentos de dicha promoción.
 * 
 */
@SuppressWarnings({"unchecked", "deprecation"})
@Service
public class PromocionesEspeciales{

	private static final Logger log = Logger.getLogger(PromocionesEspeciales.class);
	
	public static final Long ID_TIPO_PROMOCION_EMPLEADOS = 1001L;
	public static final Long ID_TIPO_PROMOCION_MONOGRAFICA = 1002L;
	
	public static final String VARIABLE_ID_TIPO_PROMOCION_EMPLEADOS = "PROMOCIONES.ID_DESCUENTO_EMPLEADOS";
	
	public static final String XML_NODO_MONOGRAFICA_DESCUENTO = "Descuento";
	
	@Autowired
	protected Sesion sesion;
	@Autowired
	protected PosRestService posRestService;
	@Autowired
	protected PromocionMapper promocionMapper;
	@Autowired
	protected PromocionesService promocionesService;
	@Autowired
	protected VariablesServices variablesServices;

	/* ############################################################################################# */
	/* ########################################## COMUNES ########################################## */
	/* ############################################################################################# */
	
	
	public void limpiarDatosPromocionesEspeciales(CARDOSOTicketManager ticketManager){		
		/* Si tenía aplicada promoción de empleado, solo se tiene que quitar los datos y recalcular. */
		if(((CARDOSOCabeceraTicket)ticketManager.getTicket().getCabecera()).getDatosDescuentoPromocionEmpleados() != null){
			((CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera()).setDatosDescuentoPromocionEmpleados(null);
			ticketManager.getTicket().getTotales().recalcular();
		}
	}
	
	/* ############################################################################################################## */
	/* ########################################## PROMOCIONES MONOGRÁFICAS ########################################## */
	/* ############################################################################################################## */
	
	
	/* #################################### CONSULTA #################################### */
	
	/**
	 * Realiza consultas en BD para sacar la promoción monográfica aplicable mas reciente.
	 * Insertamos en la linea la promoción para futuros cálculos, y el descuento que lleva en su XML. 
	 * @param linea
	 */
	public void setPromocionesMonograficasLineaTicket(CARDOSOLineaTicket linea){
		log.info("setPromocionesMonograficasLineaTicket() : GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS...");
		
		/* PASO 1 : Iniciamos los objetos, aunque no los cargemos los seateamos igual. */
		PromocionBean promocionMonografica = null;
		BigDecimal descuentoPromocionMonografica = BigDecimal.ZERO;
		try{
			/* PASO 2 : Sacamos las promociones monográficas y las recorremos para saber si podemos aplicar alguna de ellas. */
			List<PromocionBean> listPromocionMonograficasActivas = getPromocionesMonograficasActivas();
			if(listPromocionMonograficasActivas != null && !listPromocionMonograficasActivas.isEmpty()){
				/* PASO 3 : Por cada promoción recorremos sus detalles para compararlo con los datos de la linea. */ 
				for(PromocionBean promocionActiva : listPromocionMonograficasActivas){
					List<PromocionDetalleBean> listDetallesPromocionMonografica = getDetallePromocionMonografica(promocionActiva.getIdPromocion());
					if(listDetallesPromocionMonografica != null && !listDetallesPromocionMonografica.isEmpty()){
						for(PromocionDetalleBean detallePromocion : listDetallesPromocionMonografica){
							if(lineaAplicaPromocionMonografica(linea, detallePromocion)){
								promocionMonografica = promocionActiva;
								/* PASO 4 : Cogemos el descuento del detalle de promoción. */
								descuentoPromocionMonografica = getDescuentoPromocionXML(linea.getIdLinea(), linea.getArticulo().getCodArticulo(), detallePromocion);
								break;
							}
						}
					}
					
				}
			}
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al consultar las promociones monográficas activas : " + e.getMessage());
			log.error("setPromocionesMonograficasLineaTicket() - " + msgError, e);
		}
		
		/* PASO 5 : Siempre seteamos los datos aunque no esten completos. */
		linea.setPorcentajeDtoMonografica(descuentoPromocionMonografica);
		linea.setPromocionMonograficaAplicada(promocionMonografica);
	}
	
	public List<PromocionBean> getPromocionesMonograficasActivas() throws PromocionMonograficaException{
		 SqlSession sqlSession = new SqlSession();
		 List<PromocionBean> listPromocionesMonograficas = new ArrayList<PromocionBean>();
		try{
            sqlSession.openSession(SessionFactory.openSession());
            /* Sacamos en otro listado las promociones de tipo monográfica. */
            PromocionExample example = new PromocionExample();
            example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
            			.andIdTipoPromocionEqualTo(ID_TIPO_PROMOCION_MONOGRAFICA)
            			.andFechaFinGreaterThanOrEqualTo(new Date());
            example.setOrderByClause("ID_PROMOCION");
            listPromocionesMonograficas = promocionMapper.selectByExample(example);
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al consultar las promociones monográficas activas.");
			log.error("getPromocionesMonograficasActivas() - " + msgError + " : " + e.getMessage(), e);
			throw new PromocionMonograficaException(msgError, e);
		}
		finally{
			sqlSession.close();
		}
		return listPromocionesMonograficas;
	}
	
	public List<PromocionDetalleBean> getDetallePromocionMonografica(Long idPromocionMonografica) throws PromocionMonograficaException{
		List<PromocionDetalleBean> listDetallesPromocionMonografica = null;
		try{
			listDetallesPromocionMonografica = promocionesService.consultarDetallesPromocion(idPromocionMonografica);
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al consultar los detalles de la promoción monográfica " + idPromocionMonografica);
			log.error("getDetallePromocionMonografica() - " + msgError + " : " + e.getMessage(), e);
			throw new PromocionMonograficaException(msgError, e);
		}
		return listDetallesPromocionMonografica;
	}
	
	public Boolean lineaAplicaPromocionMonografica(CARDOSOLineaTicket linea, PromocionDetalleBean detallePromocion){
		/* Validación de que la fecha actual esta comprendida en las fechas del detalle de promoción y también debemos validar el código de artículo. */
		Calendar calendarioFechaActual = Calendar.getInstance();
		calendarioFechaActual.setTime(new Date());
		return calendarioFechaActual.after(detallePromocion.getFechaInicio()) && calendarioFechaActual.before(detallePromocion.getFechaFin()) && 
				linea.getArticulo().getCodArticulo().equalsIgnoreCase(detallePromocion.getCodArticulo());
	}
	
	/**
	 * Sacamos el dato de porcentaje de descuento del XML del detalle de promoción :
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <DatosPromocion> 
	 * 		<Descuento>20</Descuento> 
	 * </DatosPromocion>
	 * 
	 * @param idLinea
	 * @param codArticuloLinea
	 * @param detallePromocionMonografica
	 * @return BigDecimal
	 * @throws PromocionMonograficaException
	 */
	public BigDecimal getDescuentoPromocionXML(int idLinea, String codArticuloLinea, PromocionDetalleBean detallePromocionMonografica) throws PromocionMonograficaException{
		BigDecimal descuento = BigDecimal.ZERO;
		try{
			XMLDocument xml = new XMLDocument(detallePromocionMonografica.getDatosPromocion());
	        String descuentoString = xml.getNodo(XML_NODO_MONOGRAFICA_DESCUENTO).getValue();
	        
	        if(descuentoString.contains(".")){
	        	descuentoString = descuentoString.replace(".", ",");
	        }
	        descuento = FormatUtil.getInstance().desformateaImporte(descuentoString);
	        
	        log.info("getDescuentoPromocionXML() - " + I18N.getTexto("Aplicando descuento de \"" + descuento + "\" en la linea "
	        		+ "del ticket " + idLinea + " con artículo " + codArticuloLinea));
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al sacar el dato \"" + XML_NODO_MONOGRAFICA_DESCUENTO + "\" del XML de la promoción monográfica.");
			log.error("getDescuentoPromocionXML() - " + msgError + " : " + e.getMessage(), e);
		}
		return descuento;
	}
	
	/* #################################### APLICACIÓN #################################### */
	
	public void procesarDescuentosPromocionesMonograficas(CARDOSOTicketManager ticketManager){
		log.info("procesarPromocionesMonograficas() : GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS...");
		
		/* PASO 1 : Realizamos comprobaciones de si alguna linea contiene promociones monográficas. */
		Boolean tienePromocionesMonograficas = false;
		if(ticketManager.getTicket().getLineas() != null && !ticketManager.getTicket().getLineas().isEmpty()){
			for(CARDOSOLineaTicket lineaTicket : (List<CARDOSOLineaTicket>) ticketManager.getTicket().getLineas()){
				if(lineaTicket.getPromocionMonograficaAplicada() != null){
					tienePromocionesMonograficas = true;
				}
			}
		}
		
		if(tienePromocionesMonograficas){
			BigDecimal importeMonograficas = BigDecimal.ZERO;
			BigDecimal importeTotalMonograficas = BigDecimal.ZERO;
			
			/* PASO 2 : Recorremos las lineas del ticket que tienen promociones monográficas aplicadas, y 
			 * añadimos en cada una de las lineas el descuento manual de su promoción. 
			 * Además vamos acumulando los importe de descuento para tenerlos guardados en el ticket. */
			for(CARDOSOLineaTicket linea : (List<CARDOSOLineaTicket>) ticketManager.getTicket().getLineas()){
				if(linea.tienePromocionMonografica() && !linea.tieneCambioPrecioManual() && !linea.tieneDescuentoManual()){
					
					// Sacamos los valores de los importes antes de aplicar el descuento de la monografica.
					BigDecimal importeDto = linea.getImporteConDto();
					BigDecimal importeTotalDto = linea.getImporteTotalConDto();
					linea.setImporteSinDtoMonogafica(BigDecimalUtil.redondear(importeDto));
					linea.setImporteTotalSinDtoMonogafica(BigDecimalUtil.redondear(importeTotalDto));
					
					// Aplicamos el descuento.
					linea.setDescuentoManual(linea.getPorcentajeDtoMonografica());
					linea.recalcularImporteFinal();
					
					BigDecimal importeDtoMonografica = BigDecimalUtil.redondear(importeDto.subtract(linea.getImporteConDto()));
					linea.setImporteDtoMonografica(importeDtoMonografica);
					BigDecimal importeTotalDtoMonografica = BigDecimalUtil.redondear(importeTotalDto.subtract(linea.getImporteTotalConDto()));
					linea.setImporteTotalDtoMonografica(importeTotalDtoMonografica);
					
					importeMonograficas = importeMonograficas.add(importeDtoMonografica);
					importeTotalMonograficas = importeTotalMonograficas.add(importeTotalDtoMonografica);
					
				} 
				else{
					//linea.setPorcentajeDtoMonografica(BigDecimal.ZERO);
					importeMonograficas = importeMonograficas.add(linea.getImporteDtoMonografica());
					importeTotalMonograficas = importeTotalMonograficas.add(linea.getImporteTotalDtoMonografica());
				}
			}
			
			/* PASO 3 : Si hemos procesado alguna promoción monográfica en las lineas del ticket, debemos insertar los totales de estos 
			 * importes en la cabecera, para que quede registro de estas operaciones. 
			 * Luego realizamos el recalculo de totales. */
			if(!BigDecimalUtil.isIgualACero(importeMonograficas) && !BigDecimalUtil.isIgualACero(importeTotalMonograficas)){
				PromocionMonograficaCabeceraTicket promocionMonograficaCabecera = new PromocionMonograficaCabeceraTicket();
				promocionMonograficaCabecera.setImporte(importeMonograficas);
				promocionMonograficaCabecera.setImporteTotal(importeTotalMonograficas);
				
				/* Insertamos en el ticket los datos de promoción monográfica y recalculamos los totales después de haber aplicado el descuento manual. */
				((CARDOSOCabeceraTicket)ticketManager.getTicket().getCabecera()).setDatosDescuentoPromocionMonografica(promocionMonograficaCabecera);
				ticketManager.getTicket().getTotales().recalcular();
			}
			else{
				((CARDOSOCabeceraTicket)ticketManager.getTicket().getCabecera()).setDatosDescuentoPromocionMonografica(null);
			}
		}
		else{
			((CARDOSOCabeceraTicket)ticketManager.getTicket().getCabecera()).setDatosDescuentoPromocionMonografica(null);
		}
	}
	
	public Boolean lineaTienePromocionMonografica(List<PromocionLineaTicket> listPromocionesLineaTicket){
		for(PromocionLineaTicket promocionLinea : listPromocionesLineaTicket){
			if(isPromocionMonograficaLineaTicket(promocionLinea)){
				return true;
			}
		}
		return false;
	}
	
	public Boolean isPromocionMonograficaLineaTicket(PromocionLineaTicket promocionLinea){
		/* El tipo de promoción de la linea debe ser de tipo monográfica. */
		if(!promocionLinea.getIdTipoPromocion().equals(ID_TIPO_PROMOCION_MONOGRAFICA)){
			return false;
		}
		/* La promoción de la linea debe instanciar nuestro objeto personalizado. */
		if(!(promocionLinea instanceof PromocionMonograficaLineaTicket)){
			return false;
		}
		return true;
	}
	
	/* #################################### LIMPIAR DATOS #################################### */
	
	public void limpiarDatosLineaPromocionMonografica(List<CARDOSOLineaTicket> listLineaTicket){
		log.info("limpiarDatosLineaPromocionMonografica() : GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS...");
		
		for(CARDOSOLineaTicket linea : listLineaTicket){
			if(lineaTienePromocionMonografica(linea.getPromociones())){
				linea.setDescuentoManual(BigDecimal.ZERO);
				linea.recalcularImporteFinal();
			}
		}
	}
	
	/* ############################################################################################################## */
	/* ########################################## PROMOCIONES DE EMPLEADOS ########################################## */
	/* ############################################################################################################## */
	
	
	/* #################################### CONSULTA #################################### */

	public PromocionBean setPromocionesEmpleados(List<String> codColectivos, Long idTipoPromoDescuentoEmpleado) throws PromocionMonograficaException{
		log.debug("setPromocionesEmpleados() :  GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS...");
		try{
			Date fechaActual = new Date();
			List<PromocionBean> promociones = new ArrayList<PromocionBean>();
			
			PromocionExample example = new PromocionExample();
			if(codColectivos != null && !codColectivos.isEmpty()){
				example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
							.andIdTipoPromocionEqualTo(idTipoPromoDescuentoEmpleado)
							.andSoloFidelizacionEqualTo(true)
							.andFechaInicioLessThanOrEqualTo(fechaActual)
							.andFechaFinGreaterThanOrEqualTo(fechaActual)
							.andCodColectivoIn(codColectivos);
			}
			else{
				example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
							.andIdTipoPromocionEqualTo(idTipoPromoDescuentoEmpleado)
							.andSoloFidelizacionEqualTo(true)
							.andFechaInicioLessThanOrEqualTo(fechaActual)
							.andFechaFinGreaterThanOrEqualTo(fechaActual)
							.andCodColectivoIsNull();
			}
			example.setOrderByClause(PromocionExample.ORDER_BY_FECHA_INICIO_DESC + ", " + PromocionExample.ORDER_BY_FECHA_INICIO + ", " 
					+ PromocionExample.ORDER_BY_ID_PROMOCION_DESC);

			promociones = promocionMapper.selectByExampleWithBLOBs(example);
			return promociones != null && !promociones.isEmpty() ? promociones.get(0) : null;
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al consultar la promoción de descuento de empleado.");
			log.error("setPromocionesEmpleados() - " + msgError + " : " + e.getMessage(), e);
			throw new PromocionMonograficaException(msgError, e);
		}
	}
	
	/* #################################### APLICACIÓN #################################### */
	
	public void procesarDescuentosPromocionEmpleado(CARDOSOTicketManager ticketManager) throws PromocionMonograficaException{
		log.info("aplicarPromocionesEmpleado() : GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS...");
		
		/* PASO 1 : Limpiamos los datos de los descuentos de empleados. */
		String apiKeyPromocionEmpleados = "";
		((CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera()).setDatosDescuentoPromocionEmpleados(null);
		
		/* PASO 2 : Comprobaciones previas de que podemos aplicar promociones de empleados. */
		if(ticketManager.getTicket().getCabecera().getDatosFidelizado() == null){
			String msgInfo = I18N.getTexto("No se pueden aplicar promociones de tipo \"PROMOCIÓN DESCUENTO EMPLEADOS\" por no estar completos los datos del fidelizado.");
			log.info("aplicarPromocionesEmpleado() - " + msgInfo);
			return;
		}
		else{
			if(StringUtils.isBlank(ticketManager.getTicket().getCabecera().getDatosFidelizado().getNombre()) 
					|| StringUtils.isBlank(ticketManager.getTicket().getCabecera().getDatosFidelizado().getNumTarjetaFidelizado())){
				String msgInfo = I18N.getTexto("No se pueden aplicar promociones de tipo \"PROMOCIÓN DESCUENTO EMPLEADOS\" por no estar completos los datos del fidelizado.");
				log.info("aplicarPromocionesEmpleado() - " + msgInfo);
				return;
			}
		}
		if(ticketManager.getTicket().getTotales() == null){
			String msgInfo = I18N.getTexto("No se pueden aplicar promociones de tipo \"PROMOCIÓN DESCUENTO EMPLEADOS\" por faltar datos de importes totales en el ticket.");
			log.info("aplicarPromocionesEmpleado() - " + msgInfo);
			return;
		}
		try{
			apiKeyPromocionEmpleados = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
			if(StringUtils.isBlank(apiKeyPromocionEmpleados)){
				throw new PromocionMonograficaException();
			}
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al consultar los datos de la variable " + VariablesServices.WEBSERVICES_APIKEY + "");
			log.error("aplicarPromocionesEmpleado() - " + msgError + " : " + e.getMessage(), e);
			return;
		}
		
		/* PASO 3 : Sacamos el ID del tipo de promoción que va a usar. */
		Long idTipoPromoDescuentoEmpleado = 0L;
		String promoDescuentoEmpleadoString = variablesServices.getVariableAsString(VARIABLE_ID_TIPO_PROMOCION_EMPLEADOS);
		if(StringUtils.isBlank(promoDescuentoEmpleadoString)){
			log.error("setPromocionesEmpleados() - No se ha encontrado la variable '" + VARIABLE_ID_TIPO_PROMOCION_EMPLEADOS + ", se pone valor por defecto " + ID_TIPO_PROMOCION_EMPLEADOS);
			idTipoPromoDescuentoEmpleado = ID_TIPO_PROMOCION_EMPLEADOS;
		}
		else{
			try{
				idTipoPromoDescuentoEmpleado = Long.valueOf(promoDescuentoEmpleadoString);
			}
			catch(NumberFormatException e){
				String msgError = "La variable " + VARIABLE_ID_TIPO_PROMOCION_EMPLEADOS + " no tiene el formato correcto, se pone valor por defecto " + ID_TIPO_PROMOCION_EMPLEADOS;
				log.error("setPromocionesEmpleados() - " + msgError + " : " + e.getMessage(), e);
				idTipoPromoDescuentoEmpleado = ID_TIPO_PROMOCION_EMPLEADOS;
			}
		}
		
		/* PASO 4 : Realizamos la búsqueda en base de datos de las promociones de empleados registradas y cogemos la última creada. */
		PromocionBean promocion = setPromocionesEmpleados(ticketManager.getTicket().getCabecera().getDatosFidelizado().getCodColectivos(), idTipoPromoDescuentoEmpleado);
		if(promocion != null){
			
			/* PASO 5 : Si encontramos datos de la promoción, generamos el objeto con los datos y lo insertamos en el ticket. */
			PromocionEmpleadosCabeceraTicket datosDescuentoPromocionEmpleados = new PromocionEmpleadosCabeceraTicket();
			datosDescuentoPromocionEmpleados.setDescripcion("");
			datosDescuentoPromocionEmpleados.setTipoDto(new Long(0));
			
			datosDescuentoPromocionEmpleados.setIdPromocion(new Long(-1));
			datosDescuentoPromocionEmpleados.setTipoPromocion(idTipoPromoDescuentoEmpleado);
			
			datosDescuentoPromocionEmpleados.setTotalVenta(ticketManager.getTicket().getTotales().getTotal());
			
			datosDescuentoPromocionEmpleados.setImporteTotalAhorro(BigDecimal.ZERO);
			datosDescuentoPromocionEmpleados.setImporteAhorro(BigDecimal.ZERO);
			datosDescuentoPromocionEmpleados.setDescuento(BigDecimal.ZERO);
			
			datosDescuentoPromocionEmpleados.setApiKeyPromocionEmpleados(apiKeyPromocionEmpleados);
			datosDescuentoPromocionEmpleados.setPromocionEmpleado(promocion);
			((CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera()).setDatosDescuentoPromocionEmpleados(datosDescuentoPromocionEmpleados);
		}
		else{
			String msgInfo = I18N.getTexto("No se han encontrado resultados de promociones de tipo \"PROMOCIÓN DESCUENTO EMPLEADOS\".");
			log.info("aplicarPromocionesEmpleado() - " + msgInfo);
			return;
		}
	}
	
}
