package com.fdse.scontroller.activity.devices;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fdse.scontroller.R;
import com.fdse.scontroller.adapter.HomeDeviceAdapter;
import com.fdse.scontroller.model.HomeDevice;

import java.util.ArrayList;
import java.util.List;

public class NewDeviceListFragment extends Fragment  {
    private ListView listView;
    private HomeDeviceAdapter homeDeviceAdapter;
    private List<HomeDevice> homeDeviceList = new ArrayList<HomeDevice>();

    private void initView(View view){
        listView = (ListView) view.findViewById(R.id.list_view);
    }

    private void initHomeDevice(){
        HomeDevice homeDevice1 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice1);
    }

    private void getDeviceList(){
        // url = http://192.168.1.8:8123/api/states
        // -H Content-Type=application/json
        // -H X-HA-Access=123456
        // POST
    }

    private void deviceListParse(){
        // sensor group switch
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_device_list, container, false);
        initView(view);

        //获取传感器等物理设备数据
        initHomeDevice();
        homeDeviceAdapter = new HomeDeviceAdapter(getActivity(),
                R.layout.item_home_deivce, homeDeviceList);
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(homeDeviceAdapter);
        return view;
    }
}
