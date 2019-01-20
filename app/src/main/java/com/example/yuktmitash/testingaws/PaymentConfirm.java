package com.example.yuktmitash.testingaws;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stripe.android.view.CardInputWidget;

public class PaymentConfirm extends AppCompatActivity {
    private CardInputWidget mCardInputWidget;
    private com.stripe.android.model.Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirm);

        mCardInputWidget = findViewById(R.id.card_input_widget);
        card = mCardInputWidget.getCard();


    }
}
