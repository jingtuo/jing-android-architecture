package com.jing.android.arch.demo.ui.lottery.history;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jing.android.arch.component.BaseActivity;
import com.jing.android.arch.demo.R;
import com.jing.android.arch.demo.constant.Constants;
import com.jing.android.arch.demo.constant.Keys;
import com.jing.android.arch.demo.repo.db.LotteryResult;
import com.jing.android.arch.demo.ui.lottery.ssq.SsqAnalysisActivity;
import com.jing.android.arch.demo.util.AndroidViewModelFactory;

/**
 * 彩票开奖-结果
 *
 * @author JingTuo
 */
public class LotteryHistoryActivity extends BaseActivity {

    private LotteryHistoryViewModel vm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_history);
        Intent intent = getIntent();
        String title = intent.getStringExtra("name");
        setActionBar(R.id.tool_bar, title, true);
        vm = new ViewModelProvider(this,
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
        adapter.setOnItemClickListener((position, data) -> {
            Intent intent1 = new Intent(LotteryHistoryActivity.this, SsqAnalysisActivity.class);
            intent1.putExtra(Keys.ID, data.getId());
            intent1.putExtra(Keys.LOTTERY_NO, data.getLotteryNo());
            intent1.putExtra(Keys.NAME, getIntent().getStringExtra(Keys.NAME));
            startActivity(intent1);
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        recyclerView.setAdapter(adapter);
        vm.lotteryHistory().observe(this, adapter::submitList);
        vm.syncFailureInfo().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(LotteryHistoryActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
        vm.syncStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (Constants.SYNC_STATUS_SYNCING.equals(s)) {
                    //正在同步
                    Toast.makeText(LotteryHistoryActivity.this, R.string.syncing, Toast.LENGTH_SHORT).show();
                } else if (Constants.SYNC_STATUS_SUCCESS.equals(s)) {
                    //同步完成
                    Toast.makeText(LotteryHistoryActivity.this, R.string.sync_success, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lottery_history_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_sync) {
            vm.syncData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
