package com.comerzzia.cardoso.pos.persistence.agente;

import java.util.ArrayList;
import java.util.List;

/**
 * GAP - AGENTES 
 */
public class ClienteAgenteExample{

	public static final String ORDER_BY_CODCLI = "CODCLI";

	public static final String ORDER_BY_CODCLI_DESC = "CODCLI DESC";

	public static final String ORDER_BY_CODAGE = "CODAGE";

	public static final String ORDER_BY_CODAGE_DESC = "CODAGE DESC";

	protected String orderByClause;

	protected boolean distinct;

	protected List<Criteria> oredCriteria;

	public ClienteAgenteExample(){
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

		public Criteria andCodcliIsNull(){
			addCriterion("CODCLI is null");
			return (Criteria) this;
		}

		public Criteria andCodcliIsNotNull(){
			addCriterion("CODCLI is not null");
			return (Criteria) this;
		}

		public Criteria andCodcliEqualTo(String value){
			addCriterion("CODCLI =", value, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliNotEqualTo(String value){
			addCriterion("CODCLI <>", value, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliGreaterThan(String value){
			addCriterion("CODCLI >", value, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliGreaterThanOrEqualTo(String value){
			addCriterion("CODCLI >=", value, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliLessThan(String value){
			addCriterion("CODCLI <", value, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliLessThanOrEqualTo(String value){
			addCriterion("CODCLI <=", value, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliLike(String value){
			addCriterion("CODCLI like", value, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliNotLike(String value){
			addCriterion("CODCLI not like", value, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliIn(List<String> values){
			addCriterion("CODCLI in", values, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliNotIn(List<String> values){
			addCriterion("CODCLI not in", values, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliBetween(String value1, String value2){
			addCriterion("CODCLI between", value1, value2, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodcliNotBetween(String value1, String value2){
			addCriterion("CODCLI not between", value1, value2, "codcli");
			return (Criteria) this;
		}

		public Criteria andCodageIsNull(){
			addCriterion("CODAGE is null");
			return (Criteria) this;
		}

		public Criteria andCodageIsNotNull(){
			addCriterion("CODAGE is not null");
			return (Criteria) this;
		}

		public Criteria andCodageEqualTo(String value){
			addCriterion("CODAGE =", value, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageNotEqualTo(String value){
			addCriterion("CODAGE <>", value, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageGreaterThan(String value){
			addCriterion("CODAGE >", value, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageGreaterThanOrEqualTo(String value){
			addCriterion("CODAGE >=", value, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageLessThan(String value){
			addCriterion("CODAGE <", value, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageLessThanOrEqualTo(String value){
			addCriterion("CODAGE <=", value, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageLike(String value){
			addCriterion("CODAGE like", value, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageNotLike(String value){
			addCriterion("CODAGE not like", value, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageIn(List<String> values){
			addCriterion("CODAGE in", values, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageNotIn(List<String> values){
			addCriterion("CODAGE not in", values, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageBetween(String value1, String value2){
			addCriterion("CODAGE between", value1, value2, "codage");
			return (Criteria) this;
		}

		public Criteria andCodageNotBetween(String value1, String value2){
			addCriterion("CODAGE not between", value1, value2, "codage");
			return (Criteria) this;
		}

		public Criteria andCodcliLikeInsensitive(String value){
			addCriterion("upper(CODCLI) like", value.toUpperCase(), "codcli");
			return (Criteria) this;
		}

		public Criteria andCodageLikeInsensitive(String value){
			addCriterion("upper(CODAGE) like", value.toUpperCase(), "codage");
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