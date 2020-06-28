package com.jing.android.arch.demo.repo.source;

import android.util.Log;

import com.jing.android.arch.demo.constant.Constants;
import com.jing.android.arch.demo.repo.db.LotteryBall;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.db.LotterySubResult;
import com.jing.android.arch.demo.util.LotteryUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * 智能A预测
 * <p>
 * 最低比例取两个
 * 平均线附近取两个
 * 黄金比例取两个
 * <p>
 * 经观察大数据得出结论：
 * 一个球号连续出现两次的概率远远高于连续出现三次、连续出现三次四次，所以智能A预测只考虑连续出现两次的场景
 *
 * @author JingTuo
 */
public class IntelligentAForecastSource implements SingleOnSubscribe<String> {

    private LotteryDao dao;

    private String id;

    private String date;

    private String flag;

    public IntelligentAForecastSource(LotteryDao dao, String id, String date, String flag) {
        this.dao = dao;
        this.id = id;
        this.date = date;
        this.flag = flag;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<String> emitter) throws Throwable {
        List<LotteryBall> balls = new ArrayList<>();
        int count = dao.queryLotteryResultCount(id);
        if (Constants.LOTTERY_ID_SSQ.equals(id)) {
            //双色球
            if (Constants.LOTTERY_BALL_FLAG_RED.equals(flag)) {
                balls.addAll(getBall(33, count, 1, 6));
            } else {
                balls.addAll(getBall(16, count, 7, 7));
            }
        }
        //按出现比率升序排列
        List<LotteryBall> ballsOrderByRatio = new ArrayList<>(balls);
        Collections.sort(ballsOrderByRatio, (o1, o2) -> {
            if (o1.getFrequencyRatio() > o2.getFrequencyRatio()) {
                return 1;
            }
            if (o1.getFrequencyRatio() < o2.getFrequencyRatio()) {
                return -1;
            }
            return 0;
        });
        if (Constants.LOTTERY_ID_SSQ.equals(id) && Constants.LOTTERY_BALL_FLAG_BLUE.equals(flag)) {
            //双色球-蓝色球
            String ballNo = forecastSsqBlueBallNo(ballsOrderByRatio, balls);
            emitter.onSuccess(ballNo);
            return;
        }
        //双色球-红色球的数量是6
        int ballCount = 6;

        //先放两个最低比率的球号
        List<String> ballNos = new ArrayList<>();
        ballNos.add(balls.get(0).getBallNo());
        ballNos.add(balls.get(1).getBallNo());
        //平均线附近找两个
        float averageValue = 0.f;
        int size = balls.size();
        float totalValue = 0.f;
        for (int i = 0; i < size; i++) {
            totalValue += balls.get(i).getFrequencyRatio();
        }
        averageValue = totalValue / size;
        LotteryUtils.findBallNoByClosestRatio(balls, averageValue, ballNos, ballNos.size() + 2, true);
        //黄金比例低点找一个,黄金比例高点找一个
        float minRatio = balls.get(0).getFrequencyRatio();
        float maxRatio = balls.get(balls.size() - 1).getFrequencyRatio();
        //按照黄金比例0.382:0.618,0.618:0.382分别查找
        float range = maxRatio - minRatio;
        float firstRatio = minRatio + range * 0.382f;
        float secondRatio = minRatio + range * 0.618f;
        //基于上面两个比例分别找一个比例最接近的
        LotteryUtils.findBallNoByClosestRatio(balls, firstRatio, ballNos, ballNos.size() + 1, true);
        LotteryUtils.findBallNoByClosestRatio(balls, secondRatio, ballNos, ballNos.size() + 1, true);
        //如果前面的策略找到重复的,在黄金比例低点,把剩余球号数量补齐
        if (ballNos.size() < ballCount) {
            LotteryUtils.findBallNoByClosestRatio(balls, firstRatio, ballNos, ballCount, true);
        }
        emitter.onSuccess(LotteryUtils.formatBallNos(ballNos));
    }

    /**
     * 获取球
     *
     * @param ballMax
     * @param totalCount 开奖总次数
     * @param start
     * @param end
     * @return
     */
    private List<LotteryBall> getBall(int ballMax, int totalCount, int start, int end) {
        List<LotteryBall> result = new ArrayList<>();
        for (int i = 1; i <= ballMax; i++) {
            String ballNo = LotteryUtils.formatBallNo(i);
            int count = dao.queryLotteryBallNoCount(id, ballNo, start, end, date);
            LotteryBall ball = new LotteryBall();
            ball.setBallNo(ballNo);
            ball.setFrequency(count);
            ball.setFrequencyRatio(count * 1.f / totalCount);
            result.add(ball);
        }
        return result;
    }


    /**
     *
     */
    private String forecastSsqBlueBallNo(List<LotteryBall> ballsOrderByRatio, List<LotteryBall> ballsOrderByBallNo) {
        //最低概率的球号
        String ballNo = ballsOrderByRatio.get(0).getBallNo();
        //根据前几天的球号波动进行调整
        List<LotterySubResult> list = dao.queryLotterySubResultByDayDesc(id, 7, date, 30);
        //特殊场景:前两天出现最低球号
        String lastBallNo = list.get(0).getBallNo();
        float lastBallNoRatio = 0.f;
        int size = ballsOrderByBallNo.size();
        for (int i = 0; i < size; i++) {
            if (lastBallNo.equals(ballsOrderByRatio.get(i).getBallNo())) {
                lastBallNoRatio = ballsOrderByRatio.get(i).getFrequencyRatio();
                break;
            }
        }
        Log.i("LotteryResult", "last ballNo ratio -> " + lastBallNoRatio);
        if (lastBallNo.equals(ballNo)) {
            //最近一次是最低概率的球号
            String beforeLastBallNo = list.get(1).getBallNo();
            if (beforeLastBallNo.equals(ballNo)) {
                //连续两次是最低概率的球号
                /**
                 * 方案A,根据第二个最低概率的位置,预判走势
                 */
                String secondBallNo = ballsOrderByRatio.get(1).getBallNo();
                int ballNoI = Integer.parseInt(ballNo);
                int secondBallNoI = Integer.parseInt(secondBallNo);
                List<LotteryBall> newList = new ArrayList<>();
                if (secondBallNoI > ballNoI) {
                    //第二个最低概率的球号大于最低概率的球号,走势向上
                    for (int i = ballNoI + 1; i <= 16; i++) {
                        newList.add(ballsOrderByBallNo.get(i - 1));
                    }
                } else {
                    //第二个最低概率的球号大于最低概率的球号,走势向下
                    for (int i = 1; i < ballNoI; i++) {
                        newList.add(ballsOrderByBallNo.get(i - 1));
                    }
                }
                //根据黄金比例查找
                List<String> ballNos = new ArrayList<>();
                float[] ratios = LotteryUtils.findGoldRatio(newList);
                LotteryUtils.findBallNoByClosestRatio(newList, ratios[0], ballNos, 1, true);
                return ballNos.get(0);
            } else {
                //根据球号是否大于最低概率的球号,进行进一步预测

            }
        }
        return ballNo;
    }
}
