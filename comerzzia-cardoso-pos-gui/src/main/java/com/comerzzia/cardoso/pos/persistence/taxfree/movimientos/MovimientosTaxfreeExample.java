package com.comerzzia.cardoso.pos.persistence.taxfree.movimientos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovimientosTaxfreeExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MovimientosTaxfreeExample() {
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
            addCriterion("uid_actividad is null");
            return (Criteria) this;
        }

        public Criteria andUidActividadIsNotNull() {
            addCriterion("uid_actividad is not null");
            return (Criteria) this;
        }

        public Criteria andUidActividadEqualTo(String value) {
            addCriterion("uid_actividad =", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadNotEqualTo(String value) {
            addCriterion("uid_actividad <>", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadGreaterThan(String value) {
            addCriterion("uid_actividad >", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadGreaterThanOrEqualTo(String value) {
            addCriterion("uid_actividad >=", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadLessThan(String value) {
            addCriterion("uid_actividad <", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadLessThanOrEqualTo(String value) {
            addCriterion("uid_actividad <=", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadLike(String value) {
            addCriterion("uid_actividad like", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadNotLike(String value) {
            addCriterion("uid_actividad not like", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadIn(List<String> values) {
            addCriterion("uid_actividad in", values, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadNotIn(List<String> values) {
            addCriterion("uid_actividad not in", values, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadBetween(String value1, String value2) {
            addCriterion("uid_actividad between", value1, value2, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadNotBetween(String value1, String value2) {
            addCriterion("uid_actividad not between", value1, value2, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andCodTicketIsNull() {
            addCriterion("cod_ticket is null");
            return (Criteria) this;
        }

        public Criteria andCodTicketIsNotNull() {
            addCriterion("cod_ticket is not null");
            return (Criteria) this;
        }

        public Criteria andCodTicketEqualTo(String value) {
            addCriterion("cod_ticket =", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketNotEqualTo(String value) {
            addCriterion("cod_ticket <>", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketGreaterThan(String value) {
            addCriterion("cod_ticket >", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketGreaterThanOrEqualTo(String value) {
            addCriterion("cod_ticket >=", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketLessThan(String value) {
            addCriterion("cod_ticket <", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketLessThanOrEqualTo(String value) {
            addCriterion("cod_ticket <=", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketLike(String value) {
            addCriterion("cod_ticket like", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketNotLike(String value) {
            addCriterion("cod_ticket not like", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketIn(List<String> values) {
            addCriterion("cod_ticket in", values, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketNotIn(List<String> values) {
            addCriterion("cod_ticket not in", values, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketBetween(String value1, String value2) {
            addCriterion("cod_ticket between", value1, value2, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketNotBetween(String value1, String value2) {
            addCriterion("cod_ticket not between", value1, value2, "codTicket");
            return (Criteria) this;
        }

        public Criteria andBarcodeIsNull() {
            addCriterion("barcode is null");
            return (Criteria) this;
        }

        public Criteria andBarcodeIsNotNull() {
            addCriterion("barcode is not null");
            return (Criteria) this;
        }

        public Criteria andBarcodeEqualTo(String value) {
            addCriterion("barcode =", value, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeNotEqualTo(String value) {
            addCriterion("barcode <>", value, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeGreaterThan(String value) {
            addCriterion("barcode >", value, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeGreaterThanOrEqualTo(String value) {
            addCriterion("barcode >=", value, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeLessThan(String value) {
            addCriterion("barcode <", value, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeLessThanOrEqualTo(String value) {
            addCriterion("barcode <=", value, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeLike(String value) {
            addCriterion("barcode like", value, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeNotLike(String value) {
            addCriterion("barcode not like", value, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeIn(List<String> values) {
            addCriterion("barcode in", values, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeNotIn(List<String> values) {
            addCriterion("barcode not in", values, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeBetween(String value1, String value2) {
            addCriterion("barcode between", value1, value2, "barcode");
            return (Criteria) this;
        }

        public Criteria andBarcodeNotBetween(String value1, String value2) {
            addCriterion("barcode not between", value1, value2, "barcode");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoIsNull() {
            addCriterion("tipo_movimiento is null");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoIsNotNull() {
            addCriterion("tipo_movimiento is not null");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoEqualTo(String value) {
            addCriterion("tipo_movimiento =", value, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoNotEqualTo(String value) {
            addCriterion("tipo_movimiento <>", value, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoGreaterThan(String value) {
            addCriterion("tipo_movimiento >", value, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoGreaterThanOrEqualTo(String value) {
            addCriterion("tipo_movimiento >=", value, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoLessThan(String value) {
            addCriterion("tipo_movimiento <", value, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoLessThanOrEqualTo(String value) {
            addCriterion("tipo_movimiento <=", value, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoLike(String value) {
            addCriterion("tipo_movimiento like", value, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoNotLike(String value) {
            addCriterion("tipo_movimiento not like", value, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoIn(List<String> values) {
            addCriterion("tipo_movimiento in", values, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoNotIn(List<String> values) {
            addCriterion("tipo_movimiento not in", values, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoBetween(String value1, String value2) {
            addCriterion("tipo_movimiento between", value1, value2, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoNotBetween(String value1, String value2) {
            addCriterion("tipo_movimiento not between", value1, value2, "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoIsNull() {
            addCriterion("fecha_movimiento is null");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoIsNotNull() {
            addCriterion("fecha_movimiento is not null");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoEqualTo(Date value) {
            addCriterion("fecha_movimiento =", value, "fechaMovimiento");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoNotEqualTo(Date value) {
            addCriterion("fecha_movimiento <>", value, "fechaMovimiento");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoGreaterThan(Date value) {
            addCriterion("fecha_movimiento >", value, "fechaMovimiento");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoGreaterThanOrEqualTo(Date value) {
            addCriterion("fecha_movimiento >=", value, "fechaMovimiento");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoLessThan(Date value) {
            addCriterion("fecha_movimiento <", value, "fechaMovimiento");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoLessThanOrEqualTo(Date value) {
            addCriterion("fecha_movimiento <=", value, "fechaMovimiento");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoIn(List<Date> values) {
            addCriterion("fecha_movimiento in", values, "fechaMovimiento");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoNotIn(List<Date> values) {
            addCriterion("fecha_movimiento not in", values, "fechaMovimiento");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoBetween(Date value1, Date value2) {
            addCriterion("fecha_movimiento between", value1, value2, "fechaMovimiento");
            return (Criteria) this;
        }

        public Criteria andFechaMovimientoNotBetween(Date value1, Date value2) {
            addCriterion("fecha_movimiento not between", value1, value2, "fechaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoIsNull() {
            addCriterion("caja_movimiento is null");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoIsNotNull() {
            addCriterion("caja_movimiento is not null");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoEqualTo(String value) {
            addCriterion("caja_movimiento =", value, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoNotEqualTo(String value) {
            addCriterion("caja_movimiento <>", value, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoGreaterThan(String value) {
            addCriterion("caja_movimiento >", value, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoGreaterThanOrEqualTo(String value) {
            addCriterion("caja_movimiento >=", value, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoLessThan(String value) {
            addCriterion("caja_movimiento <", value, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoLessThanOrEqualTo(String value) {
            addCriterion("caja_movimiento <=", value, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoLike(String value) {
            addCriterion("caja_movimiento like", value, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoNotLike(String value) {
            addCriterion("caja_movimiento not like", value, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoIn(List<String> values) {
            addCriterion("caja_movimiento in", values, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoNotIn(List<String> values) {
            addCriterion("caja_movimiento not in", values, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoBetween(String value1, String value2) {
            addCriterion("caja_movimiento between", value1, value2, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoNotBetween(String value1, String value2) {
            addCriterion("caja_movimiento not between", value1, value2, "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andPasaporteIsNull() {
            addCriterion("pasaporte is null");
            return (Criteria) this;
        }

        public Criteria andPasaporteIsNotNull() {
            addCriterion("pasaporte is not null");
            return (Criteria) this;
        }

        public Criteria andPasaporteEqualTo(String value) {
            addCriterion("pasaporte =", value, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteNotEqualTo(String value) {
            addCriterion("pasaporte <>", value, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteGreaterThan(String value) {
            addCriterion("pasaporte >", value, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteGreaterThanOrEqualTo(String value) {
            addCriterion("pasaporte >=", value, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteLessThan(String value) {
            addCriterion("pasaporte <", value, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteLessThanOrEqualTo(String value) {
            addCriterion("pasaporte <=", value, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteLike(String value) {
            addCriterion("pasaporte like", value, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteNotLike(String value) {
            addCriterion("pasaporte not like", value, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteIn(List<String> values) {
            addCriterion("pasaporte in", values, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteNotIn(List<String> values) {
            addCriterion("pasaporte not in", values, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteBetween(String value1, String value2) {
            addCriterion("pasaporte between", value1, value2, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andPasaporteNotBetween(String value1, String value2) {
            addCriterion("pasaporte not between", value1, value2, "pasaporte");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(uid_actividad) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andCodTicketLikeInsensitive(String value) {
            addCriterion("upper(cod_ticket) like", value.toUpperCase(), "codTicket");
            return (Criteria) this;
        }

        public Criteria andBarcodeLikeInsensitive(String value) {
            addCriterion("upper(barcode) like", value.toUpperCase(), "barcode");
            return (Criteria) this;
        }

        public Criteria andTipoMovimientoLikeInsensitive(String value) {
            addCriterion("upper(tipo_movimiento) like", value.toUpperCase(), "tipoMovimiento");
            return (Criteria) this;
        }

        public Criteria andCajaMovimientoLikeInsensitive(String value) {
            addCriterion("upper(caja_movimiento) like", value.toUpperCase(), "cajaMovimiento");
            return (Criteria) this;
        }

        public Criteria andPasaporteLikeInsensitive(String value) {
            addCriterion("upper(pasaporte) like", value.toUpperCase(), "pasaporte");
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