package com.oosd.project.taskmanagementapp.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.oosd.project.taskmanagementapp.Activities.TasksBenchActivity;

public class RecyclerViewDragDropHelperClass extends ItemTouchHelper.Callback {
    private DragAndSwipeInterface dragAndSwipeInterface;
    private TasksBenchActivity tasksBenchActivityContext;

    public RecyclerViewDragDropHelperClass(DragAndSwipeInterface dragAndSwipeInterface, TasksBenchActivity tasksBenchActivityContext)
    {
        this.dragAndSwipeInterface = dragAndSwipeInterface;
        this.tasksBenchActivityContext = tasksBenchActivityContext;
    }
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = 0;
        if(TasksBenchActivity.CurrentPositon == 0)
        {
            // if its the left most lane then i dont want left swipe
            swipeFlags = ItemTouchHelper.END;

        }
        else if(TasksBenchActivity.CurrentPositon == tasksBenchActivityContext.getSwimLane().size()-2)
        {
            // if its the right most lane then i dont want right swipe
            swipeFlags = ItemTouchHelper.START;
        }
        else
        {
            // i want both swipes
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        dragAndSwipeInterface.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        dragAndSwipeInterface.onSwipe(direction, viewHolder.getAdapterPosition());

    }

}