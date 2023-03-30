package com.devdroiddev.carparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devdroiddev.carparking.databinding.ActivityMainBinding;
import com.devdroiddev.carparking.databinding.ActivityParkCarBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.UUID;

public class ParkCarActivity extends AppCompatActivity {


    EditText driverName, driverNumber, numberPlate, vehicleType, amount;
    Button parkCarButton;
    ActivityParkCarBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    ProgressDialog dialog;
    StorageReference reference = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        driverName = binding.driverName;
        driverNumber = binding.driverNumber;
        numberPlate = binding.numberPlate;
        vehicleType = binding.vehicleType;
        amount = binding.amount;
        parkCarButton = binding.parkCarButton;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);

        parkCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the data form the Edit Text
                String driverName = binding.driverName.getText().toString();
                String driverNumber = binding.driverNumber.getText().toString();
                String numberPlate = binding.numberPlate.getText().toString();
                String vehicleType = binding.vehicleType.getText().toString();
                String amount = binding.amount.getText().toString();
                Long time = Calendar.getInstance().getTimeInMillis();
                String id = UUID.randomUUID().toString();

                // Create the object of the ParkCarModel Class
                ParkCarModel model = new ParkCarModel();
                model.setId(id);
                model.setTime(time);
                model.setDriverName(driverName);
                model.setDriverNumber(driverNumber);
                model.setNumberPlate(numberPlate);
                model.setVehicleType(vehicleType);
                model.setParkFee(amount);
                model.setUserId(auth.getCurrentUser().getUid());
                model.setStatus("Parked");

                dialog.setTitle("Uploading");
                dialog.setMessage("Data move to Database");
                dialog.setIcon(R.drawable.baseline_upload_24);
                dialog.show();
                // Create the Document of the data in Firestore Database
                firestore.collection("Parking Info")
                        .document(id)
                        .set(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.cancel();
                                startActivity(new Intent(ParkCarActivity.this,MainActivity.class));
                                finishAffinity();
                                Toast.makeText(getApplicationContext(),"Document Created", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

    }

}