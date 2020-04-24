package com.jing.android.arch.demo.repo.source;

import com.jing.android.arch.demo.constant.Constants;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.db.LotterySubResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * 根据彩票id和日期,查询开奖结果中第n个球所有历史号码,用于生成走势数据
 * @author JingTuo
 */
public class LotterySubResultSource implements SingleOnSubscribe<List<LotterySubResult>> {


    private LotteryDao dao;

    private String id;

    private String date;

    private String flag;

    public LotterySubResultSource(LotteryDao dao, String id, String date, String flag) {
        this.dao = dao;
        this.id = id;
        this.date = date;
        this.flag = flag;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<List<LotterySubResult>> emitter) throws Throwable {
        //红球
        List<LotterySubResult> result = new ArrayList<>();
        int count = dao.queryLotteryResultCount(id);
        if (Constants.LOTTERY_ID_SSQ.equals(id)) {
            //双色球
            if (Constants.LOTTERY_BALL_FLAG_BLUE.equals(flag)) {
                result.addAll(dao.queryLotterySubResult(id, 7, date));
            }
        }
        emitter.onSuccess(result);
    }
}
