package com.comerzzia.api.loyalty.persistence.coupons;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiActivity;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@SuppressWarnings("serial")
public class CouponExample extends MultiActivity {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CouponExample() {
        oredCriteria = new ArrayList<>();
    }
    
    public CouponExample(IDatosSesion datosSesion) {
       super(datosSesion);
         oredCriteria = new ArrayList<Criteria>();
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
        protected List<Criterion> manualSelectionCriteria;

        protected List<Criterion> activeCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
            manualSelectionCriteria = new ArrayList<>();
            activeCriteria = new ArrayList<>();
        }

        public List<Criterion> getManualSelectionCriteria() {
            return manualSelectionCriteria;
        }

        protected void addManualSelectionCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            manualSelectionCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanNumberTypeHandler"));
            allCriteria = null;
        }

        protected void addManualSelectionCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            manualSelectionCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanNumberTypeHandler"));
            allCriteria = null;
        }

        public List<Criterion> getActiveCriteria() {
            return activeCriteria;
        }

        protected void addActiveCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            activeCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanNumberTypeHandler"));
            allCriteria = null;
        }

        protected void addActiveCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            activeCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanNumberTypeHandler"));
            allCriteria = null;
        }

        public boolean isValid() {
            return criteria.size() > 0
                || manualSelectionCriteria.size() > 0
                || activeCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(manualSelectionCriteria);
                allCriteria.addAll(activeCriteria);
            }
            return allCriteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
            allCriteria = null;
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
            allCriteria = null;
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
            allCriteria = null;
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

        public Criteria andCouponIdIsNull() {
            addCriterion("COUPON_ID is null");
            return (Criteria) this;
        }

        public Criteria andCouponIdIsNotNull() {
            addCriterion("COUPON_ID is not null");
            return (Criteria) this;
        }

        public Criteria andCouponIdEqualTo(Long value) {
            addCriterion("COUPON_ID =", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdNotEqualTo(Long value) {
            addCriterion("COUPON_ID <>", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdGreaterThan(Long value) {
            addCriterion("COUPON_ID >", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdGreaterThanOrEqualTo(Long value) {
            addCriterion("COUPON_ID >=", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdLessThan(Long value) {
            addCriterion("COUPON_ID <", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdLessThanOrEqualTo(Long value) {
            addCriterion("COUPON_ID <=", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdIn(List<Long> values) {
            addCriterion("COUPON_ID in", values, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdNotIn(List<Long> values) {
            addCriterion("COUPON_ID not in", values, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdBetween(Long value1, Long value2) {
            addCriterion("COUPON_ID between", value1, value2, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdNotBetween(Long value1, Long value2) {
            addCriterion("COUPON_ID not between", value1, value2, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponCodeIsNull() {
            addCriterion("COUPON_CODE is null");
            return (Criteria) this;
        }

        public Criteria andCouponCodeIsNotNull() {
            addCriterion("COUPON_CODE is not null");
            return (Criteria) this;
        }

        public Criteria andCouponCodeEqualTo(String value) {
            addCriterion("COUPON_CODE =", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeNotEqualTo(String value) {
            addCriterion("COUPON_CODE <>", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeGreaterThan(String value) {
            addCriterion("COUPON_CODE >", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeGreaterThanOrEqualTo(String value) {
            addCriterion("COUPON_CODE >=", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeLessThan(String value) {
            addCriterion("COUPON_CODE <", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeLessThanOrEqualTo(String value) {
            addCriterion("COUPON_CODE <=", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeLike(String value) {
            addCriterion("COUPON_CODE like", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeNotLike(String value) {
            addCriterion("COUPON_CODE not like", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeIn(List<String> values) {
            addCriterion("COUPON_CODE in", values, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeNotIn(List<String> values) {
            addCriterion("COUPON_CODE not in", values, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeBetween(String value1, String value2) {
            addCriterion("COUPON_CODE between", value1, value2, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeNotBetween(String value1, String value2) {
            addCriterion("COUPON_CODE not between", value1, value2, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponNameIsNull() {
            addCriterion("COUPON_NAME is null");
            return (Criteria) this;
        }

        public Criteria andCouponNameIsNotNull() {
            addCriterion("COUPON_NAME is not null");
            return (Criteria) this;
        }

        public Criteria andCouponNameEqualTo(String value) {
            addCriterion("COUPON_NAME =", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameNotEqualTo(String value) {
            addCriterion("COUPON_NAME <>", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameGreaterThan(String value) {
            addCriterion("COUPON_NAME >", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameGreaterThanOrEqualTo(String value) {
            addCriterion("COUPON_NAME >=", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameLessThan(String value) {
            addCriterion("COUPON_NAME <", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameLessThanOrEqualTo(String value) {
            addCriterion("COUPON_NAME <=", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameLike(String value) {
            addCriterion("COUPON_NAME like", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameNotLike(String value) {
            addCriterion("COUPON_NAME not like", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameIn(List<String> values) {
            addCriterion("COUPON_NAME in", values, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameNotIn(List<String> values) {
            addCriterion("COUPON_NAME not in", values, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameBetween(String value1, String value2) {
            addCriterion("COUPON_NAME between", value1, value2, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameNotBetween(String value1, String value2) {
            addCriterion("COUPON_NAME not between", value1, value2, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionIsNull() {
            addCriterion("COUPON_DESCRIPTION is null");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionIsNotNull() {
            addCriterion("COUPON_DESCRIPTION is not null");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionEqualTo(String value) {
            addCriterion("COUPON_DESCRIPTION =", value, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionNotEqualTo(String value) {
            addCriterion("COUPON_DESCRIPTION <>", value, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionGreaterThan(String value) {
            addCriterion("COUPON_DESCRIPTION >", value, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("COUPON_DESCRIPTION >=", value, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionLessThan(String value) {
            addCriterion("COUPON_DESCRIPTION <", value, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionLessThanOrEqualTo(String value) {
            addCriterion("COUPON_DESCRIPTION <=", value, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionLike(String value) {
            addCriterion("COUPON_DESCRIPTION like", value, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionNotLike(String value) {
            addCriterion("COUPON_DESCRIPTION not like", value, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionIn(List<String> values) {
            addCriterion("COUPON_DESCRIPTION in", values, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionNotIn(List<String> values) {
            addCriterion("COUPON_DESCRIPTION not in", values, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionBetween(String value1, String value2) {
            addCriterion("COUPON_DESCRIPTION between", value1, value2, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionNotBetween(String value1, String value2) {
            addCriterion("COUPON_DESCRIPTION not between", value1, value2, "couponDescription");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNull() {
            addCriterion("START_DATE is null");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNotNull() {
            addCriterion("START_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andStartDateEqualTo(Date value) {
            addCriterion("START_DATE =", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotEqualTo(Date value) {
            addCriterion("START_DATE <>", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThan(Date value) {
            addCriterion("START_DATE >", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThanOrEqualTo(Date value) {
            addCriterion("START_DATE >=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThan(Date value) {
            addCriterion("START_DATE <", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThanOrEqualTo(Date value) {
            addCriterion("START_DATE <=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateIn(List<Date> values) {
            addCriterion("START_DATE in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotIn(List<Date> values) {
            addCriterion("START_DATE not in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateBetween(Date value1, Date value2) {
            addCriterion("START_DATE between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotBetween(Date value1, Date value2) {
            addCriterion("START_DATE not between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andEndDateIsNull() {
            addCriterion("END_DATE is null");
            return (Criteria) this;
        }

        public Criteria andEndDateIsNotNull() {
            addCriterion("END_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andEndDateEqualTo(Date value) {
            addCriterion("END_DATE =", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotEqualTo(Date value) {
            addCriterion("END_DATE <>", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateGreaterThan(Date value) {
            addCriterion("END_DATE >", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateGreaterThanOrEqualTo(Date value) {
            addCriterion("END_DATE >=", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateLessThan(Date value) {
            addCriterion("END_DATE <", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateLessThanOrEqualTo(Date value) {
            addCriterion("END_DATE <=", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateIn(List<Date> values) {
            addCriterion("END_DATE in", values, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotIn(List<Date> values) {
            addCriterion("END_DATE not in", values, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateBetween(Date value1, Date value2) {
            addCriterion("END_DATE between", value1, value2, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotBetween(Date value1, Date value2) {
            addCriterion("END_DATE not between", value1, value2, "endDate");
            return (Criteria) this;
        }

        public Criteria andManualSelectionIsNull() {
            addCriterion("MANUAL_SELECTION is null");
            return (Criteria) this;
        }

        public Criteria andManualSelectionIsNotNull() {
            addCriterion("MANUAL_SELECTION is not null");
            return (Criteria) this;
        }

        public Criteria andManualSelectionEqualTo(Boolean value) {
            addManualSelectionCriterion("MANUAL_SELECTION =", value, "manualSelection");
            return (Criteria) this;
        }

        public Criteria andManualSelectionNotEqualTo(Boolean value) {
            addManualSelectionCriterion("MANUAL_SELECTION <>", value, "manualSelection");
            return (Criteria) this;
        }

        public Criteria andManualSelectionGreaterThan(Boolean value) {
            addManualSelectionCriterion("MANUAL_SELECTION >", value, "manualSelection");
            return (Criteria) this;
        }

        public Criteria andManualSelectionGreaterThanOrEqualTo(Boolean value) {
            addManualSelectionCriterion("MANUAL_SELECTION >=", value, "manualSelection");
            return (Criteria) this;
        }

        public Criteria andManualSelectionLessThan(Boolean value) {
            addManualSelectionCriterion("MANUAL_SELECTION <", value, "manualSelection");
            return (Criteria) this;
        }

        public Criteria andManualSelectionLessThanOrEqualTo(Boolean value) {
            addManualSelectionCriterion("MANUAL_SELECTION <=", value, "manualSelection");
            return (Criteria) this;
        }

        public Criteria andManualSelectionIn(List<Boolean> values) {
            addManualSelectionCriterion("MANUAL_SELECTION in", values, "manualSelection");
            return (Criteria) this;
        }

        public Criteria andManualSelectionNotIn(List<Boolean> values) {
            addManualSelectionCriterion("MANUAL_SELECTION not in", values, "manualSelection");
            return (Criteria) this;
        }

        public Criteria andManualSelectionBetween(Boolean value1, Boolean value2) {
            addManualSelectionCriterion("MANUAL_SELECTION between", value1, value2, "manualSelection");
            return (Criteria) this;
        }

        public Criteria andManualSelectionNotBetween(Boolean value1, Boolean value2) {
            addManualSelectionCriterion("MANUAL_SELECTION not between", value1, value2, "manualSelection");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeIsNull() {
            addCriterion("COUPON_TYPE_CODE is null");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeIsNotNull() {
            addCriterion("COUPON_TYPE_CODE is not null");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeEqualTo(String value) {
            addCriterion("COUPON_TYPE_CODE =", value, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeNotEqualTo(String value) {
            addCriterion("COUPON_TYPE_CODE <>", value, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeGreaterThan(String value) {
            addCriterion("COUPON_TYPE_CODE >", value, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeGreaterThanOrEqualTo(String value) {
            addCriterion("COUPON_TYPE_CODE >=", value, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeLessThan(String value) {
            addCriterion("COUPON_TYPE_CODE <", value, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeLessThanOrEqualTo(String value) {
            addCriterion("COUPON_TYPE_CODE <=", value, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeLike(String value) {
            addCriterion("COUPON_TYPE_CODE like", value, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeNotLike(String value) {
            addCriterion("COUPON_TYPE_CODE not like", value, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeIn(List<String> values) {
            addCriterion("COUPON_TYPE_CODE in", values, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeNotIn(List<String> values) {
            addCriterion("COUPON_TYPE_CODE not in", values, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeBetween(String value1, String value2) {
            addCriterion("COUPON_TYPE_CODE between", value1, value2, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeNotBetween(String value1, String value2) {
            addCriterion("COUPON_TYPE_CODE not between", value1, value2, "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andPromotionIdIsNull() {
            addCriterion("PROMOTION_ID is null");
            return (Criteria) this;
        }

        public Criteria andPromotionIdIsNotNull() {
            addCriterion("PROMOTION_ID is not null");
            return (Criteria) this;
        }

        public Criteria andPromotionIdEqualTo(Long value) {
            addCriterion("PROMOTION_ID =", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdNotEqualTo(Long value) {
            addCriterion("PROMOTION_ID <>", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdGreaterThan(Long value) {
            addCriterion("PROMOTION_ID >", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdGreaterThanOrEqualTo(Long value) {
            addCriterion("PROMOTION_ID >=", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdLessThan(Long value) {
            addCriterion("PROMOTION_ID <", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdLessThanOrEqualTo(Long value) {
            addCriterion("PROMOTION_ID <=", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdIn(List<Long> values) {
            addCriterion("PROMOTION_ID in", values, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdNotIn(List<Long> values) {
            addCriterion("PROMOTION_ID not in", values, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdBetween(Long value1, Long value2) {
            addCriterion("PROMOTION_ID between", value1, value2, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdNotBetween(Long value1, Long value2) {
            addCriterion("PROMOTION_ID not between", value1, value2, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPriorityIsNull() {
            addCriterion("PRIORITY is null");
            return (Criteria) this;
        }

        public Criteria andPriorityIsNotNull() {
            addCriterion("PRIORITY is not null");
            return (Criteria) this;
        }

        public Criteria andPriorityEqualTo(Long value) {
            addCriterion("PRIORITY =", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotEqualTo(Long value) {
            addCriterion("PRIORITY <>", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityGreaterThan(Long value) {
            addCriterion("PRIORITY >", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityGreaterThanOrEqualTo(Long value) {
            addCriterion("PRIORITY >=", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLessThan(Long value) {
            addCriterion("PRIORITY <", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLessThanOrEqualTo(Long value) {
            addCriterion("PRIORITY <=", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityIn(List<Long> values) {
            addCriterion("PRIORITY in", values, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotIn(List<Long> values) {
            addCriterion("PRIORITY not in", values, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityBetween(Long value1, Long value2) {
            addCriterion("PRIORITY between", value1, value2, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotBetween(Long value1, Long value2) {
            addCriterion("PRIORITY not between", value1, value2, "priority");
            return (Criteria) this;
        }

        public Criteria andBalanceIsNull() {
            addCriterion("BALANCE is null");
            return (Criteria) this;
        }

        public Criteria andBalanceIsNotNull() {
            addCriterion("BALANCE is not null");
            return (Criteria) this;
        }

        public Criteria andBalanceEqualTo(BigDecimal value) {
            addCriterion("BALANCE =", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceNotEqualTo(BigDecimal value) {
            addCriterion("BALANCE <>", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceGreaterThan(BigDecimal value) {
            addCriterion("BALANCE >", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("BALANCE >=", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceLessThan(BigDecimal value) {
            addCriterion("BALANCE <", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("BALANCE <=", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceIn(List<BigDecimal> values) {
            addCriterion("BALANCE in", values, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceNotIn(List<BigDecimal> values) {
            addCriterion("BALANCE not in", values, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("BALANCE between", value1, value2, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("BALANCE not between", value1, value2, "balance");
            return (Criteria) this;
        }

        public Criteria andImageUrlIsNull() {
            addCriterion("IMAGE_URL is null");
            return (Criteria) this;
        }

        public Criteria andImageUrlIsNotNull() {
            addCriterion("IMAGE_URL is not null");
            return (Criteria) this;
        }

        public Criteria andImageUrlEqualTo(String value) {
            addCriterion("IMAGE_URL =", value, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlNotEqualTo(String value) {
            addCriterion("IMAGE_URL <>", value, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlGreaterThan(String value) {
            addCriterion("IMAGE_URL >", value, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlGreaterThanOrEqualTo(String value) {
            addCriterion("IMAGE_URL >=", value, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlLessThan(String value) {
            addCriterion("IMAGE_URL <", value, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlLessThanOrEqualTo(String value) {
            addCriterion("IMAGE_URL <=", value, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlLike(String value) {
            addCriterion("IMAGE_URL like", value, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlNotLike(String value) {
            addCriterion("IMAGE_URL not like", value, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlIn(List<String> values) {
            addCriterion("IMAGE_URL in", values, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlNotIn(List<String> values) {
            addCriterion("IMAGE_URL not in", values, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlBetween(String value1, String value2) {
            addCriterion("IMAGE_URL between", value1, value2, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andImageUrlNotBetween(String value1, String value2) {
            addCriterion("IMAGE_URL not between", value1, value2, "imageUrl");
            return (Criteria) this;
        }

        public Criteria andActiveIsNull() {
            addCriterion("ACTIVE is null");
            return (Criteria) this;
        }

        public Criteria andActiveIsNotNull() {
            addCriterion("ACTIVE is not null");
            return (Criteria) this;
        }

        public Criteria andActiveEqualTo(Boolean value) {
            addActiveCriterion("ACTIVE =", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveNotEqualTo(Boolean value) {
            addActiveCriterion("ACTIVE <>", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveGreaterThan(Boolean value) {
            addActiveCriterion("ACTIVE >", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveGreaterThanOrEqualTo(Boolean value) {
            addActiveCriterion("ACTIVE >=", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveLessThan(Boolean value) {
            addActiveCriterion("ACTIVE <", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveLessThanOrEqualTo(Boolean value) {
            addActiveCriterion("ACTIVE <=", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveIn(List<Boolean> values) {
            addActiveCriterion("ACTIVE in", values, "active");
            return (Criteria) this;
        }

        public Criteria andActiveNotIn(List<Boolean> values) {
            addActiveCriterion("ACTIVE not in", values, "active");
            return (Criteria) this;
        }

        public Criteria andActiveBetween(Boolean value1, Boolean value2) {
            addActiveCriterion("ACTIVE between", value1, value2, "active");
            return (Criteria) this;
        }

        public Criteria andActiveNotBetween(Boolean value1, Boolean value2) {
            addActiveCriterion("ACTIVE not between", value1, value2, "active");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andCouponCodeLikeInsensitive(String value) {
            addCriterion("upper(COUPON_CODE) like", value.toUpperCase(), "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponNameLikeInsensitive(String value) {
            addCriterion("upper(COUPON_NAME) like", value.toUpperCase(), "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponDescriptionLikeInsensitive(String value) {
            addCriterion("upper(COUPON_DESCRIPTION) like", value.toUpperCase(), "couponDescription");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeLikeInsensitive(String value) {
            addCriterion("upper(COUPON_TYPE_CODE) like", value.toUpperCase(), "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andImageUrlLikeInsensitive(String value) {
            addCriterion("upper(IMAGE_URL) like", value.toUpperCase(), "imageUrl");
            return (Criteria) this;
        }
        
        public Criteria andCreationDateIsNull() {
           addCriterion("CREATION_DATE is null");
           return (Criteria) this;
       }

       public Criteria andCreationDateIsNotNull() {
           addCriterion("CREATION_DATE is not null");
           return (Criteria) this;
       }

       public Criteria andCreationDateEqualTo(Date value) {
           addCriterion("CREATION_DATE =", value, "creationDate");
           return (Criteria) this;
       }

       public Criteria andCreationDateNotEqualTo(Date value) {
           addCriterion("CREATION_DATE <>", value, "creationDate");
           return (Criteria) this;
       }

       public Criteria andCreationDateGreaterThan(Date value) {
           addCriterion("CREATION_DATE >", value, "creationDate");
           return (Criteria) this;
       }

       public Criteria andCreationDateGreaterThanOrEqualTo(Date value) {
           addCriterion("CREATION_DATE >=", value, "creationDate");
           return (Criteria) this;
       }

       public Criteria andCreationDateLessThan(Date value) {
           addCriterion("CREATION_DATE <", value, "creationDate");
           return (Criteria) this;
       }

       public Criteria andCreationDateLessThanOrEqualTo(Date value) {
           addCriterion("CREATION_DATE <=", value, "creationDate");
           return (Criteria) this;
       }

       public Criteria andCreationDateIn(List<Date> values) {
           addCriterion("CREATION_DATE in", values, "creationDate");
           return (Criteria) this;
       }

       public Criteria andCreationDateNotIn(List<Date> values) {
           addCriterion("CREATION_DATE not in", values, "creationDate");
           return (Criteria) this;
       }

       public Criteria andCreationDateBetween(Date value1, Date value2) {
           addCriterion("CREATION_DATE between", value1, value2, "creationDate");
           return (Criteria) this;
       }

       public Criteria andCreationDateNotBetween(Date value1, Date value2) {
           addCriterion("CREATION_DATE not between", value1, value2, "creationDate");
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
            } else {
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