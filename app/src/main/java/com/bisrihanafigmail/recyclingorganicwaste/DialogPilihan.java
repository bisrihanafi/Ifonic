package com.bisrihanafigmail.recyclingorganicwaste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

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
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.ambil:
                if (checked)
                    metode=true;
                    break;
            case R.id.antar:
                if (checked)
                    metode=false;
                    break;
        }
    }
}
