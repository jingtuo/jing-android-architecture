package com.jing.android.arch.demo.ui.lottery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jing.android.arch.component.BaseFragment;
import com.jing.android.arch.demo.R;
import com.jing.android.arch.demo.constant.Keys;
import com.jing.android.arch.demo.repo.db.Lottery;
import com.jing.android.arch.demo.ui.lottery.history.LotteryHistoryActivity;

import java.util.List;

/**
 * 彩票-列表
 *
 * @author JingTuo
 */
public class LotteryFragment extends BaseFragment {

    private LotteryViewModel lotteryViewModel;

    private ListView listView;

    @Override
    protected int layoutId() {
        return R.layout.fragment_lottery;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.list_view);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            LotteryAdapter adapter = (LotteryAdapter)parent.getAdapter();
            Lottery lottery = adapter.getItem(position);
            Intent intent = new Intent(parent.getContext(), LotteryHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Keys.ID, lottery.getId());
            intent.putExtra(Keys.NAME, lottery.getName());
            startActivity(intent);
        });
        initViewModel();
    }

    private void initViewModel() {
        lotteryViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory
                        .getInstance(requireActivity().getApplication()))
                .get(LotteryViewModel.class);
        lotteryViewModel.lotteryList().observe(getViewLifecycleOwner(), new Observer<List<Lottery>>() {
            @Override
            public void onChanged(List<Lottery> lotteries) {
                LotteryAdapter adapter = new LotteryAdapter();
                adapter.setData(lotteries);
                listView.setAdapter(adapter);
            }
        });
    }
}
