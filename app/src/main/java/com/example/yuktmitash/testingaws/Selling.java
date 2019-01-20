package com.example.yuktmitash.testingaws;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.ModelVersion;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import clarifai2.dto.prediction.Prediction;
import clarifai2.exception.ClarifaiException;
import okhttp3.OkHttpClient;


//replace red with old and white with new
public class Selling extends AppCompatActivity {

    private EditText make;
    private EditText model;
    private EditText year;
    private CardView addPic;
    private TextView pictureOrNot;
    private CardView cancel;
    private CardView create;
    private Car car;
    private int hash;

    private String makeText;
    private String modelText;
    private String yearText;
    private int yearInt;

    private long decider;

    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    private boolean picture;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESUlT_LOAD_IMAGE = 2;
    private DatabaseReference ref;

    private byte[] myPic;

    private double lattitude;
    private double longitude;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private ClarifaiClient clarifaiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling);

        picture = getIntent().getBooleanExtra("hasPic", false);
        String theMake = getIntent().getStringExtra("make");
        String theModel = getIntent().getStringExtra("model");
        int yearOfCar = getIntent().getIntExtra("Year", 0);
        if (picture) {
            myPic = getIntent().getByteArrayExtra("pics");
        }
        progressDialog = new ProgressDialog(Selling.this);
        clarifaiClient = new ClarifaiBuilder("6cc5f72d934548b6a6e0f65b32c9d4b7")
                .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
                .buildSync();

        make = findViewById(R.id.make);
        model = findViewById(R.id.model);
        year = findViewById(R.id.yearOfCar);
        addPic = findViewById(R.id.addPicture);
        pictureOrNot = findViewById(R.id.pictureButtonText);
        cancel = findViewById(R.id.cancel);
        create = findViewById(R.id.create);
        storageReference = FirebaseStorage.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference();

        ref.child("universalCounter").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    decider = (long) dataSnapshot.getValue();
                    ref.child("universalCounter").setValue(decider + 1);
                } else {
                    ref.child("universalCounter").setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lattitude = location.getLatitude();
                longitude = location.getLongitude();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Selling.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Selling.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                        10);
            } else {
                locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
                locationManager.removeUpdates(locationListener);
                if (location == null) {
                    Log.e("Maps", "Houston, we have a problem.");
                } else {
                    lattitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }


        if (picture) {
            pictureOrNot.setText("Change Picture");
        }
        if (yearOfCar != 0) {
            make.setText(theMake);
            model.setText(theModel);
            year.setText(yearOfCar + "");
        }


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), userInterface.class));
            }
        });

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeText = make.getText().toString();
                modelText = model.getText().toString();
                yearText = year.getText().toString();
                if (!yearText.equals("")) {
                    yearInt = Integer.parseInt(yearText);
                }

                if (makeText.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a make", Toast.LENGTH_SHORT).show();
                } else if (modelText.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a model", Toast.LENGTH_SHORT).show();
                } else if (yearText.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a year", Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(Selling.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(Selling.this);
                    }

                    builder.setTitle("Where do you want the picture from?")
                            .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(picIntent, CAMERA_REQUEST_CODE);


                                }
                            })
                            .setNegativeButton("Library", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(i, RESUlT_LOAD_IMAGE);


                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPic != null) {
                    Intent checkingIfOk = new Intent(getApplicationContext(), FinalCheck.class);
                    Car car = new Car(makeText, modelText, yearInt, lattitude, longitude, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    StorageReference filepath = storageReference.child("" + car.hashCode()+".jpg");
                    filepath.putBytes(myPic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Car uploaded!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), userInterface.class);
                            //add way to tell if user is selling car
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    ref.child("cars").child(car.hashCode() + "").setValue(car);
                    if (decider % 2 == 0) {
                        ref.child("colors").child(car.hashCode() + "").setValue("red");
                    } else {
                        ref.child("colors").child(car.hashCode() + "").setValue("white");
                    }

                    checkingIfOk.putExtra("carHash", car.hashCode() + "");
                    checkingIfOk.putExtra("make", car.getMake());
                    checkingIfOk.putExtra("model", car.getMode());
                    checkingIfOk.putExtra("year", car.getYear());
                    checkingIfOk.putExtra("pic", myPic );
                    startActivity(checkingIfOk);
                } else {
                    Toast.makeText(getApplicationContext(), "Please choose an image", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK)) {
            progressDialog.setMessage("Uploading image...");
            progressDialog.show();
            pictureOrNot.setText("Change Picture");


            final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            myPic = baos.toByteArray();

            runTask task = new runTask();
            task.execute();

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Picture Set", Toast.LENGTH_SHORT).show();

        } else if (requestCode == RESUlT_LOAD_IMAGE && resultCode == RESULT_OK) {
            try {
                progressDialog.setMessage("Uploading image...");
                progressDialog.show();
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                myPic = baos.toByteArray();

                runTask task = new runTask();
                task.execute();


                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Picture Set", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Selling.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }


        }
    }


    class runTask extends AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>> {


        @Override
        protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... voids) {
            ClarifaiClient client = new ClarifaiBuilder("6cc5f72d934548b6a6e0f65b32c9d4b7")
                    .buildSync();
            ClarifaiResponse<List<ClarifaiOutput<Concept>>> response = client.getDefaultModels().generalModel()
                    .predict()
                    .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(myPic)))
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
                   myPic = null;

               }


            }
        }
    }
}
