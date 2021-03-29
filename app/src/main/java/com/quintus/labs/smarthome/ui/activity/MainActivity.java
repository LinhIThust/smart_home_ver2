package com.quintus.labs.smarthome.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quintus.labs.smarthome.R;
import com.quintus.labs.smarthome.adapter.RoomAdapter;
import com.quintus.labs.smarthome.model.Room;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Smart Home
 * https://github.com/quintuslabs/SmartHome
 * Created on 27-OCT-2019.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
public class MainActivity extends AppCompatActivity {
    private List<Room> roomList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RoomAdapter mAdapter;


    RelativeLayout home_rl, time_rl, setting_rl, scene_rl;
    TextView tvNameAccout;
    CircleImageView ivAvatar;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static final String LIST_ROOM = "room";


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
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        user =mAuth.getCurrentUser();

        tvNameAccout =findViewById(R.id.tvNameAccout);
        ivAvatar =findViewById(R.id.ivAvatar);
        home_rl = findViewById(R.id.home_rl);
        time_rl = findViewById(R.id.time_rl);
        scene_rl = findViewById(R.id.scene_rl);
        setting_rl = findViewById(R.id.setting_rl);

        recyclerView = findViewById(R.id.recycler_view);
        Log.d("TAGz", "onCreate: "+user.getPhotoUrl());
        Picasso.get().load(user.getPhotoUrl()).into(ivAvatar);
        tvNameAccout.setText(user.getDisplayName());
        home_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home_rl.setBackgroundResource(0);
            }
        });

        mAdapter = new RoomAdapter(roomList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        database = FirebaseDatabase.getInstance();
        myRef =database.getReference(LIST_ROOM).child("PATH");
        prepareRoomData();
    }

    private void prepareRoomData() {
//        Room room = new Room("1", "Phòng 1");
//        roomList.add(room);
//        room = new Room("2", "Phòng 2");
//        roomList.add(room);
//        room = new Room("1", "Phòng 3");
//        roomList.add(room);
//        room = new Room("2", "Phòng 4");
//        roomList.add(room);
//        room = new Room("1", "Phòng 5");
//        roomList.add(room);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<Room> list = new ArrayList<>();
                roomList.clear();
                for(DataSnapshot deviceSnapshot:dataSnapshot.getChildren()){
                    Room device =deviceSnapshot.getValue(Room.class);
                    roomList.add(device);
                }

//                Log.d("TAGz", "onDataChange: "+roomList.size()+" x "+list.size());
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAGz", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
