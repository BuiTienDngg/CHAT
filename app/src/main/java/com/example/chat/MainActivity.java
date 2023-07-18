package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
public class MainActivity extends AppCompatActivity {
    Button button_chat;
    Button button_bluetooth;
    EditText username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ánh xạ id
        button_chat = findViewById(R.id.chat);
        button_bluetooth = findViewById(R.id.button3);
        username = findViewById(R.id.username);
        // xử lý button
        button_chat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent2 = new Intent(MainActivity.this, chatActivity.class);
                String a = username.getText().toString();
                intent2.putExtra("username",a);
                startActivity(intent2);
            }
        });

        button_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(intent);
            }
        });
    }
}