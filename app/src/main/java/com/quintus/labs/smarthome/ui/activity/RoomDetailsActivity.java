package com.quintus.labs.smarthome.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quintus.labs.smarthome.R;
import com.quintus.labs.smarthome.adapter.DeviceAdapter;
import com.quintus.labs.smarthome.model.Device;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Smart Home
 * https://github.com/quintuslabs/SmartHome
 * Created on 27-OCT-2019.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
public class RoomDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Device> deviceList = new ArrayList<>();
    private static final String TAG = "Main_Activity";
    public static final String LIST_DEVICE = "device";
    public static String PATH ="test";
    private RecyclerView recyclerView;
    private DeviceAdapter mAdapter;
    TextView tvRoomName;
    ImageView ivScanDevice;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static FirebaseAuth mAuth;
    private FirebaseUser user;
    byte[] receiveData = new byte[1024];
    String data;

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        mAuth = FirebaseAuth.getInstance();
        user =mAuth.getCurrentUser();
        ivScanDevice =findViewById(R.id.ivScanDevice);
        ivScanDevice.setOnClickListener(this);
        tvRoomName =findViewById(R.id.tvRoomName);
        tvRoomName.setText(getIntent().getStringExtra("name_room"));
        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new DeviceAdapter(deviceList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareRoomData();
    }

    private void prepareRoomData() {
        database = FirebaseDatabase.getInstance();
        PATH=user.getEmail().substring(0,user.getEmail().indexOf('@'));
        myRef =database.getReference(LIST_DEVICE).child(PATH);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deviceList.clear();
                for(DataSnapshot deviceSnapshot:snapshot.getChildren()){
                    Device device =deviceSnapshot.getValue(Device.class);
                    deviceList.add(device);
                }
                Log.d(TAG, "onDataChange: "+deviceList.size());
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void onBackClicked(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.ivScanDevice:
                new CheckStatusTask().execute();
                break;
        }
    }
    private void sendUDPMessage(String msg,String msgOK) {
        try {
            Log.d("TESST",msg);
            DatagramSocket clientSocket = new DatagramSocket(9072);
            clientSocket.setBroadcast(true);
            InetAddress address = InetAddress.getByName("255.255.255.255");
            byte[] sendData;
            sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, 2709);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            data = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), "UTF-8");
            Log.d("TESST",data);
            clientSocket.close();
        } catch (Exception e) {
            Log.d("TESST",e.toString());
            e.printStackTrace();
        }

    }
    private class CheckStatusTask extends AsyncTask<Object, Object, Boolean> {
        protected Boolean doInBackground(Object... arg0) {
            sendUDPMessage(PATH +"@","12345RR");
            return true;
        }
        protected void onPostExecute(Boolean flag) {
            Log.d(TAG, "onPostExecute: "+"vao day  "+data);
            for(int i =0;i<2;i++){
                myRef = database.getReference(LIST_DEVICE).child(PATH).child(data +i);
                myRef.setValue(new Device(1,data+i,"Thiết bị "+i));
            }

        }
    }
}
