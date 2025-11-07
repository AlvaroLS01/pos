package com.comerzzia.api.loyalty.persistence.triggers.executions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.persistence.MultiActivity;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@SuppressWarnings("serial")
public class TriggerExecutionExample extends MultiActivity {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;
    
    public TriggerExecutionExample(IDatosSesion datosSesion) {
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
            addCriterion("TEXEC.UID_ACTIVIDAD is null");
            return (Criteria) this;
        }

        public Criteria andUidActividadIsNotNull() {
            addCriterion("TEXEC.UID_ACTIVIDAD is not null");
            return (Criteria) this;
        }

        public Criteria andUidActividadEqualTo(String value) {
            addCriterion("TEXEC.UID_ACTIVIDAD =", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadNotEqualTo(String value) {
            addCriterion("TEXEC.UID_ACTIVIDAD <>", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadGreaterThan(String value) {
            addCriterion("TEXEC.UID_ACTIVIDAD >", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadGreaterThanOrEqualTo(String value) {
            addCriterion("TEXEC.UID_ACTIVIDAD >=", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadLessThan(String value) {
            addCriterion("TEXEC.UID_ACTIVIDAD <", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadLessThanOrEqualTo(String value) {
            addCriterion("TEXEC.UID_ACTIVIDAD <=", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadLike(String value) {
            addCriterion("TEXEC.UID_ACTIVIDAD like", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadNotLike(String value) {
            addCriterion("TEXEC.UID_ACTIVIDAD not like", value, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadIn(List<String> values) {
            addCriterion("TEXEC.UID_ACTIVIDAD in", values, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadNotIn(List<String> values) {
            addCriterion("TEXEC.UID_ACTIVIDAD not in", values, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadBetween(String value1, String value2) {
            addCriterion("TEXEC.UID_ACTIVIDAD between", value1, value2, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andUidActividadNotBetween(String value1, String value2) {
            addCriterion("TEXEC.UID_ACTIVIDAD not between", value1, value2, "uidActividad");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidIsNull() {
            addCriterion("TEXEC.TRIGGER_EXEC_UID is null");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidIsNotNull() {
            addCriterion("TEXEC.TRIGGER_EXEC_UID is not null");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidEqualTo(String value) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID =", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidNotEqualTo(String value) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID <>", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidGreaterThan(String value) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID >", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidGreaterThanOrEqualTo(String value) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID >=", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidLessThan(String value) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID <", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidLessThanOrEqualTo(String value) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID <=", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidLike(String value) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID like", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidNotLike(String value) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID not like", value, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidIn(List<String> values) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID in", values, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidNotIn(List<String> values) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID not in", values, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidBetween(String value1, String value2) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID between", value1, value2, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidNotBetween(String value1, String value2) {
            addCriterion("TEXEC.TRIGGER_EXEC_UID not between", value1, value2, "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidIsNull() {
            addCriterion("TEXEC.TRIGGER_UID is null");
            return (Criteria) this;
        }

        public Criteria andTriggerUidIsNotNull() {
            addCriterion("TEXEC.TRIGGER_UID is not null");
            return (Criteria) this;
        }

        public Criteria andTriggerUidEqualTo(String value) {
            addCriterion("TEXEC.TRIGGER_UID =", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidNotEqualTo(String value) {
            addCriterion("TEXEC.TRIGGER_UID <>", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidGreaterThan(String value) {
            addCriterion("TEXEC.TRIGGER_UID >", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidGreaterThanOrEqualTo(String value) {
            addCriterion("TEXEC.TRIGGER_UID >=", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidLessThan(String value) {
            addCriterion("TEXEC.TRIGGER_UID <", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidLessThanOrEqualTo(String value) {
            addCriterion("TEXEC.TRIGGER_UID <=", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidLike(String value) {
            addCriterion("TEXEC.TRIGGER_UID like", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidNotLike(String value) {
            addCriterion("TEXEC.TRIGGER_UID not like", value, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidIn(List<String> values) {
            addCriterion("TEXEC.TRIGGER_UID in", values, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidNotIn(List<String> values) {
            addCriterion("TEXEC.TRIGGER_UID not in", values, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidBetween(String value1, String value2) {
            addCriterion("TEXEC.TRIGGER_UID between", value1, value2, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidNotBetween(String value1, String value2) {
            addCriterion("TEXEC.TRIGGER_UID not between", value1, value2, "triggerUid");
            return (Criteria) this;
        }

        public Criteria andExecutionIdIsNull() {
            addCriterion("TEXEC.EXECUTION_ID is null");
            return (Criteria) this;
        }

        public Criteria andExecutionIdIsNotNull() {
            addCriterion("TEXEC.EXECUTION_ID is not null");
            return (Criteria) this;
        }

        public Criteria andExecutionIdEqualTo(Long value) {
            addCriterion("TEXEC.EXECUTION_ID =", value, "executionId");
            return (Criteria) this;
        }

        public Criteria andExecutionIdNotEqualTo(Long value) {
            addCriterion("TEXEC.EXECUTION_ID <>", value, "executionId");
            return (Criteria) this;
        }

        public Criteria andExecutionIdGreaterThan(Long value) {
            addCriterion("TEXEC.EXECUTION_ID >", value, "executionId");
            return (Criteria) this;
        }

        public Criteria andExecutionIdGreaterThanOrEqualTo(Long value) {
            addCriterion("TEXEC.EXECUTION_ID >=", value, "executionId");
            return (Criteria) this;
        }

        public Criteria andExecutionIdLessThan(Long value) {
            addCriterion("TEXEC.EXECUTION_ID <", value, "executionId");
            return (Criteria) this;
        }

        public Criteria andExecutionIdLessThanOrEqualTo(Long value) {
            addCriterion("TEXEC.EXECUTION_ID <=", value, "executionId");
            return (Criteria) this;
        }

        public Criteria andExecutionIdIn(List<Long> values) {
            addCriterion("TEXEC.EXECUTION_ID in", values, "executionId");
            return (Criteria) this;
        }

        public Criteria andExecutionIdNotIn(List<Long> values) {
            addCriterion("TEXEC.EXECUTION_ID not in", values, "executionId");
            return (Criteria) this;
        }

        public Criteria andExecutionIdBetween(Long value1, Long value2) {
            addCriterion("TEXEC.EXECUTION_ID between", value1, value2, "executionId");
            return (Criteria) this;
        }

        public Criteria andExecutionIdNotBetween(Long value1, Long value2) {
            addCriterion("TEXEC.EXECUTION_ID not between", value1, value2, "executionId");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNull() {
            addCriterion("TEXEC.START_DATE is null");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNotNull() {
            addCriterion("TEXEC.START_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andStartDateEqualTo(Date value) {
            addCriterion("TEXEC.START_DATE =", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotEqualTo(Date value) {
            addCriterion("TEXEC.START_DATE <>", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThan(Date value) {
            addCriterion("TEXEC.START_DATE >", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThanOrEqualTo(Date value) {
            addCriterion("TEXEC.START_DATE >=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThan(Date value) {
            addCriterion("TEXEC.START_DATE <", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThanOrEqualTo(Date value) {
            addCriterion("TEXEC.START_DATE <=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateIn(List<Date> values) {
            addCriterion("TEXEC.START_DATE in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotIn(List<Date> values) {
            addCriterion("TEXEC.START_DATE not in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateBetween(Date value1, Date value2) {
            addCriterion("TEXEC.START_DATE between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotBetween(Date value1, Date value2) {
            addCriterion("TEXEC.START_DATE not between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andEndDateIsNull() {
            addCriterion("TEXEC.END_DATE is null");
            return (Criteria) this;
        }

        public Criteria andEndDateIsNotNull() {
            addCriterion("TEXEC.END_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andEndDateEqualTo(Date value) {
            addCriterion("TEXEC.END_DATE =", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotEqualTo(Date value) {
            addCriterion("TEXEC.END_DATE <>", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateGreaterThan(Date value) {
            addCriterion("TEXEC.END_DATE >", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateGreaterThanOrEqualTo(Date value) {
            addCriterion("TEXEC.END_DATE >=", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateLessThan(Date value) {
            addCriterion("TEXEC.END_DATE <", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateLessThanOrEqualTo(Date value) {
            addCriterion("TEXEC.END_DATE <=", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateIn(List<Date> values) {
            addCriterion("TEXEC.END_DATE in", values, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotIn(List<Date> values) {
            addCriterion("TEXEC.END_DATE not in", values, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateBetween(Date value1, Date value2) {
            addCriterion("TEXEC.END_DATE between", value1, value2, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotBetween(Date value1, Date value2) {
            addCriterion("TEXEC.END_DATE not between", value1, value2, "endDate");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("TEXEC.USER_ID is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("TEXEC.USER_ID is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Long value) {
            addCriterion("TEXEC.USER_ID =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Long value) {
            addCriterion("TEXEC.USER_ID <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Long value) {
            addCriterion("TEXEC.USER_ID >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("TEXEC.USER_ID >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Long value) {
            addCriterion("TEXEC.USER_ID <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Long value) {
            addCriterion("TEXEC.USER_ID <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Long> values) {
            addCriterion("TEXEC.USER_ID in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Long> values) {
            addCriterion("TEXEC.USER_ID not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Long value1, Long value2) {
            addCriterion("TEXEC.USER_ID between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Long value1, Long value2) {
            addCriterion("TEXEC.USER_ID not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andStatusIdIsNull() {
            addCriterion("TEXEC.STATUS_ID is null");
            return (Criteria) this;
        }

        public Criteria andStatusIdIsNotNull() {
            addCriterion("TEXEC.STATUS_ID is not null");
            return (Criteria) this;
        }

        public Criteria andStatusIdEqualTo(Long value) {
            addCriterion("TEXEC.STATUS_ID =", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdNotEqualTo(Long value) {
            addCriterion("TEXEC.STATUS_ID <>", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdGreaterThan(Long value) {
            addCriterion("TEXEC.STATUS_ID >", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdGreaterThanOrEqualTo(Long value) {
            addCriterion("TEXEC.STATUS_ID >=", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdLessThan(Long value) {
            addCriterion("TEXEC.STATUS_ID <", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdLessThanOrEqualTo(Long value) {
            addCriterion("TEXEC.STATUS_ID <=", value, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdIn(List<Long> values) {
            addCriterion("TEXEC.STATUS_ID in", values, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdNotIn(List<Long> values) {
            addCriterion("TEXEC.STATUS_ID not in", values, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdBetween(Long value1, Long value2) {
            addCriterion("TEXEC.STATUS_ID between", value1, value2, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusIdNotBetween(Long value1, Long value2) {
            addCriterion("TEXEC.STATUS_ID not between", value1, value2, "statusId");
            return (Criteria) this;
        }

        public Criteria andStatusTextIsNull() {
            addCriterion("TEXEC.STATUS_TEXT is null");
            return (Criteria) this;
        }

        public Criteria andStatusTextIsNotNull() {
            addCriterion("TEXEC.STATUS_TEXT is not null");
            return (Criteria) this;
        }

        public Criteria andStatusTextEqualTo(String value) {
            addCriterion("TEXEC.STATUS_TEXT =", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextNotEqualTo(String value) {
            addCriterion("TEXEC.STATUS_TEXT <>", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextGreaterThan(String value) {
            addCriterion("TEXEC.STATUS_TEXT >", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextGreaterThanOrEqualTo(String value) {
            addCriterion("TEXEC.STATUS_TEXT >=", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextLessThan(String value) {
            addCriterion("TEXEC.STATUS_TEXT <", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextLessThanOrEqualTo(String value) {
            addCriterion("TEXEC.STATUS_TEXT <=", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextLike(String value) {
            addCriterion("TEXEC.STATUS_TEXT like", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextNotLike(String value) {
            addCriterion("TEXEC.STATUS_TEXT not like", value, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextIn(List<String> values) {
            addCriterion("TEXEC.STATUS_TEXT in", values, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextNotIn(List<String> values) {
            addCriterion("TEXEC.STATUS_TEXT not in", values, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextBetween(String value1, String value2) {
            addCriterion("TEXEC.STATUS_TEXT between", value1, value2, "statusText");
            return (Criteria) this;
        }

        public Criteria andStatusTextNotBetween(String value1, String value2) {
            addCriterion("TEXEC.STATUS_TEXT not between", value1, value2, "statusText");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsIsNull() {
            addCriterion("TEXEC.AFFECTED_RECORDS is null");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsIsNotNull() {
            addCriterion("TEXEC.AFFECTED_RECORDS is not null");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsEqualTo(Long value) {
            addCriterion("TEXEC.AFFECTED_RECORDS =", value, "affectedRecords");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsNotEqualTo(Long value) {
            addCriterion("TEXEC.AFFECTED_RECORDS <>", value, "affectedRecords");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsGreaterThan(Long value) {
            addCriterion("TEXEC.AFFECTED_RECORDS >", value, "affectedRecords");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsGreaterThanOrEqualTo(Long value) {
            addCriterion("TEXEC.AFFECTED_RECORDS >=", value, "affectedRecords");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsLessThan(Long value) {
            addCriterion("TEXEC.AFFECTED_RECORDS <", value, "affectedRecords");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsLessThanOrEqualTo(Long value) {
            addCriterion("TEXEC.AFFECTED_RECORDS <=", value, "affectedRecords");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsIn(List<Long> values) {
            addCriterion("TEXEC.AFFECTED_RECORDS in", values, "affectedRecords");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsNotIn(List<Long> values) {
            addCriterion("TEXEC.AFFECTED_RECORDS not in", values, "affectedRecords");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsBetween(Long value1, Long value2) {
            addCriterion("TEXEC.AFFECTED_RECORDS between", value1, value2, "affectedRecords");
            return (Criteria) this;
        }

        public Criteria andAffectedRecordsNotBetween(Long value1, Long value2) {
            addCriterion("TEXEC.AFFECTED_RECORDS not between", value1, value2, "affectedRecords");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidIsNull() {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID is null");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidIsNotNull() {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID is not null");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidEqualTo(String value) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID =", value, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidNotEqualTo(String value) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID <>", value, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidGreaterThan(String value) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID >", value, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidGreaterThanOrEqualTo(String value) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID >=", value, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidLessThan(String value) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID <", value, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidLessThanOrEqualTo(String value) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID <=", value, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidLike(String value) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID like", value, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidNotLike(String value) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID not like", value, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidIn(List<String> values) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID in", values, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidNotIn(List<String> values) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID not in", values, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidBetween(String value1, String value2) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID between", value1, value2, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidNotBetween(String value1, String value2) {
            addCriterion("TEXEC.LAST_ACC_EXECUTION_UID not between", value1, value2, "lastAccExecutionUid");
            return (Criteria) this;
        }

        public Criteria andUidActividadLikeInsensitive(String value) {
            addCriterion("upper(TEXEC.UID_ACTIVIDAD) like", value.toUpperCase(), "uidActividad");
            return (Criteria) this;
        }

        public Criteria andTriggerExecUidLikeInsensitive(String value) {
            addCriterion("upper(TEXEC.TRIGGER_EXEC_UID) like", value.toUpperCase(), "triggerExecUid");
            return (Criteria) this;
        }

        public Criteria andTriggerUidLikeInsensitive(String value) {
            addCriterion("upper(TEXEC.TRIGGER_UID) like", value.toUpperCase(), "triggerUid");
            return (Criteria) this;
        }

        public Criteria andStatusTextLikeInsensitive(String value) {
            addCriterion("upper(TEXEC.STATUS_TEXT) like", value.toUpperCase(), "statusText");
            return (Criteria) this;
        }

        public Criteria andLastAccExecutionUidLikeInsensitive(String value) {
            addCriterion("upper(TEXEC.LAST_ACC_EXECUTION_UID) like", value.toUpperCase(), "lastAccExecutionUid");
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