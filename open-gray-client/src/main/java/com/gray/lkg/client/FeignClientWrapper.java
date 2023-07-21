package com.gray.lkg.client;

import com.gray.lkg.intercepter.feign.FeignInterceptor;
import feign.Client;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/19 8:14 PM
 */
public class FeignClientWrapper {

    private static final boolean ribbonPresent;

    static {
        ribbonPresent = ClassUtils.isPresent(
                "org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient",
                null)
                && ClassUtils.isPresent(
                "org.springframework.cloud.netflix.ribbon.SpringClientFactory",
                null);
    }

    private CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory;

    private SpringClientFactory clientFactory;

    @Autowired(required = false)
    private List<FeignInterceptor> feignInterceptorList;

    private Client client;

    private BeanFactory beanFactory;


    public FeignClientWrapper(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    public Client wrap(Client client) {
        if (client instanceof CommonFeignClient) {
            return client;
        }
        if (!ribbonPresent) {
            return new Client.Default(null, null);
        }
        if (client instanceof LoadBalancerFeignClient) {
            LoadBalancerFeignClient loadBalancerFeignClient = (LoadBalancerFeignClient) client;
            return new CommonFeignClient(wrap(loadBalancerFeignClient.getDelegate()),  cachingSpringLoadBalancerFactory(), clientFactory(), feignInterceptorList);
        }
        // 如果没有ribbon
        return new Client.Default(null, null);
    }

    private CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory() {
        if (Objects.nonNull(cachingSpringLoadBalancerFactory)) {
            return cachingSpringLoadBalancerFactory;
        }
        return beanFactory.getBean(CachingSpringLoadBalancerFactory.class);
    }

    private SpringClientFactory clientFactory() {
        if (Objects.nonNull(clientFactory)) {
            return clientFactory;
        }
        return beanFactory.getBean(SpringClientFactory.class);
    }


}
