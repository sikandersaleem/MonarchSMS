package com.example.notificationtesting;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String label="Firebase Notification";
    String d= "as1298"; ///////notificATION ID
    Calendar c;
    private SimpleDateFormat dateFormatter,fortime;

    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getNotification().getBody());

        c = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        fortime = new SimpleDateFormat("HH:mm:ss", Locale.US);

        /*DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Notifications/");
        DatabaseReference ref_notification = ref1.push();
        ref_notification.child("Date").setValue(dateFormatter.format(c.getTime()));
        ref_notification.child("Time").setValue(fortime.format(c.getTime()));
        ref_notification.child("Message").setValue(remoteMessage.getNotification().getBody());
        ref_notification.child("Read").setValue("0");*/

        JSONObject jsonobj;//=new JSONObject();
        try{
            jsonobj = new JSONObject(remoteMessage.getNotification().getBody());
        }catch (Exception e)
        {
            jsonobj = new JSONObject();
        }

        String phonenumber="";
        String message="";

        if(jsonobj.has("phone"))
            try {
                phonenumber = (jsonobj.getString("phone"));
                message = (jsonobj.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(phonenumber.length() > 0)
                 sendsms(phonenumber,message);
    }

    void sendsms(String num,String message) {
       // if (num.equals("0" + nmbr) || num.equals("+92" + nmbr)) {

                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage(num, null, message, null, null);
                //Toast.makeText(getBaseContext(), "sending...", Toast.LENGTH_SHORT).show();
                //Thread.sleep(20);
                //Toast.makeText(getBaseContext(), "sent", Toast.LENGTH_SHORT).show();

        //}
    }
    private void showNotification(String message) {

        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Intent activityIntent = new Intent(MyFirebaseMessagingService.this, notificationactivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this,0,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(label)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }


}