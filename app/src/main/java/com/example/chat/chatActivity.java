package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.example.chat.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class chatActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference messagesRef;

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private EditText editTextMessage;
    private Button buttonSend;
    private FirebaseAuth mAuth;
    public  String sender = "";
    Button button_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        button_back = findViewById(R.id.button2);

        Intent receive = getIntent();
        sender = receive.getStringExtra("username");

        button_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent1 = new Intent(chatActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });
        database = FirebaseDatabase.getInstance();
        messagesRef = database.getReference("messages");

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        editTextMessage = findViewById(R.id.edit_text_message);
        buttonSend = findViewById(R.id.button_send);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editTextMessage.getText().toString();
                long timestamp = System.currentTimeMillis();

                DatabaseReference newMessageRef = messagesRef.push();
                newMessageRef.setValue(new Message(sender, content, timestamp));

                editTextMessage.setText("");
            }
        });

        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle changed messages if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle removed messages if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle moved messages if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }
}