package com.example.notificationtesting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class notifications extends AppCompatActivity {

    private ListView list;
    public notificationreceived noti_rev;
    private ProgressDialog pDialog;
    public String notiid, getdate,gettime,getmsg;
    String isread;
    int i;
    SharedPreferences editors;
    String userid,link;
    TextView datee,mesg;

    List<String> notilist= new ArrayList<String>();
    List<NotificationsClass> notiflist= new ArrayList<NotificationsClass>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
       //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        editors = getSharedPreferences("link", MODE_PRIVATE);
        userid = editors.getString("userid","");
        link = editors.getString("link","");

        list = (ListView) findViewById(R.id.notifylist);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                notiid = (String) ((TextView)view).getText();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+link+"/Notifications/"+notiid);
                ref.addValueEventListener(new ValueEventListener() {

                    public void onDataChange(DataSnapshot dataSnapshot) {
                        noti_rev = dataSnapshot.getValue(notificationreceived.class);
                        getdate=noti_rev.Date;
                        getmsg=noti_rev.Message;
                        gettime=noti_rev.Time;
                        isread=noti_rev.Read;
                        //Toast.makeText(getApplicationContext(),"get "+gettime,Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(notifications.this);
                        builder1.setTitle(notiid);
                        builder1.setMessage(getmsg+System.getProperty("line.separator")+System.getProperty("line.separator")+Html.fromHtml("<b>"+getdate+"</b>")+System.getProperty("line.separator")+"at "+gettime);
                        builder1.setCancelable(false);
                        builder1.setNeutralButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        if(!isFinishing())
                        {
                            alert11.show();
                        }
                    }
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }
        });

        final FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();
        String amg="users/"+ link+"/Notifications/";
        // Toast.makeText(getApplicationContext(),amg,Toast.LENGTH_SHORT).show();
        DatabaseReference ref = fdatabase.getReference(amg);
        //contactList = ref.getKey();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(!ds.hasChild("msgstatus"))
                    {
                            notiflist.add(new NotificationsClass(ds.child("datetime").getValue().toString(),ds.child("deviceid").getValue().toString(),ds.child("email").getValue().toString(),ds.child("message").getValue().toString(),ds.child("phone").getValue().toString(),ds.child("read").getValue().toString(),ds.child("title").getValue().toString(),ds.child("type").getValue().toString(),ds.child("userid").getValue().toString(),ds.getKey().toString()));
                            Log.i("Data:","Added");

                    }
                    notilist.add(ds.child("message").getValue().toString());
                }

                list.setAdapter(new ArrayAdapter<String>(notifications.this,
                        android.R.layout.simple_list_item_1, notilist));
                pDialog.dismiss();
                send();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
    public void send()
    {
        i= 0;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (i==notiflist.size())
                {
                    Log.i("List:","Ended");
                }
                else if (i<notiflist.size()){

                    Log.i("Status:","Message Send : "+notiflist.get(i).phone+"\n"+notiflist.get(i).message);

                    i++;
                }

            }
        }, 0, 2000);
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
