package com.quintus.labs.smarthome.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Smart Home
 * https://github.com/quintuslabs/SmartHome
 * Created on 27-OCT-2019.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
public class RoomDetailsActivity extends AppCompatActivity {
    private List<Device> deviceList = new ArrayList<>();
    private static final String TAG = "Main_Activity";
    public static final String LIST_DEVICE = "device";
    public static String PATH ="test";
    private RecyclerView recyclerView;
    private DeviceAdapter mAdapter;
    TextView tvRoomName;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static FirebaseAuth mAuth;
    private FirebaseUser user;


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
//        PATH=user.getEmail().substring(0,user.getEmail().indexOf('@'));
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
}
