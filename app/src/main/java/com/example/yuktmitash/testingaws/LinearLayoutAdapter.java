package com.example.yuktmitash.testingaws;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yuktmitash.testingaws.R;
import com.example.yuktmitash.testingaws.User;

import java.util.ArrayList;
import java.util.HashMap;

public class LinearLayoutAdapter extends ArrayAdapter<User> {

    private ArrayList<User> dataSet;
    private HashMap<User, Bitmap> pics;
    Context mContext;



    public LinearLayoutAdapter(ArrayList<User> data, HashMap<User, Bitmap> pics, Context context) {
        super(context, R.layout.user_item, data);
        this.dataSet = data;
        this.pics = pics;
        this.mContext=context;

    }


    public View getView(int position, View convertView, ViewGroup parent) {
        User user = dataSet.get(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);

        ImageView imageView = convertView.findViewById(R.id.imageView3);
        TextView textView = convertView.findViewById(R.id.username);

        String text = user.getUsername();
        textView.setText(text);
        imageView.setImageBitmap(pics.get(user));


        return convertView;
    }
}
