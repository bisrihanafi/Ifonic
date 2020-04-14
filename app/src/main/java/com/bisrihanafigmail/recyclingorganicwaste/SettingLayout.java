package com.bisrihanafigmail.recyclingorganicwaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.StorageStatsManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class SettingLayout extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    FirebaseFirestore db;
    EditText nama_user,kabupaten,kecamatan,desa,dusun, rt, rw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_layout);
        Button keluar=findViewById(R.id.logout);
        Button save=findViewById(R.id.simpan_user);
        TextView nama=findViewById(R.id.nama);
        TextView email=findViewById(R.id.email);
        nama_user=findViewById(R.id.nama_user);
        kabupaten=findViewById(R.id.kabupaten);
        kecamatan=findViewById(R.id.kecamatan);
        desa=findViewById(R.id.desa);
        dusun=findViewById(R.id.dusun);
        rt=findViewById(R.id.rt);
        rw=findViewById(R.id.rw);
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
                    startActivity(new Intent(SettingLayout.this, Login.class));
                    finish();
                }
            }
        };
        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Digunakan Untuk Logout
                auth.signOut();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveValueFromUI();
            }
        });
        email.setText(auth.getCurrentUser().getEmail());
        nama.setText(auth.getCurrentUser().getDisplayName());

        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> map= (Map<String, Object>) document.getData().get("informasi");
                    Map<String, Object> map2= (Map<String, Object>) map.get("user_basic");
                    Map<String, Object> map3= (Map<String, Object>) map.get("alamat");
                    if (document.exists()) {
                        nama_user.setText(map2.get("nama").toString());
                        kabupaten.setText(map3.get("kabupaten").toString());
                        kecamatan.setText(map3.get("kecamatan").toString());
                        desa.setText(map3.get("desa").toString());
                        dusun.setText(map3.get("dusun").toString());
                        rt.setText(map3.get("rt").toString());
                        rw.setText(map3.get("rw").toString());
                    } else {
                        nama_user.setText("(Error)");
                    }
                }
            }
        });

    }
    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            // do something here
        }
        return super.onOptionsItemSelected(item);
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

    void saveValueFromUI(){
        db.collection("users").document(auth.getCurrentUser().getUid())
                .update(
                        "informasi.user_basic.nama", nama_user.getText().toString().trim(),
                        "informasi.alamat.kabupaten", kabupaten.getText().toString().trim(),
                        "informasi.alamat.kecamatan", kecamatan.getText().toString().trim(),
                        "informasi.alamat.desa", desa.getText().toString().trim(),
                        "informasi.alamat.dusun", dusun.getText().toString().trim(),
                        "informasi.alamat.rt", rt.getText().toString().trim(),
                        "informasi.alamat.rw", rw.getText().toString().trim()
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Disimpan",Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Penyimpanan gagal",Toast.LENGTH_LONG).show();
                    }
                });

    }
}
