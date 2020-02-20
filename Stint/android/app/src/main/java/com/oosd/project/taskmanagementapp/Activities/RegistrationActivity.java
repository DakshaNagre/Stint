package com.oosd.project.taskmanagementapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

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
import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    // Variables mapping UI
    private EditText email;
    private EditText name;
    private EditText phone;
    private EditText password;
    private EditText confirmPassword;

    // Volley request queue
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Identifying UI elements
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);

        // Creating request queue
        queue = Volley.newRequestQueue(this);
    }

    //Method triggered from UI onlick on register button
    public void register(View view){
        // Checking if the password and confirm password is same or not
        if(password.getText().toString().equals(confirmPassword.getText().toString())){
            this.registerUser(name.getText().toString(), email.getText().toString(),
                    phone.getText().toString(), password.getText().toString());
        }else{
            // Toasting approprate message if the password and confirm password does not match
            Toast.makeText(RegistrationActivity.this, "Password did not match",
                    Toast.LENGTH_LONG).show();
        }

    }

    // Method to register a user
    private void registerUser(String name, String email, String phone, String password){
        try {
            // Building POST body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("name", name);
            jsonBody.put("phone", phone);
            jsonBody.put("password", password);
            jsonBody.put("confirmPassword", password);

            // Performing HTTP POST API call
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                    Constants.BACKEND_URL + "/registerUser", jsonBody,
                    new Response.Listener<JSONObject>() {
                        // On success this will be triggered
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(RegistrationActivity.this,
                                    "Registration Successful", Toast.LENGTH_LONG).show();;
                            // Navigating to login screen
                            onBackPressed();
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

    // Parsing the error response and rendering appropriate message to user
    private void renderError(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            Toast.makeText(RegistrationActivity.this,
                    jsonObject.get("errorMessage").toString(), Toast.LENGTH_LONG).show();
        }catch (JSONException exception){
            Log.d("JSONException:", exception.getLocalizedMessage());
        }
    }

}
