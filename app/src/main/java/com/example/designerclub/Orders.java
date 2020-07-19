package com.example.designerclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.designerclub.Common.common;
import com.example.designerclub.Models.FCMResponse;
import com.example.designerclub.Models.FCMSendData;
import com.example.designerclub.Models.OrderModel;
import com.example.designerclub.Remote.IFCMService;
import com.example.designerclub.Remote.RetrofitFCMClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Orders extends AppCompatActivity {
    DatabaseReference orderRefrence, removeCartRefrence,historyRefrence;
    EditText edtName, edtAddress;
    TextView tvTotalPrice;
    Button btnConfirmOrder;
    String totalPrice, currentUserPhone;
    String mDate, mTimel;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    private IFCMService ifcmService;
    private FirebaseUser user;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
        user = FirebaseAuth.getInstance().getCurrentUser();
        initViews();
        totalPrice = getIntent().getStringExtra("price");
        tvTotalPrice.setText(totalPrice);
        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });
    }


    private void initViews() {
        currentUserPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        orderRefrence = FirebaseDatabase.getInstance().getReference().child("Orders");
        removeCartRefrence = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View");
        edtName = (EditText) findViewById(R.id.edtConfirmOrderName);
        edtAddress = (EditText) findViewById(R.id.edtConfirmOrderAddress);
        btnConfirmOrder = (Button) findViewById(R.id.btnConfirmOrder);
        linearLayout = (LinearLayout) findViewById(R.id.llySnakbar);
        progressBar = (ProgressBar) findViewById(R.id.progBarConfirmOrder);
        tvTotalPrice = (TextView) findViewById(R.id.tvConfirmOrderTotalPrice);
        historyRefrence = FirebaseDatabase.getInstance().getReference("Order History");
        // get current date and time
        Calendar calanderInstance = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        mDate = currentDate.format(calanderInstance.getTime());
        SimpleDateFormat mCurrentTime = new SimpleDateFormat("HH:mm:ss a");
        mTimel = mCurrentTime.format(calanderInstance.getTime());
    }

    private void confirmOrder() {
        progressBar.setVisibility(View.VISIBLE);
        OrderModel orderModel = new OrderModel();
        orderModel.setName(edtName.getText().toString());
        orderModel.setAddress(edtAddress.getText().toString());
        orderModel.setNodePhone(currentUserPhone);
        orderModel.setTime(mTimel);
        orderModel.setDate(mDate);
        orderModel.setTotalprice(tvTotalPrice.getText().toString());
        orderModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Please Enter Name");
        } else if (edtAddress.getText().toString().isEmpty()) {
            edtAddress.setError("Please provide your Address");
        } else {
            orderRefrence.child(currentUserPhone).setValue(orderModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                historyRefrence.child(currentUserPhone).setValue(orderModel);
                                removeCartRefrence.child(currentUserPhone).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.GONE);
                                            Map<String, String> notiData = new HashMap<>();
                                            notiData.put(common.NOTI_TITLE, "New Order");
                                            notiData.put(common.NOTI_TITLE, "You have new order from " + user.getPhoneNumber());
                                            FCMSendData sendData = new FCMSendData("newOrder", notiData);
                                            compositeDisposable.add(ifcmService.sendNotification(sendData)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Consumer<FCMResponse>() {
                                                        @Override
                                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                                            Toast.makeText(getApplicationContext(), "Order placed Successfully", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(Orders.this,home.class));
                                                            finish();
                                                        }
                                                    }, new Consumer<Throwable>() {
                                                        @Override
                                                        public void accept(Throwable throwable) throws Exception {
                                                            Toast.makeText(Orders.this, "Order was placed but failed to send notification", Toast.LENGTH_SHORT).show();
                                                            //Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                            );
                                            clearFields();
                                        }
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void clearFields() {
        edtName.setText("");
        edtAddress.setText("");
        tvTotalPrice.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }
}
