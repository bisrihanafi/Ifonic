package com.bisrihanafigmail.recyclingorganicwaste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    String kabupaten_t,kecamatan_t,desa_t,dusun_t,rt_t,rw_t,alias;
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
                .document(auth.getCurrentUser().getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> map= (Map<String, Object>) document.getData().get("informasi");
                    Map<String, Object> map3= (Map<String, Object>) map.get("alamat");
                    if (document.exists()) {
                        kabupaten_t=map3.get("kabupaten").toString().trim();
                        kecamatan_t=map3.get("kecamatan").toString().trim();
                        desa_t=map3.get("desa").toString().trim();
                        dusun_t=map3.get("dusun").toString().trim();
                        rt_t=map3.get("rt").toString().trim();
                        rw_t=map3.get("rw").toString().trim();
                        if (kabupaten_t.equals("") || kecamatan_t.equals("") || desa_t.equals("") || dusun_t.equals("") || rt_t.equals("") || rw_t.equals("")){
                            Toast.makeText(getApplicationContext(),"Lengkapi informasi terlebih dahulu",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(FormInputUser.this, SettingLayout.class));
                            finish();
                        }
                    }
                    Map<String, Object> map4= (Map<String, Object>) map.get("user_basic");
                    if (document.exists()) {
                        alias=map4.get("nama").toString().trim();
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
                    kabupaten.setVisibility(View.GONE);
                    kecamatan.setVisibility(View.GONE);
                    desa.setVisibility(View.GONE);
                    dusun.setVisibility(View.GONE);
                    rt.setVisibility(View.GONE);
                    rw.setVisibility(View.GONE);
                    break;
            case R.id.use_new:
                if (checked)
                    usesetting=false;
                    kabupaten.setVisibility(View.VISIBLE);
                    kecamatan.setVisibility(View.VISIBLE);
                    desa.setVisibility(View.VISIBLE);
                    dusun.setVisibility(View.VISIBLE);
                    rt.setVisibility(View.VISIBLE);
                    rw.setVisibility(View.VISIBLE);
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
        quest.put("nama_alias", alias);
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
            String kabupaten_u=kabupaten.getText().toString().trim();
            String kecamatan_u=kecamatan.getText().toString().trim();
            String desa_u=desa.getText().toString().trim();
            String dusun_u=dusun.getText().toString().trim();
            String rt_u=rt.getText().toString().trim();
            String rw_u=rw.getText().toString().trim();
            if (!kabupaten_u.isEmpty() && !kecamatan_u.isEmpty() && !desa_u.isEmpty() && !dusun_u.isEmpty() && !rt_u.isEmpty() && !rw_u.isEmpty()) {
                quest.put("kabupaten", kabupaten_u);
                quest.put("kecamatan", kecamatan_u);
                quest.put("desa", desa_u);
                quest.put("dusun", dusun_u);
                quest.put("rt", rt_u);
                quest.put("rw", rw_u);
            }else{
                Toast.makeText(getApplicationContext(),"Lengkapi informasi terlebih dahulu",Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (use_catatan) {
            String cc=catatan.getText().toString().trim();
            if (!cc.isEmpty()) {
                quest.put("catatan", cc);
            }else {
                Toast.makeText(getApplicationContext(),"Lengkapi informasi terlebih dahulu",Toast.LENGTH_LONG).show();
                return;
            }
        }else{
            quest.put("catatan", "");
        }
        quest.put("id_admin", "");
        quest.put("nama_admin", "");
        quest.put("keterangan", "quest");
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
