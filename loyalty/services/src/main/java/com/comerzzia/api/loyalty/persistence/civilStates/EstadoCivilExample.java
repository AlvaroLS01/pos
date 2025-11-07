package com.comerzzia.api.loyalty.persistence.civilStates;

import java.util.ArrayList;
import java.util.List;

public class EstadoCivilExample {
    public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_CODESTCIVIL = "CODESTCIVIL";

    public static final String ORDER_BY_CODESTCIVIL_DESC = "CODESTCIVIL DESC";

    public static final String ORDER_BY_DESESTCIVIL = "DESESTCIVIL";

    public static final String ORDER_BY_DESESTCIVIL_DESC = "DESESTCIVIL DESC";

    public static final String ORDER_BY_ACTIVO = "ACTIVO";

    public static final String ORDER_BY_ACTIVO_DESC = "ACTIVO DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EstadoCivilExample() {
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
        protected List<Criterion> activoCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
            activoCriteria = new ArrayList<Criterion>();
        }

        public List<Criterion> getActivoCriteria() {
            return activoCriteria;
        }

        protected void addActivoCriterion(String condition, Object value, String property) {
            if (value != null) {
                activoCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        protected void addActivoCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 != null && value2 != null) {
                activoCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        public boolean isValid() {
            return criteria.size() > 0
                || activoCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<Criterion>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(activoCriteria);
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

        public Criteria andCodestcivilIsNull() {
            addCriterion("CODESTCIVIL is null");
            return (Criteria) this;
        }

        public Criteria andCodestcivilIsNotNull() {
            addCriterion("CODESTCIVIL is not null");
            return (Criteria) this;
        }

        public Criteria andCodestcivilEqualTo(String value) {
            addCriterion("CODESTCIVIL =", value, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilNotEqualTo(String value) {
            addCriterion("CODESTCIVIL <>", value, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilGreaterThan(String value) {
            addCriterion("CODESTCIVIL >", value, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilGreaterThanOrEqualTo(String value) {
            addCriterion("CODESTCIVIL >=", value, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilLessThan(String value) {
            addCriterion("CODESTCIVIL <", value, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilLessThanOrEqualTo(String value) {
            addCriterion("CODESTCIVIL <=", value, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilLike(String value) {
            addCriterion("CODESTCIVIL like", value, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilNotLike(String value) {
            addCriterion("CODESTCIVIL not like", value, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilIn(List<String> values) {
            addCriterion("CODESTCIVIL in", values, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilNotIn(List<String> values) {
            addCriterion("CODESTCIVIL not in", values, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilBetween(String value1, String value2) {
            addCriterion("CODESTCIVIL between", value1, value2, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andCodestcivilNotBetween(String value1, String value2) {
            addCriterion("CODESTCIVIL not between", value1, value2, "codestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilIsNull() {
            addCriterion("DESESTCIVIL is null");
            return (Criteria) this;
        }

        public Criteria andDesestcivilIsNotNull() {
            addCriterion("DESESTCIVIL is not null");
            return (Criteria) this;
        }

        public Criteria andDesestcivilEqualTo(String value) {
            addCriterion("DESESTCIVIL =", value, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilNotEqualTo(String value) {
            addCriterion("DESESTCIVIL <>", value, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilGreaterThan(String value) {
            addCriterion("DESESTCIVIL >", value, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilGreaterThanOrEqualTo(String value) {
            addCriterion("DESESTCIVIL >=", value, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilLessThan(String value) {
            addCriterion("DESESTCIVIL <", value, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilLessThanOrEqualTo(String value) {
            addCriterion("DESESTCIVIL <=", value, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilLike(String value) {
            addCriterion("DESESTCIVIL like", value, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilNotLike(String value) {
            addCriterion("DESESTCIVIL not like", value, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilIn(List<String> values) {
            addCriterion("DESESTCIVIL in", values, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilNotIn(List<String> values) {
            addCriterion("DESESTCIVIL not in", values, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilBetween(String value1, String value2) {
            addCriterion("DESESTCIVIL between", value1, value2, "desestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilNotBetween(String value1, String value2) {
            addCriterion("DESESTCIVIL not between", value1, value2, "desestcivil");
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

        public Criteria andActivoEqualTo(Boolean value) {
            addActivoCriterion("ACTIVO =", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoNotEqualTo(Boolean value) {
            addActivoCriterion("ACTIVO <>", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoGreaterThan(Boolean value) {
            addActivoCriterion("ACTIVO >", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoGreaterThanOrEqualTo(Boolean value) {
            addActivoCriterion("ACTIVO >=", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoLessThan(Boolean value) {
            addActivoCriterion("ACTIVO <", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoLessThanOrEqualTo(Boolean value) {
            addActivoCriterion("ACTIVO <=", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoLike(Boolean value) {
            addActivoCriterion("ACTIVO like", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoNotLike(Boolean value) {
            addActivoCriterion("ACTIVO not like", value, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoIn(List<Boolean> values) {
            addActivoCriterion("ACTIVO in", values, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoNotIn(List<Boolean> values) {
            addActivoCriterion("ACTIVO not in", values, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoBetween(Boolean value1, Boolean value2) {
            addActivoCriterion("ACTIVO between", value1, value2, "activo");
            return (Criteria) this;
        }

        public Criteria andActivoNotBetween(Boolean value1, Boolean value2) {
            addActivoCriterion("ACTIVO not between", value1, value2, "activo");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andCodestcivilLikeInsensitive(String value) {
            addCriterion("upper(CODESTCIVIL) like", value.toUpperCase(), "codestcivil");
            return (Criteria) this;
        }

        public Criteria andDesestcivilLikeInsensitive(String value) {
            addCriterion("upper(DESESTCIVIL) like", value.toUpperCase(), "desestcivil");
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