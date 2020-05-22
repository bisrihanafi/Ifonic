package com.bisrihanafigmail.recyclingorganicwaste;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class WaitingLayout extends AppCompatActivity {
    private FirebaseAuth auth;
    FirebaseFirestore db;
    TextView admin;
    String id_quest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_layout);
        admin=findViewById(R.id.take_by);
        Button batalkan=findViewById(R.id.batalkan);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance(); //Menghubungkan dengan Firebase Authentifikasi
        db.collection("quests").whereEqualTo("id_user",auth.getCurrentUser().getEmail().trim()).whereEqualTo("do", "proses")
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) { return; }
                        if (queryDocumentSnapshots.isEmpty()) {
                            startActivity(new Intent(WaitingLayout.this, DialogPilihan.class));
                            finish();
                        }else {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                id_quest=doc.getId();
                                admin.setText(doc.getData().get("nama_admin").toString() + " mengambil");
                            }
                        }
                    }
                });
        batalkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open(view);
            }
        });
    }
    public void open(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan membatalkan permintaan?");
        alertDialogBuilder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        db.collection("quests").document(id_quest)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(WaitingLayout.this, "Permintaan dibatalkan", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(WaitingLayout.this, DialogPilihan.class));
                                        finish();
                                    }
                                });
                    }
                });

        alertDialogBuilder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        try {
            alertDialog.show();
        }
        catch (WindowManager.BadTokenException e) {
            Toast.makeText(getApplicationContext(), "Error : "+e, Toast.LENGTH_LONG).show();
        }
    }
}
