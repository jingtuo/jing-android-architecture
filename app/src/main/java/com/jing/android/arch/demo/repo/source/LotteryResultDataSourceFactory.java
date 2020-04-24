package com.jing.android.arch.demo.repo.source;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.db.LotteryResult;
import com.jing.android.arch.demo.repo.service.LotteryService;

/**
 *
 * @author JingTuo
 */
public class LotteryResultDataSourceFactory extends DataSource.Factory<PageNoInfo, LotteryResult> {

    private static final String TAG = LotteryResultDataSourceFactory.class.getSimpleName();

    private MutableLiveData<LotteryResultListSource> mLotteryResultSource = new MutableLiveData<>();

    private LotteryResultListSource mLastLotteryResultSource;

    private LotteryService service;

    private LotteryDao dao;

    private String id;

    public LotteryResultDataSourceFactory(LotteryService service, LotteryDao dao, String id) {
        this.service = service;
        this.dao = dao;
        this.id = id;
    }

    @NonNull
    @Override
    public DataSource<PageNoInfo, LotteryResult> create() {
        Log.i(TAG, "create data source");
        mLastLotteryResultSource = new LotteryResultListSource(service, dao, id);
        mLotteryResultSource.postValue(mLastLotteryResultSource);
        return mLastLotteryResultSource;
    }

    public LotteryResultListSource getLastLotteryResultSource() {
        return mLastLotteryResultSource;
    }
}
