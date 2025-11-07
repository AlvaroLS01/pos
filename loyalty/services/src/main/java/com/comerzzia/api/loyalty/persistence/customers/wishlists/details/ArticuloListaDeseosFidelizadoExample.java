package com.comerzzia.api.loyalty.persistence.customers.wishlists.details;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ArticuloListaDeseosFidelizadoExample {
    public static final String ORDER_BY_UID_ACTIVIDAD = "UID_ACTIVIDAD";

    public static final String ORDER_BY_UID_ACTIVIDAD_DESC = "UID_ACTIVIDAD DESC";

    public static final String ORDER_BY_UID_LISTA_DESEOS = "UID_LISTA_DESEOS";

    public static final String ORDER_BY_UID_LISTA_DESEOS_DESC = "UID_LISTA_DESEOS DESC";

    public static final String ORDER_BY_LINEA = "LINEA";

    public static final String ORDER_BY_LINEA_DESC = "LINEA DESC";

    public static final String ORDER_BY_CODART = "CODART";

    public static final String ORDER_BY_CODART_DESC = "CODART DESC";

    public static final String ORDER_BY_DESGLOSE1 = "DESGLOSE1";

    public static final String ORDER_BY_DESGLOSE1_DESC = "DESGLOSE1 DESC";

    public static final String ORDER_BY_DESGLOSE2 = "DESGLOSE2";

    public static final String ORDER_BY_DESGLOSE2_DESC = "DESGLOSE2 DESC";

    public static final String ORDER_BY_CANTIDAD = "CANTIDAD";

    public static final String ORDER_BY_CANTIDAD_DESC = "CANTIDAD DESC";

    public static final String ORDER_BY_UNIDAD_MEDIDA = "UNIDAD_MEDIDA";

    public static final String ORDER_BY_UNIDAD_MEDIDA_DESC = "UNIDAD_MEDIDA DESC";

    public static final String ORDER_BY_CANTIDAD_MEDIDA = "CANTIDAD_MEDIDA";

    public static final String ORDER_BY_CANTIDAD_MEDIDA_DESC = "CANTIDAD_MEDIDA DESC";
    
    
    
    public static final String ORDER_BY_ARTICULOS_CODART = "ARTICULOS.CODART";

    public static final String ORDER_BY_ARTICULOS_CODART_DESC = "ARTICULOS.CODART DESC";
    
    public static final String ORDER_BY_DESART = "DESART";

    public static final String ORDER_BY_DESART_DESC = "DESART DESC";
    
    public static final String ORDER_BY_LISTAS_DESGLOSE1 = "LISTAS.DESGLOSE1";

    public static final String ORDER_BY_LISTAS_DESGLOSE1_DESC = "LISTAS.DESGLOSE1 DESC";
    
    public static final String ORDER_BY_LISTAS_DESGLOSE2 = "LISTAS.DESGLOSE2";

    public static final String ORDER_BY_LISTAS_DESGLOSE2_DESC = "LISTAS.DESGLOSE2 DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ArticuloListaDeseosFidelizadoExample() {
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

        public Criteria andUidListaDeseosIsNull() {
            addCriterion("UID_LISTA_DESEOS is null");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosIsNotNull() {
            addCriterion("UID_LISTA_DESEOS is not null");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosEqualTo(String value) {
            addCriterion("UID_LISTA_DESEOS =", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosNotEqualTo(String value) {
            addCriterion("UID_LISTA_DESEOS <>", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosGreaterThan(String value) {
            addCriterion("UID_LISTA_DESEOS >", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosGreaterThanOrEqualTo(String value) {
            addCriterion("UID_LISTA_DESEOS >=", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosLessThan(String value) {
            addCriterion("UID_LISTA_DESEOS <", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosLessThanOrEqualTo(String value) {
            addCriterion("UID_LISTA_DESEOS <=", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosLike(String value) {
            addCriterion("UID_LISTA_DESEOS like", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosNotLike(String value) {
            addCriterion("UID_LISTA_DESEOS not like", value, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosIn(List<String> values) {
            addCriterion("UID_LISTA_DESEOS in", values, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosNotIn(List<String> values) {
            addCriterion("UID_LISTA_DESEOS not in", values, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosBetween(String value1, String value2) {
            addCriterion("UID_LISTA_DESEOS between", value1, value2, "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosNotBetween(String value1, String value2) {
            addCriterion("UID_LISTA_DESEOS not between", value1, value2, "uidListaDeseos");
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

        public Criteria andCantidadIsNull() {
            addCriterion("CANTIDAD is null");
            return (Criteria) this;
        }

        public Criteria andCantidadIsNotNull() {
            addCriterion("CANTIDAD is not null");
            return (Criteria) this;
        }

        public Criteria andCantidadEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD =", value, "cantidad");
            return (Criteria) this;
        }

        public Criteria andCantidadNotEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD <>", value, "cantidad");
            return (Criteria) this;
        }

        public Criteria andCantidadGreaterThan(BigDecimal value) {
            addCriterion("CANTIDAD >", value, "cantidad");
            return (Criteria) this;
        }

        public Criteria andCantidadGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD >=", value, "cantidad");
            return (Criteria) this;
        }

        public Criteria andCantidadLessThan(BigDecimal value) {
            addCriterion("CANTIDAD <", value, "cantidad");
            return (Criteria) this;
        }

        public Criteria andCantidadLessThanOrEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD <=", value, "cantidad");
            return (Criteria) this;
        }

        public Criteria andCantidadIn(List<BigDecimal> values) {
            addCriterion("CANTIDAD in", values, "cantidad");
            return (Criteria) this;
        }

        public Criteria andCantidadNotIn(List<BigDecimal> values) {
            addCriterion("CANTIDAD not in", values, "cantidad");
            return (Criteria) this;
        }

        public Criteria andCantidadBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("CANTIDAD between", value1, value2, "cantidad");
            return (Criteria) this;
        }

        public Criteria andCantidadNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("CANTIDAD not between", value1, value2, "cantidad");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaIsNull() {
            addCriterion("UNIDAD_MEDIDA is null");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaIsNotNull() {
            addCriterion("UNIDAD_MEDIDA is not null");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaEqualTo(String value) {
            addCriterion("UNIDAD_MEDIDA =", value, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaNotEqualTo(String value) {
            addCriterion("UNIDAD_MEDIDA <>", value, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaGreaterThan(String value) {
            addCriterion("UNIDAD_MEDIDA >", value, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaGreaterThanOrEqualTo(String value) {
            addCriterion("UNIDAD_MEDIDA >=", value, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaLessThan(String value) {
            addCriterion("UNIDAD_MEDIDA <", value, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaLessThanOrEqualTo(String value) {
            addCriterion("UNIDAD_MEDIDA <=", value, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaLike(String value) {
            addCriterion("UNIDAD_MEDIDA like", value, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaNotLike(String value) {
            addCriterion("UNIDAD_MEDIDA not like", value, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaIn(List<String> values) {
            addCriterion("UNIDAD_MEDIDA in", values, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaNotIn(List<String> values) {
            addCriterion("UNIDAD_MEDIDA not in", values, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaBetween(String value1, String value2) {
            addCriterion("UNIDAD_MEDIDA between", value1, value2, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaNotBetween(String value1, String value2) {
            addCriterion("UNIDAD_MEDIDA not between", value1, value2, "unidadMedida");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaIsNull() {
            addCriterion("CANTIDAD_MEDIDA is null");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaIsNotNull() {
            addCriterion("CANTIDAD_MEDIDA is not null");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_MEDIDA =", value, "cantidadMedida");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaNotEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_MEDIDA <>", value, "cantidadMedida");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaGreaterThan(BigDecimal value) {
            addCriterion("CANTIDAD_MEDIDA >", value, "cantidadMedida");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_MEDIDA >=", value, "cantidadMedida");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaLessThan(BigDecimal value) {
            addCriterion("CANTIDAD_MEDIDA <", value, "cantidadMedida");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaLessThanOrEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_MEDIDA <=", value, "cantidadMedida");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaIn(List<BigDecimal> values) {
            addCriterion("CANTIDAD_MEDIDA in", values, "cantidadMedida");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaNotIn(List<BigDecimal> values) {
            addCriterion("CANTIDAD_MEDIDA not in", values, "cantidadMedida");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("CANTIDAD_MEDIDA between", value1, value2, "cantidadMedida");
            return (Criteria) this;
        }

        public Criteria andCantidadMedidaNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("CANTIDAD_MEDIDA not between", value1, value2, "cantidadMedida");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidListaDeseosLikeInsensitive(String value) {
            addCriterion("upper(UID_LISTA_DESEOS) like", value.toUpperCase(), "uidListaDeseos");
            return (Criteria) this;
        }

        public Criteria andCodartLikeInsensitive(String value) {
            addCriterion("upper(CODART) like", value.toUpperCase(), "codart");
            return (Criteria) this;
        }

        public Criteria andDesglose1LikeInsensitive(String value) {
            addCriterion("upper(DESGLOSE1) like", value.toUpperCase(), "desglose1");
            return (Criteria) this;
        }

        public Criteria andDesglose2LikeInsensitive(String value) {
            addCriterion("upper(DESGLOSE2) like", value.toUpperCase(), "desglose2");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaLikeInsensitive(String value) {
            addCriterion("upper(UNIDAD_MEDIDA) like", value.toUpperCase(), "unidadMedida");
            return (Criteria) this;
        }
        
        public Criteria andListaDeseosUidListaDeseosEqualTo(String value) {
            addCriterion("LISTAS.UID_LISTA_DESEOS =", value, "uidListaDeseos");
            return (Criteria) this;
        }
        public Criteria andListaDeseosUidActividadEqualTo(String value) {
            addCriterion("LISTAS.UID_ACTIVIDAD =", value, "uidActividad");
            return (Criteria) this;
        }
        public Criteria andArticuloUidActividadEqualTo(String value) {
            addCriterion("ARTICULOS.UID_ACTIVIDAD =", value, "uidActividad");
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