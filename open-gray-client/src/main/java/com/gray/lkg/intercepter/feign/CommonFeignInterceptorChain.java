package com.gray.lkg.intercepter.feign;

import feign.Request;
import feign.Response;

import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/19 4:19 PM
 */
public abstract class CommonFeignInterceptorChain implements FeignInterceptor.FeignInterceptorChain {

    private Iterator<FeignInterceptor> iterator;
    /**
     * 每次请求是不一样的，不应该通过构造函数注入
     */
    private Request request;
    private Request.Options options;

    private final long startTime = System.nanoTime();

    private Duration duration;


    public CommonFeignInterceptorChain(Iterator<FeignInterceptor> feignInterceptorIterator, Request.Options options) {
        this.options = options;
        this.iterator = feignInterceptorIterator;
    }


    @Override
    public Response process() throws IOException {
        try {
            if (iterator.hasNext()) {
                return iterator.next().intercept(this);
            } else {
                return doExecute(request, options);
            }
        } finally {
            this.duration = Duration.ofNanos(System.nanoTime() - startTime);
        }
    }

    @Override
    public Response process(Request request) throws IOException {
        this.request = request;
        return process();
    }

    @Override
    public Response process(Request request, Request.Options options) throws IOException {
        this.options = options;
        this.request = request;
        return process();
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public Request.Options getOptions() {
        return options;
    }

    @Override
    public Request getRequest() {
        return request;
    }

    protected abstract Response doExecute(Request request, Request.Options options) throws IOException;
}
