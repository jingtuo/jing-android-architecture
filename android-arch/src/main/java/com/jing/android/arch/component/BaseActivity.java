package com.jing.android.arch.component;

import android.content.ComponentCallbacks2;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 基于{@link android.content.ComponentCallbacks2}实现内存监控及优化
 * @author JingTuo
 */
public abstract class BaseActivity extends AppCompatActivity implements ComponentCallbacks2 {

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN == level) {
            onSwitchToBackground();
            return;
        }
    }

    /**
     * 当应用切换至后台
     */
    protected void onSwitchToBackground() {

    }
}
