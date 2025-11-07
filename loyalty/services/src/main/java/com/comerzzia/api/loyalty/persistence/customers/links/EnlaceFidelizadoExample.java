package com.comerzzia.api.loyalty.persistence.customers.links;

import java.util.ArrayList;
import java.util.List;

public class EnlaceFidelizadoExample {
    public static final String ORDER_BY_UID_ACTIVIDAD = "UID_ACTIVIDAD";

    public static final String ORDER_BY_UID_ACTIVIDAD_DESC = "UID_ACTIVIDAD DESC";

    public static final String ORDER_BY_ID_CLASE = "ID_CLASE";

    public static final String ORDER_BY_ID_CLASE_DESC = "ID_CLASE DESC";

    public static final String ORDER_BY_ID_OBJETO = "ID_OBJETO";

    public static final String ORDER_BY_ID_OBJETO_DESC = "ID_OBJETO DESC";

    public static final String ORDER_BY_ID_FIDELIZADO = "ID_FIDELIZADO";

    public static final String ORDER_BY_ID_FIDELIZADO_DESC = "ID_FIDELIZADO DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EnlaceFidelizadoExample() {
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

        public Criteria andIdClaseIsNull() {
            addCriterion("ID_CLASE is null");
            return (Criteria) this;
        }

        public Criteria andIdClaseIsNotNull() {
            addCriterion("ID_CLASE is not null");
            return (Criteria) this;
        }

        public Criteria andIdClaseEqualTo(String value) {
            addCriterion("ID_CLASE =", value, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseNotEqualTo(String value) {
            addCriterion("ID_CLASE <>", value, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseGreaterThan(String value) {
            addCriterion("ID_CLASE >", value, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseGreaterThanOrEqualTo(String value) {
            addCriterion("ID_CLASE >=", value, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseLessThan(String value) {
            addCriterion("ID_CLASE <", value, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseLessThanOrEqualTo(String value) {
            addCriterion("ID_CLASE <=", value, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseLike(String value) {
            addCriterion("ID_CLASE like", value, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseNotLike(String value) {
            addCriterion("ID_CLASE not like", value, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseIn(List<String> values) {
            addCriterion("ID_CLASE in", values, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseNotIn(List<String> values) {
            addCriterion("ID_CLASE not in", values, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseBetween(String value1, String value2) {
            addCriterion("ID_CLASE between", value1, value2, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdClaseNotBetween(String value1, String value2) {
            addCriterion("ID_CLASE not between", value1, value2, "idClase");
            return (Criteria) this;
        }

        public Criteria andIdObjetoIsNull() {
            addCriterion("ID_OBJETO is null");
            return (Criteria) this;
        }

        public Criteria andIdObjetoIsNotNull() {
            addCriterion("ID_OBJETO is not null");
            return (Criteria) this;
        }

        public Criteria andIdObjetoEqualTo(String value) {
            addCriterion("ID_OBJETO =", value, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoNotEqualTo(String value) {
            addCriterion("ID_OBJETO <>", value, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoGreaterThan(String value) {
            addCriterion("ID_OBJETO >", value, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoGreaterThanOrEqualTo(String value) {
            addCriterion("ID_OBJETO >=", value, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoLessThan(String value) {
            addCriterion("ID_OBJETO <", value, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoLessThanOrEqualTo(String value) {
            addCriterion("ID_OBJETO <=", value, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoLike(String value) {
            addCriterion("ID_OBJETO like", value, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoNotLike(String value) {
            addCriterion("ID_OBJETO not like", value, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoIn(List<String> values) {
            addCriterion("ID_OBJETO in", values, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoNotIn(List<String> values) {
            addCriterion("ID_OBJETO not in", values, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoBetween(String value1, String value2) {
            addCriterion("ID_OBJETO between", value1, value2, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdObjetoNotBetween(String value1, String value2) {
            addCriterion("ID_OBJETO not between", value1, value2, "idObjeto");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoIsNull() {
            addCriterion("ID_FIDELIZADO is null");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoIsNotNull() {
            addCriterion("ID_FIDELIZADO is not null");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO =", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoNotEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO <>", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoGreaterThan(Long value) {
            addCriterion("ID_FIDELIZADO >", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO >=", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoLessThan(Long value) {
            addCriterion("ID_FIDELIZADO <", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoLessThanOrEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO <=", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoIn(List<Long> values) {
            addCriterion("ID_FIDELIZADO in", values, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoNotIn(List<Long> values) {
            addCriterion("ID_FIDELIZADO not in", values, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoBetween(Long value1, Long value2) {
            addCriterion("ID_FIDELIZADO between", value1, value2, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoNotBetween(Long value1, Long value2) {
            addCriterion("ID_FIDELIZADO not between", value1, value2, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andIdClaseLikeInsensitive(String value) {
            addCriterion("upper(ID_CLASE) like", value.toUpperCase(), "idClase");
            return (Criteria) this;
        }

        public Criteria andIdObjetoLikeInsensitive(String value) {
            addCriterion("upper(ID_OBJETO) like", value.toUpperCase(), "idObjeto");
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