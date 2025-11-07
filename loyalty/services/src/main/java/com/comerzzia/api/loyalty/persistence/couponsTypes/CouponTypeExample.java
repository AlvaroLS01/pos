package com.comerzzia.api.loyalty.persistence.couponsTypes;

import java.util.ArrayList;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiActivity;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@SuppressWarnings("serial")
public class CouponTypeExample extends MultiActivity {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CouponTypeExample(IDatosSesion datosSesion) {
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
        protected List<Criterion> defManualSelectCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
            defManualSelectCriteria = new ArrayList<>();
        }

        public List<Criterion> getDefManualSelectCriteria() {
            return defManualSelectCriteria;
        }

        protected void addDefManualSelectCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            defManualSelectCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanNumberTypeHandler"));
            allCriteria = null;
        }

        protected void addDefManualSelectCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            defManualSelectCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanNumberTypeHandler"));
            allCriteria = null;
        }

        public boolean isValid() {
            return criteria.size() > 0
                || defManualSelectCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(defManualSelectCriteria);
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

        public Criteria andCouponTypeNameIsNull() {
            addCriterion("COUPON_TYPE_NAME is null");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameIsNotNull() {
            addCriterion("COUPON_TYPE_NAME is not null");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameEqualTo(String value) {
            addCriterion("COUPON_TYPE_NAME =", value, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameNotEqualTo(String value) {
            addCriterion("COUPON_TYPE_NAME <>", value, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameGreaterThan(String value) {
            addCriterion("COUPON_TYPE_NAME >", value, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameGreaterThanOrEqualTo(String value) {
            addCriterion("COUPON_TYPE_NAME >=", value, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameLessThan(String value) {
            addCriterion("COUPON_TYPE_NAME <", value, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameLessThanOrEqualTo(String value) {
            addCriterion("COUPON_TYPE_NAME <=", value, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameLike(String value) {
            addCriterion("COUPON_TYPE_NAME like", value, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameNotLike(String value) {
            addCriterion("COUPON_TYPE_NAME not like", value, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameIn(List<String> values) {
            addCriterion("COUPON_TYPE_NAME in", values, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameNotIn(List<String> values) {
            addCriterion("COUPON_TYPE_NAME not in", values, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameBetween(String value1, String value2) {
            addCriterion("COUPON_TYPE_NAME between", value1, value2, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameNotBetween(String value1, String value2) {
            addCriterion("COUPON_TYPE_NAME not between", value1, value2, "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectIsNull() {
            addCriterion("DEF_MANUAL_SELECT is null");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectIsNotNull() {
            addCriterion("DEF_MANUAL_SELECT is not null");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectEqualTo(Boolean value) {
            addDefManualSelectCriterion("DEF_MANUAL_SELECT =", value, "defManualSelect");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectNotEqualTo(Boolean value) {
            addDefManualSelectCriterion("DEF_MANUAL_SELECT <>", value, "defManualSelect");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectGreaterThan(Boolean value) {
            addDefManualSelectCriterion("DEF_MANUAL_SELECT >", value, "defManualSelect");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectGreaterThanOrEqualTo(Boolean value) {
            addDefManualSelectCriterion("DEF_MANUAL_SELECT >=", value, "defManualSelect");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectLessThan(Boolean value) {
            addDefManualSelectCriterion("DEF_MANUAL_SELECT <", value, "defManualSelect");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectLessThanOrEqualTo(Boolean value) {
            addDefManualSelectCriterion("DEF_MANUAL_SELECT <=", value, "defManualSelect");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectIn(List<Boolean> values) {
            addDefManualSelectCriterion("DEF_MANUAL_SELECT in", values, "defManualSelect");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectNotIn(List<Boolean> values) {
            addDefManualSelectCriterion("DEF_MANUAL_SELECT not in", values, "defManualSelect");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectBetween(Boolean value1, Boolean value2) {
            addDefManualSelectCriterion("DEF_MANUAL_SELECT between", value1, value2, "defManualSelect");
            return (Criteria) this;
        }

        public Criteria andDefManualSelectNotBetween(Boolean value1, Boolean value2) {
            addDefManualSelectCriterion("DEF_MANUAL_SELECT not between", value1, value2, "defManualSelect");
            return (Criteria) this;
        }

        public Criteria andPrefixIsNull() {
            addCriterion("PREFIX is null");
            return (Criteria) this;
        }

        public Criteria andPrefixIsNotNull() {
            addCriterion("PREFIX is not null");
            return (Criteria) this;
        }

        public Criteria andPrefixEqualTo(String value) {
            addCriterion("PREFIX =", value, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixNotEqualTo(String value) {
            addCriterion("PREFIX <>", value, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixGreaterThan(String value) {
            addCriterion("PREFIX >", value, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixGreaterThanOrEqualTo(String value) {
            addCriterion("PREFIX >=", value, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixLessThan(String value) {
            addCriterion("PREFIX <", value, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixLessThanOrEqualTo(String value) {
            addCriterion("PREFIX <=", value, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixLike(String value) {
            addCriterion("PREFIX like", value, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixNotLike(String value) {
            addCriterion("PREFIX not like", value, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixIn(List<String> values) {
            addCriterion("PREFIX in", values, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixNotIn(List<String> values) {
            addCriterion("PREFIX not in", values, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixBetween(String value1, String value2) {
            addCriterion("PREFIX between", value1, value2, "prefix");
            return (Criteria) this;
        }

        public Criteria andPrefixNotBetween(String value1, String value2) {
            addCriterion("PREFIX not between", value1, value2, "prefix");
            return (Criteria) this;
        }

        public Criteria andMaxLengthIsNull() {
            addCriterion("MAX_LENGTH is null");
            return (Criteria) this;
        }

        public Criteria andMaxLengthIsNotNull() {
            addCriterion("MAX_LENGTH is not null");
            return (Criteria) this;
        }

        public Criteria andMaxLengthEqualTo(Short value) {
            addCriterion("MAX_LENGTH =", value, "maxLength");
            return (Criteria) this;
        }

        public Criteria andMaxLengthNotEqualTo(Short value) {
            addCriterion("MAX_LENGTH <>", value, "maxLength");
            return (Criteria) this;
        }

        public Criteria andMaxLengthGreaterThan(Short value) {
            addCriterion("MAX_LENGTH >", value, "maxLength");
            return (Criteria) this;
        }

        public Criteria andMaxLengthGreaterThanOrEqualTo(Short value) {
            addCriterion("MAX_LENGTH >=", value, "maxLength");
            return (Criteria) this;
        }

        public Criteria andMaxLengthLessThan(Short value) {
            addCriterion("MAX_LENGTH <", value, "maxLength");
            return (Criteria) this;
        }

        public Criteria andMaxLengthLessThanOrEqualTo(Short value) {
            addCriterion("MAX_LENGTH <=", value, "maxLength");
            return (Criteria) this;
        }

        public Criteria andMaxLengthIn(List<Short> values) {
            addCriterion("MAX_LENGTH in", values, "maxLength");
            return (Criteria) this;
        }

        public Criteria andMaxLengthNotIn(List<Short> values) {
            addCriterion("MAX_LENGTH not in", values, "maxLength");
            return (Criteria) this;
        }

        public Criteria andMaxLengthBetween(Short value1, Short value2) {
            addCriterion("MAX_LENGTH between", value1, value2, "maxLength");
            return (Criteria) this;
        }

        public Criteria andMaxLengthNotBetween(Short value1, Short value2) {
            addCriterion("MAX_LENGTH not between", value1, value2, "maxLength");
            return (Criteria) this;
        }

        public Criteria andGenerationModeIsNull() {
            addCriterion("GENERATION_MODE is null");
            return (Criteria) this;
        }

        public Criteria andGenerationModeIsNotNull() {
            addCriterion("GENERATION_MODE is not null");
            return (Criteria) this;
        }

        public Criteria andGenerationModeEqualTo(Short value) {
            addCriterion("GENERATION_MODE =", value, "generationMode");
            return (Criteria) this;
        }

        public Criteria andGenerationModeNotEqualTo(Short value) {
            addCriterion("GENERATION_MODE <>", value, "generationMode");
            return (Criteria) this;
        }

        public Criteria andGenerationModeGreaterThan(Short value) {
            addCriterion("GENERATION_MODE >", value, "generationMode");
            return (Criteria) this;
        }

        public Criteria andGenerationModeGreaterThanOrEqualTo(Short value) {
            addCriterion("GENERATION_MODE >=", value, "generationMode");
            return (Criteria) this;
        }

        public Criteria andGenerationModeLessThan(Short value) {
            addCriterion("GENERATION_MODE <", value, "generationMode");
            return (Criteria) this;
        }

        public Criteria andGenerationModeLessThanOrEqualTo(Short value) {
            addCriterion("GENERATION_MODE <=", value, "generationMode");
            return (Criteria) this;
        }

        public Criteria andGenerationModeIn(List<Short> values) {
            addCriterion("GENERATION_MODE in", values, "generationMode");
            return (Criteria) this;
        }

        public Criteria andGenerationModeNotIn(List<Short> values) {
            addCriterion("GENERATION_MODE not in", values, "generationMode");
            return (Criteria) this;
        }

        public Criteria andGenerationModeBetween(Short value1, Short value2) {
            addCriterion("GENERATION_MODE between", value1, value2, "generationMode");
            return (Criteria) this;
        }

        public Criteria andGenerationModeNotBetween(Short value1, Short value2) {
            addCriterion("GENERATION_MODE not between", value1, value2, "generationMode");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andCouponTypeCodeLikeInsensitive(String value) {
            addCriterion("upper(COUPON_TYPE_CODE) like", value.toUpperCase(), "couponTypeCode");
            return (Criteria) this;
        }

        public Criteria andCouponTypeNameLikeInsensitive(String value) {
            addCriterion("upper(COUPON_TYPE_NAME) like", value.toUpperCase(), "couponTypeName");
            return (Criteria) this;
        }

        public Criteria andPrefixLikeInsensitive(String value) {
            addCriterion("upper(PREFIX) like", value.toUpperCase(), "prefix");
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