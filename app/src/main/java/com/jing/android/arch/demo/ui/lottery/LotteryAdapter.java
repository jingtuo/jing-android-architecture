package com.jing.android.arch.demo.ui.lottery;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.jing.android.arch.component.SimpleAdapter;
import com.jing.android.arch.demo.R;
import com.jing.android.arch.demo.databinding.FragmentLotteryItemBinding;
import com.jing.android.arch.demo.repo.db.Lottery;

/**
 *
 * @author JingTuo
 */
public class LotteryAdapter extends SimpleAdapter<Lottery> {

    @Override
    protected ViewHolder<Lottery> createViewHolder(ViewGroup parent, int viewType) {
        return new LotteryHolder(parent, viewType);
    }

    public class LotteryHolder extends ViewHolder<Lottery> {

        private FragmentLotteryItemBinding dataBinding;

        LotteryHolder(ViewGroup parent, int viewType) {
            super(parent, viewType);
        }

        @Override
        protected View createView(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            dataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), parent, false);
            return dataBinding.getRoot();
        }

        @Override
        protected int getLayoutId() {
            return R.layout.fragment_lottery_item;
        }

        @Override
        public void setView(int position, Lottery data) {
            super.setView(position, data);
            dataBinding.setLottery(data);
            dataBinding.executePendingBindings();
        }
    }
}
