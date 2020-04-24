package com.jing.android.arch.demo.ui.lottery.history;

/**
 * @author JingTuo
 */
public interface OnItemClickListener<T> {

    /**
     * 当点击第position + 1个
     * @param position 第position + 1个
     * @param data 第position + 1个数据
     */
    void onItemClick(int position, T data);

}
