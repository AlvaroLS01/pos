package com.comerzzia.iskaypet.pos.persistence.lenguajes;

import java.util.ArrayList;
import java.util.List;

public class LenguajeExample {
    public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_CODLENGUA = "CODLENGUA";

    public static final String ORDER_BY_CODLENGUA_DESC = "CODLENGUA DESC";

    public static final String ORDER_BY_DESLENGUA = "DESLENGUA";

    public static final String ORDER_BY_DESLENGUA_DESC = "DESLENGUA DESC";

    public static final String ORDER_BY_ACTIVO = "ACTIVO";

    public static final String ORDER_BY_ACTIVO_DESC = "ACTIVO DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public LenguajeExample() {
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

        public Criteria andCodlenguaIsNull() {
            addCriterion("CODLENGUA is null");
            return (Criteria) this;
        }

        public Criteria andCodlenguaIsNotNull() {
            addCriterion("CODLENGUA is not null");
            return (Criteria) this;
        }

        public Criteria andCodlenguaEqualTo(String value) {
            addCriterion("CODLENGUA =", value, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaNotEqualTo(String value) {
            addCriterion("CODLENGUA <>", value, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaGreaterThan(String value) {
            addCriterion("CODLENGUA >", value, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaGreaterThanOrEqualTo(String value) {
            addCriterion("CODLENGUA >=", value, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaLessThan(String value) {
            addCriterion("CODLENGUA <", value, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaLessThanOrEqualTo(String value) {
            addCriterion("CODLENGUA <=", value, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaLike(String value) {
            addCriterion("CODLENGUA like", value, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaNotLike(String value) {
            addCriterion("CODLENGUA not like", value, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaIn(List<String> values) {
            addCriterion("CODLENGUA in", values, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaNotIn(List<String> values) {
            addCriterion("CODLENGUA not in", values, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaBetween(String value1, String value2) {
            addCriterion("CODLENGUA between", value1, value2, "codlengua");
            return (Criteria) this;
        }

        public Criteria andCodlenguaNotBetween(String value1, String value2) {
            addCriterion("CODLENGUA not between", value1, value2, "codlengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaIsNull() {
            addCriterion("DESLENGUA is null");
            return (Criteria) this;
        }

        public Criteria andDeslenguaIsNotNull() {
            addCriterion("DESLENGUA is not null");
            return (Criteria) this;
        }

        public Criteria andDeslenguaEqualTo(String value) {
            addCriterion("DESLENGUA =", value, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaNotEqualTo(String value) {
            addCriterion("DESLENGUA <>", value, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaGreaterThan(String value) {
            addCriterion("DESLENGUA >", value, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaGreaterThanOrEqualTo(String value) {
            addCriterion("DESLENGUA >=", value, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaLessThan(String value) {
            addCriterion("DESLENGUA <", value, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaLessThanOrEqualTo(String value) {
            addCriterion("DESLENGUA <=", value, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaLike(String value) {
            addCriterion("DESLENGUA like", value, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaNotLike(String value) {
            addCriterion("DESLENGUA not like", value, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaIn(List<String> values) {
            addCriterion("DESLENGUA in", values, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaNotIn(List<String> values) {
            addCriterion("DESLENGUA not in", values, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaBetween(String value1, String value2) {
            addCriterion("DESLENGUA between", value1, value2, "deslengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaNotBetween(String value1, String value2) {
            addCriterion("DESLENGUA not between", value1, value2, "deslengua");
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

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andCodlenguaLikeInsensitive(String value) {
            addCriterion("upper(CODLENGUA) like", value.toUpperCase(), "codlengua");
            return (Criteria) this;
        }

        public Criteria andDeslenguaLikeInsensitive(String value) {
            addCriterion("upper(DESLENGUA) like", value.toUpperCase(), "deslengua");
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