package com.mobilesolutionworks.android.httpcache;

/**
 * Created by yunarta on 17/5/15.
 */
public class TextHttpCacheResponse implements HttpCacheResponse {

    String mText;

    public TextHttpCacheResponse(String text) {
        mText = text;
    }

    @Override
    public String toString() {
        return mText;
    }
}
