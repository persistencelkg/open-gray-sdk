package com.gray.lkg.context;

import feign.MethodMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @description: url上下文缓存
 * @author: 李开广
 * @date: 2023/7/21 7:15 PM
 */
public class FeignUrlContext {

    private final static Map<String, MethodMetadata> CACHE = new HashMap<String, MethodMetadata>();
    private final static ThreadLocal<String> URL_CACHE = new ThreadLocal<String>();

    private final static AntPathMatcher antPathMatcher = new AntPathMatcher();


    public static void addFeignUrlCache(String key, MethodMetadata metadata) {
        CACHE.put(key, metadata);
    }

    public static String getFeignUrlCache(String requestUrl) {
        String url = URL_CACHE.get();
        if (Objects.nonNull(url)) {
            return url;
        }
        MethodMetadata methodMetadata = CACHE.get(requestUrl);
        if (Objects.nonNull(methodMetadata)) {
           url = computeFeignUrl(requestUrl);
           URL_CACHE.set(url);
        }
        return url;

    }

    private static String computeFeignUrl(String requestUrl) {
        Set<String> keySet = CACHE.keySet();
        for (String url : keySet) {
            if (antPathMatcher.match(url, requestUrl)) {
                MethodMetadata methodMetadata1 = CACHE.get(url);
                return methodMetadata1.template().url();
            }
        }
        return null;
    }

    public static void removeFeignUrlCache() {
        URL_CACHE.remove();
    }
}
