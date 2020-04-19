package com.jing.android.arch.component;

import android.content.ComponentCallbacks2;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * 基于{@link android.content.ComponentCallbacks2}实现内存监控及优化
 * @author JingTuo
 */
public abstract class BaseActivity extends AppCompatActivity implements ComponentCallbacks2 {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 需要在{@link AppCompatActivity#setContentView(int)} }或者{@link AppCompatActivity#setContentView(View)}之后调用
     * @param id
     * @param title
     * @param secondaryPage
     */
    protected void setActionBar(int id, CharSequence title, boolean secondaryPage) {
        Toolbar toolbar = findViewById(id);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (!TextUtils.isEmpty(title)) {
                actionBar.setTitle(title);
            }
            if (secondaryPage) {
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
