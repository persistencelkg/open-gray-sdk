package com.gray.lkg.intercepter.filter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/**
 * @description: 过滤器拦截器
 * @author: 李开广
 * @date: 2023/7/14 5:01 PM
 */
public interface CommonFilterInterceptor {

    default int order() {
        return 0;
    }

    default void intercept(CommonFilterChain chain) throws ServletException, IOException {
        chain.process();
    }

    interface CommonFilterChain {

      HttpServletRequest request();
      HttpServletResponse response();

      void process() throws ServletException, IOException;

      Duration duration();
    }
}
