package com.oosd.project.taskmanagementapp.models;

import android.widget.ImageView;
import android.widget.TextView;

public class Board {
    private TextView boardId;
    private TextView userId;
    private TextView description;
    private String color;
    private TextView name;
    private ImageView image;

    public TextView getBoardId() {
        return boardId;
    }

    public void setBoardId(TextView boardId) {
        this.boardId = boardId;
    }

    public TextView getUserId() {
        return userId;
    }

    public void setUserId(TextView userId) {
        this.userId = userId;
    }

    public TextView getDescription() {
        return description;
    }

    public void setDescription(TextView description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
