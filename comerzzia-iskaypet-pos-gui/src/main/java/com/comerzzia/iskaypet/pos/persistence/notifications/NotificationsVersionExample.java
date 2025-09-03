package com.comerzzia.iskaypet.pos.persistence.notifications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsVersionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public NotificationsVersionExample() {
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

        public Criteria andNotificationUidIsNull() {
            addCriterion("NOTIFICATION_UID is null");
            return (Criteria) this;
        }

        public Criteria andNotificationUidIsNotNull() {
            addCriterion("NOTIFICATION_UID is not null");
            return (Criteria) this;
        }

        public Criteria andNotificationUidEqualTo(String value) {
            addCriterion("NOTIFICATION_UID =", value, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidNotEqualTo(String value) {
            addCriterion("NOTIFICATION_UID <>", value, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidGreaterThan(String value) {
            addCriterion("NOTIFICATION_UID >", value, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidGreaterThanOrEqualTo(String value) {
            addCriterion("NOTIFICATION_UID >=", value, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidLessThan(String value) {
            addCriterion("NOTIFICATION_UID <", value, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidLessThanOrEqualTo(String value) {
            addCriterion("NOTIFICATION_UID <=", value, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidLike(String value) {
            addCriterion("NOTIFICATION_UID like", value, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidNotLike(String value) {
            addCriterion("NOTIFICATION_UID not like", value, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidIn(List<String> values) {
            addCriterion("NOTIFICATION_UID in", values, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidNotIn(List<String> values) {
            addCriterion("NOTIFICATION_UID not in", values, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidBetween(String value1, String value2) {
            addCriterion("NOTIFICATION_UID between", value1, value2, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidNotBetween(String value1, String value2) {
            addCriterion("NOTIFICATION_UID not between", value1, value2, "notificationUid");
            return (Criteria) this;
        }

        public Criteria andMsgIsNull() {
            addCriterion("MSG is null");
            return (Criteria) this;
        }

        public Criteria andMsgIsNotNull() {
            addCriterion("MSG is not null");
            return (Criteria) this;
        }

        public Criteria andMsgEqualTo(String value) {
            addCriterion("MSG =", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgNotEqualTo(String value) {
            addCriterion("MSG <>", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgGreaterThan(String value) {
            addCriterion("MSG >", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgGreaterThanOrEqualTo(String value) {
            addCriterion("MSG >=", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgLessThan(String value) {
            addCriterion("MSG <", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgLessThanOrEqualTo(String value) {
            addCriterion("MSG <=", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgLike(String value) {
            addCriterion("MSG like", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgNotLike(String value) {
            addCriterion("MSG not like", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgIn(List<String> values) {
            addCriterion("MSG in", values, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgNotIn(List<String> values) {
            addCriterion("MSG not in", values, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgBetween(String value1, String value2) {
            addCriterion("MSG between", value1, value2, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgNotBetween(String value1, String value2) {
            addCriterion("MSG not between", value1, value2, "msg");
            return (Criteria) this;
        }

        public Criteria andViewedIsNull() {
            addCriterion("VIEWED is null");
            return (Criteria) this;
        }

        public Criteria andViewedIsNotNull() {
            addCriterion("VIEWED is not null");
            return (Criteria) this;
        }

        public Criteria andViewedEqualTo(String value) {
            addCriterion("VIEWED =", value, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedNotEqualTo(String value) {
            addCriterion("VIEWED <>", value, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedGreaterThan(String value) {
            addCriterion("VIEWED >", value, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedGreaterThanOrEqualTo(String value) {
            addCriterion("VIEWED >=", value, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedLessThan(String value) {
            addCriterion("VIEWED <", value, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedLessThanOrEqualTo(String value) {
            addCriterion("VIEWED <=", value, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedLike(String value) {
            addCriterion("VIEWED like", value, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedNotLike(String value) {
            addCriterion("VIEWED not like", value, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedIn(List<String> values) {
            addCriterion("VIEWED in", values, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedNotIn(List<String> values) {
            addCriterion("VIEWED not in", values, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedBetween(String value1, String value2) {
            addCriterion("VIEWED between", value1, value2, "viewed");
            return (Criteria) this;
        }

        public Criteria andViewedNotBetween(String value1, String value2) {
            addCriterion("VIEWED not between", value1, value2, "viewed");
            return (Criteria) this;
        }

        public Criteria andNotificationDateIsNull() {
            addCriterion("NOTIFICATION_DATE is null");
            return (Criteria) this;
        }

        public Criteria andNotificationDateIsNotNull() {
            addCriterion("NOTIFICATION_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andNotificationDateEqualTo(Date value) {
            addCriterion("NOTIFICATION_DATE =", value, "notificationDate");
            return (Criteria) this;
        }

        public Criteria andNotificationDateNotEqualTo(Date value) {
            addCriterion("NOTIFICATION_DATE <>", value, "notificationDate");
            return (Criteria) this;
        }

        public Criteria andNotificationDateGreaterThan(Date value) {
            addCriterion("NOTIFICATION_DATE >", value, "notificationDate");
            return (Criteria) this;
        }

        public Criteria andNotificationDateGreaterThanOrEqualTo(Date value) {
            addCriterion("NOTIFICATION_DATE >=", value, "notificationDate");
            return (Criteria) this;
        }

        public Criteria andNotificationDateLessThan(Date value) {
            addCriterion("NOTIFICATION_DATE <", value, "notificationDate");
            return (Criteria) this;
        }

        public Criteria andNotificationDateLessThanOrEqualTo(Date value) {
            addCriterion("NOTIFICATION_DATE <=", value, "notificationDate");
            return (Criteria) this;
        }

        public Criteria andNotificationDateIn(List<Date> values) {
            addCriterion("NOTIFICATION_DATE in", values, "notificationDate");
            return (Criteria) this;
        }

        public Criteria andNotificationDateNotIn(List<Date> values) {
            addCriterion("NOTIFICATION_DATE not in", values, "notificationDate");
            return (Criteria) this;
        }

        public Criteria andNotificationDateBetween(Date value1, Date value2) {
            addCriterion("NOTIFICATION_DATE between", value1, value2, "notificationDate");
            return (Criteria) this;
        }

        public Criteria andNotificationDateNotBetween(Date value1, Date value2) {
            addCriterion("NOTIFICATION_DATE not between", value1, value2, "notificationDate");
            return (Criteria) this;
        }

        public Criteria andActivityUidLikeInsensitive(String value) {
            addCriterion("upper(ACTIVITY_UID) like", value.toUpperCase(), "activityUid");
            return (Criteria) this;
        }

        public Criteria andNotificationUidLikeInsensitive(String value) {
            addCriterion("upper(NOTIFICATION_UID) like", value.toUpperCase(), "notificationUid");
            return (Criteria) this;
        }

        public Criteria andMsgLikeInsensitive(String value) {
            addCriterion("upper(MSG) like", value.toUpperCase(), "msg");
            return (Criteria) this;
        }

        public Criteria andViewedLikeInsensitive(String value) {
            addCriterion("upper(VIEWED) like", value.toUpperCase(), "viewed");
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