package com.zf.android.dinnercallwilddog;

import android.support.v4.app.Fragment;

/**
 * Created by zifeifeng on 6/23/17.
 */

public class SettingsClass extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SettingsFragment.newInstance();
    }
}
