package com.oosd.project.taskmanagementapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.oosd.project.taskmanagementapp.third.party.services.FCMService;
import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    // Variables mapping UI
    private EditText email;
    private EditText password;

    private String deviceToken;
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        deviceToken = "";

        // Identifying UI elements
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        // Creating request queue
        queue = Volley.newRequestQueue(this);
        getFcmDeviceToken();
        autoLogin();
    }

    // Method to navigate to Registration screen
    public void goToRegistrationScreen(View view){
        // Navigating to Registration activity
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    // Method called by Login button present in UI
    public void login(View view){
        this.login(email.getText().toString(), password.getText().toString());
    }

    public void autoLogin(){
        SharedPreferences pref = this.getSharedPreferences("user", 0);
        String email = pref.getString("email","");
        String password = pref.getString("password","");
        this.email.setText(email);
        this.password.setText(password);
        if(email.length() > 0 && password.length() > 0) {
            //perfrom login to get token
            //update the token in shared preference
            login(email, password);
        }
    }

    // Method to communicate with backend and login the user
    private void login(final String email, final String password){
        try {
            // Building POST body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("deviceToken", deviceToken);

            // Performing HTTP POST API call
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                    Constants.BACKEND_URL + "/login", jsonBody,
                    new Response.Listener<JSONObject>() {
                        // On success this will be triggered
                        @Override
                        public void onResponse(JSONObject response) {
                            // Storing the user data in shared preferences
                            storeInSharedPreferences(response);
                            getFcmDeviceToken();
                            // Navigating to boards activity
                            Intent intent = new Intent(LoginActivity.this, NavDrawerActivity.class);
                            startActivity(intent);
                            finish();
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
                    });
            // Adding the Async call to volley's queue
            queue.add(postRequest);
        } catch (JSONException e) {
            Log.d("JSONException", "Not able to build POST body for login API");
        }
    }

    // Getting FCM device token
    public void getFcmDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Registration: ", "Failed to instantiate firebase ", task.getException());
                            return;
                        }

                        String token = task.getResult().getToken();
                        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fcm_token", token).apply();
                        deviceToken = FCMService.getToken(getApplicationContext());
                    }
                });
    }

    // Storing the user data in shared preferences
    private void storeInSharedPreferences(JSONObject jsonObject){
        SharedPreferences userData = getApplicationContext().getSharedPreferences("user", 0);
        SharedPreferences.Editor editor = userData.edit();
        try{
            editor.putString("email", jsonObject.getString("email"));
            editor.putString("name", jsonObject.getString("name"));
            editor.putString("password", password.getText().toString());
            editor.putString("phone", jsonObject.getString("phone"));
            editor.putString("token", jsonObject.getString("token"));
            editor.putString("user_id", jsonObject.getString("user_id"));
            editor.putString("image", jsonObject.getString("image"));
            editor.putString("emailNotification", jsonObject.getString("emailNotification"));
            editor.putString("pushNotification", jsonObject.getString("pushNotification"));
            editor.commit();
        }catch (JSONException exception){
            Log.d("JSONException:", exception.getLocalizedMessage());
        }
    }

    // Parsing the error response and rendering appropriate message to user
    private void renderError(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            Toast.makeText(LoginActivity.this,
                    jsonObject.get("errorMessage").toString(), Toast.LENGTH_LONG).show();
        }catch (JSONException exception){
            Log.d("JSONException:", exception.getLocalizedMessage());
        }
    }

}
