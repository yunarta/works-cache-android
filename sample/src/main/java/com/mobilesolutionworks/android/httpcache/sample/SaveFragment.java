package com.mobilesolutionworks.android.httpcache.sample;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by yunarta on 16/5/15.
 */
@SuppressLint("LongLogTag")
public class SaveFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(BuildConfig.APPLICATION_ID, "SaveFragment.onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(BuildConfig.APPLICATION_ID, "SaveFragment.onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(BuildConfig.APPLICATION_ID, "SaveFragment.onResume");
    }
}
