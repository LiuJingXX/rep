package com.fdse.scontroller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Context context = getApplicationContext();
        try {
            Thread.sleep(4000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        if(TokenMessage.getInstance().getUserToken(context,"Token") == null){
            Toast.makeText(StartActivity.this,"AutoLogin Failed",Toast.LENGTH_LONG).show();
            Intent intent0 = new Intent(StartActivity.this,LoginActivity.class);
            startActivity(intent0);
        }else{
            /*String message = WebService.executeHttpGet(mEmail,mPassword);*/
        }
    }
}
