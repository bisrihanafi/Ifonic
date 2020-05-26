package com.bisrihanafigmail.recyclingorganicwaste;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomMe extends AppCompatActivity {
    private WebView webView = null;
    Button send;
    NotificationManager notif_manager;
    final String styleme="<html><head>" +
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
            "  padding: 10px;\n" +
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
            "div {\n" +
            "  background-color: lightgrey;\n" +

            "  border: 2px solid green;\n" +
            "  padding: 40px;\n" +
            "  margin: 20px;\n" +
            "}" +
            "</style>\n" +
            "</head><body>" +
            "<div>Ini adalah layanan pesan singkat pelanggan dan petugas, petugas yang membalas mungkin akan berbeda orang.</div>" +
            ""+
            "<ul>\n";;
    final String footer="</ul><br>" +
            "<script>window.scrollTo(0, document.body.scrollHeight);</script>" +
            "</body></html>";
    StringBuffer data;
    private FirebaseAuth auth;
    FirebaseFirestore db;
    DocumentReference dref;
    EditText papanInput;
    int count_maxed=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_me);
        webView=findViewById(R.id.webView);
        send=findViewById(R.id.senderbutton);
        papanInput=findViewById(R.id.papaninput);
        final WebSettings webSettings = webView.getSettings();
        notif_manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        notif_manager.cancelAll();

        dref=db.collection("users").document(auth.getCurrentUser().getEmail());
        dref.collection("chat").orderBy("count").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        data.append("<li class=\"him\">"+"<font style=\"color:blue\">@"+doc.get("from")+"</font> : "+doc.get("fill")+"</li>\n");
                    }
                }
                webView.loadDataWithBaseURL(null,styleme+data+footer,"text/HTML","utf-8",null);
                webView.scrollBy(0, webView.getVerticalScrollbarPosition()+1000);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!papanInput.getText().toString().trim().equalsIgnoreCase("")){
                    kirimPesan(papanInput.getText().toString());
                    papanInput.setText("");
                    notif_manager.cancelAll();
                }

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
        dref.collection("chat").document().set(chat);
    }

}
