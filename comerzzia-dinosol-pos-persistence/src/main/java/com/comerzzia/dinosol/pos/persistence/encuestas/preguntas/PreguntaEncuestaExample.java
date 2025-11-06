package com.comerzzia.dinosol.pos.persistence.encuestas.preguntas;

import java.util.ArrayList;
import java.util.List;

public class PreguntaEncuestaExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PreguntaEncuestaExample() {
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

        public Criteria andIdEncuestaIsNull() {
            addCriterion("ID_ENCUESTA is null");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaIsNotNull() {
            addCriterion("ID_ENCUESTA is not null");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaEqualTo(Long value) {
            addCriterion("ID_ENCUESTA =", value, "idEncuesta");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaNotEqualTo(Long value) {
            addCriterion("ID_ENCUESTA <>", value, "idEncuesta");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaGreaterThan(Long value) {
            addCriterion("ID_ENCUESTA >", value, "idEncuesta");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_ENCUESTA >=", value, "idEncuesta");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaLessThan(Long value) {
            addCriterion("ID_ENCUESTA <", value, "idEncuesta");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaLessThanOrEqualTo(Long value) {
            addCriterion("ID_ENCUESTA <=", value, "idEncuesta");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaIn(List<Long> values) {
            addCriterion("ID_ENCUESTA in", values, "idEncuesta");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaNotIn(List<Long> values) {
            addCriterion("ID_ENCUESTA not in", values, "idEncuesta");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaBetween(Long value1, Long value2) {
            addCriterion("ID_ENCUESTA between", value1, value2, "idEncuesta");
            return (Criteria) this;
        }

        public Criteria andIdEncuestaNotBetween(Long value1, Long value2) {
            addCriterion("ID_ENCUESTA not between", value1, value2, "idEncuesta");
            return (Criteria) this;
        }

        public Criteria andOrdenIsNull() {
            addCriterion("ORDEN is null");
            return (Criteria) this;
        }

        public Criteria andOrdenIsNotNull() {
            addCriterion("ORDEN is not null");
            return (Criteria) this;
        }

        public Criteria andOrdenEqualTo(Short value) {
            addCriterion("ORDEN =", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenNotEqualTo(Short value) {
            addCriterion("ORDEN <>", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenGreaterThan(Short value) {
            addCriterion("ORDEN >", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenGreaterThanOrEqualTo(Short value) {
            addCriterion("ORDEN >=", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenLessThan(Short value) {
            addCriterion("ORDEN <", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenLessThanOrEqualTo(Short value) {
            addCriterion("ORDEN <=", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenIn(List<Short> values) {
            addCriterion("ORDEN in", values, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenNotIn(List<Short> values) {
            addCriterion("ORDEN not in", values, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenBetween(Short value1, Short value2) {
            addCriterion("ORDEN between", value1, value2, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenNotBetween(Short value1, Short value2) {
            addCriterion("ORDEN not between", value1, value2, "orden");
            return (Criteria) this;
        }

        public Criteria andTextoIsNull() {
            addCriterion("TEXTO is null");
            return (Criteria) this;
        }

        public Criteria andTextoIsNotNull() {
            addCriterion("TEXTO is not null");
            return (Criteria) this;
        }

        public Criteria andTextoEqualTo(String value) {
            addCriterion("TEXTO =", value, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoNotEqualTo(String value) {
            addCriterion("TEXTO <>", value, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoGreaterThan(String value) {
            addCriterion("TEXTO >", value, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoGreaterThanOrEqualTo(String value) {
            addCriterion("TEXTO >=", value, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoLessThan(String value) {
            addCriterion("TEXTO <", value, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoLessThanOrEqualTo(String value) {
            addCriterion("TEXTO <=", value, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoLike(String value) {
            addCriterion("TEXTO like", value, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoNotLike(String value) {
            addCriterion("TEXTO not like", value, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoIn(List<String> values) {
            addCriterion("TEXTO in", values, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoNotIn(List<String> values) {
            addCriterion("TEXTO not in", values, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoBetween(String value1, String value2) {
            addCriterion("TEXTO between", value1, value2, "texto");
            return (Criteria) this;
        }

        public Criteria andTextoNotBetween(String value1, String value2) {
            addCriterion("TEXTO not between", value1, value2, "texto");
            return (Criteria) this;
        }

        public Criteria andTipoIsNull() {
            addCriterion("TIPO is null");
            return (Criteria) this;
        }

        public Criteria andTipoIsNotNull() {
            addCriterion("TIPO is not null");
            return (Criteria) this;
        }

        public Criteria andTipoEqualTo(String value) {
            addCriterion("TIPO =", value, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoNotEqualTo(String value) {
            addCriterion("TIPO <>", value, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoGreaterThan(String value) {
            addCriterion("TIPO >", value, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoGreaterThanOrEqualTo(String value) {
            addCriterion("TIPO >=", value, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoLessThan(String value) {
            addCriterion("TIPO <", value, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoLessThanOrEqualTo(String value) {
            addCriterion("TIPO <=", value, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoLike(String value) {
            addCriterion("TIPO like", value, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoNotLike(String value) {
            addCriterion("TIPO not like", value, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoIn(List<String> values) {
            addCriterion("TIPO in", values, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoNotIn(List<String> values) {
            addCriterion("TIPO not in", values, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoBetween(String value1, String value2) {
            addCriterion("TIPO between", value1, value2, "tipo");
            return (Criteria) this;
        }

        public Criteria andTipoNotBetween(String value1, String value2) {
            addCriterion("TIPO not between", value1, value2, "tipo");
            return (Criteria) this;
        }

        public Criteria andOpcionesIsNull() {
            addCriterion("OPCIONES is null");
            return (Criteria) this;
        }

        public Criteria andOpcionesIsNotNull() {
            addCriterion("OPCIONES is not null");
            return (Criteria) this;
        }

        public Criteria andOpcionesEqualTo(String value) {
            addCriterion("OPCIONES =", value, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesNotEqualTo(String value) {
            addCriterion("OPCIONES <>", value, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesGreaterThan(String value) {
            addCriterion("OPCIONES >", value, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesGreaterThanOrEqualTo(String value) {
            addCriterion("OPCIONES >=", value, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesLessThan(String value) {
            addCriterion("OPCIONES <", value, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesLessThanOrEqualTo(String value) {
            addCriterion("OPCIONES <=", value, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesLike(String value) {
            addCriterion("OPCIONES like", value, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesNotLike(String value) {
            addCriterion("OPCIONES not like", value, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesIn(List<String> values) {
            addCriterion("OPCIONES in", values, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesNotIn(List<String> values) {
            addCriterion("OPCIONES not in", values, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesBetween(String value1, String value2) {
            addCriterion("OPCIONES between", value1, value2, "opciones");
            return (Criteria) this;
        }

        public Criteria andOpcionesNotBetween(String value1, String value2) {
            addCriterion("OPCIONES not between", value1, value2, "opciones");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andTextoLikeInsensitive(String value) {
            addCriterion("upper(TEXTO) like", value.toUpperCase(), "texto");
            return (Criteria) this;
        }

        public Criteria andTipoLikeInsensitive(String value) {
            addCriterion("upper(TIPO) like", value.toUpperCase(), "tipo");
            return (Criteria) this;
        }

        public Criteria andOpcionesLikeInsensitive(String value) {
            addCriterion("upper(OPCIONES) like", value.toUpperCase(), "opciones");
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