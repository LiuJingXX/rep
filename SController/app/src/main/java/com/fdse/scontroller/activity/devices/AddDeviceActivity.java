package com.fdse.scontroller.activity.devices;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fdse.scontroller.R;
import com.fdse.scontroller.activity.BaseActivity;
import com.fdse.scontroller.activity.tasks.TasksWorkflowActivity;
import com.fdse.scontroller.adapter.HomeDeviceAdapter;
import com.fdse.scontroller.constant.Constant;
import com.fdse.scontroller.constant.UrlConstant;
import com.fdse.scontroller.constant.UserConstant;
import com.fdse.scontroller.http.HttpUtil;
import com.fdse.scontroller.model.HomeDevice;
import com.fdse.scontroller.util.PhotoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static java.security.AccessController.getContext;

public class AddDeviceActivity extends BaseActivity {

    private ImageView photo;
    private ImageView previous;
    private ImageView next;
    private FloatingActionButton fab;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    SharedPreferences preferences;
    private ListView listView;
    private String[] listViewData;
    private List<String> urlList = new ArrayList<>();
    private int currentImage;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        preferences = getSharedPreferences(Constant.PREFERENCES_USER_INFO, Activity.MODE_PRIVATE);
        listView = findViewById(R.id.list_view_serach_devices);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Toast.makeText(AddDeviceActivity.this, "第" + i + "行", Toast.LENGTH_LONG).show();
        });
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        photo = findViewById(R.id.photo);
        previous = findViewById(R.id.previous_photo);
        next = findViewById(R.id.next_photo);
        previous.setOnClickListener(v -> {
            currentImage-=1;
            showDeviceInfo();
        });
        next.setOnClickListener(v -> {
            currentImage+=1;
            showDeviceInfo();
        });
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> gotoCaptureCrop());
        gotoCaptureCrop();

//        try {
//            showDeviceList(responceData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
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
//                        showImages(bitmap);
                        uploadImage(cropImageUri.getPath());
                    }
                    break;
            }
        }
    }


    //将网络图片转换成bitmap
    public Bitmap getHttpBitmap(String url) {
        URL httpUrl = null;
        Bitmap bitmap = null;
        try {
            httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            InputStream in = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "图片下载失败，请稍后再试", Toast.LENGTH_SHORT).show();
        }
        return bitmap;

    }

    private void showImages(Bitmap bitmap) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(currentImage==0){
                    previous.setVisibility(View.INVISIBLE);
                }else {
                    previous.setVisibility(View.VISIBLE);
                }

                if(currentImage==(urlList.size()-1)){
                    next.setVisibility(View.INVISIBLE);
                }else {
                    next.setVisibility(View.VISIBLE);
                }
                photo.setImageBitmap(bitmap);
            }
        });

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
                    if (url.contains("url")) {
                        url = url.split("url = ")[1];
                    }
                    urlList.add(url);
                    currentImage = urlList.size() - 1;
                    searchDeviceInfo(url);
                    showDeviceInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                    showUploadFailed("图片上传失败，获取url失败");
                }

            }
        });
    }

    private void showDeviceInfo() {
        //显示上面的图片
        String url = urlList.get(currentImage);
        new Thread(() -> showImages(getHttpBitmap(url))).start();
        //查找下面数据数据信息
        //清空数据显示
        listViewData = new String[0];
        initAdapter();
        final HashMap<String, String> postData = new HashMap<String, String>();
        String serviceURL = UrlConstant.getPailitaoServiceURL(UrlConstant.PAILITAO_GET_DEVICE_INFO);
        postData.put("url", url);
        HttpUtil.doPost(serviceURL, postData, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showUploadFailed("获取设备信息失败,加载默认数据");
                String responceData = "{\n" +
                        "\"url\":\"www.baidu.com\",\n" +
                        "\"data\":[\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品名称\": \"罗技无线鼠标M171\",\n" +
                        "             \"商品编号\": \"2177845\",\n" +
                        "             \"产品类型\": \"便携鼠标\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "           \"商品名称\": \"罗技无线鼠标M172\",\n" +
                        "           \"商品编号\": \"2177845\",\n" +
                        "           \"产品类型\": \"便携鼠标\"\n" +
                        "         }\n" +
                        "       ]\n" +
                        "}";
                try {
                    showDeviceList(responceData);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responceData = response.body().string();
                    //设置数据
                    showDeviceList(responceData);
                } catch (Exception e) {
                    e.printStackTrace();
                    showUploadFailed("设备信息解析失败");
                }
            }
        });
    }

    private void searchDeviceInfo(String url) {
        final HashMap<String, String> postData = new HashMap<String, String>();
        String serviceURL = UrlConstant.getPailitaoServiceURL(UrlConstant.PAILITAO_GET_DEVICE_INFO);
        postData.put("url", url);
        HttpUtil.doPost(serviceURL, postData, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showUploadFailed("设备信息请求错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                showUploadFailed("正在查询设备信息，请稍等。");
//                try {
//                    String responceData = response.body().string();
//                    //设置数据
//                    showDeviceList(responceData);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    showUploadFailed("设备信息解析失败");
//                }
            }
        });
    }

    private void showDeviceList(String responceData) throws JSONException {
        JSONObject jsonObject = null;
        jsonObject = new JSONObject(responceData);
        String url = (String) jsonObject.get("url");
        JSONArray list = (JSONArray) jsonObject.get("data");
        listViewData = new String[list.length()];
        for (int i = 0; i < list.length(); i++) {
            JSONObject device = (JSONObject) list.get(i);
            String deivceInfo = device.toString();
            listViewData[i] = deivceInfo;
        }
        initAdapter();
    }

    private void initAdapter() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new ArrayAdapter<String>(
                        AddDeviceActivity.this, android.R.layout.simple_list_item_1, listViewData);
                listView.setAdapter(adapter);
            }
        });
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
