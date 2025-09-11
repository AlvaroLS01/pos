package com.comerzzia.iskaypet.pos.persistence.closingday;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * GAP27.2 - AMPLIACIÓN DEL CIERRE DE FIN DE DÍA
 */
public class HeaderClosingEndDayExample{

	protected String orderByClause;

	protected boolean distinct;

	protected List<Criteria> oredCriteria;

	public HeaderClosingEndDayExample(){
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

		public Criteria andActivityUidIsNull(){
			addCriterion("ACTIVITY_UID is null");
			return (Criteria) this;
		}

		public Criteria andActivityUidIsNotNull(){
			addCriterion("ACTIVITY_UID is not null");
			return (Criteria) this;
		}

		public Criteria andActivityUidEqualTo(String value){
			addCriterion("ACTIVITY_UID =", value, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidNotEqualTo(String value){
			addCriterion("ACTIVITY_UID <>", value, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidGreaterThan(String value){
			addCriterion("ACTIVITY_UID >", value, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidGreaterThanOrEqualTo(String value){
			addCriterion("ACTIVITY_UID >=", value, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidLessThan(String value){
			addCriterion("ACTIVITY_UID <", value, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidLessThanOrEqualTo(String value){
			addCriterion("ACTIVITY_UID <=", value, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidLike(String value){
			addCriterion("ACTIVITY_UID like", value, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidNotLike(String value){
			addCriterion("ACTIVITY_UID not like", value, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidIn(List<String> values){
			addCriterion("ACTIVITY_UID in", values, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidNotIn(List<String> values){
			addCriterion("ACTIVITY_UID not in", values, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidBetween(String value1, String value2){
			addCriterion("ACTIVITY_UID between", value1, value2, "activityUid");
			return (Criteria) this;
		}

		public Criteria andActivityUidNotBetween(String value1, String value2){
			addCriterion("ACTIVITY_UID not between", value1, value2, "activityUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidIsNull(){
			addCriterion("END_DAY_UID is null");
			return (Criteria) this;
		}

		public Criteria andEndDayUidIsNotNull(){
			addCriterion("END_DAY_UID is not null");
			return (Criteria) this;
		}

		public Criteria andEndDayUidEqualTo(String value){
			addCriterion("END_DAY_UID =", value, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidNotEqualTo(String value){
			addCriterion("END_DAY_UID <>", value, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidGreaterThan(String value){
			addCriterion("END_DAY_UID >", value, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidGreaterThanOrEqualTo(String value){
			addCriterion("END_DAY_UID >=", value, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidLessThan(String value){
			addCriterion("END_DAY_UID <", value, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidLessThanOrEqualTo(String value){
			addCriterion("END_DAY_UID <=", value, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidLike(String value){
			addCriterion("END_DAY_UID like", value, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidNotLike(String value){
			addCriterion("END_DAY_UID not like", value, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidIn(List<String> values){
			addCriterion("END_DAY_UID in", values, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidNotIn(List<String> values){
			addCriterion("END_DAY_UID not in", values, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidBetween(String value1, String value2){
			addCriterion("END_DAY_UID between", value1, value2, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidNotBetween(String value1, String value2){
			addCriterion("END_DAY_UID not between", value1, value2, "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeIsNull(){
			addCriterion("END_DAY_WH_CODE is null");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeIsNotNull(){
			addCriterion("END_DAY_WH_CODE is not null");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeEqualTo(String value){
			addCriterion("END_DAY_WH_CODE =", value, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeNotEqualTo(String value){
			addCriterion("END_DAY_WH_CODE <>", value, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeGreaterThan(String value){
			addCriterion("END_DAY_WH_CODE >", value, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeGreaterThanOrEqualTo(String value){
			addCriterion("END_DAY_WH_CODE >=", value, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeLessThan(String value){
			addCriterion("END_DAY_WH_CODE <", value, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeLessThanOrEqualTo(String value){
			addCriterion("END_DAY_WH_CODE <=", value, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeLike(String value){
			addCriterion("END_DAY_WH_CODE like", value, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeNotLike(String value){
			addCriterion("END_DAY_WH_CODE not like", value, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeIn(List<String> values){
			addCriterion("END_DAY_WH_CODE in", values, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeNotIn(List<String> values){
			addCriterion("END_DAY_WH_CODE not in", values, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeBetween(String value1, String value2){
			addCriterion("END_DAY_WH_CODE between", value1, value2, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeNotBetween(String value1, String value2){
			addCriterion("END_DAY_WH_CODE not between", value1, value2, "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andEndDayDateIsNull(){
			addCriterion("END_DAY_DATE is null");
			return (Criteria) this;
		}

		public Criteria andEndDayDateIsNotNull(){
			addCriterion("END_DAY_DATE is not null");
			return (Criteria) this;
		}

		public Criteria andEndDayDateEqualTo(Date value){
			addCriterion("END_DAY_DATE =", value, "endDayDate");
			return (Criteria) this;
		}

		public Criteria andEndDayDateNotEqualTo(Date value){
			addCriterion("END_DAY_DATE <>", value, "endDayDate");
			return (Criteria) this;
		}

		public Criteria andEndDayDateGreaterThan(Date value){
			addCriterion("END_DAY_DATE >", value, "endDayDate");
			return (Criteria) this;
		}

		public Criteria andEndDayDateGreaterThanOrEqualTo(Date value){
			addCriterion("END_DAY_DATE >=", value, "endDayDate");
			return (Criteria) this;
		}

		public Criteria andEndDayDateLessThan(Date value){
			addCriterion("END_DAY_DATE <", value, "endDayDate");
			return (Criteria) this;
		}

		public Criteria andEndDayDateLessThanOrEqualTo(Date value){
			addCriterion("END_DAY_DATE <=", value, "endDayDate");
			return (Criteria) this;
		}

		public Criteria andEndDayDateIn(List<Date> values){
			addCriterion("END_DAY_DATE in", values, "endDayDate");
			return (Criteria) this;
		}

		public Criteria andEndDayDateNotIn(List<Date> values){
			addCriterion("END_DAY_DATE not in", values, "endDayDate");
			return (Criteria) this;
		}

		public Criteria andEndDayDateBetween(Date value1, Date value2){
			addCriterion("END_DAY_DATE between", value1, value2, "endDayDate");
			return (Criteria) this;
		}

		public Criteria andEndDayDateNotBetween(Date value1, Date value2){
			addCriterion("END_DAY_DATE not between", value1, value2, "endDayDate");
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

		public Criteria andCreationUserIsNull(){
			addCriterion("CREATION_USER is null");
			return (Criteria) this;
		}

		public Criteria andCreationUserIsNotNull(){
			addCriterion("CREATION_USER is not null");
			return (Criteria) this;
		}

		public Criteria andCreationUserEqualTo(String value){
			addCriterion("CREATION_USER =", value, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserNotEqualTo(String value){
			addCriterion("CREATION_USER <>", value, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserGreaterThan(String value){
			addCriterion("CREATION_USER >", value, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserGreaterThanOrEqualTo(String value){
			addCriterion("CREATION_USER >=", value, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserLessThan(String value){
			addCriterion("CREATION_USER <", value, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserLessThanOrEqualTo(String value){
			addCriterion("CREATION_USER <=", value, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserLike(String value){
			addCriterion("CREATION_USER like", value, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserNotLike(String value){
			addCriterion("CREATION_USER not like", value, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserIn(List<String> values){
			addCriterion("CREATION_USER in", values, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserNotIn(List<String> values){
			addCriterion("CREATION_USER not in", values, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserBetween(String value1, String value2){
			addCriterion("CREATION_USER between", value1, value2, "creationUser");
			return (Criteria) this;
		}

		public Criteria andCreationUserNotBetween(String value1, String value2){
			addCriterion("CREATION_USER not between", value1, value2, "creationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateIsNull(){
			addCriterion("CONFIRMATION_DATE is null");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateIsNotNull(){
			addCriterion("CONFIRMATION_DATE is not null");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateEqualTo(Date value){
			addCriterion("CONFIRMATION_DATE =", value, "confirmationDate");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateNotEqualTo(Date value){
			addCriterion("CONFIRMATION_DATE <>", value, "confirmationDate");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateGreaterThan(Date value){
			addCriterion("CONFIRMATION_DATE >", value, "confirmationDate");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateGreaterThanOrEqualTo(Date value){
			addCriterion("CONFIRMATION_DATE >=", value, "confirmationDate");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateLessThan(Date value){
			addCriterion("CONFIRMATION_DATE <", value, "confirmationDate");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateLessThanOrEqualTo(Date value){
			addCriterion("CONFIRMATION_DATE <=", value, "confirmationDate");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateIn(List<Date> values){
			addCriterion("CONFIRMATION_DATE in", values, "confirmationDate");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateNotIn(List<Date> values){
			addCriterion("CONFIRMATION_DATE not in", values, "confirmationDate");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateBetween(Date value1, Date value2){
			addCriterion("CONFIRMATION_DATE between", value1, value2, "confirmationDate");
			return (Criteria) this;
		}

		public Criteria andConfirmationDateNotBetween(Date value1, Date value2){
			addCriterion("CONFIRMATION_DATE not between", value1, value2, "confirmationDate");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserIsNull(){
			addCriterion("CONFIRMATION_USER is null");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserIsNotNull(){
			addCriterion("CONFIRMATION_USER is not null");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserEqualTo(String value){
			addCriterion("CONFIRMATION_USER =", value, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserNotEqualTo(String value){
			addCriterion("CONFIRMATION_USER <>", value, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserGreaterThan(String value){
			addCriterion("CONFIRMATION_USER >", value, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserGreaterThanOrEqualTo(String value){
			addCriterion("CONFIRMATION_USER >=", value, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserLessThan(String value){
			addCriterion("CONFIRMATION_USER <", value, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserLessThanOrEqualTo(String value){
			addCriterion("CONFIRMATION_USER <=", value, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserLike(String value){
			addCriterion("CONFIRMATION_USER like", value, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserNotLike(String value){
			addCriterion("CONFIRMATION_USER not like", value, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserIn(List<String> values){
			addCriterion("CONFIRMATION_USER in", values, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserNotIn(List<String> values){
			addCriterion("CONFIRMATION_USER not in", values, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserBetween(String value1, String value2){
			addCriterion("CONFIRMATION_USER between", value1, value2, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserNotBetween(String value1, String value2){
			addCriterion("CONFIRMATION_USER not between", value1, value2, "confirmationUser");
			return (Criteria) this;
		}

		public Criteria andStoreCountIsNull(){
			addCriterion("STORE_COUNT is null");
			return (Criteria) this;
		}

		public Criteria andStoreCountIsNotNull(){
			addCriterion("STORE_COUNT is not null");
			return (Criteria) this;
		}

		public Criteria andStoreCountEqualTo(BigDecimal value){
			addCriterion("STORE_COUNT =", value, "storeCount");
			return (Criteria) this;
		}

		public Criteria andStoreCountNotEqualTo(BigDecimal value){
			addCriterion("STORE_COUNT <>", value, "storeCount");
			return (Criteria) this;
		}

		public Criteria andStoreCountGreaterThan(BigDecimal value){
			addCriterion("STORE_COUNT >", value, "storeCount");
			return (Criteria) this;
		}

		public Criteria andStoreCountGreaterThanOrEqualTo(BigDecimal value){
			addCriterion("STORE_COUNT >=", value, "storeCount");
			return (Criteria) this;
		}

		public Criteria andStoreCountLessThan(BigDecimal value){
			addCriterion("STORE_COUNT <", value, "storeCount");
			return (Criteria) this;
		}

		public Criteria andStoreCountLessThanOrEqualTo(BigDecimal value){
			addCriterion("STORE_COUNT <=", value, "storeCount");
			return (Criteria) this;
		}

		public Criteria andStoreCountIn(List<BigDecimal> values){
			addCriterion("STORE_COUNT in", values, "storeCount");
			return (Criteria) this;
		}

		public Criteria andStoreCountNotIn(List<BigDecimal> values){
			addCriterion("STORE_COUNT not in", values, "storeCount");
			return (Criteria) this;
		}

		public Criteria andStoreCountBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("STORE_COUNT between", value1, value2, "storeCount");
			return (Criteria) this;
		}

		public Criteria andStoreCountNotBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("STORE_COUNT not between", value1, value2, "storeCount");
			return (Criteria) this;
		}

		public Criteria andManagerCountIsNull(){
			addCriterion("MANAGER_COUNT is null");
			return (Criteria) this;
		}

		public Criteria andManagerCountIsNotNull(){
			addCriterion("MANAGER_COUNT is not null");
			return (Criteria) this;
		}

		public Criteria andManagerCountEqualTo(BigDecimal value){
			addCriterion("MANAGER_COUNT =", value, "managerCount");
			return (Criteria) this;
		}

		public Criteria andManagerCountNotEqualTo(BigDecimal value){
			addCriterion("MANAGER_COUNT <>", value, "managerCount");
			return (Criteria) this;
		}

		public Criteria andManagerCountGreaterThan(BigDecimal value){
			addCriterion("MANAGER_COUNT >", value, "managerCount");
			return (Criteria) this;
		}

		public Criteria andManagerCountGreaterThanOrEqualTo(BigDecimal value){
			addCriterion("MANAGER_COUNT >=", value, "managerCount");
			return (Criteria) this;
		}

		public Criteria andManagerCountLessThan(BigDecimal value){
			addCriterion("MANAGER_COUNT <", value, "managerCount");
			return (Criteria) this;
		}

		public Criteria andManagerCountLessThanOrEqualTo(BigDecimal value){
			addCriterion("MANAGER_COUNT <=", value, "managerCount");
			return (Criteria) this;
		}

		public Criteria andManagerCountIn(List<BigDecimal> values){
			addCriterion("MANAGER_COUNT in", values, "managerCount");
			return (Criteria) this;
		}

		public Criteria andManagerCountNotIn(List<BigDecimal> values){
			addCriterion("MANAGER_COUNT not in", values, "managerCount");
			return (Criteria) this;
		}

		public Criteria andManagerCountBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("MANAGER_COUNT between", value1, value2, "managerCount");
			return (Criteria) this;
		}

		public Criteria andManagerCountNotBetween(BigDecimal value1, BigDecimal value2){
			addCriterion("MANAGER_COUNT not between", value1, value2, "managerCount");
			return (Criteria) this;
		}

		public Criteria andStatusIdIsNull(){
			addCriterion("STATUS_ID is null");
			return (Criteria) this;
		}

		public Criteria andStatusIdIsNotNull(){
			addCriterion("STATUS_ID is not null");
			return (Criteria) this;
		}

		public Criteria andStatusIdEqualTo(Integer value){
			addCriterion("STATUS_ID =", value, "statusId");
			return (Criteria) this;
		}

		public Criteria andStatusIdNotEqualTo(Integer value){
			addCriterion("STATUS_ID <>", value, "statusId");
			return (Criteria) this;
		}

		public Criteria andStatusIdGreaterThan(Integer value){
			addCriterion("STATUS_ID >", value, "statusId");
			return (Criteria) this;
		}

		public Criteria andStatusIdGreaterThanOrEqualTo(Integer value){
			addCriterion("STATUS_ID >=", value, "statusId");
			return (Criteria) this;
		}

		public Criteria andStatusIdLessThan(Integer value){
			addCriterion("STATUS_ID <", value, "statusId");
			return (Criteria) this;
		}

		public Criteria andStatusIdLessThanOrEqualTo(Integer value){
			addCriterion("STATUS_ID <=", value, "statusId");
			return (Criteria) this;
		}

		public Criteria andStatusIdIn(List<Integer> values){
			addCriterion("STATUS_ID in", values, "statusId");
			return (Criteria) this;
		}

		public Criteria andStatusIdNotIn(List<Integer> values){
			addCriterion("STATUS_ID not in", values, "statusId");
			return (Criteria) this;
		}

		public Criteria andStatusIdBetween(Integer value1, Integer value2){
			addCriterion("STATUS_ID between", value1, value2, "statusId");
			return (Criteria) this;
		}

		public Criteria andStatusIdNotBetween(Integer value1, Integer value2){
			addCriterion("STATUS_ID not between", value1, value2, "statusId");
			return (Criteria) this;
		}

		public Criteria andActivityUidLikeInsensitive(String value){
			addCriterion("upper(ACTIVITY_UID) like", value.toUpperCase(), "activityUid");
			return (Criteria) this;
		}

		public Criteria andEndDayUidLikeInsensitive(String value){
			addCriterion("upper(END_DAY_UID) like", value.toUpperCase(), "endDayUid");
			return (Criteria) this;
		}

		public Criteria andEndDayWhCodeLikeInsensitive(String value){
			addCriterion("upper(END_DAY_WH_CODE) like", value.toUpperCase(), "endDayWhCode");
			return (Criteria) this;
		}

		public Criteria andCreationUserLikeInsensitive(String value){
			addCriterion("upper(CREATION_USER) like", value.toUpperCase(), "creationUser");
			return (Criteria) this;
		}

		public Criteria andConfirmationUserLikeInsensitive(String value){
			addCriterion("upper(CONFIRMATION_USER) like", value.toUpperCase(), "confirmationUser");
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