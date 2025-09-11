package com.comerzzia.iskaypet.pos.persistence.ticket.gestion.documentos.ticket.contratos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class POSTicketContratoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public POSTicketContratoExample() {
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

        public Criteria andUidTicketIsNull() {
            addCriterion("UID_TICKET is null");
            return (Criteria) this;
        }

        public Criteria andUidTicketIsNotNull() {
            addCriterion("UID_TICKET is not null");
            return (Criteria) this;
        }

        public Criteria andUidTicketEqualTo(String value) {
            addCriterion("UID_TICKET =", value, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketNotEqualTo(String value) {
            addCriterion("UID_TICKET <>", value, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketGreaterThan(String value) {
            addCriterion("UID_TICKET >", value, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketGreaterThanOrEqualTo(String value) {
            addCriterion("UID_TICKET >=", value, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketLessThan(String value) {
            addCriterion("UID_TICKET <", value, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketLessThanOrEqualTo(String value) {
            addCriterion("UID_TICKET <=", value, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketLike(String value) {
            addCriterion("UID_TICKET like", value, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketNotLike(String value) {
            addCriterion("UID_TICKET not like", value, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketIn(List<String> values) {
            addCriterion("UID_TICKET in", values, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketNotIn(List<String> values) {
            addCriterion("UID_TICKET not in", values, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketBetween(String value1, String value2) {
            addCriterion("UID_TICKET between", value1, value2, "uidTicket");
            return (Criteria) this;
        }

        public Criteria andUidTicketNotBetween(String value1, String value2) {
            addCriterion("UID_TICKET not between", value1, value2, "uidTicket");
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

        public Criteria andIdTicketIsNull() {
            addCriterion("ID_TICKET is null");
            return (Criteria) this;
        }

        public Criteria andIdTicketIsNotNull() {
            addCriterion("ID_TICKET is not null");
            return (Criteria) this;
        }

        public Criteria andIdTicketEqualTo(Long value) {
            addCriterion("ID_TICKET =", value, "idTicket");
            return (Criteria) this;
        }

        public Criteria andIdTicketNotEqualTo(Long value) {
            addCriterion("ID_TICKET <>", value, "idTicket");
            return (Criteria) this;
        }

        public Criteria andIdTicketGreaterThan(Long value) {
            addCriterion("ID_TICKET >", value, "idTicket");
            return (Criteria) this;
        }

        public Criteria andIdTicketGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_TICKET >=", value, "idTicket");
            return (Criteria) this;
        }

        public Criteria andIdTicketLessThan(Long value) {
            addCriterion("ID_TICKET <", value, "idTicket");
            return (Criteria) this;
        }

        public Criteria andIdTicketLessThanOrEqualTo(Long value) {
            addCriterion("ID_TICKET <=", value, "idTicket");
            return (Criteria) this;
        }

        public Criteria andIdTicketIn(List<Long> values) {
            addCriterion("ID_TICKET in", values, "idTicket");
            return (Criteria) this;
        }

        public Criteria andIdTicketNotIn(List<Long> values) {
            addCriterion("ID_TICKET not in", values, "idTicket");
            return (Criteria) this;
        }

        public Criteria andIdTicketBetween(Long value1, Long value2) {
            addCriterion("ID_TICKET between", value1, value2, "idTicket");
            return (Criteria) this;
        }

        public Criteria andIdTicketNotBetween(Long value1, Long value2) {
            addCriterion("ID_TICKET not between", value1, value2, "idTicket");
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

        public Criteria andProcesadoIsNull() {
            addCriterion("PROCESADO is null");
            return (Criteria) this;
        }

        public Criteria andProcesadoIsNotNull() {
            addCriterion("PROCESADO is not null");
            return (Criteria) this;
        }

        public Criteria andProcesadoEqualTo(String value) {
            addCriterion("PROCESADO =", value, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoNotEqualTo(String value) {
            addCriterion("PROCESADO <>", value, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoGreaterThan(String value) {
            addCriterion("PROCESADO >", value, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoGreaterThanOrEqualTo(String value) {
            addCriterion("PROCESADO >=", value, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoLessThan(String value) {
            addCriterion("PROCESADO <", value, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoLessThanOrEqualTo(String value) {
            addCriterion("PROCESADO <=", value, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoLike(String value) {
            addCriterion("PROCESADO like", value, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoNotLike(String value) {
            addCriterion("PROCESADO not like", value, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoIn(List<String> values) {
            addCriterion("PROCESADO in", values, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoNotIn(List<String> values) {
            addCriterion("PROCESADO not in", values, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoBetween(String value1, String value2) {
            addCriterion("PROCESADO between", value1, value2, "procesado");
            return (Criteria) this;
        }

        public Criteria andProcesadoNotBetween(String value1, String value2) {
            addCriterion("PROCESADO not between", value1, value2, "procesado");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoIsNull() {
            addCriterion("FECHA_PROCESO is null");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoIsNotNull() {
            addCriterion("FECHA_PROCESO is not null");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoEqualTo(Date value) {
            addCriterion("FECHA_PROCESO =", value, "fechaProceso");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoNotEqualTo(Date value) {
            addCriterion("FECHA_PROCESO <>", value, "fechaProceso");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoGreaterThan(Date value) {
            addCriterion("FECHA_PROCESO >", value, "fechaProceso");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_PROCESO >=", value, "fechaProceso");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoLessThan(Date value) {
            addCriterion("FECHA_PROCESO <", value, "fechaProceso");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_PROCESO <=", value, "fechaProceso");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoIn(List<Date> values) {
            addCriterion("FECHA_PROCESO in", values, "fechaProceso");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoNotIn(List<Date> values) {
            addCriterion("FECHA_PROCESO not in", values, "fechaProceso");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoBetween(Date value1, Date value2) {
            addCriterion("FECHA_PROCESO between", value1, value2, "fechaProceso");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_PROCESO not between", value1, value2, "fechaProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoIsNull() {
            addCriterion("MENSAJE_PROCESO is null");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoIsNotNull() {
            addCriterion("MENSAJE_PROCESO is not null");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoEqualTo(String value) {
            addCriterion("MENSAJE_PROCESO =", value, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoNotEqualTo(String value) {
            addCriterion("MENSAJE_PROCESO <>", value, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoGreaterThan(String value) {
            addCriterion("MENSAJE_PROCESO >", value, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoGreaterThanOrEqualTo(String value) {
            addCriterion("MENSAJE_PROCESO >=", value, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoLessThan(String value) {
            addCriterion("MENSAJE_PROCESO <", value, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoLessThanOrEqualTo(String value) {
            addCriterion("MENSAJE_PROCESO <=", value, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoLike(String value) {
            addCriterion("MENSAJE_PROCESO like", value, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoNotLike(String value) {
            addCriterion("MENSAJE_PROCESO not like", value, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoIn(List<String> values) {
            addCriterion("MENSAJE_PROCESO in", values, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoNotIn(List<String> values) {
            addCriterion("MENSAJE_PROCESO not in", values, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoBetween(String value1, String value2) {
            addCriterion("MENSAJE_PROCESO between", value1, value2, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoNotBetween(String value1, String value2) {
            addCriterion("MENSAJE_PROCESO not between", value1, value2, "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andCodcajaIsNull() {
            addCriterion("CODCAJA is null");
            return (Criteria) this;
        }

        public Criteria andCodcajaIsNotNull() {
            addCriterion("CODCAJA is not null");
            return (Criteria) this;
        }

        public Criteria andCodcajaEqualTo(String value) {
            addCriterion("CODCAJA =", value, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaNotEqualTo(String value) {
            addCriterion("CODCAJA <>", value, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaGreaterThan(String value) {
            addCriterion("CODCAJA >", value, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaGreaterThanOrEqualTo(String value) {
            addCriterion("CODCAJA >=", value, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaLessThan(String value) {
            addCriterion("CODCAJA <", value, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaLessThanOrEqualTo(String value) {
            addCriterion("CODCAJA <=", value, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaLike(String value) {
            addCriterion("CODCAJA like", value, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaNotLike(String value) {
            addCriterion("CODCAJA not like", value, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaIn(List<String> values) {
            addCriterion("CODCAJA in", values, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaNotIn(List<String> values) {
            addCriterion("CODCAJA not in", values, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaBetween(String value1, String value2) {
            addCriterion("CODCAJA between", value1, value2, "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodcajaNotBetween(String value1, String value2) {
            addCriterion("CODCAJA not between", value1, value2, "codcaja");
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

        public Criteria andCodTicketIsNull() {
            addCriterion("COD_TICKET is null");
            return (Criteria) this;
        }

        public Criteria andCodTicketIsNotNull() {
            addCriterion("COD_TICKET is not null");
            return (Criteria) this;
        }

        public Criteria andCodTicketEqualTo(String value) {
            addCriterion("COD_TICKET =", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketNotEqualTo(String value) {
            addCriterion("COD_TICKET <>", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketGreaterThan(String value) {
            addCriterion("COD_TICKET >", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketGreaterThanOrEqualTo(String value) {
            addCriterion("COD_TICKET >=", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketLessThan(String value) {
            addCriterion("COD_TICKET <", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketLessThanOrEqualTo(String value) {
            addCriterion("COD_TICKET <=", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketLike(String value) {
            addCriterion("COD_TICKET like", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketNotLike(String value) {
            addCriterion("COD_TICKET not like", value, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketIn(List<String> values) {
            addCriterion("COD_TICKET in", values, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketNotIn(List<String> values) {
            addCriterion("COD_TICKET not in", values, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketBetween(String value1, String value2) {
            addCriterion("COD_TICKET between", value1, value2, "codTicket");
            return (Criteria) this;
        }

        public Criteria andCodTicketNotBetween(String value1, String value2) {
            addCriterion("COD_TICKET not between", value1, value2, "codTicket");
            return (Criteria) this;
        }

        public Criteria andFirmaIsNull() {
            addCriterion("FIRMA is null");
            return (Criteria) this;
        }

        public Criteria andFirmaIsNotNull() {
            addCriterion("FIRMA is not null");
            return (Criteria) this;
        }

        public Criteria andFirmaEqualTo(String value) {
            addCriterion("FIRMA =", value, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaNotEqualTo(String value) {
            addCriterion("FIRMA <>", value, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaGreaterThan(String value) {
            addCriterion("FIRMA >", value, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaGreaterThanOrEqualTo(String value) {
            addCriterion("FIRMA >=", value, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaLessThan(String value) {
            addCriterion("FIRMA <", value, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaLessThanOrEqualTo(String value) {
            addCriterion("FIRMA <=", value, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaLike(String value) {
            addCriterion("FIRMA like", value, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaNotLike(String value) {
            addCriterion("FIRMA not like", value, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaIn(List<String> values) {
            addCriterion("FIRMA in", values, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaNotIn(List<String> values) {
            addCriterion("FIRMA not in", values, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaBetween(String value1, String value2) {
            addCriterion("FIRMA between", value1, value2, "firma");
            return (Criteria) this;
        }

        public Criteria andFirmaNotBetween(String value1, String value2) {
            addCriterion("FIRMA not between", value1, value2, "firma");
            return (Criteria) this;
        }

        public Criteria andSerieTicketIsNull() {
            addCriterion("SERIE_TICKET is null");
            return (Criteria) this;
        }

        public Criteria andSerieTicketIsNotNull() {
            addCriterion("SERIE_TICKET is not null");
            return (Criteria) this;
        }

        public Criteria andSerieTicketEqualTo(String value) {
            addCriterion("SERIE_TICKET =", value, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketNotEqualTo(String value) {
            addCriterion("SERIE_TICKET <>", value, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketGreaterThan(String value) {
            addCriterion("SERIE_TICKET >", value, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketGreaterThanOrEqualTo(String value) {
            addCriterion("SERIE_TICKET >=", value, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketLessThan(String value) {
            addCriterion("SERIE_TICKET <", value, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketLessThanOrEqualTo(String value) {
            addCriterion("SERIE_TICKET <=", value, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketLike(String value) {
            addCriterion("SERIE_TICKET like", value, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketNotLike(String value) {
            addCriterion("SERIE_TICKET not like", value, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketIn(List<String> values) {
            addCriterion("SERIE_TICKET in", values, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketNotIn(List<String> values) {
            addCriterion("SERIE_TICKET not in", values, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketBetween(String value1, String value2) {
            addCriterion("SERIE_TICKET between", value1, value2, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andSerieTicketNotBetween(String value1, String value2) {
            addCriterion("SERIE_TICKET not between", value1, value2, "serieTicket");
            return (Criteria) this;
        }

        public Criteria andLocatorIdIsNull() {
            addCriterion("LOCATOR_ID is null");
            return (Criteria) this;
        }

        public Criteria andLocatorIdIsNotNull() {
            addCriterion("LOCATOR_ID is not null");
            return (Criteria) this;
        }

        public Criteria andLocatorIdEqualTo(String value) {
            addCriterion("LOCATOR_ID =", value, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdNotEqualTo(String value) {
            addCriterion("LOCATOR_ID <>", value, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdGreaterThan(String value) {
            addCriterion("LOCATOR_ID >", value, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdGreaterThanOrEqualTo(String value) {
            addCriterion("LOCATOR_ID >=", value, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdLessThan(String value) {
            addCriterion("LOCATOR_ID <", value, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdLessThanOrEqualTo(String value) {
            addCriterion("LOCATOR_ID <=", value, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdLike(String value) {
            addCriterion("LOCATOR_ID like", value, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdNotLike(String value) {
            addCriterion("LOCATOR_ID not like", value, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdIn(List<String> values) {
            addCriterion("LOCATOR_ID in", values, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdNotIn(List<String> values) {
            addCriterion("LOCATOR_ID not in", values, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdBetween(String value1, String value2) {
            addCriterion("LOCATOR_ID between", value1, value2, "locatorId");
            return (Criteria) this;
        }

        public Criteria andLocatorIdNotBetween(String value1, String value2) {
            addCriterion("LOCATOR_ID not between", value1, value2, "locatorId");
            return (Criteria) this;
        }

        public Criteria andContratoIsNull() {
            addCriterion("CONTRATO is null");
            return (Criteria) this;
        }

        public Criteria andContratoIsNotNull() {
            addCriterion("CONTRATO is not null");
            return (Criteria) this;
        }

        public Criteria andContratoEqualTo(String value) {
            addCriterion("CONTRATO =", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoNotEqualTo(String value) {
            addCriterion("CONTRATO <>", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoGreaterThan(String value) {
            addCriterion("CONTRATO >", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoGreaterThanOrEqualTo(String value) {
            addCriterion("CONTRATO >=", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoLessThan(String value) {
            addCriterion("CONTRATO <", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoLessThanOrEqualTo(String value) {
            addCriterion("CONTRATO <=", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoLike(String value) {
            addCriterion("CONTRATO like", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoNotLike(String value) {
            addCriterion("CONTRATO not like", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoIn(List<String> values) {
            addCriterion("CONTRATO in", values, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoNotIn(List<String> values) {
            addCriterion("CONTRATO not in", values, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoBetween(String value1, String value2) {
            addCriterion("CONTRATO between", value1, value2, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoNotBetween(String value1, String value2) {
            addCriterion("CONTRATO not between", value1, value2, "contrato");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidTicketLikeInsensitive(String value) {
            addCriterion("upper(UID_TICKET) like", value.toUpperCase(), "uidTicket");
            return (Criteria) this;
        }

        public Criteria andCodalmLikeInsensitive(String value) {
            addCriterion("upper(CODALM) like", value.toUpperCase(), "codalm");
            return (Criteria) this;
        }

        public Criteria andProcesadoLikeInsensitive(String value) {
            addCriterion("upper(PROCESADO) like", value.toUpperCase(), "procesado");
            return (Criteria) this;
        }

        public Criteria andMensajeProcesoLikeInsensitive(String value) {
            addCriterion("upper(MENSAJE_PROCESO) like", value.toUpperCase(), "mensajeProceso");
            return (Criteria) this;
        }

        public Criteria andCodcajaLikeInsensitive(String value) {
            addCriterion("upper(CODCAJA) like", value.toUpperCase(), "codcaja");
            return (Criteria) this;
        }

        public Criteria andCodTicketLikeInsensitive(String value) {
            addCriterion("upper(COD_TICKET) like", value.toUpperCase(), "codTicket");
            return (Criteria) this;
        }

        public Criteria andFirmaLikeInsensitive(String value) {
            addCriterion("upper(FIRMA) like", value.toUpperCase(), "firma");
            return (Criteria) this;
        }

        public Criteria andSerieTicketLikeInsensitive(String value) {
            addCriterion("upper(SERIE_TICKET) like", value.toUpperCase(), "serieTicket");
            return (Criteria) this;
        }

        public Criteria andLocatorIdLikeInsensitive(String value) {
            addCriterion("upper(LOCATOR_ID) like", value.toUpperCase(), "locatorId");
            return (Criteria) this;
        }

        public Criteria andContratoLikeInsensitive(String value) {
            addCriterion("upper(CONTRATO) like", value.toUpperCase(), "contrato");
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