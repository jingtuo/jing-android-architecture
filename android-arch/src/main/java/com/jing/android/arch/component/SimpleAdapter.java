package com.jing.android.arch.component;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author JingTuo
 */
public class SimpleAdapter<T> extends BaseAdapter {

    private List<T> data;

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder<T> viewHolder;
        int viewType = getItemViewType(position);
        if (convertView == null) {
            viewHolder = createViewHolder(parent, position);
        } else {
            viewHolder = (ViewHolder<T>) convertView.getTag();
            if (viewHolder == null || viewHolder.getViewType() != viewType) {
                viewHolder = createViewHolder(parent, position);
            }
        }
        viewHolder.setView(position, data.get(position));
        return viewHolder.getView();
    }


    protected ViewHolder<T> createViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder<>(parent, viewType);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public static class ViewHolder<T> {

        private int viewType;

        private View view;

        private int position;

        private T data;

        public ViewHolder(ViewGroup parent, int viewType) {
            this.viewType = viewType;
            this.view = createView(parent);
            this.view.setTag(this);
        }

        protected View createView(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return inflater.inflate(getLayoutId(), parent, false);
        }

        protected int getLayoutId() {
            return android.R.layout.simple_list_item_1;
        }

        public int getViewType() {
            return viewType;
        }

        public View getView() {
            return view;
        }

        public void setView(int position, T data) {
            this.position = position;
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public int getPosition() {
            return position;
        }
    }
}
