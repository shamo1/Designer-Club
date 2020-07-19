package com.example.designerclub;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.designerclub.Common.common;
import com.example.designerclub.Models.CustomersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class home extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    FirebaseUser user;
    private android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            checkUserExistance();
        }

        //intializing dialog with spotsdialog
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnavbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentlayout, new homeFragment()).commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.home:
                            selectedFragment = new homeFragment();
                            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
                            toolbar.setTitle("Home");
                            break;
                        case R.id.cart:
                            selectedFragment = new cartFragment();
                            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_add_shopping_cart_black_24dp);
                            toolbar.setTitle("My Cart");
                            break;
                        case R.id.search:
                            selectedFragment = new searchFragment();
                            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_search_black_24dp);
                            toolbar.setTitle("Search");
                            break;
                        case R.id.myOrder:
                            selectedFragment = new myOrder_fragment();
                            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_local_shipping_black_24dp);
                            toolbar.setTitle("My Orders");
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentlayout, selectedFragment).commit();
                    return true;
                }
            };

    private void checkUserExistance() {

        FirebaseDatabase.getInstance().getReference(common.USER_REFERENCE).
                child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("userName")) {
                    Toast.makeText(home.this, "Welcome " + dataSnapshot.child("userName").getValue(), Toast.LENGTH_SHORT).show();
                } else {
                    showRegisterDialog(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showRegisterDialog(FirebaseUser user) {

        // this code show registration dialouge and save user info in database
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialog.setTitle("Register");
        alertDialog.setMessage("Please fill information");
        View itemView = LayoutInflater.from(this).inflate(R.layout.register_dialog, null);
        EditText name = (EditText) itemView.findViewById(R.id.edt_name);
        EditText address = (EditText) itemView.findViewById(R.id.edt_address);
        EditText phone = (EditText) itemView.findViewById(R.id.edt_phone);

        phone.setText(user.getPhoneNumber());

        alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());


        alertDialog.setPositiveButton("Register", (dialogInterface, i) -> {
            if (TextUtils.isEmpty(name.getText().toString())) {
                name.setError("Required");
                return;
            }
            if (TextUtils.isEmpty(address.getText().toString())) {
                address.setError("Required");
                return;
            }

            dialog.show();

            CustomersModel userModel = new CustomersModel();
            userModel.setUserName(name.getText().toString());
            userModel.setPhone(phone.getText().toString());
            userModel.setUserAddress(address.getText().toString());

            FirebaseDatabase.getInstance()
                    .getReference(common.USER_REFERENCE).child(user.getUid()).setValue(userModel)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Register Successfully", Toast.LENGTH_SHORT).show();

                }
            });
        });

        alertDialog.setView(itemView);
        androidx.appcompat.app.AlertDialog registerDialog = alertDialog.create();
        registerDialog.show();

    }


}
