package com.objectcomputing.checkins.util.form;

import java.nio.charset.Charset;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Singleton;

@Singleton
public class FormUrlEncodedDecoder {
    public Map<String, Object> decode(String formUrlEncodedString, Charset charset) {
        Map<String, Object> queryParams = new HashMap<>();
        String[] pairs = formUrlEncodedString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), charset);
                String value = URLDecoder.decode(pair.substring(idx + 1), charset);
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }
}
