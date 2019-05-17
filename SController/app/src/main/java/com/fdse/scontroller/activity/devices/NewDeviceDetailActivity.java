package com.fdse.scontroller.activity.devices;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fdse.scontroller.MainActivity;
import com.fdse.scontroller.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NewDeviceDetailActivity extends AppCompatActivity {
    private final String TAG = "[NewDeviceDetailActivity]";
    private Button mButton;
    private WebView mWebView;
    private TextView mTextView;

    private void initView(){
        mButton = findViewById(R.id.button_detail_return);
        mTextView = findViewById(R.id.textView_detail);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取参数
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        String device_name = intent.getStringExtra("device_name");
        String device_state = intent.getStringExtra("device_state");
        String device_detail = intent.getStringExtra("device_detail");
        String layout = intent.getStringExtra("layout");

        // 选择需要加载的布局
        if("webview".equals(layout)){
            setContentView(R.layout.activity_device_detail_webview);
            mWebView = findViewById(R.id.device_detail_webview);
            mWebView.getSettings().setJavaScriptEnabled(true);
            //支持屏幕缩放
            mWebView.getSettings().setSupportZoom(true);
            mWebView.getSettings().setBuiltInZoomControls(true);
            //不显示webview缩放按钮
            mWebView.getSettings().setDisplayZoomControls(false);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.loadUrl("http://www.126.com/");
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return super.shouldOverrideUrlLoading(view, url);
                }
                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mWebView.getSettings()
                                .setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                    }else{
                        try{
                            Method setMixedContentMode = WebSettings.class.getMethod("setMixedContentMode", int.class);
                            setMixedContentMode.invoke(mWebView.getSettings(), WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE); // 2 = MIXED_CONTENT_COMPATIBILITY_MODE
                            Log.d("WebSettings", "Successfully set MIXED_CONTENT_COMPATIBILITY_MODE");
                        }catch (NoSuchMethodException e){
                            Log.e("WebSettings", "Error getting setMixedContentMode method");
                        }catch (IllegalAccessException e){
                            Log.e("WebSettings", "Error getting setMixedContentMode method");
                        }catch (InvocationTargetException e){
                            Log.e("WebSettings", "Error getting setMixedContentMode method");
                        }
                    }
                }
            });
            mWebView.onResume();
        }else{
            setContentView(R.layout.activity_device_detail);
            initView();
            mTextView.setText("device_detail:"+device_detail);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 返回主界面
                    // Toast.makeText(NewDeviceDetailActivity.this,"To be continue...",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewDeviceDetailActivity.this, MainActivity.class);
                    intent.putExtra("fragment_id",0);
                    startActivity(intent);
                }
            });
        }


    }
}
