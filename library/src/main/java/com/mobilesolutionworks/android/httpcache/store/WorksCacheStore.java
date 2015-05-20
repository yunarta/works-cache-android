package com.mobilesolutionworks.android.httpcache.store;

import com.mobilesolutionworks.android.httpcache.WorksCache;

import java.io.IOException;

/**
 * Created by yunarta on 17/5/15.
 */
public interface WorksCacheStore {

    WorksCache load(String path);

    void deleteCache(String path);

    void store(String path, WorksCache cache) throws IOException;
}
