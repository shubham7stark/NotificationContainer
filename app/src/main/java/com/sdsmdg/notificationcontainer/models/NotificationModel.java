package com.sdsmdg.notificationcontainer.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Chirag on 19-12-2016.
 */

public class NotificationModel extends RealmObject {
    private String title;
    private String description;
    private String packageName;
    private Date time;
    private byte[] pendingIntent;
    private Boolean important;

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Date getTime() { return time; }

    public void setTime(Date time) { this.time = time; }

    public byte[] getPendingIntent() { return pendingIntent; }

    public void setPendingIntent(byte[] pendingIntent) { this.pendingIntent = pendingIntent; }

    public String getPackageName() { return packageName; }

    public void setPackageName(String packageName) { this.packageName = packageName; }

    public Boolean isImportant() { return important; }

    public void setImportant(Boolean important) { this.important = important; }
}
