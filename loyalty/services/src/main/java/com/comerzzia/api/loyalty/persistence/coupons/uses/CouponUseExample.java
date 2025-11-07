package com.comerzzia.api.loyalty.persistence.coupons.uses;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiActivity;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@SuppressWarnings("serial")
public class CouponUseExample extends MultiActivity {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CouponUseExample() {
        oredCriteria = new ArrayList<>();
    }
    
    public CouponUseExample(IDatosSesion datosSesion) {
       super(datosSesion);
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
        protected List<Criterion> usedCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
            usedCriteria = new ArrayList<>();
        }

        public List<Criterion> getUsedCriteria() {
            return usedCriteria;
        }

        protected void addUsedCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            usedCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanNumberTypeHandler"));
            allCriteria = null;
        }

        protected void addUsedCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            usedCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanNumberTypeHandler"));
            allCriteria = null;
        }

        public boolean isValid() {
            return criteria.size() > 0
                || usedCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(usedCriteria);
            }
            return allCriteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
            allCriteria = null;
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
            allCriteria = null;
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
            allCriteria = null;
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

        public Criteria andClassIdIsNull() {
            addCriterion("CLASS_ID is null");
            return (Criteria) this;
        }

        public Criteria andClassIdIsNotNull() {
            addCriterion("CLASS_ID is not null");
            return (Criteria) this;
        }

        public Criteria andClassIdEqualTo(String value) {
            addCriterion("CLASS_ID =", value, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdNotEqualTo(String value) {
            addCriterion("CLASS_ID <>", value, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdGreaterThan(String value) {
            addCriterion("CLASS_ID >", value, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdGreaterThanOrEqualTo(String value) {
            addCriterion("CLASS_ID >=", value, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdLessThan(String value) {
            addCriterion("CLASS_ID <", value, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdLessThanOrEqualTo(String value) {
            addCriterion("CLASS_ID <=", value, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdLike(String value) {
            addCriterion("CLASS_ID like", value, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdNotLike(String value) {
            addCriterion("CLASS_ID not like", value, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdIn(List<String> values) {
            addCriterion("CLASS_ID in", values, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdNotIn(List<String> values) {
            addCriterion("CLASS_ID not in", values, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdBetween(String value1, String value2) {
            addCriterion("CLASS_ID between", value1, value2, "classId");
            return (Criteria) this;
        }

        public Criteria andClassIdNotBetween(String value1, String value2) {
            addCriterion("CLASS_ID not between", value1, value2, "classId");
            return (Criteria) this;
        }

        public Criteria andObjectIdIsNull() {
            addCriterion("OBJECT_ID is null");
            return (Criteria) this;
        }

        public Criteria andObjectIdIsNotNull() {
            addCriterion("OBJECT_ID is not null");
            return (Criteria) this;
        }

        public Criteria andObjectIdEqualTo(String value) {
            addCriterion("OBJECT_ID =", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdNotEqualTo(String value) {
            addCriterion("OBJECT_ID <>", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdGreaterThan(String value) {
            addCriterion("OBJECT_ID >", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdGreaterThanOrEqualTo(String value) {
            addCriterion("OBJECT_ID >=", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdLessThan(String value) {
            addCriterion("OBJECT_ID <", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdLessThanOrEqualTo(String value) {
            addCriterion("OBJECT_ID <=", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdLike(String value) {
            addCriterion("OBJECT_ID like", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdNotLike(String value) {
            addCriterion("OBJECT_ID not like", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdIn(List<String> values) {
            addCriterion("OBJECT_ID in", values, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdNotIn(List<String> values) {
            addCriterion("OBJECT_ID not in", values, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdBetween(String value1, String value2) {
            addCriterion("OBJECT_ID between", value1, value2, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdNotBetween(String value1, String value2) {
            addCriterion("OBJECT_ID not between", value1, value2, "objectId");
            return (Criteria) this;
        }

        public Criteria andCouponIdIsNull() {
            addCriterion("COUPON_ID is null");
            return (Criteria) this;
        }

        public Criteria andCouponIdIsNotNull() {
            addCriterion("COUPON_ID is not null");
            return (Criteria) this;
        }

        public Criteria andCouponIdEqualTo(Long value) {
            addCriterion("COUPON_ID =", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdNotEqualTo(Long value) {
            addCriterion("COUPON_ID <>", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdGreaterThan(Long value) {
            addCriterion("COUPON_ID >", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdGreaterThanOrEqualTo(Long value) {
            addCriterion("COUPON_ID >=", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdLessThan(Long value) {
            addCriterion("COUPON_ID <", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdLessThanOrEqualTo(Long value) {
            addCriterion("COUPON_ID <=", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdIn(List<Long> values) {
            addCriterion("COUPON_ID in", values, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdNotIn(List<Long> values) {
            addCriterion("COUPON_ID not in", values, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdBetween(Long value1, Long value2) {
            addCriterion("COUPON_ID between", value1, value2, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdNotBetween(Long value1, Long value2) {
            addCriterion("COUPON_ID not between", value1, value2, "couponId");
            return (Criteria) this;
        }

        public Criteria andMaxUsesIsNull() {
            addCriterion("MAX_USES is null");
            return (Criteria) this;
        }

        public Criteria andMaxUsesIsNotNull() {
            addCriterion("MAX_USES is not null");
            return (Criteria) this;
        }

        public Criteria andMaxUsesEqualTo(Long value) {
            addCriterion("MAX_USES =", value, "maxUses");
            return (Criteria) this;
        }

        public Criteria andMaxUsesNotEqualTo(Long value) {
            addCriterion("MAX_USES <>", value, "maxUses");
            return (Criteria) this;
        }

        public Criteria andMaxUsesGreaterThan(Long value) {
            addCriterion("MAX_USES >", value, "maxUses");
            return (Criteria) this;
        }

        public Criteria andMaxUsesGreaterThanOrEqualTo(Long value) {
            addCriterion("MAX_USES >=", value, "maxUses");
            return (Criteria) this;
        }

        public Criteria andMaxUsesLessThan(Long value) {
            addCriterion("MAX_USES <", value, "maxUses");
            return (Criteria) this;
        }

        public Criteria andMaxUsesLessThanOrEqualTo(Long value) {
            addCriterion("MAX_USES <=", value, "maxUses");
            return (Criteria) this;
        }

        public Criteria andMaxUsesIn(List<Long> values) {
            addCriterion("MAX_USES in", values, "maxUses");
            return (Criteria) this;
        }

        public Criteria andMaxUsesNotIn(List<Long> values) {
            addCriterion("MAX_USES not in", values, "maxUses");
            return (Criteria) this;
        }

        public Criteria andMaxUsesBetween(Long value1, Long value2) {
            addCriterion("MAX_USES between", value1, value2, "maxUses");
            return (Criteria) this;
        }

        public Criteria andMaxUsesNotBetween(Long value1, Long value2) {
            addCriterion("MAX_USES not between", value1, value2, "maxUses");
            return (Criteria) this;
        }

        public Criteria andUsesIsNull() {
            addCriterion("USES is null");
            return (Criteria) this;
        }

        public Criteria andUsesIsNotNull() {
            addCriterion("USES is not null");
            return (Criteria) this;
        }

        public Criteria andUsesEqualTo(Integer value) {
            addCriterion("USES =", value, "uses");
            return (Criteria) this;
        }

        public Criteria andUsesNotEqualTo(Integer value) {
            addCriterion("USES <>", value, "uses");
            return (Criteria) this;
        }

        public Criteria andUsesGreaterThan(Integer value) {
            addCriterion("USES >", value, "uses");
            return (Criteria) this;
        }

        public Criteria andUsesGreaterThanOrEqualTo(Integer value) {
            addCriterion("USES >=", value, "uses");
            return (Criteria) this;
        }

        public Criteria andUsesLessThan(Integer value) {
            addCriterion("USES <", value, "uses");
            return (Criteria) this;
        }

        public Criteria andUsesLessThanOrEqualTo(Integer value) {
            addCriterion("USES <=", value, "uses");
            return (Criteria) this;
        }

        public Criteria andUsesIn(List<Integer> values) {
            addCriterion("USES in", values, "uses");
            return (Criteria) this;
        }

        public Criteria andUsesNotIn(List<Integer> values) {
            addCriterion("USES not in", values, "uses");
            return (Criteria) this;
        }

        public Criteria andUsesBetween(Integer value1, Integer value2) {
            addCriterion("USES between", value1, value2, "uses");
            return (Criteria) this;
        }

        public Criteria andUsesNotBetween(Integer value1, Integer value2) {
            addCriterion("USES not between", value1, value2, "uses");
            return (Criteria) this;
        }

        public Criteria andUsedIsNull() {
            addCriterion("USED is null");
            return (Criteria) this;
        }

        public Criteria andUsedIsNotNull() {
            addCriterion("USED is not null");
            return (Criteria) this;
        }

        public Criteria andUsedEqualTo(Boolean value) {
            addUsedCriterion("USED =", value, "used");
            return (Criteria) this;
        }

        public Criteria andUsedNotEqualTo(Boolean value) {
            addUsedCriterion("USED <>", value, "used");
            return (Criteria) this;
        }

        public Criteria andUsedGreaterThan(Boolean value) {
            addUsedCriterion("USED >", value, "used");
            return (Criteria) this;
        }

        public Criteria andUsedGreaterThanOrEqualTo(Boolean value) {
            addUsedCriterion("USED >=", value, "used");
            return (Criteria) this;
        }

        public Criteria andUsedLessThan(Boolean value) {
            addUsedCriterion("USED <", value, "used");
            return (Criteria) this;
        }

        public Criteria andUsedLessThanOrEqualTo(Boolean value) {
            addUsedCriterion("USED <=", value, "used");
            return (Criteria) this;
        }

        public Criteria andUsedIn(List<Boolean> values) {
            addUsedCriterion("USED in", values, "used");
            return (Criteria) this;
        }

        public Criteria andUsedNotIn(List<Boolean> values) {
            addUsedCriterion("USED not in", values, "used");
            return (Criteria) this;
        }

        public Criteria andUsedBetween(Boolean value1, Boolean value2) {
            addUsedCriterion("USED between", value1, value2, "used");
            return (Criteria) this;
        }

        public Criteria andUsedNotBetween(Boolean value1, Boolean value2) {
            addUsedCriterion("USED not between", value1, value2, "used");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountIsNull() {
            addCriterion("TOTAL_DISCOUNT is null");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountIsNotNull() {
            addCriterion("TOTAL_DISCOUNT is not null");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountEqualTo(BigDecimal value) {
            addCriterion("TOTAL_DISCOUNT =", value, "totalDiscount");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountNotEqualTo(BigDecimal value) {
            addCriterion("TOTAL_DISCOUNT <>", value, "totalDiscount");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountGreaterThan(BigDecimal value) {
            addCriterion("TOTAL_DISCOUNT >", value, "totalDiscount");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("TOTAL_DISCOUNT >=", value, "totalDiscount");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountLessThan(BigDecimal value) {
            addCriterion("TOTAL_DISCOUNT <", value, "totalDiscount");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("TOTAL_DISCOUNT <=", value, "totalDiscount");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountIn(List<BigDecimal> values) {
            addCriterion("TOTAL_DISCOUNT in", values, "totalDiscount");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountNotIn(List<BigDecimal> values) {
            addCriterion("TOTAL_DISCOUNT not in", values, "totalDiscount");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("TOTAL_DISCOUNT between", value1, value2, "totalDiscount");
            return (Criteria) this;
        }

        public Criteria andTotalDiscountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("TOTAL_DISCOUNT not between", value1, value2, "totalDiscount");
            return (Criteria) this;
        }

        public Criteria andFirstUseIsNull() {
            addCriterion("FIRST_USE is null");
            return (Criteria) this;
        }

        public Criteria andFirstUseIsNotNull() {
            addCriterion("FIRST_USE is not null");
            return (Criteria) this;
        }

        public Criteria andFirstUseEqualTo(Date value) {
            addCriterion("FIRST_USE =", value, "firstUse");
            return (Criteria) this;
        }

        public Criteria andFirstUseNotEqualTo(Date value) {
            addCriterion("FIRST_USE <>", value, "firstUse");
            return (Criteria) this;
        }

        public Criteria andFirstUseGreaterThan(Date value) {
            addCriterion("FIRST_USE >", value, "firstUse");
            return (Criteria) this;
        }

        public Criteria andFirstUseGreaterThanOrEqualTo(Date value) {
            addCriterion("FIRST_USE >=", value, "firstUse");
            return (Criteria) this;
        }

        public Criteria andFirstUseLessThan(Date value) {
            addCriterion("FIRST_USE <", value, "firstUse");
            return (Criteria) this;
        }

        public Criteria andFirstUseLessThanOrEqualTo(Date value) {
            addCriterion("FIRST_USE <=", value, "firstUse");
            return (Criteria) this;
        }

        public Criteria andFirstUseIn(List<Date> values) {
            addCriterion("FIRST_USE in", values, "firstUse");
            return (Criteria) this;
        }

        public Criteria andFirstUseNotIn(List<Date> values) {
            addCriterion("FIRST_USE not in", values, "firstUse");
            return (Criteria) this;
        }

        public Criteria andFirstUseBetween(Date value1, Date value2) {
            addCriterion("FIRST_USE between", value1, value2, "firstUse");
            return (Criteria) this;
        }

        public Criteria andFirstUseNotBetween(Date value1, Date value2) {
            addCriterion("FIRST_USE not between", value1, value2, "firstUse");
            return (Criteria) this;
        }

        public Criteria andLastUseIsNull() {
            addCriterion("LAST_USE is null");
            return (Criteria) this;
        }

        public Criteria andLastUseIsNotNull() {
            addCriterion("LAST_USE is not null");
            return (Criteria) this;
        }

        public Criteria andLastUseEqualTo(Date value) {
            addCriterion("LAST_USE =", value, "lastUse");
            return (Criteria) this;
        }

        public Criteria andLastUseNotEqualTo(Date value) {
            addCriterion("LAST_USE <>", value, "lastUse");
            return (Criteria) this;
        }

        public Criteria andLastUseGreaterThan(Date value) {
            addCriterion("LAST_USE >", value, "lastUse");
            return (Criteria) this;
        }

        public Criteria andLastUseGreaterThanOrEqualTo(Date value) {
            addCriterion("LAST_USE >=", value, "lastUse");
            return (Criteria) this;
        }

        public Criteria andLastUseLessThan(Date value) {
            addCriterion("LAST_USE <", value, "lastUse");
            return (Criteria) this;
        }

        public Criteria andLastUseLessThanOrEqualTo(Date value) {
            addCriterion("LAST_USE <=", value, "lastUse");
            return (Criteria) this;
        }

        public Criteria andLastUseIn(List<Date> values) {
            addCriterion("LAST_USE in", values, "lastUse");
            return (Criteria) this;
        }

        public Criteria andLastUseNotIn(List<Date> values) {
            addCriterion("LAST_USE not in", values, "lastUse");
            return (Criteria) this;
        }

        public Criteria andLastUseBetween(Date value1, Date value2) {
            addCriterion("LAST_USE between", value1, value2, "lastUse");
            return (Criteria) this;
        }

        public Criteria andLastUseNotBetween(Date value1, Date value2) {
            addCriterion("LAST_USE not between", value1, value2, "lastUse");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdIsNull() {
            addCriterion("LAST_TERMINAL_ID is null");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdIsNotNull() {
            addCriterion("LAST_TERMINAL_ID is not null");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdEqualTo(String value) {
            addCriterion("LAST_TERMINAL_ID =", value, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdNotEqualTo(String value) {
            addCriterion("LAST_TERMINAL_ID <>", value, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdGreaterThan(String value) {
            addCriterion("LAST_TERMINAL_ID >", value, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdGreaterThanOrEqualTo(String value) {
            addCriterion("LAST_TERMINAL_ID >=", value, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdLessThan(String value) {
            addCriterion("LAST_TERMINAL_ID <", value, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdLessThanOrEqualTo(String value) {
            addCriterion("LAST_TERMINAL_ID <=", value, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdLike(String value) {
            addCriterion("LAST_TERMINAL_ID like", value, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdNotLike(String value) {
            addCriterion("LAST_TERMINAL_ID not like", value, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdIn(List<String> values) {
            addCriterion("LAST_TERMINAL_ID in", values, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdNotIn(List<String> values) {
            addCriterion("LAST_TERMINAL_ID not in", values, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdBetween(String value1, String value2) {
            addCriterion("LAST_TERMINAL_ID between", value1, value2, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdNotBetween(String value1, String value2) {
            addCriterion("LAST_TERMINAL_ID not between", value1, value2, "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdIsNull() {
            addCriterion("LOCK_BY_TERMINAL_ID is null");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdIsNotNull() {
            addCriterion("LOCK_BY_TERMINAL_ID is not null");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdEqualTo(String value) {
            addCriterion("LOCK_BY_TERMINAL_ID =", value, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdNotEqualTo(String value) {
            addCriterion("LOCK_BY_TERMINAL_ID <>", value, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdGreaterThan(String value) {
            addCriterion("LOCK_BY_TERMINAL_ID >", value, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdGreaterThanOrEqualTo(String value) {
            addCriterion("LOCK_BY_TERMINAL_ID >=", value, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdLessThan(String value) {
            addCriterion("LOCK_BY_TERMINAL_ID <", value, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdLessThanOrEqualTo(String value) {
            addCriterion("LOCK_BY_TERMINAL_ID <=", value, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdLike(String value) {
            addCriterion("LOCK_BY_TERMINAL_ID like", value, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdNotLike(String value) {
            addCriterion("LOCK_BY_TERMINAL_ID not like", value, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdIn(List<String> values) {
            addCriterion("LOCK_BY_TERMINAL_ID in", values, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdNotIn(List<String> values) {
            addCriterion("LOCK_BY_TERMINAL_ID not in", values, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdBetween(String value1, String value2) {
            addCriterion("LOCK_BY_TERMINAL_ID between", value1, value2, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdNotBetween(String value1, String value2) {
            addCriterion("LOCK_BY_TERMINAL_ID not between", value1, value2, "lockByTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockDateIsNull() {
            addCriterion("LOCK_DATE is null");
            return (Criteria) this;
        }

        public Criteria andLockDateIsNotNull() {
            addCriterion("LOCK_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andLockDateEqualTo(Date value) {
            addCriterion("LOCK_DATE =", value, "lockDate");
            return (Criteria) this;
        }

        public Criteria andLockDateNotEqualTo(Date value) {
            addCriterion("LOCK_DATE <>", value, "lockDate");
            return (Criteria) this;
        }

        public Criteria andLockDateGreaterThan(Date value) {
            addCriterion("LOCK_DATE >", value, "lockDate");
            return (Criteria) this;
        }

        public Criteria andLockDateGreaterThanOrEqualTo(Date value) {
            addCriterion("LOCK_DATE >=", value, "lockDate");
            return (Criteria) this;
        }

        public Criteria andLockDateLessThan(Date value) {
            addCriterion("LOCK_DATE <", value, "lockDate");
            return (Criteria) this;
        }

        public Criteria andLockDateLessThanOrEqualTo(Date value) {
            addCriterion("LOCK_DATE <=", value, "lockDate");
            return (Criteria) this;
        }

        public Criteria andLockDateIn(List<Date> values) {
            addCriterion("LOCK_DATE in", values, "lockDate");
            return (Criteria) this;
        }

        public Criteria andLockDateNotIn(List<Date> values) {
            addCriterion("LOCK_DATE not in", values, "lockDate");
            return (Criteria) this;
        }

        public Criteria andLockDateBetween(Date value1, Date value2) {
            addCriterion("LOCK_DATE between", value1, value2, "lockDate");
            return (Criteria) this;
        }

        public Criteria andLockDateNotBetween(Date value1, Date value2) {
            addCriterion("LOCK_DATE not between", value1, value2, "lockDate");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andClassIdLikeInsensitive(String value) {
            addCriterion("upper(CLASS_ID) like", value.toUpperCase(), "classId");
            return (Criteria) this;
        }

        public Criteria andObjectIdLikeInsensitive(String value) {
            addCriterion("upper(OBJECT_ID) like", value.toUpperCase(), "objectId");
            return (Criteria) this;
        }

        public Criteria andLastTerminalIdLikeInsensitive(String value) {
            addCriterion("upper(LAST_TERMINAL_ID) like", value.toUpperCase(), "lastTerminalId");
            return (Criteria) this;
        }

        public Criteria andLockByTerminalIdLikeInsensitive(String value) {
            addCriterion("upper(LOCK_BY_TERMINAL_ID) like", value.toUpperCase(), "lockByTerminalId");
            return (Criteria) this;
        }
        
        public Criteria andTotalSaleIsNull() {
           addCriterion("TOTAL_SALE is null");
           return (Criteria) this;
       }

       public Criteria andTotalSaleIsNotNull() {
           addCriterion("TOTAL_SALE is not null");
           return (Criteria) this;
       }

       public Criteria andTotalSaleEqualTo(BigDecimal value) {
           addCriterion("TOTAL_SALE =", value, "totalSale");
           return (Criteria) this;
       }

       public Criteria andTotalSaleNotEqualTo(BigDecimal value) {
           addCriterion("TOTAL_SALE <>", value, "totalSale");
           return (Criteria) this;
       }

       public Criteria andTotalSaleGreaterThan(BigDecimal value) {
           addCriterion("TOTAL_SALE >", value, "totalSale");
           return (Criteria) this;
       }

       public Criteria andTotalSaleGreaterThanOrEqualTo(BigDecimal value) {
           addCriterion("TOTAL_SALE >=", value, "totalSale");
           return (Criteria) this;
       }

       public Criteria andTotalSaleLessThan(BigDecimal value) {
           addCriterion("TOTAL_SALE <", value, "totalSale");
           return (Criteria) this;
       }

       public Criteria andTotalSaleLessThanOrEqualTo(BigDecimal value) {
           addCriterion("TOTAL_SALE <=", value, "totalSale");
           return (Criteria) this;
       }

       public Criteria andTotalSaleIn(List<BigDecimal> values) {
           addCriterion("TOTAL_SALE in", values, "totalSale");
           return (Criteria) this;
       }

       public Criteria andTotalSaleNotIn(List<BigDecimal> values) {
           addCriterion("TOTAL_SALE not in", values, "totalSale");
           return (Criteria) this;
       }

       public Criteria andTotalSaleBetween(BigDecimal value1, BigDecimal value2) {
           addCriterion("TOTAL_SALE between", value1, value2, "totalSale");
           return (Criteria) this;
       }

       public Criteria andTotalSaleNotBetween(BigDecimal value1, BigDecimal value2) {
           addCriterion("TOTAL_SALE not between", value1, value2, "totalSale");
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