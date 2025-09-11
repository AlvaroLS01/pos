package com.comerzzia.iskaypet.pos.persistence.proformas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProformaBeanExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ProformaBeanExample() {
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

        public Criteria andSistemaOrigenIsNull() {
            addCriterion("SISTEMA_ORIGEN is null");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenIsNotNull() {
            addCriterion("SISTEMA_ORIGEN is not null");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenEqualTo(String value) {
            addCriterion("SISTEMA_ORIGEN =", value, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenNotEqualTo(String value) {
            addCriterion("SISTEMA_ORIGEN <>", value, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenGreaterThan(String value) {
            addCriterion("SISTEMA_ORIGEN >", value, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenGreaterThanOrEqualTo(String value) {
            addCriterion("SISTEMA_ORIGEN >=", value, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenLessThan(String value) {
            addCriterion("SISTEMA_ORIGEN <", value, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenLessThanOrEqualTo(String value) {
            addCriterion("SISTEMA_ORIGEN <=", value, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenLike(String value) {
            addCriterion("SISTEMA_ORIGEN like", value, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenNotLike(String value) {
            addCriterion("SISTEMA_ORIGEN not like", value, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenIn(List<String> values) {
            addCriterion("SISTEMA_ORIGEN in", values, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenNotIn(List<String> values) {
            addCriterion("SISTEMA_ORIGEN not in", values, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenBetween(String value1, String value2) {
            addCriterion("SISTEMA_ORIGEN between", value1, value2, "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andSistemaOrigenNotBetween(String value1, String value2) {
            addCriterion("SISTEMA_ORIGEN not between", value1, value2, "sistemaOrigen");
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

        public Criteria andIdTipoDocumentoIsNull() {
            addCriterion("ID_TIPO_DOCUMENTO is null");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoIsNotNull() {
            addCriterion("ID_TIPO_DOCUMENTO is not null");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoEqualTo(Long value) {
            addCriterion("ID_TIPO_DOCUMENTO =", value, "idTipoDocumento");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoNotEqualTo(Long value) {
            addCriterion("ID_TIPO_DOCUMENTO <>", value, "idTipoDocumento");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoGreaterThan(Long value) {
            addCriterion("ID_TIPO_DOCUMENTO >", value, "idTipoDocumento");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_TIPO_DOCUMENTO >=", value, "idTipoDocumento");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoLessThan(Long value) {
            addCriterion("ID_TIPO_DOCUMENTO <", value, "idTipoDocumento");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoLessThanOrEqualTo(Long value) {
            addCriterion("ID_TIPO_DOCUMENTO <=", value, "idTipoDocumento");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoIn(List<Long> values) {
            addCriterion("ID_TIPO_DOCUMENTO in", values, "idTipoDocumento");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoNotIn(List<Long> values) {
            addCriterion("ID_TIPO_DOCUMENTO not in", values, "idTipoDocumento");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoBetween(Long value1, Long value2) {
            addCriterion("ID_TIPO_DOCUMENTO between", value1, value2, "idTipoDocumento");
            return (Criteria) this;
        }

        public Criteria andIdTipoDocumentoNotBetween(Long value1, Long value2) {
            addCriterion("ID_TIPO_DOCUMENTO not between", value1, value2, "idTipoDocumento");
            return (Criteria) this;
        }

        public Criteria andAutomaticaIsNull() {
            addCriterion("AUTOMATICA is null");
            return (Criteria) this;
        }

        public Criteria andAutomaticaIsNotNull() {
            addCriterion("AUTOMATICA is not null");
            return (Criteria) this;
        }

        public Criteria andAutomaticaEqualTo(Boolean value) {
            addCriterion("AUTOMATICA =", value, "automatica");
            return (Criteria) this;
        }

        public Criteria andAutomaticaNotEqualTo(Boolean value) {
            addCriterion("AUTOMATICA <>", value, "automatica");
            return (Criteria) this;
        }

        public Criteria andAutomaticaGreaterThan(Boolean value) {
            addCriterion("AUTOMATICA >", value, "automatica");
            return (Criteria) this;
        }

        public Criteria andAutomaticaGreaterThanOrEqualTo(Boolean value) {
            addCriterion("AUTOMATICA >=", value, "automatica");
            return (Criteria) this;
        }

        public Criteria andAutomaticaLessThan(Boolean value) {
            addCriterion("AUTOMATICA <", value, "automatica");
            return (Criteria) this;
        }

        public Criteria andAutomaticaLessThanOrEqualTo(Boolean value) {
            addCriterion("AUTOMATICA <=", value, "automatica");
            return (Criteria) this;
        }

        public Criteria andAutomaticaIn(List<Boolean> values) {
            addCriterion("AUTOMATICA in", values, "automatica");
            return (Criteria) this;
        }

        public Criteria andAutomaticaNotIn(List<Boolean> values) {
            addCriterion("AUTOMATICA not in", values, "automatica");
            return (Criteria) this;
        }

        public Criteria andAutomaticaBetween(Boolean value1, Boolean value2) {
            addCriterion("AUTOMATICA between", value1, value2, "automatica");
            return (Criteria) this;
        }

        public Criteria andAutomaticaNotBetween(Boolean value1, Boolean value2) {
            addCriterion("AUTOMATICA not between", value1, value2, "automatica");
            return (Criteria) this;
        }

        public Criteria andCodalmIsNull() {
            addCriterion("CODALM is null");
            return (Criteria) this;
        }

        public Criteria andCodalmIsNotNull() {
            addCriterion("CODALM is not null");
            return (Criteria) this;
        }

        public Criteria andCodalmEqualTo(String value) {
            addCriterion("CODALM =", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmNotEqualTo(String value) {
            addCriterion("CODALM <>", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmGreaterThan(String value) {
            addCriterion("CODALM >", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmGreaterThanOrEqualTo(String value) {
            addCriterion("CODALM >=", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmLessThan(String value) {
            addCriterion("CODALM <", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmLessThanOrEqualTo(String value) {
            addCriterion("CODALM <=", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmLike(String value) {
            addCriterion("CODALM like", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmNotLike(String value) {
            addCriterion("CODALM not like", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmIn(List<String> values) {
            addCriterion("CODALM in", values, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmNotIn(List<String> values) {
            addCriterion("CODALM not in", values, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmBetween(String value1, String value2) {
            addCriterion("CODALM between", value1, value2, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmNotBetween(String value1, String value2) {
            addCriterion("CODALM not between", value1, value2, "codalm");
            return (Criteria) this;
        }

        public Criteria andEstadoActualIsNull() {
            addCriterion("ESTADO_ACTUAL is null");
            return (Criteria) this;
        }

        public Criteria andEstadoActualIsNotNull() {
            addCriterion("ESTADO_ACTUAL is not null");
            return (Criteria) this;
        }

        public Criteria andEstadoActualEqualTo(String value) {
            addCriterion("ESTADO_ACTUAL =", value, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualNotEqualTo(String value) {
            addCriterion("ESTADO_ACTUAL <>", value, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualGreaterThan(String value) {
            addCriterion("ESTADO_ACTUAL >", value, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualGreaterThanOrEqualTo(String value) {
            addCriterion("ESTADO_ACTUAL >=", value, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualLessThan(String value) {
            addCriterion("ESTADO_ACTUAL <", value, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualLessThanOrEqualTo(String value) {
            addCriterion("ESTADO_ACTUAL <=", value, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualLike(String value) {
            addCriterion("ESTADO_ACTUAL like", value, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualNotLike(String value) {
            addCriterion("ESTADO_ACTUAL not like", value, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualIn(List<String> values) {
            addCriterion("ESTADO_ACTUAL in", values, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualNotIn(List<String> values) {
            addCriterion("ESTADO_ACTUAL not in", values, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualBetween(String value1, String value2) {
            addCriterion("ESTADO_ACTUAL between", value1, value2, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andEstadoActualNotBetween(String value1, String value2) {
            addCriterion("ESTADO_ACTUAL not between", value1, value2, "estadoActual");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenIsNull() {
            addCriterion("UID_TICKET_ORIGEN is null");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenIsNotNull() {
            addCriterion("UID_TICKET_ORIGEN is not null");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenEqualTo(String value) {
            addCriterion("UID_TICKET_ORIGEN =", value, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenNotEqualTo(String value) {
            addCriterion("UID_TICKET_ORIGEN <>", value, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenGreaterThan(String value) {
            addCriterion("UID_TICKET_ORIGEN >", value, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenGreaterThanOrEqualTo(String value) {
            addCriterion("UID_TICKET_ORIGEN >=", value, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenLessThan(String value) {
            addCriterion("UID_TICKET_ORIGEN <", value, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenLessThanOrEqualTo(String value) {
            addCriterion("UID_TICKET_ORIGEN <=", value, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenLike(String value) {
            addCriterion("UID_TICKET_ORIGEN like", value, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenNotLike(String value) {
            addCriterion("UID_TICKET_ORIGEN not like", value, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenIn(List<String> values) {
            addCriterion("UID_TICKET_ORIGEN in", values, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenNotIn(List<String> values) {
            addCriterion("UID_TICKET_ORIGEN not in", values, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenBetween(String value1, String value2) {
            addCriterion("UID_TICKET_ORIGEN between", value1, value2, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenNotBetween(String value1, String value2) {
            addCriterion("UID_TICKET_ORIGEN not between", value1, value2, "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenIsNull() {
            addCriterion("SERIE_ORIGEN is null");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenIsNotNull() {
            addCriterion("SERIE_ORIGEN is not null");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenEqualTo(String value) {
            addCriterion("SERIE_ORIGEN =", value, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenNotEqualTo(String value) {
            addCriterion("SERIE_ORIGEN <>", value, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenGreaterThan(String value) {
            addCriterion("SERIE_ORIGEN >", value, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenGreaterThanOrEqualTo(String value) {
            addCriterion("SERIE_ORIGEN >=", value, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenLessThan(String value) {
            addCriterion("SERIE_ORIGEN <", value, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenLessThanOrEqualTo(String value) {
            addCriterion("SERIE_ORIGEN <=", value, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenLike(String value) {
            addCriterion("SERIE_ORIGEN like", value, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenNotLike(String value) {
            addCriterion("SERIE_ORIGEN not like", value, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenIn(List<String> values) {
            addCriterion("SERIE_ORIGEN in", values, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenNotIn(List<String> values) {
            addCriterion("SERIE_ORIGEN not in", values, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenBetween(String value1, String value2) {
            addCriterion("SERIE_ORIGEN between", value1, value2, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenNotBetween(String value1, String value2) {
            addCriterion("SERIE_ORIGEN not between", value1, value2, "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenIsNull() {
            addCriterion("NUMALB_ORIGEN is null");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenIsNotNull() {
            addCriterion("NUMALB_ORIGEN is not null");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenEqualTo(Long value) {
            addCriterion("NUMALB_ORIGEN =", value, "numalbOrigen");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenNotEqualTo(Long value) {
            addCriterion("NUMALB_ORIGEN <>", value, "numalbOrigen");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenGreaterThan(Long value) {
            addCriterion("NUMALB_ORIGEN >", value, "numalbOrigen");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenGreaterThanOrEqualTo(Long value) {
            addCriterion("NUMALB_ORIGEN >=", value, "numalbOrigen");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenLessThan(Long value) {
            addCriterion("NUMALB_ORIGEN <", value, "numalbOrigen");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenLessThanOrEqualTo(Long value) {
            addCriterion("NUMALB_ORIGEN <=", value, "numalbOrigen");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenIn(List<Long> values) {
            addCriterion("NUMALB_ORIGEN in", values, "numalbOrigen");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenNotIn(List<Long> values) {
            addCriterion("NUMALB_ORIGEN not in", values, "numalbOrigen");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenBetween(Long value1, Long value2) {
            addCriterion("NUMALB_ORIGEN between", value1, value2, "numalbOrigen");
            return (Criteria) this;
        }

        public Criteria andNumalbOrigenNotBetween(Long value1, Long value2) {
            addCriterion("NUMALB_ORIGEN not between", value1, value2, "numalbOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenIsNull() {
            addCriterion("CODALM_ORIGEN is null");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenIsNotNull() {
            addCriterion("CODALM_ORIGEN is not null");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenEqualTo(String value) {
            addCriterion("CODALM_ORIGEN =", value, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenNotEqualTo(String value) {
            addCriterion("CODALM_ORIGEN <>", value, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenGreaterThan(String value) {
            addCriterion("CODALM_ORIGEN >", value, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenGreaterThanOrEqualTo(String value) {
            addCriterion("CODALM_ORIGEN >=", value, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenLessThan(String value) {
            addCriterion("CODALM_ORIGEN <", value, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenLessThanOrEqualTo(String value) {
            addCriterion("CODALM_ORIGEN <=", value, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenLike(String value) {
            addCriterion("CODALM_ORIGEN like", value, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenNotLike(String value) {
            addCriterion("CODALM_ORIGEN not like", value, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenIn(List<String> values) {
            addCriterion("CODALM_ORIGEN in", values, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenNotIn(List<String> values) {
            addCriterion("CODALM_ORIGEN not in", values, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenBetween(String value1, String value2) {
            addCriterion("CODALM_ORIGEN between", value1, value2, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenNotBetween(String value1, String value2) {
            addCriterion("CODALM_ORIGEN not between", value1, value2, "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenIsNull() {
            addCriterion("CAJA_ORIGEN is null");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenIsNotNull() {
            addCriterion("CAJA_ORIGEN is not null");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenEqualTo(String value) {
            addCriterion("CAJA_ORIGEN =", value, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenNotEqualTo(String value) {
            addCriterion("CAJA_ORIGEN <>", value, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenGreaterThan(String value) {
            addCriterion("CAJA_ORIGEN >", value, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenGreaterThanOrEqualTo(String value) {
            addCriterion("CAJA_ORIGEN >=", value, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenLessThan(String value) {
            addCriterion("CAJA_ORIGEN <", value, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenLessThanOrEqualTo(String value) {
            addCriterion("CAJA_ORIGEN <=", value, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenLike(String value) {
            addCriterion("CAJA_ORIGEN like", value, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenNotLike(String value) {
            addCriterion("CAJA_ORIGEN not like", value, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenIn(List<String> values) {
            addCriterion("CAJA_ORIGEN in", values, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenNotIn(List<String> values) {
            addCriterion("CAJA_ORIGEN not in", values, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenBetween(String value1, String value2) {
            addCriterion("CAJA_ORIGEN between", value1, value2, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenNotBetween(String value1, String value2) {
            addCriterion("CAJA_ORIGEN not between", value1, value2, "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenIsNull() {
            addCriterion("TIPO_DOCUMENTO_ORIGEN is null");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenIsNotNull() {
            addCriterion("TIPO_DOCUMENTO_ORIGEN is not null");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenEqualTo(Long value) {
            addCriterion("TIPO_DOCUMENTO_ORIGEN =", value, "tipoDocumentoOrigen");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenNotEqualTo(Long value) {
            addCriterion("TIPO_DOCUMENTO_ORIGEN <>", value, "tipoDocumentoOrigen");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenGreaterThan(Long value) {
            addCriterion("TIPO_DOCUMENTO_ORIGEN >", value, "tipoDocumentoOrigen");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenGreaterThanOrEqualTo(Long value) {
            addCriterion("TIPO_DOCUMENTO_ORIGEN >=", value, "tipoDocumentoOrigen");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenLessThan(Long value) {
            addCriterion("TIPO_DOCUMENTO_ORIGEN <", value, "tipoDocumentoOrigen");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenLessThanOrEqualTo(Long value) {
            addCriterion("TIPO_DOCUMENTO_ORIGEN <=", value, "tipoDocumentoOrigen");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenIn(List<Long> values) {
            addCriterion("TIPO_DOCUMENTO_ORIGEN in", values, "tipoDocumentoOrigen");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenNotIn(List<Long> values) {
            addCriterion("TIPO_DOCUMENTO_ORIGEN not in", values, "tipoDocumentoOrigen");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenBetween(Long value1, Long value2) {
            addCriterion("TIPO_DOCUMENTO_ORIGEN between", value1, value2, "tipoDocumentoOrigen");
            return (Criteria) this;
        }

        public Criteria andTipoDocumentoOrigenNotBetween(Long value1, Long value2) {
            addCriterion("TIPO_DOCUMENTO_ORIGEN not between", value1, value2, "tipoDocumentoOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenIsNull() {
            addCriterion("CODIGO_FACTURA_ORIGEN is null");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenIsNotNull() {
            addCriterion("CODIGO_FACTURA_ORIGEN is not null");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenEqualTo(String value) {
            addCriterion("CODIGO_FACTURA_ORIGEN =", value, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenNotEqualTo(String value) {
            addCriterion("CODIGO_FACTURA_ORIGEN <>", value, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenGreaterThan(String value) {
            addCriterion("CODIGO_FACTURA_ORIGEN >", value, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenGreaterThanOrEqualTo(String value) {
            addCriterion("CODIGO_FACTURA_ORIGEN >=", value, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenLessThan(String value) {
            addCriterion("CODIGO_FACTURA_ORIGEN <", value, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenLessThanOrEqualTo(String value) {
            addCriterion("CODIGO_FACTURA_ORIGEN <=", value, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenLike(String value) {
            addCriterion("CODIGO_FACTURA_ORIGEN like", value, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenNotLike(String value) {
            addCriterion("CODIGO_FACTURA_ORIGEN not like", value, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenIn(List<String> values) {
            addCriterion("CODIGO_FACTURA_ORIGEN in", values, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenNotIn(List<String> values) {
            addCriterion("CODIGO_FACTURA_ORIGEN not in", values, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenBetween(String value1, String value2) {
            addCriterion("CODIGO_FACTURA_ORIGEN between", value1, value2, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenNotBetween(String value1, String value2) {
            addCriterion("CODIGO_FACTURA_ORIGEN not between", value1, value2, "codigoFacturaOrigen");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionIsNull() {
            addCriterion("FECHA_CREACION is null");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionIsNotNull() {
            addCriterion("FECHA_CREACION is not null");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionEqualTo(Date value) {
            addCriterion("FECHA_CREACION =", value, "fechaCreacion");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionNotEqualTo(Date value) {
            addCriterion("FECHA_CREACION <>", value, "fechaCreacion");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionGreaterThan(Date value) {
            addCriterion("FECHA_CREACION >", value, "fechaCreacion");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_CREACION >=", value, "fechaCreacion");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionLessThan(Date value) {
            addCriterion("FECHA_CREACION <", value, "fechaCreacion");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_CREACION <=", value, "fechaCreacion");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionIn(List<Date> values) {
            addCriterion("FECHA_CREACION in", values, "fechaCreacion");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionNotIn(List<Date> values) {
            addCriterion("FECHA_CREACION not in", values, "fechaCreacion");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionBetween(Date value1, Date value2) {
            addCriterion("FECHA_CREACION between", value1, value2, "fechaCreacion");
            return (Criteria) this;
        }

        public Criteria andFechaCreacionNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_CREACION not between", value1, value2, "fechaCreacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionIsNull() {
            addCriterion("FECHA_MODIFICACION is null");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionIsNotNull() {
            addCriterion("FECHA_MODIFICACION is not null");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION =", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionNotEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION <>", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionGreaterThan(Date value) {
            addCriterion("FECHA_MODIFICACION >", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION >=", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionLessThan(Date value) {
            addCriterion("FECHA_MODIFICACION <", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION <=", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionIn(List<Date> values) {
            addCriterion("FECHA_MODIFICACION in", values, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionNotIn(List<Date> values) {
            addCriterion("FECHA_MODIFICACION not in", values, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionBetween(Date value1, Date value2) {
            addCriterion("FECHA_MODIFICACION between", value1, value2, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_MODIFICACION not between", value1, value2, "fechaModificacion");
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

        public Criteria andSistemaOrigenLikeInsensitive(String value) {
            addCriterion("upper(SISTEMA_ORIGEN) like", value.toUpperCase(), "sistemaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmLikeInsensitive(String value) {
            addCriterion("upper(CODALM) like", value.toUpperCase(), "codalm");
            return (Criteria) this;
        }

        public Criteria andEstadoActualLikeInsensitive(String value) {
            addCriterion("upper(ESTADO_ACTUAL) like", value.toUpperCase(), "estadoActual");
            return (Criteria) this;
        }

        public Criteria andUidTicketOrigenLikeInsensitive(String value) {
            addCriterion("upper(UID_TICKET_ORIGEN) like", value.toUpperCase(), "uidTicketOrigen");
            return (Criteria) this;
        }

        public Criteria andSerieOrigenLikeInsensitive(String value) {
            addCriterion("upper(SERIE_ORIGEN) like", value.toUpperCase(), "serieOrigen");
            return (Criteria) this;
        }

        public Criteria andCodalmOrigenLikeInsensitive(String value) {
            addCriterion("upper(CODALM_ORIGEN) like", value.toUpperCase(), "codalmOrigen");
            return (Criteria) this;
        }

        public Criteria andCajaOrigenLikeInsensitive(String value) {
            addCriterion("upper(CAJA_ORIGEN) like", value.toUpperCase(), "cajaOrigen");
            return (Criteria) this;
        }

        public Criteria andCodigoFacturaOrigenLikeInsensitive(String value) {
            addCriterion("upper(CODIGO_FACTURA_ORIGEN) like", value.toUpperCase(), "codigoFacturaOrigen");
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