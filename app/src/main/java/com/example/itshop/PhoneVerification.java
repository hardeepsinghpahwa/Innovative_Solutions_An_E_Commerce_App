package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.itshop.Notifications.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

public class PhoneVerification extends AppCompatActivity {

    EditText code1, code2, code3, code4, code5, code6;
    TextView sendingverifcationtext, resend, codesentto;
    String code;
    String phone, p, v_id;
    int counter = 30, a = 0;
    TextView proceed,didnotget;
    ConstraintLayout constraintLayout;
    ImageView check,back;
    NetworkBroadcast networkBroadcast;
    ProgressBar progressBar,progressBar2;
    PhoneAuthProvider.ForceResendingToken resendotp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        phone = getIntent().getStringExtra("phone");
        p = phone.replaceAll("\\s", "");

        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        proceed = findViewById(R.id.proceed);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);
        codesentto = findViewById(R.id.codesenttext);
        resend = findViewById(R.id.resend);
        didnotget=findViewById(R.id.didntgettext);
        check=findViewById(R.id.check);
        sendingverifcationtext = findViewById(R.id.sendingcodetext);
        constraintLayout = findViewById(R.id.cons2);
        progressBar=findViewById(R.id.phoneverificationprogressBar);
        back=findViewById(R.id.phoneverificationback);
        progressBar2=findViewById(R.id.progressBar2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                return false;

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                FirebaseDatabase.getInstance().getReference().child("Profiles").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists())
                        {
                           FirebaseDatabase.getInstance().getReference().child("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(MyFirebaseMessagingService.getToken(getApplicationContext())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   progressBar.setVisibility(View.GONE);
                                    Intent intent=new Intent(PhoneVerification.this, Home.class);
                                    startActivity(intent);
                                    customType(PhoneVerification.this,"left-to-right");
                                    finish();
                                }
                            });


                        }
                        else {
                            Intent intent=new Intent(PhoneVerification.this, SetupProfile.class);
                            intent.putExtra("phone",p);
                            startActivity(intent);
                            customType(PhoneVerification.this,"left-to-right");
                            finish();
                            progressBar.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                code = phoneAuthCredential.getSmsCode();

                if (code != null) {
                    code1.setText(String.valueOf(code.charAt(0)));
                    code2.setText(String.valueOf(code.charAt(1)));
                    code3.setText(String.valueOf(code.charAt(2)));
                    code4.setText(String.valueOf(code.charAt(3)));
                    code5.setText(String.valueOf(code.charAt(4)));
                    code6.setText(String.valueOf(code.charAt(5)));

                    code6.clearFocus();
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                MDToast.makeText(PhoneVerification.this, "Some Error Occured. Please Check Your Number Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                onBackPressed();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull final PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.i("token", forceResendingToken.toString());

                v_id = s;
                resendotp = forceResendingToken;

                progressBar2.setVisibility(View.GONE);
                sendingverifcationtext.setText("Verification Code Sent");
                codesentto.setText("Enter code sent to " +phone);

                code1.setEnabled(true);
                code2.setEnabled(true);
                code3.setEnabled(true);
                code4.setEnabled(true);
                code5.setEnabled(true);
                code6.setEnabled(true);


                if (a == 0) {
                    new CountDownTimer(30000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            resend.setText("Send Again In " + counter);
                            counter--;
                        }

                        @Override
                        public void onFinish() {
                            resend.setText("Send Again");

                            resend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    a = 1;
                                    resend.setText("Code Sent");
                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                            p,        // Phone number to verify
                                            1,               // Timeout duration
                                            TimeUnit.MINUTES,   // Unit of timeout
                                            PhoneVerification.this,               // Activity (for callback binding)
                                            mCallBacks,         // OnVerificationStateChangedCallbacks
                                            resendotp);
                                }
                            });
                        }
                    }.start();
                }
            }
        };

        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code1.clearFocus();
                    code2.requestFocus();
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code2.clearFocus();
                    code3.requestFocus();
                } else {
                    code2.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length() == 0) {
                                code2.clearFocus();
                                code1.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code3.clearFocus();
                    code4.requestFocus();
                } else {
                    code3.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length() == 0) {
                                code3.clearFocus();
                                code2.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code4.clearFocus();
                    code5.requestFocus();
                } else {
                    code4.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length() == 0) {
                                code4.clearFocus();
                                code3.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code5.clearFocus();
                    code6.requestFocus();
                } else {
                    code5.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length() == 0) {
                                code5.clearFocus();
                                code4.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        code6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }

                if (code6.getText().toString().equals("")) {
                    code6.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length() == 0) {
                                code6.clearFocus();
                                code5.requestFocus();
                            }
                            return false;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (PhoneAuthProvider.getInstance() != null) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    p,
                    30,
                    TimeUnit.SECONDS,
                    PhoneVerification.this,
                    mCallBacks);
        }


    }

    private void signInWithPhoneAuthCredential(String id, String c) {

        final AlertDialog alertDialog = new SpotsDialog.Builder()
                .setCancelable(false)
                .setContext(PhoneVerification.this)
                .setTheme(R.style.ProgressDialog)
                .setMessage("Verifying Phone")
                .build();

        alertDialog.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, c);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        alertDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                            codesentto.setText("OTP VERIFIED");
                            codesentto.setTextColor(getColor(R.color.green));
                            check.setVisibility(View.VISIBLE);
                            didnotget.setVisibility(View.GONE);
                            resend.setVisibility(View.GONE);
                            proceed.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.SlideInUp)
                                    .duration(300)
                                    .playOn(proceed);

                            code1.setKeyListener(null);
                            code2.setKeyListener(null);
                            code3.setKeyListener(null);
                            code4.setKeyListener(null);
                            code5.setKeyListener(null);
                            code6.setKeyListener(null);
                            code1.clearFocus();
                            code2.clearFocus();
                            code3.clearFocus();
                            code4.clearFocus();
                            code5.clearFocus();
                            code6.clearFocus();

                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());

                            codesentto.setText("WRONG OTP");
                            codesentto.setTextColor(Color.RED);
                            code1.setText("");
                            code2.setText("");
                            code3.setText("");
                            code4.setText("");
                            code5.setText("");
                            code6.setText("");
                            code6.clearFocus();
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);


                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        customType(PhoneVerification.this, "left-to-right");

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        networkBroadcast=new NetworkBroadcast();
        this.registerReceiver(networkBroadcast, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(networkBroadcast);
    }
}