package com.comerzzia.iskaypet.pos.persistence.colectivos;

import java.util.ArrayList;
import java.util.List;

public class LocalColectivosExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public LocalColectivosExample() {
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

        public Criteria andDesColectivoIsNull() {
            addCriterion("DES_COLECTIVO is null");
            return (Criteria) this;
        }

        public Criteria andDesColectivoIsNotNull() {
            addCriterion("DES_COLECTIVO is not null");
            return (Criteria) this;
        }

        public Criteria andDesColectivoEqualTo(String value) {
            addCriterion("DES_COLECTIVO =", value, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoNotEqualTo(String value) {
            addCriterion("DES_COLECTIVO <>", value, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoGreaterThan(String value) {
            addCriterion("DES_COLECTIVO >", value, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoGreaterThanOrEqualTo(String value) {
            addCriterion("DES_COLECTIVO >=", value, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoLessThan(String value) {
            addCriterion("DES_COLECTIVO <", value, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoLessThanOrEqualTo(String value) {
            addCriterion("DES_COLECTIVO <=", value, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoLike(String value) {
            addCriterion("DES_COLECTIVO like", value, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoNotLike(String value) {
            addCriterion("DES_COLECTIVO not like", value, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoIn(List<String> values) {
            addCriterion("DES_COLECTIVO in", values, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoNotIn(List<String> values) {
            addCriterion("DES_COLECTIVO not in", values, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoBetween(String value1, String value2) {
            addCriterion("DES_COLECTIVO between", value1, value2, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoNotBetween(String value1, String value2) {
            addCriterion("DES_COLECTIVO not between", value1, value2, "desColectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoIsNull() {
            addCriterion("CODTIPCOLECTIVO is null");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoIsNotNull() {
            addCriterion("CODTIPCOLECTIVO is not null");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoEqualTo(String value) {
            addCriterion("CODTIPCOLECTIVO =", value, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoNotEqualTo(String value) {
            addCriterion("CODTIPCOLECTIVO <>", value, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoGreaterThan(String value) {
            addCriterion("CODTIPCOLECTIVO >", value, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoGreaterThanOrEqualTo(String value) {
            addCriterion("CODTIPCOLECTIVO >=", value, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoLessThan(String value) {
            addCriterion("CODTIPCOLECTIVO <", value, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoLessThanOrEqualTo(String value) {
            addCriterion("CODTIPCOLECTIVO <=", value, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoLike(String value) {
            addCriterion("CODTIPCOLECTIVO like", value, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoNotLike(String value) {
            addCriterion("CODTIPCOLECTIVO not like", value, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoIn(List<String> values) {
            addCriterion("CODTIPCOLECTIVO in", values, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoNotIn(List<String> values) {
            addCriterion("CODTIPCOLECTIVO not in", values, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoBetween(String value1, String value2) {
            addCriterion("CODTIPCOLECTIVO between", value1, value2, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoNotBetween(String value1, String value2) {
            addCriterion("CODTIPCOLECTIVO not between", value1, value2, "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoIsNull() {
            addCriterion("DESTIPCOLECTIVO is null");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoIsNotNull() {
            addCriterion("DESTIPCOLECTIVO is not null");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoEqualTo(String value) {
            addCriterion("DESTIPCOLECTIVO =", value, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoNotEqualTo(String value) {
            addCriterion("DESTIPCOLECTIVO <>", value, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoGreaterThan(String value) {
            addCriterion("DESTIPCOLECTIVO >", value, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoGreaterThanOrEqualTo(String value) {
            addCriterion("DESTIPCOLECTIVO >=", value, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoLessThan(String value) {
            addCriterion("DESTIPCOLECTIVO <", value, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoLessThanOrEqualTo(String value) {
            addCriterion("DESTIPCOLECTIVO <=", value, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoLike(String value) {
            addCriterion("DESTIPCOLECTIVO like", value, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoNotLike(String value) {
            addCriterion("DESTIPCOLECTIVO not like", value, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoIn(List<String> values) {
            addCriterion("DESTIPCOLECTIVO in", values, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoNotIn(List<String> values) {
            addCriterion("DESTIPCOLECTIVO not in", values, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoBetween(String value1, String value2) {
            addCriterion("DESTIPCOLECTIVO between", value1, value2, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoNotBetween(String value1, String value2) {
            addCriterion("DESTIPCOLECTIVO not between", value1, value2, "destipcolectivo");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andCodColectivoLikeInsensitive(String value) {
            addCriterion("upper(COD_COLECTIVO) like", value.toUpperCase(), "codColectivo");
            return (Criteria) this;
        }

        public Criteria andDesColectivoLikeInsensitive(String value) {
            addCriterion("upper(DES_COLECTIVO) like", value.toUpperCase(), "desColectivo");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoLikeInsensitive(String value) {
            addCriterion("upper(CODTIPCOLECTIVO) like", value.toUpperCase(), "codtipcolectivo");
            return (Criteria) this;
        }

        public Criteria andDestipcolectivoLikeInsensitive(String value) {
            addCriterion("upper(DESTIPCOLECTIVO) like", value.toUpperCase(), "destipcolectivo");
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