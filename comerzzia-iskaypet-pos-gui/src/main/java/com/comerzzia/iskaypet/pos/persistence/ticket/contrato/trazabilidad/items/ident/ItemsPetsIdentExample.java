package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident;

import java.util.ArrayList;
import java.util.List;

public class ItemsPetsIdentExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ItemsPetsIdentExample() {
        oredCriteria = new ArrayList<>();
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
            criteria = new ArrayList<>();
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

        public Criteria andCodalmIsNull() {
            addCriterion("CODALM is null");
            return (Criteria) this;
        }

        public Criteria andCodalmIsNotNull() {
            addCriterion("CODALM is not null");
            return (Criteria) this;
        }

        public Criteria andCodalmEqualTo(String value) {
            addCriterion("CODALM =", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmNotEqualTo(String value) {
            addCriterion("CODALM <>", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmGreaterThan(String value) {
            addCriterion("CODALM >", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmGreaterThanOrEqualTo(String value) {
            addCriterion("CODALM >=", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmLessThan(String value) {
            addCriterion("CODALM <", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmLessThanOrEqualTo(String value) {
            addCriterion("CODALM <=", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmLike(String value) {
            addCriterion("CODALM like", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmNotLike(String value) {
            addCriterion("CODALM not like", value, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmIn(List<String> values) {
            addCriterion("CODALM in", values, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmNotIn(List<String> values) {
            addCriterion("CODALM not in", values, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmBetween(String value1, String value2) {
            addCriterion("CODALM between", value1, value2, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodalmNotBetween(String value1, String value2) {
            addCriterion("CODALM not between", value1, value2, "codalm");
            return (Criteria) this;
        }

        public Criteria andCodartIsNull() {
            addCriterion("CODART is null");
            return (Criteria) this;
        }

        public Criteria andCodartIsNotNull() {
            addCriterion("CODART is not null");
            return (Criteria) this;
        }

        public Criteria andCodartEqualTo(String value) {
            addCriterion("CODART =", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartNotEqualTo(String value) {
            addCriterion("CODART <>", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartGreaterThan(String value) {
            addCriterion("CODART >", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartGreaterThanOrEqualTo(String value) {
            addCriterion("CODART >=", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartLessThan(String value) {
            addCriterion("CODART <", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartLessThanOrEqualTo(String value) {
            addCriterion("CODART <=", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartLike(String value) {
            addCriterion("CODART like", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartNotLike(String value) {
            addCriterion("CODART not like", value, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartIn(List<String> values) {
            addCriterion("CODART in", values, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartNotIn(List<String> values) {
            addCriterion("CODART not in", values, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartBetween(String value1, String value2) {
            addCriterion("CODART between", value1, value2, "codart");
            return (Criteria) this;
        }

        public Criteria andCodartNotBetween(String value1, String value2) {
            addCriterion("CODART not between", value1, value2, "codart");
            return (Criteria) this;
        }

        public Criteria andDesglose1IsNull() {
            addCriterion("DESGLOSE1 is null");
            return (Criteria) this;
        }

        public Criteria andDesglose1IsNotNull() {
            addCriterion("DESGLOSE1 is not null");
            return (Criteria) this;
        }

        public Criteria andDesglose1EqualTo(String value) {
            addCriterion("DESGLOSE1 =", value, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1NotEqualTo(String value) {
            addCriterion("DESGLOSE1 <>", value, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1GreaterThan(String value) {
            addCriterion("DESGLOSE1 >", value, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1GreaterThanOrEqualTo(String value) {
            addCriterion("DESGLOSE1 >=", value, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1LessThan(String value) {
            addCriterion("DESGLOSE1 <", value, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1LessThanOrEqualTo(String value) {
            addCriterion("DESGLOSE1 <=", value, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1Like(String value) {
            addCriterion("DESGLOSE1 like", value, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1NotLike(String value) {
            addCriterion("DESGLOSE1 not like", value, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1In(List<String> values) {
            addCriterion("DESGLOSE1 in", values, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1NotIn(List<String> values) {
            addCriterion("DESGLOSE1 not in", values, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1Between(String value1, String value2) {
            addCriterion("DESGLOSE1 between", value1, value2, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose1NotBetween(String value1, String value2) {
            addCriterion("DESGLOSE1 not between", value1, value2, "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose2IsNull() {
            addCriterion("DESGLOSE2 is null");
            return (Criteria) this;
        }

        public Criteria andDesglose2IsNotNull() {
            addCriterion("DESGLOSE2 is not null");
            return (Criteria) this;
        }

        public Criteria andDesglose2EqualTo(String value) {
            addCriterion("DESGLOSE2 =", value, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2NotEqualTo(String value) {
            addCriterion("DESGLOSE2 <>", value, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2GreaterThan(String value) {
            addCriterion("DESGLOSE2 >", value, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2GreaterThanOrEqualTo(String value) {
            addCriterion("DESGLOSE2 >=", value, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2LessThan(String value) {
            addCriterion("DESGLOSE2 <", value, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2LessThanOrEqualTo(String value) {
            addCriterion("DESGLOSE2 <=", value, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2Like(String value) {
            addCriterion("DESGLOSE2 like", value, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2NotLike(String value) {
            addCriterion("DESGLOSE2 not like", value, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2In(List<String> values) {
            addCriterion("DESGLOSE2 in", values, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2NotIn(List<String> values) {
            addCriterion("DESGLOSE2 not in", values, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2Between(String value1, String value2) {
            addCriterion("DESGLOSE2 between", value1, value2, "desglose2");
            return (Criteria) this;
        }

        public Criteria andDesglose2NotBetween(String value1, String value2) {
            addCriterion("DESGLOSE2 not between", value1, value2, "desglose2");
            return (Criteria) this;
        }

        public Criteria andLineaIsNull() {
            addCriterion("LINEA is null");
            return (Criteria) this;
        }

        public Criteria andLineaIsNotNull() {
            addCriterion("LINEA is not null");
            return (Criteria) this;
        }

        public Criteria andLineaEqualTo(Integer value) {
            addCriterion("LINEA =", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaNotEqualTo(Integer value) {
            addCriterion("LINEA <>", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaGreaterThan(Integer value) {
            addCriterion("LINEA >", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaGreaterThanOrEqualTo(Integer value) {
            addCriterion("LINEA >=", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaLessThan(Integer value) {
            addCriterion("LINEA <", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaLessThanOrEqualTo(Integer value) {
            addCriterion("LINEA <=", value, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaIn(List<Integer> values) {
            addCriterion("LINEA in", values, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaNotIn(List<Integer> values) {
            addCriterion("LINEA not in", values, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaBetween(Integer value1, Integer value2) {
            addCriterion("LINEA between", value1, value2, "linea");
            return (Criteria) this;
        }

        public Criteria andLineaNotBetween(Integer value1, Integer value2) {
            addCriterion("LINEA not between", value1, value2, "linea");
            return (Criteria) this;
        }

        public Criteria andChipIsNull() {
            addCriterion("CHIP is null");
            return (Criteria) this;
        }

        public Criteria andChipIsNotNull() {
            addCriterion("CHIP is not null");
            return (Criteria) this;
        }

        public Criteria andChipEqualTo(String value) {
            addCriterion("CHIP =", value, "chip");
            return (Criteria) this;
        }

        public Criteria andChipNotEqualTo(String value) {
            addCriterion("CHIP <>", value, "chip");
            return (Criteria) this;
        }

        public Criteria andChipGreaterThan(String value) {
            addCriterion("CHIP >", value, "chip");
            return (Criteria) this;
        }

        public Criteria andChipGreaterThanOrEqualTo(String value) {
            addCriterion("CHIP >=", value, "chip");
            return (Criteria) this;
        }

        public Criteria andChipLessThan(String value) {
            addCriterion("CHIP <", value, "chip");
            return (Criteria) this;
        }

        public Criteria andChipLessThanOrEqualTo(String value) {
            addCriterion("CHIP <=", value, "chip");
            return (Criteria) this;
        }

        public Criteria andChipLike(String value) {
            addCriterion("CHIP like", value, "chip");
            return (Criteria) this;
        }

        public Criteria andChipNotLike(String value) {
            addCriterion("CHIP not like", value, "chip");
            return (Criteria) this;
        }

        public Criteria andChipIn(List<String> values) {
            addCriterion("CHIP in", values, "chip");
            return (Criteria) this;
        }

        public Criteria andChipNotIn(List<String> values) {
            addCriterion("CHIP not in", values, "chip");
            return (Criteria) this;
        }

        public Criteria andChipBetween(String value1, String value2) {
            addCriterion("CHIP between", value1, value2, "chip");
            return (Criteria) this;
        }

        public Criteria andChipNotBetween(String value1, String value2) {
            addCriterion("CHIP not between", value1, value2, "chip");
            return (Criteria) this;
        }

        public Criteria andAnillaIsNull() {
            addCriterion("ANILLA is null");
            return (Criteria) this;
        }

        public Criteria andAnillaIsNotNull() {
            addCriterion("ANILLA is not null");
            return (Criteria) this;
        }

        public Criteria andAnillaEqualTo(String value) {
            addCriterion("ANILLA =", value, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaNotEqualTo(String value) {
            addCriterion("ANILLA <>", value, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaGreaterThan(String value) {
            addCriterion("ANILLA >", value, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaGreaterThanOrEqualTo(String value) {
            addCriterion("ANILLA >=", value, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaLessThan(String value) {
            addCriterion("ANILLA <", value, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaLessThanOrEqualTo(String value) {
            addCriterion("ANILLA <=", value, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaLike(String value) {
            addCriterion("ANILLA like", value, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaNotLike(String value) {
            addCriterion("ANILLA not like", value, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaIn(List<String> values) {
            addCriterion("ANILLA in", values, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaNotIn(List<String> values) {
            addCriterion("ANILLA not in", values, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaBetween(String value1, String value2) {
            addCriterion("ANILLA between", value1, value2, "anilla");
            return (Criteria) this;
        }

        public Criteria andAnillaNotBetween(String value1, String value2) {
            addCriterion("ANILLA not between", value1, value2, "anilla");
            return (Criteria) this;
        }

        public Criteria andCitesIsNull() {
            addCriterion("CITES is null");
            return (Criteria) this;
        }

        public Criteria andCitesIsNotNull() {
            addCriterion("CITES is not null");
            return (Criteria) this;
        }

        public Criteria andCitesEqualTo(String value) {
            addCriterion("CITES =", value, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesNotEqualTo(String value) {
            addCriterion("CITES <>", value, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesGreaterThan(String value) {
            addCriterion("CITES >", value, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesGreaterThanOrEqualTo(String value) {
            addCriterion("CITES >=", value, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesLessThan(String value) {
            addCriterion("CITES <", value, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesLessThanOrEqualTo(String value) {
            addCriterion("CITES <=", value, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesLike(String value) {
            addCriterion("CITES like", value, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesNotLike(String value) {
            addCriterion("CITES not like", value, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesIn(List<String> values) {
            addCriterion("CITES in", values, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesNotIn(List<String> values) {
            addCriterion("CITES not in", values, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesBetween(String value1, String value2) {
            addCriterion("CITES between", value1, value2, "cites");
            return (Criteria) this;
        }

        public Criteria andCitesNotBetween(String value1, String value2) {
            addCriterion("CITES not between", value1, value2, "cites");
            return (Criteria) this;
        }

        public Criteria andVersionIsNull() {
            addCriterion("VERSION is null");
            return (Criteria) this;
        }

        public Criteria andVersionIsNotNull() {
            addCriterion("VERSION is not null");
            return (Criteria) this;
        }

        public Criteria andVersionEqualTo(Long value) {
            addCriterion("VERSION =", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotEqualTo(Long value) {
            addCriterion("VERSION <>", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThan(Long value) {
            addCriterion("VERSION >", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThanOrEqualTo(Long value) {
            addCriterion("VERSION >=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThan(Long value) {
            addCriterion("VERSION <", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThanOrEqualTo(Long value) {
            addCriterion("VERSION <=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionIn(List<Long> values) {
            addCriterion("VERSION in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotIn(List<Long> values) {
            addCriterion("VERSION not in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionBetween(Long value1, Long value2) {
            addCriterion("VERSION between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotBetween(Long value1, Long value2) {
            addCriterion("VERSION not between", value1, value2, "version");
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