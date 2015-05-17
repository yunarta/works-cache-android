package com.mobilesolutionworks.android.httpcache;

import android.content.Context;
import android.util.TimingLogger;

import com.mobilesolutionworks.android.httpcache.config.WorksCacheConfig;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by yunarta on 17/5/15.
 */
public class WorksCacheLoader {

    TimingLogger mTimingLogger;

    Context mContext;

    Task<WorksCache> mTask;

    Task<WorksCache> mWorker;

    String mData;

    String mPath;

    public WorksCacheLoader(Context context) {
        mContext = context;

        mWorker = mTask = Task.forResult(null);

        mTimingLogger = new TimingLogger("yunarta", "cache");
    }

    public WorksCacheLoader loadCache(final String path) {
        mPath = path;
        mTimingLogger.addSplit("load cache");
        mWorker = mTask.continueWith(new Continuation<WorksCache, WorksCache>() {
            @Override
            public WorksCache then(Task<WorksCache> task) throws Exception {
                // attempt to load data cache
                WorksCache cache = WorksCacheConfig.getInstance().getStore().load(path);
                mTimingLogger.addSplit("cache loaded");
                if (cache != null) {
                    return cache;
                } else {
                    throw new RuntimeException();
                }
            }
        });

        return this;
    }

    public WorksCacheLoader validateCache(final Continuation<WorksCache, Task<WorksCache>> validate) {
        mTimingLogger.addSplit("validated cache");
        mWorker = mWorker.onSuccessTask(validate).continueWithTask(new Continuation<WorksCache, Task<WorksCache>>() {
            @Override
            public Task<WorksCache> then(Task<WorksCache> task) throws Exception {
                mTimingLogger.addSplit("after validate cache");
                return task;
            }
        });
        return this;
    }

    public WorksCacheLoader withLoadTask(final Continuation<WorksCacheLoader, Task<WorksCache>> load) {
        mWorker = mWorker.continueWithTask(new Continuation<WorksCache, Task<WorksCache>>() {
            @Override
            public Task<WorksCache> then(Task<WorksCache> task) throws Exception {
                mTimingLogger.addSplit("load task");
                if (task.isFaulted()) {
                    return Task.forResult(WorksCacheLoader.this)
                            .continueWithTask(load)
                            .onSuccess(new Continuation<WorksCache, WorksCache>() {
                                @Override
                                public WorksCache then(Task<WorksCache> task) throws Exception {
                                    mTimingLogger.addSplit("after load task");
                                    WorksCache response = task.getResult();
                                    WorksCacheConfig.getInstance().getStore().store(mPath, response);

                                    return response;
                                }
                            });
                } else {
                    mTimingLogger.addSplit("load from cache");
                    return task;
                }
            }
        });

        return this;
    }

    public void consume(Continuation<WorksCacheLoader, Void> finished) {
        mWorker.continueWith(new Continuation<WorksCache, WorksCacheLoader>() {
            @Override
            public WorksCacheLoader then(Task<WorksCache> task) throws Exception {
                mTimingLogger.addSplit("consume task");
                WorksCacheLoader cache = WorksCacheLoader.this;
                cache.mData = task.getResult().toString();

                return cache;
            }
        }).continueWith(finished)
                .continueWith(new Continuation<Void, Object>() {
                    @Override
                    public Object then(Task<Void> task) throws Exception {
                        mTimingLogger.addSplit("task finished");
                        mTimingLogger.dumpToLog();
                        return null;
                    }
                });
    }

    public String getData() {
        return mData;
    }
}
