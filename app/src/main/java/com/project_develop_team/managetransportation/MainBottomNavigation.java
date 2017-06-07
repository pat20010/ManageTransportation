package com.project_develop_team.managetransportation;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

public class MainBottomNavigation extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener {

    private AHBottomNavigation ahBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bottom_navigation);
        ahBottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        ahBottomNavigation.setOnTabSelectedListener(this);
        setAhBottomNavigation();
    }

    private void setAhBottomNavigation() {
        AHBottomNavigationItem itemList = new AHBottomNavigationItem(getString(R.string.list), R.drawable.ic_task_list_box);
        AHBottomNavigationItem itemMap = new AHBottomNavigationItem(getString(R.string.map), R.drawable.ic_location_on_map);
        AHBottomNavigationItem itemContact = new AHBottomNavigationItem(getString(R.string.contact),R.drawable.ic_contact);
        AHBottomNavigationItem itemAccount = new AHBottomNavigationItem(getString(R.string.me), R.drawable.ic_round_account_me);

        ahBottomNavigation.setBehaviorTranslationEnabled(false);
        ahBottomNavigation.setTranslucentNavigationEnabled(true);
        ahBottomNavigation.addItem(itemList);
        ahBottomNavigation.addItem(itemMap);
        ahBottomNavigation.addItem(itemContact);
        ahBottomNavigation.addItem(itemAccount);
        ahBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        ahBottomNavigation.setSelectedBackgroundVisible(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        TaskListFragment taskListFragment = new TaskListFragment();

        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_id, taskListFragment).commit();
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        if (position == 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, new TaskListFragment()).commit();
        } else if (position == 1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, new MapFragment()).commit();
        } else if (position == 2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, new ContactFragment()).commit();
        } else if (position == 3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, new MeFragment()).commit();
        }
        return true;
    }
}