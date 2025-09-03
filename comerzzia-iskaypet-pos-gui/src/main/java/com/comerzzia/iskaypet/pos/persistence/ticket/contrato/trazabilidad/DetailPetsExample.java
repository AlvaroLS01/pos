package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad;

import java.util.ArrayList;
import java.util.List;

public class DetailPetsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DetailPetsExample() {
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

        public Criteria andActivityIdIsNull() {
            addCriterion("ACTIVITY_ID is null");
            return (Criteria) this;
        }

        public Criteria andActivityIdIsNotNull() {
            addCriterion("ACTIVITY_ID is not null");
            return (Criteria) this;
        }

        public Criteria andActivityIdEqualTo(String value) {
            addCriterion("ACTIVITY_ID =", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdNotEqualTo(String value) {
            addCriterion("ACTIVITY_ID <>", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdGreaterThan(String value) {
            addCriterion("ACTIVITY_ID >", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdGreaterThanOrEqualTo(String value) {
            addCriterion("ACTIVITY_ID >=", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdLessThan(String value) {
            addCriterion("ACTIVITY_ID <", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdLessThanOrEqualTo(String value) {
            addCriterion("ACTIVITY_ID <=", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdLike(String value) {
            addCriterion("ACTIVITY_ID like", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdNotLike(String value) {
            addCriterion("ACTIVITY_ID not like", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdIn(List<String> values) {
            addCriterion("ACTIVITY_ID in", values, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdNotIn(List<String> values) {
            addCriterion("ACTIVITY_ID not in", values, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdBetween(String value1, String value2) {
            addCriterion("ACTIVITY_ID between", value1, value2, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdNotBetween(String value1, String value2) {
            addCriterion("ACTIVITY_ID not between", value1, value2, "activityId");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialIsNull() {
            addCriterion("TIPO_MATERIAL is null");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialIsNotNull() {
            addCriterion("TIPO_MATERIAL is not null");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialEqualTo(String value) {
            addCriterion("TIPO_MATERIAL =", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialNotEqualTo(String value) {
            addCriterion("TIPO_MATERIAL <>", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialGreaterThan(String value) {
            addCriterion("TIPO_MATERIAL >", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialGreaterThanOrEqualTo(String value) {
            addCriterion("TIPO_MATERIAL >=", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialLessThan(String value) {
            addCriterion("TIPO_MATERIAL <", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialLessThanOrEqualTo(String value) {
            addCriterion("TIPO_MATERIAL <=", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialLike(String value) {
            addCriterion("TIPO_MATERIAL like", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialNotLike(String value) {
            addCriterion("TIPO_MATERIAL not like", value, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialIn(List<String> values) {
            addCriterion("TIPO_MATERIAL in", values, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialNotIn(List<String> values) {
            addCriterion("TIPO_MATERIAL not in", values, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialBetween(String value1, String value2) {
            addCriterion("TIPO_MATERIAL between", value1, value2, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andTipoMaterialNotBetween(String value1, String value2) {
            addCriterion("TIPO_MATERIAL not between", value1, value2, "tipoMaterial");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosIsNull() {
            addCriterion("GRUPO_ARTICULOS is null");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosIsNotNull() {
            addCriterion("GRUPO_ARTICULOS is not null");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosEqualTo(String value) {
            addCriterion("GRUPO_ARTICULOS =", value, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosNotEqualTo(String value) {
            addCriterion("GRUPO_ARTICULOS <>", value, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosGreaterThan(String value) {
            addCriterion("GRUPO_ARTICULOS >", value, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosGreaterThanOrEqualTo(String value) {
            addCriterion("GRUPO_ARTICULOS >=", value, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosLessThan(String value) {
            addCriterion("GRUPO_ARTICULOS <", value, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosLessThanOrEqualTo(String value) {
            addCriterion("GRUPO_ARTICULOS <=", value, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosLike(String value) {
            addCriterion("GRUPO_ARTICULOS like", value, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosNotLike(String value) {
            addCriterion("GRUPO_ARTICULOS not like", value, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosIn(List<String> values) {
            addCriterion("GRUPO_ARTICULOS in", values, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosNotIn(List<String> values) {
            addCriterion("GRUPO_ARTICULOS not in", values, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosBetween(String value1, String value2) {
            addCriterion("GRUPO_ARTICULOS between", value1, value2, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andGrupoArticulosNotBetween(String value1, String value2) {
            addCriterion("GRUPO_ARTICULOS not between", value1, value2, "grupoArticulos");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaIsNull() {
            addCriterion("VALOR_CARACTERISTICA is null");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaIsNotNull() {
            addCriterion("VALOR_CARACTERISTICA is not null");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaEqualTo(String value) {
            addCriterion("VALOR_CARACTERISTICA =", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaNotEqualTo(String value) {
            addCriterion("VALOR_CARACTERISTICA <>", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaGreaterThan(String value) {
            addCriterion("VALOR_CARACTERISTICA >", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaGreaterThanOrEqualTo(String value) {
            addCriterion("VALOR_CARACTERISTICA >=", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaLessThan(String value) {
            addCriterion("VALOR_CARACTERISTICA <", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaLessThanOrEqualTo(String value) {
            addCriterion("VALOR_CARACTERISTICA <=", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaLike(String value) {
            addCriterion("VALOR_CARACTERISTICA like", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaNotLike(String value) {
            addCriterion("VALOR_CARACTERISTICA not like", value, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaIn(List<String> values) {
            addCriterion("VALOR_CARACTERISTICA in", values, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaNotIn(List<String> values) {
            addCriterion("VALOR_CARACTERISTICA not in", values, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaBetween(String value1, String value2) {
            addCriterion("VALOR_CARACTERISTICA between", value1, value2, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andValorCaracteristicaNotBetween(String value1, String value2) {
            addCriterion("VALOR_CARACTERISTICA not between", value1, value2, "valorCaracteristica");
            return (Criteria) this;
        }

        public Criteria andPaisIsNull() {
            addCriterion("PAIS is null");
            return (Criteria) this;
        }

        public Criteria andPaisIsNotNull() {
            addCriterion("PAIS is not null");
            return (Criteria) this;
        }

        public Criteria andPaisEqualTo(String value) {
            addCriterion("PAIS =", value, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisNotEqualTo(String value) {
            addCriterion("PAIS <>", value, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisGreaterThan(String value) {
            addCriterion("PAIS >", value, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisGreaterThanOrEqualTo(String value) {
            addCriterion("PAIS >=", value, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisLessThan(String value) {
            addCriterion("PAIS <", value, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisLessThanOrEqualTo(String value) {
            addCriterion("PAIS <=", value, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisLike(String value) {
            addCriterion("PAIS like", value, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisNotLike(String value) {
            addCriterion("PAIS not like", value, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisIn(List<String> values) {
            addCriterion("PAIS in", values, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisNotIn(List<String> values) {
            addCriterion("PAIS not in", values, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisBetween(String value1, String value2) {
            addCriterion("PAIS between", value1, value2, "pais");
            return (Criteria) this;
        }

        public Criteria andPaisNotBetween(String value1, String value2) {
            addCriterion("PAIS not between", value1, value2, "pais");
            return (Criteria) this;
        }

        public Criteria andRegionIsNull() {
            addCriterion("REGION is null");
            return (Criteria) this;
        }

        public Criteria andRegionIsNotNull() {
            addCriterion("REGION is not null");
            return (Criteria) this;
        }

        public Criteria andRegionEqualTo(String value) {
            addCriterion("REGION =", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionNotEqualTo(String value) {
            addCriterion("REGION <>", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionGreaterThan(String value) {
            addCriterion("REGION >", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionGreaterThanOrEqualTo(String value) {
            addCriterion("REGION >=", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionLessThan(String value) {
            addCriterion("REGION <", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionLessThanOrEqualTo(String value) {
            addCriterion("REGION <=", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionLike(String value) {
            addCriterion("REGION like", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionNotLike(String value) {
            addCriterion("REGION not like", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionIn(List<String> values) {
            addCriterion("REGION in", values, "region");
            return (Criteria) this;
        }

        public Criteria andRegionNotIn(List<String> values) {
            addCriterion("REGION not in", values, "region");
            return (Criteria) this;
        }

        public Criteria andRegionBetween(String value1, String value2) {
            addCriterion("REGION between", value1, value2, "region");
            return (Criteria) this;
        }

        public Criteria andRegionNotBetween(String value1, String value2) {
            addCriterion("REGION not between", value1, value2, "region");
            return (Criteria) this;
        }

        public Criteria andJerarquiaIsNull() {
            addCriterion("JERARQUIA is null");
            return (Criteria) this;
        }

        public Criteria andJerarquiaIsNotNull() {
            addCriterion("JERARQUIA is not null");
            return (Criteria) this;
        }

        public Criteria andJerarquiaEqualTo(String value) {
            addCriterion("JERARQUIA =", value, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaNotEqualTo(String value) {
            addCriterion("JERARQUIA <>", value, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaGreaterThan(String value) {
            addCriterion("JERARQUIA >", value, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaGreaterThanOrEqualTo(String value) {
            addCriterion("JERARQUIA >=", value, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaLessThan(String value) {
            addCriterion("JERARQUIA <", value, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaLessThanOrEqualTo(String value) {
            addCriterion("JERARQUIA <=", value, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaLike(String value) {
            addCriterion("JERARQUIA like", value, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaNotLike(String value) {
            addCriterion("JERARQUIA not like", value, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaIn(List<String> values) {
            addCriterion("JERARQUIA in", values, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaNotIn(List<String> values) {
            addCriterion("JERARQUIA not in", values, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaBetween(String value1, String value2) {
            addCriterion("JERARQUIA between", value1, value2, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andJerarquiaNotBetween(String value1, String value2) {
            addCriterion("JERARQUIA not between", value1, value2, "jerarquia");
            return (Criteria) this;
        }

        public Criteria andIdChipIsNull() {
            addCriterion("ID_CHIP is null");
            return (Criteria) this;
        }

        public Criteria andIdChipIsNotNull() {
            addCriterion("ID_CHIP is not null");
            return (Criteria) this;
        }

        public Criteria andIdChipEqualTo(String value) {
            addCriterion("ID_CHIP =", value, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipNotEqualTo(String value) {
            addCriterion("ID_CHIP <>", value, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipGreaterThan(String value) {
            addCriterion("ID_CHIP >", value, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipGreaterThanOrEqualTo(String value) {
            addCriterion("ID_CHIP >=", value, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipLessThan(String value) {
            addCriterion("ID_CHIP <", value, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipLessThanOrEqualTo(String value) {
            addCriterion("ID_CHIP <=", value, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipLike(String value) {
            addCriterion("ID_CHIP like", value, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipNotLike(String value) {
            addCriterion("ID_CHIP not like", value, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipIn(List<String> values) {
            addCriterion("ID_CHIP in", values, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipNotIn(List<String> values) {
            addCriterion("ID_CHIP not in", values, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipBetween(String value1, String value2) {
            addCriterion("ID_CHIP between", value1, value2, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdChipNotBetween(String value1, String value2) {
            addCriterion("ID_CHIP not between", value1, value2, "idChip");
            return (Criteria) this;
        }

        public Criteria andIdAnillaIsNull() {
            addCriterion("ID_ANILLA is null");
            return (Criteria) this;
        }

        public Criteria andIdAnillaIsNotNull() {
            addCriterion("ID_ANILLA is not null");
            return (Criteria) this;
        }

        public Criteria andIdAnillaEqualTo(String value) {
            addCriterion("ID_ANILLA =", value, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaNotEqualTo(String value) {
            addCriterion("ID_ANILLA <>", value, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaGreaterThan(String value) {
            addCriterion("ID_ANILLA >", value, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaGreaterThanOrEqualTo(String value) {
            addCriterion("ID_ANILLA >=", value, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaLessThan(String value) {
            addCriterion("ID_ANILLA <", value, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaLessThanOrEqualTo(String value) {
            addCriterion("ID_ANILLA <=", value, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaLike(String value) {
            addCriterion("ID_ANILLA like", value, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaNotLike(String value) {
            addCriterion("ID_ANILLA not like", value, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaIn(List<String> values) {
            addCriterion("ID_ANILLA in", values, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaNotIn(List<String> values) {
            addCriterion("ID_ANILLA not in", values, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaBetween(String value1, String value2) {
            addCriterion("ID_ANILLA between", value1, value2, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdAnillaNotBetween(String value1, String value2) {
            addCriterion("ID_ANILLA not between", value1, value2, "idAnilla");
            return (Criteria) this;
        }

        public Criteria andIdCitesIsNull() {
            addCriterion("ID_CITES is null");
            return (Criteria) this;
        }

        public Criteria andIdCitesIsNotNull() {
            addCriterion("ID_CITES is not null");
            return (Criteria) this;
        }

        public Criteria andIdCitesEqualTo(String value) {
            addCriterion("ID_CITES =", value, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesNotEqualTo(String value) {
            addCriterion("ID_CITES <>", value, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesGreaterThan(String value) {
            addCriterion("ID_CITES >", value, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesGreaterThanOrEqualTo(String value) {
            addCriterion("ID_CITES >=", value, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesLessThan(String value) {
            addCriterion("ID_CITES <", value, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesLessThanOrEqualTo(String value) {
            addCriterion("ID_CITES <=", value, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesLike(String value) {
            addCriterion("ID_CITES like", value, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesNotLike(String value) {
            addCriterion("ID_CITES not like", value, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesIn(List<String> values) {
            addCriterion("ID_CITES in", values, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesNotIn(List<String> values) {
            addCriterion("ID_CITES not in", values, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesBetween(String value1, String value2) {
            addCriterion("ID_CITES between", value1, value2, "idCites");
            return (Criteria) this;
        }

        public Criteria andIdCitesNotBetween(String value1, String value2) {
            addCriterion("ID_CITES not between", value1, value2, "idCites");
            return (Criteria) this;
        }

        public Criteria andContratoIsNull() {
            addCriterion("CONTRATO is null");
            return (Criteria) this;
        }

        public Criteria andContratoIsNotNull() {
            addCriterion("CONTRATO is not null");
            return (Criteria) this;
        }

        public Criteria andContratoEqualTo(String value) {
            addCriterion("CONTRATO =", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoNotEqualTo(String value) {
            addCriterion("CONTRATO <>", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoGreaterThan(String value) {
            addCriterion("CONTRATO >", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoGreaterThanOrEqualTo(String value) {
            addCriterion("CONTRATO >=", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoLessThan(String value) {
            addCriterion("CONTRATO <", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoLessThanOrEqualTo(String value) {
            addCriterion("CONTRATO <=", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoLike(String value) {
            addCriterion("CONTRATO like", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoNotLike(String value) {
            addCriterion("CONTRATO not like", value, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoIn(List<String> values) {
            addCriterion("CONTRATO in", values, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoNotIn(List<String> values) {
            addCriterion("CONTRATO not in", values, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoBetween(String value1, String value2) {
            addCriterion("CONTRATO between", value1, value2, "contrato");
            return (Criteria) this;
        }

        public Criteria andContratoNotBetween(String value1, String value2) {
            addCriterion("CONTRATO not between", value1, value2, "contrato");
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