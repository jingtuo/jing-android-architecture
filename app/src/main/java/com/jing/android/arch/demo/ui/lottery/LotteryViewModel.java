package com.jing.android.arch.demo.ui.lottery;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.jing.android.arch.demo.repo.db.Lottery;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.db.LotteryDatabase;
import com.jing.android.arch.demo.repo.source.JuHeLotterySource;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author JingTuo
 */
public class LotteryViewModel extends AndroidViewModel {


    private MutableLiveData<List<Lottery>> lotteryList;

    private LotteryDao dao;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public LotteryViewModel(@NonNull Application application) {
        super(application);
        this.lotteryList = new MutableLiveData<>();
        LotteryDatabase db = Room.databaseBuilder(application, LotteryDatabase.class, "lottery").build();
        dao = db.lotteryDao();
    }

    public LiveData<List<Lottery>> lotteryList() {
        return lotteryList;
    }

    public void loadData() {
        //可以使用缓存
        mDisposable.add(Flowable.create(new JuHeLotterySource(dao), BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lotteries -> lotteryList.setValue(lotteries), throwable -> {
                    Log.e("Lottery", TextUtils.isEmpty(throwable.getMessage()) ? "unknown" : throwable.getMessage());
                })
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
        mDisposable.clear();
    }
}