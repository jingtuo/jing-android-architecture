package com.jing.android.arch.demo.ui.lottery.history;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.room.Room;

import com.jing.android.arch.demo.constant.Constants;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.db.LotteryDatabase;
import com.jing.android.arch.demo.repo.db.LotteryResult;
import com.jing.android.arch.demo.repo.service.ServiceManager;
import com.jing.android.arch.demo.repo.source.LotteryResultDataSourceFactory;
import com.jing.android.arch.demo.repo.source.PageNoInfo;
import com.jing.android.arch.demo.repo.source.SyncLotteryResultListSource;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.schedulers.RxThreadFactory;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author JingTuo
 */
public class LotteryHistoryViewModel extends AndroidViewModel {

    private static final int PAGE_SIZE = 20;

    private LotteryDao dao;

    private DataSource<PageNoInfo, LotteryResult> mRecentDataSource;

    private LiveData<PagedList<LotteryResult>> mLotteryResultList;

    private String id;

    private Disposable mSyncDataDisposable;

    private MutableLiveData<String> mSyncStatus;

    private MutableLiveData<String> mSyncFailureInfo;

    public LotteryHistoryViewModel(@NonNull Application application, Bundle bundle) {
        super(application);
        LotteryDatabase database = Room.databaseBuilder(application, LotteryDatabase.class, "lottery")
                .build();
        dao = database.lotteryDao();

        mSyncStatus = new MutableLiveData<>();
        mSyncFailureInfo = new MutableLiveData<>();
        //初始请求两页数据
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(2 * PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .setMaxSize(3 * PAGE_SIZE)
                .build();
        id = bundle.getString("id");
        LotteryResultDataSourceFactory factory = new LotteryResultDataSourceFactory(
                ServiceManager.getInstance().getLotteryService(), dao, id);
        mRecentDataSource = factory.create();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1,
                5, 5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1),
                new RxThreadFactory("Lottery"));
        mLotteryResultList = new LivePagedListBuilder<>(factory, config)
                .setFetchExecutor(executor)
                .build();

    }

    public LiveData<PagedList<LotteryResult>> lotteryHistory() {
        return mLotteryResultList;
    }

    /**
     * 同步数据
     */
    public void syncData() {
        if (Constants.SYNC_STATUS_SYNCING.equals(mSyncStatus.getValue())) {
            return;
        }
        if (mSyncDataDisposable != null) {
            mSyncDataDisposable.dispose();
        }
        mSyncStatus.setValue(Constants.SYNC_STATUS_SYNCING);
        mSyncDataDisposable = Flowable.create(
                new SyncLotteryResultListSource(ServiceManager.getInstance().getLotteryService(), dao, id),
                BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        s -> mSyncStatus.setValue(Constants.SYNC_STATUS_SUCCESS),
                        throwable -> {
                            mSyncStatus.setValue(Constants.SYNC_STATUS_FAILURE);
                            mSyncFailureInfo.setValue(throwable.getMessage());
                        });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (mSyncDataDisposable != null) {
            mSyncDataDisposable.dispose();
        }
    }

    public LiveData<String> syncStatus() {
        return mSyncStatus;
    }

    public LiveData<String> syncFailureInfo() {
        return mSyncFailureInfo;
    }
}
