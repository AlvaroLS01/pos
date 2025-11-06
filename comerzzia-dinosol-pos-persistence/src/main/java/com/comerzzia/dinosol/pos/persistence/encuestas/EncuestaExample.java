package com.comerzzia.dinosol.pos.persistence.encuestas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EncuestaExample {

	protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EncuestaExample() {
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

        public Criteria andDescripcionIsNull() {
            addCriterion("DESCRIPCION is null");
            return (Criteria) this;
        }

        public Criteria andDescripcionIsNotNull() {
            addCriterion("DESCRIPCION is not null");
            return (Criteria) this;
        }

        public Criteria andDescripcionEqualTo(String value) {
            addCriterion("DESCRIPCION =", value, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionNotEqualTo(String value) {
            addCriterion("DESCRIPCION <>", value, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionGreaterThan(String value) {
            addCriterion("DESCRIPCION >", value, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionGreaterThanOrEqualTo(String value) {
            addCriterion("DESCRIPCION >=", value, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionLessThan(String value) {
            addCriterion("DESCRIPCION <", value, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionLessThanOrEqualTo(String value) {
            addCriterion("DESCRIPCION <=", value, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionLike(String value) {
            addCriterion("DESCRIPCION like", value, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionNotLike(String value) {
            addCriterion("DESCRIPCION not like", value, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionIn(List<String> values) {
            addCriterion("DESCRIPCION in", values, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionNotIn(List<String> values) {
            addCriterion("DESCRIPCION not in", values, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionBetween(String value1, String value2) {
            addCriterion("DESCRIPCION between", value1, value2, "descripcion");
            return (Criteria) this;
        }

        public Criteria andDescripcionNotBetween(String value1, String value2) {
            addCriterion("DESCRIPCION not between", value1, value2, "descripcion");
            return (Criteria) this;
        }

        public Criteria andFechaInicioIsNull() {
            addCriterion("FECHA_INICIO is null");
            return (Criteria) this;
        }

        public Criteria andFechaInicioIsNotNull() {
            addCriterion("FECHA_INICIO is not null");
            return (Criteria) this;
        }

        public Criteria andFechaInicioEqualTo(Date value) {
            addCriterion("FECHA_INICIO =", value, "fechaInicio");
            return (Criteria) this;
        }

        public Criteria andFechaInicioNotEqualTo(Date value) {
            addCriterion("FECHA_INICIO <>", value, "fechaInicio");
            return (Criteria) this;
        }

        public Criteria andFechaInicioGreaterThan(Date value) {
            addCriterion("FECHA_INICIO >", value, "fechaInicio");
            return (Criteria) this;
        }

        public Criteria andFechaInicioGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_INICIO >=", value, "fechaInicio");
            return (Criteria) this;
        }

        public Criteria andFechaInicioLessThan(Date value) {
            addCriterion("FECHA_INICIO <", value, "fechaInicio");
            return (Criteria) this;
        }

        public Criteria andFechaInicioLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_INICIO <=", value, "fechaInicio");
            return (Criteria) this;
        }

        public Criteria andFechaInicioIn(List<Date> values) {
            addCriterion("FECHA_INICIO in", values, "fechaInicio");
            return (Criteria) this;
        }

        public Criteria andFechaInicioNotIn(List<Date> values) {
            addCriterion("FECHA_INICIO not in", values, "fechaInicio");
            return (Criteria) this;
        }

        public Criteria andFechaInicioBetween(Date value1, Date value2) {
            addCriterion("FECHA_INICIO between", value1, value2, "fechaInicio");
            return (Criteria) this;
        }

        public Criteria andFechaInicioNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_INICIO not between", value1, value2, "fechaInicio");
            return (Criteria) this;
        }

        public Criteria andFechaFinIsNull() {
            addCriterion("FECHA_FIN is null");
            return (Criteria) this;
        }

        public Criteria andFechaFinIsNotNull() {
            addCriterion("FECHA_FIN is not null");
            return (Criteria) this;
        }

        public Criteria andFechaFinEqualTo(Date value) {
            addCriterion("FECHA_FIN =", value, "fechaFin");
            return (Criteria) this;
        }

        public Criteria andFechaFinNotEqualTo(Date value) {
            addCriterion("FECHA_FIN <>", value, "fechaFin");
            return (Criteria) this;
        }

        public Criteria andFechaFinGreaterThan(Date value) {
            addCriterion("FECHA_FIN >", value, "fechaFin");
            return (Criteria) this;
        }

        public Criteria andFechaFinGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_FIN >=", value, "fechaFin");
            return (Criteria) this;
        }

        public Criteria andFechaFinLessThan(Date value) {
            addCriterion("FECHA_FIN <", value, "fechaFin");
            return (Criteria) this;
        }

        public Criteria andFechaFinLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_FIN <=", value, "fechaFin");
            return (Criteria) this;
        }

        public Criteria andFechaFinIn(List<Date> values) {
            addCriterion("FECHA_FIN in", values, "fechaFin");
            return (Criteria) this;
        }

        public Criteria andFechaFinNotIn(List<Date> values) {
            addCriterion("FECHA_FIN not in", values, "fechaFin");
            return (Criteria) this;
        }

        public Criteria andFechaFinBetween(Date value1, Date value2) {
            addCriterion("FECHA_FIN between", value1, value2, "fechaFin");
            return (Criteria) this;
        }

        public Criteria andFechaFinNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_FIN not between", value1, value2, "fechaFin");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorIsNull() {
            addCriterion("MOSTRAR_EN_VISOR is null");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorIsNotNull() {
            addCriterion("MOSTRAR_EN_VISOR is not null");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorEqualTo(String value) {
            addCriterion("MOSTRAR_EN_VISOR =", value, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorNotEqualTo(String value) {
            addCriterion("MOSTRAR_EN_VISOR <>", value, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorGreaterThan(String value) {
            addCriterion("MOSTRAR_EN_VISOR >", value, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorGreaterThanOrEqualTo(String value) {
            addCriterion("MOSTRAR_EN_VISOR >=", value, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorLessThan(String value) {
            addCriterion("MOSTRAR_EN_VISOR <", value, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorLessThanOrEqualTo(String value) {
            addCriterion("MOSTRAR_EN_VISOR <=", value, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorLike(String value) {
            addCriterion("MOSTRAR_EN_VISOR like", value, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorNotLike(String value) {
            addCriterion("MOSTRAR_EN_VISOR not like", value, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorIn(List<String> values) {
            addCriterion("MOSTRAR_EN_VISOR in", values, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorNotIn(List<String> values) {
            addCriterion("MOSTRAR_EN_VISOR not in", values, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorBetween(String value1, String value2) {
            addCriterion("MOSTRAR_EN_VISOR between", value1, value2, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorNotBetween(String value1, String value2) {
            addCriterion("MOSTRAR_EN_VISOR not between", value1, value2, "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andActivoIsNull() {
            addCriterion("ACTIVO is null");
            return (Criteria) this;
        }

        public Criteria andActivoIsNotNull() {
            addCriterion("ACTIVO is not null");
            return (Criteria) this;
        }

        public Criteria andActivoEqualTo(String value) {
            addCriterion("ACTIVO =", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoNotEqualTo(String value) {
            addCriterion("ACTIVO <>", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoGreaterThan(String value) {
            addCriterion("ACTIVO >", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoGreaterThanOrEqualTo(String value) {
            addCriterion("ACTIVO >=", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoLessThan(String value) {
            addCriterion("ACTIVO <", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoLessThanOrEqualTo(String value) {
            addCriterion("ACTIVO <=", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoLike(String value) {
            addCriterion("ACTIVO like", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoNotLike(String value) {
            addCriterion("ACTIVO not like", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoIn(List<String> values) {
            addCriterion("ACTIVO in", values, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoNotIn(List<String> values) {
            addCriterion("ACTIVO not in", values, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoBetween(String value1, String value2) {
            addCriterion("ACTIVO between", value1, value2, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoNotBetween(String value1, String value2) {
            addCriterion("ACTIVO not between", value1, value2, "activo");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andDescripcionLikeInsensitive(String value) {
            addCriterion("upper(DESCRIPCION) like", value.toUpperCase(), "descripcion");
            return (Criteria) this;
        }

        public Criteria andMostrarEnVisorLikeInsensitive(String value) {
            addCriterion("upper(MOSTRAR_EN_VISOR) like", value.toUpperCase(), "mostrarEnVisor");
            return (Criteria) this;
        }

        public Criteria andActivoLikeInsensitive(String value) {
            addCriterion("upper(ACTIVO) like", value.toUpperCase(), "activo");
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