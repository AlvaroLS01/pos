package com.comerzzia.cardoso.pos.persistence.tarifas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * GAP - DESCUENTO TARIFA
 */
public class TarifaDetalleAnexaExample{

	public static final String ORDER_BY_UID_ACTIVIDAD = "UID_ACTIVIDAD";

	public static final String ORDER_BY_UID_ACTIVIDAD_DESC = "UID_ACTIVIDAD DESC";

	public static final String ORDER_BY_CODTAR = "CODTAR";

	public static final String ORDER_BY_CODTAR_DESC = "CODTAR DESC";

	public static final String ORDER_BY_CODART = "CODART";

	public static final String ORDER_BY_CODART_DESC = "CODART DESC";

	public static final String ORDER_BY_PRECIO_VENTA_SIN_DTO = "PRECIO_VENTA_SIN_DTO";

	public static final String ORDER_BY_PRECIO_VENTA_SIN_DTO_DESC = "PRECIO_VENTA_SIN_DTO DESC";

	public static final String ORDER_BY_PRECIO_TOTAL_SIN_DTO = "PRECIO_TOTAL_SIN_DTO";

	public static final String ORDER_BY_PRECIO_TOTAL_SIN_DTO_DESC = "PRECIO_TOTAL_SIN_DTO DESC";

	public static final String ORDER_BY_DESCUENTO_TARIFA = "DESCUENTO_TARIFA";

	public static final String ORDER_BY_DESCUENTO_TARIFA_DESC = "DESCUENTO_TARIFA DESC";

	protected String orderByClause;

	protected boolean distinct;

	protected List<Criteria> oredCriteria;

	public TarifaDetalleAnexaExample(){
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

		protected List<Criterion> criteria;

		protected GeneratedCriteria(){
			super();
			criteria = new ArrayList<Criterion>();
		}

		public boolean isValid(){
			return criteria.size() > 0;
		}

		public List<Criterion> getAllCriteria(){
			return criteria;
		}

		public List<Criterion> getCriteria(){
			return criteria;
		}

		protected void addCriterion(String condition){
			if(condition != null){
				criteria.add(new Criterion(condition));
			}
		}

		protected void addCriterion(String condition, Object value, String property){
			if(value != null){
				criteria.add(new Criterion(condition, value));
			}
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property){
			if(value1 != null && value2 != null){
				criteria.add(new Criterion(condition, value1, value2));
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

		public Criteria andCodtarIsNull(){
			addCriterion("CODTAR is null");
			return (Criteria) this;
		}

		public Criteria andCodtarIsNotNull(){
			addCriterion("CODTAR is not null");
			return (Criteria) this;
		}

		public Criteria andCodtarEqualTo(String value){
			addCriterion("CODTAR =", value, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarNotEqualTo(String value){
			addCriterion("CODTAR <>", value, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarGreaterThan(String value){
			addCriterion("CODTAR >", value, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarGreaterThanOrEqualTo(String value){
			addCriterion("CODTAR >=", value, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarLessThan(String value){
			addCriterion("CODTAR <", value, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarLessThanOrEqualTo(String value){
			addCriterion("CODTAR <=", value, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarLike(String value){
			addCriterion("CODTAR like", value, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarNotLike(String value){
			addCriterion("CODTAR not like", value, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarIn(List<String> values){
			addCriterion("CODTAR in", values, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarNotIn(List<String> values){
			addCriterion("CODTAR not in", values, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarBetween(String value1, String value2){
			addCriterion("CODTAR between", value1, value2, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodtarNotBetween(String value1, String value2){
			addCriterion("CODTAR not between", value1, value2, "codtar");
			return (Criteria) this;
		}

		public Criteria andCodartIsNull(){
			addCriterion("CODART is null");
			return (Criteria) this;
		}

		public Criteria andCodartIsNotNull(){
			addCriterion("CODART is not null");
			return (Criteria) this;
		}

		public Criteria andCodartEqualTo(String value){
			addCriterion("CODART =", value, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartNotEqualTo(String value){
			addCriterion("CODART <>", value, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartGreaterThan(String value){
			addCriterion("CODART >", value, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartGreaterThanOrEqualTo(String value){
			addCriterion("CODART >=", value, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartLessThan(String value){
			addCriterion("CODART <", value, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartLessThanOrEqualTo(String value){
			addCriterion("CODART <=", value, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartLike(String value){
			addCriterion("CODART like", value, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartNotLike(String value){
			addCriterion("CODART not like", value, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartIn(List<String> values){
			addCriterion("CODART in", values, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartNotIn(List<String> values){
			addCriterion("CODART not in", values, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartBetween(String value1, String value2){
			addCriterion("CODART between", value1, value2, "codart");
			return (Criteria) this;
		}

		public Criteria andCodartNotBetween(String value1, String value2){
			addCriterion("CODART not between", value1, value2, "codart");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoIsNull(){
			addCriterion("PRECIO_VENTA_SIN_DTO is null");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoIsNotNull(){
			addCriterion("PRECIO_VENTA_SIN_DTO is not null");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoEqualTo(BigDecimal value){
			addCriterion("PRECIO_VENTA_SIN_DTO =", value, "precioVentaSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoNotEqualTo(BigDecimal value){
			addCriterion("PRECIO_VENTA_SIN_DTO <>", value, "precioVentaSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoGreaterThan(BigDecimal value){
			addCriterion("PRECIO_VENTA_SIN_DTO >", value, "precioVentaSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoGreaterThanOrEqualTo(BigDecimal value){
			addCriterion("PRECIO_VENTA_SIN_DTO >=", value, "precioVentaSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoLessThan(BigDecimal value){
			addCriterion("PRECIO_VENTA_SIN_DTO <", value, "precioVentaSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoLessThanOrEqualTo(BigDecimal value){
			addCriterion("PRECIO_VENTA_SIN_DTO <=", value, "precioVentaSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoIn(List<BigDecimal> values){
			addCriterion("PRECIO_VENTA_SIN_DTO in", values, "precioVentaSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoNotIn(List<BigDecimal> values){
			addCriterion("PRECIO_VENTA_SIN_DTO not in", values, "precioVentaSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("PRECIO_VENTA_SIN_DTO between", value1, value2, "precioVentaSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioVentaSinDtoNotBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("PRECIO_VENTA_SIN_DTO not between", value1, value2, "precioVentaSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoIsNull(){
			addCriterion("PRECIO_TOTAL_SIN_DTO is null");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoIsNotNull(){
			addCriterion("PRECIO_TOTAL_SIN_DTO is not null");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoEqualTo(BigDecimal value){
			addCriterion("PRECIO_TOTAL_SIN_DTO =", value, "precioTotalSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoNotEqualTo(BigDecimal value){
			addCriterion("PRECIO_TOTAL_SIN_DTO <>", value, "precioTotalSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoGreaterThan(BigDecimal value){
			addCriterion("PRECIO_TOTAL_SIN_DTO >", value, "precioTotalSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoGreaterThanOrEqualTo(BigDecimal value){
			addCriterion("PRECIO_TOTAL_SIN_DTO >=", value, "precioTotalSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoLessThan(BigDecimal value){
			addCriterion("PRECIO_TOTAL_SIN_DTO <", value, "precioTotalSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoLessThanOrEqualTo(BigDecimal value){
			addCriterion("PRECIO_TOTAL_SIN_DTO <=", value, "precioTotalSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoIn(List<BigDecimal> values){
			addCriterion("PRECIO_TOTAL_SIN_DTO in", values, "precioTotalSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoNotIn(List<BigDecimal> values){
			addCriterion("PRECIO_TOTAL_SIN_DTO not in", values, "precioTotalSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("PRECIO_TOTAL_SIN_DTO between", value1, value2, "precioTotalSinDto");
			return (Criteria) this;
		}

		public Criteria andPrecioTotalSinDtoNotBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("PRECIO_TOTAL_SIN_DTO not between", value1, value2, "precioTotalSinDto");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaIsNull(){
			addCriterion("DESCUENTO_TARIFA is null");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaIsNotNull(){
			addCriterion("DESCUENTO_TARIFA is not null");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaEqualTo(BigDecimal value){
			addCriterion("DESCUENTO_TARIFA =", value, "descuentoTarifa");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaNotEqualTo(BigDecimal value){
			addCriterion("DESCUENTO_TARIFA <>", value, "descuentoTarifa");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaGreaterThan(BigDecimal value){
			addCriterion("DESCUENTO_TARIFA >", value, "descuentoTarifa");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaGreaterThanOrEqualTo(BigDecimal value){
			addCriterion("DESCUENTO_TARIFA >=", value, "descuentoTarifa");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaLessThan(BigDecimal value){
			addCriterion("DESCUENTO_TARIFA <", value, "descuentoTarifa");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaLessThanOrEqualTo(BigDecimal value){
			addCriterion("DESCUENTO_TARIFA <=", value, "descuentoTarifa");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaIn(List<BigDecimal> values){
			addCriterion("DESCUENTO_TARIFA in", values, "descuentoTarifa");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaNotIn(List<BigDecimal> values){
			addCriterion("DESCUENTO_TARIFA not in", values, "descuentoTarifa");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("DESCUENTO_TARIFA between", value1, value2, "descuentoTarifa");
			return (Criteria) this;
		}

		public Criteria andDescuentoTarifaNotBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("DESCUENTO_TARIFA not between", value1, value2, "descuentoTarifa");
			return (Criteria) this;
		}

		public Criteria andUidActividadLikeInsensitive(String value){
			addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
			return (Criteria) this;
		}

		public Criteria andCodtarLikeInsensitive(String value){
			addCriterion("upper(CODTAR) like", value.toUpperCase(), "codtar");
			return (Criteria) this;
		}

		public Criteria andCodartLikeInsensitive(String value){
			addCriterion("upper(CODART) like", value.toUpperCase(), "codart");
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