package com.fdse.scontroller.fragment.subfragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fdse.scontroller.R;
import com.fdse.scontroller.adapter.HomeDeviceAdapter;
import com.fdse.scontroller.customview.HomeDevice;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ListView listView;
    private List<HomeDevice> homeDeviceList = new ArrayList<HomeDevice>();

    public static HomeFragment newInstance(String s) {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_home, container, false);

        //获取（初始化设备数据）
        initHomeDevice();
        HomeDeviceAdapter homeDeviceAdapter = new HomeDeviceAdapter(getActivity(),
                R.layout.item_home_deivce, homeDeviceList);
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(homeDeviceAdapter);
        return view;
    }

    private void initHomeDevice(){
        HomeDevice homeDevice1 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice1);
        HomeDevice homeDevice2 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice2);
        HomeDevice homeDevice3 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice3);
        HomeDevice homeDevice4 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice4);
        HomeDevice homeDevice5 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice5);
        HomeDevice homeDevice6 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice6);
        HomeDevice homeDevice7 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice7);
        HomeDevice homeDevice8 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice8);
        HomeDevice homeDevice9 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice9);


    }
}
