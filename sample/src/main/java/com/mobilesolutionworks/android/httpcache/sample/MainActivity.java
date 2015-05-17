package com.mobilesolutionworks.android.httpcache.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mobilesolutionworks.android.httpcache.HttpCache;
import com.mobilesolutionworks.android.httpcache.HttpCacheResponse;
import com.mobilesolutionworks.android.httpcache.TextHttpCacheResponse;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HttpCache cache = new HttpCache(this);
        cache.loadCache("/data3").withLoadTask(new Continuation<HttpCache, Task<HttpCacheResponse>>() {
            @Override
            public Task<HttpCacheResponse> then(Task<HttpCache> task) throws Exception {
                ManagedHttpRequest request = new ManagedHttpRequest();
                request.local = request.remote = "http://mobilesandbox.cloudapp.net/rapp/dump.php";

                request.param = new Bundle();
                request.param.putString("a", "1");
                request.param.putString("b", "2");
                request.param.putString("c", "3");

                return new ABC(getApplication(), request).execute().continueWithTask(new Continuation<ABC.HttpCache, Task<HttpCacheResponse>>() {
                    @Override
                    public Task<HttpCacheResponse> then(Task<ABC.HttpCache> task) throws Exception {
                        ABC.HttpCache result = task.getResult();

                        return Task.forResult(new TextHttpCacheResponse(result.content)).cast();
                    }
                });

//                final Task<HttpCacheResponse>.TaskCompletionSource source = Task.create();
//
//                AsyncHttpClient client = new AsyncHttpClient();
//                client.get("http://mobilesandbox.cloudapp.net/rapp/stage_info.php", new TextHttpResponseHandler() {
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        source.trySetError(new RuntimeException(throwable));
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                        source.trySetResult(new TextHttpCacheResponse(responseString));
//                    }
//                });
//
//                return source.getTask();
            }
        }).consume(new Continuation<HttpCache, Void>() {
            @Override
            public Void then(Task<HttpCache> task) throws Exception {
                HttpCache result = task.getResult();
                Log.d("yunarta", "consumed result = " + result.getData());

                return null;
            }
        });


//        if (savedInstanceState == null) {
//            fragment = new SaveFragment();
//            getFragmentManager().beginTransaction().add(fragment, "save").commit();
//        } else {
//            fragment = (SaveFragment) getFragmentManager().findFragmentByTag("save");
//        }
    }
}
