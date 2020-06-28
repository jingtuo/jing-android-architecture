package com.jing.android.arch.demo.util;

import android.util.Log;

import com.jing.android.arch.demo.repo.db.LotteryBall;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.db.LotterySubResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 彩票工具类
 *
 * @author JingTuo
 */
public class LotteryUtils {

    /**
     * 根据球号的出现比率，查找最近指定比例的n个球号
     *
     * @param balls      所有球号的出现比率数据
     * @param ratio      比例
     * @param ballNos    找到的球号放在这个集合
     * @param count      要找几个球号
     * @param belowRatio true,只匹配低于ratio的球号
     */
    public static void findBallNoByClosestRatio(List<LotteryBall> balls, float ratio,
                                                List<String> ballNos, int count,
                                                boolean belowRatio) {
        //此处新建集合是为了避免下面的移除操作影响原有集合balls
        List<LotteryBall> ballList = new ArrayList<>(balls);
        int size;
        while (true) {
            if (ballNos.size() < count) {
                float dx = Float.MAX_VALUE;
                int index = -1;
                size = ballList.size();
                for (int i = 0; i < size; i++) {
                    LotteryBall ball = ballList.get(i);
                    if (belowRatio) {
                        float newDx = ratio - ball.getFrequencyRatio();
                        if (newDx > 0 && newDx < dx) {
                            dx = newDx;
                            index = i;
                        }
                    } else {
                        //不考虑低于ratio,取绝对值
                        float newDx = Math.abs(ratio - ball.getFrequencyRatio());
                        if (newDx < dx) {
                            dx = newDx;
                            index = i;
                        }
                    }

                }
                String ballNo = ballList.get(index).getBallNo();
                if (!ballNos.contains(ballNo)) {
                    ballNos.add(ballNo);
                }
                ballList.remove(index);
            } else {
                break;
            }
        }
    }

    /**
     * 用","分隔球号,返回字符串
     *
     * @param ballNos
     * @return
     */
    public static String formatBallNos(List<String> ballNos) {
        if (ballNos == null || ballNos.isEmpty()) {
            return "";
        }
        Collections.sort(ballNos, String::compareTo);
        int size = ballNos.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(ballNos.get(i));
            if (i != size - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }


    public static String formatBallNo(int i) {
        return String.format(Locale.getDefault(), "%1$02d", i);
    }

    /**
     * 根据球号出现的最低比率和最高比率,找到黄金比例对应的出现比率
     * 0.382:0.618对应于低点黄金比例,
     * 0.618:0.382对应于高点黄金比例
     * @param balls 第一个值是低点黄金比例、第二个值是高点黄金比例
     *
     * @return
     */
    public static float[] findGoldRatio(List<LotteryBall> balls) {
        List<LotteryBall> ballList = new ArrayList<>(balls);
        Collections.sort(ballList, (o1, o2) -> {
            if (o1.getFrequencyRatio() > o2.getFrequencyRatio()) {
                return 1;
            }
            if (o1.getFrequencyRatio() < o2.getFrequencyRatio()) {
                return -1;
            }
            return 0;
        });
        float minRatio = ballList.get(0).getFrequencyRatio();
        float maxRatio = ballList.get(ballList.size() - 1).getFrequencyRatio();
        float range = maxRatio - minRatio;
        return new float[]{minRatio + range * 0.382f, minRatio + range * 0.618f};
    }

    /**
     *
     * @param dao
     * @param id
     * @param date
     */
    public static void observeContinuityOfBallNo (LotteryDao dao, String id, String date) {
        List<LotterySubResult> list1 = dao.queryLotterySubResult(id, 7, date);
        int size = list1.size();
        String previousBallNo = "";
        List<String> dateList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            LotterySubResult item = list1.get(i);
            if (previousBallNo.equals(item.getBallNo())) {
                //当前球号与上一次一样
                dateList.add(item.getResultDate());
                if (i == size - 1) {
                    //最后一个
                    if (dateList.size() >= 2) {
                        Log.i("LotteryResult", previousBallNo + " -> " + LotteryUtils.formatBallNos(dateList));
                    }
                }
            } else {
                if (dateList.size() >= 2) {
                    Log.i("LotteryResult", previousBallNo + " -> " + LotteryUtils.formatBallNos(dateList));
                }
                previousBallNo = item.getBallNo();
                dateList = new ArrayList<>();
                dateList.add(item.getResultDate());
            }
        }
    }
}
