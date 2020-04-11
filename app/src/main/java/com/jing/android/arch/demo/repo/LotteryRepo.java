package com.jing.android.arch.demo.repo;


import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jing.android.arch.demo.repo.db.Lottery;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.service.LotteryService;
import com.jing.android.arch.demo.repo.source.LotterySource;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 彩票-Repo
 * @author JingTuo
 */
public class LotteryRepo {

    private LotteryDao mLotteryDao;

    private LotteryService mLotteryService;

    private MutableLiveData<List<Lottery>> mLotteryList = new MutableLiveData<>();

    private CompositeDisposable mDisposable = new CompositeDisposable();

    public LotteryRepo(LotteryDao lotteryDao, LotteryService lotteryService) {
        this.mLotteryDao = lotteryDao;
        this.mLotteryService = lotteryService;
    }

    public LiveData<List<Lottery>> getLotteryList() {
        mDisposable.add(Flowable.create(new LotterySource(mLotteryDao, mLotteryService), BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lotteries -> mLotteryList.setValue(lotteries), throwable -> {
                    Log.e("Lottery", TextUtils.isEmpty(throwable.getMessage()) ? "unknown" : throwable.getMessage());
                }));
        return mLotteryList;
    }

    /**
     * 释放资源
     */
    public void release() {
        mDisposable.dispose();
        mDisposable.clear();
    }

}
