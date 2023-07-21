package com.gray.lkg.intercepter.filter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/14 7:23 PM
 */
public class GrayFilterInterceptor implements CommonFilterInterceptor {

    private static final ThreadLocal<Map<String, String>> MAP = ThreadLocal.withInitial(LinkedHashMap::new);

    @Override
    public void intercept(CommonFilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = chain.request();
        try {
            // 参数
            putIfAbsent(request, request.getParameterNames(), false);

            // header
            putIfAbsent(request, request.getParameterNames(), true);

            chain.process();
        } finally {
            MAP.remove();
        }


    }


    public void putIfAbsent(HttpServletRequest request, Enumeration<String> enums, boolean isHeader) {
        while(enums.hasMoreElements()) {
            String nextElement = enums.nextElement();
            if (isHeader) {
                MAP.get().putIfAbsent(nextElement, request.getHeader(nextElement));
            } else {
                MAP.get().putIfAbsent(nextElement, request.getParameter(nextElement));
            }
        }
    }


    public static Map<String, String> getParamMap() {
        return Collections.unmodifiableMap(MAP.get());
    }

}
