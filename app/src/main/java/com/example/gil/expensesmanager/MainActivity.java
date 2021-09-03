package com.example.gil.expensesmanager;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gil.expensesmanager.adapter.SlidingMenuAdapter;
import com.example.gil.expensesmanager.fragment.AddItemFragment;
import com.example.gil.expensesmanager.fragment.BudgetFragment;
import com.example.gil.expensesmanager.fragment.HistoryFragment;
import com.example.gil.expensesmanager.fragment.HomeFragment;
import com.example.gil.expensesmanager.fragment.ItemDetailsFragment;
import com.example.gil.expensesmanager.fragment.ListFragment;
import com.example.gil.expensesmanager.fragment.SettingsFragment;
import com.example.gil.expensesmanager.fragment.MySharedListFragment;
import com.example.gil.expensesmanager.fragment.SharedWithMeFragment;
import com.example.gil.expensesmanager.fragment.StatisticsFragment;
import com.example.gil.expensesmanager.fragment.UsersFragment;
import com.example.gil.expensesmanager.model.SlideMenuItems;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FragManager {

    private List<SlideMenuItems> listSliding;
    private SlidingMenuAdapter adapter;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    String userName = null;
    private String uid = null;

    public String[] mani = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public int[] grantResults = new int[1];
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        onRequestPermissionsResult(MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,mani,grantResults);

        //Init component
        listViewSliding = (ListView) findViewById(R.id.lv_sliding_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listSliding = new ArrayList<>();
        userName = (String) getIntent().getExtras().get("NAME");
        setUid((String) getIntent().getExtras().get("UID"));

        //Add item for sliding list
        listSliding.add(new SlideMenuItems(R.drawable.home, "Home"));
        listSliding.add(new SlideMenuItems(R.drawable.budget, "Budget"));
        listSliding.add(new SlideMenuItems(R.drawable.statistics, "Statistics"));
        listSliding.add(new SlideMenuItems(R.drawable.history, "History"));
        listSliding.add(new SlideMenuItems(R.drawable.settings, "Settings"));
        listSliding.add(new SlideMenuItems(R.drawable.exit, "Exit"));
        adapter = new SlidingMenuAdapter(this, listSliding);
        listViewSliding.setAdapter(adapter);

        //Display icon to open/ close sliding list
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set title
//        setTitle(listSliding.get(0).getTitle());
        setTitle("Hi " + userName + ",");
        //item selected
        listViewSliding.setItemChecked(0, true);
        //Close menu
        drawerLayout.closeDrawer(listViewSliding);

        //Display fragment when start
        replaceFragment(0);
        //Hanlde on item click
        listViewSliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Set title
                if(position == 0){
                    setTitle("Hi " + userName + ",");
                }else{
                    setTitle(listSliding.get(position).getTitle());
                }
                //item selected
                listViewSliding.setItemChecked(position, true);
                //Replace fragment
                replaceFragment(position);
                //Close menu
                drawerLayout.closeDrawer(listViewSliding);
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    //Create method replace fragment
    private void replaceFragment(int pos) {
        Fragment fragment = null;
        switch (pos) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                Bundle homBbundle = new Bundle();
                homBbundle.putString("UID",uid);
                homeFragment.setArguments(homBbundle);
                FragmentTransaction homTtransaction = getFragmentManager().beginTransaction();
                homTtransaction.replace(R.id.main_content, homeFragment);
                homTtransaction.addToBackStack("homeFragment");
                homTtransaction.commit();
                break;
            case 1:
                BudgetFragment budgetFragment = new BudgetFragment();
                Bundle bundle = new Bundle();
                bundle.putString("UID",getUid());
                budgetFragment.setArguments(bundle);
                FragmentTransaction budTransaction = getFragmentManager().beginTransaction();
                budTransaction.replace(R.id.main_content, budgetFragment);
                budTransaction.addToBackStack("budgetFragment");
                budTransaction.commit();
                break;
            case 2:
                fragment = new StatisticsFragment();
                Bundle staBbundle = new Bundle();
                staBbundle.putString("UID",uid);
                fragment.setArguments(staBbundle);
                FragmentTransaction staTtransaction = getFragmentManager().beginTransaction();
                staTtransaction.replace(R.id.main_content, fragment);
                staTtransaction.addToBackStack("statisticsFragment");
                staTtransaction.commit();
                break;
            case 3:
                fragment = new HistoryFragment();
                Bundle hisBbundle = new Bundle();
                hisBbundle.putString("UID",uid);
                fragment.setArguments(hisBbundle);
                FragmentTransaction hisTtransaction = getFragmentManager().beginTransaction();
                hisTtransaction.replace(R.id.main_content, fragment);
                hisTtransaction.addToBackStack("historyFragment");
                hisTtransaction.commit();
                break;
            case 4:
                fragment = new SettingsFragment();
                Bundle settingBbundle = new Bundle();
                settingBbundle.putString("UID",uid);
                fragment.setArguments(settingBbundle);
                FragmentTransaction settingTtransaction = getFragmentManager().beginTransaction();
                settingTtransaction.replace(R.id.main_content, fragment);
                settingTtransaction.addToBackStack("settingsFragment");
                settingTtransaction.commit();
                break;
            case 5:
                finish();
                break;
            default:
                fragment = new HomeFragment();
                break;
        }

        if(null!=fragment) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_content, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void addItemFragment(String uid) {
        AddItemFragment addItemFragment = new AddItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId",uid);
        addItemFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, addItemFragment);
        transaction.addToBackStack("addItem");
        transaction.commit();
    }

    @Override
    public void budgetFragment(String uid) {
        BudgetFragment budgetFragment = new BudgetFragment();
        Bundle bundle = new Bundle();
        bundle.putString("UID",uid);
        budgetFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, budgetFragment);
        transaction.addToBackStack("budget");
        transaction.commit();
    }

    @Override
    public void itemDetailsFragment(String uid,String itemId) {
        ItemDetailsFragment itemDetailsFragment = new ItemDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid",uid);
        bundle.putString("itemId",itemId);
        itemDetailsFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, itemDetailsFragment);
        transaction.addToBackStack("itemDetails");
        transaction.commit();
    }

    @Override
    public void statisticsFragment(String uid) {
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid",uid);
        statisticsFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, statisticsFragment);
        transaction.addToBackStack("statisticsFragment");
        transaction.commit();
    }

    @Override
    public void historyFragment(String uid) {
        HistoryFragment historyFragment = new HistoryFragment();
        Bundle hisBbundle = new Bundle();
        hisBbundle.putString("UID",uid);
        historyFragment.setArguments(hisBbundle);
        FragmentTransaction hisTtransaction = getFragmentManager().beginTransaction();
        hisTtransaction.replace(R.id.main_content, historyFragment);
        hisTtransaction.addToBackStack("historyFragment");
        hisTtransaction.commit();
    }

    @Override
    public void usersFragment(String uid) {
        UsersFragment usersFragment = new UsersFragment();
        Bundle userBbundle = new Bundle();
        userBbundle.putString("UID",uid);
        usersFragment.setArguments(userBbundle);
        FragmentTransaction userTtransaction = getFragmentManager().beginTransaction();
        userTtransaction.replace(R.id.main_content, usersFragment);
        userTtransaction.addToBackStack("usersFragment");
        userTtransaction.commit();
    }

    @Override
    public void MySharedListFragment(String uid) {
        MySharedListFragment sharedListFragment = new MySharedListFragment();
        Bundle sharedBbundle = new Bundle();
        sharedBbundle.putString("UID",uid);
        sharedListFragment.setArguments(sharedBbundle);
        FragmentTransaction sharedTtransaction = getFragmentManager().beginTransaction();
        sharedTtransaction.replace(R.id.main_content, sharedListFragment);
        sharedTtransaction.addToBackStack("sharedListFragment");
        sharedTtransaction.commit();
    }

    @Override
    public void WhoSharedWithMe(String uid) {
        SharedWithMeFragment sharedWithMeFragment = new SharedWithMeFragment();
        Bundle sharedBbundle = new Bundle();
        sharedBbundle.putString("UID",uid);
        sharedWithMeFragment.setArguments(sharedBbundle);
        FragmentTransaction sharedWithMeTtransaction = getFragmentManager().beginTransaction();
        sharedWithMeTtransaction.replace(R.id.main_content, sharedWithMeFragment);
        sharedWithMeTtransaction.addToBackStack("sharedWithMeFragment");
        sharedWithMeTtransaction.commit();
    }

    @Override
    public void listFragment(String uid,String toUidShare) {
        ListFragment listFragment = new ListFragment();
        Bundle listBbundle = new Bundle();
        listBbundle.putString("UID",uid);
        listBbundle.putString("UIDTOSHARE",toUidShare);
        listFragment.setArguments(listBbundle);
        FragmentTransaction listTtransaction = getFragmentManager().beginTransaction();
        listTtransaction.replace(R.id.main_content, listFragment);
        listTtransaction.addToBackStack("listFragment");
        listTtransaction.commit();
    }

    @Override
    public void settingsFragment(String uid) {
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle settingBbundle = new Bundle();
        settingBbundle.putString("UID",uid);
        settingsFragment.setArguments(settingBbundle);
        FragmentTransaction settingTtransaction = getFragmentManager().beginTransaction();
        settingTtransaction.replace(R.id.main_content, settingsFragment);
        settingTtransaction.addToBackStack("settingsFragment");
        settingTtransaction.commit();
    }

    @Override
    public void onBackPressed() {
        //get the last Fragment from the Stack
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity ", "- onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity ", "- onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity ", "- onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity ", "- onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity ", "- onDestroy()");
    }

}