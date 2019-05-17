package com.fdse.scontroller.activity.devices;

import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fdse.scontroller.R;
import com.fdse.scontroller.view.MySurfaceView;
import com.zhy.autolayout.*;

import java.util.Locale;

public class MeasureDistActivity extends AutoLayoutActivity implements SensorEventListener {

    private Camera camera = null;
    private MySurfaceView mySurfaceView = null;

    private SensorManager sensorManager = null;
    private Sensor gyroSensor = null;//陀螺仪
    private FrameLayout preview;

    private float angle,distance;
    private double azimuth;

    private TextView mTvDistance, mTvAzimuth,mTvAngle,mInfomation;
    private int progress = 175;//高度

    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_distance);
        getViews();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preview = (FrameLayout) findViewById(R.id.camera_preview);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.release();
        camera = null;
        sensorManager.unregisterListener(this); // 解除监听器注册
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null) {
            camera = getCameraInstance();
        }
        //必须放在onResume中，不然会出现Home键之后，再回到该APP，黑屏
        mySurfaceView = new MySurfaceView(getApplicationContext(), camera);
        preview.addView(mySurfaceView);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_UI); //为传感器注册监听器
    }

    /*得到一相机对象*/
    private Camera getCameraInstance() {
        //Camera camera = null;
        try {
            camera = camera.open();
            //旋转90°
            camera.setDisplayOrientation(90);
            // 获取Camera parameters
            Camera.Parameters params = camera.getParameters();
            // 设置聚焦模式
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            // 设置Camera parameters
            camera.setParameters(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        /**
         *  方向传感器提供三个数据，分别为azimuth、pitch和roll。
         *  azimuth：方位，返回水平时磁北极和Y轴的夹角，范围为0°至360°。
         *  0°=北，90°=东，180°=南，270°=西。
         *  pitch：x轴和水平面的夹角，范围为-180°至180°。
         *  当z轴向y轴转动时，角度为正值。
         *  roll：y轴和水平面的夹角，由于历史原因，范围为-90°至90°。
         *  当x轴向z轴移动时，角度为正值。
         */
        azimuth = sensorEvent.values[0];
        angle = Math.abs(sensorEvent.values[1]);
        if (count % 5 == 0){
            mTvAzimuth.setText("设备所在的方位:"+ String.format(Locale.CHINA, "%.2f", azimuth));
            mTvAngle.setText("镜头角度：" + String.format(Locale.CHINA, "%.2f", angle));
        }
        distance = (float) (progress * Math.tan(angle * Math.PI / 180));
        if (distance < 0) {
            distance = -distance;
        }
        if (count % 5 == 0){
            mTvDistance.setText("与所测物体相距：" + String.format(Locale.CHINA, "%.2f", distance) + " cm");
            mInfomation.setText("请将十字对准设备在地面的投影");
        }

        count++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void getViews() {
        mInfomation = (TextView)findViewById(R.id.information);
        mTvAzimuth = (TextView)findViewById(R.id.azimuth);
        mTvAngle = (TextView) findViewById(R.id.angle);
        mTvDistance = (TextView) findViewById(R.id.distance);

    }

}