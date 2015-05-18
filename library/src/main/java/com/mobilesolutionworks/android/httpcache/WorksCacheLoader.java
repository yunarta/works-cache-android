package com.mobilesolutionworks.android.httpcache;

import com.mobilesolutionworks.android.httpcache.config.WorksCacheConfig;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by yunarta on 17/5/15.
 */
public class WorksCacheLoader {

    Task<WorksCache> mTask;

    Task<WorksCache> mWorker;

    String mPath;

    long mExpire;

    public WorksCacheLoader() {
        mWorker = mTask = Task.forResult(null);
        mExpire = WorksCacheConfig.getInstance().getExpire();
    }

    public WorksCacheLoader from(final String path) {
        return from(path, -1);
    }

    public WorksCacheLoader from(final String path, final long expire) {
        mPath = path;
        mExpire = expire != -1 ? mExpire : expire;

        mWorker = mWorker.continueWith(new Continuation<WorksCache, WorksCache>() {
            @Override
            public WorksCache then(Task<WorksCache> task) throws Exception {
                // attempt to load data cache
                WorksCache cache = WorksCacheConfig.getInstance().getStore().load(path);

                if (cache != null) {
                    mTask = Task.forResult(cache);
                    if (expire == -1 || cache.time() - System.currentTimeMillis() > expire) {
                        return cache;
                    }
                }

                return null;
            }
        });
        
        return this;
    }

    public WorksCacheLoader validate(final Continuation<WorksCache, WorksCache> validate) {
        mWorker = mWorker.onSuccess(validate);
        return this;
    }

    public WorksCacheLoader orLoad(final Continuation<WorksCache, Task<WorksCache>> load) {
        mWorker = mWorker.continueWithTask(new Continuation<WorksCache, Task<WorksCache>>() {
            @Override
            public Task<WorksCache> then(Task<WorksCache> task) throws Exception {
                WorksCache result = task.getResult();
                if (result == null || task.isFaulted()) {
                    return task.continueWithTask(load).onSuccess(new Continuation<WorksCache, WorksCache>() {
                        @Override
                        public WorksCache then(Task<WorksCache> task) throws Exception {
                            WorksCache response = task.getResult();
                            response.mTime = System.currentTimeMillis() + mExpire;
                            WorksCacheConfig.getInstance().getStore().store(mPath, response);

                            return response;
                        }
                    }).continueWith(new Continuation<WorksCache, WorksCache>() {
                        @Override
                        public WorksCache then(Task<WorksCache> task) throws Exception {
                            if (task.isFaulted()) {
                                Exception exception = task.getError();
                                if (exception instanceof UseExpiredWorkCache) {
                                    return mTask.getResult();
                                } else {
                                    throw exception;
                                }
                            } else {
                                return task.getResult();
                            }
                        }
                    });
                } else {
                    return task;
                }
            }
        });

        return this;
    }

    public Task<WorksCache> consumeWithTask(Continuation<WorksCache, WorksCache> finished) {
        return mWorker.continueWith(finished);
    }

    public WorksCacheLoader consume(Continuation<WorksCache, WorksCache> finished) {
        mWorker = mWorker.continueWith(finished);
        return this;
    }
}
