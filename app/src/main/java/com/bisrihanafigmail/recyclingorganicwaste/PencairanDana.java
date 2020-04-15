package com.bisrihanafigmail.recyclingorganicwaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.util.Map;

public class PencairanDana extends AppCompatActivity {
    private FirebaseAuth auth;
    DocumentReference dbref;
    FirebaseFirestore db;
    AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pencairan_dana);
        TextView id=findViewById(R.id.id_pelanggan2);
        auth = FirebaseAuth.getInstance();
        id.setText(auth.getCurrentUser().getEmail());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance(); //Menghubungkan dengan Firebase Authentifikasi
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialog = alertDialogBuilder.create();
        dbref=db.collection("users").document(auth.getCurrentUser().getUid());
        dbref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> map= (Map<String, Object>) snapshot.getData().get("log_transaksi");
                    Map<String, Object> map2= (Map<String, Object>) map.get("last_out_inf");
                    //Toast.makeText(getApplicationContext(),map3.get("nama").toString(),Toast.LENGTH_LONG).show();
                    double jumlah_decimal=Double.parseDouble(map2.get("jumlah").toString());
                    boolean izin=Boolean.parseBoolean(map2.get("izin").toString());
                    if (alertDialog.isShowing())alertDialog.hide();
                    if (jumlah_decimal>0d && !izin){
                        openDialogConfirmPencairan(jumlah_decimal, map2.get("time").toString());
                    }
                } else {
                }
            }
        });
    }
    public void openDialogConfirmPencairan(double jumlah, String time) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        alertDialogBuilder.setMessage("Anda akan mencairkan dana sebesar \nRp "+formatter.format(jumlah)+" \nKonfirmasi bahwa ini adalah anda\nTime : "+time);
        alertDialogBuilder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        db.collection("users").document(auth.getCurrentUser().getUid())
                                .update(
                                        "log_transaksi.last_out_inf.izin", true
                                )
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),"Permintaan telah dikonfirmasi",Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
        alertDialogBuilder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        db.collection("users").document(auth.getCurrentUser().getUid())
                                .update(
                                        "log_transaksi.last_out_inf.jumlah", 0,
                                        "log_transaksi.last_out_inf.izin", false
                                )
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),"Pencairan dibatalkan",Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
