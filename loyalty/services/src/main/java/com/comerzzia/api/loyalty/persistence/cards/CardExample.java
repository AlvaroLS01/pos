package com.comerzzia.api.loyalty.persistence.cards;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiInstance;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public class CardExample extends MultiInstance {
	private static final long serialVersionUID = 1L;
	
    public static final String ORDER_BY_ID_TARJETA = "ID_TARJETA";

    public static final String ORDER_BY_ID_TARJETA_DESC = "ID_TARJETA DESC";

    public static final String ORDER_BY_NUMERO_TARJETA = "NUMERO_TARJETA";

    public static final String ORDER_BY_NUMERO_TARJETA_DESC = "NUMERO_TARJETA DESC";

    public static final String ORDER_BY_FECHA_EMISION = "FECHA_EMISION";

    public static final String ORDER_BY_FECHA_EMISION_DESC = "FECHA_EMISION DESC";

    public static final String ORDER_BY_ID_CUENTA_TARJETA = "ID_CUENTA_TARJETA";

    public static final String ORDER_BY_ID_CUENTA_TARJETA_DESC = "ID_CUENTA_TARJETA DESC";

    public static final String ORDER_BY_FECHA_ACTIVACION = "FECHA_ACTIVACION";

    public static final String ORDER_BY_FECHA_ACTIVACION_DESC = "FECHA_ACTIVACION DESC";

    public static final String ORDER_BY_FECHA_ULTIMO_USO = "FECHA_ULTIMO_USO";

    public static final String ORDER_BY_FECHA_ULTIMO_USO_DESC = "FECHA_ULTIMO_USO DESC";

    public static final String ORDER_BY_FECHA_BAJA = "FECHA_BAJA";

    public static final String ORDER_BY_FECHA_BAJA_DESC = "FECHA_BAJA DESC";

    public static final String ORDER_BY_ID_FIDELIZADO = "ID_FIDELIZADO";

    public static final String ORDER_BY_ID_FIDELIZADO_DESC = "ID_FIDELIZADO DESC";

    public static final String ORDER_BY_FECHA_ASIGNACION_FIDELIZADO = "FECHA_ASIGNACION_FIDELIZADO";

    public static final String ORDER_BY_FECHA_ASIGNACION_FIDELIZADO_DESC = "FECHA_ASIGNACION_FIDELIZADO DESC";

    public static final String ORDER_BY_CODTIPOTARJ = "CODTIPOTARJ";

    public static final String ORDER_BY_CODTIPOTARJ_DESC = "CODTIPOTARJ DESC";

    public static final String ORDER_BY_SALDO = "SALDO";

    public static final String ORDER_BY_SALDO_DESC = "SALDO DESC";

    public static final String ORDER_BY_SALDO_PROVISIONAL = "SALDO_PROVISIONAL";

    public static final String ORDER_BY_SALDO_PROVISIONAL_DESC = "SALDO_PROVISIONAL DESC";

    public static final String ORDER_BY_ESTADO = "ESTADO";

    public static final String ORDER_BY_ESTADO_DESC = "ESTADO DESC";

    public static final String ORDER_BY_FECHA_ACTUALIZACION = "FECHA_ACTUALIZACION";

    public static final String ORDER_BY_FECHA_ACTUALIZACION_DESC = "FECHA_ACTUALIZACION DESC";

    public static final String ORDER_BY_DESTIPOTARJ = "DESTIPOTARJ";

    public static final String ORDER_BY_DESTIPOTARJ_DESC = "DESTIPOTARJ DESC";

    public static final String ORDER_BY_PERMITE_VINCULAR = "PERMITE_VINCULAR";

    public static final String ORDER_BY_PERMITE_VINCULAR_DESC = "PERMITE_VINCULAR DESC";

    public static final String ORDER_BY_NOMBRE_FIDELIZADO = "NOMBRE_FIDELIZADO";

    public static final String ORDER_BY_NOMBRE_FIDELIZADO_DESC = "NOMBRE_FIDELIZADO DESC";

    public static final String ORDER_BY_APELLIDOS_FIDELIZADO = "APELLIDOS_FIDELIZADO";

    public static final String ORDER_BY_APELLIDOS_FIDELIZADO_DESC = "APELLIDOS_FIDELIZADO DESC";

    public static final String ORDER_BY_DOMICILIO_FIDELIZADO = "DOMICILIO_FIDELIZADO";

    public static final String ORDER_BY_DOMICILIO_FIDELIZADO_DESC = "DOMICILIO_FIDELIZADO DESC";

    public static final String ORDER_BY_POBLACION_FIDELIZADO = "POBLACION_FIDELIZADO";

    public static final String ORDER_BY_POBLACION_FIDELIZADO_DESC = "POBLACION_FIDELIZADO DESC";

    public static final String ORDER_BY_PROVINCIA_FIDELIZADO = "PROVINCIA_FIDELIZADO";

    public static final String ORDER_BY_PROVINCIA_FIDELIZADO_DESC = "PROVINCIA_FIDELIZADO DESC";

    public static final String ORDER_BY_LOCALIDAD_FIDELIZADO = "LOCALIDAD_FIDELIZADO";

    public static final String ORDER_BY_LOCALIDAD_FIDELIZADO_DESC = "LOCALIDAD_FIDELIZADO DESC";

    public static final String ORDER_BY_CP_FIDELIZADO = "CP_FIDELIZADO";

    public static final String ORDER_BY_CP_FIDELIZADO_DESC = "CP_FIDELIZADO DESC";

    public static final String ORDER_BY_CODPAIS_FIDELIZADO = "CODPAIS_FIDELIZADO";

    public static final String ORDER_BY_CODPAIS_FIDELIZADO_DESC = "CODPAIS_FIDELIZADO DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CardExample(IDatosSesion datosSesion) {
    	super(datosSesion);
        oredCriteria = new ArrayList<Criteria>();
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
            criteria = new ArrayList<Criterion>();
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
            if (condition != null) {
                criteria.add(new Criterion(condition));
            }
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value != null) {
                criteria.add(new Criterion(condition, value));
            }
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 != null && value2 != null) {
                criteria.add(new Criterion(condition, value1, value2));
            }
        }

        public Criteria andIdTarjetaIsNull() {
            addCriterion("ID_TARJETA is null");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaIsNotNull() {
            addCriterion("ID_TARJETA is not null");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaEqualTo(Long value) {
            addCriterion("ID_TARJETA =", value, "idTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaNotEqualTo(Long value) {
            addCriterion("ID_TARJETA <>", value, "idTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaGreaterThan(Long value) {
            addCriterion("ID_TARJETA >", value, "idTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_TARJETA >=", value, "idTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaLessThan(Long value) {
            addCriterion("ID_TARJETA <", value, "idTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaLessThanOrEqualTo(Long value) {
            addCriterion("ID_TARJETA <=", value, "idTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaIn(List<Long> values) {
            addCriterion("ID_TARJETA in", values, "idTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaNotIn(List<Long> values) {
            addCriterion("ID_TARJETA not in", values, "idTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaBetween(Long value1, Long value2) {
            addCriterion("ID_TARJETA between", value1, value2, "idTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdTarjetaNotBetween(Long value1, Long value2) {
            addCriterion("ID_TARJETA not between", value1, value2, "idTarjeta");
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

        public Criteria andFechaEmisionIsNull() {
            addCriterion("FECHA_EMISION is null");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionIsNotNull() {
            addCriterion("FECHA_EMISION is not null");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionEqualTo(Date value) {
            addCriterion("FECHA_EMISION =", value, "fechaEmision");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionNotEqualTo(Date value) {
            addCriterion("FECHA_EMISION <>", value, "fechaEmision");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionGreaterThan(Date value) {
            addCriterion("FECHA_EMISION >", value, "fechaEmision");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_EMISION >=", value, "fechaEmision");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionLessThan(Date value) {
            addCriterion("FECHA_EMISION <", value, "fechaEmision");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_EMISION <=", value, "fechaEmision");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionIn(List<Date> values) {
            addCriterion("FECHA_EMISION in", values, "fechaEmision");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionNotIn(List<Date> values) {
            addCriterion("FECHA_EMISION not in", values, "fechaEmision");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionBetween(Date value1, Date value2) {
            addCriterion("FECHA_EMISION between", value1, value2, "fechaEmision");
            return (Criteria) this;
        }

        public Criteria andFechaEmisionNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_EMISION not between", value1, value2, "fechaEmision");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaIsNull() {
            addCriterion("ID_CUENTA_TARJETA is null");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaIsNotNull() {
            addCriterion("ID_CUENTA_TARJETA is not null");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaEqualTo(Long value) {
            addCriterion("ID_CUENTA_TARJETA =", value, "idCuentaTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaNotEqualTo(Long value) {
            addCriterion("ID_CUENTA_TARJETA <>", value, "idCuentaTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaGreaterThan(Long value) {
            addCriterion("ID_CUENTA_TARJETA >", value, "idCuentaTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_CUENTA_TARJETA >=", value, "idCuentaTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaLessThan(Long value) {
            addCriterion("ID_CUENTA_TARJETA <", value, "idCuentaTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaLessThanOrEqualTo(Long value) {
            addCriterion("ID_CUENTA_TARJETA <=", value, "idCuentaTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaIn(List<Long> values) {
            addCriterion("ID_CUENTA_TARJETA in", values, "idCuentaTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaNotIn(List<Long> values) {
            addCriterion("ID_CUENTA_TARJETA not in", values, "idCuentaTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaBetween(Long value1, Long value2) {
            addCriterion("ID_CUENTA_TARJETA between", value1, value2, "idCuentaTarjeta");
            return (Criteria) this;
        }

        public Criteria andIdCuentaTarjetaNotBetween(Long value1, Long value2) {
            addCriterion("ID_CUENTA_TARJETA not between", value1, value2, "idCuentaTarjeta");
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

        public Criteria andFechaUltimoUsoIsNull() {
            addCriterion("FECHA_ULTIMO_USO is null");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoIsNotNull() {
            addCriterion("FECHA_ULTIMO_USO is not null");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_USO =", value, "fechaUltimoUso");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoNotEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_USO <>", value, "fechaUltimoUso");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoGreaterThan(Date value) {
            addCriterion("FECHA_ULTIMO_USO >", value, "fechaUltimoUso");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_USO >=", value, "fechaUltimoUso");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoLessThan(Date value) {
            addCriterion("FECHA_ULTIMO_USO <", value, "fechaUltimoUso");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_USO <=", value, "fechaUltimoUso");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoIn(List<Date> values) {
            addCriterion("FECHA_ULTIMO_USO in", values, "fechaUltimoUso");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoNotIn(List<Date> values) {
            addCriterion("FECHA_ULTIMO_USO not in", values, "fechaUltimoUso");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoBetween(Date value1, Date value2) {
            addCriterion("FECHA_ULTIMO_USO between", value1, value2, "fechaUltimoUso");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoUsoNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_ULTIMO_USO not between", value1, value2, "fechaUltimoUso");
            return (Criteria) this;
        }

        public Criteria andFechaBajaIsNull() {
            addCriterion("FECHA_BAJA is null");
            return (Criteria) this;
        }

        public Criteria andFechaBajaIsNotNull() {
            addCriterion("FECHA_BAJA is not null");
            return (Criteria) this;
        }

        public Criteria andFechaBajaEqualTo(Date value) {
            addCriterion("FECHA_BAJA =", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaNotEqualTo(Date value) {
            addCriterion("FECHA_BAJA <>", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaGreaterThan(Date value) {
            addCriterion("FECHA_BAJA >", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_BAJA >=", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaLessThan(Date value) {
            addCriterion("FECHA_BAJA <", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_BAJA <=", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaIn(List<Date> values) {
            addCriterion("FECHA_BAJA in", values, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaNotIn(List<Date> values) {
            addCriterion("FECHA_BAJA not in", values, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaBetween(Date value1, Date value2) {
            addCriterion("FECHA_BAJA between", value1, value2, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_BAJA not between", value1, value2, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoIsNull() {
            addCriterion("ID_FIDELIZADO is null");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoIsNotNull() {
            addCriterion("ID_FIDELIZADO is not null");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO =", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoNotEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO <>", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoGreaterThan(Long value) {
            addCriterion("ID_FIDELIZADO >", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO >=", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoLessThan(Long value) {
            addCriterion("ID_FIDELIZADO <", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoLessThanOrEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO <=", value, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoIn(List<Long> values) {
            addCriterion("ID_FIDELIZADO in", values, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoNotIn(List<Long> values) {
            addCriterion("ID_FIDELIZADO not in", values, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoBetween(Long value1, Long value2) {
            addCriterion("ID_FIDELIZADO between", value1, value2, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoNotBetween(Long value1, Long value2) {
            addCriterion("ID_FIDELIZADO not between", value1, value2, "idFidelizado");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoIsNull() {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO is null");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoIsNotNull() {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO is not null");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoEqualTo(Date value) {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO =", value, "fechaAsignacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoNotEqualTo(Date value) {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO <>", value, "fechaAsignacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoGreaterThan(Date value) {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO >", value, "fechaAsignacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO >=", value, "fechaAsignacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoLessThan(Date value) {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO <", value, "fechaAsignacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO <=", value, "fechaAsignacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoIn(List<Date> values) {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO in", values, "fechaAsignacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoNotIn(List<Date> values) {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO not in", values, "fechaAsignacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoBetween(Date value1, Date value2) {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO between", value1, value2, "fechaAsignacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andFechaAsignacionFidelizadoNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_ASIGNACION_FIDELIZADO not between", value1, value2, "fechaAsignacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjIsNull() {
            addCriterion("CODTIPOTARJ is null");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjIsNotNull() {
            addCriterion("CODTIPOTARJ is not null");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjEqualTo(String value) {
            addCriterion("CODTIPOTARJ =", value, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjNotEqualTo(String value) {
            addCriterion("CODTIPOTARJ <>", value, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjGreaterThan(String value) {
            addCriterion("CODTIPOTARJ >", value, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjGreaterThanOrEqualTo(String value) {
            addCriterion("CODTIPOTARJ >=", value, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjLessThan(String value) {
            addCriterion("CODTIPOTARJ <", value, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjLessThanOrEqualTo(String value) {
            addCriterion("CODTIPOTARJ <=", value, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjLike(String value) {
            addCriterion("CODTIPOTARJ like", value, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjNotLike(String value) {
            addCriterion("CODTIPOTARJ not like", value, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjIn(List<String> values) {
            addCriterion("CODTIPOTARJ in", values, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjNotIn(List<String> values) {
            addCriterion("CODTIPOTARJ not in", values, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjBetween(String value1, String value2) {
            addCriterion("CODTIPOTARJ between", value1, value2, "codTipoTarj");
            return (Criteria) this;
        }

        public Criteria andCodTipoTarjNotBetween(String value1, String value2) {
            addCriterion("CODTIPOTARJ not between", value1, value2, "codTipoTarj");
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

        public Criteria andCodTipoTarjLikeInsensitive(String value) {
            addCriterion("upper(CODTIPOTARJ) like", value.toUpperCase(), "codTipoTarj");
            return (Criteria) this;
        }
        
        public Criteria andNombreFidelizadoLike(String value) {
            addCriterion("NOMBRE_FIDELIZADO like", value, "nombreFidelizado");
            return (Criteria) this;
        }
        
        public Criteria andApellidosFidelizadoLike(String value) {
            addCriterion("APELLIDOS_FIDELIZADO like", value, "apellidosFidelizado");
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