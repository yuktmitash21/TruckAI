package com.example.yuktmitash.testingaws;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.amazonaws.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.ModelVersion;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.model.output_info.ConceptOutputInfo;
import clarifai2.dto.prediction.Concept;

public class TinderCar extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private DatabaseReference reference;
    private StorageReference storageReference;


    private String modelId;
    private Map<String, Object> map;
    private Set<String> ids;
    private HashMap<Card, String> cardStringHashMap = new HashMap<>();

    final HashMap<String, Bitmap> pics = new HashMap<>();
    private ArrayList<Bitmap> list = new ArrayList<>();
    private ArrayList<String> myIdsArray = new ArrayList<>();
    private ArrayList<Card> cards = new ArrayList<>();

    private com.example.yuktmitash.testingaws.arrayAdapter arrayAdapter;
    SwipeFlingAdapterView flingContainer;

    private ClarifaiClient clarifaiClient;
    private byte[] currentByte;

    private int redCars;
    private int whiteCars;
    private int total;

    private ArrayList<byte[]> positiveTrainingData;
    private ArrayList<String> trucks;

    private boolean redDominant;
    private AlertDialog.Builder builder;
    private int difference;


    private boolean confirmed = false;

    private boolean clearedRight = false;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinder_car);
        progressDialog = new ProgressDialog(this);
       // progressDialog.setMessage("Loading Cars");
//        progressDialog.show();
        flingContainer = findViewById(R.id.frame);
        clarifaiClient = new ClarifaiBuilder("6cc5f72d934548b6a6e0f65b32c9d4b7").buildSync();
        positiveTrainingData = new ArrayList<>();
        trucks = new ArrayList<>();



        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        arrayAdapter = new com.example.yuktmitash.testingaws.arrayAdapter(getApplicationContext(), R.layout.item, cards);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                if (cards.size() > 0) {
                    cards.remove(0);
                }
                arrayAdapter.notifyDataSetChanged();
                if (redCars >= whiteCars) {
                    redDominant = true;
                    difference = redCars - whiteCars;

                } else {
                    redDominant = false;
                    difference = redCars - whiteCars;
                }
                if (total >= 3 && !confirmed && !cards.isEmpty()) {

                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                final Card card = (Card) dataObject;
                total++;
                if (cardStringHashMap.get(card).equals("red")) {
                    redCars--;
                    whiteCars++;
                } else {
                    whiteCars--;
                    redCars++;

                }

                if (redCars >= whiteCars) {
                    redDominant = true;
                    difference = redCars - whiteCars;

                } else {
                    redDominant = false;
                    difference = whiteCars - redCars;
                }

                if (total >= 3 && difference >= 5 && !cards.isEmpty() && !clearedRight) {
                    if (cardStringHashMap.get(cards.get(0)).equals("red") && redDominant ||
                            cardStringHashMap.get(cards.get(0)).equals("white") && !redDominant) {
                        dialog = builder.setTitle("Would you swipe Right?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteNonConformingCars(redDominant);
                                clearedRight = true;


                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
                    } else if (cardStringHashMap.get(cards.get(0)).equals("white") && redDominant ||
                            cardStringHashMap.get(cards.get(0)).equals("red") && !redDominant) {
                        dialog =
                        builder.setTitle("Would you swipe Left?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteNonConformingCars(redDominant);
                                clearedRight = true;

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();




                    }


                }












            }

            @Override
            public void onRightCardExit(Object dataObject) {
                total++;
                Log.d("NEWTOTAL", total+"");
                final Card card = (Card) dataObject;

                if (cardStringHashMap.get(card).equals("red")) {
                    redCars++;
                    whiteCars--;
                } else {
                    whiteCars++;
                    redCars--;

                }




                String title = "Would you like to make an offer on that " + card.getAge() + " " + card.getMake() + " " + card.getModel();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(TinderCar.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(TinderCar.this);
                }

                builder.setTitle(title).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), Question.class);
                        intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        intent.putExtra("hash", card.getHashcode());
                        intent.putExtra("ownerid", card.getOwnerFireId());
                        startActivity(intent);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (redCars >= whiteCars) {
                            redDominant = true;
                            difference = redCars - whiteCars;

                        } else {
                            redDominant = false;
                            difference = whiteCars - redCars;
                        }
                        Log.d("DIFFERENVE", "" + difference);
                        if (total >= 3 && difference >= 5 && !cards.isEmpty() && !clearedRight) {
                            if (cardStringHashMap.get(cards.get(0)).equals("red") && redDominant ||
                                    cardStringHashMap.get(cards.get(0)).equals("white") && !redDominant) {
                                dialog =
                                builder.setTitle("Would you swipe right?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteNonConformingCars(redDominant);
                                        clearedRight = true;

                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert).show();
                            } else if (cardStringHashMap.get(cards.get(0)).equals("white") && redDominant ||
                                    cardStringHashMap.get(cards.get(0)).equals("red") && !redDominant) {
                                dialog =
                                builder.setTitle("Would you swipe left?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteNonConformingCars(redDominant);
                                        clearedRight = true;

                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(redDominant) {
                                            redCars--;
                                            whiteCars++;
                                        } else {
                                            redCars++;
                                            whiteCars--;
                                        }

                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert).show();

                            }


                        }


                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();

                Bitmap bmp = card.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                currentByte = byteArray;
                positiveTrainingData.add(byteArray);
                //runTask runtask = new runTask();
               // runtask.execute();










            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }

        });

        reference.child("cars").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              // final HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    final Car car = dataSnapshot1.getValue(Car.class);
                    final String key = dataSnapshot1.getKey();
                    storageReference.child(key + ".jpg").getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            final Card card = new Card(bm, car.getMake(), car.getMode(), car.getYear(), car.hashCode() + "", car.getFireid());
                            cards.add(card);
                            arrayAdapter.notifyDataSetChanged();
                            reference.child("colors").child(card.getHashcode()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Card myCard = card;
                                    String hash = (String) dataSnapshot.getValue();
                                    cardStringHashMap.put(myCard, hash);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }



                    });


                }

        }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }

    private void deleteNonConformingCars(boolean redDominant1) {
        ArrayList<Card> newCards = new ArrayList<>();
        if(redDominant1) {
            for (Card card1 : cards) {
                if (cardStringHashMap.get(card1).equals("red")) {
                    newCards.add(card1);
                }
            }
        }
        else {
            for (Card card1: cards) {
                if (cardStringHashMap.get(card1).equals("white")) {
                    newCards.add(card1);
                }
            }
        }
        cards = newCards;
        arrayAdapter = new com.example.yuktmitash.testingaws.arrayAdapter(getApplicationContext(), R.layout.item, cards);
        flingContainer.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    class runTask extends AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>> {


        @Override
        protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... voids) {
            ClarifaiClient client = new ClarifaiBuilder("6cc5f72d934548b6a6e0f65b32c9d4b7")
                    .buildSync();
            ClarifaiResponse<List<ClarifaiOutput<Concept>>> response = client.getDefaultModels().generalModel()
                    .predict()
                    .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(currentByte)))
                    .executeSync();

            return response;
        }

        @Override
        protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> listClarifaiResponse) {
            if (!listClarifaiResponse.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Problem", Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<String> trucks = new ArrayList<>();
                List<ClarifaiOutput<Concept>> predictions = listClarifaiResponse.get();
                for (ClarifaiOutput<Concept> conceptClarifaiOutput : predictions) {
                    for( Concept concepts: conceptClarifaiOutput.data()) {
                        trucks.add(concepts.name());

                    }
                }
                if (trucks.contains("truck")) {
                    Toast.makeText(getApplicationContext(), "This image contains a truck", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "This image does not contain a truck. Choose another", Toast.LENGTH_SHORT).show();


                }


            }
        }
    }

    class PredictTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
          //  ModelVersion modelVersion = clarifaiClient.getM
            return null;
        }
    }


    protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> listClarifaiResponse) {
        if (!listClarifaiResponse.isSuccessful()) {
            Toast.makeText(getApplicationContext(), "Problem", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<String> trucks = new ArrayList<>();
            List<ClarifaiOutput<Concept>> predictions = listClarifaiResponse.get();
            for (ClarifaiOutput<Concept> conceptClarifaiOutput : predictions) {
                for( Concept concepts: conceptClarifaiOutput.data()) {
                    trucks.add(concepts.name());

                }
            }
            if (trucks.contains("truck")) {
                Toast.makeText(getApplicationContext(), "This image contains a truck", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "This image does not contain a truck. Choose another", Toast.LENGTH_SHORT).show();


            }


        }
    }

    class predictTask extends AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>> {

        @Override
        protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... voids) {
            clarifaiClient.predict(modelId);
            return null;
        }
    }
}


