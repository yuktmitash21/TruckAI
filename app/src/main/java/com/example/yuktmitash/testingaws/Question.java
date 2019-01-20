package com.example.yuktmitash.testingaws;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Question extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private Button cancelOffer;

    private DatabaseReference reference;
    private String currentUserID;
    private String carId;
    private String ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        editText = findViewById(R.id.editText);
        button = findViewById(R.id.offerButton);
        reference = FirebaseDatabase.getInstance().getReference();
        currentUserID = getIntent().getStringExtra("userid");
        carId = getIntent().getStringExtra("hash");
        ownerId = getIntent().getStringExtra("ownerid");
        cancelOffer = findViewById(R.id.cancelOffer);

        cancelOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), userInterface.class));
            }
        });




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String k = editText.getText().toString();
                if (k.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                } else {
                    final int x = Integer.parseInt(k);
                    reference.child("offers").child("counters").child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                reference.child("offers").child("counters").child(ownerId).setValue(0);
                                Offer offer = new Offer(x, currentUserID, ownerId, carId);
                                reference.child("offers").child(ownerId).child("" + 0).setValue(offer);
                            } else {
                                long xx = (long) dataSnapshot.getValue();
                                reference.child("offers").child("counters").child(ownerId).setValue(xx + 1);
                                Offer offer = new Offer(x, currentUserID, ownerId, carId);
                                reference.child("offers").child(ownerId).child("" + (xx + 1)).setValue(offer);


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                Toast.makeText(getApplicationContext(), "Offer made!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), userInterface.class));
            }
        });

    }
}
