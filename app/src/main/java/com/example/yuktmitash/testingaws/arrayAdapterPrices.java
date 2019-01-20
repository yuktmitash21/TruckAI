package com.example.yuktmitash.testingaws;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yuktmitash.testingaws.Card;
import com.example.yuktmitash.testingaws.Offer;
import com.example.yuktmitash.testingaws.OfferCard;

import java.util.List;

public class arrayAdapterPrices extends ArrayAdapter<OfferCard> {
    private Context context;

    public arrayAdapterPrices(Context context, int resourceId, List<OfferCard> items) {

        super(context, resourceId, items);
        this.context = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
       OfferCard offerCard = getItem(position);


        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.RED);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }


        TextView name = convertView.findViewById(R.id.nameFromItem);
        ImageView image = convertView.findViewById(R.id.imageFromItem);
        TextView age = convertView.findViewById(R.id.ageFromItem);

        name.setText("From " + offerCard.getUsername());
        age.setText("$" + offerCard.getOffer().getOfferNumber());
        image.setImageBitmap(offerCard.getBitmap());




        return convertView;

    }

}