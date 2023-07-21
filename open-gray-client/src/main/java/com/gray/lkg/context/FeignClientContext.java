package com.gray.lkg.context;

import com.gray.lkg.client.FeignClientWrapper;
import com.gray.lkg.intercepter.aop.AopFeignMethodInterceptor;
import feign.Client;
import feign.Contract;
import feign.Feign;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/19 8:28 PM
 */
public class FeignClientContext extends FeignContext {

    private FeignContext feignContext;

    private FeignClientWrapper feignCLientWrapper;

    private Environment environment;


    public FeignClientContext(FeignContext feignContext, FeignClientWrapper feignClientWrapper, Environment environment) {
        this.feignContext = feignContext;
        this.feignCLientWrapper = feignClientWrapper;
        this.environment = environment;
    }


    @Override
    public <T> T getInstanceWithoutAncestors(String name, Class<T> type) {
        T target = feignContext.getInstanceWithoutAncestors(name, type);
        if (Objects.isNull(target)) {
            return null;
        }
        if (target instanceof Client) {
            return (T) feignCLientWrapper.wrap((Client) target);
        }
        // 实现接口代理的契约类
        if (target instanceof Contract) {
            return (T) AopFeignMethodInterceptor.getProxy(environment, (Contract) target);
        }
        return target;
    }


    @Override
    public <T> Map<String, T> getInstancesWithoutAncestors(String name, Class<T> type) {
        return super.getInstancesWithoutAncestors(name, type);
    }
}
