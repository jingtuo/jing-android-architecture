package com.jing.android.arch.demo.repo.service.model;

import com.google.gson.annotations.SerializedName;

/**
 * 彩票-用于接收从服务器获取的数据
 *
 * @author JingTuo
 */
public class JuHeLottery {

    /**
     * 彩票id
     */
    @SerializedName("lottery_id")
    private String lotteryId;

    /**
     * 彩票名称
     */
    @SerializedName("lottery_name")
    private String lotteryName;

    /**
     * 1-福利彩票，2-体育彩票
     */
    @SerializedName("lottery_type_id")
    private String lotteryTypeId;

    /**
     * 描述信息
     */
    private String remarks;

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getLotteryName() {
        return lotteryName;
    }

    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }

    public String getLotteryTypeId() {
        return lotteryTypeId;
    }

    public void setLotteryTypeId(String lotteryTypeId) {
        this.lotteryTypeId = lotteryTypeId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
