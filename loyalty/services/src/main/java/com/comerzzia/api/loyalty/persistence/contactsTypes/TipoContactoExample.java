package com.comerzzia.api.loyalty.persistence.contactsTypes;

import java.util.ArrayList;
import java.util.List;

public class TipoContactoExample {
    public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_CODTIPOCON = "CODTIPOCON";

    public static final String ORDER_BY_CODTIPOCON_DESC = "CODTIPOCON DESC";

    public static final String ORDER_BY_DESTIPOCON = "DESTIPOCON";

    public static final String ORDER_BY_DESTIPOCON_DESC = "DESTIPOCON DESC";

    public static final String ORDER_BY_PUEDE_RECIBIR_NOTIFICACIONES = "PUEDE_RECIBIR_NOTIFICACIONES";

    public static final String ORDER_BY_PUEDE_RECIBIR_NOTIFICACIONES_DESC = "PUEDE_RECIBIR_NOTIFICACIONES DESC";

    public static final String ORDER_BY_ID_BUZON = "ID_BUZON";

    public static final String ORDER_BY_ID_BUZON_DESC = "ID_BUZON DESC";

    public static final String ORDER_BY_ACTIVO = "ACTIVO";

    public static final String ORDER_BY_ACTIVO_DESC = "ACTIVO DESC";

    public static final String ORDER_BY_VISIBLE = "VISIBLE";

    public static final String ORDER_BY_VISIBLE_DESC = "VISIBLE DESC";

    public static final String ORDER_BY_ORDEN = "ORDEN";

    public static final String ORDER_BY_ORDEN_DESC = "ORDEN DESC";

    public static final String ORDER_BY_PROTOCOLO = "PROTOCOLO";

    public static final String ORDER_BY_PROTOCOLO_DESC = "PROTOCOLO DESC";

    public static final String ORDER_BY_BUZON_DESCRIPCION = "BUZON_DESCRIPCION";

    public static final String ORDER_BY_BUZON_DESCRIPCION_DESC = "BUZON_DESCRIPCION DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TipoContactoExample() {
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

        public Criteria andDesTipoConIsNull() {
            addCriterion("DESTIPOCON is null");
            return (Criteria) this;
        }

        public Criteria andDesTipoConIsNotNull() {
            addCriterion("DESTIPOCON is not null");
            return (Criteria) this;
        }

        public Criteria andDesTipoConEqualTo(String value) {
            addCriterion("DESTIPOCON =", value, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConNotEqualTo(String value) {
            addCriterion("DESTIPOCON <>", value, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConGreaterThan(String value) {
            addCriterion("DESTIPOCON >", value, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConGreaterThanOrEqualTo(String value) {
            addCriterion("DESTIPOCON >=", value, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConLessThan(String value) {
            addCriterion("DESTIPOCON <", value, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConLessThanOrEqualTo(String value) {
            addCriterion("DESTIPOCON <=", value, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConLike(String value) {
            addCriterion("DESTIPOCON like", value, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConNotLike(String value) {
            addCriterion("DESTIPOCON not like", value, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConIn(List<String> values) {
            addCriterion("DESTIPOCON in", values, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConNotIn(List<String> values) {
            addCriterion("DESTIPOCON not in", values, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConBetween(String value1, String value2) {
            addCriterion("DESTIPOCON between", value1, value2, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andDesTipoConNotBetween(String value1, String value2) {
            addCriterion("DESTIPOCON not between", value1, value2, "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesIsNull() {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES is null");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesIsNotNull() {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES is not null");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesEqualTo(String value) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES =", value, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesNotEqualTo(String value) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES <>", value, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesGreaterThan(String value) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES >", value, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesGreaterThanOrEqualTo(String value) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES >=", value, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesLessThan(String value) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES <", value, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesLessThanOrEqualTo(String value) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES <=", value, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesLike(String value) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES like", value, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesNotLike(String value) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES not like", value, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesIn(List<String> values) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES in", values, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesNotIn(List<String> values) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES not in", values, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesBetween(String value1, String value2) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES between", value1, value2, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesNotBetween(String value1, String value2) {
            addCriterion("PUEDE_RECIBIR_NOTIFICACIONES not between", value1, value2, "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andIdBuzonIsNull() {
            addCriterion("ID_BUZON is null");
            return (Criteria) this;
        }

        public Criteria andIdBuzonIsNotNull() {
            addCriterion("ID_BUZON is not null");
            return (Criteria) this;
        }

        public Criteria andIdBuzonEqualTo(Long value) {
            addCriterion("ID_BUZON =", value, "idBuzon");
            return (Criteria) this;
        }

        public Criteria andIdBuzonNotEqualTo(Long value) {
            addCriterion("ID_BUZON <>", value, "idBuzon");
            return (Criteria) this;
        }

        public Criteria andIdBuzonGreaterThan(Long value) {
            addCriterion("ID_BUZON >", value, "idBuzon");
            return (Criteria) this;
        }

        public Criteria andIdBuzonGreaterThanOrEqualTo(Long value) {
            addCriterion("ID_BUZON >=", value, "idBuzon");
            return (Criteria) this;
        }

        public Criteria andIdBuzonLessThan(Long value) {
            addCriterion("ID_BUZON <", value, "idBuzon");
            return (Criteria) this;
        }

        public Criteria andIdBuzonLessThanOrEqualTo(Long value) {
            addCriterion("ID_BUZON <=", value, "idBuzon");
            return (Criteria) this;
        }

        public Criteria andIdBuzonIn(List<Long> values) {
            addCriterion("ID_BUZON in", values, "idBuzon");
            return (Criteria) this;
        }

        public Criteria andIdBuzonNotIn(List<Long> values) {
            addCriterion("ID_BUZON not in", values, "idBuzon");
            return (Criteria) this;
        }

        public Criteria andIdBuzonBetween(Long value1, Long value2) {
            addCriterion("ID_BUZON between", value1, value2, "idBuzon");
            return (Criteria) this;
        }

        public Criteria andIdBuzonNotBetween(Long value1, Long value2) {
            addCriterion("ID_BUZON not between", value1, value2, "idBuzon");
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

        public Criteria andVisibleIsNull() {
            addCriterion("VISIBLE is null");
            return (Criteria) this;
        }

        public Criteria andVisibleIsNotNull() {
            addCriterion("VISIBLE is not null");
            return (Criteria) this;
        }

        public Criteria andVisibleEqualTo(String value) {
            addCriterion("VISIBLE =", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotEqualTo(String value) {
            addCriterion("VISIBLE <>", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThan(String value) {
            addCriterion("VISIBLE >", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThanOrEqualTo(String value) {
            addCriterion("VISIBLE >=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThan(String value) {
            addCriterion("VISIBLE <", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThanOrEqualTo(String value) {
            addCriterion("VISIBLE <=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLike(String value) {
            addCriterion("VISIBLE like", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotLike(String value) {
            addCriterion("VISIBLE not like", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleIn(List<String> values) {
            addCriterion("VISIBLE in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotIn(List<String> values) {
            addCriterion("VISIBLE not in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleBetween(String value1, String value2) {
            addCriterion("VISIBLE between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotBetween(String value1, String value2) {
            addCriterion("VISIBLE not between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andOrdenIsNull() {
            addCriterion("ORDEN is null");
            return (Criteria) this;
        }

        public Criteria andOrdenIsNotNull() {
            addCriterion("ORDEN is not null");
            return (Criteria) this;
        }

        public Criteria andOrdenEqualTo(Short value) {
            addCriterion("ORDEN =", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenNotEqualTo(Short value) {
            addCriterion("ORDEN <>", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenGreaterThan(Short value) {
            addCriterion("ORDEN >", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenGreaterThanOrEqualTo(Short value) {
            addCriterion("ORDEN >=", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenLessThan(Short value) {
            addCriterion("ORDEN <", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenLessThanOrEqualTo(Short value) {
            addCriterion("ORDEN <=", value, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenIn(List<Short> values) {
            addCriterion("ORDEN in", values, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenNotIn(List<Short> values) {
            addCriterion("ORDEN not in", values, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenBetween(Short value1, Short value2) {
            addCriterion("ORDEN between", value1, value2, "orden");
            return (Criteria) this;
        }

        public Criteria andOrdenNotBetween(Short value1, Short value2) {
            addCriterion("ORDEN not between", value1, value2, "orden");
            return (Criteria) this;
        }

        public Criteria andProtocoloIsNull() {
            addCriterion("PROTOCOLO is null");
            return (Criteria) this;
        }

        public Criteria andProtocoloIsNotNull() {
            addCriterion("PROTOCOLO is not null");
            return (Criteria) this;
        }

        public Criteria andProtocoloEqualTo(String value) {
            addCriterion("PROTOCOLO =", value, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloNotEqualTo(String value) {
            addCriterion("PROTOCOLO <>", value, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloGreaterThan(String value) {
            addCriterion("PROTOCOLO >", value, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloGreaterThanOrEqualTo(String value) {
            addCriterion("PROTOCOLO >=", value, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloLessThan(String value) {
            addCriterion("PROTOCOLO <", value, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloLessThanOrEqualTo(String value) {
            addCriterion("PROTOCOLO <=", value, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloLike(String value) {
            addCriterion("PROTOCOLO like", value, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloNotLike(String value) {
            addCriterion("PROTOCOLO not like", value, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloIn(List<String> values) {
            addCriterion("PROTOCOLO in", values, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloNotIn(List<String> values) {
            addCriterion("PROTOCOLO not in", values, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloBetween(String value1, String value2) {
            addCriterion("PROTOCOLO between", value1, value2, "protocolo");
            return (Criteria) this;
        }

        public Criteria andProtocoloNotBetween(String value1, String value2) {
            addCriterion("PROTOCOLO not between", value1, value2, "protocolo");
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

        public Criteria andDesTipoConLikeInsensitive(String value) {
            addCriterion("upper(DESTIPOCON) like", value.toUpperCase(), "desTipoCon");
            return (Criteria) this;
        }

        public Criteria andPuedeRecibirNotificacionesLikeInsensitive(String value) {
            addCriterion("upper(PUEDE_RECIBIR_NOTIFICACIONES) like", value.toUpperCase(), "puedeRecibirNotificaciones");
            return (Criteria) this;
        }

        public Criteria andVisibleLikeInsensitive(String value) {
            addCriterion("upper(VISIBLE) like", value.toUpperCase(), "visible");
            return (Criteria) this;
        }

        public Criteria andProtocoloLikeInsensitive(String value) {
            addCriterion("upper(PROTOCOLO) like", value.toUpperCase(), "protocolo");
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