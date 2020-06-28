package com.jing.android.arch.demo.repo.source;

import android.util.Log;

import com.jing.android.arch.demo.repo.db.LotteryBall;
import com.jing.android.arch.demo.util.LotteryUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * 黄金比例预测
 * 经观察发现，命中率很低
 *
 * @author JingTuo
 */
public class GoldRatioForecastSource implements SingleOnSubscribe<String> {

    private List<LotteryBall> balls;

    private int ballCount;

    public GoldRatioForecastSource(List<LotteryBall> balls, int ballCount) {
        this.balls = balls;
        this.ballCount = ballCount;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<String> emitter) throws Throwable {
        //按照黄金比例0.382:0.618,0.618:0.382分别查找
        float[] ratios = LotteryUtils.findGoldRatio(balls);
        Log.i("LotteryResult", "goldRatio -> " + ratios[0] + ", " + ratios[1]);
        //基于上面两个比例分别找三个比例最接近的
        List<String> ballNos = new ArrayList<>();
        if (ballCount == 1) {
            //只取一个球号,如双色球的蓝色球,取低比率的
            LotteryUtils.findBallNoByClosestRatio(balls, ratios[0], ballNos, 1, true);
        } else {
            LotteryUtils.findBallNoByClosestRatio(balls, ratios[0], ballNos, ballCount / 2, true);
            LotteryUtils.findBallNoByClosestRatio(balls, ratios[1], ballNos, ballCount, true);
        }
        emitter.onSuccess(LotteryUtils.formatBallNos(ballNos));
    }
}
