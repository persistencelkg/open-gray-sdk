package com.gray.lkg.config;

import com.gray.lkg.client.FeignClientWrapper;
import com.gray.lkg.context.FeignClientContext;
import com.gray.lkg.intercepter.aop.AopFeignMethodInterceptor;
import feign.Contract;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/21 7:35 PM
 */
public class FeignClientBeanPostProcessor implements BeanPostProcessor, EnvironmentAware {

    private Environment environment;
    private BeanFactory beanFactory;

    public FeignClientBeanPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
       if ((bean instanceof FeignContext) && !(bean instanceof FeignClientContext)) {
           return new FeignClientContext((FeignContext) bean, feignClientWrapper(), environment);
       }
       if (bean instanceof Contract) {
           return AopFeignMethodInterceptor.getProxy(environment, (Contract) bean);
       }
       return bean;
    }

    private FeignClientWrapper feignClientWrapper() {
        return beanFactory.getBean(FeignClientWrapper.class);
    }
}
