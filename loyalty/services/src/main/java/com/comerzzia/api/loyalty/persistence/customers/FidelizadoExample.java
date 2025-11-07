package com.comerzzia.api.loyalty.persistence.customers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FidelizadoExample {
    public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_ID_FIDELIZADO = "ID_FIDELIZADO";

    public static final String ORDER_BY_ID_FIDELIZADO_DESC = "ID_FIDELIZADO DESC";

    public static final String ORDER_BY_NOMBRE = "NOMBRE";

    public static final String ORDER_BY_NOMBRE_DESC = "NOMBRE DESC";

    public static final String ORDER_BY_APELLIDOS = "APELLIDOS";

    public static final String ORDER_BY_APELLIDOS_DESC = "APELLIDOS DESC";

    public static final String ORDER_BY_DOMICILIO = "DOMICILIO";

    public static final String ORDER_BY_DOMICILIO_DESC = "DOMICILIO DESC";

    public static final String ORDER_BY_POBLACION = "POBLACION";

    public static final String ORDER_BY_POBLACION_DESC = "POBLACION DESC";

    public static final String ORDER_BY_LOCALIDAD = "LOCALIDAD";

    public static final String ORDER_BY_LOCALIDAD_DESC = "LOCALIDAD DESC";

    public static final String ORDER_BY_PROVINCIA = "PROVINCIA";

    public static final String ORDER_BY_PROVINCIA_DESC = "PROVINCIA DESC";

    public static final String ORDER_BY_CP = "CP";

    public static final String ORDER_BY_CP_DESC = "CP DESC";

    public static final String ORDER_BY_CODPAIS = "CODPAIS";

    public static final String ORDER_BY_CODPAIS_DESC = "CODPAIS DESC";

    public static final String ORDER_BY_CODTIPOIDEN = "CODTIPOIDEN";

    public static final String ORDER_BY_CODTIPOIDEN_DESC = "CODTIPOIDEN DESC";

    public static final String ORDER_BY_DOCUMENTO = "DOCUMENTO";

    public static final String ORDER_BY_DOCUMENTO_DESC = "DOCUMENTO DESC";

    public static final String ORDER_BY_OBSERVACIONES = "OBSERVACIONES";

    public static final String ORDER_BY_OBSERVACIONES_DESC = "OBSERVACIONES DESC";

    public static final String ORDER_BY_FECHA_NACIMIENTO = "FECHA_NACIMIENTO";

    public static final String ORDER_BY_FECHA_NACIMIENTO_DESC = "FECHA_NACIMIENTO DESC";

    public static final String ORDER_BY_SEXO = "SEXO";

    public static final String ORDER_BY_SEXO_DESC = "SEXO DESC";

    public static final String ORDER_BY_CODESTCIVIL = "CODESTCIVIL";

    public static final String ORDER_BY_CODESTCIVIL_DESC = "CODESTCIVIL DESC";

    public static final String ORDER_BY_ACTIVO = "ACTIVO";

    public static final String ORDER_BY_ACTIVO_DESC = "ACTIVO DESC";

    public static final String ORDER_BY_FECHA_ALTA = "FECHA_ALTA";

    public static final String ORDER_BY_FECHA_ALTA_DESC = "FECHA_ALTA DESC";

    public static final String ORDER_BY_FECHA_MODIFICACION = "FECHA_MODIFICACION";

    public static final String ORDER_BY_FECHA_MODIFICACION_DESC = "FECHA_MODIFICACION DESC";

    public static final String ORDER_BY_FECHA_BAJA = "FECHA_BAJA";

    public static final String ORDER_BY_FECHA_BAJA_DESC = "FECHA_BAJA DESC";

    public static final String ORDER_BY_CODFIDELIZADO = "CODFIDELIZADO";

    public static final String ORDER_BY_CODFIDELIZADO_DESC = "CODFIDELIZADO DESC";

    public static final String ORDER_BY_DESPAIS = "DESPAIS";

    public static final String ORDER_BY_DESPAIS_DESC = "DESPAIS DESC";

    public static final String ORDER_BY_DESESTCIVIL = "DESESTCIVIL";

    public static final String ORDER_BY_DESESTCIVIL_DESC = "DESESTCIVIL DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public FidelizadoExample() {
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
        
        public Criteria andNumeroTarjetaEqualTo(String value) {
            addCriterion("NUMERO_TARJETA =", value, "numeroTarjeta");
            return (Criteria) this;
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
        
        public Criteria andUidInstanciaEqualToFid(String value) {
            addCriterion("FID.UID_INSTANCIA =", value, "uidInstancia");
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

        public Criteria andNombreIsNull() {
            addCriterion("NOMBRE is null");
            return (Criteria) this;
        }

        public Criteria andNombreIsNotNull() {
            addCriterion("NOMBRE is not null");
            return (Criteria) this;
        }

        public Criteria andNombreEqualTo(String value) {
            addCriterion("NOMBRE =", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreNotEqualTo(String value) {
            addCriterion("NOMBRE <>", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreGreaterThan(String value) {
            addCriterion("NOMBRE >", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreGreaterThanOrEqualTo(String value) {
            addCriterion("NOMBRE >=", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreLessThan(String value) {
            addCriterion("NOMBRE <", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreLessThanOrEqualTo(String value) {
            addCriterion("NOMBRE <=", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreLike(String value) {
            addCriterion("NOMBRE like", value, "nombre");
            return (Criteria) this;
        }
        
        public Criteria andUpperNombreLike(String value) {
            addCriterion("UPPER(NOMBRE) like", value.toUpperCase(), "nombre");
            return (Criteria) this;
        }


        public Criteria andNombreNotLike(String value) {
            addCriterion("NOMBRE not like", value, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreIn(List<String> values) {
            addCriterion("NOMBRE in", values, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreNotIn(List<String> values) {
            addCriterion("NOMBRE not in", values, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreBetween(String value1, String value2) {
            addCriterion("NOMBRE between", value1, value2, "nombre");
            return (Criteria) this;
        }

        public Criteria andNombreNotBetween(String value1, String value2) {
            addCriterion("NOMBRE not between", value1, value2, "nombre");
            return (Criteria) this;
        }

        public Criteria andApellidosIsNull() {
            addCriterion("APELLIDOS is null");
            return (Criteria) this;
        }

        public Criteria andApellidosIsNotNull() {
            addCriterion("APELLIDOS is not null");
            return (Criteria) this;
        }

        public Criteria andApellidosEqualTo(String value) {
            addCriterion("APELLIDOS =", value, "apellidos");
            return (Criteria) this;
        }

        public Criteria andApellidosNotEqualTo(String value) {
            addCriterion("APELLIDOS <>", value, "apellidos");
            return (Criteria) this;
        }

        public Criteria andApellidosGreaterThan(String value) {
            addCriterion("APELLIDOS >", value, "apellidos");
            return (Criteria) this;
        }

        public Criteria andApellidosGreaterThanOrEqualTo(String value) {
            addCriterion("APELLIDOS >=", value, "apellidos");
            return (Criteria) this;
        }

        public Criteria andApellidosLessThan(String value) {
            addCriterion("APELLIDOS <", value, "apellidos");
            return (Criteria) this;
        }

        public Criteria andApellidosLessThanOrEqualTo(String value) {
            addCriterion("APELLIDOS <=", value, "apellidos");
            return (Criteria) this;
        }

        public Criteria andUpperApellidosLike(String value) {
            addCriterion("UPPER(APELLIDOS) like", value.toUpperCase(), "apellidos");
            return (Criteria) this;
        }

        public Criteria andApellidosNotLike(String value) {
            addCriterion("APELLIDOS not like", value, "apellidos");
            return (Criteria) this;
        }

        public Criteria andApellidosIn(List<String> values) {
            addCriterion("APELLIDOS in", values, "apellidos");
            return (Criteria) this;
        }

        public Criteria andApellidosNotIn(List<String> values) {
            addCriterion("APELLIDOS not in", values, "apellidos");
            return (Criteria) this;
        }

        public Criteria andApellidosBetween(String value1, String value2) {
            addCriterion("APELLIDOS between", value1, value2, "apellidos");
            return (Criteria) this;
        }

        public Criteria andApellidosNotBetween(String value1, String value2) {
            addCriterion("APELLIDOS not between", value1, value2, "apellidos");
            return (Criteria) this;
        }

        public Criteria andDomicilioIsNull() {
            addCriterion("DOMICILIO is null");
            return (Criteria) this;
        }

        public Criteria andDomicilioIsNotNull() {
            addCriterion("DOMICILIO is not null");
            return (Criteria) this;
        }

        public Criteria andDomicilioEqualTo(String value) {
            addCriterion("DOMICILIO =", value, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioNotEqualTo(String value) {
            addCriterion("DOMICILIO <>", value, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioGreaterThan(String value) {
            addCriterion("DOMICILIO >", value, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioGreaterThanOrEqualTo(String value) {
            addCriterion("DOMICILIO >=", value, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioLessThan(String value) {
            addCriterion("DOMICILIO <", value, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioLessThanOrEqualTo(String value) {
            addCriterion("DOMICILIO <=", value, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioLike(String value) {
            addCriterion("DOMICILIO like", value, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioNotLike(String value) {
            addCriterion("DOMICILIO not like", value, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioIn(List<String> values) {
            addCriterion("DOMICILIO in", values, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioNotIn(List<String> values) {
            addCriterion("DOMICILIO not in", values, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioBetween(String value1, String value2) {
            addCriterion("DOMICILIO between", value1, value2, "domicilio");
            return (Criteria) this;
        }

        public Criteria andDomicilioNotBetween(String value1, String value2) {
            addCriterion("DOMICILIO not between", value1, value2, "domicilio");
            return (Criteria) this;
        }

        public Criteria andPoblacionIsNull() {
            addCriterion("POBLACION is null");
            return (Criteria) this;
        }

        public Criteria andPoblacionIsNotNull() {
            addCriterion("POBLACION is not null");
            return (Criteria) this;
        }

        public Criteria andPoblacionEqualTo(String value) {
            addCriterion("POBLACION =", value, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionNotEqualTo(String value) {
            addCriterion("POBLACION <>", value, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionGreaterThan(String value) {
            addCriterion("POBLACION >", value, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionGreaterThanOrEqualTo(String value) {
            addCriterion("POBLACION >=", value, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionLessThan(String value) {
            addCriterion("POBLACION <", value, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionLessThanOrEqualTo(String value) {
            addCriterion("POBLACION <=", value, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionLike(String value) {
            addCriterion("POBLACION like", value, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionNotLike(String value) {
            addCriterion("POBLACION not like", value, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionIn(List<String> values) {
            addCriterion("POBLACION in", values, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionNotIn(List<String> values) {
            addCriterion("POBLACION not in", values, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionBetween(String value1, String value2) {
            addCriterion("POBLACION between", value1, value2, "poblacion");
            return (Criteria) this;
        }

        public Criteria andPoblacionNotBetween(String value1, String value2) {
            addCriterion("POBLACION not between", value1, value2, "poblacion");
            return (Criteria) this;
        }

        public Criteria andLocalidadIsNull() {
            addCriterion("LOCALIDAD is null");
            return (Criteria) this;
        }

        public Criteria andLocalidadIsNotNull() {
            addCriterion("LOCALIDAD is not null");
            return (Criteria) this;
        }

        public Criteria andLocalidadEqualTo(String value) {
            addCriterion("LOCALIDAD =", value, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadNotEqualTo(String value) {
            addCriterion("LOCALIDAD <>", value, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadGreaterThan(String value) {
            addCriterion("LOCALIDAD >", value, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadGreaterThanOrEqualTo(String value) {
            addCriterion("LOCALIDAD >=", value, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadLessThan(String value) {
            addCriterion("LOCALIDAD <", value, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadLessThanOrEqualTo(String value) {
            addCriterion("LOCALIDAD <=", value, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadLike(String value) {
            addCriterion("LOCALIDAD like", value, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadNotLike(String value) {
            addCriterion("LOCALIDAD not like", value, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadIn(List<String> values) {
            addCriterion("LOCALIDAD in", values, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadNotIn(List<String> values) {
            addCriterion("LOCALIDAD not in", values, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadBetween(String value1, String value2) {
            addCriterion("LOCALIDAD between", value1, value2, "localidad");
            return (Criteria) this;
        }

        public Criteria andLocalidadNotBetween(String value1, String value2) {
            addCriterion("LOCALIDAD not between", value1, value2, "localidad");
            return (Criteria) this;
        }

        public Criteria andProvinciaIsNull() {
            addCriterion("PROVINCIA is null");
            return (Criteria) this;
        }

        public Criteria andProvinciaIsNotNull() {
            addCriterion("PROVINCIA is not null");
            return (Criteria) this;
        }

        public Criteria andProvinciaEqualTo(String value) {
            addCriterion("PROVINCIA =", value, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaNotEqualTo(String value) {
            addCriterion("PROVINCIA <>", value, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaGreaterThan(String value) {
            addCriterion("PROVINCIA >", value, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaGreaterThanOrEqualTo(String value) {
            addCriterion("PROVINCIA >=", value, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaLessThan(String value) {
            addCriterion("PROVINCIA <", value, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaLessThanOrEqualTo(String value) {
            addCriterion("PROVINCIA <=", value, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaLike(String value) {
            addCriterion("PROVINCIA like", value, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaNotLike(String value) {
            addCriterion("PROVINCIA not like", value, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaIn(List<String> values) {
            addCriterion("PROVINCIA in", values, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaNotIn(List<String> values) {
            addCriterion("PROVINCIA not in", values, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaBetween(String value1, String value2) {
            addCriterion("PROVINCIA between", value1, value2, "provincia");
            return (Criteria) this;
        }

        public Criteria andProvinciaNotBetween(String value1, String value2) {
            addCriterion("PROVINCIA not between", value1, value2, "provincia");
            return (Criteria) this;
        }

        public Criteria andCpIsNull() {
            addCriterion("CP is null");
            return (Criteria) this;
        }

        public Criteria andCpIsNotNull() {
            addCriterion("CP is not null");
            return (Criteria) this;
        }

        public Criteria andCpEqualTo(String value) {
            addCriterion("CP =", value, "cp");
            return (Criteria) this;
        }

        public Criteria andCpNotEqualTo(String value) {
            addCriterion("CP <>", value, "cp");
            return (Criteria) this;
        }

        public Criteria andCpGreaterThan(String value) {
            addCriterion("CP >", value, "cp");
            return (Criteria) this;
        }

        public Criteria andCpGreaterThanOrEqualTo(String value) {
            addCriterion("CP >=", value, "cp");
            return (Criteria) this;
        }

        public Criteria andCpLessThan(String value) {
            addCriterion("CP <", value, "cp");
            return (Criteria) this;
        }

        public Criteria andCpLessThanOrEqualTo(String value) {
            addCriterion("CP <=", value, "cp");
            return (Criteria) this;
        }

        public Criteria andCpLike(String value) {
            addCriterion("CP like", value, "cp");
            return (Criteria) this;
        }

        public Criteria andCpNotLike(String value) {
            addCriterion("CP not like", value, "cp");
            return (Criteria) this;
        }

        public Criteria andCpIn(List<String> values) {
            addCriterion("CP in", values, "cp");
            return (Criteria) this;
        }

        public Criteria andCpNotIn(List<String> values) {
            addCriterion("CP not in", values, "cp");
            return (Criteria) this;
        }

        public Criteria andCpBetween(String value1, String value2) {
            addCriterion("CP between", value1, value2, "cp");
            return (Criteria) this;
        }

        public Criteria andCpNotBetween(String value1, String value2) {
            addCriterion("CP not between", value1, value2, "cp");
            return (Criteria) this;
        }

        public Criteria andCodPaisIsNull() {
            addCriterion("CODPAIS is null");
            return (Criteria) this;
        }

        public Criteria andCodPaisIsNotNull() {
            addCriterion("CODPAIS is not null");
            return (Criteria) this;
        }

        public Criteria andCodPaisEqualTo(String value) {
            addCriterion("CODPAIS =", value, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisNotEqualTo(String value) {
            addCriterion("CODPAIS <>", value, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisGreaterThan(String value) {
            addCriterion("CODPAIS >", value, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisGreaterThanOrEqualTo(String value) {
            addCriterion("CODPAIS >=", value, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisLessThan(String value) {
            addCriterion("CODPAIS <", value, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisLessThanOrEqualTo(String value) {
            addCriterion("CODPAIS <=", value, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisLike(String value) {
            addCriterion("CODPAIS like", value, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisNotLike(String value) {
            addCriterion("CODPAIS not like", value, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisIn(List<String> values) {
            addCriterion("CODPAIS in", values, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisNotIn(List<String> values) {
            addCriterion("CODPAIS not in", values, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisBetween(String value1, String value2) {
            addCriterion("CODPAIS between", value1, value2, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodPaisNotBetween(String value1, String value2) {
            addCriterion("CODPAIS not between", value1, value2, "codPais");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenIsNull() {
            addCriterion("CODTIPOIDEN is null");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenIsNotNull() {
            addCriterion("CODTIPOIDEN is not null");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenEqualTo(String value) {
            addCriterion("CODTIPOIDEN =", value, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenNotEqualTo(String value) {
            addCriterion("CODTIPOIDEN <>", value, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenGreaterThan(String value) {
            addCriterion("CODTIPOIDEN >", value, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenGreaterThanOrEqualTo(String value) {
            addCriterion("CODTIPOIDEN >=", value, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenLessThan(String value) {
            addCriterion("CODTIPOIDEN <", value, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenLessThanOrEqualTo(String value) {
            addCriterion("CODTIPOIDEN <=", value, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenLike(String value) {
            addCriterion("CODTIPOIDEN like", value, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenNotLike(String value) {
            addCriterion("CODTIPOIDEN not like", value, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenIn(List<String> values) {
            addCriterion("CODTIPOIDEN in", values, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenNotIn(List<String> values) {
            addCriterion("CODTIPOIDEN not in", values, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenBetween(String value1, String value2) {
            addCriterion("CODTIPOIDEN between", value1, value2, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenNotBetween(String value1, String value2) {
            addCriterion("CODTIPOIDEN not between", value1, value2, "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andDocumentoIsNull() {
            addCriterion("DOCUMENTO is null");
            return (Criteria) this;
        }

        public Criteria andDocumentoIsNotNull() {
            addCriterion("DOCUMENTO is not null");
            return (Criteria) this;
        }

        public Criteria andDocumentoEqualTo(String value) {
            addCriterion("DOCUMENTO =", value, "documento");
            return (Criteria) this;
        }
        
        public Criteria andUpperDocumentoEqualTo(String value) {
            addCriterion("UPPER(DOCUMENTO) =", value.toUpperCase(), "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoNotEqualTo(String value) {
            addCriterion("DOCUMENTO <>", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoGreaterThan(String value) {
            addCriterion("DOCUMENTO >", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoGreaterThanOrEqualTo(String value) {
            addCriterion("DOCUMENTO >=", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoLessThan(String value) {
            addCriterion("DOCUMENTO <", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoLessThanOrEqualTo(String value) {
            addCriterion("DOCUMENTO <=", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoLike(String value) {
            addCriterion("DOCUMENTO like", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoNotLike(String value) {
            addCriterion("DOCUMENTO not like", value, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoIn(List<String> values) {
            addCriterion("DOCUMENTO in", values, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoNotIn(List<String> values) {
            addCriterion("DOCUMENTO not in", values, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoBetween(String value1, String value2) {
            addCriterion("DOCUMENTO between", value1, value2, "documento");
            return (Criteria) this;
        }

        public Criteria andDocumentoNotBetween(String value1, String value2) {
            addCriterion("DOCUMENTO not between", value1, value2, "documento");
            return (Criteria) this;
        }

        public Criteria andObservacionesIsNull() {
            addCriterion("OBSERVACIONES is null");
            return (Criteria) this;
        }

        public Criteria andObservacionesIsNotNull() {
            addCriterion("OBSERVACIONES is not null");
            return (Criteria) this;
        }

        public Criteria andObservacionesEqualTo(String value) {
            addCriterion("OBSERVACIONES =", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesNotEqualTo(String value) {
            addCriterion("OBSERVACIONES <>", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesGreaterThan(String value) {
            addCriterion("OBSERVACIONES >", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesGreaterThanOrEqualTo(String value) {
            addCriterion("OBSERVACIONES >=", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesLessThan(String value) {
            addCriterion("OBSERVACIONES <", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesLessThanOrEqualTo(String value) {
            addCriterion("OBSERVACIONES <=", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesLike(String value) {
            addCriterion("OBSERVACIONES like", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesNotLike(String value) {
            addCriterion("OBSERVACIONES not like", value, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesIn(List<String> values) {
            addCriterion("OBSERVACIONES in", values, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesNotIn(List<String> values) {
            addCriterion("OBSERVACIONES not in", values, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesBetween(String value1, String value2) {
            addCriterion("OBSERVACIONES between", value1, value2, "observaciones");
            return (Criteria) this;
        }

        public Criteria andObservacionesNotBetween(String value1, String value2) {
            addCriterion("OBSERVACIONES not between", value1, value2, "observaciones");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoIsNull() {
            addCriterion("FECHA_NACIMIENTO is null");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoIsNotNull() {
            addCriterion("FECHA_NACIMIENTO is not null");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoEqualTo(Date value) {
            addCriterion("FECHA_NACIMIENTO =", value, "fechaNacimiento");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoNotEqualTo(Date value) {
            addCriterion("FECHA_NACIMIENTO <>", value, "fechaNacimiento");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoGreaterThan(Date value) {
            addCriterion("FECHA_NACIMIENTO >", value, "fechaNacimiento");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_NACIMIENTO >=", value, "fechaNacimiento");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoLessThan(Date value) {
            addCriterion("FECHA_NACIMIENTO <", value, "fechaNacimiento");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_NACIMIENTO <=", value, "fechaNacimiento");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoIn(List<Date> values) {
            addCriterion("FECHA_NACIMIENTO in", values, "fechaNacimiento");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoNotIn(List<Date> values) {
            addCriterion("FECHA_NACIMIENTO not in", values, "fechaNacimiento");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoBetween(Date value1, Date value2) {
            addCriterion("FECHA_NACIMIENTO between", value1, value2, "fechaNacimiento");
            return (Criteria) this;
        }

        public Criteria andFechaNacimientoNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_NACIMIENTO not between", value1, value2, "fechaNacimiento");
            return (Criteria) this;
        }

        public Criteria andSexoIsNull() {
            addCriterion("SEXO is null");
            return (Criteria) this;
        }

        public Criteria andSexoIsNotNull() {
            addCriterion("SEXO is not null");
            return (Criteria) this;
        }

        public Criteria andSexoEqualTo(String value) {
            addCriterion("SEXO =", value, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoNotEqualTo(String value) {
            addCriterion("SEXO <>", value, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoGreaterThan(String value) {
            addCriterion("SEXO >", value, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoGreaterThanOrEqualTo(String value) {
            addCriterion("SEXO >=", value, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoLessThan(String value) {
            addCriterion("SEXO <", value, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoLessThanOrEqualTo(String value) {
            addCriterion("SEXO <=", value, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoLike(String value) {
            addCriterion("SEXO like", value, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoNotLike(String value) {
            addCriterion("SEXO not like", value, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoIn(List<String> values) {
            addCriterion("SEXO in", values, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoNotIn(List<String> values) {
            addCriterion("SEXO not in", values, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoBetween(String value1, String value2) {
            addCriterion("SEXO between", value1, value2, "sexo");
            return (Criteria) this;
        }

        public Criteria andSexoNotBetween(String value1, String value2) {
            addCriterion("SEXO not between", value1, value2, "sexo");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilIsNull() {
            addCriterion("CODESTCIVIL is null");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilIsNotNull() {
            addCriterion("CODESTCIVIL is not null");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilEqualTo(String value) {
            addCriterion("CODESTCIVIL =", value, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilNotEqualTo(String value) {
            addCriterion("CODESTCIVIL <>", value, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilGreaterThan(String value) {
            addCriterion("CODESTCIVIL >", value, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilGreaterThanOrEqualTo(String value) {
            addCriterion("CODESTCIVIL >=", value, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilLessThan(String value) {
            addCriterion("CODESTCIVIL <", value, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilLessThanOrEqualTo(String value) {
            addCriterion("CODESTCIVIL <=", value, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilLike(String value) {
            addCriterion("CODESTCIVIL like", value, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilNotLike(String value) {
            addCriterion("CODESTCIVIL not like", value, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilIn(List<String> values) {
            addCriterion("CODESTCIVIL in", values, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilNotIn(List<String> values) {
            addCriterion("CODESTCIVIL not in", values, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilBetween(String value1, String value2) {
            addCriterion("CODESTCIVIL between", value1, value2, "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilNotBetween(String value1, String value2) {
            addCriterion("CODESTCIVIL not between", value1, value2, "codEstCivil");
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

        public Criteria andFechaAltaIsNull() {
            addCriterion("FECHA_ALTA is null");
            return (Criteria) this;
        }

        public Criteria andFechaAltaIsNotNull() {
            addCriterion("FECHA_ALTA is not null");
            return (Criteria) this;
        }

        public Criteria andFechaAltaEqualTo(Date value) {
            addCriterion("FECHA_ALTA =", value, "fechaAlta");
            return (Criteria) this;
        }

        public Criteria andFechaAltaNotEqualTo(Date value) {
            addCriterion("FECHA_ALTA <>", value, "fechaAlta");
            return (Criteria) this;
        }

        public Criteria andFechaAltaGreaterThan(Date value) {
            addCriterion("FECHA_ALTA >", value, "fechaAlta");
            return (Criteria) this;
        }

        public Criteria andFechaAltaGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_ALTA >=", value, "fechaAlta");
            return (Criteria) this;
        }

        public Criteria andFechaAltaLessThan(Date value) {
            addCriterion("FECHA_ALTA <", value, "fechaAlta");
            return (Criteria) this;
        }

        public Criteria andFechaAltaLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_ALTA <=", value, "fechaAlta");
            return (Criteria) this;
        }

        public Criteria andFechaAltaIn(List<Date> values) {
            addCriterion("FECHA_ALTA in", values, "fechaAlta");
            return (Criteria) this;
        }

        public Criteria andFechaAltaNotIn(List<Date> values) {
            addCriterion("FECHA_ALTA not in", values, "fechaAlta");
            return (Criteria) this;
        }

        public Criteria andFechaAltaBetween(Date value1, Date value2) {
            addCriterion("FECHA_ALTA between", value1, value2, "fechaAlta");
            return (Criteria) this;
        }

        public Criteria andFechaAltaNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_ALTA not between", value1, value2, "fechaAlta");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionIsNull() {
            addCriterion("FECHA_MODIFICACION is null");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionIsNotNull() {
            addCriterion("FECHA_MODIFICACION is not null");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION =", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionNotEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION <>", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionGreaterThan(Date value) {
            addCriterion("FECHA_MODIFICACION >", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION >=", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionLessThan(Date value) {
            addCriterion("FECHA_MODIFICACION <", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION <=", value, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionIn(List<Date> values) {
            addCriterion("FECHA_MODIFICACION in", values, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionNotIn(List<Date> values) {
            addCriterion("FECHA_MODIFICACION not in", values, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionBetween(Date value1, Date value2) {
            addCriterion("FECHA_MODIFICACION between", value1, value2, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_MODIFICACION not between", value1, value2, "fechaModificacion");
            return (Criteria) this;
        }

        public Criteria andFechaBajaIsNull() {
            addCriterion("FECHA_BAJA is null");
            return (Criteria) this;
        }

        public Criteria andFechaBajaIsNotNull() {
            addCriterion("FECHA_BAJA is not null");
            return (Criteria) this;
        }

        public Criteria andFechaBajaEqualTo(Date value) {
            addCriterion("FECHA_BAJA =", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaNotEqualTo(Date value) {
            addCriterion("FECHA_BAJA <>", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaGreaterThan(Date value) {
            addCriterion("FECHA_BAJA >", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_BAJA >=", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaLessThan(Date value) {
            addCriterion("FECHA_BAJA <", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_BAJA <=", value, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaIn(List<Date> values) {
            addCriterion("FECHA_BAJA in", values, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaNotIn(List<Date> values) {
            addCriterion("FECHA_BAJA not in", values, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaBetween(Date value1, Date value2) {
            addCriterion("FECHA_BAJA between", value1, value2, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andFechaBajaNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_BAJA not between", value1, value2, "fechaBaja");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoIsNull() {
            addCriterion("CODFIDELIZADO is null");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoIsNotNull() {
            addCriterion("CODFIDELIZADO is not null");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoEqualTo(String value) {
            addCriterion("CODFIDELIZADO =", value, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoNotEqualTo(String value) {
            addCriterion("CODFIDELIZADO <>", value, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoGreaterThan(String value) {
            addCriterion("CODFIDELIZADO >", value, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoGreaterThanOrEqualTo(String value) {
            addCriterion("CODFIDELIZADO >=", value, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoLessThan(String value) {
            addCriterion("CODFIDELIZADO <", value, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoLessThanOrEqualTo(String value) {
            addCriterion("CODFIDELIZADO <=", value, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoLike(String value) {
            addCriterion("CODFIDELIZADO like", value, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoNotLike(String value) {
            addCriterion("CODFIDELIZADO not like", value, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoIn(List<String> values) {
            addCriterion("CODFIDELIZADO in", values, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoNotIn(List<String> values) {
            addCriterion("CODFIDELIZADO not in", values, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoBetween(String value1, String value2) {
            addCriterion("CODFIDELIZADO between", value1, value2, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoNotBetween(String value1, String value2) {
            addCriterion("CODFIDELIZADO not between", value1, value2, "codFidelizado");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andNombreLikeInsensitive(String value) {
            addCriterion("upper(NOMBRE) like", value.toUpperCase(), "nombre");
            return (Criteria) this;
        }

        public Criteria andApellidosLikeInsensitive(String value) {
            addCriterion("upper(APELLIDOS) like", value.toUpperCase(), "apellidos");
            return (Criteria) this;
        }

        public Criteria andDomicilioLikeInsensitive(String value) {
            addCriterion("upper(DOMICILIO) like", value.toUpperCase(), "domicilio");
            return (Criteria) this;
        }

        public Criteria andPoblacionLikeInsensitive(String value) {
            addCriterion("upper(POBLACION) like", value.toUpperCase(), "poblacion");
            return (Criteria) this;
        }

        public Criteria andLocalidadLikeInsensitive(String value) {
            addCriterion("upper(LOCALIDAD) like", value.toUpperCase(), "localidad");
            return (Criteria) this;
        }

        public Criteria andProvinciaLikeInsensitive(String value) {
            addCriterion("upper(PROVINCIA) like", value.toUpperCase(), "provincia");
            return (Criteria) this;
        }

        public Criteria andCpLikeInsensitive(String value) {
            addCriterion("upper(CP) like", value.toUpperCase(), "cp");
            return (Criteria) this;
        }

        public Criteria andCodPaisLikeInsensitive(String value) {
            addCriterion("upper(CODPAIS) like", value.toUpperCase(), "codPais");
            return (Criteria) this;
        }

        public Criteria andCodTipoIdenLikeInsensitive(String value) {
            addCriterion("upper(CODTIPOIDEN) like", value.toUpperCase(), "codTipoIden");
            return (Criteria) this;
        }

        public Criteria andDocumentoLikeInsensitive(String value) {
            addCriterion("upper(DOCUMENTO) like", value.toUpperCase(), "documento");
            return (Criteria) this;
        }

        public Criteria andObservacionesLikeInsensitive(String value) {
            addCriterion("upper(OBSERVACIONES) like", value.toUpperCase(), "observaciones");
            return (Criteria) this;
        }

        public Criteria andSexoLikeInsensitive(String value) {
            addCriterion("upper(SEXO) like", value.toUpperCase(), "sexo");
            return (Criteria) this;
        }

        public Criteria andCodEstCivilLikeInsensitive(String value) {
            addCriterion("upper(CODESTCIVIL) like", value.toUpperCase(), "codEstCivil");
            return (Criteria) this;
        }

        public Criteria andCodFidelizadoLikeInsensitive(String value) {
            addCriterion("upper(CODFIDELIZADO) like", value.toUpperCase(), "codFidelizado");
            return (Criteria) this;
        }
        
        public Criteria andActivoEqualToFidelizados(Boolean value) {
            addActivoCriterion("FIDELIZADOS.ACTIVO =", value, "activo");
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