package com.jing.android.arch.demo.repo.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 *
 * @author JingTuo
 */
@Entity(primaryKeys = {"id", "no"})
public class LotteryResult {

    /**
     * 彩票ID
     */
    @NonNull
    private String id;

    /**
     * 开奖结果
     */
    private String result;

    /**
     * 开奖期号
     */
    @NonNull
    private String no;

    /**
     * 开奖日期
     */
    private String date;

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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
