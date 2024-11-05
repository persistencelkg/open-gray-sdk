package com.gray.lkg.core.gray;

import com.gray.lkg.core.AbstractGrayInterceptor;
import feign.Request;
import feign.Response;
import org.lkg.metric.rpc.feign.SelfFeignInterceptor;

import java.io.IOException;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/5 8:07 PM
 */
public class FeignGrayInterceptor extends AbstractGrayInterceptor<Request, Response> implements SelfFeignInterceptor {


    @Override
    public Response interceptor(FeignChain feignChain) throws IOException {
        return null;
    }



    @Override
    public int getOrder() {
        return 0;
    }
}
