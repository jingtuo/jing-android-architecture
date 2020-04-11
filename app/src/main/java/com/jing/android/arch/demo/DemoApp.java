package com.jing.android.arch.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.multidex.MultiDex;

import java.util.List;

/**
 * @author JingTuo
 */
public class DemoApp extends Application {

    public static final String APP_LAST_RUN_STATE = "app_last_run_state";

    public static final int APP_LAST_RUN_STATE_DEFAULT = 1;
    public static final int APP_LAST_RUN_STATE_USER_EXIT = 2;
    public static final int APP_LAST_RUN_STATE_KILLED_BY_SYSTEM = 3;
    public static final int APP_LAST_RUN_STATE_UNKNOWN = 4;

    @Override
    public void onCreate() {
        super.onCreate();
        startStrictMode();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * @return
     */
    private boolean isMainProcess() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int mainProcessId = -1;
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo process : processList) {
                if (process.processName.equals(getPackageName())) {
                    mainProcessId = process.pid;
                    break;
                }
            }
        }
        return Process.myPid() == mainProcessId;
    }

    private void startStrictMode() {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE == level) {
            //应用正在运行,系统内存降低,目前还可以正常运行
            return;
        }
        if (ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW == level
                || ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL == level) {
            //应用正在运行,系统内存降低,此时需要释放不影响页面显示的对象,否则应用将出现卡顿

            return;
        }
        if (ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN == level) {
            //应用切到后台,此时可以适当释放部分内存
            Toast.makeText(this, R.string.app_has_been_switched_to_background, Toast.LENGTH_SHORT).show();
            return;
        }
        if (ComponentCallbacks2.TRIM_MEMORY_COMPLETE == level) {
            //应用在LRU（least-recently used）队列中,应用将是第一个被杀死用于回收内存的,此时只能放弃挣扎,但是还是努力释放内存,并记录
            SharedPreferences preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            preferences.edit().putInt(APP_LAST_RUN_STATE, APP_LAST_RUN_STATE_KILLED_BY_SYSTEM).apply();
            Toast.makeText(this, R.string.app_will_be_killed_to_free_memory, Toast.LENGTH_SHORT).show();
            return;
        }
        if (ComponentCallbacks2.TRIM_MEMORY_BACKGROUND == level
                || ComponentCallbacks2.TRIM_MEMORY_MODERATE == level) {
            //应用在LRU（least-recently used）队列中,应用虽然不会是第一个被杀用于回收内存,这个要尽最大努力释放内存,避免被系统杀死

            return;
        }
        SharedPreferences preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        preferences.edit().putInt(APP_LAST_RUN_STATE, APP_LAST_RUN_STATE_UNKNOWN).putInt("trim_memory", level).apply();
    }
}
