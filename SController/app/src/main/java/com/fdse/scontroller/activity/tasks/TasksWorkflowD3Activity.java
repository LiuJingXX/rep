package com.fdse.scontroller.activity.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fdse.scontroller.R;
import com.fdse.scontroller.constant.UrlConstant;

public class TasksWorkflowD3Activity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_workflow_d3);
        Intent intent =getIntent();
        int key = (int) intent.getSerializableExtra("key");

        webView = (WebView)findViewById(R.id.wv_tasks_workflow_d3);

        webView.getSettings().setJavaScriptEnabled(true);
        //支持屏幕缩放
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        webView.getSettings().setDisplayZoomControls(false);
        String serviceURL = UrlConstant.getAppBackEndServiceURL("?taskId="+key);
        webView.loadUrl(serviceURL);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

}
