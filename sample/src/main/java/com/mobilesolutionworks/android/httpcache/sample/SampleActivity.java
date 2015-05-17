package com.mobilesolutionworks.android.httpcache.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mobilesolutionworks.android.httpcache.UseExpiredWorkCache;
import com.mobilesolutionworks.android.httpcache.WorksCache;
import com.mobilesolutionworks.android.httpcache.WorksCacheLoader;

import org.apache.http.Header;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by yunarta on 16/5/15.
 */
public class SampleActivity extends Activity {

    public void testUpdateLoopJ(View view) {
        WorksCacheLoader cache = new WorksCacheLoader();
        cache.from("/data3").validate(new Continuation<WorksCache, WorksCache>() {
            @Override
            public WorksCache then(Task<WorksCache> task) throws Exception {
                WorksCache result = task.getResult();
                if (result.time() > System.currentTimeMillis()) {
                    return result;
                } else {
                    return null;
                }
            }
        }).orLoad(new Continuation<WorksCache, Task<WorksCache>>() {
            @Override
            public Task<WorksCache> then(final Task<WorksCache> worksCacheTask) throws Exception {
                final Task<WorksCache>.TaskCompletionSource source = Task.create();
                AsyncHttpClient client = new AsyncHttpClient();

                RequestParams params = new RequestParams();
                params.add("x", "1");
                params.add("y", "2");
                params.add("z", "3");

                client.get("http://mobilesandbox.cloudapp.net/rapp/dump.php", params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        source.trySetResult(new WorksCache(responseString.getBytes()));

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        source.trySetError(new UseExpiredWorkCache());
                    }
                });

                return source.getTask();
            }
        }).consumeWithTask(new Continuation<WorksCache, WorksCache>() {
            @Override
            public WorksCache then(Task<WorksCache> task) throws Exception {
                WorksCache result = task.getResult();
                Log.d("yunarta", "result = " + result);
                return null;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (savedInstanceState == null) {
//            fragment = new SaveFragment();
//            getFragmentManager().beginTransaction().add(fragment, "save").commit();
//        } else {
//            fragment = (SaveFragment) getFragmentManager().findFragmentByTag("save");
//        }
    }
}
