package com.gray.lkg.intercepter.feign;

import feign.Request;
import feign.Response;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.time.Duration;

/**
 * @description: 自定义feign拦截器
 * @author: 李开广
 * @date: 2023/7/19 2:21 PM
 */
public interface FeignInterceptor {
    default int order() {
        return 0;
    }
    Response intercept(FeignInterceptorChain chain) throws IOException;

    interface FeignInterceptorChain {
        Response process(Request response, Request.Options options) throws IOException;

        Response process() throws IOException;

        Response process(Request request) throws IOException;

        Request.Options getOptions();

        Request getRequest();

        Duration getDuration();

    }

}

