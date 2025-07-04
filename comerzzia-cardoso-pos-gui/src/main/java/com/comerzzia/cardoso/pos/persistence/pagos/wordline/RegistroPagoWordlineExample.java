package com.comerzzia.cardoso.pos.persistence.pagos.wordline;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RegistroPagoWordlineExample {

	protected String orderByClause;

	protected boolean distinct;

	protected List<Criteria> oredCriteria;

	public RegistroPagoWordlineExample() {
		oredCriteria = new ArrayList<>();
	}

	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	public String getOrderByClause() {
		return orderByClause;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	protected abstract static class GeneratedCriteria {

		protected List<Criterion> criteria;

		protected GeneratedCriteria() {
			super();
			criteria = new ArrayList<>();
		}

		public boolean isValid() {
			return criteria.size() > 0;
		}

		public List<Criterion> getAllCriteria() {
			return criteria;
		}

		public List<Criterion> getCriteria() {
			return criteria;
		}

		protected void addCriterion(String condition) {
			if (condition == null) {
				throw new RuntimeException("Value for condition cannot be null");
			}
			criteria.add(new Criterion(condition));
		}

		protected void addCriterion(String condition, Object value, String property) {
			if (value == null) {
				throw new RuntimeException("Value for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value));
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property) {
			if (value1 == null || value2 == null) {
				throw new RuntimeException("Between values for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value1, value2));
		}

		protected void addCriterionForJDBCDate(String condition, Date value, String property) {
			if (value == null) {
				throw new RuntimeException("Value for " + property + " cannot be null");
			}
			addCriterion(condition, new java.sql.Date(value.getTime()), property);
		}

		protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
			if (values == null || values.size() == 0) {
				throw new RuntimeException("Value list for " + property + " cannot be null or empty");
			}
			List<java.sql.Date> dateList = new ArrayList<>();
			Iterator<Date> iter = values.iterator();
			while (iter.hasNext()) {
				dateList.add(new java.sql.Date(iter.next().getTime()));
			}
			addCriterion(condition, dateList, property);
		}

		protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
			if (value1 == null || value2 == null) {
				throw new RuntimeException("Between values for " + property + " cannot be null");
			}
			addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
		}

		public Criteria andUidActividadIsNull() {
			addCriterion("UID_ACTIVIDAD is null");
			return (Criteria) this;
		}

		public Criteria andUidActividadIsNotNull() {
			addCriterion("UID_ACTIVIDAD is not null");
			return (Criteria) this;
		}

		public Criteria andUidActividadEqualTo(String value) {
			addCriterion("UID_ACTIVIDAD =", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadNotEqualTo(String value) {
			addCriterion("UID_ACTIVIDAD <>", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadGreaterThan(String value) {
			addCriterion("UID_ACTIVIDAD >", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadGreaterThanOrEqualTo(String value) {
			addCriterion("UID_ACTIVIDAD >=", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadLessThan(String value) {
			addCriterion("UID_ACTIVIDAD <", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadLessThanOrEqualTo(String value) {
			addCriterion("UID_ACTIVIDAD <=", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadLike(String value) {
			addCriterion("UID_ACTIVIDAD like", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadNotLike(String value) {
			addCriterion("UID_ACTIVIDAD not like", value, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadIn(List<String> values) {
			addCriterion("UID_ACTIVIDAD in", values, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadNotIn(List<String> values) {
			addCriterion("UID_ACTIVIDAD not in", values, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadBetween(String value1, String value2) {
			addCriterion("UID_ACTIVIDAD between", value1, value2, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andUidActividadNotBetween(String value1, String value2) {
			addCriterion("UID_ACTIVIDAD not between", value1, value2, "uidActividad");
			return (Criteria) this;
		}

		public Criteria andFechaIsNull() {
			addCriterion("FECHA is null");
			return (Criteria) this;
		}

		public Criteria andFechaIsNotNull() {
			addCriterion("FECHA is not null");
			return (Criteria) this;
		}

		public Criteria andFechaEqualTo(Date value) {
			addCriterionForJDBCDate("FECHA =", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaNotEqualTo(Date value) {
			addCriterionForJDBCDate("FECHA <>", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaGreaterThan(Date value) {
			addCriterionForJDBCDate("FECHA >", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaGreaterThanOrEqualTo(Date value) {
			addCriterionForJDBCDate("FECHA >=", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaLessThan(Date value) {
			addCriterionForJDBCDate("FECHA <", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaLessThanOrEqualTo(Date value) {
			addCriterionForJDBCDate("FECHA <=", value, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaIn(List<Date> values) {
			addCriterionForJDBCDate("FECHA in", values, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaNotIn(List<Date> values) {
			addCriterionForJDBCDate("FECHA not in", values, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaBetween(Date value1, Date value2) {
			addCriterionForJDBCDate("FECHA between", value1, value2, "fecha");
			return (Criteria) this;
		}

		public Criteria andFechaNotBetween(Date value1, Date value2) {
			addCriterionForJDBCDate("FECHA not between", value1, value2, "fecha");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionIsNull() {
			addCriterion("TIPO_OPERACION is null");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionIsNotNull() {
			addCriterion("TIPO_OPERACION is not null");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionEqualTo(String value) {
			addCriterion("TIPO_OPERACION =", value, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionNotEqualTo(String value) {
			addCriterion("TIPO_OPERACION <>", value, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionGreaterThan(String value) {
			addCriterion("TIPO_OPERACION >", value, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionGreaterThanOrEqualTo(String value) {
			addCriterion("TIPO_OPERACION >=", value, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionLessThan(String value) {
			addCriterion("TIPO_OPERACION <", value, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionLessThanOrEqualTo(String value) {
			addCriterion("TIPO_OPERACION <=", value, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionLike(String value) {
			addCriterion("TIPO_OPERACION like", value, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionNotLike(String value) {
			addCriterion("TIPO_OPERACION not like", value, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionIn(List<String> values) {
			addCriterion("TIPO_OPERACION in", values, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionNotIn(List<String> values) {
			addCriterion("TIPO_OPERACION not in", values, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionBetween(String value1, String value2) {
			addCriterion("TIPO_OPERACION between", value1, value2, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionNotBetween(String value1, String value2) {
			addCriterion("TIPO_OPERACION not between", value1, value2, "tipoOperacion");
			return (Criteria) this;
		}

		public Criteria andImporteIsNull() {
			addCriterion("IMPORTE is null");
			return (Criteria) this;
		}

		public Criteria andImporteIsNotNull() {
			addCriterion("IMPORTE is not null");
			return (Criteria) this;
		}

		public Criteria andImporteEqualTo(BigDecimal value) {
			addCriterion("IMPORTE =", value, "importe");
			return (Criteria) this;
		}

		public Criteria andImporteNotEqualTo(BigDecimal value) {
			addCriterion("IMPORTE <>", value, "importe");
			return (Criteria) this;
		}

		public Criteria andImporteGreaterThan(BigDecimal value) {
			addCriterion("IMPORTE >", value, "importe");
			return (Criteria) this;
		}

		public Criteria andImporteGreaterThanOrEqualTo(BigDecimal value) {
			addCriterion("IMPORTE >=", value, "importe");
			return (Criteria) this;
		}

		public Criteria andImporteLessThan(BigDecimal value) {
			addCriterion("IMPORTE <", value, "importe");
			return (Criteria) this;
		}

		public Criteria andImporteLessThanOrEqualTo(BigDecimal value) {
			addCriterion("IMPORTE <=", value, "importe");
			return (Criteria) this;
		}

		public Criteria andImporteIn(List<BigDecimal> values) {
			addCriterion("IMPORTE in", values, "importe");
			return (Criteria) this;
		}

		public Criteria andImporteNotIn(List<BigDecimal> values) {
			addCriterion("IMPORTE not in", values, "importe");
			return (Criteria) this;
		}

		public Criteria andImporteBetween(BigDecimal value1, BigDecimal value2) {
			addCriterion("IMPORTE between", value1, value2, "importe");
			return (Criteria) this;
		}

		public Criteria andImporteNotBetween(BigDecimal value1, BigDecimal value2) {
			addCriterion("IMPORTE not between", value1, value2, "importe");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaIsNull() {
			addCriterion("NUM_OPER_VENTA is null");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaIsNotNull() {
			addCriterion("NUM_OPER_VENTA is not null");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaEqualTo(Integer value) {
			addCriterion("NUM_OPER_VENTA =", value, "numOperVenta");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaNotEqualTo(Integer value) {
			addCriterion("NUM_OPER_VENTA <>", value, "numOperVenta");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaGreaterThan(Integer value) {
			addCriterion("NUM_OPER_VENTA >", value, "numOperVenta");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaGreaterThanOrEqualTo(Integer value) {
			addCriterion("NUM_OPER_VENTA >=", value, "numOperVenta");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaLessThan(Integer value) {
			addCriterion("NUM_OPER_VENTA <", value, "numOperVenta");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaLessThanOrEqualTo(Integer value) {
			addCriterion("NUM_OPER_VENTA <=", value, "numOperVenta");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaIn(List<Integer> values) {
			addCriterion("NUM_OPER_VENTA in", values, "numOperVenta");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaNotIn(List<Integer> values) {
			addCriterion("NUM_OPER_VENTA not in", values, "numOperVenta");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaBetween(Integer value1, Integer value2) {
			addCriterion("NUM_OPER_VENTA between", value1, value2, "numOperVenta");
			return (Criteria) this;
		}

		public Criteria andNumOperVentaNotBetween(Integer value1, Integer value2) {
			addCriterion("NUM_OPER_VENTA not between", value1, value2, "numOperVenta");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolIsNull() {
			addCriterion("NUM_OPER_DEVOL is null");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolIsNotNull() {
			addCriterion("NUM_OPER_DEVOL is not null");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolEqualTo(Integer value) {
			addCriterion("NUM_OPER_DEVOL =", value, "numOperDevol");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolNotEqualTo(Integer value) {
			addCriterion("NUM_OPER_DEVOL <>", value, "numOperDevol");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolGreaterThan(Integer value) {
			addCriterion("NUM_OPER_DEVOL >", value, "numOperDevol");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolGreaterThanOrEqualTo(Integer value) {
			addCriterion("NUM_OPER_DEVOL >=", value, "numOperDevol");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolLessThan(Integer value) {
			addCriterion("NUM_OPER_DEVOL <", value, "numOperDevol");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolLessThanOrEqualTo(Integer value) {
			addCriterion("NUM_OPER_DEVOL <=", value, "numOperDevol");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolIn(List<Integer> values) {
			addCriterion("NUM_OPER_DEVOL in", values, "numOperDevol");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolNotIn(List<Integer> values) {
			addCriterion("NUM_OPER_DEVOL not in", values, "numOperDevol");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolBetween(Integer value1, Integer value2) {
			addCriterion("NUM_OPER_DEVOL between", value1, value2, "numOperDevol");
			return (Criteria) this;
		}

		public Criteria andNumOperDevolNotBetween(Integer value1, Integer value2) {
			addCriterion("NUM_OPER_DEVOL not between", value1, value2, "numOperDevol");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulIsNull() {
			addCriterion("NUM_OPER_ANUL is null");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulIsNotNull() {
			addCriterion("NUM_OPER_ANUL is not null");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulEqualTo(Integer value) {
			addCriterion("NUM_OPER_ANUL =", value, "numOperAnul");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulNotEqualTo(Integer value) {
			addCriterion("NUM_OPER_ANUL <>", value, "numOperAnul");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulGreaterThan(Integer value) {
			addCriterion("NUM_OPER_ANUL >", value, "numOperAnul");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulGreaterThanOrEqualTo(Integer value) {
			addCriterion("NUM_OPER_ANUL >=", value, "numOperAnul");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulLessThan(Integer value) {
			addCriterion("NUM_OPER_ANUL <", value, "numOperAnul");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulLessThanOrEqualTo(Integer value) {
			addCriterion("NUM_OPER_ANUL <=", value, "numOperAnul");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulIn(List<Integer> values) {
			addCriterion("NUM_OPER_ANUL in", values, "numOperAnul");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulNotIn(List<Integer> values) {
			addCriterion("NUM_OPER_ANUL not in", values, "numOperAnul");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulBetween(Integer value1, Integer value2) {
			addCriterion("NUM_OPER_ANUL between", value1, value2, "numOperAnul");
			return (Criteria) this;
		}

		public Criteria andNumOperAnulNotBetween(Integer value1, Integer value2) {
			addCriterion("NUM_OPER_ANUL not between", value1, value2, "numOperAnul");
			return (Criteria) this;
		}

		public Criteria andUidActividadLikeInsensitive(String value) {
			addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
			return (Criteria) this;
		}

		public Criteria andTipoOperacionLikeInsensitive(String value) {
			addCriterion("upper(TIPO_OPERACION) like", value.toUpperCase(), "tipoOperacion");
			return (Criteria) this;
		}
	}

	public static class Criteria extends GeneratedCriteria {

		protected Criteria() {
			super();
		}
	}

	public static class Criterion {

		private String condition;

		private Object value;

		private Object secondValue;

		private boolean noValue;

		private boolean singleValue;

		private boolean betweenValue;

		private boolean listValue;

		private String typeHandler;

		public String getCondition() {
			return condition;
		}

		public Object getValue() {
			return value;
		}

		public Object getSecondValue() {
			return secondValue;
		}

		public boolean isNoValue() {
			return noValue;
		}

		public boolean isSingleValue() {
			return singleValue;
		}

		public boolean isBetweenValue() {
			return betweenValue;
		}

		public boolean isListValue() {
			return listValue;
		}

		public String getTypeHandler() {
			return typeHandler;
		}

		protected Criterion(String condition) {
			super();
			this.condition = condition;
			this.typeHandler = null;
			this.noValue = true;
		}

		protected Criterion(String condition, Object value, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.typeHandler = typeHandler;
			if (value instanceof List<?>) {
				this.listValue = true;
			}
			else {
				this.singleValue = true;
			}
		}

		protected Criterion(String condition, Object value) {
			this(condition, value, null);
		}

		protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.secondValue = secondValue;
			this.typeHandler = typeHandler;
			this.betweenValue = true;
		}

		protected Criterion(String condition, Object value, Object secondValue) {
			this(condition, value, secondValue, null);
		}
	}
}