package com.comerzzia.iskaypet.pos.persistence.promotionsticker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * GAP62 - PEGATINAS PROMOCIONALES
 */
public class PromotionalStickerExample{

	protected String orderByClause;

	protected boolean distinct;

	protected List<Criteria> oredCriteria;

	public PromotionalStickerExample(){
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

		public Criteria andBarcodeIsNull(){
			addCriterion("BARCODE is null");
			return (Criteria) this;
		}

		public Criteria andBarcodeIsNotNull(){
			addCriterion("BARCODE is not null");
			return (Criteria) this;
		}

		public Criteria andBarcodeEqualTo(String value){
			addCriterion("BARCODE =", value, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeNotEqualTo(String value){
			addCriterion("BARCODE <>", value, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeGreaterThan(String value){
			addCriterion("BARCODE >", value, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeGreaterThanOrEqualTo(String value){
			addCriterion("BARCODE >=", value, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeLessThan(String value){
			addCriterion("BARCODE <", value, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeLessThanOrEqualTo(String value){
			addCriterion("BARCODE <=", value, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeLike(String value){
			addCriterion("BARCODE like", value, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeNotLike(String value){
			addCriterion("BARCODE not like", value, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeIn(List<String> values){
			addCriterion("BARCODE in", values, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeNotIn(List<String> values){
			addCriterion("BARCODE not in", values, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeBetween(String value1, String value2){
			addCriterion("BARCODE between", value1, value2, "barcode");
			return (Criteria) this;
		}

		public Criteria andBarcodeNotBetween(String value1, String value2){
			addCriterion("BARCODE not between", value1, value2, "barcode");
			return (Criteria) this;
		}

		public Criteria andCreationDateIsNull(){
			addCriterion("CREATION_DATE is null");
			return (Criteria) this;
		}

		public Criteria andCreationDateIsNotNull(){
			addCriterion("CREATION_DATE is not null");
			return (Criteria) this;
		}

		public Criteria andCreationDateEqualTo(Date value){
			addCriterion("CREATION_DATE =", value, "creationDate");
			return (Criteria) this;
		}

		public Criteria andCreationDateNotEqualTo(Date value){
			addCriterion("CREATION_DATE <>", value, "creationDate");
			return (Criteria) this;
		}

		public Criteria andCreationDateGreaterThan(Date value){
			addCriterion("CREATION_DATE >", value, "creationDate");
			return (Criteria) this;
		}

		public Criteria andCreationDateGreaterThanOrEqualTo(Date value){
			addCriterion("CREATION_DATE >=", value, "creationDate");
			return (Criteria) this;
		}

		public Criteria andCreationDateLessThan(Date value){
			addCriterion("CREATION_DATE <", value, "creationDate");
			return (Criteria) this;
		}

		public Criteria andCreationDateLessThanOrEqualTo(Date value){
			addCriterion("CREATION_DATE <=", value, "creationDate");
			return (Criteria) this;
		}

		public Criteria andCreationDateIn(List<Date> values){
			addCriterion("CREATION_DATE in", values, "creationDate");
			return (Criteria) this;
		}

		public Criteria andCreationDateNotIn(List<Date> values){
			addCriterion("CREATION_DATE not in", values, "creationDate");
			return (Criteria) this;
		}

		public Criteria andCreationDateBetween(Date value1, Date value2){
			addCriterion("CREATION_DATE between", value1, value2, "creationDate");
			return (Criteria) this;
		}

		public Criteria andCreationDateNotBetween(Date value1, Date value2){
			addCriterion("CREATION_DATE not between", value1, value2, "creationDate");
			return (Criteria) this;
		}

		public Criteria andItemCodeIsNull(){
			addCriterion("ITEM_CODE is null");
			return (Criteria) this;
		}

		public Criteria andItemCodeIsNotNull(){
			addCriterion("ITEM_CODE is not null");
			return (Criteria) this;
		}

		public Criteria andItemCodeEqualTo(String value){
			addCriterion("ITEM_CODE =", value, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeNotEqualTo(String value){
			addCriterion("ITEM_CODE <>", value, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeGreaterThan(String value){
			addCriterion("ITEM_CODE >", value, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeGreaterThanOrEqualTo(String value){
			addCriterion("ITEM_CODE >=", value, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeLessThan(String value){
			addCriterion("ITEM_CODE <", value, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeLessThanOrEqualTo(String value){
			addCriterion("ITEM_CODE <=", value, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeLike(String value){
			addCriterion("ITEM_CODE like", value, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeNotLike(String value){
			addCriterion("ITEM_CODE not like", value, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeIn(List<String> values){
			addCriterion("ITEM_CODE in", values, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeNotIn(List<String> values){
			addCriterion("ITEM_CODE not in", values, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeBetween(String value1, String value2){
			addCriterion("ITEM_CODE between", value1, value2, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemCodeNotBetween(String value1, String value2){
			addCriterion("ITEM_CODE not between", value1, value2, "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemDesIsNull(){
			addCriterion("ITEM_DES is null");
			return (Criteria) this;
		}

		public Criteria andItemDesIsNotNull(){
			addCriterion("ITEM_DES is not null");
			return (Criteria) this;
		}

		public Criteria andItemDesEqualTo(String value){
			addCriterion("ITEM_DES =", value, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesNotEqualTo(String value){
			addCriterion("ITEM_DES <>", value, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesGreaterThan(String value){
			addCriterion("ITEM_DES >", value, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesGreaterThanOrEqualTo(String value){
			addCriterion("ITEM_DES >=", value, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesLessThan(String value){
			addCriterion("ITEM_DES <", value, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesLessThanOrEqualTo(String value){
			addCriterion("ITEM_DES <=", value, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesLike(String value){
			addCriterion("ITEM_DES like", value, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesNotLike(String value){
			addCriterion("ITEM_DES not like", value, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesIn(List<String> values){
			addCriterion("ITEM_DES in", values, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesNotIn(List<String> values){
			addCriterion("ITEM_DES not in", values, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesBetween(String value1, String value2){
			addCriterion("ITEM_DES between", value1, value2, "itemDes");
			return (Criteria) this;
		}

		public Criteria andItemDesNotBetween(String value1, String value2){
			addCriterion("ITEM_DES not between", value1, value2, "itemDes");
			return (Criteria) this;
		}

		public Criteria andDiscountIsNull(){
			addCriterion("DISCOUNT is null");
			return (Criteria) this;
		}

		public Criteria andDiscountIsNotNull(){
			addCriterion("DISCOUNT is not null");
			return (Criteria) this;
		}

		public Criteria andDiscountEqualTo(BigDecimal value){
			addCriterion("DISCOUNT =", value, "discount");
			return (Criteria) this;
		}

		public Criteria andDiscountNotEqualTo(BigDecimal value){
			addCriterion("DISCOUNT <>", value, "discount");
			return (Criteria) this;
		}

		public Criteria andDiscountGreaterThan(BigDecimal value){
			addCriterion("DISCOUNT >", value, "discount");
			return (Criteria) this;
		}

		public Criteria andDiscountGreaterThanOrEqualTo(BigDecimal value){
			addCriterion("DISCOUNT >=", value, "discount");
			return (Criteria) this;
		}

		public Criteria andDiscountLessThan(BigDecimal value){
			addCriterion("DISCOUNT <", value, "discount");
			return (Criteria) this;
		}

		public Criteria andDiscountLessThanOrEqualTo(BigDecimal value){
			addCriterion("DISCOUNT <=", value, "discount");
			return (Criteria) this;
		}

		public Criteria andDiscountIn(List<BigDecimal> values){
			addCriterion("DISCOUNT in", values, "discount");
			return (Criteria) this;
		}

		public Criteria andDiscountNotIn(List<BigDecimal> values){
			addCriterion("DISCOUNT not in", values, "discount");
			return (Criteria) this;
		}

		public Criteria andDiscountBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("DISCOUNT between", value1, value2, "discount");
			return (Criteria) this;
		}

		public Criteria andDiscountNotBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("DISCOUNT not between", value1, value2, "discount");
			return (Criteria) this;
		}

		public Criteria andReasonCodeIsNull(){
			addCriterion("REASON_CODE is null");
			return (Criteria) this;
		}

		public Criteria andReasonCodeIsNotNull(){
			addCriterion("REASON_CODE is not null");
			return (Criteria) this;
		}

		public Criteria andReasonCodeEqualTo(Long value){
			addCriterion("REASON_CODE =", value, "reasonCode");
			return (Criteria) this;
		}

		public Criteria andReasonCodeNotEqualTo(Long value){
			addCriterion("REASON_CODE <>", value, "reasonCode");
			return (Criteria) this;
		}

		public Criteria andReasonCodeGreaterThan(Long value){
			addCriterion("REASON_CODE >", value, "reasonCode");
			return (Criteria) this;
		}

		public Criteria andReasonCodeGreaterThanOrEqualTo(Long value){
			addCriterion("REASON_CODE >=", value, "reasonCode");
			return (Criteria) this;
		}

		public Criteria andReasonCodeLessThan(Long value){
			addCriterion("REASON_CODE <", value, "reasonCode");
			return (Criteria) this;
		}

		public Criteria andReasonCodeLessThanOrEqualTo(Long value){
			addCriterion("REASON_CODE <=", value, "reasonCode");
			return (Criteria) this;
		}

		public Criteria andReasonCodeIn(List<Long> values){
			addCriterion("REASON_CODE in", values, "reasonCode");
			return (Criteria) this;
		}

		public Criteria andReasonCodeNotIn(List<Long> values){
			addCriterion("REASON_CODE not in", values, "reasonCode");
			return (Criteria) this;
		}

		public Criteria andReasonCodeBetween(Long value1, Long value2){
			addCriterion("REASON_CODE between", value1, value2, "reasonCode");
			return (Criteria) this;
		}

		public Criteria andReasonCodeNotBetween(Long value1, Long value2){
			addCriterion("REASON_CODE not between", value1, value2, "reasonCode");
			return (Criteria) this;
		}

		public Criteria andAvailableIsNull(){
			addCriterion("AVAILABLE is null");
			return (Criteria) this;
		}

		public Criteria andAvailableIsNotNull(){
			addCriterion("AVAILABLE is not null");
			return (Criteria) this;
		}

		public Criteria andAvailableEqualTo(String value){
			addCriterion("AVAILABLE =", value, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableNotEqualTo(String value){
			addCriterion("AVAILABLE <>", value, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableGreaterThan(String value){
			addCriterion("AVAILABLE >", value, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableGreaterThanOrEqualTo(String value){
			addCriterion("AVAILABLE >=", value, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableLessThan(String value){
			addCriterion("AVAILABLE <", value, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableLessThanOrEqualTo(String value){
			addCriterion("AVAILABLE <=", value, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableLike(String value){
			addCriterion("AVAILABLE like", value, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableNotLike(String value){
			addCriterion("AVAILABLE not like", value, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableIn(List<String> values){
			addCriterion("AVAILABLE in", values, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableNotIn(List<String> values){
			addCriterion("AVAILABLE not in", values, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableBetween(String value1, String value2){
			addCriterion("AVAILABLE between", value1, value2, "available");
			return (Criteria) this;
		}

		public Criteria andAvailableNotBetween(String value1, String value2){
			addCriterion("AVAILABLE not between", value1, value2, "available");
			return (Criteria) this;
		}

		public Criteria andActivityIdLikeInsensitive(String value){
			addCriterion("upper(ACTIVITY_ID) like", value.toUpperCase(), "activityId");
			return (Criteria) this;
		}

		public Criteria andBarcodeLikeInsensitive(String value){
			addCriterion("upper(BARCODE) like", value.toUpperCase(), "barcode");
			return (Criteria) this;
		}

		public Criteria andItemCodeLikeInsensitive(String value){
			addCriterion("upper(ITEM_CODE) like", value.toUpperCase(), "itemCode");
			return (Criteria) this;
		}

		public Criteria andItemDesLikeInsensitive(String value){
			addCriterion("upper(ITEM_DES) like", value.toUpperCase(), "itemDes");
			return (Criteria) this;
		}

		public Criteria andAvailableLikeInsensitive(String value){
			addCriterion("upper(AVAILABLE) like", value.toUpperCase(), "available");
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