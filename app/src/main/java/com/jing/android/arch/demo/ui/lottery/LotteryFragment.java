package com.jing.android.arch.demo.ui.lottery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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

    private static final int REQUEST_PERMISSION_INTERNET = 1;

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
        if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET)) {
            //已经授权
            initViewModel();
            return;
        }
        boolean flag = shouldShowRequestPermissionRationale(Manifest.permission.INTERNET);
        if (flag) {
            //用户拒绝授权,未勾选不再访问,向用户描述为什么要使用权限,用户同意之后requestPermissions
//            requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUEST_PERMISSION_INTERNET);
        } else {
            //用户拒绝授权,并且勾选了不再访问,TODO 待验证requestPermissions是否有效,如果无效引导用户进入应用设置页面
            requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUEST_PERMISSION_INTERNET);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_PERMISSION_INTERNET == requestCode) {
            if (Manifest.permission.INTERNET.equals(permissions[0])
                    && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                initViewModel();
            }

        }
    }
}
