package com.jing.android.arch.component;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * androidx库中,当使用{@link androidx.viewpager.widget.ViewPager}和{@link androidx.fragment.app.FragmentPagerAdapter}，
 * 当fragment生命状态为onStart不代表真正看到，详见下面日志
 * 首页进入页面：
 * HomeFragment: onCreate
 * LotteryFragment: onCreate
 * HomeFragment: onCreateView: true
 * HomeFragment: onViewCreated
 * HomeFragment: onActivityCreated
 * HomeFragment: onStart
 * LotteryFragment: onCreateView: true
 * LotteryFragment: onViewCreated
 * LotteryFragment: onActivityCreated
 * LotteryFragment: onStart
 * HomeFragment: onResume
 * 锁屏
 * HomeFragment: onPause
 * HomeFragment: onStop
 * LotteryFragment: onStop
 * 解锁
 * HomeFragment: onStart
 * LotteryFragment: onStart
 * HomeFragment: onResume
 * 切换至第二个tab
 * NotificationsFragment: onCreate
 * NotificationsFragment: onCreateView: true
 * NotificationsFragment: onViewCreated
 * NotificationsFragment: onActivityCreated
 * NotificationsFragment: onStart
 * HomeFragment: onPause
 * LotteryFragment: onResume
 *
 * @author JingTuo
 */
public class BaseFragment extends Fragment {

    private boolean containerIsViewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        containerIsViewPager = container instanceof ViewPager;
        Log.i(getClass().getSimpleName(), "onCreateView: " + containerIsViewPager);
        return inflater.inflate(layoutId(), container, false);
    }

    protected int layoutId() {
        return android.R.layout.list_content;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(getClass().getSimpleName(), "onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(getClass().getSimpleName(), "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(getClass().getSimpleName(), "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(getClass().getSimpleName(), "onStop");
    }

}
