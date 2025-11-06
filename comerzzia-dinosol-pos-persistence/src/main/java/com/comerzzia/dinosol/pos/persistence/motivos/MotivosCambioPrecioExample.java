package com.comerzzia.dinosol.pos.persistence.motivos;

import java.util.ArrayList;
import java.util.List;

public class MotivosCambioPrecioExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MotivosCambioPrecioExample() {
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

        public Criteria andCodMotivoIsNull() {
            addCriterion("COD_MOTIVO is null");
            return (Criteria) this;
        }

        public Criteria andCodMotivoIsNotNull() {
            addCriterion("COD_MOTIVO is not null");
            return (Criteria) this;
        }

        public Criteria andCodMotivoEqualTo(String value) {
            addCriterion("COD_MOTIVO =", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoNotEqualTo(String value) {
            addCriterion("COD_MOTIVO <>", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoGreaterThan(String value) {
            addCriterion("COD_MOTIVO >", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoGreaterThanOrEqualTo(String value) {
            addCriterion("COD_MOTIVO >=", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoLessThan(String value) {
            addCriterion("COD_MOTIVO <", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoLessThanOrEqualTo(String value) {
            addCriterion("COD_MOTIVO <=", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoLike(String value) {
            addCriterion("COD_MOTIVO like", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoNotLike(String value) {
            addCriterion("COD_MOTIVO not like", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoIn(List<String> values) {
            addCriterion("COD_MOTIVO in", values, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoNotIn(List<String> values) {
            addCriterion("COD_MOTIVO not in", values, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoBetween(String value1, String value2) {
            addCriterion("COD_MOTIVO between", value1, value2, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoNotBetween(String value1, String value2) {
            addCriterion("COD_MOTIVO not between", value1, value2, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoIsNull() {
            addCriterion("DES_MOTIVO is null");
            return (Criteria) this;
        }

        public Criteria andDesMotivoIsNotNull() {
            addCriterion("DES_MOTIVO is not null");
            return (Criteria) this;
        }

        public Criteria andDesMotivoEqualTo(String value) {
            addCriterion("DES_MOTIVO =", value, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoNotEqualTo(String value) {
            addCriterion("DES_MOTIVO <>", value, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoGreaterThan(String value) {
            addCriterion("DES_MOTIVO >", value, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoGreaterThanOrEqualTo(String value) {
            addCriterion("DES_MOTIVO >=", value, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoLessThan(String value) {
            addCriterion("DES_MOTIVO <", value, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoLessThanOrEqualTo(String value) {
            addCriterion("DES_MOTIVO <=", value, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoLike(String value) {
            addCriterion("DES_MOTIVO like", value, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoNotLike(String value) {
            addCriterion("DES_MOTIVO not like", value, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoIn(List<String> values) {
            addCriterion("DES_MOTIVO in", values, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoNotIn(List<String> values) {
            addCriterion("DES_MOTIVO not in", values, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoBetween(String value1, String value2) {
            addCriterion("DES_MOTIVO between", value1, value2, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoNotBetween(String value1, String value2) {
            addCriterion("DES_MOTIVO not between", value1, value2, "desMotivo");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionIsNull() {
            addCriterion("VISIBLE_EN_EDICION is null");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionIsNotNull() {
            addCriterion("VISIBLE_EN_EDICION is not null");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionEqualTo(String value) {
            addCriterion("VISIBLE_EN_EDICION =", value, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionNotEqualTo(String value) {
            addCriterion("VISIBLE_EN_EDICION <>", value, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionGreaterThan(String value) {
            addCriterion("VISIBLE_EN_EDICION >", value, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionGreaterThanOrEqualTo(String value) {
            addCriterion("VISIBLE_EN_EDICION >=", value, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionLessThan(String value) {
            addCriterion("VISIBLE_EN_EDICION <", value, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionLessThanOrEqualTo(String value) {
            addCriterion("VISIBLE_EN_EDICION <=", value, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionLike(String value) {
            addCriterion("VISIBLE_EN_EDICION like", value, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionNotLike(String value) {
            addCriterion("VISIBLE_EN_EDICION not like", value, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionIn(List<String> values) {
            addCriterion("VISIBLE_EN_EDICION in", values, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionNotIn(List<String> values) {
            addCriterion("VISIBLE_EN_EDICION not in", values, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionBetween(String value1, String value2) {
            addCriterion("VISIBLE_EN_EDICION between", value1, value2, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionNotBetween(String value1, String value2) {
            addCriterion("VISIBLE_EN_EDICION not between", value1, value2, "visibleEnEdicion");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andCodMotivoLikeInsensitive(String value) {
            addCriterion("upper(COD_MOTIVO) like", value.toUpperCase(), "codMotivo");
            return (Criteria) this;
        }

        public Criteria andDesMotivoLikeInsensitive(String value) {
            addCriterion("upper(DES_MOTIVO) like", value.toUpperCase(), "desMotivo");
            return (Criteria) this;
        }

        public Criteria andVisibleEnEdicionLikeInsensitive(String value) {
            addCriterion("upper(VISIBLE_EN_EDICION) like", value.toUpperCase(), "visibleEnEdicion");
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