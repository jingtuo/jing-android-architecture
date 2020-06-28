package com.jing.android.arch.demo.repo.source;

import android.util.Log;

import com.jing.android.arch.demo.repo.db.LotteryBall;
import com.jing.android.arch.demo.util.LotteryUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * 平均线预测
 * 根据所有球号的出现概率找出概率最接近平均值的球号
 * 经观察发现，命中率很低
 *
 * @author JingTuo JingTuo
 */
public class AverageForecastSource implements SingleOnSubscribe<String> {

    private List<LotteryBall> balls;

    private int ballCount;

    public AverageForecastSource(List<LotteryBall> balls, int ballCount) {
        this.balls = balls;
        this.ballCount = ballCount;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<String> emitter) throws Throwable {
        float averageValue = 0.f;
        int size = balls.size();
        float totalValue = 0.f;
        for (int i = 0; i < size; i++) {
            totalValue += balls.get(i).getFrequencyRatio();
        }
        averageValue = totalValue / size;
        Log.i("LotteryResult", "averageValue -> " + averageValue);
        List<String> ballNos = new ArrayList<>();
        LotteryUtils.findBallNoByClosestRatio(balls, averageValue, ballNos, ballCount, false);
        emitter.onSuccess(LotteryUtils.formatBallNos(ballNos));
    }
}
