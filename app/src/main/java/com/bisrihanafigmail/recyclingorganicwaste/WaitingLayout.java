package com.bisrihanafigmail.recyclingorganicwaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class WaitingLayout extends AppCompatActivity {
    private FirebaseAuth auth;
    FirebaseFirestore db;
    TextView admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_layout);
        admin=findViewById(R.id.take_by);
        Button batalkan=findViewById(R.id.batalkan);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance(); //Menghubungkan dengan Firebase Authentifikasi
        db.collection("quests").document(auth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    admin.setText(snapshot.getData().get("nama_admin").toString()+" mengambil");
                }
            }
        });
        batalkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("quests").document(auth.getCurrentUser().getEmail())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });
    }
}
