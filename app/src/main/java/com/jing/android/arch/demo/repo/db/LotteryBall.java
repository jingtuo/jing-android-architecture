package com.jing.android.arch.demo.repo.db;

import androidx.room.Entity;

/**
 * 彩票球的信息
 * @author JingTuo
 */
@Entity
public class LotteryBall {

    /**
     * 球号:如01
     */
    private String ballNo;

    /**
     * 出现的次数
     */
    private int frequency;

    /**
     * 出现的次数的占比
     */
    private float frequencyRatio;

    public String getBallNo() {
        return ballNo;
    }

    public void setBallNo(String ballNo) {
        this.ballNo = ballNo;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public float getFrequencyRatio() {
        return frequencyRatio;
    }

    public void setFrequencyRatio(float frequencyRatio) {
        this.frequencyRatio = frequencyRatio;
    }
}
