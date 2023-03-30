package com.devdroiddev.carparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devdroiddev.carparking.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CarAdapterInterface{

    ActivityMainBinding binding;
    RelativeLayout parkVehicleTxt;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    CarAdapter carAdapter;
    int parked = 0, paid = 0;
    List<ParkCarModel> paidModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        carAdapter = new CarAdapter(this,this);
        paidModels = new ArrayList<>();


        parkVehicleTxt = binding.parkAVehicleButton;

        binding.carRecycler.setAdapter(carAdapter);
        binding.carRecycler.setLayoutManager(new LinearLayoutManager(this));
        loadData();

        parkVehicleTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ParkCarActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Create a Progress Dialogue
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Wait..");
        dialog.setIcon(R.drawable.time);
        dialog.setMessage("Processing");
        if (auth.getCurrentUser() == null)
        {
            auth.signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            dialog.cancel();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(),""+ e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    // Method to get the data from firebase Database
    private void loadData() {
        firestore.collection("Parking Info")
                .whereEqualTo("userId",auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsnapList = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot ds: dsnapList) {
                            ParkCarModel model = ds.toObject(ParkCarModel.class);
                            if (model.getStatus().equals("Parked")) {
                                String fee = model.getParkFee();
                                parked = parked+Integer.parseInt(fee);
                            }
                            if (model.getStatus().equals("Paid")) {
                                String fee = model.getParkFee();
                                paid = paid+Integer.parseInt(fee);
                                paidModels.add(model);
                            }
                            carAdapter.add(model);
                        }
                        binding.paid.setText(paid+"");
                        binding.pending.setText(parked+"");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void OnLongClick(int position, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Status")
                .setPositiveButton("Paid", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changeStatus("Paid", id);
                    }
                })
                .setNegativeButton("Cancelled", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changeStatus("Cancelled", id);
                    }
                });
        builder.show();
    }

    private void changeStatus(String status, String id) {
        firestore.collection("Parking Info")
                .document(id)
                .update("Status",status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this,"Status Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}