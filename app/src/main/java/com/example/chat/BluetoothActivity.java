package com.example.chat;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothApp";
    private static final String DEVICE_ADDRESS = "B0:A7:32:DD:50:DE";
    private static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int MESSAGE_READ = 1;
    private static final int MESSAGE_WRITE = 2;
    private static final int CONNECTING_STATUS = 3;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private ConnectedThread connectedThread;
    private TextView statusTextView;

    private TextView receiveTextview ;
    private boolean connected = false;
    public long count = 0;
    Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);



        Button resetButton = findViewById(R.id.reset_button);
        Button connectButton = findViewById(R.id.connect_button);
//        Button sendButton = findViewById(R.id.send_button);
        Button backButton = findViewById(R.id.back_button);
        statusTextView = findViewById(R.id.status_textview);
        receiveTextview = findViewById(R.id.receive_textview);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // hiển thị count = 0
        String stringCount = String.valueOf(count);
        receiveTextview.setText(stringCount);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(BluetoothActivity.this,MainActivity.class);
                startActivity(intent3);
            }
        });
        connectToESP32();
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToESP32();
            }
        });

//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendDataToESP32("Hello ESP32!");
//            }
//        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                count = 0;
                String stringCount = String.valueOf(count);
                receiveTextview.setText(stringCount);
            }
        });
    }

    @SuppressLint("MissingPermission")

    public void connectToESP32() {
        device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
        statusTextView.setText("Connecting...");

        new Thread() {
            public void run() {
                try {
                    socket = device.createRfcommSocketToServiceRecord(DEVICE_UUID);
                    socket.connect();
                    mHandler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
                    connected = true;
                } catch (IOException e) {
                    Log.e(TAG, "Socket connection failed: " + e.getMessage());
                    mHandler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                }
            }
        }.start();
    }

    private void sendDataToESP32(String message) {
        if (connectedThread != null) {
            connectedThread.write(message.getBytes());
        }
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ: // nhan dữ liệu từ ESP
                    byte[] readBuffer = (byte[]) msg.obj;
                    String receivedMessage = new String(readBuffer, 0, msg.arg1);
                    Log.d(TAG, "Received message: " + receivedMessage);
                    String doiso = "h";
                    if(receivedMessage.equals(doiso)){
                        count++ ;
                        String stringCount = String.valueOf(count);
                        receiveTextview.setText(stringCount);
                        Log.d(TAG, "da nhan duocccc ");
                    }else {
                        Log.d(TAG, "da nhan duocccc ");

                    }

                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuffer = (byte[]) msg.obj;
                    String sentMessage = new String(writeBuffer);
                    Log.d(TAG, "Sent message: " + sentMessage);
                    break;
                case CONNECTING_STATUS:
                    int status = msg.arg1;
                    if (status == 1) {
                        statusTextView.setText("Connected to ESP32");
                        connectedThread = new ConnectedThread(socket);
                        connectedThread.start();
                    } else {
                        statusTextView.setText("Connection failed");
                    }
                    break;
            }
            return true;
        }
    });

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating I/O streams: " + e.getMessage());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int numBytes;

            while (true) {
                try {
                    numBytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(MESSAGE_READ, numBytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Input stream was disconnected: " + e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, bytes).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data: " + e.getMessage());
            }
        }
    }
}
