package com.oosd.project.taskmanagementapp.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.oosd.project.taskmanagementapp.adapters.BoardsAdapter;
import com.oosd.project.taskmanagementapp.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BoardController {
    private Context mContext;
    public BoardController(Context mContext)
    {
        this.mContext = mContext;
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

}
