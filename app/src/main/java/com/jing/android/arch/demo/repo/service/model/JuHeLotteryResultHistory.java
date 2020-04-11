package com.jing.android.arch.demo.repo.service.model;

import com.google.gson.annotations.SerializedName;
import com.jing.android.arch.demo.repo.db.LotteryResult;

import java.util.List;

/**
 *
 * @author JingTuo
 */
public class JuHeLotteryResultHistory {

    @SerializedName("page")
    private int pageNo;

    private int pageSize;

    private int totalPage;

    @SerializedName("lotteryResList")
    private List<JuHeLotteryResult> list;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<JuHeLotteryResult> getList() {
        return list;
    }

    public void setList(List<JuHeLotteryResult> list) {
        this.list = list;
    }
}
