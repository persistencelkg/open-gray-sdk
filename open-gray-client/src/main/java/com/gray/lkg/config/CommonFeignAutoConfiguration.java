package com.gray.lkg.config;

import com.gray.lkg.client.FeignClientWrapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/19 8:49 PM
 */
@Configuration
@ConditionalOnClass(FeignClientsConfiguration.class)
public class CommonFeignAutoConfiguration {


    @Bean
    public FeignClientWrapper feignClientWrapper(ApplicationContext beanFactory) {
        return new FeignClientWrapper(beanFactory);
    }


    @Configuration
    @ConditionalOnClass(FeignContext.class)
    @ConditionalOnProperty(value = "feign.client.context.enabled", matchIfMissing = true)
    static class FeignClientBeanPostProcessorAutoConfiguration {

        @Bean
        public FeignClientBeanPostProcessor feignClientBeanPostProcessor(ApplicationContext  context) {
            return new FeignClientBeanPostProcessor(context);
        }
    }



}
