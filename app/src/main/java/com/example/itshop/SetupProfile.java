package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

public class SetupProfile extends AppCompatActivity {

    ImageView profilepic;
    EditText name, gender, email;
    TextView save;
    ConstraintLayout constraintLayout;
    Animation shake;
    Uri image;
    String phone;
    NetworkBroadcast networkBroadcast;
    ScrollView scrollView;
    ImageView cross;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        phone=getIntent().getStringExtra("phone");
        type=getIntent().getStringExtra("type");

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        gender = findViewById(R.id.gender);
        profilepic = findViewById(R.id.profilepic);
        constraintLayout = findViewById(R.id.cons3);
        save = findViewById(R.id.savedetails);
        scrollView=findViewById(R.id.scrollview);
        cross=findViewById(R.id.setupcross);
        shake = AnimationUtils.loadAnimation(SetupProfile.this, R.anim.shake);

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;

            }
        });


        if(type!=null)
        {
            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        Glide.with(SetupProfile.this).load(snapshot.child("profilepic").getValue(String.class)).into(profilepic);
                        name.setText(snapshot.child("name").getValue(String.class));
                        email.setText(snapshot.child("email").getValue(String.class));
                        gender.setText(snapshot.child("gender").getValue(String.class));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(SetupProfile.this)
                        .crop(6f, 6f)
                        .compress(1024)
                        .maxResultSize(540, 540)
                        .start();
            }
        });

        gender.setKeyListener(null);
        final String[] listItems = {"Male", "Female", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(SetupProfile.this);
        builder.setTitle("Your Gender");

        builder.setItems(listItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gender.setText(listItems[which]);
            }
        });

        final AlertDialog dialog = builder.create();

        gender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dialog.show();
                return false;
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    name.setError("Enter Your Name");
                    name.requestFocus();
                    name.startAnimation(shake);
                } else if (email.getText().toString().equals("")) {
                    email.setError("Enter Your Email");
                    email.requestFocus();
                    email.startAnimation(shake);
                } else if (!isEmailValid(email.getText().toString())) {
                    email.setError("Enter A Proper Email");
                    email.requestFocus();
                    email.startAnimation(shake);
                } else if (gender.getText().toString().equals("")) {
                    gender.setError("Select Your Gender");
                    gender.requestFocus();
                    gender.startAnimation(shake);
                } else {

                    if (type == null) {
                        final android.app.AlertDialog alertDialog = new SpotsDialog.Builder()
                                .setCancelable(false)
                                .setContext(SetupProfile.this)
                                .setTheme(R.style.ProgressDialog)
                                .setMessage("Saving Your Details")
                                .build();

                        alertDialog.show();


                        if (image == null) {

                            profiledetails profiledetails = new profiledetails("https://firebasestorage.googleapis.com/v0/b/it-shop-d827d.appspot.com/o/profilepic.png?alt=media&token=4efc3cae-323e-4a7e-a641-7b69c06ddf48", name.getText().toString(), gender.getText().toString(), email.getText().toString(), phone);

                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profiledetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    alertDialog.dismiss();
                                    if (task.isSuccessful()) {

                                        MDToast.makeText(SetupProfile.this, "We Got Your Details", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                                        save.setText("Proceed");
                                        constraintLayout.setBackgroundColor(Color.WHITE);

                                        scrollView.smoothScrollTo(0, 0);

                                        save.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                       /* FirebaseDatabase.getInstance().getReference().child("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(MyFirebaseMessagingService.getToken(getApplicationContext())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });*/

                                                startActivity(new Intent(SetupProfile.this, Home.class));
                                                finish();
                                                customType(SetupProfile.this, "fadein-to-fadeout");

                                            }
                                        });
                                    } else {
                                        MDToast.makeText(SetupProfile.this, "Some Error Occured. Please Try Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                    }
                                }
                            });


                        } else {
                            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Profile Pictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            UploadTask uploadTask;
                            uploadTask = ref.putFile(image);

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    // Continue with the task to get the download URL
                                    return ref.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();

                                        profiledetails profiledetails = new profiledetails(downloadUri.toString(), name.getText().toString(), gender.getText().toString(), email.getText().toString(), phone);

                                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profiledetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                alertDialog.dismiss();
                                                if (task.isSuccessful()) {

                                                    MDToast.makeText(SetupProfile.this, "We Got Your Details", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                                                    save.setText("Proceed");
                                                    constraintLayout.setBackgroundColor(Color.WHITE);

                                                    scrollView.smoothScrollTo(0, 0);

                                                    save.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                       /* FirebaseDatabase.getInstance().getReference().child("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(MyFirebaseMessagingService.getToken(getApplicationContext())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                startActivity(new Intent(SetupProfile.this, Home.class));
                                                                finish();
                                                                customType(SetupProfile.this, "fadein-to-fadeout");

                                                            }
                                                        });*/

                                                            startActivity(new Intent(SetupProfile.this, Home.class));
                                                            finish();
                                                            customType(SetupProfile.this, "fadein-to-fadeout");

                                                        }
                                                    });
                                                } else {
                                                    MDToast.makeText(SetupProfile.this, "Some Error Occured. Please Try Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                }
                                            }
                                        });

                                    } else {
                                        alertDialog.dismiss();
                                        MDToast.makeText(SetupProfile.this, "Some Error Occured. Please Try Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                                    }
                                }
                            });
                        }
                    }
                    else {
                        final android.app.AlertDialog alertDialog = new SpotsDialog.Builder()
                                .setCancelable(false)
                                .setContext(SetupProfile.this)
                                .setTheme(R.style.ProgressDialog)
                                .setMessage("Saving Your Details")
                                .build();

                        alertDialog.show();


                        final Map det=new HashMap();

                        if(image==null) {
                            det.clear();
                            det.put("name", name.getText().toString());
                            det.put("gender", gender.getText().toString());
                            det.put("email",email.getText().toString());

                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(det).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    alertDialog.dismiss();
                                    if (task.isSuccessful()) {

                                        MDToast.makeText(SetupProfile.this, "Changes Saved", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                                    } else {
                                        MDToast.makeText(SetupProfile.this, "Some Error Occured. Please Try Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                    }
                                }
                            });


                        }
                        else {
                            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Profile Pictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            UploadTask uploadTask;
                            uploadTask = ref.putFile(image);

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    // Continue with the task to get the download URL
                                    return ref.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                   if(task.isSuccessful())
                                   {
                                       det.clear();
                                       det.put("name", name.getText().toString());
                                       det.put("gender", gender.getText().toString());
                                       det.put("email",email.getText().toString());
                                       det.put("profilepic",task.getResult().toString());

                                       FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(det).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               alertDialog.dismiss();
                                               if (task.isSuccessful()) {

                                                   MDToast.makeText(SetupProfile.this, "Changes Saved", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                                               } else {
                                                   MDToast.makeText(SetupProfile.this, "Some Error Occured. Please Try Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                               }
                                           }
                                       });

                                   }
                                   else {
                                       Toast.makeText(SetupProfile.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                   }
                                }
                            });

                        }


                    }
                }
            }
        });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data.getData() != null) {
                image = data.getData();
                profilepic.setImageURI(image);
            }
        }
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