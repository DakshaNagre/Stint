package com.oosd.project.taskmanagementapp.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import com.google.android.material.textfield.TextInputLayout;
import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.adapters.BoardsAdapter;
import com.oosd.project.taskmanagementapp.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Dashboard extends Fragment {
    private RequestQueue queue;
    private AlertDialog.Builder builder;
    private GridView boardsGrid;
    private LayoutInflater li;
    private View viewForAddNewBoard;
    private AlertDialog.Builder dialogToAddNewBoard;
    private AlertDialog alertDialog;
    private TextView boardName;
    private MaterialButton cancel, addBoard;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_boards, container, false);

        boardsGrid = root.findViewById(R.id.boardsGrid);
        queue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getContext());

        this.builder = new AlertDialog.Builder(getActivity());

        // Floating button to add board
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show popup to add a board
                showAddBoardPopUp();
            }
        });

        // Getting boards and rendering in UI
        this.getBoards();

        return root;
    }
    private void showAddBoardPopUp(){

        li = LayoutInflater.from(getContext());
        viewForAddNewBoard= li.inflate(R.layout.add_new_board, null);
        dialogToAddNewBoard = new AlertDialog.Builder(getContext(),R.style.MyDialogTheme);
        dialogToAddNewBoard.setView(viewForAddNewBoard);
        alertDialog = dialogToAddNewBoard.create();
        boardName = viewForAddNewBoard.findViewById(R.id.boardName);
        boardName.setText("");

        addBoard = viewForAddNewBoard.findViewById(R.id.addBoard);
        cancel = viewForAddNewBoard.findViewById(R.id.cancel);
        alertDialog.show();
        addBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the add card api here
                alertDialog.cancel();
                progressDialog.setTitle("Loading...");
                progressDialog.setMessage("Creating Board...");
                progressDialog.show();
                createBoard(boardName.getText().toString());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        dialogToAddNewBoard.setCancelable(true);
    }

    // Method to create board
    public void createBoard(String name){
        try {

            // Building POST body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            Random random = new Random();
            int randomColorIndex = random.nextInt(Constants.BOARD_COLORS.length);
            jsonBody.put("color", Constants.BOARD_COLORS[randomColorIndex]);

            // Performing HTTP POST API call
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                    Constants.BACKEND_URL + "/createBoard", jsonBody,
                    new Response.Listener<JSONObject>() {
                        // On success this will be triggered
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getActivity(),
                                    "Board Created successfully", Toast.LENGTH_LONG).show();
                            getBoards();
                            progressDialog.cancel();
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
            // Adding the Async call to volley's queue
            queue.add(postRequest);
        } catch (JSONException e) {
            Log.d("JSONException", "Not able to build POST body for login API");
        }
    }

    // Method to get boards from backend
    private JSONArray getBoards(){
        // Preparing request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET,
                Constants.BACKEND_URL + "/getBoards", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // displaying boards
                        boardsGrid.setAdapter(new BoardsAdapter(getActivity(), response));
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
                                    " parsing the error response received from /getBoards API");
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

        // adding it to the RequestQueue
        queue.add(getRequest);
        return null;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        getBoards();
//    }

    // Getting token from shared preferences
    private String getTokenFromSharedPReferences(){
        SharedPreferences pref = getContext().getSharedPreferences("user", 0);
        return pref.getString("token", null);
    }

    // Parsing the error response and rendering appropriate message to user
    private void renderError(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            Toast.makeText(getActivity(),
                    jsonObject.get("errorMessage").toString(), Toast.LENGTH_LONG).show();
        }catch (JSONException exception){
            Log.d("JSONException:", exception.getLocalizedMessage());
        }
    }

}