package com.fdse.scontroller.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.fdse.scontroller.R;
import com.fdse.scontroller.fragment.subfragment.HomeFragment;


public class NavigationFragment extends Fragment implements BottomNavigationBar.OnTabSelectedListener{

    private BottomNavigationBar bottomNavigationBar;
    private HomeFragment mHomeFragment;
    private TextView textView;

    public static NavigationFragment newInstance(String s) {
        NavigationFragment navigationFragment = new NavigationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("args", s);
        navigationFragment.setArguments(bundle);
        return navigationFragment;
    }

    @Override
    public void onTabSelected(int position) {

    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_navigation_bar,container,false);
        bottomNavigationBar = (BottomNavigationBar) view.findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);

        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.home_fill,"HOME").setInactiveIconResource(R.drawable.home))
                .addItem(new BottomNavigationItem(R.drawable.home_fill,"TASK").setInactiveIconResource(R.drawable.home))
                .addItem(new BottomNavigationItem(R.drawable.home_fill,"PERSON").setInactiveIconResource(R.drawable.home))
                .setFirstSelectedPosition(0)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(this);

        setDefaultFragment();
        return view;
    }

    private void setDefaultFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        HomeFragment homeFragment = mHomeFragment.newInstance("HOME");
        transaction.replace(R.id.sub_content,homeFragment).commit();

    }
}
