package com.jing.android.arch.demo.ui.lottery.history;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.jing.android.arch.component.BaseActivity;
import com.jing.android.arch.demo.R;
import com.jing.android.arch.demo.util.AndroidViewModelFactory;

/**
 * 彩票开奖-结果
 *
 * @author JingTuo
 */
public class LotteryHistoryActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_history);
        Intent intent = getIntent();
        String title = intent.getStringExtra("name");
        setActionBar(R.id.tool_bar, title, true);
        LotteryHistoryViewModel vm = new ViewModelProvider(this,
                new AndroidViewModelFactory() {
                    @Override
                    protected Application getApplication() {
                        return LotteryHistoryActivity.this.getApplication();
                    }

                    @Override
                    protected Bundle getBundle() {
                        return getIntent().getExtras();
                    }
                })
                .get(LotteryHistoryViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        LotteryResultAdapter adapter = new LotteryResultAdapter();
        recyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        recyclerView.setAdapter(adapter);
        vm.lotteryHistory().observe(this, adapter::submitList);
    }
}
