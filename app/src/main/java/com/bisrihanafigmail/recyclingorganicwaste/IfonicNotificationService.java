package com.bisrihanafigmail.recyclingorganicwaste;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class IfonicNotificationService extends Service {

    FirebaseFirestore db;
    int count_n=0;
    private FirebaseAuth auth;
    Intent mIntent;


    public IfonicNotificationService() {

    }
    @Override
    public void onCreate() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance(); //Menghubungkan dengan Firebase Authentifikasi
        mIntent = new Intent(this, ChatRoomMe.class);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db.collection("users").document(auth.getCurrentUser().getEmail()).collection("chat").orderBy("count", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) { return; }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    count_n=Integer.parseInt(doc.get("count").toString());
                    if (!(doc.get("from").toString().equals(auth.getCurrentUser().getDisplayName()))) {
                        showNotif("@" + doc.get("from").toString() + " : " + doc.get("fill").toString());
                    }
                }

            }
        });
        return  START_STICKY;
    }
    private void showNotif(String i) {
        String NOTIFICATION_CHANNEL_ID = "NOTIF_MSG";
        Context context = this.getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "chat notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_chat_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_chat_black_24dp))
                .setTicker("chat added")
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("Ifonic : Pesan Baru")
                .setContentText(i);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(115, builder.build());
    }


}