package com.comerzzia.iskaypet.pos.persistence.auditorias.motivos;

import java.util.ArrayList;
import java.util.List;

public class MotivosAuditoriaExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MotivosAuditoriaExample() {
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

        public Criteria andCodEmpIsNull() {
            addCriterion("CODEMP is null");
            return (Criteria) this;
        }

        public Criteria andCodEmpIsNotNull() {
            addCriterion("CODEMP is not null");
            return (Criteria) this;
        }

        public Criteria andCodEmpEqualTo(String value) {
            addCriterion("CODEMP =", value, "codemp");
            return (Criteria) this;
        }

        public Criteria andCodEmpNotEqualTo(String value) {
            addCriterion("CODEMP <>", value, "codemp");
            return (Criteria) this;
        }

        public Criteria andCodEmpIn(List<String> values) {
            addCriterion("CODEMP in", values, "codemp");
            return (Criteria) this;
        }

        public Criteria andCodEmpNotIn(List<String> values) {
            addCriterion("CODEMP not in", values, "codemp");
            return (Criteria) this;
        }

        public Criteria andMotivoActivoIsNull() {
            addCriterion("ACTIVO is null");
            return (Criteria) this;
        }

        public Criteria andMotivoActivoIsNotNull() {
            addCriterion("ACTIVO is not null");
            return (Criteria) this;
        }

        public Criteria andMotivoActivoEqualTo(Boolean value) {
            addCriterion("ACTIVO =", value ? (byte) 1 : (byte) 0, "motivoActivo");
            return (Criteria) this;
        }

        public Criteria andMotivoActivoNotEqualTo(Boolean value) {
            addCriterion("ACTIVO <>", value ? (byte) 1 : (byte) 0, "motivoActivo");
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

        public Criteria andPermiteObservacionesIsNull() {
            addCriterion("PERMITE_OBSERVACIONES is null");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesIsNotNull() {
            addCriterion("PERMITE_OBSERVACIONES is not null");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesEqualTo(String value) {
            addCriterion("PERMITE_OBSERVACIONES =", value, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesNotEqualTo(String value) {
            addCriterion("PERMITE_OBSERVACIONES <>", value, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesGreaterThan(String value) {
            addCriterion("PERMITE_OBSERVACIONES >", value, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesGreaterThanOrEqualTo(String value) {
            addCriterion("PERMITE_OBSERVACIONES >=", value, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesLessThan(String value) {
            addCriterion("PERMITE_OBSERVACIONES <", value, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesLessThanOrEqualTo(String value) {
            addCriterion("PERMITE_OBSERVACIONES <=", value, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesLike(String value) {
            addCriterion("PERMITE_OBSERVACIONES like", value, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesNotLike(String value) {
            addCriterion("PERMITE_OBSERVACIONES not like", value, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesIn(List<String> values) {
            addCriterion("PERMITE_OBSERVACIONES in", values, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesNotIn(List<String> values) {
            addCriterion("PERMITE_OBSERVACIONES not in", values, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesBetween(String value1, String value2) {
            addCriterion("PERMITE_OBSERVACIONES between", value1, value2, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesNotBetween(String value1, String value2) {
            addCriterion("PERMITE_OBSERVACIONES not between", value1, value2, "permiteObservaciones");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andTipoAuditoriaLikeInsensitive(String value) {
            addCriterion("upper(TIPO_AUDITORIA) like", value.toUpperCase(), "tipoAuditoria");
            return (Criteria) this;
        }

        public Criteria andDesMotivoLikeInsensitive(String value) {
            addCriterion("upper(DES_MOTIVO) like", value.toUpperCase(), "desMotivo");
            return (Criteria) this;
        }

        public Criteria andPermiteObservacionesLikeInsensitive(String value) {
            addCriterion("upper(PERMITE_OBSERVACIONES) like", value.toUpperCase(), "permiteObservaciones");
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