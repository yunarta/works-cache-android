package com.mobilesolutionworks.android.httpcache;

import java.io.UnsupportedEncodingException;

/**
 * Created by yunarta on 17/5/15.
 */
public class WorksCache {

    byte[] mData;

    long mTime;

    public WorksCache() {
    }

    public WorksCache(byte[] data) {
        mData = data;
    }

    public WorksCache(byte[] data, long time) {
        mData = data;
        mTime = time;
    }

    public byte[] data() {
        return mData;
    }

    @Override
    public String toString() {
        try {
            return new String(mData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new String(mData);
        }
    }

    public long time() {
        return mTime;
    }
}
