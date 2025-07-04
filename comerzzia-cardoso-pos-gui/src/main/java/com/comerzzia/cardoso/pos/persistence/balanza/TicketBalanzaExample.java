package com.comerzzia.cardoso.pos.persistence.balanza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA
 */
public class TicketBalanzaExample{

	public static final String ORDER_BY_UID_ACTIVIDAD = "UID_ACTIVIDAD";

	public static final String ORDER_BY_UID_ACTIVIDAD_DESC = "UID_ACTIVIDAD DESC";

	public static final String ORDER_BY_UID_TICKET_BALANZA = "UID_TICKET_BALANZA";

	public static final String ORDER_BY_UID_TICKET_BALANZA_DESC = "UID_TICKET_BALANZA DESC";

	public static final String ORDER_BY_NUM_TICKET_BALANZA = "NUM_TICKET_BALANZA";

	public static final String ORDER_BY_NUM_TICKET_BALANZA_DESC = "NUM_TICKET_BALANZA DESC";

	public static final String ORDER_BY_CODSECCION = "CODSECCION";

	public static final String ORDER_BY_CODSECCION_DESC = "CODSECCION DESC";

	public static final String ORDER_BY_FECHA = "FECHA";

	public static final String ORDER_BY_FECHA_DESC = "FECHA DESC";

	public static final String ORDER_BY_TOTAL = "TOTAL";

	public static final String ORDER_BY_TOTAL_DESC = "TOTAL DESC";

	public static final String ORDER_BY_UID_TICKET = "UID_TICKET";

	public static final String ORDER_BY_UID_TICKET_DESC = "UID_TICKET DESC";

	public static final String ORDER_BY_PROCESADO = "PROCESADO";

	public static final String ORDER_BY_PROCESADO_DESC = "PROCESADO DESC";

	public static final String ORDER_BY_FECHA_PROCESO = "FECHA_PROCESO";

	public static final String ORDER_BY_FECHA_PROCESO_DESC = "FECHA_PROCESO DESC";

	public static final String ORDER_BY_MENSAJE_PROCESO = "MENSAJE_PROCESO";

	public static final String ORDER_BY_MENSAJE_PROCESO_DESC = "MENSAJE_PROCESO DESC";

	public static final String ORDER_BY_TICKET_BALANZA = "TICKET_BALANZA";

	public static final String ORDER_BY_TICKET_BALANZA_DESC = "TICKET_BALANZA DESC";

	protected String orderByClause;

	protected boolean distinct;

	protected List<Criteria> oredCriteria;

	public TicketBalanzaExample(){
		oredCriteria = new ArrayList<Criteria>();
	}

	public void setOrderByClause(String orderByClause){
		this.orderByClause = orderByClause;
	}

	public String getOrderByClause(){
		return orderByClause;
	}

	public void setDistinct(boolean distinct){
		this.distinct = distinct;
	}

	public boolean isDistinct(){
		return distinct;
	}

	public List<Criteria> getOredCriteria(){
		return oredCriteria;
	}

	public void or(Criteria criteria){
		oredCriteria.add(criteria);
	}

	public Criteria or(){
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	public Criteria createCriteria(){
		Criteria criteria = createCriteriaInternal();
		if(oredCriteria.size() == 0){
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	protected Criteria createCriteriaInternal(){
		Criteria criteria = new Criteria();
		return criteria;
	}

	public void clear(){
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	protected abstract static class GeneratedCriteria{

		protected List<Criterion> procesadoCriteria;

		protected List<Criterion> allCriteria;

		protected List<Criterion> criteria;

		protected GeneratedCriteria(){
			super();
			criteria = new ArrayList<Criterion>();
			procesadoCriteria = new ArrayList<Criterion>();
		}

		public List<Criterion> getProcesadoCriteria(){
			return procesadoCriteria;
		}

		protected void addProcesadoCriterion(String condition, Object value, String property){
			if(value != null){
				procesadoCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
				allCriteria = null;
			}
		}

		protected void addProcesadoCriterion(String condition, Boolean value1, Boolean value2, String property){
			if(value1 != null && value2 != null){
				procesadoCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
				allCriteria = null;
			}
		}

		public boolean isValid(){
			return criteria.size() > 0 || procesadoCriteria.size() > 0;
		}

		public List<Criterion> getAllCriteria(){
			if(allCriteria == null){
				allCriteria = new ArrayList<Criterion>();
				allCriteria.addAll(criteria);
				allCriteria.addAll(procesadoCriteria);
			}
			return allCriteria;
		}

		public List<Criterion> getCriteria(){
			return criteria;
		}

		protected void addCriterion(String condition){
			if(condition != null){
				criteria.add(new Criterion(condition));
				allCriteria = null;
			}
		}

		protected void addCriterion(String condition, Object value, String property){
			if(value != null){
				criteria.add(new Criterion(condition, value));
				allCriteria = null;
			}
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property){
			if(value1 != null && value2 != null){
				criteria.add(new Criterion(condition, value1, value2));
				allCriteria = null;
			}
		}

		public Criteria andUidActividadIsNull(){
			addCriterion("UID_ACTIVIDAD is null");
			return (Criteria) this;
		}

		public Criteria andUidActividadIsNotNull(){
			addCriterion("UID_ACTIVIDAD is not null");
			return (Criteria) this;
		}

		public Criteria andUidActividadEqualTo(String value){
			addCriterion("UID_ACTIVIDAD =", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadNotEqualTo(String value){
			addCriterion("UID_ACTIVIDAD <>", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadGreaterThan(String value){
			addCriterion("UID_ACTIVIDAD >", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadGreaterThanOrEqualTo(String value){
			addCriterion("UID_ACTIVIDAD >=", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadLessThan(String value){
			addCriterion("UID_ACTIVIDAD <", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadLessThanOrEqualTo(String value){
			addCriterion("UID_ACTIVIDAD <=", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadLike(String value){
			addCriterion("UID_ACTIVIDAD like", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadNotLike(String value){
			addCriterion("UID_ACTIVIDAD not like", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadIn(List<String> values){
			addCriterion("UID_ACTIVIDAD in", values, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadNotIn(List<String> values){
			addCriterion("UID_ACTIVIDAD not in", values, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadBetween(String value1, String value2){
			addCriterion("UID_ACTIVIDAD between", value1, value2, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadNotBetween(String value1, String value2){
			addCriterion("UID_ACTIVIDAD not between", value1, value2, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaIsNull(){
			addCriterion("UID_TICKET_BALANZA is null");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaIsNotNull(){
			addCriterion("UID_TICKET_BALANZA is not null");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaEqualTo(String value){
			addCriterion("UID_TICKET_BALANZA =", value, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaNotEqualTo(String value){
			addCriterion("UID_TICKET_BALANZA <>", value, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaGreaterThan(String value){
			addCriterion("UID_TICKET_BALANZA >", value, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaGreaterThanOrEqualTo(String value){
			addCriterion("UID_TICKET_BALANZA >=", value, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaLessThan(String value){
			addCriterion("UID_TICKET_BALANZA <", value, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaLessThanOrEqualTo(String value){
			addCriterion("UID_TICKET_BALANZA <=", value, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaLike(String value){
			addCriterion("UID_TICKET_BALANZA like", value, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaNotLike(String value){
			addCriterion("UID_TICKET_BALANZA not like", value, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaIn(List<String> values){
			addCriterion("UID_TICKET_BALANZA in", values, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaNotIn(List<String> values){
			addCriterion("UID_TICKET_BALANZA not in", values, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaBetween(String value1, String value2){
			addCriterion("UID_TICKET_BALANZA between", value1, value2, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaNotBetween(String value1, String value2){
			addCriterion("UID_TICKET_BALANZA not between", value1, value2, "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaIsNull(){
			addCriterion("NUM_TICKET_BALANZA is null");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaIsNotNull(){
			addCriterion("NUM_TICKET_BALANZA is not null");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaEqualTo(String value){
			addCriterion("NUM_TICKET_BALANZA =", value, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaNotEqualTo(String value){
			addCriterion("NUM_TICKET_BALANZA <>", value, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaGreaterThan(String value){
			addCriterion("NUM_TICKET_BALANZA >", value, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaGreaterThanOrEqualTo(String value){
			addCriterion("NUM_TICKET_BALANZA >=", value, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaLessThan(String value){
			addCriterion("NUM_TICKET_BALANZA <", value, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaLessThanOrEqualTo(String value){
			addCriterion("NUM_TICKET_BALANZA <=", value, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaLike(String value){
			addCriterion("NUM_TICKET_BALANZA like", value, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaNotLike(String value){
			addCriterion("NUM_TICKET_BALANZA not like", value, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaIn(List<String> values){
			addCriterion("NUM_TICKET_BALANZA in", values, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaNotIn(List<String> values){
			addCriterion("NUM_TICKET_BALANZA not in", values, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaBetween(String value1, String value2){
			addCriterion("NUM_TICKET_BALANZA between", value1, value2, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaNotBetween(String value1, String value2){
			addCriterion("NUM_TICKET_BALANZA not between", value1, value2, "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andCodseccionIsNull(){
			addCriterion("CODSECCION is null");
			return (Criteria) this;
		}

		public Criteria andCodseccionIsNotNull(){
			addCriterion("CODSECCION is not null");
			return (Criteria) this;
		}

		public Criteria andCodseccionEqualTo(String value){
			addCriterion("CODSECCION =", value, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionNotEqualTo(String value){
			addCriterion("CODSECCION <>", value, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionGreaterThan(String value){
			addCriterion("CODSECCION >", value, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionGreaterThanOrEqualTo(String value){
			addCriterion("CODSECCION >=", value, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionLessThan(String value){
			addCriterion("CODSECCION <", value, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionLessThanOrEqualTo(String value){
			addCriterion("CODSECCION <=", value, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionLike(String value){
			addCriterion("CODSECCION like", value, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionNotLike(String value){
			addCriterion("CODSECCION not like", value, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionIn(List<String> values){
			addCriterion("CODSECCION in", values, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionNotIn(List<String> values){
			addCriterion("CODSECCION not in", values, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionBetween(String value1, String value2){
			addCriterion("CODSECCION between", value1, value2, "codseccion");
			return (Criteria) this;
		}

		public Criteria andCodseccionNotBetween(String value1, String value2){
			addCriterion("CODSECCION not between", value1, value2, "codseccion");
			return (Criteria) this;
		}

		public Criteria andFechaIsNull(){
			addCriterion("FECHA is null");
			return (Criteria) this;
		}

		public Criteria andFechaIsNotNull(){
			addCriterion("FECHA is not null");
			return (Criteria) this;
		}

		public Criteria andFechaEqualTo(Date value){
			addCriterion("FECHA =", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaNotEqualTo(Date value){
			addCriterion("FECHA <>", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaGreaterThan(Date value){
			addCriterion("FECHA >", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaGreaterThanOrEqualTo(Date value){
			addCriterion("FECHA >=", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaLessThan(Date value){
			addCriterion("FECHA <", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaLessThanOrEqualTo(Date value){
			addCriterion("FECHA <=", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaIn(List<Date> values){
			addCriterion("FECHA in", values, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaNotIn(List<Date> values){
			addCriterion("FECHA not in", values, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaBetween(Date value1, Date value2){
			addCriterion("FECHA between", value1, value2, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaNotBetween(Date value1, Date value2){
			addCriterion("FECHA not between", value1, value2, "fecha");
			return (Criteria) this;
		}

		public Criteria andTotalIsNull(){
			addCriterion("TOTAL is null");
			return (Criteria) this;
		}

		public Criteria andTotalIsNotNull(){
			addCriterion("TOTAL is not null");
			return (Criteria) this;
		}

		public Criteria andTotalEqualTo(BigDecimal value){
			addCriterion("TOTAL =", value, "total");
			return (Criteria) this;
		}

		public Criteria andTotalNotEqualTo(BigDecimal value){
			addCriterion("TOTAL <>", value, "total");
			return (Criteria) this;
		}

		public Criteria andTotalGreaterThan(BigDecimal value){
			addCriterion("TOTAL >", value, "total");
			return (Criteria) this;
		}

		public Criteria andTotalGreaterThanOrEqualTo(BigDecimal value){
			addCriterion("TOTAL >=", value, "total");
			return (Criteria) this;
		}

		public Criteria andTotalLessThan(BigDecimal value){
			addCriterion("TOTAL <", value, "total");
			return (Criteria) this;
		}

		public Criteria andTotalLessThanOrEqualTo(BigDecimal value){
			addCriterion("TOTAL <=", value, "total");
			return (Criteria) this;
		}

		public Criteria andTotalIn(List<BigDecimal> values){
			addCriterion("TOTAL in", values, "total");
			return (Criteria) this;
		}

		public Criteria andTotalNotIn(List<BigDecimal> values){
			addCriterion("TOTAL not in", values, "total");
			return (Criteria) this;
		}

		public Criteria andTotalBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("TOTAL between", value1, value2, "total");
			return (Criteria) this;
		}

		public Criteria andTotalNotBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("TOTAL not between", value1, value2, "total");
			return (Criteria) this;
		}

		public Criteria andUidTicketIsNull(){
			addCriterion("UID_TICKET is null");
			return (Criteria) this;
		}

		public Criteria andUidTicketIsNotNull(){
			addCriterion("UID_TICKET is not null");
			return (Criteria) this;
		}

		public Criteria andUidTicketEqualTo(String value){
			addCriterion("UID_TICKET =", value, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketNotEqualTo(String value){
			addCriterion("UID_TICKET <>", value, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketGreaterThan(String value){
			addCriterion("UID_TICKET >", value, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketGreaterThanOrEqualTo(String value){
			addCriterion("UID_TICKET >=", value, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketLessThan(String value){
			addCriterion("UID_TICKET <", value, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketLessThanOrEqualTo(String value){
			addCriterion("UID_TICKET <=", value, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketLike(String value){
			addCriterion("UID_TICKET like", value, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketNotLike(String value){
			addCriterion("UID_TICKET not like", value, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketIn(List<String> values){
			addCriterion("UID_TICKET in", values, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketNotIn(List<String> values){
			addCriterion("UID_TICKET not in", values, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketBetween(String value1, String value2){
			addCriterion("UID_TICKET between", value1, value2, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andUidTicketNotBetween(String value1, String value2){
			addCriterion("UID_TICKET not between", value1, value2, "uidTicket");
			return (Criteria) this;
		}

		public Criteria andProcesadoIsNull(){
			addCriterion("PROCESADO is null");
			return (Criteria) this;
		}

		public Criteria andProcesadoIsNotNull(){
			addCriterion("PROCESADO is not null");
			return (Criteria) this;
		}

		public Criteria andProcesadoEqualTo(Boolean value){
			addProcesadoCriterion("PROCESADO =", value, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoNotEqualTo(Boolean value){
			addProcesadoCriterion("PROCESADO <>", value, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoGreaterThan(Boolean value){
			addProcesadoCriterion("PROCESADO >", value, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoGreaterThanOrEqualTo(Boolean value){
			addProcesadoCriterion("PROCESADO >=", value, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoLessThan(Boolean value){
			addProcesadoCriterion("PROCESADO <", value, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoLessThanOrEqualTo(Boolean value){
			addProcesadoCriterion("PROCESADO <=", value, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoLike(Boolean value){
			addProcesadoCriterion("PROCESADO like", value, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoNotLike(Boolean value){
			addProcesadoCriterion("PROCESADO not like", value, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoIn(List<Boolean> values){
			addProcesadoCriterion("PROCESADO in", values, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoNotIn(List<Boolean> values){
			addProcesadoCriterion("PROCESADO not in", values, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoBetween(Boolean value1, Boolean value2){
			addProcesadoCriterion("PROCESADO between", value1, value2, "procesado");
			return (Criteria) this;
		}

		public Criteria andProcesadoNotBetween(Boolean value1, Boolean value2){
			addProcesadoCriterion("PROCESADO not between", value1, value2, "procesado");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoIsNull(){
			addCriterion("FECHA_PROCESO is null");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoIsNotNull(){
			addCriterion("FECHA_PROCESO is not null");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoEqualTo(Date value){
			addCriterion("FECHA_PROCESO =", value, "fechaProceso");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoNotEqualTo(Date value){
			addCriterion("FECHA_PROCESO <>", value, "fechaProceso");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoGreaterThan(Date value){
			addCriterion("FECHA_PROCESO >", value, "fechaProceso");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoGreaterThanOrEqualTo(Date value){
			addCriterion("FECHA_PROCESO >=", value, "fechaProceso");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoLessThan(Date value){
			addCriterion("FECHA_PROCESO <", value, "fechaProceso");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoLessThanOrEqualTo(Date value){
			addCriterion("FECHA_PROCESO <=", value, "fechaProceso");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoIn(List<Date> values){
			addCriterion("FECHA_PROCESO in", values, "fechaProceso");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoNotIn(List<Date> values){
			addCriterion("FECHA_PROCESO not in", values, "fechaProceso");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoBetween(Date value1, Date value2){
			addCriterion("FECHA_PROCESO between", value1, value2, "fechaProceso");
			return (Criteria) this;
		}

		public Criteria andFechaProcesoNotBetween(Date value1, Date value2){
			addCriterion("FECHA_PROCESO not between", value1, value2, "fechaProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoIsNull(){
			addCriterion("MENSAJE_PROCESO is null");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoIsNotNull(){
			addCriterion("MENSAJE_PROCESO is not null");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoEqualTo(String value){
			addCriterion("MENSAJE_PROCESO =", value, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoNotEqualTo(String value){
			addCriterion("MENSAJE_PROCESO <>", value, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoGreaterThan(String value){
			addCriterion("MENSAJE_PROCESO >", value, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoGreaterThanOrEqualTo(String value){
			addCriterion("MENSAJE_PROCESO >=", value, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoLessThan(String value){
			addCriterion("MENSAJE_PROCESO <", value, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoLessThanOrEqualTo(String value){
			addCriterion("MENSAJE_PROCESO <=", value, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoLike(String value){
			addCriterion("MENSAJE_PROCESO like", value, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoNotLike(String value){
			addCriterion("MENSAJE_PROCESO not like", value, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoIn(List<String> values){
			addCriterion("MENSAJE_PROCESO in", values, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoNotIn(List<String> values){
			addCriterion("MENSAJE_PROCESO not in", values, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoBetween(String value1, String value2){
			addCriterion("MENSAJE_PROCESO between", value1, value2, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoNotBetween(String value1, String value2){
			addCriterion("MENSAJE_PROCESO not between", value1, value2, "mensajeProceso");
			return (Criteria) this;
		}

		public Criteria andUidActividadLikeInsensitive(String value){
			addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidTicketBalanzaLikeInsensitive(String value){
			addCriterion("upper(UID_TICKET_BALANZA) like", value.toUpperCase(), "uidTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andNumTicketBalanzaLikeInsensitive(String value){
			addCriterion("upper(NUM_TICKET_BALANZA) like", value.toUpperCase(), "numTicketBalanza");
			return (Criteria) this;
		}

		public Criteria andCodseccionLikeInsensitive(String value){
			addCriterion("upper(CODSECCION) like", value.toUpperCase(), "codseccion");
			return (Criteria) this;
		}

		public Criteria andUidTicketLikeInsensitive(String value){
			addCriterion("upper(UID_TICKET) like", value.toUpperCase(), "uidTicket");
			return (Criteria) this;
		}

		public Criteria andMensajeProcesoLikeInsensitive(String value){
			addCriterion("upper(MENSAJE_PROCESO) like", value.toUpperCase(), "mensajeProceso");
			return (Criteria) this;
		}
	}

	public static class Criteria extends GeneratedCriteria{

		protected Criteria(){
			super();
		}
	}

	public static class Criterion{

		private String condition;

		private Object value;

		private Object secondValue;

		private boolean noValue;

		private boolean singleValue;

		private boolean betweenValue;

		private boolean listValue;

		private String typeHandler;

		public String getCondition(){
			return condition;
		}

		public Object getValue(){
			return value;
		}

		public Object getSecondValue(){
			return secondValue;
		}

		public boolean isNoValue(){
			return noValue;
		}

		public boolean isSingleValue(){
			return singleValue;
		}

		public boolean isBetweenValue(){
			return betweenValue;
		}

		public boolean isListValue(){
			return listValue;
		}

		public String getTypeHandler(){
			return typeHandler;
		}

		protected Criterion(String condition){
			super();
			this.condition = condition;
			this.typeHandler = null;
			this.noValue = true;
		}

		protected Criterion(String condition, Object value, String typeHandler){
			super();
			this.condition = condition;
			this.value = value;
			this.typeHandler = typeHandler;
			if(value instanceof List<?>){
				this.listValue = true;
			}
			else{
				this.singleValue = true;
			}
		}

		protected Criterion(String condition, Object value){
			this(condition, value, null);
		}

		protected Criterion(String condition, Object value, Object secondValue, String typeHandler){
			super();
			this.condition = condition;
			this.value = value;
			this.secondValue = secondValue;
			this.typeHandler = typeHandler;
			this.betweenValue = true;
		}

		protected Criterion(String condition, Object value, Object secondValue){
			this(condition, value, secondValue, null);
		}
	}
}