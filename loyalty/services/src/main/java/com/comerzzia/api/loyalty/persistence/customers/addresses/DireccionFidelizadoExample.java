package com.comerzzia.api.loyalty.persistence.customers.addresses;

import java.util.ArrayList;
import java.util.List;

public class DireccionFidelizadoExample {
    public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_ID_FIDELIZADO = "ID_FIDELIZADO";

    public static final String ORDER_BY_ID_FIDELIZADO_DESC = "ID_FIDELIZADO DESC";

    public static final String ORDER_BY_DESCRIPCION_DIRECCION = "DESCRIPCION_DIRECCION";

    public static final String ORDER_BY_DESCRIPCION_DIRECCION_DESC = "DESCRIPCION_DIRECCION DESC";

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

    public static final String ORDER_BY_DESPAIS = "DESPAIS";

    public static final String ORDER_BY_DESPAIS_DESC = "DESPAIS DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DireccionFidelizadoExample() {
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

        public Criteria andDescripcionDireccionIsNull() {
            addCriterion("DESCRIPCION_DIRECCION is null");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionIsNotNull() {
            addCriterion("DESCRIPCION_DIRECCION is not null");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionEqualTo(String value) {
            addCriterion("DESCRIPCION_DIRECCION =", value, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionNotEqualTo(String value) {
            addCriterion("DESCRIPCION_DIRECCION <>", value, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionGreaterThan(String value) {
            addCriterion("DESCRIPCION_DIRECCION >", value, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionGreaterThanOrEqualTo(String value) {
            addCriterion("DESCRIPCION_DIRECCION >=", value, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionLessThan(String value) {
            addCriterion("DESCRIPCION_DIRECCION <", value, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionLessThanOrEqualTo(String value) {
            addCriterion("DESCRIPCION_DIRECCION <=", value, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionLike(String value) {
            addCriterion("DESCRIPCION_DIRECCION like", value, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionNotLike(String value) {
            addCriterion("DESCRIPCION_DIRECCION not like", value, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionIn(List<String> values) {
            addCriterion("DESCRIPCION_DIRECCION in", values, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionNotIn(List<String> values) {
            addCriterion("DESCRIPCION_DIRECCION not in", values, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionBetween(String value1, String value2) {
            addCriterion("DESCRIPCION_DIRECCION between", value1, value2, "descripcionDireccion");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionNotBetween(String value1, String value2) {
            addCriterion("DESCRIPCION_DIRECCION not between", value1, value2, "descripcionDireccion");
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

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andDescripcionDireccionLikeInsensitive(String value) {
            addCriterion("upper(DESCRIPCION_DIRECCION) like", value.toUpperCase(), "descripcionDireccion");
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