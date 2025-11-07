package com.comerzzia.api.loyalty.persistence.collectives;

import java.util.ArrayList;
import java.util.List;

public class ColectivoExample {
    public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_COD_COLECTIVO = "COD_COLECTIVO";

    public static final String ORDER_BY_COD_COLECTIVO_DESC = "COD_COLECTIVO DESC";

    public static final String ORDER_BY_DES_COLECTIVO = "DES_COLECTIVO";

    public static final String ORDER_BY_DES_COLECTIVO_DESC = "DES_COLECTIVO DESC";

    public static final String ORDER_BY_ACTIVO = "ACTIVO";

    public static final String ORDER_BY_ACTIVO_DESC = "ACTIVO DESC";

    public static final String ORDER_BY_AMBITO_APLICACION = "AMBITO_APLICACION";

    public static final String ORDER_BY_AMBITO_APLICACION_DESC = "AMBITO_APLICACION DESC";

    public static final String ORDER_BY_CODTIPCOLECTIVO = "CODTIPCOLECTIVO";

    public static final String ORDER_BY_CODTIPCOLECTIVO_DESC = "CODTIPCOLECTIVO DESC";

    public static final String ORDER_BY_DESTIPCOLECTIVO = "DESTIPCOLECTIVO";

    public static final String ORDER_BY_DESTIPCOLECTIVO_DESC = "DESTIPCOLECTIVO DESC";

    public static final String ORDER_BY_PRIVADO = "PRIVADO";

    public static final String ORDER_BY_PRIVADO_DESC = "PRIVADO DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ColectivoExample() {
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

        public Criteria andAmbitoAplicacionIsNull() {
            addCriterion("AMBITO_APLICACION is null");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionIsNotNull() {
            addCriterion("AMBITO_APLICACION is not null");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionEqualTo(String value) {
            addCriterion("AMBITO_APLICACION =", value, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionNotEqualTo(String value) {
            addCriterion("AMBITO_APLICACION <>", value, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionGreaterThan(String value) {
            addCriterion("AMBITO_APLICACION >", value, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionGreaterThanOrEqualTo(String value) {
            addCriterion("AMBITO_APLICACION >=", value, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionLessThan(String value) {
            addCriterion("AMBITO_APLICACION <", value, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionLessThanOrEqualTo(String value) {
            addCriterion("AMBITO_APLICACION <=", value, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionLike(String value) {
            addCriterion("AMBITO_APLICACION like", value, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionNotLike(String value) {
            addCriterion("AMBITO_APLICACION not like", value, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionIn(List<String> values) {
            addCriterion("AMBITO_APLICACION in", values, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionNotIn(List<String> values) {
            addCriterion("AMBITO_APLICACION not in", values, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionBetween(String value1, String value2) {
            addCriterion("AMBITO_APLICACION between", value1, value2, "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andAmbitoAplicacionNotBetween(String value1, String value2) {
            addCriterion("AMBITO_APLICACION not between", value1, value2, "ambitoAplicacion");
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

        public Criteria andAmbitoAplicacionLikeInsensitive(String value) {
            addCriterion("upper(AMBITO_APLICACION) like", value.toUpperCase(), "ambitoAplicacion");
            return (Criteria) this;
        }

        public Criteria andCodtipcolectivoLikeInsensitive(String value) {
            addCriterion("upper(CODTIPCOLECTIVO) like", value.toUpperCase(), "codtipcolectivo");
            return (Criteria) this;
        }
        
        public Criteria andPrivadoIsNull() {
            addCriterion("PRIVADO is null");
            return (Criteria) this;
        }

        public Criteria andPrivadoIsNotNull() {
            addCriterion("PRIVADO is not null");
            return (Criteria) this;
        }
        
        public Criteria andPrivadoEqualTo(String value) {
            addCriterion("PRIVADO =", value, "privado");
            return (Criteria) this;
        }

        public Criteria andPrivadoNotEqualTo(String value) {
            addCriterion("PRIVADO <>", value, "privado");
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