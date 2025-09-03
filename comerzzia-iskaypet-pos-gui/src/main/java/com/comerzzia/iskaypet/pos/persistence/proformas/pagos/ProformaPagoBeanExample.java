package com.comerzzia.iskaypet.pos.persistence.proformas.pagos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProformaPagoBeanExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ProformaPagoBeanExample() {
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

        public Criteria andIdProformaIsNull() {
            addCriterion("ID_PROFORMA is null");
            return (Criteria) this;
        }

        public Criteria andIdProformaIsNotNull() {
            addCriterion("ID_PROFORMA is not null");
            return (Criteria) this;
        }

        public Criteria andIdProformaEqualTo(String value) {
            addCriterion("ID_PROFORMA =", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaNotEqualTo(String value) {
            addCriterion("ID_PROFORMA <>", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaGreaterThan(String value) {
            addCriterion("ID_PROFORMA >", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaGreaterThanOrEqualTo(String value) {
            addCriterion("ID_PROFORMA >=", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaLessThan(String value) {
            addCriterion("ID_PROFORMA <", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaLessThanOrEqualTo(String value) {
            addCriterion("ID_PROFORMA <=", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaLike(String value) {
            addCriterion("ID_PROFORMA like", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaNotLike(String value) {
            addCriterion("ID_PROFORMA not like", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaIn(List<String> values) {
            addCriterion("ID_PROFORMA in", values, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaNotIn(List<String> values) {
            addCriterion("ID_PROFORMA not in", values, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaBetween(String value1, String value2) {
            addCriterion("ID_PROFORMA between", value1, value2, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaNotBetween(String value1, String value2) {
            addCriterion("ID_PROFORMA not between", value1, value2, "idProforma");
            return (Criteria) this;
        }

        public Criteria andLineaIsNull() {
            addCriterion("LINEA is null");
            return (Criteria) this;
        }

        public Criteria andLineaIsNotNull() {
            addCriterion("LINEA is not null");
            return (Criteria) this;
        }

        public Criteria andLineaEqualTo(Integer value) {
            addCriterion("LINEA =", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaNotEqualTo(Integer value) {
            addCriterion("LINEA <>", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaGreaterThan(Integer value) {
            addCriterion("LINEA >", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaGreaterThanOrEqualTo(Integer value) {
            addCriterion("LINEA >=", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaLessThan(Integer value) {
            addCriterion("LINEA <", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaLessThanOrEqualTo(Integer value) {
            addCriterion("LINEA <=", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaIn(List<Integer> values) {
            addCriterion("LINEA in", values, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaNotIn(List<Integer> values) {
            addCriterion("LINEA not in", values, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaBetween(Integer value1, Integer value2) {
            addCriterion("LINEA between", value1, value2, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaNotBetween(Integer value1, Integer value2) {
            addCriterion("LINEA not between", value1, value2, "linea");
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

        public Criteria andImportePagoIsNull() {
            addCriterion("IMPORTE_PAGO is null");
            return (Criteria) this;
        }

        public Criteria andImportePagoIsNotNull() {
            addCriterion("IMPORTE_PAGO is not null");
            return (Criteria) this;
        }

        public Criteria andImportePagoEqualTo(BigDecimal value) {
            addCriterion("IMPORTE_PAGO =", value, "importePago");
            return (Criteria) this;
        }

        public Criteria andImportePagoNotEqualTo(BigDecimal value) {
            addCriterion("IMPORTE_PAGO <>", value, "importePago");
            return (Criteria) this;
        }

        public Criteria andImportePagoGreaterThan(BigDecimal value) {
            addCriterion("IMPORTE_PAGO >", value, "importePago");
            return (Criteria) this;
        }

        public Criteria andImportePagoGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("IMPORTE_PAGO >=", value, "importePago");
            return (Criteria) this;
        }

        public Criteria andImportePagoLessThan(BigDecimal value) {
            addCriterion("IMPORTE_PAGO <", value, "importePago");
            return (Criteria) this;
        }

        public Criteria andImportePagoLessThanOrEqualTo(BigDecimal value) {
            addCriterion("IMPORTE_PAGO <=", value, "importePago");
            return (Criteria) this;
        }

        public Criteria andImportePagoIn(List<BigDecimal> values) {
            addCriterion("IMPORTE_PAGO in", values, "importePago");
            return (Criteria) this;
        }

        public Criteria andImportePagoNotIn(List<BigDecimal> values) {
            addCriterion("IMPORTE_PAGO not in", values, "importePago");
            return (Criteria) this;
        }

        public Criteria andImportePagoBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("IMPORTE_PAGO between", value1, value2, "importePago");
            return (Criteria) this;
        }

        public Criteria andImportePagoNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("IMPORTE_PAGO not between", value1, value2, "importePago");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andIdProformaLikeInsensitive(String value) {
            addCriterion("upper(ID_PROFORMA) like", value.toUpperCase(), "idProforma");
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