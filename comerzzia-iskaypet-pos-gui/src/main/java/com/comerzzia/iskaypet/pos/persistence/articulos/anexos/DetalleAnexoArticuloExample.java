package com.comerzzia.iskaypet.pos.persistence.articulos.anexos;

import java.util.ArrayList;
import java.util.List;

public class DetalleAnexoArticuloExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DetalleAnexoArticuloExample() {
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

        public Criteria andCodartIsNull() {
            addCriterion("CODART is null");
            return (Criteria) this;
        }

        public Criteria andCodartIsNotNull() {
            addCriterion("CODART is not null");
            return (Criteria) this;
        }

        public Criteria andCodartEqualTo(String value) {
            addCriterion("CODART =", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartNotEqualTo(String value) {
            addCriterion("CODART <>", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartGreaterThan(String value) {
            addCriterion("CODART >", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartGreaterThanOrEqualTo(String value) {
            addCriterion("CODART >=", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartLessThan(String value) {
            addCriterion("CODART <", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartLessThanOrEqualTo(String value) {
            addCriterion("CODART <=", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartLike(String value) {
            addCriterion("CODART like", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartNotLike(String value) {
            addCriterion("CODART not like", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartIn(List<String> values) {
            addCriterion("CODART in", values, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartNotIn(List<String> values) {
            addCriterion("CODART not in", values, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartBetween(String value1, String value2) {
            addCriterion("CODART between", value1, value2, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartNotBetween(String value1, String value2) {
            addCriterion("CODART not between", value1, value2, "codart");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialIsNull() {
            addCriterion("TIPO_MATERIAL is null");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialIsNotNull() {
            addCriterion("TIPO_MATERIAL is not null");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialEqualTo(String value) {
            addCriterion("TIPO_MATERIAL =", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialNotEqualTo(String value) {
            addCriterion("TIPO_MATERIAL <>", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialGreaterThan(String value) {
            addCriterion("TIPO_MATERIAL >", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialGreaterThanOrEqualTo(String value) {
            addCriterion("TIPO_MATERIAL >=", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialLessThan(String value) {
            addCriterion("TIPO_MATERIAL <", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialLessThanOrEqualTo(String value) {
            addCriterion("TIPO_MATERIAL <=", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialLike(String value) {
            addCriterion("TIPO_MATERIAL like", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialNotLike(String value) {
            addCriterion("TIPO_MATERIAL not like", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialIn(List<String> values) {
            addCriterion("TIPO_MATERIAL in", values, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialNotIn(List<String> values) {
            addCriterion("TIPO_MATERIAL not in", values, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialBetween(String value1, String value2) {
            addCriterion("TIPO_MATERIAL between", value1, value2, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialNotBetween(String value1, String value2) {
            addCriterion("TIPO_MATERIAL not between", value1, value2, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaIsNull() {
            addCriterion("VALOR_CARACTERISTICA is null");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaIsNotNull() {
            addCriterion("VALOR_CARACTERISTICA is not null");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaEqualTo(String value) {
            addCriterion("VALOR_CARACTERISTICA =", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaNotEqualTo(String value) {
            addCriterion("VALOR_CARACTERISTICA <>", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaGreaterThan(String value) {
            addCriterion("VALOR_CARACTERISTICA >", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaGreaterThanOrEqualTo(String value) {
            addCriterion("VALOR_CARACTERISTICA >=", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaLessThan(String value) {
            addCriterion("VALOR_CARACTERISTICA <", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaLessThanOrEqualTo(String value) {
            addCriterion("VALOR_CARACTERISTICA <=", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaLike(String value) {
            addCriterion("VALOR_CARACTERISTICA like", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaNotLike(String value) {
            addCriterion("VALOR_CARACTERISTICA not like", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaIn(List<String> values) {
            addCriterion("VALOR_CARACTERISTICA in", values, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaNotIn(List<String> values) {
            addCriterion("VALOR_CARACTERISTICA not in", values, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaBetween(String value1, String value2) {
            addCriterion("VALOR_CARACTERISTICA between", value1, value2, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaNotBetween(String value1, String value2) {
            addCriterion("VALOR_CARACTERISTICA not between", value1, value2, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andCodartLikeInsensitive(String value) {
            addCriterion("upper(CODART) like", value.toUpperCase(), "codart");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialLikeInsensitive(String value) {
            addCriterion("upper(TIPO_MATERIAL) like", value.toUpperCase(), "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaLikeInsensitive(String value) {
            addCriterion("upper(VALOR_CARACTERISTICA) like", value.toUpperCase(), "valorCaracteristica");
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