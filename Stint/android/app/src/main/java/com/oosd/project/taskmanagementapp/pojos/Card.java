package com.oosd.project.taskmanagementapp.pojos;

public class Card {
    private String id;
    private String laneId;
    private String title;
    private String description;
    private String dueDateTime;
    private String notificationDateTime;
    private String userId;
    private int position;
    private String notificationTimeMinutes;

    public String getNotificationTimeMinutes() {
        return notificationTimeMinutes;
    }

    public void setNotificationTimeMinutes(String notificationTimeMinutes) {
        this.notificationTimeMinutes = notificationTimeMinutes;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLaneId() {
        return laneId;
    }

    public void setLaneId(String laneId) {
        this.laneId = laneId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(String dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public String getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(String notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
