package com.comerzzia.api.loyalty.persistence.customers.contacts;

import java.util.ArrayList;
import java.util.List;

public class TiposContactoFidelizadoExample {
    public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_ID_FIDELIZADO = "ID_FIDELIZADO";

    public static final String ORDER_BY_ID_FIDELIZADO_DESC = "ID_FIDELIZADO DESC";

    public static final String ORDER_BY_CODTIPOCON = "CODTIPOCON";

    public static final String ORDER_BY_CODTIPOCON_DESC = "CODTIPOCON DESC";

    public static final String ORDER_BY_VALOR = "VALOR";

    public static final String ORDER_BY_VALOR_DESC = "VALOR DESC";

    public static final String ORDER_BY_RECIBE_NOTIFICACIONES = "RECIBE_NOTIFICACIONES";

    public static final String ORDER_BY_RECIBE_NOTIFICACIONES_DESC = "RECIBE_NOTIFICACIONES DESC";
    
    public static final String ORDER_BY_RECIBE_NOTIFICACIONES_COM = "RECIBE_NOTIFICACIONES_COM";

    public static final String ORDER_BY_RECIBE_NOTIFICACIONES_COM_DESC = "RECIBE_NOTIFICACIONES_COM DESC";

    public static final String ORDER_BY_DESTIPOCON = "DESTIPOCON";

    public static final String ORDER_BY_DESTIPOCON_DESC = "DESTIPOCON DESC";

    public static final String ORDER_BY_PUEDE_RECIBIR_NOTIFICACIONES = "PUEDE_RECIBIR_NOTIFICACIONES";

    public static final String ORDER_BY_PUEDE_RECIBIR_NOTIFICACIONES_DESC = "PUEDE_RECIBIR_NOTIFICACIONES DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TiposContactoFidelizadoExample() {
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
        protected List<Criterion> recibeNotificacionesCriteria;
        
        protected List<Criterion> recibeNotificacionesComCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
            recibeNotificacionesCriteria = new ArrayList<Criterion>();
            recibeNotificacionesComCriteria = new ArrayList<Criterion>();
        }

        public List<Criterion> getRecibeNotificacionesCriteria() {
            return recibeNotificacionesCriteria;
        }
        
        public List<Criterion> getRecibeNotificacionesComCriteria() {
            return recibeNotificacionesComCriteria;
        }

        protected void addRecibeNotificacionesCriterion(String condition, Object value, String property) {
            if (value != null) {
                recibeNotificacionesCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        protected void addRecibeNotificacionesCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 != null && value2 != null) {
                recibeNotificacionesCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }
        
        protected void addRecibeNotificacionesComCriterion(String condition, Object value, String property) {
            if (value != null) {
                recibeNotificacionesComCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        protected void addRecibeNotificacionesComCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 != null && value2 != null) {
                recibeNotificacionesComCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        public boolean isValid() {
            return criteria.size() > 0
                || recibeNotificacionesCriteria.size() > 0
                || recibeNotificacionesComCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<Criterion>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(recibeNotificacionesCriteria);
                allCriteria.addAll(recibeNotificacionesComCriteria);
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

        public Criteria andCodTipoConIsNull() {
            addCriterion("CODTIPOCON is null");
            return (Criteria) this;
        }

        public Criteria andCodTipoConIsNotNull() {
            addCriterion("CODTIPOCON is not null");
            return (Criteria) this;
        }

        public Criteria andCodTipoConEqualTo(String value) {
            addCriterion("CODTIPOCON =", value, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConNotEqualTo(String value) {
            addCriterion("CODTIPOCON <>", value, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConGreaterThan(String value) {
            addCriterion("CODTIPOCON >", value, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConGreaterThanOrEqualTo(String value) {
            addCriterion("CODTIPOCON >=", value, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConLessThan(String value) {
            addCriterion("CODTIPOCON <", value, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConLessThanOrEqualTo(String value) {
            addCriterion("CODTIPOCON <=", value, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConLike(String value) {
            addCriterion("CODTIPOCON like", value, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConNotLike(String value) {
            addCriterion("CODTIPOCON not like", value, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConIn(List<String> values) {
            addCriterion("CODTIPOCON in", values, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConNotIn(List<String> values) {
            addCriterion("CODTIPOCON not in", values, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConBetween(String value1, String value2) {
            addCriterion("CODTIPOCON between", value1, value2, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andCodTipoConNotBetween(String value1, String value2) {
            addCriterion("CODTIPOCON not between", value1, value2, "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andValorIsNull() {
            addCriterion("VALOR is null");
            return (Criteria) this;
        }

        public Criteria andValorIsNotNull() {
            addCriterion("VALOR is not null");
            return (Criteria) this;
        }

        public Criteria andValorEqualTo(String value) {
            addCriterion("VALOR =", value, "valor");
            return (Criteria) this;
        }
        
        public Criteria andUpperValorEqualTo(String value) {
            addCriterion("UPPER(VALOR) =", value, "valor");
            return (Criteria) this;
        }

        public Criteria andValorNotEqualTo(String value) {
            addCriterion("VALOR <>", value, "valor");
            return (Criteria) this;
        }

        public Criteria andValorGreaterThan(String value) {
            addCriterion("VALOR >", value, "valor");
            return (Criteria) this;
        }

        public Criteria andValorGreaterThanOrEqualTo(String value) {
            addCriterion("VALOR >=", value, "valor");
            return (Criteria) this;
        }

        public Criteria andValorLessThan(String value) {
            addCriterion("VALOR <", value, "valor");
            return (Criteria) this;
        }

        public Criteria andValorLessThanOrEqualTo(String value) {
            addCriterion("VALOR <=", value, "valor");
            return (Criteria) this;
        }

        public Criteria andValorLike(String value) {
            addCriterion("VALOR like", value, "valor");
            return (Criteria) this;
        }

        public Criteria andValorNotLike(String value) {
            addCriterion("VALOR not like", value, "valor");
            return (Criteria) this;
        }

        public Criteria andValorIn(List<String> values) {
            addCriterion("VALOR in", values, "valor");
            return (Criteria) this;
        }

        public Criteria andValorNotIn(List<String> values) {
            addCriterion("VALOR not in", values, "valor");
            return (Criteria) this;
        }

        public Criteria andValorBetween(String value1, String value2) {
            addCriterion("VALOR between", value1, value2, "valor");
            return (Criteria) this;
        }

        public Criteria andValorNotBetween(String value1, String value2) {
            addCriterion("VALOR not between", value1, value2, "valor");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesIsNull() {
            addCriterion("RECIBE_NOTIFICACIONES is null");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesIsNotNull() {
            addCriterion("RECIBE_NOTIFICACIONES is not null");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesEqualTo(Boolean value) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES =", value, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesNotEqualTo(Boolean value) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES <>", value, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesGreaterThan(Boolean value) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES >", value, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesGreaterThanOrEqualTo(Boolean value) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES >=", value, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesLessThan(Boolean value) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES <", value, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesLessThanOrEqualTo(Boolean value) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES <=", value, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesLike(Boolean value) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES like", value, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesNotLike(Boolean value) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES not like", value, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesIn(List<Boolean> values) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES in", values, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesNotIn(List<Boolean> values) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES not in", values, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesBetween(Boolean value1, Boolean value2) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES between", value1, value2, "recibeNotificaciones");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesNotBetween(Boolean value1, Boolean value2) {
            addRecibeNotificacionesCriterion("RECIBE_NOTIFICACIONES not between", value1, value2, "recibeNotificaciones");
            return (Criteria) this;
        }
        
        public Criteria andRecibeNotificacionesComIsNull() {
            addCriterion("RECIBE_NOTIFICACIONES_COM is null");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComIsNotNull() {
            addCriterion("RECIBE_NOTIFICACIONES_COM is not null");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComEqualTo(Boolean value) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM =", value, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComNotEqualTo(Boolean value) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM <>", value, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComGreaterThan(Boolean value) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM >", value, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComGreaterThanOrEqualTo(Boolean value) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM >=", value, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComLessThan(Boolean value) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM <", value, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComLessThanOrEqualTo(Boolean value) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM <=", value, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComLike(Boolean value) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM like", value, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComNotLike(Boolean value) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM not like", value, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComIn(List<Boolean> values) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM in", values, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComNotIn(List<Boolean> values) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM not in", values, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComBetween(Boolean value1, Boolean value2) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM between", value1, value2, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andRecibeNotificacionesComNotBetween(Boolean value1, Boolean value2) {
            addRecibeNotificacionesComCriterion("RECIBE_NOTIFICACIONES_COM not between", value1, value2, "recibeNotificacionesCom");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andCodTipoConLikeInsensitive(String value) {
            addCriterion("upper(CODTIPOCON) like", value.toUpperCase(), "codTipoCon");
            return (Criteria) this;
        }

        public Criteria andValorLikeInsensitive(String value) {
            addCriterion("upper(VALOR) like", value.toUpperCase(), "valor");
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