package com.comerzzia.iskaypet.pos.persistence.movimientos.manualEyS;

import java.util.ArrayList;
import java.util.List;

public class MovimientoEySExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MovimientoEySExample() {
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

        public Criteria andUidDiarioCajaIsNull() {
            addCriterion("UID_DIARIO_CAJA is null");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaIsNotNull() {
            addCriterion("UID_DIARIO_CAJA is not null");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaEqualTo(String value) {
            addCriterion("UID_DIARIO_CAJA =", value, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaNotEqualTo(String value) {
            addCriterion("UID_DIARIO_CAJA <>", value, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaGreaterThan(String value) {
            addCriterion("UID_DIARIO_CAJA >", value, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaGreaterThanOrEqualTo(String value) {
            addCriterion("UID_DIARIO_CAJA >=", value, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaLessThan(String value) {
            addCriterion("UID_DIARIO_CAJA <", value, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaLessThanOrEqualTo(String value) {
            addCriterion("UID_DIARIO_CAJA <=", value, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaLike(String value) {
            addCriterion("UID_DIARIO_CAJA like", value, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaNotLike(String value) {
            addCriterion("UID_DIARIO_CAJA not like", value, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaIn(List<String> values) {
            addCriterion("UID_DIARIO_CAJA in", values, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaNotIn(List<String> values) {
            addCriterion("UID_DIARIO_CAJA not in", values, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaBetween(String value1, String value2) {
            addCriterion("UID_DIARIO_CAJA between", value1, value2, "uidDiarioCaja");
            return (Criteria) this;
        }

        public Criteria andUidDiarioCajaNotBetween(String value1, String value2) {
            addCriterion("UID_DIARIO_CAJA not between", value1, value2, "uidDiarioCaja");
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

        public Criteria andTipoAuditoriaIsNull() {
            addCriterion("TIPO_AUDITORIA is null");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaIsNotNull() {
            addCriterion("TIPO_AUDITORIA is not null");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaEqualTo(String value) {
            addCriterion("TIPO_AUDITORIA =", value, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaNotEqualTo(String value) {
            addCriterion("TIPO_AUDITORIA <>", value, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaGreaterThan(String value) {
            addCriterion("TIPO_AUDITORIA >", value, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaGreaterThanOrEqualTo(String value) {
            addCriterion("TIPO_AUDITORIA >=", value, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaLessThan(String value) {
            addCriterion("TIPO_AUDITORIA <", value, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaLessThanOrEqualTo(String value) {
            addCriterion("TIPO_AUDITORIA <=", value, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaLike(String value) {
            addCriterion("TIPO_AUDITORIA like", value, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaNotLike(String value) {
            addCriterion("TIPO_AUDITORIA not like", value, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaIn(List<String> values) {
            addCriterion("TIPO_AUDITORIA in", values, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaNotIn(List<String> values) {
            addCriterion("TIPO_AUDITORIA not in", values, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaBetween(String value1, String value2) {
            addCriterion("TIPO_AUDITORIA between", value1, value2, "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaNotBetween(String value1, String value2) {
            addCriterion("TIPO_AUDITORIA not between", value1, value2, "tipoAuditoria");
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

        public Criteria andCodMotivoEqualTo(Long value) {
            addCriterion("COD_MOTIVO =", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoNotEqualTo(Long value) {
            addCriterion("COD_MOTIVO <>", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoGreaterThan(Long value) {
            addCriterion("COD_MOTIVO >", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoGreaterThanOrEqualTo(Long value) {
            addCriterion("COD_MOTIVO >=", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoLessThan(Long value) {
            addCriterion("COD_MOTIVO <", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoLessThanOrEqualTo(Long value) {
            addCriterion("COD_MOTIVO <=", value, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoIn(List<Long> values) {
            addCriterion("COD_MOTIVO in", values, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoNotIn(List<Long> values) {
            addCriterion("COD_MOTIVO not in", values, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoBetween(Long value1, Long value2) {
            addCriterion("COD_MOTIVO between", value1, value2, "codMotivo");
            return (Criteria) this;
        }

        public Criteria andCodMotivoNotBetween(Long value1, Long value2) {
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

        public Criteria andObservacionesIsNull() {
            addCriterion("OBSERVACIONES is null");
            return (Criteria) this;
        }

        public Criteria andObservacionesIsNotNull() {
            addCriterion("OBSERVACIONES is not null");
            return (Criteria) this;
        }

        public Criteria andObservacionesEqualTo(String value) {
            addCriterion("OBSERVACIONES =", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesNotEqualTo(String value) {
            addCriterion("OBSERVACIONES <>", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesGreaterThan(String value) {
            addCriterion("OBSERVACIONES >", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesGreaterThanOrEqualTo(String value) {
            addCriterion("OBSERVACIONES >=", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesLessThan(String value) {
            addCriterion("OBSERVACIONES <", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesLessThanOrEqualTo(String value) {
            addCriterion("OBSERVACIONES <=", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesLike(String value) {
            addCriterion("OBSERVACIONES like", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesNotLike(String value) {
            addCriterion("OBSERVACIONES not like", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesIn(List<String> values) {
            addCriterion("OBSERVACIONES in", values, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesNotIn(List<String> values) {
            addCriterion("OBSERVACIONES not in", values, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesBetween(String value1, String value2) {
            addCriterion("OBSERVACIONES between", value1, value2, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesNotBetween(String value1, String value2) {
            addCriterion("OBSERVACIONES not between", value1, value2, "observaciones");
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