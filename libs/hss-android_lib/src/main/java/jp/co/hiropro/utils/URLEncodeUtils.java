package jp.co.hiropro.utils;

import android.util.Log;

import java.util.Map;

/**
 * Created by LamND on 1/5/2016.
 */
public class URLEncodeUtils {
    public static String format(String url, Map<String, Object> params) {
        if (params != null && params.size() > 0) {
            StringBuilder sb = new StringBuilder(url);
            if (!url.endsWith("?")) sb.append('?');
            for (String key : params.keySet()) {
                sb.append(key).append('=').append(params.get(key).toString()).append('&');
            }
            sb.deleteCharAt(sb.length() - 1);
            url = sb.toString();
        }
        Log.d("URL", url);
        return url;
    }
}
