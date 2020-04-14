package com.bisrihanafigmail.recyclingorganicwaste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseActivityHome extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    TextView deposit, last_in,last_out;
    DocumentReference  dbref;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_home);
        ImageButton setting= findViewById(R.id.pengaturan);
        ImageButton stor = findViewById(R.id.stor);
        ImageButton pencairan = findViewById(R.id.pencairan);

        deposit=findViewById(R.id.deposit);
        last_in=findViewById(R.id.pemasukan);
        last_out=findViewById(R.id.pengeluaran);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance(); //Menghubungkan dengan Firebase Authentifikasi
        //Authentifikasi Listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                /*
                 * Jika User sebelumnya telah keluar/logout
                 * Saat aplikasi dibuka kembali, Activity ini akan langsung dialihkan pada Activity Login
                 */
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(BaseActivityHome.this, Login.class));
                    finish();
                }
            }
        };
        /*
        Keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Digunakan Untuk Logout
                auth.signOut();
            }
        });
        */
        stor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference dbrf=db.collection("quests").document(auth.getCurrentUser().getEmail());
                dbrf.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                startActivity(new Intent(BaseActivityHome.this, WaitingLayout.class));
                            } else {
                                startActivity(new Intent(BaseActivityHome.this, DialogPilihan.class));
                            }
                        }
                    }
                });

            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivityHome.this, SettingLayout.class));

            }
        });
        pencairan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivityHome.this, PencairanDana.class));

            }
        });


        dbref=db.collection("users").document(auth.getCurrentUser().getUid());
        dbref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> map= (Map<String, Object>) snapshot.getData().get("informasi");
                    Map<String, Object> map2= (Map<String, Object>) map.get("user_basic");
                    //Toast.makeText(getApplicationContext(),map3.get("nama").toString(),Toast.LENGTH_LONG).show();
                    setUiApp(map2.get("deposit").toString(), map2.get("last_in").toString(),map2.get("last_out").toString());
                } else {
                    buatDatabase();
                }
            }
        });
    }
    void setUiApp(String deposit_local, String in_local, String out_local){
        double double_deposit= Double.parseDouble(deposit_local);
        double double_in= Double.parseDouble(in_local);
        double double_out= Double.parseDouble(out_local);
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        deposit.setText("Rp "+formatter.format(double_deposit));
        last_in.setText("+Rp "+formatter.format(double_in));
        last_out.setText("-Rp "+formatter.format(double_out));
    }
    void buatDatabase(){
        // Create a new user with a first and last name
        Map<String, Object> user_basic = new HashMap<>();
        user_basic.put("id_google_account", auth.getCurrentUser().getUid());
        user_basic.put("nama_google", auth.getCurrentUser().getDisplayName());
        user_basic.put("email", auth.getCurrentUser().getEmail());
        user_basic.put("nama", "");
        user_basic.put("deposit", 0);
        user_basic.put("last_in", 0);
        user_basic.put("last_out", 0);

        //Alamat collection
        Map<String, Object> alamat = new HashMap<>();
        alamat.put("kabupaten", "");
        alamat.put("kecamatan", "");
        alamat.put("desa", "");
        alamat.put("dusun", "");
        alamat.put("rt", 0);
        alamat.put("rw", 0);

        //Chat
        Map<String, Object> chat = new HashMap<>();
        Date currentTime = Calendar.getInstance().getTime();
        Map<String, Object> pesan = new HashMap<>();
        pesan.put("count", "");
        pesan.put("time", currentTime.toString());
        pesan.put("from", "");
        pesan.put("fill", "");
        chat.put(currentTime.toString(), pesan);

        //Transaksi
        Map<String, Object> transaksi = new HashMap<>();
        Map<String, Object> isi = new HashMap<>();
        isi.put("count", "");
        isi.put("time", currentTime.toString());
        isi.put("type", "");
        isi.put("fill", 0);
        transaksi.put(currentTime.toString(), isi);
        //penarikan
        Map<String, Object> last_out_inf = new HashMap<>();
        last_out_inf.put("jumlah", 0);
        last_out_inf.put("time", currentTime.toString());
        last_out_inf.put("izinkan", false);
        transaksi.put("last_out_inf",last_out_inf);


        //Add Collcetion Extend
        Map<String, Object> user = new HashMap<>();
        Map<String, Object> user_informasi = new HashMap<>();
        user_informasi.put("user_basic",user_basic);
        user_informasi.put("alamat",alamat);
        user.put("informasi", user_informasi);
        user.put("chat", chat);
        user.put("log_transaski", transaksi);

        // Add a new document with a generated ID
        db.collection("users").document(auth.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
