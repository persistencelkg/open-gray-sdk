package com.gray.lkg.core.flow;

import com.gray.lkg.core.AbstractGrayInterceptor;
import com.gray.lkg.core.GrayExecutor;
import feign.Request;
import feign.Response;
import lombok.AllArgsConstructor;
import org.lkg.metric.rpc.feign.FeignMetaDataContext;
import org.lkg.metric.rpc.feign.SelfFeignInterceptor;

import java.io.IOException;

/**
 * 基于feign 流量拦截器
 * Description:
 * Author: 李开广
 * Date: 2024/11/5 8:07 PM
 */
public class FeignGrayInterceptor extends AbstractGrayInterceptor<Request, Response> implements SelfFeignInterceptor {


    @Override
    public Response interceptor(FeignChain feignChain) throws IOException {
        Request request = feignChain.request();
        FeignMetaDataContext.FeignMetaData feignMetaContext = FeignMetaDataContext.getFeignMetaContext(request.url());
        return super.flowIntercept(new FeignGrayExecutor(feignChain), request.url(), feignMetaContext.getUri());
    }

    @AllArgsConstructor
    private static class FeignGrayExecutor implements GrayExecutor<Request,Response>{

        private final FeignChain feignChain;


        @Override
        public Response execute() throws IOException {
            return feignChain.process();
        }

        @Override
        public Response execute(Request request) throws IOException {
            return feignChain.process(request);
        }

        @Override
        public Response execute(String url) throws IOException {
            Request request = feignChain.request();
            Request newRequest = Request.create(request.httpMethod(), url, request.headers(), request.requestBody());
            return feignChain.process(newRequest);
        }
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
