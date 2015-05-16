package com.mobilesolutionworks.android.httpcache.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mobilesolutionworks.android.httpcache.HttpCache;
import com.mobilesolutionworks.android.httpcache.HttpCacheResponse;
import com.mobilesolutionworks.android.httpcache.TextHttpCacheResponse;

import org.apache.http.Header;

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
        cache.loadCache("/data").withLoadTask(new Continuation<HttpCache, Task<HttpCacheResponse>>() {
            @Override
            public Task<HttpCacheResponse> then(Task<HttpCache> task) throws Exception {
                final Task<HttpCacheResponse>.TaskCompletionSource source = Task.create();

                AsyncHttpClient client = new AsyncHttpClient();
                client.get("http://mobilesandbox.cloudapp.net/rapp/stage_info.php", new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        source.trySetError(new RuntimeException(throwable));
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        source.trySetResult(new TextHttpCacheResponse(responseString));
                    }
                });

                return source.getTask();
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
