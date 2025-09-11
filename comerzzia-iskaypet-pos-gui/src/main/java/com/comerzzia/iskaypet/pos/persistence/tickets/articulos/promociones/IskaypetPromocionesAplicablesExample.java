package com.comerzzia.iskaypet.pos.persistence.tickets.articulos.promociones;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IskaypetPromocionesAplicablesExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public IskaypetPromocionesAplicablesExample() {
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

        public Criteria andIdPromocionIsNull() {
            addCriterion("ID_PROMOCION is null");
            return (Criteria) this;
        }

        public Criteria andIdPromocionIsNotNull() {
            addCriterion("ID_PROMOCION is not null");
            return (Criteria) this;
        }

        public Criteria andIdPromocionEqualTo(Long value) {
            addCriterion("ID_PROMOCION =", value, "idPromocion");
            return (Criteria) this;
        }

        public Criteria andIdPromocionNotEqualTo(Long value) {
            addCriterion("ID_PROMOCION <>", value, "idPromocion");
            return (Criteria) this;
        }

        public Criteria andIdPromocionGreaterThan(Long value) {
            addCriterion("ID_PROMOCION >", value, "idPromocion");
            return (Criteria) this;
        }

        public Criteria andIdPromocionGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_PROMOCION >=", value, "idPromocion");
            return (Criteria) this;
        }

        public Criteria andIdPromocionLessThan(Long value) {
            addCriterion("ID_PROMOCION <", value, "idPromocion");
            return (Criteria) this;
        }

        public Criteria andIdPromocionLessThanOrEqualTo(Long value) {
            addCriterion("ID_PROMOCION <=", value, "idPromocion");
            return (Criteria) this;
        }

        public Criteria andIdPromocionIn(List<Long> values) {
            addCriterion("ID_PROMOCION in", values, "idPromocion");
            return (Criteria) this;
        }

        public Criteria andIdPromocionNotIn(List<Long> values) {
            addCriterion("ID_PROMOCION not in", values, "idPromocion");
            return (Criteria) this;
        }

        public Criteria andIdPromocionBetween(Long value1, Long value2) {
            addCriterion("ID_PROMOCION between", value1, value2, "idPromocion");
            return (Criteria) this;
        }

        public Criteria andIdPromocionNotBetween(Long value1, Long value2) {
            addCriterion("ID_PROMOCION not between", value1, value2, "idPromocion");
            return (Criteria) this;
        }

        public Criteria andCodtarIsNull() {
            addCriterion("CODTAR is null");
            return (Criteria) this;
        }

        public Criteria andCodtarIsNotNull() {
            addCriterion("CODTAR is not null");
            return (Criteria) this;
        }

        public Criteria andCodtarEqualTo(String value) {
            addCriterion("CODTAR =", value, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarNotEqualTo(String value) {
            addCriterion("CODTAR <>", value, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarGreaterThan(String value) {
            addCriterion("CODTAR >", value, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarGreaterThanOrEqualTo(String value) {
            addCriterion("CODTAR >=", value, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarLessThan(String value) {
            addCriterion("CODTAR <", value, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarLessThanOrEqualTo(String value) {
            addCriterion("CODTAR <=", value, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarLike(String value) {
            addCriterion("CODTAR like", value, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarNotLike(String value) {
            addCriterion("CODTAR not like", value, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarIn(List<String> values) {
            addCriterion("CODTAR in", values, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarNotIn(List<String> values) {
            addCriterion("CODTAR not in", values, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarBetween(String value1, String value2) {
            addCriterion("CODTAR between", value1, value2, "codtar");
            return (Criteria) this;
        }

        public Criteria andCodtarNotBetween(String value1, String value2) {
            addCriterion("CODTAR not between", value1, value2, "codtar");
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

        public Criteria andSoloFidelizacionIsNull() {
            addCriterion("SOLO_FIDELIZACION is null");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionIsNotNull() {
            addCriterion("SOLO_FIDELIZACION is not null");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionEqualTo(String value) {
            addCriterion("SOLO_FIDELIZACION =", value, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionNotEqualTo(String value) {
            addCriterion("SOLO_FIDELIZACION <>", value, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionGreaterThan(String value) {
            addCriterion("SOLO_FIDELIZACION >", value, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionGreaterThanOrEqualTo(String value) {
            addCriterion("SOLO_FIDELIZACION >=", value, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionLessThan(String value) {
            addCriterion("SOLO_FIDELIZACION <", value, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionLessThanOrEqualTo(String value) {
            addCriterion("SOLO_FIDELIZACION <=", value, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionLike(String value) {
            addCriterion("SOLO_FIDELIZACION like", value, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionNotLike(String value) {
            addCriterion("SOLO_FIDELIZACION not like", value, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionIn(List<String> values) {
            addCriterion("SOLO_FIDELIZACION in", values, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionNotIn(List<String> values) {
            addCriterion("SOLO_FIDELIZACION not in", values, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionBetween(String value1, String value2) {
            addCriterion("SOLO_FIDELIZACION between", value1, value2, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionNotBetween(String value1, String value2) {
            addCriterion("SOLO_FIDELIZACION not between", value1, value2, "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionIsNull() {
            addCriterion("ID_TIPO_PROMOCION is null");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionIsNotNull() {
            addCriterion("ID_TIPO_PROMOCION is not null");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionEqualTo(Long value) {
            addCriterion("ID_TIPO_PROMOCION =", value, "idTipoPromocion");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionNotEqualTo(Long value) {
            addCriterion("ID_TIPO_PROMOCION <>", value, "idTipoPromocion");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionGreaterThan(Long value) {
            addCriterion("ID_TIPO_PROMOCION >", value, "idTipoPromocion");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_TIPO_PROMOCION >=", value, "idTipoPromocion");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionLessThan(Long value) {
            addCriterion("ID_TIPO_PROMOCION <", value, "idTipoPromocion");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionLessThanOrEqualTo(Long value) {
            addCriterion("ID_TIPO_PROMOCION <=", value, "idTipoPromocion");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionIn(List<Long> values) {
            addCriterion("ID_TIPO_PROMOCION in", values, "idTipoPromocion");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionNotIn(List<Long> values) {
            addCriterion("ID_TIPO_PROMOCION not in", values, "idTipoPromocion");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionBetween(Long value1, Long value2) {
            addCriterion("ID_TIPO_PROMOCION between", value1, value2, "idTipoPromocion");
            return (Criteria) this;
        }

        public Criteria andIdTipoPromocionNotBetween(Long value1, Long value2) {
            addCriterion("ID_TIPO_PROMOCION not between", value1, value2, "idTipoPromocion");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaIsNull() {
            addCriterion("VERSION_TARIFA is null");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaIsNotNull() {
            addCriterion("VERSION_TARIFA is not null");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaEqualTo(Long value) {
            addCriterion("VERSION_TARIFA =", value, "versionTarifa");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaNotEqualTo(Long value) {
            addCriterion("VERSION_TARIFA <>", value, "versionTarifa");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaGreaterThan(Long value) {
            addCriterion("VERSION_TARIFA >", value, "versionTarifa");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaGreaterThanOrEqualTo(Long value) {
            addCriterion("VERSION_TARIFA >=", value, "versionTarifa");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaLessThan(Long value) {
            addCriterion("VERSION_TARIFA <", value, "versionTarifa");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaLessThanOrEqualTo(Long value) {
            addCriterion("VERSION_TARIFA <=", value, "versionTarifa");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaIn(List<Long> values) {
            addCriterion("VERSION_TARIFA in", values, "versionTarifa");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaNotIn(List<Long> values) {
            addCriterion("VERSION_TARIFA not in", values, "versionTarifa");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaBetween(Long value1, Long value2) {
            addCriterion("VERSION_TARIFA between", value1, value2, "versionTarifa");
            return (Criteria) this;
        }

        public Criteria andVersionTarifaNotBetween(Long value1, Long value2) {
            addCriterion("VERSION_TARIFA not between", value1, value2, "versionTarifa");
            return (Criteria) this;
        }

        public Criteria andTipoDtoIsNull() {
            addCriterion("TIPO_DTO is null");
            return (Criteria) this;
        }

        public Criteria andTipoDtoIsNotNull() {
            addCriterion("TIPO_DTO is not null");
            return (Criteria) this;
        }

        public Criteria andTipoDtoEqualTo(Long value) {
            addCriterion("TIPO_DTO =", value, "tipoDto");
            return (Criteria) this;
        }

        public Criteria andTipoDtoNotEqualTo(Long value) {
            addCriterion("TIPO_DTO <>", value, "tipoDto");
            return (Criteria) this;
        }

        public Criteria andTipoDtoGreaterThan(Long value) {
            addCriterion("TIPO_DTO >", value, "tipoDto");
            return (Criteria) this;
        }

        public Criteria andTipoDtoGreaterThanOrEqualTo(Long value) {
            addCriterion("TIPO_DTO >=", value, "tipoDto");
            return (Criteria) this;
        }

        public Criteria andTipoDtoLessThan(Long value) {
            addCriterion("TIPO_DTO <", value, "tipoDto");
            return (Criteria) this;
        }

        public Criteria andTipoDtoLessThanOrEqualTo(Long value) {
            addCriterion("TIPO_DTO <=", value, "tipoDto");
            return (Criteria) this;
        }

        public Criteria andTipoDtoIn(List<Long> values) {
            addCriterion("TIPO_DTO in", values, "tipoDto");
            return (Criteria) this;
        }

        public Criteria andTipoDtoNotIn(List<Long> values) {
            addCriterion("TIPO_DTO not in", values, "tipoDto");
            return (Criteria) this;
        }

        public Criteria andTipoDtoBetween(Long value1, Long value2) {
            addCriterion("TIPO_DTO between", value1, value2, "tipoDto");
            return (Criteria) this;
        }

        public Criteria andTipoDtoNotBetween(Long value1, Long value2) {
            addCriterion("TIPO_DTO not between", value1, value2, "tipoDto");
            return (Criteria) this;
        }

        public Criteria andCodColectivoIsNull() {
            addCriterion("COD_COLECTIVO is null");
            return (Criteria) this;
        }

        public Criteria andCodColectivoIsNotNull() {
            addCriterion("COD_COLECTIVO is not null");
            return (Criteria) this;
        }

        public Criteria andCodColectivoEqualTo(String value) {
            addCriterion("COD_COLECTIVO =", value, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoNotEqualTo(String value) {
            addCriterion("COD_COLECTIVO <>", value, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoGreaterThan(String value) {
            addCriterion("COD_COLECTIVO >", value, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoGreaterThanOrEqualTo(String value) {
            addCriterion("COD_COLECTIVO >=", value, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoLessThan(String value) {
            addCriterion("COD_COLECTIVO <", value, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoLessThanOrEqualTo(String value) {
            addCriterion("COD_COLECTIVO <=", value, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoLike(String value) {
            addCriterion("COD_COLECTIVO like", value, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoNotLike(String value) {
            addCriterion("COD_COLECTIVO not like", value, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoIn(List<String> values) {
            addCriterion("COD_COLECTIVO in", values, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoNotIn(List<String> values) {
            addCriterion("COD_COLECTIVO not in", values, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoBetween(String value1, String value2) {
            addCriterion("COD_COLECTIVO between", value1, value2, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andCodColectivoNotBetween(String value1, String value2) {
            addCriterion("COD_COLECTIVO not between", value1, value2, "codColectivo");
            return (Criteria) this;
        }

        public Criteria andExclusivaIsNull() {
            addCriterion("EXCLUSIVA is null");
            return (Criteria) this;
        }

        public Criteria andExclusivaIsNotNull() {
            addCriterion("EXCLUSIVA is not null");
            return (Criteria) this;
        }

        public Criteria andExclusivaEqualTo(String value) {
            addCriterion("EXCLUSIVA =", value, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaNotEqualTo(String value) {
            addCriterion("EXCLUSIVA <>", value, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaGreaterThan(String value) {
            addCriterion("EXCLUSIVA >", value, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaGreaterThanOrEqualTo(String value) {
            addCriterion("EXCLUSIVA >=", value, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaLessThan(String value) {
            addCriterion("EXCLUSIVA <", value, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaLessThanOrEqualTo(String value) {
            addCriterion("EXCLUSIVA <=", value, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaLike(String value) {
            addCriterion("EXCLUSIVA like", value, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaNotLike(String value) {
            addCriterion("EXCLUSIVA not like", value, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaIn(List<String> values) {
            addCriterion("EXCLUSIVA in", values, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaNotIn(List<String> values) {
            addCriterion("EXCLUSIVA not in", values, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaBetween(String value1, String value2) {
            addCriterion("EXCLUSIVA between", value1, value2, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andExclusivaNotBetween(String value1, String value2) {
            addCriterion("EXCLUSIVA not between", value1, value2, "exclusiva");
            return (Criteria) this;
        }

        public Criteria andCodCuponIsNull() {
            addCriterion("COD_CUPON is null");
            return (Criteria) this;
        }

        public Criteria andCodCuponIsNotNull() {
            addCriterion("COD_CUPON is not null");
            return (Criteria) this;
        }

        public Criteria andCodCuponEqualTo(String value) {
            addCriterion("COD_CUPON =", value, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponNotEqualTo(String value) {
            addCriterion("COD_CUPON <>", value, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponGreaterThan(String value) {
            addCriterion("COD_CUPON >", value, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponGreaterThanOrEqualTo(String value) {
            addCriterion("COD_CUPON >=", value, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponLessThan(String value) {
            addCriterion("COD_CUPON <", value, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponLessThanOrEqualTo(String value) {
            addCriterion("COD_CUPON <=", value, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponLike(String value) {
            addCriterion("COD_CUPON like", value, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponNotLike(String value) {
            addCriterion("COD_CUPON not like", value, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponIn(List<String> values) {
            addCriterion("COD_CUPON in", values, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponNotIn(List<String> values) {
            addCriterion("COD_CUPON not in", values, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponBetween(String value1, String value2) {
            addCriterion("COD_CUPON between", value1, value2, "codCupon");
            return (Criteria) this;
        }

        public Criteria andCodCuponNotBetween(String value1, String value2) {
            addCriterion("COD_CUPON not between", value1, value2, "codCupon");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasIsNull() {
            addCriterion("APLICA_A_TARIFAS is null");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasIsNotNull() {
            addCriterion("APLICA_A_TARIFAS is not null");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasEqualTo(String value) {
            addCriterion("APLICA_A_TARIFAS =", value, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasNotEqualTo(String value) {
            addCriterion("APLICA_A_TARIFAS <>", value, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasGreaterThan(String value) {
            addCriterion("APLICA_A_TARIFAS >", value, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasGreaterThanOrEqualTo(String value) {
            addCriterion("APLICA_A_TARIFAS >=", value, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasLessThan(String value) {
            addCriterion("APLICA_A_TARIFAS <", value, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasLessThanOrEqualTo(String value) {
            addCriterion("APLICA_A_TARIFAS <=", value, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasLike(String value) {
            addCriterion("APLICA_A_TARIFAS like", value, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasNotLike(String value) {
            addCriterion("APLICA_A_TARIFAS not like", value, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasIn(List<String> values) {
            addCriterion("APLICA_A_TARIFAS in", values, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasNotIn(List<String> values) {
            addCriterion("APLICA_A_TARIFAS not in", values, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasBetween(String value1, String value2) {
            addCriterion("APLICA_A_TARIFAS between", value1, value2, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasNotBetween(String value1, String value2) {
            addCriterion("APLICA_A_TARIFAS not between", value1, value2, "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosIsNull() {
            addCriterion("CODTAR_PRECIOS is null");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosIsNotNull() {
            addCriterion("CODTAR_PRECIOS is not null");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosEqualTo(String value) {
            addCriterion("CODTAR_PRECIOS =", value, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosNotEqualTo(String value) {
            addCriterion("CODTAR_PRECIOS <>", value, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosGreaterThan(String value) {
            addCriterion("CODTAR_PRECIOS >", value, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosGreaterThanOrEqualTo(String value) {
            addCriterion("CODTAR_PRECIOS >=", value, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosLessThan(String value) {
            addCriterion("CODTAR_PRECIOS <", value, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosLessThanOrEqualTo(String value) {
            addCriterion("CODTAR_PRECIOS <=", value, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosLike(String value) {
            addCriterion("CODTAR_PRECIOS like", value, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosNotLike(String value) {
            addCriterion("CODTAR_PRECIOS not like", value, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosIn(List<String> values) {
            addCriterion("CODTAR_PRECIOS in", values, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosNotIn(List<String> values) {
            addCriterion("CODTAR_PRECIOS not in", values, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosBetween(String value1, String value2) {
            addCriterion("CODTAR_PRECIOS between", value1, value2, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosNotBetween(String value1, String value2) {
            addCriterion("CODTAR_PRECIOS not between", value1, value2, "codtarPrecios");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andCodtarLikeInsensitive(String value) {
            addCriterion("upper(CODTAR) like", value.toUpperCase(), "codtar");
            return (Criteria) this;
        }

        public Criteria andDescripcionLikeInsensitive(String value) {
            addCriterion("upper(DESCRIPCION) like", value.toUpperCase(), "descripcion");
            return (Criteria) this;
        }

        public Criteria andSoloFidelizacionLikeInsensitive(String value) {
            addCriterion("upper(SOLO_FIDELIZACION) like", value.toUpperCase(), "soloFidelizacion");
            return (Criteria) this;
        }

        public Criteria andCodColectivoLikeInsensitive(String value) {
            addCriterion("upper(COD_COLECTIVO) like", value.toUpperCase(), "codColectivo");
            return (Criteria) this;
        }

        public Criteria andExclusivaLikeInsensitive(String value) {
            addCriterion("upper(EXCLUSIVA) like", value.toUpperCase(), "exclusiva");
            return (Criteria) this;
        }

        public Criteria andCodCuponLikeInsensitive(String value) {
            addCriterion("upper(COD_CUPON) like", value.toUpperCase(), "codCupon");
            return (Criteria) this;
        }

        public Criteria andAplicaATarifasLikeInsensitive(String value) {
            addCriterion("upper(APLICA_A_TARIFAS) like", value.toUpperCase(), "aplicaATarifas");
            return (Criteria) this;
        }

        public Criteria andCodtarPreciosLikeInsensitive(String value) {
            addCriterion("upper(CODTAR_PRECIOS) like", value.toUpperCase(), "codtarPrecios");
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