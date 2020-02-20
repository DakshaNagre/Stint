package com.oosd.project.taskmanagementapp.util;

public class Constants {
    public static final String BACKEND_URL = "http://tm-stint-api.herokuapp.com";
    public static final String[] BOARD_COLORS = {"#FAD0C9FF", "#89ABE3FF", "#603F83FF", "#2BAE66FF"};
    public static final String S3_IMAGE_BUCKET = "https://task-management-app.s3.amazonaws.com";
    public static final String[] SPINNER_ITEMS = {"None", "At time of Due Date","5 Minutes Before", "10 Minutes Before", "15 Minutes Before", "1 Hour Before", "2 Hour Before" , "1 Day Before" , "2 Day Before"};
    public static final int[] SPINNER_ITEMS_VALUES = {-1,0,5,10,15,60,120,1440,2880};
}
