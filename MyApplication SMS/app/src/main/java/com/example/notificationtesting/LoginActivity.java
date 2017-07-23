package com.example.notificationtesting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    String carnum="";
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgress;
    String uemail, uid, deviceid;
    String token;
    List<String> cookies;
    String getcookie="";
    public String email;
    public String password;
    SharedPreferences.Editor editor;
    private Boolean saveLogin;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    JSONObject jsb;
    CheckBox saveLoginCheckBox;

    FetchData fd;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        mProgress =new ProgressDialog(this);
        mProgress.setMessage("Signing in...");
        mProgress.setCancelable(false);

        saveLoginCheckBox = (CheckBox)findViewById(R.id.saveLoginCheckBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        editor = getSharedPreferences("link", MODE_PRIVATE).edit();

        fd = new FetchData();
        //jsb =
        //new PostTask().execute("http://monarch.esajee.com:8082/traccar/rest/login?payload=[%22admin%22,%22admin%22]");
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            mEmailView.setText(loginPreferences.getString("username", ""));
            mPasswordView.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    finish();
                    mProgress.dismiss();
                    Intent myintent= new Intent(LoginActivity.this, MainActivity.class);
                    myintent.putExtra("msg",carnum);
                    startActivity(myintent);
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
                    mEmailSignInButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);

                            if (saveLoginCheckBox.isChecked()) {
                                loginPrefsEditor.putBoolean("saveLogin", true);
                                loginPrefsEditor.putString("username", mEmailView.getText().toString());
                                loginPrefsEditor.putString("password", mPasswordView.getText().toString());
                                loginPrefsEditor.commit();
                            } else {
                                loginPrefsEditor.clear();
                                loginPrefsEditor.commit();
                            }
                            checkAndRequestPermissions();
                            mProgress.show();
                            //Toast.makeText(getApplicationContext(),"fxbscbssd",Toast.LENGTH_SHORT).show();
                            //signIn(mEmailView.getText().toString(),mPasswordView.getText().toString());
                            signin();
                /*
                mProgress.dismiss();
                finish();
                Intent myintent= new Intent(LoginActivity.this, MainActivity.class);
                myintent.putExtra("message",message);
                startActivity(myintent);*/
                        }
                    });
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private  boolean checkAndRequestPermissions() {

        int cellphone = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE);
        int state = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        int internet = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (cellphone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CALL_PHONE);
        }
        if (state != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //mProgress.setMessage("Signing in...");


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("");
                    builder.setMessage("Without permissions application may not work properly.\nDo you want to give permissions?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            checkAndRequestPermissions();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            finish();
                            System.exit(0);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return;
            }
        }
    }
    void signin()
    {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Login Failed.",
                                    Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();

                        }
                        else
                        {
                            email = mEmailView.getText().toString();
                            password = mPasswordView.getText().toString();
                            try
                            {
                                new PostTask().execute("http://monarch.esajee.com:8082/traccar/rest/login?payload=[%22admin%22,%22admin%22]");
                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Login/"+mEmailView.getText().toString()+"/");
                                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("password"))
                                        {
                                            if(mPasswordView.getText().toString().equals( dataSnapshot.child("password").getValue().toString()))
                                            {
                                                //email = mEmailView.getText().toString();
                                                //password = mPasswordView.getText().toString();
                                                editor.putString("link", dataSnapshot.child("id").getValue().toString());
                                                editor.putString("userid", mEmailView.getText().toString());
                                                editor.commit();
                                                //dataupload();
                                                finish();
                                                mProgress.dismiss();
                                                Intent myintent= new Intent(LoginActivity.this, MainActivity.class);
                                                myintent.putExtra("msg",carnum);
                                                Toast.makeText(getApplicationContext(),"Login Successfuly",Toast.LENGTH_SHORT).show();
                                                startActivity(myintent);
                                            }

                                            //else
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                //new PostTask().execute("http://monarch.esajee.com:8082/traccar/rest/login?payload=[%22admin%22,%22admin%22]");
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                            }

                        }

                        // ...
                    }
                });
    }

    /*void signIn(final String email, String password) {
        Log.d(TAG, "signIn:" + email);
      *//*  if (!validateForm()) {
            return;
        }*//*
        mProgress.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();

                            //mEmailView.setText("");
                            mPasswordView.setText("");
                        }
                        else
                        {
                            dataupload();
                            finish();
                            mProgress.dismiss();
                            Intent myintent= new Intent(LoginActivity.this, MainActivity.class);
                            myintent.putExtra("msg",carnum);
                            //Toast.makeText(getApplicationContext(),"sending carnum",Toast.LENGTH_SHORT).show();
                            startActivity(myintent);
                        }
                    }
                });
    }*/

    private class PostTask extends AsyncTask<Object, Object, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {

            Object url=params[0];
            JSONObject result;//=new JSONObject();
            //JSONObject result;
            String str = "payload=[\""+email+"\",\""+password+"\""+"]";
            result = fd.requestWebService("login",str,email);
            //Toast.makeText(getApplicationContext(),"payload=[%22"+email+"%22,%22"+password+"%22]",Toast.LENGTH_SHORT).show();

            return result;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            //updateProgressBar(values[0]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            try
            {
                if (result.length()>0)
                {
                    Log.i("check",result.toString());
                    DatabaseReference newuser= FirebaseDatabase.getInstance().getReference("Login/"+email+"/");
                    DatabaseReference newuserprof= FirebaseDatabase.getInstance().getReference("users/");
                    try {
                        newuser.child("id").setValue(result.getString("id"));
                        newuser.child("username").setValue(email);
                        newuser.child("password").setValue(password);
                        newuserprof.child(result.getString("id")).child("email").setValue(email);
                        newuserprof.child(result.getString("id")).child("name").setValue(email);
                        //newuserprof.child(result.getString("id")).child("email").setValue(email);

                        Log.i("DataUpload","Uploaded");
                        //FirebaseAuth.getInstance().signOut();
                        //startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("DataUpload","Not Upload");
                    }
                }
                else
                {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                    Log.i("Log","Log out");
                }
            }
            catch (Exception e)
            {

            }


        }
    }

}