package com.quintus.labs.smarthome.adapter;


import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quintus.labs.smarthome.R;
import com.quintus.labs.smarthome.model.Device;
import com.quintus.labs.smarthome.model.Room;
import com.quintus.labs.smarthome.utils.SwitchButton;

import java.util.List;

import static com.quintus.labs.smarthome.ui.activity.RoomDetailsActivity.LIST_DEVICE;
import static com.quintus.labs.smarthome.ui.activity.RoomDetailsActivity.PATH;

/**
 * Smart Home
 * https://github.com/quintuslabs/SmartHome
 * Created on 27-OCT-2019.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder> {

    Context context;
    private List<Device> deviceList;

    public DeviceAdapter(List<Device> deviceList, Context context) {
        this.deviceList = deviceList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_room_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Device device = deviceList.get(position);
        holder.setData(device);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public CardView cardView;
        public EditText edNameDevice;
        SwitchButton sbToggle;
        public Device dv;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(LIST_DEVICE).child(PATH);
        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            cardView = view.findViewById(R.id.card_view);
            sbToggle =view.findViewById(R.id.sbToggle);
            edNameDevice =view.findViewById(R.id.edNameDevice);
            sbToggle.setChecked(true);
            title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    title.setVisibility(View.INVISIBLE);
                    edNameDevice.setText(title.getText().toString());
                    edNameDevice.setVisibility(View.VISIBLE);
                    return true;
                }
            });
            edNameDevice.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode==KeyEvent.KEYCODE_ENTER) {
                        title.setVisibility(View.VISIBLE);
                        edNameDevice.setVisibility(View.INVISIBLE);
                        myRef.child(dv.getId()).setValue(new Device(dv.getStatus(), dv.getId(), edNameDevice.getText().toString()));
                    }
                    return true;
                }

            });
            sbToggle.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    myRef.child(dv.getId()).setValue(new Device(1-dv.getStatus(), dv.getId(), dv.getName()));
                }
            });

        }

        public void setData(Device device) {
            dv = device;
            title.setText(device.getName());
            if(device.getStatus() ==1) sbToggle.setChecked(true);
            else sbToggle.setChecked(false);
        }
    }
}