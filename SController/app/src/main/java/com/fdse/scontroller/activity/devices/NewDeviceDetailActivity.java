package com.fdse.scontroller.activity.devices;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fdse.scontroller.MainActivity;
import com.fdse.scontroller.R;

public class NewDeviceDetailActivity extends AppCompatActivity {
    private final String TAG = "[NewDeviceDetailActivity]";
    private Button mButton;
    private TextView mTextView;

    private void initView(){
        mButton = findViewById(R.id.button_detail_return);
        mTextView = findViewById(R.id.textView_detail);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        initView();
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        String device_name = intent.getStringExtra("device_name");
        String device_state = intent.getStringExtra("device_state");
        String device_detail = intent.getStringExtra("device_detail");

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
