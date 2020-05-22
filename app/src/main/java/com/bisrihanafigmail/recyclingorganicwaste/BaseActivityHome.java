package com.bisrihanafigmail.recyclingorganicwaste;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseActivityHome extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    TextView deposit, last_in,last_out;
    FirebaseFirestore db;
    double double_deposit,min_cair=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_home);
        CardView setting= findViewById(R.id.pengaturan);
        CardView stor = findViewById(R.id.stor);
        CardView pencairan = findViewById(R.id.pencairan);
        CardView chat = findViewById(R.id.xchat);
        ImageButton help = findViewById(R.id.helpbase);
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
        db.collection("system").document("aturan")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            min_cair=Double.parseDouble(snapshot.get("minimalpencairan").toString().trim());
                            if (!snapshot.get("versi").toString().trim().equalsIgnoreCase("1")){
                                Toast.makeText(getApplicationContext(),"Aplikasi Kadaluarsa",Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                ambildata();
                            }
                        }
                    }
                });

        stor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivityHome.this, WaitingLayout.class));
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivityHome.this, SettingLayout.class));

            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivityHome.this, Help.class));

            }
        });
        pencairan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (double_deposit>=min_cair){
                    openDialogMe(view);
                }else {
                    openDialogMe2(view);
                }
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivityHome.this, ChatRoomMe.class));
            }
        });
    }
    void ambildata(){
        db.collection("users").document(auth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Map<String, Object> map= (Map<String, Object>) snapshot.getData().get("informasi");
                            Map<String, Object> map2= (Map<String, Object>) map.get("user_basic");
                            setUiApp(map2.get("deposit").toString(), map2.get("last_in").toString(),map2.get("last_out").toString());
                        } else {
                            buatDatabase();
                        }
                    }
                });
    }
    void setUiApp(String deposit_local, String in_local, String out_local){
        double_deposit= Math.floor(Double.parseDouble(deposit_local));
        double double_in= Math.floor(Double.parseDouble(in_local));
        double double_out= Math.floor(Double.parseDouble(out_local));
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        deposit.setText("Rp "+formatter.format(double_deposit));
        last_in.setText("+Rp "+formatter.format(double_in));
        last_out.setText("-Rp "+formatter.format(double_out));
    }
    void buatDatabase(){
        // Create a new user with a first and last name
        Map<String, Object> user_basic = new HashMap<>();
        user_basic.put("nama_google", auth.getCurrentUser().getDisplayName());
        user_basic.put("email", auth.getCurrentUser().getEmail());
        user_basic.put("nama", "");
        user_basic.put("deposit", 0);
        user_basic.put("last_in", 0);
        user_basic.put("last_out", 0);

        //Alamat
        Map<String, Object> alamat = new HashMap<>();
        alamat.put("kabupaten", "");
        alamat.put("kecamatan", "");
        alamat.put("desa", "");
        alamat.put("dusun", "");
        alamat.put("rt", 0);
        alamat.put("rw", 0);

        Date currentTime = Calendar.getInstance().getTime();

        //penarikan
        Map<String, Object> last_out_inf = new HashMap<>();
        last_out_inf.put("jumlah", 0);
        last_out_inf.put("time", currentTime.toString());
        last_out_inf.put("izin", false);

        //Add Collcetion Extend
        Map<String, Object> user = new HashMap<>();
        Map<String, Object> user_informasi = new HashMap<>();
        user_informasi.put("user_basic",user_basic);
        user_informasi.put("alamat",alamat);
        user.put("informasi", user_informasi);
        user.put("sedang_transaksi", last_out_inf);

        // Add a new document with a generated ID
        db.collection("users").document(auth.getCurrentUser().getEmail()).set(user);

        Map<String, Object> pesan = new HashMap<>();
        pesan.put("count", 0);
        pesan.put("time", currentTime.toString());
        pesan.put("from", "System");
        pesan.put("fill", "Hallo, selamat bergabung dengan Ifonic");
        db.collection("users").document(auth.getCurrentUser().getEmail()).collection("chat").document().set(pesan);
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
    public void openDialogMe(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Saat ini pengambilan dana hanya dapat dilakukan di tempat pengelolaan langsung. \nLanjutkan membuka panel pencairan dana?");
        alertDialogBuilder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(BaseActivityHome.this, PencairanDana.class));
                        Toast.makeText(getApplicationContext(), "Panel terbuka", Toast.LENGTH_LONG).show();
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
    public void openDialogMe2(View view) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Deposit anda belum mencapai batas minimal pencairan. Batas minimal pencairan deposit adalah Rp "+formatter.format(min_cair)+",-");
        alertDialogBuilder.setPositiveButton("Mengerti",
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
