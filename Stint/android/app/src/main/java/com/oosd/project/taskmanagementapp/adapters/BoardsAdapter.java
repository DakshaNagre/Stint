package com.oosd.project.taskmanagementapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.Activities.TasksBenchActivity;
import com.oosd.project.taskmanagementapp.models.Board;
import com.oosd.project.taskmanagementapp.util.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Adapter to create cards to render in boards screen
public class BoardsAdapter extends BaseAdapter{

    private Context context;
    private JSONArray boards;
    private static LayoutInflater inflater=null;

    public BoardsAdapter(Context dashboard, JSONArray boards) {

        this.context = dashboard;
        this.boards = boards;
        this.inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return boards.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Board boardObject = new Board();
        final View rowView = this.inflater.inflate(R.layout.board_card, null);
        try {
            JSONObject board = this.boards.getJSONObject(position);

            // Setting the name on the board card
            boardObject.setName((TextView) rowView.findViewById(R.id.boardName));
            boardObject.getName().setText(board.getString("name"));
            boardObject.getName().setBackgroundColor(Color.parseColor("#fefefe"));
            // Setting the background color of the card
            String color = board.getString("color");
            if(color.equals("null")){
                rowView.setBackgroundColor(Color.parseColor("#f2f2f2"));
            }else{
                rowView.setBackgroundColor(Color.parseColor(color));
            }

            if(board.getBoolean("image")){
                boardObject.setImage((ImageView) rowView.findViewById(R.id.boardBackgroundImage));
                Picasso.get()
                        .load(Constants.S3_IMAGE_BUCKET + "/" + board.getString("board_id"))
                        .fit().centerCrop()
                        .into(boardObject.getImage());
            }
        }catch (JSONException e){
            Log.d("JSON Exception", "Exception occured while reading boards array");
        }

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(context, TasksBenchActivity.class);
                    intent.putExtra("boardObject", boards.getJSONObject(position).toString());

                    context.startActivity(intent);
                }catch (JSONException exception){
                    Log.d("JSONException", "Exception has occurred while tapping on board");
                }
            }
        });

        return rowView;
    }

}

