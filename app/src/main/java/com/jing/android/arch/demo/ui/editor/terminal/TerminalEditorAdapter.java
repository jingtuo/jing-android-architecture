package com.jing.android.arch.demo.ui.editor.terminal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.jing.android.arch.demo.ui.editor.terminal.fragment.TerminalEditorFragment;

/**
 * @author JingTuo
 */
public class TerminalEditorAdapter extends FragmentStatePagerAdapter {

    private int count;

    public TerminalEditorAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return TerminalEditorFragment.newInstance();
    }

    @Override
    public int getCount() {
        return count;
    }
}
