package com.comerzzia.iskaypet.pos.persistence.articulos.lotes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoteArticuloExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public LoteArticuloExample() {
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

        public Criteria andActivityUidIsNull() {
            addCriterion("ACTIVITY_UID is null");
            return (Criteria) this;
        }

        public Criteria andActivityUidIsNotNull() {
            addCriterion("ACTIVITY_UID is not null");
            return (Criteria) this;
        }

        public Criteria andActivityUidEqualTo(String value) {
            addCriterion("ACTIVITY_UID =", value, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidNotEqualTo(String value) {
            addCriterion("ACTIVITY_UID <>", value, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidGreaterThan(String value) {
            addCriterion("ACTIVITY_UID >", value, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidGreaterThanOrEqualTo(String value) {
            addCriterion("ACTIVITY_UID >=", value, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidLessThan(String value) {
            addCriterion("ACTIVITY_UID <", value, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidLessThanOrEqualTo(String value) {
            addCriterion("ACTIVITY_UID <=", value, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidLike(String value) {
            addCriterion("ACTIVITY_UID like", value, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidNotLike(String value) {
            addCriterion("ACTIVITY_UID not like", value, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidIn(List<String> values) {
            addCriterion("ACTIVITY_UID in", values, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidNotIn(List<String> values) {
            addCriterion("ACTIVITY_UID not in", values, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidBetween(String value1, String value2) {
            addCriterion("ACTIVITY_UID between", value1, value2, "activityUid");
            return (Criteria) this;
        }

        public Criteria andActivityUidNotBetween(String value1, String value2) {
            addCriterion("ACTIVITY_UID not between", value1, value2, "activityUid");
            return (Criteria) this;
        }

        public Criteria andWhCodeIsNull() {
            addCriterion("WH_CODE is null");
            return (Criteria) this;
        }

        public Criteria andWhCodeIsNotNull() {
            addCriterion("WH_CODE is not null");
            return (Criteria) this;
        }

        public Criteria andWhCodeEqualTo(String value) {
            addCriterion("WH_CODE =", value, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeNotEqualTo(String value) {
            addCriterion("WH_CODE <>", value, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeGreaterThan(String value) {
            addCriterion("WH_CODE >", value, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeGreaterThanOrEqualTo(String value) {
            addCriterion("WH_CODE >=", value, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeLessThan(String value) {
            addCriterion("WH_CODE <", value, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeLessThanOrEqualTo(String value) {
            addCriterion("WH_CODE <=", value, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeLike(String value) {
            addCriterion("WH_CODE like", value, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeNotLike(String value) {
            addCriterion("WH_CODE not like", value, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeIn(List<String> values) {
            addCriterion("WH_CODE in", values, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeNotIn(List<String> values) {
            addCriterion("WH_CODE not in", values, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeBetween(String value1, String value2) {
            addCriterion("WH_CODE between", value1, value2, "whCode");
            return (Criteria) this;
        }

        public Criteria andWhCodeNotBetween(String value1, String value2) {
            addCriterion("WH_CODE not between", value1, value2, "whCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeIsNull() {
            addCriterion("ITEM_CODE is null");
            return (Criteria) this;
        }

        public Criteria andItemCodeIsNotNull() {
            addCriterion("ITEM_CODE is not null");
            return (Criteria) this;
        }

        public Criteria andItemCodeEqualTo(String value) {
            addCriterion("ITEM_CODE =", value, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeNotEqualTo(String value) {
            addCriterion("ITEM_CODE <>", value, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeGreaterThan(String value) {
            addCriterion("ITEM_CODE >", value, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeGreaterThanOrEqualTo(String value) {
            addCriterion("ITEM_CODE >=", value, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeLessThan(String value) {
            addCriterion("ITEM_CODE <", value, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeLessThanOrEqualTo(String value) {
            addCriterion("ITEM_CODE <=", value, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeLike(String value) {
            addCriterion("ITEM_CODE like", value, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeNotLike(String value) {
            addCriterion("ITEM_CODE not like", value, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeIn(List<String> values) {
            addCriterion("ITEM_CODE in", values, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeNotIn(List<String> values) {
            addCriterion("ITEM_CODE not in", values, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeBetween(String value1, String value2) {
            addCriterion("ITEM_CODE between", value1, value2, "itemCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeNotBetween(String value1, String value2) {
            addCriterion("ITEM_CODE not between", value1, value2, "itemCode");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeIsNull() {
            addCriterion("COMBINATION1_CODE is null");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeIsNotNull() {
            addCriterion("COMBINATION1_CODE is not null");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeEqualTo(String value) {
            addCriterion("COMBINATION1_CODE =", value, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeNotEqualTo(String value) {
            addCriterion("COMBINATION1_CODE <>", value, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeGreaterThan(String value) {
            addCriterion("COMBINATION1_CODE >", value, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeGreaterThanOrEqualTo(String value) {
            addCriterion("COMBINATION1_CODE >=", value, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeLessThan(String value) {
            addCriterion("COMBINATION1_CODE <", value, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeLessThanOrEqualTo(String value) {
            addCriterion("COMBINATION1_CODE <=", value, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeLike(String value) {
            addCriterion("COMBINATION1_CODE like", value, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeNotLike(String value) {
            addCriterion("COMBINATION1_CODE not like", value, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeIn(List<String> values) {
            addCriterion("COMBINATION1_CODE in", values, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeNotIn(List<String> values) {
            addCriterion("COMBINATION1_CODE not in", values, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeBetween(String value1, String value2) {
            addCriterion("COMBINATION1_CODE between", value1, value2, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeNotBetween(String value1, String value2) {
            addCriterion("COMBINATION1_CODE not between", value1, value2, "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeIsNull() {
            addCriterion("COMBINATION2_CODE is null");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeIsNotNull() {
            addCriterion("COMBINATION2_CODE is not null");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeEqualTo(String value) {
            addCriterion("COMBINATION2_CODE =", value, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeNotEqualTo(String value) {
            addCriterion("COMBINATION2_CODE <>", value, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeGreaterThan(String value) {
            addCriterion("COMBINATION2_CODE >", value, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeGreaterThanOrEqualTo(String value) {
            addCriterion("COMBINATION2_CODE >=", value, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeLessThan(String value) {
            addCriterion("COMBINATION2_CODE <", value, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeLessThanOrEqualTo(String value) {
            addCriterion("COMBINATION2_CODE <=", value, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeLike(String value) {
            addCriterion("COMBINATION2_CODE like", value, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeNotLike(String value) {
            addCriterion("COMBINATION2_CODE not like", value, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeIn(List<String> values) {
            addCriterion("COMBINATION2_CODE in", values, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeNotIn(List<String> values) {
            addCriterion("COMBINATION2_CODE not in", values, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeBetween(String value1, String value2) {
            addCriterion("COMBINATION2_CODE between", value1, value2, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeNotBetween(String value1, String value2) {
            addCriterion("COMBINATION2_CODE not between", value1, value2, "combination2Code");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4IsNull() {
            addCriterion("BATCH_NUMBER_S4 is null");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4IsNotNull() {
            addCriterion("BATCH_NUMBER_S4 is not null");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4EqualTo(String value) {
            addCriterion("BATCH_NUMBER_S4 =", value, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4NotEqualTo(String value) {
            addCriterion("BATCH_NUMBER_S4 <>", value, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4GreaterThan(String value) {
            addCriterion("BATCH_NUMBER_S4 >", value, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4GreaterThanOrEqualTo(String value) {
            addCriterion("BATCH_NUMBER_S4 >=", value, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4LessThan(String value) {
            addCriterion("BATCH_NUMBER_S4 <", value, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4LessThanOrEqualTo(String value) {
            addCriterion("BATCH_NUMBER_S4 <=", value, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4Like(String value) {
            addCriterion("BATCH_NUMBER_S4 like", value, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4NotLike(String value) {
            addCriterion("BATCH_NUMBER_S4 not like", value, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4In(List<String> values) {
            addCriterion("BATCH_NUMBER_S4 in", values, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4NotIn(List<String> values) {
            addCriterion("BATCH_NUMBER_S4 not in", values, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4Between(String value1, String value2) {
            addCriterion("BATCH_NUMBER_S4 between", value1, value2, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4NotBetween(String value1, String value2) {
            addCriterion("BATCH_NUMBER_S4 not between", value1, value2, "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchIsNull() {
            addCriterion("BATCH is null");
            return (Criteria) this;
        }

        public Criteria andBatchIsNotNull() {
            addCriterion("BATCH is not null");
            return (Criteria) this;
        }

        public Criteria andBatchEqualTo(String value) {
            addCriterion("BATCH =", value, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchNotEqualTo(String value) {
            addCriterion("BATCH <>", value, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchGreaterThan(String value) {
            addCriterion("BATCH >", value, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchGreaterThanOrEqualTo(String value) {
            addCriterion("BATCH >=", value, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchLessThan(String value) {
            addCriterion("BATCH <", value, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchLessThanOrEqualTo(String value) {
            addCriterion("BATCH <=", value, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchLike(String value) {
            addCriterion("BATCH like", value, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchNotLike(String value) {
            addCriterion("BATCH not like", value, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchIn(List<String> values) {
            addCriterion("BATCH in", values, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchNotIn(List<String> values) {
            addCriterion("BATCH not in", values, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchBetween(String value1, String value2) {
            addCriterion("BATCH between", value1, value2, "batch");
            return (Criteria) this;
        }

        public Criteria andBatchNotBetween(String value1, String value2) {
            addCriterion("BATCH not between", value1, value2, "batch");
            return (Criteria) this;
        }

        public Criteria andExpirationDateIsNull() {
            addCriterion("EXPIRATION_DATE is null");
            return (Criteria) this;
        }

        public Criteria andExpirationDateIsNotNull() {
            addCriterion("EXPIRATION_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andExpirationDateEqualTo(Date value) {
            addCriterion("EXPIRATION_DATE =", value, "expirationDate");
            return (Criteria) this;
        }

        public Criteria andExpirationDateNotEqualTo(Date value) {
            addCriterion("EXPIRATION_DATE <>", value, "expirationDate");
            return (Criteria) this;
        }

        public Criteria andExpirationDateGreaterThan(Date value) {
            addCriterion("EXPIRATION_DATE >", value, "expirationDate");
            return (Criteria) this;
        }

        public Criteria andExpirationDateGreaterThanOrEqualTo(Date value) {
            addCriterion("EXPIRATION_DATE >=", value, "expirationDate");
            return (Criteria) this;
        }

        public Criteria andExpirationDateLessThan(Date value) {
            addCriterion("EXPIRATION_DATE <", value, "expirationDate");
            return (Criteria) this;
        }

        public Criteria andExpirationDateLessThanOrEqualTo(Date value) {
            addCriterion("EXPIRATION_DATE <=", value, "expirationDate");
            return (Criteria) this;
        }

        public Criteria andExpirationDateIn(List<Date> values) {
            addCriterion("EXPIRATION_DATE in", values, "expirationDate");
            return (Criteria) this;
        }

        public Criteria andExpirationDateNotIn(List<Date> values) {
            addCriterion("EXPIRATION_DATE not in", values, "expirationDate");
            return (Criteria) this;
        }

        public Criteria andExpirationDateBetween(Date value1, Date value2) {
            addCriterion("EXPIRATION_DATE between", value1, value2, "expirationDate");
            return (Criteria) this;
        }

        public Criteria andExpirationDateNotBetween(Date value1, Date value2) {
            addCriterion("EXPIRATION_DATE not between", value1, value2, "expirationDate");
            return (Criteria) this;
        }

        public Criteria andStockIsNull() {
            addCriterion("STOCK is null");
            return (Criteria) this;
        }

        public Criteria andStockIsNotNull() {
            addCriterion("STOCK is not null");
            return (Criteria) this;
        }

        public Criteria andStockEqualTo(BigDecimal value) {
            addCriterion("STOCK =", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockNotEqualTo(BigDecimal value) {
            addCriterion("STOCK <>", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockGreaterThan(BigDecimal value) {
            addCriterion("STOCK >", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("STOCK >=", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockLessThan(BigDecimal value) {
            addCriterion("STOCK <", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockLessThanOrEqualTo(BigDecimal value) {
            addCriterion("STOCK <=", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockIn(List<BigDecimal> values) {
            addCriterion("STOCK in", values, "stock");
            return (Criteria) this;
        }

        public Criteria andStockNotIn(List<BigDecimal> values) {
            addCriterion("STOCK not in", values, "stock");
            return (Criteria) this;
        }

        public Criteria andStockBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("STOCK between", value1, value2, "stock");
            return (Criteria) this;
        }

        public Criteria andStockNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("STOCK not between", value1, value2, "stock");
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

        public Criteria andActivityUidLikeInsensitive(String value) {
            addCriterion("upper(ACTIVITY_UID) like", value.toUpperCase(), "activityUid");
            return (Criteria) this;
        }

        public Criteria andWhCodeLikeInsensitive(String value) {
            addCriterion("upper(WH_CODE) like", value.toUpperCase(), "whCode");
            return (Criteria) this;
        }

        public Criteria andItemCodeLikeInsensitive(String value) {
            addCriterion("upper(ITEM_CODE) like", value.toUpperCase(), "itemCode");
            return (Criteria) this;
        }

        public Criteria andCombination1CodeLikeInsensitive(String value) {
            addCriterion("upper(COMBINATION1_CODE) like", value.toUpperCase(), "combination1Code");
            return (Criteria) this;
        }

        public Criteria andCombination2CodeLikeInsensitive(String value) {
            addCriterion("upper(COMBINATION2_CODE) like", value.toUpperCase(), "combination2Code");
            return (Criteria) this;
        }

        public Criteria andBatchNumberS4LikeInsensitive(String value) {
            addCriterion("upper(BATCH_NUMBER_S4) like", value.toUpperCase(), "batchNumberS4");
            return (Criteria) this;
        }

        public Criteria andBatchLikeInsensitive(String value) {
            addCriterion("upper(BATCH) like", value.toUpperCase(), "batch");
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