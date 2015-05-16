package com.mobilesolutionworks.android.httpcache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by yunarta on 17/5/15.
 */
@SuppressLint("LongLogTag")
public class HttpCache {

    Context mContext;

    Task<HttpCacheResponse> mTask;

    Task<HttpCacheResponse> mWorker;

    String mData;

    public HttpCache(Context context) {
        mContext = context;

        mTask = Task.forResult(null);
        mWorker = mTask;
    }

    public HttpCache loadCache(String path) {
        mWorker = mTask.continueWith(new Continuation<HttpCacheResponse, HttpCacheResponse>() {
            @Override
            public HttpCacheResponse then(Task<HttpCacheResponse> task) throws Exception {
                // attempt to load data cache
                SharedPreferences preferences = mContext.getSharedPreferences("test", Context.MODE_PRIVATE);
                String data = preferences.getString("data", null);

                if (!TextUtils.isEmpty(data)) {
                    return new TextHttpCacheResponse(data);
                } else {
                    throw new RuntimeException();
                }
            }
        });

        return this;
    }

    public HttpCache withLoadTask(final Continuation<HttpCache, Task<HttpCacheResponse>> load) {
        mWorker = mWorker.continueWithTask(new Continuation<HttpCacheResponse, Task<HttpCacheResponse>>() {
            @Override
            public Task<HttpCacheResponse> then(Task<HttpCacheResponse> task) throws Exception {
                if (task.isFaulted()) {
                    return Task.forResult(HttpCache.this)
                            .continueWithTask(load)
                            .onSuccess(new Continuation<HttpCacheResponse, HttpCacheResponse>() {
                                @Override
                                public HttpCacheResponse then(Task<HttpCacheResponse> task) throws Exception {
                                    HttpCacheResponse response = task.getResult();

                                    if (response instanceof TextHttpCacheResponse) {
                                        SharedPreferences preferences = mContext.getSharedPreferences("test", Context.MODE_PRIVATE);
                                        preferences.edit().putString("data", response.toString()).commit();
                                    }

                                    return response;
                                }
                            });
                } else {
                    return task;
                }
            }
        });

        return this;
    }

    public void consume(Continuation<HttpCache, Void> finished) {
        mWorker
                .continueWith(new Continuation<HttpCacheResponse, HttpCache>() {
                    @Override
                    public HttpCache then(Task<HttpCacheResponse> task) throws Exception {
                        HttpCache cache = HttpCache.this;
                        cache.mData = task.getResult().toString();

                        return cache;
                    }
                })
                .continueWith(finished);
    }

    public String getData() {
        return mData;
    }
}
