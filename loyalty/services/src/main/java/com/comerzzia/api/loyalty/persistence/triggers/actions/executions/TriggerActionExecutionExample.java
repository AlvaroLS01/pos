package com.comerzzia.api.loyalty.persistence.triggers.actions.executions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiActivity;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@SuppressWarnings("serial")
public class TriggerActionExecutionExample extends MultiActivity {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;
    
    public TriggerActionExecutionExample(IDatosSesion datosSesion) {
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

        public Criteria andAccExecutionUidIsNull() {
            addCriterion("ACC_EXECUTION_UID is null");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidIsNotNull() {
            addCriterion("ACC_EXECUTION_UID is not null");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidEqualTo(String value) {
            addCriterion("ACC_EXECUTION_UID =", value, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidNotEqualTo(String value) {
            addCriterion("ACC_EXECUTION_UID <>", value, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidGreaterThan(String value) {
            addCriterion("ACC_EXECUTION_UID >", value, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidGreaterThanOrEqualTo(String value) {
            addCriterion("ACC_EXECUTION_UID >=", value, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidLessThan(String value) {
            addCriterion("ACC_EXECUTION_UID <", value, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidLessThanOrEqualTo(String value) {
            addCriterion("ACC_EXECUTION_UID <=", value, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidLike(String value) {
            addCriterion("ACC_EXECUTION_UID like", value, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidNotLike(String value) {
            addCriterion("ACC_EXECUTION_UID not like", value, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidIn(List<String> values) {
            addCriterion("ACC_EXECUTION_UID in", values, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidNotIn(List<String> values) {
            addCriterion("ACC_EXECUTION_UID not in", values, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidBetween(String value1, String value2) {
            addCriterion("ACC_EXECUTION_UID between", value1, value2, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidNotBetween(String value1, String value2) {
            addCriterion("ACC_EXECUTION_UID not between", value1, value2, "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidIsNull() {
            addCriterion("TRIGGER_EXEC_UID is null");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidIsNotNull() {
            addCriterion("TRIGGER_EXEC_UID is not null");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidEqualTo(String value) {
            addCriterion("TRIGGER_EXEC_UID =", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidNotEqualTo(String value) {
            addCriterion("TRIGGER_EXEC_UID <>", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidGreaterThan(String value) {
            addCriterion("TRIGGER_EXEC_UID >", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidGreaterThanOrEqualTo(String value) {
            addCriterion("TRIGGER_EXEC_UID >=", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidLessThan(String value) {
            addCriterion("TRIGGER_EXEC_UID <", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidLessThanOrEqualTo(String value) {
            addCriterion("TRIGGER_EXEC_UID <=", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidLike(String value) {
            addCriterion("TRIGGER_EXEC_UID like", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidNotLike(String value) {
            addCriterion("TRIGGER_EXEC_UID not like", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidIn(List<String> values) {
            addCriterion("TRIGGER_EXEC_UID in", values, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidNotIn(List<String> values) {
            addCriterion("TRIGGER_EXEC_UID not in", values, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidBetween(String value1, String value2) {
            addCriterion("TRIGGER_EXEC_UID between", value1, value2, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidNotBetween(String value1, String value2) {
            addCriterion("TRIGGER_EXEC_UID not between", value1, value2, "triggerExecUid");
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

        public Criteria andStartDateIsNull() {
            addCriterion("START_DATE is null");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNotNull() {
            addCriterion("START_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andStartDateEqualTo(Date value) {
            addCriterion("START_DATE =", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotEqualTo(Date value) {
            addCriterion("START_DATE <>", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThan(Date value) {
            addCriterion("START_DATE >", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThanOrEqualTo(Date value) {
            addCriterion("START_DATE >=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThan(Date value) {
            addCriterion("START_DATE <", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThanOrEqualTo(Date value) {
            addCriterion("START_DATE <=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateIn(List<Date> values) {
            addCriterion("START_DATE in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotIn(List<Date> values) {
            addCriterion("START_DATE not in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateBetween(Date value1, Date value2) {
            addCriterion("START_DATE between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotBetween(Date value1, Date value2) {
            addCriterion("START_DATE not between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andEndDateIsNull() {
            addCriterion("END_DATE is null");
            return (Criteria) this;
        }

        public Criteria andEndDateIsNotNull() {
            addCriterion("END_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andEndDateEqualTo(Date value) {
            addCriterion("END_DATE =", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotEqualTo(Date value) {
            addCriterion("END_DATE <>", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateGreaterThan(Date value) {
            addCriterion("END_DATE >", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateGreaterThanOrEqualTo(Date value) {
            addCriterion("END_DATE >=", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateLessThan(Date value) {
            addCriterion("END_DATE <", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateLessThanOrEqualTo(Date value) {
            addCriterion("END_DATE <=", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateIn(List<Date> values) {
            addCriterion("END_DATE in", values, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotIn(List<Date> values) {
            addCriterion("END_DATE not in", values, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateBetween(Date value1, Date value2) {
            addCriterion("END_DATE between", value1, value2, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotBetween(Date value1, Date value2) {
            addCriterion("END_DATE not between", value1, value2, "endDate");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("USER_ID is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("USER_ID is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Long value) {
            addCriterion("USER_ID =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Long value) {
            addCriterion("USER_ID <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Long value) {
            addCriterion("USER_ID >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("USER_ID >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Long value) {
            addCriterion("USER_ID <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Long value) {
            addCriterion("USER_ID <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Long> values) {
            addCriterion("USER_ID in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Long> values) {
            addCriterion("USER_ID not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Long value1, Long value2) {
            addCriterion("USER_ID between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Long value1, Long value2) {
            addCriterion("USER_ID not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andStatusIdIsNull() {
            addCriterion("STATUS_ID is null");
            return (Criteria) this;
        }

        public Criteria andStatusIdIsNotNull() {
            addCriterion("STATUS_ID is not null");
            return (Criteria) this;
        }

        public Criteria andStatusIdEqualTo(Long value) {
            addCriterion("STATUS_ID =", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdNotEqualTo(Long value) {
            addCriterion("STATUS_ID <>", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdGreaterThan(Long value) {
            addCriterion("STATUS_ID >", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdGreaterThanOrEqualTo(Long value) {
            addCriterion("STATUS_ID >=", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdLessThan(Long value) {
            addCriterion("STATUS_ID <", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdLessThanOrEqualTo(Long value) {
            addCriterion("STATUS_ID <=", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdIn(List<Long> values) {
            addCriterion("STATUS_ID in", values, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdNotIn(List<Long> values) {
            addCriterion("STATUS_ID not in", values, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdBetween(Long value1, Long value2) {
            addCriterion("STATUS_ID between", value1, value2, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdNotBetween(Long value1, Long value2) {
            addCriterion("STATUS_ID not between", value1, value2, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusTextIsNull() {
            addCriterion("STATUS_TEXT is null");
            return (Criteria) this;
        }

        public Criteria andStatusTextIsNotNull() {
            addCriterion("STATUS_TEXT is not null");
            return (Criteria) this;
        }

        public Criteria andStatusTextEqualTo(String value) {
            addCriterion("STATUS_TEXT =", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextNotEqualTo(String value) {
            addCriterion("STATUS_TEXT <>", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextGreaterThan(String value) {
            addCriterion("STATUS_TEXT >", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextGreaterThanOrEqualTo(String value) {
            addCriterion("STATUS_TEXT >=", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextLessThan(String value) {
            addCriterion("STATUS_TEXT <", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextLessThanOrEqualTo(String value) {
            addCriterion("STATUS_TEXT <=", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextLike(String value) {
            addCriterion("STATUS_TEXT like", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextNotLike(String value) {
            addCriterion("STATUS_TEXT not like", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextIn(List<String> values) {
            addCriterion("STATUS_TEXT in", values, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextNotIn(List<String> values) {
            addCriterion("STATUS_TEXT not in", values, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextBetween(String value1, String value2) {
            addCriterion("STATUS_TEXT between", value1, value2, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextNotBetween(String value1, String value2) {
            addCriterion("STATUS_TEXT not between", value1, value2, "statusText");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andAccExecutionUidLikeInsensitive(String value) {
            addCriterion("upper(ACC_EXECUTION_UID) like", value.toUpperCase(), "accExecutionUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidLikeInsensitive(String value) {
            addCriterion("upper(TRIGGER_EXEC_UID) like", value.toUpperCase(), "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andActionUidLikeInsensitive(String value) {
            addCriterion("upper(ACTION_UID) like", value.toUpperCase(), "actionUid");
            return (Criteria) this;
        }

        public Criteria andStatusTextLikeInsensitive(String value) {
            addCriterion("upper(STATUS_TEXT) like", value.toUpperCase(), "statusText");
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