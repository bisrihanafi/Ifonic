package com.bisrihanafigmail.recyclingorganicwaste;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class PencairanDana extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pencairan_dana);
        TextView id=findViewById(R.id.id_pelanggan2);
        auth = FirebaseAuth.getInstance();
        id.setText(auth.getCurrentUser().getEmail());
    }
}
