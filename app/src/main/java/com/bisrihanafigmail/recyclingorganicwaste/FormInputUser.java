package com.bisrihanafigmail.recyclingorganicwaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FormInputUser extends AppCompatActivity {
    boolean usesetting,usecatatan;
    EditText kabupaten,kecamatan,desa,dusun,rt,rw,catatan;
    String kabupaten_t,kecamatan_t,desa_t,dusun_t,rt_t,rw_t;
    ScrollView alamat2;
    Button ok;
    FirebaseFirestore db;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_input_user);
        kabupaten=findViewById(R.id.kabupaten2);
        kecamatan=findViewById(R.id.kecamatan2);
        desa=findViewById(R.id.desa2);
        dusun=findViewById(R.id.dusun2);
        rt=findViewById(R.id.rt2);
        rw=findViewById(R.id.rw2);
        alamat2=findViewById(R.id.scrollalamat);
        catatan=findViewById(R.id.catatan);
        ok=findViewById(R.id.ok);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance(); //Menghubungkan dengan Firebase Authentifikasi
        usecatatan=false;
        usesetting=true;
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buatQuest(usesetting, usecatatan);
            }
        });
        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> map= (Map<String, Object>) document.getData().get("informasi");
                    Map<String, Object> map3= (Map<String, Object>) map.get("alamat");
                    if (document.exists()) {
                        kabupaten_t=map3.get("kabupaten").toString();
                        kecamatan_t=map3.get("kecamatan").toString();
                        desa_t=map3.get("desa").toString();
                        dusun_t=map3.get("dusun").toString();
                        rt_t=map3.get("rt").toString();
                        rw_t=map3.get("rw").toString();
                    }
                }
            }
        });
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.use_setting:
                if (checked)
                    usesetting=true;
                    alamat2.setVisibility(View.GONE);

                break;
            case R.id.use_new:
                if (checked)
                    usesetting=false;
                    alamat2.setVisibility(View.VISIBLE);
                break;
        }
    }
    public void itemClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){
            catatan.setVisibility(View.VISIBLE);
            usecatatan=true;
        }else{
            catatan.setVisibility(View.GONE);
            catatan.setText("");
            usecatatan=false;
        }
    }
    void buatQuest(boolean use_setting, boolean use_catatan){
        // Create a new user with a first and last name
        Map<String, Object> quest = new HashMap<>();
        quest.put("id_user", auth.getCurrentUser().getEmail());
        quest.put("nama_user", auth.getCurrentUser().getDisplayName());
        Date currentTime = Calendar.getInstance().getTime();
        if(use_setting){
            quest.put("kabupaten", kabupaten_t);
            quest.put("kecamatan", kecamatan_t);
            quest.put("desa", desa_t);
            quest.put("dusun", dusun_t);
            quest.put("rt", rt_t);
            quest.put("rw", rw_t);
        }else {
            //alamt baru
            quest.put("kabupaten", kabupaten.getText().toString().trim());
            quest.put("kecamatan", kecamatan.getText().toString().trim());
            quest.put("desa", desa.getText().toString().trim());
            quest.put("dusun", dusun.getText().toString().trim());
            quest.put("rt", rt.getText().toString().trim());
            quest.put("rw", rw.getText().toString().trim());
        }

        if (use_catatan) {
            quest.put("catatan", catatan.getText().toString().trim());
        }else{
            quest.put("catatan", "");
        }
        quest.put("id_admin", "");
        quest.put("nama_admin", "");
        quest.put("keterangan", "proses");
        quest.put("time",currentTime.toString());
        //Toast.makeText(getApplicationContext(),kabupaten_t,Toast.LENGTH_LONG).show();


        // Add a new document with a generated ID
        db.collection("quests").document(auth.getCurrentUser().getEmail())
                .set(quest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(FormInputUser.this, WaitingLayout.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


    }
}
