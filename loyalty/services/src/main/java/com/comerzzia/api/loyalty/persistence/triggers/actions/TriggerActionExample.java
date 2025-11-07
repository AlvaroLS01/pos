package com.comerzzia.api.loyalty.persistence.triggers.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiActivity;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@SuppressWarnings("serial")
public class TriggerActionExample extends MultiActivity {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;
    
    public TriggerActionExample(IDatosSesion datosSesion) {
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
        protected List<Criterion> manualExecutionCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
            manualExecutionCriteria = new ArrayList<>();
        }

        public List<Criterion> getManualExecutionCriteria() {
            return manualExecutionCriteria;
        }

        protected void addManualExecutionCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            manualExecutionCriteria.add(new Criterion(condition, value, "com.comerzzia.core.util.mybatis.typehandlers.BooleanNumberTypeHandler"));
            allCriteria = null;
        }

        protected void addManualExecutionCriterion(String condition, Boolean value1, Boolean value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            manualExecutionCriteria.add(new Criterion(condition, value1, value2, "com.comerzzia.core.util.mybatis.typehandlers.BooleanNumberTypeHandler"));
            allCriteria = null;
        }

        public boolean isValid() {
            return criteria.size() > 0
                || manualExecutionCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(manualExecutionCriteria);
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

        public Criteria andActionTypeIsNull() {
            addCriterion("ACTION_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andActionTypeIsNotNull() {
            addCriterion("ACTION_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andActionTypeEqualTo(Short value) {
            addCriterion("ACTION_TYPE =", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeNotEqualTo(Short value) {
            addCriterion("ACTION_TYPE <>", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeGreaterThan(Short value) {
            addCriterion("ACTION_TYPE >", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeGreaterThanOrEqualTo(Short value) {
            addCriterion("ACTION_TYPE >=", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeLessThan(Short value) {
            addCriterion("ACTION_TYPE <", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeLessThanOrEqualTo(Short value) {
            addCriterion("ACTION_TYPE <=", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeIn(List<Short> values) {
            addCriterion("ACTION_TYPE in", values, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeNotIn(List<Short> values) {
            addCriterion("ACTION_TYPE not in", values, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeBetween(Short value1, Short value2) {
            addCriterion("ACTION_TYPE between", value1, value2, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeNotBetween(Short value1, Short value2) {
            addCriterion("ACTION_TYPE not between", value1, value2, "actionType");
            return (Criteria) this;
        }

        public Criteria andManualExecutionIsNull() {
            addCriterion("MANUAL_EXECUTION is null");
            return (Criteria) this;
        }

        public Criteria andManualExecutionIsNotNull() {
            addCriterion("MANUAL_EXECUTION is not null");
            return (Criteria) this;
        }

        public Criteria andManualExecutionEqualTo(Boolean value) {
            addManualExecutionCriterion("MANUAL_EXECUTION =", value, "manualExecution");
            return (Criteria) this;
        }

        public Criteria andManualExecutionNotEqualTo(Boolean value) {
            addManualExecutionCriterion("MANUAL_EXECUTION <>", value, "manualExecution");
            return (Criteria) this;
        }

        public Criteria andManualExecutionGreaterThan(Boolean value) {
            addManualExecutionCriterion("MANUAL_EXECUTION >", value, "manualExecution");
            return (Criteria) this;
        }

        public Criteria andManualExecutionGreaterThanOrEqualTo(Boolean value) {
            addManualExecutionCriterion("MANUAL_EXECUTION >=", value, "manualExecution");
            return (Criteria) this;
        }

        public Criteria andManualExecutionLessThan(Boolean value) {
            addManualExecutionCriterion("MANUAL_EXECUTION <", value, "manualExecution");
            return (Criteria) this;
        }

        public Criteria andManualExecutionLessThanOrEqualTo(Boolean value) {
            addManualExecutionCriterion("MANUAL_EXECUTION <=", value, "manualExecution");
            return (Criteria) this;
        }

        public Criteria andManualExecutionIn(List<Boolean> values) {
            addManualExecutionCriterion("MANUAL_EXECUTION in", values, "manualExecution");
            return (Criteria) this;
        }

        public Criteria andManualExecutionNotIn(List<Boolean> values) {
            addManualExecutionCriterion("MANUAL_EXECUTION not in", values, "manualExecution");
            return (Criteria) this;
        }

        public Criteria andManualExecutionBetween(Boolean value1, Boolean value2) {
            addManualExecutionCriterion("MANUAL_EXECUTION between", value1, value2, "manualExecution");
            return (Criteria) this;
        }

        public Criteria andManualExecutionNotBetween(Boolean value1, Boolean value2) {
            addManualExecutionCriterion("MANUAL_EXECUTION not between", value1, value2, "manualExecution");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateIsNull() {
            addCriterion("NEXT_EXECUTION_DATE is null");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateIsNotNull() {
            addCriterion("NEXT_EXECUTION_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateEqualTo(Date value) {
            addCriterion("NEXT_EXECUTION_DATE =", value, "nextExecutionDate");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateNotEqualTo(Date value) {
            addCriterion("NEXT_EXECUTION_DATE <>", value, "nextExecutionDate");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateGreaterThan(Date value) {
            addCriterion("NEXT_EXECUTION_DATE >", value, "nextExecutionDate");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateGreaterThanOrEqualTo(Date value) {
            addCriterion("NEXT_EXECUTION_DATE >=", value, "nextExecutionDate");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateLessThan(Date value) {
            addCriterion("NEXT_EXECUTION_DATE <", value, "nextExecutionDate");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateLessThanOrEqualTo(Date value) {
            addCriterion("NEXT_EXECUTION_DATE <=", value, "nextExecutionDate");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateIn(List<Date> values) {
            addCriterion("NEXT_EXECUTION_DATE in", values, "nextExecutionDate");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateNotIn(List<Date> values) {
            addCriterion("NEXT_EXECUTION_DATE not in", values, "nextExecutionDate");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateBetween(Date value1, Date value2) {
            addCriterion("NEXT_EXECUTION_DATE between", value1, value2, "nextExecutionDate");
            return (Criteria) this;
        }

        public Criteria andNextExecutionDateNotBetween(Date value1, Date value2) {
            addCriterion("NEXT_EXECUTION_DATE not between", value1, value2, "nextExecutionDate");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalIsNull() {
            addCriterion("TIME_INTERVAL is null");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalIsNotNull() {
            addCriterion("TIME_INTERVAL is not null");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalEqualTo(String value) {
            addCriterion("TIME_INTERVAL =", value, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalNotEqualTo(String value) {
            addCriterion("TIME_INTERVAL <>", value, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalGreaterThan(String value) {
            addCriterion("TIME_INTERVAL >", value, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalGreaterThanOrEqualTo(String value) {
            addCriterion("TIME_INTERVAL >=", value, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalLessThan(String value) {
            addCriterion("TIME_INTERVAL <", value, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalLessThanOrEqualTo(String value) {
            addCriterion("TIME_INTERVAL <=", value, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalLike(String value) {
            addCriterion("TIME_INTERVAL like", value, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalNotLike(String value) {
            addCriterion("TIME_INTERVAL not like", value, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalIn(List<String> values) {
            addCriterion("TIME_INTERVAL in", values, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalNotIn(List<String> values) {
            addCriterion("TIME_INTERVAL not in", values, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalBetween(String value1, String value2) {
            addCriterion("TIME_INTERVAL between", value1, value2, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalNotBetween(String value1, String value2) {
            addCriterion("TIME_INTERVAL not between", value1, value2, "timeInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalIsNull() {
            addCriterion("CRON_INTERVAL is null");
            return (Criteria) this;
        }

        public Criteria andCronIntervalIsNotNull() {
            addCriterion("CRON_INTERVAL is not null");
            return (Criteria) this;
        }

        public Criteria andCronIntervalEqualTo(String value) {
            addCriterion("CRON_INTERVAL =", value, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalNotEqualTo(String value) {
            addCriterion("CRON_INTERVAL <>", value, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalGreaterThan(String value) {
            addCriterion("CRON_INTERVAL >", value, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalGreaterThanOrEqualTo(String value) {
            addCriterion("CRON_INTERVAL >=", value, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalLessThan(String value) {
            addCriterion("CRON_INTERVAL <", value, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalLessThanOrEqualTo(String value) {
            addCriterion("CRON_INTERVAL <=", value, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalLike(String value) {
            addCriterion("CRON_INTERVAL like", value, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalNotLike(String value) {
            addCriterion("CRON_INTERVAL not like", value, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalIn(List<String> values) {
            addCriterion("CRON_INTERVAL in", values, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalNotIn(List<String> values) {
            addCriterion("CRON_INTERVAL not in", values, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalBetween(String value1, String value2) {
            addCriterion("CRON_INTERVAL between", value1, value2, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalNotBetween(String value1, String value2) {
            addCriterion("CRON_INTERVAL not between", value1, value2, "cronInterval");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidIsNull() {
            addCriterion("LAST_ACTION_EXEC_UID is null");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidIsNotNull() {
            addCriterion("LAST_ACTION_EXEC_UID is not null");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidEqualTo(String value) {
            addCriterion("LAST_ACTION_EXEC_UID =", value, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidNotEqualTo(String value) {
            addCriterion("LAST_ACTION_EXEC_UID <>", value, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidGreaterThan(String value) {
            addCriterion("LAST_ACTION_EXEC_UID >", value, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidGreaterThanOrEqualTo(String value) {
            addCriterion("LAST_ACTION_EXEC_UID >=", value, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidLessThan(String value) {
            addCriterion("LAST_ACTION_EXEC_UID <", value, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidLessThanOrEqualTo(String value) {
            addCriterion("LAST_ACTION_EXEC_UID <=", value, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidLike(String value) {
            addCriterion("LAST_ACTION_EXEC_UID like", value, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidNotLike(String value) {
            addCriterion("LAST_ACTION_EXEC_UID not like", value, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidIn(List<String> values) {
            addCriterion("LAST_ACTION_EXEC_UID in", values, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidNotIn(List<String> values) {
            addCriterion("LAST_ACTION_EXEC_UID not in", values, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidBetween(String value1, String value2) {
            addCriterion("LAST_ACTION_EXEC_UID between", value1, value2, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidNotBetween(String value1, String value2) {
            addCriterion("LAST_ACTION_EXEC_UID not between", value1, value2, "lastActionExecUid");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andActionUidLikeInsensitive(String value) {
            addCriterion("upper(ACTION_UID) like", value.toUpperCase(), "actionUid");
            return (Criteria) this;
        }

        public Criteria andTimeIntervalLikeInsensitive(String value) {
            addCriterion("upper(TIME_INTERVAL) like", value.toUpperCase(), "timeInterval");
            return (Criteria) this;
        }

        public Criteria andCronIntervalLikeInsensitive(String value) {
            addCriterion("upper(CRON_INTERVAL) like", value.toUpperCase(), "cronInterval");
            return (Criteria) this;
        }

        public Criteria andLastActionExecUidLikeInsensitive(String value) {
            addCriterion("upper(LAST_ACTION_EXEC_UID) like", value.toUpperCase(), "lastActionExecUid");
            return (Criteria) this;
        }
        
        public Criteria andTriggerUidEqualTo(String value) {
           addCriterion("TRIGGER_UID =", value, "triggerUid");
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