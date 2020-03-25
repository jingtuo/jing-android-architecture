package com.jing.android.arch.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jing.android.arch.component.BaseActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/**
 * 主页
 * @author JingTuo
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final long PRESS_BACK_TIME_DIFF = 1000L;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_lottery, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        int appLastRunState = preferences.getInt(DemoApp.APP_LAST_RUN_STATE, DemoApp.APP_LAST_RUN_STATE_DEFAULT);
        if (DemoApp.APP_LAST_RUN_STATE_KILLED_BY_SYSTEM == appLastRunState) {
            Toast.makeText(this, R.string.app_last_run_state_killed_by_system_prompt, Toast.LENGTH_SHORT).show();
        }
        preferences.edit().putInt(DemoApp.APP_LAST_RUN_STATE, DemoApp.APP_LAST_RUN_STATE_DEFAULT).apply();
    }

    private long lastPressBack = 0;

    @Override
    public void onBackPressed() {
        if (lastPressBack == 0) {
            lastPressBack = System.currentTimeMillis();
            Toast.makeText(this, R.string.press_back_again_exit_app, Toast.LENGTH_SHORT).show();
            return;
        }
        long dTime = System.currentTimeMillis() - lastPressBack;
        if (PRESS_BACK_TIME_DIFF < dTime) {
            lastPressBack = System.currentTimeMillis();
            Toast.makeText(this, R.string.press_back_again_exit_app, Toast.LENGTH_SHORT).show();
            return;
        }
        //此处不使用apply是因为apply是异步的，杀进程会导致无法正常保存数据
        boolean result = preferences.edit().putInt(DemoApp.APP_LAST_RUN_STATE, DemoApp.APP_LAST_RUN_STATE_USER_EXIT).commit();
        Log.d(TAG, "user exits app manually: " + result);
        Process.killProcess(Process.myPid());
    }
}
