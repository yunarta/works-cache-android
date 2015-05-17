package com.mobilesolutionworks.android.httpcache.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mobilesolutionworks.android.httpcache.WorksCacheLoader;
import com.mobilesolutionworks.android.httpcache.WorksCache;
import com.mobilesolutionworks.android.managedhttp.ABC;
import com.mobilesolutionworks.android.managedhttp.ManagedHttpRequest;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by yunarta on 16/5/15.
 */
@SuppressLint("LongLogTag")
public class MainActivity extends Activity {

    private SaveFragment fragment;

    public void testUpdate(View view) {
        WorksCacheLoader cache = new WorksCacheLoader(this);
        cache.loadCache("/data3").validateCache(new Continuation<WorksCache, Task<WorksCache>>() {
            @Override
            public Task<WorksCache> then(Task<WorksCache> task) throws Exception {
                WorksCache result = task.getResult();
                if (result.time() > System.currentTimeMillis()) {
                    Log.d("yunarta", "cache validated");
                    return Task.forResult(result);
                } else {
                    Log.d("yunarta", "cache invalidated");
                    return Task.forError(new RuntimeException());
                }
            }
        }).withLoadTask(new Continuation<WorksCacheLoader, Task<WorksCache>>() {
            @Override
            public Task<WorksCache> then(Task<WorksCacheLoader> task) throws Exception {
                Log.d("yunarta", "load task");
                ManagedHttpRequest request = new ManagedHttpRequest();
                request.local = request.remote = "http://mobilesandbox.cloudapp.net/rapp/dump.php";

                request.param = new Bundle();
                request.param.putString("a", "1");
                request.param.putString("b", "2");
                request.param.putString("c", "3");

                return new ABC(getApplication(), request).execute().continueWithTask(new Continuation<ABC.HttpCache, Task<WorksCache>>() {
                    @Override
                    public Task<WorksCache> then(Task<ABC.HttpCache> task) throws Exception {
                        ABC.HttpCache result = task.getResult();

                        return Task.forResult(new WorksCache(result.content.getBytes())).cast();
                    }
                });
            }
        }).consume(new Continuation<WorksCacheLoader, Void>() {
            @Override
            public Void then(Task<WorksCacheLoader> task) throws Exception {
                WorksCacheLoader result = task.getResult();
                Log.d("yunarta", "consumed result = " + result.getData());

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
