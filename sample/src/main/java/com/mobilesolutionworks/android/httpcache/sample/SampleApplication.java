package com.mobilesolutionworks.android.httpcache.sample;

import android.app.Application;

import com.mobilesolutionworks.android.httpcache.config.WorksCacheConfig;
import com.mobilesolutionworks.android.httpcache.store.WorksCacheFileStore;

/**
 * Created by yunarta on 17/5/15.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WorksCacheConfig instance = WorksCacheConfig.getInstance();
        instance.setContext(this);
        instance.setStore(new WorksCacheFileStore());
//        instance.setStore(new WorksCacheSQLStore("works_cache"));
        instance.setExpire(10000);
    }
}
