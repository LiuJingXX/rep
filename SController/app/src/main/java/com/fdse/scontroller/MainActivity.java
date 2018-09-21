package com.fdse.scontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.fdse.scontroller.activity.tasks.TasksWorkflowD3Activity;
import com.fdse.scontroller.constant.Constant;
import com.fdse.scontroller.fragment.subfragment.HomeFragment;
import com.fdse.scontroller.fragment.subfragment.PersonFragment;
import com.fdse.scontroller.fragment.subfragment.TaskFragment;
import com.fdse.scontroller.heartbeatpackage.HeartBeatService;
import com.fdse.scontroller.service.MQTTService;
import com.fdse.scontroller.service.MessageEvent;
import com.fdse.scontroller.util.NotificationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends FragmentActivity {

    //fragmet嵌入在这里
    private FrameLayout main_frameLayout;
    //Fragment管理
    private FragmentManager fragmentManager;
    //Fragment类
    private HomeFragment homeFragment;
    private PersonFragment personFragment;
    private TaskFragment taskFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            hideAllFragment(transaction);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                        transaction.add(R.id.main_fragment_container, homeFragment);
                    } else {
                        transaction.show(homeFragment);
                    }
                    break;
                case R.id.navigation_tasks:
                    if (taskFragment == null) {
                        taskFragment = new TaskFragment();
                        transaction.add(R.id.main_fragment_container, taskFragment);
                    } else {
                        transaction.show(taskFragment);
                    }
                    break;
                case R.id.navigation_settings:
                    if (personFragment == null) {
                        personFragment = new PersonFragment();
                        transaction.add(R.id.main_fragment_container, personFragment);
                    } else {
                        transaction.show(personFragment);
                    }
                    break;
            }
            transaction.commit();
            return true;
        }
    };

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (personFragment != null) {
            transaction.hide(personFragment);
        }
        if (taskFragment != null) {
            transaction.hide(taskFragment);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(MainActivity.this, HeartBeatService.class);
        startService(serviceIntent);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //碎片管理
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //初始时假如homeFragment碎片
        homeFragment = new HomeFragment();
        transaction.add(R.id.main_fragment_container, homeFragment);
        transaction.commit();

        //调用mqtt
        EventBus.getDefault().register(this);
        startService(new Intent(this, MQTTService.class));
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMqttMessage1(MessageEvent messageEvent) {
        int eventType=messageEvent.getEventType();
        if(eventType== Constant.EVENT_TASK_MINE_NODE_COMPLETE){
            int taskId=messageEvent.getTaskId();
            String nodeId=messageEvent.getNodeId();
            String contentTitle="任务流程更新！";
            String contentText="任务"+taskId+"已完成至节点"+nodeId;
            int notificationId=taskId*10+eventType;//唯一标识该通知，之后用于唯一表示这个任务的pendingintent
            Intent intent = new Intent(this, TasksWorkflowD3Activity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("key", taskId);
            intent.putExtras(bundle);
            NotificationUtils.initNotification(this,contentTitle,contentText,intent,notificationId);
        }else if(eventType== Constant.EVENT_TASK_CROWDSOURCING_NEW){

        }else if(eventType== Constant.EVENT_TASK_SPECIFY_NEW){

        }
//        Log.i(MQTTService.TAG, "MainActivity收到消息:" + messageEvent.getMessage());
//        Toast.makeText(this, "MainActivity收到消息:" + messageEvent.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
