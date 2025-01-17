package com.bisrihanafigmail.recyclingorganicwaste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DialogPilihan extends AppCompatActivity {
    Button next;
    boolean metode=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_pilihan);
        next=findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(metode) {
                    startActivity(new Intent(DialogPilihan.this, FormInputUser.class));
                    finish();
                }else{
                    startActivity(new Intent(DialogPilihan.this, ShowID.class));
                    finish();
                }

            }
        });
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.ambil:
                metode=true;
                break;
            case R.id.antar:
                metode=false;
                break;
        }
    }
}
