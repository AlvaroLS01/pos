package com.comerzzia.api.loyalty.persistence.customers.access;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccesoFidelizadoExample {
    public static final String ORDER_BY_UID_INSTANCIA = "UID_INSTANCIA";

    public static final String ORDER_BY_UID_INSTANCIA_DESC = "UID_INSTANCIA DESC";

    public static final String ORDER_BY_USUARIO = "USUARIO";

    public static final String ORDER_BY_USUARIO_DESC = "USUARIO DESC";

    public static final String ORDER_BY_ID_FIDELIZADO = "ID_FIDELIZADO";

    public static final String ORDER_BY_ID_FIDELIZADO_DESC = "ID_FIDELIZADO DESC";

    public static final String ORDER_BY_EMAIL = "EMAIL";

    public static final String ORDER_BY_EMAIL_DESC = "EMAIL DESC";

    public static final String ORDER_BY_CLAVE = "CLAVE";

    public static final String ORDER_BY_CLAVE_DESC = "CLAVE DESC";

    public static final String ORDER_BY_ACTIVO = "ACTIVO";

    public static final String ORDER_BY_ACTIVO_DESC = "ACTIVO DESC";

    public static final String ORDER_BY_FECHA_ALTA = "FECHA_ALTA";

    public static final String ORDER_BY_FECHA_ALTA_DESC = "FECHA_ALTA DESC";

    public static final String ORDER_BY_FECHA_MODIFICACION = "FECHA_MODIFICACION";

    public static final String ORDER_BY_FECHA_MODIFICACION_DESC = "FECHA_MODIFICACION DESC";

    public static final String ORDER_BY_FECHA_MODIFICACION_CLAVE = "FECHA_MODIFICACION_CLAVE";

    public static final String ORDER_BY_FECHA_MODIFICACION_CLAVE_DESC = "FECHA_MODIFICACION_CLAVE DESC";

    public static final String ORDER_BY_FECHA_BAJA = "FECHA_BAJA";

    public static final String ORDER_BY_FECHA_BAJA_DESC = "FECHA_BAJA DESC";

    public static final String ORDER_BY_CLAVE_RESETEADA = "CLAVE_RESETEADA";

    public static final String ORDER_BY_CLAVE_RESETEADA_DESC = "CLAVE_RESETEADA DESC";

    public static final String ORDER_BY_EMAIL_VERIFICADO = "EMAIL_VERIFICADO";

    public static final String ORDER_BY_EMAIL_VERIFICADO_DESC = "EMAIL_VERIFICADO DESC";

    public static final String ORDER_BY_FECHA_ULTIMO_LOGIN = "FECHA_ULTIMO_LOGIN";

    public static final String ORDER_BY_FECHA_ULTIMO_LOGIN_DESC = "FECHA_ULTIMO_LOGIN DESC";

    public static final String ORDER_BY_FECHA_ULTIMO_INTENTO_FALLIDO = "FECHA_ULTIMO_INTENTO_FALLIDO";

    public static final String ORDER_BY_FECHA_ULTIMO_INTENTO_FALLIDO_DESC = "FECHA_ULTIMO_INTENTO_FALLIDO DESC";

    public static final String ORDER_BY_NUMERO_INTENTOS_FALLIDOS = "NUMERO_INTENTOS_FALLIDOS";

    public static final String ORDER_BY_NUMERO_INTENTOS_FALLIDOS_DESC = "NUMERO_INTENTOS_FALLIDOS DESC";

    public static final String ORDER_BY_RECUERDO_CLAVE_PREGUNTA = "RECUERDO_CLAVE_PREGUNTA";

    public static final String ORDER_BY_RECUERDO_CLAVE_PREGUNTA_DESC = "RECUERDO_CLAVE_PREGUNTA DESC";

    public static final String ORDER_BY_RECUERDO_CLAVE_RESPUESTA = "RECUERDO_CLAVE_RESPUESTA";

    public static final String ORDER_BY_RECUERDO_CLAVE_RESPUESTA_DESC = "RECUERDO_CLAVE_RESPUESTA DESC";

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AccesoFidelizadoExample() {
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

        public Criteria andUsuarioIsNull() {
            addCriterion("USUARIO is null");
            return (Criteria) this;
        }

        public Criteria andUsuarioIsNotNull() {
            addCriterion("USUARIO is not null");
            return (Criteria) this;
        }

        public Criteria andUsuarioEqualTo(String value) {
            addCriterion("USUARIO =", value, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioNotEqualTo(String value) {
            addCriterion("USUARIO <>", value, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioGreaterThan(String value) {
            addCriterion("USUARIO >", value, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioGreaterThanOrEqualTo(String value) {
            addCriterion("USUARIO >=", value, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioLessThan(String value) {
            addCriterion("USUARIO <", value, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioLessThanOrEqualTo(String value) {
            addCriterion("USUARIO <=", value, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioLike(String value) {
            addCriterion("USUARIO like", value, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioNotLike(String value) {
            addCriterion("USUARIO not like", value, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioIn(List<String> values) {
            addCriterion("USUARIO in", values, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioNotIn(List<String> values) {
            addCriterion("USUARIO not in", values, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioBetween(String value1, String value2) {
            addCriterion("USUARIO between", value1, value2, "usuario");
            return (Criteria) this;
        }

        public Criteria andUsuarioNotBetween(String value1, String value2) {
            addCriterion("USUARIO not between", value1, value2, "usuario");
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

        public Criteria andEmailIsNull() {
            addCriterion("EMAIL is null");
            return (Criteria) this;
        }

        public Criteria andEmailIsNotNull() {
            addCriterion("EMAIL is not null");
            return (Criteria) this;
        }

        public Criteria andEmailEqualTo(String value) {
            addCriterion("EMAIL =", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailNotEqualTo(String value) {
            addCriterion("EMAIL <>", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailGreaterThan(String value) {
            addCriterion("EMAIL >", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailGreaterThanOrEqualTo(String value) {
            addCriterion("EMAIL >=", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailLessThan(String value) {
            addCriterion("EMAIL <", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailLessThanOrEqualTo(String value) {
            addCriterion("EMAIL <=", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailLike(String value) {
            addCriterion("EMAIL like", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailNotLike(String value) {
            addCriterion("EMAIL not like", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailIn(List<String> values) {
            addCriterion("EMAIL in", values, "email");
            return (Criteria) this;
        }

        public Criteria andEmailNotIn(List<String> values) {
            addCriterion("EMAIL not in", values, "email");
            return (Criteria) this;
        }

        public Criteria andEmailBetween(String value1, String value2) {
            addCriterion("EMAIL between", value1, value2, "email");
            return (Criteria) this;
        }

        public Criteria andEmailNotBetween(String value1, String value2) {
            addCriterion("EMAIL not between", value1, value2, "email");
            return (Criteria) this;
        }

        public Criteria andClaveIsNull() {
            addCriterion("CLAVE is null");
            return (Criteria) this;
        }

        public Criteria andClaveIsNotNull() {
            addCriterion("CLAVE is not null");
            return (Criteria) this;
        }

        public Criteria andClaveEqualTo(String value) {
            addCriterion("CLAVE =", value, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveNotEqualTo(String value) {
            addCriterion("CLAVE <>", value, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveGreaterThan(String value) {
            addCriterion("CLAVE >", value, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveGreaterThanOrEqualTo(String value) {
            addCriterion("CLAVE >=", value, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveLessThan(String value) {
            addCriterion("CLAVE <", value, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveLessThanOrEqualTo(String value) {
            addCriterion("CLAVE <=", value, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveLike(String value) {
            addCriterion("CLAVE like", value, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveNotLike(String value) {
            addCriterion("CLAVE not like", value, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveIn(List<String> values) {
            addCriterion("CLAVE in", values, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveNotIn(List<String> values) {
            addCriterion("CLAVE not in", values, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveBetween(String value1, String value2) {
            addCriterion("CLAVE between", value1, value2, "clave");
            return (Criteria) this;
        }

        public Criteria andClaveNotBetween(String value1, String value2) {
            addCriterion("CLAVE not between", value1, value2, "clave");
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

        public Criteria andFechaModificacionClaveIsNull() {
            addCriterion("FECHA_MODIFICACION_CLAVE is null");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveIsNotNull() {
            addCriterion("FECHA_MODIFICACION_CLAVE is not null");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION_CLAVE =", value, "fechaModificacionClave");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveNotEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION_CLAVE <>", value, "fechaModificacionClave");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveGreaterThan(Date value) {
            addCriterion("FECHA_MODIFICACION_CLAVE >", value, "fechaModificacionClave");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION_CLAVE >=", value, "fechaModificacionClave");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveLessThan(Date value) {
            addCriterion("FECHA_MODIFICACION_CLAVE <", value, "fechaModificacionClave");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_MODIFICACION_CLAVE <=", value, "fechaModificacionClave");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveIn(List<Date> values) {
            addCriterion("FECHA_MODIFICACION_CLAVE in", values, "fechaModificacionClave");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveNotIn(List<Date> values) {
            addCriterion("FECHA_MODIFICACION_CLAVE not in", values, "fechaModificacionClave");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveBetween(Date value1, Date value2) {
            addCriterion("FECHA_MODIFICACION_CLAVE between", value1, value2, "fechaModificacionClave");
            return (Criteria) this;
        }

        public Criteria andFechaModificacionClaveNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_MODIFICACION_CLAVE not between", value1, value2, "fechaModificacionClave");
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

        public Criteria andClaveReseteadaIsNull() {
            addCriterion("CLAVE_RESETEADA is null");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaIsNotNull() {
            addCriterion("CLAVE_RESETEADA is not null");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaEqualTo(String value) {
            addCriterion("CLAVE_RESETEADA =", value, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaNotEqualTo(String value) {
            addCriterion("CLAVE_RESETEADA <>", value, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaGreaterThan(String value) {
            addCriterion("CLAVE_RESETEADA >", value, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaGreaterThanOrEqualTo(String value) {
            addCriterion("CLAVE_RESETEADA >=", value, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaLessThan(String value) {
            addCriterion("CLAVE_RESETEADA <", value, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaLessThanOrEqualTo(String value) {
            addCriterion("CLAVE_RESETEADA <=", value, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaLike(String value) {
            addCriterion("CLAVE_RESETEADA like", value, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaNotLike(String value) {
            addCriterion("CLAVE_RESETEADA not like", value, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaIn(List<String> values) {
            addCriterion("CLAVE_RESETEADA in", values, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaNotIn(List<String> values) {
            addCriterion("CLAVE_RESETEADA not in", values, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaBetween(String value1, String value2) {
            addCriterion("CLAVE_RESETEADA between", value1, value2, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaNotBetween(String value1, String value2) {
            addCriterion("CLAVE_RESETEADA not between", value1, value2, "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoIsNull() {
            addCriterion("EMAIL_VERIFICADO is null");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoIsNotNull() {
            addCriterion("EMAIL_VERIFICADO is not null");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoEqualTo(String value) {
            addCriterion("EMAIL_VERIFICADO =", value, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoNotEqualTo(String value) {
            addCriterion("EMAIL_VERIFICADO <>", value, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoGreaterThan(String value) {
            addCriterion("EMAIL_VERIFICADO >", value, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoGreaterThanOrEqualTo(String value) {
            addCriterion("EMAIL_VERIFICADO >=", value, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoLessThan(String value) {
            addCriterion("EMAIL_VERIFICADO <", value, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoLessThanOrEqualTo(String value) {
            addCriterion("EMAIL_VERIFICADO <=", value, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoLike(String value) {
            addCriterion("EMAIL_VERIFICADO like", value, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoNotLike(String value) {
            addCriterion("EMAIL_VERIFICADO not like", value, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoIn(List<String> values) {
            addCriterion("EMAIL_VERIFICADO in", values, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoNotIn(List<String> values) {
            addCriterion("EMAIL_VERIFICADO not in", values, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoBetween(String value1, String value2) {
            addCriterion("EMAIL_VERIFICADO between", value1, value2, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoNotBetween(String value1, String value2) {
            addCriterion("EMAIL_VERIFICADO not between", value1, value2, "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginIsNull() {
            addCriterion("FECHA_ULTIMO_LOGIN is null");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginIsNotNull() {
            addCriterion("FECHA_ULTIMO_LOGIN is not null");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_LOGIN =", value, "fechaUltimoLogin");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginNotEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_LOGIN <>", value, "fechaUltimoLogin");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginGreaterThan(Date value) {
            addCriterion("FECHA_ULTIMO_LOGIN >", value, "fechaUltimoLogin");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_LOGIN >=", value, "fechaUltimoLogin");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginLessThan(Date value) {
            addCriterion("FECHA_ULTIMO_LOGIN <", value, "fechaUltimoLogin");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_LOGIN <=", value, "fechaUltimoLogin");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginIn(List<Date> values) {
            addCriterion("FECHA_ULTIMO_LOGIN in", values, "fechaUltimoLogin");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginNotIn(List<Date> values) {
            addCriterion("FECHA_ULTIMO_LOGIN not in", values, "fechaUltimoLogin");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginBetween(Date value1, Date value2) {
            addCriterion("FECHA_ULTIMO_LOGIN between", value1, value2, "fechaUltimoLogin");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoLoginNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_ULTIMO_LOGIN not between", value1, value2, "fechaUltimoLogin");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoIsNull() {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO is null");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoIsNotNull() {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO is not null");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO =", value, "fechaUltimoIntentoFallido");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoNotEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO <>", value, "fechaUltimoIntentoFallido");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoGreaterThan(Date value) {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO >", value, "fechaUltimoIntentoFallido");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoGreaterThanOrEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO >=", value, "fechaUltimoIntentoFallido");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoLessThan(Date value) {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO <", value, "fechaUltimoIntentoFallido");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoLessThanOrEqualTo(Date value) {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO <=", value, "fechaUltimoIntentoFallido");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoIn(List<Date> values) {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO in", values, "fechaUltimoIntentoFallido");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoNotIn(List<Date> values) {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO not in", values, "fechaUltimoIntentoFallido");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoBetween(Date value1, Date value2) {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO between", value1, value2, "fechaUltimoIntentoFallido");
            return (Criteria) this;
        }

        public Criteria andFechaUltimoIntentoFallidoNotBetween(Date value1, Date value2) {
            addCriterion("FECHA_ULTIMO_INTENTO_FALLIDO not between", value1, value2, "fechaUltimoIntentoFallido");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosIsNull() {
            addCriterion("NUMERO_INTENTOS_FALLIDOS is null");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosIsNotNull() {
            addCriterion("NUMERO_INTENTOS_FALLIDOS is not null");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosEqualTo(Short value) {
            addCriterion("NUMERO_INTENTOS_FALLIDOS =", value, "numeroIntentosFallidos");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosNotEqualTo(Short value) {
            addCriterion("NUMERO_INTENTOS_FALLIDOS <>", value, "numeroIntentosFallidos");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosGreaterThan(Short value) {
            addCriterion("NUMERO_INTENTOS_FALLIDOS >", value, "numeroIntentosFallidos");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosGreaterThanOrEqualTo(Short value) {
            addCriterion("NUMERO_INTENTOS_FALLIDOS >=", value, "numeroIntentosFallidos");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosLessThan(Short value) {
            addCriterion("NUMERO_INTENTOS_FALLIDOS <", value, "numeroIntentosFallidos");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosLessThanOrEqualTo(Short value) {
            addCriterion("NUMERO_INTENTOS_FALLIDOS <=", value, "numeroIntentosFallidos");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosIn(List<Short> values) {
            addCriterion("NUMERO_INTENTOS_FALLIDOS in", values, "numeroIntentosFallidos");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosNotIn(List<Short> values) {
            addCriterion("NUMERO_INTENTOS_FALLIDOS not in", values, "numeroIntentosFallidos");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosBetween(Short value1, Short value2) {
            addCriterion("NUMERO_INTENTOS_FALLIDOS between", value1, value2, "numeroIntentosFallidos");
            return (Criteria) this;
        }

        public Criteria andNumeroIntentosFallidosNotBetween(Short value1, Short value2) {
            addCriterion("NUMERO_INTENTOS_FALLIDOS not between", value1, value2, "numeroIntentosFallidos");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaIsNull() {
            addCriterion("RECUERDO_CLAVE_PREGUNTA is null");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaIsNotNull() {
            addCriterion("RECUERDO_CLAVE_PREGUNTA is not null");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaEqualTo(String value) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA =", value, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaNotEqualTo(String value) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA <>", value, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaGreaterThan(String value) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA >", value, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaGreaterThanOrEqualTo(String value) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA >=", value, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaLessThan(String value) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA <", value, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaLessThanOrEqualTo(String value) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA <=", value, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaLike(String value) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA like", value, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaNotLike(String value) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA not like", value, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaIn(List<String> values) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA in", values, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaNotIn(List<String> values) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA not in", values, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaBetween(String value1, String value2) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA between", value1, value2, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaNotBetween(String value1, String value2) {
            addCriterion("RECUERDO_CLAVE_PREGUNTA not between", value1, value2, "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaIsNull() {
            addCriterion("RECUERDO_CLAVE_RESPUESTA is null");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaIsNotNull() {
            addCriterion("RECUERDO_CLAVE_RESPUESTA is not null");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaEqualTo(String value) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA =", value, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaNotEqualTo(String value) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA <>", value, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaGreaterThan(String value) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA >", value, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaGreaterThanOrEqualTo(String value) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA >=", value, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaLessThan(String value) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA <", value, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaLessThanOrEqualTo(String value) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA <=", value, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaLike(String value) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA like", value, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaNotLike(String value) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA not like", value, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaIn(List<String> values) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA in", values, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaNotIn(List<String> values) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA not in", values, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaBetween(String value1, String value2) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA between", value1, value2, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaNotBetween(String value1, String value2) {
            addCriterion("RECUERDO_CLAVE_RESPUESTA not between", value1, value2, "recuerdoClaveRespuesta");
            return (Criteria) this;
        }

        public Criteria andUidInstanciaLikeInsensitive(String value) {
            addCriterion("upper(UID_INSTANCIA) like", value.toUpperCase(), "uidInstancia");
            return (Criteria) this;
        }

        public Criteria andUsuarioLikeInsensitive(String value) {
            addCriterion("upper(USUARIO) like", value.toUpperCase(), "usuario");
            return (Criteria) this;
        }

        public Criteria andEmailLikeInsensitive(String value) {
            addCriterion("upper(EMAIL) like", value.toUpperCase(), "email");
            return (Criteria) this;
        }

        public Criteria andClaveLikeInsensitive(String value) {
            addCriterion("upper(CLAVE) like", value.toUpperCase(), "clave");
            return (Criteria) this;
        }

        public Criteria andClaveReseteadaLikeInsensitive(String value) {
            addCriterion("upper(CLAVE_RESETEADA) like", value.toUpperCase(), "claveReseteada");
            return (Criteria) this;
        }

        public Criteria andEmailVerificadoLikeInsensitive(String value) {
            addCriterion("upper(EMAIL_VERIFICADO) like", value.toUpperCase(), "emailVerificado");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClavePreguntaLikeInsensitive(String value) {
            addCriterion("upper(RECUERDO_CLAVE_PREGUNTA) like", value.toUpperCase(), "recuerdoClavePregunta");
            return (Criteria) this;
        }

        public Criteria andRecuerdoClaveRespuestaLikeInsensitive(String value) {
            addCriterion("upper(RECUERDO_CLAVE_RESPUESTA) like", value.toUpperCase(), "recuerdoClaveRespuesta");
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