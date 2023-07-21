package com.gray.lkg.client;

import com.gray.lkg.intercepter.feign.CommonFeignInterceptorChain;
import com.gray.lkg.intercepter.feign.FeignInterceptor;
import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/19 5:10 PM
 */
public class CommonFeignClient extends LoadBalancerFeignClient {


    private final List<FeignInterceptor> feignInterceptorList;

    public CommonFeignClient(Client delegate, CachingSpringLoadBalancerFactory lbClientFactory,
                             SpringClientFactory clientFactory, List<FeignInterceptor> feignInterceptorList) {
        super(delegate, lbClientFactory, clientFactory);
        this.feignInterceptorList = Objects.isNull(feignInterceptorList) ? Collections.emptyList() : feignInterceptorList;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return new LoadBalancerInterceptorChain(options).process(request);
    }


   private class LoadBalancerInterceptorChain extends CommonFeignInterceptorChain {

       LoadBalancerInterceptorChain(Request.Options options) {
           super(CommonFeignClient.this.feignInterceptorList.iterator(),options);
       }

       @Override
       protected Response doExecute(Request request, Request.Options options) throws IOException {
           return CommonFeignClient.super.execute(request, options);
       }
   }
}
