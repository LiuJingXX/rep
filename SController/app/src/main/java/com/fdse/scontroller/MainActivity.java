package com.fdse.scontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fdse.scontroller.fragment.subfragment.HomeFragment;
import com.fdse.scontroller.fragment.subfragment.PersonFragment;
import com.fdse.scontroller.fragment.subfragment.TaskFragment;
import com.fdse.scontroller.heartbeatpackage.HeartBeatService;

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
                    if(homeFragment==null){
                        homeFragment = new HomeFragment();
                        transaction.add(R.id.main_fragment_container,homeFragment);
                    }else{
                        transaction.show(homeFragment);
                    }
                    break;
                case R.id.navigation_tasks:
                    if(taskFragment==null){
                        taskFragment = new TaskFragment();
                        transaction.add(R.id.main_fragment_container,taskFragment);
                    }else{
                        transaction.show(taskFragment);
                    }
                    break;
                case R.id.navigation_settings:
                    if(personFragment==null){
                        personFragment = new PersonFragment();
                        transaction.add(R.id.main_fragment_container,personFragment);
                    }else{
                        transaction.show(personFragment);
                    }
                    break;
            }
            transaction.commit();
            return true;
        }
    };

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction){
        if(homeFragment!=null){
            transaction.hide(homeFragment);
        }
        if(personFragment!=null){
            transaction.hide(personFragment);
        }
        if(taskFragment!=null){
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
        transaction.add(R.id.main_fragment_container,homeFragment);
        transaction.commit();
    }

}
