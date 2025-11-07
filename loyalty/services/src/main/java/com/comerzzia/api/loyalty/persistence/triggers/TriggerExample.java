package com.comerzzia.api.loyalty.persistence.triggers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiActivity;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@SuppressWarnings("serial")
public class TriggerExample extends MultiActivity {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TriggerExample(IDatosSesion datosSesion) {
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

        public Criteria andTriggerUidIsNull() {
            addCriterion("TRIGGER_UID is null");
            return (Criteria) this;
        }

        public Criteria andTriggerUidIsNotNull() {
            addCriterion("TRIGGER_UID is not null");
            return (Criteria) this;
        }

        public Criteria andTriggerUidEqualTo(String value) {
            addCriterion("TRIGGER_UID =", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidNotEqualTo(String value) {
            addCriterion("TRIGGER_UID <>", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidGreaterThan(String value) {
            addCriterion("TRIGGER_UID >", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidGreaterThanOrEqualTo(String value) {
            addCriterion("TRIGGER_UID >=", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidLessThan(String value) {
            addCriterion("TRIGGER_UID <", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidLessThanOrEqualTo(String value) {
            addCriterion("TRIGGER_UID <=", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidLike(String value) {
            addCriterion("TRIGGER_UID like", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidNotLike(String value) {
            addCriterion("TRIGGER_UID not like", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidIn(List<String> values) {
            addCriterion("TRIGGER_UID in", values, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidNotIn(List<String> values) {
            addCriterion("TRIGGER_UID not in", values, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidBetween(String value1, String value2) {
            addCriterion("TRIGGER_UID between", value1, value2, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidNotBetween(String value1, String value2) {
            addCriterion("TRIGGER_UID not between", value1, value2, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeIsNull() {
            addCriterion("TRIGGER_CODE is null");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeIsNotNull() {
            addCriterion("TRIGGER_CODE is not null");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeEqualTo(String value) {
            addCriterion("TRIGGER_CODE =", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeNotEqualTo(String value) {
            addCriterion("TRIGGER_CODE <>", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeGreaterThan(String value) {
            addCriterion("TRIGGER_CODE >", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeGreaterThanOrEqualTo(String value) {
            addCriterion("TRIGGER_CODE >=", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeLessThan(String value) {
            addCriterion("TRIGGER_CODE <", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeLessThanOrEqualTo(String value) {
            addCriterion("TRIGGER_CODE <=", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeLike(String value) {
            addCriterion("TRIGGER_CODE like", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeNotLike(String value) {
            addCriterion("TRIGGER_CODE not like", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeIn(List<String> values) {
            addCriterion("TRIGGER_CODE in", values, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeNotIn(List<String> values) {
            addCriterion("TRIGGER_CODE not in", values, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeBetween(String value1, String value2) {
            addCriterion("TRIGGER_CODE between", value1, value2, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeNotBetween(String value1, String value2) {
            addCriterion("TRIGGER_CODE not between", value1, value2, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionIsNull() {
            addCriterion("TRIGGER_DESCRIPTION is null");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionIsNotNull() {
            addCriterion("TRIGGER_DESCRIPTION is not null");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionEqualTo(String value) {
            addCriterion("TRIGGER_DESCRIPTION =", value, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionNotEqualTo(String value) {
            addCriterion("TRIGGER_DESCRIPTION <>", value, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionGreaterThan(String value) {
            addCriterion("TRIGGER_DESCRIPTION >", value, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("TRIGGER_DESCRIPTION >=", value, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionLessThan(String value) {
            addCriterion("TRIGGER_DESCRIPTION <", value, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionLessThanOrEqualTo(String value) {
            addCriterion("TRIGGER_DESCRIPTION <=", value, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionLike(String value) {
            addCriterion("TRIGGER_DESCRIPTION like", value, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionNotLike(String value) {
            addCriterion("TRIGGER_DESCRIPTION not like", value, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionIn(List<String> values) {
            addCriterion("TRIGGER_DESCRIPTION in", values, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionNotIn(List<String> values) {
            addCriterion("TRIGGER_DESCRIPTION not in", values, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionBetween(String value1, String value2) {
            addCriterion("TRIGGER_DESCRIPTION between", value1, value2, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionNotBetween(String value1, String value2) {
            addCriterion("TRIGGER_DESCRIPTION not between", value1, value2, "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdIsNull() {
            addCriterion("LAST_MOD_USER_ID is null");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdIsNotNull() {
            addCriterion("LAST_MOD_USER_ID is not null");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdEqualTo(Long value) {
            addCriterion("LAST_MOD_USER_ID =", value, "lastModUserId");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdNotEqualTo(Long value) {
            addCriterion("LAST_MOD_USER_ID <>", value, "lastModUserId");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdGreaterThan(Long value) {
            addCriterion("LAST_MOD_USER_ID >", value, "lastModUserId");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("LAST_MOD_USER_ID >=", value, "lastModUserId");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdLessThan(Long value) {
            addCriterion("LAST_MOD_USER_ID <", value, "lastModUserId");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdLessThanOrEqualTo(Long value) {
            addCriterion("LAST_MOD_USER_ID <=", value, "lastModUserId");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdIn(List<Long> values) {
            addCriterion("LAST_MOD_USER_ID in", values, "lastModUserId");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdNotIn(List<Long> values) {
            addCriterion("LAST_MOD_USER_ID not in", values, "lastModUserId");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdBetween(Long value1, Long value2) {
            addCriterion("LAST_MOD_USER_ID between", value1, value2, "lastModUserId");
            return (Criteria) this;
        }

        public Criteria andLastModUserIdNotBetween(Long value1, Long value2) {
            addCriterion("LAST_MOD_USER_ID not between", value1, value2, "lastModUserId");
            return (Criteria) this;
        }

        public Criteria andLastModDateIsNull() {
            addCriterion("LAST_MOD_DATE is null");
            return (Criteria) this;
        }

        public Criteria andLastModDateIsNotNull() {
            addCriterion("LAST_MOD_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andLastModDateEqualTo(Date value) {
            addCriterion("LAST_MOD_DATE =", value, "lastModDate");
            return (Criteria) this;
        }

        public Criteria andLastModDateNotEqualTo(Date value) {
            addCriterion("LAST_MOD_DATE <>", value, "lastModDate");
            return (Criteria) this;
        }

        public Criteria andLastModDateGreaterThan(Date value) {
            addCriterion("LAST_MOD_DATE >", value, "lastModDate");
            return (Criteria) this;
        }

        public Criteria andLastModDateGreaterThanOrEqualTo(Date value) {
            addCriterion("LAST_MOD_DATE >=", value, "lastModDate");
            return (Criteria) this;
        }

        public Criteria andLastModDateLessThan(Date value) {
            addCriterion("LAST_MOD_DATE <", value, "lastModDate");
            return (Criteria) this;
        }

        public Criteria andLastModDateLessThanOrEqualTo(Date value) {
            addCriterion("LAST_MOD_DATE <=", value, "lastModDate");
            return (Criteria) this;
        }

        public Criteria andLastModDateIn(List<Date> values) {
            addCriterion("LAST_MOD_DATE in", values, "lastModDate");
            return (Criteria) this;
        }

        public Criteria andLastModDateNotIn(List<Date> values) {
            addCriterion("LAST_MOD_DATE not in", values, "lastModDate");
            return (Criteria) this;
        }

        public Criteria andLastModDateBetween(Date value1, Date value2) {
            addCriterion("LAST_MOD_DATE between", value1, value2, "lastModDate");
            return (Criteria) this;
        }

        public Criteria andLastModDateNotBetween(Date value1, Date value2) {
            addCriterion("LAST_MOD_DATE not between", value1, value2, "lastModDate");
            return (Criteria) this;
        }

        public Criteria andActionUidIsNull() {
            addCriterion("ACTION_UID is null");
            return (Criteria) this;
        }

        public Criteria andActionUidIsNotNull() {
            addCriterion("ACTION_UID is not null");
            return (Criteria) this;
        }

        public Criteria andActionUidEqualTo(String value) {
            addCriterion("ACTION_UID =", value, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidNotEqualTo(String value) {
            addCriterion("ACTION_UID <>", value, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidGreaterThan(String value) {
            addCriterion("ACTION_UID >", value, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidGreaterThanOrEqualTo(String value) {
            addCriterion("ACTION_UID >=", value, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidLessThan(String value) {
            addCriterion("ACTION_UID <", value, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidLessThanOrEqualTo(String value) {
            addCriterion("ACTION_UID <=", value, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidLike(String value) {
            addCriterion("ACTION_UID like", value, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidNotLike(String value) {
            addCriterion("ACTION_UID not like", value, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidIn(List<String> values) {
            addCriterion("ACTION_UID in", values, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidNotIn(List<String> values) {
            addCriterion("ACTION_UID not in", values, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidBetween(String value1, String value2) {
            addCriterion("ACTION_UID between", value1, value2, "actionUid");
            return (Criteria) this;
        }

        public Criteria andActionUidNotBetween(String value1, String value2) {
            addCriterion("ACTION_UID not between", value1, value2, "actionUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidIsNull() {
            addCriterion("LAST_TRIGGER_EXEC_UID is null");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidIsNotNull() {
            addCriterion("LAST_TRIGGER_EXEC_UID is not null");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidEqualTo(String value) {
            addCriterion("LAST_TRIGGER_EXEC_UID =", value, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidNotEqualTo(String value) {
            addCriterion("LAST_TRIGGER_EXEC_UID <>", value, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidGreaterThan(String value) {
            addCriterion("LAST_TRIGGER_EXEC_UID >", value, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidGreaterThanOrEqualTo(String value) {
            addCriterion("LAST_TRIGGER_EXEC_UID >=", value, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidLessThan(String value) {
            addCriterion("LAST_TRIGGER_EXEC_UID <", value, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidLessThanOrEqualTo(String value) {
            addCriterion("LAST_TRIGGER_EXEC_UID <=", value, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidLike(String value) {
            addCriterion("LAST_TRIGGER_EXEC_UID like", value, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidNotLike(String value) {
            addCriterion("LAST_TRIGGER_EXEC_UID not like", value, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidIn(List<String> values) {
            addCriterion("LAST_TRIGGER_EXEC_UID in", values, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidNotIn(List<String> values) {
            addCriterion("LAST_TRIGGER_EXEC_UID not in", values, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidBetween(String value1, String value2) {
            addCriterion("LAST_TRIGGER_EXEC_UID between", value1, value2, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidNotBetween(String value1, String value2) {
            addCriterion("LAST_TRIGGER_EXEC_UID not between", value1, value2, "lastTriggerExecUid");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andTriggerUidLikeInsensitive(String value) {
            addCriterion("upper(TRIGGER_UID) like", value.toUpperCase(), "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeLikeInsensitive(String value) {
            addCriterion("upper(TRIGGER_CODE) like", value.toUpperCase(), "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerDescriptionLikeInsensitive(String value) {
            addCriterion("upper(TRIGGER_DESCRIPTION) like", value.toUpperCase(), "triggerDescription");
            return (Criteria) this;
        }

        public Criteria andActionUidLikeInsensitive(String value) {
            addCriterion("upper(ACTION_UID) like", value.toUpperCase(), "actionUid");
            return (Criteria) this;
        }

        public Criteria andLastTriggerExecUidLikeInsensitive(String value) {
            addCriterion("upper(LAST_TRIGGER_EXEC_UID) like", value.toUpperCase(), "lastTriggerExecUid");
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