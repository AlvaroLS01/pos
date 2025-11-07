package com.comerzzia.api.loyalty.persistence.cardsTypes;

import java.util.ArrayList;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiActivity;

public class TipoTarjetaExample extends MultiActivity {
    public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_CODTIPOTARJ = "CODTIPOTARJ";

    public static final String ORDER_BY_CODTIPOTARJ_DESC = "CODTIPOTARJ DESC";

    public static final String ORDER_BY_DESTIPOTARJ = "DESTIPOTARJ";

    public static final String ORDER_BY_DESTIPOTARJ_DESC = "DESTIPOTARJ DESC";

    public static final String ORDER_BY_PERMITE_VINCULAR = "PERMITE_VINCULAR";

    public static final String ORDER_BY_PERMITE_VINCULAR_DESC = "PERMITE_VINCULAR DESC";

    public static final String ORDER_BY_PREFIJO = "PREFIJO";

    public static final String ORDER_BY_PREFIJO_DESC = "PREFIJO DESC";

    public static final String ORDER_BY_LONGITUD_TOTAL = "LONGITUD_TOTAL";

    public static final String ORDER_BY_LONGITUD_TOTAL_DESC = "LONGITUD_TOTAL DESC";

    public static final String ORDER_BY_PERMITE_PAGO = "PERMITE_PAGO";

    public static final String ORDER_BY_PERMITE_PAGO_DESC = "PERMITE_PAGO DESC";

    public static final String ORDER_BY_VISIBLE_EN_PAGO = "VISIBLE_EN_PAGO";

    public static final String ORDER_BY_VISIBLE_EN_PAGO_DESC = "VISIBLE_EN_PAGO DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TipoTarjetaExample() {
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
        protected List<Criterion> permiteVincularCriteria;

        protected List<Criterion> permitePagoCriteria;

        protected List<Criterion> visibleEnPagoCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
            permiteVincularCriteria = new ArrayList<Criterion>();
            permitePagoCriteria = new ArrayList<Criterion>();
            visibleEnPagoCriteria = new ArrayList<Criterion>();
        }

        public List<Criterion> getPermiteVincularCriteria() {
            return permiteVincularCriteria;
        }

        protected void addPermiteVincularCriterion(String condition, Object value, String property) {
            if (value != null) {
                permiteVincularCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        protected void addPermiteVincularCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 != null && value2 != null) {
                permiteVincularCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        public List<Criterion> getPermitePagoCriteria() {
            return permitePagoCriteria;
        }

        protected void addPermitePagoCriterion(String condition, Object value, String property) {
            if (value != null) {
                permitePagoCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        protected void addPermitePagoCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 != null && value2 != null) {
                permitePagoCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        public List<Criterion> getVisibleEnPagoCriteria() {
            return visibleEnPagoCriteria;
        }

        protected void addVisibleEnPagoCriterion(String condition, Object value, String property) {
            if (value != null) {
                visibleEnPagoCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        protected void addVisibleEnPagoCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 != null && value2 != null) {
                visibleEnPagoCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanStringTypeHandler"));
                allCriteria = null;
            }
        }

        public boolean isValid() {
            return criteria.size() > 0
                || permiteVincularCriteria.size() > 0
                || permitePagoCriteria.size() > 0
                || visibleEnPagoCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<Criterion>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(permiteVincularCriteria);
                allCriteria.addAll(permitePagoCriteria);
                allCriteria.addAll(visibleEnPagoCriteria);
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

        public Criteria andCodtipotarjIsNull() {
            addCriterion("CODTIPOTARJ is null");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjIsNotNull() {
            addCriterion("CODTIPOTARJ is not null");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjEqualTo(String value) {
            addCriterion("CODTIPOTARJ =", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjNotEqualTo(String value) {
            addCriterion("CODTIPOTARJ <>", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjGreaterThan(String value) {
            addCriterion("CODTIPOTARJ >", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjGreaterThanOrEqualTo(String value) {
            addCriterion("CODTIPOTARJ >=", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjLessThan(String value) {
            addCriterion("CODTIPOTARJ <", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjLessThanOrEqualTo(String value) {
            addCriterion("CODTIPOTARJ <=", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjLike(String value) {
            addCriterion("CODTIPOTARJ like", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjNotLike(String value) {
            addCriterion("CODTIPOTARJ not like", value, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjIn(List<String> values) {
            addCriterion("CODTIPOTARJ in", values, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjNotIn(List<String> values) {
            addCriterion("CODTIPOTARJ not in", values, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjBetween(String value1, String value2) {
            addCriterion("CODTIPOTARJ between", value1, value2, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjNotBetween(String value1, String value2) {
            addCriterion("CODTIPOTARJ not between", value1, value2, "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjIsNull() {
            addCriterion("DESTIPOTARJ is null");
            return (Criteria) this;
        }

        public Criteria andDestipotarjIsNotNull() {
            addCriterion("DESTIPOTARJ is not null");
            return (Criteria) this;
        }

        public Criteria andDestipotarjEqualTo(String value) {
            addCriterion("DESTIPOTARJ =", value, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjNotEqualTo(String value) {
            addCriterion("DESTIPOTARJ <>", value, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjGreaterThan(String value) {
            addCriterion("DESTIPOTARJ >", value, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjGreaterThanOrEqualTo(String value) {
            addCriterion("DESTIPOTARJ >=", value, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjLessThan(String value) {
            addCriterion("DESTIPOTARJ <", value, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjLessThanOrEqualTo(String value) {
            addCriterion("DESTIPOTARJ <=", value, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjLike(String value) {
            addCriterion("DESTIPOTARJ like", value, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjNotLike(String value) {
            addCriterion("DESTIPOTARJ not like", value, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjIn(List<String> values) {
            addCriterion("DESTIPOTARJ in", values, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjNotIn(List<String> values) {
            addCriterion("DESTIPOTARJ not in", values, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjBetween(String value1, String value2) {
            addCriterion("DESTIPOTARJ between", value1, value2, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjNotBetween(String value1, String value2) {
            addCriterion("DESTIPOTARJ not between", value1, value2, "destipotarj");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularIsNull() {
            addCriterion("PERMITE_VINCULAR is null");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularIsNotNull() {
            addCriterion("PERMITE_VINCULAR is not null");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularEqualTo(Boolean value) {
            addPermiteVincularCriterion("PERMITE_VINCULAR =", value, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularNotEqualTo(Boolean value) {
            addPermiteVincularCriterion("PERMITE_VINCULAR <>", value, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularGreaterThan(Boolean value) {
            addPermiteVincularCriterion("PERMITE_VINCULAR >", value, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularGreaterThanOrEqualTo(Boolean value) {
            addPermiteVincularCriterion("PERMITE_VINCULAR >=", value, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularLessThan(Boolean value) {
            addPermiteVincularCriterion("PERMITE_VINCULAR <", value, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularLessThanOrEqualTo(Boolean value) {
            addPermiteVincularCriterion("PERMITE_VINCULAR <=", value, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularLike(Boolean value) {
            addPermiteVincularCriterion("PERMITE_VINCULAR like", value, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularNotLike(Boolean value) {
            addPermiteVincularCriterion("PERMITE_VINCULAR not like", value, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularIn(List<Boolean> values) {
            addPermiteVincularCriterion("PERMITE_VINCULAR in", values, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularNotIn(List<Boolean> values) {
            addPermiteVincularCriterion("PERMITE_VINCULAR not in", values, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularBetween(Boolean value1, Boolean value2) {
            addPermiteVincularCriterion("PERMITE_VINCULAR between", value1, value2, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPermiteVincularNotBetween(Boolean value1, Boolean value2) {
            addPermiteVincularCriterion("PERMITE_VINCULAR not between", value1, value2, "permiteVincular");
            return (Criteria) this;
        }

        public Criteria andPrefijoIsNull() {
            addCriterion("PREFIJO is null");
            return (Criteria) this;
        }

        public Criteria andPrefijoIsNotNull() {
            addCriterion("PREFIJO is not null");
            return (Criteria) this;
        }

        public Criteria andPrefijoEqualTo(String value) {
            addCriterion("PREFIJO =", value, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoNotEqualTo(String value) {
            addCriterion("PREFIJO <>", value, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoGreaterThan(String value) {
            addCriterion("PREFIJO >", value, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoGreaterThanOrEqualTo(String value) {
            addCriterion("PREFIJO >=", value, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoLessThan(String value) {
            addCriterion("PREFIJO <", value, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoLessThanOrEqualTo(String value) {
            addCriterion("PREFIJO <=", value, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoLike(String value) {
            addCriterion("PREFIJO like", value, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoNotLike(String value) {
            addCriterion("PREFIJO not like", value, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoIn(List<String> values) {
            addCriterion("PREFIJO in", values, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoNotIn(List<String> values) {
            addCriterion("PREFIJO not in", values, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoBetween(String value1, String value2) {
            addCriterion("PREFIJO between", value1, value2, "prefijo");
            return (Criteria) this;
        }

        public Criteria andPrefijoNotBetween(String value1, String value2) {
            addCriterion("PREFIJO not between", value1, value2, "prefijo");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalIsNull() {
            addCriterion("LONGITUD_TOTAL is null");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalIsNotNull() {
            addCriterion("LONGITUD_TOTAL is not null");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalEqualTo(Short value) {
            addCriterion("LONGITUD_TOTAL =", value, "longitudTotal");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalNotEqualTo(Short value) {
            addCriterion("LONGITUD_TOTAL <>", value, "longitudTotal");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalGreaterThan(Short value) {
            addCriterion("LONGITUD_TOTAL >", value, "longitudTotal");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalGreaterThanOrEqualTo(Short value) {
            addCriterion("LONGITUD_TOTAL >=", value, "longitudTotal");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalLessThan(Short value) {
            addCriterion("LONGITUD_TOTAL <", value, "longitudTotal");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalLessThanOrEqualTo(Short value) {
            addCriterion("LONGITUD_TOTAL <=", value, "longitudTotal");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalIn(List<Short> values) {
            addCriterion("LONGITUD_TOTAL in", values, "longitudTotal");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalNotIn(List<Short> values) {
            addCriterion("LONGITUD_TOTAL not in", values, "longitudTotal");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalBetween(Short value1, Short value2) {
            addCriterion("LONGITUD_TOTAL between", value1, value2, "longitudTotal");
            return (Criteria) this;
        }

        public Criteria andLongitudTotalNotBetween(Short value1, Short value2) {
            addCriterion("LONGITUD_TOTAL not between", value1, value2, "longitudTotal");
            return (Criteria) this;
        }

        public Criteria andPermitePagoIsNull() {
            addCriterion("PERMITE_PAGO is null");
            return (Criteria) this;
        }

        public Criteria andPermitePagoIsNotNull() {
            addCriterion("PERMITE_PAGO is not null");
            return (Criteria) this;
        }

        public Criteria andPermitePagoEqualTo(Boolean value) {
            addPermitePagoCriterion("PERMITE_PAGO =", value, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoNotEqualTo(Boolean value) {
            addPermitePagoCriterion("PERMITE_PAGO <>", value, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoGreaterThan(Boolean value) {
            addPermitePagoCriterion("PERMITE_PAGO >", value, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoGreaterThanOrEqualTo(Boolean value) {
            addPermitePagoCriterion("PERMITE_PAGO >=", value, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoLessThan(Boolean value) {
            addPermitePagoCriterion("PERMITE_PAGO <", value, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoLessThanOrEqualTo(Boolean value) {
            addPermitePagoCriterion("PERMITE_PAGO <=", value, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoLike(Boolean value) {
            addPermitePagoCriterion("PERMITE_PAGO like", value, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoNotLike(Boolean value) {
            addPermitePagoCriterion("PERMITE_PAGO not like", value, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoIn(List<Boolean> values) {
            addPermitePagoCriterion("PERMITE_PAGO in", values, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoNotIn(List<Boolean> values) {
            addPermitePagoCriterion("PERMITE_PAGO not in", values, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoBetween(Boolean value1, Boolean value2) {
            addPermitePagoCriterion("PERMITE_PAGO between", value1, value2, "permitePago");
            return (Criteria) this;
        }

        public Criteria andPermitePagoNotBetween(Boolean value1, Boolean value2) {
            addPermitePagoCriterion("PERMITE_PAGO not between", value1, value2, "permitePago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoIsNull() {
            addCriterion("VISIBLE_EN_PAGO is null");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoIsNotNull() {
            addCriterion("VISIBLE_EN_PAGO is not null");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoEqualTo(Boolean value) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO =", value, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoNotEqualTo(Boolean value) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO <>", value, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoGreaterThan(Boolean value) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO >", value, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoGreaterThanOrEqualTo(Boolean value) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO >=", value, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoLessThan(Boolean value) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO <", value, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoLessThanOrEqualTo(Boolean value) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO <=", value, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoLike(Boolean value) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO like", value, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoNotLike(Boolean value) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO not like", value, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoIn(List<Boolean> values) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO in", values, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoNotIn(List<Boolean> values) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO not in", values, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoBetween(Boolean value1, Boolean value2) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO between", value1, value2, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andVisibleEnPagoNotBetween(Boolean value1, Boolean value2) {
            addVisibleEnPagoCriterion("VISIBLE_EN_PAGO not between", value1, value2, "visibleEnPago");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andCodtipotarjLikeInsensitive(String value) {
            addCriterion("upper(CODTIPOTARJ) like", value.toUpperCase(), "codtipotarj");
            return (Criteria) this;
        }

        public Criteria andDestipotarjLikeInsensitive(String value) {
            addCriterion("upper(DESTIPOTARJ) like", value.toUpperCase(), "destipotarj");
            return (Criteria) this;
        }

        public Criteria andPrefijoLikeInsensitive(String value) {
            addCriterion("upper(PREFIJO) like", value.toUpperCase(), "prefijo");
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