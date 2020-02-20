package com.oosd.project.taskmanagementapp.models;

public class Event {

    private String title;
    private String description;
    private String dueDateTime;

    public Event(String title, String description, String dueDateTime)
    {
        this.title = title;
        this.description = description;
        this. dueDateTime = dueDateTime;
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

}
