package com.jing.android.arch.demo.repo.source;

import com.jing.android.arch.demo.constant.Constants;
import com.jing.android.arch.demo.repo.db.LotteryBall;
import com.jing.android.arch.demo.repo.db.LotteryDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * @author JingTuo
 */
public class LotteryBallSource implements SingleOnSubscribe<List<LotteryBall>> {


    private LotteryDao dao;

    private String id;

    private String date;

    private String flag;

    public LotteryBallSource(LotteryDao dao, String id, String date, String flag) {
        this.dao = dao;
        this.id = id;
        this.date = date;
        this.flag = flag;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<List<LotteryBall>> emitter) throws Throwable {
        //红球
        List<LotteryBall> result = new ArrayList<>();
        int count = dao.queryLotteryResultCount(id);
        if (Constants.LOTTERY_ID_SSQ.equals(id)) {
            //双色球
            if (Constants.LOTTERY_BALL_FLAG_RED.equals(flag)) {
                result.addAll(getBall(33, count,1, 6));
            } else {
                result.addAll(getBall(16, count,7, 7));
            }
        }
        emitter.onSuccess(result);
    }

    /**
     * 获取球
     * @param ballMax
     * @param totalCount 开奖总次数
     * @param start
     * @param end
     * @return
     */
    private List<LotteryBall> getBall(int ballMax, int totalCount, int start, int end) {
        List<LotteryBall> result = new ArrayList<>();
        for (int i = 1; i <= ballMax; i++) {
            String ballNo = String.format(Locale.getDefault(), "%1$02d", i);
            int count = dao.queryLotteryBallNoCount(id, ballNo, start, end, date);
            LotteryBall ball = new LotteryBall();
            ball.setBallNo(ballNo);
            ball.setFrequency(count);
            ball.setFrequencyRatio(count * 1.f / totalCount);
            result.add(ball);
        }
        return result;
    }

}
