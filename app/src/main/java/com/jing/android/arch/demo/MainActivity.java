package com.jing.android.arch.demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.jing.android.arch.component.BaseActivity;
import com.jing.android.arch.demo.databinding.ActivityMainBinding;
import com.jing.android.arch.demo.presenter.MainPresenter;
import com.jing.android.arch.demo.ui.home.HomeFragment;
import com.jing.android.arch.demo.ui.lottery.LotteryFragment;
import com.jing.android.arch.demo.ui.notifications.NotificationsFragment;
import com.jing.android.arch.repo.source.WritePreferenceSource;


import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 主页
 *
 * @author JingTuo
 */
public class MainActivity extends BaseActivity {

    private static final long PRESS_BACK_TIME_DIFF = 1000L;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        MainPresenter presenter = new MainPresenter();
        binding.setPresenter(presenter);
        binding.setLifecycleOwner(this);

        //设置标题默认值,actionBar.setTitle和presenter.setTitle缺一不可,暂时未相当恰当的方案
        MaterialToolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.home));
        }
        presenter.setTitle(getString(R.string.home));

        ViewPager viewPager = findViewById(R.id.view_pager);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            @Override
            public int getCount() {
                return 3;
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (0 == position) {
                    return new HomeFragment();
                } else if (1 == position) {
                    return new LotteryFragment();
                }
                return new NotificationsFragment();
            }
        };
        viewPager.setAdapter(adapter);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
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
        Process.killProcess(Process.myPid());

        mDisposable.add(Single.create(new WritePreferenceSource(this) {
            @Override
            protected void write(SharedPreferences.Editor editor) {
                super.write(editor);
                editor.putInt(DemoApp.APP_LAST_RUN_STATE, DemoApp.APP_LAST_RUN_STATE_USER_EXIT);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(bBoolean -> {
                            Process.killProcess(Process.myPid());
                        },
                        throwable -> {
                            Process.killProcess(Process.myPid());
                        }
                ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }
}
