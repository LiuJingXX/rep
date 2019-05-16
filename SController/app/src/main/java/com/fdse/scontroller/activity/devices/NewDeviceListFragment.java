package com.fdse.scontroller.activity.devices;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fdse.scontroller.R;
import com.fdse.scontroller.adapter.HomeDeviceAdapter;
import com.fdse.scontroller.http.HttpUtil;
import com.fdse.scontroller.model.HomeDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewDeviceListFragment extends Fragment  {
    private ListView listView;
    private String deviceList="";
    private SwipeRefreshLayout swipeRefreshView;
    private HomeDeviceAdapter homeDeviceAdapter;
    private List<HomeDevice> homeDeviceList = new ArrayList<HomeDevice>();

    private void initView(View view){
        listView = (ListView) view.findViewById(R.id.list_view);
        swipeRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_home_device);
    }

    private void initHomeDevice(){
        final String TAG = "[initHomeDevice]";
        getDeviceList();
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        try {
            JSONArray jsonArray = new JSONArray(deviceList);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                String attributes = result.getString("attributes");
                String entity_id = result.getString("entity_id");
                String state = result.getString("state");
                String friendly_name = "";
                try {
                    JSONObject jobj = new JSONObject(attributes);
                    friendly_name = jobj.getString("friendly_name");
                } catch (JSONException e) {
                    Log.d(TAG, "Device No Friendly Name:" + entity_id);
                    continue;
                }
//                        Log.d(TAG, "getDeviceList: friendly_name="+friendly_name);
//                        Log.d(TAG, "getDeviceList: state="+state);
                AppendDeviceList(entity_id, friendly_name, state);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    private void getDeviceList(){
        // url = http://192.168.1.8:8123/api/states
        // -H Content-Type=application/json
        // -H X-HA-Access=123456
        // GET
        final String TAG = "[initHomeDevice]";

        HttpUtil httpUtil = new HttpUtil();
        httpUtil.getHASSApiState("http://192.168.1.8:8123/api/states", "123456", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                deviceList = response.body().string();
                //Log.d(TAG, "onResponse: " + deviceList);

            }
        });
    }
//    private void getDeviceList(){
//        // url = http://192.168.1.8:8123/api/states
//        // -H Content-Type=application/json
//        // -H X-HA-Access=123456
//        // GET
//        final String TAG = "[getDeviceList]";
//        String attributes;
//        String state;
//        String entity_id;
//        JSONObject jobj;
//        String friendly_name;
//
//        HttpService httpService = new HttpService();
//        try{
//            deviceList = httpService.getHASSApiState("http://192.168.1.8:8123/api/states","123456");
//
//            Log.d(TAG, deviceList);
//            JSONArray jsonArray = new JSONArray(deviceList);
//            for(int i=0;i<jsonArray.length();i++){
//                JSONObject result = jsonArray.getJSONObject(i);
//                attributes = result.getString("attributes");
//                entity_id = result.getString("entity_id");
//                state = result.getString("state");
//                try{
//                    jobj = new JSONObject(attributes);
//                    friendly_name = jobj.getString("friendly_name");
//                }catch (JSONException e){
//                    Log.d(TAG, "Device No Friendly Name:"+entity_id);
//                    continue;
//                }
////                Log.d(TAG, "getDeviceList: friendly_name="+friendly_name);
////                Log.d(TAG, "getDeviceList: state="+state);
//                AppendDeviceList(entity_id,friendly_name,state);
//
//            }
//
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//        catch (JSONException e){
//            e.printStackTrace();
//        }
//    }

    private void AppendDeviceList(String entity_id,String friendly_name, String state) {
        // sensor group switch
        //entity_id.split(".");
        HomeDevice homeDevice = new HomeDevice(R.drawable.home_saodijiqiren,friendly_name,state);
        homeDeviceList.add(homeDevice);
    }

    private void setSwipeRefresh(View view){

        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                //homeDeviceAdapter.clear();
                homeDeviceList.clear();
                //homeDeviceAdapter.notifyDataSetChanged();
                initHomeDevice();
                homeDeviceAdapter.notifyDataSetChanged();
                //listView.setAdapter(homeDeviceAdapter);
                swipeRefreshView.setRefreshing(false);
            }
        });
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
        listView.setAdapter(homeDeviceAdapter);

        //设置下拉刷新
        setSwipeRefresh(view);


        return view;
    }
}
