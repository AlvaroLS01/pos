package com.comerzzia.iskaypet.pos.persistence.notifications;

public class NotificationsVersionKey {
    private String activityUid;

    private String notificationUid;

    public String getActivityUid() {
        return activityUid;
    }

    public void setActivityUid(String activityUid) {
        this.activityUid = activityUid == null ? null : activityUid.trim();
    }

    public String getNotificationUid() {
        return notificationUid;
    }

    public void setNotificationUid(String notificationUid) {
        this.notificationUid = notificationUid == null ? null : notificationUid.trim();
    }
}