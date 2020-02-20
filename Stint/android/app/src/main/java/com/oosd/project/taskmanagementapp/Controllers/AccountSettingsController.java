package com.oosd.project.taskmanagementapp.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.oosd.project.taskmanagementapp.Fragments.AccountSettings;
import com.oosd.project.taskmanagementapp.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AccountSettingsController {

    private RequestQueue queue;
    private Context mContext;
    public AccountSettingsController(Context mContext)
    {
        this.mContext = mContext;
        this.queue = Volley.newRequestQueue(mContext);
    }

    public List<Object> getAccountSettings() {
            List<Object> returnObjects = new ArrayList<>();
            SharedPreferences pref = mContext.getSharedPreferences("user", 0);
            returnObjects.add(pref.getString("name", null));
            returnObjects.add(pref.getString("email", null));
            returnObjects.add(pref.getString("emailNotification", null));
            returnObjects.add(pref.getString("pushNotification", null));
            returnObjects.add(pref.getString("image", null));
            returnObjects.add(pref.getString("user_id", null));

            return returnObjects;
    }

    public void updateData(org.json.JSONObject jsonBody, final ProgressDialog progressDialog)
    {
        try {
            // Performing HTTP POST API call
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                    Constants.BACKEND_URL + "/editUser", jsonBody,
                    new Response.Listener<JSONObject>() {
                        // On success this will be triggered
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(mContext,
                                    "Account edited successfully", Toast.LENGTH_LONG).show();
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
        } catch (Exception e) {
           e.printStackTrace();
        }
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
}
