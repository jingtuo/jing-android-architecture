package com.jing.android.arch.demo.repo.service.model;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author JingTuo
 */
public class JuHeLotteryResult {

    /**
     * 彩票ID
     */
    @SerializedName("lottery_id")
    private String lotteryId;

    /**
     * 开奖结果
     */
    @SerializedName("lottery_res")
    private String lotteryResult;

    /**
     * 开奖期号
     */
    @SerializedName("lottery_no")
    private String lotteryNo;

    /**
     * 开奖日期
     */
    @SerializedName("lottery_date")
    private String lotteryDate;

    /**
     * 兑奖截止日期
     */
    @SerializedName("lottery_exdate")
    private String lotteryExpireDate;

    /**
     * 本期销售额，可能为空
     */
    @SerializedName("lottery_sale_amount")
    private String lotterySaleAmount;

    /**
     * 奖池滚存，可能为空
     */
    @SerializedName("lottery_pool_amount")
    private String lotteryPoolAmount;


    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getLotteryResult() {
        return lotteryResult;
    }

    public void setLotteryResult(String lotteryResult) {
        this.lotteryResult = lotteryResult;
    }

    public String getLotteryNo() {
        return lotteryNo;
    }

    public void setLotteryNo(String lotteryNo) {
        this.lotteryNo = lotteryNo;
    }

    public String getLotteryDate() {
        return lotteryDate;
    }

    public void setLotteryDate(String lotteryDate) {
        this.lotteryDate = lotteryDate;
    }

    public String getLotteryExpireDate() {
        return lotteryExpireDate;
    }

    public void setLotteryExpireDate(String lotteryExpireDate) {
        this.lotteryExpireDate = lotteryExpireDate;
    }

    public String getLotterySaleAmount() {
        return lotterySaleAmount;
    }

    public void setLotterySaleAmount(String lotterySaleAmount) {
        this.lotterySaleAmount = lotterySaleAmount;
    }

    public String getLotteryPoolAmount() {
        return lotteryPoolAmount;
    }

    public void setLotteryPoolAmount(String lotteryPoolAmount) {
        this.lotteryPoolAmount = lotteryPoolAmount;
    }
}
