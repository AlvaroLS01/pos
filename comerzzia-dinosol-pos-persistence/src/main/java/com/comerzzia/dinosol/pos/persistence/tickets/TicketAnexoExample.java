package com.comerzzia.dinosol.pos.persistence.tickets;

import java.util.ArrayList;
import java.util.List;

public class TicketAnexoExample {
    public static final String ORDER_BY_UID_ACTIVIDAD = "UID_ACTIVIDAD";

    public static final String ORDER_BY_UID_ACTIVIDAD_DESC = "UID_ACTIVIDAD DESC";

    public static final String ORDER_BY_UID_TICKET = "UID_TICKET";

    public static final String ORDER_BY_UID_TICKET_DESC = "UID_TICKET DESC";

    public static final String ORDER_BY_TIENE_RECARGA = "TIENE_RECARGA";

    public static final String ORDER_BY_TIENE_RECARGA_DESC = "TIENE_RECARGA DESC";

    public static final String ORDER_BY_OPERADOR = "OPERADOR";

    public static final String ORDER_BY_OPERADOR_DESC = "OPERADOR DESC";

    public static final String ORDER_BY_TELEFONO = "TELEFONO";

    public static final String ORDER_BY_TELEFONO_DESC = "TELEFONO DESC";

    public static final String ORDER_BY_COD_VALIDACION = "COD_VALIDACION";

    public static final String ORDER_BY_COD_VALIDACION_DESC = "COD_VALIDACION DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TicketAnexoExample() {
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
        protected List<Criterion> tieneRecargaCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
            tieneRecargaCriteria = new ArrayList<Criterion>();
        }

        public List<Criterion> getTieneRecargaCriteria() {
            return tieneRecargaCriteria;
        }

        protected void addTieneRecargaCriterion(String condition, Object value, String property) {
            if (value != null) {
                tieneRecargaCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        protected void addTieneRecargaCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 != null && value2 != null) {
                tieneRecargaCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        public boolean isValid() {
            return criteria.size() > 0
                || tieneRecargaCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<Criterion>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(tieneRecargaCriteria);
            }
            return allCriteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition != null) {
                criteria.add(new Criterion(condition));
                allCriteria = null;
            }
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value != null) {
                criteria.add(new Criterion(condition, value));
                allCriteria = null;
            }
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 != null && value2 != null) {
                criteria.add(new Criterion(condition, value1, value2));
                allCriteria = null;
            }
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

        public Criteria andTieneRecargaIsNull() {
            addCriterion("TIENE_RECARGA is null");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaIsNotNull() {
            addCriterion("TIENE_RECARGA is not null");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaEqualTo(Boolean value) {
            addTieneRecargaCriterion("TIENE_RECARGA =", value, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaNotEqualTo(Boolean value) {
            addTieneRecargaCriterion("TIENE_RECARGA <>", value, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaGreaterThan(Boolean value) {
            addTieneRecargaCriterion("TIENE_RECARGA >", value, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaGreaterThanOrEqualTo(Boolean value) {
            addTieneRecargaCriterion("TIENE_RECARGA >=", value, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaLessThan(Boolean value) {
            addTieneRecargaCriterion("TIENE_RECARGA <", value, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaLessThanOrEqualTo(Boolean value) {
            addTieneRecargaCriterion("TIENE_RECARGA <=", value, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaLike(Boolean value) {
            addTieneRecargaCriterion("TIENE_RECARGA like", value, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaNotLike(Boolean value) {
            addTieneRecargaCriterion("TIENE_RECARGA not like", value, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaIn(List<Boolean> values) {
            addTieneRecargaCriterion("TIENE_RECARGA in", values, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaNotIn(List<Boolean> values) {
            addTieneRecargaCriterion("TIENE_RECARGA not in", values, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaBetween(Boolean value1, Boolean value2) {
            addTieneRecargaCriterion("TIENE_RECARGA between", value1, value2, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andTieneRecargaNotBetween(Boolean value1, Boolean value2) {
            addTieneRecargaCriterion("TIENE_RECARGA not between", value1, value2, "tieneRecarga");
            return (Criteria) this;
        }

        public Criteria andOperadorIsNull() {
            addCriterion("OPERADOR is null");
            return (Criteria) this;
        }

        public Criteria andOperadorIsNotNull() {
            addCriterion("OPERADOR is not null");
            return (Criteria) this;
        }

        public Criteria andOperadorEqualTo(String value) {
            addCriterion("OPERADOR =", value, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorNotEqualTo(String value) {
            addCriterion("OPERADOR <>", value, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorGreaterThan(String value) {
            addCriterion("OPERADOR >", value, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorGreaterThanOrEqualTo(String value) {
            addCriterion("OPERADOR >=", value, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorLessThan(String value) {
            addCriterion("OPERADOR <", value, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorLessThanOrEqualTo(String value) {
            addCriterion("OPERADOR <=", value, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorLike(String value) {
            addCriterion("OPERADOR like", value, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorNotLike(String value) {
            addCriterion("OPERADOR not like", value, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorIn(List<String> values) {
            addCriterion("OPERADOR in", values, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorNotIn(List<String> values) {
            addCriterion("OPERADOR not in", values, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorBetween(String value1, String value2) {
            addCriterion("OPERADOR between", value1, value2, "operador");
            return (Criteria) this;
        }

        public Criteria andOperadorNotBetween(String value1, String value2) {
            addCriterion("OPERADOR not between", value1, value2, "operador");
            return (Criteria) this;
        }

        public Criteria andTelefonoIsNull() {
            addCriterion("TELEFONO is null");
            return (Criteria) this;
        }

        public Criteria andTelefonoIsNotNull() {
            addCriterion("TELEFONO is not null");
            return (Criteria) this;
        }

        public Criteria andTelefonoEqualTo(String value) {
            addCriterion("TELEFONO =", value, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoNotEqualTo(String value) {
            addCriterion("TELEFONO <>", value, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoGreaterThan(String value) {
            addCriterion("TELEFONO >", value, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoGreaterThanOrEqualTo(String value) {
            addCriterion("TELEFONO >=", value, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoLessThan(String value) {
            addCriterion("TELEFONO <", value, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoLessThanOrEqualTo(String value) {
            addCriterion("TELEFONO <=", value, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoLike(String value) {
            addCriterion("TELEFONO like", value, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoNotLike(String value) {
            addCriterion("TELEFONO not like", value, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoIn(List<String> values) {
            addCriterion("TELEFONO in", values, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoNotIn(List<String> values) {
            addCriterion("TELEFONO not in", values, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoBetween(String value1, String value2) {
            addCriterion("TELEFONO between", value1, value2, "telefono");
            return (Criteria) this;
        }

        public Criteria andTelefonoNotBetween(String value1, String value2) {
            addCriterion("TELEFONO not between", value1, value2, "telefono");
            return (Criteria) this;
        }

        public Criteria andCodValidacionIsNull() {
            addCriterion("COD_VALIDACION is null");
            return (Criteria) this;
        }

        public Criteria andCodValidacionIsNotNull() {
            addCriterion("COD_VALIDACION is not null");
            return (Criteria) this;
        }

        public Criteria andCodValidacionEqualTo(String value) {
            addCriterion("COD_VALIDACION =", value, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionNotEqualTo(String value) {
            addCriterion("COD_VALIDACION <>", value, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionGreaterThan(String value) {
            addCriterion("COD_VALIDACION >", value, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionGreaterThanOrEqualTo(String value) {
            addCriterion("COD_VALIDACION >=", value, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionLessThan(String value) {
            addCriterion("COD_VALIDACION <", value, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionLessThanOrEqualTo(String value) {
            addCriterion("COD_VALIDACION <=", value, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionLike(String value) {
            addCriterion("COD_VALIDACION like", value, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionNotLike(String value) {
            addCriterion("COD_VALIDACION not like", value, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionIn(List<String> values) {
            addCriterion("COD_VALIDACION in", values, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionNotIn(List<String> values) {
            addCriterion("COD_VALIDACION not in", values, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionBetween(String value1, String value2) {
            addCriterion("COD_VALIDACION between", value1, value2, "codValidacion");
            return (Criteria) this;
        }

        public Criteria andCodValidacionNotBetween(String value1, String value2) {
            addCriterion("COD_VALIDACION not between", value1, value2, "codValidacion");
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

        public Criteria andOperadorLikeInsensitive(String value) {
            addCriterion("upper(OPERADOR) like", value.toUpperCase(), "operador");
            return (Criteria) this;
        }

        public Criteria andTelefonoLikeInsensitive(String value) {
            addCriterion("upper(TELEFONO) like", value.toUpperCase(), "telefono");
            return (Criteria) this;
        }

        public Criteria andCodValidacionLikeInsensitive(String value) {
            addCriterion("upper(COD_VALIDACION) like", value.toUpperCase(), "codValidacion");
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