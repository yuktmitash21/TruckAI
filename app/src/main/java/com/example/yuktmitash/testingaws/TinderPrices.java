package com.example.yuktmitash.testingaws;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class TinderPrices extends AppCompatActivity {
    private SwipeFlingAdapterView flingContainer;
    private ArrayList<OfferCard> offerCards;
    private arrayAdapterPrices myAdapter;

    private DatabaseReference ref;
    private StorageReference storageReference;
    private String currentUid;
    private String userNameOfOffer;

    private User currentUser;
    private  User otherUser;

    private long numberOfContacts;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinder_prices);
        flingContainer = findViewById(R.id.frame1);

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        offerCards = new ArrayList<>();
        myAdapter = new arrayAdapterPrices(getApplicationContext(), R.layout.item, offerCards);
        flingContainer.setAdapter(myAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                offerCards.remove(0);
                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLeftCardExit(Object o) {


            }

            @Override
            public void onRightCardExit(Object o) {
                final OfferCard offerCard = (OfferCard)o;
                final String from = offerCard.getOffer().getFrom();
                ref.child("users").child(from).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        otherUser = dataSnapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                String title = "Are you sure you would you like to accept $" + offerCard.getOffer().getOfferNumber() + " for your truck!?";
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(TinderPrices.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(TinderPrices.this);
                }

                builder.setTitle(title).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        ref.child("chats").child(from).child("counter").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) {
                                    //set other students chats
                                    ref.child("chats").child(from).child("counter").setValue(0);
                                    ref.child("chats").child(from).child(0 + "").setValue(currentUser);

                                } else {
                                    numberOfContacts = (long) dataSnapshot.getValue() + 1;
                                    ref.child("chats").child(from).child("counter").setValue(numberOfContacts);
                                    ref.child("chats").child(from).child(numberOfContacts + "").setValue(currentUser);




                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        ref.child("chats").child(currentUid).child("counter").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) {
                                    ref.child("chats").child(currentUid).child("counter").setValue(0);
                                    ref.child("chats").child(currentUid).child(0 + "").setValue(otherUser);

                                } else {

                                    numberOfContacts = (long) dataSnapshot.getValue() + 1;
                                    ref.child("chats").child(currentUid).child("counter").setValue(numberOfContacts);
                                    ref.child("chats").child(currentUid).child(numberOfContacts + "").setValue(otherUser);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(getApplicationContext(), PeopleMessages.class);
                        intent.putExtra("to", from);
                        ChatObj chatObj = new ChatObj(currentUser, "Hi " + otherUser.getUsername() + ". " +
                                "Feel free to pay me $" + offerCard.getOffer().getOfferNumber() + " through the Citi Bank API!");
                        ref.child("messages").child(currentUser.getFireid()).child("counter").setValue(0);
                        ref.child("messages").child("objects").child("0").setValue(chatObj);
                        startActivity(intent);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();


            }

            @Override
            public void onAdapterAboutToEmpty(int i) {

            }

            @Override
            public void onScroll(float v) {

            }
        });




        ref.child("offers").child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dat: dataSnapshot.getChildren()) {
                    final Offer offer = dat.getValue(Offer.class);
                    ref.child("users").child(offer.getFrom()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userNameOfOffer = (String) dataSnapshot.getValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    storageReference.child(offer.getCarId() + ".jpg").getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            OfferCard offerCard = new OfferCard(offer, userNameOfOffer, bm);
                            offerCards.add(offerCard);
                            myAdapter.notifyDataSetChanged();
                            Log.d("NOTIFIED", "YEET");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.child("users").child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser =  dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
