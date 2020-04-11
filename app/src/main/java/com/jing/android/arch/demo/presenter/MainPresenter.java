package com.jing.android.arch.demo.presenter;

import android.util.Log;
import android.view.MenuItem;

import androidx.databinding.ObservableField;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jing.android.arch.demo.R;
import com.jing.android.arch.demo.binding.ListenerAdapter;

/**
 * 主页的Presenter
 * @author JingTuo
 */
public class MainPresenter implements ListenerAdapter.OnPageSelectedListener {


    private boolean ignoreNavItemSelected = false;

    private boolean ignoreViewPagerSelected = false;

    private ObservableField<CharSequence> title = new ObservableField<>();

    /**
     * DataBinding根据xml中的属性onNavigationItemSelectedListener
     * 调用View的setOnNavigationItemSelectedListener方法，
     * 最终调用此方法
     * @param item
     * @param viewPager
     * @return
     */
    public boolean onNavigationItemSelected(MenuItem item, ViewPager viewPager) {
        title.set(item.getTitle());
        if (ignoreNavItemSelected) {
            ignoreNavItemSelected = false;
            return true;
        }
        ignoreViewPagerSelected = true;
        if (R.id.navigation_home == item.getItemId() && viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0,  false);
        } else if (R.id.navigation_lottery == item.getItemId() && viewPager.getCurrentItem() != 1) {
            viewPager.setCurrentItem(1, false);
        } else if (R.id.navigation_notifications == item.getItemId() && viewPager.getCurrentItem() != 2) {
            viewPager.setCurrentItem(2, false);
        }
        return true;
    }


    @Override
    public void onPageSelected(ViewPager viewPager, int position, BottomNavigationView navView) {
        if (ignoreViewPagerSelected) {
            ignoreViewPagerSelected = false;
            return;
        }
        int navCurrentSelectedId = navView.getSelectedItemId();
        ignoreNavItemSelected = true;
        if (0 == position && navCurrentSelectedId != R.id.navigation_home) {
            navView.setSelectedItemId(R.id.navigation_home);
        } else if (1 == position && navCurrentSelectedId != R.id.navigation_lottery) {
            navView.setSelectedItemId(R.id.navigation_lottery);
        } else if (2 == position && navCurrentSelectedId != R.id.navigation_notifications) {
            navView.setSelectedItemId(R.id.navigation_notifications);
        }
    }

    public ObservableField<CharSequence> getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title.set(title); ;
    }
}
