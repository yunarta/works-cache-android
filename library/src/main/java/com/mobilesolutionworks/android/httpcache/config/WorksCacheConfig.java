package com.mobilesolutionworks.android.httpcache.config;

import android.content.Context;

import com.mobilesolutionworks.android.httpcache.store.WorksCacheStore;

/**
 * Created by yunarta on 17/5/15.
 */
public class WorksCacheConfig {

    protected static WorksCacheConfig sInstance;

    protected Context mContext;

    protected WorksCacheStore mStore;

    public static WorksCacheConfig getInstance() {
        if (sInstance == null) {
            sInstance = new WorksCacheConfig();
        }

        return sInstance;
    }

    public void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public Context getContext() {
        return mContext;
    }

    public void setStore(WorksCacheStore store) {
        mStore = store;
    }

    public WorksCacheStore getStore() {
        return mStore;
    }
}
