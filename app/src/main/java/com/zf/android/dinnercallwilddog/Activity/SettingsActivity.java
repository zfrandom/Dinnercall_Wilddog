package com.zf.android.dinnercallwilddog.Activity;

import android.support.v4.app.Fragment;

import com.zf.android.dinnercallwilddog.Fragment.SettingsFragment;

/**
 * Created by zifeifeng on 6/23/17.
 */

public class SettingsActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SettingsFragment.newInstance();
    }
}
