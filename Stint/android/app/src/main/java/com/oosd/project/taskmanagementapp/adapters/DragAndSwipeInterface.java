package com.oosd.project.taskmanagementapp.adapters;

public interface DragAndSwipeInterface {
    void onMove(int fromPosition, int toPosition);
    void onSwipe(int direction, int position);
}