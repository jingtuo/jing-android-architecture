package com.jing.android.arch.demo.repo.source;

import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.db.LotteryResult;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * 查询彩票和开奖期号查询开奖结果
 *
 * @author JingTuo
 */
public class LotteryResultSource implements SingleOnSubscribe<LotteryResult> {

    private LotteryDao dao;

    private String id;

    private String lotteryNo;

    public LotteryResultSource(LotteryDao dao, String id, String lotteryNo) {
        this.dao = dao;
        this.id = id;
        this.lotteryNo = lotteryNo;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<LotteryResult> emitter) throws Throwable {
        emitter.onSuccess(dao.queryLotteryResult(id, lotteryNo));
    }
}
