package com.bisrihanafigmail.recyclingorganicwaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomMe extends AppCompatActivity {
    private WebView webView = null;
    Button send;
    final String styleme="<html><body>" +
            "<style>\n" +
            "ul{\n" +
            "  list-style: none;\n" +
            "  margin: 0;\n" +
            "  padding: 0;\n" +
            "}\n" +
            "\n" +
            "ul li{\n" +
            "  display:inline-block;\n" +
            "  clear: both;\n" +
            "  padding: 20px;\n" +
            "  border-radius: 30px;\n" +
            "  margin-bottom: 2px;\n" +
            "  font-family: Helvetica, Arial, sans-serif;\n" +
            "}\n" +
            "\n" +
            ".him{\n" +
            "  background: #eee;\n" +
            "  float: left;\n" +
            "}\n" +
            "\n" +
            ".me{\n" +
            "  float: right;\n" +
            "  background: #0084ff;\n" +
            "  color: #fff;\n" +
            "}\n" +
            "</style>\n" +
            "</head>"+
            "<ul>\n";;
    final String footer="</ul>" +
            "<script>window.scrollTo(0, document.body.scrollHeight);</script>" +
            "</body></html>";
    StringBuffer data;
    private FirebaseAuth auth;
    DocumentReference dbref;
    FirebaseFirestore db;
    CollectionReference idsRef;
    EditText papanInput;
    int count_maxed=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_me);
        webView=findViewById(R.id.webView);
        send=findViewById(R.id.senderbutton);
        papanInput=findViewById(R.id.papaninput);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        //data=" <li class=\"him\">By Other User</li>\n" +" <li class=\"me\">By this User, first message</li>\n" ;

        db.collection("users").document(auth.getCurrentUser().getUid()).collection("chat").orderBy("count").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                data=new StringBuffer("");
                count_maxed=queryDocumentSnapshots.size()+1;
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    if(doc.get("from").toString().trim().equals(auth.getCurrentUser().getDisplayName().toString().trim())){
                        data.append("<li class=\"me\">"+doc.get("fill")+"</li>\n");

                    }else{
                        data.append("<li class=\"him\">"+doc.get("fill")+"</li>\n");
                    }
                }
                webView.loadDataWithBaseURL(null,styleme+data+footer,"text/HTML","utf-8",null);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kirimPesan(papanInput.getText().toString());
                papanInput.setText("");
            }
        });
    }
    void kirimPesan(String isi){
        Map<String, Object> chat = new HashMap<>();
        chat.put("from", auth.getCurrentUser().getDisplayName());
        chat.put("count",count_maxed);
        chat.put("fill", isi);
        Date currentTime = Calendar.getInstance().getTime();
        chat.put("time", currentTime);
        db.collection("users").document(auth.getCurrentUser().getUid()).collection("chat").document().set(chat);
    }
}
