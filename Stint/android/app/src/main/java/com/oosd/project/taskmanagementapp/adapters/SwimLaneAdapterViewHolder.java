package com.oosd.project.taskmanagementapp.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.Activities.TasksBenchActivity;

public class SwimLaneAdapterViewHolder extends RecyclerView.ViewHolder {

    public TextView swimLaneTitle;
    public SwimLaneAdapterViewHolder(View v, final TasksBenchActivity tasksBenchActivityContext) {
        super(v);
        swimLaneTitle = v.findViewById(R.id.swimLaneTitle);
        swimLaneTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tasksBenchActivityContext.getSwimLane().get(getAdapterPosition()).getLaneId().equals("-1"))
                {
                    //open modal here
                    tasksBenchActivityContext.addNewLaneModal();
                }
                else
                {
                    TasksBenchActivity.CurrentPositon = getAdapterPosition();
                    tasksBenchActivityContext.initializeListViewForTask(getAdapterPosition());
                }

            }
        });
    }
}
