package com.jing.android.arch.demo.repo.db;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseView;
import androidx.room.Entity;

/**
 *
 * @author JingTuo
 */
public class LotterySubResult {

    /**
     * 彩票ID
     */
    @NonNull
    private String id;

    /**
     * 开奖期号,由于no是关键词,不能是no作为名称
     */
    @NonNull
    private String lotteryNo;

    /**
     * 球号
     */
    private String ballNo;

    /**
     * 开奖日期,由于date是日期函数,不能是date作为名称
     */
    private String resultDate;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getLotteryNo() {
        return lotteryNo;
    }

    public void setLotteryNo(@NonNull String lotteryNo) {
        this.lotteryNo = lotteryNo;
    }

    public String getBallNo() {
        return ballNo;
    }

    public void setBallNo(String ballNo) {
        this.ballNo = ballNo;
    }

    public String getResultDate() {
        return resultDate;
    }

    public void setResultDate(String resultDate) {
        this.resultDate = resultDate;
    }
}
