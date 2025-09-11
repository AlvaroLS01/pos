package com.comerzzia.iskaypet.pos.persistence.articlepoints.conditions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * GAP46 - CANJEO ARTÍCULOS POR PUNTOS
 */
public class ArticlePointsConditionsExample{

	protected String orderByClause;

	protected boolean distinct;

	protected List<Criteria> oredCriteria;

	public ArticlePointsConditionsExample(){
		oredCriteria = new ArrayList<>();
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
			criteria = new ArrayList<>();
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
			if(condition == null){
				throw new RuntimeException("Value for condition cannot be null");
			}
			criteria.add(new Criterion(condition));
		}

		protected void addCriterion(String condition, Object value, String property){
			if(value == null){
				throw new RuntimeException("Value for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value));
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property){
			if(value1 == null || value2 == null){
				throw new RuntimeException("Between values for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value1, value2));
		}

		public Criteria andActivityIdIsNull(){
			addCriterion("ACTIVITY_ID is null");
			return (Criteria) this;
		}

		public Criteria andActivityIdIsNotNull(){
			addCriterion("ACTIVITY_ID is not null");
			return (Criteria) this;
		}

		public Criteria andActivityIdEqualTo(String value){
			addCriterion("ACTIVITY_ID =", value, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdNotEqualTo(String value){
			addCriterion("ACTIVITY_ID <>", value, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdGreaterThan(String value){
			addCriterion("ACTIVITY_ID >", value, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdGreaterThanOrEqualTo(String value){
			addCriterion("ACTIVITY_ID >=", value, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdLessThan(String value){
			addCriterion("ACTIVITY_ID <", value, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdLessThanOrEqualTo(String value){
			addCriterion("ACTIVITY_ID <=", value, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdLike(String value){
			addCriterion("ACTIVITY_ID like", value, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdNotLike(String value){
			addCriterion("ACTIVITY_ID not like", value, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdIn(List<String> values){
			addCriterion("ACTIVITY_ID in", values, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdNotIn(List<String> values){
			addCriterion("ACTIVITY_ID not in", values, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdBetween(String value1, String value2){
			addCriterion("ACTIVITY_ID between", value1, value2, "activityId");
			return (Criteria) this;
		}

		public Criteria andActivityIdNotBetween(String value1, String value2){
			addCriterion("ACTIVITY_ID not between", value1, value2, "activityId");
			return (Criteria) this;
		}

		public Criteria andItemcodeIsNull(){
			addCriterion("ITEMCODE is null");
			return (Criteria) this;
		}

		public Criteria andItemcodeIsNotNull(){
			addCriterion("ITEMCODE is not null");
			return (Criteria) this;
		}

		public Criteria andItemcodeEqualTo(String value){
			addCriterion("ITEMCODE =", value, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeNotEqualTo(String value){
			addCriterion("ITEMCODE <>", value, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeGreaterThan(String value){
			addCriterion("ITEMCODE >", value, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeGreaterThanOrEqualTo(String value){
			addCriterion("ITEMCODE >=", value, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeLessThan(String value){
			addCriterion("ITEMCODE <", value, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeLessThanOrEqualTo(String value){
			addCriterion("ITEMCODE <=", value, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeLike(String value){
			addCriterion("ITEMCODE like", value, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeNotLike(String value){
			addCriterion("ITEMCODE not like", value, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeIn(List<String> values){
			addCriterion("ITEMCODE in", values, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeNotIn(List<String> values){
			addCriterion("ITEMCODE not in", values, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeBetween(String value1, String value2){
			addCriterion("ITEMCODE between", value1, value2, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeNotBetween(String value1, String value2){
			addCriterion("ITEMCODE not between", value1, value2, "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalIsNull(){
			addCriterion("ITEMCODE_CONDITIONAL is null");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalIsNotNull(){
			addCriterion("ITEMCODE_CONDITIONAL is not null");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalEqualTo(String value){
			addCriterion("ITEMCODE_CONDITIONAL =", value, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalNotEqualTo(String value){
			addCriterion("ITEMCODE_CONDITIONAL <>", value, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalGreaterThan(String value){
			addCriterion("ITEMCODE_CONDITIONAL >", value, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalGreaterThanOrEqualTo(String value){
			addCriterion("ITEMCODE_CONDITIONAL >=", value, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalLessThan(String value){
			addCriterion("ITEMCODE_CONDITIONAL <", value, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalLessThanOrEqualTo(String value){
			addCriterion("ITEMCODE_CONDITIONAL <=", value, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalLike(String value){
			addCriterion("ITEMCODE_CONDITIONAL like", value, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalNotLike(String value){
			addCriterion("ITEMCODE_CONDITIONAL not like", value, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalIn(List<String> values){
			addCriterion("ITEMCODE_CONDITIONAL in", values, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalNotIn(List<String> values){
			addCriterion("ITEMCODE_CONDITIONAL not in", values, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalBetween(String value1, String value2){
			addCriterion("ITEMCODE_CONDITIONAL between", value1, value2, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalNotBetween(String value1, String value2){
			addCriterion("ITEMCODE_CONDITIONAL not between", value1, value2, "itemcodeConditional");
			return (Criteria) this;
		}

		public Criteria andQuantityIsNull(){
			addCriterion("QUANTITY is null");
			return (Criteria) this;
		}

		public Criteria andQuantityIsNotNull(){
			addCriterion("QUANTITY is not null");
			return (Criteria) this;
		}

		public Criteria andQuantityEqualTo(BigDecimal value){
			addCriterion("QUANTITY =", value, "quantity");
			return (Criteria) this;
		}

		public Criteria andQuantityNotEqualTo(BigDecimal value){
			addCriterion("QUANTITY <>", value, "quantity");
			return (Criteria) this;
		}

		public Criteria andQuantityGreaterThan(BigDecimal value){
			addCriterion("QUANTITY >", value, "quantity");
			return (Criteria) this;
		}

		public Criteria andQuantityGreaterThanOrEqualTo(BigDecimal value){
			addCriterion("QUANTITY >=", value, "quantity");
			return (Criteria) this;
		}

		public Criteria andQuantityLessThan(BigDecimal value){
			addCriterion("QUANTITY <", value, "quantity");
			return (Criteria) this;
		}

		public Criteria andQuantityLessThanOrEqualTo(BigDecimal value){
			addCriterion("QUANTITY <=", value, "quantity");
			return (Criteria) this;
		}

		public Criteria andQuantityIn(List<BigDecimal> values){
			addCriterion("QUANTITY in", values, "quantity");
			return (Criteria) this;
		}

		public Criteria andQuantityNotIn(List<BigDecimal> values){
			addCriterion("QUANTITY not in", values, "quantity");
			return (Criteria) this;
		}

		public Criteria andQuantityBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("QUANTITY between", value1, value2, "quantity");
			return (Criteria) this;
		}

		public Criteria andQuantityNotBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("QUANTITY not between", value1, value2, "quantity");
			return (Criteria) this;
		}

		public Criteria andActivityIdLikeInsensitive(String value){
			addCriterion("upper(ACTIVITY_ID) like", value.toUpperCase(), "activityId");
			return (Criteria) this;
		}

		public Criteria andItemcodeLikeInsensitive(String value){
			addCriterion("upper(ITEMCODE) like", value.toUpperCase(), "itemcode");
			return (Criteria) this;
		}

		public Criteria andItemcodeConditionalLikeInsensitive(String value){
			addCriterion("upper(ITEMCODE_CONDITIONAL) like", value.toUpperCase(), "itemcodeConditional");
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