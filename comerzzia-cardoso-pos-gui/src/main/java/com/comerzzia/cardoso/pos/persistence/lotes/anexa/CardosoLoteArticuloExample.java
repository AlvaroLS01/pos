package com.comerzzia.cardoso.pos.persistence.lotes.anexa;

import java.util.ArrayList;
import java.util.List;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
public class CardosoLoteArticuloExample{

	public static final String ORDER_BY_UID_ACTIVIDAD = "UID_ACTIVIDAD";

	public static final String ORDER_BY_UID_ACTIVIDAD_DESC = "UID_ACTIVIDAD DESC";

	public static final String ORDER_BY_CODART = "CODART";

	public static final String ORDER_BY_CODART_DESC = "CODART DESC";

	public static final String ORDER_BY_LOTE = "LOTE";

	public static final String ORDER_BY_LOTE_DESC = "LOTE DESC";

	protected String orderByClause;

	protected boolean distinct;

	protected List<Criteria> oredCriteria;

	public CardosoLoteArticuloExample(){
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

		public Criteria andLoteIsNull(){
			addCriterion("LOTE is null");
			return (Criteria) this;
		}

		public Criteria andLoteIsNotNull(){
			addCriterion("LOTE is not null");
			return (Criteria) this;
		}

		public Criteria andLoteEqualTo(String value){
			addCriterion("LOTE =", value, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteNotEqualTo(String value){
			addCriterion("LOTE <>", value, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteGreaterThan(String value){
			addCriterion("LOTE >", value, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteGreaterThanOrEqualTo(String value){
			addCriterion("LOTE >=", value, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteLessThan(String value){
			addCriterion("LOTE <", value, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteLessThanOrEqualTo(String value){
			addCriterion("LOTE <=", value, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteLike(String value){
			addCriterion("LOTE like", value, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteNotLike(String value){
			addCriterion("LOTE not like", value, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteIn(List<String> values){
			addCriterion("LOTE in", values, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteNotIn(List<String> values){
			addCriterion("LOTE not in", values, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteBetween(String value1, String value2){
			addCriterion("LOTE between", value1, value2, "lote");
			return (Criteria) this;
		}

		public Criteria andLoteNotBetween(String value1, String value2){
			addCriterion("LOTE not between", value1, value2, "lote");
			return (Criteria) this;
		}

		public Criteria andUidActividadLikeInsensitive(String value){
			addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
			return (Criteria) this;
		}

		public Criteria andCodartLikeInsensitive(String value){
			addCriterion("upper(CODART) like", value.toUpperCase(), "codart");
			return (Criteria) this;
		}

		public Criteria andLoteLikeInsensitive(String value){
			addCriterion("upper(LOTE) like", value.toUpperCase(), "lote");
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