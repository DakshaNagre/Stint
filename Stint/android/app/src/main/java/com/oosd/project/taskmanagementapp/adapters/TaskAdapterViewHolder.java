package com.oosd.project.taskmanagementapp.adapters;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.Activities.TasksBenchActivity;
import com.oosd.project.taskmanagementapp.pojos.Card;
import com.oosd.project.taskmanagementapp.pojos.SwimLane;
import com.oosd.project.taskmanagementapp.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.oosd.project.taskmanagementapp.Activities.TasksBenchActivity.CurrentPositon;

public class TaskAdapterViewHolder extends RecyclerView.ViewHolder
{
    public TextView cardTitle;
    private TextView cardDescription;
    private TextView cardDueDateTime;
    private ImageButton cardEditButton;
    DatePickerDialog.OnDateSetListener date;
    private RequestQueue queue;


    private TextView editTitle;
    private TextView editDescription;
    private TextView editDueDate;
    private TextView editDueTime;
    private Spinner editNotificationTime;
    private Button editSaveButton;
    private Button editCancelButton;
    private Button editDeleteButton;
    private FloatingActionButton addCardFab;
    private View taskView;

    private LayoutInflater li;
    private View viewForMovingTaskAmongLanes;
    private AlertDialog.Builder dialogToMoveTaskAmongLanes;
    private AlertDialog alertDialog;
    private Spinner moveLane;
    private MaterialButton moveButton;
    private MaterialButton cancelButton;


    BottomSheetBehavior bottomSheetBehavior;
    ConstraintLayout bottomSheetLayout;
    final Calendar myCalendar = Calendar.getInstance();
    TasksBenchActivity mContext;
    ProgressDialog progressDialog;

    ArrayAdapter<String> arrayAdapter;
    List<String> laneTitles;
    List<String> laneIds;
    private LinearLayout greyBackground;




    public TaskAdapterViewHolder(View layout, TasksBenchActivity tasksBenchActivityContext, ConstraintLayout bottomSheetLayout, List<Object> objectsFromActivity)
    {
        super(layout);
        this.taskView = layout;
        this.mContext = tasksBenchActivityContext;
        cardTitle = layout.findViewById(R.id.cardTitle);
        cardDescription = layout.findViewById(R.id.cardDescription);
        cardDueDateTime = layout.findViewById(R.id.cardDueDateTime);
        cardEditButton = layout.findViewById(R.id.cardEditButton);

        editTitle = (EditText)objectsFromActivity.get(0);
        editDescription = (EditText)objectsFromActivity.get(1);
        editDueDate = (EditText)objectsFromActivity.get(2);
        editDueTime = (EditText)objectsFromActivity.get(3);

        editNotificationTime = (Spinner) objectsFromActivity.get(4);

        editSaveButton = (Button)objectsFromActivity.get(5);
        editCancelButton = (Button)objectsFromActivity.get(6);
        editDeleteButton = (Button)objectsFromActivity.get(7);
        addCardFab = (FloatingActionButton)objectsFromActivity.get(9);
        greyBackground = (LinearLayout)objectsFromActivity.get(10);

        this.bottomSheetLayout = bottomSheetLayout;
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Called every time when the bottom sheet changes its state.
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Called when the bottom sheet is being dragged
                if(slideOffset == 0.0)
                {
                    addCardFab.show();
                    greyBackground.setVisibility(View.GONE);
                }
                if(slideOffset == 1.0)
                {
                    addCardFab.hide();
                    greyBackground.setVisibility(View.VISIBLE);
                }
            }
        });

        queue = Volley.newRequestQueue(tasksBenchActivityContext);
        progressDialog = new ProgressDialog(tasksBenchActivityContext);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Updating data .....");


        laneTitles = new ArrayList<>();
        laneIds = new ArrayList<>();
        for(SwimLane swimLane : mContext.getSwimLane())
        {
            if(!swimLane.getLaneId().equals("-1"))
            {
                laneTitles.add(swimLane.getTitle());
                laneIds.add(swimLane.getLaneId());
            }
        }
        arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, laneTitles);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        cardEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSheet();
            }
        });
        initializeListeners();

    }

    public TextView getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle.setText(cardTitle);
    }

    public TextView getCardDescription() {
        return cardDescription;
    }

    public void setCardDescription(String cardDescription) {
        this.cardDescription.setText(cardDescription);
    }

    public TextView getCardDueDateTime() {
        return cardDueDateTime;
    }

    public void setCardDueDateTime(String cardDueDateTime) {
        this.cardDueDateTime.setText(cardDueDateTime);
    }

    private void toggleSheet()
    {
        editTitle.setText(this.getCardTitle().getText());
        editDescription.setText(this.getCardDescription().getText());
        editDueDate.setText(this.cardDueDateTime.getText());
        editDueTime.setText(this.cardDueDateTime.getText());
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
    private void initializeListeners()
    {
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };


        this.taskView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openModalToMoveTask();
                return true;
            }
        });



        //setting a listener to select the date to the edit text
        editDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        // selecting the time for due date
        editDueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editDueTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        editSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call the edit api here
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                progressDialog.show();
                updateTheCardinUI(editTitle.getText().toString(), editDescription.getText().toString(),
                        Integer.toString(Constants.SPINNER_ITEMS_VALUES[editNotificationTime.getSelectedItemPosition()]),
                        editDueDate.getText().toString()+ " " + editDueTime.getText().toString()
                        );
                apiForEditingCardPosition(false,"-1");
            }
        });
        editCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close the bottom sheet here
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        editDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call the delete api over here
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });
    }
    private void openModalToMoveTask()
    {
        li = LayoutInflater.from(mContext);
        viewForMovingTaskAmongLanes = li.inflate(R.layout.view_for_moving_task_between_lanes, null);
        dialogToMoveTaskAmongLanes = new AlertDialog.Builder(mContext,R.style.MyDialogTheme);
        dialogToMoveTaskAmongLanes.setView(viewForMovingTaskAmongLanes);
        alertDialog = dialogToMoveTaskAmongLanes.create();
        moveLane = viewForMovingTaskAmongLanes.findViewById(R.id.moveLane);
        moveLane.setAdapter(arrayAdapter);
        moveButton = viewForMovingTaskAmongLanes.findViewById(R.id.moveButton);
        cancelButton = viewForMovingTaskAmongLanes.findViewById(R.id.cancelButton);
        alertDialog.show();
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the add card api here
                progressDialog.setMessage("Moving Task...");
                progressDialog.show();
                apiForEditingCardPosition(true,laneIds.get(moveLane.getSelectedItemPosition()));
                alertDialog.cancel();

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        dialogToMoveTaskAmongLanes.setCancelable(true);
    }

    private void updateLabel()
    {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editDueDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void apiForEditingCardPosition(final boolean fromModal, final String toLaneId)
    {
        JSONObject jsonBody = new JSONObject();
        int cardPositionInLane = getAdapterPosition();



        if(fromModal) //this data is coming from modal on long press
        {
            String dueDateTime = "";
            if(mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).getDueDateTime() != null)
            {
                dueDateTime = mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).getDueDateTime();
            }
            try {
                jsonBody.put("title", mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).getTitle());
                jsonBody.put("laneId", toLaneId);
                jsonBody.put("id" , mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).getId());
                jsonBody.put("description", mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).getDescription());
                jsonBody.put("position", mContext.getSwimLane().get(laneIds.indexOf(toLaneId)).getCards().size());
                jsonBody.put("dueDateTime", dueDateTime);
                jsonBody.put("notificationTimeMinutes", mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).getNotificationTimeMinutes());

                //updating the list for UI
                Card cardToRemove = mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane);
                mContext.getSwimLane().get(CurrentPositon).getCards().remove(cardToRemove);
                cardToRemove.setLaneId(toLaneId);
                cardToRemove.setPosition(mContext.getSwimLane().get(laneIds.indexOf(toLaneId)).getCards().size());
                mContext.getSwimLane().get(laneIds.indexOf(toLaneId)).getCards().add(cardToRemove);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else //this data is coming from the bottom sheet
        {
            try {
                jsonBody.put("title", editTitle.getText().toString());
                jsonBody.put("laneId", mContext.getSwimLane().get(CurrentPositon).getLaneId());
                jsonBody.put("id" , mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).getId());
                jsonBody.put("description", editDescription.getText().toString());
                jsonBody.put("position", cardPositionInLane);
                jsonBody.put("dueDateTime", editDueDate.getText().toString() + " " +editDueTime.getText().toString());
                jsonBody.put("notificationTimeMinutes", Integer.toString(Constants.SPINNER_ITEMS_VALUES[editNotificationTime.getSelectedItemPosition()]));

                //updating the list for UI
                mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).setTitle(editTitle.getText().toString());
                mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).setDescription(editDescription.getText().toString());
                mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).setNotificationDateTime(Integer.toString(Constants.SPINNER_ITEMS_VALUES[editNotificationTime.getSelectedItemPosition()]));
                mContext.getSwimLane().get(CurrentPositon).getCards().get(cardPositionInLane).setDueDateTime(editDueDate.getText().toString() + " " + editDueTime.getText().toString());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.BACKEND_URL + "/editTask", jsonBody,
                new Response.Listener<JSONObject>() {
                    // On success this will be triggered
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(mContext,
                                "Task Moved successfully", Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                        if(fromModal){
                            CurrentPositon = laneIds.indexOf(toLaneId);
                            mContext.initializeListViewForTask(laneIds.indexOf(toLaneId));
                        }
                        else
                        {
                            mContext.initializeListViewForTask(CurrentPositon);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    // Showing a toast message with necessary errorMessage
                    progressDialog.cancel();
                    renderError(new String(error.networkResponse.data, "UTF-8"));
                }catch (Exception exception){
                    Log.d("Exception", "Exception has occuered while" +
                            " parsing the error response received from login API");
                }
            }
        }){
            // Passing Authorization in headers
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", getTokenFromSharedPReferences());
                return headers;
            }
        };
        queue.add(postRequest);
    }
    private void renderError(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            Toast.makeText(mContext,
                    jsonObject.get("errorMessage").toString(), Toast.LENGTH_LONG).show();
        }catch (JSONException exception){
            Log.d("JSONException:", exception.getLocalizedMessage());
        }
    }
    private String getTokenFromSharedPReferences(){
        SharedPreferences pref = mContext.getSharedPreferences("user", 0);
        return pref.getString("token", null);
    }
    private void updateTheCardinUI(String title, String description, String notificationTimeMinutes, String dueDateTime)
    {
        mContext.getSwimLane().get(CurrentPositon).getCards().get(getAdapterPosition()).setTitle(title);
        mContext.getSwimLane().get(CurrentPositon).getCards().get(getAdapterPosition()).setDescription(description);
        mContext.getSwimLane().get(CurrentPositon).getCards().get(getAdapterPosition()).setNotificationTimeMinutes(notificationTimeMinutes);
        mContext.getSwimLane().get(CurrentPositon).getCards().get(getAdapterPosition()).setDueDateTime(dueDateTime);
        mContext.initializeListViewForTask(CurrentPositon);
    }
}
