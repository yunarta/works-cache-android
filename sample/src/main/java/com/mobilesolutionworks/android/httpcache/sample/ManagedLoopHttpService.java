package com.mobilesolutionworks.android.httpcache.sample;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mobilesolutionworks.android.managedhttp.ManagedHttpService;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by yunarta on 17/5/15.
 */
public class ManagedLoopHttpService extends ManagedHttpService {

    private CookieStore mCookieStore;

    @Override
    public void onCreate() {
        super.onCreate();

        mCookieStore = new BasicCookieStore();
    }

    @Override
    protected CancelToken executeRequest(final String local, final String remote, String method, Bundle params, final int cache, int timeout) {
        RequestParams rp = new RequestParams();
        if (params != null) {
            for (String key : params.keySet()) {
                String value = params.getString(key);
                if (!TextUtils.isEmpty(value)) {
                    rp.add(key, value);
                }
            }
        }

        TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable instanceof HttpResponseException) {
                    ContentValues values = new ContentValues();
                    values.put("local", local);
                    values.put("remote", remote);
                    values.put("data", "");
                    values.put("time", System.currentTimeMillis() + cache);

                    values.put("error", "2");
                    values.putNull("trace");

                    values.put("status", statusCode);

                    insert(values, local);
                } else {
                    ContentValues values = new ContentValues();
                    values.put("local", local);
                    values.put("remote", remote);
                    values.put("data", "");
                    values.put("time", System.currentTimeMillis() + cache);
                    values.put("error", "-2");

                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
                        ObjectOutputStream out = new ObjectOutputStream(baos);
                        out.writeObject(throwable);

                        values.put("trace", baos.toByteArray());
                    } catch (IOException e) {
                        // e.printStackTrace();
                    }

                    values.put("status", statusCode);

                    insert(values, local);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ContentValues values = new ContentValues();

                values.put("local", local);
                values.put("remote", remote);
                values.put("data", responseString);
                values.put("time", System.currentTimeMillis() + cache);
                values.put("error", "1");

                values.putNull("trace");
                values.put("status", statusCode);
                insert(values, local);
            }
        };

        AsyncHttpClient client = new AsyncHttpClient();
        client.setCookieStore(mCookieStore);

        final RequestHandle handle;
        if ("POST".equals(method)) {
            handle = client.post(this, remote, rp, handler);
        } else {
            handle = client.get(this, remote, rp, handler);
        }

        return new CancelToken() {

            @Override
            public void cancel() {
                handle.cancel(true);
            }
        };
    }
}
