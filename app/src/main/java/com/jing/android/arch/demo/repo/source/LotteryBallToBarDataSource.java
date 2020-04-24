package com.jing.android.arch.demo.repo.source;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.jing.android.arch.demo.repo.db.LotteryBall;
import com.jing.android.arch.demo.ui.model.MyChartData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * 彩票开奖结果转换为BarData
 * @author JingTuo
 */
public class LotteryBallToBarDataSource implements SingleOnSubscribe<MyChartData<BarData, List<LotteryBall>>> {

    private String label;

    private List<LotteryBall> balls;

    public LotteryBallToBarDataSource(String label, List<LotteryBall> balls) {
        this.label = label;
        this.balls = balls;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<MyChartData<BarData, List<LotteryBall>>> emitter) throws Throwable {
        MyChartData<BarData, List<LotteryBall>> myChartData = new MyChartData<>();
        myChartData.setSourceData(balls);
        BarData barData = new BarData();
        List<BarEntry> barEntries = new ArrayList<>();
        if (balls != null) {
            int length = balls.size();
            for (int i = 0; i < length; i++) {
                LotteryBall ball = balls.get(i);
                BarEntry entry = new BarEntry(i, ball.getFrequencyRatio() * 100, ball);
                barEntries.add(entry);
            }
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, label);
        barData.addDataSet(barDataSet);
        myChartData.setChartData(barData);
        emitter.onSuccess(myChartData);
    }
}
