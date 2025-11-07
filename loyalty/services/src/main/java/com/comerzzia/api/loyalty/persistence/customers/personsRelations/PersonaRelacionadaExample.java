package com.comerzzia.api.loyalty.persistence.customers.personsRelations;

import java.util.ArrayList;
import java.util.List;

public class PersonaRelacionadaExample {
    public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_UID_RELACION_FIDELIZADO = "UID_RELACION_FIDELIZADO";

    public static final String ORDER_BY_UID_RELACION_FIDELIZADO_DESC = "UID_RELACION_FIDELIZADO DESC";

    public static final String ORDER_BY_ID_FIDELIZADO_ORIGEN = "ID_FIDELIZADO_ORIGEN";

    public static final String ORDER_BY_ID_FIDELIZADO_ORIGEN_DESC = "ID_FIDELIZADO_ORIGEN DESC";

    public static final String ORDER_BY_ID_FIDELIZADO_DESTINO = "ID_FIDELIZADO_DESTINO";

    public static final String ORDER_BY_ID_FIDELIZADO_DESTINO_DESC = "ID_FIDELIZADO_DESTINO DESC";

    public static final String ORDER_BY_CODTIPO_RELACION = "CODTIPO_RELACION";

    public static final String ORDER_BY_CODTIPO_RELACION_DESC = "CODTIPO_RELACION DESC";

    public static final String ORDER_BY_NOMBRE = "NOMBRE";

    public static final String ORDER_BY_NOMBRE_DESC = "NOMBRE DESC";

    public static final String ORDER_BY_APELLIDOS = "APELLIDOS";

    public static final String ORDER_BY_APELLIDOS_DESC = "APELLIDOS DESC";

    public static final String ORDER_BY_SEXO = "SEXO";

    public static final String ORDER_BY_SEXO_DESC = "SEXO DESC";

    public static final String ORDER_BY_FECHA_NACIMIENTO = "FECHA_NACIMIENTO";

    public static final String ORDER_BY_FECHA_NACIMIENTO_DESC = "FECHA_NACIMIENTO DESC";

    public static final String ORDER_BY_DESTIPO_RELACION = "DESTIPO_RELACION";

    public static final String ORDER_BY_DESTIPO_RELACION_DESC = "DESTIPO_RELACION DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PersonaRelacionadaExample() {
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

        public Criteria andUidRelacionFidelizadoIsNull() {
            addCriterion("UID_RELACION_FIDELIZADO is null");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoIsNotNull() {
            addCriterion("UID_RELACION_FIDELIZADO is not null");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoEqualTo(String value) {
            addCriterion("UID_RELACION_FIDELIZADO =", value, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoNotEqualTo(String value) {
            addCriterion("UID_RELACION_FIDELIZADO <>", value, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoGreaterThan(String value) {
            addCriterion("UID_RELACION_FIDELIZADO >", value, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoGreaterThanOrEqualTo(String value) {
            addCriterion("UID_RELACION_FIDELIZADO >=", value, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoLessThan(String value) {
            addCriterion("UID_RELACION_FIDELIZADO <", value, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoLessThanOrEqualTo(String value) {
            addCriterion("UID_RELACION_FIDELIZADO <=", value, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoLike(String value) {
            addCriterion("UID_RELACION_FIDELIZADO like", value, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoNotLike(String value) {
            addCriterion("UID_RELACION_FIDELIZADO not like", value, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoIn(List<String> values) {
            addCriterion("UID_RELACION_FIDELIZADO in", values, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoNotIn(List<String> values) {
            addCriterion("UID_RELACION_FIDELIZADO not in", values, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoBetween(String value1, String value2) {
            addCriterion("UID_RELACION_FIDELIZADO between", value1, value2, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoNotBetween(String value1, String value2) {
            addCriterion("UID_RELACION_FIDELIZADO not between", value1, value2, "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenIsNull() {
            addCriterion("ID_FIDELIZADO_ORIGEN is null");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenIsNotNull() {
            addCriterion("ID_FIDELIZADO_ORIGEN is not null");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO_ORIGEN =", value, "idFidelizadoOrigen");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenNotEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO_ORIGEN <>", value, "idFidelizadoOrigen");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenGreaterThan(Long value) {
            addCriterion("ID_FIDELIZADO_ORIGEN >", value, "idFidelizadoOrigen");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO_ORIGEN >=", value, "idFidelizadoOrigen");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenLessThan(Long value) {
            addCriterion("ID_FIDELIZADO_ORIGEN <", value, "idFidelizadoOrigen");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenLessThanOrEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO_ORIGEN <=", value, "idFidelizadoOrigen");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenIn(List<Long> values) {
            addCriterion("ID_FIDELIZADO_ORIGEN in", values, "idFidelizadoOrigen");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenNotIn(List<Long> values) {
            addCriterion("ID_FIDELIZADO_ORIGEN not in", values, "idFidelizadoOrigen");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenBetween(Long value1, Long value2) {
            addCriterion("ID_FIDELIZADO_ORIGEN between", value1, value2, "idFidelizadoOrigen");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoOrigenNotBetween(Long value1, Long value2) {
            addCriterion("ID_FIDELIZADO_ORIGEN not between", value1, value2, "idFidelizadoOrigen");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoIsNull() {
            addCriterion("ID_FIDELIZADO_DESTINO is null");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoIsNotNull() {
            addCriterion("ID_FIDELIZADO_DESTINO is not null");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO_DESTINO =", value, "idFidelizadoDestino");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoNotEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO_DESTINO <>", value, "idFidelizadoDestino");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoGreaterThan(Long value) {
            addCriterion("ID_FIDELIZADO_DESTINO >", value, "idFidelizadoDestino");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO_DESTINO >=", value, "idFidelizadoDestino");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoLessThan(Long value) {
            addCriterion("ID_FIDELIZADO_DESTINO <", value, "idFidelizadoDestino");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoLessThanOrEqualTo(Long value) {
            addCriterion("ID_FIDELIZADO_DESTINO <=", value, "idFidelizadoDestino");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoIn(List<Long> values) {
            addCriterion("ID_FIDELIZADO_DESTINO in", values, "idFidelizadoDestino");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoNotIn(List<Long> values) {
            addCriterion("ID_FIDELIZADO_DESTINO not in", values, "idFidelizadoDestino");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoBetween(Long value1, Long value2) {
            addCriterion("ID_FIDELIZADO_DESTINO between", value1, value2, "idFidelizadoDestino");
            return (Criteria) this;
        }

        public Criteria andIdFidelizadoDestinoNotBetween(Long value1, Long value2) {
            addCriterion("ID_FIDELIZADO_DESTINO not between", value1, value2, "idFidelizadoDestino");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionIsNull() {
            addCriterion("CODTIPO_RELACION is null");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionIsNotNull() {
            addCriterion("CODTIPO_RELACION is not null");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionEqualTo(String value) {
            addCriterion("CODTIPO_RELACION =", value, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionNotEqualTo(String value) {
            addCriterion("CODTIPO_RELACION <>", value, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionGreaterThan(String value) {
            addCriterion("CODTIPO_RELACION >", value, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionGreaterThanOrEqualTo(String value) {
            addCriterion("CODTIPO_RELACION >=", value, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionLessThan(String value) {
            addCriterion("CODTIPO_RELACION <", value, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionLessThanOrEqualTo(String value) {
            addCriterion("CODTIPO_RELACION <=", value, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionLike(String value) {
            addCriterion("CODTIPO_RELACION like", value, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionNotLike(String value) {
            addCriterion("CODTIPO_RELACION not like", value, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionIn(List<String> values) {
            addCriterion("CODTIPO_RELACION in", values, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionNotIn(List<String> values) {
            addCriterion("CODTIPO_RELACION not in", values, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionBetween(String value1, String value2) {
            addCriterion("CODTIPO_RELACION between", value1, value2, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionNotBetween(String value1, String value2) {
            addCriterion("CODTIPO_RELACION not between", value1, value2, "codTipoRelacion");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUidRelacionFidelizadoLikeInsensitive(String value) {
            addCriterion("upper(UID_RELACION_FIDELIZADO) like", value.toUpperCase(), "uidRelacionFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodTipoRelacionLikeInsensitive(String value) {
            addCriterion("upper(CODTIPO_RELACION) like", value.toUpperCase(), "codTipoRelacion");
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