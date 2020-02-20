package com.oosd.project.taskmanagementapp.Fragments;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.oosd.project.taskmanagementapp.R;


public class CalendarFragment extends Fragment {


    private View root;
    private WebView calendarwebview;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.calendar_layout, container, false);
        calendarwebview = root.findViewById(R.id.webviewCalendar);
        calendarwebview.setWebViewClient(new WebViewClient());
        WebSettings webSettings = calendarwebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String webUrl = "http://tm-stint-webapp.herokuapp.com/calendar?token=" + getTokenFromSharedPReferences();
        calendarwebview.loadUrl(webUrl);

        return root;

    }
    private String getTokenFromSharedPReferences(){
        SharedPreferences pref = getContext().getSharedPreferences("user", 0);
        return pref.getString("token", null);
    }
}