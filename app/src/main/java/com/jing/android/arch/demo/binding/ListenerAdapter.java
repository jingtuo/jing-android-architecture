package com.jing.android.arch.demo.binding;

import androidx.databinding.BindingAdapter;
import androidx.databinding.adapters.ListenerUtil;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jing.android.arch.demo.R;

/**
 * @author JingTuo
 */
public class ListenerAdapter {

    @BindingAdapter(value = {"navView", "onPageSelected"})
    public static void setOnPageSelectedListener(ViewPager viewPager, BottomNavigationView navView,
                                                 OnPageSelectedListener onPageSelectedListener) {
        ViewPager.OnPageChangeListener newListener;
        if (onPageSelectedListener == null) {
            newListener = null;
        } else {
            newListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    onPageSelectedListener.onPageSelected(viewPager, position, navView);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };
        }


        ViewPager.OnPageChangeListener oldListener = ListenerUtil.trackListener(viewPager, newListener, R.id.onPageChangeListener);
        if (oldListener != null) {
            viewPager.removeOnPageChangeListener(oldListener);
        }
        if (newListener != null) {
            viewPager.addOnPageChangeListener(newListener);
        }
    }

    public interface OnPageSelectedListener {

        /**
         * ViewPager的某个页面选中
         *
         * @param viewPager
         * @param position
         * @param navView
         */
        void onPageSelected(ViewPager viewPager, int position, BottomNavigationView navView);
    }


}
