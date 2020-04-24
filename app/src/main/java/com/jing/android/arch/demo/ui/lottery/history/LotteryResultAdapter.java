package com.jing.android.arch.demo.ui.lottery.history;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jing.android.arch.demo.R;
import com.jing.android.arch.demo.databinding.LotteryResultItemBinding;
import com.jing.android.arch.demo.repo.db.LotteryResult;

/**
 * 彩票结果-Adapter
 *
 * @author JingTuo
 */
public class LotteryResultAdapter extends PagedListAdapter<LotteryResult, LotteryResultAdapter.LotteryResultViewHolder> {

    protected LotteryResultAdapter() {
        super(DIFF_CALLBACK);
    }

    private OnItemClickListener<LotteryResult> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<LotteryResult> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LotteryResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LotteryResultItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.lottery_result_item, parent, false);
        return new LotteryResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LotteryResultViewHolder holder, int position) {
        holder.binding.setItem(getItem(position));
        holder.binding.executePendingBindings();
    }

    /**
     * @author JingTuo
     */
    class LotteryResultViewHolder extends RecyclerView.ViewHolder {

        private LotteryResultItemBinding binding;

        LotteryResultViewHolder(LotteryResultItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    onItemClickListener.onItemClick(position, getItem(position));
                }
            });
        }


    }

    /**
     *
     */
    private static DiffUtil.ItemCallback<LotteryResult> DIFF_CALLBACK = new DiffUtil.ItemCallback<LotteryResult>() {
        @Override
        public boolean areItemsTheSame(@NonNull LotteryResult oldItem, @NonNull LotteryResult newItem) {
            return oldItem.getId() != null
                    && oldItem.getId().equals(newItem.getId())
                    && oldItem.getLotteryNo().equals(newItem.getLotteryNo());
        }

        @Override
        public boolean areContentsTheSame(@NonNull LotteryResult oldItem, @NonNull LotteryResult newItem) {
            return oldItem.getId() != null
                    && oldItem.getId().equals(newItem.getId())
                    && oldItem.getLotteryNo().equals(newItem.getLotteryNo())
                    && oldItem.getResult() != null
                    && oldItem.getResult().equals(newItem.getResult());
        }
    };
}
