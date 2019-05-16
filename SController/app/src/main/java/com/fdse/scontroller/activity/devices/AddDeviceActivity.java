package com.fdse.scontroller.activity.devices;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fdse.scontroller.R;
import com.fdse.scontroller.activity.BaseActivity;
import com.fdse.scontroller.adapter.HomeDeviceAdapter;
import com.fdse.scontroller.constant.Constant;
import com.fdse.scontroller.constant.UrlConstant;
import com.fdse.scontroller.constant.UserConstant;
import com.fdse.scontroller.http.HttpUtil;
import com.fdse.scontroller.model.HomeDevice;
import com.fdse.scontroller.util.PhotoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class AddDeviceActivity extends BaseActivity {

    private ImageView photo;
    private FloatingActionButton fab;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    SharedPreferences preferences;
    private List<HomeDevice> deviceList = new ArrayList<HomeDevice>();
    private HomeDeviceAdapter homeDeviceAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        preferences = getSharedPreferences(Constant.PREFERENCES_USER_INFO, Activity.MODE_PRIVATE);
        listView = (ListView) findViewById(R.id.list_view);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        photo = (ImageView) findViewById(R.id.photo);
        fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoCaptureCrop();
            }
        });
    }

    // 拍照 + 裁切
    private void gotoCaptureCrop() {
        requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
            @Override
            public void granted() {
                if (hasSdcard()) {
                    imageUri = Uri.fromFile(fileUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        //通过FileProvider创建一个content类型的Uri
                        imageUri = FileProvider.getUriForFile(AddDeviceActivity.this, "com.fdse.scontroller.fileprovider", fileUri);
                    PhotoUtils.takePicture(AddDeviceActivity.this, imageUri, CODE_CAMERA_REQUEST);
                } else {
                    Toast.makeText(AddDeviceActivity.this, "设备没有SD卡！", Toast.LENGTH_SHORT).show();
                    Log.e("asd", "设备没有SD卡");
                }
            }

            @Override
            public void denied() {
                Toast.makeText(AddDeviceActivity.this, "部分权限获取失败，正常功能受到影响", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int output_X = 480, output_Y = 480;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
                    cropImageUri = Uri.fromFile(fileCropUri);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                    break;
                case CODE_GALLERY_REQUEST://访问相册完成回调
                    if (hasSdcard()) {
                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            newUri = FileProvider.getUriForFile(this, "com.fdse.scontroller.fileprovider", new File(newUri.getPath()));
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                    } else {
                        Toast.makeText(AddDeviceActivity.this, "设备没有SD卡!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    if (bitmap != null) {
                        showImages(bitmap);
                        uploadImage(cropImageUri.getPath());
                    }
                    break;
            }
        }
    }


    private void showImages(Bitmap bitmap) {
        photo.setImageBitmap(bitmap);
    }

    private void uploadImage(String filePath) {
        final HashMap<String, String> postData = new HashMap<String, String>();
        String serviceURL = UrlConstant.getFlieServiceURL(UrlConstant.FILE_ADD_DEVICE_IMAGE);
        postData.put("userId", String.valueOf(preferences.getInt("userId", 0)));
        HttpUtil.uploadImage(serviceURL, filePath, postData, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showUploadFailed("图片上传失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String url = response.body().string();
                    getDeviceInfo(url);
                } catch (Exception e) {
                    e.printStackTrace();
                    showUploadFailed("图片上传失败，获取url失败");
                }

            }
        });
    }

    private void getDeviceInfo(String url) {
        final HashMap<String, String> postData = new HashMap<String, String>();
        String serviceURL = UrlConstant.getPailitaoServiceURL(UrlConstant.PAILITAO_GET_DEVICE_INFO);
        postData.put("url", url);
        HttpUtil.doPost(serviceURL, postData, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showUploadFailed("获取设备信息失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responceData = response.body().string();
                    //设置数据
                    getDeviceList(responceData);
                } catch (Exception e) {
                    e.printStackTrace();
                    showUploadFailed("设备信息解析失败");
                }

            }
        });
    }

    private void getDeviceList(String responceData) throws JSONException {
        JSONObject jsonObject = null;
        jsonObject = new JSONObject(responceData);
        String url = (String) jsonObject.get("url");
        List<Object> list = (List<Object>) jsonObject.get("data");
        for(int i = 0; i < list.size(); i++){
            HomeDevice homeDevice1 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
            deviceList.add(homeDevice1);
        }
        //显示查询到的设备信息（列表）
        showDeviceList();
    }

    private void showDeviceList() throws JSONException {
        homeDeviceAdapter = new HomeDeviceAdapter(this,
                R.layout.item_home_deivce, deviceList);
        listView.setAdapter(homeDeviceAdapter);
    }


    private void showUploadFailed(String error) {
        Snackbar.make(fab, error, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

}
