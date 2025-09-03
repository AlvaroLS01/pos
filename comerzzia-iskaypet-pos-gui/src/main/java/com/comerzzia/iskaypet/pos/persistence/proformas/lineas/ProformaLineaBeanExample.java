package com.comerzzia.iskaypet.pos.persistence.proformas.lineas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProformaLineaBeanExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ProformaLineaBeanExample() {
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

        public Criteria andIdProformaIsNull() {
            addCriterion("ID_PROFORMA is null");
            return (Criteria) this;
        }

        public Criteria andIdProformaIsNotNull() {
            addCriterion("ID_PROFORMA is not null");
            return (Criteria) this;
        }

        public Criteria andIdProformaEqualTo(String value) {
            addCriterion("ID_PROFORMA =", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaNotEqualTo(String value) {
            addCriterion("ID_PROFORMA <>", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaGreaterThan(String value) {
            addCriterion("ID_PROFORMA >", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaGreaterThanOrEqualTo(String value) {
            addCriterion("ID_PROFORMA >=", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaLessThan(String value) {
            addCriterion("ID_PROFORMA <", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaLessThanOrEqualTo(String value) {
            addCriterion("ID_PROFORMA <=", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaLike(String value) {
            addCriterion("ID_PROFORMA like", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaNotLike(String value) {
            addCriterion("ID_PROFORMA not like", value, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaIn(List<String> values) {
            addCriterion("ID_PROFORMA in", values, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaNotIn(List<String> values) {
            addCriterion("ID_PROFORMA not in", values, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaBetween(String value1, String value2) {
            addCriterion("ID_PROFORMA between", value1, value2, "idProforma");
            return (Criteria) this;
        }

        public Criteria andIdProformaNotBetween(String value1, String value2) {
            addCriterion("ID_PROFORMA not between", value1, value2, "idProforma");
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

        public Criteria andUnidadesIsNull() {
            addCriterion("UNIDADES is null");
            return (Criteria) this;
        }

        public Criteria andUnidadesIsNotNull() {
            addCriterion("UNIDADES is not null");
            return (Criteria) this;
        }

        public Criteria andUnidadesEqualTo(Integer value) {
            addCriterion("UNIDADES =", value, "unidades");
            return (Criteria) this;
        }

        public Criteria andUnidadesNotEqualTo(Integer value) {
            addCriterion("UNIDADES <>", value, "unidades");
            return (Criteria) this;
        }

        public Criteria andUnidadesGreaterThan(Integer value) {
            addCriterion("UNIDADES >", value, "unidades");
            return (Criteria) this;
        }

        public Criteria andUnidadesGreaterThanOrEqualTo(Integer value) {
            addCriterion("UNIDADES >=", value, "unidades");
            return (Criteria) this;
        }

        public Criteria andUnidadesLessThan(Integer value) {
            addCriterion("UNIDADES <", value, "unidades");
            return (Criteria) this;
        }

        public Criteria andUnidadesLessThanOrEqualTo(Integer value) {
            addCriterion("UNIDADES <=", value, "unidades");
            return (Criteria) this;
        }

        public Criteria andUnidadesIn(List<Integer> values) {
            addCriterion("UNIDADES in", values, "unidades");
            return (Criteria) this;
        }

        public Criteria andUnidadesNotIn(List<Integer> values) {
            addCriterion("UNIDADES not in", values, "unidades");
            return (Criteria) this;
        }

        public Criteria andUnidadesBetween(Integer value1, Integer value2) {
            addCriterion("UNIDADES between", value1, value2, "unidades");
            return (Criteria) this;
        }

        public Criteria andUnidadesNotBetween(Integer value1, Integer value2) {
            addCriterion("UNIDADES not between", value1, value2, "unidades");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaIsNull() {
            addCriterion("CANTIDAD_CONVERTIDA is null");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaIsNotNull() {
            addCriterion("CANTIDAD_CONVERTIDA is not null");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_CONVERTIDA =", value, "cantidadConvertida");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaNotEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_CONVERTIDA <>", value, "cantidadConvertida");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaGreaterThan(BigDecimal value) {
            addCriterion("CANTIDAD_CONVERTIDA >", value, "cantidadConvertida");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_CONVERTIDA >=", value, "cantidadConvertida");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaLessThan(BigDecimal value) {
            addCriterion("CANTIDAD_CONVERTIDA <", value, "cantidadConvertida");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaLessThanOrEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_CONVERTIDA <=", value, "cantidadConvertida");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaIn(List<BigDecimal> values) {
            addCriterion("CANTIDAD_CONVERTIDA in", values, "cantidadConvertida");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaNotIn(List<BigDecimal> values) {
            addCriterion("CANTIDAD_CONVERTIDA not in", values, "cantidadConvertida");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("CANTIDAD_CONVERTIDA between", value1, value2, "cantidadConvertida");
            return (Criteria) this;
        }

        public Criteria andCantidadConvertidaNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("CANTIDAD_CONVERTIDA not between", value1, value2, "cantidadConvertida");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaIsNull() {
            addCriterion("CANTIDAD_SUMINISTRADA is null");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaIsNotNull() {
            addCriterion("CANTIDAD_SUMINISTRADA is not null");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_SUMINISTRADA =", value, "cantidadSuministrada");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaNotEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_SUMINISTRADA <>", value, "cantidadSuministrada");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaGreaterThan(BigDecimal value) {
            addCriterion("CANTIDAD_SUMINISTRADA >", value, "cantidadSuministrada");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_SUMINISTRADA >=", value, "cantidadSuministrada");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaLessThan(BigDecimal value) {
            addCriterion("CANTIDAD_SUMINISTRADA <", value, "cantidadSuministrada");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaLessThanOrEqualTo(BigDecimal value) {
            addCriterion("CANTIDAD_SUMINISTRADA <=", value, "cantidadSuministrada");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaIn(List<BigDecimal> values) {
            addCriterion("CANTIDAD_SUMINISTRADA in", values, "cantidadSuministrada");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaNotIn(List<BigDecimal> values) {
            addCriterion("CANTIDAD_SUMINISTRADA not in", values, "cantidadSuministrada");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("CANTIDAD_SUMINISTRADA between", value1, value2, "cantidadSuministrada");
            return (Criteria) this;
        }

        public Criteria andCantidadSuministradaNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("CANTIDAD_SUMINISTRADA not between", value1, value2, "cantidadSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaIsNull() {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA is null");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaIsNotNull() {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA is not null");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaEqualTo(String value) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA =", value, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaNotEqualTo(String value) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA <>", value, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaGreaterThan(String value) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA >", value, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaGreaterThanOrEqualTo(String value) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA >=", value, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaLessThan(String value) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA <", value, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaLessThanOrEqualTo(String value) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA <=", value, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaLike(String value) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA like", value, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaNotLike(String value) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA not like", value, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaIn(List<String> values) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA in", values, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaNotIn(List<String> values) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA not in", values, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaBetween(String value1, String value2) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA between", value1, value2, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaNotBetween(String value1, String value2) {
            addCriterion("UNIDAD_MEDIDA_SUMINISTRADA not between", value1, value2, "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andDescuentoIsNull() {
            addCriterion("DESCUENTO is null");
            return (Criteria) this;
        }

        public Criteria andDescuentoIsNotNull() {
            addCriterion("DESCUENTO is not null");
            return (Criteria) this;
        }

        public Criteria andDescuentoEqualTo(String value) {
            addCriterion("DESCUENTO =", value, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoNotEqualTo(String value) {
            addCriterion("DESCUENTO <>", value, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoGreaterThan(String value) {
            addCriterion("DESCUENTO >", value, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoGreaterThanOrEqualTo(String value) {
            addCriterion("DESCUENTO >=", value, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoLessThan(String value) {
            addCriterion("DESCUENTO <", value, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoLessThanOrEqualTo(String value) {
            addCriterion("DESCUENTO <=", value, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoLike(String value) {
            addCriterion("DESCUENTO like", value, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoNotLike(String value) {
            addCriterion("DESCUENTO not like", value, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoIn(List<String> values) {
            addCriterion("DESCUENTO in", values, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoNotIn(List<String> values) {
            addCriterion("DESCUENTO not in", values, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoBetween(String value1, String value2) {
            addCriterion("DESCUENTO between", value1, value2, "descuento");
            return (Criteria) this;
        }

        public Criteria andDescuentoNotBetween(String value1, String value2) {
            addCriterion("DESCUENTO not between", value1, value2, "descuento");
            return (Criteria) this;
        }

        public Criteria andImporteIsNull() {
            addCriterion("IMPORTE is null");
            return (Criteria) this;
        }

        public Criteria andImporteIsNotNull() {
            addCriterion("IMPORTE is not null");
            return (Criteria) this;
        }

        public Criteria andImporteEqualTo(BigDecimal value) {
            addCriterion("IMPORTE =", value, "importe");
            return (Criteria) this;
        }

        public Criteria andImporteNotEqualTo(BigDecimal value) {
            addCriterion("IMPORTE <>", value, "importe");
            return (Criteria) this;
        }

        public Criteria andImporteGreaterThan(BigDecimal value) {
            addCriterion("IMPORTE >", value, "importe");
            return (Criteria) this;
        }

        public Criteria andImporteGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("IMPORTE >=", value, "importe");
            return (Criteria) this;
        }

        public Criteria andImporteLessThan(BigDecimal value) {
            addCriterion("IMPORTE <", value, "importe");
            return (Criteria) this;
        }

        public Criteria andImporteLessThanOrEqualTo(BigDecimal value) {
            addCriterion("IMPORTE <=", value, "importe");
            return (Criteria) this;
        }

        public Criteria andImporteIn(List<BigDecimal> values) {
            addCriterion("IMPORTE in", values, "importe");
            return (Criteria) this;
        }

        public Criteria andImporteNotIn(List<BigDecimal> values) {
            addCriterion("IMPORTE not in", values, "importe");
            return (Criteria) this;
        }

        public Criteria andImporteBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("IMPORTE between", value1, value2, "importe");
            return (Criteria) this;
        }

        public Criteria andImporteNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("IMPORTE not between", value1, value2, "importe");
            return (Criteria) this;
        }

        public Criteria andLoteIsNull() {
            addCriterion("LOTE is null");
            return (Criteria) this;
        }

        public Criteria andLoteIsNotNull() {
            addCriterion("LOTE is not null");
            return (Criteria) this;
        }

        public Criteria andLoteEqualTo(String value) {
            addCriterion("LOTE =", value, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteNotEqualTo(String value) {
            addCriterion("LOTE <>", value, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteGreaterThan(String value) {
            addCriterion("LOTE >", value, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteGreaterThanOrEqualTo(String value) {
            addCriterion("LOTE >=", value, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteLessThan(String value) {
            addCriterion("LOTE <", value, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteLessThanOrEqualTo(String value) {
            addCriterion("LOTE <=", value, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteLike(String value) {
            addCriterion("LOTE like", value, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteNotLike(String value) {
            addCriterion("LOTE not like", value, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteIn(List<String> values) {
            addCriterion("LOTE in", values, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteNotIn(List<String> values) {
            addCriterion("LOTE not in", values, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteBetween(String value1, String value2) {
            addCriterion("LOTE between", value1, value2, "lote");
            return (Criteria) this;
        }

        public Criteria andLoteNotBetween(String value1, String value2) {
            addCriterion("LOTE not between", value1, value2, "lote");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadIsNull() {
            addCriterion("FECHA_CADUCIDAD is null");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadIsNotNull() {
            addCriterion("FECHA_CADUCIDAD is not null");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadEqualTo(Date value) {
            addCriterion("FECHA_CADUCIDAD =", value, "fechaCaducidad");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadNotEqualTo(Date value) {
            addCriterion("FECHA_CADUCIDAD <>", value, "fechaCaducidad");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadGreaterThan(Date value) {
            addCriterion("FECHA_CADUCIDAD >", value, "fechaCaducidad");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_CADUCIDAD >=", value, "fechaCaducidad");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadLessThan(Date value) {
            addCriterion("FECHA_CADUCIDAD <", value, "fechaCaducidad");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_CADUCIDAD <=", value, "fechaCaducidad");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadIn(List<Date> values) {
            addCriterion("FECHA_CADUCIDAD in", values, "fechaCaducidad");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadNotIn(List<Date> values) {
            addCriterion("FECHA_CADUCIDAD not in", values, "fechaCaducidad");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadBetween(Date value1, Date value2) {
            addCriterion("FECHA_CADUCIDAD between", value1, value2, "fechaCaducidad");
            return (Criteria) this;
        }

        public Criteria andFechaCaducidadNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_CADUCIDAD not between", value1, value2, "fechaCaducidad");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andIdProformaLikeInsensitive(String value) {
            addCriterion("upper(ID_PROFORMA) like", value.toUpperCase(), "idProforma");
            return (Criteria) this;
        }

        public Criteria andCodartLikeInsensitive(String value) {
            addCriterion("upper(CODART) like", value.toUpperCase(), "codart");
            return (Criteria) this;
        }

        public Criteria andUnidadMedidaSuministradaLikeInsensitive(String value) {
            addCriterion("upper(UNIDAD_MEDIDA_SUMINISTRADA) like", value.toUpperCase(), "unidadMedidaSuministrada");
            return (Criteria) this;
        }

        public Criteria andDescuentoLikeInsensitive(String value) {
            addCriterion("upper(DESCUENTO) like", value.toUpperCase(), "descuento");
            return (Criteria) this;
        }

        public Criteria andLoteLikeInsensitive(String value) {
            addCriterion("upper(LOTE) like", value.toUpperCase(), "lote");
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