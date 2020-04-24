package com.jing.android.arch.demo.repo.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 *
 * @author JingTuo
 */
@Entity(primaryKeys = {"id", "lotteryNo"})
public class LotteryResult {

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
     * 开奖结果
     */
    private String result;

    /**
     * 开奖日期,由于date是日期函数,不能是date作为名称
     */
    private String resultDate;

    /**
     * 兑奖截止日期
     */
    private String expireDate;

    /**
     * 本期销售额，可能为空
     */
    private String saleAmount;

    /**
     * 奖池滚存，可能为空
     */
    private String poolAmount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @NonNull
    public String getLotteryNo() {
        return lotteryNo;
    }

    public void setLotteryNo(@NonNull String lotteryNo) {
        this.lotteryNo = lotteryNo;
    }

    public String getResultDate() {
        return resultDate;
    }

    public void setResultDate(String resultDate) {
        this.resultDate = resultDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(String saleAmount) {
        this.saleAmount = saleAmount;
    }

    public String getPoolAmount() {
        return poolAmount;
    }

    public void setPoolAmount(String poolAmount) {
        this.poolAmount = poolAmount;
    }
}
