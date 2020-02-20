package com.oosd.project.taskmanagementapp.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.adapters.DragAndSwipeInterface;
import com.oosd.project.taskmanagementapp.adapters.RecyclerViewDragDropHelperClass;
import com.oosd.project.taskmanagementapp.adapters.SwimLaneTitleAdapter;
import com.oosd.project.taskmanagementapp.adapters.TaskAdapter;
import com.oosd.project.taskmanagementapp.pojos.Card;
import com.oosd.project.taskmanagementapp.pojos.SwimLane;
import com.oosd.project.taskmanagementapp.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksBenchActivity extends AppCompatActivity implements DragAndSwipeInterface {

    private JSONObject boardObject;
    private RequestQueue queue;
    private RecyclerView recyclerView;


    List<SwimLane> swimLane;

    private Gson gson;
    private TextView boardTitle;
    private RecyclerView recyclerViewForTask;
    private ConstraintLayout bottomSheetLayout;
    private FloatingActionButton addCard;
    private LayoutInflater li;
    private View viewForAddNewCard;
    private AlertDialog.Builder dialogToAddNewCard;
    private AlertDialog alertDialog;

    private EditText addTitle;
    private EditText addDescription;

    private EditText addLaneTitle;
    private EditText addLaneDescription;

    private MaterialButton cancel;

    public TaskAdapter adapater;
    public TextView noCardText;
    private static final int GALLERY_REQUEST_CODE  = 1;
    public String image;

    private ImageView imageView;
    private EditText name;
    protected EditText description;



    List<Object> dataToPassToAdapter = new ArrayList<>();
    public static int CurrentPositon = 0;
    public static Map<Integer, TextView> swimLanePositionTitleMapping = new HashMap<>();
    ProgressDialog progressDialog;
    private AlertDialog alertDialogForEditBoard;

    public List<SwimLane> getSwimLane() {
        return swimLane;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_bench);
        image = "";
        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        boardTitle = findViewById(R.id.boardTitle);
        recyclerViewForTask = findViewById(R.id.listViewForTasks);
        noCardText = findViewById(R.id.noCardsText);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();
        recyclerViewForTask.setHasFixedSize(false);

        recyclerView = findViewById(R.id.recyclerViewForSwimLaneTitles);
        recyclerView.setHasFixedSize(true);

        queue = Volley.newRequestQueue(this);
        gson = new Gson();
        this.swimLane = new ArrayList<>();

        initializeSpinner();

        dataToPassToAdapter.add(findViewById(R.id.editTitle));
        dataToPassToAdapter.add(findViewById(R.id.editDescription));
        dataToPassToAdapter.add(findViewById(R.id.editDueDate));
        dataToPassToAdapter.add(findViewById(R.id.editdueTime));
        dataToPassToAdapter.add(findViewById(R.id.editNotificationTime));
        dataToPassToAdapter.add(findViewById(R.id.confirmButton));
        dataToPassToAdapter.add(findViewById(R.id.cancelButton));
        dataToPassToAdapter.add(findViewById(R.id.deleteButton));
        dataToPassToAdapter.add(findViewById(R.id.mainLayout));
        dataToPassToAdapter.add(findViewById(R.id.addCard));
        dataToPassToAdapter.add(findViewById(R.id.greyBackground));

        initializeDialog();
        handleAddCard();
        initializeUI();

    }
    public void addNewLaneModal()
    {
        li = LayoutInflater.from(this);
        viewForAddNewCard= li.inflate(R.layout.add_new_lane, null);
        dialogToAddNewCard = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        dialogToAddNewCard.setView(viewForAddNewCard);
        alertDialog = dialogToAddNewCard.create();
        addLaneTitle = viewForAddNewCard.findViewById(R.id.addLaneTitle);
        addLaneDescription = viewForAddNewCard.findViewById(R.id.addLaneDescription);
        addLaneTitle.setText("");
        addLaneDescription.setText("");
        MaterialButton addLane = viewForAddNewCard.findViewById(R.id.addLane);
        cancel = viewForAddNewCard.findViewById(R.id.cancel);
        alertDialog.show();
        addLane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the add card api here
                alertDialog.cancel();
                progressDialog.setMessage("Creating Lane...");
                progressDialog.show();
                apiToAddNewLane(addLaneTitle.getText().toString(), addLaneDescription.getText().toString());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        dialogToAddNewCard.setCancelable(true);
    }
    private void apiToAddNewLane(String laneTitle, String laneDescription)
    {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("boardId", this.boardObject.getString("board_id"));
            jsonBody.put("position", this.swimLane.size()-1);
            jsonBody.put("title", laneTitle);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.BACKEND_URL + "/createSwimLane", jsonBody,
                new Response.Listener<JSONObject>() {
                    // On success this will be triggered
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(TasksBenchActivity.this,
                                "Lane added successfully", Toast.LENGTH_LONG).show();
                        SwimLane newLane = new SwimLane();
                        try {
                            newLane.setLaneId(response.getString("id"));
                            newLane.setTitle(response.getString("title"));
                            newLane.setBoardId(response.getString("boardId"));
                            newLane.setCards(new ArrayList<Card>());
                            newLane.setPosition(Integer.parseInt(response.getString("position")));
                            swimLane.add(newLane);
                            Collections.swap(swimLane, swimLane.size()-1, swimLane.size()-2);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        finally
                        {
                            progressDialog.cancel();
                            initializeRecyclerView();
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
    private void initializeSpinner()
    {
        Spinner spinner = findViewById(R.id.editNotificationTime);
        List<String> arrayList = new ArrayList<>();
        arrayList.addAll(Arrays.asList(Constants.SPINNER_ITEMS));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    // Method to naviagte to edit baotd screen
    public void goToBoardEdit(View view){

        li = LayoutInflater.from(this);
        viewForAddNewCard= li.inflate(R.layout.activity_edit_board, null);
        AlertDialog.Builder dialogToEditBoard = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        dialogToEditBoard.setView(viewForAddNewCard);
        alertDialogForEditBoard = dialogToEditBoard.create();
        this.imageView = viewForAddNewCard.findViewById(R.id.backgroundImage);
        this.name = viewForAddNewCard.findViewById(R.id.name);
        this.description = viewForAddNewCard.findViewById(R.id.description);
        dialogToEditBoard.setCancelable(true);
        alertDialogForEditBoard.show();
    }

    // Method to handle the data returned by edit board screen
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == GALLERY_REQUEST_CODE)
            {
                try {
                    Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    this.convertImageToBase64(selectedImage);
                    this.imageView.setImageBitmap(selectedImage);
                }catch (Exception exception){
                    Log.d("Exception: ", "Exception has occurred while loading image");
                }
            }
            else
            {
                try {
                    this.boardObject = new JSONObject((String) data.getSerializableExtra("boardObject"));
                    boardTitle.setText(this.boardObject.getString("name"));
                }catch (JSONException exception){
                    Log.d("JSON Exception", "Exception while upating board");
                }
            }
        }
    }

    // Private method to get board data
    private void initializeUI(){
        try {
            Intent intent = getIntent();
            this.boardObject = new JSONObject((String)intent.getSerializableExtra("boardObject"));
            boardTitle.setText(this.boardObject.getString("name"));
            getSwimLanes();
            String color = this.boardObject.getString("color");
            if(color.equals("null")){
                findViewById(R.id.mainLayout).setBackgroundColor(Color.parseColor("#f2f2f2"));
            }else{
                findViewById(R.id.mainLayout).setBackgroundColor(Color.parseColor(color));
            }
        }catch (JSONException err){
            Log.d("JSONException", "Error has occurred while parsing the boardObject");
        }
    }

    private void getSwimLanes()
    {
        try {
            String boardId = this.boardObject.getString("board_id");
            JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET,
                    Constants.BACKEND_URL + "/getSwimLanes/boardId/"+boardId, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            swimLane = gson.fromJson(response.toString(), new TypeToken<List<SwimLane>>() {}.getType());
                            SwimLane addLane = new SwimLane();
                            addLane.setTitle("Add Lane");
                            addLane.setLaneId("-1");
                            swimLane.add(addLane);
                            initializeRecyclerView();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // display error message
                            try{
                                // Showing a toast message with necessary errorMessage
                                renderError(new String(error.networkResponse.data, "UTF-8"));
                            }catch (Exception exception){
                                Log.d("Exception", "Exception has occuered while" +
                                        " parsing the error response received from /getswimlane API");
                            }
                        }
                    }
            ){
                // Passing Authorization in headers
                @Override
                public Map getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", getTokenFromSharedPReferences());
                    return headers;
                }
            };
            queue.add(getRequest);

        }
        catch (Exception err)
        {
            Log.d("JSONException", "Error has occurred while getting the String names");
        }
    }
    private void renderError(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            Toast.makeText(TasksBenchActivity.this,
                    jsonObject.get("errorMessage").toString(), Toast.LENGTH_LONG).show();
        }catch (JSONException exception){
            Log.d("JSONException:", exception.getLocalizedMessage());
        }
    }
    private String getTokenFromSharedPReferences(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("user", 0);
        return pref.getString("token", null);
    }

    private void initializeRecyclerView()
    {
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(TasksBenchActivity.this);
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (this.swimLane.size() > 0 & recyclerView != null) {
            recyclerView.setAdapter(new SwimLaneTitleAdapter(this.swimLane, TasksBenchActivity.this));
            recyclerView.setLayoutManager(MyLayoutManager);
            recyclerView.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            initializeListViewForTask(0); //initially load the view with the first swim lane cards
                            recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
        }
    }

    public void initializeListViewForTask(int position)
    {
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(TasksBenchActivity.this);
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (this.swimLane.size()>1 && this.swimLane.get(position).getCards().size() > 0 && recyclerViewForTask != null) {
            recyclerViewForTask.setVisibility(View.VISIBLE);
            noCardText.setVisibility(View.GONE);
            this.adapater = new TaskAdapter(this.swimLane.get(position).getCards(),TasksBenchActivity.this, bottomSheetLayout, dataToPassToAdapter);
            recyclerViewForTask.setAdapter(this.adapater);
            recyclerViewForTask.setLayoutManager(MyLayoutManager);
            ItemTouchHelper.Callback callback = new RecyclerViewDragDropHelperClass(this,TasksBenchActivity.this);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerViewForTask);
            recyclerViewForTask.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            changeButtonColorToActive();
                            recyclerViewForTask.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
        }
        else
        {
            if(this.swimLane.size()>1)
            {
                changeButtonColorToActive();
            }
            recyclerViewForTask.setVisibility(View.GONE);
            noCardText.setVisibility(View.VISIBLE);
        }
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }
    private void handleAddCard()
    {
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeDialog();
                alertDialog.show();
            }
        });
    }
    private void initializeDialog()
    {
        addCard = findViewById(R.id.addCard);
        li = LayoutInflater.from(this);
        viewForAddNewCard= li.inflate(R.layout.add_new_card, null);
        dialogToAddNewCard = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        dialogToAddNewCard.setView(viewForAddNewCard);
        alertDialog = dialogToAddNewCard.create();
        addTitle = viewForAddNewCard.findViewById(R.id.addTitle);
        addDescription = viewForAddNewCard.findViewById(R.id.addDescription);
        addTitle.setText("");
        addDescription.setText("");
        MaterialButton addTask = viewForAddNewCard.findViewById(R.id.addTask);
        cancel = viewForAddNewCard.findViewById(R.id.cancel);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the add card api here
                alertDialog.cancel();
                progressDialog.setMessage("Creating task...");
                progressDialog.show();
                apiForAddNewCard(addTitle.getText().toString(), addDescription.getText().toString());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        dialogToAddNewCard.setCancelable(true);
    }
    private void addNewCardinList(String title, String description, String cardIdFromDatabase)
    {
        Card card = new Card();
        card.setTitle(title);
        card.setDescription(description);
        card.setNotificationTimeMinutes("-1");
        card.setPosition(this.swimLane.get(CurrentPositon).getCards().size()+1);
        card.setLaneId(this.swimLane.get(CurrentPositon).getLaneId());
        card.setId(cardIdFromDatabase);
        this.swimLane.get(CurrentPositon).getCards().add(card);
    }

    private void apiForAddNewCard(String title, String description)
    {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("title", title);
            jsonBody.put("laneId", this.swimLane.get(CurrentPositon).getLaneId());
            jsonBody.put("description", description);
            jsonBody.put("position", this.swimLane.get(CurrentPositon).getCards().size()+1);
            jsonBody.put("notificationTimeMinutes", Integer.toString(-1));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.BACKEND_URL + "/createTask", jsonBody,
                new Response.Listener<JSONObject>() {
                    // On success this will be triggered
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(TasksBenchActivity.this,
                                "Task Created successfully", Toast.LENGTH_LONG).show();
                        try {
                            addNewCardinList(response.getString("title"), response.getString("description"), response.getString("id"));
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        initializeListViewForTask(CurrentPositon);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    // Showing a toast message with necessary errorMessage
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
    private void apiForEditingCardPosition()
    {
        JSONObject jsonBody = new JSONObject();
        String dueDateTime = "";
        String notificationTime = "-1";
        int cardPositionInLane = this.swimLane.get(CurrentPositon).getCards().size()-1;

        if(this.swimLane.get(CurrentPositon).getCards().get(cardPositionInLane).getDueDateTime() != null &&
                !this.swimLane.get(CurrentPositon).getCards().get(cardPositionInLane).getDueDateTime().equals(""))
        {
                dueDateTime = this.swimLane.get(CurrentPositon).getCards().get(cardPositionInLane).getDueDateTime();
        }

        if(this.swimLane.get(CurrentPositon).getCards().get(cardPositionInLane).getNotificationDateTime() != null &&
                !this.swimLane.get(CurrentPositon).getCards().get(cardPositionInLane).getNotificationDateTime().equals(""))
        {
            notificationTime = this.swimLane.get(CurrentPositon).getCards().get(cardPositionInLane).getNotificationTimeMinutes();
        }



        try {
            jsonBody.put("title", this.swimLane.get(CurrentPositon).getCards().get(cardPositionInLane).getTitle());
            jsonBody.put("laneId", this.swimLane.get(CurrentPositon).getLaneId());
            jsonBody.put("id" , this.swimLane.get(CurrentPositon).getCards().get(cardPositionInLane).getId());
            jsonBody.put("description", this.swimLane.get(CurrentPositon).getCards().get(cardPositionInLane).getDescription());
            jsonBody.put("position", cardPositionInLane);
            jsonBody.put("dueDateTime", dueDateTime);
            jsonBody.put("notificationTimeMinutes", notificationTime);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.BACKEND_URL + "/editTask", jsonBody,
                new Response.Listener<JSONObject>() {
                    // On success this will be triggered
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(TasksBenchActivity.this,
                                "Task Moved successfully", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    // Showing a toast message with necessary errorMessage
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
    private void changeButtonColorToActive()
    {
        swimLanePositionTitleMapping.get(CurrentPositon).setBackgroundColor(getResources().getColor(R.color.activeSwimLaneColor));
        for(Map.Entry entry: swimLanePositionTitleMapping.entrySet())
        {
            if((int)entry.getKey() == CurrentPositon)
            {
                continue;
            }
            ((TextView)entry.getValue()).setBackgroundColor(getResources().getColor(R.color.swimLaneColor));
        }
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(this.swimLane.get(CurrentPositon).getCards(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(this.swimLane.get(CurrentPositon).getCards(), i, i - 1);
            }
        }
        this.adapater.notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onSwipe(int direction, int position) {
        System.out.println("Direction: "+direction);
        System.out.println("Position: "+position);

        if(direction == ItemTouchHelper.START) // swiped in left direction
        {
           if(CurrentPositon != 0)
           {
               // it is already in the first swim lane so cannot swipe left
               this.swimLane.get(CurrentPositon-1).getCards().add(this.swimLane.get(CurrentPositon).getCards().remove(position));
               initializeListViewForTask(CurrentPositon-1);
               CurrentPositon--;
               apiForEditingCardPosition();
               changeButtonColorToActive();
           }
        }

        else if(direction == ItemTouchHelper.END) // swiped in right direction
        {
            if(CurrentPositon != this.swimLane.size()-1)
            {
                this.swimLane.get(CurrentPositon+1).getCards().add(this.swimLane.get(CurrentPositon).getCards().remove(position));
                initializeListViewForTask(CurrentPositon+1);
                CurrentPositon++;
                apiForEditingCardPosition();
                changeButtonColorToActive();            }
        }
    }

    public void pickImageFromGallery(View view) //called from layout.xml
    {
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Setting the type as image/*.
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void convertImageToBase64(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        this.image = "data:image/png;base64," + encoded;
    }

    public void update(View view) //called from layout
    {
        progressDialog.setMessage("Updating the board....");
        progressDialog.show();
        try {
            // Building POST body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name.getText().toString());
            jsonBody.put("color", this.boardObject.getString("color"));
            jsonBody.put("image", image);
            jsonBody.put("description", description.getText().toString());
            jsonBody.put("board_id", this.boardObject.getString("board_id"));

            // Performing HTTP POST API call
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                    Constants.BACKEND_URL + "/editBoard", jsonBody,
                    new Response.Listener<JSONObject>() {
                        // On success this will be triggered
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.cancel();
                            Toast.makeText(TasksBenchActivity.this,
                                    "Board Updated successfully", Toast.LENGTH_LONG).show();
                            alertDialogForEditBoard.cancel();
                        }
                    }, new Response.ErrorListener() {
                // On failure this will be triggered
                @Override
                public void onErrorResponse(VolleyError error) {
                    try{
                        // Showing a toast message with necessary errorMessage
                        renderError(new String(error.networkResponse.data, "UTF-8"));
                    }catch (Exception exception){
                        Log.d("Exception", "Exception has occuered while" +
                                " parsing the error response received from editBoard API");
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
            // Adding the Async call to volley's queue
            queue.add(postRequest);
        } catch (JSONException e) {
            Log.d("JSONException", "Not able to build POST body for Edit board API");
        }
    }
}
