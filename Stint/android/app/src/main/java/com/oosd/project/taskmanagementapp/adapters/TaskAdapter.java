package com.oosd.project.taskmanagementapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.Activities.TasksBenchActivity;
import com.oosd.project.taskmanagementapp.pojos.Card;
import java.util.List;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapterViewHolder> implements View.OnClickListener{

    private TasksBenchActivity mContext;
    private ConstraintLayout bottomSheetLayout;
    private List<Object> objectsFromActivity;
    private List<Card> data;

    @NonNull
    @Override
    public TaskAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_for_tasks_in_lanes, parent, false);
        TaskAdapterViewHolder holder = new TaskAdapterViewHolder(view,mContext,bottomSheetLayout,objectsFromActivity);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapterViewHolder holder, int position) {
        holder.setCardTitle(data.get(position).getTitle());
        holder.setCardDescription(data.get(position).getDescription());
        try
        {
            if(data.get(position).getDueDateTime().equals("") || data.get(position).getDueDateTime()== null)
            {
                holder.setCardDueDateTime("No Due Date");
            }
            else
            {
                holder.setCardDueDateTime(data.get(position).getDueDateTime());
            }
        }
        catch(Exception ex)
        {
            holder.setCardDueDateTime("No Due Date");
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public TaskAdapter(List<Card> data, TasksBenchActivity context, ConstraintLayout bottomSheetLayout, List<Object> objectsFromActivity) {
        this.data = data;
        this.mContext=context;
        this.bottomSheetLayout = bottomSheetLayout;
        this.objectsFromActivity = objectsFromActivity;
    }

    @Override
    public void onClick(View v) {
        return;
    }
}