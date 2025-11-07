package com.comerzzia.api.loyalty.persistence.cardsTypes.activities;

import java.util.ArrayList;
import java.util.List;

public class TipoActTarjetaExample {
    public static final String ORDER_BY_UID_ACTIVIDAD = "UID_ACTIVIDAD";

    public static final String ORDER_BY_UID_ACTIVIDAD_DESC = "UID_ACTIVIDAD DESC";

    public static final String ORDER_BY_CODTIPOTARJ = "CODTIPOTARJ";

    public static final String ORDER_BY_CODTIPOTARJ_DESC = "CODTIPOTARJ DESC";

    public static final String ORDER_BY_CODMEDPAG = "CODMEDPAG";

    public static final String ORDER_BY_CODMEDPAG_DESC = "CODMEDPAG DESC";

    public static final String ORDER_BY_DESMEDPAG = "DESMEDPAG";

    public static final String ORDER_BY_DESMEDPAG_DESC = "DESMEDPAG DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TipoActTarjetaExample() {
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
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
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
            if (condition != null) {
                criteria.add(new Criterion(condition));
            }
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value != null) {
                criteria.add(new Criterion(condition, value));
            }
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 != null && value2 != null) {
                criteria.add(new Criterion(condition, value1, value2));
            }
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

        public Criteria andCodtipotarjIsNull() {
            addCriterion("CODTIPOTARJ is null");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjIsNotNull() {
            addCriterion("CODTIPOTARJ is not null");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjEqualTo(String value) {
            addCriterion("CODTIPOTARJ =", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjNotEqualTo(String value) {
            addCriterion("CODTIPOTARJ <>", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjGreaterThan(String value) {
            addCriterion("CODTIPOTARJ >", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjGreaterThanOrEqualTo(String value) {
            addCriterion("CODTIPOTARJ >=", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjLessThan(String value) {
            addCriterion("CODTIPOTARJ <", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjLessThanOrEqualTo(String value) {
            addCriterion("CODTIPOTARJ <=", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjLike(String value) {
            addCriterion("CODTIPOTARJ like", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjNotLike(String value) {
            addCriterion("CODTIPOTARJ not like", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjIn(List<String> values) {
            addCriterion("CODTIPOTARJ in", values, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjNotIn(List<String> values) {
            addCriterion("CODTIPOTARJ not in", values, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjBetween(String value1, String value2) {
            addCriterion("CODTIPOTARJ between", value1, value2, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjNotBetween(String value1, String value2) {
            addCriterion("CODTIPOTARJ not between", value1, value2, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodmedpagIsNull() {
            addCriterion("CODMEDPAG is null");
            return (Criteria) this;
        }

        public Criteria andCodmedpagIsNotNull() {
            addCriterion("CODMEDPAG is not null");
            return (Criteria) this;
        }

        public Criteria andCodmedpagEqualTo(String value) {
            addCriterion("CODMEDPAG =", value, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagNotEqualTo(String value) {
            addCriterion("CODMEDPAG <>", value, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagGreaterThan(String value) {
            addCriterion("CODMEDPAG >", value, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagGreaterThanOrEqualTo(String value) {
            addCriterion("CODMEDPAG >=", value, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagLessThan(String value) {
            addCriterion("CODMEDPAG <", value, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagLessThanOrEqualTo(String value) {
            addCriterion("CODMEDPAG <=", value, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagLike(String value) {
            addCriterion("CODMEDPAG like", value, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagNotLike(String value) {
            addCriterion("CODMEDPAG not like", value, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagIn(List<String> values) {
            addCriterion("CODMEDPAG in", values, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagNotIn(List<String> values) {
            addCriterion("CODMEDPAG not in", values, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagBetween(String value1, String value2) {
            addCriterion("CODMEDPAG between", value1, value2, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andCodmedpagNotBetween(String value1, String value2) {
            addCriterion("CODMEDPAG not between", value1, value2, "codmedpag");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjLikeInsensitive(String value) {
            addCriterion("upper(CODTIPOTARJ) like", value.toUpperCase(), "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodmedpagLikeInsensitive(String value) {
            addCriterion("upper(CODMEDPAG) like", value.toUpperCase(), "codmedpag");
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