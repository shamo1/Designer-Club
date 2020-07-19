package com.example.designerclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.designerclub.Common.common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText inputNumber, inputOTP;
    private TextView msg, textView;
    private Button btnContinue, btnVerify;
    private ProgressDialog progressDialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private String phoneNumber;
    private FirebaseAuth mAuth;
    private CountryCodePicker ccp;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInternetConnection();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            sendUserToDashBoard();
        }
        init();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phoneNumber = ccp.getFullNumberWithPlus();
                if (TextUtils.isEmpty(phoneNumber)) {
                    inputNumber.setError("Required");
                }

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        MainActivity.this,               // Activity (for callback binding)
                        callbacks);        // OnVerificationStateChangedCallbacks
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                //loadingbar.dismiss();
                Toast.makeText(getApplicationContext(), "Login Fail" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                //loadingbar.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;
                btnContinue.setVisibility(View.INVISIBLE);
                inputNumber.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                ccp.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.VISIBLE);
                inputOTP.setVisibility(View.VISIBLE);
                btnVerify.setVisibility(View.VISIBLE);
                msg.setText("Verification code has been sent " + phoneNumber);
            }
        };
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnContinue.setVisibility(View.INVISIBLE);
                inputNumber.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                ccp.setVisibility(View.INVISIBLE);
                String VerificationCode = inputOTP.getText().toString();
                if (TextUtils.isEmpty(VerificationCode)) {
                    inputOTP.setError("Required");
                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, VerificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }


    private void init() {
        progressDialog = new ProgressDialog(getApplicationContext());
        inputNumber = findViewById(R.id.input_phone);
        inputOTP = findViewById(R.id.input_otp);
        msg = findViewById(R.id.verification_code_sent_message);
        btnContinue = findViewById(R.id.btn_continue);
        btnVerify = findViewById(R.id.btn_verify);
        textView = findViewById(R.id.textview);
        ccp = findViewById(R.id.country_code_picker);
        ccp.registerCarrierNumberEditText(inputNumber);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        //progressDialog.setTitle("Please Wait....");
        //progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login sucessfully", Toast.LENGTH_SHORT).show();
                            sendUserToDashBoard();
                        } else {
                            //progressDialog.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(getApplicationContext(), "Error! " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendUserToDashBoard() {
        // getting device token and upadate it in firebase
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), home.class));
                        finish();
                    }
                }).addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                common.updateToken(getApplicationContext(), task.getResult().getToken());
                startActivity(new Intent(getApplicationContext(), home.class));
                finish();
            }
        });
    }


    private void checkInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(this, "Wifi is Enabled", Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(this, "Mobile Data is Enabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No Internet Connected", Toast.LENGTH_LONG).show();
        }
    }
}
