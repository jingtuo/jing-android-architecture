package com.jing.android.arch.demo.ui.lottery;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jing.android.arch.component.BaseFragment;
import com.jing.android.arch.demo.R;
import com.jing.android.arch.demo.repo.db.Lottery;

import java.util.List;

/**
 * 彩票-列表
 *
 * @author JingTuo
 */
public class LotteryFragment extends BaseFragment {

    private LotteryViewModel lotteryViewModel;

    @Override
    protected int layoutId() {
        return R.layout.fragment_lottery;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView listView = view.findViewById(R.id.list_view);
        lotteryViewModel = new ViewModelProvider(this).get(LotteryViewModel.class);
        lotteryViewModel.lotteryList().observe(getActivity(), new Observer<List<Lottery>>() {
            @Override
            public void onChanged(List<Lottery> lotteries) {
                LotteryAdapter adapter = new LotteryAdapter();
                adapter.setData(lotteries);
                listView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        lotteryViewModel.loadData();
    }
}
