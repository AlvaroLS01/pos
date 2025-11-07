package com.comerzzia.api.loyalty.persistence.accounts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiInstance;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public class AccountExample extends MultiInstance {

	private static final long serialVersionUID = -6366685742133811168L;

	public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_ID_CUENTA_TARJETA = "ID_CUENTA_TARJETA";

    public static final String ORDER_BY_ID_CUENTA_TARJETA_DESC = "ID_CUENTA_TARJETA DESC";

    public static final String ORDER_BY_ESTADO = "ESTADO";

    public static final String ORDER_BY_ESTADO_DESC = "ESTADO DESC";

    public static final String ORDER_BY_SALDO = "SALDO";

    public static final String ORDER_BY_SALDO_DESC = "SALDO DESC";

    public static final String ORDER_BY_FECHA_ACTUALIZACION = "FECHA_ACTUALIZACION";

    public static final String ORDER_BY_FECHA_ACTUALIZACION_DESC = "FECHA_ACTUALIZACION DESC";

    public static final String ORDER_BY_SALDO_PROVISIONAL = "SALDO_PROVISIONAL";

    public static final String ORDER_BY_SALDO_PROVISIONAL_DESC = "SALDO_PROVISIONAL DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AccountExample(IDatosSesion datosSesion) {
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

        public Criteria andEstadoIsNull() {
            addCriterion("ESTADO is null");
            return (Criteria) this;
        }

        public Criteria andEstadoIsNotNull() {
            addCriterion("ESTADO is not null");
            return (Criteria) this;
        }

        public Criteria andEstadoEqualTo(Integer value) {
            addCriterion("ESTADO =", value, "estado");
            return (Criteria) this;
        }

        public Criteria andEstadoNotEqualTo(Integer value) {
            addCriterion("ESTADO <>", value, "estado");
            return (Criteria) this;
        }

        public Criteria andEstadoGreaterThan(Integer value) {
            addCriterion("ESTADO >", value, "estado");
            return (Criteria) this;
        }

        public Criteria andEstadoGreaterThanOrEqualTo(Integer value) {
            addCriterion("ESTADO >=", value, "estado");
            return (Criteria) this;
        }

        public Criteria andEstadoLessThan(Integer value) {
            addCriterion("ESTADO <", value, "estado");
            return (Criteria) this;
        }

        public Criteria andEstadoLessThanOrEqualTo(Integer value) {
            addCriterion("ESTADO <=", value, "estado");
            return (Criteria) this;
        }

        public Criteria andEstadoIn(List<Integer> values) {
            addCriterion("ESTADO in", values, "estado");
            return (Criteria) this;
        }

        public Criteria andEstadoNotIn(List<Integer> values) {
            addCriterion("ESTADO not in", values, "estado");
            return (Criteria) this;
        }

        public Criteria andEstadoBetween(Integer value1, Integer value2) {
            addCriterion("ESTADO between", value1, value2, "estado");
            return (Criteria) this;
        }

        public Criteria andEstadoNotBetween(Integer value1, Integer value2) {
            addCriterion("ESTADO not between", value1, value2, "estado");
            return (Criteria) this;
        }

        public Criteria andSaldoIsNull() {
            addCriterion("SALDO is null");
            return (Criteria) this;
        }

        public Criteria andSaldoIsNotNull() {
            addCriterion("SALDO is not null");
            return (Criteria) this;
        }

        public Criteria andSaldoEqualTo(Double value) {
            addCriterion("SALDO =", value, "saldo");
            return (Criteria) this;
        }

        public Criteria andSaldoNotEqualTo(Double value) {
            addCriterion("SALDO <>", value, "saldo");
            return (Criteria) this;
        }

        public Criteria andSaldoGreaterThan(Double value) {
            addCriterion("SALDO >", value, "saldo");
            return (Criteria) this;
        }

        public Criteria andSaldoGreaterThanOrEqualTo(Double value) {
            addCriterion("SALDO >=", value, "saldo");
            return (Criteria) this;
        }

        public Criteria andSaldoLessThan(Double value) {
            addCriterion("SALDO <", value, "saldo");
            return (Criteria) this;
        }

        public Criteria andSaldoLessThanOrEqualTo(Double value) {
            addCriterion("SALDO <=", value, "saldo");
            return (Criteria) this;
        }

        public Criteria andSaldoIn(List<Double> values) {
            addCriterion("SALDO in", values, "saldo");
            return (Criteria) this;
        }

        public Criteria andSaldoNotIn(List<Double> values) {
            addCriterion("SALDO not in", values, "saldo");
            return (Criteria) this;
        }

        public Criteria andSaldoBetween(Double value1, Double value2) {
            addCriterion("SALDO between", value1, value2, "saldo");
            return (Criteria) this;
        }

        public Criteria andSaldoNotBetween(Double value1, Double value2) {
            addCriterion("SALDO not between", value1, value2, "saldo");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionIsNull() {
            addCriterion("FECHA_ACTUALIZACION is null");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionIsNotNull() {
            addCriterion("FECHA_ACTUALIZACION is not null");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionEqualTo(Date value) {
            addCriterion("FECHA_ACTUALIZACION =", value, "fechaActualizacion");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionNotEqualTo(Date value) {
            addCriterion("FECHA_ACTUALIZACION <>", value, "fechaActualizacion");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionGreaterThan(Date value) {
            addCriterion("FECHA_ACTUALIZACION >", value, "fechaActualizacion");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_ACTUALIZACION >=", value, "fechaActualizacion");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionLessThan(Date value) {
            addCriterion("FECHA_ACTUALIZACION <", value, "fechaActualizacion");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_ACTUALIZACION <=", value, "fechaActualizacion");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionIn(List<Date> values) {
            addCriterion("FECHA_ACTUALIZACION in", values, "fechaActualizacion");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionNotIn(List<Date> values) {
            addCriterion("FECHA_ACTUALIZACION not in", values, "fechaActualizacion");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionBetween(Date value1, Date value2) {
            addCriterion("FECHA_ACTUALIZACION between", value1, value2, "fechaActualizacion");
            return (Criteria) this;
        }

        public Criteria andFechaActualizacionNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_ACTUALIZACION not between", value1, value2, "fechaActualizacion");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalIsNull() {
            addCriterion("SALDO_PROVISIONAL is null");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalIsNotNull() {
            addCriterion("SALDO_PROVISIONAL is not null");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalEqualTo(Double value) {
            addCriterion("SALDO_PROVISIONAL =", value, "saldoProvisional");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalNotEqualTo(Double value) {
            addCriterion("SALDO_PROVISIONAL <>", value, "saldoProvisional");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalGreaterThan(Double value) {
            addCriterion("SALDO_PROVISIONAL >", value, "saldoProvisional");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalGreaterThanOrEqualTo(Double value) {
            addCriterion("SALDO_PROVISIONAL >=", value, "saldoProvisional");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalLessThan(Double value) {
            addCriterion("SALDO_PROVISIONAL <", value, "saldoProvisional");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalLessThanOrEqualTo(Double value) {
            addCriterion("SALDO_PROVISIONAL <=", value, "saldoProvisional");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalIn(List<Double> values) {
            addCriterion("SALDO_PROVISIONAL in", values, "saldoProvisional");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalNotIn(List<Double> values) {
            addCriterion("SALDO_PROVISIONAL not in", values, "saldoProvisional");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalBetween(Double value1, Double value2) {
            addCriterion("SALDO_PROVISIONAL between", value1, value2, "saldoProvisional");
            return (Criteria) this;
        }

        public Criteria andSaldoProvisionalNotBetween(Double value1, Double value2) {
            addCriterion("SALDO_PROVISIONAL not between", value1, value2, "saldoProvisional");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
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