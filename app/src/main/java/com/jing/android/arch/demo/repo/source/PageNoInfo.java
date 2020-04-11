package com.jing.android.arch.demo.repo.source;

/**
 *
 * @author JingTuo
 */
public class PageNoInfo {

    int pageNo;

    /**
     * 下一页优先从数据库获取
     */
    boolean nextPageGetFromDbFirst;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public boolean isNextPageGetFromDbFirst() {
        return nextPageGetFromDbFirst;
    }

    public void setNextPageGetFromDbFirst(boolean nextPageGetFromDbFirst) {
        this.nextPageGetFromDbFirst = nextPageGetFromDbFirst;
    }
}
