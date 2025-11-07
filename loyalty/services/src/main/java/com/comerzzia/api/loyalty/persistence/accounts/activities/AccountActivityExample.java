package com.comerzzia.api.loyalty.persistence.accounts.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiInstance;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public class AccountActivityExample extends MultiInstance{

	private static final long serialVersionUID = -7275133276864160604L;

	public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_ID_CUENTA_MOVIMIENTO = "ID_CUENTA_MOVIMIENTO";

    public static final String ORDER_BY_ID_CUENTA_MOVIMIENTO_DESC = "ID_CUENTA_MOVIMIENTO DESC";

    public static final String ORDER_BY_ID_CUENTA_TARJETA = "ID_CUENTA_TARJETA";

    public static final String ORDER_BY_ID_CUENTA_TARJETA_DESC = "ID_CUENTA_TARJETA DESC";

    public static final String ORDER_BY_FECHA = "FECHA";

    public static final String ORDER_BY_FECHA_DESC = "FECHA DESC";

    public static final String ORDER_BY_ID_TARJETA = "ID_TARJETA";

    public static final String ORDER_BY_ID_TARJETA_DESC = "ID_TARJETA DESC";

    public static final String ORDER_BY_ID_USUARIO = "ID_USUARIO";

    public static final String ORDER_BY_ID_USUARIO_DESC = "ID_USUARIO DESC";

    public static final String ORDER_BY_DOCUMENTO = "DOCUMENTO";

    public static final String ORDER_BY_DOCUMENTO_DESC = "DOCUMENTO DESC";

    public static final String ORDER_BY_CONCEPTO = "CONCEPTO";

    public static final String ORDER_BY_CONCEPTO_DESC = "CONCEPTO DESC";

    public static final String ORDER_BY_ENTRADA = "ENTRADA";

    public static final String ORDER_BY_ENTRADA_DESC = "ENTRADA DESC";

    public static final String ORDER_BY_SALIDA = "SALIDA";

    public static final String ORDER_BY_SALIDA_DESC = "SALIDA DESC";

    public static final String ORDER_BY_FECHA_PROCESO = "FECHA_PROCESO";

    public static final String ORDER_BY_FECHA_PROCESO_DESC = "FECHA_PROCESO DESC";

    public static final String ORDER_BY_ESTADO_MOVIMIENTO = "ESTADO_MOVIMIENTO";

    public static final String ORDER_BY_ESTADO_MOVIMIENTO_DESC = "ESTADO_MOVIMIENTO DESC";

    public static final String ORDER_BY_UID_TRANSACCION = "UID_TRANSACCION";

    public static final String ORDER_BY_UID_TRANSACCION_DESC = "UID_TRANSACCION DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AccountActivityExample(IDatosSesion datosSesion) {
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

        public Criteria andUidInstanciaIsNull() {
            addCriterion("UID_INSTANCIA is null");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaIsNotNull() {
            addCriterion("UID_INSTANCIA is not null");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaEqualTo(String value) {
            addCriterion("UID_INSTANCIA =", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaTransaccionEqualTo(String value) {
            addCriterion("MOVIMIENTO.UID_INSTANCIA =", value, "uidInstancia");
            return (Criteria) this;
        }
        
        public Criteria andMovimientoUidInstanciaEqualTo(String value) {
            addCriterion("MOVIMIENTO.UID_INSTANCIA =", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaNotEqualTo(String value) {
            addCriterion("UID_INSTANCIA <>", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaGreaterThan(String value) {
            addCriterion("UID_INSTANCIA >", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaGreaterThanOrEqualTo(String value) {
            addCriterion("UID_INSTANCIA >=", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLessThan(String value) {
            addCriterion("UID_INSTANCIA <", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLessThanOrEqualTo(String value) {
            addCriterion("UID_INSTANCIA <=", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLike(String value) {
            addCriterion("UID_INSTANCIA like", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaNotLike(String value) {
            addCriterion("UID_INSTANCIA not like", value, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaIn(List<String> values) {
            addCriterion("UID_INSTANCIA in", values, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaNotIn(List<String> values) {
            addCriterion("UID_INSTANCIA not in", values, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaBetween(String value1, String value2) {
            addCriterion("UID_INSTANCIA between", value1, value2, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaNotBetween(String value1, String value2) {
            addCriterion("UID_INSTANCIA not between", value1, value2, "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoIsNull() {
            addCriterion("ID_CUENTA_MOVIMIENTO is null");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoIsNotNull() {
            addCriterion("ID_CUENTA_MOVIMIENTO is not null");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoEqualTo(Long value) {
            addCriterion("ID_CUENTA_MOVIMIENTO =", value, "idCuentaMovimiento");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoNotEqualTo(Long value) {
            addCriterion("ID_CUENTA_MOVIMIENTO <>", value, "idCuentaMovimiento");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoGreaterThan(Long value) {
            addCriterion("ID_CUENTA_MOVIMIENTO >", value, "idCuentaMovimiento");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_CUENTA_MOVIMIENTO >=", value, "idCuentaMovimiento");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoLessThan(Long value) {
            addCriterion("ID_CUENTA_MOVIMIENTO <", value, "idCuentaMovimiento");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoLessThanOrEqualTo(Long value) {
            addCriterion("ID_CUENTA_MOVIMIENTO <=", value, "idCuentaMovimiento");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoIn(List<Long> values) {
            addCriterion("ID_CUENTA_MOVIMIENTO in", values, "idCuentaMovimiento");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoNotIn(List<Long> values) {
            addCriterion("ID_CUENTA_MOVIMIENTO not in", values, "idCuentaMovimiento");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoBetween(Long value1, Long value2) {
            addCriterion("ID_CUENTA_MOVIMIENTO between", value1, value2, "idCuentaMovimiento");
            return (Criteria) this;
        }

        public Criteria andIdCuentaMovimientoNotBetween(Long value1, Long value2) {
            addCriterion("ID_CUENTA_MOVIMIENTO not between", value1, value2, "idCuentaMovimiento");
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
        
        public Criteria andMovimientoIdCuentaTarjetaEqualTo(Long value) {
            addCriterion("MOVIMIENTO.ID_CUENTA_TARJETA =", value, "idCuentaTarjeta");
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

        public Criteria andMovimientoFechaGreaterThanOrEqualTo(Date value) {
            addCriterion("MOVIMIENTO.FECHA >=", value, "fecha");
            return (Criteria) this;
        }
        
        public Criteria andFechaGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA >=", value, "fecha");
            return (Criteria) this;
        }
        
        public Criteria andFechaMovimientoGreaterThanOrEqualTo(Date value) {
            addCriterion("MOVIMIENTO.FECHA >=", value, "fecha");
            return (Criteria) this;
        }

        public Criteria andFechaLessThan(Date value) {
            addCriterion("FECHA <", value, "fecha");
            return (Criteria) this;
        }

        public Criteria andMovimientoFechaLessThanOrEqualTo(Date value) {
            addCriterion("MOVIMIENTO.FECHA <=", value, "fecha");
            return (Criteria) this;
        }
        
        public Criteria andFechaLessThanOrEqualTo(Date value) {
            addCriterion("FECHA <=", value, "fecha");
            return (Criteria) this;
        }
        
        public Criteria andFechaMovimientoLessThanOrEqualTo(Date value) {
            addCriterion("MOVIMIENTO.FECHA <=", value, "fecha");
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
        
        public Criteria andMovimientoIdTarjetaEqualTo(Long value) {
            addCriterion("MOVIMIENTO.ID_TARJETA =", value, "idTarjeta");
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

        public Criteria andIdUsuarioIsNull() {
            addCriterion("ID_USUARIO is null");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioIsNotNull() {
            addCriterion("ID_USUARIO is not null");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioEqualTo(Long value) {
            addCriterion("ID_USUARIO =", value, "idUsuario");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioNotEqualTo(Long value) {
            addCriterion("ID_USUARIO <>", value, "idUsuario");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioGreaterThan(Long value) {
            addCriterion("ID_USUARIO >", value, "idUsuario");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_USUARIO >=", value, "idUsuario");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioLessThan(Long value) {
            addCriterion("ID_USUARIO <", value, "idUsuario");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioLessThanOrEqualTo(Long value) {
            addCriterion("ID_USUARIO <=", value, "idUsuario");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioIn(List<Long> values) {
            addCriterion("ID_USUARIO in", values, "idUsuario");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioNotIn(List<Long> values) {
            addCriterion("ID_USUARIO not in", values, "idUsuario");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioBetween(Long value1, Long value2) {
            addCriterion("ID_USUARIO between", value1, value2, "idUsuario");
            return (Criteria) this;
        }

        public Criteria andIdUsuarioNotBetween(Long value1, Long value2) {
            addCriterion("ID_USUARIO not between", value1, value2, "idUsuario");
            return (Criteria) this;
        }

        public Criteria andDocumentoIsNull() {
            addCriterion("DOCUMENTO is null");
            return (Criteria) this;
        }

        public Criteria andDocumentoIsNotNull() {
            addCriterion("DOCUMENTO is not null");
            return (Criteria) this;
        }

        public Criteria andDocumentoEqualTo(String value) {
            addCriterion("DOCUMENTO =", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoNotEqualTo(String value) {
            addCriterion("DOCUMENTO <>", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoGreaterThan(String value) {
            addCriterion("DOCUMENTO >", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoGreaterThanOrEqualTo(String value) {
            addCriterion("DOCUMENTO >=", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoLessThan(String value) {
            addCriterion("DOCUMENTO <", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoLessThanOrEqualTo(String value) {
            addCriterion("DOCUMENTO <=", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoLike(String value) {
            addCriterion("DOCUMENTO like", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoNotLike(String value) {
            addCriterion("DOCUMENTO not like", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoIn(List<String> values) {
            addCriterion("DOCUMENTO in", values, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoNotIn(List<String> values) {
            addCriterion("DOCUMENTO not in", values, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoBetween(String value1, String value2) {
            addCriterion("DOCUMENTO between", value1, value2, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoNotBetween(String value1, String value2) {
            addCriterion("DOCUMENTO not between", value1, value2, "documento");
            return (Criteria) this;
        }

        public Criteria andConceptoIsNull() {
            addCriterion("CONCEPTO is null");
            return (Criteria) this;
        }

        public Criteria andConceptoIsNotNull() {
            addCriterion("CONCEPTO is not null");
            return (Criteria) this;
        }

        public Criteria andConceptoEqualTo(String value) {
            addCriterion("CONCEPTO =", value, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoNotEqualTo(String value) {
            addCriterion("CONCEPTO <>", value, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoGreaterThan(String value) {
            addCriterion("CONCEPTO >", value, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoGreaterThanOrEqualTo(String value) {
            addCriterion("CONCEPTO >=", value, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoLessThan(String value) {
            addCriterion("CONCEPTO <", value, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoLessThanOrEqualTo(String value) {
            addCriterion("CONCEPTO <=", value, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoLike(String value) {
            addCriterion("CONCEPTO like", value, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoNotLike(String value) {
            addCriterion("CONCEPTO not like", value, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoIn(List<String> values) {
            addCriterion("CONCEPTO in", values, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoNotIn(List<String> values) {
            addCriterion("CONCEPTO not in", values, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoBetween(String value1, String value2) {
            addCriterion("CONCEPTO between", value1, value2, "concepto");
            return (Criteria) this;
        }

        public Criteria andConceptoNotBetween(String value1, String value2) {
            addCriterion("CONCEPTO not between", value1, value2, "concepto");
            return (Criteria) this;
        }

        public Criteria andEntradaIsNull() {
            addCriterion("ENTRADA is null");
            return (Criteria) this;
        }

        public Criteria andEntradaIsNotNull() {
            addCriterion("ENTRADA is not null");
            return (Criteria) this;
        }

        public Criteria andEntradaEqualTo(Double value) {
            addCriterion("ENTRADA =", value, "entrada");
            return (Criteria) this;
        }

        public Criteria andEntradaNotEqualTo(Double value) {
            addCriterion("ENTRADA <>", value, "entrada");
            return (Criteria) this;
        }

        public Criteria andEntradaGreaterThan(Double value) {
            addCriterion("ENTRADA >", value, "entrada");
            return (Criteria) this;
        }

        public Criteria andEntradaGreaterThanOrEqualTo(Double value) {
            addCriterion("ENTRADA >=", value, "entrada");
            return (Criteria) this;
        }

        public Criteria andEntradaLessThan(Double value) {
            addCriterion("ENTRADA <", value, "entrada");
            return (Criteria) this;
        }

        public Criteria andEntradaLessThanOrEqualTo(Double value) {
            addCriterion("ENTRADA <=", value, "entrada");
            return (Criteria) this;
        }

        public Criteria andEntradaIn(List<Double> values) {
            addCriterion("ENTRADA in", values, "entrada");
            return (Criteria) this;
        }

        public Criteria andEntradaNotIn(List<Double> values) {
            addCriterion("ENTRADA not in", values, "entrada");
            return (Criteria) this;
        }

        public Criteria andEntradaBetween(Double value1, Double value2) {
            addCriterion("ENTRADA between", value1, value2, "entrada");
            return (Criteria) this;
        }

        public Criteria andEntradaNotBetween(Double value1, Double value2) {
            addCriterion("ENTRADA not between", value1, value2, "entrada");
            return (Criteria) this;
        }

        public Criteria andSalidaIsNull() {
            addCriterion("SALIDA is null");
            return (Criteria) this;
        }

        public Criteria andSalidaIsNotNull() {
            addCriterion("SALIDA is not null");
            return (Criteria) this;
        }

        public Criteria andSalidaEqualTo(Double value) {
            addCriterion("SALIDA =", value, "salida");
            return (Criteria) this;
        }

        public Criteria andSalidaNotEqualTo(Double value) {
            addCriterion("SALIDA <>", value, "salida");
            return (Criteria) this;
        }

        public Criteria andSalidaGreaterThan(Double value) {
            addCriterion("SALIDA >", value, "salida");
            return (Criteria) this;
        }

        public Criteria andSalidaGreaterThanOrEqualTo(Double value) {
            addCriterion("SALIDA >=", value, "salida");
            return (Criteria) this;
        }

        public Criteria andSalidaLessThan(Double value) {
            addCriterion("SALIDA <", value, "salida");
            return (Criteria) this;
        }

        public Criteria andSalidaLessThanOrEqualTo(Double value) {
            addCriterion("SALIDA <=", value, "salida");
            return (Criteria) this;
        }

        public Criteria andSalidaIn(List<Double> values) {
            addCriterion("SALIDA in", values, "salida");
            return (Criteria) this;
        }

        public Criteria andSalidaNotIn(List<Double> values) {
            addCriterion("SALIDA not in", values, "salida");
            return (Criteria) this;
        }

        public Criteria andSalidaBetween(Double value1, Double value2) {
            addCriterion("SALIDA between", value1, value2, "salida");
            return (Criteria) this;
        }

        public Criteria andSalidaNotBetween(Double value1, Double value2) {
            addCriterion("SALIDA not between", value1, value2, "salida");
            return (Criteria) this;
        }

        public Criteria andFechaProcesoIsNull() {
            addCriterion("FECHA_PROCESO is null");
            return (Criteria) this;
        }
        
        public Criteria andFechaProcesoMovimientoIsNull() {
            addCriterion("MOVIMIENTO.FECHA_PROCESO is null");
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

        public Criteria andEstadoMovimientoIsNull() {
            addCriterion("ESTADO_MOVIMIENTO is null");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoIsNotNull() {
            addCriterion("ESTADO_MOVIMIENTO is not null");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoEqualTo(Integer value) {
            addCriterion("ESTADO_MOVIMIENTO =", value, "estadoMovimiento");
            return (Criteria) this;
        }
        
        public Criteria andMovimientoEstadoMovimientoEqualTo(Integer value) {
            addCriterion("MOVIMIENTO.ESTADO_MOVIMIENTO =", value, "estadoMovimiento");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoNotEqualTo(Integer value) {
            addCriterion("ESTADO_MOVIMIENTO <>", value, "estadoMovimiento");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoGreaterThan(Integer value) {
            addCriterion("ESTADO_MOVIMIENTO >", value, "estadoMovimiento");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoGreaterThanOrEqualTo(Integer value) {
            addCriterion("ESTADO_MOVIMIENTO >=", value, "estadoMovimiento");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoLessThan(Integer value) {
            addCriterion("ESTADO_MOVIMIENTO <", value, "estadoMovimiento");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoLessThanOrEqualTo(Integer value) {
            addCriterion("ESTADO_MOVIMIENTO <=", value, "estadoMovimiento");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoIn(List<Integer> values) {
            addCriterion("ESTADO_MOVIMIENTO in", values, "estadoMovimiento");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoNotIn(List<Integer> values) {
            addCriterion("ESTADO_MOVIMIENTO not in", values, "estadoMovimiento");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoBetween(Integer value1, Integer value2) {
            addCriterion("ESTADO_MOVIMIENTO between", value1, value2, "estadoMovimiento");
            return (Criteria) this;
        }

        public Criteria andEstadoMovimientoNotBetween(Integer value1, Integer value2) {
            addCriterion("ESTADO_MOVIMIENTO not between", value1, value2, "estadoMovimiento");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionIsNull() {
            addCriterion("UID_TRANSACCION is null");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionIsNotNull() {
            addCriterion("UID_TRANSACCION is not null");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionEqualTo(String value) {
            addCriterion("UID_TRANSACCION =", value, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionNotEqualTo(String value) {
            addCriterion("UID_TRANSACCION <>", value, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionGreaterThan(String value) {
            addCriterion("UID_TRANSACCION >", value, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionGreaterThanOrEqualTo(String value) {
            addCriterion("UID_TRANSACCION >=", value, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionLessThan(String value) {
            addCriterion("UID_TRANSACCION <", value, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionLessThanOrEqualTo(String value) {
            addCriterion("UID_TRANSACCION <=", value, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionLike(String value) {
            addCriterion("UID_TRANSACCION like", value, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionNotLike(String value) {
            addCriterion("UID_TRANSACCION not like", value, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionIn(List<String> values) {
            addCriterion("UID_TRANSACCION in", values, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionNotIn(List<String> values) {
            addCriterion("UID_TRANSACCION not in", values, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionBetween(String value1, String value2) {
            addCriterion("UID_TRANSACCION between", value1, value2, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionNotBetween(String value1, String value2) {
            addCriterion("UID_TRANSACCION not between", value1, value2, "uidTransaccion");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andDocumentoLikeInsensitive(String value) {
            addCriterion("upper(DOCUMENTO) like", value.toUpperCase(), "documento");
            return (Criteria) this;
        }

        public Criteria andConceptoLikeInsensitive(String value) {
            addCriterion("upper(CONCEPTO) like", value.toUpperCase(), "concepto");
            return (Criteria) this;
        }

        public Criteria andUidTransaccionLikeInsensitive(String value) {
            addCriterion("upper(UID_TRANSACCION) like", value.toUpperCase(), "uidTransaccion");
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