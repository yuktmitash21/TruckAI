package com.example.yuktmitash.testingaws;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<Card> {

    Context context;

    public arrayAdapter(Context context, int resourceId, List<Card> items) {

        super(context, resourceId, items);
        this.context = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Card card_item = getItem(position);

       // if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);

        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.RED);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }


        TextView name = convertView.findViewById(R.id.nameFromItem);
        ImageView image = convertView.findViewById(R.id.imageFromItem);
        TextView age = convertView.findViewById(R.id.ageFromItem);

        name.setText(card_item.getMake() + " " + card_item.getModel());
        age.setText(card_item.getAge() + "");
        image.setImageBitmap(card_item.getBitmap());




        return convertView;

    }
}