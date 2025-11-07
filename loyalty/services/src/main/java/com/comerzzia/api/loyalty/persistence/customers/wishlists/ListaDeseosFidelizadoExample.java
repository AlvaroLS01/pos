package com.comerzzia.api.loyalty.persistence.customers.wishlists;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListaDeseosFidelizadoExample {
    public static final String ORDER_BY_UID_ACTIVIDAD = "UID_ACTIVIDAD";

    public static final String ORDER_BY_UID_ACTIVIDAD_DESC = "UID_ACTIVIDAD DESC";

    public static final String ORDER_BY_UID_LISTA_DESEOS = "UID_LISTA_DESEOS";

    public static final String ORDER_BY_UID_LISTA_DESEOS_DESC = "UID_LISTA_DESEOS DESC";

    public static final String ORDER_BY_ID_FIDELIZADO = "ID_FIDELIZADO";

    public static final String ORDER_BY_ID_FIDELIZADO_DESC = "ID_FIDELIZADO DESC";

    public static final String ORDER_BY_NOMBRE = "NOMBRE";

    public static final String ORDER_BY_NOMBRE_DESC = "NOMBRE DESC";

    public static final String ORDER_BY_FECHA = "FECHA";

    public static final String ORDER_BY_FECHA_DESC = "FECHA DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ListaDeseosFidelizadoExample() {
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

        public Criteria andUidListaDeseosIsNull() {
            addCriterion("UID_LISTA_DESEOS is null");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosIsNotNull() {
            addCriterion("UID_LISTA_DESEOS is not null");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosEqualTo(String value) {
            addCriterion("UID_LISTA_DESEOS =", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosNotEqualTo(String value) {
            addCriterion("UID_LISTA_DESEOS <>", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosGreaterThan(String value) {
            addCriterion("UID_LISTA_DESEOS >", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosGreaterThanOrEqualTo(String value) {
            addCriterion("UID_LISTA_DESEOS >=", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosLessThan(String value) {
            addCriterion("UID_LISTA_DESEOS <", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosLessThanOrEqualTo(String value) {
            addCriterion("UID_LISTA_DESEOS <=", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosLike(String value) {
            addCriterion("UID_LISTA_DESEOS like", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosNotLike(String value) {
            addCriterion("UID_LISTA_DESEOS not like", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosIn(List<String> values) {
            addCriterion("UID_LISTA_DESEOS in", values, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosNotIn(List<String> values) {
            addCriterion("UID_LISTA_DESEOS not in", values, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosBetween(String value1, String value2) {
            addCriterion("UID_LISTA_DESEOS between", value1, value2, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosNotBetween(String value1, String value2) {
            addCriterion("UID_LISTA_DESEOS not between", value1, value2, "uidListaDeseos");
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

        public Criteria andNombreIsNull() {
            addCriterion("NOMBRE is null");
            return (Criteria) this;
        }

        public Criteria andNombreIsNotNull() {
            addCriterion("NOMBRE is not null");
            return (Criteria) this;
        }

        public Criteria andNombreEqualTo(String value) {
            addCriterion("NOMBRE =", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreNotEqualTo(String value) {
            addCriterion("NOMBRE <>", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreGreaterThan(String value) {
            addCriterion("NOMBRE >", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreGreaterThanOrEqualTo(String value) {
            addCriterion("NOMBRE >=", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreLessThan(String value) {
            addCriterion("NOMBRE <", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreLessThanOrEqualTo(String value) {
            addCriterion("NOMBRE <=", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreLike(String value) {
            addCriterion("NOMBRE like", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreNotLike(String value) {
            addCriterion("NOMBRE not like", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreIn(List<String> values) {
            addCriterion("NOMBRE in", values, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreNotIn(List<String> values) {
            addCriterion("NOMBRE not in", values, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreBetween(String value1, String value2) {
            addCriterion("NOMBRE between", value1, value2, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreNotBetween(String value1, String value2) {
            addCriterion("NOMBRE not between", value1, value2, "nombre");
            return (Criteria) this;
        }

        public Criteria andFechaIsNull() {
            addCriterion("FECHA is null");
            return (Criteria) this;
        }

        public Criteria andFechaIsNotNull() {
            addCriterion("FECHA is not null");
            return (Criteria) this;
        }

        public Criteria andFechaEqualTo(Date value) {
            addCriterion("FECHA =", value, "fecha");
            return (Criteria) this;
        }

        public Criteria andFechaNotEqualTo(Date value) {
            addCriterion("FECHA <>", value, "fecha");
            return (Criteria) this;
        }

        public Criteria andFechaGreaterThan(Date value) {
            addCriterion("FECHA >", value, "fecha");
            return (Criteria) this;
        }

        public Criteria andFechaGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA >=", value, "fecha");
            return (Criteria) this;
        }

        public Criteria andFechaLessThan(Date value) {
            addCriterion("FECHA <", value, "fecha");
            return (Criteria) this;
        }

        public Criteria andFechaLessThanOrEqualTo(Date value) {
            addCriterion("FECHA <=", value, "fecha");
            return (Criteria) this;
        }

        public Criteria andFechaIn(List<Date> values) {
            addCriterion("FECHA in", values, "fecha");
            return (Criteria) this;
        }

        public Criteria andFechaNotIn(List<Date> values) {
            addCriterion("FECHA not in", values, "fecha");
            return (Criteria) this;
        }

        public Criteria andFechaBetween(Date value1, Date value2) {
            addCriterion("FECHA between", value1, value2, "fecha");
            return (Criteria) this;
        }

        public Criteria andFechaNotBetween(Date value1, Date value2) {
            addCriterion("FECHA not between", value1, value2, "fecha");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosLikeInsensitive(String value) {
            addCriterion("upper(UID_LISTA_DESEOS) like", value.toUpperCase(), "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andNombreLikeInsensitive(String value) {
            addCriterion("upper(NOMBRE) like", value.toUpperCase(), "nombre");
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