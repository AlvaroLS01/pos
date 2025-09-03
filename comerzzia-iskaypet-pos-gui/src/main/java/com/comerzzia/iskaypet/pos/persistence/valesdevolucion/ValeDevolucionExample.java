package com.comerzzia.iskaypet.pos.persistence.valesdevolucion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ValeDevolucionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ValeDevolucionExample() {
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

        public Criteria andUidInstanciaIsNull() {
            addCriterion("UID_INSTANCIA is null");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaIsNotNull() {
            addCriterion("UID_INSTANCIA is not null");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaEqualTo(String value) {
            addCriterion("UID_INSTANCIA =", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaNotEqualTo(String value) {
            addCriterion("UID_INSTANCIA <>", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaGreaterThan(String value) {
            addCriterion("UID_INSTANCIA >", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaGreaterThanOrEqualTo(String value) {
            addCriterion("UID_INSTANCIA >=", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLessThan(String value) {
            addCriterion("UID_INSTANCIA <", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLessThanOrEqualTo(String value) {
            addCriterion("UID_INSTANCIA <=", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLike(String value) {
            addCriterion("UID_INSTANCIA like", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaNotLike(String value) {
            addCriterion("UID_INSTANCIA not like", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaIn(List<String> values) {
            addCriterion("UID_INSTANCIA in", values, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaNotIn(List<String> values) {
            addCriterion("UID_INSTANCIA not in", values, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaBetween(String value1, String value2) {
            addCriterion("UID_INSTANCIA between", value1, value2, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaNotBetween(String value1, String value2) {
            addCriterion("UID_INSTANCIA not between", value1, value2, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaIsNull() {
            addCriterion("NUMERO_TARJETA is null");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaIsNotNull() {
            addCriterion("NUMERO_TARJETA is not null");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaEqualTo(String value) {
            addCriterion("NUMERO_TARJETA =", value, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaNotEqualTo(String value) {
            addCriterion("NUMERO_TARJETA <>", value, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaGreaterThan(String value) {
            addCriterion("NUMERO_TARJETA >", value, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaGreaterThanOrEqualTo(String value) {
            addCriterion("NUMERO_TARJETA >=", value, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaLessThan(String value) {
            addCriterion("NUMERO_TARJETA <", value, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaLessThanOrEqualTo(String value) {
            addCriterion("NUMERO_TARJETA <=", value, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaLike(String value) {
            addCriterion("NUMERO_TARJETA like", value, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaNotLike(String value) {
            addCriterion("NUMERO_TARJETA not like", value, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaIn(List<String> values) {
            addCriterion("NUMERO_TARJETA in", values, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaNotIn(List<String> values) {
            addCriterion("NUMERO_TARJETA not in", values, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaBetween(String value1, String value2) {
            addCriterion("NUMERO_TARJETA between", value1, value2, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaNotBetween(String value1, String value2) {
            addCriterion("NUMERO_TARJETA not between", value1, value2, "numeroTarjeta");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleIsNull() {
            addCriterion("SALDO_DISPONIBLE is null");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleIsNotNull() {
            addCriterion("SALDO_DISPONIBLE is not null");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleEqualTo(BigDecimal value) {
            addCriterion("SALDO_DISPONIBLE =", value, "saldoDisponible");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleNotEqualTo(BigDecimal value) {
            addCriterion("SALDO_DISPONIBLE <>", value, "saldoDisponible");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleGreaterThan(BigDecimal value) {
            addCriterion("SALDO_DISPONIBLE >", value, "saldoDisponible");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("SALDO_DISPONIBLE >=", value, "saldoDisponible");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleLessThan(BigDecimal value) {
            addCriterion("SALDO_DISPONIBLE <", value, "saldoDisponible");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleLessThanOrEqualTo(BigDecimal value) {
            addCriterion("SALDO_DISPONIBLE <=", value, "saldoDisponible");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleIn(List<BigDecimal> values) {
            addCriterion("SALDO_DISPONIBLE in", values, "saldoDisponible");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleNotIn(List<BigDecimal> values) {
            addCriterion("SALDO_DISPONIBLE not in", values, "saldoDisponible");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("SALDO_DISPONIBLE between", value1, value2, "saldoDisponible");
            return (Criteria) this;
        }

        public Criteria andSaldoDisponibleNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("SALDO_DISPONIBLE not between", value1, value2, "saldoDisponible");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionIsNull() {
            addCriterion("FECHA_ACTIVACION is null");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionIsNotNull() {
            addCriterion("FECHA_ACTIVACION is not null");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionEqualTo(Date value) {
            addCriterion("FECHA_ACTIVACION =", value, "fechaActivacion");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionNotEqualTo(Date value) {
            addCriterion("FECHA_ACTIVACION <>", value, "fechaActivacion");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionGreaterThan(Date value) {
            addCriterion("FECHA_ACTIVACION >", value, "fechaActivacion");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_ACTIVACION >=", value, "fechaActivacion");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionLessThan(Date value) {
            addCriterion("FECHA_ACTIVACION <", value, "fechaActivacion");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_ACTIVACION <=", value, "fechaActivacion");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionIn(List<Date> values) {
            addCriterion("FECHA_ACTIVACION in", values, "fechaActivacion");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionNotIn(List<Date> values) {
            addCriterion("FECHA_ACTIVACION not in", values, "fechaActivacion");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionBetween(Date value1, Date value2) {
            addCriterion("FECHA_ACTIVACION between", value1, value2, "fechaActivacion");
            return (Criteria) this;
        }

        public Criteria andFechaActivacionNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_ACTIVACION not between", value1, value2, "fechaActivacion");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andNumeroTarjetaLikeInsensitive(String value) {
            addCriterion("upper(NUMERO_TARJETA) like", value.toUpperCase(), "numeroTarjeta");
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