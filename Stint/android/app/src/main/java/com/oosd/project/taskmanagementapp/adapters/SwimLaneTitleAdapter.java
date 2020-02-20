package com.oosd.project.taskmanagementapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.Activities.TasksBenchActivity;
import com.oosd.project.taskmanagementapp.pojos.SwimLane;

import java.util.List;

public class SwimLaneTitleAdapter extends RecyclerView.Adapter<SwimLaneAdapterViewHolder> {
    private List<SwimLane> swimLane;
    TasksBenchActivity tasksBenchActivityContext;
    public SwimLaneTitleAdapter(List<SwimLane> Data, TasksBenchActivity tasksBenchActivityContext) {
        this.tasksBenchActivityContext = tasksBenchActivityContext;
        this.swimLane = Data;
    }
    @Override
    public SwimLaneAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swim_lane_titles, parent, false);
        SwimLaneAdapterViewHolder holder = new SwimLaneAdapterViewHolder(view,tasksBenchActivityContext);
        return holder;
    }
    @Override
    public void onBindViewHolder(final SwimLaneAdapterViewHolder holder, int position) {
        TasksBenchActivity.swimLanePositionTitleMapping.put(position , holder.swimLaneTitle);
        holder.swimLaneTitle.setText(swimLane.get(position).getTitle());
    }
    @Override
    public int getItemCount() {
        return swimLane.size();
    }
}
