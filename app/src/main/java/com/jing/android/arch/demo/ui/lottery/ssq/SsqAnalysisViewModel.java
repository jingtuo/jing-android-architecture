package com.jing.android.arch.demo.ui.lottery.ssq;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.jing.android.arch.demo.constant.Constants;
import com.jing.android.arch.demo.constant.Keys;
import com.jing.android.arch.demo.repo.db.LotteryBall;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.db.LotteryDatabase;
import com.jing.android.arch.demo.repo.db.LotteryResult;
import com.jing.android.arch.demo.repo.db.LotterySubResult;
import com.jing.android.arch.demo.repo.source.AverageForecastSource;
import com.jing.android.arch.demo.repo.source.GoldRatioForecastSource;
import com.jing.android.arch.demo.repo.source.IntelligentAForecastSource;
import com.jing.android.arch.demo.repo.source.LotteryBallSource;
import com.jing.android.arch.demo.repo.source.LotteryBallToBarDataSource;
import com.jing.android.arch.demo.repo.source.LotteryResultSource;
import com.jing.android.arch.demo.repo.source.LotterySubResultSource;
import com.jing.android.arch.demo.repo.source.LotterySubResultToLineDataSource;
import com.jing.android.arch.demo.ui.model.MyChartData;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author JingTuo
 */
public class SsqAnalysisViewModel extends AndroidViewModel {

    private String id;

    private LotteryDao dao;

    private MutableLiveData<LotteryResult> mResult;
    private MutableLiveData<MyChartData<BarData, List<LotteryBall>>> mRedBallFrequency;
    private MutableLiveData<MyChartData<BarData, List<LotteryBall>>> mBlueBallFrequency;
    private MutableLiveData<MyChartData<LineData, List<LotterySubResult>>> mBlueBallTrend;
    /**
     * 平均线预测-红色球
     */
    private MutableLiveData<String> mAfRedBall;

    /**
     * 平均线预测-蓝色球
     */
    private MutableLiveData<String> mAfBlueBall;

    /**
     * 黄金比例预测-红色球
     */
    private MutableLiveData<String> mGrfRedBall;

    /**
     * 黄金比例预测-蓝色球
     */
    private MutableLiveData<String> mGrfBlueBall;

    /**
     * 智能A预测-红色球
     */
    private MutableLiveData<String> mZafRedBall;

    /**
     * 智能A预测-蓝色球
     */
    private MutableLiveData<String> mZafBlueBall;


    private CompositeDisposable mDisposable;

    public SsqAnalysisViewModel(@NonNull Application application, Bundle bundle) {
        super(application);
        id = bundle.getString(Keys.ID);
        String lotteryNo = bundle.getString(Keys.LOTTERY_NO);
        LotteryDatabase database = Room.databaseBuilder(application, LotteryDatabase.class, "lottery")
                .build();
        dao = database.lotteryDao();

        mResult = new MutableLiveData<>();
        mRedBallFrequency = new MutableLiveData<>();
        mBlueBallFrequency = new MutableLiveData<>();
        mBlueBallTrend = new MutableLiveData<>();
        mBlueBallTrend = new MutableLiveData<>();
        mAfRedBall = new MutableLiveData<>();
        mAfBlueBall = new MutableLiveData<>();
        mGrfRedBall = new MutableLiveData<>();
        mGrfBlueBall = new MutableLiveData<>();
        mZafRedBall = new MutableLiveData<>();
        mZafBlueBall = new MutableLiveData<>();

        mDisposable = new CompositeDisposable();
        //开奖结果
        mDisposable.add(Single.create(new LotteryResultSource(dao, id, lotteryNo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lotteryResult -> {
                    mResult.setValue(lotteryResult);
                    loadChartData(lotteryResult.getResultDate());
                    forecast(lotteryResult.getResultDate());
                }));

    }

    /**
     *
     * @param date
     */
    private void loadChartData(String date) {
        //红球出现的频率
        mDisposable.add(Single.create(new LotteryBallSource(dao, id, date, Constants.LOTTERY_BALL_FLAG_RED))
                .flatMap((Function<List<LotteryBall>, SingleSource<MyChartData<BarData, List<LotteryBall>>>>) lotteryBalls ->
                        Single.create(new LotteryBallToBarDataSource("red ball", lotteryBalls)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(barData -> mRedBallFrequency.setValue(barData)));
        //蓝球出现的频率
        mDisposable.add(Single.create(new LotteryBallSource(dao, id, date, Constants.LOTTERY_BALL_FLAG_BLUE))
                .flatMap((Function<List<LotteryBall>, SingleSource<MyChartData<BarData, List<LotteryBall>>>>) lotteryBalls ->
                        Single.create(new LotteryBallToBarDataSource("blue ball", lotteryBalls)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(barData -> mBlueBallFrequency.setValue(barData)));
        //蓝球出现的趋势
        mDisposable.add(Single.create(new LotterySubResultSource(dao, id, date, Constants.LOTTERY_BALL_FLAG_BLUE))
                .flatMap((Function<List<LotterySubResult>, SingleSource<MyChartData<LineData, List<LotterySubResult>>>>) result ->
                        Single.create(new LotterySubResultToLineDataSource("blue ball", result)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lineData -> mBlueBallTrend.setValue(lineData)));
    }

    /**
     * 预测
     * @param date
     */
    private void forecast(String date) {
        //平均线预测-红色球
        mDisposable.add(Single.create(new LotteryBallSource(dao, id, date, Constants.LOTTERY_BALL_FLAG_RED))
                .flatMap((Function<List<LotteryBall>, SingleSource<String>>) lotteryBalls ->
                        Single.create(new AverageForecastSource(lotteryBalls, 6)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mAfRedBall.setValue(result)));
        //平均线预测-蓝色球
        mDisposable.add(Single.create(new LotteryBallSource(dao, id, date, Constants.LOTTERY_BALL_FLAG_BLUE))
                .flatMap((Function<List<LotteryBall>, SingleSource<String>>) lotteryBalls ->
                        Single.create(new AverageForecastSource(lotteryBalls, 1)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mAfBlueBall.setValue(result)));

        //黄金比例预测-红色球
        mDisposable.add(Single.create(new LotteryBallSource(dao, id, date, Constants.LOTTERY_BALL_FLAG_RED))
                .flatMap((Function<List<LotteryBall>, SingleSource<String>>) lotteryBalls ->
                        Single.create(new GoldRatioForecastSource(lotteryBalls, 6)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mGrfRedBall.setValue(result)));
        //黄金比例预测-蓝色球
        mDisposable.add(Single.create(new LotteryBallSource(dao, id, date, Constants.LOTTERY_BALL_FLAG_BLUE))
                .flatMap((Function<List<LotteryBall>, SingleSource<String>>) lotteryBalls ->
                        Single.create(new GoldRatioForecastSource(lotteryBalls, 1)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mGrfBlueBall.setValue(result)));

        //智能A预测-红色球
        mDisposable.add(Single.create(new IntelligentAForecastSource(dao, id, date, Constants.LOTTERY_BALL_FLAG_RED))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mZafRedBall.setValue(result)));
        //智能A预测-蓝色球
        mDisposable.add(Single.create(new IntelligentAForecastSource(dao, id, date, Constants.LOTTERY_BALL_FLAG_BLUE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mZafBlueBall.setValue(result)));
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
    }

    public LiveData<MyChartData<BarData, List<LotteryBall>>> redBallFrequency() {
        return mRedBallFrequency;
    }

    public LiveData<MyChartData<BarData, List<LotteryBall>>> blueBallFrequency() {
        return mBlueBallFrequency;
    }

    public LiveData<MyChartData<LineData, List<LotterySubResult>>> blueBallTrend() {
        return mBlueBallTrend;
    }

    public LiveData<LotteryResult> result() {
        return mResult;
    }


    public LiveData<String> averageForecastRedBall() {
        return mAfRedBall;
    }

    public LiveData<String> averageForecastBlueBall() {
        return mAfBlueBall;
    }

    public LiveData<String> goldRatioForecastRedBall() {
        return mGrfRedBall;
    }

    public LiveData<String> goldRatioForecastBlueBall() {
        return mGrfBlueBall;
    }

    public LiveData<String> zafRedBall() {
        return mZafRedBall;
    }

    public LiveData<String> zafBlueBall() {
        return mZafBlueBall;
    }

}
